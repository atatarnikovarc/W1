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
my $certer_id = 26;
my $deviceid;
my $testpageurl1 = "http://www.a101tech.com/qa/demo.html";
my $testpageurl = "http://www.a101tech.com/qa/chuck_blog/blog_q1.htm";
my $utacentralurl = "http://10.50.150.151:8080/controlcenter/views/webcommon/login.jsp";
my $qcentralurl ="http://10.50.150.150:8080/nebulis/views/webcommon/login.jsp";
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






my $i = 0;
while ($i < 1) {
  &beaconenabledisableignorelogic(); #use with qacluter4 for now
  $i++;
}

sub beaconenabledisableignorelogic {
  &checktoseeifconnectedthroughqacluster4vpn();
  &goodCerterNameGoodJValue();
  &badCerterNameGoodJValue();
  &goodCerterNamerecentJValue();
  &goodCerterNameoldJValue();
  &invalidCerterNameoldJValue();
  &invalidCerterNamenoJValue();
  &invalidCerterNameGoodJValue();
  &tequalpwithoutCparameter();
  &tequalpwithoutCparameteroldJValue();
  &invalidCerterNameoldJValueARspot();
  &invalidCerterNameoldJValuenoneARspot();
}


sub checktoseeifconnectedthroughqacluster4vpn {
  my $url = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url);
  my $fetchedpage = $mech->content();
  if (($fetchedpage =~ m/qacluster4UTA/g)) {
  } else {
      print ("\n###################################################\n");
      print ("# Please VPN in to qacluster4 VPN and try again ...\n");
      print ("###################################################\n\n\n");
      die;
  }
}



sub goodCerterNameGoodJValue {
  print ("\n\n********************* t=s Valid Certer Name and recent j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
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

  $mech->get($tburl);
  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpage =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpage =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpage =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n\n");

}


sub badCerterNameGoodJValue {
  print ("\n\n********************* t=s Fake Certer Name and good j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }
  $tsurl =~ s/qacluster4UTA/fakecerter/;
  print ("t=s url altered:\t\t$tsurl\n");
  $mech->get($tsurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");
  my $tburl;
  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("No t=b since fake certer name\tPass\n\n\n");
  }
}



sub goodCerterNamerecentJValue {
  print ("\n\n********************* t=p url with Valid Certer Name and recent j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'j') {
      #$val = 1234;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";

  my $tpurl = "http://qacluster4-13.nebuad.com/a?t=p&c=qacluster4UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");


  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);
  $mech->get($tpurl);

  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp");
  #die;

#beacon logic has moved to t=f from t=p

#  my $tburl;
#  if ($fetchedpagetp =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\t$tburl\n");
#  } else {
#    print ("Got no t=b\n");
#  }
  #print ("$fetchedpagetp\n");

  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/) {
  #if ($fetchedpagetp =~ m/http.+t=f.+g=0/g) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }

  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Failed because got t=k\t$tk\n\n");
  } else {
    print ("No t=k as expected\n");
  }

  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("t=b url:\t\t\tFailed, Got no t=b\n");
  }

  $mech->get($tburl);
  my $fetchedpagetb = $mech->content();
  #print ("$fetchedpagetb\n");
  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n");
}

sub goodCerterNameoldJValue {
  print ("\n\n********************* t=p url with Valid Certer Name and old j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'j') {
      $val = 1234;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";

  my $tpurl = "http://qacluster4-13.nebuad.com/a?t=p&c=qacluster4UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);
  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();

#  my $tburl;
#  if ($fetchedpagetp =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\t$tburl\n");
#  } else {
#    print ("Got no t=b as expected\t\tPass\n");
#  }

  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }

  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");

  #die;
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Failed\t$tk\n\n");
  } else {
    print ("No t=k as expected..\n");
  }

  print ("t=b:\t\t\t\t");
  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("No t=b as expected\t\tPass\n");
  }


  $mech->get($tburl);
  my $fetchedpagetb = $mech->content();
  #print ("$fetchedpagetb\n");
  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n\n");

}



