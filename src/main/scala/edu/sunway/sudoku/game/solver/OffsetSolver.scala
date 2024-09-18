package edu.sunway.sudoku.game.solver

import edu.sunway.sudoku.game.board.Cell

import scala.collection.mutable.ArrayBuffer

class OffsetSolver (var _cellsToBeProcessed: ArrayBuffer[Cell], val _board: ArrayBuffer[ArrayBuffer [Cell]]) extends Solver (_cellsToBeProcessed, _board){

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
    checkHorizontal(row, num) && checkVertical(col, num) && checkSubGrid(row, col, num) && checkOffset(row, col, num)
  }

  private def checkOffset(row: Int, col: Int, num: Int): Boolean = {
    val offsetRow = row % 3
    val offsetCol = col % 3

    for(i <- board.indices) {
      for(j <- board.indices) {
        if (i % 3 == offsetRow && j % 3 == offsetCol)
          if (board(i)(j).ans == num) return false
      }
    }
    true
  }
}