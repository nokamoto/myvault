package myvault.cli

import myvault.cli.Command.{Cont, Term}
import myvault.service.{LocalFileSync, MyVaultService, VaultKey}

import scala.util.{Failure, Success, Try}

object Main {
  private[this] def service(f: MyVaultService => Unit)(implicit cli: Cli): Unit = {
    import cli._

    val key = VaultKey(key = askPassword("Enter your vault key:"))

    Try(new MyVaultService(key = key) with LocalFileSync) match {
      case Failure(e) => error(s"Your vault key may be incorrect: ${e.getMessage}")
      case Success(svc) => f(svc)
    }
  }

  private[this] def command(svc: MyVaultService)(implicit cli: Cli): Unit = {
    import cli._

    val c = ask(">").toLowerCase

    val res = Command.all.find(_.commands.contains(c)) match {
      case Some(v) =>
        v.exec(svc)
        v.result

      case None =>
        error(s"Not a valid command: '$c'")
        Command.Cont
    }

    res match {
      case Term => ()
      case Cont => command(svc)
    }
  }

  private[cli] def run(implicit cli: Cli): Unit = {
    service(command)
  }

  def main(args: Array[String]): Unit = {
    run(Cli)
  }
}
