package com.socure.socuretakehome

import scala.concurrent.duration._
import org.scalatra.test.scalatest._

class WelcomeServletTests extends ScalatraFunSuite {

  addServlet(classOf[WelcomeServlet], "/*")

  test("GET / on WelcomeServlet should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

  test("GET / otp/send/:email") {
    get("/otp/send/raj.psgit@gma") {
      status should equal (400)
    }

    get("/otp/send/raj.psgit@gmail.com") {
      status should equal (200)
    }
    get("/otp/send/raj.psgit@gmail.com") {
      status should equal (200)
    }
    get("/otp/send/raj.psgit@gmail.com") {
      status should equal (200)
    }
    get("/otp/send/raj.psgit@gmail.com") {
      status should equal (429)
    }
    Thread.sleep(5 * 60 * 1000)

    get("/otp/send/raj.psgit@gmail.com") {
      status should equal (200)
    }
  }

  test("GET / otp/send/:email/:otp") {


    get("/otp/send/raj.psgit@gmail.com") {
      status should equal (200)
    }

  }

}
