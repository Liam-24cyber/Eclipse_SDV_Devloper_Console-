#!/usr/bin/env node
/**
 * Mock Webhook Server for Testing
 * Receives webhook deliveries and logs them
 */

const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 9999;
const LOG_FILE = path.join(__dirname, 'webhook-deliveries.log');

// Store received webhooks in memory
const receivedWebhooks = [];

const server = http.createServer((req, res) => {
  if (req.method === 'POST' && req.url === '/webhook') {
    let body = '';
    
    req.on('data', chunk => {
      body += chunk.toString();
    });
    
    req.on('end', () => {
      const timestamp = new Date().toISOString();
      const webhook = {
        timestamp,
        headers: req.headers,
        body: body,
        parsedBody: null
      };
      
      try {
        webhook.parsedBody = JSON.parse(body);
      } catch (e) {
        webhook.parseError = e.message;
      }
      
      receivedWebhooks.push(webhook);
      
      // Log to file
      const logEntry = `\n${'='.repeat(80)}\n` +
                      `Timestamp: ${timestamp}\n` +
                      `Headers: ${JSON.stringify(req.headers, null, 2)}\n` +
                      `Body: ${body}\n` +
                      `${'='.repeat(80)}\n`;
      
      fs.appendFileSync(LOG_FILE, logEntry);
      
      // Log to console
      console.log(`\n✅ Webhook received at ${timestamp}`);
      console.log(`Headers:`, req.headers);
      console.log(`Body:`, webhook.parsedBody || body);
      console.log(`Total webhooks received: ${receivedWebhooks.length}`);
      
      // Return success response
      res.writeHead(200, { 'Content-Type': 'application/json' });
      res.end(JSON.stringify({ 
        status: 'success', 
        message: 'Webhook received',
        timestamp,
        count: receivedWebhooks.length
      }));
    });
    
  } else if (req.method === 'GET' && req.url === '/webhooks') {
    // List all received webhooks
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ 
      count: receivedWebhooks.length,
      webhooks: receivedWebhooks 
    }, null, 2));
    
  } else if (req.method === 'GET' && req.url === '/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ 
      status: 'healthy',
      webhooksReceived: receivedWebhooks.length,
      uptime: process.uptime()
    }));
    
  } else {
    res.writeHead(404, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ error: 'Not found' }));
  }
});

server.listen(PORT, '0.0.0.0', () => {
  console.log('\n' + '='.repeat(80));
  console.log('🎯 Mock Webhook Server Started');
  console.log('='.repeat(80));
  console.log(`\n📍 Listening on: http://localhost:${PORT}`);
  console.log(`\n📥 Webhook endpoint: http://localhost:${PORT}/webhook`);
  console.log(`📊 View received: http://localhost:${PORT}/webhooks`);
  console.log(`💚 Health check: http://localhost:${PORT}/health`);
  console.log(`\n📝 Logging to: ${LOG_FILE}`);
  console.log('\n' + '='.repeat(80));
  console.log('Waiting for webhooks...\n');
});

// Handle graceful shutdown
process.on('SIGINT', () => {
  console.log('\n\n🛑 Shutting down webhook server...');
  console.log(`📊 Total webhooks received: ${receivedWebhooks.length}`);
  server.close(() => {
    console.log('✅ Server closed');
    process.exit(0);
  });
});
