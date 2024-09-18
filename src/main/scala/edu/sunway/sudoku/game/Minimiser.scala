package edu.sunway.sudoku.game

import edu.sunway.sudoku.game.solver.MinimiseSolver
import edu.sunway.sudoku.game.board.Cell

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

// Used to hide cells while still ensuring the board is solvable
class Minimiser(private val board: ArrayBuffer[ArrayBuffer [Cell]]) {

  var cellList = new ArrayBuffer[Cell]
  private val copiedBoard = board.map(row => row.map { c =>
    val newCell = c.copy()
    cellList.append(newCell)
    newCell
  })
  private val cellsToBeProcessed = new ArrayBuffer[Cell]
  private val rand = new Random()

  def minimiseBoard(difficulty: Int): Unit = {
    cellList = rand.shuffle(cellList)
    validateBoard(difficulty)

    for (row <- board.indices){
      for (col <- board.indices){
        board(row)(col).shown = copiedBoard(row)(col).shown
      }
    }
  }

  private def validateBoard(difficulty: Int): Unit = {
    def removeCells(count: Int): Map[Cell, Int] = {
      var cellAns: Map[Cell, Int] = Map()
      var removedCell = cellList.remove(0)

      def removeCell(): Unit = {
        cellsToBeProcessed.append(removedCell)
        cellAns += (removedCell -> removedCell.ans)
        removedCell.ans = 0
        removedCell.shown = false
      }

      removeCell()

      for (i <- 0 until count){
        val temp = cellList.find(Cell => ((Cell.row + 1) == removedCell.row && (Cell.col + 1) == removedCell.col)||
          ((Cell.row + 1) == removedCell.row && (Cell.col - 1) == removedCell.col) ||
          ((Cell.row - 1) == removedCell.row && (Cell.col + 1) == removedCell.col)||
          ((Cell.row - 1) == removedCell.row && (Cell.col - 1) == removedCell.col))
        if(temp.nonEmpty) {
          removedCell = temp.get
          cellList -= removedCell
          removeCell()
        }
      }
      cellAns
    }

    def restoreCells(cellAns: Map[Cell, Int]): Unit = {
      cellAns.transform{ (Cell, num) =>
        Cell.ans = num
        Cell.shown = true
        cellsToBeProcessed -= Cell
      }
    }

    var counter = 0
    var sentinel = 0
    while (counter < difficulty && sentinel != -1){
      var tempAns: Map[Cell, Int] = Map()
      if (counter < 20) {
        tempAns = removeCells(4)
        counter += 4
      } else if (counter < 30) {
        tempAns = removeCells(2)
        counter += 2
      } else {
        tempAns = removeCells(1)
        counter += 1
      }

      if (hasUniqueSolution() == 0){
        restoreCells(tempAns)
      } else if (hasUniqueSolution() == -1){
        restoreCells(tempAns)
        sentinel = -1
      }
    }
  }

  private def hasUniqueSolution(): Int ={
    val solver = new MinimiseSolver(cellsToBeProcessed, copiedBoard)
    if(solver.countSolutions() > 1 ) {
      0
    } else if (solver.countSolutions() == 1) {
      1
    } else{
      -1
    }
  }

}