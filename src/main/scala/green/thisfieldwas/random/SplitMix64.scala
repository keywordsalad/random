package green.thisfieldwas.random

/**
 * PRNG for initializing [[Xoshiro256ss]]'s state. [[https://en.wikipedia.org/wiki/Xorshift#Initialization See here]]
 * for definition.
 *
 * @param state
 *   The PRNG state.
 */
private case class SplitMix64(state: Long) extends RNG {

  override def next(): (RNG, Long) = {
    var z = state
    z = (z ^ (z >> 30)) * 0xbf58476d1ce4e5b9L
    z = (z ^ (z >> 27)) * 0x94d049bb133111ebL
    (SplitMix64(z), z)
  }
}
