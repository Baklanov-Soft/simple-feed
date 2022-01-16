package ru.baklanovsoft.simplefeed.config

import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader
import ru.baklanovsoft.simplefeed.jdbc.model.DBConfig

case class AppConfiguration(db: DBConfig)

object AppConfiguration {
  implicit val coder: ConfigReader[AppConfiguration] = deriveReader[AppConfiguration]
}
