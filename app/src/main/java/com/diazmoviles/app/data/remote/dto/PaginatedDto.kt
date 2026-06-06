package com.diazmoviles.app.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PaginatedDto<T>(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<T>
)
