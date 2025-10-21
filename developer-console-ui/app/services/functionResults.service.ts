import apollo from '../libs/apollo'
import { gql } from '@apollo/client'
import { 
  mockResultsData, 
  mockDetailedResults, 
  mockTestChecks, 
  mockEventLogs, 
  mockPerformanceMetrics, 
  MOCK_CONFIG 
} from './mockData/results.mock'

// GraphQL query for getting simulation results
// Updated to match backend schema: noOfVehicle, noOfScenarios, executionDuration, resultSummary
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
        hardware
        noOfVehicle
        noOfScenarios
        brands
        description
        createdBy
        startDate
        endDate
        executionDuration
        resultSummary
        errorMessage
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
  console.log('🚀 getResultsData called with page:', page, 'size:', size)
  console.log('📋 MOCK_CONFIG:', MOCK_CONFIG)
  
  try {
    // Check if we should use mock data
    if (MOCK_CONFIG.USE_MOCK_DATA) {
      console.log('🔧 Using mock data for results')
      console.log('📦 mockResultsData available:', !!mockResultsData)
      
      // Simulate network delay
      if (MOCK_CONFIG.MOCK_DELAY > 0) {
        console.log('⏳ Simulating network delay:', MOCK_CONFIG.MOCK_DELAY, 'ms')
        await new Promise(resolve => setTimeout(resolve, MOCK_CONFIG.MOCK_DELAY))
      }
      
      // Return mock data with pagination simulation
      const startIndex = (page - 1) * size
      const endIndex = startIndex + size
      const paginatedContent = mockResultsData.data.simulationReadByQuery.content.slice(startIndex, endIndex)
      
      const result = {
        data: {
          simulationReadByQuery: {
            ...mockResultsData.data.simulationReadByQuery,
            content: paginatedContent,
            page,
            size
          }
        }
      }
      
      console.log('📊 Mock results data length:', paginatedContent.length)
      console.log('📈 Total results:', mockResultsData.data.simulationReadByQuery.total)
      return result
    }
    
    // Real GraphQL query (when backend is ready)
    console.log('🌐 Using real API for results')
    const result = await apollo.query({
      query: SIMULATION_RESULTS_QUERY,
      variables: { page: page - 1, size },
      fetchPolicy: 'network-only'
    })
    return result
    
  } catch (error) {
    console.error('❌ Error fetching results data:', error)
    
    // Fallback to mock data on error
    console.log('🔄 Falling back to mock data due to error')
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
}

// Function to transform results data for table display
// Updated to use correct backend field names: noOfVehicle, noOfScenarios
export const resultsRowData = (data: any) => {
  if (!data?.data?.simulationReadByQuery?.content) {
    return []
  }

  return data.data.simulationReadByQuery.content.map((simulation: any) => ({
    id: simulation.id,
    name: simulation.name, // Fixed: was simulationName
    simulationName: simulation.name,
    status: simulation.status,
    platform: simulation.platform,
    environment: simulation.environment,
    scenarioType: simulation.scenarioType,
    vehicles: simulation.noOfVehicle || 0, // Fixed: backend uses noOfVehicle
    scenarios: simulation.noOfScenarios || 0, // Fixed: backend uses noOfScenarios
    hardware: simulation.hardware,
    brands: simulation.brands || [],
    description: simulation.description,
    createdBy: simulation.createdBy,
    startDate: simulation.startDate ? new Date(simulation.startDate).toLocaleDateString() : '-',
    endDate: simulation.endDate ? new Date(simulation.endDate).toLocaleDateString() : '-',
    executionDuration: simulation.executionDuration || 0,
    resultSummary: simulation.resultSummary,
    errorMessage: simulation.errorMessage,
    // Keep results for backward compatibility (will be undefined from real API)
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
