INSERT INTO "users" ("id", "name", "password", "created", "version")
VALUES (0, 'admin', '$2a$10$3YJoG98ZESDQIJpjDpoIIurVpk7escVMLEtxxgXWA6h1G2vU3Df.6', NOW(), 0)
ON CONFLICT DO NOTHING;
INSERT INTO "user_roles" ("id", "role") VALUES (0, 'admin')
ON CONFLICT DO NOTHING;
INSERT INTO "user_roles" ("id", "role") VALUES (0, 'user')
ON CONFLICT DO NOTHING;

ALTER SEQUENCE IF EXISTS hibernate_sequence RESTART WITH 10;
