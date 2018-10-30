package controllers

import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class StubAuthAction(accountId: Int)(val parser: BodyParser[AnyContent])(implicit val executionContext: ExecutionContext) extends AuthAction {
  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] = {
    block(new AuthRequest(accountId, request))
  }
}

