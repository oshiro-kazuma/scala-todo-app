package controllers

import javax.inject._
import play.api.mvc._
import pdi.jwt.JwtSession._
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

case class Account(id: Int, name: String)

object Account {
  implicit val jsonWrites = Json.writes[Account]
  implicit val jsonReads = Json.reads[Account]
}

class AuthRequest[A](val accountId: Int, request: Request[A]) extends WrappedRequest[A](request)

class AuthAction @Inject()(val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext) extends ActionBuilder[AuthRequest, AnyContent] {
  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] = {
    request.jwtSession.getAs[Account]("account") match {
      case Some(u) => block(new AuthRequest(u.id, request))
      case None => Future.successful {
        Results.Unauthorized("Unauthorized")
      }
    }
  }
}
