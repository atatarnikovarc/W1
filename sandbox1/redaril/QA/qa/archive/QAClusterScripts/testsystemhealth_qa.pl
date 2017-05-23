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
use LWP::Debug qw(+);


#open STDOUT, ">C:\\Perl\\projects\\healthtest.txt";
open STDERR, ">C:\\errtest.txt";
print ("Starting up now...\n");

$| = 1;

#qacluster1 environment
my $databaseserver = "10.50.150.151";
my $certername = "qacluster1UTA";
my $umserver = "10.50.150.150";
my $icserver = "10.50.150.151";
my $yieldmanager = "10.50.150.150";
my $adserver = "qacluster1-1.nebuad.com";
my $virtualcerter = "10.50.150.150";
my $certer_id = 4;
my $deviceid;
my $testpageurl1 = "http://www.a101tech.com/qa/demo.html";
my $testpageurl = "http://www.a101tech.com/qa/chuck_blog/blog_q1.htm";
#my $utacentralurl = "http://10.50.150.151:8080/controlcenter/views/webcommon/login.jsp";
my $qcentralurl ="http://10.50.150.150:8080/nebulis/views/webcommon/login.jsp";
my $utacentralurl = "http://10.50.150.151/controlcenter/views/webcommon/login.jsp";
our $dbh1 = DBI->connect( "dbi:mysql:database=logdb;host=$databaseserver;port=3306",'root', '901nebuad', { RaiseError => 1, AutoCommit => 1 });
our $dbh2 = DBI->connect( "dbi:mysql:database=logdb;host=$databaseserver;port=3306",'root', '901nebuad', { RaiseError => 1, AutoCommit => 1 });
our $dbh3 = DBI->connect( "dbi:mysql:database=logdb;host=$databaseserver;port=3306",'root', '901nebuad', { RaiseError => 1, AutoCommit => 1 });
our $dbh4 = DBI->connect("dbi:Oracle:host=10.50.200.11;sid=qa", "qacluster1", "qacluster1");
our $dbh5 = DBI->connect("dbi:Oracle:host=10.50.200.11;sid=qa", "qacluster1", "qacluster1");

our $contentwritelogcount;
our $ncqualifieruservisitcount;
our $ncusersessioncount;
our $usermodelereventlogcount;
our $usermodelerrequestlogcount;
our $yieldmanagerrequestlogcount;
our $ncusercount;
our $ncusermodelcount;
our $ncuserodccount;
our $ncqualifiergroupuservisitcount;


#&checkcoarseprofile();
#&mysqldataverification();
#&oracledataverification();
#&sanitycheckcertertraffic();
#&footprintid();
#&piiremoval();
#&verifyoptout();
#adservercalls
#die;

my $i = 0;
while ($i < 1) {
  &checkthatadserverisresponsive();
  &checkthatusermodelerisresponsive();
  &checkutacentral();
  &checkqcentral();
  &checkcerterinjections();
  &verifysettingsinnccertertable();
  &verifyoptout();
  &getinitialmeasurements();
  &supersessiontest();
  &footprintid();
  &checkuserpagevisitstablegetsupdated();
  &checksomelargepages();
  &checkcerterdoesadmods();
  &checkpagestatusfromtables();
  &checkcoarseprofile();
  &checkfineprofile();
  &getfinalmeasurements();
  &checkcertertocscprotocol();
  &mysqldataverification();
  &oracledataverification();
  &sanitycheckcertertraffic();
  $i++;
  sleep(10);
}




sub piiremoval {
  use strict;
  use warnings;
  #use Time::HiRes qw(sleep);
  #use Test::WWW::Selenium;
  #use Test::More "no_plan";
  #use Test::Exception;

  print ("Testing PII Removal\t\t");
  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.ohiostatealumni.org/yourcontactinfo/updateinfo_name.php",
			      );
  $sel->start();

  $sel->open("http://www.ohiostatealumni.org/yourcontactinfo/updateinfo_name.php");
  $sel->type("field[oldfirstname]", "abcd");
  $sel->type("field[oldmiddlename]", "efgh");
  $sel->type("field[maidenname]", "bbcc");
  $sel->type("field[oldlastname]", "ddee");
  $sel->type("field[spousefirstname]", "abcd");
  $sel->type("field[spousemiddlename]", "edds");
  $sel->type("field[spousemaidenname]", "asdfa");
  $sel->type("field[spouselastname]", "fdffds");
  $sel->type("field[address1]", "123 main street");
  $sel->type("field[address2]", "125 main street");
  $sel->type("field[city]", "cleavland");
  $sel->type("field[state]", "akron");
  $sel->type("field[zip]", "99333");
  $sel->type("field[country]", "88222");
  $sel->type("field[daytimephone]", "333-3333");
  $sel->type("field[eveningphone]", "222-2222");
  $sel->type("field[e-mail]", "abcd\@ohiostatetest.com");
  $sel->type("field[newfirstname]", "james");
  $sel->type("field[newlastname]", "jamesly");
  $sel->type("field[newmiddlename]", "matt");
  $sel->type("field[e-mail]", "abc\@ohiostatetest.com");
  $sel->type("field[yourclassyears]", "1988");
  $sel->click("updateinfo_submit");
  $sel->wait_for_page_to_load("30000");
  $sel->stop();
  print ("OK\n");
}




sub verifyoptout  {
  use LWP::LastURI;
  print ("Opt-Out testing\t\t\tOK\n");
  my $tsurl;
  #my $url1 = "http://$adserver:8080/a?t=o&nai=yes";
  my $url1 = "http://$adserver/a?t=o&nai=yes";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  print ("initial state\t\t\t");
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  my $redirected_final = LWP::LastURI::last_uri;
  if ($redirected_final =~ m/no_cookie\.gif/) {
    print ("Pass\t");
    print ("$redirected_final\n");
  } else {
    print ("Failed\t");
    print ("$redirected_final\n");
  }

  print ("Opting Out\t\t\tOK\n");
  #my $url2 = "http://"."$adserver".":8080/a?t=o&track=no&noads=all"; #opt out
  my $url2 = "http://"."$adserver"."/a?t=o&track=no&noads=all"; #opt out
  $mech->get($url2);
  my $response = $mech->response();
  my $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  if ( $header =~ m/o=9/) {
    print ("o cookie set to 9\t\tPass\n");
  }


  print ("nai url\t\t\t\t");
  $mech->get($url1);
  $fetchedpage = $mech->content();
  $redirected_final = LWP::LastURI::last_uri;
  if ($redirected_final =~ m/cookie_optout\.gif/) {
    print ("Pass\t");
    print ("$redirected_final\n");
  } else {
    print ("Failed\t");
    print ("$redirected_final\n");
  }


  print ("Opting In\t\t\tOK\n");
  #my $url3 = "http://"."$adserver".":8080/a?t=o&track=yes&noads=none"; #opt in
  my $url3 = "http://"."$adserver"."/a?t=o&track=yes&noads=none"; #opt in


  $mech->get($url3);
  $response = $mech->response();
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  if ( $header =~ m/o=0/) {
    print ("o cookie set to 0\t\tPass\n");
  }


  print ("nai url\t\t\t\t");
  $mech->get($url1);
  $fetchedpage = $mech->content();
  $redirected_final = LWP::LastURI::last_uri;
  if ($redirected_final =~ m/cookie_exists\.gif/) {
    print ("Pass\t");
    print ("$redirected_final\n");
  } else {
    print ("Failed\t");
    print ("$redirected_final\n");
  }


  my $url4 = "http://www.google.com";
  $mech->get($url4);

  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  } else {
    print ("Failed to get t=s script\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();

  my $tburl;
  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("Failed to get t=b script\n");
  }

  $mech->get( $tburl );
  $response = $mech->response();
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  if ($header =~ m/j=/) {
    print ("Got all cookies (opted in)\tPass\n");
  } else {
    print ("Failed, didn't get all the cookies although opted in\n");
  }

  ##
  print ("Opting Out\t\t\tOK\n");
  #$url2 = "http://"."$adserver"."8080/a?t=o&track=no&noads=all"; #opt out
  $url2 = "http://"."$adserver"."/a?t=o&track=no&noads=all"; #opt out


  $mech->get($url2);
  $response = $mech->response();
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  if ( $header =~ m/o=9/) {
    print ("o cookie set to 9\t\tPass\n");
  }

  print ("nai url\t\t\t\t");
  $mech->get($url1);
  $fetchedpage = $mech->content();
  $redirected_final = LWP::LastURI::last_uri;
  if ($redirected_final =~ m/cookie_optout\.gif/) {
    print ("Pass\t");
    print ("$redirected_final\n");
  } else {
    print ("Failed\t");
    print ("$redirected_final\n");
  }
  ##
  $mech->get($url4);

  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  } else {
    print ("Failed to get t=s script\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();

  #my $tburl;
  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("t=b\t\t\t\tPass\tNo t=b script as expected\n");
  }

  $mech->get( $tburl );
  $response = $mech->response();
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  if ($header =~ m/j=/) {
    print ("Failed, getting all cookies eventhough opted out\n");
  } elsif ($header =~ m/o=/) {
    print ("Got only o cookie (opted out)\tPassed\n");
  }

  ###
  print ("Opting In\t\t\tOK\n");
  #$url3 = "http://"."$adserver".":8080/a?t=o&track=yes&noads=none"; #opt in
  $url3 = "http://"."$adserver"."/a?t=o&track=yes&noads=none"; #opt in


  $mech->get($url3);
  $response = $mech->response();
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  if ( $header =~ m/o=0/) {
    print ("o cookie set to 0\t\tPass\n");
  }

  print ("nai url\t\t\t\t");
  $mech->get($url1);
  $fetchedpage = $mech->content();
  $redirected_final = LWP::LastURI::last_uri;
  if ($redirected_final =~ m/cookie_exists\.gif/) {
    print ("Pass\t");
    print ("$redirected_final\n");
  } else {
    print ("Failed\t");
    print ("$redirected_final\n");
  }


  $url4 = "http://www.google.com";
  $mech->get($url4);

  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  } else {
    print ("Failed to get t=s script\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();


  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("Failed to get t=b script\n");
  }

  $mech->get( $tburl );
  $response = $mech->response();
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  if ($header =~ m/j=/) {
    print ("Got all cookies (opted in)\tPass\n");
  } else {
    print ("Failed, didn't get all the cookies although opted in\n");
  }
  ###

}



