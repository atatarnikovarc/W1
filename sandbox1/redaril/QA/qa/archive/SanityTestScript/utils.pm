require Exporter;
our @ISA = ("Exporter");

sub clearoutncusertables {
  print ("Clearing out nc_user tables\tOK\n");
  my $sth4 = $dbh4->prepare("delete from nc_user");
  $sth4->execute();
  $sth4 = $dbh4->prepare("delete from nc_user_odc");
  $sth4->execute();
  $sth4 = $dbh4->prepare("delete from nc_user_model");
  $sth4->execute();
  $sth4 = $dbh4->prepare("commit");
  $sth4->execute();
  &imflush();
}


sub gethipid {
  my ($hipid, $line);
  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.google.com",
			      );
  $sel->start();

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
      #print ("hipid is $hipid\n");
    }
  }

  if ($hipid eq "") {
    print ("couldn't get the hipid\n");
    die;
  }
  $sel->stop();
  return $hipid;
}







sub getinitialmeasurements {
  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.google.com",
			      );
  $sel->start();

  print ("clearing my prev nc_user info\t");
  my $url = "http://"."$umserver".":8080/usermodeler/getcategories?p";

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


  print ("OK\n");

  print ("getting initial measurements\t");
  print ("OK\n");



  $userSessionlogcount = &readfileuserSession();

  print ("userSessionlogcount:\t\t$userSessionlogcount\n");

  $asRequestProdlogcount = &readfileasRequestProd();
  print ("asRequestProdlogcount:\t\t$asRequestProdlogcount\n");

  $asRequestRdlogcount = &readfileasRequestRd();
  print ("asRequestRdlogcount:\t\t$asRequestRdlogcount\n");

  $umRequestProdlogcount = &readfileumRequestProd();
  print ("umRequestProdlogcount:\t\t$umRequestProdlogcount\n");

  $umRequestRdlogcount = &readfileumRequestRd();
  print ("umRequestRdlogcount:\t\t$umRequestRdlogcount\n");

  $userIdHistorylogcount = &readfileuserIdHistory();
  print ("userIdHistorylogcount:\t\t$userIdHistorylogcount\n");

  $userQualRdlogcount = &readfileuserQualRd();
  print ("userQualRdlogcount:\t\t$userQualRdlogcount\n");

  $userQualProdlogcount = &readfileuserQualProd();
  print ("userQualProdlogcount:\t\t$userQualProdlogcount\n");

  $upvrdlogcount = &readfileupvrd();
  print ("upvrdlogcount:\t\t\t$upvrdlogcount\n");

  $umEventRdlogcount = &readfileumEventRd();
  print ("umEventRdlogcount:\t\t$umEventRdlogcount\n");

  $umEventProdlogcount = &readfileumEventProd();
  print ("umEventProdlogcount:\t\t$umEventProdlogcount\n");

  $icSearchKwdlogcount = &readfileicSearchKwd();
  print ("icSearchKwdlogcount:\t\t$icSearchKwdlogcount\n");

  #$icSearchClicklogcount = &readfileicSearchClick();
  #print ("icSearchClicklogcount:\t\t$icSearchClicklogcount\n");

  $contentWriterlogcount = &readfilecontentWriter();
  print ("contentWriterlogcount:\t\t$contentWriterlogcount\n");



  my $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user");
  $sth4->execute();
  $ncusercount = $sth4->fetchrow();
  print ("nc_user count:\t\t\t$ncusercount\n");
  $sth4->finish();

  $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user_model");
  $sth4->execute();
  $ncusermodelcount = $sth4->fetchrow();
  print ("nc_user_model count:\t\t$ncusermodelcount\n");
  $sth4->finish();

  $sth4 = $dbh4->prepare("SELECT count(*) FROM nc_user_odc");
  $sth4->execute();
  $ncuserodccount = $sth4->fetchrow();
  print ("nc_user_odc count:\t\t$ncuserodccount\n");
  $sth4->finish();


  $sel->stop();
}



