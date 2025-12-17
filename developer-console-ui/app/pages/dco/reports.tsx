import React, { useEffect, useState } from 'react'
import { Box, Table } from '@dco/sdv-ui'
import Dco from '.'
import {
  getEvaluationResult,
  type EvaluationResult,
  type EvaluationMetricResult,
  getVerdictColor,
  getScoreColor,
} from '../../services/functionEvaluation.service'

const ReportsPage = () => {
  const [selectedSimulationId, setSelectedSimulationId] = useState<string>('')
  const [evaluationResult, setEvaluationResult] = useState<EvaluationResult | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleLoadReport = async () => {
    if (!selectedSimulationId.trim()) {
      setError('Please enter a simulation ID')
      return
    }

    try {
      setLoading(true)
      setError(null)
      const result = await getEvaluationResult(selectedSimulationId)
      setEvaluationResult(result)
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to load evaluation report')
      setEvaluationResult(null)
    } finally {
      setLoading(false)
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString()
  }

  const metricColumns = [
    {
      Header: 'Rule Name',
      accessor: 'ruleName',
      width: 200,
    },
    {
      Header: 'Metric',
      accessor: 'metricName',
      formatter: (value: string) => (
        <code style={{ fontSize: '13px', fontFamily: 'monospace', color: '#00bcd4' }}>
          {value}
        </code>
      )
    },
    {
      Header: 'Expected',
      accessor: 'expectedValue',
      width: 120,
      formatter: (value: number, cell: any) => {
        const rule = cell.row.original.rule
        return `${rule.operator} ${value}`
      }
    },
    {
      Header: 'Actual',
      accessor: 'actualValue',
      width: 100,
      formatter: (value: number | null) => {
        if (value === null) return <span style={{ color: '#999' }}>N/A</span>
        return value.toFixed(2)
      }
    },
    {
      Header: 'Weight',
      accessor: 'rule',
      width: 80,
      formatter: (rule: any) => rule?.weight || 'N/A'
    },
    {
      Header: 'Result',
      accessor: 'passed',
      width: 100,
      formatter: (value: boolean) => (
        <span style={{
          padding: '4px 12px',
          borderRadius: '4px',
          backgroundColor: value ? '#4CAF50' : '#F44336',
          color: 'white',
          fontSize: '12px',
          fontWeight: '500',
        }}>
          {value ? '✓ PASS' : '✗ FAIL'}
        </span>
      )
    },
  ]

  return (
    <Dco>
      <Box fullHeight padding="large" scrollY>
        <div style={{ maxWidth: '1400px', margin: '0 auto' }}>
          <div style={{ marginBottom: '24px' }}>
            <h1 style={{ margin: '0 0 8px 0', fontSize: '24px', fontWeight: 'bold', color: '#fff' }}>
              Evaluation Reports
            </h1>
            <p style={{ margin: 0, color: '#aaa' }}>
              View detailed evaluation results for simulations
            </p>
          </div>

          {/* Search Section */}
          <div style={{
            backgroundColor: '#2a2a2a',
            border: '1px solid #444',
            borderRadius: '8px',
            padding: '20px',
            marginBottom: '24px',
          }}>
            <h3 style={{ margin: '0 0 16px 0', fontSize: '16px', color: '#fff' }}>
              Load Evaluation Report
            </h3>
            <div style={{ display: 'flex', gap: '12px', alignItems: 'flex-start' }}>
              <div style={{ flex: 1 }}>
                <input
                  type="text"
                  value={selectedSimulationId}
                  onChange={(e) => setSelectedSimulationId(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleLoadReport()}
                  placeholder="Enter Simulation ID (e.g., sim-1234567890)"
                  style={{
                    width: '100%',
                    padding: '10px 14px',
                    border: '1px solid #555',
                    borderRadius: '4px',
                    fontSize: '14px',
                    backgroundColor: '#1a1a1a',
                    color: '#fff',
                    fontFamily: 'monospace',
                  }}
                />
              </div>
              <button
                onClick={handleLoadReport}
                disabled={loading}
                style={{
                  padding: '10px 24px',
                  fontSize: '14px',
                  border: 'none',
                  backgroundColor: loading ? '#555' : '#1976d2',
                  color: 'white',
                  borderRadius: '4px',
                  cursor: loading ? 'not-allowed' : 'pointer',
                  fontWeight: '500',
                  minWidth: '100px',
                }}
              >
                {loading ? 'Loading...' : 'Load Report'}
              </button>
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div style={{
              backgroundColor: '#ffebee',
              color: '#c62828',
              padding: '12px 16px',
              borderRadius: '4px',
              marginBottom: '20px',
            }}>
              {error}
            </div>
          )}

          {/* Evaluation Results */}
          {evaluationResult && (
            <>
              {/* Summary Card */}
              <div style={{
                backgroundColor: '#2a2a2a',
                border: '1px solid #444',
                borderRadius: '8px',
                padding: '24px',
                marginBottom: '24px',
              }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '20px' }}>
                  <h3 style={{ margin: 0, fontSize: '18px', color: '#fff' }}>
                    Evaluation Summary
                  </h3>
                  <span style={{
                    padding: '8px 20px',
                    borderRadius: '6px',
                    backgroundColor: getVerdictColor(evaluationResult.verdict),
                    color: 'white',
                    fontSize: '16px',
                    fontWeight: 'bold',
                  }}>
                    {evaluationResult.verdict}
                  </span>
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px' }}>
                  <div>
                    <div style={{ fontSize: '12px', color: '#999', marginBottom: '4px' }}>Simulation ID</div>
                    <div style={{ fontSize: '14px', color: '#fff', fontFamily: 'monospace' }}>
                      {evaluationResult.simulationId}
                    </div>
                  </div>
                  
                  <div>
                    <div style={{ fontSize: '12px', color: '#999', marginBottom: '4px' }}>Overall Score</div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                      <div style={{
                        fontSize: '24px',
                        fontWeight: 'bold',
                        color: getScoreColor(evaluationResult.overallScore)
                      }}>
                        {evaluationResult.overallScore.toFixed(1)}
                      </div>
                      <div style={{ fontSize: '14px', color: '#999' }}>/ 100</div>
                    </div>
                    <div style={{
                      marginTop: '4px',
                      height: '6px',
                      backgroundColor: '#1a1a1a',
                      borderRadius: '3px',
                      overflow: 'hidden'
                    }}>
                      <div style={{
                        height: '100%',
                        width: `${evaluationResult.overallScore}%`,
                        backgroundColor: getScoreColor(evaluationResult.overallScore),
                        transition: 'width 0.3s ease'
                      }} />
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#999', marginBottom: '4px' }}>Evaluated At</div>
                    <div style={{ fontSize: '14px', color: '#fff' }}>
                      {formatDate(evaluationResult.evaluatedAt)}
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#999', marginBottom: '4px' }}>Duration</div>
                    <div style={{ fontSize: '14px', color: '#fff' }}>
                      {evaluationResult.evaluationDurationMs}ms
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '12px', color: '#999', marginBottom: '4px' }}>Rules Passed</div>
                    <div style={{ fontSize: '14px', color: '#fff' }}>
                      {evaluationResult.metricResults.filter(m => m.passed).length} / {evaluationResult.metricResults.length}
                    </div>
                  </div>
                </div>
              </div>

              {/* Detailed Results Table */}
              <div style={{
                backgroundColor: '#2a2a2a',
                border: '1px solid #444',
                borderRadius: '8px',
                padding: '24px',
              }}>
                <h3 style={{ margin: '0 0 16px 0', fontSize: '18px', color: '#fff' }}>
                  Rule Evaluation Details
                </h3>
                
                {evaluationResult.metricResults.length > 0 ? (
                  <Table 
                    columns={metricColumns}
                    data={evaluationResult.metricResults}
                  />
                ) : (
                  <p style={{ color: '#999', fontStyle: 'italic', textAlign: 'center', padding: '20px' }}>
                    No metric results available
                  </p>
                )}
              </div>
            </>
          )}

          {/* Empty State */}
          {!evaluationResult && !error && !loading && (
            <div style={{
              textAlign: 'center',
              padding: '60px 20px',
              color: '#aaa',
            }}>
              <div style={{ fontSize: '48px', marginBottom: '16px' }}>📊</div>
              <h3 style={{ margin: '0 0 8px 0', fontSize: '18px', color: '#999' }}>
                No Report Loaded
              </h3>
              <p style={{ margin: 0, fontSize: '14px' }}>
                Enter a simulation ID above to view its evaluation report
              </p>
            </div>
          )}
        </div>
      </Box>
    </Dco>
  )
}

export default ReportsPage
