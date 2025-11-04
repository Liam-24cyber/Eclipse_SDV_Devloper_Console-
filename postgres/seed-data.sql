-- ========================================
-- SDV Developer Console - Seed Data
-- ========================================
-- This script populates the database with sample scenarios and tracks
-- for testing and development purposes
-- ========================================
-- IMPORTANT: Scenarios must have status='CREATED' to appear in the UI!
-- ========================================

-- Clean existing data (optional - comment out if you want to keep existing data)
-- TRUNCATE TABLE scenario, track, simulation CASCADE;

-- ========================================
-- SCENARIOS
-- ========================================

-- Urban Driving Scenarios (using CAN type - compatible with GraphQL schema)
INSERT INTO scenario (id, name, description, type, status, created_at, updated_at, created_by) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Urban Traffic Navigation', 'Navigate through dense city traffic with multiple intersections and traffic lights', 'CAN', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440002', 'Pedestrian Crossing Detection', 'Detect and respond to pedestrians crossing at marked and unmarked crossings', 'CAN', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440003', 'Parking Maneuvers', 'Test automated parking in various scenarios including parallel, perpendicular, and angle parking', 'CAN', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440004', 'Roundabout Navigation', 'Navigate multi-lane roundabouts with yielding to traffic', 'CAN', 'CREATED', NOW(), NOW(), 'system'),

-- Highway Scenarios (using CAN type - compatible with GraphQL schema)
('550e8400-e29b-41d4-a716-446655440005', 'Highway Lane Keeping', 'Maintain lane position at highway speeds with lane departure warnings', 'CAN', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440006', 'Adaptive Cruise Control', 'Test adaptive cruise control with varying traffic density and speeds', 'CAN', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440007', 'Lane Change Assist', 'Automated lane changes with blind spot detection and safe gap finding', 'CAN', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440008', 'Highway Merging', 'Merge onto highway from on-ramp with traffic flow analysis', 'CAN', 'CREATED', NOW(), NOW(), 'system'),

-- Safety Scenarios (using MQTT type - compatible with GraphQL schema)
('550e8400-e29b-41d4-a716-446655440009', 'Emergency Braking', 'Test automatic emergency braking with various obstacles and speeds', 'MQTT', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440010', 'Collision Avoidance', 'Avoid collisions with sudden obstacles and crossing vehicles', 'MQTT', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440011', 'Vulnerable Road Users', 'Detect and protect cyclists, motorcyclists, and pedestrians', 'MQTT', 'CREATED', NOW(), NOW(), 'system'),

-- Weather Conditions (using MQTT type - compatible with GraphQL schema)
('550e8400-e29b-41d4-a716-446655440012', 'Rain Driving', 'Test sensor performance and vehicle control in heavy rain conditions', 'MQTT', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440013', 'Fog Navigation', 'Navigate with reduced visibility in foggy conditions', 'MQTT', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440014', 'Night Driving', 'Test headlight adaptive systems and low-light sensor performance', 'MQTT', 'CREATED', NOW(), NOW(), 'system'),

-- Edge Cases (using CAN type - compatible with GraphQL schema)
('550e8400-e29b-41d4-a716-446655440015', 'Construction Zone Navigation', 'Navigate through construction zones with lane shifts and temporary signs', 'CAN', 'CREATED', NOW(), NOW(), 'system'),
('550e8400-e29b-41d4-a716-446655440016', 'School Zone Safety', 'Reduced speed in school zones with child detection', 'MQTT', 'CREATED', NOW(), NOW(), 'system');

-- ========================================
-- TRACKS
-- ========================================

-- Urban Test Tracks
INSERT INTO track (id, name, description, track_type, state, duration, created_at) VALUES
('660e8400-e29b-41d4-a716-446655440001', 'Downtown City Circuit', 'Dense urban environment with traffic lights, intersections, and pedestrian crossings', 'URBAN', 'ACTIVE', '15 minutes', NOW()),
('660e8400-e29b-41d4-a716-446655440002', 'Suburban Route', 'Mixed residential and commercial areas with moderate traffic', 'SUBURBAN', 'ACTIVE', '20 minutes', NOW()),
('660e8400-e29b-41d4-a716-446655440003', 'Urban Parking Complex', 'Multi-story parking facility with various parking scenarios', 'PARKING', 'ACTIVE', '10 minutes', NOW()),

-- Highway Tracks
('660e8400-e29b-41d4-a716-446655440004', 'Autobahn Test Section', 'High-speed highway section with multiple lanes and on/off ramps', 'HIGHWAY', 'ACTIVE', '30 minutes', NOW()),
('660e8400-e29b-41d4-a716-446655440005', 'Highway Interchange Complex', 'Complex highway interchange with multiple merging and diverging lanes', 'HIGHWAY', 'ACTIVE', '25 minutes', NOW()),

-- Safety Test Tracks
('660e8400-e29b-41d4-a716-446655440006', 'Emergency Response Track', 'Dedicated track for emergency braking and collision avoidance tests', 'SAFETY', 'ACTIVE', '12 minutes', NOW()),
('660e8400-e29b-41d4-a716-446655440007', 'Vulnerable User Circuit', 'Track designed for testing interactions with pedestrians and cyclists', 'SAFETY', 'ACTIVE', '18 minutes', NOW()),

-- Environmental Tracks
('660e8400-e29b-41d4-a716-446655440008', 'Weather Simulation Track', 'Track with artificial rain, fog, and lighting control systems', 'WEATHER', 'ACTIVE', '22 minutes', NOW()),
('660e8400-e29b-41d4-a716-446655440009', 'Night Testing Circuit', 'Track optimized for night driving and low-light condition testing', 'WEATHER', 'ACTIVE', '20 minutes', NOW()),

-- Mixed Condition Tracks
('660e8400-e29b-41d4-a716-446655440010', 'Country Road Mix', 'Combination of rural roads, small towns, and varying road conditions', 'MIXED', 'ACTIVE', '35 minutes', NOW()),
('660e8400-e29b-41d4-a716-446655440011', 'Alpine Route', 'Mountain roads with curves, elevation changes, and scenic routes', 'MOUNTAIN', 'ACTIVE', '40 minutes', NOW()),

-- Comprehensive Test Tracks
('660e8400-e29b-41d4-a716-446655440012', 'Full Stack Test Track', 'Comprehensive track covering urban, highway, and rural scenarios', 'COMPREHENSIVE', 'ACTIVE', '60 minutes', NOW()),
('660e8400-e29b-41d4-a716-446655440013', 'Proving Ground Circuit', 'Professional proving ground with all scenario types', 'COMPREHENSIVE', 'ACTIVE', '90 minutes', NOW());

-- ========================================
-- Verification Queries
-- ========================================
-- Run these to verify the data was inserted correctly

SELECT 'Total Scenarios:' as info, COUNT(*) as count FROM scenario;
SELECT 'Total Tracks:' as info, COUNT(*) as count FROM track;

SELECT 'Scenarios by Type' as info;
SELECT type, COUNT(*) as count FROM scenario GROUP BY type ORDER BY count DESC;

SELECT 'Tracks by Type' as info;
SELECT track_type, COUNT(*) as count FROM track GROUP BY track_type ORDER BY count DESC;

-- ========================================
-- Sample Output:
-- You should now have:
-- - 16 scenarios covering various SDV test cases
-- - 13 tracks with different characteristics
-- - Ready to use in simulation runs via the UI
-- ========================================
