package ru.baklanovsoft.simplefeed.core.records

import java.time.Instant
import java.util.UUID

case class TagTimestamp(
    id: UUID,
    tag: String,
    lastVisited: Instant
)
