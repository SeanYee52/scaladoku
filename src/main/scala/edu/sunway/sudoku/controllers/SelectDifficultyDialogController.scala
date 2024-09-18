package edu.sunway.sudoku.controllers

import scalafx.collections.ObservableBuffer
import scalafx.event.ActionEvent
import scalafx.scene.control.ChoiceBox
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class SelectDifficultyDialogController (
                                         private val sudokuChoiceBox: ChoiceBox[String],
                                         private val difficultyChoiceBox: ChoiceBox[String],
                                       )
{
  var dialogStage: Stage = _
  var gameType: Int = 0
  var difficulty: Int = 0
  var choiceSelected: Boolean = false

  def initialise(): Unit = {
    initialiseSudokuChoiceBox()
    initialiseDifficultyChoiceBox()
  }

  private def initialiseSudokuChoiceBox(): Unit = {
    val options = ObservableBuffer("Normal Sudoku", "Killer Sudoku", "Offset Sudoku", "Diagonal Sudoku")
    sudokuChoiceBox.setItems(options)
    sudokuChoiceBox.value = options.head
  }

  private def initialiseDifficultyChoiceBox(): Unit = {
    val options = ObservableBuffer("Easy", "Normal", "Hard")
    difficultyChoiceBox.setItems(options)
    difficultyChoiceBox.value = options.head
  }

  def handleConfirm(actionEvent: ActionEvent): Unit = {
    sudokuChoiceBox.getValue match {
      case "Normal Sudoku" => gameType = 0
      case "Killer Sudoku" => gameType = 1
      case "Offset Sudoku" => gameType = 2
      case "Diagonal Sudoku" => gameType = 3
    }
    difficultyChoiceBox.getValue match {
      case "Easy" => difficulty = 25
      case "Normal" => difficulty = 30
      case "Hard" => difficulty = 35
    }
    choiceSelected = true
    dialogStage.close()
  }

  def handleExit(actionEvent: ActionEvent): Unit = {
    dialogStage.close()
  }
}