sub mysqldataverification {
  print ("****************************************\nVerifying adserver_request_log\tOK\n");
  my $sth1 = $dbh1->prepare("SELECT created_date_time, guid, ok, cookie_user_id, is_repeat_user, remote_address, request_url, query_string, header_user_agent,  user_hipplus_identifier, is_certer_user, ad_type, categories, spot_id, campaign_id, ad_id, yield_manager_calltime_millis, request_handling_time_millis, shown_ad FROM adserver_request_log order by id desc limit 1");
  $sth1->execute();
  while (my ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_user_agent, $user_hipplus_identifier, $is_certer_user, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad) = $sth1->fetchrow()) {
    print ("created_date_time\t\t");
    if ($created_date_time =~ m/2008/g) {
      print ("Passed\t$created_date_time\n");
    } else {
      print ("Failed<--\t$created_date_time\n");
    }

    print ("user_page_visit_guid\t\t");
    if ($guid =~ m/\d/g) {
      print ("Passed\t$guid\n");
    } else {
      print ("Failed<--\t$guid\n");
    }

    print ("ok column\t\t\t");
    if ($ok <= 1) {
      print ("Passed\t$ok\n");
    } else {
      print ("Failed<--\t$ok\n");
    }

    print ("cookie_user_id column\t\t");
    if ($cookie_user_id =~ m/\d+/) {
      print ("Passed\t$cookie_user_id\n");
    } else {
      print ("Failed<--\t$cookie_user_id\n");
    }

    print ("is_repeat_user\t\t\t");
    if ($is_repeat_user <= 1) {
      print ("Passed\t$is_repeat_user\n");
    } else {
      print ("Failed<--\t$is_repeat_user\n");
    }

    print ("remote_address column\t\t");
    if ($remote_address =~ m/\d+/) {
      print ("Passed\t$remote_address\n");
    } else {
      print ("Failed<--\t$remote_address\n");
    }

    print ("request_url column\t\t");
    if ($request_url =~ m/http/) {
      print ("Passed\t$request_url\n");
    } else {
      print ("Failed<--\t$request_url\n");
    }

    print ("query_string column\t\t");
    if ($query_string =~ m/t=/) {
      print ("Passed\t$query_string\n");
    } else {
      print ("Failed<--\t$query_string\n");
    }

    print ("header_user_agent column\t");
    if ($header_user_agent =~ m/Mozilla/) {
      print ("Passed\t$header_user_agent\n");
    } else {
      print ("Failed<--\t$header_user_agent\n");
    }

    print ("user_hipplus_identifier column\t");
    if ($user_hipplus_identifier =~ m/\d+/) {
      print ("Passed\t$user_hipplus_identifier\n");
    } else {
      print ("Failed<--\t$user_hipplus_identifier\n");
    }

    print ("is_certer_user column\t\t");
    if ($is_certer_user <= 1) {
      print ("Passed\t$is_certer_user\n");
    } else {
      print ("Failed<--\t$is_certer_user\n");
    }

    print ("ad_type column\t\t\t");
    if ($ad_type =~ m/[a-z]/) {
      print ("Passed\t$ad_type\n");
    } else {
      print ("Failed<--\t$ad_type\n");
    }

    print ("categories\t\t\tTBD\n");
    print ("spot_id\t\t\t\tTBD\n");
    print ("campaign_id\t\t\tTBD\n");
    print ("ad_id\t\t\t\tTBD\n");
    print ("shown_ad\t\t\tTBD\n");

  }
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT certer_identifier, connection_id, certer_timestamp, version FROM adserver_request_log where query_string like '%t=s%' order by id desc limit 1");
  $sth1->execute();
  while (my ( $certer_identifier, $connection_id, $certer_timestamp, $version) = $sth1->fetchrow()) {

    print ("certer_identifier column\t");
    if ($certer_identifier =~ m/[a-z]+/) {
      print ("Passed\t$certer_identifier\n");
    } else {
      print ("Failed<--\t$certer_identifier\n");
    }

    print ("connection_id column\t\t");
    if ($connection_id =~ m/\d+/) {
      print ("Passed\t$connection_id\n");
    } else {
      print ("Failed<--\t$connection_id\n");
    }

    print ("certer_timestamp column\t\t");
    if ($certer_timestamp =~ m/\d+/) {
      print ("Passed\t$certer_timestamp\n");
    } else {
      print ("Failed<--\t$certer_timestamp\n");
    }

    print ("version column\t\t\t");
    if ($version <= 2) {
      print ("Passed\t$version\n");
    } else {
      print ("Failed<--\t$version\n");
    }


  }
  $sth1->finish();

  print ("****************************************\nVerify content_write_log table\tOK\n");



  $sth1 = $dbh1->prepare("SELECT visit_guid, file_path  FROM content_write_log order by id desc limit 1");
  $sth1->execute();
  while (my ( $visit_guid, $file_path) = $sth1->fetchrow()) {
    print ("visit guid column\t\t");
    if ($visit_guid =~ m/0000/g) {
      print ("Passed\t$visit_guid\n");
    } else {
      print ("Failed<--\t$visit_guid\n");
    }

    print ("file_path column\t\t");
    if ($file_path =~ m/\/home\/csc\/content\/2008.+zip/) {
      print ("Passed\t$file_path\n");
    } else {
      print ("Failed<--\t$file_path\n");
    }
  }
  $sth1->finish();

  print ("****************************************\nVerify nc_qualifier_user_visit\tOK\n");


  $sth1 = $dbh1->prepare("SELECT created_date_time, qualifier_id, user_page_visit_guid, certer_id, interest, interest_id, interest_weight, is_internal, request_time_millisecond, domain_id, site_tree_category_id, parent_qualifier_id, qualifier_group_id  FROM nc_qualifier_user_visit order by qualifier_user_visit_id desc limit 1");
  $sth1->execute();
  while (my ( $created_date_time2, $qualifier_id, $user_page_visit_guid2, $certer_id, $interest, $interest_id, $interest_weight, $is_internal, $request_time_millisecond, $domain_id, $site_tree_category_id, $parent_qualifier_id, $qualifier_group_id) = $sth1->fetchrow()) {


    print ("created_date_time\t\t");
    if ($created_date_time2 =~ m/2008/g) {
      print ("Passed\t$created_date_time2\n");
    } else {
      print ("Failed<--\t$created_date_time2\n");
    }

    print ("qualifier_id\t\t\t");
    if ($qualifier_id > 0) {
      print ("Passed\t$qualifier_id\n");
    } else {
      print ("Failed<--\t$qualifier_id\n");
    }

    print ("user_page_visit_guid\t\t");
    if ($user_page_visit_guid2 =~ m/\d/g) {
      print ("Passed\t$user_page_visit_guid2\n");
    } else {
      print ("Failed<--\t$user_page_visit_guid2\n");
    }

    print ("certer_id\t\t\t");
    if ($certer_id > 0) {
      print ("Passed\t$certer_id\n");
    } else {
      print ("Failed<--\t$certer_id\n");
    }

    print ("interest column\t\t\tTBD\n");

    print ("interest_id\t\t\t");
    if ($interest_id > 0) {
      print ("Passed\t$interest_id\n");
    } else {
      print ("Failed<--\t$interest_id\n");
    }

    print ("interest_weight\t\t\t");
    if ($interest_weight == 127) {
      print ("Passed\t$interest_weight\n");
    } else {
      print ("Failed<--\t$interest_weight\n");
    }

    print ("is_internal\t\t\t");
    if ($is_internal <= 1) {
      print ("Passed\t$is_internal\n");
    } else {
      print ("Failed<--\t$is_internal\n");
    }

    print ("request_time_millisecond\t");
    if ($request_time_millisecond > 1208450000000) {
      print ("Passed\t$request_time_millisecond\n");
    } else {
      print ("Failed<--\t$request_time_millisecond\n");
    }

    print ("domain_id column\t\tTBD\n");


    print ("site_tree_category_id column\t");
    print ("TBD\n");
    #    if ($site_tree_category_id >= 1) {
    #      print ("Passed\t$site_tree_category_id\n");
    #    } else {
    #      print ("Failed<--\t$site_tree_category_id\n");
    #    }

    print ("parent_qualifier_id column\t");
    print ("TBD\n");
    #    if ($parent_qualifier_id >= 1) {
    #      print ("Passed\t$parent_qualifier_id\n");
    #    } else {
    #      print ("Failed<--\t$parent_qualifier_id\n");
    #    }

    print ("qualifier_group_id column\tTBD\n");
  }
  $sth1->finish();

  #nc_user_session table

  print ("****************************************\nVerifying nc_user_session table\tOK\n");

  $sth1 = $dbh1->prepare("SELECT id, request_time_millisecond, user_page_visit_guid, connection_id, certer_id, user_id, raw_user_identifier, user_hipplus_identifier, super_session_identifier, user_agent, user_cookie_identifier, cookies, host, set_cookies, od_cookie_domain_id, od_cookie_identifier FROM nc_user_session order by id desc limit 1");
  $sth1->execute();
  while (my ( $id, $request_time_millisecond_us, $guid_us, $connection_id_us, $certer_id_us, $user_id_us, $raw_user_identifier, $user_hipplus_identifier, $super_session_identifier, $user_agent, $user_cookie_id, $cookies, $host, $set_cookies, $od_cookie_domain_id, $od_cookie_identifier) = $sth1->fetchrow()) {

    print ("id column\t\t\t");
    if ($id > 1) {
      print ("Passed\t$id\n");
    } else {
      print ("Failed<--\t$id\n");
    }

    print ("request_time_millisecond\t");
    if ($request_time_millisecond_us > 1208450000000) {
      print ("Passed\t$request_time_millisecond_us\n");
    } else {
      print ("Failed<--\t$request_time_millisecond_us\n");
    }

    print ("user_page_visit_guid\t\t");
    if ($guid_us =~ m/\d/g) {
      print ("Passed\t$guid_us\n");
    } else {
      print ("Failed<--\t$guid_us\n");
    }

    print ("connection_id column\t\t");
    if ($connection_id_us =~ m/\d+/) {
      print ("Passed\t$connection_id_us\n");
    } else {
      print ("Failed<--\t$connection_id_us\n");
    }

    print ("certer_id\t\t\t");
    if ($certer_id_us > 0) {
      print ("Passed\t$certer_id_us\n");
    } else {
      print ("Failed<--\t$certer_id_us\n");
    }

    print ("user_id column\t\t\t");
    if ($user_id_us =~ m/\d+/) {
      print ("Passed\t$user_id_us\n");
    } else {
      print ("Failed<--\t$user_id_us\n");
    }

    print ("raw_user_identifier\t\t");
    if ($raw_user_identifier =~ m/\d+/) {
      print ("Passed\t$raw_user_identifier\n");
    } else {
      print ("Failed<--\t$raw_user_identifier\n");
    }


    print ("user_hipplus_identifier\t\t");
    if ($user_hipplus_identifier =~ m/\d+/) {
      print ("Passed\t$user_hipplus_identifier\n");
    } else {
      print ("Failed<--\t$user_hipplus_identifier\n");
    }

    print ("super_session_identifier\t");
    if ($super_session_identifier =~ m/\d+/) {
      print ("Passed\t$super_session_identifier\n");
    } else {
      print ("Failed<--\t$super_session_identifier\n");
    }

    print ("user_agent\t\t\t");
    if ($user_agent =~ m/Windows/g) {
      print ("Passed\t$user_agent\n");
    } else {
      print ("Failed<--\t$user_agent\n");
    }

    print ("user_cookie_id\t\t\t");
    if ($user_cookie_id =~ m/\d+/g) {
      print ("Passed\t$user_cookie_id\n");
    } else {
      print ("Failed<--\t$user_cookie_id\n");
    }

    my $idtostartwatching = $id - 300;
    my $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_session where host like '%google%' and set_cookies is not null and id > $idtostartwatching");
    $sth2->execute();
    while (my($setcookieyesno) = $sth2->fetchrow()) {
      if ($setcookieyesno > 0) {
	print ("set_cookies\t\t\tPassed\n");
      } else {
	print ("set_cookies\t\t\tFailed<--\n");
      }
    }
    $sth2->finish();

    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_session where host like '%google%' and cookies is not null and id > $idtostartwatching");
    $sth2->execute();
    while (my($cookieyesno) = $sth2->fetchrow()) {
      if ($cookieyesno > 0) {
	print ("cookies\t\t\t\tPassed\n");
      } else {
	print ("cookies\t\t\t\tFailed<--\n");
      }
    }
    $sth2->finish();

    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_session where od_cookie_domain_id is not null and id > $idtostartwatching");
    $sth2->execute();
    while (my($odcookiedomain) = $sth2->fetchrow()) {
      if ($odcookiedomain > 0) {
	print ("od_cookie_domain_id\t\tPassed\n");
      } else {
	print ("od_cookie_domain_id\t\tFailed<--\n");
      }
    }
    $sth2->finish();


    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_session where od_cookie_identifier is not null and id > $idtostartwatching");
    $sth2->execute();
    while (my($odcookieidentifier) = $sth2->fetchrow()) {
      if ($odcookieidentifier > 0) {
	print ("od_cookie_identifier\t\tPassed\n");
      } else {
	print ("od_cookie_identifier\t\tFailed<--\n");
      }
    }
    $sth2->finish();


  }
  $sth1->finish();



  #usermodeler_event_log table

  print ("****************************************\nVerifying usermodeler_event_log table\tOK\n");


  $sth1 = $dbh1->prepare("SELECT created_date_time, request_time_millis, certer_id, user_hipplus_id, user_adserver_id, user_page_visit_guid, user_interest, event_handling_time_micros, event_processed FROM usermodeler_event_log order by id desc limit 1");
  $sth1->execute();
  while (my ( $created_date_time3, $request_time_millisecond3, $certer_id_ue, $user_hipplus_id, $user_adserver_id, $user_page_visit_guid3, $user_interest, $event_handling_time_micros, $event_processed) = $sth1->fetchrow()) {

    print ("created_date_time\t\t");
    if ($created_date_time3 =~ m/2008/g) {
      print ("Passed\t$created_date_time3\n");
    } else {
      print ("Failed<--\t$created_date_time3\n");
    }

    print ("request_time_millisecond\t");
    if ($request_time_millisecond3 > 1208450000000) {
      print ("Passed\t$request_time_millisecond3\n");
    } else {
      print ("Failed<--\t$request_time_millisecond3\n");
    }

    print ("certer_id\t\t\t");
    if ($certer_id_ue > 0) {
      print ("Passed\t$certer_id_ue\n");
    } else {
      print ("Failed<--\t$certer_id_ue\n");
    }

    print ("userhipplusid\t\t\t");
    if ($user_hipplus_id > 0) {
      print ("Passed\t$user_hipplus_id\n");
    } else {
      print ("Failed<--\t$user_hipplus_id\n");
    }

    print ("useradserverid\t\t\t");
    if ($user_adserver_id > 0) {
      print ("Passed\t$user_adserver_id\n");
    } else {
      print ("Failed<--\t$user_adserver_id\n");
    }

    print ("user_page_visit_guid\t\t");
    if ($user_page_visit_guid3 =~ m/\d/g) {
      print ("Passed\t$user_page_visit_guid3\n");
    } else {
      print ("Failed<--\t$user_page_visit_guid3\n");
    }

    print ("user_interest\t\t\tTBD\n");

    print ("event_handling_time_micros\t");
    if ($event_handling_time_micros =~ m/\d/g) {
      print ("Passed\t$event_handling_time_micros\n");
    } else {
      print ("Failed<--\t");
    }

    print ("event_processed\t\t\t");
    if ($event_processed =~ m/\d/g) {
      print ("Passed\t$event_processed\n");
    } else {
      print ("Failed<--\t");
    }
  }
  $sth1->finish();

  #usermodeler_request_log table
  print ("****************************************\nVerifying usermodeler_request_log table\tOK\n");


  $sth1 = $dbh1->prepare("SELECT created_date_time, action, ok, certer_id, user_hipplus_id, user_adserver_id, guid, aux_categories, categories, request_handling_time_micros, interest_identifier FROM usermodeler_request_log order by id desc limit 1");
  $sth1->execute();
  while (my ( $created_date_time, $action, $ok, $certer_id, $user_hipplus_identifier, $user_adserver_id, $guid, $aux_categories, $categories, $request_handling_time_micros, $interest_identifier) = $sth1->fetchrow()) {
    print ("created_date_time\t\t");
    if ($created_date_time =~ m/2008/g) {
      print ("Passed\t$created_date_time\n");
    } else {
      print ("Failed<--\t$created_date_time\n");
    }

    print ("action column\t\t\t");
    if ($action =~ m/[A-Z]/) {
      print ("Passed\t$action\n");
    } else {
      print ("Failed<--\t$action\n");
    }


    print ("ok column\t\t\t");
    if ($ok <= 1) {
      print ("Passed\t$ok\n");
    } else {
      print ("Failed<--\t$ok\n");
    }

    print ("certer_id column\t\t");
    if ($certer_id =~ m/\d+/) {
      print ("Passed\t$certer_id\n");
    } else {
      print ("Failed<--\t$certer_id\n");
    }

    print ("user_hipplus_identifier\t\t");
    if (($user_hipplus_identifier =~ m/\d+/) || ($user_hipplus_identifier =~ m/none/)) {
      print ("Passed\t$user_hipplus_identifier\n");
    } else {
      print ("Failed<--\t$user_hipplus_identifier\n");
    }

    print ("useradserverid\t\t\t");
    if ($user_adserver_id > 0) {
      print ("Passed\t$user_adserver_id\n");
    } else {
      print ("Failed<--\t$user_adserver_id\n");
    }

    print ("guid\t\t\t\tTBD\n");
    print ("aux_categories\t\t\tTBD\n");
    print ("categories\t\t\tTBD\n");

    print ("request_handling_time_micros\t");
    if ($request_handling_time_micros =~ m/\d/g) {
      print ("Passed\t$request_handling_time_micros\n");
    } else {
      print ("Failed<--\t");
    }

    print ("interest_identifier\t\tTBD\n");

  }
  $sth1->finish();


  #yieldmanager_request_log table

  print ("****************************************\nyieldmanager_request_log\tOK\n");

  $sth1 = $dbh1->prepare("SELECT created_date_time, ok, user_identifier, user_cookie_identifier, spot_id, guid, request_handling_time_millis FROM yieldmanager_request_log order by id desc limit 1");
  $sth1->execute();
  while (my ($created_date_time, $ok, $user_identifier, $user_cookie_identifier, $spot_id, $guid, $request_handling_time_millis) = $sth1->fetchrow()) {
    print ("created_date_time\t\t");
    if ($created_date_time =~ m/2008/g) {
      print ("Passed\t$created_date_time\n");
    } else {
      print ("Failed<--\t$created_date_time\n");
    }

    print ("ok column\t\t\t");
    if ($ok <= 1) {
      print ("Passed\t$ok\n");
    } else {
      print ("Failed<--\t$ok\n");
    }

    print ("user_identifier\t\t\t");
    if ($user_identifier =~ m/\d+/) {
      print ("Passed\t$user_identifier\n");
    } else {
      print ("Failed<--\t$user_identifier\n");
    }

    print ("user_cookie_identifier\t\t");
    if ($user_cookie_identifier =~ m/\d+/g) {
      print ("Passed\t$user_cookie_identifier\n");
    } else {
      print ("Failed<--\t$user_cookie_identifier\n");
    }

    print ("spot_id column\t\t\t");
    if ($spot_id >= 1) {
      print ("Passed\t$spot_id\n");
    } else {
      print ("Failed<--\t$spot_id\n");
    }

    print ("guid\t\t\t\t");
    if ($guid =~ m/\d/g) {
      print ("Passed\t$guid\n");
    } else {
      print ("Failed<--\t$guid\n");
    }

    print ("request_handling_time_millis\t");
    if ($request_handling_time_millis =~ m/\d/) {
      print ("Passed\t$request_handling_time_millis\n");
    } else {
      print ("Failed<--\t$request_handling_time_millis\n");
    }

  }
  $sth1->finish();
}

