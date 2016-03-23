# DATA OBJECT SCHEMA

# --- !Ups

CREATE TABLE organization (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE campaign (
  id         BIGSERIAL PRIMARY KEY,
  org_id     BIGINT NOT NULL REFERENCES organization (id) ON DELETE CASCADE,
  name       TEXT   NOT NULL,
  start_date DATE   NOT NULL,
  end_date   DATE,
  UNIQUE (org_id, name)
);

CREATE TABLE event (
  id          BIGSERIAL PRIMARY KEY,
  campaign_id BIGINT NOT NULL REFERENCES campaign (id) ON DELETE CASCADE,
  name        TEXT   NOT NULL,
  start_date  DATE   NOT NULL,
  end_date    DATE,
  UNIQUE (campaign_id, name)
);

CREATE TABLE work_schedule (
  id         BIGSERIAL PRIMARY KEY,
  event_id   BIGINT NOT NULL REFERENCES event (id) ON DELETE CASCADE,
  "user"     BIGINT NOT NULL REFERENCES user_info (id) ON DELETE CASCADE,
  start_date DATE   NOT NULL,
  end_date   DATE   NOT NULL
);

CREATE TABLE sale_type (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);
INSERT INTO sale_type (name) VALUES ('OverTheCounter');
INSERT INTO sale_type (name) VALUES ('Auction');
INSERT INTO sale_type (name) VALUES ('SilentAuction');

CREATE TABLE item (
  id          BIGSERIAL PRIMARY KEY,
  campaign_id BIGINT NOT NULL REFERENCES campaign (id) ON DELETE CASCADE,
  item_number TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT   NOT NULL,
  est_value   MONEY,
  sale_type   BIGINT NOT NULL REFERENCES sale_type (id) ON DELETE RESTRICT
);

CREATE TABLE donation (
  id      BIGSERIAL PRIMARY KEY,
  item_id BIGINT NOT NULL REFERENCES item (id) ON DELETE CASCADE,
  donor   TEXT   NOT NULL
);

CREATE TABLE over_the_counter_item (
  item_id         BIGINT NOT NULL PRIMARY KEY REFERENCES item (id) ON DELETE CASCADE,
  price           MONEY  NOT NULL,
  inventory_count INT NOT NULL DEFAULT -1
);

CREATE TABLE auction_item (
  item_id BIGINT NOT NULL PRIMARY KEY REFERENCES item (id) ON DELETE CASCADE,
  min_bid MONEY  NOT NULL DEFAULT 0.0
);

CREATE TABLE silent_auction_item (
  item_id    BIGINT NOT NULL PRIMARY KEY REFERENCES item (id) ON DELETE CASCADE,
  min_bid    MONEY  NOT NULL DEFAULT 0.0,
  start_date DATE   NOT NULL,
  end_date   DATE   NOT NULL
);

CREATE TABLE bid (
  id BIGSERIAL PRIMARY KEY,
  item_id BIGINT NOT NULL REFERENCES item (id) ON DELETE RESTRICT,
  bidder BIGINT NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  cashier BIGINT NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  amount MONEY NOT NULL
);

CREATE TABLE purchase (
  id BIGSERIAL PRIMARY KEY,
  item_id BIGINT NOT NULL REFERENCES item (id) ON DELETE RESTRICT,
  purchaser BIGINT NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  cashier BIGINT NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  amount MONEY NOT NULL
);

CREATE TABLE payment (
  id BIGSERIAL PRIMARY KEY,
  payer BIGINT NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  cashier BIGINT NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  amount MONEY NOT NULL,
  description TEXT NOT NULL
)

# --- !Downs

DROP TABLE payment;
DROP TABLE purchase;
DROP TABLE bid;
DROP TABLE silent_auction_item;
DROP TABLE auction_item;
DROP TABLE over_the_counter_item;
DROP TABLE donation;
DROP TABLE item;
DROP TABLE sale_type;
DROP TABLE work_schedule;
DROP TABLE event;
DROP TABLE campaign;
DROP TABLE organization;
