package example

import org.scalatest.{FlatSpec, Matchers}

class UserServiceTest extends FlatSpec with Matchers {

  "The user repository method calls" should "return valid result" in {
    val userRepositoryId = new UserRepositoryId()
    val username = "John Smith"
    userRepositoryId.registerUser(username)
    val createdUser = userRepositoryId.getByUsername(username).get
    createdUser.id shouldEqual 1
    createdUser.username shouldEqual username
  }
}
