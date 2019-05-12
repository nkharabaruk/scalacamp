package example.utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class Retrier {

  final def retry[A](block: () => Future[A],
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): Future[A] = {
    block().flatMap(result => {
      if (acceptResult(result) || retries.isEmpty) {
        Future.successful(result)
      } else {
        Thread.sleep(retries.head.toMillis)
        retry(block, acceptResult, retries.tail)
      }
    })
  }
}
