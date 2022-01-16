package ru.baklanovsoft.simplefeed.core.records

import java.time.Instant

case class TagTimestamp(
    tag: String,
    lastVisited: Instant
)
