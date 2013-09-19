package com.typesafe.webwords.indexer.mongo

import de.flapdoodle.embedmongo._
import de.flapdoodle.embedmongo.config.{MongodProcessOutputConfig, RuntimeConfig, MongodConfig}
import distribution.Version
import org.scalatest.BeforeAndAfter
import java.util.logging.{Level, Logger}
import de.flapdoodle.embedmongo.io.Processors

trait EmbedConnection {

  //Override this method to personalize testing port
  def embedConnectionPort(): Int = { 27017 }

  //Override this method to personalize MongoDB version
  def embedMongoDBVersion(): Version = { Version.V2_2_0_RC0 }
  val logger = Logger.getLogger(getClass.getName)
  val runtimeConfig = new RuntimeConfig()
  runtimeConfig.setMongodOutputConfig(new MongodProcessOutputConfig(Processors.logTo(logger, Level.FINE),
	Processors.logTo(logger, Level.FINE), Processors.named("[console>]",Processors.logTo(logger, Level.FINE))))
  lazy val runtime: MongoDBRuntime = MongoDBRuntime.getInstance(runtimeConfig)
  lazy val mongodExe: MongodExecutable = runtime.prepare(new MongodConfig(embedMongoDBVersion(), embedConnectionPort(), true))
  lazy val mongod: MongodProcess = mongodExe.start()

  def start() {
    mongod
  }

  def stop() {
    mongod.stop()
    mongodExe.cleanup()
  }
}
