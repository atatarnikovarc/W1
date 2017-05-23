require Exporter;
our @ISA = ("Exporter");



sub beaconenabledisableignorelogic {
  print ("\n************************* Partner Beacons Testing *************************\n");
  &optedoutusernopartnerbeacons();
  &validcerternamepartnerbeaconenable();
  &validcerternamepartnerbeaconenableoldjvalue();
  &invalidcerternamenopartnerbeaconenable();
  &nocerternamenopartnerbeaconenable();
  &disablebeaconnocerternamenebuadcookieoldjvalueonnetwork();
  &disablebeaconnocerternamenebuadcookieoldjvalueoffnetwork();
  &invalidCerterNamenoJValue();
  &nocerternamenopartnerbeaconenablenonAR();
}



sub optedoutusernopartnerbeacons {
  print ("********************* Opted out, but Valid Certer Name and recent j value gets NO Partner Beacons\n");
  my $tpurl;
  my $url1 = $testpageurl;
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
  #print ("$fetchedpage");
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp");

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'o') {
      $val = 1;
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
  if ($fetchedpagetf =~ m/http.+t=k.+/g) {
    my $tk = $&;
    print ("Failed because got t=k\t$tk\n");
  } else {
    print ("Pass\tNo t=k as expected\n");
  }

  print ("t=b:\t\t\t\t");
  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Pass\t$tburl\n");
  } else {
    print ("Pass\tNo t=b as expected because opted out\n");
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
}




sub validcerternamepartnerbeaconenable {
  print ("\n********************* Valid Certer Name and recent j value gets Partner Beacons Enable\n");
  my $tpurl;
  my $url1 = $testpageurl;
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
  #print ("$fetchedpage");
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");

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
    print ("Failed because got t=k\t$tk\n");
  } else {
    print ("Pass\tNo t=k as expected\n");
  }

  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\tPass\t$tburl\n");
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

}


