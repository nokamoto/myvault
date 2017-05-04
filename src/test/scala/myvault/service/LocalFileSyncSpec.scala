package myvault.service

import java.nio.file.{Files, Path}

import myvault.Password
import org.scalatest.{Assertion, FlatSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import myvault.service.LocalFileSyncSpec._
import myvault.service.MyVaultService.MyVaultInitializationException

class LocalFileSyncSpec extends FlatSpec with GeneratorDrivenPropertyChecks {
  it should "add passwords" in {
    forAll { (passwords: Seq[Password], key: VaultKey) =>
      service(key) { (svc, _) =>
        passwords.foreach(password => svc.add(password))

        assert(svc.vault.password === passwords)
      }
    }
  }

  it should "sync my vault" in {
    forAll { (passwords: Seq[Password], key: VaultKey) =>
      service(key) { (svc1, dir) =>
        passwords.foreach(password => svc1.add(password))

        svc1.sync()

        val svc2 = service(key, dir)

        assert(svc1.vault === svc2.vault)

        assertThrows[MyVaultInitializationException](service(key.copy(key = key.key + "!"), dir))
      }
    }
  }
}

object LocalFileSyncSpec {
  def temp(): Path = Files.createTempDirectory("LocalFileSyncSpec")

  def service(key: VaultKey, d: Path): MyVaultService = {
    val svc = new MyVaultService(key = key) with LocalFileSync {
      override protected[this] def dir: String = d.toAbsolutePath.toString
    }
    svc
  }

  def service(key: VaultKey)(f: (MyVaultService, Path) => Assertion): Assertion = {
    val t = temp()
    try {
      f(service(key, t), t)
    } finally {
      t.toFile.deleteOnExit()
    }
  }
}
