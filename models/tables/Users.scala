package models.tables

import javax.inject._
import scala.concurrent.Future
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.profile.RelationalProfile
import slick.driver.PostgresDriver.api._
import models.User

@Singleton
class Users @Inject()(
	val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[RelationalProfile]{

	val query = TableQuery[UsersTable]

	def all: Future[Seq[User]] = db.run(query.result)

	def add(user: User): Future[Int] = 
		db.run(query returning query.map(_.id) += user)

	def update(user: User): Future[Int] = 
		db.run(query.filter(_.id === user.id).update(user))

	def delete(id: Int): Future[Int] = 
		db.run(query.filter(_.id === id).delete)

	def findById(id: Int): Future[Option[User]] =
		db.run(query.filter(_.id === id).result.headOption)

	def findByUsername(username: String): Future[Option[User]] =
		db.run(query.filter(_.username === username).result.headOption)

	class UsersTable(tag: Tag) extends Table[User](tag,"users"){
		def id 					= column[Int]("id", O.PrimaryKey, O.AutoInc)
		def fullname 		= column[String]("fullname")
		def birthdate 	= column[java.sql.Date]("birthdate")
		def email 			= column[String]("email")
		def number 			= column[String]("number")
		def address 		= column[String]("address")
		def nationality = column[String]("nationality")
		def username 		= column[String]("username")
		def password 		= column[String]("password")
		
		def * = (
			id.?, 
			fullname, 
			birthdate, 
			email, 
			number, 
			address, 
			nationality, 
			username, 
			password
		) <> (User.tupled, User.unapply)
	}
}