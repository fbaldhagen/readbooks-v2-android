package com.fbaldhagen.readbooks.domain.model

data class TtsUtterance(
    val text: String,
    val locator: DomainLocator
)