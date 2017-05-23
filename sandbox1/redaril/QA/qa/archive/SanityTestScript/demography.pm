require Exporter;
our @ISA = ("Exporter");


sub demographytests {
  #to be used against qac4
  print ("\ndemography tests:\n");
  &exactdomainvisitclassify();
  &mastersubdomainvisitclassify();
  &masterdomainvisitclassify();
  &nodemographyfordomainvisit();
  &nodemographyforsubdomainvisit();


  #UM Demography Test Cases #set ComscoreDemographyInformativePeriod to 10 using jmx console before proceeding.
  &umdemographydataaddonemodel();
  &umdemographyinformativeperiod();
  &umdemographynodataupdatefornotinformativecategories();
  &umdemographyeventbothinterestanddemographydata();
  &umdemographyqualificationtest();
  &umdemographyqualificationchangelifecycletest();

  #AdRequest Processing Test Cases

}



sub checknumberofevents {
  my $goal = shift;
  my $hipid = shift;
  my $umserver = "qacluster2-0.nebuad.com";

  my $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");

  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);



  $mech->get($umdemographymodelurlcoarse);
  my $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");

  my $i = 0;
  if ($fetchedpagecoarse =~ m/NumOfEvents: \d\d?/g)  {
    $i = $&;
    $i =~ s/NumOfEvents: //g;
  }

  print ("NumOfEvents:$i\tgoal:$goal\n");

  if ($i >= $goal) {
    return 0;
  } else {
    return 1;
  }
}

sub umdemographyqualificationchangelifecycletest {
  print ("\n");
  print ("umdemographyqualificationchangelifecycletest\n");
  sleep (11);

  ######get hipid
  my $hipid = &gethipidmech();
  print ("hipid\t\t\t\t$hipid\n");

  ######get cookie id
  my $cu;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  my $url = "http://qacluster2-1.nebuad.com/a?t=p&c=qacluster2UTA&v=1.3&ts=0&g=0";
  $mech->get($url);

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );


    if ($key eq 'u') {
      $cu = $val;
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

  print ("cookie id\t\t\t$cu\n");


  ###### Reset fine and coarse profiles
  print ("Reset coarse and fine profiles\tOK\n");
  my $umserver = "qacluster2-0.nebuad.com";
  my $finereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&r=yes";
  my $coarsereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&r=yes";
  $mech->get($finereseturl);
  $mech->get($coarsereseturl);
  sleep (10);

  ###### Now the test
  my $urltotest = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&i=444";
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);

  $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);


  while ( &checknumberofevents(11, $hipid) ) {
    #print (".");
    $mech->get($urltotest);
    sleep (13);
  }


  ###### Verify demography model is updated for coarse profile
  $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");

  if ($fetchedpagecoarse =~ m/Eligible Aspect Id: 9/g) {
    print ("Eligible Aspect 9 as expected\tPassed\n");
  } else {
    print ("Eligible Aspect 9 as expected\tFailed\n");
  }

####

  $urltotest = "http://www.mediaplazza.com";
  $mech->get($urltotest);
  sleep (13);


  while ( &checknumberofevents(12, $hipid) ) {
    $mech->get($urltotest);
    sleep (13);
  }


  ###### Verify demography model is updated for coarse profile
  $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  $fetchedpagecoarse = $mech->content();
  print ("$fetchedpagecoarse\n");


  if ($fetchedpagecoarse =~ m/Category Id: 3 'Income'\nNumOfEvents: \d+\nEligible Aspect Id: NONE/g) {
    print ("Eligible Aspect back to NONE\tPassed\n");
  } else {
    print ("Eligible Aspect back to NONE\tFailed\n");
    print ("retrying...\n");
    while ( &checknumberofevents(18, $hipid) ) {
      $mech->get($urltotest);
      sleep (13);
    }
    $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
    #print ("$umdemographymodelurlcoarse\n");
    $mech->get($umdemographymodelurlcoarse);
    $fetchedpagecoarse = $mech->content();
    print ("$fetchedpagecoarse\n");
    if ($fetchedpagecoarse =~ m/Category Id: 3 'Income'\nNumOfEvents: \d+\nEligible Aspect Id: NONE/g) {
      print ("Eligible Aspect back to NONE\tPassed\n");
    } else {
      print ("Eligible Aspect back to NONE\tFailed\n");
    }
  }

