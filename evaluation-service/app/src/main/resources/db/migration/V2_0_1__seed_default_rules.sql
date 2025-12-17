-- Seed default evaluation rules (idempotent - safe to run multiple times)

INSERT INTO evaluation_rules (rule_name, metric_name, operator, threshold_value, weight, is_active, created_at, updated_at)
VALUES
  ('Max Duration', 
   'simulation_duration_seconds', 
   '<', 
   60.0, 
   20, 
   true, 
   NOW(), 
   NOW()),
  
  ('Max Latency', 
   'webhook_delivery_duration_seconds', 
   '<', 
   2.0, 
   25, 
   true, 
   NOW(), 
   NOW()),
  
  ('Zero Errors', 
   'simulation_errors_total', 
   '=', 
   0.0, 
   30, 
   true, 
   NOW(), 
   NOW()),
  
  ('CPU Limit', 
   'simulation_cpu_percent', 
   '<', 
   80.0, 
   15, 
   true, 
   NOW(), 
   NOW()),
  
  ('Memory Limit', 
   'simulation_memory_percent', 
   '<', 
   85.0, 
   10, 
   true, 
   NOW(), 
   NOW())
ON CONFLICT (rule_name) DO NOTHING;