sub oracledataverification {

  print ("****************************************\nverify nc_user table\t\tOK\n");

  my $sth4 = $dbh4->prepare("select user_id, certer_id, user_ip_address, hip_ua, ad_server_cookie, created_date_time, is_opt_out, is_fine, merged_with, modified_date_time FROM nc_user order by created_date_time desc");
  $sth4->execute();
  while (my ($user_id, $certer_id, $user_ip_address, $hip_ua, $ad_server_cookie, $created_date_time, $is_opt_out, $is_fine, $merged_with, $modified_date_time) = $sth4->fetchrow()) {

    print ("user_id column\t\t\t");
    if ($user_id =~ m/\d+/) {
      print ("Passed\t$user_id\n");
    } else {
      print ("Failed<--\t$user_id\n");
    }

    print ("certer_id\t\t\t");
    if ($certer_id > 0) {
      print ("Passed\t$certer_id\n");
    } else {
      print ("Failed<--\t$certer_id\n");
    }

    print ("user_ip_address\t\t\t");
    if ($user_ip_address > 0) {
      print ("Passed\t$user_ip_address\n");
    } else {
      print ("Failed<--\t$user_ip_address\n");
    }

    print ("hip_ua\t\t\t\t");
    if ($hip_ua =~ m/\d/) {
      print ("Passed\t$hip_ua\n");
    } else {
      print ("Failed<--\t$hip_ua\n");
    }

    print ("ad_server_cookie\t\tTBD\n");

    print ("created_date_time\t\t");
    if ($created_date_time =~ m/-08/g) {
      print ("Passed\t$created_date_time\n");
    } else {
      print ("Failed<--\t$created_date_time\n");
    }

    print ("is_opt_out\t\t\t");
    if ($is_opt_out =~ m/[A-Z]/) {
      print ("Passed\t$is_opt_out\n");
    } else {
      print ("Failed<--\t$is_opt_out\n");
    }

    print ("is_fine\t\t\t\t");
    if ($is_fine =~ m/[A-Z]/) {
      print ("Passed\t$is_fine\n");
    } else {
      print ("Failed<--\t$is_fine\n");
    }

    print ("merged_with\t\t\tTBD\n");
    print ("modified_date_time\t\t");
    if ($modified_date_time =~ m/-08/g) {
      print ("Passed\t$modified_date_time\n");
    } else {
      print ("Failed<--\t$modified_date_time\n");
    }
    last;
  }
  $sth4->finish();


  ###nc_user_model table
  print ("****************************************\nVerifying nc_user_model\t\tOK\n");


  $sth4 = $dbh4->prepare("select user_model_id, user_identifier, user_identifier_type, created_date_time, event_count, modified_date_time FROM nc_user_model order by created_date_time desc");
  $sth4->execute();
  while (my ($user_model_id, $user_identifier, $user_identifier_type, $created_date_time, $event_count, $modified_date_time) = $sth4->fetchrow()) {

    print ("user_model_id column\t\t");
    if ($user_model_id =~ m/\d+/) {
      print ("Passed\t$user_model_id\n");
    } else {
      print ("Failed<--\t$user_model_id\n");
    }

    print ("user_identifier\t\t\t");
    if ($user_identifier =~ m/\d/) {
      print ("Passed\t$user_identifier\n");
    } else {
      print ("Failed<--\t$user_identifier\n");
    }

    print ("user_identifier_type\t\t");
    if ($user_identifier_type =~ m/[A-Z]/) {
      print ("Passed\t$user_identifier_type\n");
    } else {
      print ("Failed<--\t$user_identifier_type\n");
    }

    print ("created_date_time\t\t");
    if ($created_date_time =~ m/-08/g) {
      print ("Passed\t$created_date_time\n");
    } else {
      print ("Failed<--\t$created_date_time\n");
    }

    print ("event_count\t\t\t");
    if ($event_count =~ m/\d/g) {
      print ("Passed\t$event_count\n");
    } else {
      print ("Failed<--\t$event_count\n");
    }

    print ("modified_date_time\t\t");
    if ($modified_date_time =~ m/-08/g) {
      print ("Passed\t$modified_date_time\n");
    } else {
      print ("Failed<--\t$modified_date_time\n");
    }
    last;
  }
  $sth4->finish();
}




