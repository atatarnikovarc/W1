#!/usr/bin/python
# Copyright 2009 Google Inc. All Rights Reserved.
"""Unit tests for generator.py."""

import base64
import unittest

import realtime_bidding_pb2

import generator


class RandomBidGeneratorTest(unittest.TestCase):
  """Tests the RandomBidGenerator class."""

  def setUp(self):
    self.generator = generator.RandomBidGenerator()

  def testGeneratingOneRandomBidRequest(self):
    """Tests that generating one random bid request works."""
    bid_request = self.generator.GenerateBidRequest()
    self.assertFalse(bid_request is None)
    self.assertTrue(bid_request.IsInitialized())
    self.assertTrue(
        (bid_request.HasField('url') and bid_request.HasField('seller_network'))
         or bid_request.HasField('anonymous_id'))

    expected_scalar_fields = [
        'DEPRECATED_protocol_version',
        'is_test',
        'id',
        'detected_language',
        'country',
        'region',
        'city',
        'google_user_id',
        'cookie_version',
        'cookie_age_seconds',
        'user_agent',
        'ip',
        'publisher_settings_list_id',
    ]
    for field in expected_scalar_fields:
      self.assertTrue(bid_request.HasField(field),
                      'Bid request missing field: %s' % field)
    # Metro only defined for the USA.
    if bid_request.country == 'US':
      self.assertTrue(bid_request.HasField('metro'),
                      'Bid request missing field: metro')

    # Either postal_code or postal_code_prefix should be defined
    if not bid_request.HasField('postal_code'):
      self.assertTrue(bid_request.HasField('postal_code_prefix') and
                      len(bid_request.postal_code_prefix) > 1,
                      'Bid request missing both postal_code and'
                      'postal_code_prefix')
    # Ip should be 3 bytes long (IPv4 with last byte truncated).
    self.assertEqual(3, len(bid_request.ip))

    self.assertTrue(len(bid_request.detected_vertical) > 0)
    for vertical in bid_request.detected_vertical:
      self.assertNotEqual(vertical.id, 0)
      self.assertNotEqual(vertical.id, 0)

    self.assertTrue(len(bid_request.adslot) > 0)
    for adslot in bid_request.adslot:
      self.assertEqual(len(adslot.DEPRECATED_allowed_attribute), 0)
      self.assertNotEqual(adslot.id, 0)
      self.assertEqual(len(adslot.height), 1)
      self.assertEqual(len(adslot.width), 1)
      self.assertNotEqual(adslot.height[0], 0)
      self.assertNotEqual(adslot.width[0], 0)
      self.assertTrue(len(adslot.excluded_attribute) > 0)
      self.assertTrue(len(adslot.allowed_vendor_type) > 0)
      self.assertTrue(len(adslot.matching_ad_data) > 0)
      self.assertEqual(1, len(adslot.publisher_settings_list_id))
      self.assertEqual(generator.PUBLISHER_SETTINGS_ID_LENGTH,
                       len(adslot.publisher_settings_list_id[0]))
      if not bid_request.HasField('seller_network'):
        self.assertFalse(adslot.targetable_channel)
      for ad_data in adslot.matching_ad_data:
        self.assertTrue(ad_data.HasField('adgroup_id'))
        self.assertNotEqual(ad_data.adgroup_id, 0)

  def testGeneratePingRequest(self):
    """Tests generating a ping request."""
    bid_request = self.generator.GeneratePingRequest()
    self.assertFalse(bid_request is None)
    self.assertTrue(bid_request.IsInitialized())
    self.assertTrue(bid_request.HasField('is_ping'))
    self.assertTrue(bid_request.is_ping)

  def testGenerateGoogleIdFromList(self):
    """Tests generating a Google ID from a list."""
    # Real sample values from the Cookie Matching service.
    google_ids = [
        'CAESEBs2zXrUdzP08NfyRcwuOic',
        'CAESEGDp9dG8BR26gKybOFwKVnQ',
        'CAESELGsXo_3kPoHM2EdtzhqvSs',
        'CAESEPOq9BSD5GWBmb716V8R-uo',
        'CAESEJaOYa0vEUojtGMTlkf9UoM',
        'CAESECwK0aD3FY0QIaU6cA1ln2A',
    ]


    self.generator = generator.RandomBidGenerator(google_ids)
    bid_request = realtime_bidding_pb2.BidRequest()
    self.generator._GenerateGoogleID(bid_request)
    self.assertTrue(bid_request.HasField('google_user_id'))
    self.assertTrue(bid_request.google_user_id in google_ids)

  def testGenerateRandomGoogleId(self):
    """Tests generating a random Google ID."""
    self.generator = generator.RandomBidGenerator()
    bid_request = realtime_bidding_pb2.BidRequest()
    self.generator._GenerateGoogleID(bid_request)
    self.assertTrue(bid_request.HasField('google_user_id'))

  def testGeneratedBidRequestsNotEqual(self):
    """Tests that two generated bid requests are not equal."""
    bid_request1 = self.generator.GenerateBidRequest()
    bid_request2 = self.generator.GenerateBidRequest()
    self.assertNotEqual(bid_request1, bid_request2)

if __name__ == '__main__':
  unittest.main()
