package edu.sunway.sudoku.controllers

import edu.sunway.sudoku.game.{Player, Sudoku}
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.VBox
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class LeaderboardDialogController(
                                 private val listVBox: VBox,
                                 private val topGamesButton: Button,
                                 private val topPlayersButton: Button
                                 )
{
  var dialogStage: Stage = _
  var gamesList: List[Sudoku] = _
  var playersList: List[Player] = _
  private val buttonDefaultStyle = "-fx-text-fill: grey; -fx-background-color: white; -fx-border-color: gray; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-font-size: 14pt; -fx-font-family: Georgia;"
  private val buttonHoveredStyle = "-fx-text-fill: royalblue; -fx-background-color: gainsboro; -fx-border-color: gray; -fx-font-weight: bold; -fx-border-radius: 5px; -fx-font-size: 14pt; -fx-font-family: Georgia;"

  def initialise(): Unit = {
    setButtonStyle(topGamesButton)
    setButtonStyle(topPlayersButton)
    gamesList = gamesList.sorted
    playersList = playersList.sorted
    showPlayersList()
    buttonSelected(topPlayersButton)
  }

  private def setButtonStyle(button: Button): Unit = {
    button.setStyle(buttonDefaultStyle)
    button.setOnMouseEntered(_ => button.setStyle(buttonHoveredStyle))
    button.setOnMouseExited(_ => button.setStyle(buttonDefaultStyle))
  }

  private def buttonSelected(button: Button): Unit = {
    button.setStyle(buttonHoveredStyle)
    button.setOnMouseExited(_ => button.setStyle(buttonHoveredStyle))
  }

  private def showPlayersList(): Unit = {
    if(!listVBox.children.isEmpty) listVBox.children.clear()
    playersList.foreach{ player =>
      val playerBox = new VBox()
      playerBox.setStyle("-fx-padding: 5px; -fx-border-insets: 5px; -fx-background-insets: 5px;")
      playerBox.setSpacing(5)

      val playerNameLabel = new Label(s"Player: ${player.name}")
      val playerTotalScoreLabel = new Label(s"Total Score = ${player.totalScore}")

      playerNameLabel.getStyleClass.add("labeledText")

      playerBox.getChildren.addAll(playerNameLabel, playerTotalScoreLabel)

      listVBox.getChildren.add(playerBox)
    }
  }

  private def showGamesList(): Unit = {
    if(!listVBox.children.isEmpty) listVBox.children.clear()
    gamesList.foreach{ game =>
      val gameBox = new VBox()
      gameBox.setStyle("-fx-padding: 5px; -fx-border-insets: 5px; -fx-background-insets: 5px;")
      gameBox.setSpacing(5)

      val gameNameLabel = new Label(s"Game #${game.id}")
      val gamePlayerLabel = new Label(s"Player: ${game.player.name}")
      val gameScoreLabel = new Label(s"Score = ${game.score}")

      gameNameLabel.getStyleClass.add("labeledText")

      gameBox.getChildren.addAll(gameNameLabel, gamePlayerLabel, gameScoreLabel)

      listVBox.getChildren.add(gameBox)
    }
  }

  def handleTopPlayers(actionEvent: ActionEvent): Unit = {
    buttonSelected(topPlayersButton)
    setButtonStyle(topGamesButton)
    showPlayersList()
  }

  def handleTopGames(actionEvent: ActionEvent): Unit = {
    buttonSelected(topGamesButton)
    setButtonStyle(topPlayersButton)
    showGamesList()
  }

}