sub getinitialmeasurements {

  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.google.com",
			      );
  $sel->start();

  print ("clearing my prev nc_user info\t");
  #my $url = "http://10.50.150.150:8080/usermodeler/getcategories?p";
  my $url = "http://"."$umserver".":8080/usermodeler/getcategories?p";


  #print ("getting hipid\t\t\t");
  $sel->open("$url");
  sleep (1);
  my @source2 =  $sel->get_html_source();
  sleep (1);
  my $line;
  my $hipid;

  foreach $line(@source2) {
    #if ($line =~  m/HIP\+:\s\d+.+\s$/g) {
    if ($line =~  m/HIP\+\sId\:\s\d+\w+/g) {
      $hipid = $& ;
      $hipid =~ s/HIP\+\sId\://;
      $hipid =~ s/ //;
      #print ("hipid is $hipid\n");
    }
  }

  if ($hipid eq "") {
    print ("couldn't get the hipid\n");
    die;
  }

  #  my $sth4 = $dbh4->prepare("select ad_server_cookie FROM nc_user where hip_ua like '$hipid'");
  #  $sth4->execute();
  #  my $adservercookie = $sth4->fetchrow();
  #  $sth4->finish();

  #  my $userid;
  #  if ($adservercookie) {
  #    $sth4 = $dbh4->prepare("select user_id FROM nc_user where hip_ua like '$hipid' or hip_ua like '$adservercookie'");
  #    $sth4->execute();
  #    while (($userid) = $sth4->fetchrow()) {
  #      my $sth5 = $dbh5->prepare("delete FROM nc_user_odc where user_id like '$userid'");
  #      $sth5->execute();
  #      $sth5->finish();
  #      #print ("$userid\n");
  #    }
  #    $sth4->finish();
  #  }



  #  my $sth4 = $dbh4->prepare("delete FROM nc_user where hip_ua like '$hipid'");
  #  $sth4->execute();
  #  $sth4->finish();

  #  $sth4 = $dbh4->prepare("delete FROM nc_user_model where user_identifier like '$hipid'");
  #  $sth4->execute();
  #  $sth4->finish();

  #  $sth4 = $dbh4->prepare("delete FROM nc_user_model where user_identifier like '$adservercookie'");
  #  $sth4->execute();
  #  $sth4->finish();



  print ("OK\n");



  print ("getting initial measurements\t");
  my $sth1 = $dbh1->prepare("SELECT count(*) FROM content_write_log");
  $sth1->execute();
  $contentwritelogcount = $sth1->fetchrow();
  #print ("$contentwritelogcount\n");
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_qualifier_group_user_visit");
  $sth1->execute();
  my $ncqualifiergroupuservisitcount = $sth1->fetchrow();
  #print ("$ncqualifiergroupuservisitcount\n");
  $sth1->finish();


  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_qualifier_user_visit");
  $sth1->execute();
  $ncqualifieruservisitcount = $sth1->fetchrow();
  #print ("$ncqualifieruservisitcount\n");
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_user_session");
  $sth1->execute();
  $ncusersessioncount = $sth1->fetchrow();
  #print ("$ncusersessioncount\n");
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM usermodeler_event_log");
  $sth1->execute();
  $usermodelereventlogcount = $sth1->fetchrow();
  #print ("$usermodelereventlogcount\n");
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM usermodeler_request_log");
  $sth1->execute();
  $usermodelerrequestlogcount = $sth1->fetchrow();
  #print ("$usermodelerrequestlogcount\n");
  $sth1->finish();


  $sth1 = $dbh1->prepare("SELECT count(*) FROM yieldmanager_request_log");
  $sth1->execute();
  $yieldmanagerrequestlogcount = $sth1->fetchrow();
  #print ("$yieldmanagerrequestlogcount\n");
  $sth1->finish();

  my $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user");
  $sth4->execute();
  $ncusercount = $sth4->fetchrow();
  #print ("$ncusercount\n");
  $sth4->finish();

  $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user_model");
  $sth4->execute();
  $ncusermodelcount = $sth4->fetchrow();
  #print ("$ncusermodelcount\n");
  $sth4->finish();

  $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user_odc");
  $sth4->execute();
  $ncuserodccount = $sth4->fetchrow();
  #print ("$ncuserodccount\n");
  $sth4->finish();


  $sel->stop();
  print ("OK\n");
}

sub getfinalmeasurements {
  print ("getting final measurements\t");
  print ("OK\n");

  my $sth1 = $dbh1->prepare("SELECT count(*) FROM content_write_log");
  $sth1->execute();
  my $contentwritelogcount2 = $sth1->fetchrow();
  if ( $contentwritelogcount2 <= $contentwritelogcount) {
    print ("Content Writer db growth\tFailed\n");
  } else {
    print ("content write log growth\tOK\n");
  }
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_qualifier_group_user_visit");
  $sth1->execute();
  my $ncqualifiergroupuservisitcount2 = $sth1->fetchrow();
  if ( $ncqualifiergroupuservisitcount2 <= $ncqualifiergroupuservisitcount) {
    print ("nc_qualifier_group_user_visit db growth\tFailed\n");
  } else {
    print ("nc qualifier group user visit growth\tOK\n");
  }
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_qualifier_user_visit");
  $sth1->execute();
  my $ncqualifieruservisitcount2 = $sth1->fetchrow();
  if ( $ncqualifieruservisitcount2 <= $ncqualifieruservisitcount) {
    print ("nc_qualifier_user_visit db growth\tFailed\n");
  } else {
    print ("nc qualifier uservisit growth\tOK\n");
  }
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_user_session");
  $sth1->execute();
  my $ncusersessioncount2 = $sth1->fetchrow();
  if ( $ncusersessioncount2 <= $ncusersessioncount) {
    print ("nc_user_session db growth\t\tFailed\n");
  } else {
    print ("nc user session growth\t\tOK\n");
  }
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM usermodeler_event_log");
  $sth1->execute();
  my $usermodelereventlogcount2 = $sth1->fetchrow();
  if ( $usermodelereventlogcount2 <= $usermodelereventlogcount) {
    print ("usermodeler_event_log db growth\t\tFailed\n");
  } else {
    print ("usermodeler event log growth\tOK\n");
  }
  $sth1->finish();

  $sth1 = $dbh1->prepare("SELECT count(*) FROM usermodeler_request_log");
  $sth1->execute();
  my $usermodelerrequestlogcount2 = $sth1->fetchrow();
  if ( $usermodelerrequestlogcount2 <= $usermodelerrequestlogcount) {
    print ("usermodeler_request_log db growth\tFailed\n");
  } else {
    print ("usermodeler request log growth\tOK\n");
  }
  $sth1->finish();


  $sth1 = $dbh1->prepare("SELECT count(*) FROM yieldmanager_request_log");
  $sth1->execute();
  my $yieldmanagerrequestlogcount2 = $sth1->fetchrow();
  if ( $yieldmanagerrequestlogcount2 <= $yieldmanagerrequestlogcount) {
    print ("yieldmanager_request_log db growth\tFailed. If YM is turned on, this would be a bug.\n");
  } else {
    print ("yieldmanager request log growth\tOK\n");
  }
  $sth1->finish();

  my $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user");
  $sth4->execute();
  my $ncusercount2 = $sth4->fetchrow();
  if ( $ncusercount2 <= $ncusercount) {
    print ("nc_user db growth\t\t\tFailed\n");
  } else {
    print ("nc user growth\t\t\tOK\n");
  }
  $sth4->finish();


  #checking nc_user_model db growth right here
  $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user_model");
  $sth4->execute();
  my $ncusermodelcount2 = $sth4->fetchrow();
  if ( $ncusermodelcount2 <= $ncusermodelcount) {
    print ("nc_user_model db growth\t\tFailed. Remove all cookies and retry test if not already done so.\n");
  } else {
    print ("nc usermodel growth\t\tOK\n");
  }
  $sth4->finish();

  $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user_odc");
  $sth4->execute();
  my $ncuserodccount2 = $sth4->fetchrow();
  if ( $ncuserodccount2 <= $ncuserodccount) {
    print ("nc_user_odc db growth\t\tFailed\n");
  } else {
    print ("nc user odc growth\t\tOK\n");
  }
  $sth4->finish();



}

sub supersessiontest {
  my $testresult;
  my $line;
  my $hipid;
  my $userid;
  my $i = 0;
  while ($i < 2) {
    my $sel = WWW::Selenium->new( host => "localhost", 
				  port => 4444, 
				  browser => "*iehta", 
				  browser_url => "http://www.google.com",
				);
    $sel->start();
    my $url = "http://www.google.com";
    my $j = 0;
    if ($i == 0) {
      while ($j < 10) {
	$sel->open("$url"); 
	$sel->wait_for_page_to_load("30000");
	sleep (1);
	$j++;
      }
    }

    if ($i == 0) {
      $url = "http://www.inkandtoner.net";
    } else {
      $url = "http://www.cars.com";
    }

    $j = 0;
    while ($j < 10) {
      #print ("$j\n");
      $sel->open("$url"); 
      $sel->wait_for_page_to_load("30000");
      sleep (1);
      $j++;
      if ($j == 9) {
	my $line;
	my @checkinjection =  $sel->get_html_source();
	sleep (1);
	foreach $line(@checkinjection) {
	  if (($line =~  m/t=s/g) && ($line =~  m/qacluster1/g)) {
	    print ("Got t=s injection, stopping, should switch to whitelist...\n");
	    die;
	  }
	}
      }
    }

    my $sth1 = $dbh1->prepare("SELECT user_adserver_id FROM logdb.usermodeler_event_log where user_adserver_id > 0 order by id desc limit 1");
    $sth1->execute();
    my $cu = $sth1->fetchrow();

    $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";

    $sel->open("$url");
    $sel->wait_for_page_to_load("30000");
    sleep (3);

    if ($i == 0) {
      if ($sel->is_text_present("Ink")) {
	$testresult = 'Passed';
      } else {
	my $ssidchanged = &checkifsupersessionchanged(1);
	if ($ssidchanged == 1) {
	  $testresult = 'SSID changed by the Certer during test, redo test<--';
	} else {
	  $testresult = 'Failed<--';
	}
      }
      print ("SuperSession 1st Test\t\t$testresult\n");
      sleep (1);
    } else {
      if ($sel->is_text_present("Auto")) {
	$testresult = 'Passed';
      } else {
	my $ssidchanged = &checkifsupersessionchanged(2);
	if ($ssidchanged == 1) {
	  $testresult = 'SSID changed by the Certer during test, redo test<--';
	} else {
	  $testresult = 'Failed<--';
	}
      }
      print ("SuperSession 2nd Test\t\t$testresult\n");
      sleep (1);
    }

    ####

    #$url = "http://10.50.150.150:8080/usermodeler/getcategories?p";
    $url = "http://"."$umserver".":8080/usermodeler/getcategories?p";
    print ("getting hipid\t\t\t");
    $sel->open("$url");
    sleep (1);
    my @source2 =  $sel->get_html_source();
    sleep (1);
    print ("OK\n");
    foreach $line(@source2) {
      #if ($line =~  m/HIP\+:\s\d+.+\s$/g) {
      if ($line =~  m/HIP\+\sId\:\s\d+\w+/g) {
	$hipid = $& ;
	$hipid =~ s/HIP\+\sId\://;
	$hipid =~ s/ //;
	print ("hipid is $hipid\n");
      }
    }

    if ($hipid eq "") {
      print ("couldn't get the hipid\n");
      die;
    }

    #$url = "http://10.50.150.150:8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";
    $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";

    $sel->open("$url");
    $sel->wait_for_page_to_load("30000");
    #print ("$url\n");
    sleep (5);
    if ($i == 0) {
      if ($sel->is_text_present("Ink")) {
	$testresult = 'Passed';
      } else {
	$testresult = 'Failed<--';
      }
      print ("Coarse has same interests\t$testresult\n");
      sleep (10);
    } elsif ($i == 1) {
      if ($sel->is_text_present("Auto")) {
	$testresult = 'Passed';
      } else {
	$testresult = 'Failed<--';
      }
      print ("Coarse has same interests\t$testresult\n");
      sleep (10);
    }
    ####

    print ("resetting fine memory\t\t");
    $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";

    $sel->open("$url");
    $sel->wait_for_page_to_load("30000");
    sleep (1);
    print ("OK\n");
    $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
    $sel->open("$url");
    $sel->wait_for_page_to_load("30000");
    #print ("$url\n");
    sleep (1);
    if ($sel->is_text_present("Auto")) {
      $testresult = 'Failed<--';
    } elsif ($sel->is_text_present("Ink")) {
      $testresult = 'Failed<--';
    } else {
      $testresult = 'Passed';
    }
    print ("UserModeler fine Memory reset\t$testresult\n");
    sleep (1);
    $sel->stop();

    $i++;
  }

}


sub checkifsupersessionchanged {
  my $key = shift;

  my $sth1 = $dbh1->prepare("select super_session_identifier from nc_user_session where host like 'www.google.com' order by id desc limit 1;");
  $sth1->execute();
  my $ssid1 = $sth1->fetchrow();
  $sth1->finish();

  $sth1 = $dbh1->prepare("select super_session_identifier from nc_user_session where host like 'www.inkandtoner.com' order by id desc limit 1;");
  $sth1->execute();
  my $ssid2 = $sth1->fetchrow();
  $sth1->finish();

  $sth1 = $dbh1->prepare("select super_session_identifier from nc_user_session where host like 'www.cars.com' order by id desc limit 1;");
  $sth1->execute();
  my $ssid3 = $sth1->fetchrow();
  $sth1->finish();

  if ($key == 1) {
    unless ($ssid1 == $ssid2) {
      return 1;
    }
  } elsif ($key == 2) {
    unless ($ssid1 == $ssid3) {
      return 1;
    }
  }
  return 0;
}




