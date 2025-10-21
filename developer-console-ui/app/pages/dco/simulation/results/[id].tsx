import { Box, Table } from '@dco/sdv-ui'
import { useRouter } from 'next/router'
import { useEffect, useState } from 'react'
import { useQuery, gql } from '@apollo/client'
import { GET_SIMULATION_RESULTS, GET_SIMULATION_LOGS, GET_SIMULATION_METRICS, GET_SIMULATIONS } from '../../../../services/queries'
import Dco from '../..'
import Status from '../../../shared/status'

const SimulationResults = () => {
  const router = useRouter()
  const { id } = router.query
  const [activeTab, setActiveTab] = useState('overview')

  // Use fetch instead of useQuery for now since GET_SIMULATIONS is a string, not gql
  const [simulationData, setSimulationData] = useState<any>(null)
  const [simulationLoading, setSimulationLoading] = useState(true)
  const [simulationError, setSimulationError] = useState<any>(null)

  useEffect(() => {
    if (!id) return

    const fetchSimulationData = async () => {
      try {
        setSimulationLoading(true)
        // For now, just use mock data based on the simulation ID
        setSimulationData({
          data: {
            simulationReadByQuery: {
              content: [{
                id: id,
                name: `Simulation ${id}`,
                status: 'Done',
                environment: 'Development',
                platform: 'Task Management',
                scenarioType: 'Vehicle Management',
                createdBy: 'abc@t-systems.com',
                startDate: new Date().toISOString()
              }]
            }
          }
        })
      } catch (error) {
        setSimulationError(error)
      } finally {
        setSimulationLoading(false)
      }
    }

    fetchSimulationData()
  }, [id])

  // Mock data for results, logs, and metrics until backend endpoints are available
  const resultsData = {
    getSimulationResults: {
      id: id,
      simulationId: id,
      status: 'COMPLETED',
      summary: 'Simulation completed successfully',
      detailedResults: 'All test scenarios passed',
      artifactPath: '/results/artifacts',
      createdAt: new Date().toISOString(),
      completedAt: new Date().toISOString()
    }
  }

  const logsData = {
    getSimulationLogs: {
      content: [
        {
          id: '1',
          simulationId: id,
          logLevel: 'INFO',
          component: 'SimulationEngine',
          message: 'Simulation started successfully',
          timestamp: new Date(Date.now() - 300000).toISOString()
        },
        {
          id: '2',
          simulationId: id,
          logLevel: 'INFO',
          component: 'VehicleManager',
          message: 'Vehicle initialized and ready',
          timestamp: new Date(Date.now() - 240000).toISOString()
        },
        {
          id: '3',
          simulationId: id,
          logLevel: 'WARN',
          component: 'ScenarioEngine',
          message: 'Minor timing adjustment made',
          timestamp: new Date(Date.now() - 180000).toISOString()
        },
        {
          id: '4',
          simulationId: id,
          logLevel: 'INFO',
          component: 'SimulationEngine',
          message: 'Simulation completed successfully',
          timestamp: new Date(Date.now() - 60000).toISOString()
        }
      ],
      totalElements: 4,
      totalPages: 1,
      size: 50,
      page: 0
    }
  }

  const metricsData = {
    getSimulationMetrics: [
      {
        id: '1',
        simulationId: id,
        metricName: 'CPU Usage',
        metricValue: '65.4',
        unit: '%',
        category: 'Performance',
        timestamp: new Date().toISOString()
      },
      {
        id: '2',
        simulationId: id,
        metricName: 'Memory Usage',
        metricValue: '2.1',
        unit: 'GB',
        category: 'Performance',
        timestamp: new Date().toISOString()
      },
      {
        id: '3',
        simulationId: id,
        metricName: 'Execution Time',
        metricValue: '245',
        unit: 'seconds',
        category: 'Duration',
        timestamp: new Date().toISOString()
      },
      {
        id: '4',
        simulationId: id,
        metricName: 'Success Rate',
        metricValue: '98.5',
        unit: '%',
        category: 'Quality',
        timestamp: new Date().toISOString()
      }
    ]
  }

  const resultsLoading = false
  const resultsError = null
  const logsLoading = false
  const logsError = null
  const metricsLoading = false
  const metricsError = null

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString()
  }

  const formatDuration = (seconds: number) => {
    if (!seconds) return 'N/A'
    const hours = Math.floor(seconds / 3600)
    const minutes = Math.floor((seconds % 3600) / 60)
    const secs = seconds % 60
    return `${hours}h ${minutes}m ${secs}s`
  }

  const getLogLevelColor = (level: string) => {
    switch (level) {
      case 'ERROR': case 'FATAL': return '#ff4444'
      case 'WARN': return '#ff8800'
      case 'INFO': return '#0088cc'
      case 'DEBUG': return '#888888'
      default: return '#000000'
    }
  }

  const logColumns = [
    {
      Header: 'Timestamp',
      accessor: 'timestamp',
      formatter: (value: string) => formatDate(value)
    },
    {
      Header: 'Level',
      accessor: 'logLevel',
      formatter: (value: string) => (
        <span style={{ color: getLogLevelColor(value), fontWeight: 'bold' }}>
          {value}
        </span>
      )
    },
    {
      Header: 'Component',
      accessor: 'component'
    },
    {
      Header: 'Message',
      accessor: 'message'
    }
  ]

  const metricColumns = [
    {
      Header: 'Metric Name',
      accessor: 'metricName'
    },
    {
      Header: 'Value',
      accessor: 'metricValue'
    },
    {
      Header: 'Unit',
      accessor: 'unit'
    },
    {
      Header: 'Category',
      accessor: 'category'
    },
    {
      Header: 'Timestamp',
      accessor: 'timestamp',
      formatter: (value: string) => formatDate(value)
    }
  ]

  // Prepare overview data
  const simulation = simulationData?.data?.simulationReadByQuery?.content?.[0]
  const results = resultsData?.getSimulationResults

  const overviewData = simulation ? [
    { label: 'Name', value: simulation.name },
    { label: 'Status', value: simulation.status },
    { label: 'Platform', value: simulation.platform },
    { label: 'Environment', value: simulation.environment },
    { label: 'Start Time', value: simulation.startDate ? formatDate(simulation.startDate) : 'N/A' },
    { label: 'End Time', value: results?.completedAt ? formatDate(results.completedAt) : 'N/A' },
    { label: 'Duration', value: 'N/A' }, // Will be calculated if we have both start and end times
    { label: 'Vehicles', value: simulation.noOfVehicle || 'N/A' },
    { label: 'Scenarios', value: simulation.noOfScenarios || 'N/A' },
    { label: 'Summary', value: results?.summary || simulation.description || 'N/A' }
  ] : []

  const overviewColumns = [
    {
      Header: 'Property',
      accessor: 'label'
    },
    {
      Header: 'Value',
      accessor: 'value',
      formatter: (value: any, cell: any) => {
        if (cell?.row?.values?.label === 'Status') {
          return <Status status={value} type={'SS'}></Status>
        }
        return value
      }
    }
  ]

  const isLoading = resultsLoading || simulationLoading
  const hasError = resultsError || simulationError

  if (isLoading) {
    return (
      <Dco>
        <div style={{ padding: '20px' }}>
          <h2>Simulation Results</h2>
          <p>Loading simulation data...</p>
        </div>
      </Dco>
    )
  }

  if (hasError) {
    return (
      <Dco>
        <div style={{ padding: '20px' }}>
          <h2>Simulation Results</h2>
          <p style={{ color: 'red' }}>
            Error loading simulation data. Please try again.
          </p>
          {resultsError && <p style={{ color: 'red', fontSize: '12px' }}>Error loading results</p>}
        </div>
      </Dco>
    )
  }

  return (
    <Dco>
      <div style={{ padding: '20px' }}>
        <h2>Simulation Results</h2>
        <p style={{ color: '#666', fontSize: '14px', marginBottom: '20px' }}>
          Simulation ID: {id}
        </p>

        {/* Tab Navigation */}
        <div style={{ marginBottom: '20px', borderBottom: '1px solid #ddd' }}>
          <div style={{ display: 'flex', gap: '20px' }}>
            {['overview', 'logs', 'metrics', 'files'].map((tab) => (
              <button
                key={tab}
                onClick={() => setActiveTab(tab)}
                style={{
                  background: 'none',
                  border: 'none',
                  padding: '10px 0',
                  cursor: 'pointer',
                  borderBottom: activeTab === tab ? '2px solid #0088cc' : '2px solid transparent',
                  color: activeTab === tab ? '#0088cc' : '#666',
                  fontWeight: activeTab === tab ? 'bold' : 'normal',
                  textTransform: 'capitalize'
                }}
              >
                {tab}
              </button>
            ))}
          </div>
        </div>

        {/* Tab Content */}
        <div>
          {activeTab === 'overview' && (
            <div>
              <h3>Simulation Overview</h3>
              {overviewData.length > 0 ? (
                <Table 
                  columns={overviewColumns}
                  data={overviewData}
                />
              ) : (
                <p>No simulation data available.</p>
              )}
            </div>
          )}

          {activeTab === 'logs' && (
            <div>
              <h3>Execution Logs</h3>
              {logsLoading ? (
                <p>Loading logs...</p>
              ) : logsError ? (
                <p style={{ color: 'red' }}>Error loading logs</p>
              ) : logsData?.getSimulationLogs?.content?.length > 0 ? (
                <Table 
                  columns={logColumns}
                  data={logsData.getSimulationLogs.content}
                />
              ) : (
                <p style={{ color: '#666', fontStyle: 'italic' }}>
                  No logs available for this simulation.
                </p>
              )}
            </div>
          )}

          {activeTab === 'metrics' && (
            <div>
              <h3>Performance Metrics</h3>
              {metricsLoading ? (
                <p>Loading metrics...</p>
              ) : metricsError ? (
                <p style={{ color: 'red' }}>Error loading metrics</p>
              ) : metricsData?.getSimulationMetrics?.length > 0 ? (
                <Table 
                  columns={metricColumns}
                  data={metricsData.getSimulationMetrics}
                />
              ) : (
                <p style={{ color: '#666', fontStyle: 'italic' }}>
                  No metrics available for this simulation.
                </p>
              )}
            </div>
          )}

          {activeTab === 'files' && (
            <div>
              <h3>Result Files & Artifacts</h3>
              {results?.artifactPath ? (
                <div>
                  <p>Artifact Path: {results.artifactPath}</p>
                  <button 
                    style={{
                      background: '#0088cc',
                      color: 'white',
                      border: 'none',
                      padding: '8px 16px',
                      borderRadius: '4px',
                      cursor: 'pointer'
                    }}
                    onClick={() => {
                      // In a real implementation, this would download or open the file
                      alert('File download functionality would be implemented here')
                    }}
                  >
                    Download Results
                  </button>
                </div>
              ) : (
                <p style={{ color: '#666', fontStyle: 'italic' }}>
                  No result files available for this simulation.
                </p>
              )}
            </div>
          )}
        </div>
      </div>
    </Dco>
  )
}

export default SimulationResults
