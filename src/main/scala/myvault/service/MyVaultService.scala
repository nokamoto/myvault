package myvault.service

import myvault.service.MyVaultService.MyVaultInitializationException
import myvault.{Password, PlainVault}

/**
  * Throws [[MyVaultInitializationException]] if [[key]] is invalid.
  */
abstract class MyVaultService(key: VaultKey) {
  private[this] val holder: MyVaultHolder = {
    try {
      new MyVaultHolder(key = key, init = init())
    } catch {
      case e: Exception => throw MyVaultInitializationException(e.getMessage, e)
    }
  }

  protected[this] def init(): Option[Array[Byte]]

  protected[this] def sync(bytes: Array[Byte]): Unit

  def vault: PlainVault = holder.vault

  def add(password: Password): Unit = holder.add(password)

  def sync(): Unit = {
    sync(holder.toByteArray)
  }
}

object MyVaultService {
  case class MyVaultInitializationException(message: String, cause: Exception) extends RuntimeException(message, cause)
}