package edu.sunway.sudoku.game.board

import edu.sunway.sudoku.game.solver.NormalSolver

import scala.collection.mutable.{ArrayBuffer, ListBuffer}

class KillerBoard extends Board with AdditionalHiding {

  private val _cagesList = ListBuffer.empty[Cage]

  def cagesList: ListBuffer[Cage] = _cagesList

  def generateBoard(difficulty: Int, size: Int): Unit = {
    createCells(size)
    val solver = new NormalSolver(cellsToBeProcessed, _boardBuffer)
    fillBoard(difficulty, size, solver)
    generateCages(difficulty)
    hideCells(difficulty / 2)
  }

  // Algorithm that adds cages procedurally
  private def generateCages(difficulty: Int): Unit ={
    var cageSizes = new ArrayBuffer[Int]
    var cellList = new ArrayBuffer[Cell]

    _boardBuffer.foreach{ row =>
      row.foreach { cell =>
        cellList.append(cell)
      }
    }

    cellList = rand.shuffle(cellList)

    def checkForNeighbouringCells(r: Int, c: Int): Option[Cell] = {
      // Randomises how the program will search for neighbouring nodes, thus creating a bit of a snaking cage maybe
      if (rand.nextBoolean()) {
        if (checkForNeigbouringRows(r, c).nonEmpty) return checkForNeigbouringRows(r, c)
        if (checkForNeighbouringCols(r, c).nonEmpty) return checkForNeighbouringCols(r, c)
      } else{
        if (checkForNeighbouringCols(r, c).nonEmpty) return checkForNeighbouringCols(r, c)
        if (checkForNeigbouringRows(r, c).nonEmpty) return checkForNeigbouringRows(r, c)
      }
      None
    }

    def checkForNeigbouringRows(r: Int, c: Int): Option[Cell] = {
      // Check above and below the current cell
      for (startingRow <- r - 1 to r + 1) {
        if (startingRow != r && startingRow >= 0 && startingRow < 9) { // assuming grid is 9x9
          val cell = cellList.find(cell => cell.row == startingRow && cell.col == c)
          if (cell.isDefined) return cell
        }
      }
      None
    }

    def checkForNeighbouringCols(r: Int, c: Int): Option[Cell] = {
      // Check left and right of the current cell
      for (startingCol <- c - 1 to c + 1) {
        if (startingCol != c && startingCol >= 0 && startingCol < 9) { // assuming grid is 9x9
          val cell = cellList.find(cell => cell.row == r && cell.col == startingCol)
          if (cell.isDefined) return cell
        }
      }
      None
    }

    while (cageSizes.size < 100) {
      val chance = rand.nextInt(10) + rand.nextInt(difficulty / 15) + 1

      // Reassign the cage sizes
      chance match {
        case 1 => cageSizes.append(1)
        case 2 => cageSizes.append(2)
        case 3 => cageSizes.append(2)
        case 4 => cageSizes.append(2)
        case 5 => cageSizes.append(3)
        case 6 => cageSizes.append(3)
        case 7 => cageSizes.append(3)
        case 8 => cageSizes.append(4)
        case 9 => cageSizes.append(4)
        case 10 => cageSizes.append(5)
        case 11 => cageSizes.append(6)
        case 12 => cageSizes.append(7)
        case 13 => cageSizes.append(8)
      }
    }

    cageSizes = rand.shuffle(cageSizes)

    while (cellList.nonEmpty){
      val cage = new Cage
      var check = false
      cage.limit = cageSizes.remove(0)
      var cell = if (cage.cells.size < cage.limit - 1){
        Option(cellList.remove(0))
      } else{
        None
      }

      while (cell.nonEmpty && cage.addCell(cell) && cage.checkCage()) {
        check = true
        cell = checkForNeighbouringCells(cell.get.row, cell.get.col)
        if (cage.checkNewCell(cell)) {
          cellList -= cell.get
        }
      }

      cage.calculateSum()
      _cagesList.append(cage)
    }
  }
}