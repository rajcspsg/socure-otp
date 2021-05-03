package otp

import scala.concurrent.duration._
import java.time.temporal.ChronoUnit

case class OTPRequest(requestedAtMillis: List[Long], otp: String, usedOtps: Set[String] = Set.empty)

object OTPRequest {
  val default = OTPRequest(List(), "")
 // def apply(requestedAtMillis: Long, otp: String) = OTPRequest( List(requestedAtMillis), otp)

  def isOtpsMatching(otpDb: OTPRequest, otpInRequest: String) : Boolean = otpDb.otp == otpInRequest

  def numberOfRequestsWithinThreshold(currentTimeInMillis: Long, otpDb: OTPRequest)(implicit maxAllowedAttempts: Int = 3, otpExpiryThreshold: Int = 5): Boolean = {
    otpDb.requestedAtMillis.filter(x => (x - otpExpiryThreshold.minutes.toMillis) < currentTimeInMillis ).size < maxAllowedAttempts
  }

  def lastOtpRequestWithinExpiryThreshold(currentTimeInMillis: Long, otpDb: OTPRequest) (implicit otpExpiryThreshold: Int = 5): Boolean = {
    (otpDb.requestedAtMillis.max - otpExpiryThreshold.minutes.toMillis) < currentTimeInMillis
  }
}
