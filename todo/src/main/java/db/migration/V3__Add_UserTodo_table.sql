create table "UserTodo" (
    id int8 not null,
    "userId" int8 references "User"(id),
    title text not null,
    completed boolean not null,
    primary key (id)
)