sub getfinalmeasurements {
  print ("getting final measurements\t");
  print ("OK\n");

  my $contentWriterlogcount1 = &readfilecontentWriter();
  print ("contentWriterlogcount1:\t\t$contentWriterlogcount1\n");
  if (( $contentWriterlogcount1 > $contentWriterlogcount) and ($contentWriterlogcount > 0)) {
    print ("Content Writer log growth\tOK\n");
  } else {
    print ("content write log growth\tFailed\n");
  }

  my $userQualRdlogcount1 = &readfileuserQualRd();
  print ("userQualRdlogcount1:\t\t$userQualRdlogcount1\n");
  if (( $userQualRdlogcount1 > $userQualRdlogcount) and ($userQualRdlogcount > 0)) {
    print ("userQualRd log growth\t\tOK\n");
  } else {
    print ("userQualRd log growth\t\tFailed\n");
  }

  my $userQualProdlogcount1 = &readfileuserQualProd();
  print ("userQualProdlogcount1:\t\t$userQualProdlogcount1\n");
  if (( $userQualProdlogcount1 > $userQualProdlogcount) and ($userQualProdlogcount > 0)) {
    print ("userQualProd log growth\t\tOK\n");
  } else {
    print ("userQualProd log growth\t\tFailed\n");
  }


  my $userSessionlogcount1 = &readfileuserSession();
  print ("userSessionlogcount1:\t\t$userSessionlogcount1\n");
  if (( $userSessionlogcount1 > $userSessionlogcount) and ( $userSessionlogcount > 0)) {
    print ("userSessionlogcount log growth\tOK\n");
  } else {
    print ("userSessionlogcount log growth\tFailed\n");
  }



  my $umEventRdlogcount1 = &readfileumEventRd();
  print ("umEventRdlogcount1:\t\t$umEventRdlogcount1\n");
  if (( $umEventRdlogcount1 > $umEventRdlogcount) and ($umEventRdlogcount > 0)) {
    print ("umEventRdlogcount log growth\tOK\n");
  } else {
    print ("umEventRdlogcount log growth\tFailed\n");
  }

  my $umEventProdlogcount1 = &readfileumEventProd();
  print ("umEventProdlogcount1:\t\t$umEventProdlogcount1\n");
  if (( $umEventProdlogcount1 > $umEventProdlogcount) and ($umEventProdlogcount > 0)) {
    print ("umEventProdlogcount log growth\tOK\n");
  } else {
    print ("umEventProdlogcount log growth\tFailed\n");
  }


  my $umRequestRdlogcount1 = &readfileumRequestRd();
  print ("umRequestRdlogcount1:\t\t$umRequestRdlogcount1\n");
  if (( $umRequestRdlogcount1 > $umRequestRdlogcount) and ($umRequestRdlogcount > 0)) {
    print ("umRequestRdlogcount log growth\tOK\n");
  } else {
    print ("umRequestRdlogcount log growth\tFailed\n");
  }

  my $umRequestProdlogcount1 = &readfileumRequestProd();
  print ("umRequestProdlogcount1:\t\t$umRequestProdlogcount1\n");
  if (( $umRequestProdlogcount1 > $umRequestProdlogcount) and ( $umRequestProdlogcount  > 0)) {
    print ("umRequestProdlogcount loggrowth\tOK\n");
  } else {
    print ("umRequestProdlogcount loggrowth\tFailed\n");
  }


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

  return 1;

}



sub readfileuserSession {
  &ftpgetfiles();
  my ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $certer_id, $user_id, $domain, $domain_top, $domain_second_level, $od_cookie_domain_id, $od_cookie_identifier, $page_status, $raw_user_identifier, $user_cookie_identifier, $user_agent, $user_hipplus_identifier, $super_session_identifier, $cookies, $set_cookies, $batch_id);

  my $data_file="userSessionRd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $certer_id, $user_id, $domain, $domain_top, $domain_second_level, $od_cookie_domain_id, $od_cookie_identifier, $page_status, $raw_user_identifier, $user_cookie_identifier, $user_agent, $user_hipplus_identifier, $super_session_identifier, $cookies, $set_cookies, $batch_id) = split("\t");

    $i--;
  }
  return $numberoflines;
}


