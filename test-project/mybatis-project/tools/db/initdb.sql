CREATE TABLE "user" (
  id integer PRIMARY KEY,
  name char(100),
  companyId integer
);

INSERT INTO "user" (
  id, name, companyId
) VALUES (
  1,
  'Taro Test',
  1
),
(
  2,
  'Jiro Test',
  1
),
(
  3,
  'Hanako Test',
  2
);