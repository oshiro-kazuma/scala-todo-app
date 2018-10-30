package repositories

import com.google.inject.Inject
import models.Task
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted

import scala.concurrent.{ExecutionContext, Future}

trait TaskRepository {

  def all(): Future[Seq[Task]]

  def find(id: Int): Future[Option[Task]]

  def findByAccountId(accountId: Int): Future[Seq[Task]]

  def insert(task: Task): Future[Unit]

  def update(task: Task): Future[Unit]

  def delete(id: Int): Future[Unit]

}

class TaskRepositoryMySQL @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends TaskRepository with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Tasks = lifted.TableQuery[TasksTable]

  def all(): Future[Seq[Task]] = db.run(Tasks.result)

  def find(id: Int): Future[Option[Task]] = db.run(Tasks.filter(_.id === id).result.headOption)

  def findByAccountId(accountId: Int): Future[Seq[Task]] = db.run(Tasks.filter(_.accountId === accountId).result)

  def insert(task: Task): Future[Unit] = db.run(Tasks += task).map { _ => () }

  def update(task: Task): Future[Unit] = db.run(Tasks.filter(_.id === task.id).update(task)).map { _ => () }

  def delete(id: Int): Future[Unit] = db.run(Tasks.filter(_.id === id).delete).map { _ => () }

  private class TasksTable(tag: Tag) extends Table[Task](tag, "tasks") {

    def id = column[Int]("id", O.PrimaryKey)

    def accountId = column[Int]("account_id")

    def name = column[String]("name")

    def status = column[String]("status")

    def * = (id, accountId, name, status) <> (Task.tupled, Task.unapply)

  }

}
