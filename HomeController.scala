package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }

import models.{Contact, ContactFormData}

import ejisan.play.libs.{ PageMetaSupport, PageMetaApi }

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
  val messagesApi: MessagesApi,
  val pageMetaApi: PageMetaApi,
  implicit val wja: WebJarAssets
) extends Controller with I18nSupport with PageMetaSupport {

  /**
   * Create an Action to render an HTML page.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */

  val registrationForm = Form(
    tuple(
      "firstname" -> nonEmptyText,
      "lastname"  -> nonEmptyText,
      "username"  -> nonEmptyText,
      "password"  -> nonEmptyText,
      "age"    -> optional(number)
    )
  )

  val loginForm = Form (
    tuple(
      "username" ->   nonEmptyText,
      "password" ->   nonEmptyText
    )
  )

  val contactForm = Form (
    mapping(
      "name"    -> nonEmptyText,
      "number"  -> nonEmptyText
    )(ContactFormData.apply)(ContactFormData.unapply _)
  )

  def index = Action { implicit request =>
    Ok(views.html.index(registrationForm))
  }

  def login = Action{ implicit request =>
    Ok(views.html.login(loginForm))
  }

  def contact = Action{ implicit request =>
    Ok(views.html.contact(contactForm))
  }

  def loginAction = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => { BadRequest(views.html.login(formWithErrors)) },
      data => Redirect(routes.HomeController.dashboard).withSession("name" -> "test")
    )
  }

  def registration = Action { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => { 
        BadRequest(views.html.index(formWithErrors)) },
      data => {
        Ok(views.html.registration(data))
      }
    )
  }

  def dashboard = Action { implicit request =>
    val name = request.session.get("name")
    println(name)
    Ok(views.html.dashboard(name))
  }

}
