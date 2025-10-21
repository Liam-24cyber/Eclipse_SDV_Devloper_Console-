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

  // Mock data instead of GraphQL queries
  const [resultsData, setResultsData] = useState<any>(null)
  const [logsData, setLogsData] = useState<any>(null)
  const [metricsData, setMetricsData] = useState<any>(null)
  const [simulationData, setSimulationData] = useState<any>(null)
  const [resultsLoading, setResultsLoading] = useState(true)
  const [logsLoading, setLogsLoading] = useState(false)
  const [metricsLoading, setMetricsLoading] = useState(false)
  const [simulationLoading, setSimulationLoading] = useState(true)
  const resultsError = null
  const logsError = null
  const metricsError = null
  const simulationError = null

  useEffect(() => {
    if (!id) return

    // Mock simulation results data
    setTimeout(() => {
      setResultsData({
        getSimulationResults: {
          id: `result-${id}`,
          simulationId: id,
          status: 'COMPLETED',
          summary: 'Simulation completed successfully with all test cases passed.',
          detailedResults: JSON.stringify({
            totalTests: 25,
            passed: 23,
            failed: 2,
            warnings: 5,
            executionTime: 1847
          }),
          artifactPath: '/artifacts/simulation-results.zip',
          createdAt: new Date('2025-10-18T10:30:00').toISOString(),
          completedAt: new Date('2025-10-18T11:00:47').toISOString()
        }
      })
      setResultsLoading(false)
    }, 500)

    // Mock simulation basic info
    setTimeout(() => {
      setSimulationData({
        simulationReadByQuery: {
          content: [{
            id: id,
            name: 'Highway Overtaking Test',
            status: 'COMPLETED',
            platform: 'Task Management',
            environment: 'Development',
            startDate: new Date('2025-10-18T10:30:00').toISOString(),
            noOfVehicle: 3,
            noOfScenarios: 2,
            scenarioType: 'Over-The-Air Service'
          }]
        }
      })
      setSimulationLoading(false)
    }, 500)
  }, [id])

  useEffect(() => {
    if (!id || activeTab !== 'logs') return

    setLogsLoading(true)
    // Mock logs data
    setTimeout(() => {
      setLogsData({
        getSimulationLogs: [
          {
            id: 'log-001',
            simulationId: id,
            logLevel: 'INFO',
            component: 'SimulationEngine',
            message: 'Simulation started successfully',
            timestamp: new Date('2025-10-18T10:30:00').toISOString(),
            additionalData: null
          },
          {
            id: 'log-002',
            simulationId: id,
            logLevel: 'INFO',
            component: 'VehicleController',
            message: 'Vehicle 1 initialized',
            timestamp: new Date('2025-10-18T10:30:05').toISOString(),
            additionalData: '{"vehicleId": "v001", "model": "Tesla Model 3"}'
          },
          {
            id: 'log-003',
            simulationId: id,
            logLevel: 'WARN',
            component: 'TrackManager',
            message: 'Track surface condition: wet',
            timestamp: new Date('2025-10-18T10:35:12').toISOString(),
            additionalData: null
          },
          {
            id: 'log-004',
            simulationId: id,
            logLevel: 'ERROR',
            component: 'ScenarioRunner',
            message: 'Scenario timeout exceeded for test case TC-023',
            timestamp: new Date('2025-10-18T10:45:30').toISOString(),
            additionalData: '{"testCase": "TC-023", "timeout": 300}'
          },
          {
            id: 'log-005',
            simulationId: id,
            logLevel: 'INFO',
            component: 'ResultsCollector',
            message: 'Collecting simulation results',
            timestamp: new Date('2025-10-18T10:59:45').toISOString(),
            additionalData: null
          },
          {
            id: 'log-006',
            simulationId: id,
            logLevel: 'INFO',
            component: 'SimulationEngine',
            message: 'Simulation completed successfully',
            timestamp: new Date('2025-10-18T11:00:47').toISOString(),
            additionalData: null
          }
        ]
      })
      setLogsLoading(false)
    }, 300)
  }, [id, activeTab])

  useEffect(() => {
    if (!id || activeTab !== 'metrics') return

    setMetricsLoading(true)
    // Mock metrics data
    setTimeout(() => {
      setMetricsData({
        getSimulationMetrics: [
          {
            id: 'metric-001',
            simulationId: id,
            metricName: 'Average Speed',
            metricValue: '85.4',
            unit: 'km/h',
            category: 'Performance',
            timestamp: new Date('2025-10-18T11:00:47').toISOString()
          },
          {
            id: 'metric-002',
            simulationId: id,
            metricName: 'Braking Distance',
            metricValue: '45.2',
            unit: 'meters',
            category: 'Safety',
            timestamp: new Date('2025-10-18T11:00:47').toISOString()
          },
          {
            id: 'metric-003',
            simulationId: id,
            metricName: 'Lane Changes',
            metricValue: '12',
            unit: 'count',
            category: 'Behavior',
            timestamp: new Date('2025-10-18T11:00:47').toISOString()
          },
          {
            id: 'metric-004',
            simulationId: id,
            metricName: 'Fuel Consumption',
            metricValue: '7.8',
            unit: 'L/100km',
            category: 'Efficiency',
            timestamp: new Date('2025-10-18T11:00:47').toISOString()
          },
          {
            id: 'metric-005',
            simulationId: id,
            metricName: 'Response Time',
            metricValue: '0.23',
            unit: 'seconds',
            category: 'Performance',
            timestamp: new Date('2025-10-18T11:00:47').toISOString()
          },
          {
            id: 'metric-006',
            simulationId: id,
            metricName: 'Success Rate',
            metricValue: '92',
            unit: '%',
            category: 'Quality',
            timestamp: new Date('2025-10-18T11:00:47').toISOString()
          }
        ]
      })
      setMetricsLoading(false)
    }, 300)
  }, [id, activeTab])
  
  // Original GraphQL queries (commented out for mock mode)
  // const { data: resultsData, loading: resultsLoading, error: resultsError } = useQuery(GET_SIMULATION_RESULTS, {
  //   variables: { simulationId: id },
  //   skip: !id
  // })
  // const { data: logsData, loading: logsLoading, error: logsError } = useQuery(GET_SIMULATION_LOGS, {
  //   variables: { simulationId: id },
  //   skip: !id || activeTab !== 'logs'
  // })
  // const { data: metricsData, loading: metricsLoading, error: metricsError } = useQuery(GET_SIMULATION_METRICS, {
  //   variables: { simulationId: id },
  //   skip: !id || activeTab !== 'metrics'
  // })
  // const { data: simulationData, loading: simulationLoading, error: simulationError } = useQuery(gql(GET_SIMULATIONS), {
  //   variables: { 
  //     query: `id:${id}`,
  //     page: 0,
  //     size: 1
  //   },
  //   skip: !id
  // })

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
  const simulation = simulationData?.simulationReadByQuery?.content?.[0]
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
              ) : logsData?.getSimulationLogs?.length > 0 ? (
                <Table 
                  columns={logColumns}
                  data={logsData.getSimulationLogs}
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
