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

  // Use real GraphQL queries instead of mock data
  const { data: resultsData, loading: resultsLoading, error: resultsError } = useQuery(GET_SIMULATION_RESULTS, {
    variables: { simulationId: id },
    skip: !id,
    pollInterval: 5000 // Poll every 5 seconds for updates
  })

  const { data: logsData, loading: logsLoading, error: logsError } = useQuery(GET_SIMULATION_LOGS, {
    variables: { simulationId: id, page: 0, size: 50 },
    skip: !id || activeTab !== 'logs',
    pollInterval: activeTab === 'logs' ? 5000 : 0
  })

  const { data: metricsData, loading: metricsLoading, error: metricsError } = useQuery(GET_SIMULATION_METRICS, {
    variables: { simulationId: id },
    skip: !id || activeTab !== 'metrics',
    pollInterval: activeTab === 'metrics' ? 5000 : 0
  })

  const { data: simulationData, loading: simulationLoading, error: simulationError } = useQuery(GET_SIMULATIONS, {
    variables: {
      search: null,
      query: `id=="${id}"`,
      page: 0,
      size: 1,
      sort: ["startDate:desc"]
    },
    skip: !id,
    pollInterval: 5000
  })

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
