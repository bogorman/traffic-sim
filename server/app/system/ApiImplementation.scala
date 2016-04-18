package system

import autowire.Server
import shared.map.MapApi
import upickle.Js
import upickle.default._

object ApiImplementation extends MapApi {
  override def test: String = "Takie zajebiste Ajaxy ktore nie dzialaja"
}

object MyServer extends Server[Js.Value, Reader, Writer] {

  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)

  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)


  //  val routes = MyServer.route[MapApi](ApiImplementation)
}
