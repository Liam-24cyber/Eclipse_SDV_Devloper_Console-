import axios from 'axios'

// Get the evaluation service URL
const getEvaluationServiceUrl = () => {
  const isBrowser = typeof window !== 'undefined'
  if (isBrowser) {
    // Browser: use current hostname with evaluation service port
    return `${window.location.protocol}//${window.location.hostname}:8085`
  }
  // Server-side: use docker service name
  return 'http://evaluation-service:8085'
}

const EVALUATION_API_BASE = `${getEvaluationServiceUrl()}/api/v1`

// Types
export interface EvaluationRule {
  id: number
  ruleName: string
  metricName: string
  operator: string
  thresholdValue: number
  weight: number
  isActive: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateRuleRequest {
  ruleName: string
  metricName: string
  operator: string
  threshold: number
  weight: number
}

export interface UpdateRuleRequest {
  ruleName?: string
  operator?: string
  threshold?: number
  weight?: number
  isActive?: boolean
}

export interface EvaluationMetricResult {
  id: number
  ruleName: string
  metricName: string
  actualValue: number | null
  expectedValue: number
  passed: boolean
  createdAt: string
  rule: EvaluationRule
}

export interface EvaluationResult {
  id: number
  simulationId: string
  overallScore: number
  verdict: 'PASS' | 'FAIL' | 'WARNING'
  evaluatedAt: string
  evaluationDurationMs: number
  metricResults: EvaluationMetricResult[]
}

// Rules API
export const getRules = async (): Promise<EvaluationRule[]> => {
  try {
    const response = await axios.get(`${EVALUATION_API_BASE}/rules`)
    return response.data
  } catch (error) {
    console.error('Error fetching rules:', error)
    throw error
  }
}

export const getRuleById = async (ruleId: number): Promise<EvaluationRule> => {
  try {
    const response = await axios.get(`${EVALUATION_API_BASE}/rules/${ruleId}`)
    return response.data
  } catch (error) {
    console.error('Error fetching rule:', error)
    throw error
  }
}

export const createRule = async (request: CreateRuleRequest): Promise<EvaluationRule> => {
  try {
    const response = await axios.post(`${EVALUATION_API_BASE}/rules`, request)
    return response.data
  } catch (error) {
    console.error('Error creating rule:', error)
    throw error
  }
}

export const updateRule = async (
  ruleId: number,
  request: UpdateRuleRequest
): Promise<EvaluationRule> => {
  try {
    const response = await axios.put(`${EVALUATION_API_BASE}/rules/${ruleId}`, request)
    return response.data
  } catch (error) {
    console.error('Error updating rule:', error)
    throw error
  }
}

export const deleteRule = async (ruleId: number): Promise<void> => {
  try {
    await axios.delete(`${EVALUATION_API_BASE}/rules/${ruleId}`)
  } catch (error) {
    console.error('Error deleting rule:', error)
    throw error
  }
}

// Evaluation API
export const triggerEvaluation = async (simulationId: string): Promise<EvaluationResult> => {
  try {
    const response = await axios.post(`${EVALUATION_API_BASE}/evaluations/trigger`, {
      simulationId,
    })
    return response.data
  } catch (error) {
    console.error('Error triggering evaluation:', error)
    throw error
  }
}

export const getEvaluationResult = async (simulationId: string): Promise<EvaluationResult> => {
  try {
    const response = await axios.get(`${EVALUATION_API_BASE}/evaluations/${simulationId}`)
    return response.data
  } catch (error) {
    console.error('Error fetching evaluation result:', error)
    throw error
  }
}

// Helper functions
export const formatRuleData = (rules: EvaluationRule[]) => {
  return rules.map((rule) => ({
    id: rule.id,
    ruleName: rule.ruleName,
    metricName: rule.metricName,
    operator: rule.operator,
    threshold: rule.thresholdValue,
    weight: rule.weight,
    status: rule.isActive ? 'Active' : 'Inactive',
    lastUpdated: new Date(rule.updatedAt).toLocaleDateString() + ', ' + new Date(rule.updatedAt).toLocaleTimeString(),
  }))
}

export const getVerdictColor = (verdict: string): string => {
  switch (verdict) {
    case 'PASS':
      return '#4CAF50' // Green
    case 'FAIL':
      return '#F44336' // Red
    case 'WARNING':
      return '#FF9800' // Orange
    default:
      return '#9E9E9E' // Gray
  }
}

export const getScoreColor = (score: number): string => {
  if (score >= 80) return '#4CAF50' // Green
  if (score >= 60) return '#FF9800' // Orange
  return '#F44336' // Red
}
