package myvault.service

import java.nio.file.Path

import myvault.Password
import myvault.service.LocalFileSyncSpec._
import myvault.service.MyVaultService.MyVaultInitializationException
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Assertion, FlatSpec}

class LocalFileSyncSpec extends FlatSpec with GeneratorDrivenPropertyChecks {
  it should "sync my vault" in {
    forAll { (passwords: Seq[Password], key: VaultKey) =>
      service(key) { (svc1, dir) =>
        passwords.foreach(password => svc1.add(password))

        svc1.sync()

        val svc2 = service(key, dir)

        assert(svc1.vault === svc2.vault)

        assertThrows[MyVaultInitializationException](service(key.copy(key = key.key + "!"), dir))

        svc2.vault.password.foreach(password => svc2.del(password.passwordId))

        assert(svc2.vault.password === Nil)

        svc2.sync()

        val svc3 = service(key, dir)

        assert(svc2.vault === svc3.vault)
      }
    }
  }
}

object LocalFileSyncSpec extends TempLocalFile {
  def service(key: VaultKey, d: Path): MyVaultService = {
    val svc = new MyVaultService(key = key) with LocalFileSync {
      override protected[this] def dir: String = d.toAbsolutePath.toString
    }
    svc
  }

  def service(key: VaultKey)(f: (MyVaultService, Path) => Assertion): Assertion = {
    withTemp(t =>  f(service(key, t), t))
  }
}
