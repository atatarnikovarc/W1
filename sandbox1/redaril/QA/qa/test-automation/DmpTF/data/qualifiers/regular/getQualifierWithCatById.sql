SELECT INTName, nt_qualifier.qualifier_id, nt_qualifier.interest_id, INTSource
FROM nt_qualifier 
JOIN (select interest_id as IntId, data_source as INTSource,name as INTName from nc_interest)
ON  nt_qualifier.interest_id = IntId
WHERE nt_qualifier.qualifier_id = {0}