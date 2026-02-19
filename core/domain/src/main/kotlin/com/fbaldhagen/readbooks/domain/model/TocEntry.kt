package com.fbaldhagen.readbooks.domain.model

data class TocEntry(
    val title: String,
    val href: String,
    val children: List<TocEntry> = emptyList()
)