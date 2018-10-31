package controllers

import javax.inject._
import pdi.jwt.JwtSession
import play.api.libs.json.Json
import play.api.mvc._
import repositories.AccountRepository

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class AuthController @Inject()(cc: ControllerComponents, accountRepository: AccountRepository)
                              (implicit executionContext: ExecutionContext) extends AbstractController(cc) {

  import scalaz._
  import Scalaz._

  def login() = Action.async(parse.json) { implicit request =>
    request.body.validate[LoginRequest].asOpt match {
      case None => Future.successful(BadRequest(Json.toJson("Invalid json")))
      case Some(r) =>
        for (account <- accountRepository.findByName(r.name)) yield account match {
          case Some(act) if act.validate(r.password)  =>
            val token = JwtSession() + ("account", AuthAccount(act.id, act.name))
            Ok(Json.toJson(LoginResponse(token.serialize)))
          case _ => Unauthorized(Json.toJson("Unauthorized"))
        }
    }
  }

  def register() = Action.async(parse.json) { implicit request =>
    request.body.validate[AccountRegisterRequest].asOpt match {
      case None => Future.successful(BadRequest(Json.toJson("Invalid json")))
      case Some(r) =>
        (for {
          _ <- EitherT(accountExits(r))
          _ <- EitherT(registerAccount(r))
          act <- EitherT(findAccount(r))
        } yield act).run.map {
          case -\/(result) => result
          case \/-(act) =>
            val token = JwtSession() + ("account", AuthAccount(act.id, act.name))
            Created(Json.toJson(LoginResponse(token.serialize)))
        }
    }
  }

  private def accountExits(req: AccountRegisterRequest) = {
    accountRepository.findByName(req.name).map {
      case None => Unit.right[Status]
      case Some(_) => Conflict(Json.toJson(s"${req.name} is already registered")).left[Unit]
    }
  }

  private def registerAccount(req: AccountRegisterRequest) = {
    accountRepository.create(req.name, req.password).map(_ => Unit.right[Status]).recover {
      case NonFatal(_) => InternalServerError(Json.toJson("Account registration failed")).left[Unit]
    }
  }

  private def findAccount(req: AccountRegisterRequest) = {
    accountRepository.findByName(req.name).map {
      case Some(a) => a.right[Status]
      case None => InternalServerError(Json.toJson("Account registration failed")).left[models.Account]
    }
  }
}

