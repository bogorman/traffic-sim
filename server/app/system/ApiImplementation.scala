package system

import autowire.Server
import shared.map.MapApi
import upickle.{ReaderPicker, WriterPicker}

object ApiImplementation extends MapApi {
  override def test: String = "Takie zajebiste Ajaxy ktore nie dzialaja"
}

object MyServer extends Server[String, ReaderPicker, WriterPicker] {
  def write[Result: WriterPicker](r: Result) = upickle.default.write(r)

  def read[Result: ReaderPicker](p: String) = upickle.default.read[Result](p)

  val routes = MyServer.route[MapApi](ApiImplementation)
}