sub verifysettingsinnccertertable {
  print ("Verifying Certer settings\t");
  my $sth4 = $dbh4->prepare("select certer_id, use_partner_spot, interest_multiplier, is_research from nc_certer where certer_identifier like '$certername'");
  $sth4->execute();
  my($certer_id, $use_partner_spot, $interest_multiplier, $is_research) = $sth4->fetchrow();
  unless ( ($use_partner_spot eq 'Y') && ($interest_multiplier eq '1') && ($is_research eq 'Y') ) {
    print ("correct the nc_certer table before continuing.  Set use_partner_spot to Y and interest_multiplier to 1, and is_research to Y\n");
    die;
  }
  #print ("$certer_id, $use_partner_spot, $interest_multiplier, $is_research\n");
  sleep (2);
  print ("OK\n");
}





sub fetchcookieidfromfile {
  my $data_file="C:\\errtest.txt";
  open(DAT, $data_file) || die("Could not open file!");
  my @raw_data=<DAT>;
  close(DAT);
  return @raw_data;
}


sub getcookieid {
  my @lines = &fetchcookieidfromfile();
  my $size = @lines;
  my $line;
  my $cookieid;
  #my $count = 0;
  my $counttp = 0;
  foreach $line(@lines) {
    if ($line =~ m/ID=\w+:/g) {
      $cookieid = $&;
      $cookieid =~ s/://;
      return $cookieid;
    }
  }
}

sub getamazoncookie {
  my @lines = &fetchcookieidfromfile();
  my $size = @lines;
  my $line;
  my $cookieid;
  #my $count = 0;
  my $counttp = 0;
  foreach $line(@lines) {
    if ($line =~ m/ubid-main=\d+-\d+-\d+/g) {
      $cookieid = $&;
      #$cookieid =~ s/://;
      return $cookieid;
    }
  }
}



sub getasecookieid {
  my @lines = &fetchcookieidfromfile();
  my $size = @lines;
  my $line;
  my $cookieid;
  my $counttp = 0;
  foreach $line(@lines) {
    if ($line =~ m/u => \d+/g) {
      $cookieid = $&;
      $cookieid =~ s/://;
      $cookieid =~ s/u => //;
      return $cookieid;
    }
  }
}


sub checkcerterdoesadmods {
  my $testresult;
  print ("Certer Ad Mods logged\t\t");
  #my $sth1 = $dbh1->prepare("SELECT ad_type FROM adserver.request_log where certer_identifier='$certername' order by id desc limit 350");
  my $sth1 = $dbh1->prepare("SELECT ad_type FROM logdb.adserver_request_log where certer_identifier='$certername' order by id desc limit 3500");
  $sth1->execute();
  while (my($adtype) = $sth1->fetchrow()) {
    if ($adtype eq "p") {
      $testresult = 'Passed';
      #print ("$testresult\n");
      last;
    }
    $testresult = 'Failed<--';
  }
  print ("$testresult\n");
  $sth1->finish();
  sleep (3);
}




sub sanitycheckcertertraffic  {
  my $testresult;
  my $sth1 = $dbh1->prepare("select max(user_page_visit_id) from logdb.nc_user_page_visit");
  $sth1->execute();
  my $maxpagevisitid =  $sth1->fetchrow();
  $sth1->finish();
  my $maxpagevisitidtouse = $maxpagevisitid - 5000;

  $sth1 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where injection_type = 1 and content_length > 0 and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
  $sth1->execute();
  my $count =  $sth1->fetchrow();
  $sth1->finish();
  print ("injection_type1,content_length0\t");
  if ($count > 0) {
    $testresult = 'Failed<--';
  } else {
    $testresult = 'Passed';
  }
  print ("$testresult\n");




  print ("any missing content\t\t");
  $sth1 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where page_status = 200 and user_page_visit_id > '$maxpagevisitidtouse' and injection_type = 0 and content_length = 0 and url not like '%track.searchignite.com%' and url not like '%qaas1.nebuad.com%' and url not like '%ad.yieldmanager%' and url not like '%toolbarqueries%' and url not like '%sb.google.com%'  and url not like '%abc.nebuad.com%' ");
  $sth1->execute();
  my $count4 =  $sth1->fetchrow();
  if ($count4 > 0) {
    $testresult = 'Failed<--';
    print ("$testresult\n");
    my $sth2 = $dbh2->prepare("SELECT url FROM logdb.nc_user_page_visit where page_status = 200 and user_page_visit_id > '$maxpagevisitidtouse' and injection_type = 0 and content_length = 0 and url not like '%qaas1.nebuad.com%' and url not like '%searchignite%' and  url not like '%favicon.ico%' and url not like '%ad.yieldmanager%' and url not like '%toolbarqueries%' and url not like '%sb.google.com%'  and url not like '%abc.nebuad.com%' ");
    $sth2->execute();
    print ("\nhere are the urls for missing contents:\n");
    while (my $url = $sth2->fetchrow()) {
      print ("$url\n");
    }
    print ("\n");
    $sth2->finish();
  } else {
    $testresult = 'Passed';
    print ("$testresult\n");
  }
  $sth1->finish();

  print ("Value of Is_Certer_User test1\t");
  $sth1 = $dbh1->prepare("SELECT count(*) FROM logdb.adserver_request_log where ((certer_identifier is NULL or certer_identifier like 'adserver1') and (is_certer_user = 1))");
  $sth1->execute();
  my $count5 =  $sth1->fetchrow();
  if ($count5 > 0) {
    $testresult = 'Failed<--';
    print ("$testresult\n");
  } else {
    $testresult = 'Passed';
    print ("$testresult\n");
  }
  $sth1->finish();

  print ("Value of Is_Certer_User test2\t");
  $sth1 = $dbh1->prepare("SELECT count(*) FROM logdb.adserver_request_log where ((certer_identifier is not NULL or certer_identifier not like 'adserver1') and (is_certer_user = 0))");
  $sth1->execute();
  my $count6 =  $sth1->fetchrow();
  if ($count6 > 0) {
    $testresult = 'Failed<--';
    print ("$testresult\n");
  } else {
    $testresult = 'Passed';
    print ("$testresult\n");
  }
  $sth1->finish();





}



sub checkfineprofile {
  my $testresult;
  my $line;
  my $hipid;
  my $userid;

  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.a101tech.com/qa/demo.html",
			      );
  $sel->start();

  print ("Go to Google\t\t\t");
  my $url = "http://www.google.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);


  print ("Get cookie u value\t\t");
  my $sth2 = $dbh3->prepare("SELECT cookie_user_id FROM logdb.adserver_request_log order by id desc limit 1;");
  #my $sth2 = $dbh3->prepare("SELECT user_cookie_identifier FROM logdb.usermodeler_request_log where user_cookie_identifier > 0 order by created_date_time desc limit 1;");
  $sth2->execute();
  my $cu =  $sth2->fetchrow();
  $sth2->finish();
  print ("$cu\n");
  unless ($cu =~ m/\d/g) {
    print ("Couldn't get cookie value\n");
    die;
  }
  sleep (5);


  print ("resetting fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";

  $sel->open("$url");
  sleep (10);
  print ("OK\n");


  #  $url = "http://www.freecreditreport.com/";
  #  print ("Go to free credit report page\t");
  #  $sel->open("$url"); 
  #  #$sel->click("link=exact:http://www.choicehotels.com/");
  #  $sel->wait_for_page_to_load("30000");
  #  print ("OK\n");
  #  sleep (10);

  #  $url = "http://www.freecreditreport.com/";
  #  print ("Go to free credit report page\t");
  #  $sel->open("$url"); 
  #  #$sel->click("link=exact:http://www.choicehotels.com/");
  #  $sel->wait_for_page_to_load("30000");
  #  print ("OK\n");
  #  sleep (10);

  print ("Go to Google\t\t\t");
  $url = "http://www.google.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for Credit Score\t\t");
  $sel->type("q", "Credit Score");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);



  print ("Go to Google\t\t\t");
  $url = "http://www.google.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for Fico\t\t\t");
  $sel->type("q", "Fico");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Credit Report\t");
  $sel->type("q", "Credit Report");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Equifax\t\t");
  $sel->type("q", "Equifax");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Fico\t\t\t");
  $sel->type("q", "Fico");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Credit Report\t");
  $sel->type("q", "Credit Report");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Equifax\t\t");
  $sel->type("q", "Equifax");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);


  print ("Get cookie u value\t\t");
  $sth2 = $dbh3->prepare("SELECT cookie_user_id FROM logdb.adserver_request_log order by id desc limit 1;");
  #my $sth2 = $dbh3->prepare("SELECT user_cookie_identifier FROM logdb.usermodeler_request_log where user_cookie_identifier > 0 order by created_date_time desc limit 1;");
  $sth2->execute();
  $cu =  $sth2->fetchrow();
  $sth2->finish();
  print ("$cu\n");
  unless ($cu =~ m/\d/g) {
    print ("Couldn't get cookie value\n");
    die;
  }
  sleep (5);



  #get user interest fine
  #$url = "http://10.50.150.150:8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  #print ("$url\n");
  sleep (5);
  if ($sel->is_text_present("Credit")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler fine Memory test\t$testresult\n");
  sleep (13);


  print ("resetting fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";

  $sel->open("$url");
  sleep (10);
  print ("OK\n");


  print ("verify no fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  if ($sel->is_text_present("Credit")) {
    $testresult = 'Failed<--';
  } else {
    $testresult = 'Passed';
  }
  print ("OK\n");
  print ("UserModeler fine Memory Init\t$testresult\n");
  sleep (10);

  #  print ("Go to lavalife\t\t\t");
  #  $url = "http://www.lavalife.com/";
  #  $sel->open("$url");
  #  sleep (10);
  #  print ("OK\n");

  #  print ("Go to eharmony\t\t\t");
  #  $url ="http://www.eharmony.com/";
  #  $sel->open("$url");
  #  sleep (10);
  #  print ("OK\n");

  print ("Go to Google\t\t\t");
  $url = "http://www.google.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for Date service\t\t");
  $sel->type("q", "Date service");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for Dating site\t\t");
  $sel->type("q", "Dating site");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for Date advice\t\t");
  $sel->type("q", "Date advice");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);


  print ("Search for Date service\t\t");
  $sel->type("q", "Date service");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for Dating site\t\t");
  $sel->type("q", "Dating site");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for Date advice\t\t");
  $sel->type("q", "Date advice");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);


  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  sleep (11);
  if ($sel->is_text_present("Personals")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler fine Memory After\t$testresult\n");

  print ("resetting fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";

  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  print ("Go to Google\t\t\t");
  $url = "http://www.google.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for Credit Score\t\t");
  $sel->type("q", "Credit Score");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);



  print ("Go to Google\t\t\t");
  $url = "http://www.google.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for Fico\t\t\t");
  $sel->type("q", "Fico");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Credit Report\t");
  $sel->type("q", "Credit Report");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Equifax\t\t");
  $sel->type("q", "Equifax");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Fico\t\t\t");
  $sel->type("q", "Fico");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Credit Report\t");
  $sel->type("q", "Credit Report");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);

  print ("Search for Equifax\t\t");
  $sel->type("q", "Equifax");
  $sel->click("btnG");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (13);


  print ("Get cookie u value\t\t");
  $sth2 = $dbh3->prepare("SELECT cookie_user_id FROM logdb.adserver_request_log order by id desc limit 1;");
  #my $sth2 = $dbh3->prepare("SELECT user_cookie_identifier FROM logdb.usermodeler_request_log where user_cookie_identifier > 0 order by created_date_time desc limit 1;");
  $sth2->execute();
  $cu =  $sth2->fetchrow();
  $sth2->finish();
  print ("$cu\n");
  unless ($cu =~ m/\d/g) {
    print ("Couldn't get cookie value\n");
    die;
  }
  sleep (5);



  #get user interest fine
  #$url = "http://10.50.150.150:8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  #print ("$url\n");
  sleep (5);
  if ($sel->is_text_present("Credit")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler fine Memory test\t$testresult\n");
  sleep (13);


  print ("resetting fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");


  print ("verify no fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  if ($sel->is_text_present("Credit")) {
    $testresult = 'Failed<--';
  } else {
    $testresult = 'Passed';
  }
  print ("OK\n");
  print ("UserModeler fine Memory Init\t$testresult\n");
  sleep (10);


  $sel->stop();
}







