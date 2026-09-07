package com.example.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SupabaseClient
import com.example.data.models.Game
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameDetailViewModel : ViewModel() {
    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun fetchGameDetails(gameId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedGame = SupabaseClient.client.postgrest["games"]
                    .select { filter { eq("id", gameId) } }
                    .decodeSingleOrNull<Game>()
                
                if (fetchedGame != null) {
                    _game.value = fetchedGame
                } else {
                    _error.value = "Game not found"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load game details"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
