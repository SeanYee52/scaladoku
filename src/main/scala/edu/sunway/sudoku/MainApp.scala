package edu.sunway.sudoku

import edu.sunway.sudoku.controllers._
import edu.sunway.sudoku.game.{Player, Sudoku}
import edu.sunway.sudoku.util.Database
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.stage.{Modality, Stage}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object MainApp extends JFXApp {

  // Database Initialised
  Database.setupDB()

  // Global variables to be passed into controllers
  private var _currentPlayer: Option[Player] = None
  def currentPlayer: Option[Player] = _currentPlayer
  private var _playersList: List[Player] = Player.getAllPlayers()
  def playersList: List[Player] = _playersList
  private var _gamesList: List[Sudoku] = Sudoku.getAllGames(playersList)
  def gamesList: List[Sudoku] = _gamesList
  private var gameType: Int = 0
  private var size: Int = 9
  private var difficulty: Int = 0

  // Root layout
  private val rootResource = getClass.getResource("view/RootLayout.fxml")
  private val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load();
  private val roots = loader.getRoot[jfxs.layout.BorderPane]

  // Root controller
  private val controller = loader.getController[RootLayoutController#Controller]
  controller.initialise()

  stage = new PrimaryStage {
    title = "Scaladoku"
    icons += new Image(getClass.getResourceAsStream("images/sudoku_icon.png"))
    scene = new Scene {
      root = roots
    }
  }

  // Main menu layout
  def showMainMenu(): Unit = {
    val resource = getClass.getResource("view/MainMenu.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val mainMenuOverview = loader.getRoot[jfxs.layout.BorderPane]

    // Get the controller and initialise the main menu
    val controller = loader.getController[MainMenuController#Controller]
    controller.initialise()

    roots.setCenter(mainMenuOverview)
  }

  showMainMenu()

  // Board layout
  def showBoardOverview(): Unit = {
    val resource = getClass.getResource("view/BoardOverview.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val boardOverview = loader.getRoot[jfxs.layout.BorderPane]

    // Get the controller and initialise the board
    val controller = loader.getController[BoardOverviewController#Controller]
    controller.difficulty = difficulty
    controller.player = _currentPlayer.get
    controller.initialise(gameType, size)

    roots.setCenter(boardOverview)
  }

  // Dialog Controllers
  def showProfileDialog(): Unit = {
    _gamesList = Sudoku.getAllGames(playersList) // Updates the list of games

    val resource = getClass.getResource("view/ProfileDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[ProfileDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      icons += new Image(getClass.getResourceAsStream("images/profile_icon.png"))
      scene = new Scene {
        root = roots
      }
    }

    controller.dialogStage = dialog
    controller.currentPlayer = currentPlayer
    controller.gamesList = gamesList
    controller.initialise()
    dialog.showAndWait()
    _currentPlayer = controller.currentPlayer
  }

  def showLeaderboardDialog(): Unit = {
    _playersList = Player.getAllPlayers() // Updates the list of players
    _gamesList = Sudoku.getAllGames(playersList) // Updates the list of games

    val resource = getClass.getResource("view/LeaderboardDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[LeaderboardDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      icons += new Image(getClass.getResourceAsStream("images/leaderboard_icon.png"))
      scene = new Scene {
        root = roots
      }
    }

    controller.dialogStage = dialog
    controller.gamesList = gamesList
    controller.playersList = playersList
    controller.initialise()
    dialog.showAndWait()
  }

  def showLoadProfileDialog(): Unit = {
    val resource = getClass.getResource("view/LoadProfileDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[LoadProfileDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      icons += new Image(getClass.getResourceAsStream("images/profile_icon.png"))
      scene = new Scene {
        root = roots
      }
    }

    controller.dialogStage = dialog
    controller.currentPlayer = currentPlayer
    dialog.showAndWait()
    _currentPlayer = controller.currentPlayer
    if (controller.newStatus) _playersList = _playersList :+ controller.currentPlayer.get
  }

  def showSelectDifficultyDialog(): Unit = {
    val resource = getClass.getResource("view/SelectDifficultyDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[SelectDifficultyDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      icons += new Image(getClass.getResourceAsStream("images/sudoku_icon.png"))
      scene = new Scene {
        root = roots
      }
    }

    controller.dialogStage = dialog
    controller.initialise
    dialog.showAndWait()
    if (controller.choiceSelected){
      gameType = controller.gameType
      difficulty = controller.difficulty
      showBoardOverview()
    }
  }

  def showHowToPlayDialog(): Unit = {
    val resource = getClass.getResource("view/HowToPlayDialog.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.Parent]
    val controller = loader.getController[HowToPlayDialogController#Controller]

    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      icons += new Image(getClass.getResourceAsStream("images/question_icon.png"))
      scene = new Scene {
        root = roots
      }
    }

    controller.dialogStage = dialog
    controller.initialise()
    dialog.showAndWait()
  }

}
