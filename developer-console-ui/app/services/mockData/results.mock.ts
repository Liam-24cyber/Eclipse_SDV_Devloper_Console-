// Mock data for simulation results
// This file contains all mock data for the Results page
// You can easily switch between mock and real data by importing from here

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
          results: {
            averageSpeed: 85.2,
            maxSpeed: 120.0,
            fuelEfficiency: 7.8,
            safetyScore: 92,
            totalDistance: 145.8,
            testDuration: 75
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
          results: {
            brakingDistance: 45.5,
            reactionTime: 0.8,
            collisionAvoidance: true,
            safetyScore: 98,
            emergencyStops: 12,
            successfulAvoidance: 100
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

// Configuration for enabling/disabling mock data
export const MOCK_CONFIG = {
  USE_MOCK_DATA: true, // Set to false to use real API
  MOCK_DELAY: 500,     // Simulate network delay in milliseconds
  ENABLE_PAGINATION: true
}
