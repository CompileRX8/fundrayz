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

CREATE TABLE item (
  id          BIGSERIAL PRIMARY KEY,
  campaign_id BIGINT NOT NULL REFERENCES campaign (id) ON DELETE RESTRICT,
  item_number TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT   NOT NULL,
  est_value   MONEY,
  sale_type   TEXT   NOT NULL REFERENCES sale_type (id) ON DELETE RESTRICT
);

CREATE TABLE donation (
  id      BIGSERIAL PRIMARY KEY,
  item_id BIGINT NOT NULL REFERENCES item (id) ON DELETE CASCADE,
  donor   TEXT   NOT NULL
);

CREATE TABLE sale_type (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);
INSERT INTO sale_type (name) VALUES ('OverTheCounter');
INSERT INTO sale_type (name) VALUES ('Auction');
INSERT INTO sale_type (name) VALUES ('SilentAuction');

CREATE TABLE over_the_counter_item (
  item_id BIGINT NOT NULL PRIMARY KEY REFERENCES item (id) ON DELETE CASCADE,
  price   MONEY  NOT NULL
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

# --- !Downs

DROP TABLE item;
DROP TABLE sale_type;
DROP TABLE event;
DROP TABLE campaign;
DROP TABLE organization;
