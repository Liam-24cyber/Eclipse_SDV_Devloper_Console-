// Mock data for simulation results with comprehensive test details
// This file contains all mock data for the Results page including:
// - Detailed test results with pass/fail status
// - Visual log events with timestamps
// - Performance metrics and safety scores
// - Component-level test results
// You can easily switch between mock and real data by importing from here

export const MOCK_CONFIG = {
  USE_MOCK_DATA: false,  // Toggle mock vs real data - CHANGED TO USE REAL API
  MOCK_DELAY: 800,       // Simulate network delay for results
  ENABLE_PAGINATION: true
}

// Detailed test checks with pass/fail status
export const mockTestChecks = {
  'sim-001': [
    { id: 'speed-001', name: 'Maximum Speed Compliance', status: 'PASSED', expected: '≤120 km/h', actual: '118.5 km/h', timestamp: '2025-10-15T10:32:15Z' },
    { id: 'speed-002', name: 'Average Speed Consistency', status: 'PASSED', expected: '80-90 km/h', actual: '85.2 km/h', timestamp: '2025-10-15T10:45:22Z' },
    { id: 'fuel-001', name: 'Fuel Efficiency Target', status: 'PASSED', expected: '≤8.0 L/100km', actual: '7.8 L/100km', timestamp: '2025-10-15T11:20:18Z' },
    { id: 'safety-001', name: 'Lane Keeping Accuracy', status: 'PASSED', expected: '≥90%', actual: '96.8%', timestamp: '2025-10-15T11:35:45Z' },
    { id: 'safety-002', name: 'Following Distance', status: 'WARNING', expected: '≥3.0s', actual: '2.8s', timestamp: '2025-10-15T11:42:12Z' }
  ],
  'sim-002': [
    { id: 'brake-001', name: 'Emergency Brake Response Time', status: 'PASSED', expected: '≤1.0s', actual: '0.8s', timestamp: '2025-10-14T14:25:33Z' },
    { id: 'brake-002', name: 'Stopping Distance Compliance', status: 'PASSED', expected: '≤50m', actual: '45.5m', timestamp: '2025-10-14T14:28:15Z' },
    { id: 'collision-001', name: 'Collision Avoidance Rate', status: 'PASSED', expected: '≥95%', actual: '100%', timestamp: '2025-10-14T14:45:22Z' },
    { id: 'sensor-001', name: 'Radar Detection Accuracy', status: 'PASSED', expected: '≥98%', actual: '99.2%', timestamp: '2025-10-14T15:02:18Z' },
    { id: 'system-001', name: 'ABS System Response', status: 'PASSED', expected: 'Functional', actual: 'Optimal', timestamp: '2025-10-14T15:08:45Z' }
  ]
}

