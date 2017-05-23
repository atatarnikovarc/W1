#!/usr/bin/python
# Copyright 2009 Google Inc. All Rights Reserved.
"""A class to generate random BidRequest protocol buffers."""

import base64
import random
import time

import realtime_bidding_pb2

PROTOCOL_VERSION = 1

BID_REQUEST_ID_LENGTH = 16  # In bytes.
COOKIE_LENGTH = 20  # In bytes.
COOKIE_VERSION = 1

# Placement.
CHANNELS = ['12345']

# Data describing branded publishers.
# Tuple description: (publisher url, seller id, publisher settings id, seller)
# The below are example values for these fields that are used to populate
# publisher info.
BRANDED_PUB_DATA = [
    ('http://www.youtube.com', 502, 32423234, 'Youtube'),
    ('http://www.youtube.com/shows', 502, 32423234, 'Youtube'),
    ('http://news.google.com', 10001, 56751341, 'Google News'),
    ('http://news.google.com/news?pz=1&ned=us&topic=b&ict=ln', 10001, 12672383,
     'Google News'),
    ('http://www.google.com/finance?hl=en&ned=us&tab=ne', 1528, 84485234,
     'Google Finance'),
    ('http://www.nytimes.com/pages/technology/index.html', 936, 9034124,
     'New York Times'),
    ('http://some.gcn.site.com', 10002, 12002392, 'GCN'),
]

# Data for anonymous publishers.
# Tuple description: (anonymous url, publisher settings id)
ANONYMOUS_PUB_DATA = [
    ('http://1.google.anonymous/', 90002301),
    ('http://2.google.anonymous/', 90002302),
    ('http://3.google.anonymous/', 90002303),
    ('http://4.google.anonymous/', 93002304),
    ('http://5.google.anonymous/', 93002305),
]

MAX_ADGROUP_ID = 99999999
MAX_DIRECT_DEAL_ID = 1 << 62
MAX_MATCHING_ADGROUPS = 3

DIMENSIONS = [
#    (468, 60),
 #   (120, 600),
  #  (728, 90),
   # (300, 250),
    #(250, 250),
    #(336, 280),
    #(120, 240),
    #(125, 125),
    (160, 600),
    #(180, 150),
    #(110, 32),
    #(120, 60),
    #(180, 60),
    #(420, 600),
    #(420, 200),
    #(234, 60),
    #(200, 200),
]

MAX_SLOT_ID = 200

# Verticals.
MAX_NUM_VERTICALS = 5
VERTICALS = [
    66, 563, 607, 379, 380, 119, 570, 22, 355, 608, 540, 565, 474, 433, 609,
    23, 24,
]

# Geo.
LANGUAGE_CODES = ['en']

# Example geo targets used to populate requests.
# Tuple description (geo_criteria_id, postal code, postal code prefix)
# Only one of postal code or postal code prefix will be set.
# Canada has only postal code prefixes available.
GEO_CRITERIA = [
#    (9005559, '10116', None),  # New York, United States
 #   (9031936, '94087', None),  # California, United States
 #   (1015214, '33601', None),  # Tampa, Florida, United States
 #   (1021337, '27583', None),  # Timberlake, North Carolina, United States
 #   (1012873, '99501', None),  # Anchorage, Alaska, United States
    (1018127, '02102', None),  # Boston, Massachusetts, United States
 #   (1002451, None, 'M4C'),  # Toronto, Ontario, Canada
 #   (1002113, None, 'B3H'),  # Halifax, Nova Scotia, Canada
 #   (1002061, None, 'E1B'),  # Moncton, New Brunswick, Canada
 #   (1000278, '2753', None),  # Richmond, New South Wales, Australia
 #   (1000142, '2600', None),  # Canberra, Australian Capital Territory,Australia
 #   (1000414, '4810', None),  # Townsville, Queensland, Australia
 #   (1000567, '3000', None),  # Melbourne, Victoria, Australia
]

