-- Drop the legacy roles column after migration to user_roles table
ALTER TABLE users DROP COLUMN roles;
