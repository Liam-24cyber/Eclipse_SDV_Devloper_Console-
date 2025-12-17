import React, { useEffect, useState } from 'react'
import { Box, Table } from '@dco/sdv-ui'
import Dco from '.'
import {
  getRules,
  createRule,
  updateRule,
  deleteRule,
  type EvaluationRule,
  type CreateRuleRequest,
  type UpdateRuleRequest,
} from '../../services/functionEvaluation.service'

const EvaluationRulesPage = () => {
  const [rules, setRules] = useState<EvaluationRule[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState<string | null>(null)
  const [showModal, setShowModal] = useState(false)
  const [editingRule, setEditingRule] = useState<EvaluationRule | null>(null)
  const [lastUpdatedRuleId, setLastUpdatedRuleId] = useState<number | null>(null)
  const [formData, setFormData] = useState<CreateRuleRequest>({
    ruleName: '',
    metricName: '',
    operator: '<',
    threshold: 0,
    weight: 50,
  })

  useEffect(() => {
    loadRules()
  }, [])

  const loadRules = async () => {
    try {
      setLoading(true)
      setError(null)
      const data = await getRules()
      // Add actions field to each rule for the Table component
      const rulesWithActions = data.map(rule => ({ ...rule, actions: rule }))
      setRules(rulesWithActions)
    } catch (err: any) {
      setError(err.message || 'Failed to load rules')
    } finally {
      setLoading(false)
    }
  }

  const handleCreateRule = () => {
    setEditingRule(null)
    setFormData({
      ruleName: '',
      metricName: '',
      operator: '<',
      threshold: 0,
      weight: 50,
    })
    setShowModal(true)
  }

  const handleEditRule = (rule: EvaluationRule) => {
    setEditingRule(rule)
    setFormData({
      ruleName: rule.ruleName,
      metricName: rule.metricName,
      operator: rule.operator,
      threshold: rule.thresholdValue,
      weight: rule.weight,
    })
    setShowModal(true)
  }

  const handleSubmit = async () => {
    try {
      setError(null)
      if (editingRule) {
        const updateData: UpdateRuleRequest = {
          ruleName: formData.ruleName,
          operator: formData.operator,
          threshold: formData.threshold,
          weight: formData.weight,
        }
        await updateRule(editingRule.id, updateData)
        setLastUpdatedRuleId(editingRule.id)
        setSuccess('Rule updated successfully')
        setTimeout(() => setLastUpdatedRuleId(null), 3000)
      } else {
        await createRule(formData)
        setSuccess('Rule created successfully')
      }
      setShowModal(false)
      loadRules()
      setTimeout(() => setSuccess(null), 3000)
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to save rule')
    }
  }

  const handleDelete = async (ruleId: number) => {
    if (!confirm('Are you sure you want to delete this rule?')) return
    
    try {
      setError(null)
      await deleteRule(ruleId)
      setSuccess('Rule deleted successfully')
      loadRules()
      setTimeout(() => setSuccess(null), 3000)
    } catch (err: any) {
      setError(err.message || 'Failed to delete rule')
    }
  }

  const handleToggleActive = async (rule: EvaluationRule) => {
    try {
      setError(null)
      await updateRule(rule.id, { isActive: !rule.isActive })
      setSuccess(`Rule ${!rule.isActive ? 'activated' : 'deactivated'} successfully`)
      loadRules()
      setTimeout(() => setSuccess(null), 3000)
    } catch (err: any) {
      setError(err.message || 'Failed to update rule')
    }
  }

  return (
    <Dco>
      <Box fullHeight padding="large" scrollY>
        <div style={{ maxWidth: '1400px', margin: '0 auto' }}>
          <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h1 style={{ margin: '0 0 8px 0', fontSize: '24px', fontWeight: 'bold', color: '#fff' }}>
                Evaluation Rules
              </h1>
              <p style={{ margin: 0, color: '#aaa' }}>
                Manage rules for automated simulation evaluation
              </p>
            </div>
            <button
              onClick={handleCreateRule}
              style={{
                backgroundColor: '#1976d2',
                color: 'white',
                border: 'none',
                padding: '10px 20px',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '14px',
                fontWeight: '500',
              }}
            >
              + Create Rule
            </button>
          </div>

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
          
          {success && (
            <div style={{
              backgroundColor: '#e8f5e9',
              color: '#2e7d32',
              padding: '12px 16px',
              borderRadius: '4px',
              marginBottom: '20px',
            }}>
              {success}
            </div>
          )}

          {loading ? (
            <div style={{ textAlign: 'center', padding: '40px', color: '#fff' }}>Loading...</div>
          ) : rules.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '40px', color: '#aaa' }}>
              No evaluation rules found. Create your first rule to get started.
            </div>
          ) : (
            <Table 
              columns={[
                {
                  Header: 'ID',
                  accessor: 'id',
                  width: 60,
                },
                {
                  Header: 'Rule Name',
                  accessor: 'ruleName',
                  formatter: (value: any, cell: any) => {
                    const rule = cell.row.original
                    return (
                      <div style={{ display: 'flex', alignItems: 'center' }}>
                        {rule.ruleName}
                        {lastUpdatedRuleId === rule.id && (
                          <span style={{
                            marginLeft: '8px',
                            padding: '2px 6px',
                            backgroundColor: '#4CAF50',
                            color: 'white',
                            fontSize: '11px',
                            borderRadius: '3px',
                            fontWeight: '500'
                          }}>
                            ✓ UPDATED
                          </span>
                        )}
                      </div>
                    )
                  }
                },
                {
                  Header: 'Metric',
                  accessor: 'metricName',
                  formatter: (value: any) => (
                    <code style={{ fontSize: '13px', fontFamily: 'monospace', color: '#00bcd4' }}>
                      {value}
                    </code>
                  )
                },
                {
                  Header: 'Operator',
                  accessor: 'operator',
                  width: 80,
                },
                {
                  Header: 'Threshold',
                  accessor: 'thresholdValue',
                  width: 100,
                },
                {
                  Header: 'Weight',
                  accessor: 'weight',
                  width: 80,
                },
                {
                  Header: 'Status',
                  accessor: 'isActive',
                  width: 100,
                  formatter: (value: any) => (
                    <span style={{
                      padding: '4px 8px',
                      borderRadius: '4px',
                      backgroundColor: value ? '#4CAF50' : '#9E9E9E',
                      color: 'white',
                      fontSize: '12px',
                      fontWeight: '500',
                    }}>
                      {value ? 'Active' : 'Inactive'}
                    </span>
                  )
                },
                {
                  Header: 'Actions',
                  accessor: 'actions',
                  width: 250,
                  disableSortBy: true,
                  formatter: (value: any, cell: any) => {
                    // value is the full rule object we passed in the actions field
                    const rule = value || cell?.row?.original || cell?.row?.values
                    if (!rule || !rule.id) return null
                    
                    return (
                      <div style={{ display: 'flex', gap: '6px', flexWrap: 'nowrap' }}>
                        <button
                          onClick={(e) => {
                            e.preventDefault()
                            e.stopPropagation()
                            handleEditRule(rule)
                          }}
                          style={{
                            padding: '4px 8px',
                            fontSize: '12px',
                            border: '1px solid #1976d2',
                            backgroundColor: 'transparent',
                            color: '#1976d2',
                            borderRadius: '3px',
                            cursor: 'pointer',
                            minWidth: '50px',
                          }}
                          title="Edit rule"
                          type="button"
                        >
                          Edit
                        </button>
                        <button
                          onClick={(e) => {
                            e.preventDefault()
                            e.stopPropagation()
                            handleToggleActive(rule)
                          }}
                          style={{
                            padding: '4px 8px',
                            fontSize: '12px',
                            border: '1px solid #666',
                            backgroundColor: 'transparent',
                            color: '#ccc',
                            borderRadius: '3px',
                            cursor: 'pointer',
                            minWidth: '70px',
                          }}
                          title={rule.isActive ? 'Deactivate rule' : 'Activate rule'}
                          type="button"
                        >
                          {rule.isActive ? 'Deactivate' : 'Activate'}
                        </button>
                        <button
                          onClick={(e) => {
                            e.preventDefault()
                            e.stopPropagation()
                            handleDelete(rule.id)
                          }}
                          style={{
                            padding: '4px 8px',
                            fontSize: '12px',
                            border: '1px solid #d32f2f',
                            backgroundColor: 'transparent',
                            color: '#d32f2f',
                            borderRadius: '3px',
                            cursor: 'pointer',
                            minWidth: '55px',
                          }}
                          title="Delete rule"
                          type="button"
                        >
                          Delete
                        </button>
                      </div>
                    )
                  }
                }
              ]}
              data={rules}
            />
          )}

          {showModal && (
            <div style={{
              position: 'fixed',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              backgroundColor: 'rgba(0,0,0,0.8)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              zIndex: 1000,
            }}>
              <div style={{
                backgroundColor: '#2a2a2a',
                border: '1px solid #444',
                borderRadius: '8px',
                padding: '24px',
                maxWidth: '500px',
                width: '100%',
                maxHeight: '90vh',
                overflow: 'auto',
              }}>
                <h2 style={{ margin: '0 0 20px 0', fontSize: '20px', color: '#fff' }}>
                  {editingRule ? 'Edit Rule' : 'Create New Rule'}
                </h2>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: '500', color: '#fff' }}>
                      Rule Name
                    </label>
                    <input
                      type="text"
                      value={formData.ruleName}
                      onChange={(e) => setFormData({ ...formData, ruleName: e.target.value })}
                      placeholder="e.g., Max Duration"
                      disabled={!!editingRule}
                      style={{
                        width: '100%',
                        padding: '8px 12px',
                        border: '1px solid #555',
                        borderRadius: '4px',
                        fontSize: '14px',
                        backgroundColor: '#1a1a1a',
                        color: '#fff',
                      }}
                    />
                  </div>

                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: '500', color: '#fff' }}>
                      Metric Name
                    </label>
                    <input
                      type="text"
                      value={formData.metricName}
                      onChange={(e) => setFormData({ ...formData, metricName: e.target.value })}
                      placeholder="e.g., simulation_duration_seconds"
                      disabled={!!editingRule}
                      style={{
                        width: '100%',
                        padding: '8px 12px',
                        border: '1px solid #555',
                        borderRadius: '4px',
                        fontSize: '14px',
                        fontFamily: 'monospace',
                        backgroundColor: '#1a1a1a',
                        color: '#00bcd4',
                      }}
                    />
                  </div>

                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: '500', color: '#fff' }}>
                      Operator
                    </label>
                    <select
                      value={formData.operator}
                      onChange={(e) => setFormData({ ...formData, operator: e.target.value })}
                      style={{
                        width: '100%',
                        padding: '8px 12px',
                        border: '1px solid #555',
                        borderRadius: '4px',
                        fontSize: '14px',
                        backgroundColor: '#1a1a1a',
                        color: '#fff',
                      }}
                    >
                      <option value="<">Less than (&lt;)</option>
                      <option value="<=">Less than or equal (&lt;=)</option>
                      <option value="=">Equal (=)</option>
                      <option value="!=">Not equal (!=)</option>
                      <option value=">">Greater than (&gt;)</option>
                      <option value=">=">Greater than or equal (&gt;=)</option>
                    </select>
                  </div>

                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: '500', color: '#fff' }}>
                      Threshold Value
                    </label>
                    <input
                      type="number"
                      step="0.01"
                      value={formData.threshold}
                      onChange={(e) => setFormData({ ...formData, threshold: parseFloat(e.target.value) })}
                      placeholder="e.g., 60.00"
                      style={{
                        width: '100%',
                        padding: '8px 12px',
                        border: '1px solid #555',
                        borderRadius: '4px',
                        fontSize: '14px',
                        backgroundColor: '#1a1a1a',
                        color: '#fff',
                      }}
                    />
                  </div>

                  <div>
                    <label style={{ display: 'block', marginBottom: '6px', fontWeight: '500', color: '#fff' }}>
                      Weight (1-100)
                    </label>
                    <input
                      type="number"
                      min="1"
                      max="100"
                      value={formData.weight}
                      onChange={(e) => setFormData({ ...formData, weight: parseInt(e.target.value) })}
                      placeholder="e.g., 50"
                      style={{
                        width: '100%',
                        padding: '8px 12px',
                        border: '1px solid #555',
                        borderRadius: '4px',
                        fontSize: '14px',
                        backgroundColor: '#1a1a1a',
                        color: '#fff',
                      }}
                    />
                  </div>
                </div>

                <div style={{ marginTop: '24px', display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                  <button
                    onClick={() => setShowModal(false)}
                    style={{
                      padding: '10px 20px',
                      fontSize: '14px',
                      border: '1px solid #555',
                      backgroundColor: 'transparent',
                      color: '#ccc',
                      borderRadius: '4px',
                      cursor: 'pointer',
                    }}
                  >
                    Cancel
                  </button>
                  <button
                    onClick={handleSubmit}
                    style={{
                      padding: '10px 20px',
                      fontSize: '14px',
                      border: 'none',
                      backgroundColor: '#1976d2',
                      color: 'white',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontWeight: '500',
                    }}
                  >
                    {editingRule ? 'Update' : 'Create'}
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      </Box>
    </Dco>
  )
}

export default EvaluationRulesPage
