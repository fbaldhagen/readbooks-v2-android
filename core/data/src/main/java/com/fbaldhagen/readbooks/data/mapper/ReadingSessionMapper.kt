package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.data.local.db.entity.ReadingSessionEntity
import com.fbaldhagen.readbooks.domain.model.ReadingSession

fun ReadingSessionEntity.toDomain(): ReadingSession = ReadingSession(
    id = id,
    bookId = bookId,
    startTime = startTime,
    endTime = endTime,
    durationMinutes = durationMinutes,
    pagesRead = pagesRead
)

fun ReadingSession.toEntity(): ReadingSessionEntity = ReadingSessionEntity(
    id = id,
    bookId = bookId,
    startTime = startTime,
    endTime = endTime,
    durationMinutes = durationMinutes,
    pagesRead = pagesRead
)