#####

  sleep (15);
  $urltotest = "http://www.mediaplazza.com";
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);

  while ( &checknumberofevents(38, $hipid) ) {
    #print (".");
    $mech->get($urltotest);
    sleep (13);
  }


  ###### Verify demography model is updated for coarse profile
  $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  $fetchedpagecoarse = $mech->content();
  print ("$fetchedpagecoarse\n");


  if ($fetchedpagecoarse =~ m/Category Id: 3 \'Income\'\nNumOfEvents: \d+\nEligible Aspect Id: \d/g) {
    print ("Category 3 Aspect as expected\tPassed\n");
  } else {
    print ("Category 3 Aspect as expected\tFailed\n");
    print ("retrying...\n");
      while ( &checknumberofevents(69, $hipid) ) {
	#print (".");
	$mech->get($urltotest);
	sleep (13);
      }
    $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
    #print ("$umdemographymodelurlcoarse\n");
    $mech->get($umdemographymodelurlcoarse);
    $fetchedpagecoarse = $mech->content();
    print ("$fetchedpagecoarse\n");
    if ($fetchedpagecoarse =~ m/Category Id: 3 \'Income\'\nNumOfEvents: \d+\nEligible Aspect Id: \d/g) {
      print ("Category 3 Aspect as expected\tPassed\n");
    } else {
      print ("Category 3 Aspect as expected\tFailed\n");
    }
  }

  if ($fetchedpagecoarse =~ m/Gender/g) {
    print ("Category 1 Gender present\tPassed\n");
  } else {
    print ("Category 1 Gender present\tFailed\n");
  }



}





sub umdemographyqualificationtest {
  print ("\n");
  print ("umdemographyqualificationtest\n");
  sleep (11);


  ######get hipid
  my $hipid = &gethipidmech();
  print ("hipid\t\t\t\t$hipid\n");

  ######get cookie id
  my $cu;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  my $url = "http://qacluster2-1.nebuad.com/a?t=p&c=qacluster2UTA&v=1.3&ts=0&g=0";
  $mech->get($url);

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'u') {
      $cu = $val;
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

  print ("cookie id\t\t\t$cu\n");


  ###### Reset fine and coarse profiles
  print ("Reset coarse and fine profiles\tOK\n");
  my $umserver = "qacluster2-0.nebuad.com";
  my $finereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&r=yes";
  my $coarsereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&r=yes";
  $mech->get($finereseturl);
  $mech->get($coarsereseturl);
  sleep (10);

  ###### Now the test
  my $urltotest = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&i=444";
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);

  $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);

  sleep (30);


  ###### Verify demography model is updated for coarse profile
  my $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  my $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");
  if (($fetchedpagecoarse =~ m/3:0/g) and ($fetchedpagecoarse =~ m/Cosmetics/)) {
    print ("demog & interest updated coarse\tPassed\n");
  } else {
    print ("demog & interest updated coarse\tFailed\n");
  }

  my $i = 0;
  if ($fetchedpagecoarse =~ m/NumOfEvents: \d/g)  {
    $i = $&;
    $i =~ s/NumOfEvents: //g;
  }


  while ($i < 10) {
    $mech->get($urltotest);
    sleep (13);
    $i++;
  }




  $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);
  $mech->get($urltotest);
  sleep (13);


  ###### Verify demography model is updated for coarse profile
  $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");
  if (($fetchedpagecoarse =~ m/Eligible Aspect Id: NONE/g) and ($fetchedpagecoarse =~ m/Eligible Aspect Id: \d/)) {
    print ("demography diff aspct id coarse\tPassed\n");
  } else {
    print ("demography diff aspct id coarse\tFailed\n");
  }

}



