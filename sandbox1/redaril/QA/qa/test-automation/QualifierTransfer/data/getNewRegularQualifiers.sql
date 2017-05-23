SELECT nt_qualifier.base_url, nc_interest.name, nt_qualifier.qualifier_id, nt_qualifier.interest_id
FROM nt_qualifier JOIN nc_interest ON nc_interest.interest_id = nt_qualifier.interest_id
WHERE nt_qualifier.created_date_time >= TO_DATE('{DATE}','dd.mm.yy') 
	AND nt_qualifier.created_by_user_id = 107 
	AND nt_qualifier.status_code_id = 95
	ORDER BY nt_qualifier.created_date_time DESC