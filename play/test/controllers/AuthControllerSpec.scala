package controllers

import org.mindrot.jbcrypt.BCrypt
import org.mockito.Mockito._
import org.mockito.ArgumentMatchersSugar
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.test.Helpers._
import play.api.test._
import repositories.AccountRepository

import scala.concurrent.{ExecutionContext, Future}

class AuthControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar with ArgumentMatchersSugar {

  "POST /auth/login" should {
    "Login with name and pass should succeed" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext

      val mockAccountRepository = mock[AccountRepository]
      val password = BCrypt.hashpw("pass123", BCrypt.gensalt())
      when(mockAccountRepository.findByName("oshiro")) thenReturn Future.successful(Some(models.Account(1, "oshiro", password)))

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val home = controller.login().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"oshiro","password": "pass123"}""")))

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("{\"token\":")
      verify(mockAccountRepository, times(1)).findByName("oshiro")
    }
    "Login with bad name should fail" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext

      val mockAccountRepository = mock[AccountRepository]
      when(mockAccountRepository.findByName("bad_user")) thenReturn Future.successful(None)

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val home = controller.login().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"bad_user","password": "pass123"}""")))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Unauthorized")
      verify(mockAccountRepository, times(1)).findByName("bad_user")
    }
    "Login with bad pass should fail" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext

      val mockAccountRepository = mock[AccountRepository]
      val password = BCrypt.hashpw("pass123", BCrypt.gensalt())
      when(mockAccountRepository.findByName("oshiro")) thenReturn Future.successful(Some(models.Account(1, "oshiro", password)))

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val home = controller.login().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"oshiro","password": "bad_pass"}""")))

      status(home) mustBe UNAUTHORIZED
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("Unauthorized")
      verify(mockAccountRepository, times(1)).findByName("oshiro")
    }
  }

  "POST /auth/register" should {
    "User creation should be successful" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext

      val mockAccountRepository = mock[AccountRepository]
      val salt = BCrypt.gensalt()
      when(mockAccountRepository.findByName("new_user"))
        .thenReturn(Future.successful(None))
        .thenReturn(Future.successful(Some(models.Account(1, "new_user", "dummy_pass"))))
      when(mockAccountRepository.create("new_user", "new_pass")) thenReturn Future.successful(())

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val home = controller.register().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"new_user","password": "new_pass"}""")))

      status(home) mustBe CREATED
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("{\"token\":")
      verify(mockAccountRepository, times(2)).findByName(any[String])
      verify(mockAccountRepository, times(1)).create("new_user", "new_pass")
    }
    "Failure with already used name" in {
      val controllerComponents = stubControllerComponents()
      implicit val ec: ExecutionContext = controllerComponents.executionContext

      val mockAccountRepository = mock[AccountRepository]
      val password = BCrypt.hashpw("pass123", BCrypt.gensalt())
      when(mockAccountRepository.findByName("oshiro")) thenReturn Future.successful(Some(models.Account(1, "oshiro", password)))

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val home = controller.register().apply(FakeRequest(POST, "/").withHeaders("Content-type" -> "application/json").withBody(Json.parse("""{"name":"oshiro","password": "pass123"}""")))

      status(home) mustBe CONFLICT
      contentType(home) mustBe Some("application/json")
      contentAsString(home) must include("oshiro is already registered")
      verify(mockAccountRepository, times(1)).findByName("oshiro")
    }
  }

}
