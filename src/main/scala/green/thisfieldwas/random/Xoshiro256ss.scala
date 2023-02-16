package green.thisfieldwas.random

/**
 * Xoshiro256** pseudo-random number generator for 64-bit values. Mnemonic for the PRNG algorithm name is
 * "xor/shift/rotate with 256-bits state". [[https://en.wikipedia.org/wiki/Xorshift#xoshiro256** See here]] for
 * definition.
 *
 * @param x
 *   State value #0.
 * @param y
 *   State value #1.
 * @param z
 *   State value #2.
 * @param w
 *   State value #3.
 */
case class Xoshiro256ss(x: Long, y: Long, z: Long, w: Long) extends RNG {

  import Xoshiro256ss._

  override def next(): (Xoshiro256ss, Long) = {
    val result = rol64(y * 5, 7) * 9
    val t = y << 17

    val tempZ = z ^ x
    val tempW = w ^ y
    val nextY = y ^ tempZ
    val nextX = x ^ tempW

    val nextZ = tempZ ^ t
    val nextW = rol64(tempW, 45)

    (Xoshiro256ss(x = nextX, y = nextY, z = nextZ, w = nextW), result)
  }

  /**
   * This is the jump function for the generator. It is equivalent to 2^128 calls to next(); it can be used to generate
   * 2^128 non-overlapping subsequences for parallel computations. The generated [[Stream]] is infinite and uses the
   * last generated jump to seed the next series of jumps.
   *
   * @return
   *   An infinite Stream of jumped generators.
   */
  def jump(): Stream[Xoshiro256ss] =
    jumpWith(
      this,
      Array(
        0x180ec6d33cfd0abaL,
        0xd5a61266f0c9392cL,
        0xa9582618e03fc9aaL,
        0x39abdc4529b1661cL
      )
    )

  /**
   * This is the long-jump function for the generator. It is equivalent to 2^192 calls to next(); it can be used to
   * generate 2^64 starting points, from each of which jump() will generate 2^64 non-overlapping subsequences for
   * parallel distributed computations. The generated [[Stream]] is infinite and uses the last generated jump to seed
   * the next series of jumps.
   *
   * @return
   *   An infinite Stream of jumped generators.
   */
  def longJump(): Stream[Xoshiro256ss] =
    jumpWith(
      this,
      Array(
        0x76e15d3efefdcbbfL,
        0xc5004e441c522fb3L,
        0x77710069854ee241L,
        0x39109bb02acbe635L
      )
    )
}

object Xoshiro256ss {

  def apply(seed: Long): Xoshiro256ss = {
    import Rand._

    val (_, (first, second)) = (for {
      first <- nextLong()
      second <- nextLong()
    } yield (first, second))(SplitMix64(seed))

    val x = first & 0xfffffffL
    val y = first >> 32
    val z = second & 0xffffffL
    val w = second >> 32

    new Xoshiro256ss(x, y, z, w)
  }

  /**
   * "Rotate Left" 64-bit hash function used by [[Xoshiro256ss]] PRNG.
   */
  private def rol64(x: Long, k: Int): Long = (x << k) | (x >>> (64 - k))

  /**
   * Generates an infinite [[Stream]] of jumped [[Xoshiro256ss]] instances.
   *
   * @param startRNG
   *   The starting generator.
   * @param jumps
   *   The array of jump values to use.
   * @return
   *   An infinite Stream of non-overlapping instances.
   */
  private def jumpWith(startRNG: Xoshiro256ss, jumps: Array[Long]): Stream[Xoshiro256ss] = {
    def tail(rng: Xoshiro256ss, jump: Int, shift: Int, state: (Long, Long, Long, Long)): Stream[Xoshiro256ss] = {
      val (x, y, z, w) = state
      if (jump == jumps.length) {
        // jumps and shifts exhausted, restart tail() with the latest generator state
        tail(rng, jump = 0, shift = 0, state = (0, 0, 0, 0))
      } else if (shift == java.lang.Long.SIZE) {
        // shifts exhausted, restart tail() with the next jump and reset shifts
        tail(rng, jump + 1, shift = 0, (x, y, z, w))
      } else if ((jumps(jump) & 1L << shift) != 0L) {
        // calculate the next state and advance the shift if non-zero jump/shift produced
        val nextX = x ^ rng.x
        val nextY = y ^ rng.y
        val nextZ = z ^ rng.z
        val nextW = w ^ rng.w
        Xoshiro256ss(x = nextX, y = nextY, z = nextZ, w = nextW) #:: tail(
          rng.next()._1,
          jump,
          shift = shift + 1,
          state = (nextX, nextY, nextZ, nextW)
        )
      } else {
        // no state produced, advanced the shift
        tail(rng.next()._1, jump, shift + 1, (x, y, z, w))
      }
    }
    tail(startRNG, jump = 0, shift = 0, state = (0, 0, 0, 0))
  }
}
