#!/usr/bin/env node

const express = require('express');
const { exec } = require('child_process');
const path = require('path');

const app = express();
const PORT = 9191;

// Middleware
app.use(express.json());

// CORS for frontend access
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers', 'Origin, X-Requested-With, Content-Type, Accept');
    res.header('Access-Control-Allow-Methods', 'GET, POST, PUT, DELETE, OPTIONS');
    next();
});

// Health check endpoint
app.get('/health', (req, res) => {
    res.json({ 
        status: 'healthy', 
        timestamp: new Date().toISOString(),
        service: 'SDV E2E API Server'
    });
});

// System status endpoint
app.get('/api/status', (req, res) => {
    // Check if Docker services are running
    exec('docker ps | grep postgres', (error, stdout, stderr) => {
        const isHealthy = !error && stdout.includes('postgres');
        res.json({
            status: isHealthy ? 'ready' : 'not ready',
            services: {
                postgres: isHealthy,
                docker: !error
            },
            timestamp: new Date().toISOString()
        });
    });
});

// Main E2E simulation endpoint - This is what you'll call from Postman!
app.post('/api/run-simulation', (req, res) => {
    console.log('🚀 E2E Simulation triggered via API');
    
    // Set response headers for streaming
    res.writeHead(200, {
        'Content-Type': 'application/json',
        'Transfer-Encoding': 'chunked'
    });

    // Send initial response
    res.write(JSON.stringify({
        status: 'started',
        message: 'E2E simulation workflow initiated with Prometheus/Grafana integration',
        timestamp: new Date().toISOString(),
        monitoring: {
            prometheus: 'http://localhost:9090',
            grafana: 'http://localhost:3001',
            pushgateway: 'http://localhost:9091'
        }
    }) + '\n');

    // Execute the enhanced E2E demo script
    const scriptPath = path.join(__dirname, 'run-e2e-demo.sh');
    const child = exec(`bash "${scriptPath}"`, { 
        cwd: __dirname,
        env: { ...process.env, FORCE_COLOR: '0' } // Disable colors for API output
    });

    let outputBuffer = '';
    let errorBuffer = '';

    // Capture stdout
    child.stdout.on('data', (data) => {
        outputBuffer += data;
        console.log(data.toString());
    });

    // Capture stderr
    child.stderr.on('data', (data) => {
        errorBuffer += data;
        console.error(data.toString());
    });

    // Handle completion
    child.on('close', (code) => {
        const success = code === 0;
        const result = {
            status: success ? 'completed' : 'failed',
            exitCode: code,
            message: success ? 'E2E simulation completed with metrics pushed to Prometheus' : 'E2E simulation failed',
            output: outputBuffer,
            error: errorBuffer,
            timestamp: new Date().toISOString(),
            duration: 'approximately 30-50 seconds',
            monitoring: {
                metricsAvailable: success,
                grafanaDashboard: 'http://localhost:3001/dashboards',
                prometheusTargets: 'http://localhost:9090/targets'
            }
        };

        // Send final response
        res.write(JSON.stringify(result));
        res.end();
        
        console.log(`✅ E2E simulation ${success ? 'completed' : 'failed'} with exit code: ${code}`);
    });

    // Handle errors
    child.on('error', (err) => {
        const result = {
            status: 'error',
            message: 'Failed to execute E2E simulation script',
            error: err.message,
            timestamp: new Date().toISOString()
        };
        
        res.write(JSON.stringify(result));
        res.end();
        
        console.error('❌ E2E simulation error:', err);
    });
});

// Quick scenario creation endpoint (alternative)
app.post('/api/scenario/create', (req, res) => {
    const { name, description, type = 'CAN' } = req.body;
    const scenarioName = name || `API Scenario ${new Date().toLocaleTimeString()}`;
    const scenarioDesc = description || 'Created via API';

    const query = `
        INSERT INTO scenario (name, description, type, status, created_at, updated_at, created_by) 
        VALUES ('${scenarioName}', '${scenarioDesc}', '${type}', 'CREATED', NOW(), NOW(), 'api-server')
        RETURNING id, name, status;
    `;

    exec(`docker exec postgres psql -U postgres -d postgres -t -A -c "${query}"`, (error, stdout, stderr) => {
        if (error) {
            return res.status(500).json({
                success: false,
                error: error.message,
                stderr: stderr
            });
        }

        const lines = stdout.trim().split('\n');
        const dataLine = lines.find(line => line.includes('|') && !line.includes('id|name|status'));
        
        if (dataLine) {
            const [id, name, status] = dataLine.split('|');
            res.json({
                success: true,
                scenario: { id, name, status },
                message: 'Scenario created successfully'
            });
        } else {
            res.status(500).json({
                success: false,
                error: 'Failed to parse scenario creation result',
                output: stdout
            });
        }
    });
});

// Get available tracks
app.get('/api/tracks', (req, res) => {
    exec('docker exec postgres psql -U postgres -d postgres -t -A -c "SELECT id, name, description FROM track LIMIT 10;"', (error, stdout, stderr) => {
        if (error) {
            return res.status(500).json({
                success: false,
                error: error.message
            });
        }

        const tracks = stdout.trim().split('\n')
            .filter(line => line && line.includes('|'))
            .map(line => {
                const [id, name, description] = line.split('|');
                return { id, name, description };
            });

        res.json({
            success: true,
            tracks: tracks,
            count: tracks.length
        });
    });
});

// Start the server
app.listen(PORT, () => {
    console.log(`🚀 SDV E2E API Server running on http://localhost:${PORT}`);
    console.log('');
    console.log('📡 Available Endpoints:');
    console.log(`   GET  /health                     - Health check`);
    console.log(`   GET  /api/status                 - System status`);
    console.log(`   POST /api/run-simulation         - Run complete E2E flow`);
    console.log(`   POST /api/scenario/create        - Create scenario only`);
    console.log(`   GET  /api/tracks                 - Get available tracks`);
    console.log('');
    console.log('🎯 For Postman: POST http://localhost:9191/api/run-simulation');
    console.log('');
});

// Graceful shutdown
process.on('SIGINT', () => {
    console.log('\n📴 Shutting down E2E API Server...');
    process.exit(0);
});

module.exports = app;
