drop table if exists toCheck;
create table if not exists toCheck (rowID INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, url TEXT, interest TEXT, interestID TEXT, dataSource  TEXT, unique(id) on conflict replace);
create table if not exists checked (rowID INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, url TEXT, interest TEXT, interestID TEXT, dataSource TEXT, unique(id) on conflict replace);
