-- Add soft-delete column to users with safe defaults and backfill
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS deleted BOOLEAN;

-- Backfill existing rows to false where null
UPDATE users SET deleted = FALSE WHERE deleted IS NULL;

-- Enforce NOT NULL and default going forward
ALTER TABLE users
    ALTER COLUMN deleted SET DEFAULT FALSE,
    ALTER COLUMN deleted SET NOT NULL;


