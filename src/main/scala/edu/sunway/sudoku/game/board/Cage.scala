package edu.sunway.sudoku.game.board

import scala.collection.{SortedSet, mutable}

class Cage {

  private var _sum = 0
  private var _cells: SortedSet[Cell] = mutable.TreeSet.empty[Cell]
  private var _limit = 0

  def sum: Int = _sum
  def cells: SortedSet[Cell] = _cells
  def limit: Int = _limit
  def limit_= (newLimit: Int): Unit = if(newLimit > 0 && newLimit < 10) _limit = newLimit

  def calculateSum(): Int = {
    cells.foreach{ cell =>
      _sum = sum + cell.ans
    }
    sum
  }

  def addCell(newCell: Option[Cell]): Boolean = {
    if (checkNewCell(newCell)) {
      _cells += newCell.get
      true
    } else {
      false
    }
  }

  def checkNewCell(newCell: Option[Cell]): Boolean = {
    _cells.size <= _limit && newCell.nonEmpty && !_cells.exists(cell => cell.ans == newCell.get.ans)
  }

  def checkCage(): Boolean = {
    if (_cells.nonEmpty) {
      _cells.forall { cell =>
        val row = cell.row
        val col = cell.col
        // Check if this cell has a neighboring cell (either through a row or col)
        if(_cells.size > 1)
          _cells.exists(Cell => (Cell.row + 1) == row || (Cell.row - 1) == row || (Cell.col + 1) == col || (Cell.col - 1) == col)
        else
          true
      }
    } else
      true
  }

  def checkTopLeft(row: Int, col: Int) : Boolean = {
    _cells.firstKey.row == row && _cells.firstKey.col == col
  }

  def cellExists(row: Int, col: Int): Boolean = {
    _cells.exists(cell => cell.row == row && cell.col == col)
  }
}