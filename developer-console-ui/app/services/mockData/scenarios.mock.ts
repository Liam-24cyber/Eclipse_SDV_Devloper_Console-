// Mock data for scenarios
// This file contains all comprehensive mock data for SDV (Software Defined Vehicle) scenarios

export const mockScenariosData = {
  data: {
    scenarioReadByQuery: {
      content: [
        {
          id: 'scen-001',
          name: 'Highway Overtaking Maneuver',
          description: 'Advanced highway overtaking scenario with multiple vehicles, lane changes, and speed variations',
          type: 'Behavioral',
          status: 'active',
          difficulty: 'Medium',
          duration: 300,
          vehicles: 8,
          environment: 'Highway',
          weather: 'Clear',
          createdDate: '2025-10-10T09:00:00Z',
          lastModified: '2025-10-15T14:30:00Z',
          tags: ['highway', 'overtaking', 'behavioral', 'lane-change', 'multi-vehicle'],
          file: {
            name: 'highway_overtaking_advanced.odx',
            size: 3072,
            uploadDate: '2025-10-10T09:00:00Z'
          }
        },
        {
          id: 'scen-002',
          name: 'Emergency Collision Avoidance',
          description: 'Critical safety scenario testing autonomous emergency braking and collision avoidance systems',
          type: 'Safety',
          status: 'active',
          difficulty: 'High',
          duration: 120,
          vehicles: 4,
          environment: 'Urban',
          weather: 'Rain',
          createdDate: '2025-10-08T11:20:00Z',
          lastModified: '2025-10-14T16:45:00Z',
          tags: ['emergency', 'collision-avoidance', 'AEB', 'safety-critical', 'wet-conditions'],
          file: {
            name: 'emergency_collision_avoidance.odx',
            size: 2048,
            uploadDate: '2025-10-08T11:20:00Z'
          }
        },
        {
          id: 'scen-003',
          name: 'Smart Intersection Navigation',
          description: 'Complex intersection scenario with traffic lights, pedestrians, and cross-traffic coordination',
          type: 'Navigation',
          status: 'active',
          difficulty: 'High',
          duration: 180,
          vehicles: 12,
          environment: 'Urban',
          weather: 'Clear',
          createdDate: '2025-10-12T14:15:00Z',
          lastModified: '2025-10-16T10:20:00Z',
          tags: ['intersection', 'traffic-lights', 'pedestrians', 'V2I', 'coordination'],
          file: {
            name: 'smart_intersection_nav.odx',
            size: 4096,
            uploadDate: '2025-10-12T14:15:00Z'
          }
        },
        {
          id: 'scen-004',
          name: 'Parking Lot Automation',
          description: 'Automated parking scenario with obstacle detection, space recognition, and precise maneuvering',
          type: 'Automation',
          status: 'active',
          difficulty: 'Medium',
          duration: 240,
          vehicles: 6,
          environment: 'Parking',
          weather: 'Clear',
          createdDate: '2025-10-09T16:30:00Z',
          lastModified: '2025-10-13T12:15:00Z',
          tags: ['parking', 'automation', 'obstacle-detection', 'precision-maneuvering'],
          file: {
            name: 'automated_parking_scenario.odx',
            size: 2560,
            uploadDate: '2025-10-09T16:30:00Z'
          }
        },
        {
          id: 'scen-005',
          name: 'Adverse Weather Driving',
          description: 'Testing vehicle behavior in heavy snow, reduced visibility, and slippery road conditions',
          type: 'Environmental',
          status: 'active',
          difficulty: 'High',
          duration: 450,
          vehicles: 5,
          environment: 'Mixed',
          weather: 'Snow',
          createdDate: '2025-10-05T08:45:00Z',
          lastModified: '2025-10-11T15:30:00Z',
          tags: ['weather', 'snow', 'visibility', 'traction', 'environmental'],
          file: {
            name: 'adverse_weather_snow.odx',
            size: 3584,
            uploadDate: '2025-10-05T08:45:00Z'
          }
        },
        {
          id: 'scen-006',
          name: 'Construction Zone Navigation',
          description: 'Dynamic construction zone with lane closures, temporary signals, and worker safety protocols',
          type: 'Behavioral',
          status: 'active',
          difficulty: 'High',
          duration: 360,
          vehicles: 10,
          environment: 'Highway',
          weather: 'Clear',
          createdDate: '2025-10-07T13:20:00Z',
          lastModified: '2025-10-14T09:45:00Z',
          tags: ['construction', 'lane-closure', 'dynamic-signs', 'worker-safety'],
          file: {
            name: 'construction_zone_nav.odx',
            size: 4608,
            uploadDate: '2025-10-07T13:20:00Z'
          }
        },
        {
          id: 'scen-007',
          name: 'School Zone Safety Protocol',
          description: 'School zone scenario with children, reduced speed limits, and crossing guard interactions',
          type: 'Safety',
          status: 'active',
          difficulty: 'Medium',
          duration: 200,
          vehicles: 7,
          environment: 'Urban',
          weather: 'Clear',
          createdDate: '2025-10-11T07:30:00Z',
          lastModified: '2025-10-15T11:20:00Z',
          tags: ['school-zone', 'children', 'speed-limit', 'crossing-guard', 'safety'],
          file: {
            name: 'school_zone_safety.odx',
            size: 2304,
            uploadDate: '2025-10-11T07:30:00Z'
          }
        },
        {
          id: 'scen-008',
          name: 'Highway Merge Coordination',
          description: 'Complex highway merge scenario with high-speed traffic and cooperative driving behaviors',
          type: 'Behavioral',
          status: 'active',
          difficulty: 'Medium',
          duration: 280,
          vehicles: 15,
          environment: 'Highway',
          weather: 'Cloudy',
          createdDate: '2025-10-06T10:15:00Z',
          lastModified: '2025-10-12T14:50:00Z',
          tags: ['highway', 'merge', 'high-speed', 'cooperation', 'traffic-flow'],
          file: {
            name: 'highway_merge_coordination.odx',
            size: 3840,
            uploadDate: '2025-10-06T10:15:00Z'
          }
        },
        {
          id: 'scen-009',
          name: 'Night Driving with Wildlife',
          description: 'Night-time driving scenario with wildlife detection, headlight optimization, and reaction testing',
          type: 'Environmental',
          status: 'active',
          difficulty: 'High',
          duration: 420,
          vehicles: 3,
          environment: 'Rural',
          weather: 'Clear',
          createdDate: '2025-10-04T19:45:00Z',
          lastModified: '2025-10-10T21:30:00Z',
          tags: ['night-driving', 'wildlife', 'headlights', 'rural', 'detection'],
          file: {
            name: 'night_wildlife_scenario.odx',
            size: 2816,
            uploadDate: '2025-10-04T19:45:00Z'
          }
        },
        {
          id: 'scen-010',
          name: 'V2X Communication Test',
          description: 'Vehicle-to-Everything communication testing with infrastructure, other vehicles, and pedestrians',
          type: 'Communication',
          status: 'active',
          difficulty: 'High',
          duration: 350,
          vehicles: 20,
          environment: 'Urban',
          weather: 'Clear',
          createdDate: '2025-10-13T12:00:00Z',
          lastModified: '2025-10-16T16:15:00Z',
          tags: ['V2X', 'communication', 'infrastructure', 'connectivity', 'coordination'],
          file: {
            name: 'v2x_communication_test.odx',
            size: 5120,
            uploadDate: '2025-10-13T12:00:00Z'
          }
        },
        {
          id: 'scen-011',
          name: 'Roundabout Multi-Exit Navigation',
          description: 'Complex multi-lane roundabout with various exit strategies and traffic density variations',
          type: 'Navigation',
          status: 'active',
          difficulty: 'Medium',
          duration: 200,
          vehicles: 9,
          environment: 'Urban',
          weather: 'Cloudy',
          createdDate: '2025-10-08T11:45:00Z',
          lastModified: '2025-10-14T13:20:00Z',
          tags: ['roundabout', 'multi-lane', 'navigation', 'traffic-flow'],
          file: {
            name: 'roundabout_multi_exit.odx',
            size: 2944,
            uploadDate: '2025-10-08T11:45:00Z'
          }
        },
        {
          id: 'scen-012',
          name: 'Tunnel Driving Challenge',
          description: 'Tunnel driving with reduced GPS signal, lighting changes, and ventilation effects',
          type: 'Environmental',
          status: 'active',
          difficulty: 'High',
          duration: 320,
          vehicles: 6,
          environment: 'Tunnel',
          weather: 'Clear',
          createdDate: '2025-10-03T15:20:00Z',
          lastModified: '2025-10-09T17:45:00Z',
          tags: ['tunnel', 'GPS-denied', 'lighting', 'environmental'],
          file: {
            name: 'tunnel_driving_challenge.odx',
            size: 3328,
            uploadDate: '2025-10-03T15:20:00Z'
          }
        },
        {
          id: 'scen-013',
          name: 'Pedestrian Crossing Prediction',
          description: 'AI-based pedestrian behavior prediction at various crossing points and traffic scenarios',
          type: 'AI-Safety',
          status: 'active',
          difficulty: 'High',
          duration: 280,
          vehicles: 4,
          environment: 'Urban',
          weather: 'Clear',
          createdDate: '2025-10-12T09:30:00Z',
          lastModified: '2025-10-17T14:10:00Z',
          tags: ['pedestrian', 'AI', 'prediction', 'crossing', 'safety'],
          file: {
            name: 'pedestrian_crossing_ai.odx',
            size: 3712,
            uploadDate: '2025-10-12T09:30:00Z'
          }
        },
        {
          id: 'scen-014',
          name: 'Delivery Vehicle Interaction',
          description: 'Scenarios involving delivery trucks, double-parking, and urban logistics challenges',
          type: 'Commercial',
          status: 'draft',
          difficulty: 'Medium',
          duration: 300,
          vehicles: 8,
          environment: 'Urban',
          weather: 'Clear',
          createdDate: '2025-10-15T10:15:00Z',
          lastModified: '2025-10-18T16:30:00Z',
          tags: ['delivery', 'commercial', 'double-parking', 'logistics', 'urban'],
          file: {
            name: 'delivery_vehicle_interaction.odx',
            size: 2688,
            uploadDate: '2025-10-15T10:15:00Z'
          }
        },
        {
          id: 'scen-015',
          name: 'Adaptive Cruise Control Test',
          description: 'Comprehensive ACC testing with varying traffic patterns, cut-ins, and speed variations',
          type: 'ADAS',
          status: 'active',
          difficulty: 'Medium',
          duration: 400,
          vehicles: 12,
          environment: 'Highway',
          weather: 'Clear',
          createdDate: '2025-10-01T12:45:00Z',
          lastModified: '2025-10-07T18:20:00Z',
          tags: ['ACC', 'ADAS', 'cruise-control', 'cut-in', 'highway'],
          file: {
            name: 'adaptive_cruise_control.odx',
            size: 4352,
            uploadDate: '2025-10-01T12:45:00Z'
          }
        },
        {
          id: 'scen-016',
          name: 'Cybersecurity Stress Test',
          description: 'Vehicle cybersecurity testing with simulated attacks and security protocol validation',
          type: 'Security',
          status: 'restricted',
          difficulty: 'High',
          duration: 180,
          vehicles: 3,
          environment: 'Test Track',
          weather: 'Clear',
          createdDate: '2025-10-16T14:00:00Z',
          lastModified: '2025-10-19T11:45:00Z',
          tags: ['cybersecurity', 'security', 'protocol', 'stress-test'],
          file: {
            name: 'cybersecurity_stress_test.odx',
            size: 2176,
            uploadDate: '2025-10-16T14:00:00Z'
          }
        },
        {
          id: 'scen-017',
          name: 'Fleet Coordination Protocol',
          description: 'Multi-vehicle fleet coordination with shared objectives and communication protocols',
          type: 'Fleet',
          status: 'active',
          difficulty: 'High',
          duration: 500,
          vehicles: 25,
          environment: 'Mixed',
          weather: 'Variable',
          createdDate: '2025-09-28T08:30:00Z',
          lastModified: '2025-10-05T13:15:00Z',
          tags: ['fleet', 'coordination', 'multi-vehicle', 'communication'],
          file: {
            name: 'fleet_coordination_protocol.odx',
            size: 6144,
            uploadDate: '2025-09-28T08:30:00Z'
          }
        },
        {
          id: 'scen-018',
          name: 'Edge Case Anomaly Detection',
          description: 'Rare and unexpected driving scenarios to test AI decision-making under uncertainty',
          type: 'AI-Testing',
          status: 'active',
          difficulty: 'High',
          duration: 350,
          vehicles: 7,
          environment: 'Mixed',
          weather: 'Variable',
          createdDate: '2025-10-02T16:20:00Z',
          lastModified: '2025-10-08T12:40:00Z',
          tags: ['edge-case', 'anomaly', 'AI', 'uncertainty', 'rare-events'],
          file: {
            name: 'edge_case_anomaly_detection.odx',
            size: 4736,
            uploadDate: '2025-10-02T16:20:00Z'
          }
        },
        {
          id: 'scen-019',
          name: 'Smart City Integration',
          description: 'Full smart city integration with traffic management, infrastructure communication, and optimization',
          type: 'Smart-City',
          status: 'beta',
          difficulty: 'High',
          duration: 600,
          vehicles: 30,
          environment: 'Smart City',
          weather: 'Clear',
          createdDate: '2025-09-25T09:00:00Z',
          lastModified: '2025-10-10T15:30:00Z',
          tags: ['smart-city', 'infrastructure', 'traffic-management', 'optimization'],
          file: {
            name: 'smart_city_integration.odx',
            size: 7680,
            uploadDate: '2025-09-25T09:00:00Z'
          }
        },
        {
          id: 'scen-020',
          name: 'Autonomous Racing Circuit',
          description: 'High-performance autonomous driving on racing circuit with optimal lap time objectives',
          type: 'Performance',
          status: 'active',
          difficulty: 'High',
          duration: 420,
          vehicles: 6,
          environment: 'Race Track',
          weather: 'Clear',
          createdDate: '2025-10-14T13:45:00Z',
          lastModified: '2025-10-19T10:20:00Z',
          tags: ['racing', 'performance', 'autonomous', 'lap-time', 'high-speed'],
          file: {
            name: 'autonomous_racing_circuit.odx',
            size: 5248,
            uploadDate: '2025-10-14T13:45:00Z'
          }
        }
      ],
      pages: 2,
      total: 20,
      page: 1,
      size: 10
    }
  }
}

