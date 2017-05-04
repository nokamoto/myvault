package myvault

import org.scalacheck.{Arbitrary, Gen}

package object service {
  implicit val vaultPasswordArb: Arbitrary[VaultKey] = Arbitrary(Gen.alphaNumStr.map(VaultKey.apply))
}
