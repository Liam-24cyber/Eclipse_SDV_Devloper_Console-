import apollo from '../libs/apollo'
import { gql } from '@apollo/client'
import { mockResultsData, mockDetailedResults, MOCK_CONFIG } from './mockData/results.mock'

// GraphQL query for getting simulation results
const SIMULATION_RESULTS_QUERY = gql`
  query simulationReadByQuery($page: Int, $size: Int) {
    simulationReadByQuery(page: $page, size: $size) {
      content {
        id
        name
        status
        platform
        environment
        scenarioType
        vehicles
        scenarios
        startDate
        endDate
        results {
          averageSpeed
          maxSpeed
          fuelEfficiency
          safetyScore
          brakingDistance
          reactionTime
          collisionAvoidance
          averageWaitTime
          throughput
          congestionLevel
          successRate
          averageParkingTime
          collisions
          successfulLaneChanges
          averageChangeTime
          smoothnessScore
        }
      }
      pages
      total
      page
      size
    }
  }
`

// Function to get results data
export const getResultsData = async (page: number = 1, size: number = 10) => {
  try {
    // Check if we should use mock data
    if (MOCK_CONFIG.USE_MOCK_DATA) {
      // Simulate network delay
      if (MOCK_CONFIG.MOCK_DELAY > 0) {
        await new Promise(resolve => setTimeout(resolve, MOCK_CONFIG.MOCK_DELAY))
      }
      
      // Return mock data with pagination simulation
      const startIndex = (page - 1) * size
      const endIndex = startIndex + size
      const paginatedContent = mockResultsData.data.simulationReadByQuery.content.slice(startIndex, endIndex)
      
      return {
        data: {
          simulationReadByQuery: {
            ...mockResultsData.data.simulationReadByQuery,
            content: paginatedContent,
            page,
            size
          }
        }
      }
    }
    
    // Real GraphQL query (uncomment when backend is ready)
    const result = await apollo.query({
      query: SIMULATION_RESULTS_QUERY,
      variables: { page: page - 1, size },
      fetchPolicy: 'network-only'
    })
    return result
    
  } catch (error) {
    console.error('Error fetching results data:', error)
    throw error
  }
}

// Function to transform results data for table display
export const resultsRowData = (data: any) => {
  if (!data?.data?.simulationReadByQuery?.content) {
    return []
  }

  return data.data.simulationReadByQuery.content.map((simulation: any) => ({
    id: simulation.id,
    simulationName: simulation.name,
    status: simulation.status,
    platform: simulation.platform,
    environment: simulation.environment,
    scenarioType: simulation.scenarioType,
    vehicles: simulation.vehicles,
    scenarios: simulation.scenarios,
    startDate: simulation.startDate ? new Date(simulation.startDate).toLocaleDateString() : '-',
    endDate: simulation.endDate ? new Date(simulation.endDate).toLocaleDateString() : '-',
    results: simulation.results
  }))
}

// Function to get specific simulation results details
export const getSimulationResults = async (simulationId: string) => {
  try {
    // Find the simulation in mock data
    const simulation = mockResultsData.data.simulationReadByQuery.content.find(
      sim => sim.id === simulationId
    )
    
    if (!simulation) {
      throw new Error(`Simulation with ID ${simulationId} not found`)
    }
    
    return {
      data: {
        simulation
      }
    }
  } catch (error) {
    console.error('Error fetching simulation results:', error)
    throw error
  }
}

// Export mock data for testing
export { mockResultsData }
