package edu.sunway.sudoku.game.board

import edu.sunway.sudoku.game.solver.NormalSolver

class NormalBoard extends Board{
  def generateBoard(difficulty: Int, size: Int): Unit = {
    createCells(size)
    val solver = new NormalSolver(cellsToBeProcessed, _boardBuffer)
    fillBoard(difficulty, size, solver)
  }
}
