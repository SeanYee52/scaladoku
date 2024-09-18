package edu.sunway.sudoku.game

import edu.sunway.sudoku.util.Database
import scalikejdbc._

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

case class Player (private val idI: Int, private val nameS: String, private val passwordS: String) extends Database{
  def this() = this(0,null,null)

  private var _id: Int = idI
  private var _totalScore = 0

  def id: Int = _id
  def name: String = nameS
  def password: String = passwordS
  def totalScore: Int = _totalScore


  def updateTotalScore(score: Int): Unit = {
    _totalScore += score
    save()
  }

  def save(): Unit = {
    if(id == 0){
      Try(DB autoCommit{ implicit session =>
        val generatedID =
          sql"""
            INSERT INTO Players (name, totalScore, password)
            VALUES ($name, $totalScore, $password)
             """.updateAndReturnGeneratedKey().apply()

        _id = generatedID.toInt
      })
    } else{
      Try(DB autoCommit{ implicit session =>
        sql"""
             UPDATE Players
             SET totalScore = $totalScore
             WHERE id = $id
           """.update.apply()
      })
    }
  }

  def delete(): Unit = {
    if (isExist()) {
      Try(DB autoCommit{ implicit session =>
        sql"""
             DELETE FROM Games WHERE playerId = $id
           """.update.apply()
        sql"""
              DELETE FROM Players WHERE id = $id
           """.update.apply()
      })
    } else{
      throw new Exception("Player does not exist in database")
    }
  }

  def isExist(): Boolean = {
    DB readOnly{ implicit session =>
      sql"""
           SELECT * FROM Players WHERE id = $id
         """.map(rs => rs.string("name")).single.apply()
    } match {
      case Some(x) => true
      case None => false
    }
  }
}

object Player extends Database {
  implicit val listOrder: Ordering[Player] = Ordering.by[Player, Int](_.totalScore).reverse

  def apply (
            id: Int,
            name: String,
            totalScoreI: Int,
            password: String
            ) : Player = {
    new Player(id, name, password){
      updateTotalScore(totalScoreI)
    }
  }

  def initialiseTable(): Boolean = {
    DB autoCommit { implicit session =>
      sql"""
           CREATE TABLE Players (
           id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) PRIMARY KEY,
           name VARCHAR(64),
           totalScore INT,
           password VARCHAR(64)
           )
         """.execute.apply()
    }
  }

  def getAllPlayers(): List[Player] = {
    DB readOnly{ implicit session =>
      sql"SELECT * FROM Players".map(rs => Player(rs.int("id"), rs.string("name"), rs.int("totalScore"), rs.string("password"))).list.apply()
    }
  }
}