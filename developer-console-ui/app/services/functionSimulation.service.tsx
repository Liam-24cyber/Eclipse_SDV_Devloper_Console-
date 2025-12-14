import router from "next/router";
import { Link } from '../libs/apollo'
import { ClearAllTypes, RawDataSimType } from "../types";
import { GET_SIMULATIONS } from "./queries";
import { createApprovalRequest } from "./approvals.service";
// Simulation data start****
export const simRowData = (rawData: RawDataSimType) =>
  rawData?.data?.simulationReadByQuery?.content?.map((item: any) => {
    return {
      id: item.id,
      name: item.name,
      status: item.status,
      numberVehicles: item.noOfVehicle,
      brand: item.brands,
      type: item.scenarioType,
      numberScenarios: item.noOfScenarios,
      platform: item.platform,
      env: item.environment,
      date: new Date(item.startDate).toLocaleDateString() +
        ', ' +
        new Date(item.startDate).toLocaleTimeString(),
      createdBy: item.createdBy || 'System'
    }
  })
export const getSimData = async (pageNo: number) => {
  // Mock simulation data
  const mockSimulationData = {
    data: {
      simulationReadByQuery: {
        content: [
          {
            id: 'sim-001',
            name: 'Highway Speed Test',
            status: 'completed',
            noOfVehicle: 5,
            brands: 'BMW, Audi',
            scenarioType: 'Performance',
            noOfScenarios: 3,
            platform: 'CARLA',
            environment: 'Highway',
            startDate: '2025-10-15T10:30:00Z',
            createdBy: 'john.doe@example.com'
          },
          {
            id: 'sim-002',
            name: 'Emergency Braking Test',
            status: 'completed',
            noOfVehicle: 3,
            brands: 'Mercedes, Tesla',
            scenarioType: 'Safety',
            noOfScenarios: 5,
            platform: 'SUMO',
            environment: 'Urban',
            startDate: '2025-10-14T14:20:00Z',
            createdBy: 'sarah.smith@example.com'
          },
          {
            id: 'sim-003',
            name: 'Weather Adaptation Test',
            status: 'running',
            noOfVehicle: 8,
            brands: 'Toyota, Honda',
            scenarioType: 'Environmental',
            noOfScenarios: 7,
            platform: 'AirSim',
            environment: 'Mixed',
            startDate: '2025-10-19T09:00:00Z',
            createdBy: 'mike.johnson@example.com'
          },
          {
            id: 'sim-004',
            name: 'Traffic Jam Simulation',
            status: 'completed',
            noOfVehicle: 25,
            brands: 'Ford, Chevrolet',
            scenarioType: 'Traffic',
            noOfScenarios: 4,
            platform: 'CARLA',
            environment: 'Urban',
            startDate: '2025-10-18T16:30:00Z',
            createdBy: 'anna.wilson@example.com'
          },
          {
            id: 'sim-005',
            name: 'Autonomous Parking Test',
            status: 'failed',
            noOfVehicle: 2,
            brands: 'Volvo, BMW',
            scenarioType: 'Autonomous',
            noOfScenarios: 6,
            platform: 'SUMO',
            environment: 'Parking Lot',
            startDate: '2025-10-17T11:45:00Z',
            createdBy: 'david.brown@example.com'
          },
          {
            id: 'sim-006',
            name: 'Lane Change Validation',
            status: 'completed',
            noOfVehicle: 12,
            brands: 'Audi, Mercedes',
            scenarioType: 'Behavioral',
            noOfScenarios: 8,
            platform: 'AirSim',
            environment: 'Highway',
            startDate: '2025-10-16T13:15:00Z',
            createdBy: 'lisa.garcia@example.com'
          }
        ],
        pages: 3,
        total: 18,
        page: pageNo - 1,
        size: 10
      }
    }
  }

  // Simulate pagination
  const startIndex = (pageNo - 1) * 10
  const endIndex = startIndex + 10
  const paginatedContent = mockSimulationData.data.simulationReadByQuery.content.slice(startIndex, endIndex)

  // Return mock data with pagination
  return Promise.resolve({
    data: {
      simulationReadByQuery: {
        ...mockSimulationData.data.simulationReadByQuery,
        content: paginatedContent
      }
    }
  })

  // Original code commented out for now
  /*
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
        sort: "DESC"
      },
    }),
  })
    .then((res) => res.json())
    .then((result) => result).catch(error => {
      console.log("Error fetching data:::", error.message);
    })
  */
}
//  Simulation data end****

