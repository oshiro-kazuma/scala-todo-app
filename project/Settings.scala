import sbt._
import Keys._
import sbt.Def.SettingList

object Settings {
  lazy val commons = new SettingList(Seq(

    scalaVersion := "2.12.6",

    // ビルド時のコンパイラオプション
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xfuture"
    ),

    // 依存ライブラリ
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.4" % Test
    )
  ))
}
