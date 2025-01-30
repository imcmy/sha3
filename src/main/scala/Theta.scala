//see LICENSE for license
//authors: Colin Schmidt, Adam Izraelevitz
package sha3

import chisel3._
import chisel3.util._

class ThetaModule(val W: Int = 64) extends Module {
  val io = IO(new Bundle {
    val state_i = Input(Vec(5 * 5, UInt(W.W)))
    val state_o = Output(Vec(5 * 5, UInt(W.W)))
  })

  val bc = VecInit(Seq.fill(5)(UInt(W.W)))

  (0 until 5).map(i => {
    bc(i) := io.state_i(i * 5 + 0) ^ io.state_i(i * 5 + 1) ^ io.state_i(i * 5 + 2) ^ io.state_i(i * 5 + 3) ^ io.state_i(
      i * 5 + 4
    )
  })

  (0 until 5).map(i => {
    val t = Wire(UInt(W.W))
    t := bc((i + 4) % 5) ^ Common.ROTL(bc((i + 1) % 5), 1.U, W.U)
    (0 until 5).map(j => {
      io.state_o(i * 5 + j) := io.state_i(i * 5 + j) ^ t
    })
  })
}

class Parity extends Module {
  val io = IO(new Bundle {
    val in = Input(Vec(5, Bool()))
    val res = Output(Bool())
  })
  io.res := io.in(0) ^ io.in(1) ^ io.in(2) ^ io.in(3) ^ io.in(4)
}
