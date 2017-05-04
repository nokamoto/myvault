package myvault.cli

import myvault.Password
import myvault.cli.Command.Result
import myvault.service.MyVaultService

trait Command {
  protected[this] def get(svc: MyVaultService)(implicit cli: Cli): Option[Password] = {
    import cli._

    val s = ask("Enter a number:")

    try {
      val password = svc.vault.password.apply(s.toInt)

      println(password.name)
      println(s"Password: " + "*" * password.password.length)

      Some(password)
    } catch {
      case _: Exception =>
        error(s"Not a valid number: '$s'")
        None
    }
  }

  def result: Result = Command.Cont

  def commands: List[String]

  def exec(svc: MyVaultService)(implicit cli: Cli): Unit
}

object Command {
  sealed trait Result

  case object Term extends Result
  case object Cont extends Result

  val all: List[Command] = AddCommand :: ListCommand :: HelpCommand :: CopyCommand :: DelCommand :: QuitCommand :: SyncCommand :: Nil
}
