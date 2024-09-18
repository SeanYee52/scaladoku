package edu.sunway.sudoku.controllers

import edu.sunway.sudoku.game.{Player, Sudoku}
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, ButtonType, Label}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class ProfileDialogController(
                              private val usernameLabel: Label,
                              private val totalScoreLabel: Label,
                              private val totalGamesLabel: Label,
                              private val bestScoreLabel: Label,
                             )
{

  var dialogStage: Stage = _
  var currentPlayer: Option[Player] = None
  var gamesList: List[Sudoku] = _
  private var filteredList: List[Sudoku] = List.empty[Sudoku]

  def initialise(): Unit = {
    filteredList = gamesList.filter(game => game.player.equals(currentPlayer.get))

    usernameLabel.setText(currentPlayer.get.name)
    totalScoreLabel.setText(currentPlayer.get.totalScore.toString)
    totalGamesLabel.setText(filteredList.size.toString)
    bestScoreLabel.setText(if(filteredList.nonEmpty) filteredList.min.score.toString else "0") // Due to reverse ordering declared in the Sudoku class, used min instead of max
  }

  def handleDelete(actionEvent: ActionEvent): Unit = {
    val confirmation = new Alert(Alert.AlertType.Confirmation){
      initOwner(dialogStage)
      title = "Are you sure?"
      headerText = "Are you sure you want to delete this profile?"
      contentText = "Once deleted, it is gone forever!"
    }.showAndWait()

    if (confirmation.contains(ButtonType.OK)){
      currentPlayer.get.delete()
      currentPlayer = None
      dialogStage.close()
    }
  }

  def handleExit(actionEvent: ActionEvent): Unit = {
    dialogStage.close()
  }

}