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
export const libRowData = (rawData: any) => {
  const content = rawData?.data?.searchScenarioByPattern?.content || []
  return content.map((item: any) => {
    // If either date is missing or empty, treat both as invalid
    const hasValidDates = item.createdAt && item.lastModifiedAt
    const createdDate = hasValidDates ? new Date(item.createdAt) : new Date('invalid')
    const modifiedDate = hasValidDates ? new Date(item.lastModifiedAt) : new Date('invalid')
    
    return {
      check: '',
      sid: item.id,
      scenario: item.name,
      type: item.type,
      filename: item.file?.path?.split('/').pop(),
      lastUpdated: `${createdDate.toLocaleDateString()}, ${modifiedDate.toLocaleDateString()}`,
      createdBy: item.createdBy,
      description: item.description,
      menu: ''
    }
  })
}

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

// Scenario submission handlers
export const handleNewScenarioSubmitInService = (
  formData: any,
  email: string,
  setFunctions: any,
  callback1: Function,
  callback2: Function
) => {
  // Validate required fields
  if (!formData.name || !formData.type || !formData.selectedUploadFile) {
    if (!formData.name) setFunctions.setNameError?.(true)
    if (!formData.type) setFunctions.setTypeError?.(true)
    if (!formData.selectedUploadFile) setFunctions.setFileError?.(true)
    return undefined
  }

  // Reset errors
  setFunctions.setNameError?.(false)
  setFunctions.setTypeError?.(false)
  setFunctions.setFileError?.(false)

  // Call callbacks if provided
  callback1?.()
  callback2?.()

  return undefined
}

export const handleUpdateScenarioSubmitInService = (
  formData: any,
  email: string,
  setFunctions: any,
  callback1: Function,
  callback2: Function,
  router: any
) => {
  // Validate required fields
  if (!formData.name || !formData.type || !formData.selectedUploadFile) {
    if (!formData.name) setFunctions.setNameError?.(true)
    if (!formData.type) setFunctions.setTypeError?.(true)
    if (!formData.selectedUploadFile) setFunctions.setFileError?.(true)
    return undefined
  }

  // Reset errors
  setFunctions.setNameError?.(false)
  setFunctions.setTypeError?.(false)
  setFunctions.setFileError?.(false)

  // Call callbacks if provided
  callback1?.()
  callback2?.()

  return undefined
}

export const resetFormForScenario = (reset: any) => {
  reset.setName?.('')
  reset.setType?.('')
  reset.setDescription?.('')
  reset.setFileSizeError?.(false)
  reset.setFileNameError?.(false)
  return undefined
}

export const setToastMessageForNewScenario = (
  data: any,
  setToastMsg: Function,
  callback: Function,
  setToastOpen: Function,
  setFunctions: any
) => {
  if (data.errors && data.errors.length > 0) {
    setToastMsg(data.errors[0].message)
    setToastOpen(true)
    setTimeout(() => {
      setToastOpen(false)
    }, 3000)
    return true
  }

  if (data.data && data.data.createScenario) {
    setToastMsg('Scenario created successfully')
    setToastOpen(true)
    // Reset form
    setFunctions.setName?.('')
    setFunctions.setType?.('')
    setFunctions.setDescription?.('')
    setFunctions.setUploadFile?.(undefined)
    setTimeout(() => {
      setToastOpen(false)
      callback?.()
    }, 2000)
    return false
  }

  return false
}

export const setToastMessageForUpdateScenario = (
  data: any,
  setSuccessMsgScenario: Function,
  callback: Function,
  setToastOpenScenario: Function
) => {
  if (data.data && data.data.updateScenario) {
    setSuccessMsgScenario('Scenario updated successfully')
    setToastOpenScenario(true)
    setTimeout(() => {
      setToastOpenScenario(false)
      callback?.()
    }, 2000)
    return false
  }

  return false
}

export const setToastMessageForDeleteScenario = (
  data: any,
  setToastMsg: Function,
  setToastOpen: Function,
  type: string
) => {
  setToastOpen(true)
  setTimeout(() => {
    setToastOpen(false)
  }, 2000)

  if (type === 'success') {
    setToastMsg('Scenario has been deleted successfully')
    setTimeout(() => {
      window.location.reload()
    }, 2000)
    return true
  } else {
    setToastMsg(data.message || 'Failed to delete scenario')
    return false
  }
}

export const onClickScenario = (id: string, setFunction1: Function, setFunction2: Function) => {
  setFunction1?.(id)
  setFunction2?.(true)
  return undefined
}

export const scenarioDataFromMap = (result: any) => {
  const content = result?.data?.searchScenarioByPattern?.content || []
  return content.map((item: any) => {
    // If either date is missing or empty, treat both as invalid
    const hasValidDates = item.createdAt && item.lastModifiedAt
    const createdDate = hasValidDates ? new Date(item.createdAt) : new Date('invalid')
    const modifiedDate = hasValidDates ? new Date(item.lastModifiedAt) : new Date('invalid')
    
    return {
      sid: item.id,
      scenario: item.name,
      description: item.description,
      type: item.type,
      filename: undefined,
      lastUpdated: `${createdDate.toLocaleDateString()}, ${modifiedDate.toLocaleDateString()}`,
      createdBy: item.createdBy,
      delete: ''
    }
  })
}

export const uploadFileCondition = (
  nameError: number,
  typeError: number,
  maxFileSize: number,
  minFileSize: number,
  setUploadFile: Function,
  event: any,
  uploadFile: any
) => {
  if (nameError === 0 && typeError === 0) {
    return true
  }
  return false
}

export const getUploadFormDataForNewScenario = (
  name: string,
  type: string,
  description: string,
  file: any,
  email: string
) => {
  const formData = new FormData()
  formData.append('name', name)
  formData.append('type', type)
  formData.append('description', description)
  if (file) {
    formData.append('file', file)
  }
  formData.append('email', email)
  return formData
}

export const getUploadFormDataForUpdateScenario = (
  id: string,
  name: string,
  type: string,
  fileName: string,
  description: string,
  email: string
) => {
  const formData = new FormData()
  formData.append('id', id)
  formData.append('name', name)
  formData.append('type', type)
  formData.append('fileName', fileName)
  formData.append('description', description)
  formData.append('email', email)
  return formData
}

export const callUploadAxiosAPIForNewScenario = async (formData: any, email: string) => {
  try {
    // Mock implementation - in real scenario this would call the API
    return Promise.resolve({ data: { createScenario: 'success' } })
  } catch (error) {
    return { data: { errors: [{ message: 'Failed to create scenario' }] } }
  }
}

export const callUploadAxiosAPIForUpdateScenario = async (formData: any, id: string) => {
  try {
    // Mock implementation - in real scenario this would call the API
    return Promise.resolve({ data: { updateScenario: 'success' } })
  } catch (error) {
    return []
  }
}
