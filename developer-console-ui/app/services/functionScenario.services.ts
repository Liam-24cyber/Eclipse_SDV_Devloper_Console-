// Simplified scenario service with minimal functions to prevent build errors

// Mock data import
export const mockScenariosData = {
  data: {
    scenarioReadByQuery: {
      content: [
        {
          id: 'scen-001',
          name: 'Test Scenario 1',
          description: 'A basic test scenario',
          type: 'Behavioral',
          status: 'active',
          difficulty: 'Medium',
          duration: 300,
          vehicles: 8,
          environment: 'Highway',
          weather: 'Clear',
          createdDate: '2025-10-10T09:00:00Z',
          lastModified: '2025-10-15T14:30:00Z',
          tags: ['test', 'highway'],
          file: {
            name: 'test_scenario.txt',
            size: 1024,
            type: 'text/plain'
          }
        }
      ],
      pages: 1,
      total: 1,
      page: 1,
      size: 10
    }
  }
}

export const SCENARIOS_MOCK_CONFIG = {
  useRealAPI: false,
  mockDelay: 800
}

// Basic row data transformation
export const libRowData = (rawData: any) =>
  rawData?.data?.scenarioReadByQuery?.content?.map((item: any) => ({
    id: item.id,
    name: item.name,
    description: item.description,
    type: item.type,
    status: item.status,
    difficulty: item.difficulty,
    duration: item.duration,
    vehicles: item.vehicles,
    environment: item.environment,
    weather: item.weather,
    createdDate: item.createdDate,
    lastModified: item.lastModified,
    tags: item.tags,
    file: item.file
  })) || []

// Basic scenario data fetching function
export const getLibData = async (currentPage: number = 1, searchPattern: string = '') => {
  // Return mock data for now
  return Promise.resolve(mockScenariosData)
}

// File size validation function
export const getFileSIzeInService = (
  e: any,
  setUploadFile: Function,
  uploadFile: any,
  maxFileSizeInMB: number,
  minFileSizeInMB: number,
  setFileSizeError: Function,
  setFileNameError: Function
) => {
  const file = e.target.files[0]
  if (!file) return

  const fileSizeInMB = file.size / (1024 * 1024)
  const fileName = file.name
  const fileExtension = fileName.split('.').pop()?.toLowerCase()

  // Validate file extension
  if (!['txt', 'odx'].includes(fileExtension || '')) {
    setFileNameError(true)
    e.target.value = null
    setUploadFile(undefined)
    return
  }

  // Validate file size
  if (fileSizeInMB < minFileSizeInMB || fileSizeInMB > maxFileSizeInMB) {
    setFileSizeError(true)
    e.target.value = null
    setUploadFile(undefined)
    return
  }

  // File is valid
  setFileSizeError(false)
  setFileNameError(false)
  setUploadFile(file)
}

// Stub functions to prevent import errors
export const handleNewScenarioSubmitInService = () => Promise.resolve()
export const handleUpdateScenarioSubmitInService = () => Promise.resolve()
export const resetFormForScenario = () => {}
export const setToastMessageForNewScenario = () => {}
export const setToastMessageForUpdateScenario = () => {}
export const setToastMessageForDeleteScenario = () => {}
export const onClickScenario = () => {}
export const scenarioDataFromMap = () => ({})
export const uploadFileCondition = () => false
export const getUploadFormDataForNewScenario = () => new FormData()
export const getUploadFormDataForUpdateScenario = () => new FormData()
export const callUploadAxiosAPIForNewScenario = () => Promise.resolve()
export const callUploadAxiosAPIForUpdateScenario = () => Promise.resolve()