sub readfileuserQualRd {
  &ftpgetfiles();
  my ($certer_id, $created_date_time, $request_time_millisecond, $user_page_visit_guid, $is_internal, $qualifier_id, $interest_id, $domain_id, $interest_weight, $site_tree_category_id, $parent_qualifier_id, $qualifier_group_id);

  my $data_file="userQualRd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($certer_id, $created_date_time, $request_time_millisecond, $user_page_visit_guid, $is_internal, $qualifier_id, $interest_id, $domain_id, $interest_weight, $site_tree_category_id, $parent_qualifier_id, $qualifier_group_id) = split("\t");
    $i--;
  }
  return $numberoflines;
}


sub readfileuserQualProd {
  &ftpgetfiles();
  my ($certer_id, $created_date_time, $request_time_millisecond, $user_page_visit_guid, $is_internal, $qualifier_id, $interest_id, $domain_id, $interest_weight, $site_tree_category_id, $parent_qualifier_id, $qualifier_group_id);

  my $data_file="userQualProd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($certer_id, $created_date_time, $request_time_millisecond, $user_page_visit_guid, $is_internal, $qualifier_id, $interest_id, $domain_id, $interest_weight, $site_tree_category_id, $parent_qualifier_id, $qualifier_group_id) = split("\t");
    $i--;
  }
  return $numberoflines;
}


sub readfileupvrd {
  &ftpgetfiles();
  my ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $host, $url, $referer_url, $location, $form_data, $type, $page_status, $accept, $cache_control, $content_encoding, $content_language, $content_location, $content_type, $expires, $page_last_modified, $pragma, $is_dynamic, $priority, $response_time_millisecond, $request_method, $certer_id, $certer_version, $injection_type, $content_length, $current_length_header, $url_hash, $referer_hash, $location_hash);

  my $data_file="upvrd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $host, $url, $referer_url, $location, $form_data, $type, $page_status, $accept, $cache_control, $content_encoding, $content_language, $content_location, $content_type, $expires, $page_last_modified, $pragma, $is_dynamic, $priority, $response_time_millisecond, $request_method, $certer_id, $certer_version, $injection_type, $content_length, $current_length_header, $url_hash, $referer_hash, $location_hash) = split("\t");

    $i--;
  }
  return $numberoflines;
}

####
sub readfileasRequestProd {
  &ftpgetfiles();
  my ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_accept_language, $header_referer, $header_user_agent, $certer_identifier, $device_identifier, $user_identifier, $is_certer_user, $version, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $user_modeler_calltime_millis, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad, $connection_id, $certer_timestamp, $cookie_certer_identifier, $is_old, $aj_dim);

  my $data_file="asRequestProd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_accept_language, $header_referer, $header_user_agent, $certer_identifier, $device_identifier, $user_identifier, $is_certer_user, $version, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $user_modeler_calltime_millis, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad, $connection_id, $certer_timestamp, $cookie_certer_identifier, $is_old, $aj_dim) = split("\t");

    $i--;
  }
  return $numberoflines;
}


sub readfileasRequestRd {
  &ftpgetfiles();
  my ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_accept_language, $header_referer, $header_user_agent, $certer_identifier, $user_hipplus_identifier, $is_certer_user, $version, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad, $connection_id, $certer_timestamp, $is_old);

  my $data_file="asRequestRd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_accept_language, $header_referer, $header_user_agent, $certer_identifier, $user_hipplus_identifier, $is_certer_user, $version, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad, $connection_id, $certer_timestamp, $is_old) = split("\t");

    $i--;
  }
  return $numberoflines;
}



sub readfileumRequestProd {
  &ftpgetfiles();
  my ($created_date_time, $guid, $certer_id, $user_hipplus_id, $user_adserver_id, $action, $categories);

  my $data_file="umRequestProd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $guid, $certer_id, $user_hipplus_id, $user_adserver_id, $action, $categories) = split("\t");

    $i--;
  }
  return $numberoflines;
}



sub readfileumRequestRd {
  &ftpgetfiles();
  my ($created_date_time, $action, $ok, $certer_id, $user_hipplus_id, $user_adserver_id, $guid, $aux_categories, $categories);

  my $data_file="umRequestRd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $action, $ok, $certer_id, $user_hipplus_id, $user_adserver_id, $guid, $aux_categories, $categories) = split("\t");

    $i--;
  }
  return $numberoflines;
}



