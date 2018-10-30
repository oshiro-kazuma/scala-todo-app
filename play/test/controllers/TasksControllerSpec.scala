package controllers

import models.Task
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.test.Helpers._
import play.api.test._

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
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task1", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("""[{"id":1,"accountId":1,"name":"task1","status":"NotStarted"},{"id":2,"accountId":1,"name":"task1","status":"Completed"}]""")
    }

    "/tasks/2" in {
      val controllerComponents = stubControllerComponents()
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val home = controller.show(2).apply(FakeRequest(GET, "/2"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("""{"id":2,"accountId":1,"name":"task2","status":"Completed"}""")
    }

  }
}
