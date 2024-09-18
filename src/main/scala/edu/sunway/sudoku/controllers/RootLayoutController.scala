package edu.sunway.sudoku.controllers

import edu.sunway.sudoku.MainApp
import javafx.collections.ListChangeListener
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, ButtonType, MenuBar, MenuItem}
import scalafx.scene.layout.BorderPane
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController (
                           private val menuBar: MenuBar,
                           private val howToPlayMenu: MenuItem,
                           private val exitMenu: MenuItem,
                           private val layout: BorderPane
                           )
{

  def initialise(): Unit = {
    layout.getChildren.addListener(new ListChangeListener[javafx.scene.Node] {
      override def onChanged(change: ListChangeListener.Change[_ <: javafx.scene.Node]): Unit = {
        if (layout.getChildren.size > 1) {
          if (layout.getChildren.get(1).getId == "mainMenu") {
            menuBar.visible = false
            menuBar.disable = true
            howToPlayMenu.disable = true
            exitMenu.disable = true
          } else{
            menuBar.visible = true
            menuBar.disable = false
            howToPlayMenu.disable = false
            exitMenu.disable = false
          }
        }
      }
    })
  }


  def handleExit(actionEvent: ActionEvent): Unit = {
    val confirmation = new Alert(Alert.AlertType.Confirmation){
      initOwner(MainApp.stage)
      title = "Are you sure?"
      headerText = "Are you sure you want to exit the game?"
      contentText = "Your progress is not saved!"
    }.showAndWait()

    if (confirmation.contains(ButtonType.OK)){
      MainApp.showMainMenu()
    }
  }

  def handleShowHowToPlay(actionEvent: ActionEvent): Unit = {
    MainApp.showHowToPlayDialog()
  }
}
