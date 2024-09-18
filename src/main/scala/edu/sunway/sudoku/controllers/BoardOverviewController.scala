package edu.sunway.sudoku.controllers

import _root_.edu.sunway.sudoku.game._
import edu.sunway.sudoku.MainApp
import edu.sunway.sudoku.game.board._
import javafx.geometry
import javafx.scene.Node
import javafx.scene.input.KeyCode
import scalafx.animation.AnimationTimer
import scalafx.event.ActionEvent
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.control.{Alert, Button, Label}
import scalafx.scene.layout.{GridPane, StackPane}
import scalafx.scene.media.{Media, MediaPlayer}
import scalafx.scene.shape.Line
import scalafx.scene.text.Font
import scalafxml.core.macros.sfxml

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

/*
 */

@sfxml
class BoardOverviewController (
                                private val numpadContainer: GridPane,
                                private val boardGrid: GridPane,
                                private val noteButton: Button,
                                private val timerLabel: Label
                              ){
  private var board: Board = _
  private var game: Sudoku = _
  private var seconds = 0
  private var lastTime = 0L
  private val timer: AnimationTimer = AnimationTimer(now =>
    if (lastTime != 0) {
      if (now > lastTime + 1000000000) {
        seconds += 1
        val minutes = seconds / 60
        val displaySeconds = seconds % 60
        timerLabel.setText(f"$minutes%02d:$displaySeconds%02d")
        lastTime = now
      }
    } else {
      lastTime = now
    }
  )
  private var _player: Player = _
  private var _difficulty: Int = 0
  private var noteTrigger = false
  private val buttonDefaultStyle = "-fx-text-fill: grey; -fx-background-color: white; -fx-border-color: gray; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-font-size: 24px; -fx-font-family: Georgia;"
  private val buttonHoveredStyle = "-fx-text-fill: royalblue; -fx-background-color: gainsboro; -fx-border-color: gray; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-font-size: 24px; -fx-font-family: Georgia;"

  def player: Player = _player
  def player_=(newPlayer: Player): Unit = _player = newPlayer

  def difficulty: Int = _difficulty
  def difficulty_= (newDifficulty: Int): Unit = _difficulty = newDifficulty

  def initialise(selectedBoard: Int, size: Int): Unit = {
    board = selectedBoard match {
      case 0 => new NormalBoard
      case 1 => new KillerBoard
      case 2 => new OffsetBoard
      case 3 => new DiagonalBoard
    }
    game = new Sudoku(0, difficulty, board, player, size)
    game.createBoard()
    updateBoardGrid()
    connectNumpad()
    timer.start()
  }

  // Methods related to the sudoku grid
  private def handleKeyPress(event: javafx.scene.input.KeyEvent, cell: StackPane, row: Int, col: Int): Unit = {
    val text = event.getText
    event.getCode match {
      case KeyCode.UP =>
        if (row > 0) focusCell(row - 1, col)
      case KeyCode.DOWN=>
        if (row < 8) focusCell(row + 1, col)
      case KeyCode.LEFT=>
        if (col > 0) focusCell(row, col - 1)
      case KeyCode.RIGHT=>
        if (col < 8) focusCell(row, col + 1)
      case KeyCode.W =>
        if (row > 0) focusCell(row - 1, col)
      case KeyCode.S =>
        if (row < 8) focusCell(row + 1, col)
      case KeyCode.A =>
        if (col > 0) focusCell(row, col - 1)
      case KeyCode.D =>
        if (col < 8) focusCell(row, col + 1)
      case _ =>
        if (event.isShiftDown) {
          triggerNoteMode()
        }
        else if (text.matches("[1-9]") && cell.getId != "FIXED" && noteTrigger) {
          toggleNoteInCell(cell, text.toInt)
        }
        else if(cell.getId != "FIXED"){
          updateCell(cell, row, col, text)
        }
    }
  }

  private def returnSameSubGridCells(row: Int, col: Int): Iterable[Node] = {
    val startRow = row - (row % 3)
    val startCol = col - (col % 3)
    val endRow = startRow + 3
    val endCol = startCol + 3

    val sameSubGridCells = boardGrid.getChildren.filter{ node =>
      val nodeRow = javafx.scene.layout.GridPane.getRowIndex(node)
      val nodeCol = javafx.scene.layout.GridPane.getColumnIndex(node)
      nodeRow >= startRow && nodeRow < endRow && nodeCol >= startCol && nodeCol < endCol
    }
    sameSubGridCells
  }

  private def returnSameRowColCells(row: Int, col: Int): Iterable[Node] = {
    val sameRowColCells = boardGrid.getChildren.filter(node =>
      javafx.scene.layout.GridPane.getRowIndex(node) == row || javafx.scene.layout.GridPane.getColumnIndex(node) == col
    )
    sameRowColCells
  }

  private def focusCell(row: Int, col: Int): Unit = {

    def setCellsStyles(cells: Iterable[Node]): Unit = {
      cells.foreach{cell =>
        if (!cell.getStyle.contains("-fx-background-color: lightblue;"))
          cell.setStyle(cell.getStyle + "-fx-background-color: gainsboro;")
      }
    }

    def focusSameRowColCells(row: Int, col: Int): Unit = {
      val sameRowColCells = returnSameRowColCells(row, col)
      setCellsStyles(sameRowColCells)
    }

    def focusSameSubGrid(row: Int, col: Int): Unit = {
      val sameSubGridCells = returnSameSubGridCells(row, col)

      setCellsStyles(sameSubGridCells)
    }

    def deselectCells(): Unit ={
      boardGrid.getChildren.foreach{ child =>
        child.setStyle(child.getStyle.replace("-fx-background-color: lightblue", "-fx-background-color: white"))
        child.setStyle(child.getStyle.replace("-fx-background-color: lightcyan", "-fx-background-color: white"))
        child.setStyle(child.getStyle.replace("-fx-background-color: gainsboro", "-fx-background-color: white"))
      }
    }

    deselectCells()
    val cellOption = boardGrid.getChildren.find(node =>
      javafx.scene.layout.GridPane.getRowIndex(node) == row && javafx.scene.layout.GridPane.getColumnIndex(node) == col
    )
    cellOption.foreach { cell =>
      cell.requestFocus()
      val sameRow = javafx.scene.layout.GridPane.getRowIndex(cell)
      val sameCol = javafx.scene.layout.GridPane.getColumnIndex(cell)
      focusSameRowColCells(sameRow, sameCol)
      focusSameSubGrid(sameRow, sameCol)

      val selectedNumber = cell.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(1).asInstanceOf[javafx.scene.control.Label].getText

      if (selectedNumber.nonEmpty){
        val sameNumCells = boardGrid.getChildren.filter(node =>
          node.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(1).asInstanceOf[javafx.scene.control.Label].getText == selectedNumber)
        sameNumCells.foreach {cell =>
          cell.setStyle(cell.getStyle + "-fx-background-color: lightcyan;")
        }
      }
      cell.setStyle(cell.getStyle + "-fx-background-color: lightblue;")
    }
  }

  private def returnSelectedCell(): Option[Node] = {
    val selectedCellOption = boardGrid.getChildren.find(_.getStyle.contains("-fx-background-color: lightblue"))
    selectedCellOption
  }

  private def returnRow(node: Node): Int = {
    javafx.scene.layout.GridPane.getRowIndex(node)
  }

  private def returnCol(node: Node): Int = {
    javafx.scene.layout.GridPane.getColumnIndex(node)
  }

  private def triggerNoteMode(): Unit = {
    noteTrigger = if (noteTrigger) {
      noteButton.setStyle(buttonDefaultStyle)
      noteButton.setOnMouseExited(_ => noteButton.setStyle(buttonDefaultStyle))
      false
    }
    else{
      noteButton.setStyle(buttonHoveredStyle)
      noteButton.setOnMouseExited(_ => noteButton.setStyle(buttonHoveredStyle))
      true
    }
  }

  // Methods related to the numpad and buttons
  private def handleInput(num: Int): Unit = {
    val selectedCellOption = returnSelectedCell()
    selectedCellOption.foreach { cell =>

      if (cell.getId != "FIXED") {
        if(noteTrigger && num != 0)
          toggleNoteInCell(cell.asInstanceOf[javafx.scene.layout.StackPane], num)
        else
          updateCell(cell.asInstanceOf[javafx.scene.layout.StackPane], returnRow(cell), returnCol(cell), num.toString)
      }
    }
  }

  def handleNoteTrigger(actionEvent: ActionEvent): Unit = {
    triggerNoteMode()
  }

  def handleErase(actionEvent: ActionEvent): Unit = {
    handleInput(0)
  }

  def handleShowAns(actionEvent: ActionEvent): Unit = {
    val selectedCellOption = returnSelectedCell()
    selectedCellOption.foreach { cell =>

      if (cell.getId != "FIXED") {
        handleInput(board.boardBuffer(returnRow(cell))(returnCol(cell)).ans)
        cell.setId("FIXED")
        cell.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(1).setStyle(
          cell.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(1).getStyle.replace("-fx-text-fill: royalblue;", "-fx-text-fill: black;")
        )
      }
    }
  }

  private def connectNumpad(): GridPane = {

    val buttons = numpadContainer.children

    buttons.foreach { node =>
      val button = node.asInstanceOf[javafx.scene.control.Button]
      val number = button.getText.toInt
      button.setFont(new Font(24.0))
      button.setPrefSize(80, 80)
      button.setOnAction(_ => handleInput(number))
      button.setFocusTraversable(false)
    }

    numpadContainer
  }

  // Methods to display changes to the board
  private def stopTimer(): Unit = {
    timer.stop()
    game.setScore(seconds)
  }

  private def updateCell(cell: javafx.scene.layout.StackPane, row: Int, col: Int, text: String): Unit = {

    def cleanRowColSubgrid(row: Int, col: Int, text: String): Unit = {
      val sameRowColCells = returnSameRowColCells(row, col)
      val sameSubGridCells = returnSameSubGridCells(row, col)

      val sameCells = sameRowColCells ++ sameSubGridCells
      sameCells.foreach{ cell =>
        val notesGrid = cell.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(0).asInstanceOf[javafx.scene.layout.GridPane]
        if (!cell.getStyle.contains("-fx-background-color: lightblue;"))
          cell.setStyle(cell.getStyle + "-fx-background-color: gainsboro;")
        notesGrid.getChildren.foreach { child =>
          val noteLabel = child.asInstanceOf[javafx.scene.control.Label]
          if (noteLabel.getText == text)
            noteLabel.setText("")
        }
      }
    }

    def clearNoteInCell(cell: javafx.scene.layout.StackPane): Unit = {
      val notesGrid = cell.getChildren.get(0).asInstanceOf[javafx.scene.layout.GridPane]
      notesGrid.getChildren.foreach(child => child.asInstanceOf[javafx.scene.control.Label].setText(""))
    }

    val label = cell.getChildren.get(1).asInstanceOf[javafx.scene.control.Label] // Main number label
    if (text.matches("[1-9]")) {
      label.setText(text)
      cleanRowColSubgrid(row, col, text)
      clearNoteInCell(cell)
      if (text.toInt != board.boardBuffer(row)(col).ans) {
        label.setStyle(label.getStyle.replace("-fx-text-fill: black", "-fx-text-fill: red"))
        label.setStyle(label.getStyle.replace("-fx-text-fill: royalblue", "-fx-text-fill: red"))
        cell.setId(null)
      } else {
        label.setStyle(label.getStyle.replace("-fx-text-fill: black", "-fx-text-fill: royalblue"))
        label.setStyle(label.getStyle.replace("-fx-text-fill: red", "-fx-text-fill: royalblue"))
        cell.setId("CORRECT")
      }
      focusCell(row, col)
    } else if (text.matches("0")){
      label.setText("")
      clearNoteInCell(cell)
    }

    checkGameFinished()
  }

  private def toggleNoteInCell(cell: javafx.scene.layout.StackPane, num: Int): Unit = {
    val ansLabel = cell.getChildren.get(1).asInstanceOf[javafx.scene.control.Label]
    ansLabel.setText("")
    val notesGrid = cell.getChildren.get(0).asInstanceOf[javafx.scene.layout.GridPane]
    val noteLabel = notesGrid.getChildren.get(num - 1).asInstanceOf[javafx.scene.control.Label]
    if (noteLabel.getText.isEmpty) {
      noteLabel.setText(num.toString)
    } else {
      noteLabel.setText("")
    }
  }

  private def updateBoardGrid(): Unit = {

    def offsetBoardColouring(): Unit = {

      println("COLOURED CALLED")

      def setColourStyle(offsetNodes: Iterable[Node],Colour: String): Unit = {
        offsetNodes.foreach{node =>
          node.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(0).setStyle(
            node.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(0).getStyle.concat(s"-fx-background-color: $Colour")
          )
        }
      }

      for (i <- 0 until board.boardBuffer.size / 3){
        for (j <- 0 until board.boardBuffer.size / 3){
          val offsetNodes = boardGrid.getChildren.filter{ node =>
            val nodeRow = javafx.scene.layout.GridPane.getRowIndex(node)
            val nodeCol = javafx.scene.layout.GridPane.getColumnIndex(node)
            nodeRow % 3 == i && nodeCol % 3 == j
          }
          val colour = (i, j) match {
            case (0,0) => "LightGray"
            case (0,1) => "Lavender"
            case (0,2) => "LightCoral"
            case (1,0) => "LightGreen"
            case (1,1) => "PaleGoldenRod"
            case (1,2) => "Thistle"
            case (2,0) => "PeachPuff"
            case (2,1) => "LightSalmon"
            case (2,2) => "MistyRose"
          }
          setColourStyle(offsetNodes, colour)
        }
      }
    }

    def diagonalBoardLine(): Unit = {

      val nodesLR = boardGrid.getChildren.filter { node =>
        javafx.scene.layout.GridPane.getRowIndex(node) == javafx.scene.layout.GridPane.getColumnIndex(node)
      }

      val nodesRL =  boardGrid.getChildren.filter { node =>
        javafx.scene.layout.GridPane.getRowIndex(node) == 8 - javafx.scene.layout.GridPane.getColumnIndex(node)
      }

      nodesLR.foreach{ node =>
        // val variantLabel = node.asInstanceOf[javafx.scene.layout.StackPane].getChildren.get(2)
        val line = Line(0, 0, 65, 55)
        node.asInstanceOf[javafx.scene.layout.StackPane].getChildren.add(line)
      }

      nodesRL.foreach{ node =>
        val line = Line(0, 55, 65, 0)
        node.asInstanceOf[javafx.scene.layout.StackPane].getChildren.add(line)
      }

    }

    boardGrid.children.clear()

    val cages = if (board.isInstanceOf[KillerBoard]) board.asInstanceOf[KillerBoard].cagesList else null

    for (i <- board.boardBuffer.indices) {
      for (j <- board.boardBuffer.indices) {
        val stackPaneStyle = new StringBuilder("-fx-border-color: black; -fx-text-fill: black; -fx-border-style: solid;")
        val mainLabelStyle = new StringBuilder("-fx-alignment: center; -fx-text-fill: black; -fx-font-family: Georgia;")
        val stackPane = new StackPane()
        val mainLabel = new Label()
        val variantLabel = new Label() // A label for variant sudoku boards

        // For 3 x 3 subgrid
        if (i % 3 == 2 && i != 8 && (j % 3 != 2 || j == 8)) stackPaneStyle.append(" -fx-border-width: 1 1 3 1;")
        if (j % 3 == 2 && j != 8 && (i % 3 != 2 || i == 8)) stackPaneStyle.append(" -fx-border-width: 1 3 1 1;")
        if (i % 3 == 2 && j % 3 == 2 && (i != 8 && j != 8)) stackPaneStyle.append(" -fx-border-width: 1 3 3 1;")

        if (cages != null){
          cages.foreach{ cage =>
            if (cage.cellExists(i, j)) {
              val top = if (!cage.cellExists(i-1, j)) 2 else 0
              val right = if (!cage.cellExists(i, j+1)) 2 else 0
              val bottom = if (!cage.cellExists(i+1, j)) 2 else 0
              val left = if (!cage.cellExists(i, j-1)) 2 else 0
              mainLabelStyle.append(s" -fx-border-width: $top $right $bottom $left;")

              if (cage.checkTopLeft(i, j)){
                variantLabel.setText(cage.sum.toString)
                variantLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: red;")
                variantLabel.setFont(new Font(12.0))
                StackPane.setAlignment(variantLabel, Pos.TopLeft)
                StackPane.setMargin(variantLabel, new Insets(new geometry.Insets(5, 0, 0, 5)))
              }
            }
          }

          mainLabelStyle.append("-fx-border-color: dimgrey; -fx-border-style: segments(5, 5, 5, 5)  line-cap round ; -fx-border-insets: 2px;")
        }

        if (board.boardBuffer(i)(j).shown){
          mainLabel.setText(board.boardBuffer(i)(j).ans.toString)
          stackPane.id = "FIXED"
        } else{
          mainLabel.setText("")
        }


        mainLabel.setStyle(mainLabelStyle.toString)
        mainLabel.setFont(new Font(25.0))
        mainLabel.setPrefSize(65, 65)

        stackPane.setStyle(stackPaneStyle.toString)

        val notesGrid = new GridPane()
        notesGrid.setStyle("-fx-alignment: center; -fx-font-family: Georgia;")
        for (n <- board.boardBuffer.indices) {
          val noteLabel = new Label("")
          noteLabel.setFont(new Font(12.0))
          noteLabel.setPrefSize(10, 10)
          noteLabel.setStyle("-fx-alignment: center;")
          noteLabel.getStyleClass.clear() // removes default style
          notesGrid.add(noteLabel, n % 3, n / 3)
        }

        stackPane.getChildren.addAll(notesGrid, mainLabel, variantLabel)
        stackPane.setOnMouseClicked(_ => focusCell(i, j))
        stackPane.setOnKeyPressed(event => handleKeyPress(event, stackPane, i, j))
        boardGrid.add(stackPane, j, i)
        mainLabel.getStyleClass.clear() // removes default style
      }
    }

    if (board.isInstanceOf[OffsetBoard]) offsetBoardColouring()
    if (board.isInstanceOf[DiagonalBoard]) diagonalBoardLine()

    boardGrid.setAlignment(Pos.Center)
  }

  // Methods handling the completion of the board
  private def checkGameFinished(): Unit = {

    def checkBoardCompleted(): Boolean = {
      boardGrid.children.forall{node =>
        node.getId == "CORRECT" || node.getId == "FIXED"
      }
    }

    if (checkBoardCompleted()) {
      stopTimer()
      playCongratulationsAudio()
      val alert = new Alert(Alert.AlertType.Information){
        initOwner(MainApp.stage)
        title = "Game is completed!"
        headerText = "Congratulations on finishing the puzzle!"
        contentText = s"You scored ${game.score} points!"
      }.showAndWait()
      MainApp.showMainMenu()
    }
  }

  private def playCongratulationsAudio(): Unit = {
    val musicFile = getClass.getResource("../media/puzzle_solved.mp3")

    val audio = new Media(musicFile.toString)
    val mediaPlayer = new MediaPlayer(audio)
    mediaPlayer.volume = 100
    mediaPlayer.play()
  }

}