# Copyright 2009 Google Inc. All Rights Reserved.
# Author: foox@google.com (Igor Foox)
#
# Package:
#   contentads/adx/networkbid/sample/requester
#
# Description:
#   A Python application that sends sample BidRequests to Real Time Bidders.
#   This code will be shared with customers.
#   See README.google for build instructions.

# Visibility:  please choose a more appropriate default for the package,
# and update any rules that should be different.
# See: http://goto/package-visibility
package(default_visibility = ["//visibility:legacy_public"])

# Copies customer facing proto file and renames it to realtime-bidding.proto.
genrule(
    name = "renamed_proto",
    srcs = ["//contentads/adx/networkbid/proto:network-bid-customer-facing_genproto"],
    outs = ["realtime-bidding.proto"],
    cmd = "cp -f $< $@",
    local = 1,
)

# All of the files needed for the exported application. Add any new files here.
entries = [
    # Renamed customer facing protobuf.
    FilesetEntry(
        srcdir = "//contentads/adx/networkbid/sample/requester",
        files = ["realtime-bidding.proto"],
    ),

    # Application files.
    FilesetEntry(
        files = [
            "Makefile",
            "README",
            "generator.py",
            "generator_test.py",
            "log.py",
            "log_test.py",
            "requester.py",
            "requester_test.py",
            "sender.py",
            "sender_test.py",
        ],
    ),
]

Fileset(
    name = "requester_files",
    out = "requester",
    entries = entries,
)

genrule(
    name = "export",
    srcs = [
        "//contentads/adx/networkbid/sample/requester:requester_files",
    ],
    outs = ["requester.tar.gz"],
    cmd = "tar -C `dirname $<` -czhf $@ `basename $<` ",
)
