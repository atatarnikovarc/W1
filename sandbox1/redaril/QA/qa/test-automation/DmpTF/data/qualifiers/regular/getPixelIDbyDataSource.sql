select tag_id 
from data_pixel 
inner join pixel using (id) 
inner join data_source 
on (data_source.data_owner=data_pixel.data_owner) 
where data_source.id={0}
and rownum <= 1
