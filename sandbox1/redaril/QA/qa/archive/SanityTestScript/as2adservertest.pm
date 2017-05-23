require Exporter;
our @ISA = ("Exporter");


sub as2adservertest {
  print ("\n********************* AdServer 2.0 test  **************************\n");

  my $tcreurl;
  my $nebuadservingurl;


  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );

  my $cookie_jar = HTTP::Cookies->new(
				      autosave => 1,
				      file => "c:\\cookies.txt"
				     );
  $cookie_jar->clear();
  $mech->cookie_jar($cookie_jar);
  #$mech->cookie_jar(HTTP::Cookies->new());

  my $time = (time);
  $time = $time * 1000;


  my $inpurl = "http://qacluster1-1.nebuad.com:8080/adserver/as?t=inp&c=qacluster1UTA&d=120x600&s=2375";
  $mech->get($inpurl);
  my $fetchedpageinp = $mech->content();
  #print ("$fetchedpageinp");

  if ($fetchedpageinp =~ m/http.+tc=/g) {
    $tcreurl = $&;
    #    $tcreurl =~ s/src="//;
    print ("t=cre url:\t\t\t$tcreurl\n");
  } else {
    print ("Failed to get t=cre script\n");
  }

  $tcreurl = "$tcreurl"."$time";
  print ("tcreurl to hit\t\t\t$tcreurl\n");


  sleep (5);
  my $cu = &getcuas2();

  print ("qualifying for automotive\tOK\n");
  my $url2 = "http://10.50.150.150:8080/usermodeler/getcategories?cu="."$cu"."&i=1";
  $mech->get($url2);
  sleep (3);
  $mech->get($url2);
  sleep (3);
  $mech->get($url2);
  sleep (3);
  $mech->get($url2);
  sleep (3);
  $mech->get($url2);
  sleep (3);


  print ("checking interests...\t\t");
  my $url3 = "http://10.50.150.150:8080/usermodeler/getcategories?cu="."$cu"."&v";
#  print ("$url3\n");

  $mech->get($url3);
  sleep (3);
  my $fetchedpage2 = $mech->content();
  #print ("$fetchedpage2");
  if ($fetchedpage2 =~ m/Automotive/g) {
    print ("Pass\n");
  } else {
    print ("Failed\n");
  }

  $mech->get($tcreurl);
  my $fetchedpagetcre = $mech->content();
  #print ("$fetchedpagetcre\n");

  my $redirected_final = LWP::LastURI::last_uri;
  print ("imager server url:\t\t$redirected_final\n");


  if ($fetchedpagetcre =~ m/Created with The GIMP/) {
    print ("Got Image:\t\t\tPass\n");
  } else {
    print ("Got Image:\t\t\tFailed\n");
  }

}
