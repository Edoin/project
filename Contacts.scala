package models.tables

import javax.inject.{ Singleton, Inject }
import scala.concurrent.Future
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.profile.RelationalProfile
import slick.driver.PostgresDriver.api._
import models.Contact

@Singleton
class Contacts @Inject()(
	val dbConfigProvider: DatabaseConfigProvider
) extends HasDatabaseConfigProvider[RelationalProfile]{

	val query = TableQuery[ContactsTable]

	def all: Future[Seq[Contact]] = db.run(query.result)

	def add(contact: Contact): Future[Int] = 
		db.run(query returning query.map(_.id) += contact)

	def update(contact: Contact): Future[Int] = 
		db.run(query.filter(_.id === contact.id).update(contact))

	def delete(id: Int): Future[Int] = 
		db.run(query.filter(_.id === id).delete)

	def findById(id: Int): Future[Option[Contact]] =
		db.run(query.filter(_.id === id).result.headOption)

	class ContactsTable(tag: Tag) extends Table[Contact](tag,"CONTACTS"){
		def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
		def name = column[String]("NAME", O.Length(50))
		def number = column[String]("NUMBER", O.Length(20))

		def * = (id.?, name, number) <> (Contact.tupled, Contact.unapply)
	}
}