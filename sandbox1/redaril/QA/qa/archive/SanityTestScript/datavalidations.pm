require Exporter;
our @ISA = ("Exporter");


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

    print ("parent_qualifier_id column\t");
    print ("TBD\n");


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


  $sth4 = $dbh4->prepare("select user_model_id, user_identifier, user_identifier_type, created_date_time, modified_date_time FROM nc_user_model order by created_date_time desc");
  $sth4->execute();
  while (my ($user_model_id, $user_identifier, $user_identifier_type, $created_date_time, $modified_date_time) = $sth4->fetchrow()) {

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

#    print ("event_count\t\t\t");
#    if ($event_count =~ m/\d/g) {
#      print ("Passed\t$event_count\n");
#    } else {
#      print ("Failed<--\t$event_count\n");
#    }

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
  $sth1 = $dbh1->prepare("SELECT count(*) FROM logdb.nc_user_page_visit where page_status = 200 and url not like '%nebuad.com/images%' and referer_url not like '%a\?t=%' and url not like '%a\?t=%' and user_page_visit_id > '$maxpagevisitidtouse' and injection_type = 0 and content_length = 0 and url not like '%track.searchignite.com%' and url not like '%qaas1.nebuad.com%' and url not like '%searchignite%' and  url not like '%favicon.ico%' and url not like '%ad.yieldmanager%' and url not like '%toolbarqueries%' and url not like '%sb.google.com%'  and url not like '%abc.nebuad.com%' ");
  $sth1->execute();
  my $count4 =  $sth1->fetchrow();
  if ($count4 > 0) {
    $testresult = 'Failed<--';
    print ("$testresult\n");
    my $sth2 = $dbh2->prepare("SELECT url FROM logdb.nc_user_page_visit where page_status = 200 and url not like '%nebuad.com/images%' and referer_url not like '%a\?t=%' and url not like '%a\?t=%' and user_page_visit_id > '$maxpagevisitidtouse' and injection_type = 0 and content_length = 0 and url not like '%track.searchignite.com%' and url not like '%qaas1.nebuad.com%' and url not like '%searchignite%' and  url not like '%favicon.ico%' and url not like '%ad.yieldmanager%' and url not like '%toolbarqueries%' and url not like '%sb.google.com%'  and url not like '%abc.nebuad.com%' ");
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
  my ($testresult, $line, $hipid, $userid);

  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.live.com",
			      );
  $sel->start();

  print ("Go to live.com\t\t\t");
  my $url = "http://www.live.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Go to ad page\t\t\t");
  $url = $testpageurl;
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Get cookie u value\t\t");

  my $cu;
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
    if ($cookie_user_id =~ m/\d/) {
      $cu = $cookie_user_id;
      last;
    }
    $i--;
  }

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



  $url = "http://www.usedcars.com/";
  print ("Go to usedcars page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://www.autobytel.com/";
  print ("Go to autobytel page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://www.usedcars.com/";
  print ("Go to usedcars page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);


  print ("Go to Yahoo\t\t\t");
  $url = "http://www.yahoo.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for automotive\t\t");
  $sel->type("p", "automotive");
  $sel->click("searchsubmit");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for cars\t\t\t");
  $sel->type("yschsp", "cars");
  $sel->click("y");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Go to ad page\t\t\t");
  $url = $testpageurl;
  $sel->open("$url");
  print ("OK\n");
  sleep (10);


  #get user interest fine
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu"."&v";
  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  #print ("$url\n");
  sleep (5);
  if ($sel->is_text_present("cars")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler fine Memory test\t$testresult\n");
  sleep (13);


  print ("resetting fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");


  print ("verify no fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu"."&v";


  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  if ($sel->is_text_present("cars")) {
    $testresult = 'Failed<--';
  } else {
    $testresult = 'Passed';
  }
  print ("OK\n");
  print ("UserModeler fine Memory Init\t$testresult\n");
  sleep (10);

  print ("Go to peoplemeet\t\t");
  $url = "http://www.peoplemeet.com";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  print ("Go to primesingles\t\t");
  $url = "http://www.primesingles.net";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  print ("Go to people2people\t\t");
  $url = "http://www.people2people.com";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

###
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
  sleep (80);

  print ("Go to ad page\t\t\t");
  $url = $testpageurl;
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu"."&v";

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

  $sel->open("$url");
  sleep (10);
  print ("OK\n");


  $url = "http://www.usedcars.com/";
  print ("Go to usedcars page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://www.autobytel.com/";
  print ("Go to autobytel page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://www.usedcars.com/";
  print ("Go to usedcars page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Go to Yahoo\t\t\t");
  $url = "http://www.yahoo.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for automotive\t\t");
  $sel->type("p", "automotive");
  $sel->click("searchsubmit");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for cars\t\t\t");
  $sel->type("yschsp", "cars");
  $sel->click("y");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu"."&v";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  #print ("$url\n");
  sleep (5);
  if ($sel->is_text_present("cars")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler fine Memory test\t$testresult\n");
  sleep (13);

  print ("resetting fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&cu="."$cu"."&r=yes";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  print ("verify no fine memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u=none&cu="."$cu";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  if ($sel->is_text_present("cars")) {
    $testresult = 'Failed<--';
  } else {
    $testresult = 'Passed';
  }
  print ("OK\n");
  print ("UserModeler fine Memory reset\t$testresult\n");
  sleep (10);
  $sel->stop();
}


sub checkcoarseprofile {
  my ($testresult, $line, $hipid, $userid);

  $hipid = &gethipid();
  print ("hipid is $hipid\n");

  my $sel = WWW::Selenium->new( host => "localhost", 
				port => 4444, 
				browser => "*iehta", 
				browser_url => "http://www.google.com",
			      );
  $sel->start();


  $url = "http://www.usedcars.com/";
  print ("Go to usedcars page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://www.autobytel.com/";
  print ("Go to autobytel page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://www.usedcars.com/";
  print ("Go to usedcars page\t\t");
  $sel->open("$url"); 
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);


  print ("Go to Yahoo\t\t\t");
  $url = "http://www.yahoo.com";
  $sel->open("$url");
  print ("OK\n");
  sleep (10);

  print ("Search for automotive\t\t");
  $sel->type("p", "automotive");
  $sel->click("searchsubmit");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  print ("Search for cars\t\t\t");
  $sel->type("yschsp", "cars");
  $sel->click("y");
  $sel->wait_for_page_to_load("30000");
  print ("OK\n");
  sleep (10);

  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&v";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  print ("$url\n");
  sleep (5);
  if ($sel->is_text_present("Auto")) {
    $testresult = 'Passed';
  } elsif  ($sel->is_text_present("cars")) {
    $testresult = 'Passed';
  } else {
    $testresult = 'Failed<--';
  }
  print ("UserModeler coarse Memory test\t$testresult\n");
  sleep (10);


  print ("resetting coarse memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&r=yes";

  $sel->open("$url");
  sleep (10);
  print ("OK\n");


  print ("verify no coarse memory\t\t");
  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&v";

  $sel->open("$url");
  $sel->wait_for_page_to_load("30000");
  if ($sel->is_text_present("Auto")) {
    $testresult = 'Failed<--';
  } else {
    $testresult = 'Passed';
  }
  print ("OK\n");
  print ("UserModeler coarse Memory Init\t$testresult\n");
  sleep (10);

  print ("Go to peoplemeet\t\t");
  $url = "http://www.peoplemeet.com";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  print ("Go to primesingles\t\t");
  $url = "http://www.primesingles.net";
  $sel->open("$url");
  sleep (10);
  print ("OK\n");

  print ("Go to people2people\t\t");
  $url = "http://www.people2people.com";
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
  sleep (80);


  $url = "http://"."$umserver".":8080/usermodeler/getcategories?c="."$certername"."&d=0000007&u="."$hipid"."&v";

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

  $sel->open("$url");
  sleep (10);
  print ("OK\n");
  $sel->stop();
}



sub checkpagestatusfromtables {
  my $counter200 = 0;
  my $counter304 = 0;
  my $counter302 = 0;

  my $data_file="upvrd";
  open(DAT, $data_file) || die("Could not open file!");
  my @lines=<DAT>;
  close(DAT);

  my $numberoflines = @lines;
  #  print ("number of lines:\t$numberoflines\n");

  my $i = $numberoflines -1;
  while ($i > 0) {
    $_ = $lines[$i];
    #print ("$lines[$i]\n");
    chomp;
    my ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $host, $url, $referer_url, $location, $form_data, $type, $page_status, $accept, $cache_control, $content_encoding, $content_language, $content_location, $content_type, $expires, $page_last_modified, $pragma, $is_dynamic, $priority, $response_time_millisecond, $request_method, $certer_id, $certer_version, $injection_type, $content_length, $current_length_header, $url_hash, $referer_hash, $location_hash) = split("\t");


    if ($page_status eq '200') {
      $counter200++;
    }
    if ($page_status eq '302') {
      $counter302++;
    }
    if ($page_status eq '304') {
      $counter304++;
    }

    $i--;
  }

  if (($counter200 >= 1) and ($counter302 >= 1) and ($counter304 >= 1)) {
    my $testresult = 'Passed';
    print ("PageStatus Mix\t\t\t$testresult\n");
  } else {
    my $testresult = 'Failed<--';
    print ("PageStatus Mix\t\t\t$testresult\n");
  }
}





sub checksomelargepages {
  print ("Checking large pages:\t\t");
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
  sleep(15);

  &ftpgetfiles();
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
    my ($created_date_time, $request_time_millisecond, $user_page_visit_guid, $host, $url, $referer_url, $location, $form_data, $type, $page_status, $accept, $cache_control, $content_encoding, $content_language, $content_location, $content_type, $expires, $page_last_modified, $pragma, $is_dynamic, $priority, $response_time_millisecond, $request_method, $certer_id, $certer_version, $injection_type, $content_length, $current_length_header, $url_hash, $referer_hash, $location_hash) = split("\t");
    if ($content_length == 25000) {
      print ("Pass\t$url\t$content_length\n");
      return;
    }
    #print "$url\t$content_length\n";
    $i--;
  }
  print ("Failed\n");
}


sub checkuserpagevisitstablegetsupdated {
  my $upvrdlogcount0 = &readfileupvrd();
  print ("upvrdlogcount0:\t\t\t$upvrdlogcount0\n");

  my $testresult;
  my $mech = WWW::Mechanize->new();
  $mech->agent_alias( 'Windows IE 6' );
  $mech->cookie_jar(HTTP::Cookies->new());
  my $url0 = "$testpageurl";
  print ("Browsing...\t\t\t");
  my $i = 0;
  while ($i < 5) {
    $mech->get($url0);
    #my $fetchedpage = $mech->content();
    sleep(5);
    print (".");
    $i++;
  }
  print ("\n");
  sleep (10);

  my $upvrdlogcount1 = &readfileupvrd();
  print ("upvrdlogcount1:\t\t\t$upvrdlogcount1\n");
  if ($upvrdlogcount1 > $upvrdlogcount0) {
    $testresult = 'Passed';
    #print ("$testresult\n");
  } else {
    $testresult = 'Failed<--';
  }
  print ("upvrd file updated\t\t");
  print ("$testresult\n");
  sleep (3);
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



    #Test 10
    my $numtype;
    print ("type\t\t\t\t");
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


    #Test 37
    print ("content_length\t\t\t");
    if ($content_length > 0) {
      print ("Passed\t$content_length\n");
    } else {
      print ("Failed<--\t$content_length\n");
    }


    #Test 38: Verify t=p tags are audited in the database

    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_page_visit where url like '%t=p%' and url not like '%promo%' and user_page_visit_id > $idtostartwatching");
    $sth2->execute();
    while (my($tequalpyesno) = $sth2->fetchrow()) {
      if ($tequalpyesno > 0) {
	print ("t=p logged or not\t\tPassed\n");
      } else {
	print ("t=p logged or not\t\tFailed<--or going through 8080 so certer ignoring.\n");
      }
    }
    $sth2->finish();


    #Test 39: Verify t=s tags are audited in the database

    $sth2 = $dbh2->prepare("select count(*) from logdb.nc_user_page_visit where url like '%t=s%' and url not like '%suggest%' and url not like '%t=suv%' and url not like '%sigpro%' and user_page_visit_id > $idtostartwatching");
    $sth2->execute();
    while (my($tequalsyesno) = $sth2->fetchrow()) {
      if ($tequalsyesno > 4) {
	print ("t=s logged or not\t\tPassed\n");
      } else {
	print ("t=s logged or not\t\tFailed<--or going through 8080 so certer ignoring.\n");
      }
    }
    $sth2->finish();


    #Test 40:


  }
  $sth1->finish();
}

