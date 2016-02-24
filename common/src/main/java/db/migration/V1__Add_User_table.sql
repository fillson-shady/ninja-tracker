create table "User" (
    id int8 not null,
    email text not null,
    name text not null,
    passwordHash bytea not null,
    salt bytea not null,
    primary key (id)
)