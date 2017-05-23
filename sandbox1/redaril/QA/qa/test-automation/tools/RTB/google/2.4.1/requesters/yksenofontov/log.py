#!/usr/bin/env python
# Copyright 2009 Google Inc. All Rights Reserved.
"""Contains classes for logging Real Time Bidder requests and responses."""

import base64
import cgi
import datetime
import httplib
import random
import re
import sys
import threading
import urllib
import urlparse

import google.protobuf.message
import realtime_bidding_pb2


class Record(object):
  """A record of each request/response pair."""
  def __init__(self, bid_request, status_code, payload):
    self.bid_request = bid_request
    self.status = status_code
    self.payload = payload

    # The following fields get filled in by the LogSummarizer after the
    # response protocol buffer has been succesfully parsed.

    # A list of strings with problem descriptions.
    self.problems = []
    # A realtime_bidding_pb2.BidResponse instance.
    self.bid_response = None
    # A map of ad index -> validated HTML snippet (after macro substitutions).
    self.html_snippets = {}


class LoggerException(Exception):
  """An exception thrown for invalid uses of a Logger."""
  pass


class Logger(object):
  """A class that keeps a log of all requests and responses."""

  def __iter__(self):
    if not self._done:
      raise LoggerException("Only locked Loggers are iterable.")
    return self

  def __getitem__(self, item):
    if not self._done:
      raise LoggerException("Only locked Loggers are iterable.")
    return self._records[item]

  def next(self):
    if not self._done:
      raise LoggerException("Only locked Loggers can are iterable.")
    if self._current_iteration >= len(self._records):
      self._current_iteration = 0
      raise StopIteration

    c = self._current_iteration
    self._current_iteration += 1
    return self._records[c]

  def __init__(self):
    # Stores Record objects.
    self._records = []
    self._record_lock = threading.Lock()
    self._current_iteration = 0
    self._done = False

  def Done(self):
    """Signals that logging is done, locking this object to modifications."""
    self._record_lock.acquire()
    try:
      self._done = True
    finally:
      self._record_lock.release()

  def IsDone(self):
    """Returns True if this logger has been locked for updates."""
    self._record_lock.acquire()
    try:
      return self._done
    finally:
      self._record_lock.release()

  def LogSynchronousRequest(self, bid_request, status_code, payload):
    """Logs a synchronous request.

    Args:
      bid_request: A realtime_bidding_pb2.BidRequest object.
      status_code: The HTTP status code.
      payload: The HTTP response payload.

    Returns:
      True if the request was logged, False otherwise.
    """
    if self.IsDone():
      return False

    self._record_lock.acquire()
    try:

      record = Record(bid_request, status_code, payload)
      self._records.append(record)
    finally:
      self._record_lock.release()

    return True


def EscapeUrl(input_str):
  """Returned the URL-escaped version of input_str.

  The input is URL-escaped as documented at:
  https://sites.google.com/a/google.com/adx-rtb/technical-documentation/response

  Args:
    input_str: A string to URL-escape.

  Returns:
    The URL-escaped version of input_str.
  """
  return urllib.quote_plus(input_str, "!()*,-./:_~")


