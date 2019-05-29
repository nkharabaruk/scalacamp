package example.service

import example.repository.{IotDeviceRepository, UserRepository}
import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._

/**
  * Future inplementation of the IOT Device Service.
  * @param repository the repository of the IOT Device.
  * @param userRepository the repository of the User.
  * @param ec the scope of execution context used by the service.
  */
class IotDeviceServiceFuture(repository: IotDeviceRepository[Future],
                             userRepository: UserRepository[Future])
                            (implicit ec: ExecutionContext) extends IotDeviceService[Future](repository, userRepository)
