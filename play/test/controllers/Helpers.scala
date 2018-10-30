package controllers

import models.Task
import play.api.mvc._
import repositories.TaskRepository

import scala.concurrent.{ExecutionContext, Future}

class StubAuthAction(accountId: Int)(val parser: BodyParser[AnyContent])(implicit val executionContext: ExecutionContext) extends AuthAction {
  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] = {
    block(new AuthRequest(accountId, request))
  }
}

class StubTaskRepository(var data: Seq[Task]) extends TaskRepository {
  override def all(): Future[Seq[Task]] = Future.successful(data)

  override def find(id: Int): Future[Option[Task]] = Future.successful(data.find(_.id == id))

  override def findByAccountId(accountId: Int): Future[Seq[Task]] = Future.successful(data.filter(_.accountId == accountId))

  override def insert(task: Task): Future[Unit] = Future.successful {
    if (data.exists(_.id == task.id)) throw new RuntimeException("Duplicate")
  }

  override def update(task: Task): Future[Unit] = Future.successful {
    data = data.filterNot(_.id == task.id) :+ task
  }

  override def delete(id: Int): Future[Unit] = Future.successful {
    data = data.filterNot(_.id == id)
  }
}

