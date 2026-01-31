-- Add indexes for performance optimization

-- Index for email_verification_token
CREATE INDEX IF NOT EXISTS idx_token ON email_verification_token(token);
CREATE INDEX IF NOT EXISTS idx_user_id ON email_verification_token(user_id);

-- Index for password_reset_token
CREATE INDEX IF NOT EXISTS idx_password_reset_token ON password_reset_token(token);
CREATE INDEX IF NOT EXISTS idx_password_reset_user_id ON password_reset_token(user_id);

-- Index for fortis_user
CREATE INDEX IF NOT EXISTS idx_user_email ON fortis_user(email);

-- Index for refresh_token
CREATE INDEX IF NOT EXISTS idx_refresh_token ON refresh_token(token);
CREATE INDEX IF NOT EXISTS idx_refresh_user_id ON refresh_token(user_id);