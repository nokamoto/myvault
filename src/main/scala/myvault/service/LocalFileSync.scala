package myvault.service

import java.nio.file.{Files, Paths, StandardOpenOption}

trait LocalFileSync {
  private[this] def prop = "sync.local"

  private[this] def filename = "myvault"

  protected[this] def dir: String = sys.props.get(prop).getOrElse(Paths.get(".").normalize().toAbsolutePath.toString)

  private[this] def file = Paths.get(dir, filename).normalize()

  private[this] def exists(): Boolean = file.toFile.exists()

  protected[this] def init(): Option[Array[Byte]] = {
    if (exists()) {
      Some(Files.readAllBytes(file))
    } else {
      None
    }
  }

  protected[this] def sync(bytes: Array[Byte]): Unit = {
    Files.write(file, bytes, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
  }
}
