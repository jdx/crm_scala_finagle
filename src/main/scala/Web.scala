import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}
import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Response}
import com.twitter.finagle.Service
import com.twitter.util.Future
import com.mongodb.casbah.Imports._
import java.net.InetSocketAddress
import util.Properties

object Web {
  def main(args: Array[String]) {
    val port = Properties.envOrElse("PORT", "8080").toInt
    println("Starting on port:"+port)
    ServerBuilder()
      .codec(Http())
      .name("hello-server")
      .bindTo(new InetSocketAddress(port))
      .build(new Hello)
      println("Started.")
  }
}

class Hello extends Service[HttpRequest, HttpResponse] {
  def apply(req: HttpRequest): Future[HttpResponse] = {
    val response = Response()
    val mongoClient =  MongoClient()
    val db = mongoClient("crm")
    val collection = db("customers")
    val customers = collection.find()
    val customersList = customers.map { case(c) => ("foo") }
    val customersJson = "[" + customers.mkString(",") + "]"
    response.setStatusCode(200)
    response.setContentString(customersJson)
    Future(response)
  }
}
