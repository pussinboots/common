package org.frank.test

import co.freeside.betamax._
import co.freeside.betamax.proxy.jetty.ProxyServer
import org.scalatest._
import java.util.Comparator
import co.freeside.betamax.message.Request
import org.apache.http.client.utils.URIBuilder
import java.util

trait BetamaxSuite {

  protected def test(testName: String, testTags: Tag*)(testFun: => Unit)

  def testWithBetamax(tape: String, mode: Option[TapeMode] = None)(testName: String, testTags: Tag*)(testFun: => Unit) = {
    test(testName, testTags: _*) {
      Betamax.withBetamax(tape, mode) {
        testFun
      }
    }
  }
}

trait BetamaxSpec {

  def withBetamax(tape: String, mode: Option[TapeMode] = None)(testFun: => Unit) = {
    Betamax.withBetamax(tape, mode)(testFun)
  }
}

object Betamax {

  def withBetamax(tape: String, mode: Option[TapeMode] = None)(testFun: => Unit) = {
    val recorder = new Recorder
    val proxyServer = new ProxyServer(recorder)
    val uriTrimMatchers = new OrMatcher(Seq(new UriQueryTrimMatcher("now.notebooksbilliger.de", "/999661753100028"), new UriQueryTrimMatcher("googleads.g.doubleclick.net", "/pagead/viewthroughconversion/"),
      new UriQueryTrimMatcher("www.google-analytics.com", "/__utm.gif"), new UriQueryTrimMatcher("dev.visualwebsiteoptimizer.com", "/")))
    val matcher: Map[Any, Any] = Map[Any, Any]("match" -> new util.ArrayList(util.Arrays.asList(MatchRule.method, uriTrimMatchers)))
    import scala.collection.JavaConversions
    recorder.insertTape(tape, JavaConversions.mapAsJavaMap(matcher))
    recorder.getTape.setMode(mode.getOrElse(recorder.getDefaultMode()))
    proxyServer.start()
    try {
      testFun
    } finally {
      recorder.ejectTape()
      proxyServer.stop()
    }
  }
}

class OrMatcher(matchers: Seq[Comparator[Request]]) extends Comparator[Request] {
  def compare(a: Request, b: Request): Int = {
    val matchedMatcher = matchers.find(_.compare(a, b) == 0)
    matchedMatcher.getOrElse(matchers(0)).compare(a, b)
  }
}

class UriQueryTrimMatcher(host: String, path: String) extends Comparator[Request] {
  def compare(a: Request, b: Request): Int = {
    val uri = new URIBuilder(a.getUri)
    val urib = new URIBuilder(b.getUri)
    if (a.getUri.getHost == host && a.getUri.getPath().startsWith(path)) {
      uri.setQuery("")
    }

    if (b.getUri.getHost == host && b.getUri.getPath().startsWith(path)) {
      urib.setQuery("")
    }
    uri.build().compareTo(urib.build())
  }
}
