package com.example.db

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.Tag
import scala.slick.lifted.ProvenShape
import scala.util.Try
import scala.language.postfixOps

case class Address( id: Option[Int] = None,  street: String, city: String, 
  country: String, customer: Customer )

// The 'customers' relation table definition
class Addresses( tag: Tag ) extends Table[ Address ]( tag, "addresses" ) {
  def id = column[ Int ]( "id", O.PrimaryKey, O.AutoInc )

  def street = column[ String ]( "street", O.Length( 100, true ), O.NotNull )
  def city = column[ String ]( "city", O.Length( 50, true ), O.NotNull )
  def country = column[ String ]( "country", O.Length( 50, true ), O.NotNull )

  // Foreign key to 'customers' table
  def customerId = column[Int]( "customer_id", O.NotNull )
  def customer = foreignKey( "customer_fk", customerId, Customers )( _.id )

  // Converts from Customer domain instance to table model and vice-versa
  def * = ( id.?, street, city, country, customerId ).shaped <> ( 
    tuple => {
      Address.apply(
        id = tuple._1,
        street = tuple._2,
        city = tuple._3,
        country = tuple._4,
        customer = Customer( Some( tuple._5 ), "" )
      )
    }, {
      (address: Address) =>
        Some { (
          address.id,
          address.street,
          address.city,
          address.country,
          address.customer.id getOrElse 0 
        )
      }
    }
  )
}

object Addresses extends TableQuery[ Addresses ]( new Addresses( _ ) ) {
  def autoIncrement = this returning this.map( _.id )
 
  def create( implicit db: Database, address: Address ): Try[ Address ] = db.withSession { implicit session =>
    Try( this.autoIncrement.insert( address ) ) map { id => address.copy( id = Some( id ) ) }
  }
  
  def findAll( implicit db: Database, customer: Customer ): Seq[ Address ] = db.withSession { implicit session =>      
     ( for { address <- this if address.customerId === customer.id } yield address ).list map( _.copy( customer = customer ) ) 
  }
}
