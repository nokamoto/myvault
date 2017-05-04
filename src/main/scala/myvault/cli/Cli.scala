package myvault.cli

import scala.util.Try

trait Cli {
  def println(s: String): Unit = print(s"$s\n")

  def print(s: String): Unit = System.out.print(s)

  def success(s: String): Unit = println(s"[${Console.GREEN}success${Console.RESET}] $s")

  def error(s: String): Unit = println(s"[${Console.RED}error${Console.RESET}] $s")

  private[this] def prompt(s: String): Unit = print(s"$s ")

  def ask(s: String): String = {
    prompt(s)
    System.console().readLine()
  }

  def askInt(s: String): Try[Int] = Try(ask(s).toInt)

  def askPassword(s: String): String = {
    prompt(s)
    System.console().readPassword().mkString
  }
}

object Cli extends Cli
