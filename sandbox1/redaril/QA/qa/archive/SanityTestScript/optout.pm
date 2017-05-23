require Exporter;
our @ISA = ("Exporter");

sub verifyoptout  {
  my $adserver = "qacluster1-1.nebuad.test";  #because of cookie transitioning, using the future cookie instead for this test
  print ("\n********************* Opt-Out testing ***********************\n");
  my $tsurl;
  #my $url1 = "http://$adserver:8080/a?t=o&nai=yes";
  my $url1 = "http://$adserver/a?t=o&nai=yes";
  #print ("$url1");

  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);

  # Before our callback.
  print $cookie_jar->as_string, "\n";

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
  #print ("$url2\n");
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
  sleep (3);

}
