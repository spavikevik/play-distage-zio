package controllers

import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import services.TimeService
import zio.Runtime
import zio.Unsafe

import java.time.format.DateTimeFormatter

class TimeController(val controllerComponents: ControllerComponents, timeService: TimeService, runtime: Runtime[Any])
    extends BaseController {

  def index() = Action.async(request =>
    Unsafe.unsafe { implicit unsafe =>
      runtime.unsafe.runToFuture(
        timeService.currentTime
          .map(time => Ok(s"The current time is ${time.format(DateTimeFormatter.ofPattern("HH:mm:ss"))}"))
      )
    }
  )
}
