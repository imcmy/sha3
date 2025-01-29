package sha3

// class IotaModuleTests(c: IotaModule) extends PeekPokeTester(c) {
//     val W       = 64
//     val maxInt  = 1 << (5*5*W)
//       //val state_i = rnd.nextInt(maxInt)
//       val round = 0
//       val state = Array.fill(5*5){BigInt(3)}
//       val out_state = Array.fill(5*5){BigInt(3)}
//       out_state(0) = state(0) ^ BigInt(1)
//       poke(c.io.state_i, state)
//       poke(c.io.round, round)
//       step(1)
//       expect(c.io.state_o, out_state)
// }
