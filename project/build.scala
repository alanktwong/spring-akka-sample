import sbt._
import Keys._

/**
 * @see http://www.scala-sbt.org/release/docs/Examples/Full-Configuration-Example.html
 * @see https://github.com/scalatra/scalatra/tree/develop/project
 * @see https://github.com/dispatch/dispatch/tree/master/project
 */
object BuildSettings {
  val buildProject      = "spring-akka-travel"
  val buildOrganization = "org.springframework.samples"
  val buildVersion      = "1.0.0-BUILD-SNAPSHOT"
  val buildScalaVersion = "2.9.3"
  val buildJavaVersion   = "1.6"

  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization  := buildOrganization,
    version       := buildVersion,
    scalaVersion  := buildScalaVersion,
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    javacOptions  ++= Seq("-target", buildJavaVersion, "-source", buildJavaVersion),
    shellPrompt  := ShellPrompt.buildShellPrompt,
    licenses := Seq("LGPL v3" -> url("http://www.gnu.org/licenses/lgpl.txt"))
  )
}

// Shell prompt which show the current project,
// git branch and build version
object ShellPrompt {
  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) { }
    def buffer[T] (f: => T): T = f
  }
  def currBranch = (
    ("git status -sb" lines_! devnull headOption)
      getOrElse "-" stripPrefix "## "
  )

  val buildShellPrompt = {
    (state: State) => {
      val currProject = Project.extract (state).currentProject.id
      "%s:%s:%s> ".format (
        currProject, currBranch, BuildSettings.buildVersion
      )
    }
  }
}

object Resolvers {
  val sunrepo    = "Sun Maven2 Repo" at "http://download.java.net/maven/2"
  val sunrepoGF  = "Sun GF Maven2 Repo" at "http://download.java.net/maven/glassfish"
  val oraclerepo = "Oracle Maven2 Repo" at "http://download.oracle.com/maven"
  
  val springReleaseRepo           = "EBR Spring Release Repository" at "http://repository.springsource.com/maven/bundles/release"
  val springExternalReleaseRepo   = "EBR Spring External Release Repository" at "http://repository.springsource.com/maven/bundles/external"
  val springMilestoneRepo         = "Spring Milestone Repository" at "https://repo.springsource.org/libs-milestone"
  val rooReleaseRepo              = "Spring Roo Repository" at "http://spring-roo-repository.springsource.org/release"

  val jBossRepo = "JBoss Public Maven Repository Group" at "https://repository.jboss.org/nexus/content/groups/public-jboss/"

  val oracleResolvers = Seq(sunrepo, sunrepoGF, oraclerepo)
  val springAppResolvers = Seq(springReleaseRepo, springExternalReleaseRepo, rooReleaseRepo)
  val allResolvers = springAppResolvers ++ oracleResolvers ++ Seq(jBossRepo)
  
}

object Dependencies {
  import BuildSettings._
  
  val javaVer   = buildJavaVersion
  val provided  = "provided"
  val test      = "test"
  val runtime   = "runtime"
  val compile   = "compile"
  val container = "container"
  val containerCompile = container + ", " + compile

  val hsqldb   = "org.hsqldb"   % "hsqldb"   % "1.8.0.10" % runtime
  val junit    = "junit"        % "junit"    % "4.10"     % test
  val easymock = "org.easymock" % "easymock" % "2.5.2"    % test

  val scalatest  = "org.scalatest"    %% "scalatest"   % "1.9.1" % test
  val scalazFull = "org.scalaz"       %% "scalaz-full" % "6.0.4"
  val guava      = "com.google.guava" %  "guava"       % "12.0"

  val json4s =  "org.json4s" %% "json4s-native" % "3.2.4"

  val jodaTime     = "joda-time" % "joda-time"         % "1.6.2"
  val jodaTimeTags = "joda-time" % "joda-time-jsptags" % "1.1.1" % runtime
  val xerces       = "xerces"    % "xercesImpl"        % "2.9.1" % runtime
  
  val aspectjVer  = "1.6.10"
  val aspectj     = "org.aspectj" % "aspectjrt" % aspectjVer

  val hibernateDependencies = Seq(
      "org.hibernate" % "hibernate-entitymanager" % "3.6.0.Final",
      "org.hibernate" % "hibernate-validator" % "4.1.0.Final"
      )
  
  val tiles = "org.apache.tiles" % "tiles-jsp" % "2.2.2" exclude("commons-logging", "commons-logging-api")
  val cglib = "cglib" % "cglib-nodep" % "2.2"
  
