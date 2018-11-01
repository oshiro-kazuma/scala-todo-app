package controllers

import models.Task
import models.TaskStatus.TaskStatus
import play.api.mvc._
import repositories.TaskRepository

import scala.concurrent.{ExecutionContext, Future}

class StubAuthAction(accountId: Int)(val parser: BodyParser[AnyContent])(implicit val executionContext: ExecutionContext) extends AuthAction {
  override def invokeBlock[A](request: Request[A], block: AuthRequest[A] => Future[Result]): Future[Result] = {
    block(new AuthRequest(accountId, request))
  }
}

class StubTaskRepository(var tasks: Seq[Task]) extends TaskRepository {
  override def all(): Future[Seq[Task]] = Future.successful(tasks)

  override def find(id: Int): Future[Option[Task]] = Future.successful(tasks.find(_.id == id))

  override def findByAccountId(accountId: Int): Future[Seq[Task]] = Future.successful(tasks.filter(_.accountId == accountId))

  override def findByAccountIdAndStatus(accountId: Int, status: TaskStatus): Future[Seq[Task]] = Future.successful {
    tasks.filter(t => t.accountId == accountId && t.status == status.value)
  }

  override def create(task: Task): Future[Unit] = Future.successful {
    if (tasks.exists(_.id == task.id)) throw new RuntimeException("Duplicate")
  }

  override def update(task: Task): Future[Unit] = Future.successful {
    tasks = tasks.filterNot(_.id == task.id) :+ task
  }

  override def delete(id: Int): Future[Unit] = Future.successful {
    tasks = tasks.filterNot(_.id == id)
  }
}

