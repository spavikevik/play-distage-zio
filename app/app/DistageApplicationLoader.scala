package app

import controllers.AssetsComponents
import controllers.HomeController
import distage.Activation
import distage.Injector
import distage.ModuleDef
import izumi.distage.model.plan.Roots
import play.api.Application
import play.api.ApplicationLoader
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.ContextBasedBuiltInComponents
import play.api.LoggerConfigurator
import play.api.mvc.ControllerComponents
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import router.Routes
import zio.*

class DistageApplicationLoader extends ApplicationLoader {

  override def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }

    def componentsModule = new ModuleDef {
      make[ContextBasedBuiltInComponents].fromValue {
        new BuiltInComponentsFromContext(context) with HttpFiltersComponents with AssetsComponents {
          override def router: Router = Router.empty
        }
      }
    }

    val module = componentsModule ++ new ModuleDef {
      make[ControllerComponents].from((components: ContextBasedBuiltInComponents) => components.controllerComponents)
      make[HomeController]
      modify[ContextBasedBuiltInComponents].by(_.flatAp {
        (homeController: HomeController) => (current: ContextBasedBuiltInComponents) =>
          new BuiltInComponentsFromContext(context) with HttpFiltersComponents with AssetsComponents {
            override def router: Router = current.router.orElse(new Routes(httpErrorHandler, homeController, assets))
          }
      })
      make[Application].from((components: ContextBasedBuiltInComponents) => components.application)
    }

    val injector = Injector[Task]()

    val plan = injector
      .plan(
        bindings = module,
        activation = Activation.empty,
        roots = Roots.target[Application]
      )
      .getOrThrow()

    val resource = injector.produce(plan)

    Unsafe.unsafe { implicit unsafe =>
      // Use ZIO runtime to run the effect
      val result = Runtime.default.unsafe.run {
        resource.use(locator => ZIO.attempt(locator.get[Application]))
      }

      result.getOrThrowFiberFailure()
    }
  }
}
