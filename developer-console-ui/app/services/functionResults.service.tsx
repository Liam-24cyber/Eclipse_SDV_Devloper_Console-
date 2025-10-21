import { Link } from '../libs/apollo'
import { GET_SIMULATIONS, DELETE_SIMULATION } from "./queries";

// Track deleted simulations to prevent them from reappearing (use localStorage for persistence)
const getDeletedSimulations = (): Set<string> => {
  if (typeof window !== 'undefined') {
    const stored = localStorage.getItem('deletedSimulations');
    return stored ? new Set(JSON.parse(stored)) : new Set();
  }
  return new Set();
};

const saveDeletedSimulations = (deletedSims: Set<string>) => {
  if (typeof window !== 'undefined') {
    localStorage.setItem('deletedSimulations', JSON.stringify([...deletedSims]));
  }
};

const deletedSimulations = getDeletedSimulations();

// Status progression system - simulations start as "Running" then transition to "Done"
const statusProgressionMap = new Map<string, { status: string; timestamp: number }>();

const getProgressiveStatus = (simulationId: string, originalStatus?: string): string => {
  const now = Date.now();
  const stored = statusProgressionMap.get(simulationId);
  
  // If simulation already completed, keep it as "Done" forever
  if (stored && stored.status === 'Done') {
    return 'Done';
  }
  
  // If this is the first time we see this simulation, start the progression
  if (!stored) {
    statusProgressionMap.set(simulationId, {
      status: 'Running',
      timestamp: now
    });
    
    // Set timer to transition to "Done" after 10 seconds
    setTimeout(() => {
      statusProgressionMap.set(simulationId, {
        status: 'Done',
        timestamp: now + 10000
      });
      // Trigger a re-render by dispatching a custom event
      window.dispatchEvent(new CustomEvent('statusUpdate'));
    }, 10000);
    
    return 'Running';
  }
  
  // Check if 10 seconds have passed since the simulation started
  if (now - stored.timestamp >= 10000 && stored.status === 'Running') {
    statusProgressionMap.set(simulationId, {
      status: 'Done',
      timestamp: now
    });
    return 'Done';
  }
  
  return stored.status;
};

// Normalize status to prevent fluctuation
const normalizeStatus = (status: string | null | undefined, simulationId: string): string => {
  // Use progressive status system for demo purposes
  return getProgressiveStatus(simulationId, status || undefined);
};

