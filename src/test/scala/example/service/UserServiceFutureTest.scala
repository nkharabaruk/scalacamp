package example.service

import example.domain.User
import example.repository.UserRepositoryFuture
import org.scalatest.{AsyncFlatSpec, Matchers}

class UserServiceFutureTest extends AsyncFlatSpec with Matchers {

  val userRepositoryFuture = new UserRepositoryFuture()
  val userService = new UserServiceFuture(userRepositoryFuture)
  val username = "John Smith"
  val userId = 1

  "Register user" should "return valid result" in {
    userService.registerUser(username).map { registeredUser =>
      registeredUser.isRight shouldEqual true
      val existingUser = registeredUser.right.get
      existingUser.id shouldEqual userId
      existingUser.username shouldEqual username
    }
  }

  "Register already existed user" should "return error message" in {
    userService.registerUser(username).map { nonRegisteredUser =>
    nonRegisteredUser.isLeft shouldEqual true
    nonRegisteredUser shouldEqual Left(s"example.User ${User(userId, username)} already exists.")
    }
  }

  "Retrieve user by username" should "return valid result" in {
    userService.getByUsername(username).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual userId
      retrievedByUsername.get.username shouldEqual username
    }
  }

  "Retrieve user with invalid username" should "return none" in {
    userService.getByUsername("Random user name").map { nonRegisteredUser =>
      nonRegisteredUser shouldEqual None
    }
  }

  "Retrieve user by id" should "return valid result" in {
    userService.getById(userId).map { retrievedById =>
      retrievedById.get.id shouldEqual userId
      retrievedById.get.username shouldEqual username
    }
  }

  "Retrieve user with invalid id" should "return none" in {
    userService.getById(2).map { nonRegisteredUser =>
      nonRegisteredUser shouldEqual None
    }
  }
}
