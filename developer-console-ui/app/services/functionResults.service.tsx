import { Link } from '../libs/apollo'
import { GET_SIMULATIONS } from "./queries";

// Results data processing
export const resultsRowData = (rawData: any) =>
  rawData?.data?.simulationReadByQuery?.content?.map((item: any) => {
    return {
      id: item.id,
      simulationName: item.name,
      status: item.status,
      platform: item.platform,
      environment: item.environment,
      startDate: item.startDate ? new Date(item.startDate).toLocaleDateString() + 
        ', ' + new Date(item.startDate).toLocaleTimeString() : 'N/A',
      vehicles: item.noOfVehicle || 0,
      scenarios: item.noOfScenarios || 0,
      scenarioType: item.scenarioType
    }
  })

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