sub umdemographyeventbothinterestanddemographydata {
  print ("\n");
  print ("umdemographyeventbothinterestanddemographydata\n");
  sleep (11);

  ######get hipid
  my $hipid = &gethipidmech();
  print ("hipid\t\t\t\t$hipid\n");

  ######get cookie id
  my $cu;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  my $url = "http://qacluster2-1.nebuad.com/a?t=p&c=qacluster2UTA&v=1.3&ts=0&g=0";
  $mech->get($url);

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'u') {
      $cu = $val;
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

  print ("cookie id\t\t\t$cu\n");


  ###### Reset fine and coarse profiles
  print ("Reset coarse and fine profiles\tOK\n");
  my $umserver = "qacluster2-0.nebuad.com";
  my $finereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&r=yes";
  my $coarsereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&r=yes";
  $mech->get($finereseturl);
  $mech->get($coarsereseturl);
  sleep (10);

  ###### Now the test
  my $urltotest = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&i=444";
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);

  $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);

  sleep (10);





  ###### Verify demography model is updated for coarse profile
  my $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  my $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");
  if (($fetchedpagecoarse =~ m/3:0./g) and ($fetchedpagefine =~ m/Cosmetics/)) {
    print ("demog & interest updated coarse\tPassed\n");
  } else {
    print ("demog & interest updated coarse\tFailed\n");
  }

}





sub umdemographynodataupdatefornotinformativecategories {
  print ("\n");
  print ("umdemographynodataupdatefornotinformativecategories\n");
  sleep (11);


  ######get hipid
  my $hipid = &gethipidmech();
  print ("hipid\t\t\t\t$hipid\n");

  ######get cookie id
  my $cu;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  my $url = "http://qacluster2-1.nebuad.com/a?t=p&c=qacluster2UTA&v=1.3&ts=0&g=0";
  $mech->get($url);

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'u') {
      $cu = $val;
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

  print ("cookie id\t\t\t$cu\n");


  ###### Reset fine and coarse profiles
  print ("Reset coarse and fine profiles\tOK\n");
  my $umserver = "qacluster2-0.nebuad.com";
  my $finereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&r=yes";
  my $coarsereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&r=yes";
  $mech->get($finereseturl);
  $mech->get($coarsereseturl);
  sleep (10);

  ###### Now the test
  my $urltotest = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&i=444";
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);

  $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);

  sleep (10);



  ###### Verify demography model is updated for coarse profile
  my $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  my $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");
  if ($fetchedpagecoarse =~ m/3:0/g) {
    print ("UM demography model coarse\tPassed\n");
  } else {
    print ("UM demography model coarse\tFailed\n");
  }

  my $events1;
  my $NumOfEvents;
  if ($fetchedpagecoarse =~ m/NumOfEvents: \d/) {
    $events1 = $&;
    $events1 =~ s/NumOfEvents: //g;
    #print ("NumOfEvents:\t$events1\n");
    print ("demography informative period\tPassed\n");
  } else {
    print ("demography informative period\tFailed\n");
  }


  $urltotest = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&i=1078";
  $mech->get($urltotest);
  $mech->get($urltotest);
  $mech->get($urltotest);


  $urltotest = "http://www.moes.com";
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (10);

  ###### Verify demography model is updated for coarse profile
  $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");

  my $events2;
 if ($fetchedpagecoarse =~ m/Category Id: 2 'Age'\nNumOfEvents: \d/) {
    $events2 = $&;
    $events2 =~ s/Category Id: 2 'Age'\nNumOfEvents: //g;
    #print ("no informative probability\tPassed\n");
  } else {
    #print ("no informative probability\tFailed\n");
  }

  my $events3;
 if ($fetchedpagecoarse =~ m/Category Id: 3 'Income'\nNumOfEvents: \d/) {
    $events3 = $&;
    $events3 =~ s/Category Id: 3 'Income'\nNumOfEvents: //g;
    #print ("no informative probability\tPassed\n");
  } else {
    #print ("no informative probability\tFailed\n");
  }




  if (($events2 > $events3) and ($events2 > $events1)) {
    print ("no informative probability\tPassed\n");
  } else {
    print ("no informative probability\tFailed\n");
  }



}



