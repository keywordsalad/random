package green.thisfieldwas.random

import Rand._

trait RNG {

  /**
   * Generates the next 64 bits from the RNG and returns the next state.
   *
   * @return
   */
  def next64(): (RNG, Long) =
    (for {
      highBits <- next(32).map(_ << 32L)
      lowBits <- next(32)
    } yield highBits | lowBits)(this)

  /**
   * Generates the next 32 bits from the RNG and returns the next state.
   *
   * @return
   */
  def next32(): (RNG, Long) =
    next(64).map(_ & 0xFFFFFFFF)(this)
}