// Configuration for scenarios mock data
export const SCENARIOS_MOCK_CONFIG = {
  USE_MOCK_DATA: true,
  MOCK_DELAY: 500,
  ENABLE_PAGINATION: true,
  ENABLE_FILTERING: true,
  SUPPORTED_FILTERS: {
    type: ['Behavioral', 'Safety', 'Navigation', 'Environmental', 'Communication', 'ADAS', 'AI-Safety', 'Commercial', 'Security', 'Fleet', 'AI-Testing', 'Smart-City', 'Performance'],
    status: ['active', 'draft', 'beta', 'restricted', 'inactive'],
    difficulty: ['Low', 'Medium', 'High'],
    environment: ['Highway', 'Urban', 'Rural', 'Parking', 'Mixed', 'Tunnel', 'Test Track', 'Smart City', 'Race Track'],
    weather: ['Clear', 'Rain', 'Snow', 'Cloudy', 'Variable']
  }
}

// Additional scenario categories for comprehensive testing
export const SCENARIO_CATEGORIES = {
  SAFETY_CRITICAL: [
    'Emergency Collision Avoidance',
    'School Zone Safety Protocol', 
    'Pedestrian Crossing Prediction',
    'Construction Zone Navigation'
  ],
  AUTONOMOUS_FEATURES: [
    'Parking Lot Automation',
    'Adaptive Cruise Control Test',
    'Autonomous Racing Circuit'
  ],
  ENVIRONMENTAL_CHALLENGES: [
    'Adverse Weather Driving',
    'Night Driving with Wildlife',
    'Tunnel Driving Challenge'
  ],
  COMMUNICATION_TECH: [
    'V2X Communication Test',
    'Fleet Coordination Protocol',
    'Smart City Integration'
  ],
  AI_TESTING: [
    'Edge Case Anomaly Detection',
    'Pedestrian Crossing Prediction',
    'Cybersecurity Stress Test'
  ]
}
