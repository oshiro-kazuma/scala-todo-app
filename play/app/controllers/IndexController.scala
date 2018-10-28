package controllers

import dao.AccountDAO
import javax.inject._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Singleton
class IndexController @Inject()(cc: ControllerComponents, accountDAO: AccountDAO)
                               (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  def index() = Action.async { implicit request: Request[AnyContent] =>
    for {
      accounts <- accountDAO.all()
    } yield {
      Ok(Json.toJson(
        accounts.map(a => Map(
          "id" -> a.id.toString,
          "name" -> a.name,
          "mail" -> a.mail
        ))
      ))
    }
  }

}
