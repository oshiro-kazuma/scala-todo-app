package controllers

import javax.inject._
import play.api.mvc._
import pdi.jwt.JwtSession._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

case class AuthAccount(id: Int, name: String)

object AuthAccount {
  implicit val jsonWrites = Json.writes[AuthAccount]
  implicit val jsonReads = Json.reads[AuthAccount]
}

class AuthRequest[A](val accountId: Int, request: Request[A]) extends WrappedRequest[A](request)

trait AuthAction extends ActionBuilder[AuthRequest, AnyContent] {
  def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result]
}

class AuthActionImpl @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext) extends AuthAction {
  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] = {
    request.jwtSession.getAs[AuthAccount]("account") match {
      case Some(u) => block(new AuthRequest(u.id, request))
      case None => Future.successful {
        Results.Unauthorized(Json.toJson("Unauthorized"))
      }
    }
  }
}
