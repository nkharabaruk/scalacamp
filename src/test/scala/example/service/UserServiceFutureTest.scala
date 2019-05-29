package example.service

import example.domain.User
import example.repository.UserRepositoryFuture
import org.scalatest.{AsyncFlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

/**
  * Integration level testing of the User Service Future.
  */
class UserServiceFutureTest extends AsyncFlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val userRepository = new UserRepositoryFuture(db)
  db.run(userRepository.users.schema.create)
  private val userService = new UserServiceFuture(userRepository)
  private val username = "John Smith"
  private val address = Option("Philadelphia, PA 19101")
  private val email = "john_smith@gmail.com"
  private val userId = 1

  "Register user" should "return valid result" in {
    userService.registerUser(username, address, email).map { registeredUser =>
      registeredUser.isRight shouldEqual true
      val existingUser = registeredUser.right.get
      existingUser.id shouldEqual userId
      existingUser.username shouldEqual username
      existingUser.address shouldEqual address
      existingUser.email shouldEqual email
    }
  }

  "Register already existed user" should "return error message" in {
    userService.registerUser(username, address, email).map { nonRegisteredUser =>
    nonRegisteredUser.isLeft shouldEqual true
    nonRegisteredUser shouldEqual Left(s"example.User ${User(userId, username, address, email)} already exists.")
    }
  }

  "Retrieve user by username" should "return valid result" in {
    userService.getByUsername(username).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual userId
      retrievedByUsername.get.username shouldEqual username
      retrievedByUsername.get.address shouldEqual address
      retrievedByUsername.get.email shouldEqual email
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
      retrievedById.get.address shouldEqual address
      retrievedById.get.email shouldEqual email
    }
  }

  "Retrieve user with invalid id" should "return none" in {
    userService.getById(2).map { nonRegisteredUser =>
      nonRegisteredUser shouldEqual None
    }
  }

  "Retrieve all users" should "return not empty collection" in {
    userService.getAll.map { emptyUsers =>
      emptyUsers.isEmpty shouldEqual true
    }

    val user = User(1, username, address, email)
    userService.registerUser(user.username, user.address, user.email).map { registeredUser =>
      registeredUser shouldEqual user
    }

    userService.getAll.map { allUsers =>
      allUsers.nonEmpty shouldEqual true
      allUsers.head shouldEqual user
    }
  }
}
