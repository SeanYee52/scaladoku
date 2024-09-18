package edu.sunway.sudoku.game.board

import edu.sunway.sudoku.game.solver.DiagonalSolver

class DiagonalBoard extends AdditionalHiding with Board {
  def generateBoard(difficulty: Int, size: Int): Unit = {
    createCells(size)
    val solver = new DiagonalSolver(cellsToBeProcessed, _boardBuffer)
    fillBoard(difficulty - 5, size, solver)
    hideCells(difficulty - 10)
  }
}
