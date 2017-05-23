select sum(COUN) from nt_qualifier
INNER JOIN (select count(1) AS COUN, qualifier_id as qId from nt_qualifier_phrase where phrase_opcode_id='92'
group by qualifier_id) ON qualifier_id = qId where coun>1 and qualifier_type_id='55' and status_code_id='95'