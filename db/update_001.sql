drop table if exists employees;
create table if not exists employees (
    id serial primary key not null,
    name varchar(100) not null,
    surname varchar (100) not null,
    individualTaxNumber numeric not null unique,
    hiredDate timestamp without time zone not null default now()
);
insert into employees (name, surname, individualTaxNumber) VALUES ('Bob', 'Smith', 123456789123);
insert into employees (name, surname, individualTaxNumber) VALUES ('Tom', 'Sleep', 234567891234);
insert into employees (name, surname, individualTaxNumber) VALUES ('Mike', 'Taison', 345678912345);

drop table if exists person;
create table if not exists person (
    id       serial primary key not null,
    login    varchar(2000),
    password varchar(2000),
    employee_id int references employees(id)
);

insert into person (login, password, employee_id)
values ('parsentev', '123', 1);
insert into person (login, password, employee_id)
values ('petrov', '123', 1);
insert into person (login, password, employee_id)
values ('ban', '123', 2);
insert into person (login, password, employee_id)
values ('ivanov', '123', 2);
insert into person (login, password, employee_id)
values ('ivan', '123', 3);
insert into person (login, password, employee_id)
values ('ivanov', '123', 3);
