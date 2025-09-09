-- Migration: normalize roles to user_roles table

CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Backfill roles from legacy users.roles CSV
WITH split AS (
    SELECT u.id AS user_id,
           regexp_split_to_table(u.roles, '\s*,\s*') AS role_raw
    FROM users u
    WHERE u.roles IS NOT NULL AND u.roles <> ''
), normalized AS (
    SELECT user_id,
           CASE
               WHEN upper(role_raw) LIKE 'ROLE_%' THEN upper(role_raw)
               ELSE 'ROLE_' || upper(role_raw)
           END AS role
    FROM split
)
INSERT INTO user_roles (user_id, role)
SELECT user_id, role FROM normalized;