// Visual event logs with detailed information
export const mockEventLogs = {
  'sim-001': [
    { id: 'log-001', timestamp: '2025-10-15T10:30:00Z', level: 'INFO', component: 'SimulationEngine', message: 'Highway Speed Test simulation started', category: 'SYSTEM', details: 'Initializing 5 vehicles on highway track' },
    { id: 'log-002', timestamp: '2025-10-15T10:30:15Z', level: 'INFO', component: 'VehicleController', message: 'All vehicles spawned successfully', category: 'VEHICLE', details: 'Vehicle IDs: V001, V002, V003, V004, V005' },
    { id: 'log-003', timestamp: '2025-10-15T10:32:15Z', level: 'DEBUG', component: 'SpeedMonitor', message: 'Speed check executed - PASSED', category: 'TEST', details: 'Maximum speed: 118.5 km/h (within limit)' },
    { id: 'log-004', timestamp: '2025-10-15T10:35:22Z', level: 'INFO', component: 'TrafficManager', message: 'Traffic scenario activated', category: 'SCENARIO', details: 'Medium traffic density applied' },
    { id: 'log-005', timestamp: '2025-10-15T10:42:33Z', level: 'WARN', component: 'SafetyMonitor', message: 'Following distance below optimal', category: 'SAFETY', details: 'Vehicle V003: 2.8s (recommended: ≥3.0s)' },
    { id: 'log-006', timestamp: '2025-10-15T10:45:22Z', level: 'INFO', component: 'PerformanceAnalyzer', message: 'Average speed consistency check - PASSED', category: 'TEST', details: 'Maintained 85.2 km/h average' },
    { id: 'log-007', timestamp: '2025-10-15T11:20:18Z', level: 'INFO', component: 'FuelMonitor', message: 'Fuel efficiency target achieved', category: 'PERFORMANCE', details: '7.8 L/100km (target: ≤8.0)' },
    { id: 'log-008', timestamp: '2025-10-15T11:35:45Z', level: 'INFO', component: 'LaneKeeping', message: 'Lane keeping accuracy verified', category: 'SAFETY', details: '96.8% accuracy maintained' },
    { id: 'log-009', timestamp: '2025-10-15T11:45:00Z', level: 'INFO', component: 'SimulationEngine', message: 'Simulation completed successfully', category: 'SYSTEM', details: 'Total duration: 75 minutes, 4/5 tests passed' }
  ],
  'sim-002': [
    { id: 'log-010', timestamp: '2025-10-14T14:20:00Z', level: 'INFO', component: 'SimulationEngine', message: 'Emergency Braking Test started', category: 'SYSTEM', details: 'Urban environment with 3 test vehicles' },
    { id: 'log-011', timestamp: '2025-10-14T14:22:10Z', level: 'INFO', component: 'SensorArray', message: 'All sensors calibrated and active', category: 'SYSTEM', details: 'Radar, LiDAR, Camera systems online' },
    { id: 'log-012', timestamp: '2025-10-14T14:25:33Z', level: 'INFO', component: 'EmergencyBrake', message: 'Emergency scenario triggered', category: 'TEST', details: 'Pedestrian crossing detected at 60 km/h' },
    { id: 'log-013', timestamp: '2025-10-14T14:25:34Z', level: 'DEBUG', component: 'BrakeSystem', message: 'Brake response time: 0.8s - PASSED', category: 'TEST', details: 'Within acceptable limit of 1.0s' },
    { id: 'log-014', timestamp: '2025-10-14T14:28:15Z', level: 'INFO', component: 'DistanceCalculator', message: 'Stopping distance measured', category: 'TEST', details: '45.5m stopping distance (limit: 50m)' },
    { id: 'log-015', timestamp: '2025-10-14T14:35:45Z', level: 'INFO', component: 'CollisionDetector', message: 'Collision successfully avoided', category: 'SAFETY', details: 'All 12 emergency scenarios handled' },
    { id: 'log-016', timestamp: '2025-10-14T14:45:22Z', level: 'INFO', component: 'TestController', message: 'Collision avoidance rate: 100%', category: 'TEST', details: '12/12 scenarios passed' },
    { id: 'log-017', timestamp: '2025-10-14T15:02:18Z', level: 'DEBUG', component: 'RadarSystem', message: 'Radar detection accuracy verified', category: 'TEST', details: '99.2% object detection accuracy' },
    { id: 'log-018', timestamp: '2025-10-14T15:08:45Z', level: 'INFO', component: 'ABSSystem', message: 'ABS performance optimal', category: 'SYSTEM', details: 'No wheel lock incidents detected' },
    { id: 'log-019', timestamp: '2025-10-14T15:10:00Z', level: 'INFO', component: 'SimulationEngine', message: 'Emergency Braking Test completed', category: 'SYSTEM', details: 'All tests passed successfully' }
  ]
}

