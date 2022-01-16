package ru.baklanovsoft.simplefeed.repos

import cats.data.Kleisli
import cats.effect.Sync
import doobie.Transactor
import doobie.implicits._
import ru.baklanovsoft.simplefeed.core.records.TagTimestamp
import ru.baklanovsoft.simplefeed.core.repos.TagTimestampRepo
import ru.baklanovsoft.simplefeed.jdbc.repos.TagTimestampRepoDoobieImpl

import java.time.Instant

class TagTimestampRepoImpl[F[_]: Sync](doobieImpl: TagTimestampRepoDoobieImpl, xa: Transactor[F])
    extends TagTimestampRepo[F] {
  override def get(tag: String): F[TagTimestamp] = doobieImpl.get(tag).transact(xa)

  override def lookup(tag: String): F[Option[TagTimestamp]] = doobieImpl.lookup(tag).transact(xa)

  override def list(): fs2.Stream[F, TagTimestamp] = doobieImpl.list().transact(xa)

  override def create(tag: String, timestamp: Instant): F[TagTimestamp] = doobieImpl.create(tag, timestamp).transact(xa)

  override def update(tag: String, timestamp: Instant): F[TagTimestamp] = doobieImpl.update(tag, timestamp).transact(xa)

  override def delete(tag: String): F[Unit] = doobieImpl.delete(tag).transact(xa)
}

object TagTimestampRepoImpl {

  def make[F[_]: Sync]: Kleisli[F, (TagTimestampRepoDoobieImpl, Transactor[F]), TagTimestampRepoImpl[F]] =
    Kleisli(args => Sync[F].pure(new TagTimestampRepoImpl[F](args._1, args._2)))
}