sub readfileuserIdHistory {
  &ftpgetfiles();
  my ($created_date_time, $merge_time_millisecond, $new_user_id, $old_user_id, $is_fine);

  my $data_file="userIdHistory";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $merge_time_millisecond, $new_user_id, $old_user_id, $is_fine) = split("\t");

    $i--;
  }
  return $numberoflines;
}



sub readfileumEventRd {
  &ftpgetfiles();
  my ($created_date_time, $certer_id, $user_page_visit_guid, $request_time_millisecond, $user_hipplus_id, $user_adserver_id, $user_interest);

  my $data_file="umEventRd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $certer_id, $user_page_visit_guid, $request_time_millisecond, $user_hipplus_id, $user_adserver_id, $user_interest) = split("\t");

    $i--;
  }
  return $numberoflines;
}


sub readfileumEventProd {
  &ftpgetfiles();
  my ($created_date_time, $request_time_millisecond, $certer_id, $user_page_visit_guid, $user_hipplus_id, $user_adserver_id, $user_interest);

  my $data_file="umEventProd";
  #open(DAT, $data_file) || die("Could not open file!");
  open(DAT, $data_file);

  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $request_time_millisecond, $certer_id, $user_page_visit_guid, $user_hipplus_id, $user_adserver_id, $user_interest) = split("\t");

    $i--;
  }
  return $numberoflines;

}

sub readfileicSearchKwd {
  &ftpgetfiles();
  my ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $domain_id, $search_expression, $is_general_search, $search_url, $qualifier_id, $is_duplicate);

  my $data_file="icSearchKwd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $domain_id, $search_expression, $is_general_search, $search_url, $qualifier_id, $is_duplicate) = split("\t");

    $i--;
  }
  return $numberoflines;
}


sub readfileicSearchClick {
  &ftpgetfiles();
  my ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $domain_id, $search_expression, $landing_url);

  my $data_file="icSearchClick";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $domain_id, $search_expression, $landing_url) = split("\t");

    $i--;
  }
  return $numberoflines;

}

sub readfilecontentWriter {
  &ftpgetfiles();
  my ($created_date_time, $visit_guid, $file_path);

  my $data_file="contentWriter";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 10)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    ($created_date_time, $visit_guid, $file_path) = split("\t");

    $i--;
  }
  return $numberoflines;
}



sub ftpgetfiles {
  use Net::FTP::Simple;
  my $username = "kamyar";
  my $password = "901nebuad";


  my @received_files = Net::FTP::Simple->retrieve_files({
							 username        => $username,
							 password        => $password,
							 server          => $ftpserver,
							 remote_dir      => '/var/log/etl',
							 debug_ftp       => 0,
							 files           => [
									     'contentWriter',
									     'asRequestProd',
									     'asRequestRd',
									     'as2RequestProd',
									     'as2RequestRd',
									     'umRequestProd',
									     'umRequestRd',
									     'userIdHistory',
									     'icSearchClick',
									     'icSearchKwd',
									     'umEventProd',
									     'umEventRd',
									     'upvrd',
									     'userQualProd',
									     'userQualRd',
									     'userSessionRd',
									     'userSessionProd'
									    ],
							});

  my $count2 = @received_files;
  #print ("$count2\n");
  #print ("$received_files[0]");
}


sub imflush {
  #my $ExecString = 'C:\Progra~1\
  my $ExecString;

  if ($clustername eq 'qacluster1') {
    $ExecString = 'imflushqac1.bat';
  } elsif ($clustername eq 'qacluster2') {
    $ExecString = 'imflushqac2.bat';
  }


  system $ExecString;

  print ("Reset Identity Manager, waiting 15 seconds....\n");
  sleep (15);
}



