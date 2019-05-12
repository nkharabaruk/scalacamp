package example.repository

import org.scalatest.{FlatSpec, Matchers}

class UserRepositoryIdTest extends FlatSpec with Matchers {

  "The user repository id method calls" should "return valid result" in {
    val userRepositoryId = new UserRepositoryId()

    val johnUsername = "John Smith"
    val johnId = 1

    val registeredUser = userRepositoryId.registerUser(johnUsername)
    registeredUser.id shouldEqual johnId
    registeredUser.username shouldEqual johnUsername

    val retrievedByUsername = userRepositoryId.getByUsername(johnUsername).get
    retrievedByUsername.id shouldEqual johnId
    retrievedByUsername.username shouldEqual johnUsername

    val retrievedById = userRepositoryId.getById(johnId).get
    retrievedById.id shouldEqual johnId
    retrievedById.username shouldEqual johnUsername

    val breadUsername = "Bread Pitt"
    val breadId = 2
    userRepositoryId.registerUser(breadUsername).id shouldEqual breadId

    userRepositoryId.getById(3) shouldBe None
    userRepositoryId.getByUsername("Random user name") shouldBe None

    val john2Username = "John Smith"
    val john2Id = 3

    userRepositoryId.registerUser(john2Username).id shouldEqual john2Id
  }
}
