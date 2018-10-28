package dao

import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import models.Task
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

class TaskDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Tasks = TableQuery[TasksTable]

  def all(): Future[Seq[Task]] = db.run(Tasks.result)

  def find(id: Int): Future[Option[Task]] = db.run(Tasks.filter(_.id === id).result.headOption)

  def findByAccountId(accountId: Int): Future[Seq[Task]] = db.run(Tasks.filter(_.accountId === accountId).result)

  def insert(task: Task): Future[Unit] = db.run(Tasks += task).map { _ => () }

  def delete(id: Int): Future[Unit] = db.run(Tasks.filter(_.id === id).delete).map { _ => () }

  private class TasksTable(tag: Tag) extends Table[Task](tag, "tasks") {

    def id = column[Int]("id", O.PrimaryKey)

    def accountId = column[Int]("account_id")

    def name = column[String]("name")

    def status = column[String]("status")

    def * = (id, accountId, name, status) <> (Task.tupled, Task.unapply)

  }

}
