CREATE TABLE IF NOT EXISTS approved_plans (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    plan_id VARCHAR(128) NOT NULL,
    artifact_id VARCHAR(128) NOT NULL,
    plan_hash VARCHAR(128) NOT NULL,
    approved_plan_json TEXT NOT NULL,
    approved_by VARCHAR(255) NOT NULL,
    approved_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (organization_id, plan_id)
);

CREATE INDEX IF NOT EXISTS idx_approved_plans_org_approved
    ON approved_plans (organization_id, approved_at);
