package com.example.db

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.Tag
import scala.slick.lifted.ProvenShape
import scala.util.Try
import scala.language.postfixOps

case class Customer( id: Option[Int] = None, email: String, 
  firstName: Option[ String ] = None, lastName: Option[ String ] = None)

// The 'customers' relation table definition
class Customers( tag: Tag ) extends Table[ Customer ]( tag, "customers" ) {
  def id = column[ Int ]( "id", O.PrimaryKey, O.AutoInc )

  def email = column[ String ]( "email", O.Length( 512, true ), O.NotNull )
  def firstName = column[ String ]( "first_name", O.Length( 256, true ), O.Nullable )
  def lastName = column[ String ]( "last_name", O.Length( 256, true ), O.Nullable )

  // Unique index for customer's email
  def emailIndex = index( "idx_email", email, unique = true )

  // Converts from Customer domain instance to table model and vice-versa
  def * = ( id.?, email, firstName.?, lastName.? ).shaped <> ( Customer.tupled, Customer.unapply )
}

object Customers extends TableQuery[ Customers ]( new Customers( _ ) ) {
  def autoIncrement = this returning this.map( _.id )

  def findByEmail( email: String )( implicit db: Database ) : Option[ Customer ] = db.withSession { implicit session =>
    ( for { customer <- this if ( customer.email === email.toLowerCase ) } yield customer ) firstOption
  }
   
  def findAll( implicit db: Database ): Seq[ Customer ] = db.withSession { implicit session =>      
     ( for { customer <- this } yield customer ) list
  }
   
  def update( customer: Customer )( implicit db: Database ): Boolean = db.withSession { implicit session =>
    val query = for { c <- this if ( c.id === customer.id ) } yield (c.email, c.firstName.?, c.lastName.?)
    query.update(customer.email, customer.firstName, customer.lastName) > 0
  }
 
  def create( customer: Customer )( implicit db: Database ): Customer = db.withSession { implicit session =>
    val id = this.autoIncrement.insert( customer )
    customer.copy( id = Some( id ) )
  }   

  def remove( customer: Customer )( implicit db: Database ) : Boolean = db.withSession { implicit session =>
    ( for { c <- this if ( c.id === customer.id ) } yield c ).delete > 0
  }
}
