package ru.baklanovsoft.simplefeed.jdbc.repos

import cats.effect.Sync
import doobie.ConnectionIO
import doobie.implicits._
import ru.baklanovsoft.simplefeed.core.records.TagTimestamp
import ru.baklanovsoft.simplefeed.core.repos.TagTimestampRepo
// this contains Instant implicit codecs for Doobie so it is actually used
import doobie.implicits.javatimedrivernative.JavaTimeInstantMeta

import java.time.Instant

class TagTimestampRepoDoobieImpl extends TagTimestampRepo[ConnectionIO] {

  override def get(tag: String): ConnectionIO[TagTimestamp] =
    for {
      maybeTag <- lookup(tag)
      res      <- Sync[ConnectionIO].fromOption(maybeTag, TagTimestampRepo.TagNotFound)
    } yield res

  override def lookup(tag: String): ConnectionIO[Option[TagTimestamp]] =
    sql"SELECT tag, last_visited FROM tag_timestamp WHERE tag = $tag".query[TagTimestamp].option

  override def list(): fs2.Stream[ConnectionIO, TagTimestamp] =
    sql"SELECT * FROM tag_timestamp".query[TagTimestamp].stream

  override def create(tag: String, timestamp: Instant): ConnectionIO[TagTimestamp] =
    for {
      maybeTag <- lookup(tag)
      _        <- Sync[ConnectionIO]
                    .whenA(maybeTag.nonEmpty)(Sync[ConnectionIO].raiseError(TagTimestampRepo.DuplicateTag))
      _        <- sql"INSERT INTO tag_timestamp(tag, last_visited) VALUES ($tag, $timestamp)".update.run
    } yield TagTimestamp(tag, timestamp)

  override def update(tag: String, timestamp: Instant): ConnectionIO[TagTimestamp] =
    for {
      maybeTag <- lookup(tag)
      _        <- Sync[ConnectionIO].fromOption(maybeTag, TagTimestampRepo.TagNotFound)
      _        <- sql"UPDATE tag_timestamp SET last_visited = $timestamp".update.run
    } yield TagTimestamp(tag, timestamp)

  override def delete(tag: String): ConnectionIO[Unit] =
    for {
      maybeTag <- lookup(tag)
      _        <- Sync[ConnectionIO].fromOption(maybeTag, TagTimestampRepo.TagNotFound)
      _        <- sql"DELETE FROM tag_timestamp WHERE tag = $tag".update.run
    } yield ()
}
