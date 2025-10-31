-- Webhook Management Service Database Schema

-- Create webhooks table
CREATE TABLE IF NOT EXISTS webhooks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    url VARCHAR(2048) NOT NULL,
    secret VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_delivery_at TIMESTAMP WITH TIME ZONE,
    
    -- Retry configuration
    max_retry_attempts INTEGER DEFAULT 3,
    initial_retry_delay INTEGER DEFAULT 5000,
    backoff_multiplier DECIMAL(3,2) DEFAULT 2.0,
    max_retry_delay INTEGER DEFAULT 300000,
    
    -- Statistics
    total_deliveries INTEGER DEFAULT 0,
    successful_deliveries INTEGER DEFAULT 0,
    failed_deliveries INTEGER DEFAULT 0,
    
    UNIQUE(name)
);

-- Create webhook_headers table for custom headers
CREATE TABLE IF NOT EXISTS webhook_headers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    webhook_id UUID NOT NULL REFERENCES webhooks(id) ON DELETE CASCADE,
    header_name VARCHAR(255) NOT NULL,
    header_value VARCHAR(1024) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(webhook_id, header_name)
);

-- Create webhook_event_types table for subscribed event types
CREATE TABLE IF NOT EXISTS webhook_event_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    webhook_id UUID NOT NULL REFERENCES webhooks(id) ON DELETE CASCADE,
    event_type VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(webhook_id, event_type)
);

-- Create webhook_deliveries table for delivery tracking
CREATE TABLE IF NOT EXISTS webhook_deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    webhook_id UUID NOT NULL REFERENCES webhooks(id) ON DELETE CASCADE,
    event_id VARCHAR(255),
    event_type VARCHAR(255) NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    payload JSONB,
    
    -- HTTP response details
    status_code INTEGER,
    response_body TEXT,
    response_time INTEGER, -- in milliseconds
    
    -- Retry information
    attempt_count INTEGER DEFAULT 0,
    max_attempts INTEGER DEFAULT 3,
    next_retry_at TIMESTAMP WITH TIME ZONE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    
    -- Error tracking
    error_message TEXT,
    
    INDEX idx_webhook_deliveries_webhook_id (webhook_id),
    INDEX idx_webhook_deliveries_status (status),
    INDEX idx_webhook_deliveries_created_at (created_at),
    INDEX idx_webhook_deliveries_next_retry_at (next_retry_at)
);

-- Create webhook_delivery_attempts table for detailed attempt tracking
CREATE TABLE IF NOT EXISTS webhook_delivery_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    delivery_id UUID NOT NULL REFERENCES webhook_deliveries(id) ON DELETE CASCADE,
    attempt_number INTEGER NOT NULL,
    status_code INTEGER,
    response_body TEXT,
    response_time INTEGER, -- in milliseconds
    error_message TEXT,
    attempted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_delivery_attempts_delivery_id (delivery_id),
    INDEX idx_delivery_attempts_attempted_at (attempted_at)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_webhooks_is_active ON webhooks(is_active);
CREATE INDEX IF NOT EXISTS idx_webhooks_created_at ON webhooks(created_at);
CREATE INDEX IF NOT EXISTS idx_webhook_event_types_event_type ON webhook_event_types(event_type);

-- Insert some default webhook event types that the system supports
INSERT INTO webhook_event_types (webhook_id, event_type) 
SELECT w.id, event_type 
FROM webhooks w, (VALUES 
    ('scenario.created'),
    ('scenario.updated'),
    ('scenario.deleted'),
    ('track.uploaded'),
    ('track.processed'),
    ('track.deleted'),
    ('simulation.started'),
    ('simulation.completed'),
    ('simulation.failed')
) AS event_types(event_type)
WHERE NOT EXISTS (
    SELECT 1 FROM webhook_event_types wet 
    WHERE wet.webhook_id = w.id 
    AND wet.event_type = event_types.event_type
);

-- Function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_webhooks_updated_at BEFORE UPDATE ON webhooks
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create a view for webhook statistics
CREATE OR REPLACE VIEW webhook_stats AS
SELECT 
    w.id,
    w.name,
    w.url,
    w.is_active,
    w.total_deliveries,
    w.successful_deliveries,
    w.failed_deliveries,
    COALESCE(pending.pending_deliveries, 0) as pending_deliveries,
    CASE 
        WHEN w.total_deliveries > 0 THEN 
            ROUND((w.successful_deliveries::decimal / w.total_deliveries * 100), 2)
        ELSE 0 
    END as success_rate,
    w.last_delivery_at,
    w.created_at,
    w.updated_at
FROM webhooks w
LEFT JOIN (
    SELECT 
        webhook_id, 
        COUNT(*) as pending_deliveries
    FROM webhook_deliveries 
    WHERE status IN ('PENDING', 'RETRY')
    GROUP BY webhook_id
) pending ON w.id = pending.webhook_id;
