package controllers

import models.Task
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext

class TasksControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  "GET /tasks" should {
    "should response all my task" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task1", "Completed"), Task(3, 2, "other account task", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val task = controller.index().apply(FakeRequest(GET, "/"))

      status(task) mustBe OK
      contentType(task) mustBe Some("application/json")
      contentAsString(task) must include("""[{"id":1,"accountId":1,"name":"task1","status":"NotStarted"},{"id":2,"accountId":1,"name":"task1","status":"Completed"}]""")
    }
    "filter Completed" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task1", "Completed"), Task(3, 2, "other account task", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val task = controller.index().apply(FakeRequest(GET, "/?status=Completed"))

      status(task) mustBe OK
      contentType(task) mustBe Some("application/json")
      contentAsString(task) must include("""[{"id":2,"accountId":1,"name":"task1","status":"Completed"}]""")
    }
    "If illegal status, return all status" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task1", "Completed"), Task(3, 2, "other account task", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val task = controller.index().apply(FakeRequest(GET, "/?status=HogeHoge"))

      status(task) mustBe OK
      contentType(task) mustBe Some("application/json")
      contentAsString(task) must include("""[{"id":1,"accountId":1,"name":"task1","status":"NotStarted"},{"id":2,"accountId":1,"name":"task1","status":"Completed"}]""")
    }
  }

  "GET /tasks/:id" should {
    "should response task id: 2" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val task = controller.show(2).apply(FakeRequest(POST, "/"))

      status(task) mustBe OK
      contentType(task) mustBe Some("application/json")
      contentAsString(task) must include("""{"id":2,"accountId":1,"name":"task2","status":"Completed"}""")
    }
  }

  "POST /tasks" should {
    "should create new task" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val home = controller.create().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"hoge","status": "todo"}""")))

      status(home) mustBe CREATED
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Created")
    }
  }

  "PUT /tasks/:id" should {
    "should update task id: 2" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val home = controller.update(2).apply(FakeRequest(PUT, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"changed","status": "Completed"}""")))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Updated")
    }
  }

  "DELETE /tasks/:id" should {
    "should delete task id: 2" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext
      val stubAuthAction = new StubAuthAction(1)(controllerComponents.parsers.default)(controllerComponents.executionContext)
      val taskRepository = new StubTaskRepository(Seq(Task(1, 1, "task1", "NotStarted"), Task(2, 1, "task2", "Completed")))
      val controller = new TasksController(stubAuthAction, controllerComponents, taskRepository)
      val home = controller.delete(2).apply(FakeRequest(DELETE, "/").withHeaders("Content-type" -> "application/json"))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Deleted")
    }
  }

}
