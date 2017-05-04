package myvault.service

case class VaultKey(key: String) {
  def bytes: Array[Byte] = key.getBytes("UTF-8")

  override def toString: String = "*" * key.length
}
