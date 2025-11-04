-- Align the webhook schema with the application expectations.
-- This script is idempotent and can safely re-run.

-- The legacy view prevents column type changes; drop it temporarily.
DROP VIEW IF EXISTS webhook_stats;

-- Rename legacy secret column if present.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'secret_token'
  ) THEN
    EXECUTE 'ALTER TABLE webhooks RENAME COLUMN secret_token TO secret';
  END IF;
END;
$$ LANGUAGE plpgsql;

-- Ensure webhook headers support extended storage and timestamps.
ALTER TABLE webhooks
  ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
  ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE webhook_headers
  ALTER COLUMN header_value TYPE varchar(1024);

ALTER TABLE webhook_headers
  ADD COLUMN IF NOT EXISTS created_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP;

-- Replace the legacy event type catalogue with the many-to-one relation used by the service.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhook_event_types'
      AND column_name = 'description'
  ) THEN
    EXECUTE 'DROP TABLE webhook_event_types CASCADE';
  END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TABLE IF NOT EXISTS webhook_event_types (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  webhook_id uuid NOT NULL REFERENCES webhooks(id) ON DELETE CASCADE,
  event_type varchar(255) NOT NULL,
  created_at timestamp(6) with time zone DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (webhook_id, event_type)
);

-- Expand webhook URL column to match application constraint.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'url'
      AND (character_maximum_length IS NULL OR character_maximum_length < 2048)
  ) THEN
    EXECUTE 'ALTER TABLE webhooks ALTER COLUMN url TYPE varchar(2048)';
  END IF;
END;
$$ LANGUAGE plpgsql;

-- Migrate timestamp columns to timestamptz while preserving existing values.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'created_at'
      AND data_type = 'timestamp without time zone'
  ) THEN
    EXECUTE 'ALTER TABLE webhooks ALTER COLUMN created_at TYPE timestamp(6) with time zone USING created_at AT TIME ZONE ''UTC''';
  ELSIF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'created_at'
      AND data_type = 'timestamp with time zone'
      AND (datetime_precision IS NULL OR datetime_precision <> 6)
  ) THEN
    EXECUTE 'ALTER TABLE webhooks ALTER COLUMN created_at TYPE timestamp(6) with time zone';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'updated_at'
      AND data_type = 'timestamp without time zone'
  ) THEN
    EXECUTE 'ALTER TABLE webhooks ALTER COLUMN updated_at TYPE timestamp(6) with time zone USING updated_at AT TIME ZONE ''UTC''';
  ELSIF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'updated_at'
      AND data_type = 'timestamp with time zone'
      AND (datetime_precision IS NULL OR datetime_precision <> 6)
  ) THEN
    EXECUTE 'ALTER TABLE webhooks ALTER COLUMN updated_at TYPE timestamp(6) with time zone';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'last_delivery_at'
      AND data_type = 'timestamp without time zone'
  ) THEN
    EXECUTE 'ALTER TABLE webhooks ALTER COLUMN last_delivery_at TYPE timestamp(6) with time zone USING last_delivery_at AT TIME ZONE ''UTC''';
  ELSIF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhooks'
      AND column_name = 'last_delivery_at'
      AND data_type = 'timestamp with time zone'
      AND (datetime_precision IS NULL OR datetime_precision <> 6)
  ) THEN
    EXECUTE 'ALTER TABLE webhooks ALTER COLUMN last_delivery_at TYPE timestamp(6) with time zone';
  END IF;
END;
$$ LANGUAGE plpgsql;

ALTER TABLE webhooks ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE webhooks ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

-- Normalise retry defaults to millisecond values.
ALTER TABLE webhooks ALTER COLUMN initial_retry_delay SET DEFAULT 5000;
ALTER TABLE webhooks ALTER COLUMN max_retry_delay SET DEFAULT 300000;

UPDATE webhooks
SET initial_retry_delay = 5000
WHERE initial_retry_delay = 60;

UPDATE webhooks
SET max_retry_delay = 300000
WHERE max_retry_delay = 3600;

-- Convert webhook payloads to jsonb and relax nullability to match entity mapping.
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'webhook_deliveries'
      AND column_name = 'payload'
      AND data_type <> 'jsonb'
  ) THEN
    EXECUTE $conv$
      UPDATE webhook_deliveries
      SET payload = '{}' 
      WHERE payload IS NULL OR trim(payload) = ''
    $conv$;

    EXECUTE 'ALTER TABLE webhook_deliveries ALTER COLUMN payload TYPE jsonb USING payload::jsonb';
  END IF;

  EXECUTE 'ALTER TABLE webhook_deliveries ALTER COLUMN payload DROP NOT NULL';
END;
$$ LANGUAGE plpgsql;

ALTER TABLE webhook_deliveries ALTER COLUMN event_id DROP NOT NULL;

-- Recreate the webhook statistics view with the expected projection.
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
  SELECT 
    webhook_id, 
    COUNT(*) AS pending_deliveries
  FROM webhook_deliveries 
  WHERE status IN ('PENDING', 'RETRY')
  GROUP BY webhook_id
) pending ON w.id = pending.webhook_id
LEFT JOIN (
  SELECT 
    webhook_id, 
    COUNT(*) AS retry_deliveries
  FROM webhook_deliveries 
  WHERE status = 'RETRY'
  GROUP BY webhook_id
) retry ON w.id = retry.webhook_id;

-- Ensure supporting indexes exist (idempotent when rerun).
CREATE INDEX IF NOT EXISTS idx_webhook_deliveries_created_at ON webhook_deliveries(created_at);
CREATE INDEX IF NOT EXISTS idx_webhook_deliveries_next_retry_at ON webhook_deliveries(next_retry_at);
CREATE INDEX IF NOT EXISTS idx_delivery_attempts_delivery_id ON webhook_delivery_attempts(delivery_id);
CREATE INDEX IF NOT EXISTS idx_delivery_attempts_attempted_at ON webhook_delivery_attempts(attempted_at);
CREATE INDEX IF NOT EXISTS idx_webhook_event_types_event_type ON webhook_event_types(event_type);
CREATE INDEX IF NOT EXISTS idx_webhook_headers_webhook_id ON webhook_headers(webhook_id);
