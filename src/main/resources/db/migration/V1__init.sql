CREATE TABLE IF NOT EXISTS plan_drafts (
    plan_id VARCHAR(128) PRIMARY KEY,
    draft_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS plan_runs (
    run_id VARCHAR(128) PRIMARY KEY,
    plan_id VARCHAR(128) NOT NULL,
    status VARCHAR(64) NOT NULL,
    plan_json TEXT NOT NULL,
    steps_json TEXT NOT NULL,
    approved_steps_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_plan_runs_draft
        FOREIGN KEY (plan_id) REFERENCES plan_drafts (plan_id)
);

CREATE TABLE IF NOT EXISTS approval_records (
    approval_id VARCHAR(128) PRIMARY KEY,
    run_id VARCHAR(128) NOT NULL,
    step_id VARCHAR(128) NOT NULL,
    approved_by VARCHAR(255) NOT NULL,
    decision VARCHAR(64) NOT NULL,
    payload_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_approval_records_run
        FOREIGN KEY (run_id) REFERENCES plan_runs (run_id),
    CONSTRAINT uq_approval_records_run_step
        UNIQUE (run_id, step_id)
);

CREATE INDEX IF NOT EXISTS idx_approval_records_run
    ON approval_records (run_id, created_at);

CREATE TABLE IF NOT EXISTS audit_events (
    event_id VARCHAR(128) PRIMARY KEY,
    run_id VARCHAR(128),
    plan_id VARCHAR(128),
    step_id VARCHAR(128),
    event_type VARCHAR(128) NOT NULL,
    detail_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_audit_events_run
        FOREIGN KEY (run_id) REFERENCES plan_runs (run_id)
);

CREATE INDEX IF NOT EXISTS idx_audit_events_run
    ON audit_events (run_id, created_at);

CREATE TABLE IF NOT EXISTS connect_idempotency_records (
    idempotency_key VARCHAR(128) PRIMARY KEY,
    result_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS monitor_definitions (
    monitor_id VARCHAR(128) PRIMARY KEY,
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
    CONSTRAINT fk_monitor_definitions_run
        FOREIGN KEY (run_id) REFERENCES plan_runs (run_id),
    CONSTRAINT uq_monitor_definitions_run_step
        UNIQUE (run_id, step_id)
);

CREATE INDEX IF NOT EXISTS idx_monitor_definitions_run_step
    ON monitor_definitions (run_id, step_id);

CREATE INDEX IF NOT EXISTS idx_monitor_definitions_due
    ON monitor_definitions (status, next_run_at, lease_until);

CREATE TABLE IF NOT EXISTS monitor_runs (
    monitor_run_id VARCHAR(128) PRIMARY KEY,
    monitor_id VARCHAR(128) NOT NULL,
    observed_value DOUBLE PRECISION NOT NULL,
    threshold_breached BOOLEAN NOT NULL,
    status VARCHAR(64) NOT NULL,
    recommendation TEXT NOT NULL,
    raw_json TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_monitor_runs_definition
        FOREIGN KEY (monitor_id) REFERENCES monitor_definitions (monitor_id)
);

CREATE INDEX IF NOT EXISTS idx_monitor_runs_monitor
    ON monitor_runs (monitor_id, created_at);

CREATE TABLE IF NOT EXISTS monitor_recommendations (
    recommendation_id VARCHAR(128) PRIMARY KEY,
    monitor_id VARCHAR(128) NOT NULL,
    monitor_run_id VARCHAR(128) NOT NULL,
    metric VARCHAR(255) NOT NULL,
    observed_value DOUBLE PRECISION NOT NULL,
    threshold_json TEXT NOT NULL,
    summary TEXT NOT NULL,
    status VARCHAR(64) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP,
    CONSTRAINT fk_monitor_recommendations_definition
        FOREIGN KEY (monitor_id) REFERENCES monitor_definitions (monitor_id),
    CONSTRAINT fk_monitor_recommendations_run
        FOREIGN KEY (monitor_run_id) REFERENCES monitor_runs (monitor_run_id)
);

CREATE INDEX IF NOT EXISTS idx_monitor_recommendations_status
    ON monitor_recommendations (status, created_at);
