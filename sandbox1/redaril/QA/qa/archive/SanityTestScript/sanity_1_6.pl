#!c:Perl/bin/perl -w

use strict;
use WWW::Selenium;
use WWW::Mechanize;
use DBI;
use warnings;
use Time::HiRes qw(sleep);
use Test::WWW::Selenium;
use LWP::Simple;
use URI::URL;
use LWP::UserAgent;
use HTTP::Cookies;
use LWP::LastURI;
use LWP::Debug qw(+);
require "readinparams.pm";
require "systemuptests.pm";
require "optout.pm";
require "partnerbeacons.pm";
require "utils.pm";
require "datavalidations.pm";
require "demography.pm";
require "adserverlegacy.pm";
require "as2adservertest.pm";



our ($contentwritelogcount, $userSessionlogcount, $userQualRdlogcount, $userQualProdlogcount, $upvrdlogcount, $umEventRdlogcount, $asRequestProdlogcount, $asRequestRdlogcount, $umRequestProdlogcount, $umRequestRdlogcount, $userIdHistorylogcount, $umEventProdlogcount, $icSearchKwdlogcount, $icSearchClicklogcount, $contentWriterlogcount, $ncqualifieruservisitcount, $ncusersessioncount, $usermodelereventlogcount, $usermodelerrequestlogcount, $yieldmanagerrequestlogcount, $ncusercount, $ncusermodelcount, $ncuserodccount, $ncqualifiergroupuservisitcount, $hipid);

our($clustername, $databaseserver, $certername, $umserver, $icserver, $yieldmanager, $adserver, $virtualcerter, $certer_id, $buildversion, $deviceid, $testpageurl, $utacentralurl, $adcentralurl, $optouturl, $optinurl, $ftpserver) = &readinParams();

open STDERR, ">C:\\Projects\\Scripts\\SanityTestScript\\log\\errtest.txt";
print ("Starting up now...\n");

$| = 1;

our $dbh4 = DBI->connect( "dbi:Oracle:host=10.50.200.11;sid=qacluster", "$clustername", "$clustername");
our $dbh5 = DBI->connect( "dbi:Oracle:host=10.50.200.11;sid=qacluster", "$clustername", "$clustername");

#&demographytests();  #run this separately against qac4
#die;


my $i = 0;
while ($i < 1) {
  &checkthatadserverisresponsive();
  &checkthatusermodelerisresponsive();
  &checkadcentral();
  &checkutacentral();
  &checkcerterinjections();
  &verifysettingsinnccertertable();
  &verifyoptout();
  &beaconenabledisableignorelogic();
  &clearoutncusertables();
  &checkfrequencycaps();
  &legacyadservertest();
  &as2adservertest();
  &checkuserpagevisitstablegetsupdated();
  &checksomelargepages();
  &checkcerterdoesadmods();
  &checkpagestatusfromtables();
#  &checkcoarseprofile(); no longer needed, no coarse profiles
#  &checkfineprofile();
  &oracledataverification();
  $i++;
}