sub invalidCerterNameGoodJValue {
  print ("\n\n********************* t=p url with Invalid Certer Name and recent j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();



  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    $domain =~ s/13/13a/;

    if ($key eq 'j') {
      #$val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      $val =~ s/qacluster4UTA/fakeclusterUTA/;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";

  #adding the 8080 so that the certer doesn't append to this request, adding a to point directly to server instead of LB
  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p&c=fakeclusterUTA&v=1.3&g=$g&ts=$ts";
##  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p&c=qacluster4UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p";


  #my $file = "c:\\cookies2.txt";
  #$cookie_jar->load($file);
  print ("t=p url to hit\t\t\t$tpurl\n");
  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");
  #die;

  my $tburl;
#  if ($fetchedpagetp =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\t$tburl\n");
#  } else {
#    print ("Got no t=b as expected\t\tPass\n");
#  }

  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/) {
    $tfurl = $&;
    $tfurl =~ s/qacluster4-13/qacluster4-13a/;
    $tfurl =~ s/nebuad.com/nebuad.com:8080/;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }



  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");

#die;
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Pass\t$tk\n");
  } else {
    print ("No t=k as expected\tPass\n");
  }

  print ("t=b:\t\t\t\t");
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b instead of t=k$tburl\n");
  } else {
    print ("No t=b as expected\tPass\n\n");
  }



#  my $tkurl;
#  if ($fetchedpagetp =~ m/http.+t=k.+UTA/g) {
#    $tkurl = $&;
#    #$tkurl =~ s/src='//;
#    print ("t=k url:\t\t\tPass\t$tkurl\t\n");
#  } else {
#    print ("Got no t=k\n");
#  }


  $mech->get($tburl);
  my $fetchedpagetb = $mech->content();
  #print ("$fetchedpagetb\n");
  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n\n");

}


sub invalidCerterNameoldJValue {
  print ("\n\n********************* t=p url with Invalid Certer Name and old j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    $tsurl =~ s/qacluster4-13/qacluster4-13a/;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    $domain =~ s/13/13a/;


    if ($key eq 'j') {
      $val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      $val =~ s/qacluster4UTA/fakeclusterUTA/;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";


  #adding the 8080 so that the certer doesn't append to this request, adding a to point directly to server instead of LB
  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p&c=fakeclusterUTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p";


  #my $file = "c:\\cookies2.txt";
  #$cookie_jar->load($file);
  print ("t=p url to hit\t\t\t$tpurl\n");
  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");
  #die;

#  my $tburl;
#  if ($fetchedpagetp =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\t$tburl\n");
#  } else {
#    print ("Got no t=b as expected\t\tPass\n");
#  }

  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }


  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");


  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Pass\t$tk\n");
  } else {
    print ("Failed\tNo t=k\n");
  }

  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b instead of t=k$tburl\n");
  }


  $mech->get($tburl);
  my $fetchedpagetb = $mech->content();
  #print ("$fetchedpagetb\n");
  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n\n");

}


sub invalidCerterNamenoJValue {
  print ("\n\n********************* t=p url with Invalid Certer Name and NO j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    $tsurl =~ s/qacluster4-13/qacluster4-13a/;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'j') {
      $val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      $val =~ s/qacluster4UTA/fakeclusterUTA/;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";


  #adding the 8080 so that the certer doesn't append to this request, adding a to point directly to server instead of LB
  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p&c=fakeclusterUTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p";


  #my $file = "c:\\cookies2.txt";
  #$cookie_jar->load($file);
  print ("t=p url to hit\t\t\t$tpurl\n");
  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");
  #die;

  my $tburl;
#  if ($fetchedpagetp =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\t$tburl\n");
#  } else {
#    print ("Got no t=b as expected\t\tPass\n");
#  }

  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }


  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");


  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Pass\t$tk\n");
  } else {
    print ("Failed\tNo t=k\n");
  }

  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b instead of t=k$tburl\n");
  }



#  my $tkurl;
#  if ($fetchedpagetp =~ m/http.+t=k.+UTA/g) {
#    $tkurl = $&;
#    #$tkurl =~ s/src='//;
#    print ("t=k url:\t\t\tPass\t$tkurl\t\n");
#  } else {
#    print ("Failed\tGot no t=k\n");
#  }


  $mech->get($tburl);
  my $fetchedpagetb = $mech->content();
  #print ("$fetchedpagetb\n");
  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n\n");

}


