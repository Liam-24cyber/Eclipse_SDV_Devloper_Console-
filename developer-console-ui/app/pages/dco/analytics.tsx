/**
 * SDV Developer Console - Analytics Dashboard
 * Simple implementation using plain HTML/CSS and fetch API
 */

import { useEffect, useState } from 'react';

interface OverviewStats {
  totalSimulations: number;
  successfulSimulations: number;
  failedSimulations: number;
  runningSimulations: number;
  pendingSimulations: number;
  successRate: number;
  averageDuration: number;
  totalExecutionTime: number;
}

interface AnalyticsDashboard {
  overviewStats: OverviewStats;
  successRateTrends: Array<any>;
  platformDistribution: Array<{ label: string; count: number; percentage: number }>;
  scenarioTypeDistribution: Array<{ label: string; count: number; percentage: number }>;
  performanceMetrics: any;
  recentSimulations: Array<any>;
  historicalTrends: Array<any>;
}

export default function AnalyticsPage() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<AnalyticsDashboard | null>(null);
  const [daysBack, setDaysBack] = useState(30);

  useEffect(() => {
    fetchAnalytics();
  }, [daysBack]);

  const fetchAnalytics = async () => {
    try {
      setLoading(true);
      const response = await fetch(
        `http://localhost:8082/api/analytics/dashboard?daysBack=${daysBack}`,
        {
          headers: {
            'Authorization': 'Basic ' + btoa('developer:password'),
            'Content-Type': 'application/json',
          },
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      const analyticsData = await response.json();
      setData(analyticsData);
      setError(null);
    } catch (err) {
      console.error('Analytics fetch error:', err);
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  const formatDuration = (seconds: number) => {
    if (!seconds) return '-';
    if (seconds < 60) return `${seconds}s`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ${seconds % 60}s`;
    return `${Math.floor(seconds / 3600)}h ${Math.floor((seconds % 3600) / 60)}m`;
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <div style={{ fontSize: '24px' }}>Loading Analytics...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={{ padding: '20px', backgroundColor: '#ffebee', borderRadius: '8px', margin: '20px' }}>
        <h3 style={{ color: '#d32f2f', margin: '0 0 10px 0' }}>Error Loading Analytics</h3>
        <p style={{ margin: 0 }}>{error}</p>
        <button 
          onClick={fetchAnalytics}
          style={{ marginTop: '10px', padding: '8px 16px', cursor: 'pointer' }}
        >
          Retry
        </button>
      </div>
    );
  }

  if (!data) {
    return (
      <div style={{ padding: '20px', backgroundColor: '#e3f2fd', borderRadius: '8px', margin: '20px' }}>
        No analytics data available
      </div>
    );
  }

  const { overviewStats, platformDistribution, scenarioTypeDistribution, recentSimulations } = data;

  return (
    <div style={{ padding: '24px', maxWidth: '1400px', margin: '0 auto' }}>
      <h1 style={{ fontSize: '32px', fontWeight: 'bold', marginBottom: '24px' }}>
        📊 Analytics Dashboard
      </h1>

      {/* Time Range Selector */}
      <div style={{ marginBottom: '24px', display: 'flex', gap: '10px' }}>
        {[7, 30, 90, 365].map((days) => (
          <button
            key={days}
            onClick={() => setDaysBack(days)}
            style={{
              padding: '10px 20px',
              backgroundColor: daysBack === days ? '#1976d2' : '#f5f5f5',
              color: daysBack === days ? 'white' : 'black',
              border: 'none',
              borderRadius: '8px',
              cursor: 'pointer',
              fontSize: '14px',
              fontWeight: daysBack === days ? 'bold' : 'normal',
            }}
          >
            Last {days} Days
          </button>
        ))}
      </div>

      {/* Overview Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '20px', marginBottom: '30px' }}>
        <div style={{ backgroundColor: '#e3f2fd', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>Total Simulations</div>
          <div style={{ fontSize: '36px', fontWeight: 'bold', color: '#1976d2' }}>
            {overviewStats.totalSimulations.toLocaleString()}
          </div>
        </div>

        <div style={{ backgroundColor: '#e8f5e9', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>Successful</div>
          <div style={{ fontSize: '36px', fontWeight: 'bold', color: '#2e7d32' }}>
            {overviewStats.successfulSimulations.toLocaleString()}
          </div>
          <div style={{ fontSize: '12px', color: '#666', marginTop: '4px' }}>
            {overviewStats.successRate.toFixed(1)}% Success Rate
          </div>
        </div>

        <div style={{ backgroundColor: '#ffebee', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>Failed</div>
          <div style={{ fontSize: '36px', fontWeight: 'bold', color: '#d32f2f' }}>
            {overviewStats.failedSimulations.toLocaleString()}
          </div>
        </div>

        <div style={{ backgroundColor: '#fff3e0', padding: '20px', borderRadius: '12px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>Running</div>
          <div style={{ fontSize: '36px', fontWeight: 'bold', color: '#ed6c02' }}>
            {overviewStats.runningSimulations.toLocaleString()}
          </div>
        </div>
      </div>

      {/* Performance Stats */}
      <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>⚡ Performance Metrics</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '20px' }}>
          <div>
            <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>Average Duration</div>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1976d2' }}>
              {formatDuration(Math.round(overviewStats.averageDuration))}
            </div>
          </div>
          <div>
            <div style={{ fontSize: '14px', color: '#666', marginBottom: '8px' }}>Total Execution Time</div>
            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#1976d2' }}>
              {formatDuration(overviewStats.totalExecutionTime)}
            </div>
          </div>
        </div>
      </div>

      {/* Platform Distribution */}
      {platformDistribution && platformDistribution.length > 0 && (
        <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
          <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>🖥️ Platform Distribution</h2>
          {platformDistribution.map((item, index) => (
            <div key={index} style={{ marginBottom: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                <span style={{ fontWeight: '500' }}>{item.label}</span>
                <span style={{ color: '#666' }}>{item.count} ({item.percentage.toFixed(1)}%)</span>
              </div>
              <div style={{ 
                height: '10px', 
                backgroundColor: '#e0e0e0', 
                borderRadius: '5px', 
                overflow: 'hidden' 
              }}>
                <div style={{ 
                  height: '100%', 
                  width: `${item.percentage}%`, 
                  backgroundColor: '#1976d2',
                  transition: 'width 0.3s ease'
                }} />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Scenario Type Distribution */}
      {scenarioTypeDistribution && scenarioTypeDistribution.length > 0 && (
        <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)', marginBottom: '30px' }}>
          <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>📋 Scenario Type Distribution</h2>
          {scenarioTypeDistribution.map((item, index) => (
            <div key={index} style={{ marginBottom: '16px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '8px' }}>
                <span style={{ fontWeight: '500' }}>{item.label}</span>
                <span style={{ color: '#666' }}>{item.count} ({item.percentage.toFixed(1)}%)</span>
              </div>
              <div style={{ 
                height: '10px', 
                backgroundColor: '#e0e0e0', 
                borderRadius: '5px', 
                overflow: 'hidden' 
              }}>
                <div style={{ 
                  height: '100%', 
                  width: `${item.percentage}%`, 
                  backgroundColor: '#2e7d32',
                  transition: 'width 0.3s ease'
                }} />
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Recent Simulations */}
      {recentSimulations && recentSimulations.length > 0 && (
        <div style={{ backgroundColor: 'white', padding: '24px', borderRadius: '12px', boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
          <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '20px' }}>📝 Recent Simulations</h2>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #e0e0e0', textAlign: 'left' }}>
                  <th style={{ padding: '12px', fontWeight: '600' }}>Name</th>
                  <th style={{ padding: '12px', fontWeight: '600' }}>Status</th>
                  <th style={{ padding: '12px', fontWeight: '600' }}>Platform</th>
                  <th style={{ padding: '12px', fontWeight: '600' }}>Type</th>
                  <th style={{ padding: '12px', fontWeight: '600' }}>Duration</th>
                  <th style={{ padding: '12px', fontWeight: '600' }}>Created By</th>
                  <th style={{ padding: '12px', fontWeight: '600' }}>Date</th>
                </tr>
              </thead>
              <tbody>
                {recentSimulations.map((sim, index) => (
                  <tr key={sim.id || index} style={{ borderBottom: '1px solid #f0f0f0' }}>
                    <td style={{ padding: '12px' }}>{sim.name || '-'}</td>
                    <td style={{ padding: '12px' }}>
                      <span style={{
                        padding: '4px 12px',
                        borderRadius: '12px',
                        fontSize: '12px',
                        fontWeight: '500',
                        backgroundColor: 
                          sim.status === 'SUCCESS' ? '#e8f5e9' :
                          sim.status === 'FAILED' ? '#ffebee' :
                          sim.status === 'RUNNING' ? '#fff3e0' : '#f5f5f5',
                        color:
                          sim.status === 'SUCCESS' ? '#2e7d32' :
                          sim.status === 'FAILED' ? '#d32f2f' :
                          sim.status === 'RUNNING' ? '#ed6c02' : '#666',
                      }}>
                        {sim.status || 'PENDING'}
                      </span>
                    </td>
                    <td style={{ padding: '12px' }}>{sim.platform || '-'}</td>
                    <td style={{ padding: '12px' }}>{sim.scenarioType || '-'}</td>
                    <td style={{ padding: '12px' }}>{formatDuration(sim.duration)}</td>
                    <td style={{ padding: '12px' }}>{sim.createdBy || '-'}</td>
                    <td style={{ padding: '12px' }}>
                      {sim.createdAt ? new Date(sim.createdAt).toLocaleString() : '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Refresh Button */}
      <div style={{ marginTop: '30px', textAlign: 'center' }}>
        <button
          onClick={fetchAnalytics}
          style={{
            padding: '12px 32px',
            backgroundColor: '#1976d2',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            fontSize: '16px',
            fontWeight: '500',
            cursor: 'pointer',
            boxShadow: '0 2px 4px rgba(0,0,0,0.2)',
          }}
        >
          🔄 Refresh Dashboard
        </button>
      </div>
    </div>
  );
}
