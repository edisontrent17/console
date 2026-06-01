ALTER TABLE plan_runs
    ADD COLUMN IF NOT EXISTS approved_plan_json TEXT;
