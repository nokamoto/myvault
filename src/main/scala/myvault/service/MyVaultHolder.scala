package myvault.service

import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

import com.google.protobuf.ByteString
import myvault.{MyVault, Password, PlainVault}

class MyVaultHolder(key: VaultKey, init: Option[Array[Byte]]) {
  private[this] val myVaultRef: AtomicReference[MyVault] = {
    val v = init.map(MyVault.parseFrom).getOrElse {
      val salt = ByteString.copyFromUtf8(UUID.randomUUID().toString)
      MyVault().update(
        _.version := "0",
        _.salt := salt,
        _.encrypted := ByteString.copyFrom(encrypt(PlainVault().update(_.check := salt), salt.toByteArray))
      )
    }

    require(v.salt == decrypt(v.encrypted.toByteArray, v.salt.toByteArray).check)

    new AtomicReference[MyVault](v)
  }

  private[this] def withVault[A](f: MyVault => A): A = f(myVaultRef.get())

  private[this] def gen(f: MessageDigest => Unit): Array[Byte] = {
    val md = MessageDigest.getInstance("MD5")
    f(md)
    md.digest()
  }

  private[this] def genKey(salt: Array[Byte]): Array[Byte] = {
    gen { md =>
      md.update(key.bytes)
      md.update(salt)
      md.digest()
    }
  }

  private[this] def genIv(salt: Array[Byte]): Array[Byte] = {
    gen { md =>
      md.update(genKey(salt))
      md.update(key.bytes)
      md.update(salt)
    }
  }

  private[this] def aes(mode: Int, bytes: Array[Byte], salt: Array[Byte]): Array[Byte] = {
    val k = new SecretKeySpec(genKey(salt), "AES")
    val iv = new IvParameterSpec(genIv(salt))
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(mode, k, iv)
    cipher.doFinal(bytes)
  }

  private[this] def encrypt(vault: PlainVault, salt: Array[Byte]): Array[Byte] = {
    aes(Cipher.ENCRYPT_MODE, vault.toByteArray, salt)
  }

  private[this] def decrypt(bytes: Array[Byte], salt: Array[Byte]): PlainVault = {
    PlainVault.parseFrom(aes(Cipher.DECRYPT_MODE, bytes, salt))
  }

  private[this] def update(f: PlainVault => PlainVault) = {
    withVault { v =>
      val updated = f(decrypt(v.encrypted.toByteArray, v.salt.toByteArray))
      myVaultRef.set(v.update(_.encrypted := ByteString.copyFrom(encrypt(updated, v.salt.toByteArray))))
    }
  }

  def add(password: Password): Unit = {
    update(_.update(_.password :+= password))
  }

  def vault: PlainVault = {
    withVault { v =>
      decrypt(v.encrypted.toByteArray, v.salt.toByteArray)
    }
  }

  def toByteArray: Array[Byte] = withVault(_.toByteArray)
}