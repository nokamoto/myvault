package myvault.cli

import myvault.service.MyVaultService

object HelpCommand extends Command {
  override def exec(svc: MyVaultService)(implicit cli: Cli): Unit = {
    println(Command.all.map(c => c.commands.mkString(", ")).mkString("\n"))
  }

  override def commands: List[String] = "h" :: "help" :: Nil
}
