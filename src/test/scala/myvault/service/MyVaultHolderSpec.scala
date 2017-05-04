package myvault.service

import myvault.Password
import org.scalatest.FlatSpec
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class MyVaultHolderSpec extends FlatSpec with GeneratorDrivenPropertyChecks {
  it should "add passwords" in {
    forAll { (passwords: Seq[Password], key: VaultKey) =>
      val svc = new MyVaultHolder(key = key, init = None)

      passwords.foreach(password => svc.add(password))

      assert(svc.vault.password === passwords)
    }
  }

  it should "copy from bytes" in {
    forAll { (passwords: Seq[Password], key: VaultKey) =>
      val svc1 = new MyVaultHolder(key = key, init = None)

      passwords.foreach(password => svc1.add(password))

      val svc2 = new MyVaultHolder(key = key, init = Some(svc1.toByteArray))

      assert(svc1.vault === svc2.vault)
    }
  }

  it should "raise an exception if copy from bytes with an illegal key" in {
    forAll { (passwords: Seq[Password], key: VaultKey) =>
      val svc1 = new MyVaultHolder(key = key, init = None)

      passwords.foreach(password => svc1.add(password))

      assertThrows[Exception] {
        new MyVaultHolder(key = key.copy(key = key.key + "!"), init = Some(svc1.toByteArray))
      }
    }
  }
}