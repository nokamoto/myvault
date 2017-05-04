package myvault.cli

import myvault.service.MyVaultService

object ListCommand extends Command {
  override def exec(svc: MyVaultService)(implicit cli: Cli): Unit = {
    svc.vault.password.zipWithIndex.foreach { case (password, n) =>
      cli.println(s"[$n] ${password.name}")
      cli.println(s"Username: ${password.username}")
      cli.println(s"Password: " + "*" * password.password.length)
    }
  }

  override def commands: List[String] = "l" :: "list" :: Nil
}
