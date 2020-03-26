-- users table
CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  email TEXT NOT NULL,
  encrypted_password TEXT NOT NULL,
  confirmation_token TEXT,
  confirmed_at TIMESTAMPTZ,
  confirmation_sent_at TIMESTAMPTZ,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now());
--;;
CREATE UNIQUE INDEX users_id ON users(id);
CREATE UNIQUE INDEX confirmation_token_idx ON users(confirmation_token);
--;;
-- Add the trigger that automatically updates created_at & updated_at columns
--;;
CREATE OR REPLACE FUNCTION _update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
NEW.updated_at := now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql VOLATILE;
-- The below trigger needs to be added to each table
--;;
CREATE trigger update_updated_at_trigger
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE PROCEDURE _update_updated_at();
