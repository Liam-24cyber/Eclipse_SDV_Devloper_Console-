/**
 * SDV Developer Console - Analytics Dashboard Page
 * 
 * Comprehensive analytics and insights for simulations
 */

import { useEffect, useState } from 'react';

interface AnalyticsDashboard {
  overviewStats: {
    totalSimulations: number;
    successfulSimulations: number;
    failedSimulations: number;
    runningSimulations: number;
    pendingSimulations: number;
    successRate: number;
    averageDuration: number;
    totalExecutionTime: number;
  };
  successRateTrends: Array<{
    date: string;
    totalCount: number;
    successCount: number;
    failureCount: number;
    successRate: number;
  }>;
  platformDistribution: Array<{
    label: string;
    count: number;
    percentage: number;
  }>;
  scenarioTypeDistribution: Array<{
    label: string;
    count: number;
    percentage: number;
  }>;
  performanceMetrics: {
    averageDuration: number;
    minDuration: number;
    maxDuration: number;
    medianDuration: number;
    durationTrend: Array<{
      timestamp: string;
      value: number;
      metricName: string;
      unit: string;
    }>;
  };
  recentSimulations: Array<{
    id: string;
    name: string;
    status: string;
    platform: string;
    scenarioType: string;
    createdAt: string;
    createdBy: string;
    duration: number;
  }>;
  historicalTrends: Array<{
    date: string;
    totalSimulations: number;
    successfulSimulations: number;
    failedSimulations: number;
    averageDuration: number;
  }>;
}

