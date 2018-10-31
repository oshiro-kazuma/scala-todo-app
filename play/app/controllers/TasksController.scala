package controllers

import javax.inject._
import models.Task
import play.api.libs.json.Json
import play.api.mvc._
import repositories.TaskRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TasksController @Inject()(authAction: AuthAction, cc: ControllerComponents, taskRepository: TaskRepository)
                               (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  import scalaz._
  import Scalaz._

  def index() = authAction.async { implicit request =>
    for (tasks <- taskRepository.findByAccountId(request.accountId)) yield {
      Ok(Json.toJson(tasks.map(t => TaskResponse(t.id, t.accountId, t.name, t.status))))
    }
  }

  def create() = authAction.async(parse.json) { implicit request =>
    request.body.validate[TaskStoreRequest].asOpt match {
      case Some(t) => for (_ <- taskRepository.insert(Task(0, request.accountId, t.name, t.status))) yield Created("created")
      case None => BadRequest("Bad request").pure[Future]
    }
  }

  def show(id: Int) = authAction.async { implicit request =>
    for (task <- taskRepository.find(id)) yield task match {
      case Some(t) if t.accountId == request.accountId => Ok(Json.toJson(TaskResponse(t.id, t.accountId, t.name, t.status)))
      case _ => NotFound("Not found")
    }
  }

  def delete(id: Int) = authAction.async { implicit request =>
    (for {
      task <- OptionT(taskRepository.find(id))
      if task.accountId == request.accountId
      _ <- taskRepository.delete(task.id).liftM[OptionT]
    } yield Ok("Deleted")).run.map {
      case Some(r) => r
      case None => NotFound("Not found")
    }
  }

  def update(id: Int) = authAction.async(parse.json) { implicit request =>
    request.body.validate[TaskStoreRequest].asOpt match {
      case None => BadRequest("Bad request").pure[Future]
      case Some(task) => {
        for {
          record <- OptionT(taskRepository.find(id))
          if record.accountId == request.accountId
          _ <- taskRepository.update(Task(record.id, record.accountId, task.name, task.status)).liftM[OptionT]
        } yield Ok("Updated")
      }.run.map {
        case Some(r) => r
        case None => NotFound("Not Found")
      }
    }
  }
}

