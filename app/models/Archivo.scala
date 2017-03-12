package models

import java.sql.Timestamp

case class Archivo(
  id:    Long,
  name:  String,
  hash:  String,
  fecha: Timestamp
)
