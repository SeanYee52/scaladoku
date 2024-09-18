package edu.sunway.sudoku.controllers

import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.Node.sfxNode2jfx
import scalafx.scene.control.ChoiceBox
import scalafx.scene.layout.VBox
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class HowToPlayDialogController (
                                private val normalSudokuText: VBox,
                                private val killerSudokuText: VBox,
                                private val offsetSudokuText: VBox,
                                private val diagonalSudokuText: VBox,
                                private val selectPuzzleBox: ChoiceBox[String]
                                )
{

  var dialogStage: Stage  = _
  private val textBoxList = List(normalSudokuText, killerSudokuText, offsetSudokuText, diagonalSudokuText)

  def initialise(): Unit = {
    initialiseSelectPuzzleBox()
  }

  private def initialiseSelectPuzzleBox(): Unit = {
    val options = ObservableBuffer("Normal Sudoku", "Killer Sudoku", "Offset Sudoku", "Diagonal Sudoku")
    selectPuzzleBox.setItems(options)
    selectPuzzleBox.value = options.head
    selectPuzzleBox.setOnAction { event =>
      textBoxList.foreach{ textBox =>
        textBox.setVisible(false)
      }
      selectPuzzleBox.getValue match {
        case "Normal Sudoku" => textBoxList(0).setVisible(true)
        case "Killer Sudoku" => textBoxList(1).setVisible(true)
        case "Offset Sudoku" => textBoxList(2).setVisible(true)
        case "Diagonal Sudoku" => textBoxList(3).setVisible(true)
      }
    }
  }

  def handleExitButton(actionEvent: ActionEvent): Unit = {
    dialogStage.close()
  }

}
