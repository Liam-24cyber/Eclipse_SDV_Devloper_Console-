/* scenario-library-service */
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS scenario (id uuid DEFAULT uuid_generate_v4 () not null, created_at timestamp, created_by varchar(255), description varchar(255), updated_at timestamp, updated_by varchar(255), name varchar(255), status varchar(255), type varchar(255), file_id uuid, primary key (id));
CREATE TABLE IF NOT EXISTS file (id uuid DEFAULT uuid_generate_v4 () not null, checksum varchar(255), file_key varchar(255), path varchar(255), size varchar(255), updated_by varchar(255), updated_on timestamp, primary key (id));
ALTER TABLE scenario ADD CONSTRAINT fk_track_id FOREIGN KEY (file_id) REFERENCES file;

CREATE TABLE IF NOT EXISTS simulation (id uuid DEFAULT uuid_generate_v4 () not null, campaign_id uuid, created_at timestamp, description varchar(255), environment varchar(255), hardware varchar(255), platform varchar(255), scenario_type int4, created_by varchar(255), start_date timestamp, status varchar(255), name varchar(255), primary key (id));
CREATE TABLE IF NOT EXISTS simulation_scenarios (simulation_id uuid DEFAULT uuid_generate_v4 () not null, scenarios uuid);
CREATE TABLE IF NOT EXISTS simulation_tracks (simulation_id uuid DEFAULT uuid_generate_v4 () not null, tracks uuid);
ALTER TABLE simulation_scenarios ADD CONSTRAINT fk_simulation_id FOREIGN KEY (simulation_id) REFERENCES simulation;
ALTER TABLE simulation_tracks ADD CONSTRAINT fk_simulation_id FOREIGN KEY (simulation_id) REFERENCES simulation;

/* simulation results and logging tables */
CREATE TABLE IF NOT EXISTS simulation_results (
  id uuid DEFAULT uuid_generate_v4 () not null,
  simulation_id uuid not null,
  result_type varchar(50) not null, -- 'logs', 'metrics', 'files', 'summary'
  title varchar(255),
  content text,
  file_path varchar(500),
  file_size bigint,
  mime_type varchar(100),
  created_at timestamp DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamp DEFAULT CURRENT_TIMESTAMP,
  primary key (id)
);

CREATE TABLE IF NOT EXISTS simulation_logs (
  id uuid DEFAULT uuid_generate_v4 () not null,
  simulation_id uuid not null,
  log_level varchar(20) not null, -- 'INFO', 'WARN', 'ERROR', 'DEBUG'
  message text not null,
  component varchar(100), -- which component generated the log
  timestamp_log timestamp DEFAULT CURRENT_TIMESTAMP,
  primary key (id)
);

CREATE TABLE IF NOT EXISTS simulation_metrics (
  id uuid DEFAULT uuid_generate_v4 () not null,
  simulation_id uuid not null,
  metric_name varchar(100) not null,
  metric_value numeric,
  metric_unit varchar(50),
  category varchar(100), -- 'performance', 'vehicle', 'scenario', 'system'
  recorded_at timestamp DEFAULT CURRENT_TIMESTAMP,
  primary key (id)
);

-- Add foreign key constraints
ALTER TABLE simulation_results ADD CONSTRAINT fk_sim_results_simulation_id FOREIGN KEY (simulation_id) REFERENCES simulation(id) ON DELETE CASCADE;
ALTER TABLE simulation_logs ADD CONSTRAINT fk_sim_logs_simulation_id FOREIGN KEY (simulation_id) REFERENCES simulation(id) ON DELETE CASCADE;
ALTER TABLE simulation_metrics ADD CONSTRAINT fk_sim_metrics_simulation_id FOREIGN KEY (simulation_id) REFERENCES simulation(id) ON DELETE CASCADE;

-- Add indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_simulation_results_sim_id ON simulation_results(simulation_id);
CREATE INDEX IF NOT EXISTS idx_simulation_results_type ON simulation_results(result_type);
CREATE INDEX IF NOT EXISTS idx_simulation_logs_sim_id ON simulation_logs(simulation_id);
CREATE INDEX IF NOT EXISTS idx_simulation_logs_level ON simulation_logs(log_level);
CREATE INDEX IF NOT EXISTS idx_simulation_metrics_sim_id ON simulation_metrics(simulation_id);
CREATE INDEX IF NOT EXISTS idx_simulation_metrics_name ON simulation_metrics(metric_name);

