CREATE TABLE plan_drafts_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    plan_id VARCHAR(128) NOT NULL,
    draft_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_plan_drafts_tenant PRIMARY KEY (organization_id, plan_id)
);

INSERT INTO plan_drafts_v9 (organization_id, plan_id, draft_json, created_at, updated_at)
SELECT organization_id, plan_id, draft_json, created_at, updated_at
FROM plan_drafts;

CREATE TABLE plan_runs_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    run_id VARCHAR(128) NOT NULL,
    plan_id VARCHAR(128) NOT NULL,
    status VARCHAR(64) NOT NULL,
    plan_json TEXT NOT NULL,
    steps_json TEXT NOT NULL,
    approved_steps_json TEXT NOT NULL,
    operation_bindings_json TEXT NOT NULL DEFAULT '[]',
    approved_plan_json TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_plan_runs_tenant PRIMARY KEY (organization_id, run_id),
    CONSTRAINT fk_plan_runs_draft_tenant
        FOREIGN KEY (organization_id, plan_id) REFERENCES plan_drafts_v9 (organization_id, plan_id)
);

INSERT INTO plan_runs_v9 (
    organization_id, run_id, plan_id, status, plan_json, steps_json,
    approved_steps_json, operation_bindings_json, approved_plan_json,
    created_at, updated_at
)
SELECT organization_id, run_id, plan_id, status, plan_json, steps_json,
       approved_steps_json, operation_bindings_json, approved_plan_json,
       created_at, updated_at
FROM plan_runs;

CREATE TABLE approval_records_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    approval_id VARCHAR(128) NOT NULL,
    run_id VARCHAR(128) NOT NULL,
    step_id VARCHAR(128) NOT NULL,
    approved_by VARCHAR(255) NOT NULL,
    decision VARCHAR(64) NOT NULL,
    payload_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_approval_records_tenant PRIMARY KEY (organization_id, approval_id),
    CONSTRAINT fk_approval_records_run_tenant
        FOREIGN KEY (organization_id, run_id) REFERENCES plan_runs_v9 (organization_id, run_id),
    CONSTRAINT uq_approval_records_run_step_tenant
        UNIQUE (organization_id, run_id, step_id)
);

INSERT INTO approval_records_v9 (
    organization_id, approval_id, run_id, step_id, approved_by,
    decision, payload_json, created_at
)
SELECT organization_id, approval_id, run_id, step_id, approved_by,
       decision, payload_json, created_at
FROM approval_records;

CREATE TABLE audit_events_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    event_id VARCHAR(128) NOT NULL,
    run_id VARCHAR(128),
    plan_id VARCHAR(128),
    step_id VARCHAR(128),
    event_type VARCHAR(128) NOT NULL,
    detail_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_audit_events_tenant PRIMARY KEY (organization_id, event_id),
    CONSTRAINT fk_audit_events_run_tenant
        FOREIGN KEY (organization_id, run_id) REFERENCES plan_runs_v9 (organization_id, run_id)
);

INSERT INTO audit_events_v9 (
    organization_id, event_id, run_id, plan_id, step_id,
    event_type, detail_json, created_at
)
SELECT organization_id, event_id, run_id, plan_id, step_id,
       event_type, detail_json, created_at
FROM audit_events;

CREATE TABLE monitor_definitions_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    monitor_id VARCHAR(128) NOT NULL,
    plan_id VARCHAR(128) NOT NULL,
    run_id VARCHAR(128) NOT NULL,
    step_id VARCHAR(128) NOT NULL,
    metric VARCHAR(255) NOT NULL,
    cadence VARCHAR(64) NOT NULL,
    threshold_json TEXT NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    last_run_at TIMESTAMP,
    next_run_at TIMESTAMP,
    lease_until TIMESTAMP,
    CONSTRAINT pk_monitor_definitions_tenant PRIMARY KEY (organization_id, monitor_id),
    CONSTRAINT fk_monitor_definitions_run_tenant
        FOREIGN KEY (organization_id, run_id) REFERENCES plan_runs_v9 (organization_id, run_id),
    CONSTRAINT uq_monitor_definitions_run_step_tenant
        UNIQUE (organization_id, run_id, step_id)
);

INSERT INTO monitor_definitions_v9 (
    organization_id, monitor_id, plan_id, run_id, step_id, metric, cadence,
    threshold_json, status, created_at, last_run_at, next_run_at, lease_until
)
SELECT organization_id, monitor_id, plan_id, run_id, step_id, metric, cadence,
       threshold_json, status, created_at, last_run_at, next_run_at, lease_until
FROM monitor_definitions;

CREATE TABLE monitor_runs_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    monitor_run_id VARCHAR(128) NOT NULL,
    monitor_id VARCHAR(128) NOT NULL,
    observed_value DOUBLE PRECISION NOT NULL,
    threshold_breached BOOLEAN NOT NULL,
    status VARCHAR(64) NOT NULL,
    recommendation TEXT NOT NULL,
    raw_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_monitor_runs_tenant PRIMARY KEY (organization_id, monitor_run_id),
    CONSTRAINT fk_monitor_runs_definition_tenant
        FOREIGN KEY (organization_id, monitor_id) REFERENCES monitor_definitions_v9 (organization_id, monitor_id)
);