sub checkcoarseprofile {
  my $testresult;
  my $line;
  my $hipid;
  my $userid;

  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.a101tech.com/qa/demo.html",
			      );
  $sel->start();


  #getting $hipid now
  #my $url = "http://10.50.150.150:8080/usermodeler/getcategories?p";
  my $url = "http://"."$umserver".":8080/usermodeler/getcategories?p";

  print ("getting hipid\t\t\t");
  $sel->open("$url");
  sleep (1);
  my @source2 =  $sel->get_html_source();
  sleep (1);
  print ("OK\n");
  foreach $line(@source2) {
    #if ($line =~  m/HIP\+:\s\d+.+\s$/g) {
    if ($line =~  m/HIP\+\sId\:\s\d+\w+/g) {
      $hipid = $& ;
      $hipid =~ s/HIP\+\sId\://;
      $hipid =~ s/ //;
      print ("hipid is $hipid\n");
    }
  }

  if ($hipid eq "") {
    print ("couldn't get the hipid\n");
    die;
  }

  #  $url = "http://www.creditreport.com";
  #  print ("Go to credit report page\t");
  #  $sel->open("$url");
  #  print ("OK\n");
  #  sleep (10);


  $url = "http://www.freecreditreport.com/";
  print ("Go to free credit report page\t");
  $sel->open("$url"); 
  #$sel->click("link=exact:http://www.choicehotels.com/");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://www.freecreditreport.com/";
  print ("Go to free credit report page\t");
  $sel->open("$url"); 
  #$sel->click("link=exact:http://www.choicehotels.com/");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);


  print ("Go to Yahoo\t\t\t");
  $url = "http://www.yahoo.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for Credit Score\t\t");
  $sel->type("p", "Credit Score");
  $sel->click("searchsubmit");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for Fico\t\t\t");
  $sel->type("yschsp", "Fico");
  $sel->click("y");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for Credit Report\t");
  $sel->type("yschsp", "Credit Report");
  $sel->click("y");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for Equifax\t\t");
  $sel->type("yschsp", "Equifax");
  $sel->click("y");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);


  #get user interest coarse
  #$url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu";
  #$url = "http://10.50.150.150:8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  print ("$url\n");
  sleep (5);
  if ($sel->is_text_present("Credit")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler coarse Memory test\t$testresult\n");
  sleep (10);


  print ("resetting coarse memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&r=yes";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&r=yes";

  $sel->open("$url");
  sleep (10);
  print ("OK\n");


  print ("verify no coarse memory\t\t");
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  if ($sel->is_text_present("Credit")) {
    $testresult = 'Failed<--';
  } else {
    $testresult = 'Passed';
  }
  print ("OK\n");
  print ("UserModeler coarse Memory Init\t$testresult\n");
  sleep (10);

  #  print ("Go to cupid.com\t\t");
  #  $url ="http://www.cupid.com/";
  #  $sel->open("$url");
  #  sleep (10);
  #  print ("OK\n");

  print ("Go to lavalife\t\t\t");
  $url = "http://www.lavalife.com/";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  #  print ("Go to eharmony\t\t\t");
  #  $url ="http://www.eharmony.com/";
  #  $sel->open("$url");
  #  sleep (10);
  #  print ("OK\n");

  print ("Go to lavalife\t\t\t");
  $url = "http://www.lavalife.com/";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  print ("Go to Yahoo\t\t\t");
  $url = "http://www.yahoo.com";
  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search Yahoo Date service\t");
  $sel->type("p", "Date service");
  $sel->click("searchsubmit");
  $sel->wait_for_page_to_load("30000");
  sleep (10);
  print ("OK\n");

  print ("Search yahoo Dating site\t");
  $sel->type("yschsp", "Dating site");
  $sel->click("y");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search yahoo Date advice\t");
  $sel->type("yschsp", "Date advice");
  $sel->click("y");
  $sel->wait_for_page_to_load("50000");
  print ("OK\n");
  sleep (10);


  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  sleep (11);
  if ($sel->is_text_present("Personals")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler coarse Memory After\t$testresult\n");

  print ("resetting coarse memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&r=yes";
  #$url = "http://"."$umserver"."/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&r=yes";

  $sel->open("$url");
  sleep (10);
  print ("OK\n");
  $sel->stop();
}






sub checkpagestatusfromtables {
  #my $sth1 = $dbh1->prepare("SELECT page_status, count(*) FROM certer_qauta500.nc_user_page_visit group by page_status order by user_page_visit_id desc limit 5000");
  my $sth1 = $dbh1->prepare("SELECT page_status, count(*) FROM logdb.nc_user_page_visit group by page_status order by user_page_visit_id desc limit 5000");
  $sth1->execute();
  my $counter = 0;
  while (my($pagestatus, $count) = $sth1->fetchrow()) {
    if ($pagestatus == 200) {
      $counter++;
    }
    #    if ($pagestatus == 304) {
    #      $counter++;
    #    }
    #    if ($pagestatus == 302) {
    #      $counter++;
    #    }
  }
  if ($counter >= 1) {
    my $testresult = 'Passed';
    print ("PageStatus Mix\t\t\t$testresult\n");
  } else {
    my $testresult = 'Failed<--';
    print ("PageStatus Mix\t\t\t$testresult\n");
  }
  $sth1->finish();
}





sub checksomelargepages {
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $urlebay = "http://wwww.ebay.com";
  my $urlnextag = "http://wwww.nextag.com";
  $mech->get($urlebay);
  sleep(1);
  $mech->get($urlnextag);
  sleep(1);
  $mech->get($urlebay);
  sleep(1);
  $mech->get($urlnextag);
  sleep(1);
  $mech->get($urlebay);
  sleep(1);
  $mech->get($urlnextag);
  sleep(1);
  $mech->get($urlebay);
  sleep(1);
  $mech->get($urlnextag);
  sleep(1);
  my $urlforcontentlangtest = "http://travel.nextag.com/";
  $mech->get($urlforcontentlangtest);
  sleep(1);
  my $urlforcontentlocation = "http://www.sixapart.com/typepad/whyblog";
  $mech->get($urlforcontentlocation);
  sleep(1);
  my $urlforcontentencoding = "http://www.rhapsody.com/";
  $mech->get($urlforcontentencoding);
  sleep(1);
}





sub checkthatusermodelerisresponsive {
  my $testresult;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $url = "http://"."$umserver".":8080/usermodeler/getcategories?l";
  #my $url = "http://"."$umserver"."/usermodeler/getcategories?l";

  $mech->get($url);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/Education/g ) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler Alive\t\t$testresult\n");
  sleep (3);
}




sub checkthatadserverisresponsive {
  my $testresult;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  #my $url = "http://"."$adserver".":8080/jmx-console/HtmlAdaptor";
  #my $url = "http://"."$adserver".":8080/a?t=p";
  my $url = "http://"."$adserver"."/a?t=p";

  #print ("$url\n");
  $mech->get($url);
  my $fetchedpage = $mech->content();
  #if ($fetchedpage =~ m/AdServerVersion/g ) {
  if ($fetchedpage =~ m/t=f/g ) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("AdServer Alive\t\t\t$testresult\n");
  sleep (3);
}

sub checkcerterinjections {
  my $testresult;
  my $url = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url);
  my $fetchedpage = $mech->content();
  if (($fetchedpage =~ m/$adserver/g)) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--Did you VPN in?';
  }
  print ("Certer injections\t\t$testresult\n");
  sleep (3);
}



sub checkuserpagevisitstablegetsupdated {
  my $sth1 = $dbh1->prepare("SELECT user_page_visit_id, created_date_time, host, url, referer_url, page_status, content_length FROM logdb.nc_user_page_visit where url not like '%google%' and url not like '%abc.nebuad.com%' order by user_page_visit_id desc limit 1");
  $sth1->execute();
  my($user_page_visit_id, $created_date_time, $host, $url, $referrer_url, $page_status, $content_length) = $sth1->fetchrow();
  $sth1->finish();
  my $testresult;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $url0 = "$testpageurl";
  print ("PageVisit table updated\t\t");
  my $i = 0;
  while ($i < 10) {
    $mech->get($url0);
    #my $fetchedpage = $mech->content();
    sleep(5);
    #print (".");
    $i++;
  }
  sleep (20);
  $sth1 = $dbh1->prepare("SELECT user_page_visit_id, url FROM logdb.nc_user_page_visit where url not like '%abc.nebuad.com%' order by user_page_visit_id desc limit 10");
  $sth1->execute();
  while (my($user_page_visit_id1, $url1) = $sth1->fetchrow()) {
    #print ("$user_page_visit_id1\t$userid1\told id: $user_page_visit_id\n");
    if (($user_page_visit_id1 > $user_page_visit_id) && ($url1 =~ m/a101/g)) {
      $testresult = 'Passed';
      #print ("$testresult\n");
      last;
    }
    $testresult = 'Failed<--';
  }
  print ("$testresult\n");
  $sth1->finish();
  sleep (3);
}

