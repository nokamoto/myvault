package myvault.cli

import myvault.service.MyVaultService

object SyncCommand extends Command {
  override def exec(svc: MyVaultService)(implicit cli: Cli): Unit = {
    svc.sync()
  }

  override def commands: List[String] = "s" :: "sync" :: Nil
}
