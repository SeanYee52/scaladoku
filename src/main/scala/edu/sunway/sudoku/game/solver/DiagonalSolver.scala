package edu.sunway.sudoku.game.solver

import edu.sunway.sudoku.game.board.Cell

import scala.collection.mutable.ArrayBuffer

class DiagonalSolver (var _cellsToBeProcessed: ArrayBuffer[Cell], val _board: ArrayBuffer[ArrayBuffer [Cell]]) extends Solver (_cellsToBeProcessed, _board) {

  override def solve(): Boolean = {
    if (cellsToBeProcessed.isEmpty) return true

    cellsToBeProcessed = cellsToBeProcessed.sortBy(_.candidates.length)
    val cell = cellsToBeProcessed.remove(0)

    for (candidate <- cell.candidates) {
      if (isSafe(cell.row, cell.col, candidate)) {
        cell.ans = candidate

        if (solve()) {
          return true
        }
        cell.ans = 0
      }
    }

    cellsToBeProcessed.prepend(cell)
    false
  }

  override def isSafe(row: Int, col: Int, num: Int): Boolean = {
    checkHorizontal(row, num) && checkVertical(col, num) && checkSubGrid(row, col, num) && (
      if (row == col && row == (board.size - 1 - col)) { // Check both diagonals for center cell
        checkLeftRightDiagonal(num) && checkRightLeftDiagonal(num)
      } else if (row == col) { // Only on the left-right diagonal
        checkLeftRightDiagonal(num)
      } else if (row == (board.size - 1 - col)) { // Only on the right-left diagonal
        checkRightLeftDiagonal(num)
      } else true
      )
  }

  private def checkLeftRightDiagonal(num: Int): Boolean = {
    for (i <- board.indices) {
      if (board(i)(i).ans == num) return false
    }
    true
  }

  private def checkRightLeftDiagonal(num: Int): Boolean = {
    for (i <- board.indices) {
      if (board(i)(board.size - 1 - i).ans == num) return false
    }
    true
  }

}