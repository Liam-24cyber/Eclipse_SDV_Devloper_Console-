import { Link } from '../libs/apollo'
import { GET_SIMULATIONS } from "./queries";

// Results data processing
export const resultsRowData = (rawData: any) => {
  const processedData = rawData?.data?.simulationReadByQuery?.content?.map((item: any, index: number) => {
    const processedItem = {
      id: item.id || `sim-${index + 1}`, // Use index-based ID as fallback
      name: item.name || item.simulationName || 'Task Management', // Use actual name or fallback
      status: item.status || 'Done',
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
    // Fallback data if API returns nothing
    {
      id: 'sim-1',
      name: 'Task Management',
      status: 'Done',
      platform: 'Development',
      environment: 'Development',
      startDate: '10/21/2025, 12:51:56 PM',
      vehicles: 2,
      scenarios: 1,
      scenarioType: 'Vehicle Management',
      createdBy: 'john.doe@example.com',
      actions: 'sim-1'
    },
    {
      id: 'sim-2', 
      name: 'Task Management',
      status: 'Running',
      platform: 'Development',
      environment: 'Development',
      startDate: '10/21/2025, 12:37:08 PM',
      vehicles: 1,
      scenarios: 1,
      scenarioType: 'Over-The-Air Service',
      createdBy: 'sarah.smith@example.com',
      actions: 'sim-2'
    }
  ];
  
  return processedData;
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
