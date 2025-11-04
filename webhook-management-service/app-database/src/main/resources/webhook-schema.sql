-- Webhook Management Service canonical schema.
-- Keeps the application, Flyway migrations, and local tooling in sync.

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

CREATE OR REPLACE VIEW webhook_stats AS
SELECT 
    w.id,
    w.name,
    w.url,
    w.is_active,
    w.total_deliveries,
    w.successful_deliveries,
    w.failed_deliveries,
    COALESCE(pending.pending_deliveries, 0) AS pending_deliveries,
    COALESCE(retry.retry_deliveries, 0) AS retry_deliveries,
    CASE 
        WHEN w.total_deliveries > 0 THEN 
            ROUND((w.successful_deliveries::decimal / w.total_deliveries::decimal * 100), 2)
        ELSE 0 
    END AS success_rate,
    w.last_delivery_at,
    w.created_at,
    w.updated_at
FROM webhooks w
LEFT JOIN (
    SELECT webhook_id, COUNT(*) AS pending_deliveries
    FROM webhook_deliveries 
    WHERE status IN ('PENDING', 'RETRY')
    GROUP BY webhook_id
) pending ON w.id = pending.webhook_id
LEFT JOIN (
    SELECT webhook_id, COUNT(*) AS retry_deliveries
    FROM webhook_deliveries 
    WHERE status = 'RETRY'
    GROUP BY webhook_id
) retry ON w.id = retry.webhook_id;
