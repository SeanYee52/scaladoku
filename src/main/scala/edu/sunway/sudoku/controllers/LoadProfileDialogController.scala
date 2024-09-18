package edu.sunway.sudoku.controllers

import edu.sunway.sudoku.game.Player
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, TextField}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class LoadProfileDialogController(
                                 private val nameTextField: TextField,
                                 private val passwordTextField: TextField,
                                 )
{
  var dialogStage: Stage  = _
  var currentPlayer: Option[Player] = None
  var newStatus = false

  def handleLoad(actionEvent: ActionEvent): Unit = {
    def checkPassword(name: String, password: String): Option[Player] = {
      val players = Player.getAllPlayers()
      players.find(player => player.name.equals(name) && player.password.equals(password))
    }

    if(isInputValid()) {
      val check = checkIfPlayerExists(nameTextField.text.value)
      if (check) {
        val temp = checkPassword(nameTextField.text.value, passwordTextField.text.value)
        if (temp.nonEmpty) {
          currentPlayer = temp
          dialogStage.close()
        } else {
          val alert = new Alert(Alert.AlertType.Error) {
            initOwner(dialogStage)
            title = "Wrong password!"
            headerText = "Please enter your password again!"
            contentText = "Password is incorrect, please try again"
          }.showAndWait()
        }
      } else {
        val alert = new Alert(Alert.AlertType.Error) {
          initOwner(dialogStage)
          title = "Player does not exist!"
          headerText = "This player does not exist!"
          contentText = "Please use another name or create a new profile!"
        }.showAndWait()
      }
    }

  }

  def handleCreate(actionEvent: ActionEvent): Unit = {
    if (isInputValid())
      if (!checkIfPlayerExists(nameTextField.text.value)) {
        val newPlayer = new Player(0, nameTextField.text.value, passwordTextField.text.value)
        newPlayer.save()
        currentPlayer = Some(newPlayer)
        newStatus = true
        dialogStage.close()
      } else {
        val alert = new Alert(Alert.AlertType.Error){
          initOwner(dialogStage)
          title = "Player already exists!"
          headerText = "This player already exists!"
          contentText = "Please use another name or log into the existing profile!"
        }.showAndWait()
      }
  }

  private def checkIfPlayerExists(name: String): Boolean = {
    val players = Player.getAllPlayers()
    players.exists(player => player.name.equals(name))
  }

  private def isInputValid(): Boolean = {
    var errorMessage = ""

    def nullChecking (x : String) = x == null || x.isEmpty
    def textLimitChecking (x: String) = x.length > 64
    def passwordLengthMinimumCheck (x: String) = x.length < 8

    if(nullChecking(nameTextField.text.value))
      errorMessage += "Name is empty!\n"
    if(nullChecking(passwordTextField.text.value) || passwordLengthMinimumCheck(passwordTextField.text.value))
      errorMessage += "Password is too short! Minimum is 8 characters!\n"
    if(textLimitChecking(nameTextField.text.value))
      errorMessage += "Name is too long!\n"
    if(textLimitChecking(passwordTextField.text.value))
      errorMessage += "Password is too long"

    if(errorMessage.nonEmpty){
      // Show the error message.
      val alert = new Alert(Alert.AlertType.Error){
        initOwner(dialogStage)
        title = "Invalid Fields"
        headerText = "Please correct invalid fields"
        contentText = errorMessage
      }.showAndWait()
      false
    } else {
      true
    }
  }

  def handleExit(actionEvent: ActionEvent): Unit = {
    dialogStage.close()
  }
}
