package example.service

import example.repository.UserRepositoryId
import org.scalatest.{FlatSpec, Matchers}

class UserServiceIdTest extends FlatSpec with Matchers {

  "The user service id method calls" should "return valid result" in {
    val userRepositoryId = new UserRepositoryId()
    val userService = new UserServiceId(userRepositoryId)

    val username = "John Smith"
    val userId = 1

    val registeredUser = userService.registerUser(username)
    registeredUser.isRight shouldEqual true
    val existingUser = registeredUser.right.get
    existingUser.id shouldEqual userId
    existingUser.username shouldEqual username

    val nonRegisteredUser = userService.registerUser(username)
    nonRegisteredUser.isLeft shouldEqual true
    nonRegisteredUser shouldEqual Left(s"example.User $existingUser already exists.")

    val retrievedByUsername = userService.getByUsername(username).get
    retrievedByUsername.id shouldEqual userId
    retrievedByUsername.username shouldEqual username

    val retrievedById = userService.getById(userId).get
    retrievedById.id shouldEqual userId
    retrievedById.username shouldEqual username

    userService.getByUsername("Random user name") shouldEqual None
    userService.getById(2) shouldEqual None
  }
}
