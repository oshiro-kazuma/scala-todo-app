package repositories

import com.google.inject.Inject
import models.Account
import org.mindrot.jbcrypt.BCrypt
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait AccountRepository {

  def all(): Future[Seq[Account]]

  def findByName(name: String): Future[Option[Account]]

  def create(name: String, password: String): Future[Unit]

}

class AccountRepositoryMySQL @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext) extends AccountRepository with HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  private val Accounts = TableQuery[AccountsTable]

  def all(): Future[Seq[Account]] = db.run(Accounts.result)

  def findByName(name: String): Future[Option[Account]] = {
    db.run(Accounts.filter(a => a.name === name).result.headOption)
  }

  def create(name: String, password: String): Future[Unit] = {
    val account = models.Account(0, name, BCrypt.hashpw(password, BCrypt.gensalt()))
    db.run(Accounts += account).map { _ => () }
  }

  private class AccountsTable(tag: Tag) extends Table[Account](tag, "accounts") {

    def id = column[Int]("id", O.PrimaryKey)

    def name = column[String]("name")

    def hashedPassword = column[String]("password")

    def * = (id, name, hashedPassword) <> (Account.tupled, Account.unapply)

  }

}
