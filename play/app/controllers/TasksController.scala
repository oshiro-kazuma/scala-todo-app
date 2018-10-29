package controllers

import dao.TaskDAO
import javax.inject._
import models.Task
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TasksController @Inject()(cc: ControllerComponents, taskDAO: TaskDAO)
                               (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  import scalaz._, Scalaz._

  def index() = Action.async { implicit request: Request[AnyContent] =>
    for (tasks <- taskDAO.findByAccountId(1)) yield {
      Ok(Json.toJson(tasks.map(t => TaskResponse(t.id, t.accountId, t.name, t.status))))
    }
  }

  def create() = Action.async(parse.json) { implicit request =>
    request.body.validate[TaskStoreRequest].asOpt match {
      case Some(t) => for (_ <- taskDAO.insert(Task(0, 1, t.name, t.status))) yield Created("created")
      case None => BadRequest("Bad request").pure[Future]
    }
  }

  def show(id: Int) = Action.async { implicit request: Request[AnyContent] =>
    for (task <- taskDAO.find(id)) yield task match {
      case Some(t) if t.accountId == 1 => Ok(Json.toJson(TaskResponse(t.id, t.accountId, t.name, t.status)))
      case _ => NotFound("Not found")
    }
  }

  def delete(id: Int) = Action.async { implicit request: Request[AnyContent] =>
    (for {
      task <- OptionT(taskDAO.find(id))
      if task.accountId == 1
      _ <- taskDAO.delete(task.id).liftM[OptionT]
    } yield Ok("Deleted")).run.map {
      case Some(r) => r
      case None => NotFound("Not found")
    }
  }

  def update(id: Int) = Action.async(parse.json) { implicit request =>
    request.body.validate[TaskStoreRequest].asOpt match {
      case None => BadRequest("Bad request").pure[Future]
      case Some(task) => {
        for {
          record <- OptionT(taskDAO.find(id))
          if record.accountId == 1
          _ <- taskDAO.update(Task(record.id, record.accountId, task.name, task.status)).liftM[OptionT]
        } yield Ok("Updated")
      }.run.map {
        case Some(r) => r
        case None => NotFound("Not Found")
      }
    }
  }
}
