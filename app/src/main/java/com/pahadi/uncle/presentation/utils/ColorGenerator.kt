package com.pahadi.uncle.presentation.utils

object ColorGenerator {
    private val colors = listOf(
        "#cd5d7d",
        "#a7c5eb",
        "#ffab73",
        "#ffaec0",
        "#4a3933",
        "#709fb0",
        "#f14668",
        "#faf3e0"
    )
    private var index = 0

    val color:String
    get() = colors[index++ % colors.size]
}