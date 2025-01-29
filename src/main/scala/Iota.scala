//see LICENSE for license
//authors: Colin Schmidt, Adam Izraelevitz
package sha3

import chisel3._

class IotaModule(val W: Int = 64) extends Module {
  val io = IO(new Bundle {
    val state_i = Input(Vec(5 * 5, UInt(W.W)))
    val state_o = Output(Vec(5 * 5, UInt(W.W)))
    val round = Input(UInt(5.W))
  })

  (0 until 5).map(i => {
    (0 until 5).map(j => {
      if (i != 0 || j != 0)
        io.state_o(i * 5 + j) := io.state_i(i * 5 + j)
    })
  })

  val const = IOTA.round_const(io.round)
  io.state_o(0) := io.state_i(0) ^ const
}
