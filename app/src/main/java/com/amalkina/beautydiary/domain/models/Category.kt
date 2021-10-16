package com.amalkina.beautydiary.domain.models

data class Category(
    val id: Long = -1,
    val name: String = "",
    val imagePath: String? = null,
    val drawableName: String? = null,
    val updateDate: Long = System.currentTimeMillis(),
    val creationDate: Long = System.currentTimeMillis()
)