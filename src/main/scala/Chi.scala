//see LICENSE for license
//authors: Colin Schmidt, Adam Izraelevitz
package sha3

import chisel3._

class ChiModule(val W: Int = 64) extends Module {
  val io = IO(new Bundle {
    val state_i = Input(Vec(5 * 5, UInt(W.W)))
    val state_o = Output(Vec(5 * 5, UInt(W.W)))
  })

  (0 until 5).map(i => {
    (0 until 5).map(j => {
      io.state_o(i * 5 + j) := io.state_i(i * 5 + j) ^
        ((~io.state_i(((i + 1) % 5) * 5 + ((j) % 5))) & io.state_i(((i + 2) % 5) * 5 + ((j) % 5)))
    })
  })
}