class LogSummarizer(object):
  """Summarizes information stored in a Logger and outputs a report."""

  REQUEST_ERROR_MESSAGES = {
    "not-ok": "The HTTP response code was not 200/OK.",
  }

  RESPONSE_ERROR_MESSAGES = {
      "empty": "Response is empty (0 bytes).",
      "parse-error": "Response could not be parsed.",
      "uninitialized": "Response did not contain all required fields.",
      "no-processing-time": "Response contains no processing time information.",
      "ads-in-ping": "Response for ping message contains ads.",
  }

  AD_ERROR_TEMPLATE = "Ad %d: %s"
  AD_ERROR_MESSAGES = {
      "video-and-snippet": "video_url and html_snippet are both set.",
      "invalid-video-url": "invalid video_url.",
      "empty-snippet": "snippet is empty.",
      "no-adslots": "ad does not target any adslots.",
      "no-click-through-urls": "ad does not contain any click-through urls.",
      "invalid-url": "invalid click-through URL: ",
  }

  SNIPPET_ERROR_TEMPLATE = "HTML snippet for ad %d: %s"
  SNIPPET_ERROR_MESSAGES = {
      "click-url-missing": "click url macros missing (CLICK_URL_UNESC or"
          " CLICK_URL_ESC)",

  }
  ADSLOT_ERROR_TEMPLATE = "Ad %d, adslot %d: %s"
  ADSLOT_ERROR_MESSAGES = {
      "zero-bid": "0 max CPM bid.",
      "zero-min-cpm": "0 min CPM bid",
      "min-more-than-max": "min CPM >= max CPM",
      "invalid-slot-id": "ad slot id is not present in the BidRequest."
  }

  CLICK_URL_UNESC = "http://www.google.com/url?sa=D&q="
  CLICK_URL_ESC = EscapeUrl(CLICK_URL_UNESC)
  CLICK_URL_UNESC_RE = re.compile("%%CLICK_URL_UNESC(:(.*?))?%%")
  CLICK_URL_ESC_RE = re.compile("%%CLICK_URL_ESC(:(.*?))?%%")
  CACHEBUSTER_RE = re.compile("%%CACHEBUSTER(:(.*?))?%%")
  SITE_RE = re.compile("%%SITE(:(.*?))?%%")
  WINNING_PRICE_RE = re.compile("%%WINNING_PRICE(:(.*?))?%%")
  WINNING_PRICE_ESC_RE = re.compile("%%WINNING_PRICE_ESC(:(.*?))?%%")

  WINNING_PRICE_RATIO = 0.33

  def __init__(self, logger):
    """Initializes a LogSummarizer.

    Args:
      logger: An iterable object containing Record instances.
    """
    self._logger = logger
    self._requests_sent = 0
    self._responses_ok = 0
    self._responses_successful_without_bids = 0
    self._processing_time_sum = 0
    self._processing_time_count = 0
    self._encrypted_price = None

    # Store records in the following buckets:
    # Good: the response can be parsed and no errors were detected.
    self._good = []
    # Problematic: the response can be parsed but has some problems.
    self._problematic = []
    # Invalid: the response can not be parsed.
    self._invalid = []
    # Error: the HTTP response had a non-200 response code.
    self._error = []

  def SetSampleEncryptedPrice(self, encrypted_price):
    """Sets the encrypted price to use for the ENCRYPTED_PRICE macro.

    Args:
      encrypted_price: A string with the encrypted price to return.
    """
    self._encrypted_price = encrypted_price

  def Summarize(self):
    """Collects and summarizes information from the logger."""
    for record in self._logger:
      self._requests_sent += 1
      if record.status == httplib.OK:
        self._responses_ok += 1
      else:
        # Responded with a non-OK code, don't to parse.
        record.problems.append(self.REQUEST_ERROR_MESSAGES["not-ok"])
        self._error.append(record)
        continue

      if len(record.payload) == 0:
        record.problems.append(self.RESPONSE_ERROR_MESSAGES["empty"])
        self._invalid.append(record)
        # Empty response, don't try to parse.
        continue

      bid_response = realtime_bidding_pb2.BidResponse()
      try:
        bid_response.ParseFromString(record.payload)
      except google.protobuf.message.DecodeError:
        record.problems.append(self.RESPONSE_ERROR_MESSAGES["parse-error"])
        self._invalid.append(record)
        # Unparseable response, don't check its validity.
        continue

      if not bid_response.IsInitialized():
        # It parsed but the message is not initialized which means it's not
        # well-formed, consider this unparseable.
        record.problems.append(self.RESPONSE_ERROR_MESSAGES["uninitialized"])
        self._invalid.append(record)
        continue

      record.bid_response = bid_response

      if not bid_response.HasField("processing_time_ms"):
        record.problems.append(
            self.RESPONSE_ERROR_MESSAGES["no-processing-time"])
      else:
        self._processing_time_count += 1
        self._processing_time_sum += bid_response.processing_time_ms

      if record.bid_request.is_ping:
        self.ValidatePing(record)
      else:
        if not bid_response.ad:
          self._responses_successful_without_bids += 1
          self._good.append(record)
          continue
          # No ads returned, don't validate ads.

        for i, ad in enumerate(bid_response.ad):
          self.ValidateAd(ad, i, record)

      if record.problems:
        self._problematic.append(record)
      else:
        self._good.append(record)

  def ValidatePing(self, record):
    """Validates a response for a ping request.

    Args:
      record: A Record instance containing the context for this validation.
    """
    bid_response = record.bid_response
    if bid_response.ad:
      record.problems.append(self.RESPONSE_ERROR_MESSAGES["ads-in-ping"])

  def ValidateAd(self, ad, ad_index, record):
    """Validates a returned ad.

    Args:
      ad: A realtime_bidding_pb2.BidResponse_Ad instance.
      ad_index: The index of the ad in the BidResponse, used to make error
          messages more informative.
      record: A Record instance containing the context for this validation.
    """
    if ad.HasField("html_snippet") and ad.HasField("video_url"):
      record.problems.append(self.AD_ERROR_TEMPLATE % (
          ad_index, self.AD_ERROR_MESSAGES["video-and-snippet"]))

    if ad.HasField("video_url"):
      parsed_url = urlparse.urlparse(ad.video_url)
      if not (parsed_url[0] and
              (parsed_url[0] == "http" or parsed_url[0] == "https") and
              parsed_url[1]):
        record.problems.append(self.AD_ERROR_TEMPLATE % (
            ad_index, self.AD_ERROR_MESSAGES["invalid-video-url"]))
    elif not ad.HasField("html_snippet") or not ad.html_snippet:
      record.problems.append(self.AD_ERROR_TEMPLATE % (
          ad_index, self.AD_ERROR_MESSAGES["empty-snippet"]))

    if not ad.adslot:
      record.problems.append(self.AD_ERROR_TEMPLATE % (
          ad_index, self.AD_ERROR_MESSAGES["no-adslots"]))
      self._responses_successful_without_bids += 1

    if not ad.click_through_url:
      record.problems.append(self.AD_ERROR_TEMPLATE % (
          ad_index, self.AD_ERROR_MESSAGES["no-click-through-urls"]))
    for click_through_url in ad.click_through_url:
      parsed_url = urlparse.urlparse(click_through_url)
      # Must have scheme and netloc.
      if not (parsed_url[0]
              and (parsed_url[0] == "http" or parsed_url[0] == "https")
              and parsed_url[1]):
        record.problems.append((self.AD_ERROR_TEMPLATE % (
            ad_index, self.AD_ERROR_MESSAGES["invalid-url"])) +
            click_through_url)

    adslot_problems = False
    for adslot_index, adslot in enumerate(ad.adslot):
      adslot_problems = (adslot_problems or
                         self.ValidateAdSlot(adslot, ad_index, adslot_index,
                                             record))
    # Only validate snippets if all adslots are valid and there's a snippet.
    if ad.HasField("html_snippet") and ad.html_snippet and not adslot_problems:
      self.ValidateHtmlSnippet(ad, ad_index, record)

  def ValidateHtmlSnippet(self, ad, ad_index, record):
    """Validates a returned HTML snippet, including macro substitution.

    The winning price macro is substituted with an unencrypted value for the
    purpose of initial testing.

    This method assumes that all adslots in the ad are valid and that the ad
    has an HTML snippet

    Args:
      ad: A realtime_bidding_pb2.BidResponse_Ad instance.
      ad_index: The index of the ad in the BidResponse, used to make error
          messages more informative.
      record: A Record instance containing the context for this validation.
    """
    if not ad.adslot:
      return

    # Check that one of the required click url macros is present.
    if not (re.search(self.CLICK_URL_ESC_RE, ad.html_snippet) or
            re.search(self.CLICK_URL_UNESC_RE, ad.html_snippet)):
      record.problems.append(self.SNIPPET_ERROR_TEMPLATE % (
          ad_index, self.SNIPPET_ERROR_MESSAGES['click-url-missing']))

    adslot_id = ad.adslot[0].id

    width, height = 0, 0
    for adslot in record.bid_request.adslot:
      if adslot.id == adslot_id:
        width = adslot.width[0]
        height = adslot.height[0]

    if not height or not width:
      # Could not find the corresponding request adslot, invalid response.
      return

    # Substitute click string macros.
    html_snippet = re.sub(self.CLICK_URL_UNESC_RE,
                          self.CLICK_URL_UNESC,
                          ad.html_snippet)
    html_snippet = re.sub(self.CLICK_URL_ESC_RE,
                          self.CLICK_URL_ESC,
                          html_snippet)

    # Winning price notification is in CPI, not CPM.
    winning_price = (ad.adslot[0].max_cpm_micros * self.WINNING_PRICE_RATIO
                     / 1000)

    # Substitute winning price.
    if (self._encrypted_price):
      html_snippet = re.sub(self.WINNING_PRICE_RE,
                            self._encrypted_price,
                            html_snippet)
      html_snippet = re.sub(self.WINNING_PRICE_ESC_RE,
                            EscapeUrl(self._encrypted_price),
                            html_snippet)
    else:
      html_snippet = re.sub(self.WINNING_PRICE_RE,
                            str(winning_price),
                            html_snippet)
      html_snippet = re.sub(self.WINNING_PRICE_ESC_RE,
                            str(winning_price),
                            html_snippet)

    # Substitute cache buster macro.
    random.seed()
    cachebuster = random.randint(0, sys.maxint)
    html_snippet = re.sub(self.CACHEBUSTER_RE,
                          str(cachebuster),
                          html_snippet)

    # Substitute the site.
    parsed_url = urlparse.urlparse(record.bid_request.url)
    netloc = parsed_url[1]
    if netloc:
      domain = netloc
      if ":" in netloc:
        domain = netloc[0:netloc.rfind(":")]
      html_snippet = re.sub(self.SITE_RE,
                            EscapeUrl(domain),
                            html_snippet)

    record.html_snippets[ad_index] = html_snippet

  def FindAdSlotInRequest(self, adslot_id, bid_request):
    """Returns the adslot with the given id from the bid request.

    Args:
      adslot_id: The id of an adslot.
      bid_request: A realtime_bidding_pb2.BidRequest instance.

    Returns:
      A realtime_bidding_pb2.BidRequest_AdSlot or None if a matching adslot
      could not be found.
    """
    for request_adslot in bid_request.adslot:
      if request_adslot.id == adslot_id:
        return request_adslot
    return None

  def ValidateAdSlot(self, adslot, ad_index, adslot_index, record):
    """Validates a returned ad slot.

    Args:
      adslot: A realtime_bidding_pb2.BidResponse_Ad_AdSlot instance.
      ad_index: The index of the ad in the BidResponse, used to make error
          messages more informative.
      adslot_index: The index of the adslot in the ad, used to make error
          messages more informative.
      record: A Record instance containing the context for this validation.

    Returns:
      True if any problems were found with the adslot.
    """
    problems_found = False
    if adslot.max_cpm_micros == 0:
      record.problems.append(self.ADSLOT_ERROR_TEMPLATE % (
          adslot_index, ad_index, self.ADSLOT_ERROR_MESSAGES["zero-bid"]))
      problems_found = True

    if adslot.HasField('min_cpm_micros'):
      if adslot.min_cpm_micros == 0:
        record.problems.append(self.ADSLOT_ERROR_TEMPLATE % (
            adslot_index,
            ad_index,
            self.ADSLOT_ERROR_MESSAGES["zero-min-cpm"]))
        problems_found = True
      elif adslot.min_cpm_micros >= adslot.max_cpm_micros:
        record.problems.append(self.ADSLOT_ERROR_TEMPLATE % (
            adslot_index,
            ad_index,
            self.ADSLOT_ERROR_MESSAGES["min-more-than-max"]))
        problems_found = True

    request_adslot = self.FindAdSlotInRequest(adslot.id, record.bid_request)
    if not request_adslot:
      record.problems.append(self.ADSLOT_ERROR_TEMPLATE % (
          ad_index,
          adslot_index,
          self.ADSLOT_ERROR_MESSAGES["invalid-slot-id"]))
      problems_found = True

    return problems_found

  def WriteLogFiles(self, good_log, problematic_log, invalid_log, error_log,
                    snippet_log):
    """Writes log files for successful/error/problematic/invalid requests.

    Args:
      good_log: A file like object for writing the log of good requests, will
          not be closed by LogSummarizer.
      problematic_log: A file like object for writing the log of problematic
          requests, will not be closed by LogSummarizer.
      invalid_log: A file like object for writing the log of invalid requests,
          will not be closed by LogSummarizer.
      error_log: A file like object for writing the log of error requests, will
          not be closed by LogSummarizer.
      snippet_log: A file like object for writing the rendered snippets, will
          not be closed by LogSummarizer.
    """
    # Write header into snippet log file.
    if self._good or self._problematic:
      snippet_log.write("<html><head><title>Rendered snippets</title></head>\n")
      snippet_log.write("<body><h1>Rendered Snippets</h1>")
      snippet_log.write("<p>Your server has returned the following renderable"
                        " snippets:</p>")
      snippet_log.write("<ul>")

    if self._problematic:
      problematic_log.write("=== Responses that parsed but had problems ===\n")
    for record in self._problematic:
      problematic_log.write("BidRequest:\n")
      problematic_log.write(str(record.bid_request))
      problematic_log.write("\nBidResponse:\n")
      problematic_log.write(str(record.bid_response))
      problematic_log.write("\nProblems:\n")
      for problem in record.problems:
        problematic_log.write("\t%s\n" % problem)
      self.WriteSnippet(record, snippet_log)

    if self._good:
      good_log.write("=== Successful responses ===\n")
    for record in self._good:
      good_log.write("BidRequest:\n")
      good_log.write(str(record.bid_request))
      good_log.write("\nBidResponse:\n")
      good_log.write(str(record.bid_response))
      self.WriteSnippet(record, snippet_log)

    if self._good or self._problematic:
      # Write footer into snippet log file.
      snippet_log.write("</ul></body></html>")

    if self._invalid:
      invalid_log.write("=== Responses that failed to parse ===\n")
    for record in self._invalid:
      invalid_log.write("BidRequest:\n")
      invalid_log.write(str(record.bid_request))
      invalid_log.write("\nPayload represented as a python list of bytes:\n")
      byte_list = [ord(c) for c in record.payload]
      invalid_log.write(str(byte_list))

    if self._error:
      error_log.write("=== Requests that received a non 200 HTTP response"
                      " ===\n")
    for record in self._error:
      error_log.write("BidRequest:\n")
      error_log.write(str(record.bid_request))
      error_log.write("HTTP response status code: %d\n" % record.status)
      error_log.write("\nPayload represented as a python list of bytes:\n")
      byte_list = [ord(c) for c in record.payload]
      error_log.write(str(byte_list))

  def WriteSnippet(self, record, log):
    """Writes the snippets in the given record into the log."""
    if not record.html_snippets:
      # No snippets to print. Records that are problematic may or may not have
      # snippets.
      return

    for ad_index, snippet in record.html_snippets.iteritems():
      response_adslot_id = record.bid_response.ad[ad_index].adslot[0].id
      request_adslot = self.FindAdSlotInRequest(response_adslot_id,
                                                record.bid_request)
      if request_adslot is None:
        continue
      log.write("<li>")
      log.write("<h3>Bid Request</h3>")
      log.write("<pre>%s</pre>" % cgi.escape(str(record.bid_request)))
      log.write("<h3>Bid Response</h3>")
      log.write("<pre>%s</pre>" % cgi.escape(str(record.bid_response)))
      log.write("<h3>Rendered Snippet</h3>")

      iframe = ("<iframe src='data:text/html;base64,\n%s' "
                "width=%d height=%d scrolling=no marginwidth=0 "
                "marginheight=0></iframe>\n" % (
                    base64.b64encode(snippet),
                    request_adslot.width[0],
                    request_adslot.height[0]))
      log.write(iframe)
      log.write("</li>")

  def PrintReport(self):
    """Prints a summary report."""
    print "=== Summary of Real-time Bidding test ==="
    print "Requests sent: %d" % self._requests_sent
    print "Responses with a 200/OK HTTP response code: %d" % self._responses_ok
    print "Responses with a non-200 HTTP response code: %d" % len(self._error)
    print "Good responses (no problems found): %d" % len(self._good)
    print "Invalid (unparseable) with a 200/OK HTTP response code: %d" % len(
        self._invalid)
    print "Parseable responses with problems: %d" % len(self._problematic)
    if self._processing_time_count:
      print "Average processing time in milliseconds %d" % (
          self._processing_time_sum * 1.0 / self._processing_time_count)
    if self._responses_successful_without_bids == self._requests_sent:
      print "ERROR: None of the responses had bids!"
