package edu.sunway.sudoku.game.board

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Cell (private val _row: Int, private val _col: Int){
  private var _candidates = ListBuffer.empty[Int]
  private var _ans = 0
  private var _shown = true
  private val rand = new Random()

  def row: Int = _row
  def col: Int = _col
  def candidates: ListBuffer[Int] = _candidates
  def candidates_= (newCandidates: ListBuffer[Int]): Unit = _candidates = newCandidates
  def ans: Int = _ans
  def ans_= (newAns:Int): Unit = _ans = newAns
  def shown: Boolean = _shown
  def shown_= (newShown:Boolean): Unit = _shown = newShown

  def copy(): Cell = {
    val newCell = new Cell(_row, _col)
    newCell._candidates = _candidates.clone()
    newCell._ans = _ans
    newCell._shown = _shown
    newCell
  }

  private def randomiseCandidateOrder(): Unit = {
    candidates = rand.shuffle(candidates)
  }

  def initialiseCandidateList(size: Int): Unit = {
    for (i <- 1 to size){
      candidates.append(i)
    }
    randomiseCandidateOrder()
  }
}

// For sorting cells
object Cell {
  implicit val cellOrdering: Ordering[Cell] = (x: Cell, y: Cell) => {
    val rowCompare = x.row.compare(y.row)
    if (rowCompare != 0) rowCompare
    else x.col.compare(y.col)
  }
}