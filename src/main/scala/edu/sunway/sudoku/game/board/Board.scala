package edu.sunway.sudoku.game.board

import edu.sunway.sudoku.game.solver.Solver
import edu.sunway.sudoku.game.Minimiser

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

trait Board{
  // For solver
  protected val cellsToBeProcessed = new ArrayBuffer[Cell]
  // Empty Board
  var _boardBuffer = new ArrayBuffer[ArrayBuffer[Cell]]
  def boardBuffer: ArrayBuffer[ArrayBuffer[Cell]] = _boardBuffer

  def generateBoard(difficulty: Int, size: Int): Unit

  def fillBoard(difficulty: Int, size: Int, solver: Solver): Unit = {
    if(!solver.solve()){
      generateBoard(difficulty, size)
    }
    val minimiser = new Minimiser(_boardBuffer)
    minimiser.minimiseBoard(difficulty)
  }

  def createCells(size: Int): Unit = {
    cellsToBeProcessed.clear()

    for (r <- 0 until size){
      val row = new ArrayBuffer[Cell]
      for (c <- 0 until size){
        val cell = new Cell(r, c)

        cell.initialiseCandidateList(size)
        cellsToBeProcessed.append(cell)
        row.append(cell)
      }
      _boardBuffer.append(row)
    }
  }
}

// Hides additional cells to balance difficulty for variants of Sudoku
trait AdditionalHiding {
  this: Board =>
  val rand = new Random()

  def hideCells(difficulty: Int): Unit ={
    var cellList = new ArrayBuffer[Cell]

    _boardBuffer.foreach{ row =>
      row.foreach { cell =>
        if (cell.shown) cellList.append(cell)
      }
    }

    cellList = rand.shuffle(cellList)

    for(i <- 0 until (160 / difficulty)) {
      cellList(i).shown = false
    }
  }
}