package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._
import repositories.TaskRepository

import scala.concurrent.ExecutionContext

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class TasksControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  "TaskController GET" should {

    "/tasks" in {
      val controllerComponents = stubControllerComponents()
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val controller = new TasksController(stubAuthAction, controllerComponents, inject[TaskRepository])
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Ok")
    }

    "/tasks/1" in {
      val controllerComponents = stubControllerComponents()
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val controller = new TasksController(stubAuthAction, controllerComponents, inject[TaskRepository])
      val home = controller.show(11).apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("""{"id":11,"accountId":1,"name":"xxx","status":"todxo"}""")
    }

  }
}
