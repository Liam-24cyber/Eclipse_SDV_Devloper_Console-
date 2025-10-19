// Mock data for tracks
// This file contains all mock data for the Tracks page

export const mockTracksData = {
  data: {
    trackReadByQuery: {
      content: [
        {
          id: 'track-001',
          name: 'Nürburgring Nordschleife',
          trackType: 'Racing Circuit',
          state: 'active',
          duration: 480,
          description: 'Famous German racing circuit known for its challenging layout and elevation changes',
          environment: 'Mixed',
          surface: 'Asphalt',
          length: 20.832,
          elevationGain: 300,
          turns: 154,
          difficulty: 'Expert',
          weather: 'Variable',
          createdDate: '2025-10-01T10:00:00Z',
          lastModified: '2025-10-15T14:20:00Z',
          vehicles: [
            {
              id: 'veh-001',
              vin: 'BMW123456789',
              model: 'BMW M3',
              brand: 'BMW'
            },
            {
              id: 'veh-002',
              vin: 'AUD987654321',
              model: 'Audi RS6',
              brand: 'Audi'
            }
          ]
        },
        {
          id: 'track-002',
          name: 'Urban Test Circuit',
          trackType: 'City Streets',
          state: 'active',
          duration: 240,
          description: 'Realistic urban environment with traffic lights, pedestrians, and varied road conditions',
          environment: 'Urban',
          surface: 'Mixed',
          length: 8.5,
          elevationGain: 45,
          turns: 28,
          difficulty: 'Intermediate',
          weather: 'Clear',
          createdDate: '2025-10-03T09:30:00Z',
          lastModified: '2025-10-18T11:45:00Z',
          vehicles: [
            {
              id: 'veh-003',
              vin: 'TES123789456',
              model: 'Model S',
              brand: 'Tesla'
            },
            {
              id: 'veh-004',
              vin: 'MER456123789',
              model: 'E-Class',
              brand: 'Mercedes'
            },
            {
              id: 'veh-005',
              vin: 'TOY789456123',
              model: 'Prius',
              brand: 'Toyota'
            }
          ]
        },
        {
          id: 'track-003',
          name: 'Highway Performance Loop',
          trackType: 'Highway',
          state: 'active',
          duration: 360,
          description: 'High-speed highway track for testing acceleration, top speed, and fuel efficiency',
          environment: 'Highway',
          surface: 'Asphalt',
          length: 15.2,
          elevationGain: 120,
          turns: 8,
          difficulty: 'Beginner',
          weather: 'Clear',
          createdDate: '2025-10-05T14:15:00Z',
          lastModified: '2025-10-16T16:30:00Z',
          vehicles: [
            {
              id: 'veh-006',
              vin: 'POR234567890',
              model: '911 Turbo',
              brand: 'Porsche'
            }
          ]
        },
        {
          id: 'track-004',
          name: 'Off-Road Adventure',
          trackType: 'Off-Road',
          state: 'maintenance',
          duration: 180,
          description: 'Challenging off-road terrain with mud, rocks, and steep inclines',
          environment: 'Rural',
          surface: 'Dirt',
          length: 12.8,
          elevationGain: 450,
          turns: 42,
          difficulty: 'Expert',
          weather: 'Rain',
          createdDate: '2025-09-28T08:00:00Z',
          lastModified: '2025-10-10T13:20:00Z',
          vehicles: [
            {
              id: 'veh-007',
              vin: 'JEE345678901',
              model: 'Wrangler',
              brand: 'Jeep'
            },
            {
              id: 'veh-008',
              vin: 'LAN456789012',
              model: 'Defender',
              brand: 'Land Rover'
            }
          ]
        },
        {
          id: 'track-005',
          name: 'Winter Testing Ground',
          trackType: 'Test Circuit',
          state: 'active',
          duration: 300,
          description: 'Specialized track for winter driving conditions with ice and snow',
          environment: 'Arctic',
          surface: 'Ice/Snow',
          length: 6.4,
          elevationGain: 80,
          turns: 16,
          difficulty: 'Expert',
          weather: 'Snow',
          createdDate: '2025-10-02T12:45:00Z',
          lastModified: '2025-10-14T10:15:00Z',
          vehicles: [
            {
              id: 'veh-009',
              vin: 'SUB567890123',
              model: 'Outback',
              brand: 'Subaru'
            },
            {
              id: 'veh-010',
              vin: 'VOL678901234',
              model: 'XC90',
              brand: 'Volvo'
            }
          ]
        },
        {
          id: 'track-006',
          name: 'Parking Skills Test',
          trackType: 'Parking Lot',
          state: 'active',
          duration: 120,
          description: 'Comprehensive parking scenarios including parallel, perpendicular, and angled parking',
          environment: 'Parking Lot',
          surface: 'Asphalt',
          length: 0.8,
          elevationGain: 5,
          turns: 0,
          difficulty: 'Intermediate',
          weather: 'Clear',
          createdDate: '2025-10-07T15:30:00Z',
          lastModified: '2025-10-19T09:20:00Z',
          vehicles: [
            {
              id: 'veh-011',
              vin: 'HYU789012345',
              model: 'Sonata',
              brand: 'Hyundai'
            }
          ]
        },
        {
          id: 'track-007',
          name: 'Emergency Response Circuit',
          trackType: 'Emergency',
          state: 'active',
          duration: 150,
          description: 'Track designed for testing emergency braking, collision avoidance, and safety systems',
          environment: 'Test Facility',
          surface: 'Asphalt',
          length: 3.2,
          elevationGain: 15,
          turns: 12,
          difficulty: 'Intermediate',
          weather: 'Clear',
          createdDate: '2025-10-09T11:00:00Z',
          lastModified: '2025-10-17T14:50:00Z',
          vehicles: [
            {
              id: 'veh-012',
              vin: 'VWG890123456',
              model: 'Golf GTI',
              brand: 'Volkswagen'
            },
            {
              id: 'veh-013',
              vin: 'HON901234567',
              model: 'Civic Type R',
              brand: 'Honda'
            }
          ]
        }
      ],
      pages: 4,
      total: 27,
      page: 1,
      size: 10
    }
  }
}

// Configuration for tracks mock data
export const TRACKS_MOCK_CONFIG = {
  USE_MOCK_DATA: true,
  MOCK_DELAY: 300,
  ENABLE_PAGINATION: true
}