sub footprintid {
  open STDERR, ">C:\\errtest.txt";
  my $testresult;
  my $line;
  my $cookieid;
  my $asecookie;
  my $hipid;
  my $userid;

  #  my $mech = WWW::Mechanize->new();
  #  $mech->agent_alias( 'Windows IE 6' );
  #  $mech->cookie_jar(HTTP::Cookies->new());
  #  my $url = "http://www.google.com";
  #  $mech->get($url);
  #  sleep(1);


  ##

  my $url = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  $mech->get($url);

  ##
  my $response = $mech->response();
  my $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  my $gc;
  if ( $header =~ m/PREF=ID=.+\:TM/) {
    $gc = $&;
    $gc =~ s/PREF=//;
    $gc =~ s/:TM//;
    #print ("Got google cookie: $gc\n");
  }

  ##
  $url="http://www.google.com/search?hl=en&q=cars&btnG=Google+Search";
  $mech->get($url);

  ##
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $url = "http://www.google.com";
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);
  $mech->get($url);
  sleep(1);


  my $fetchedpage = $mech->content();
  #$cookieid = &getcookieid();
  #$cookieid =~ s/ //;

  $cookieid = $gc;
  print ("Google Cookie Id:\t\t$cookieid\n");

  my $tsurl;
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  } else {
    print ("Failed to get t=s script\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();

  #$asecookie = &getasecookieid();
  #print ("ASE Cookie Id:\t\t\t$asecookie\n");

  my $tburl;
  #if ($fetchedpage =~ m/src=.http.+t=b.UTA/g) {
  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("Failed to get t=b script\n");
  }

  #$mech = WWW::Mechanize->new( autocheck => 1 );
  $mech->get( $tburl );
  $response = $mech->response();
  #  for my $key ( $response->header_field_names() ) {
  #    print $key, " : ", $response->header( $key ), "\n";
  #  }
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");

  my $u;
  if ( $header =~ m/o=0/) {
    print ("o cookie set to 0\t\tok\n");
  }
  if ( $header =~ m/w=\d+/) {
    my $w = $&;
    print ("w cookie is set\t\t\t$w\n");
  }
  if ( $header =~ m/u=\d+/) {
    $u = $&;
    print ("u cookie is set\t\t\t$u\n");
  }
  $asecookie = $u;
  print ("ASE Cookie Id:\t\t\t$asecookie\n");
  sleep (12);


  #  my $sth1 = $dbh1->prepare("SELECT user_page_visit_guid, user_id from nc_user_session where OD_COOKIE_IDENTIFIER like '%$cookieid%' and set_cookies is null order by request_time_millisecond desc limit 20");
  #  $sth1->execute();
  #  my($userpagevisitguid, $user_id) = $sth1->fetchrow();
  #  print ("userpagevisitguid is:\t\t$userpagevisitguid\n");
  #  print ("user_id:\t\t\t$user_id\n");
  #  sleep (3);


  my $sth1 = $dbh1->prepare("SELECT user_id from nc_user_session where OD_COOKIE_IDENTIFIER like '%$cookieid%' order by request_time_millisecond desc limit 1");
  $sth1->execute();
  my($user_id) = $sth1->fetchrow();
  #print ("userpagevisitguid is:\t\t$userpagevisitguid\n");
  print ("user_id:\t\t\t$user_id\n");
  sleep (3);

  $sth1 = $dbh1->prepare("SELECT user_page_visit_guid, user_interest FROM usermodeler_event_log order by id desc limit 1");
  $sth1->execute();
  my($userpagevisitguid, $user_interest) = $sth1->fetchrow();
  print ("userpagevisitguid is:\t\t$userpagevisitguid\n");
  #print ("user_interest is:\t\t$user_interest\t");
  sleep (3);

  #verify this upvg belongs to this user
  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_user_session where (user_page_visit_guid like '$userpagevisitguid') and (user_id like '$user_id')");
  $sth1->execute();
  my($count) = $sth1->fetchrow();
  if ($count > 0) {
    #print ("Confirmed this upvg is for the above user_id...\n");
  } else {
    print ("Something went wrong, couldn't confirm. Redo test...\n");
  }
  sleep (3);




  if (($user_id =~ m/\d+/g) and ($userpagevisitguid =~ m/\d+/g)) {
    my $sth4 = $dbh4->prepare("select is_fine from nc_user where user_id=$user_id");
    $sth4->execute();
    my($is_fine) = $sth4->fetchrow();
    print ("is_fine is:\t\t\t$is_fine\n");
    sleep (3);

    $sth4 = $dbh4->prepare("select count(*) from nc_user_odc where user_id=$user_id and cookie like '%$cookieid%'");
    $sth4->execute();
    my($count) = $sth4->fetchrow();
    if ($count > 0) {
      print ("nc_user_odc table has this user and cookie id matches.\n");
      sleep (3);
    } else {
      print ("nc_user_odc table doesn't have this user yet, should wait 1 minute for QA cluster or 3 min for production...\n");
      sleep(65);
      $sth4 = $dbh4->prepare("select count(*) from nc_user_odc where user_id=$user_id and cookie like '%$cookieid%'");
      $sth4->execute();
      my($count) = $sth4->fetchrow();
      if ($count > 0) {
	print ("Now nc_user_odc table has this user and cookie id matches.\n");
	sleep (3);
      }
    }

    #    $sth1 = $dbh1->prepare("SELECT user_interest from usermodeler_event_log where user_page_visit_guid like '$userpagevisitguid'");
    #    $sth1->execute();
    #    my($userinterest) = $sth1->fetchrow();
    #    print ("userinterest is\t\t\t$userinterest\n");
    #    sleep (3);
  }

  $url="http://www.amazon.com";
  $mech->get($url);
  sleep(2);
  $mech->get($url);

  ##
  $response = $mech->response();
  $header = $mech->response()->header( 'Set-Cookie' );
  #print ("cookies being set:\t\t$header\n");
  my $ac;
  #die;
  if ( $header =~ m/ubid-main=\d+-\d+-\d+/) {
    $ac = $&;
    #$ac =~ s/\//;
    #$ac =~ s/:TM//;
    #print ("Got amazon cookie: $ac\n");
  }
  #die;
  ##


  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);
  $mech->get($url);
  sleep(2);

  my $amazoncookie = $ac;	#&getamazoncookie();
  print ("Amazon cookie Id:\t\t$amazoncookie\n");


  ###

  #  $sth1 = $dbh1->prepare("SELECT user_page_visit_guid, user_id from nc_user_session where OD_COOKIE_IDENTIFIER like '%$amazoncookie%'");
  #  $sth1->execute();
  #  ($userpagevisitguid, $user_id) = $sth1->fetchrow();
  #  print ("userpgagevisitguid\t\t$userpagevisitguid\nuser_id\t\t\t\t$user_id\n");
  #  sleep (3);


  #####
  $sth1 = $dbh1->prepare("SELECT user_id from nc_user_session where OD_COOKIE_IDENTIFIER like '%$amazoncookie%' limit 1");
  $sth1->execute();
  $user_id = $sth1->fetchrow();
  #print ("userpagevisitguid is:\t\t$userpagevisitguid\n");
  unless ($user_id > 1) {
    sleep(61);
    $sth1 = $dbh1->prepare("SELECT user_id from nc_user_session where OD_COOKIE_IDENTIFIER like '%$amazoncookie%' limit 1");
    $sth1->execute();
    $user_id = $sth1->fetchrow();
  }
  print ("user_id:\t\t\t$user_id\n");
  sleep (3);

  $sth1 = $dbh1->prepare("SELECT user_page_visit_guid, user_interest FROM usermodeler_event_log order by id desc limit 1");
  $sth1->execute();
  ($userpagevisitguid, $user_interest) = $sth1->fetchrow();
  print ("userpagevisitguid is:\t\t$userpagevisitguid\t");
  #print ("user_interest is:\t\t$user_interest\t");
  sleep (3);

  #verify this upvg belongs to this user
  $sth1 = $dbh1->prepare("SELECT count(*) FROM nc_user_session where (user_page_visit_guid like '$userpagevisitguid') and (user_id like '$user_id')");
  $sth1->execute();
  $count = $sth1->fetchrow();
  if ($count > 0) {
    print ("Confirmed this upvg is for the above user_id...\n");
  } else {
    print ("Something went wrong, couldn't confirm. Redo test...\n");
  }
  sleep (3);
  #####

  my $sth4 = $dbh4->prepare("select count(*) from nc_user_odc where user_id=$user_id and cookie like '%$amazoncookie%' and domain_id = 67");
  $sth4->execute();
  $count = $sth4->fetchrow();
  if ($count > 0) {
    print ("nc_user_odc table has this user and amazon cookie id matches.\n");
    sleep (3);
  } else {
    print ("nc_user_odc table doesn't have this user yet, should wait 1 minute for QA cluster or 3 min for production...\n");
    sleep(65);
    $sth4 = $dbh4->prepare("select count(*) from nc_user_odc where user_id=$user_id and cookie like '%$amazoncookie%' and domain_id = 67");
    $sth4->execute();
    ($count) = $sth4->fetchrow();
    if ($count > 0) {
      print ("Now nc_user_odc table has this user and amazon cookie id matches.\n");
      sleep (3);
    }
  }


  ###
  $url = "http://www.a101tech.com/qa/chuck_blog/qacluster1.htm";
  $mech->get($url);
  $fetchedpage = $mech->content();
  my $tpurl;
  if ($fetchedpage =~ m/src=.http.+t=p.+g=0/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $url = "$tpurl";
  $mech->get($url);
  $fetchedpage = $mech->content();
  my $tfurl;
  if ($fetchedpage =~ m/t=f/g) {
    $tfurl = $&;
    #$tfurl =~ s/src='//;
    print ("t=f url:\t\t\tPresent\n");
  } else {
    print ("Failed to get t=f script\n");
  }


}



sub checkutacentral {
  sleep(4);
  my $testresult;
  print ("UTACentral Alive\t\t");
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $url = "$utacentralurl";
  $mech->get($url);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/generally/g) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("$testresult\n");
}

sub checkqcentral {
  sleep(4);
  my $testresult;
  print ("QCentral_Nebulis Alive\t\t");
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $url = "$qcentralurl";
  $mech->get($url);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/generally/g) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("$testresult\n");
}

