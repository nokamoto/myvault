package myvault.cli

import myvault.service.MyVaultService

object QuitCommand extends Command {
  override def result: Command.Result = Command.Term

  override def exec(svc: MyVaultService)(implicit cli: Cli): Unit = ()

  override def commands: List[String] = "q" :: "quit" :: Nil
}
