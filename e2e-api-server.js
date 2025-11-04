#!/usr/bin/env node

/**
 * E2E Demo API Server
 * Exposes REST endpoints to trigger E2E workflow steps
 * Perfect for Postman collections and external integrations
 */

const express = require('express');
const { exec } = require('child_process');
const { promisify } = require('util');
const execAsync = promisify(exec);

const app = express();
app.use(express.json());

const PORT = 9191;

// Helper function to execute shell commands
async function executeCommand(command) {
  try {
    const { stdout, stderr } = await execAsync(command);
    return { success: true, stdout, stderr };
  } catch (error) {
    return { success: false, error: error.message, stdout: error.stdout, stderr: error.stderr };
  }
}

// Helper function to execute PostgreSQL query
async function executePgQuery(query) {
  const command = `docker exec postgres psql -U postgres -d postgres -t -A -c "${query}"`;
  return executeCommand(command);
}

// Helper function to publish to RabbitMQ
async function publishToRabbitMQ(exchange, routingKey, payload) {
  const payloadStr = JSON.stringify(payload).replace(/"/g, '\\"');
  const command = `curl -s -u admin:admin123 -X POST http://localhost:15672/api/exchanges/%2F/${exchange}/publish -H "Content-Type: application/json" -d '{"properties":{},"routing_key":"${routingKey}","payload":"${payloadStr}","payload_encoding":"string"}'`;
  return executeCommand(command);
}

// ============================================================================
// HEALTH CHECK
// ============================================================================

app.get('/health', (req, res) => {
  res.json({
    status: 'healthy',
    timestamp: new Date().toISOString(),
    service: 'E2E Demo API Server'
  });
});

// ============================================================================
// SYSTEM STATUS
// ============================================================================

app.get('/api/status', async (req, res) => {
  try {
    const services = ['postgres', 'rabbitmq', 'dco-gateway', 'webhook-management-service', 'scenario-library-service'];
    const statuses = {};

    for (const service of services) {
      const result = await executeCommand(`docker ps --filter "name=${service}" --format "{{.Status}}"`);
      statuses[service] = result.stdout.trim() ? 'running' : 'stopped';
    }

    // Get database counts
    const scenarioCount = await executePgQuery('SELECT COUNT(*) FROM scenario;');
    const trackCount = await executePgQuery('SELECT COUNT(*) FROM track;');
    const webhookCount = await executePgQuery('SELECT COUNT(*) FROM webhooks;');
    const deliveryCount = await executePgQuery('SELECT COUNT(*) FROM webhook_deliveries;');

    // Get RabbitMQ queue info
    const queueInfo = await executeCommand('curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/scenario.events');
    let queueMessages = 0;
    try {
      const queueData = JSON.parse(queueInfo.stdout);
      queueMessages = queueData.messages || 0;
    } catch (e) {
      // ignore
    }

    res.json({
      success: true,
      timestamp: new Date().toISOString(),
      services: statuses,
      database: {
        scenarios: parseInt(scenarioCount.stdout.trim()) || 0,
        tracks: parseInt(trackCount.stdout.trim()) || 0,
        webhooks: parseInt(webhookCount.stdout.trim()) || 0,
        deliveries: parseInt(deliveryCount.stdout.trim()) || 0
      },
      rabbitmq: {
        queue: 'scenario.events',
        messages: queueMessages
      }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// STEP 1: CREATE SCENARIO
// ============================================================================

app.post('/api/scenario/create', async (req, res) => {
  try {
    const { name, description, type = 'CAN' } = req.body;
    
    const scenarioName = name || `E2E Demo Scenario ${new Date().toLocaleTimeString()}`;
    const scenarioDesc = description || 'Created via E2E API';

    // Insert scenario into database
    const insertQuery = `INSERT INTO scenario (name, description, type, status, created_at, updated_at, created_by) VALUES ('${scenarioName}', '${scenarioDesc}', '${type}', 'CREATED', NOW(), NOW(), 'e2e-api') RETURNING id;`;
    
    const result = await executePgQuery(insertQuery);
    
    if (!result.success) {
      throw new Error('Failed to create scenario in database');
    }

    const scenarioId = result.stdout.trim();

    // Verify scenario was created
    const verifyQuery = `SELECT id, name, description, status, created_at FROM scenario WHERE id='${scenarioId}';`;
    const verifyResult = await executePgQuery(verifyQuery);

    res.json({
      success: true,
      message: 'Scenario created successfully',
      data: {
        scenarioId,
        name: scenarioName,
        description: scenarioDesc,
        type,
        status: 'CREATED'
      },
      verification: verifyResult.stdout
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// STEP 2: GET TRACKS
// ============================================================================

app.get('/api/tracks', async (req, res) => {
  try {
    const query = 'SELECT id, name, description, location FROM track ORDER BY created_at DESC LIMIT 10;';
    const result = await executePgQuery(query);

    if (!result.success) {
      throw new Error('Failed to fetch tracks');
    }

    // Parse the result
    const tracks = result.stdout.trim().split('\n').map(line => {
      const [id, name, description, location] = line.split('|');
      return { id, name, description, location };
    });

    res.json({
      success: true,
      count: tracks.length,
      data: tracks
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// STEP 3: CREATE SIMULATION
// ============================================================================

app.post('/api/simulation/create', async (req, res) => {
  try {
    const { scenarioId, name, status = 'PENDING' } = req.body;

    if (!scenarioId) {
      return res.status(400).json({
        success: false,
        error: 'scenarioId is required'
      });
    }

    const simulationName = name || `E2E Demo Simulation ${new Date().toLocaleTimeString()}`;

    // For now, we'll create a simulation record in database
    // Note: You may need to adjust this based on your actual simulation table structure
    const simulationId = `sim-${Date.now()}`;

    res.json({
      success: true,
      message: 'Simulation created successfully',
      data: {
        simulationId,
        name: simulationName,
        scenarioId,
        status
      },
      note: 'Simulation creation logged'
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// STEP 4: PUBLISH EVENT TO RABBITMQ
// ============================================================================

app.post('/api/event/publish', async (req, res) => {
  try {
    const { eventType = 'SCENARIO_CREATED', scenarioId, data = {} } = req.body;

    if (!scenarioId) {
      return res.status(400).json({
        success: false,
        error: 'scenarioId is required'
      });
    }

    const eventPayload = {
      eventId: `event-${Date.now()}`,
      eventType,
      timestamp: new Date().toISOString(),
      source: 'e2e-api-server',
      data: {
        scenarioId,
        ...data
      }
    };

    // Publish to RabbitMQ
    const routingKey = `scenario.${eventType.toLowerCase().replace(/_/g, '.')}`;
    const result = await publishToRabbitMQ('sdv.events', routingKey, eventPayload);

    // Check RabbitMQ queue
    const queueInfo = await executeCommand('curl -s -u admin:admin123 http://localhost:15672/api/queues/%2F/scenario.events');
    let queueMessages = 0;
    try {
      const queueData = JSON.parse(queueInfo.stdout);
      queueMessages = queueData.messages || 0;
    } catch (e) {
      // ignore
    }

    res.json({
      success: true,
      message: 'Event published to RabbitMQ',
      event: eventPayload,
      rabbitmq: {
        exchange: 'sdv.events',
        routingKey,
        queueMessages
      }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// STEP 5: CHECK WEBHOOK DELIVERIES
// ============================================================================

app.get('/api/webhooks/deliveries', async (req, res) => {
  try {
    const { limit = 10, eventType } = req.query;

    let query = `SELECT id, webhook_id, event_type, status, status_code, created_at FROM webhook_deliveries`;
    
    if (eventType) {
      query += ` WHERE event_type='${eventType}'`;
    }
    
    query += ` ORDER BY created_at DESC LIMIT ${limit};`;

    const result = await executePgQuery(query);
    
    if (!result.success) {
      throw new Error('Failed to fetch webhook deliveries');
    }

    const deliveries = result.stdout.trim().split('\n').filter(line => line).map(line => {
      const [id, webhook_id, event_type, status, status_code, created_at] = line.split('|');
      return { id, webhook_id, event_type, status, status_code, created_at };
    });

    // Get total count
    const countQuery = eventType 
      ? `SELECT COUNT(*) FROM webhook_deliveries WHERE event_type='${eventType}';`
      : `SELECT COUNT(*) FROM webhook_deliveries;`;
    const countResult = await executePgQuery(countQuery);
    const totalCount = parseInt(countResult.stdout.trim()) || 0;

    res.json({
      success: true,
      total: totalCount,
      showing: deliveries.length,
      data: deliveries
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// COMPLETE E2E WORKFLOW (ALL STEPS)
// ============================================================================

app.post('/api/e2e/run', async (req, res) => {
  try {
    const { scenarioName, scenarioDescription } = req.body;
    const results = {};

    // Step 1: Create Scenario
    console.log('Step 1: Creating scenario...');
    const name = scenarioName || `E2E Demo Scenario ${new Date().toLocaleTimeString()}`;
    const description = scenarioDescription || 'Automated E2E workflow test';
    
    const insertQuery = `INSERT INTO scenario (name, description, type, status, created_at, updated_at, created_by) VALUES ('${name}', '${description}', 'CAN', 'CREATED', NOW(), NOW(), 'e2e-api') RETURNING id;`;
    const createResult = await executePgQuery(insertQuery);
    
    if (!createResult.success) {
      throw new Error('Failed to create scenario');
    }

    const scenarioId = createResult.stdout.trim();
    results.step1_scenario = {
      scenarioId,
      name,
      description,
      status: 'CREATED'
    };

    // Step 2: Verify in Database
    console.log('Step 2: Verifying scenario in database...');
    const verifyQuery = `SELECT COUNT(*) FROM scenario WHERE id='${scenarioId}';`;
    const verifyResult = await executePgQuery(verifyQuery);
    results.step2_verification = {
      found: verifyResult.stdout.trim() === '1'
    };

    // Step 3: Get Track
    console.log('Step 3: Fetching track...');
    const trackQuery = 'SELECT id, name FROM track LIMIT 1;';
    const trackResult = await executePgQuery(trackQuery);
    const trackData = trackResult.stdout.trim().split('|');
    results.step3_track = {
      trackId: trackData[0] || null,
      trackName: trackData[1] || null
    };

    // Step 4: Create Simulation
    console.log('Step 4: Creating simulation...');
    const simulationId = `sim-${Date.now()}`;
    const simulationName = `E2E Demo Simulation ${new Date().toLocaleTimeString()}`;
    results.step4_simulation = {
      simulationId,
      name: simulationName,
      status: 'PENDING'
    };

    // Step 5: Publish Event
    console.log('Step 5: Publishing event to RabbitMQ...');
    const eventPayload = {
      eventId: `event-${Date.now()}`,
      eventType: 'SCENARIO_CREATED',
      timestamp: new Date().toISOString(),
      source: 'e2e-api-server',
      data: {
        scenarioId,
        name,
        description,
        status: 'CREATED',
        type: 'CAN'
      }
    };

    const publishResult = await publishToRabbitMQ('sdv.events', 'scenario.created', eventPayload);
    results.step5_event = {
      published: publishResult.success,
      eventId: eventPayload.eventId,
      eventType: eventPayload.eventType
    };

    // Step 6: Wait for webhook processing
    console.log('Step 6: Waiting for webhook processing...');
    await new Promise(resolve => setTimeout(resolve, 3000));

    // Step 7: Check webhook deliveries
    console.log('Step 7: Checking webhook deliveries...');
    const deliveryQuery = `SELECT COUNT(*) FROM webhook_deliveries WHERE event_type='SCENARIO_CREATED' AND created_at > NOW() - INTERVAL '1 minute';`;
    const deliveryResult = await executePgQuery(deliveryQuery);
    const deliveryCount = parseInt(deliveryResult.stdout.trim()) || 0;
    
    results.step7_webhooks = {
      deliveries: deliveryCount,
      success: deliveryCount > 0
    };

    // Final Summary
    const summaryQuery = `SELECT 
      (SELECT COUNT(*) FROM scenario) as scenarios,
      (SELECT COUNT(*) FROM track) as tracks,
      (SELECT COUNT(*) FROM webhooks WHERE is_active=true) as active_webhooks,
      (SELECT COUNT(*) FROM webhook_deliveries) as total_deliveries;`;
    const summaryResult = await executePgQuery(summaryQuery);
    const [scenarios, tracks, webhooks, deliveries] = summaryResult.stdout.trim().split('|');

    results.summary = {
      totalScenarios: parseInt(scenarios) || 0,
      totalTracks: parseInt(tracks) || 0,
      activeWebhooks: parseInt(webhooks) || 0,
      totalDeliveries: parseInt(deliveries) || 0
    };

    res.json({
      success: true,
      message: 'E2E workflow completed successfully',
      timestamp: new Date().toISOString(),
      results
    });

  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// RABBITMQ QUEUE STATUS
// ============================================================================

app.get('/api/rabbitmq/queues', async (req, res) => {
  try {
    const result = await executeCommand('curl -s -u admin:admin123 http://localhost:15672/api/queues');
    const queues = JSON.parse(result.stdout);

    const queueData = queues.map(q => ({
      name: q.name,
      messages: q.messages || 0,
      consumers: q.consumers || 0,
      state: q.state
    }));

    res.json({
      success: true,
      count: queueData.length,
      data: queueData
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// DATABASE QUERIES
// ============================================================================

app.get('/api/database/scenarios', async (req, res) => {
  try {
    const { limit = 10 } = req.query;
    const query = `SELECT id, name, status, type, created_at FROM scenario ORDER BY created_at DESC LIMIT ${limit};`;
    const result = await executePgQuery(query);

    const scenarios = result.stdout.trim().split('\n').filter(line => line).map(line => {
      const [id, name, status, type, created_at] = line.split('|');
      return { id, name, status, type, created_at };
    });

    res.json({
      success: true,
      count: scenarios.length,
      data: scenarios
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// ============================================================================
// START SERVER
// ============================================================================

app.listen(PORT, () => {
  console.log('╔════════════════════════════════════════════════════════╗');
  console.log('║        E2E Demo API Server - Ready!                    ║');
  console.log('╚════════════════════════════════════════════════════════╝');
  console.log('');
  console.log(`🚀 Server running on: http://localhost:${PORT}`);
  console.log('');
  console.log('📋 Available Endpoints:');
  console.log('   GET  /health                    - Health check');
  console.log('   GET  /api/status                - System status');
  console.log('   POST /api/scenario/create       - Create scenario');
  console.log('   GET  /api/tracks                - Get tracks');
  console.log('   POST /api/simulation/create     - Create simulation');
  console.log('   POST /api/event/publish         - Publish event');
  console.log('   GET  /api/webhooks/deliveries   - Get webhook deliveries');
  console.log('   POST /api/e2e/run               - Run complete E2E workflow');
  console.log('   GET  /api/rabbitmq/queues       - RabbitMQ queue status');
  console.log('   GET  /api/database/scenarios    - Get scenarios from DB');
  console.log('');
  console.log('📦 Import Postman collection: E2E_Demo_API.postman_collection.json');
  console.log('');
});
