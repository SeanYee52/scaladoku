package edu.sunway.sudoku.util

import scalikejdbc._
import edu.sunway.sudoku.game.Player
import edu.sunway.sudoku.game.Sudoku

trait Database {
  private val dbURL = "jdbc:derby:myDB;create=true;"; // initialize JDBC driver & connection pool Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "me", "mine") // ad-hoc session provider on the REPL
  implicit val session: AutoSession.type = AutoSession
}

object Database extends Database {
  def setupDB(): Unit = {

    if (!hasTableInitialise("Players")) {
      Player.initialiseTable()
    }
    if (!hasTableInitialise("Games")) {
      Sudoku.initialiseTable()
    }

    def hasTableInitialise(Table: String): Boolean = {
      DB getTable Table match {
        case Some(_) => true
        case None => false
      }
    }

  }
}