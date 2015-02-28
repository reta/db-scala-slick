package com.example.db

import scala.language.postfixOps
import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.StaticQuery.interpolation
import scala.slick.jdbc.GetResult

object DbApp extends App {
  implicit lazy val DB = Database.forURL( "jdbc:h2:mem:test", driver = "org.h2.Driver" )
  
  DB withSession { implicit session =>
    ( Customers.ddl ++ Addresses.ddl ).create
        
    Customers.create( Customer( None, "tom@b.com",  Some( "Tom" ), Some( "Tommyknocker" ) ) )
    Customers.create( Customer( None, "bob@b.com",  Some( "Bob" ), Some( "Bobbyknocker" ) ) )
    
    val customers = Customers.findAll
    val customer = Customers.findByEmail( "bob@b.com" )
    
    println( customers )
    println( customer )
    
    Customers.findByEmail( "bob@b.com" ) map { customer =>
      Customers.update( customer.copy( firstName = Some( "Tommy" ) ) )
      Customers.remove( customer )
    }
  }
}