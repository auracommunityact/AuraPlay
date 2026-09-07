package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: String = "",
    val title: String = "",
    val developer: String? = null,
    val genre: String? = null,
    val icon_url: String? = null,
    val cover_url: String? = null,
    val rating: Double? = null,
    val description: String? = null,
    val screenshots: List<String>? = null,
    val platforms: List<String>? = null
)
