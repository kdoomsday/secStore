package controllers

import javax.inject._
import java.nio.file.{ Files, Paths }
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import play.api.mvc._
import com.roundeights.hasher.Implicits._
import dao.FileStore

case class FormData(name: String)

/** This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (store: FileStore)(implicit val messagesApi: MessagesApi)
    extends Controller with i18n.I18nSupport {

  val form = Form(
    mapping(
      "archivo" → text
    )(FormData.apply)(FormData.unapply)
  )

  /** Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action { implicit request ⇒
    Ok(views.html.index(form, store.archivos()))
  }

  def upload = Action(parse.multipartFormData) { request ⇒
    request.body.file("archivo").map { archivo ⇒
      val name = archivo.filename
      val bytes = Files.readAllBytes(archivo.ref.file.toPath)
      val hash = bytes.sha256.hex
      store.store(name, bytes, hash)

      Redirect(routes.HomeController.index).flashing("message" → "Uploaded")
    }.getOrElse {
      Redirect(routes.HomeController.index).flashing("message" → "No file")
    }
  }
}
