package dao

import javax.inject._
import com.google.inject.ImplementedBy
import play.api.db.Database
import models.Archivo

@ImplementedBy(classOf[DoobieStore])
trait FileStore {
  /** Guardar un archivo. Devuelve el id del mismo */
  def store(name: String, bytes: Array[Byte], hash: String): Long

  def archivos(): List[Archivo]
}

class DoobieStore @Inject() (db: Database) extends FileStore {
  import doobie.imports._
  import cats._, cats.data._, cats.implicits._

  val xa = DataSourceTransactor[IOLite](db.dataSource)

  def insert(name: String, bytes: Array[Byte], hash: String): ConnectionIO[Long] =
    sql"""insert into file_info(name, hash, data)
          values($name, $hash, $bytes)""".update.withUniqueGeneratedKeys("id")

  def all: ConnectionIO[List[Archivo]] = {
    sql"""select id, name, hash, fecha from file_info""".query[Archivo].list
  }

  def store(name: String, bytes: Array[Byte], hash: String): Long = {
    insert(name, bytes, hash).transact(xa).unsafePerformIO
  }

  def archivos() = all.transact(xa).unsafePerformIO
}
