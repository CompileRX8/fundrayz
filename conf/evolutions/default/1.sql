-- noinspection SqlNoDataSourceInspectionForFile
# SECURITY SCHEMA

# --- !Ups

CREATE TABLE user_info (
  id            BIGSERIAL PRIMARY KEY,
  id_token      TEXT UNIQUE,
  first_name    TEXT,
  last_name     TEXT,
  email         TEXT,
  tags          JSONB
);

CREATE TABLE user_role (
  id BIGINT PRIMARY KEY,
  name TEXT UNIQUE NOT NULL
);
INSERT INTO user_role (id, name) VALUES (0, 'super_admin');
INSERT INTO user_role (id, name) VALUES (1, 'org_admin');
INSERT INTO user_role (id, name) VALUES (2, 'event_admin');
INSERT INTO user_role (id, name) VALUES (3, 'payment_admin');
INSERT INTO user_role (id, name) VALUES (4, 'purchase_admin');
INSERT INTO user_role (id, name) VALUES (5, 'bid_admin');
INSERT INTO user_role (id, name) VALUES (10, 'normal');

CREATE TABLE role_permission (
  role_id BIGINT NOT NULL REFERENCES user_role(id) ON DELETE RESTRICT,
  permission TEXT NOT NULL,
  PRIMARY KEY (role_id, permission)
);
INSERT INTO role_permission VALUES (0, 'the_world');
INSERT INTO role_permission VALUES (10, 'org_create');
INSERT INTO role_permission VALUES (1, 'org_edit');
INSERT INTO role_permission VALUES (1, 'org_delete');
INSERT INTO role_permission VALUES (1, 'event_create');
INSERT INTO role_permission VALUES (2, 'event_edit');
INSERT INTO role_permission VALUES (1, 'event_delete');
INSERT INTO role_permission VALUES (2, 'item_create');
INSERT INTO role_permission VALUES (2, 'item_edit');
INSERT INTO role_permission VALUES (2, 'item_delete');
INSERT INTO role_permission VALUES (2, 'work_schedule_create');
INSERT INTO role_permission VALUES (2, 'work_schedule_edit');
INSERT INTO role_permission VALUES (2, 'work_schedule_delete');
INSERT INTO role_permission VALUES (1, 'role_assign');
INSERT INTO role_permission VALUES (3, 'payment_create');
INSERT INTO role_permission VALUES (2, 'payment_edit');
INSERT INTO role_permission VALUES (1, 'payment_delete');
INSERT INTO role_permission VALUES (4, 'purchase_create');
INSERT INTO role_permission VALUES (3, 'purchase_edit');
INSERT INTO role_permission VALUES (2, 'purchase_delete');
INSERT INTO role_permission VALUES (5, 'bid_create');
INSERT INTO role_permission VALUES (5, 'bid_edit');
INSERT INTO role_permission VALUES (5, 'bid_delete');
INSERT INTO role_permission VALUES (10, 'bid_create_self');

# --- !Downs

DROP TABLE role_permission;
DROP TABLE user_role;
DROP TABLE user_info;
