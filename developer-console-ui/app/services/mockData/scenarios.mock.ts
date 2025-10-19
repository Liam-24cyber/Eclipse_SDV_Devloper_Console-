// Mock data for scenarios
// This file contains all mock data for the Scenarios page

export const mockScenariosData = {
  data: {
    scenarioReadByQuery: {
      content: [
        {
          id: 'scen-001',
          name: 'Highway Overtaking',
          description: 'Test vehicle behavior during highway overtaking maneuvers',
          type: 'Behavioral',
          status: 'active',
          difficulty: 'Medium',
          duration: 300,
          vehicles: 8,
          environment: 'Highway',
          weather: 'Clear',
          createdDate: '2025-10-10T09:00:00Z',
          lastModified: '2025-10-15T14:30:00Z',
          tags: ['highway', 'overtaking', 'behavioral'],
          file: {
            name: 'highway_overtaking.xml',
            size: 2048,
            uploadDate: '2025-10-10T09:00:00Z'
          }
        },
        {
          id: 'scen-002',
          name: 'Emergency Stop',
          description: 'Sudden obstacle detection and emergency braking scenario',
          type: 'Safety',
          status: 'active',
          difficulty: 'High',
          duration: 120,
          vehicles: 3,
          environment: 'Urban',
          weather: 'Rain',
          createdDate: '2025-10-08T11:20:00Z',
          lastModified: '2025-10-14T16:45:00Z',
          tags: ['emergency', 'braking', 'safety'],
          file: {
            name: 'emergency_stop.xml',
            size: 1536,
            uploadDate: '2025-10-08T11:20:00Z'
          }
        },
        {
          id: 'scen-003',
          name: 'Intersection Navigation',
          description: 'Complex intersection with traffic lights and pedestrians',
          type: 'Traffic',
          status: 'active',
          difficulty: 'High',
          duration: 240,
          vehicles: 12,
          environment: 'Urban',
          weather: 'Clear',
          createdDate: '2025-10-12T13:15:00Z',
          lastModified: '2025-10-16T10:20:00Z',
          tags: ['intersection', 'traffic-lights', 'pedestrians'],
          file: {
            name: 'intersection_nav.xml',
            size: 3072,
            uploadDate: '2025-10-12T13:15:00Z'
          }
        },
        {
          id: 'scen-004',
          name: 'Parking Lot Challenge',
          description: 'Autonomous parking in crowded parking lot with obstacles',
          type: 'Autonomous',
          status: 'draft',
          difficulty: 'Medium',
          duration: 180,
          vehicles: 5,
          environment: 'Parking Lot',
          weather: 'Clear',
          createdDate: '2025-10-14T08:30:00Z',
          lastModified: '2025-10-17T12:15:00Z',
          tags: ['parking', 'autonomous', 'obstacles'],
          file: {
            name: 'parking_challenge.xml',
            size: 1789,
            uploadDate: '2025-10-14T08:30:00Z'
          }
        },
        {
          id: 'scen-005',
          name: 'Night Driving',
          description: 'Low visibility driving with headlight effectiveness testing',
          type: 'Environmental',
          status: 'active',
          difficulty: 'Medium',
          duration: 360,
          vehicles: 6,
          environment: 'Mixed',
          weather: 'Clear',
          createdDate: '2025-10-09T19:45:00Z',
          lastModified: '2025-10-13T21:30:00Z',
          tags: ['night', 'visibility', 'headlights'],
          file: {
            name: 'night_driving.xml',
            size: 2234,
            uploadDate: '2025-10-09T19:45:00Z'
          }
        },
        {
          id: 'scen-006',
          name: 'Weather Adaptation',
          description: 'Vehicle behavior in varying weather conditions',
          type: 'Environmental',
          status: 'active',
          difficulty: 'High',
          duration: 450,
          vehicles: 10,
          environment: 'Mixed',
          weather: 'Variable',
          createdDate: '2025-10-11T14:00:00Z',
          lastModified: '2025-10-18T09:20:00Z',
          tags: ['weather', 'adaptation', 'rain', 'snow'],
          file: {
            name: 'weather_adaptation.xml',
            size: 3456,
            uploadDate: '2025-10-11T14:00:00Z'
          }
        },
        {
          id: 'scen-007',
          name: 'School Zone Safety',
          description: 'Reduced speed zone with children and crossing guards',
          type: 'Safety',
          status: 'active',
          difficulty: 'Medium',
          duration: 200,
          vehicles: 4,
          environment: 'Urban',
          weather: 'Clear',
          createdDate: '2025-10-07T07:30:00Z',
          lastModified: '2025-10-12T15:45:00Z',
          tags: ['school-zone', 'children', 'safety'],
          file: {
            name: 'school_zone.xml',
            size: 1987,
            uploadDate: '2025-10-07T07:30:00Z'
          }
        },
        {
          id: 'scen-008',
          name: 'Highway Merging',
          description: 'On-ramp merging with heavy traffic conditions',
          type: 'Traffic',
          status: 'inactive',
          difficulty: 'High',
          duration: 180,
          vehicles: 15,
          environment: 'Highway',
          weather: 'Clear',
          createdDate: '2025-10-05T16:20:00Z',
          lastModified: '2025-10-11T11:10:00Z',
          tags: ['merging', 'highway', 'traffic'],
          file: {
            name: 'highway_merging.xml',
            size: 2567,
            uploadDate: '2025-10-05T16:20:00Z'
          }
        }
      ],
      pages: 3,
      total: 23,
      page: 1,
      size: 10
    }
  }
}

// Configuration for scenarios mock data
export const SCENARIOS_MOCK_CONFIG = {
  USE_MOCK_DATA: true,
  MOCK_DELAY: 400,
  ENABLE_PAGINATION: true
}
