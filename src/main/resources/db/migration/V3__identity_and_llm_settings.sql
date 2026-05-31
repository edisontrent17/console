CREATE TABLE IF NOT EXISTS organizations (
    organization_id VARCHAR(128) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_organizations_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS app_users (
    user_id VARCHAR(128) PRIMARY KEY,
    organization_id VARCHAR(128) NOT NULL,
    email VARCHAR(320) NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(64) NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_app_users_organization
        FOREIGN KEY (organization_id) REFERENCES organizations (organization_id),
    CONSTRAINT uq_app_users_org_email UNIQUE (organization_id, email)
);

CREATE INDEX IF NOT EXISTS idx_app_users_email
    ON app_users (email);

CREATE TABLE IF NOT EXISTS user_sessions (
    session_id VARCHAR(128) PRIMARY KEY,
    user_id VARCHAR(128) NOT NULL,
    session_token_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP,
    CONSTRAINT fk_user_sessions_user
        FOREIGN KEY (user_id) REFERENCES app_users (user_id),
    CONSTRAINT uq_user_sessions_token_hash UNIQUE (session_token_hash)
);

CREATE INDEX IF NOT EXISTS idx_user_sessions_token
    ON user_sessions (session_token_hash, expires_at, revoked_at);

CREATE TABLE IF NOT EXISTS llm_settings (
    organization_id VARCHAR(128) PRIMARY KEY,
    provider VARCHAR(64) NOT NULL,
    model VARCHAR(255) NOT NULL,
    encrypted_api_key TEXT,
    updated_by VARCHAR(128),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_llm_settings_organization
        FOREIGN KEY (organization_id) REFERENCES organizations (organization_id)
);
