package myvault.cli

import myvault.service.MyVaultService

object DelCommand extends Command {
  override def exec(svc: MyVaultService)(implicit cli: Cli): Unit = {
    import cli._

    get(svc).foreach { password =>
      ask("Are you sure you want to delete? (y/n)").toLowerCase match {
        case "y" =>
          svc.del(password.passwordId)
          success("Ok")
      }
    }
  }

  override def commands: List[String] = "d" :: "del" :: Nil
}
