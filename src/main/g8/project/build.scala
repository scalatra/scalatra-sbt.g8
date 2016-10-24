import sbt._
import Keys._
import org.scalatra.sbt._
import org.scalatra.sbt.PluginKeys._
import com.earldouglas.xwp.JettyPlugin
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

object $name;format="Camel"$Build extends Build {
  val Organization = "$organization$"
  val Name = "$name$"
  val Version = "$version$"
  val ScalaVersion = "$scala_version$"
  val ScalatraVersion = "$scalatra_version$"

  lazy val commonDeps = Seq(
    "commons-lang" % "commons-lang" % "2.6",
    "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime"
  )

  lazy val commonSettings = Seq (
    organization := Organization,
    version := Version,
    scalaVersion := ScalaVersion,
    resolvers += Classpaths.typesafeReleases,
    resolvers += "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases",
    libraryDependencies ++= commonDeps
  )


  lazy val Common =
    Project(
      id = "common",
      base = file("common"))
      .settings(commonSettings: _*)

  lazy val DataTier =
    Project(
      id = "dataTier",
      base = file("dataTier"))
      .settings(commonSettings: _*)
      .dependsOn(
        Common
      )


  lazy val DomainTier =
    Project(
      id = "domainTier",
      base = file("domainTier"))
      .settings(commonSettings: _*)
      .dependsOn(
        DataTier,
        Common
      )

  lazy val webApp = Project (
    "$name;format="norm"$",
    file("."),
    settings = ScalatraPlugin.scalatraSettings ++ scalateSettings ++ Seq(
      name := Name,
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % ScalatraVersion,
        "org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
        "org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
        "org.eclipse.jetty" % "jetty-webapp" % "9.2.15.v20160210" % "container",
        "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided;compile",
        "org.scalatra" % "scalatra-metrics_2.11" % "2.4.1",
        "io.dropwizard.metrics" % "metrics-core" % "3.1.2",
        "io.dropwizard.metrics" % "metrics-servlet" % "3.1.2",
        "io.dropwizard.metrics" % "metrics-servlets" % "3.1.2",
        "nl.grons" % "metrics-scala_2.11" % "3.5.5"
      ),
      scalateTemplateConfig in Compile <<= (sourceDirectory in Compile){ base =>
          Seq(
            TemplateConfig(
              base / "webapp" / "WEB-INF" / "templates",
              Seq.empty,  /* default imports should be added here */
              Seq(
                Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
              ),  /* add extra bindings here */
              Some("templates")
            )
          )
        }
      )
    )
    .settings(commonSettings: _*)
    .enablePlugins(JettyPlugin)
    .aggregate(DomainTier, DataTier, Common)
}