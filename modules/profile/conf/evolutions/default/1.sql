# --- !Ups

create table "people" (
  "id" bigserial not null primary key,
  "name" varchar not null,
  "content" JSONB not null
);

# --- !Downs

drop table "people";
