package modules

import com.google.inject.AbstractModule
import controllers.{AuthAction, AuthActionImpl}

class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[AuthAction]).to(classOf[AuthActionImpl])
  }
}

