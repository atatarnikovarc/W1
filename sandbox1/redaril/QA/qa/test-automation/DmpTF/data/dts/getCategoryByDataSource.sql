select id, external_id from category where data_source = ( select id from data_source where name like '%{0}%') and deleted=0 and buyable=1