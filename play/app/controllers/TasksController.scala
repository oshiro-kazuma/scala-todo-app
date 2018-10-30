package controllers

import dao.TaskDAO
import javax.inject._
import models.Task
import play.api.libs.json.Json
import play.api.mvc._
import pdi.jwt.JwtSession._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TasksController @Inject()(authAction: AuthAction, cc: ControllerComponents, taskDAO: TaskDAO)
                               (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  import scalaz._, Scalaz._

  def index() = authAction.async { implicit request =>
    request.jwtSession.getAs[Account]("account")
    for (tasks <- taskDAO.findByAccountId(request.accountId)) yield {
      Ok(Json.toJson(tasks.map(t => TaskResponse(t.id, t.accountId, t.name, t.status))))
    }
  }

  def create() = authAction.async(parse.json) { implicit request =>
    request.body.validate[TaskStoreRequest].asOpt match {
      case Some(t) => for (_ <- taskDAO.insert(Task(0, request.accountId, t.name, t.status))) yield Created("created")
      case None => BadRequest("Bad request").pure[Future]
    }
  }

  def show(id: Int) = authAction.async { implicit request =>
    for (task <- taskDAO.find(id)) yield task match {
      case Some(t) if t.accountId == request.accountId => Ok(Json.toJson(TaskResponse(t.id, t.accountId, t.name, t.status)))
      case _ => NotFound("Not found")
    }
  }

  def delete(id: Int) = authAction.async { implicit request =>
    (for {
      task <- OptionT(taskDAO.find(id))
      if task.accountId == request.accountId
      _ <- taskDAO.delete(task.id).liftM[OptionT]
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
          record <- OptionT(taskDAO.find(id))
          if record.accountId == request.accountId
          _ <- taskDAO.update(Task(record.id, record.accountId, task.name, task.status)).liftM[OptionT]
        } yield Ok("Updated")
      }.run.map {
        case Some(r) => r
        case None => NotFound("Not Found")
      }
    }
  }
}