sub validcerternamepartnerbeaconenableoldjvalue {
  print ("\n********************* Valid Certer Name and old j value gets Partner Beacons Enable\n");
  my $tsurl;
  my $url1 = $testpageurl;
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
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();



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


#  my $tpurl = "http://"."$adserver"."/a?t=p&c="."$certername"."&v=1.3&g=$g&ts=$ts";
#  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);
  $mech->get($tpurl);
  $fetchedpagetp = $mech->content();

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
    print ("Failed\t$tk\n");
  } else {
    print ("Pass\tNo t=k as expected..\n");
  }

  #print ("t=b:\t\t\t\t");
  my $tburl;
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("t=b url:\t\t\tPass\t$tburl\n");
  } else {
    print ("Failed\tNo t=b as expected\n");
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





sub invalidcerternamenopartnerbeaconenable {
  print ("\n********************* unknown Certer Name and old j value\n");
  #my $tsurl;
  my $url1 = $testpageurl;
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
  #print ("$fetchedpage");
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp");
  #die;

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    ####$domain =~ s/13/13a/;
    ##$domain =~ s/-1/-1a/;


    if ($key eq 'j') {
      #$val = 12345;
      #print ("updated j cookie to 1234\tOK\n");
      #$val = 1208797473284;
    }
    if ($key eq 'c') {
      ##$val =~ s/$certername/fakeclusterUTA/;
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


  my $adservera = $adserver;
  ##$adservera =~ s/-1/-1a/;
  #my $tpurl = "http://"."$adservera".":8080/a?t=p&c=fakeclusterUTA&v=1.3&g=$g&ts=$ts";
  my $tpurl = "http://"."$adservera".":8080/a?t=p&c=fakeclusterUTA";

  #my $file = "c:\\cookies2.txt";
  #$cookie_jar->load($file);
  print ("t=p url to hit\t\t\t$tpurl\n");

  $mech->get($tpurl);
  $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");
  #die;

  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+/) {
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

  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Failed certer is invalid so no beacons but got t=k \t$tk\n");
  } else {
    print ("Pass\tNo t=k as expected\n");
  }


  print ("t=b:\t\t\t\t");
  if ($fetchedpagetf =~ m/src=.http.+t=b.+/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b $tburl\n");
  }  else {
    print ("Pass\tNo t=b since certer name is unknown\n");
  }


}


sub invalidCerterNamenoJValue {
  print ("\n********************* Unknown Certer Name and NO j value\n");
  #my $tsurl;
  #my $url1 = "http://www.yahoo.com";
  my $url1 = $testpageurl;
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
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp");
  #die;

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
      #$val =~ s/$certername/fakeclusterUTA/;
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

  my $adservera = $adserver;
  #$adservera =~ s/-1/-1a/;
  my $tpurl = "http://"."$adservera".":8080/a?t=p&c=fakeclusterUTA&v=1.3&g=0&ts=0";
  #my $tpurl = "http://"."$adservera".":8080/a?t=p&c=fakeclusterUTA&v=1.3&g=$g&ts=$ts";


  #my $file = "c:\\cookies2.txt";
  #$cookie_jar->load($file);
  print ("t=p url to hit\t\t\t$tpurl\n");

  $mech->get($tpurl);
  $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp\n");

  my $tburl;
  my $tfurl;
  if ($fetchedpagetp =~ m/http.+t=f.+/) {
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

  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k.+UTA/g) {
    my $tk = $&;
    print ("Failed, got t=k but should not since unknown certer\t$tk\n");
  } else {
    print ("Pass\tNo t=k since certer name is unknown\n");
  }

  print ("t=b:\t\t\t\t");
  if ($fetchedpagetf =~ m/src=.http.+t=b.+/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b $tburl\n");
  }  else {
    print ("Pass\tNo t=b since certer name is unknown\n");
  }
}


sub nocerternamenopartnerbeaconenablenonAR {
  print ("\n********************* No Certer Name appended by certer (not going through Certer) and recent j value with NONE AR spot\n");

  my $url1 = $testpageurl;
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
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    #$domain =~ s/1/1a/;

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

  my $adservera = $adserver;
  #$adservera =~ s/-1/-1a/;

  my $tpurl = "http://"."$adservera".":8080/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);

  $mech->get($tpurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");

  my $tburl;

  my $tfurl;
  if ($fetchedpage =~ m/http.+t=f.+/g) {
    $tfurl = $&;
    $tfurl =~ s/src='//;
    $tfurl =~ s/';//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Got no t=f\n");
  }


  print ("t=f to hit with NON AR Spot:\t");
  #4790 for sandbox
  #350 for test nebuad zone
  $tfurl = "$tfurl"."&s=4784&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");

  #die;
  my $tk;
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k/g) {
    $tk = $&;
    print ("Fail since this is none AR spot\t$tk\n");
  } else {
    print ("Pass\tNo t=k as expected since not certer user\n");
  }

#  print ("t=b:\t\t\t\t");
#  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("Failed, got t=b instead of t=k$tburl\n");
#  } else {
#    print ("Pass\tNo t=b as expected\n");
#  }


#  $mech->get($tk);
#  my $fetchedpagetk = $mech->content();
#  #print ("$fetchedpagetk\n");

#  if ($fetchedpagetk =~ m/ajrotator\/2598/g) {
#    print ("Got sandbox enable beacon\tPass\n");
#  } elsif ($fetchedpagetk =~ m/ajrotator\/1084/g) {
#    print ("Got production enable beacon\tPass\n");
#  } elsif ($fetchedpagetk =~ m/ajrotator\/2887/g) {
#    print ("Got sandbox disable beacon\tPass\n");
#  } elsif ($fetchedpagetk =~ m/ajrotator\/2888/g) {
#    print ("Got production disable beacon\tPass\n");
#  }
}


