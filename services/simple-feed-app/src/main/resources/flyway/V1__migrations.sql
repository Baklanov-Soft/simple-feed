CREATE TABLE tag_timestamp(
  tag VARCHAR(200) NOT NULL PRIMARY KEY,
  last_visited TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_tag ON tag_timestamp(tag);
