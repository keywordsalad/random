package green.thisfieldwas.random

trait RNG {

  def next(): (RNG, Long)
}
