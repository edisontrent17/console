ALTER TABLE mcp_server_settings ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE mcp_server_settings ADD COLUMN IF NOT EXISTS transport VARCHAR(64) DEFAULT 'stdio' NOT NULL;
ALTER TABLE mcp_server_settings ADD COLUMN IF NOT EXISTS endpoint TEXT;
ALTER TABLE mcp_server_settings ADD COLUMN IF NOT EXISTS arguments_json TEXT DEFAULT '[]' NOT NULL;
ALTER TABLE mcp_server_settings ADD COLUMN IF NOT EXISTS environment_json TEXT DEFAULT '[]' NOT NULL;
ALTER TABLE mcp_server_settings ADD COLUMN IF NOT EXISTS environment_passthrough_json TEXT DEFAULT '[]' NOT NULL;
ALTER TABLE mcp_server_settings ADD COLUMN IF NOT EXISTS working_directory TEXT;
