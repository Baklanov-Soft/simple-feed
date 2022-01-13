package ru.baklanovsoft.simplefeed.core.repos

import fs2.Stream
import ru.baklanovsoft.simplefeed.core.DomainError
import ru.baklanovsoft.simplefeed.core.records.TagTimestamp

import java.time.Instant

trait TagTimestampRepo[F[_]] {
  def get(tag: String): F[TagTimestamp]

  def lookup(tag: String): F[Option[TagTimestamp]]

  def list(): Stream[F, TagTimestamp]

  def create(tag: String, timestamp: Instant): F[TagTimestamp]

  def update(tag: String, timestamp: Instant): F[TagTimestamp]

  def delete(tag: String): F[Unit]
}

object TagTimestampRepo {
  sealed trait TagTimestampRepoError extends DomainError

  final object TagNotFound extends TagTimestampRepoError {
    override def httpStatusCode: Int = 404
  }

  final object DuplicateTag extends TagTimestampRepoError {
    override def httpStatusCode: Int = 409
  }
}
