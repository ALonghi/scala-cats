import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

name := "cats"

version := "0.1"

scalaVersion := "2.13.10"

val catsVersion = "2.7.0"

Seq(
  ScalariformKeys.autoformat := true,
  ScalariformKeys.preferences := ScalariformKeys
    .preferences
    .value
    .setPreference(DoubleIndentConstructorArguments, true)
    .setPreference(DoubleIndentMethodDeclaration, true)
    .setPreference(AlignSingleLineCaseStatements, true)
    .setPreference(AlignParameters, true)
    .setPreference(AlignArguments, true)
    .setPreference(PreserveSpaceBeforeArguments, true)
    .setPreference(FirstArgumentOnNewline, Force)
    .setPreference(DanglingCloseParenthesis, Force)
)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
)

scalacOptions ++= Seq(
  "-language:higherKinds"
)