# User info.
USER_AGENTS = [
    'Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.0.2) '
    'Gecko/2008092313 Ubuntu/8.04 (hardy) Firefox/3.1',
    'Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.1.2pre) '
    'Gecko/20070118 Firefox/2.0.0.2pre',
    'Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.7pre) Gecko/20070815 '
    'Firefox/2.0.0.6 Navigator/9.0b3',
    'Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10_4_11; en) AppleWebKit/528.5+'
    ' (KHTML, like Gecko) Version/4.0 Safari/528.1',
    'Mozilla/5.0 (Macintosh; U; PPC Mac OS X; sv-se) AppleWebKit/419 '
    '(KHTML, like Gecko) Safari/419.3',
    'Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)',
    'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0;)',
    'Mozilla/4.08 (compatible; MSIE 6.0; Windows NT 5.1)',
]

# Criteria.
MAX_EXCLUDED_ATTRIBUTES = 3
CREATIVE_ATTRIBUTES = [9]

MAX_EXCLUDED_BUYER_NETWORKS = 2

MAX_INCLUDED_VENDOR_TYPES = 4
VENDOR_TYPES = [
    0, 10, 28, 42, 51, 65, 71, 92, 94, 113, 126, 128, 129, 130, 143, 144, 145,
    146, 147, 148, 149, 152, 179, 194, 198, 225, 226, 227, 228, 229, 230, 231,
    232, 233, 234, 235, 236, 237, 238, 255, 303,  311, 312, 313, 314, 315, 316,
]
#VENDOR_TYPES = [0]

INSTREAM_VIDEO_VENDOR_TYPES = [297, 220, 306, 307, 308, 309, 310, 317, 318,]

MAX_EXCLUDED_CATEGORIES = 1
AD_CATEGORIES = [0, 3, 4, 5, 7, 8, 10, 18, 19, 23, 24, 25,]


MAX_TARGETABLE_CHANNELS = 3
TARGETABLE_CHANNELS = [
    'all top banner ads', 'right hand side banner', 'sports section',
    'user generated comments', 'weather and news',
]

DEFAULT_INSTREAM_VIDEO_PROPORTION = 0.1
INSTREAM_VIDEO_START_DELAY_MAX_SECONDS = 60
INSTREAM_VIDEO_DURATION_MAX_SECONDS = 60
# Types of invideo_requests.
INSTREAM_VIDEO_PREROLL = 0
INSTREAM_VIDEO_MIDROLL = 1
INSTREAM_VIDEO_POSTROLL = -1
INSTREAM_VIDEO_TYPES = [
    INSTREAM_VIDEO_PREROLL, INSTREAM_VIDEO_MIDROLL, INSTREAM_VIDEO_POSTROLL]


random.seed(time.time())


