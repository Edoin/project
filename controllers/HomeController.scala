package controllers

import javax.inject._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api._
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }

import ejisan.play.libs.{ PageMetaSupport, PageMetaApi }
import models.User
import models.tables.Users
/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (
  val messagesApi: MessagesApi,
  val pageMetaApi: PageMetaApi,
  implicit val wja: WebJarAssets,
  val users: Users
) extends Controller with I18nSupport with PageMetaSupport {

  /**
   * Create an Action to render an HTML page.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  
  val loginForm = Form (
    tuple(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
  )

  val signupForm = Form (
    mapping(
      "id"          -> optional(number),  
      "fullname" 		-> nonEmptyText,
      "birthdate"		-> sqlDate("yyyy-MM-dd"),
      "email" 			-> nonEmptyText,
      "number" 			-> nonEmptyText,
      "address"			-> nonEmptyText,
      "nationality" -> nonEmptyText,
      "username" 		-> nonEmptyText,
      "password" 		-> nonEmptyText
    )(User.apply)(User.unapply)
  )

  def index = Action { implicit request =>
    Ok(views.html.index())
  }

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }
  def signup = Action { implicit request =>
    Ok(views.html.signup(signupForm))
  }

  def welcome = Action.async { implicit request =>
    users.all.map(user => 
      Ok(views.html.welcome(user))
    )
  }

  def dash = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => { BadRequest(views.html.login(formWithErrors)) },
      data => { Ok(views.html.dash(data)) }
    )
  }

  def signupAction = Action.async { implicit request =>
    signupForm.bindFromRequest.fold(
      formWithErrors => { Future.successful(
        BadRequest(views.html.signup(formWithErrors))) },
      data => { 
        users.add(data) map ( d =>
          Redirect(routes.HomeController.login).flashing("success" -> "Successfuly signed up. You can now Login using your account")
        )
      }
    )
  }

  // def loginAction = Action.async { implicit request =>
  //   loginForm.bindFromRequest.fold(
  //     formWithErrors => { Future.successful(BadRequest(views.html.login(formWithErrors))) },
  //     data => { users.findByUsername(data._1) map{
  //       case Some(user) => Redirect(routes.ProfileController.index(user))
  //       case None => NotFound  
  //     }}
  //   )
  // }

    // def edit(id: Int) = Action.async { implicit request =>
    // contacts.findById(id).map {
    //   case Some(contact) =>  Ok(views.html.update(signupForm.fill(contact)))
    //   case None => NotFound
    // }


}
