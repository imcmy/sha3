//see LICENSE for license
//authors: Colin Schmidt, Adam Izraelevitz
package sha3

import chisel3._

class RhoPiModule(val W: Int = 64) extends Module {
  val io = IO(new Bundle {
    val state_i = Input(Vec(25, UInt(W.W)))
    val state_o = Output(Vec(25, UInt(W.W)))
  })

  (0 until 5).map(i => {
    (0 until 5).map(j => {
      val temp = Wire(Bits())
      if ((RHOPI.tri(i * 5 + j) % W) == 0) {
        temp := io.state_i(i * 5 + j)
      } else {
        temp := Cat(
          io.state_i(i * 5 + j)((W - 1) - (RHOPI.tri(i * 5 + j) - 1) % W, 0),
          io.state_i(i * 5 + j)(W - 1, W - 1 - ((RHOPI.tri(i * 5 + j) - 1) % W))
        )
      }
      io.state_o(j * 5 + ((2 * i + 3 * j) % 5)) := temp
    })
  })
}
