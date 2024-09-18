package edu.sunway.sudoku.controllers

import edu.sunway.sudoku.MainApp
import edu.sunway.sudoku.game.Player
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, Label, Tooltip}
import scalafxml.core.macros.sfxml

/*
   */

@sfxml
class MainMenuController (
                         private val newGameButton: Button,
                         private val profileButton: Button,
                         private val newGameLayer: Label,
                         )
{

  private var player: Option[Player] = _

  def initialise(): Unit = {
    checkPlayer()
  }

  def handleNewGame(actionEvent: ActionEvent): Unit = {
    MainApp.showSelectDifficultyDialog()
  }

  def handleTutorial(actionEvent: ActionEvent): Unit = {
    MainApp.showHowToPlayDialog()
  }

  def handleLeaderboard(actionEvent: ActionEvent): Unit = {
    MainApp.showLeaderboardDialog()
  }

  def handleExit(actionEvent: ActionEvent): Unit = {
    System.exit(0)
  }

  private def checkPlayer(): Unit = {
    player = MainApp.currentPlayer
    if(player.isEmpty){
      newGameLayer.tooltip = new Tooltip("You need to have a profile to play!")
      newGameButton.disable = true
      profileButton.setText("Load Profile")
      profileButton.setOnMouseClicked{ _ =>
        MainApp.showLoadProfileDialog()
        checkPlayer()
      }
    } else {
      newGameLayer.tooltip = null
      newGameButton.disable = false
      profileButton.setText("View Profile")
      profileButton.setOnMouseClicked{ _ =>
        MainApp.showProfileDialog()
        checkPlayer()
      }
    }
  }


}