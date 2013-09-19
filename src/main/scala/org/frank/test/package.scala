package org.frank


package object test {

  def waitUntil(steps: Int = 10, waitInMs: Int = 10000, condition: => Boolean) {
    var i = 1
    do {
      Thread.sleep(waitInMs)
      println("wait for " + i * 10 + " seconds")
      i = i + 1
    } while (condition && i <= steps)
  }
}
