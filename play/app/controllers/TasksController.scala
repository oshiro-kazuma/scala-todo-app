package controllers

import javax.inject._
import models.{Task, TaskStatus}
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
    val taskStatus = request.getQueryString("status").map(TaskStatus.apply)
    for {
      tasks <- taskStatus match {
        case None | Some(TaskStatus.Unknown) =>
          taskRepository.findByAccountId(request.accountId)
        case Some(s) =>
          taskRepository.findByAccountIdAndStatus(request.accountId, s)
      }
    } yield {
      Ok(Json.toJson(tasks.map(t => TaskResponse(t.id, t.accountId, t.name, t.status))))
    }
  }

  def create() = authAction.async(parse.json) { implicit request =>
    request.body.validate[TaskStoreRequest].asOpt match {
      case Some(t) if TaskStatus(t.status) != TaskStatus.Unknown => {
        for (_ <- taskRepository.create(Task(0, request.accountId, t.name, t.status))) yield Created(Json.toJson("Created"))
      }
      case _ => BadRequest(Json.toJson("Bad request")).pure[Future]
    }
  }

  def show(id: Int) = authAction.async { implicit request =>
    for (task <- taskRepository.find(id)) yield task match {
      case Some(t) if t.accountId == request.accountId => Ok(Json.toJson(TaskResponse(t.id, t.accountId, t.name, t.status)))
      case _ => NotFound(Json.toJson("Not found"))
    }
  }

  def delete(id: Int) = authAction.async { implicit request =>
    (for {
      task <- OptionT(taskRepository.find(id))
      if task.accountId == request.accountId
      _ <- taskRepository.delete(task.id).liftM[OptionT]
    } yield Ok(Json.toJson("Deleted"))).run.map {
      case Some(r) => r
      case None => NotFound(Json.toJson("Not found"))
    }
  }

  def update(id: Int) = authAction.async(parse.json) { implicit request =>
    request.body.validate[TaskStoreRequest].asOpt match {
      case Some(task) if TaskStatus(task.status) != TaskStatus.Unknown => {
        for {
          record <- OptionT(taskRepository.find(id))
          if record.accountId == request.accountId
          _ <- taskRepository.update(Task(record.id, record.accountId, task.name, task.status)).liftM[OptionT]
        } yield Ok(Json.toJson("Updated"))
      }.run.map {
        case Some(r) => r
        case None => NotFound(Json.toJson("Not Found"))
      }
      case _ => BadRequest(Json.toJson("Bad request")).pure[Future]
    }
  }
}

