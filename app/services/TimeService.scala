package services

import zio.*

import java.time.OffsetDateTime

trait TimeService {
  def currentTime: Task[OffsetDateTime]
}

object TimeService {

  def live: ZLayer[Clock, Nothing, TimeService] = ZLayer.fromZIO(
    ZIO.serviceWith[Clock](clock =>
      new TimeService {
        override def currentTime: Task[OffsetDateTime] = clock.currentDateTime
      }
    )
  )
}
