SELECT nt_qualifier.base_url, INTName, nt_qualifier.qualifier_id, nt_qualifier.interest_id, INTSource
FROM nt_qualifier 
JOIN (select interest_id as IntId, data_source as INTSource,name as INTName from nc_interest where nc_interest.category_identifier not like '%DEACTIVATED%')
ON  nt_qualifier.interest_id = IntId
JOIN (select qualifier_type_id AS QualifierType from nt_qualifier_type	where nt_qualifier_type.is_enabled='Y' and nt_qualifier_type.qualifier_type_id=55)
on nt_qualifier.qualifier_type_id = QualifierType
WHERE (nt_qualifier.created_date_time >= TO_DATE('{0}','dd.mm.yyyy') and nt_qualifier.created_date_time < TO_DATE('{1}','dd.mm.yyyy')
    {2}	)AND (nt_qualifier.status_code_id = 95 OR nt_qualifier.status_code_id = 93) AND
    ((nt_qualifier.probability_lower_bound=0 AND nt_qualifier.probability_upper_bound=100) OR
    (nt_qualifier.probability_lower_bound is null AND nt_qualifier.probability_upper_bound=100) OR
     (nt_qualifier.probability_lower_bound=0 AND nt_qualifier.probability_upper_bound is null) OR
     (nt_qualifier.probability_lower_bound is null AND nt_qualifier.probability_upper_bound is null))
	ORDER BY nt_qualifier.created_date_time DESC