package com.socure.socuretakehome

import org.scalatra._
import org.slf4j.LoggerFactory
import otp.{OTPRequest, SimpleRNG, Utils}
import java.util.Date
import scala.collection.mutable
import scala.concurrent.duration._
import OTPRequest._

class WelcomeServlet() extends ScalatraServlet {

  import WelcomeServlet._
  val rnd = new scala.util.Random
  var rng: SimpleRNG = SimpleRNG(rnd.nextLong())
  val logger = LoggerFactory.getLogger(getClass)
  val maxAllowedAttempts: Int = MAX_NUMBER_OF_ATTEMPTS
  val otpExpiryThreshold: Int = OTP_EXPIRER_AFTER_MINUTES

  val map: mutable.Map[String, OTPRequest] = mutable.Map.empty

  get("/") {
    "Hello from Socure, " +
      "We are excited to take a step further in our journey..." +
      "GOOD LUCK!!!"
  }

  get("/otp/send/:email") {
    val email = params("email")
    val isValidEmail = Option(email).map(Utils.validateEmail).getOrElse(false)
    val currentTime = System.currentTimeMillis()
    if (isValidEmail) {
      val otpDB: Option[OTPRequest] = map.get(email)
      val otpRequest = otpDB.fold(default)(identity)
      val attempted = otpRequest.requestedAtMillis.filter(x => (currentTime - otpExpiryThreshold.minutes.toMillis) < x)
      if (attempted.size >= maxAllowedAttempts) {
        map += (email -> otpRequest.copy(requestedAtMillis = currentTime :: attempted))
        TooManyRequests(s"Too many requests. Please try again after $otpExpiryThreshold minutes")
      } else {
        val newOtp = otpRequest.copy(requestedAtMillis = currentTime :: attempted, otp = getOTPValue)
        map += (email -> newOtp)
        emailAndSendResponse(email, newOtp.otp)
      }
    } else {
      BadRequest("Please provide valid Email address")
    }
  }

  get("/otp/validate/:email/:otp") {
    val email = params("email")
    val otpInRequest = params("otp")
    val currentTime: Long = System.currentTimeMillis()
    val otpDetailsInDB: Option[OTPRequest] = map.get(email)
    otpDetailsInDB.fold(Unauthorized("OTP Verification Request for inValid Email")) { otpDb => getOTPResponse(currentTime, otpDb, otpInRequest) }
  }

  def getOTPValue: String = {
    val (number, r) = rng.nextInt
    rng = r
    f"${Math.abs(number)}%10.0f".replace(" ", "0")
  }

  def getOTPResponse(currentTimeInMillis: Long, otpDb: OTPRequest, otpInRequest: String): ActionResult = {
    if (!isOtpsMatching(otpDb, otpInRequest)) {
      BadRequest("OTP is not matching")
    } else if (!lastOtpRequestWithinExpiryThreshold(currentTimeInMillis, otpDb)) {
      Forbidden("OTP Expired")
    } else {
      Ok("OTP Matched")
    }
  }

  def emailAndSendResponse(email: String, otp: String): ActionResult = {
    logger.info(s"Sending otp = $otp to  Email=$email @ Time = ${new Date}")
    Ok("Please check you inbox for OTP Details")
  }
}

object WelcomeServlet {
  val MAX_NUMBER_OF_ATTEMPTS = 3
  val OTP_EXPIRER_AFTER_MINUTES = 5
}