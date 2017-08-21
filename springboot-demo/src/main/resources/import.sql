-- MySQL style
-- drop table city;
-- create table city (id int primary key auto_increment, name varchar(30), `state` varchar(5), country varchar(30));
-- insert into city (name, state, country) values ('San Francisco', 'CA', 'US');

-- drop table if exists hotel;
-- create table hotel (city int, name varchar(30), address varchar(80), zip varchar(16));
-- insert into hotel(city, name, address, zip) values (1, 'Conrad Treasury Place', 'William & George Streets', '4001');

-- H2 style
drop table if exists city;
drop table if exists hotel;

create table city (id int primary key auto_increment, name varchar, state varchar, country varchar);
create table hotel (city int, name varchar, address varchar, zip varchar);

insert into city (name, state, country) values ('San Francisco', 'CA', 'US');
insert into hotel(city, name, address, zip) values (1, 'Conrad Treasury Place', 'William & George Streets', '4001')