export default function AnalyticsPage() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<AnalyticsDashboard | null>(null);
  const [daysBack, setDaysBack] = useState(30);
  const [currentTab, setCurrentTab] = useState(0);

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
            Authorization: 'Basic ' + btoa('developer:password'),
          },
        }
      );

      if (!response.ok) {
        throw new Error('Failed to fetch analytics data');
      }

      const analyticsData = await response.json();
      setData(analyticsData);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Alert severity="error" sx={{ m: 2 }}>
        {error}
      </Alert>
    );
  }

  if (!data) {
    return (
      <Alert severity="info" sx={{ m: 2 }}>
        No analytics data available
      </Alert>
    );
  }

  const { overviewStats, successRateTrends, platformDistribution, scenarioTypeDistribution, performanceMetrics, recentSimulations, historicalTrends } = data;

  // Overview Cards Data
  const overviewCards = [
    {
      title: 'Total Simulations',
      value: overviewStats.totalSimulations,
      icon: <PlayArrow sx={{ fontSize: 40 }} />,
      color: '#1976d2',
    },
    {
      title: 'Successful',
      value: overviewStats.successfulSimulations,
      icon: <CheckCircle sx={{ fontSize: 40 }} />,
      color: '#2e7d32',
      subtitle: `${overviewStats.successRate}% Success Rate`,
    },
    {
      title: 'Failed',
      value: overviewStats.failedSimulations,
      icon: <ErrorIcon sx={{ fontSize: 40 }} />,
      color: '#d32f2f',
    },
    {
      title: 'Running',
      value: overviewStats.runningSimulations,
      icon: <Schedule sx={{ fontSize: 40 }} />,
      color: '#ed6c02',
    },
  ];

  // Success Rate Trend Chart Data
  const successRateChartData = {
    labels: successRateTrends.map((d) => d.date).reverse(),
    datasets: [
      {
        label: 'Success Rate (%)',
        data: successRateTrends.map((d) => d.successRate).reverse(),
        borderColor: '#2e7d32',
        backgroundColor: 'rgba(46, 125, 50, 0.1)',
        tension: 0.4,
      },
    ],
  };

  // Platform Distribution Pie Chart
  const platformChartData = {
    labels: platformDistribution.map((d) => d.label),
    datasets: [
      {
        data: platformDistribution.map((d) => d.count),
        backgroundColor: [
          '#1976d2',
          '#2e7d32',
          '#ed6c02',
          '#d32f2f',
          '#9c27b0',
          '#00897b',
        ],
      },
    ],
  };

  // Historical Trends Bar Chart
  const historicalTrendsChartData = {
    labels: historicalTrends.map((d) => d.date).reverse(),
    datasets: [
      {
        label: 'Successful',
        data: historicalTrends.map((d) => d.successfulSimulations).reverse(),
        backgroundColor: '#2e7d32',
      },
      {
        label: 'Failed',
        data: historicalTrends.map((d) => d.failedSimulations).reverse(),
        backgroundColor: '#d32f2f',
      },
    ],
  };

  // Performance Duration Chart
  const performanceChartData = {
    labels: performanceMetrics.durationTrend.map((d) => 
      new Date(d.timestamp).toLocaleTimeString()
    ).reverse(),
    datasets: [
      {
        label: 'Duration (seconds)',
        data: performanceMetrics.durationTrend.map((d) => d.value).reverse(),
        borderColor: '#1976d2',
        backgroundColor: 'rgba(25, 118, 210, 0.1)',
        fill: true,
        tension: 0.4,
      },
    ],
  };

  const formatDuration = (seconds: number) => {
    if (seconds < 60) return `${seconds}s`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}m ${seconds % 60}s`;
    return `${Math.floor(seconds / 3600)}h ${Math.floor((seconds % 3600) / 60)}m`;
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h4" gutterBottom sx={{ mb: 3, fontWeight: 'bold' }}>
        📊 Analytics Dashboard
      </Typography>

      {/* Period Selector */}
      <Paper sx={{ mb: 3, p: 2 }}>
        <Tabs value={daysBack} onChange={(e, val) => setDaysBack(val)}>
          <Tab label="Last 7 Days" value={7} />
          <Tab label="Last 30 Days" value={30} />
          <Tab label="Last 90 Days" value={90} />
          <Tab label="Last Year" value={365} />
        </Tabs>
      </Paper>

      {/* Overview Cards */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        {overviewCards.map((card, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <Card sx={{ height: '100%', position: 'relative', overflow: 'visible' }}>
              <CardContent>
                <Box display="flex" alignItems="center" justifyContent="space-between">
                  <Box>
                    <Typography color="text.secondary" variant="body2">
                      {card.title}
                    </Typography>
                    <Typography variant="h3" sx={{ fontWeight: 'bold', my: 1 }}>
                      {card.value.toLocaleString()}
                    </Typography>
                    {card.subtitle && (
                      <Typography variant="caption" color="text.secondary">
                        {card.subtitle}
                      </Typography>
                    )}
                  </Box>
                  <Box sx={{ color: card.color }}>{card.icon}</Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Charts Row 1 */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Success Rate Trend
              </Typography>
              <Box sx={{ height: 300 }}>
                <Line
                  data={successRateChartData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                      y: {
                        beginAtZero: true,
                        max: 100,
                      },
                    },
                  }}
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Platform Distribution
              </Typography>
              <Box sx={{ height: 300 }}>
                <Pie
                  data={platformChartData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                      legend: {
                        position: 'bottom',
                      },
                    },
                  }}
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts Row 2 */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Historical Trends
              </Typography>
              <Box sx={{ height: 300 }}>
                <Bar
                  data={historicalTrendsChartData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                      x: {
                        stacked: true,
                      },
                      y: {
                        stacked: true,
                      },
                    },
                  }}
                />
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card>
            <CardContent>
              <Typography variant="h6" gutterBottom>
                Performance Metrics
              </Typography>
              <Box sx={{ height: 300 }}>
                <Line
                  data={performanceChartData}
                  options={{
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                      y: {
                        beginAtZero: true,
                      },
                    },
                  }}
                />
              </Box>
              <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-around' }}>
                <Box textAlign="center">
                  <Typography variant="caption" color="text.secondary">
                    Average
                  </Typography>
                  <Typography variant="h6">
                    {formatDuration(performanceMetrics.averageDuration)}
                  </Typography>
                </Box>
                <Box textAlign="center">
                  <Typography variant="caption" color="text.secondary">
                    Min
                  </Typography>
                  <Typography variant="h6">
                    {formatDuration(performanceMetrics.minDuration)}
                  </Typography>
                </Box>
                <Box textAlign="center">
                  <Typography variant="caption" color="text.secondary">
                    Max
                  </Typography>
                  <Typography variant="h6">
                    {formatDuration(performanceMetrics.maxDuration)}
                  </Typography>
                </Box>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Recent Simulations Table */}
      <Card>
        <CardContent>
          <Typography variant="h6" gutterBottom>
            Recent Simulations
          </Typography>
          <Box sx={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '2px solid #e0e0e0' }}>
                  <th style={{ padding: '12px', textAlign: 'left' }}>Name</th>
                  <th style={{ padding: '12px', textAlign: 'left' }}>Status</th>
                  <th style={{ padding: '12px', textAlign: 'left' }}>Platform</th>
                  <th style={{ padding: '12px', textAlign: 'left' }}>Type</th>
                  <th style={{ padding: '12px', textAlign: 'left' }}>Duration</th>
                  <th style={{ padding: '12px', textAlign: 'left' }}>Created By</th>
                  <th style={{ padding: '12px', textAlign: 'left' }}>Date</th>
                </tr>
              </thead>
              <tbody>
                {recentSimulations.map((sim) => (
                  <tr key={sim.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
                    <td style={{ padding: '12px' }}>{sim.name}</td>
                    <td style={{ padding: '12px' }}>
                      <span
                        style={{
                          padding: '4px 8px',
                          borderRadius: '4px',
                          fontSize: '12px',
                          backgroundColor:
                            sim.status === 'SUCCESS'
                              ? '#e8f5e9'
                              : sim.status === 'FAILED'
                              ? '#ffebee'
                              : '#fff3e0',
                          color:
                            sim.status === 'SUCCESS'
                              ? '#2e7d32'
                              : sim.status === 'FAILED'
                              ? '#d32f2f'
                              : '#ed6c02',
                        }}
                      >
                        {sim.status}
                      </span>
                    </td>
                    <td style={{ padding: '12px' }}>{sim.platform}</td>
                    <td style={{ padding: '12px' }}>{sim.scenarioType}</td>
                    <td style={{ padding: '12px' }}>
                      {sim.duration ? formatDuration(sim.duration) : '-'}
                    </td>
                    <td style={{ padding: '12px' }}>{sim.createdBy}</td>
                    <td style={{ padding: '12px' }}>
                      {new Date(sim.createdAt).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </Box>
        </CardContent>
      </Card>
    </Box>
  );
}
