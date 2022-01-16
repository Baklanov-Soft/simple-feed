package ru.baklanovsoft.simplefeed.jdbc.repos

import cats.effect.{IO, Resource}
import org.h2.tools.Server
import ru.baklanovsoft.simplefeed.core.records.TagTimestamp
import ru.baklanovsoft.simplefeed.core.repos.TagTimestampRepo.{DuplicateTag, TagNotFound}
import ru.baklanovsoft.simplefeed.jdbc.app.{Migrator, Transactor => AppTransactor}
import ru.baklanovsoft.simplefeed.jdbc.model.DBConfig
import ru.baklanovsoft.simplefeed.repos.TagTimestampRepoImpl
import weaver.IOSuite

import java.time.Instant
import scala.concurrent.duration._
import scala.util.Random

object TagTimestampRepoDoobieImplSpec extends IOSuite {

  override type Res = TagTimestampRepoImpl[IO]

  // resource will be constructed once for suite thanks to weaver.IOSuite
  override def sharedResource: Resource[IO, TagTimestampRepoImpl[IO]] =
    for {
      // tcp server will release port after test exit (webServer won't somehow)
      _ <- Resource.make(IO(Server.createTcpServer().start())) { s =>
             IO(println("Closing the db connection")) >> IO(s.shutdown())
           }

      dbConfig = DBConfig(
                   driver = "org.h2.Driver",
                   url = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", // delay -1 will make data persistent between connections
                   user = "",
                   password = "",
                   migrationsLocation = "flyway",
                   threads = 3,
                   migrateOnStart = true
                 )

      transactor <- {
        for {
          _ <- Migrator.migrate[IO].mapF(Resource.eval)
          t <- AppTransactor.make[IO]
        } yield t
      }.run(dbConfig)

      repo <- Resource.eval(TagTimestampRepoImpl.make[IO].run(new TagTimestampRepoDoobieImpl -> transactor))

    } yield repo

  test("crud methods") { repo =>
    val testTag = TagTimestamp("crud", Instant.now())

    for {
      createSuccess <- repo.create(testTag.tag, testTag.lastVisited).map(r => expect(r == testTag))
      listSuccess   <- repo.list().compile.toList.map(l => expect(l.contains(testTag)))
      lookupSuccess <- repo.lookup(testTag.tag).map(o => expect(o.contains(testTag)))
      readSuccess   <- repo.get(testTag.tag).map(t => expect(t == testTag))

      _ <- IO.sleep(10.millis)

      updateSuccess <- repo
                         .update(testTag.tag, Instant.now())
                         .map(t => expect(t.lastVisited.toEpochMilli > testTag.lastVisited.toEpochMilli))

      _             <- repo.delete(testTag.tag)
      deleteSuccess <- repo.list().compile.toList.map(l => expect(!l.contains(testTag)))

    } yield createSuccess && listSuccess && lookupSuccess && readSuccess && updateSuccess && deleteSuccess
  }

  test("errors") { repo =>
    val testTag = TagTimestamp("errors", Instant.now())

    for {
      getFailure    <- repo.get(testTag.tag).attempt.map(e => expect(e == Left(TagNotFound)))
      _             <- repo.create(testTag.tag, testTag.lastVisited).map(r => expect(r == testTag))
      createFailure <- repo.create(testTag.tag, Instant.now()).attempt.map(e => expect(e == Left(DuplicateTag)))
      deleteFailure <- repo.delete(Random.nextString(10)).attempt.map(e => expect(e == Left(TagNotFound)))
      updateFailure <-
        repo.update(Random.nextString(10), Instant.now()).attempt.map(e => expect(e == Left(TagNotFound)))
    } yield getFailure && createFailure && deleteFailure && updateFailure
  }

}
