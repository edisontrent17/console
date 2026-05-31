CREATE TABLE IF NOT EXISTS mcp_server_settings (
    organization_id VARCHAR(128) NOT NULL,
    server_id VARCHAR(128) NOT NULL,
    enabled BOOLEAN NOT NULL,
    command TEXT,
    updated_by VARCHAR(128),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (organization_id, server_id),
    CONSTRAINT fk_mcp_server_settings_organization
        FOREIGN KEY (organization_id) REFERENCES organizations (organization_id)
);