INSERT INTO monitor_runs_v9 (
    organization_id, monitor_run_id, monitor_id, observed_value,
    threshold_breached, status, recommendation, raw_json, created_at
)
SELECT COALESCE(md.organization_id, 'org_default'), mr.monitor_run_id, mr.monitor_id,
       mr.observed_value, mr.threshold_breached, mr.status, mr.recommendation,
       mr.raw_json, mr.created_at
FROM monitor_runs mr
LEFT JOIN monitor_definitions md ON md.monitor_id = mr.monitor_id;

CREATE TABLE monitor_recommendations_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    recommendation_id VARCHAR(128) NOT NULL,
    monitor_id VARCHAR(128) NOT NULL,
    monitor_run_id VARCHAR(128) NOT NULL,
    metric VARCHAR(255) NOT NULL,
    observed_value DOUBLE PRECISION NOT NULL,
    threshold_json TEXT NOT NULL,
    summary TEXT NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP,
    CONSTRAINT pk_monitor_recommendations_tenant PRIMARY KEY (organization_id, recommendation_id),
    CONSTRAINT fk_monitor_recommendations_definition_tenant
        FOREIGN KEY (organization_id, monitor_id) REFERENCES monitor_definitions_v9 (organization_id, monitor_id),
    CONSTRAINT fk_monitor_recommendations_run_tenant
        FOREIGN KEY (organization_id, monitor_run_id) REFERENCES monitor_runs_v9 (organization_id, monitor_run_id)
);

INSERT INTO monitor_recommendations_v9 (
    organization_id, recommendation_id, monitor_id, monitor_run_id, metric,
    observed_value, threshold_json, summary, status, created_at, reviewed_at
)
SELECT organization_id, recommendation_id, monitor_id, monitor_run_id, metric,
       observed_value, threshold_json, summary, status, created_at, reviewed_at
FROM monitor_recommendations;

CREATE TABLE approved_plans_v9 (
    organization_id VARCHAR(128) NOT NULL DEFAULT 'org_default',
    plan_id VARCHAR(128) NOT NULL,
    artifact_id VARCHAR(128) NOT NULL,
    plan_hash VARCHAR(128) NOT NULL,
    approved_plan_json TEXT NOT NULL,
    approved_by VARCHAR(255) NOT NULL,
    approved_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_approved_plans_tenant PRIMARY KEY (organization_id, plan_id),
    CONSTRAINT fk_approved_plans_draft_tenant
        FOREIGN KEY (organization_id, plan_id) REFERENCES plan_drafts_v9 (organization_id, plan_id)
);

INSERT INTO approved_plans_v9 (
    organization_id, plan_id, artifact_id, plan_hash, approved_plan_json,
    approved_by, approved_at, created_at, updated_at
)
SELECT organization_id, plan_id, artifact_id, plan_hash, approved_plan_json,
       approved_by, approved_at, created_at, updated_at
FROM approved_plans;

DROP TABLE monitor_recommendations;
DROP TABLE monitor_runs;
DROP TABLE monitor_definitions;
DROP TABLE approval_records;
DROP TABLE audit_events;
DROP TABLE plan_runs;
DROP TABLE approved_plans;
DROP TABLE plan_drafts;

ALTER TABLE plan_drafts_v9 RENAME TO plan_drafts;
ALTER TABLE plan_runs_v9 RENAME TO plan_runs;
ALTER TABLE approval_records_v9 RENAME TO approval_records;
ALTER TABLE audit_events_v9 RENAME TO audit_events;
ALTER TABLE monitor_definitions_v9 RENAME TO monitor_definitions;
ALTER TABLE monitor_runs_v9 RENAME TO monitor_runs;
ALTER TABLE monitor_recommendations_v9 RENAME TO monitor_recommendations;
ALTER TABLE approved_plans_v9 RENAME TO approved_plans;

CREATE INDEX idx_plan_drafts_org_updated
    ON plan_drafts (organization_id, updated_at);

CREATE INDEX idx_plan_runs_org_run
    ON plan_runs (organization_id, run_id);

CREATE INDEX idx_audit_events_org_run
    ON audit_events (organization_id, run_id, created_at);

CREATE INDEX idx_approval_records_org_run
    ON approval_records (organization_id, run_id, created_at);

CREATE INDEX idx_monitor_definitions_org_created
    ON monitor_definitions (organization_id, created_at);

CREATE INDEX idx_monitor_definitions_due
    ON monitor_definitions (status, next_run_at, lease_until);

CREATE INDEX idx_monitor_runs_monitor
    ON monitor_runs (organization_id, monitor_id, created_at);

CREATE INDEX idx_monitor_recommendations_org_status
    ON monitor_recommendations (organization_id, status, created_at);

CREATE INDEX idx_approved_plans_org_approved
    ON approved_plans (organization_id, approved_at);
