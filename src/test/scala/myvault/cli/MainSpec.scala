package myvault.cli

import java.io.{BufferedReader, StringReader}

import myvault.Password
import myvault.service.{LocalFileSync, MyVaultService, TempLocalFile, VaultKey}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Assertion, FlatSpec}

import scala.util.Try

class MainSpec extends FlatSpec  with GeneratorDrivenPropertyChecks {
  import myvault.cli.MainSpec._

  it should "quit" in {
    forAll { (key: VaultKey, password: Password) =>
      val input =
        s"""${key.key}
           |${addCommand(password)}
           |quit
       """.stripMargin

      withMain(input) { _ =>
        val svc = new MyVaultService(key = key) with LocalFileSync
        assert(svc.vault.password === Nil)
      }
    }
  }

  it should "sync" in {
    forAll { (key: VaultKey, password: Password) =>
      val input =
        s"""${key.key}
           |${addCommand(password)}
           |sync
           |quit
       """.stripMargin

      withMain(input) { _ =>
        val svc = new MyVaultService(key = key) with LocalFileSync
        assert(svc.vault.password.map(_.downscaled) === password.downscaled :: Nil)
      }
    }
  }

  it should "not add a password not confirmed" in {
    forAll { (key: VaultKey, password: Password) =>
      val input =
        s"""${key.key}
           |add
           |${password.name}
           |${password.password}
           |${password.password + "!"}
           |sync
           |quit
       """.stripMargin

      withMain(input) { _ =>
        val svc = new MyVaultService(key = key) with LocalFileSync
        assert(svc.vault.password === Nil)
      }
    }
  }

  it should "list passwords" in {
    forAll { (key: VaultKey, password1: Password, password2: Password) =>

      val input =
        s"""${key.key}
           |${addCommand(password1)}
           |${addCommand(password2)}
           |list
           |sync
           |quit
         """.stripMargin

      withMain(input) { output =>
        val svc = new MyVaultService(key = key) with LocalFileSync
        assert(svc.vault.password.map(_.downscaled) === (password1 :: password2 :: Nil).map(_.downscaled))

        val lines = output.split("\n")
        assert(lines(2) === s"[0] ${password1.name}")
        assert(lines(5) === s"[1] ${password2.name}")
      }
    }
  }
}

object MainSpec extends TempLocalFile {
  def addCommand(password: Password): String = {
    s"""add
       |${password.name}
       |${password.username}
       |${password.password}
       |${password.password}""".stripMargin
  }

  def withMain(input: String)(f: String => Assertion): Unit = {
    val prop = "sync.local"
    withTemp { temp =>
      try {
        sys.props.update(prop, temp.toAbsolutePath.toString)

        var output = ""

        val cli = new Cli {
          private[this] val buffer = new BufferedReader(new StringReader(input))

          override def print(s: String): Unit = output = output + s

          override def ask(s: String): String = buffer.readLine()

          override def askInt(s: String): Try[Int] = Try(buffer.readLine().toInt)

          override def askPassword(s: String): String = buffer.readLine()
        }

        Main.run(cli)

        f(output)
      } finally {
        sys.props.remove(prop)
      }
    }
  }
}
