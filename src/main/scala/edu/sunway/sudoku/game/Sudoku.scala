package edu.sunway.sudoku.game

import edu.sunway.sudoku.game.board._
import edu.sunway.sudoku.util.Database
import scalikejdbc._

import scala.util.Try

// A 9 by 9 board for Sudoku
class Sudoku (private val idI: Int, private val difficultyI: Int, private val board: Board, private val _player: Player, private val sizeI : Int) extends Database {

  // Variables to be initialised
  private var _id = idI
  def id: Int = _id
  private var _score: Int = 0

  def difficulty: Int = difficultyI
  def size: Int = sizeI
  def score: Int = _score
  def score_= (newScore: Int): Unit = _score = newScore
  def player: Player = _player

  def createBoard(): Unit = {
    board.generateBoard(difficulty, size)
  }

  def setScore(totalTime: Int): Unit = {
    _score = (81 * difficulty * difficulty * difficulty) / totalTime
    player.updateTotalScore(_score)
    save()
  }

  private def getType(): String = {
    board match {
      case _: KillerBoard => "Killer"
      case _: NormalBoard => "Normal"
      case _: OffsetBoard => "Offset"
      case _: DiagonalBoard => "Diagonal"
      case _ => "Invalid"
    }
  }

  def save(): Unit = {
    if (id == 0) {
      Try(DB autoCommit{ implicit session =>
        val generatedID =
          sql"""
               INSERT INTO Games (difficulty, score, type, playerID, size)
               VALUES ($difficulty, $score, ${getType()}, ${player.id}, $size)
             """.updateAndReturnGeneratedKey().apply()

        _id = generatedID.toInt
      })
    }
  }

  def delete(): Unit = {
    if (isExist()) {
      Try(DB autoCommit{ implicit session =>
        sql"""
             DELETE FROM Games WHERE id = $id
           """.update.apply()
      })
    } else {
      throw new Exception("Game does not exist in database")
    }
  }

  def isExist(): Boolean = {
    DB readOnly{ implicit session =>
      sql"""
           SELECT * FROM Games WHERE id = $id
         """.map(rs => rs.int("score")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }
  }

}

object Sudoku extends Database{
  implicit val listOrder: Ordering[Sudoku] = Ordering.by[Sudoku, Int](_.score).reverse

  def apply(
           id: Int,
           difficulty: Int,
           board: Board,
           player: Player,
           scoreI: Int,
           size: Int
           ): Sudoku = {
    new Sudoku (id, difficulty, board, player, size){
      score = scoreI
    }
  }

  def initialiseTable(): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
           CREATE TABLE Games (
           id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
           difficulty INT NOT NULL,
           score INT NOT NULL,
           size INT NOT NULL,
           type VARCHAR(20) NOT NULL,
           playerID INT NOT NULL,
           FOREIGN KEY (playerID) REFERENCES Players
           )
         """.execute.apply()
    }
  }

  def getAllGames(Players: List[Player]): List[Sudoku] = {
    DB readOnly{ implicit session =>
      sql"SELECT * FROM Games".map(rs =>
        Sudoku(
          rs.int("id"),
          rs.int("difficulty"),
          rs.string("type") match {
            case "Normal" => new NormalBoard
            case "Killer" => new KillerBoard
            case "Diagonal" => new DiagonalBoard
            case "Offset" => new OffsetBoard
          },
          Players.find(player => player.id == rs.int("playerID")).get,
          rs.int("score"),
          rs.int("size")
        )
      ).list.apply()
    }
  }
}