sub tequalpwithoutCparameter {
  print ("\n\n********************* t=p url with no Certer Name appended by certer (not going through Certer)  and good j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    $domain =~ s/13/13a/;

    if ($key eq 'j') {
      #$val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      #$val =~ s/qacluster4UTA/fakeclusterUTA/;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };


  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";




  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&ts=12310&g=123123";
  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);

  $mech->get($tpurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");


  my $tburl;
#  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\tFailed\t$tburl\n");
#  } else {
#    print ("Got no t=b\t\t\tPass\n");
#  }
  #print ("$fetchedpage\n");


#  $mech->get($tburl);
#  my $fetchedpagetb = $mech->content();
#  #print ("$fetchedpage\n");
#  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
#    print ("Got sandbox enable beacon\tPass\n");
#  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
#    print ("Got production enable beacon\tPass\n");
#  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
#    print ("Got sandbox disable beacon\tPass\n");
#  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
#    print ("Got production disable beacon\tPass\n");
#  }

  my $tfurl;
  if ($fetchedpage =~ m/http.+t=f.+/g) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    $tfurl =~ s/';//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }


  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");

#die;
  my $tk;
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    $tk = $&;
    print ("Pass\t$tk\n");
  } else {
    print ("No t=k as expected\tPass\n");
  }

  print ("t=b:\t\t\t\t");
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b instead of t=k$tburl\n");
  } else {
    print ("No t=b as expected\tPass\n\n");
  }

#  $mech->get($tfurl);
#  my $fetchedpagetf = $mech->content();
#  print ("$fetchedpagetf\n");
#  die;

#  my $tkurl;
#  if ($fetchedpage =~ m/http.+t=k/g) {
#    $tkurl = $&;
#    $tkurl =~ s/src='//;
#    print ("t=k url:\t\t\t$tkurl\n");
#  } else {
#    print ("Got no t=k\n");
#  }
  #print ("tkurl is $tkurl\n");
#die;
  $mech->get($tk);
  my $fetchedpagetk = $mech->content();
  #print ("$fetchedpagetk\n");

  if ($fetchedpagetk =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetk =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetk =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetk =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
}





sub tequalpwithoutCparameteroldJValue {
  print ("\n\n********************* t=p url with no Certer Name appended by certer (simulate not going through Certer) and old j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    $domain =~ s/13/13a/;

    if ($key eq 'j') {
      $val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      #$val =~ s/qacluster4UTA/fakeclusterUTA/;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };


  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";




  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&ts=12310&g=123123";
  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);

  $mech->get($tpurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");


  my $tburl;
#  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\tFailed\t$tburl\n");
#  } else {
#    print ("Got no t=b\t\t\tPass\n");
#  }
#  print ("$fetchedpage\n");


#  $mech->get($tburl);
#  my $fetchedpagetb = $mech->content();
#  #print ("$fetchedpage\n");
#  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
#    print ("Got sandbox enable beacon\tPass\n");
#  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
#    print ("Got production enable beacon\tPass\n");
#  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
#    print ("Got sandbox disable beacon\tPass\n");
#  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
#    print ("Got production disable beacon\tPass\n");
#  }


  my $tfurl;
  if ($fetchedpage =~ m/http.+t=f.+/g) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    $tfurl =~ s/';//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }


  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");

  my $tk;
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    $tk = $&;
    print ("Fail\t$tk\n");
  } else {
    print ("No t=k as expected\tPass\n");
  }

  print ("t=b:\t\t\t\t");
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b $tburl\n");
  } else {
    print ("No t=b as expected\tPass\n\n");
  }



#  $mech->get($tfurl);
#  my $fetchedpagetf = $mech->content();
#  print ("$fetchedpagetf\n");
#  die;

#  my $tkurl;
#  if ($fetchedpage =~ m/http.+t=k/g) {
#    $tkurl = $&;
#    $tkurl =~ s/src='//;
#    print ("t=k url:\t\t\t$tkurl\n");
#  } else {
#    print ("Got no t=k\n");
#  }
  #print ("tkurl is $tkurl\n");
#die;
  $mech->get($tk);
  my $fetchedpagetk = $mech->content();
  #print ("$fetchedpagetk\n");

  if ($fetchedpagetk =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetk =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetk =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetk =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
}



#############################################################
########
########


sub beaconenabledisableignorelogic_orig {
  print ("*********************Good Certer Name and good j value\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
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

#  $mech->get( $tburl );
#  my $response = $mech->response();
#  my $header = $mech->response()->header( 'Set-Cookie' );
#  #print ("cookies being set:\t\t$header\n");
#  if ( $header =~ m/o=0/) {
#    print ("o cookie set to 0\t\tok\n");
#  }
#  if ( $header =~ m/w=\d+/) {
#    my $w = $&;
#    print ("w cookie is set\t\t\t$w\n");
#  }
#  if ( $header =~ m/u=\d+/) {
#    my $u = $&;
#    print ("u cookie is set\t\t\t$u\n");
#  }
#  if ( $header =~ m/b=\d+/) {
#    my $u = $&;
#    print ("b cookie is set\t\t\t$u\n");
#  }
#  if ( $header =~ m/j=\d+/) {
#    my $u = $&;
#    print ("j cookie is set\t\t\t$u\n");
#  }

  $mech->get($tburl);
  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpage =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpage =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpage =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  #print ("$fetchedpage\n");



##now with bad certer name
  print ("\n\n*********************Bad Certer Name and good j value\n");
  #$tsurl;
  $url1 = "http://www.google.com";
  $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }
  $tsurl =~ s/qacluster1UTA/fakecerter/;
  print ("t=s url altered:\t\t$tsurl\n");
  $mech->get($tsurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");

  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("No t=b since fake certer name\tPass\n");
  }

##now with old j value

  print ("\n\n********************* t=p url with Good Certer Name and old j value\n");
  #$tsurl;
  $url1 = "http://www.google.com";
  $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'j') {
      #$val = 1234;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";


  my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&ts=0&g=0";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);
  $mech->get($tpurl);

  $fetchedpage = $mech->content();
  #print ("$fetchedpage");

  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("Got no t=b\n");
  }

  my $tfurl;
  if ($fetchedpage =~ m/http.+t=f.+g=0/g) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }
  #$tfurl = '$tfurl'.'&s=1213&d=364&ajpage=0&ajparams=%26dim%3D364%26pos%3D1%26pv%3D7188242069969297%26nc%3D868370321&ndl=http%3A//www.a101tech.com/qa/chuck_blog/qacluster1.htm&ndr=';
  #print ("tfurl is $tfurl\n");
die;
  $mech->get($tfurl);
  $fetchedpage = $mech->content();
  print ("$fetchedpage\n");
#  if ($fetchedpage =~ m/src=.http.+/g) {
die;


##now with good j value

  print ("\n\n********************* t=p url with Good Certer Name and Good j value\n");
  $url1 = "http://www.google.com";
  $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
#  #$mech->cookie_jar(HTTP::Cookies->new());
#  $mech->get($url1);
#  $fetchedpage = $mech->content();
#  if ($fetchedpage =~ m/src=.http.+/g) {
#    $tsurl = $&;
#    $tsurl =~ s/src="//;
#    $tsurl =~ s/">//;
#    $tsurl =~ s/<\/script>//;
#    #print ("t=s url:\t\t\t$tsurl\n");
#    #print ("$tsurl\n");
#  }  else {
#    print ("Failed to get t=s script\n");
#  }


  $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'j') {
      #$val = 1234;
      #print ("updated j cookie to 1234\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );

  $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&ts=0&g=0";

  $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);

  $mech->get($tpurl);
  $fetchedpage = $mech->content();
  print ("$fetchedpage");

  if ($fetchedpage =~ m/http.+t=.+g=0/g) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Failed to get t=f script\n");
  }


  $mech->get($tfurl);
  $fetchedpage = $mech->content();
  #print ("***\n$fetchedpage");

