package myvault.cli

import myvault.Password
import myvault.service.MyVaultService

object AddCommand extends Command {
  override def exec(svc: MyVaultService)(implicit cli: Cli): Unit = {
    import cli._

    val name = ask("Enter a name:")
    val user = ask("Enter a username:")
    val password = askPassword("Enter a password:")
    val confirm = askPassword("Confirm:")

    if (password == confirm) {
      svc.add(Password().update(_.name := name, _.username := user, _.password := password))
      success("Ok")
    } else {
      error("Not confirmed")
    }
  }

  override def commands: List[String] = "a" :: "add" :: Nil
}