// Performance metrics with detailed breakdowns
export const mockPerformanceMetrics = {
  'sim-001': [
    { metric: 'Average Speed', value: 85.2, unit: 'km/h', target: '80-90', status: 'PASSED', trend: '+2.1%' },
    { metric: 'Maximum Speed', value: 118.5, unit: 'km/h', target: '≤120', status: 'PASSED', trend: '-1.3%' },
    { metric: 'Fuel Efficiency', value: 7.8, unit: 'L/100km', target: '≤8.0', status: 'PASSED', trend: '-5.4%' },
    { metric: 'Lane Keeping', value: 96.8, unit: '%', target: '≥90', status: 'PASSED', trend: '+1.8%' },
    { metric: 'Following Distance', value: 2.8, unit: 's', target: '≥3.0', status: 'WARNING', trend: '-6.7%' },
    { metric: 'Safety Score', value: 92, unit: 'points', target: '≥85', status: 'PASSED', trend: '+3.4%' }
  ],
  'sim-002': [
    { metric: 'Brake Response Time', value: 0.8, unit: 's', target: '≤1.0', status: 'PASSED', trend: '-12.5%' },
    { metric: 'Stopping Distance', value: 45.5, unit: 'm', target: '≤50', status: 'PASSED', trend: '-8.1%' },
    { metric: 'Collision Avoidance', value: 100, unit: '%', target: '≥95', status: 'PASSED', trend: '+0.0%' },
    { metric: 'Radar Accuracy', value: 99.2, unit: '%', target: '≥98', status: 'PASSED', trend: '+1.2%' },
    { metric: 'Emergency Stops', value: 12, unit: 'count', target: '12', status: 'PASSED', trend: '+0.0%' },
    { metric: 'System Response', value: 98.5, unit: '%', target: '≥95', status: 'PASSED', trend: '+2.1%' }
  ]
}