sub checkcertertocscprotocol {
  sleep(4);
  my $testresult;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $url0;
  my $url;
  my $i = 0;

  print ("Google cookietest url\t\t");
  my $urlcookietest = "http://www.google.com";
  $mech->get($urlcookietest);
  print (".");
  sleep (5);
  $mech->get($urlcookietest);
  print (".\n");


  print ("travelocity,expedia,kayak 5*\t");
  while ($i < 5) {
    $url0 = "http://www.travelocity.com";
    $mech->get($url0);
    print (".");
    sleep(1);
    $url0 = "http://www.expedia.com";
    $mech->get($url0);
    print (".");
    sleep(1);
    $url0= "http://www.kayak.com";
    $mech->get($url0);
    sleep(1);
    $i++;
    if ($i =~ m/0/g) {
      print (".");
    }
  }
  print ("\n");

  my $sth1 = $dbh1->prepare("SELECT user_page_visit_id, created_date_time, host, url, referer_url, location, form_data, type, page_status, user_page_visit_guid, accept, cache_control, content_encoding, content_language, content_location, content_type, expires, page_last_modified, pragma, is_dynamic, priority, request_time_millisecond, response_time_millisecond, request_method, certer_version, injection_type, content_length FROM logdb.nc_user_page_visit order by user_page_visit_id desc limit 1");
  $sth1->execute();
  while (my($user_page_visit_id, $created_date_time, $host, $url, $referer_url, $location, $form_data, $type, $page_status, $user_page_visit_guid, $accept, $cache_control, $content_encoding, $content_language, $content_location, $content_type, $expires, $page_last_modified, $pragma, $is_dynamic, $priority, $request_time_millisecond, $response_time_millisecond, $request_method, $certer_version, $injection_type, $content_length) = $sth1->fetchrow()) {

    print ("****************************************\nVerifying nc_user_page_visit table\tOK\n");



    #Test 1
    print ("user_page_visit_id\t\t");
    if (($user_page_visit_id =~ m/\d+/) and  ($user_page_visit_id > 0)) {
      print ("Passed\t$user_page_visit_id\n");
    } else {
      print ("Failed<--\t$user_page_visit_id\n");
    }

    #    #Test 2
    #    print ("user_id\t\t\t\t");
    #    if ($user_id =~ m/\d+/) {
    #      print ("Passed\t$user_id\n");
    #    } else {
    #      print ("Failed<--\t$user_id\n");
    #    }

    #    #Test 2.1
    #    print ("raw_user_identifier\t");
    #    if ($raw_user_identifier =~ m/\d+/) {
    #      print ("Passed\t$raw_user_identifier\n");
    #    } else {
    #      print ("Failed<--\t$raw_user_identifier\n");
    #    }


    #Test 3
    print ("created_date_time\t\t");
    if ($created_date_time =~ m/2008/g) {
      print ("Passed\t$created_date_time\n");
    } else {
      print ("Failed<--\t$created_date_time\n");
    }
    #Test 4
    print ("host\t\t\t\t");
    if (($host =~ m/travelocity/) || ($host =~ m/expedia/) || ($host =~ m/kayak/)  || ($host =~ m/sb.google/) || ($host =~ m/abc.nebuad.com/)  || ($host =~ m/www.google.com/) ) {
      print ("Passed\t$host\n");
    } else {
      print ("Failed<--\t$host\n");
    }

    #Test 5
    print ("url\t\t\t\t");
    if (($url =~ m/http/g) and (($url =~ m/expedia/) || ($host =~ m/kayak/)  || ($host =~ m/travelocity/) || ($host =~ m/sb.google/)  || ($host =~ m/abc.nebuad.com/)  || ($host =~ m/www.google.com/)    )) {
      print ("Passed\t$url\n");
    } else {
      print ("Failed<--\t$url\n");
    }

    #Test 6
    print ("refererurl\t\t\t");
    if (($referer_url =~ m/http/g) and (($referer_url =~ m/expedia/) || ($referer_url =~ m/kayak/)  || ($referer_url =~ m/travelocity/)   || ($host =~ m/abc.nebuad.com/)  || ($host =~ m/www.google.com/)   )) {
      print ("Passed\t$referer_url\n");
    } elsif (($url =~ m/sb.google/g) and ($referer_url eq '')) {
      print ("Passed\t$referer_url\n");
    } else {
      print ("Failed<--\t$referer_url\n");
    }

    #Test 7
    my $numlocation;
    my $testresult;
    #my $sth2 = $dbh1->prepare("select max(user_page_visit_id) from certer_qauta500.nc_user_page_visit");
    my $sth2 = $dbh1->prepare("select max(user_page_visit_id) from logdb.nc_user_page_visit");
    $sth2->execute();
    my $maxpagevisitid =  $sth2->fetchrow();
    $sth2->finish();
    my $maxpagevisitidtouse = $maxpagevisitid - 5000;

    print ("location field used\t\t");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where location is not null and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where location is not null and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    $sth2->execute();
    $numlocation = $sth2->fetchrow();
    $sth2->finish();
    if ($numlocation > 0) {
      print ("Passed\t$location\n");
    } else {
      print ("Failed<--\t$location\n");
    }


    #Test 8
    print ("form_data\t\t\t");
    if ($form_data =~ m/[a-z]/g) {
      #print ("Passed\t$form_data\n");
    } else {
      #print ("Failed<--\t$form_data\n");
    }
    print ("TBD\n");

    #Test 9
    #    print ("search_data\t\t\t");
    #    if ($search_data =~ m/[a-z]/g) {
    #      print ("Passed\t$search_data\n");
    #    } else {
    #      print ("Failed<--\t$search_data\n");
    #    }

    #Test 10
    my $numtype;
    print ("type\t\t\t\t");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where type is not null and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where type is not null and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    $sth2->execute();
    $numtype = $sth2->fetchrow();
    $sth2->finish();
    if ($numtype > 0) {
      print ("Passed\t$location\n");
    } else {
      print ("Failed<--\t$location\n");
    }



    #Test 11
    print ("page_status\t\t\t");
    if ($page_status =~ m/\d+/g) {
      print ("Passed\t$page_status\n");
    } else {
      print ("Failed<--\t$page_status\n");
    }

    #Test 12
    #    print ("page_path\t\t\t");
    #    if ($page_path =~ m/\/nebuad\/content/g) {
    #      print ("Passed\t$page_path\n");
    #    } else {
    #      print ("Failed<--\t$page_path\n");
    #    }

    #Test 13
    #    print ("is_opt_out\t\t\t");
    #    if (($is_opt_out =~ m/\d/g) || ($is_opt_out =~ m/[A-Z][a-z]/g))  {
    #      print ("Passed\t$is_opt_out\n");
    #    } else {
    #      print ("Failed<--\t$is_opt_out\n");
    #    }

    #Test 14
    print ("user_page_visit_guid\t\t");
    if ($user_page_visit_guid =~ m/\d/g) {
      print ("Passed\t$user_page_visit_guid\n");
    } else {
      print ("Failed<--\t$user_page_visit_guid\n");
    }


    #Test 15
    print ("accept\t\t\t\t");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where accept like '%text/html%' and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where accept like '%text/html%' and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    $sth2->execute();
    my $numaccept = $sth2->fetchrow();
    $sth2->finish();
    if ($numaccept > 0) {
      print ("Passed\t$location\n");
    } else {
      print ("Failed<--\t$location\n");
    }


    #Test 16    $sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where accept like '%text/html%' and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");

    print ("cache_control\t\t\t");
    if (($cache_control =~ m/[a-z]/g) || ($cache_control =~ m/\d/g)) {
      #print ("Passed\t$cache_control\n");
    } else {
      #print ("Failed<--\t$cache_control\n");
    }
    print ("TBD\n");


    #Test 17
    print ("content_encoding\t\t");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where content_encoding like '%gzip%' and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where content_encoding like '%gzip%' and user_page_visit_id > '$maxpagevisitidtouse'  and url not like '%abc.nebuad.com%'");
    $sth2->execute();
    my $numcontentenc = $sth2->fetchrow();
    $sth2->finish();
    if ($numcontentenc > 0) {
      print ("Passed\t$location\n");
    } else {
      print ("Failed<--\t$location\n");
    }


    #Test 18
    print ("content_language\t\t");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where content_language like '%en%' and url not like '%abc.nebuad.com%' and user_page_visit_id > '$maxpagevisitidtouse'");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where content_language like '%en%' and url not like '%abc.nebuad.com%' and user_page_visit_id > '$maxpagevisitidtouse'");
    $sth2->execute();
    my $numcontentlang = $sth2->fetchrow();
    $sth2->finish();
    if ($numcontentlang > 0) {
      print ("Passed\t$location\n");
    } else {
      print ("Failed<--\t$location\n");
    }



    #Test 19
    print ("content_location\t\t");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where content_location like '%whyblog.html.en'  and url not like '%abc.nebuad.com%'  and   user_page_visit_id > '$maxpagevisitidtouse'");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where content_location like '%whyblog.html.en'  and url not like '%abc.nebuad.com%'  and   user_page_visit_id > '$maxpagevisitidtouse'");
    $sth2->execute();
    my $numcontentlocation = $sth2->fetchrow();
    $sth2->finish();
    if ($numcontentlocation > 0) {
      print ("Passed\t$location\n");
    } else {
      print ("Failed<--\t$location\n");
    }



    #Test 20
    print ("content_type\t\t\t");
    if ($content_type =~ m/[a-z]/g) {
      print ("Passed\t$content_type\n");
    } else {
      print ("Failed<--\t$content_type\n");
    }



    #Test 21
    print ("expires\t\t\t\t");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where (expires is not null) and (expires != 0)  and url not like '%abc.nebuad.com%'   and (user_page_visit_id > '$maxpagevisitidtouse')");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where (expires is not null) and (expires != 0)  and url not like '%abc.nebuad.com%'   and (user_page_visit_id > '$maxpagevisitidtouse')");
    $sth2->execute();
    my $numexpires = $sth2->fetchrow();
    $sth2->finish();
    if ($numexpires > 0) {
      print ("Passed\n");
    } else {
      print ("Failed<--\n");
    }


    #Test 22
    print ("page_last_modified\t\t");
    #$sth2 = $dbh1->prepare("SELECT count(*) FROM certer_qauta500.nc_user_page_visit where page_last_modified is not null  and url not like '%abc.nebuad.com%'  and user_page_visit_id > '$maxpagevisitidtouse'");
    $sth2 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where page_last_modified is not null  and url not like '%abc.nebuad.com%'  and user_page_visit_id > '$maxpagevisitidtouse'");
    $sth2->execute();
    my $nummod = $sth2->fetchrow();
    $sth2->finish();
    if ($nummod > 0) {
      print ("Passed\n");
    } else {
      print ("Failed<--\n");
    }

    #Test 23
    print ("pragma\t\t\t\t");
    print ("Failed<--\t$pragma\n");

    #    #Test 24
    #    print ("user_agent\t\t\t");
    #    if ($user_agent =~ m/Windows/g) {
    #      print ("Passed\t$user_agent\n");
    #    } else {
    #      print ("Failed<--\t$user_agent\n");
    #    }

    #Test 25
    print ("is_dynamic\t\t\t");
    if ($is_dynamic == 1) {
      print ("Passed\t$is_dynamic\n");
    } else {
      print ("Failed<--\t$is_dynamic\n");
    }

    #Test 26
    print ("priority\t\t\t");
    print ("Passed\t$priority\n");

    #Test 27
    #$sth2 = $dbh2->prepare("select max(user_page_visit_id) from certer_qauta500.nc_user_page_visit");
    $sth2 = $dbh2->prepare("select max(user_page_visit_id) from logdb.nc_user_page_visit");
    $sth2->execute();
    my $maxvisitid =  $sth2->fetchrow();
    $sth2->finish();
    my $idtostartwatching = $maxvisitid - 250;
    #    #$sth2 = $dbh2->prepare("select count(*) from certer_qauta500.nc_user_page_visit where host like '%google%' and cookies is not null and user_page_visit_id > $idtostartwatching");
    #    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_page_visit where host like '%google%' and cookies is not null and user_page_visit_id > $idtostartwatching");
    #    $sth2->execute();
    #    while (my($cookieyesno) = $sth2->fetchrow()) {
    #      if ($cookieyesno > 0) {
    #	print ("cookies\t\t\t\tPassed\n");
    #      } else {
    #	print ("cookies\t\t\t\tFailed<--\n");
    #      }
    #    }
    #    $sth2->finish();




    #Test 28
    print ("request_time_millisecond\t");
    if ($request_time_millisecond =~ m/\d\d/g) {
      print ("Passed\t$request_time_millisecond\n");
    } else {
      print ("Failed<--\t$request_time_millisecond\n");
    }


    #Test 29
    print ("response_time_millisecond\t");
    if ($response_time_millisecond =~ m/\d\d/g) {
      print ("Passed\t$response_time_millisecond\n");
    } else {
      print ("Failed<--\t$response_time_millisecond\n");
    }

    #Test 30
    print ("time diff req resp\t\t");
    if (($response_time_millisecond - $request_time_millisecond) < 1000) {
      print ("Passed\n");
    } else {
      print ("Failed<--\n");
    }

    #    #Test 31
    #    #$sth2 = $dbh2->prepare("select count(*) from certer_qauta500.nc_user_page_visit where host like '%google%' and set_cookies is not null and user_page_visit_id > $idtostartwatching");
    #    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_page_visit where host like '%google%' and set_cookies is not null and user_page_visit_id > $idtostartwatching");
    #    $sth2->execute();
    #    while (my($setcookieyesno) = $sth2->fetchrow()) {
    #      if ($setcookieyesno > 0) {
    #	print ("set_cookies\t\t\tPassed\n");
    #      } else {
    #	print ("set_cookies\t\t\tFailed<--\n");
    #      }
    #    }
    #    $sth2->finish();



    #Test 32
    print ("request_method\t\t\t");
    if (($request_method =~ m/GET/g) ||  ($request_method =~ m/POST/)) {
      print ("Passed\t$request_method\n");
    } else {
      print ("Failed<--\t$request_method\n");
    }


    #Test 33
    print ("certer_version\t\t\t");
    if (($certer_version > 0) and ($certer_version != 11111)) {
      print ("Passed\t$certer_version\n");
    } else {
      print ("Failed<--\t$certer_version\n");
    }


    #Test 34
    print ("injection_type\t\t\t");
    if ($injection_type >= 0) {
      print ("Passed\t$injection_type\n");
    } else {
      print ("Failed<--\t$injection_type\n");
    }


    #    #Test 35
    #    print ("certer_connection_identifier\t");
    #    if ($certer_connection_identifier > 0) {
    #      print ("Passed\t$certer_connection_identifier\n");
    #    } else {
    #      print ("Failed<--\t$certer_connection_identifier\n");
    #    }


    #    #Test 36
    #    print ("certer_user_identifier\t\t");
    #    if ($certer_user_identifier > 0) {
    #      print ("Passed\t$certer_user_identifier\n");
    #    } else {
    #      print ("Failed<--\t$certer_user_identifier\n");
    #    }


    #Test 37
    print ("content_length\t\t\t");
    if ($content_length > 0) {
      print ("Passed\t$content_length\n");
    } else {
      print ("Failed<--\t$content_length\n");
    }


    #Test 38: Verify t=p tags are audited in the database
    #$sth2 = $dbh2->prepare("select count(*) from certer_qauta500.nc_user_page_visit where url like '%t=p%' and url not like '%promo%' and user_page_visit_id > $idtostartwatching");

#    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_page_visit where url like '%t=p%' and url not like '%promo%' and user_page_visit_id > $idtostartwatching");
#    $sth2->execute();
#    while (my($tequalpyesno) = $sth2->fetchrow()) {
#      if ($tequalpyesno > 0) {
#	print ("t=p logged or not\t\tPassed\n");
#      } else {
#	print ("t=p logged or not\t\tFailed<--or going through 8080 so certer ignoring.\n");
#      }
#    }
#    $sth2->finish();


    #Test 39: Verify t=s tags are audited in the database
    #$sth2 = $dbh2->prepare("select count(*) from certer_qauta500.nc_user_page_visit where url like '%t=s%' and url not like '%suggest%' and url not like '%t=suv%' and url not like '%sigpro%' and user_page_visit_id > $idtostartwatching");


#    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_page_visit where url like '%t=s%' and url not like '%suggest%' and url not like '%t=suv%' and url not like '%sigpro%' and user_page_visit_id > $idtostartwatching");
#    $sth2->execute();
#    while (my($tequalsyesno) = $sth2->fetchrow()) {
#      if ($tequalsyesno > 4) {
#	print ("t=s logged or not\t\tPassed\n");
#      } else {
#	print ("t=s logged or not\t\tFailed<--or going through 8080 so certer ignoring.\n");
#      }
#    }
#    $sth2->finish();


    #Test 40:


  }
  $sth1->finish();
}


