import { Link } from '../libs/apollo'
import { GET_SIMULATIONS } from "./queries";

// Results data processing
export const resultsRowData = (rawData: any) => {
  console.log('🔍 Raw data structure:', JSON.stringify(rawData, null, 2));
  
  return rawData?.data?.simulationReadByQuery?.content?.map((item: any) => {
    console.log('🔍 Processing item:', JSON.stringify(item, null, 2));
    
    const result = {
      id: item.id,
      name: item.name || item.simulationName || 'Unnamed Simulation', // Handle different possible field names
      status: item.status || 'Unknown',
      platform: item.platform || 'Unknown Platform',
      environment: item.environment || 'Unknown Environment',
      startDate: item.startDate ? new Date(item.startDate).toLocaleDateString() + 
        ', ' + new Date(item.startDate).toLocaleTimeString() : 'N/A',
      vehicles: item.noOfVehicle || 0,
      scenarios: item.noOfScenarios || 0,
      scenarioType: item.scenarioType || item.scenario || 'Unknown Scenario',
      createdBy: item.createdBy || item.creator || item.owner || 'developer@example.com', // Handle different possible field names with fallback
      actions: item.id // This will be used by the Actions column formatter
    };
    
    console.log('✅ Processed result:', JSON.stringify(result, null, 2));
    return result;
  })
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
    .catch((error) => {
      console.error('Error fetching results data:', error);
      throw error;
    });
}
