package myvault.cli

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

import myvault.service.MyVaultService

object CopyCommand extends Command {
  override def exec(svc: MyVaultService)(implicit cli: Cli): Unit = {
    import cli._

    get(svc).foreach { password =>
      val s = new StringSelection(password.password)
      Toolkit.getDefaultToolkit.getSystemClipboard.setContents(s, s)
      success("Ok")
    }
  }

  override def commands: List[String] = "c" :: "copy" :: Nil
}