sub gotourls {
  my $urla = shift;
  my $urlb = shift;
  my $urlc = shift;

  print ("Browsing\t\t\t$urla..\n");
  print ("Browsing\t\t\t$urlb..\n");
  print ("Browsing\t\t\t$urlc..");
  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.google.com",
			      );
  $sel->start();


  my $k = 0;
  while ($k < 3) {
    print (".");
    $sel->open("$urla"); 
    $sel->wait_for_page_to_load("60000");
    sleep (5);
    $sel->open("$urlb"); 
    $sel->wait_for_page_to_load("60000");
    sleep (5);
    $sel->open("$urlc"); 
    $sel->wait_for_page_to_load("60000");
    sleep (5);
    $k++;
  }
  print ("\n");
  $sel->stop();
}


sub gotourl {
  my $url = shift;

  print ("Browsing\t\t\t$url...");
  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.google.com",
			      );
  $sel->start();

  print (".");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  sleep (5);
  print ("\n");
  $sel->stop();
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
  my $testresult = 'Failed';

  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.google.com",
			      );
  $sel->start();


  print ("Go to ad page\t\t\t");
  my $url = $testpageurl;
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  $sel->stop();

  &ftpgetfiles();

  print ("Certer Ad Mods logged\t\t");


  my $data_file="asRequestProd";
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
    my ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_accept_language, $header_referer, $header_user_agent, $certer_identifier, $device_identifier, $user_identifier, $is_certer_user, $version, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $user_modeler_calltime_millis, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad, $connection_id, $certer_timestamp, $cookie_certer_identifier, $is_old, $aj_dim) = split("\t");

    if ($ad_type eq "p") {
      $testresult = 'Passed';
      #print ("$testresult\n");
      last;
    }
    $i--;
  }
  print ("$testresult\n");
}


sub getcuas2 {
  &ftpgetfiles();
  my $cu;

  my ($upvg, $uid, $odlivecookie, $ssid, $uci);
  #my $data_file="umrequestProd";
  my $data_file="as2RequestProd";

  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 100)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    #my ($created_date_time, $guid, $certer_id, $user_hipplus_id, $user_adserver_id, $action, $categories) = split("\t");

    my ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_accept_language, $header_referer, $header_user_agent, $certer_identifier, $device_identifier, $user_identifier, $is_certer_user, $version, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $user_modeler_calltime_millis, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad, $connection_id, $certer_timestamp, $cookie_certer_identifier, $is_old, $aj_dim) = split("\t");

    if (($cookie_user_id =~ m/\d/) and ($request_url =~ m/qacluster/)) {
      $cu = $cookie_user_id;
      last;
    }


#    if (($user_adserver_id =~ m/\d/) and ($user_adserver_id > 0)) {
#      $cu = $user_adserver_id;
#      last;
#    }

    $i--;
  }

  print ("cookieId\t\t\t$cu\n");
  unless ($cu =~ m/\d/g) {
    print ("Couldn't get cookie value\n");
    die;
  }
  sleep (5);
  return $cu;


}




sub getcu {
  &ftpgetfiles();
  my $cu;

  my ($upvg, $uid, $odlivecookie, $ssid, $uci);
  #my $data_file="umrequestProd";
  my $data_file="asRequestProd";

  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > ($numberoflines - 100)) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    #my ($created_date_time, $guid, $certer_id, $user_hipplus_id, $user_adserver_id, $action, $categories) = split("\t");

    my ($created_date_time, $guid, $ok, $cookie_user_id, $is_repeat_user, $remote_address, $request_url, $query_string, $header_accept_language, $header_referer, $header_user_agent, $certer_identifier, $device_identifier, $user_identifier, $is_certer_user, $version, $ad_type, $categories, $spot_id, $campaign_id, $ad_id, $user_modeler_calltime_millis, $yield_manager_calltime_millis, $request_handling_time_millis, $shown_ad, $connection_id, $certer_timestamp, $cookie_certer_identifier, $is_old, $aj_dim) = split("\t");

    if (($cookie_user_id =~ m/\d/) and ($request_url =~ m/qacluster/)) {
      $cu = $cookie_user_id;
      last;
    }


#    if (($user_adserver_id =~ m/\d/) and ($user_adserver_id > 0)) {
#      $cu = $user_adserver_id;
#      last;
#    }

    $i--;
  }

  print ("cookieId\t\t\t$cu\n");
  unless ($cu =~ m/\d/g) {
    print ("Couldn't get cookie value\n");
    die;
  }
  sleep (5);
  return $cu;


}

