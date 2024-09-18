package edu.sunway.sudoku.game.solver

import edu.sunway.sudoku.game.board.Cell

import scala.collection.mutable.ArrayBuffer

abstract class Solver (protected var cellsToBeProcessed: ArrayBuffer[Cell], protected val board: ArrayBuffer[ArrayBuffer [Cell]]){
  // Algorithm to solve a sudoku board
  def solve(): Boolean
  // Default method to check if placing an answer in the board maintains board validity
  def isSafe(row: Int, col: Int, num: Int): Boolean

  // Implemented methods that are to be used for all solvers
  // Checks if the basic rules of Sudoku are complied with
  def checkHorizontal(row: Int, num: Int): Boolean = {
    !board(row).exists(cell => cell.ans == num)
  }

  def checkVertical(col: Int, num: Int): Boolean = {
    !board.exists(row => row(col).ans == num)
  }

  def checkSubGrid(row: Int, col: Int, num: Int): Boolean = {
    val subGridRowStart = row - row % 3
    val subGridColStart = col - col % 3

    for (i <- 0 until 3) {
      for (j <- 0 until 3) {
        if (board(subGridRowStart + i)(subGridColStart + j).ans == num) {
          return false
        }
      }
    }
    true
  }
}