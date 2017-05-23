select c.id, c.external_id from category c where c.data_source in (
select d.data_source from exchange e inner join data_transfer_exchange d on e.id = d.id where e.exchange_id = {0})
and c.deleted is null and c.buyable = 1;