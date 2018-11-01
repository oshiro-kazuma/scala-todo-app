package controllers

import org.mindrot.jbcrypt.BCrypt
import org.mockito.Mockito._
import org.mockito.ArgumentMatchersSugar
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.api.test.Helpers._
import play.api.test._
import repositories.AccountRepository

import scala.concurrent.{ExecutionContext, Future}

class AuthControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar with ArgumentMatchersSugar {

  val controllerComponents: ControllerComponents = stubControllerComponents()
  implicit val ec: ExecutionContext = controllerComponents.executionContext

  "POST /auth/login" should {
    "Login with name and pass should succeed" in {
      val loginAccountName = "oshiro"
      val mockAccountRepository = mock[AccountRepository]
      val password = BCrypt.hashpw("pass123", BCrypt.gensalt())
      when(mockAccountRepository.findByName(loginAccountName)) thenReturn Future.successful(Some(models.Account(1, loginAccountName, password)))

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val actual = controller.login().apply(FakeRequest(POST, "/")
        .withHeaders("Content-type" -> "application/json")
        .withBody(Json.parse("""{"name":"oshiro","password": "pass123"}""")))

      status(actual) mustBe OK
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("{\"token\":")
      verify(mockAccountRepository, times(1)).findByName(loginAccountName)
    }
    "Login with bad name should fail" in {
      val mockAccountRepository = mock[AccountRepository]
      when(mockAccountRepository.findByName("bad_user")) thenReturn Future.successful(None)

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val actual = controller.login().apply(FakeRequest(POST, "/")
        .withHeaders("Content-type" -> "application/json")
        .withBody(Json.parse("""{"name":"bad_user","password": "pass123"}""")))

      status(actual) mustBe UNAUTHORIZED
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("Unauthorized")
      verify(mockAccountRepository, times(1)).findByName("bad_user")
    }
    "Login with bad pass should fail" in {
      val loginAccountName = "oshiro"
      val mockAccountRepository = mock[AccountRepository]
      val password = BCrypt.hashpw("pass123", BCrypt.gensalt())
      when(mockAccountRepository.findByName(loginAccountName)) thenReturn Future.successful(Some(models.Account(1, loginAccountName, password)))

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val actual = controller.login().apply(FakeRequest(POST, "/")
        .withHeaders("Content-type" -> "application/json")
        .withBody(Json.parse("""{"name":"oshiro","password": "bad_pass"}""")))

      status(actual) mustBe UNAUTHORIZED
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("Unauthorized")
      verify(mockAccountRepository, times(1)).findByName(loginAccountName)
    }
  }

  "POST /auth/register" should {
    "User creation should be successful" in {
      val mockAccountRepository = mock[AccountRepository]
      when(mockAccountRepository.findByName("new_user"))
        .thenReturn(Future.successful(None))
        .thenReturn(Future.successful(Some(models.Account(1, "new_user", "dummy_pass"))))
      when(mockAccountRepository.create("new_user", "new_pass")) thenReturn Future.successful(())

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val actual = controller.register().apply(FakeRequest(POST, "/")
        .withHeaders("Content-type" -> "application/json")
        .withBody(Json.parse("""{"name":"new_user","password": "new_pass"}""")))

      status(actual) mustBe CREATED
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("{\"token\":")
      verify(mockAccountRepository, times(2)).findByName(any[String])
      verify(mockAccountRepository, times(1)).create("new_user", "new_pass")
    }
    "Failure with already used name" in {
      val loginAccountName = "oshiro"
      val mockAccountRepository = mock[AccountRepository]
      val password = BCrypt.hashpw("pass123", BCrypt.gensalt())
      when(mockAccountRepository.findByName(loginAccountName)) thenReturn Future.successful(Some(models.Account(1, loginAccountName, password)))

      val controller = new AuthController(controllerComponents, mockAccountRepository)
      val actual = controller.register().apply(FakeRequest(POST, "/")
        .withHeaders("Content-type" -> "application/json")
        .withBody(Json.parse("""{"name":"oshiro","password": "pass123"}""")))

      status(actual) mustBe CONFLICT
      contentType(actual) mustBe Some("application/json")
      contentAsString(actual) must include("oshiro is already registered")
      verify(mockAccountRepository, times(1)).findByName(loginAccountName)
    }
  }

}
