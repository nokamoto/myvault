import org.scalacheck.{Arbitrary, Gen}

package object myvault {
  implicit val passwordArb: Arbitrary[Password] = Arbitrary(Gen.resultOf(Password.apply _))

  implicit class Downscale(password: Password) {
    def downscaled: Password = password.update(_.passwordId := "")
  }
}
