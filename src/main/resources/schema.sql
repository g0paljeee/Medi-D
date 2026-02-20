-- Ensure the "active" column exists on users table (idempotent)
ALTER TABLE IF EXISTS users
ADD COLUMN IF NOT EXISTS active boolean DEFAULT true;

-- Ensure audit columns exist on users table (idempotent)
ALTER TABLE IF EXISTS users
ADD COLUMN IF NOT EXISTS created_at timestamp DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE IF EXISTS users
ADD COLUMN IF NOT EXISTS updated_at timestamp DEFAULT CURRENT_TIMESTAMP;
