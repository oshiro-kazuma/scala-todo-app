package modules

import com.google.inject.AbstractModule
import controllers.{AuthAction, AuthActionImpl}
import repositories.{AccountRepository, AccountRepositoryMySQL, TaskRepository, TaskRepositoryMySQL}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[AuthAction]).to(classOf[AuthActionImpl])
    bind(classOf[TaskRepository]).to(classOf[TaskRepositoryMySQL])
    bind(classOf[AccountRepository]).to(classOf[AccountRepositoryMySQL])
  }
}

