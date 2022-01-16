package ru.baklanovsoft.simplefeed.core

trait DomainError extends Error {
  def httpStatusCode: Int
}
