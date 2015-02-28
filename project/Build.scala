import sbt._
import Keys._
import com.typesafe.sbteclipse.core.EclipsePlugin._

object ProjectBuild extends Build {
  override val settings = super.settings ++ Seq(
    organization := "com.example",    
    name := "db",    
    version := "0.0.1-SNAPSHOT",    
    
    scalaVersion := "2.11.4",
    scalacOptions ++= Seq( "-encoding", "UTF-8", "-target:jvm-1.8", "-feature", "-deprecation" ),    
    javacOptions ++= Seq( "-encoding", "UTF-8", "-source", "1.8", "-target", "1.8" ),        
    outputStrategy := Some( StdoutOutput ),
    compileOrder := CompileOrder.JavaThenScala,
    
    resolvers ++= Seq( 
      Resolver.mavenLocal, 
      Resolver.sonatypeRepo( "releases" ), 
      Resolver.typesafeRepo( "releases" )
    ),        
    
    libraryDependencies ++= Seq(
      "com.h2database" % "h2" % "1.4.185",
      "com.typesafe.slick" %% "slick" % "2.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.2"
    ),

    crossPaths := false,
    fork in run := true,
    connectInput in run := true,
    
    EclipseKeys.executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE18)
  )
}
