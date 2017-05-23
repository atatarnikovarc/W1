require Exporter;
our @ISA = ("Exporter");


sub readinParams {
  #my $data_file="C:\\nebuad\\ACTIVE_PROJECTS\\PerlScripts\\SanityTestScript\\config\\qac1_config.txt";
  my $data_file="C:\\Projects\\Scripts\\SanityTestScript\\config\\qac1_config.txt";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #print ("number of lines:\t\t$numberoflines\n");

  my $i = 0;
  while ($i < ($numberoflines)) {
    $_ = $lines[$i];
    if ($_ =~ m/clustername = [a-z]+\d/g) {
      chomp $_;
      $clustername = $_ ;
      $clustername =~ s/clustername = // ;
    }

    if ($_ =~ m/databaseserver = .+/g) {
      chomp $_;
      $databaseserver = $_ ;
      $databaseserver =~ s/databaseserver = // ;
    }

    if ($_ =~ m/certername = [a-z]+\d?/g) {
      chomp $_;
      $certername = $_ ;
      $certername =~ s/certername = // ;
    }

    if ($_ =~ m/umserver = .+/g) {
      chomp $_;
      $umserver = $_ ;
      $umserver =~ s/umserver = // ;
    }

    if ($_ =~ m/icserver = .+/g) {
      chomp $_;
      $icserver = $_ ;
      $icserver =~ s/icserver = // ;
    }

    if ($_ =~ m/yieldmanager = .+/g) {
      chomp $_;
      $yieldmanager = $_ ;
      $yieldmanager=~ s/yieldmanager = // ;
    }

    if ($_ =~ m/adserver = .+/g) {
      chomp $_;
      $adserver = $_ ;
      $adserver =~ s/adserver = // ;
    }

    if ($_ =~ m/virtualcerter = .+/g) {
      chomp $_;
      $virtualcerter = $_ ;
      $virtualcerter =~ s/virtualcerter = // ;
    }

    if ($_ =~ m/certer_id = \d+/g) {
      chomp $_;
      $certer_id = $_ ;
      $certer_id =~ s/certer_id = // ;
    }

    if ($_ =~ m/buildversion = .+/g) {
      chomp $_;
      $buildversion = $_ ;
      $buildversion =~ s/buildversion = // ;
    }

    if ($_ =~ m/deviceid = .+/g) {
      chomp $_;
      $deviceid = $_ ;
      $deviceid =~ s/deviceid = // ;
    }

    if ($_ =~ m/testpageurl = .+/g) {
      chomp $_;
      $testpageurl = $_ ;
      $testpageurl  =~ s/testpageurl = // ;
    }

    if ($_ =~ m/utacentralurl = .+/g) {
      chomp $_;
      $utacentralurl = $_ ;
      $utacentralurl =~ s/utacentralurl = // ;
    }

    if ($_ =~ m/adcentralurl = .+/g) {
      chomp $_;
      $adcentralurl = $_ ;
      $adcentralurl =~ s/adcentralurl = // ;
    }

    if ($_ =~ m/optouturl = .+/g) {
      chomp $_;
      $optouturl = $_ ;
      $optouturl =~ s/optouturl = // ;
    }

    if ($_ =~ m/optinurl = .+/g) {
      chomp $_;
      $optinurl = $_ ;
      $optinurl =~ s/optinurl = // ;
    }


    if ($_ =~ m/ftpserver = .+/g) {
      chomp $_;
      $ftpserver = $_ ;
      $ftpserver =~ s/ftpserver = // ;
    }

    if ($_ =~ m/dbh1 = .+/g) {
      chomp $_;
      $dbh1 = $_ ;
      $dbh1 =~ s/dbh1 = // ;
    }

    if ($_ =~ m/dbh2 = .+/g) {
      chomp $_;
      $dbh2 = $_ ;
      $dbh2 =~ s/dbh2 = // ;
    }

    if ($_ =~ m/dbh3 = .+/g) {
      chomp $_;
      $dbh3 = $_ ;
      $dbh3 =~ s/dbh3 = // ;
    }

    if ($_ =~ m/dbh4 = .+/g) {
      chomp $_;
      $dbh4 = $_ ;
      $dbh4 =~ s/dbh4 = // ;
    }

    if ($_ =~ m/dbh5 = .+/g) {
      chomp $_;
      $dbh5 = $_ ;
      $dbh5 =~ s/dbh5 = // ;
    }

    if ($_ =~ m/dbh6 = .+/g) {
      chomp $_;
      $dbh6 = $_ ;
      $dbh6 =~ s/dbh6 = // ;
    }

    if ($_ =~ m/dbh7 = .+/g) {
      chomp $_;
      $dbh7 = $_ ;
      $dbh7 =~ s/dbh7 = // ;
    }

    $i++;
  }

  print ("clustername:\t\t\t$clustername\n");
  print ("databaseserver:\t\t\t$databaseserver\n");
  print ("certername:\t\t\t$certername\n");
  print ("umserver:\t\t\t$umserver\n");
  print ("icserver:\t\t\t$icserver\n");
  print ("yieldmanager:\t\t\t$yieldmanager\n");
  print ("adserver:\t\t\t$adserver\n");
  print ("virtualcerter:\t\t\t$virtualcerter\n");
  print ("certer_id:\t\t\t$certer_id\n");
  print ("buildversion:\t\t\t$buildversion\n");
  print ("deviceid:\t\t\t$deviceid\n");
  print ("testpageurl:\t\t\t$testpageurl\n");
  print ("utacentralurl:\t\t\t$utacentralurl\n");
  print ("adcentralurl:\t\t\t$adcentralurl\n");
  print ("optouturl:\t\t\t$optouturl\n");
  print ("optinurl:\t\t\t$optinurl\n");
  print ("ftpserver:\t\t\t$ftpserver\n");

  return ($clustername, $databaseserver, $certername, $umserver, $icserver, $yieldmanager, $adserver, $virtualcerter, $certer_id, $buildversion, $deviceid, $testpageurl, $utacentralurl, $adcentralurl, $optouturl, $optinurl, $ftpserver);
}