sub umdemographyinformativeperiod {
  print ("\n");
  print ("umdemographyinformativeperiod\n");
  sleep (11);

  ######get hipid
  my $hipid = &gethipidmech();
  print ("hipid\t\t\t\t$hipid\n");

  ######get cookie id
  my $cu;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  my $url = "http://qacluster2-1.nebuad.com/a?t=p&c=qacluster2UTA&v=1.3&ts=0&g=0";
  $mech->get($url);

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'u') {
      $cu = $val;
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

  print ("cookie id\t\t\t$cu\n");


  ###### Reset fine and coarse profiles
  print ("Reset coarse and fine profiles\tOK\n");
  my $umserver = "qacluster2-0.nebuad.com";
  my $finereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&r=yes";
  my $coarsereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&r=yes";
  $mech->get($finereseturl);
  $mech->get($coarsereseturl);
  sleep (10);

  ###### Now the test
  my $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (15);
  $mech->get($urltotest);
  sleep (5);


  ###### Verify demography model is updated for fine profile
  my $umdemographymodelurlfine = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&v";
  #print ("$umdemographymodelurlfine\n");
  $mech->get($umdemographymodelurlfine);
  my $fetchedpagefine = $mech->content();
  #print ("$fetchedpagefine\n");

  if ($fetchedpagefine =~ m/6:0.10/g) {
    print ("UM demography model fine\tPassed\n");
  } else {
    print ("UM demography model fine\tFailed\n");
  }


  ###### Verify demography model is updated for coarse profile
  my $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  my $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");
  if ($fetchedpagecoarse =~ m/8:0.19491525/g) {
    print ("UM demography model coarse\tPassed\n");
  } else {
    print ("UM demography model coarse\tFailed\n");
  }

  my $NumOfEvents;
  if ($fetchedpagecoarse =~ m/NumOfEvents: 2/) {
    print ("demography informative period\tPassed\n");
  } else {
    print ("demography informative period\tFailed\n");
  }



  my $probability1 = 0;
  my $probability2 = 0;
  my $probability3 = 0;
  my $matches;

  my $dbh4 = DBI->connect( "dbi:Oracle:host=10.50.200.11;sid=qa", "qacluster2", "qacluster2");
  $sth4 = $dbh4->prepare("SELECT c.DEMOGRAPHY_CATEGORY_ID, a.DEMOGRAPHY_CATEGORY_ASPECT_ID, a.PROBABILITY FROM NT_COMSCORE_DOMAIN_ASPECT a INNER JOIN NC_DEMOGRAPHY_CATEGORY_ASPECT c ON a. DEMOGRAPHY_CATEGORY_ASPECT_ID = c.DEMOGRAPHY_CATEGORY_ASPECT_ID WHERE a.COMSCORE_DOMAIN_ID = 1275 ORDER BY c.DEMOGRAPHY_CATEGORY_ID , a. DEMOGRAPHY_CATEGORY_ASPECT_ID");
  $sth4->execute();
  while (my ($democatid, $demoaspectid, $probability) = $sth4->fetchrow()) {
    if ($democatid == 1) {
      $probability1 = $probability1 + $probability;
    }
    if ($democatid == 2) {
      $probability2 = $probability2 + $probability;
    }
    if (($democatid == 2) and ($probability == 0.62711865)) {
      $matches = 1;
    }

    if ($democatid == 3) {
      $probability3 = $probability3 + $probability;
    }
  }
  $sth4->finish();
  if (($probability1 > 0.999) and ($probability1 < 1.001) and ($probability2 > 0.999) and ($probability2 < 1.001) and ($probability3 > 0.999) and ($probability3 < 1.001) and ($matches == 1)) {
    print ("database probabilities\t\tPassed\n");
  }

  $urltotest = "http://www.moes.com";
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (1);
  $mech->get($urltotest);
  sleep (10);


  ###### Verify demography model is updated for coarse profile
  $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");

  if ($fetchedpagecoarse =~ m/NumOfEvents: 3/) {
    print ("demography informative period\tPassed\n");
  } else {
    print ("demography informative period\tFailed\n");
  }


}





