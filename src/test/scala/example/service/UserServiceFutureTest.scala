package example.service

import example.domain.User
import example.repository.UserRepositoryFuture
import org.scalatest.{AsyncFlatSpec, Matchers}

class UserServiceFutureTest extends AsyncFlatSpec with Matchers {

  "The user service future method calls" should "return valid result" in {
    val userRepositoryFuture = new UserRepositoryFuture()
    val userService = new UserServiceFuture(userRepositoryFuture)

    val username = "John Smith"
    val userId = 1

    userService.registerUser(username).map { registeredUser =>
      registeredUser.isRight shouldEqual true
      val existingUser = registeredUser.right.get
      existingUser.id shouldEqual userId
      existingUser.username shouldEqual username
    }
    userService.registerUser(username).map { nonRegisteredUser =>
      nonRegisteredUser.isLeft shouldEqual true
      nonRegisteredUser shouldEqual Left(s"example.User ${User(userId, username)} already exists.")
    }
    userService.getByUsername(username).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual userId
      retrievedByUsername.get.username shouldEqual username
    }
    userService.getById(userId).map { retrievedById =>
      retrievedById.get.id shouldEqual userId
      retrievedById.get.username shouldEqual username
    }

    userService.getByUsername("Random user name").map { nonRegisteredUser =>
      nonRegisteredUser shouldEqual None
    }
    userService.getById(2).map { nonRegisteredUser =>
      nonRegisteredUser shouldEqual None
    }
  }
}
