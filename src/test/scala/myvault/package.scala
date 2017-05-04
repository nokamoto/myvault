import org.scalacheck.{Arbitrary, Gen}

package object myvault {
  implicit val passwordArb: Arbitrary[Password] = Arbitrary(Gen.resultOf(Password.apply _))
}
