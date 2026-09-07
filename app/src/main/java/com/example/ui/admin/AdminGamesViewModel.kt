package com.example.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SupabaseClient
import com.example.data.models.Game
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AdminGamesViewModel : ViewModel() {
    private val _games = MutableStateFlow<List<Game>>(emptyList())
    val games: StateFlow<List<Game>> = _games.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchGames()
    }

    fun fetchGames() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val fetchedGames = SupabaseClient.client.postgrest["games"]
                    .select(columns = Columns.ALL)
                    .decodeList<Game>()
                _games.value = fetchedGames
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load games"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun saveGame(game: Game): Boolean {
        return try {
            if (game.id.isBlank()) {
                // Insert
                val newGame = game.copy(id = UUID.randomUUID().toString())
                SupabaseClient.client.postgrest["games"].insert(newGame)
            } else {
                // Update
                SupabaseClient.client.postgrest["games"].update(game) {
                    filter { eq("id", game.id) }
                }
            }
            fetchGames()
            true
        } catch (e: Exception) {
            _error.value = e.message ?: "Failed to save game"
            false
        }
    }
    
    fun deleteGame(gameId: String) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.postgrest["games"].delete {
                    filter { eq("id", gameId) }
                }
                fetchGames()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete game"
            }
        }
    }
}