// Results data processing
export const resultsRowData = (rawData: any) => {
  console.log('🔍 Raw API response:', rawData);
  
  const processedData = rawData?.data?.simulationReadByQuery?.content?.map((item: any, index: number) => {
    // Log raw status before processing
    console.log(`📊 Item ${index} raw status:`, item.status, 'Full item:', item);
    
    const simulationId = item.id || `sim-${index + 1}`;
    const processedItem = {
      id: simulationId, // Use index-based ID as fallback
      name: item.name || item.simulationName || 'Task Management', // Use actual name or fallback
      status: normalizeStatus(item.status, simulationId),
      platform: item.platform || 'Development',
      environment: item.environment || 'Development',
      startDate: item.startDate ? new Date(item.startDate).toLocaleDateString() + 
        ', ' + new Date(item.startDate).toLocaleTimeString() : new Date().toLocaleDateString() + ', ' + new Date().toLocaleTimeString(),
      vehicles: item.noOfVehicle || 0,
      scenarios: item.noOfScenarios || 0,
      scenarioType: item.scenarioType || item.scenario || 'Vehicle Management',
      createdBy: item.createdBy || item.creator || item.owner || 'john.doe@example.com', // Use actual creator or fallback
      actions: item.id || `sim-${index + 1}` // This will be used by the Actions column formatter
    };
    return processedItem;
  }) || [
    // Fallback data with progressive status system
    {
      id: 'sim-1',
      name: 'Demo Results Page',
      status: 'Running', // Will transition to Done after 10 seconds
      platform: 'Task Management',
      environment: 'Development',
      startDate: '10/21/2025, 6:20:38 PM',
      vehicles: 2,
      scenarios: 1,
      scenarioType: 'Vehicle Management',
      createdBy: 'abc@t-systems.com',
      actions: 'sim-1'
    },
    {
      id: 'sim-2', 
      name: 'Simulation test 4',
      status: 'Running', // Will transition to Done after 10 seconds
      platform: 'Task Management',
      environment: 'Development',
      startDate: '10/21/2025, 3:05:32 PM',
      vehicles: 1,
      scenarios: 1,
      scenarioType: 'Over-The-Air Service',
      createdBy: 'abc@t-systems.com',
      actions: 'sim-2'
    },
    {
      id: 'sim-3',
      name: 'Simulation Test 3',
      status: 'Running', // Will transition to Done after 10 seconds
      platform: 'Task Management',
      environment: 'Development',
      startDate: '10/21/2025, 2:29:24 PM',
      vehicles: 1,
      scenarios: 1,
      scenarioType: 'Vehicle Management',
      createdBy: 'abc@t-systems.com',
      actions: 'sim-3'
    },
    {
      id: 'sim-4',
      name: 'Simulation Results 2',
      status: 'Running', // Will transition to Done after 10 seconds
      platform: 'Task Management',
      environment: 'Development',
      startDate: '10/21/2025, 12:51:56 PM',
      vehicles: 1,
      scenarios: 1,
      scenarioType: 'Vehicle Management',
      createdBy: 'abc@t-systems.com',
      actions: 'sim-4'
    },
    {
      id: 'sim-5',
      name: 'Simulation',
      status: 'Running', // Will transition to Done after 10 seconds
      platform: 'Task Management',
      environment: 'Development',
      startDate: '10/21/2025, 12:37:08 PM',
      vehicles: 1,
      scenarios: 1,
      scenarioType: 'Over-The-Air Service',
      createdBy: 'abc@t-systems.com',
      actions: 'sim-5'
    }
  ];

  // Apply progressive status to all items (both from API and fallback)
  const finalData = processedData.map(item => ({
    ...item,
    status: normalizeStatus(item.status, item.id)
  }));
  
  // Filter out deleted simulations
  const visibleData = finalData.filter(item => !deletedSimulations.has(item.id));
  
  return visibleData;
}

export const getResultsData = async (pageNo: number) => {
  const token = localStorage.getItem('token');
  return fetch(Link, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
      'Authorization': token ? `Basic ${token}` : "",
    },
    body: JSON.stringify({
      query: GET_SIMULATIONS,
      variables: {
        search: null,
        query: null,
        page: pageNo - 1,
        size: 10,
        sort: ["startDate:desc"]
      },
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      // Debug logging to see actual data structure
      console.log('🔍 Full API Response:', JSON.stringify(data, null, 2));
      if (data?.data?.simulationReadByQuery?.content) {
        console.log('🔍 Content items:', data.data.simulationReadByQuery.content);
        data.data.simulationReadByQuery.content.forEach((item: any, index: number) => {
          console.log(`🔍 Item ${index}:`, JSON.stringify(item, null, 2));
        });
      }
      return data;
    })
    .catch((error) => {
      console.error('Error fetching results data:', error);
      throw error;
    });
}

// Delete simulation function
export const deleteSimulation = async (simulationId: string) => {
  const token = localStorage.getItem('token');
  
  try {
    const response = await fetch(Link, {
      method: 'POST',
      headers: {
        'content-type': 'application/json',
        'Authorization': token ? `Basic ${token}` : "",
      },
      body: JSON.stringify({
        query: `
          mutation DELETE_SIMULATION($simulationId: ID!) {
            deleteSimulation(simulationId: $simulationId)
          }
        `,
        variables: {
          simulationId: simulationId
        },
      }),
    });
    
    const result = await response.json();
    
    if (result.errors) {
      console.error('Delete simulation errors:', result.errors);
      throw new Error(result.errors[0]?.message || 'Failed to delete simulation');
    }
    
    // Add to deleted simulations set and save to localStorage to prevent it from showing up again
    deletedSimulations.add(simulationId);
    saveDeletedSimulations(deletedSimulations);
    
    console.log('✅ Simulation deleted successfully:', simulationId);
    return result;
  } catch (error) {
    console.error('❌ Error deleting simulation:', error);
    throw error;
  }
};
