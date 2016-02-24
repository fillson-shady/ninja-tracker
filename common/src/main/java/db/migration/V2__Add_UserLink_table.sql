create table "UserLink" (
    id int8 not null,
    "userId" int8 references "User"(id),
    "linkedUserId" int8 references "User"(id),
    primary key (id),
    CONSTRAINT UNIQUE_USER_LINK UNIQUE ("userId", "linkedUserId")
)