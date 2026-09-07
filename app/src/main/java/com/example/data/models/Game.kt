package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: String = "",
    val title: String = "",
    val short_description: String? = null,
    val description: String? = null,
    val developer: String? = null,
    val publisher: String? = null,
    val genre: String? = null,
    val icon_url: String? = null,
    val cover_url: String? = null,
    val banner_url: String? = null,
    val rating: Double? = null,
    val version: String? = null,
    val size: String? = null,
    val package_name: String? = null,
    val app_store_id: String? = null,
    val google_play_url: String? = null,
    val apple_store_url: String? = null,
    val age_rating: String? = null,
    val release_date: String? = null,
    val status: String = "draft",
    val featured: Boolean = false,
    val trending: Boolean = false,
    val views: Int = 0,
    val downloads: Int = 0,
    val screenshots: List<String>? = null,
    val platforms: List<String>? = null
)