export const mockResultsData = {
  data: {
    simulationReadByQuery: {
      content: [
        {
          id: 'sim-001',
          name: 'Highway Speed Test',
          status: 'completed',
          platform: 'CARLA',
          environment: 'Highway',
          scenarioType: 'Performance',
          vehicles: 5,
          scenarios: 3,
          startDate: '2025-10-15T10:30:00Z',
          endDate: '2025-10-15T11:45:00Z',
          testsPassed: 4,
          testsTotal: 5,
          successRate: 80,
          results: {
            averageSpeed: 85.2,
            maxSpeed: 118.5,
            fuelEfficiency: 7.8,
            safetyScore: 92,
            totalDistance: 145.8,
            testDuration: 75,
            passedChecks: 4,
            totalChecks: 5,
            warningCount: 1,
            errorCount: 0
          }
        },
        {
          id: 'sim-002',
          name: 'Emergency Braking Test',
          status: 'completed',
          platform: 'SUMO',
          environment: 'Urban',
          scenarioType: 'Safety',
          vehicles: 3,
          scenarios: 5,
          startDate: '2025-10-14T14:20:00Z',
          endDate: '2025-10-14T15:10:00Z',
          testsPassed: 5,
          testsTotal: 5,
          successRate: 100,
          results: {
            brakingDistance: 45.5,
            reactionTime: 0.8,
            collisionAvoidance: true,
            safetyScore: 98,
            emergencyStops: 12,
            successfulAvoidance: 100,
            passedChecks: 5,
            totalChecks: 5,
            warningCount: 0,
            errorCount: 0
          }
        },
        {
          id: 'sim-003',
          name: 'Weather Adaptation Test',
          status: 'running',
          platform: 'AirSim',
          environment: 'Mixed',
          scenarioType: 'Environmental',
          vehicles: 8,
          scenarios: 7,
          startDate: '2025-10-19T09:00:00Z',
          endDate: null,
          results: null
        },
        {
          id: 'sim-004',
          name: 'Traffic Jam Simulation',
          status: 'completed',
          platform: 'CARLA',
          environment: 'Urban',
          scenarioType: 'Traffic',
          vehicles: 25,
          scenarios: 4,
          startDate: '2025-10-18T16:30:00Z',
          endDate: '2025-10-18T18:15:00Z',
          results: {
            averageWaitTime: 125.5,
            throughput: 450,
            congestionLevel: 0.75,
            safetyScore: 88,
            totalVehicleHours: 42.5,
            averageSpeed: 15.2
          }
        },
        {
          id: 'sim-005',
          name: 'Autonomous Parking Test',
          status: 'failed',
          platform: 'SUMO',
          environment: 'Parking Lot',
          scenarioType: 'Autonomous',
          vehicles: 2,
          scenarios: 6,
          startDate: '2025-10-17T11:45:00Z',
          endDate: '2025-10-17T12:30:00Z',
          results: {
            successRate: 0.65,
            averageParkingTime: 180.2,
            collisions: 2,
            safetyScore: 45,
            parkingAttempts: 15,
            parallelParkingSuccess: 8
          }
        },
        {
          id: 'sim-006',
          name: 'Lane Change Validation',
          status: 'completed',
          platform: 'AirSim',
          environment: 'Highway',
          scenarioType: 'Behavioral',
          vehicles: 12,
          scenarios: 8,
          startDate: '2025-10-16T13:15:00Z',
          endDate: '2025-10-16T14:45:00Z',
          results: {
            successfulLaneChanges: 245,
            averageChangeTime: 4.2,
            smoothnessScore: 94,
            safetyScore: 96,
            totalLaneChanges: 260,
            signalUsage: 98.5
          }
        },
        {
          id: 'sim-007',
          name: 'Night Vision Test',
          status: 'completed',
          platform: 'CARLA',
          environment: 'Urban',
          scenarioType: 'Environmental',
          vehicles: 6,
          scenarios: 4,
          startDate: '2025-10-13T22:00:00Z',
          endDate: '2025-10-13T23:30:00Z',
          results: {
            visibilityScore: 87,
            objectDetectionRate: 0.92,
            safetyScore: 89,
            averageSpeed: 35.8,
            nightIncidents: 1,
            headlightEfficiency: 95
          }
        },
        {
          id: 'sim-008',
          name: 'Intersection Navigation',
          status: 'completed',
          platform: 'SUMO',
          environment: 'Urban',
          scenarioType: 'Traffic',
          vehicles: 18,
          scenarios: 6,
          startDate: '2025-10-12T08:30:00Z',
          endDate: '2025-10-12T10:15:00Z',
          results: {
            successfulTurns: 156,
            averageWaitTime: 45.3,
            trafficLightCompliance: 99.2,
            safetyScore: 94,
            rightOfWayViolations: 2,
            intersectionEfficiency: 88
          }
        },
        {
          id: 'sim-009',
          name: 'Pedestrian Detection',
          status: 'running',
          platform: 'AirSim',
          environment: 'Mixed',
          scenarioType: 'Safety',
          vehicles: 4,
          scenarios: 9,
          startDate: '2025-10-19T14:20:00Z',
          endDate: null,
          results: null
        },
        {
          id: 'sim-010',
          name: 'Highway Merging Test',
          status: 'completed',
          platform: 'CARLA',
          environment: 'Highway',
          scenarioType: 'Behavioral',
          vehicles: 15,
          scenarios: 5,
          startDate: '2025-10-11T16:45:00Z',
          endDate: '2025-10-11T18:00:00Z',
          results: {
            successfulMerges: 142,
            averageMergeTime: 8.7,
            safetyScore: 91,
            mergeGapAccuracy: 87.5,
            aggressiveMerges: 8,
            smoothnessScore: 89
          }
        }
      ],
      pages: 5,
      total: 48,
      page: 1,
      size: 10
    }
  }
}

// Additional mock data for detailed simulation analysis
export const mockDetailedResults = {
  'sim-001': {
    performanceMetrics: {
      topSpeed: 120.0,
      averageAcceleration: 2.3,
      fuelConsumption: 8.2,
      emissionLevels: 45.6
    },
    safetyMetrics: {
      nearMisses: 2,
      hardBraking: 5,
      aggressiveTurns: 3,
      speedingViolations: 1
    },
    environmentalConditions: {
      weather: 'Clear',
      visibility: 'Excellent',
      roadCondition: 'Dry',
      trafficDensity: 'Medium'
    }
  },
  'sim-002': {
    emergencyMetrics: {
      minimumBrakingDistance: 42.1,
      maximumBrakingDistance: 48.9,
      averageReactionTime: 0.8,
      emergencySuccess: 100
    },
    vehicleMetrics: {
      tireWear: 'Minimal',
      brakeTemperature: 'Normal',
      systemResponse: 'Optimal'
    }
  }
  // Add more detailed results as needed
}

// Note: MOCK_CONFIG is defined at the top of this file