sub umdemographydataaddonemodel {
  print ("\n");
  print ("umdemographydataaddonemodel\n");
  sleep (11);

  ######get hipid
  my $hipid = &gethipidmech();
  print ("hipid\t\t\t\t$hipid\n");

  ######get cookie id
  my $cu;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  my $url = "http://qacluster2-1.nebuad.com/a?t=p&c=qacluster2UTA&v=1.3&ts=0&g=0";
  $mech->get($url);

  my $self_enclosed_callback = sub {
    my ( $version, $key, $val, $path, $domain, $port, $path_spec,
         $secure, $expires, $discard, $extra ) = @_;

    # Remove the currently iterating cookie from the jar.
    # NB: this might be dangerous! Seems to work though.
    $cookie_jar->clear( $domain, $path, $key );

    # Now change domain, just for example.
    #$domain =~ s/\.org\z/.com/;
    if ($key eq 'u') {
      $cu = $val;
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

  print ("cookie id\t\t\t$cu\n");


  ###### Reset fine and coarse profiles
  print ("Reset coarse and fine profiles\tOK\n");
  my $umserver = "qacluster2-0.nebuad.com";
  my $finereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?cu=$cu&r=yes";
  my $coarsereseturl = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&r=yes";
  $mech->get($finereseturl);
  $mech->get($coarsereseturl);
  sleep (5);

  ###### Now the test
  my $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  $urltotest = "http://www.loreal.com";
  $mech->get($urltotest);
  sleep (10);




  ###### Verify demography model is updated for coarse profile
  my $umdemographymodelurlcoarse = "http://"."$umserver".":8080/usermodeler/getcategories?u=$hipid&v";
  #print ("$umdemographymodelurlcoarse\n");
  $mech->get($umdemographymodelurlcoarse);
  my $fetchedpagecoarse = $mech->content();
  #print ("$fetchedpagecoarse\n");
  if ($fetchedpagecoarse =~ m/8:0.19491525/g) {
    print ("UM demography model coarse\tPassed\n");
  } else {
    print ("UM demography model coarse\tFailed\n");
  }

  my $NumOfEvents;
  if ($fetchedpagecoarse =~ m/NumOfEvents: 1/) {
    print ("demography informative period\tPassed\n");
  } else {
    print ("demography informative period\tFailed\n");
  }



  my $probability1 = 0;
  my $probability2 = 0;
  my $probability3 = 0;
  my $matches;

  my $dbh4 = DBI->connect( "dbi:Oracle:host=10.50.200.11;sid=qa", "qacluster2", "qacluster2");
  $sth4 = $dbh4->prepare("SELECT c.DEMOGRAPHY_CATEGORY_ID, a.DEMOGRAPHY_CATEGORY_ASPECT_ID, a.PROBABILITY FROM NT_COMSCORE_DOMAIN_ASPECT a INNER JOIN NC_DEMOGRAPHY_CATEGORY_ASPECT c ON a. DEMOGRAPHY_CATEGORY_ASPECT_ID = c.DEMOGRAPHY_CATEGORY_ASPECT_ID WHERE a.COMSCORE_DOMAIN_ID = 1275 ORDER BY c.DEMOGRAPHY_CATEGORY_ID , a. DEMOGRAPHY_CATEGORY_ASPECT_ID");
  $sth4->execute();
  while (my ($democatid, $demoaspectid, $probability) = $sth4->fetchrow()) {
    if ($democatid == 1) {
      $probability1 = $probability1 + $probability;
    }
    if ($democatid == 2) {
      $probability2 = $probability2 + $probability;
    }
    if (($democatid == 2) and ($probability == 0.62711865)) {
      $matches = 1;
    }

    if ($democatid == 3) {
      $probability3 = $probability3 + $probability;
    }
  }
  $sth4->finish();
  if (($probability1 > 0.999) and ($probability1 < 1.001) and ($probability2 > 0.999) and ($probability2 < 1.001) and ($probability3 > 0.999) and ($probability3 < 1.001) and ($matches == 1)) {
    print ("database probabilities\t\tPassed\n");
  }



}





sub gethipidmech {
  my ($hipid, $line);
  my $umserver = "qacluster2-0.nebuad.com";
  my $url = "http://"."$umserver".":8080/usermodeler/getcategories?p";

  print ("getting hipid\t\t\t");

  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);

  $mech->get($url);
  sleep (2);
  my $fetchedpage = $mech->content();

  print ("OK\n");

  if ($fetchedpage =~  m/HIP\+\sId\:\s\d+\w+/g) {
    $hipid = $& ;
    $hipid =~ s/HIP\+\sId\://;
    $hipid =~ s/ //;
    #print ("hipid is $hipid\n");
  }

  if ($hipid eq "") {
    print ("couldn't get the hipid\n");
    die;
  }
  return $hipid;
}



sub nodemographyforsubdomainvisit {
  my $testresult;

  print ("no demography subdomain visit\t");
  $url = "http://midwest.meeks.com/index.php";
  &gotourl1($url);

  &ftpgeticlog();
  my $data_file="interestclassifiers.log";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 1000)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;

    if ($_ =~ m/Event for domain: midwest.meeks.com wasn't classified in demography/g ) {
      $testresult = 'Passed';
      print ("$testresult\n");
      last;
    }
    $i--;
  }

  my $dbh4 = DBI->connect( "dbi:Oracle:host=10.50.200.11;sid=qa", "qacluster2", "qacluster2");
  $sth4 = $dbh4->prepare("SELECT IS_MASTER FROM NT_COMSCORE_DOMAIN WHERE NAME like 'meeks.com'");
  $sth4->execute();
  my ($is_master) = $sth4->fetchrow();
  $sth4->finish();
  if ($is_master eq 'N') {
    print ("IS_MASTER\t\t\tPassed\t$is_master\n");
  } else {
    print ("IS_MASTER\t\t\tFailed\t$is_master\n");
  }
}