sub nocerternamenopartnerbeaconenable {
  print ("\n********************* No Certer Name appended by certer (not going through Certer) and good j value with AR spot\n");

  my $url1 = $testpageurl;
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
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    #$domain =~ s/1/1a/;

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

  my $adservera = $adserver;
  #$adservera =~ s/-1/-1a/;

  my $tpurl = "http://"."$adservera".":8080/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);

  $mech->get($tpurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");


  my $tburl;

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
  #4790 for sandbox
  #350 for test nebuad zone
  $tfurl = "$tfurl"."&s=4790&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");

  #die;
  my $tk;
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k/g) {
    $tk = $&;
    print ("Pass, got t=k as expected because not a certer user\t$tk\n");
  } else {
    print ("Pass\tno t=k as expected because no certername\n");
  }

  print ("t=b:\t\t\t\t");
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b instead of t=k$tburl\n");
  } else {
    print ("Pass\tNo t=b as expected\n");
  }


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


sub disablebeaconnocerternamenebuadcookieoldjvalueoffnetwork {
  print ("\n********************* No Certer Name appended by certer (simulate not going through Certer) and old j value, OFF Network\n");
  my $url1 = $testpageurl;
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
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    #$domain =~ s/1/1a/;

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
  my $adservera = $adserver;
  #$adservera =~ s/-1/-1a/;


  my $tpurl = "http://"."$adservera".":8080/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);

  $mech->get($tpurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");
  #die;

  my $tburl;

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
  $tfurl = "$tfurl"."&s=4784&d=357&ajpage=0&ajparams=%26dim%3D357%26pos%3D1%26pv%3D1824663848550009%26nc%3D946552701&ndl=http%3A//www.test.com/%3Fid%3D456%26action%3Dview&ndr=";
  print ("$tfurl\n");
  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf");

  my $tk;
  print ("t=k:\t\t\t\t");
  if ($fetchedpagetf =~ m/http.+t=k/g) {
    $tk = $&;
    print ("Failed\tt=k should not be sent because Off Network spot$tk\n");
  } else {
    print ("Pass\tNo t=k as expected because offnetwork\n");
  }

#  print ("t=b:\t\t\t\t");
#  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
#    $tburl = $&;
#    $tburl =~ s/src='//;
#    print ("Failed, got t=b $tburl\n");
#  } else {
#    print ("Pass\tNo t=b as expected\n");
#  }

#  $mech->get($tk);
#  my $fetchedpagetk = $mech->content();
#  #print ("$fetchedpagetk\n");

#  if ($fetchedpagetk =~ m/ajrotator\/2598/g) {
#    print ("Got sandbox enable beacon\tPass\n");
#  } elsif ($fetchedpagetk =~ m/ajrotator\/1084/g) {
#    print ("Got production enable beacon\tPass\n");
#  } elsif ($fetchedpagetk =~ m/ajrotator\/2887/g) {
#    print ("Got sandbox disable beacon\tPass\n");
#  } elsif ($fetchedpagetk =~ m/ajrotator\/2888/g) {
#    print ("Got production disable beacon\tPass\n");
#  }
}


sub disablebeaconnocerternamenebuadcookieoldjvalueonnetwork {
  print ("\n********************* No Certer Name appended by certer (simulate not going through Certer) and old j value, On Network\n");
  my $url1 = $testpageurl;
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
  if ($fetchedpage =~ m/src=.http.+t=p/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }

  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();


  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    $domain =~ s/1/1a/;

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
  my $adservera = $adserver;
  #$adservera =~ s/-1/-1a/;


  my $tpurl = "http://"."$adservera".":8080/a?t=p";
  #my $tpurl = "http://"."$adservera"."/a?t=p";
  print ("t=p url to hit\t\t\t$tpurl\n");

  my $file = "c:\\cookies2.txt";
  $cookie_jar->load($file);

  $mech->get($tpurl);
  $fetchedpage = $mech->content();
  #print ("$fetchedpage\n");
  #die;

  my $tburl;

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
  if ($fetchedpagetf =~ m/http.+t=k/g) {
    $tk = $&;
    print ("Pass\tt=k sent because On Network and no certerid and old j value $tk\n");
  } else {
    print ("Failed\tshould have gotten t=k\n");
  }

  print ("t=b:\t\t\t\t");
  if ($fetchedpagetf =~ m/src=.http.+t=b.+UTA/g) {
    $tburl = $&;
    $tburl =~ s/src='//;
    print ("Failed, got t=b $tburl\n");
  } else {
    print ("Pass\tNo t=b as expected\n");
  }

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



