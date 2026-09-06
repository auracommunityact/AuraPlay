package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String? = null,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val banner_url: String? = null,
    val bio: String? = null,
    val role: String = "user"
)

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val profile: Profile?) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            SupabaseClient.client.auth.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> fetchProfile()
                    is SessionStatus.NotAuthenticated -> _authState.value = AuthState.Unauthenticated
                    is SessionStatus.LoadingFromStorage -> _authState.value = AuthState.Loading
                    is SessionStatus.NetworkError -> _authState.value = AuthState.Error("Network error checking session")
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                val user = SupabaseClient.client.auth.currentUserOrNull()
                if (user != null) {
                    val profile = SupabaseClient.client.postgrest["profiles"]
                        .select { filter { eq("id", user.id) } }
                        .decodeSingleOrNull<Profile>()
                    _authState.value = AuthState.Authenticated(profile)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Authenticated(null) // Auth'd but profile failed
            }
        }
    }

    fun signUp(email: String, password: String, fullName: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    data = kotlinx.serialization.json.buildJsonObject {
                        put("full_name", kotlinx.serialization.json.JsonPrimitive(fullName))
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Signup failed")
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    fun uploadAvatar(imageBytes: ByteArray, fileExtension: String) {
        val currentProfile = (_authState.value as? AuthState.Authenticated)?.profile ?: return
        viewModelScope.launch {
            try {
                val path = "${currentProfile.id}/avatar-${System.currentTimeMillis()}.$fileExtension"
                SupabaseClient.client.storage.from("avatars").upload(path, imageBytes, upsert = true)
                val publicUrl = SupabaseClient.client.storage.from("avatars").publicUrl(path)
                
                SupabaseClient.client.postgrest["profiles"]
                    .update(mapOf("avatar_url" to publicUrl)) {
                        filter { eq("id", currentProfile.id) }
                    }
                
                fetchProfile()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
