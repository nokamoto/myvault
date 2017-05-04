package myvault.service

import java.nio.file.{Files, Path}

trait TempLocalFile {
  protected[this] def withTemp[A](f: Path => A): A = {
    val file = Files.createTempDirectory("TempLocalFile")
    try {
      f(file)
    } finally {
      file.toFile.deleteOnExit()
    }
  }
}
