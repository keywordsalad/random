package green.thisfieldwas.random

/**
 * A [[https://en.wikipedia.org/wiki/Linear_congruential_generator linear congruential generator]] which generates
 * 32-bit values from 64-bit iterations, extracting bits 47..16 from each iteration.
 *
 * @param modulus
 * @param multiplier
 * @param increment
 * @param seed
 */
case class Rand48(modulus: Long, multiplier: Long, increment: Long, seed: Long) extends RNG {

  override def next32(): (RNG, Long) = {
    val nextSeed = (multiplier * seed + increment) % modulus
    (copy(seed = nextSeed), (nextSeed & 0xFFFFFFFF0000L) >>> 16)
  }
}

object Rand48 {

  def apply(seed: Long): Rand48 =
    Rand48(
      modulus = Math.pow(2, 32).toLong,
      multiplier = 0x5DEECE66D1L,
      increment = 11,
      seed = seed
    )
}
