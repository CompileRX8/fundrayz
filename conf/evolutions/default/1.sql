-- noinspection SqlNoDataSourceInspectionForFile
# SECURITY SCHEMA

# --- !Ups

CREATE TABLE login_info (
  id           BIGSERIAL PRIMARY KEY,
  provider_id  TEXT NOT NULL,
  provider_key TEXT NOT NULL,
  UNIQUE (provider_id, provider_key)
);

CREATE TABLE password_info (
  id            BIGSERIAL PRIMARY KEY,
  login_info_id BIGINT NOT NULL UNIQUE REFERENCES login_info (id) ON DELETE CASCADE,
  hasher        TEXT   NOT NULL,
  password      TEXT   NOT NULL,
  salt          TEXT
);

CREATE TABLE oauth1_info (
  id            BIGSERIAL PRIMARY KEY,
  login_info_id BIGINT NOT NULL UNIQUE REFERENCES login_info (id) ON DELETE CASCADE,
  token         TEXT   NOT NULL,
  secret        TEXT   NOT NULL
);

CREATE TABLE oauth2_info (
  id            BIGSERIAL PRIMARY KEY,
  login_info_id BIGINT NOT NULL UNIQUE REFERENCES login_info (id) ON DELETE CASCADE,
  access_token  TEXT   NOT NULL,
  token_type    TEXT,
  expires_in    INT,
  refresh_token TEXT
);

CREATE TABLE oauth2_info_params (
  oauth2_info_id BIGINT NOT NULL REFERENCES oauth2_info (id) ON DELETE CASCADE,
  param_name     TEXT   NOT NULL,
  param_value    TEXT   NOT NULL,
  UNIQUE (param_name, param_value)
);

CREATE TABLE openid_info (
  id            BIGSERIAL PRIMARY KEY,
  login_info_id BIGINT NOT NULL UNIQUE REFERENCES login_info (id) ON DELETE CASCADE,
  open_id       TEXT   NOT NULL
);

CREATE TABLE openid_info_attributes (
  openid_info_id  BIGINT NOT NULL REFERENCES openid_info (id) ON DELETE CASCADE,
  attribute_name  TEXT   NOT NULL,
  attribute_value TEXT   NOT NULL,
  UNIQUE (attribute_name, attribute_value)
);

CREATE TABLE user_info (
  id            BIGSERIAL PRIMARY KEY,
  user_id       UUID UNIQUE,
  login_info_id BIGINT NOT NULL REFERENCES login_info (id) ON DELETE CASCADE,
  first_name    TEXT,
  last_name     TEXT,
  full_name     TEXT,
  email         TEXT,
  avatar_url    TEXT,
  email_token   TEXT
);

# --- !Downs

DROP TABLE user_info;
DROP TABLE openid_info_attributes;
DROP TABLE openid_info;
DROP TABLE oauth2_info_params;
DROP TABLE oauth2_info;
DROP TABLE oauth1_info;
DROP TABLE password_info;
DROP TABLE login_info;
