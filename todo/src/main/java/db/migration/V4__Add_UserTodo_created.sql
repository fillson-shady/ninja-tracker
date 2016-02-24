alter table "UserTodo"
add column created timestamp with time zone not null
default now()