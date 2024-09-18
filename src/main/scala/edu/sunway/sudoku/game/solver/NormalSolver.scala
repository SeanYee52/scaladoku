package edu.sunway.sudoku.game.solver

import edu.sunway.sudoku.game.board.Cell

import scala.collection.mutable.ArrayBuffer

class NormalSolver(var _cellsToBeProcessed: ArrayBuffer[Cell], val _board: ArrayBuffer[ArrayBuffer [Cell]]) extends Solver(_cellsToBeProcessed, _board){
  // Default algorithm that is used in all solvers, uses backtracking
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
    checkHorizontal(row, num) && checkVertical(col, num) && checkSubGrid(row, col, num)
  }
}