die;
  #my $tfurl;
  if ($fetchedpage =~ m/http.+t=.+UTA/g) {
    $tsurl = $&;
    $tsurl =~ s/src='//;
    print ("t=s url:\t\t\t$tsurl\n");
  } else {
    print ("Failed to get t=s script\n");
  }

die;


######################
  print ("\n\n*********************BAD Certer Name but good j value\n");
  #$tsurl =~ s/qacluster1UTA/qacluster1UTAfake/;
  $mech->get($tsurl);
  $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("Failed to get t=b script so, in this case this is a Pass because of face certer name\n");
  }


##now with old j parameter

  $tsurl =~ s/qacluster1UTAfake/qacluster1UTA/;
  print ("tsurl is $tsurl\n");
#  my $j = 123;
#  $cookie_jar->set_cookie( $j )
  $mech->get($tsurl);
  $fetchedpage = $mech->content();
  print ("here is the fetchedpage: $fetchedpage\n");
  if ($fetchedpage =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\t$tburl\n");
  } else {
    print ("Failed to get t=b script so, in this case this is a Pass because of face certer name\n");
  }
  print ("$fetchedpage\n");

  die;

}





sub invalidCerterNameoldJValueARspot {
  print ("\n\n********************* t=p url with Invalid Certer Name and old j value and AR Spot\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    $tsurl =~ s/qacluster4-13/qacluster4-13a/;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    $domain =~ s/13/13a/;


    if ($key eq 'j') {
      $val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      $val =~ s/qacluster4UTA/fakeclusterUTA/;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";


  #adding the 8080 so that the certer doesn't append to this request, adding a to point directly to server instead of LB
  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p&c=fakeclusterUTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p";


  #my $file = "c:\\cookies2.txt";
  #$cookie_jar->load($file);
  print ("t=p url to hit\t\t\t$tpurl\n");
  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");
  #die;


  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }

  print ("t=f to hit with AR Spot:\t");
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//bugzilla.nebuad.com/attachment.cgi%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("$tk\tPass\n");
  } else {
    print ("Failed\t\tNo t=k\n\n");
  }

  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed\tt=b url:\t\t\t$tburl\n");
  } else {
    print ("t=b:\t\t\t\tGot no t=b as expected\t\tPass\n");
  }

  #die;