-- Add completion tracking to simulation table
ALTER TABLE simulation ADD COLUMN IF NOT EXISTS end_date timestamp;
ALTER TABLE simulation ADD COLUMN IF NOT EXISTS execution_duration integer; -- duration in seconds
ALTER TABLE simulation ADD COLUMN IF NOT EXISTS result_summary text;
ALTER TABLE simulation ADD COLUMN IF NOT EXISTS error_message text;

/* tracks-management-service */
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE IF NOT EXISTS track (id uuid DEFAULT uuid_generate_v4 () not null, created_at timestamp, description varchar(255), duration varchar(255), name varchar(255), state varchar(255), track_type varchar(255), primary key (id));
CREATE TABLE IF NOT EXISTS vehicle (id uuid DEFAULT uuid_generate_v4 () not null, country varchar(255), vin varchar(255), track_id uuid, primary key (id));
ALTER TABLE vehicle add constraint fk_track_id foreign key (track_id) references track;

/* webhook-management-service */
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS webhooks (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    name varchar(255) NOT NULL UNIQUE,
    description text,
    url varchar(2048) NOT NULL,
    secret varchar(255),
    is_active boolean DEFAULT true,
    max_retry_attempts integer DEFAULT 3,
    initial_retry_delay integer DEFAULT 5000,
    backoff_multiplier numeric(3, 2) DEFAULT 2.00,
    max_retry_delay integer DEFAULT 300000,
    total_deliveries integer DEFAULT 0,
    successful_deliveries integer DEFAULT 0,
    failed_deliveries integer DEFAULT 0,
    last_delivery_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS webhook_headers (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    webhook_id uuid NOT NULL REFERENCES webhooks(id) ON DELETE CASCADE,
    header_name varchar(255) NOT NULL,
    header_value varchar(1024) NOT NULL,
    created_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (webhook_id, header_name)
);

CREATE TABLE IF NOT EXISTS webhook_event_types (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    webhook_id uuid NOT NULL REFERENCES webhooks(id) ON DELETE CASCADE,
    event_type varchar(255) NOT NULL,
    created_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (webhook_id, event_type)
);

CREATE TABLE IF NOT EXISTS webhook_deliveries (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    webhook_id uuid NOT NULL REFERENCES webhooks(id) ON DELETE CASCADE,
    event_id varchar(255),
    event_type varchar(255) NOT NULL,
    status varchar(50) DEFAULT 'PENDING',
    payload jsonb,
    status_code integer,
    response_body text,
    response_time integer,
    attempt_count integer DEFAULT 0,
    max_attempts integer DEFAULT 3,
    next_retry_at timestamp(6) with time zone,
    created_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP,
    completed_at timestamp(6) with time zone,
    error_message text
);

CREATE TABLE IF NOT EXISTS webhook_delivery_attempts (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    delivery_id uuid NOT NULL REFERENCES webhook_deliveries(id) ON DELETE CASCADE,
    attempt_number integer NOT NULL,
    status_code integer,
    response_body text,
    response_time integer,
    error_message text,
    attempted_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_webhooks_is_active ON webhooks(is_active);
CREATE INDEX IF NOT EXISTS idx_webhooks_created_at ON webhooks(created_at);

CREATE INDEX IF NOT EXISTS idx_webhook_headers_webhook_id ON webhook_headers(webhook_id);

CREATE INDEX IF NOT EXISTS idx_webhook_event_types_event_type ON webhook_event_types(event_type);

CREATE INDEX IF NOT EXISTS idx_webhook_deliveries_webhook_id ON webhook_deliveries(webhook_id);
CREATE INDEX IF NOT EXISTS idx_webhook_deliveries_status ON webhook_deliveries(status);
CREATE INDEX IF NOT EXISTS idx_webhook_deliveries_created_at ON webhook_deliveries(created_at);
CREATE INDEX IF NOT EXISTS idx_webhook_deliveries_next_retry_at ON webhook_deliveries(next_retry_at);

CREATE INDEX IF NOT EXISTS idx_webhook_delivery_attempts_delivery_id ON webhook_delivery_attempts(delivery_id);
CREATE INDEX IF NOT EXISTS idx_webhook_delivery_attempts_attempted_at ON webhook_delivery_attempts(attempted_at);