sub gotourl1 {
  my $url = shift;

  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);

  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);
  $mech->get($url);
  sleep (2);

}


sub nodemographyfordomainvisit {
  my $testresult;

  print ("no demography for domain visit\t");
  $url = "http://www.yahoo.com";
  &gotourl1($url);

  &ftpgeticlog();
  my $data_file="interestclassifiers.log";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 1000)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;

    if ($_ =~ m/Event for domain: yahoo.com wasn't classified in demography/g ) {
      $testresult = 'Passed';
      print ("$testresult\n");
      last;
    }
    $i--;
  }

}


sub masterdomainvisitclassify {
  my $testresult;

  print ("Master domain visit classify\t");
  $url = "http://www.realsimple.com";
  &gotourl1($url);


  &ftpgeticlog();
  my $data_file="interestclassifiers.log";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 1000)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;

    if ($_ =~ m/Event for domain:realsimple.com was classified in demography by Comscore domain Id=7257/g ) {
      $testresult = 'Passed';
      print ("$testresult\n");
      last;
    }
    $i--;
  }

}




sub mastersubdomainvisitclassify {
  my $testresult;

  print ("Master subdomain classify\t");
  $url = "http://food.realsimple.com/realsimple/recipefinder.dyn?action=advanceSearch";
  &gotourl1($url);


  &ftpgeticlog();
  my $data_file="interestclassifiers.log";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 100)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;

    if ($_ =~ m/Event for domain:food.realsimple.com was classified in demography by Comscore Master domain Id=7257/g ) {
      $testresult = 'Passed';
      print ("$testresult\n");
      last;
    }
    $i--;
  }
}


sub exactdomainvisitclassify {
  my $testresult;

  print ("Exact domain visit classify\t");
  $url = "http://www.intellicast.com";
  &gotourl1($url);

  &ftpgeticlog();
  my $data_file="interestclassifiers.log";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 100)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;

    if ($_ =~ m/Event for domain:intellicast.com was classified in demography by Comscore domain Id=831/g ) {
      $testresult = 'Passed';
      print ("$testresult\n");
      last;
    }
    $i--;
  }
}



sub ftpgeticlog {
  use Net::FTP::Simple;
  my $ftpserver = "10.50.150.153";
  my $username = "root";
  my $password = "901nebuad";

  my @received_files = Net::FTP::Simple->retrieve_files({
							 username        => $username,
							 password        => $password,
							 server          => $ftpserver,
							 remote_dir      => '/home/csc/logs',
							 debug_ftp       => 0,
							 files           => [
									     'interestclassifiers.log'
									    ],
							});

  my $count2 = @received_files;
  #print ("$count2\n");
  #print ("$received_files[0]");
}
