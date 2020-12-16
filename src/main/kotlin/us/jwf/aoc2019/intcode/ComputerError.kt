package us.jwf.aoc2019.intcode

interface ComputerError {
  class ProgramFinished : ComputerError, Exception()
}