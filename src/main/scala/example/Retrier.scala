package example

import scala.annotation.tailrec
import scala.concurrent.duration._

class Retrier {

  @tailrec
  final def retry[A](block: () => A,
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): A = {
    if (block == null || acceptResult == null) throw new IllegalArgumentException
    val result = block.apply()
    val accept = if (result != null) acceptResult.apply(result) else false
    val retriesEmpty = retries == null || retries.isEmpty
    if (accept || retriesEmpty) return result
    if (!retriesEmpty && retries.head != null) Thread.sleep(retries.head.toMillis)
    retry(block, acceptResult, retries.tail)
  }
}
