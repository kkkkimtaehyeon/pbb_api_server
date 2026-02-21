alter table product add column version bigint default 0 not null;
alter table orders add column strategy_type varchar(255) null;