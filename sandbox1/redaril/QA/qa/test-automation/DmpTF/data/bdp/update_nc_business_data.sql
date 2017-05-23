update nc_business_data set
industry_code={0},
business_size={1},
update_date=TO_DATE({2},'dd.mm.yy'),
value_min={3},
value_max={4},
zip={5},
city={6},
business_name={7}
where business_name = {8}