class RandomBidGenerator(object):
  """Generates random BidRequests."""

  def __init__(self, google_id_list=None,
               instream_video_proportion=DEFAULT_INSTREAM_VIDEO_PROPORTION,
               adgroup_ids_list=None):
    """Constructs a new RandomBidGenerator.

    Args:
      google_id_list: A list of Google IDs (as strings), or None to randomly
          generate IDs.
      instream_video_proportion: The proportion of requests which are for
          instream video ads [0.0, 1.0].
      adgroup_ids_list: A list of AdGroup IDs (as ints), or None to randomly
          generate IDs.
    """
    self._google_id_list = google_id_list
    self._instream_video_proportion = instream_video_proportion
    if adgroup_ids_list is not None:
      self._adgroup_ids = set(adgroup_ids_list)
    else:
      self._adgroup_ids = None

  def GenerateBidRequest(self):
    """Generates a random BidRequest.

    Returns:
      An instance of realtime_bidding_pb2.BidRequest.
    """
    bid_request = realtime_bidding_pb2.BidRequest()
    bid_request.is_test = True
    bid_request.id = self._GenerateId(BID_REQUEST_ID_LENGTH)

    self._GeneratePageInfo(bid_request)
    self._GenerateUserInfo(bid_request)
    self._GenerateAdSlot(bid_request)

    return bid_request

  def GeneratePingRequest(self):
    """Generates a special ping request.

    A ping request only has the id and is_ping fields set.

    Returns:
      An instance of realtime_bidding_pb2.BidRequest.
    """
    bid_request = realtime_bidding_pb2.BidRequest()
    bid_request.id = self._GenerateId(BID_REQUEST_ID_LENGTH)
    bid_request.is_ping = True
    return bid_request

  def _GenerateId(self, length):
    """Generates a random ID.

    The generated ID is not guaranteed to be unique.

    Args:
      length: Length of generated ID in bytes.
    Returns:
      A random ID of the given length."""
    random_id = ''
    for _ in range(length):
      random_id += chr(random.randint(0, 255))
    return random_id

  def _GeneratePageInfo(self, bid_request):
    """Generates page information for the given bid_request.

    Args:
      bid_request: a realtime_bidding_pb2.BidRequest instance"""
    # 50% chance of anonymous ID/branded URL.
    if random.choice([True, False]):
      url, seller_id, pub_id, seller = random.choice(BRANDED_PUB_DATA)
      bid_request.url = url
      bid_request.seller_network_id = seller_id
      bid_request.publisher_settings_list_id = pub_id
      bid_request.DEPRECATED_seller_network = seller
    else:
      anonymous_id, pub_id = random.choice(ANONYMOUS_PUB_DATA)
      bid_request.anonymous_id = anonymous_id
      bid_request.publisher_settings_list_id = pub_id

    # Random chance of generating an instream video request.
    #if random.random() < self._instream_video_proportion:
    if 1 > 2:
      video = bid_request.video
      request_type = random.choice(INSTREAM_VIDEO_TYPES)

      if request_type == INSTREAM_VIDEO_MIDROLL:
        delay_seconds = random.randint(1,
                                       INSTREAM_VIDEO_START_DELAY_MAX_SECONDS)
        video.videoad_start_delay = delay_seconds * 1000  # In milliseconds.
      else:
        video.videoad_start_delay = request_type

      # 50% chance of setting max_ad_duration.
      if random.choice([True, False]):
        max_ad_duration_seconds = random.randint(
            1, INSTREAM_VIDEO_DURATION_MAX_SECONDS)
        # In milliseconds.
        video.max_ad_duration = max_ad_duration_seconds * 1000

    bid_request.detected_language = random.choice(LANGUAGE_CODES)
    self._GenerateVerticals(bid_request)

  def _GenerateAdSlot(self, bid_request):
    """Generates a single ad slot with random data.

    Args:
      bid_request: a realtime_bidding_pb2.BidRequest instance"""
    ad_slot = bid_request.adslot.add()
    ad_slot.id = random.randint(1, MAX_SLOT_ID)
    # Don't generate dimensions for instream video adslots.
    if not bid_request.HasField('video'):
      width, height = random.choice(DIMENSIONS)
      ad_slot.width.append(width)
      ad_slot.height.append(height)

    # Generate random allowed vendor types, if this is an instream video request
    # use instream video vendors.
    #vendor_list = VENDOR_TYPES
    #if bid_request.HasField('video'):
    #  vendor_list = INSTREAM_VIDEO_VENDOR_TYPES
    #num_included_vendor_types = random.randint(1, MAX_INCLUDED_VENDOR_TYPES)
    #for allowed_vendor in self._GenerateSet(vendor_list,
    #                                        num_included_vendor_types):
    #  ad_slot.allowed_vendor_type.append(allowed_vendor)
    ad_slot.allowed_vendor_type.append(0)

    # Generate random excluded creative attributes.
    num_excluded_creative_attributes = random.randint(1,
                                                      MAX_EXCLUDED_ATTRIBUTES)
    for creative_attribute in self._GenerateSet(
        CREATIVE_ATTRIBUTES, num_excluded_creative_attributes):
      ad_slot.excluded_attribute.append(creative_attribute)

    # Generate excluded categories for 20% of requests.
    if random.random() < 0.2:
      num_excluded_categories = random.randint(1, MAX_EXCLUDED_CATEGORIES)
      for excluded_category in self._GenerateSet(AD_CATEGORIES,
                                                 num_excluded_categories):
        ad_slot.excluded_sensitive_category.append(excluded_category)

    # Generate ad slot publisher settings list id by combining bid request
    # pub settings id and slot id.
    ad_slot.publisher_settings_list_id = (bid_request.publisher_settings_list_id
                                          + ad_slot.id)

    # We generate channels only for branded sites, simplifying by using the
    # same list of channels for all publishers.
    if bid_request.HasField('seller_network_id'):
      # Send only for 10% of bid requests, to simulate that few bid requests
      # have targetable channels in reality.
      send_channels = random.random < 0.1
      if send_channels:
        num_targetable_channels = random.randint(1, MAX_TARGETABLE_CHANNELS)
        for channel in self._GenerateSet(TARGETABLE_CHANNELS,
                                         num_targetable_channels):
          ad_slot.targetable_channel.append(channel)

    # Generate adgroup IDs, either randomly or from the ID list parameter
    if self._adgroup_ids:
      num_matching_adgroups = random.randint(1, len(self._adgroup_ids))
      generated_ids = random.sample(self._adgroup_ids, num_matching_adgroups)
    else:
      num_matching_adgroups = random.randint(1, MAX_MATCHING_ADGROUPS)
      generated_ids = [random.randint(1, MAX_ADGROUP_ID)
                       for _ in xrange(num_matching_adgroups)]

    for generated_id in generated_ids:
      ad_data = ad_slot.matching_ad_data.add()
      ad_data.adgroup_id = 11223344#generated_id

      # 10% of adgroup requests will have a direct deal enabled
      if random.random() < 0.10:
        direct_deal = ad_data.direct_deal.add()
        direct_deal.direct_deal_id = random.randint(1, MAX_DIRECT_DEAL_ID)
        direct_deal.fixed_cpm_micros = random.randint(1, 99) * 10000
        ad_data.minimum_cpm_micros = direct_deal.fixed_cpm_micros

  def _GenerateVerticals(self, bid_request):
    """Populates bid_request with random verticals.

    Args:
      bid_request: a realtime_bidding_pb2.BidRequest instance.
    """
    verticals = self._GenerateSet(VERTICALS, MAX_NUM_VERTICALS)
    for vertical in verticals:
      vertical_pb = bid_request.detected_vertical.add()
      vertical_pb.id = vertical
      vertical_pb.weight = random.random()

  def _GenerateGoogleID(self, bid_request):
    """Generates the google id field.

    If the RandomBidGenerator was initated with a list of Google IDs, one of
    these is picked at random, otherwise a random ID is generated.

    Args:
      bid_request: A realtime_bidding_pb2.BidRequest instance.
    """
    if self._google_id_list:
      bid_request.google_user_id = random.choice(self._google_id_list)
    else:
      hashed_cookie = self._GenerateId(COOKIE_LENGTH)
      google_user_id = base64.urlsafe_b64encode(hashed_cookie)
      # Remove padding, i.e. remove '='s off the end.
      bid_request.google_user_id = google_user_id[:google_user_id.find('=')]
    # Cookie age of [1 second, 30 days).
    bid_request.cookie_age_seconds = random.randint(1, 60*60*24*30)

  def _GenerateUserInfo(self, bid_request):
    """Generates random user inforamation.

    Args:
      bid_request: a realtime_bidding_pb2.BidRequest instance"""

    geo_id, postal, postal_prefix = random.choice(GEO_CRITERIA)
    bid_request.geo_criteria_id = geo_id
    if postal:
      bid_request.postal_code = postal
    elif postal_prefix:
      bid_request.postal_code_prefix = postal_prefix
    self._GenerateGoogleID(bid_request)
    bid_request.cookie_version = COOKIE_VERSION

    bid_request.user_agent = random.choice(USER_AGENTS)
    # 4 bytes in IPv4, but last byte is truncated giving an overall length of 3
    # bytes.
    ip = self._GenerateId(3)
    bid_request.ip = ip

  def _GenerateSet(self, collection, set_size):
    """Generates a set of randomly chosen elements from the given collection.

    Args:
      collection: a list-like collection of elements
      set_size: the size of set to generate

    Returns:
      A set of randomly chosen elements from the given collection.
    """
    unique_collection = set(collection)
    if len(unique_collection) < set_size:
      return unique_collection

    s = set()
    while len(s) < set_size:
      s.add(random.choice(collection))

    return s
