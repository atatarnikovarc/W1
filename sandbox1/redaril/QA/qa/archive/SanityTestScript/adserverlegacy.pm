require Exporter;
our @ISA = ("Exporter");




sub legacyadservertest {
  print ("\n********************* legacy adserver test  **************************\n");
  my $url1 = $testpageurl;
  my $tpurl;
  my $tfurl;
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
  $mech->get($url1);
  sleep (3);


  my $fetchedpage = $mech->content();
  #print ("$fetchedpage");
  if ($fetchedpage =~ m/src=.http.+t=p.+g=\d+/g) {
    $tpurl = $&;
    $tpurl =~ s/src="//;
    print ("t=p url:\t\t\t$tpurl\n");
  } else {
    print ("Failed to get t=p script\n");
  }


  $mech->get($tpurl);
  my $fetchedpagetp = $mech->content();
  #print ("$fetchedpagetp");

  if ($fetchedpagetp =~ m/http.+t=f.+g=\d+/g) {
    $tfurl = $&;
    $tfurl =~ s/src="//;
    print ("t=f url:\t\t\t$tfurl\n");
  } else {
    print ("Failed to get t=f script\n");
  }

  $tfurl = "$tfurl"."&s=1213&d=364&ajpage=0&ajparams=%26dim%3D364%26pos%3D1%26pv%3D5136286535232383%26nc%3D858385141&ndl=http%3A//www.a101tech.com/qa/chuck_blog/qacluster1.htm&ndr=";
  print ("tfurl to hit\t\t\t$tfurl\n");


  sleep (5);
  my $cu = &getcu();

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

  $mech->get($url3);
  sleep (3);
  my $fetchedpage2 = $mech->content();
  #print ("$fetchedpage2");
  if ($fetchedpage2 =~ m/Automotive/g) {
    print ("passed\n");
  } else {
    print ("failed\n");
  }


  $mech->get($tfurl);
  my $fetchedpagetf = $mech->content();
  #print ("$fetchedpagetf\n");
  #die;
  if ($fetchedpagetf =~ m/http.+nebuadserving.+/g) {
    $nebuadservingurl = $&;
    $nebuadservingurl =~ s/'\>\<\/script\>//;
    print ("nebuadservingurl:\t\t$nebuadservingurl\n");
  } else {
    print ("Failed to get nebuadservingurl script\n");
  }

  print ("Checking Ad rendering:\t\t");

  $mech->get($nebuadservingurl);
  my $fetchedpagens = $mech->content();
  #print ("$fetchedpagens\n");
  if ($fetchedpagens =~ m/automotive_300.jpg/g) {
    print ("Passed\tGot Automotive Ad\n");
  } else {
    print ("Failed\tDid not get Automotive Ad\n");
  }
}
