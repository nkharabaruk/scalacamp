package example.service

import cats.Id
import example.repository.UserRepository

/**
  * Id implementation of the User service.
  * @param repository the repository of the user.
  */
class UserServiceId(repository: UserRepository[Id]) extends UserService[Id](repository)
