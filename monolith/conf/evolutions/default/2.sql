-- noinspection SqlNoDataSourceInspectionForFile
# DATA OBJECT SCHEMA

# --- !Ups

CREATE TABLE organization (
  id   BIGSERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE event (
  id         BIGSERIAL PRIMARY KEY,
  org_id     BIGINT                   NOT NULL REFERENCES organization (id) ON DELETE CASCADE,
  name       TEXT                     NOT NULL,
  start_date TIMESTAMP WITH TIME ZONE NOT NULL,
  end_date   TIMESTAMP WITH TIME ZONE CHECK (end_date IS NULL OR end_date > start_date),
  campaign   TEXT,
  UNIQUE (org_id, name, campaign)
);

CREATE TABLE work_schedule (
  id         BIGSERIAL PRIMARY KEY,
  event_id   BIGINT                   NOT NULL REFERENCES event (id) ON DELETE CASCADE,
  worker     BIGINT                   NOT NULL REFERENCES user_info (id) ON DELETE CASCADE,
  start_date TIMESTAMP WITH TIME ZONE NOT NULL,
  end_date   TIMESTAMP WITH TIME ZONE NOT NULL CHECK (end_date > start_date)
);
CREATE INDEX work_schedule_event_id_idx ON work_schedule (event_id);
CREATE INDEX work_schedule_worker_idx ON work_schedule (worker);

CREATE TABLE contact (
  person    BIGINT NOT NULL REFERENCES user_info (id) ON DELETE CASCADE,
  org_id    BIGINT NOT NULL REFERENCES organization (id) ON DELETE CASCADE,
  user_role BIGINT NOT NULL DEFAULT 10 REFERENCES user_role (id) ON DELETE RESTRICT,
  UNIQUE (person, org_id)
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
  event_id    BIGINT NOT NULL REFERENCES event (id) ON DELETE CASCADE,
  item_number TEXT   NOT NULL,
  name        TEXT   NOT NULL,
  description TEXT   NOT NULL,
  est_value   DECIMAL(13, 3) CHECK (est_value IS NULL OR est_value >= 0.0),
  sale_type   BIGINT NOT NULL REFERENCES sale_type (id) ON DELETE RESTRICT,
  UNIQUE (event_id, item_number)
);

CREATE TABLE item_donation (
  id      BIGSERIAL PRIMARY KEY,
  item_id BIGINT NOT NULL REFERENCES item (id) ON DELETE CASCADE,
  donor   TEXT,
  person  BIGINT REFERENCES user_info (id) ON DELETE RESTRICT,
  CHECK (donor IS NOT NULL OR person IS NOT NULL)
);
CREATE INDEX item_donation_item_id_idx ON item_donation (item_id);

CREATE TABLE over_the_counter_item (
  item_id         BIGINT         NOT NULL PRIMARY KEY REFERENCES item (id) ON DELETE CASCADE,
  price           DECIMAL(13, 3) NOT NULL CHECK (price >= 0.0),
  inventory_count INT            NOT NULL DEFAULT -1
);

CREATE TABLE auction_item (
  item_id BIGINT         NOT NULL PRIMARY KEY REFERENCES item (id) ON DELETE CASCADE,
  min_bid DECIMAL(13, 3) NOT NULL DEFAULT 0.0 CHECK (min_bid >= 0.0)
);

CREATE TABLE silent_auction_item (
  item_id    BIGINT                   NOT NULL PRIMARY KEY REFERENCES item (id) ON DELETE CASCADE,
  min_bid    DECIMAL(13, 3)           NOT NULL DEFAULT 0.0 CHECK (min_bid >= 0.0),
  start_date TIMESTAMP WITH TIME ZONE NOT NULL,
  end_date   TIMESTAMP WITH TIME ZONE NOT NULL CHECK (end_date > start_date)
);

CREATE TABLE bid (
  id      BIGSERIAL PRIMARY KEY,
  item_id BIGINT         NOT NULL REFERENCES item (id) ON DELETE RESTRICT,
  bidder  BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  cashier BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  amount  DECIMAL(13, 3) NOT NULL CHECK (amount > 0)
);
CREATE INDEX bid_item_id_idx ON bid (item_id);
CREATE INDEX bid_bidder_idx ON bid (bidder);
CREATE INDEX bid_cashier_idx ON bid (cashier);

CREATE TABLE purchase (
  id        BIGSERIAL PRIMARY KEY,
  item_id   BIGINT         NOT NULL REFERENCES item (id) ON DELETE RESTRICT,
  purchaser BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  cashier   BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  amount    DECIMAL(13, 3) NOT NULL
);
CREATE INDEX purchase_item_id_idx ON purchase (item_id);
CREATE INDEX purchase_purchaser_idx ON purchase (purchaser);
CREATE INDEX purchase_cashier_idx ON purchase (cashier);

CREATE TABLE payment (
  id          BIGSERIAL PRIMARY KEY,
  org_id      BIGINT         NOT NULL REFERENCES organization (id) ON DELETE RESTRICT,
  payer       BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  cashier     BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  amount      DECIMAL(13, 3) NOT NULL,
  description TEXT           NOT NULL
);
CREATE INDEX payment_org_id_payer_idx ON payment (org_id, payer);
CREATE INDEX payment_payer_idx ON payment (payer);
CREATE INDEX payment_cashier_idx ON payment (cashier);

CREATE TABLE donation (
  id      BIGSERIAL PRIMARY KEY,
  org_id  BIGINT         NOT NULL REFERENCES organization (id) ON DELETE RESTRICT,
  payer   BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  cashier BIGINT         NOT NULL REFERENCES user_info (id) ON DELETE RESTRICT,
  amount  DECIMAL(13, 3) NOT NULL
);
CREATE INDEX donation_org_id_payer_idx ON donation (org_id, payer);
CREATE INDEX donation_payer_idx ON donation (payer);
CREATE INDEX donation_cashier_idx ON donation (cashier);

# --- !Downs

DROP INDEX donation_org_id_payer_idx;
DROP INDEX donation_payer_idx;
DROP INDEX donation_cashier_idx;
DROP TABLE donation;
DROP INDEX payment_org_id_payer_idx;
DROP INDEX payment_payer_idx;
DROP INDEX payment_cashier_idx;
DROP TABLE payment;
DROP INDEX purchase_item_id_idx;
DROP INDEX purchase_purchaser_idx;
DROP INDEX purchase_cashier_idx;
DROP TABLE purchase;
DROP INDEX bid_item_id_idx;
DROP INDEX bid_bidder_idx;
DROP INDEX bid_cashier_idx;
DROP TABLE bid;
DROP TABLE silent_auction_item;
DROP TABLE auction_item;
DROP TABLE over_the_counter_item;
DROP INDEX item_donation_item_id_idx;
DROP TABLE item_donation;
DROP TABLE item;
DROP TABLE sale_type;
DROP TABLE contact;
DROP INDEX work_schedule_event_id_idx;
DROP INDEX work_schedule_worker_idx;
DROP TABLE work_schedule;
DROP TABLE event;
DROP TABLE organization;
