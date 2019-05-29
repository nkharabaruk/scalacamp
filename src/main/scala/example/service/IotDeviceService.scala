package example.service

import cats.Monad
import cats.implicits._
import example.domain.IotDevice
import example.repository.{IotDeviceRepository, UserRepository}

/**
  * Manages operations with repository adding addition business logic.
  * @param repository the repository of the IOT Device.
  * @param userRepository the repository of the User.
  * @param monad allows composition of dependent effectful functions
  * @tparam F specific return type of the function.
  */
class IotDeviceService[F[_]](repository: IotDeviceRepository[F],
                             userRepository: UserRepository[F])
                            (implicit monad: Monad[F]) {
  // the register should fail with Left if the user doesn't exist or the sn already exists.
  def registerDevice(userId: Long, sn: String): F[Either[String, IotDevice]] = {
    repository.getBySn(sn).flatMap({
      case Some(_) => Monad[F].pure(Left(s"Serial number $sn already exists."))
      case _ => userRepository.getById(userId).flatMap({
        case Some(user) =>
          repository.registerDevice(userId, sn).map(Right(_))
        case None =>
          Monad[F].pure(Left(s"User with id $userId not found."))
      })
    })
  }
}
