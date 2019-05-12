package example.service

import cats.Id
import example.repository.UserRepository

class UserServiceId(repository: UserRepository[Id]) extends UserService[Id](repository)
