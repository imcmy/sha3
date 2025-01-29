package sha3

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.util.Random


class ChiTest extends AnyFlatSpec with ChiselScalatestTester {
  behavior.of("Chi")

  it should "pass" in {
    val W = 4
    test(new ChiModule(W)) { c =>
      val rand = new Random(1)
      for (_ <- 0 until 1) {
        val state = Reg(Vec(5 * 5, UInt(W.W)))
        (0 until 5).map(i => {
          (0 until 5).map(j => {
            state(i * 5 + j) := rand.nextInt(1 << W).U
          })
        })
        val outState = Reg(Vec(5 * 5, UInt(W.W)))
        (0 until 5).map(i => {
          (0 until 5).map(j => {
            outState(i * 5 + j) := state(i * 5 + j) ^
              ((~state(((i + 1) % 5) * 5 + ((j) % 5)) & state(((i + 2) % 5) * 5 + ((j) % 5))))
          })
        })
        c.io.state_i.poke(state)
        c.clock.step(1)
        c.io.state_o.expect(outState)
      }
    }
  }
}
