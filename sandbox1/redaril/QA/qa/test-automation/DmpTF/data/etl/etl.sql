==RT_DMP_USER_MODEL
	SELECT * from RT_DMP_USER_MODEL 
		WHERE created_date_time > TO_DATE('[sysdate]','dd.mm.yy') 
		ORDER BY  created_date_time desc;	
==RT_DMP_USER_MODEL

==RT_USER_DATA_CALL
	SELECT * from RT_USER_DATA_CALL 
		WHERE created_date_time >  TO_DATE('[sysdate]','dd.mm.yy') 
		ORDER BY  created_date_time desc;	
==RT_USER_DATA_CALL

==RT_DMP_SUPPLIED_DATA
	SELECT * from RT_DMP_SUPPLIED_DATA
		WHERE created_date_time >  TO_DATE('[sysdate]','dd.mm.yy') 
		ORDER BY  created_date_time desc;	
==RT_DMP_SUPPLIED_DATA

==RT_USER_BSNS_DATA_LOG
	SELECT * from RT_USER_BSNS_DATA_LOG 
		WHERE created_date_time >  TO_DATE('[sysdate]','dd.mm.yy')
		ORDER BY  created_date_time desc;
==RT_USER_BSNS_DATA_LOG		

==RT_EXCHANGE_MAPPING_CALL
	SELECT * from RT_EXCHANGE_MAPPING_CALL 
            WHERE created_date_time >  TO_DATE('[sysdate]','dd.mm.yy')
                   ORDER BY  created_date_time desc;
==RT_EXCHANGE_MAPPING_CALL