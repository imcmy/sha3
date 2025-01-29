//see LICENSE for license
//authors: Colin Schmidt, Adam Izraelevitz
package sha3

import chisel3._
import chisel3.util._
import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer
import scala.util.Random
import org.chipsalliance.cde.config.Parameters

class DpathModule(val W: Int, val S: Int)(implicit p: Parameters) extends Module {
  //constants
  val r = 2 * 256
  val c = 25 * W - r
  val round_size_words = c / W
  val rounds = 24 // 12 + 2l
  val hash_size_words = 256 / W
  val bytes_per_word = W / 8

  val io = IO(new Bundle {
    val absorb = Input(Bool())
    val init = Input(Bool())
    val write = Input(Bool())
    val round = Input(UInt(5.W))
    val stage = Input(UInt(log2Up(S).W))
    val aindex = Input(UInt(log2Up(round_size_words).W))
    val message_in = Input(UInt(W.W))
    val hash_out = Output(Vec(hash_size_words, UInt(W.W)))
  })

  val state = RegInit(VecInit(Seq.fill(5 * 5)(0.U(W.W))))

  //submodules
  val theta = Module(new ThetaModule(W)).io
  val rhopi = Module(new RhoPiModule(W)).io
  val chi = Module(new ChiModule(W)).io
  val iota = Module(new IotaModule(W))

  //default
  theta.state_i := VecInit(Seq.fill(5 * 5)(0.U(W.W)))
  iota.io.round := 0.U

  //connect submodules to each other
  if (S == 1) {
    theta.state_i := state
    rhopi.state_i <> theta.state_o
    chi.state_i <> rhopi.state_o
    iota.io.state_i <> chi.state_o
    state := iota.io.state_o
  }
  if (S == 2) {
    //stage 1
    theta.state_i := state
    rhopi.state_i <> theta.state_o

    //stage 2
    chi.state_i := state
    iota.io.state_i <> chi.state_o
  }
  if (S == 4) {
    //stage 1
    theta.state_i := state
    //stage 2
    rhopi.state_i := state
    //stage 3
    chi.state_i := state
    //stage 3
    iota.io.state_i := state
  }

  iota.io.round := io.round

  //try moving mux out
  switch(io.stage) {
    is(0.U) {
      if (S == 1) {
        state := iota.io.state_o
      } else if (S == 2) {
        state := rhopi.state_o
      } else if (S == 4) {
        state := theta.state_o
      }
    }
    is(1.U) {
      if (S == 2) {
        state := iota.io.state_o
      } else if (S == 4) {
        state := rhopi.state_o
      }
    }
    is(2.U) {
      if (S == 4) {
        state := chi.state_o
      }
    }
    is(3.U) {
      if (S == 4) {
        state := iota.io.state_o
      }
    }
  }

  when(io.absorb) {
    state := state
    when(io.aindex < round_size_words.U) {
      state((io.aindex % 5.U) * 5.U + (io.aindex / 5.U)) :=
        state((io.aindex % 5.U) * 5.U + (io.aindex / 5.U)) ^ io.message_in
    }
  }

  val hash_res = Wire(Vec(hash_size_words, UInt(W.W)))
  for (i <- 0 until hash_size_words) {
    io.hash_out(i) := state(i * 5)
  }

  //keep state from changing while we write
  when(io.write) {
    state := state
  }

  //initialize state to 0 for new hashes or at reset
  when(io.init) {
    state := VecInit(Seq.fill(5 * 5)(0.U(W.W)))
  }

  when(reset.asBool) {
    state := VecInit(Seq.fill(5 * 5)(0.U(W.W)))
  }
}
