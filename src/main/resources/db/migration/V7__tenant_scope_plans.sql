ALTER TABLE plan_drafts
    ADD COLUMN IF NOT EXISTS organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default';

ALTER TABLE plan_runs
    ADD COLUMN IF NOT EXISTS organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default';

ALTER TABLE approval_records
    ADD COLUMN IF NOT EXISTS organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default';

ALTER TABLE audit_events
    ADD COLUMN IF NOT EXISTS organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default';

ALTER TABLE monitor_definitions
    ADD COLUMN IF NOT EXISTS organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default';

ALTER TABLE monitor_recommendations
    ADD COLUMN IF NOT EXISTS organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default';

CREATE INDEX IF NOT EXISTS idx_plan_drafts_org_updated
    ON plan_drafts (organization_id, updated_at);

CREATE INDEX IF NOT EXISTS idx_plan_runs_org_run
    ON plan_runs (organization_id, run_id);

CREATE INDEX IF NOT EXISTS idx_audit_events_org_run
    ON audit_events (organization_id, run_id, created_at);

CREATE INDEX IF NOT EXISTS idx_approval_records_org_run
    ON approval_records (organization_id, run_id, created_at);

CREATE INDEX IF NOT EXISTS idx_monitor_definitions_org_created
    ON monitor_definitions (organization_id, created_at);

CREATE INDEX IF NOT EXISTS idx_monitor_recommendations_org_status
    ON monitor_recommendations (organization_id, status, created_at);