export function prepareSimulationInput(variable: any, setVariable: any) {
  const selectedScenarios = variable.scenario
    .filter((c: any) => c.checked)
    .map((l: any) => l.id)
    .filter((id: string) => {
      if (!isValidUUID(id)) {
        console.error(`Invalid scenario UUID: ${id}`);
        return false;
      }
      return true;
    });

  const selectedTracks = variable.track
    .filter((c: any) => c.checked)
    .map((l: any) => l.id)
    .filter((id: string) => {
      if (!isValidUUID(id)) {
        console.error(`Invalid track UUID: ${id}`);
        return false;
      }
      return true;
    });

  if (selectedScenarios.length === 0) {
    setVariable.setScenarioError(true);
  }

  if (selectedTracks.length === 0) {
    setVariable.setTrackError(true);
  }

  if (!(variable.title && variable.scenarioType && selectedScenarios.length !== 0 && selectedTracks.length !== 0)) {
    setVariable.setTitleError(!variable.title);
    setVariable.setSTypeError(!variable.scenarioType);
    return null;
  }

  return {
    name: variable.title,
    environment: variable.environment,
    description: variable.description,
    platform: variable.platform,
    scenarioType: variable.scenarioType,
    hardware: variable.hardware,
    tracks: selectedTracks,
    scenarios: selectedScenarios,
    createdBy: localStorage.getItem('user') || 'unknown',
  };
}

export function launchSimulation(preparedInput: any, createSimulation: Function) {
  if (!preparedInput) return;

  createSimulation({
    variables: {
      simulationInput: {
        name: preparedInput.name,
        environment: preparedInput.environment,
        description: preparedInput.description,
        platform: preparedInput.platform,
        scenarioType: preparedInput.scenarioType,
        hardware: preparedInput.hardware,
        tracks: preparedInput.tracks,
        scenarios: preparedInput.scenarios,
        createdBy: preparedInput.createdBy || "abc@t-systems.com",
      },
    },
  })
}

export function requestSimulationApproval(preparedInput: any) {
  if (!preparedInput) return;
  createApprovalRequest({
    ...preparedInput,
    requiredCompute: 'NULL',
    status: 'pending',
  });
}
export function onLaunchedSimulation(setSelectedscenario: Function, setSelectedtrack: Function, setIsToastOpen: Function, setToastMsg: Function, res: any, flag: boolean) {
  if (flag) {
    setIsToastOpen(true)
    setToastMsg('Simulation has been launched successfully')
    setTimeout(() => {
      router.push('/dco/simulation')
      setSelectedscenario([{ id: '93b866de-a642-4543-886c-a3597dbe9d8f', checked: false }])
      setSelectedtrack([{ id: 'a633a44b-0df6-43c5-9250-aaca94191054', checked: false }])
    }, 2500)

  } else {
    setIsToastOpen(true)
    setToastMsg(JSON.parse(JSON.stringify(res)).message)
    setTimeout(() => {
      router.push('/dco/simulation')
      setSelectedscenario([{ id: '93b866de-a642-4543-886c-a3597dbe9d8f', checked: false }])
      setSelectedtrack([{ id: 'a633a44b-0df6-43c5-9250-aaca94191054', checked: false }])
    }, 3000)
  }
}
export function clearAll(setVariable: ClearAllTypes) {
  setVariable.setTitle('');
  setVariable.setDescription('');
  setVariable.setEnvironment('');
  setVariable.setPlatform('');
  setVariable.setSelectedscenario([{ id: '93b866de-a642-4543-886c-a3597dbe9d8f', checked: false }]);
  setVariable.setSelectedtrack([{ id: 'a633a44b-0df6-43c5-9250-aaca94191054', checked: false }]);
  setVariable.setScenarioType('');
  setVariable.setHardware('');
  setVariable.setSearchval('');
  setVariable.setTitleError(false);
  setVariable.setSTypeError(false);
  setVariable.setTrackError(false);
  setVariable.setScenarioError(false);
}
export function onClickNewSimulation() {
  setTimeout(() => {
    router.push('/dco/addSimulation')
  }, 0)
}

// UUID validation function
const isValidUUID = (id: string): boolean => {
  const uuidRegex = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
  return uuidRegex.test(id);
};
