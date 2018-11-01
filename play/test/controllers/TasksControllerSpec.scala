package controllers

import models.Task
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext

class TasksControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  val controllerComponents: ControllerComponents = stubControllerComponents()
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  "GET /tasks" should {
    val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)
    val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task1", "Completed"), Task(3, 1, "task1", "InProgress"), Task(4, 2, "other account task", "Completed")))
    val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)

    "should response all my task" in {
      val actual = controller.index().apply(FakeRequest(GET, "/"))
      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("""[{"id":1,"accountId":1,"name":"task1","status":"NotStarted"},{"id":2,"accountId":1,"name":"task1","status":"Completed"},{"id":3,"accountId":1,"name":"task1","status":"InProgress"}]""")
    }
    "filter Completed" in {
      val actual = controller.index().apply(FakeRequest(GET, "/?status=Completed"))
      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("""[{"id":2,"accountId":1,"name":"task1","status":"Completed"}]""")
    }
    "filter Not Completed" in {
      val actual = controller.index().apply(FakeRequest(GET, "/?status=NotStarted,InProgress"))
      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("""[{"id":1,"accountId":1,"name":"task1","status":"NotStarted"},{"id":3,"accountId":1,"name":"task1","status":"InProgress"}]""")
    }
    "If illegal status, return all status" in {
      val actual = controller.index().apply(FakeRequest(GET, "/?status=HogeHoge"))
      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("""[{"id":1,"accountId":1,"name":"task1","status":"NotStarted"},{"id":2,"accountId":1,"name":"task1","status":"Completed"},{"id":3,"accountId":1,"name":"task1","status":"InProgress"}]""")
    }
  }

  "GET /tasks/:id" should {
    "should response task id: 2" in {
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val actual = controller.show(2).apply(FakeRequest(POST, "/"))

      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("""{"id":2,"accountId":1,"name":"task2","status":"Completed"}""")
    }
  }

  "POST /tasks" should {
    val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
    val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
    val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)

    "create new task" in {
      val actual = controller.create().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"hoge","status": "NotStarted"}""")))
      status(actual) mustBe CREATED
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("Created")
    }
    "Illegal status return bad request" in {
      val actual = controller.create().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"hoge","status": "BadStatus"}""")))
      status(actual) mustBe BAD_REQUEST
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("Bad request")
    }
  }

  "PUT /tasks/:id" should {
    "should update task id: 2" in {
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val actual = controller.update(2).apply(FakeRequest(PUT, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"changed","status": "Completed"}""")))

      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("Updated")
    }
  }

  "DELETE /tasks/:id" should {
    "should delete task id: 2" in {
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val actual = controller.delete(2).apply(FakeRequest(DELETE, "/").withHeaders("Content-type" -> "application/json"))

      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("Deleted")
    }
  }

}
