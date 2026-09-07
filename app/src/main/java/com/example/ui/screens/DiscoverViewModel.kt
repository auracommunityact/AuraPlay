package com.example.ui.screens

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

class DiscoverViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Game>>(emptyList())
    val searchResults: StateFlow<List<Game>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchGames() // Initial load
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        fetchGames(query)
    }

    private fun fetchGames(query: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val games = if (query.isBlank()) {
                    SupabaseClient.client.postgrest["games"]
                        .select(columns = Columns.ALL)
                        .decodeList<Game>()
                } else {
                    SupabaseClient.client.postgrest["games"]
                        .select(columns = Columns.ALL) {
                            filter {
                                or {
                                    ilike("title", "%$query%")
                                    ilike("developer", "%$query%")
                                    ilike("genre", "%$query%")
                                }
                            }
                        }
                        .decodeList<Game>()
                }
                _searchResults.value = games
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load games"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