  val akkaOrg = "com.typesafe.akka"
  val akkaVer = "2.0.3"
  val akkaDependencies = Seq(
      akkaOrg % "akka-actor" % akkaVer
      )

  val springOrg         = "org.springframework"
  val springVer         = "3.0.5.RELEASE"
  val springSecurityVer = "3.0.8.RELEASE"
  val springWebflowVer  = "2.2.1.RELEASE"
  val springCoreDependencies = Seq(
      springOrg % "org.springframework.context"     % springVer exclude("commons-logging", "commons-logging-api"),
      springOrg % "org.springframework.jdbc"        % springVer,
      springOrg % "org.springframework.transaction" % springVer,
      springOrg % "org.springframework.orm"         % springVer,
      springOrg % "org.springframework.web"         % springVer,
      springOrg % "org.springframework.web.servlet" % springVer,
      springOrg % "org.springframework.test"        % springVer % test
      )
  val springSecurityDependencies = Seq(
      "org.springframework.security" % "org.springframework.security.core"    % springSecurityVer,
      "org.springframework.security" % "org.springframework.security.web"     % springSecurityVer exclude("commons-logging", "commons-logging-api"),
      "org.springframework.security" % "org.springframework.security.config"  % springSecurityVer exclude("commons-logging", "commons-logging-api"),
      "org.springframework.security" % "org.springframework.security.taglibs" % springSecurityVer
      )
  val springWebflowDependencies = Seq(
      "org.springframework.webflow"  % "org.springframework.webflow" % springWebflowVer,
      "org.springframework.webflow"  % "org.springframework.js"      % springWebflowVer
      )
  val springScala      = "org.springframework.scala" % "spring-scala" % "1.0.0.M1"
  val springRoo        = "org.springframework.roo"   % "org.springframework.roo.annotations" % "1.1.0.RELEASE"
  val springDependencies = springCoreDependencies ++
                           springSecurityDependencies ++
                           springWebflowDependencies ++
                           Seq(springRoo, springScala)
  

  val commonsLogging = "commons-logging" % "commons-logging" % "1.1.1" % runtime
  val slf4jVer          = "1.6.1"
  val loggingDependencies = Seq(
      "log4j" % "log4j" % "1.2.16" excludeAll(
        ExclusionRule(organization = "com.sun.jdmk"),
        ExclusionRule(organization = "com.sun.jmx"),
        ExclusionRule(organization = "javax.mail"),
        ExclusionRule(organization = "javax.jms")
      ),
      "org.slf4j" % "slf4j-api"      % slf4jVer % runtime,
      "org.slf4j" % "jcl-over-slf4j" % slf4jVer % runtime,
      "org.slf4j" % "slf4j-log4j12"  % slf4jVer % runtime
      )
  
  val testDependencies   = Seq(scalatest, junit, easymock)
  
  val j2eeDependencies = Seq(
      "javax.inject"      % "javax.inject"    % "1",
      "javax.servlet"     % "servlet-api"     % "2.5"       % provided,
      "javax.servlet"     % "jstl"            % "1.2"       % provided,
      "javax.validation"  % "validation-api"  % "1.0.0.GA",
      "javax.servlet.jsp" % "jsp-api"         % "2.1"       % provided
      )
  val jettyVer            = "8.0.0.M3"
  val jettyDependencies = Seq(
      "org.mortbay.jetty" % "servlet-api"  % "3.0.20100224" % provided,
      "org.eclipse.jetty" % "jetty-server" % jettyVer       % containerCompile,
      "org.eclipse.jetty" % "jetty-util"   % jettyVer       % containerCompile,
      "org.eclipse.jetty" % "jetty-webapp" % jettyVer       % containerCompile
      )
  val utilityDependencies = Seq(xerces, aspectj, jodaTime, jodaTimeTags, hsqldb, tiles, cglib)
  
  val allDependencies    = testDependencies ++
                           utilityDependencies ++
                           loggingDependencies ++
                           jettyDependencies ++
                           j2eeDependencies ++
                           hibernateDependencies ++
                           springDependencies ++
                           akkaDependencies
}
/**
 *  sbt
 *     clean -> clean the build
 *     compile -> compiles classes
 *     eclipsify -> create eclipse files from sbt
 *     package -> make a war
 *     container:start -> deploy to jetty
 */
object ProjectBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  // Sub-project specific dependencies
  lazy val all = Project (
    id = buildProject,
    base = file ("."),
    settings = buildSettings  ++ com.earldouglas.xsbtwebplugin.WebPlugin.webSettings ++ Seq(
      resolvers ++= allResolvers,
      libraryDependencies ++= allDependencies,
      description := "Wraps up all the modules"
    )
  )
}