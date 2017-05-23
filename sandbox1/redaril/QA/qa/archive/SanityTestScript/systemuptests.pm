require Exporter;
our @ISA = ("Exporter");


sub checkthatadserverisresponsive {
  my $testresult;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
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
  #&updatetestresults(101, $testresult);
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
  #&updatetestresults(201, $testresult);
}


sub checkadcentral {
  sleep(4);
  my $testresult;
  print ("AdCentral Alive\t\t\t");
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $url = "$adcentralurl";
  $mech->get($url);
  my $fetchedpage = $mech->content();
  if ($fetchedpage =~ m/authorized Nebuad employees /g) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("$testresult\n");
  #&updatetestresults(301, $testresult);
}

sub checkutacentral {
  sleep(4);
  my $testresult;
  print ("UTA central Alive\t\t");
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
  #&updatetestresults(401, $testresult);

}

#not injecting for now
sub checkcerterinjections {
  my $testresult;
  my $url = "http://www.yahoo.com";
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
  #not injecting right now
  $testresult = 'N/A';
  print ("Certer injections\t\t$testresult\n");
  sleep (3);
  #&updatetestresults(501, $testresult);
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
  $sth4->finish();

  sleep (2);
  print ("OK\n");
  &checkncdomaincookieregex();
}


sub checkncdomaincookieregex {

  print ("Verifying ncdomaincookieregex\t");
  my $sth4 = $dbh4->prepare("INSERT INTO NC_DOMAIN_COOKIE_REGEX (DOMAIN_COOKIE_REGEX_ID, NAME, REVERSE_MATCH_NAME, REG_EXPRESSION) VALUES ('99', 'qacluster1-1.nebuad.com', 'moc.dauben.1-1retsulcaq', '(\bu=(.+?)(?:\;|$)')");
  $sth4->execute();
  $sth4->finish();
  print ("OK\n");
}


sub checkfrequencycaps {
  print ("Checking frequency caps\t\t");

  my $sth4 = $dbh4->prepare("update NC_COMMERCIAL_CATEGORY set FREQUENCY_CAP_DELAY_SECONDS = 5, FREQUENCY_CAP_LIMIT = 6, FREQUENCY_CAP_TIME_SECONDS = 60, FREQUENCY_CAP_ENABLED = 'Y' WHERE NAME like 'Office_Supply'");
  $sth4->execute();
  $sth4->finish();
  print ("OK\n");

  $sth4 = $dbh4->prepare("update NC_COMMERCIAL_CATEGORY set FREQUENCY_CAP_DELAY_SECONDS = 5, FREQUENCY_CAP_LIMIT = 6, FREQUENCY_CAP_TIME_SECONDS = 60, FREQUENCY_CAP_ENABLED = 'Y' WHERE NAME like 'Automotive%'");
  $sth4->execute();
  $sth4->finish();
  print ("OK\n");

  $sth4 = $dbh4->prepare("update JJ_CAMPAIGN set NC_FREQUENCY_CAP_DELAY_SECONDS = 5, NC_FREQUENCY_CAP_LIMIT = 6, NC_FREQUENCY_CAP_TIME_SECONDS = 60, NC_FREQUENCY_CAP_ENABLED = '1' WHERE NAME like 'Automotive%'");
  $sth4->execute();
  $sth4->finish();
  print ("OK\n");

  $sth4 = $dbh4->prepare("update JJ_CAMPAIGN set NC_FREQUENCY_CAP_DELAY_SECONDS = 5, NC_FREQUENCY_CAP_LIMIT = 6, NC_FREQUENCY_CAP_TIME_SECONDS = 60, NC_FREQUENCY_CAP_ENABLED = '1' WHERE NAME like '%Ink%'");
  $sth4->execute();
  $sth4->finish();
  print ("OK\n");

}

