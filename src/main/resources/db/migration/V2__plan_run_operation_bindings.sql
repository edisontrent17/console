ALTER TABLE plan_runs
    ADD COLUMN IF NOT EXISTS operation_bindings_json TEXT NOT NULL DEFAULT '[]';
