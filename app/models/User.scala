package models

case class User(
	optId: Option[Int],
	fullname: String,
	birthdate: java.sql.Date,	
	email: String,
	number: String,
	address: String,
	nationality: String,
	username: String,
	password: String
) {
	val id = optId.getOrElse(0)
}