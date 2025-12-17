import { useEffect, useState } from 'react';
import { Box, Headline, Button, StatusMessage } from '@dco/sdv-ui';

const DiagnosticPage = () => {
  const [results, setResults] = useState<any>({
    graphqlUrl: '',
    token: '',
    scenarioCount: null,
    trackCount: null,
    error: null,
    rawResponse: null,
  });

  useEffect(() => {
    // Get configuration info
    const token = localStorage.getItem('token');
    const graphqlUrl = `${window.location.protocol}//${window.location.hostname}:8080/graphql`;
    
    setResults((prev: any) => ({
      ...prev,
      graphqlUrl,
      token: token || 'NOT SET',
    }));
  }, []);

  const testScenarios = async () => {
    try {
      const token = localStorage.getItem('token');
      const graphqlUrl = `${window.location.protocol}//${window.location.hostname}:8080/graphql`;
      
      const response = await fetch(graphqlUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Basic ${token}` : '',
        },
        body: JSON.stringify({
          query: `{
            searchScenarioByPattern(scenarioPattern: "", page: 0, size: 5) {
              total
              content {
                id
                name
                type
              }
            }
          }`
        }),
      });

      const data = await response.json();
      
      setResults((prev: any) => ({
        ...prev,
        scenarioCount: data?.data?.searchScenarioByPattern?.total || 0,
        rawResponse: JSON.stringify(data, null, 2),
        error: data.errors ? JSON.stringify(data.errors, null, 2) : null,
      }));
    } catch (err: any) {
      setResults((prev: any) => ({
        ...prev,
        error: err.message,
      }));
    }
  };

  const testTracks = async () => {
    try {
      const token = localStorage.getItem('token');
      const graphqlUrl = `${window.location.protocol}//${window.location.hostname}:8080/graphql`;
      
      const response = await fetch(graphqlUrl, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': token ? `Basic ${token}` : '',
        },
        body: JSON.stringify({
          query: `{
            searchTrackByPattern(trackPattern: "", page: 0, size: 5) {
              total
              content {
                id
                name
                trackType
              }
            }
          }`
        }),
      });

      const data = await response.json();
      
      setResults((prev: any) => ({
        ...prev,
        trackCount: data?.data?.searchTrackByPattern?.total || 0,
        rawResponse: JSON.stringify(data, null, 2),
        error: data.errors ? JSON.stringify(data.errors, null, 2) : null,
      }));
    } catch (err: any) {
      setResults((prev: any) => ({
        ...prev,
        error: err.message,
      }));
    }
  };

  const setAuth = () => {
    localStorage.setItem('token', btoa('developer:password'));
    localStorage.setItem('user', 'developer');
    alert('Authentication set! Token: ' + btoa('developer:password'));
    window.location.reload();
  };

  return (
    <Box padding="large">
      <Headline level={1}>GraphQL Connection Diagnostic</Headline>
      
      <Box padding="small" style={{ marginTop: '20px' }}>
        <Headline level={3}>Configuration:</Headline>
        <p><strong>GraphQL URL:</strong> {results.graphqlUrl}</p>
        <p><strong>Auth Token:</strong> {results.token}</p>
      </Box>

      <Box padding="small" style={{ marginTop: '20px' }}>
        <Button onClick={setAuth} style={{ marginRight: '10px' }}>
          Set Authentication
        </Button>
        <Button onClick={testScenarios} style={{ marginRight: '10px' }}>
          Test Scenarios
        </Button>
        <Button onClick={testTracks}>
          Test Tracks
        </Button>
      </Box>

      {results.scenarioCount !== null && (
        <Box padding="small" style={{ marginTop: '20px' }}>
          <StatusMessage variant="success">
            ✅ Scenarios: {results.scenarioCount}
          </StatusMessage>
        </Box>
      )}

      {results.trackCount !== null && (
        <Box padding="small" style={{ marginTop: '20px' }}>
          <StatusMessage variant="success">
            ✅ Tracks: {results.trackCount}
          </StatusMessage>
        </Box>
      )}

      {results.error && (
        <Box padding="small" style={{ marginTop: '20px' }}>
          <StatusMessage variant="error">
            ❌ Error: {results.error}
          </StatusMessage>
        </Box>
      )}

      {results.rawResponse && (
        <Box padding="small" style={{ marginTop: '20px' }}>
          <Headline level={3}>Raw Response:</Headline>
          <pre style={{ background: '#f5f5f5', padding: '10px', overflow: 'auto' }}>
            {results.rawResponse}
          </pre>
        </Box>
      )}
    </Box>
  );
};

export default DiagnosticPage;