#  my $tkurl;
#  if ($fetchedpagetp =~ m/http.+t=k.+UTA/g) {
#    $tkurl = $&;
#    #$tkurl =~ s/src='//;
#    print ("t=k url:\t\t\tPass\t$tkurl\t\n");
#  } else {
#    print ("Failed\tGot no t=k\n");
#  }


  $mech->get($tburl);
  my $fetchedpagetb = $mech->content();
  #print ("$fetchedpagetb\n");
  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n\n");

}


sub invalidCerterNameoldJValuenoneARspot {
  print ("\n\n********************* t=p url with Invalid Certer Name and old j value and none AR Spot\n");
  my $tsurl;
  my $url1 = "http://www.google.com";
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());
  $mech->get($url1);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/src=.http.+/g) {
    $tsurl = $&;
    $tsurl =~ s/src="//;
    $tsurl =~ s/">//;
    $tsurl =~ s/<\/script>//;
    print ("t=s url:\t\t\t$tsurl\n");
    $tsurl =~ s/qacluster4-13/qacluster4-13a/;
    print ("t=s url:\t\t\t$tsurl\n");
    #print ("$tsurl\n");
  }  else {
    print ("Failed to get t=s script\n");
  }

  my $ts;
  my $g;

  if ($tsurl =~ m/ts=\d+/) {
    $ts = $&;
    $ts =~ s/ts=//g;
    #print ("$ts\n");
  }

  if ($tsurl =~ m/g=\d+/) {
    $g = $&;
    $g =~ s/g=//g;
    #print ("$g\n");
  }

  $mech->get($tsurl);
  $fetchedpage = $mech->content();

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    $domain =~ s/13/13a/;


    if ($key eq 'j') {
      $val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      $val =~ s/qacluster4UTA/fakeclusterUTA/;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }

    $cookie_jar->set_cookie( $version, $key, $val, $path, $domain,
                             $port, $path_spec, $secure, $expires,
                             $discard, $extra );
  };

  # Before our callback.
  #print $cookie_jar->as_string, "\n";

  $cookie_jar->scan( $self_enclosed_callback );
  #print $cookie_jar->as_string, "\n";


  #adding the 8080 so that the certer doesn't append to this request, adding a to point directly to server instead of LB
  my $tpurl = "http://qacluster4-13a.nebuad.com:8080/a?t=p&c=fakeclusterUTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p&c=qacluster1UTA&v=1.3&g=$g&ts=$ts";
  #my $tpurl = "http://qacluster1-1.nebuad.com:8080/a?t=p";


  #my $file = "c:\\cookies2.txt";
  #$cookie_jar->load($file);
  print ("t=p url to hit\t\t\t$tpurl\n");
  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");
  #die;

#  my $tburl;
#  if ($fetchedpagetp =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("t=b url:\t\t\t$tburl\n");
#  } else {
#    print ("Got no t=b as expected\t\tPass\n");
#  }

  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }

  print ("t=f to hit with NONE AR Spot:\t");
  $tfurl = "$tfurl"."&s=4784&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//bugzilla.nebuad.com/attachment.cgi%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Failed\tShould not get t=k here, got $tk\n");
  } else {
    print ("Got no t=k as expected\tPass\n");
  }

  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed\tt=b url:\t\t\t$tburl\n");
  } else {
    print ("t=b:\t\t\t\tGot no t=b as expected\tPass\n");
  }

  #die;


#  my $tkurl;
#  if ($fetchedpagetp =~ m/http.+t=k.+UTA/g) {
#    $tkurl = $&;
#    #$tkurl =~ s/src='//;
#    print ("t=k url:\t\t\tPass\t$tkurl\t\n");
#  } else {
#    print ("Failed\tGot no t=k\n");
#  }


  $mech->get($tburl);
  my $fetchedpagetb = $mech->content();
  #print ("$fetchedpagetb\n");
  if ($fetchedpagetb =~ m/ajrotator\/2598/g) {
    print ("Got sandbox enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/1084/g) {
    print ("Got production enable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2887/g) {
    print ("Got sandbox disable beacon\tPass\n");
  } elsif ($fetchedpagetb =~ m/ajrotator\/2888/g) {
    print ("Got production disable beacon\tPass\n");
  }
  print ("\n\n");

}
