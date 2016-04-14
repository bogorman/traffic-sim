package system

import shared.map.MapApi
import upickle._
import autowire._

object ApiImplementation extends MapApi{
  override def test: String = "Takie zajebiste Ajaxy"
}

object MyServer extends autowire.Server[String, upickle.Reader, upickle.Writer]{
  def write[Result: Writer](r: Result) = upickle.write(r)
  def read[Result: Reader](p: String) = upickle.read[Result](p)

  val routes = MyServer.route[MyApi](MyApiImpl)
}
