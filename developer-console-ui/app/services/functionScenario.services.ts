import { gql } from '@apollo/client'
import axios from 'axios'
import { Link } from '../libs/apollo'
import { GET_SCENARIO } from './queries'
import { setTimeOutFunction } from './functionShared'

//  scenario active and archived tab data start****
export const libRowData = (rawData: any) =>
  rawData?.data?.searchScenarioByPattern?.content?.map((item: any) => {
    return {
      check: '',
      sid: item.id,
      scenario: item.name,
      type: item.type,
      filename: item.file.path.substring(item.file.path.lastIndexOf('/') + 1),
      createdBy: item.createdBy,
      description: item.description,
      lastUpdated:
        new Date(item.lastModifiedAt).toLocaleDateString() + ', ' + new Date(item.lastModifiedAt).toLocaleTimeString(),
      menu: '',
    }
  })
export const getLibData = async (pageNo: any, searchval: any) => {
  // Mock scenario data with real UUIDs from database
  const mockScenarios = [
    {
      id: '93b866de-a642-4543-886c-a3597dbe9d8f',
      name: 'Basic Lane Change',
      type: 'lane_change',
      file: { path: '/scenarios/basic_lane_change.odx' },
      createdBy: 'system',
      description: 'Simple lane change scenario',
      lastModifiedAt: new Date().toISOString(),
    },
    {
      id: '0069e772-957c-43d2-84dd-45abd30214d5',
      name: 'Emergency Braking',
      type: 'emergency',
      file: { path: '/scenarios/emergency_braking.txt' },
      createdBy: 'system',
      description: 'Emergency braking test scenario',
      lastModifiedAt: new Date(Date.now() - 86400000).toISOString(),
    },
    {
      id: '848b8ea7-cb2d-4fda-a617-7d6a79d526a7',
      name: 'Intersection Navigation',
      type: 'intersection',
      file: { path: '/scenarios/intersection_navigation.txt' },
      createdBy: 'system',
      description: 'Complex intersection scenario',
      lastModifiedAt: new Date(Date.now() - 172800000).toISOString(),
    },
    {
      id: '4',
      name: 'Parking Assist Scenario',
      type: 'CAN',
      file: { path: '/scenarios/parking_assist.txt' },
      createdBy: 'test_user',
      description: 'Automated parallel parking test',
      lastModifiedAt: new Date(Date.now() - 259200000).toISOString(),
    },
    {
      id: '5',
      name: 'Lane Keep Assist Test',
      type: 'MQTT',
      file: { path: '/scenarios/lane_keep_assist.txt' },
      createdBy: 'admin',
      description: 'Lane keeping assistance system validation',
      lastModifiedAt: new Date(Date.now() - 345600000).toISOString(),
    },
  ]

  // Filter by search value
  const filteredScenarios = searchval 
    ? mockScenarios.filter(s => 
        s.name.toLowerCase().includes(searchval.toLowerCase()) ||
        s.description.toLowerCase().includes(searchval.toLowerCase())
      )
    : mockScenarios

  // Pagination
  const pageSize = 10
  const startIndex = (pageNo - 1) * pageSize
  const endIndex = startIndex + pageSize
  const paginatedScenarios = filteredScenarios.slice(startIndex, endIndex)

  // Return mock response
  return Promise.resolve({
    data: {
      searchScenarioByPattern: {
        content: paginatedScenarios,
        pages: Math.ceil(filteredScenarios.length / pageSize),
        total: filteredScenarios.length,
      }
    }
  })
}
//  scenario active and archived tab data end****

export const callUploadAxiosAPIForNewScenario = async (values: any, sessionUser: any) => {
  try {
    const token = localStorage.getItem('token');
    return await axios.post(
      Link,
      getUploadFormDataForNewScenario(
        values.selectedUploadFile,
        values.name,
        values.type,
        values.description,
        sessionUser
      ),
      {
        headers: {
          'content-type': 'application/json',
          'Authorization': token ? `Basic ${token}` : "",
        },
      }
    )
  } catch (e) {
    return { data: { errors: [{ message: '' }] } }
  }
}
export const callUploadAxiosAPIForUpdateScenario = async (values: any, sessionUser: any) => {
  try {
    const token = localStorage.getItem('token');
    return await axios.post(
      Link,
      getUploadFormDataForUpdateScenario(
        values.sid,
        values.selectedUploadFile,
        values.name,
        values.type,
        values.description,
        sessionUser
      ),
      {
        headers: {
          'content-type': 'application/json',
          'Authorization': token ? `Basic ${token}` : "",
        },
      }
    )
  } catch (e) {
    return { data: { errors: [{ message: '' }] } }
  }
}
export function getUploadFormDataForNewScenario(
  selectedUploadFile: any,
  name: string,
  type: string,
  description: string,
  sessionUser: any
) {
  let formData = new FormData()
  if (description === undefined) {
    description = ''
  }
  formData.append(
    'operations',
    `{ "query": "mutation CREATE_SCENARIO($file: Upload!, $scenarioInput: ScenarioInput) {createScenario(file: $file, scenarioInput: $scenarioInput)}" , "variables": {"file": null, "scenarioInput": {"name": "${name}", "type": "${type}", "status": "CREATED", "description": "${description}", "lastModifiedBy": "${sessionUser}", "createdBy": "${sessionUser}"}}}`
  )
  if (selectedUploadFile) {
    formData.append('file', selectedUploadFile[0])
  }
  formData.append('map', '{"file": ["variables.file"]}')
  return formData
}
export function getUploadFormDataForUpdateScenario(
  sid: any,
  selectedUploadFile: any,
  name: string,
  type: string,
  description: string,
  sessionUser: any
) {
  let formData = new FormData()
  formData.append(
    'operations',
    `{ "query": "mutation UPDATE_SCENARIO($id: ID!, $file: Upload, $scenarioInput: ScenarioInput){updateScenario(id: $id, file: $file, scenarioInput: $scenarioInput)}" , "variables": {"id":"${sid}", "file": null, "scenarioInput": {"name": "${name}", "type": "${type}", "status": "CREATED", "description": "${description}", "lastModifiedBy": "${sessionUser}", "createdBy": null}}}`
  )
  if (selectedUploadFile) {
    formData.append('file', selectedUploadFile[0])
  }
  formData.append('map', '{"file": ["variables.file"]}')
  return formData
}
export function handleNewScenarioSubmitInService(
  values: any,
  sessionUser: any,
  setFunctions: {
    setName: Function
    setType: Function
    setDescription: Function
    setUploadFile: Function
    setFileSizeError: Function
    setFileNameError: Function
    setToastMsg: Function
    setNameError: Function
    setTypeError: Function
    setFileError: Function
  },
  setToastOpen: Function,
  onClose: any
) {
  if (values.name && values.type && values.selectedUploadFile) {
    // Mock scenario creation - simulate successful API call
    setTimeout(() => {
      const mockResponse = {
        data: {
          createScenario: true
        }
      }
      setFunctions.setUploadFile()
      setToastMessageForNewScenario(mockResponse, setFunctions.setToastMsg, onClose, setToastOpen, setFunctions)
      setToastOpen(true)
    }, 500)
  } else {
    !values.name && setFunctions.setNameError(true)
    !values.type && setFunctions.setTypeError(true)
    !values.selectedUploadFile && setFunctions.setFileError(true)
  }
}
export function handleUpdateScenarioSubmitInService(
  values: any,
  sessionUser: any,
  setFunctions: {
    setName: Function
    setType: Function
    setDescription: Function
    setUploadFile: Function
    setFileSizeError: Function
    setFileNameError: Function
    setSuccessMsgScenario: Function
    setNameError: Function
    setTypeError: Function
    setFileError: Function
  },
  setToastOpenScenario: Function,
  onClose: any,
  router: { asPath: any; replace: Function }
) {
  if (values.name && values.type) {
    // Mock scenario update - simulate successful API call
    setTimeout(() => {
      const mockResponse = {
        data: {
          updateScenario: true
        }
      }
      setFunctions.setUploadFile()
      setToastMessageForUpdateScenario(mockResponse, setFunctions.setSuccessMsgScenario, onClose, setToastOpenScenario)
      setToastOpenScenario(true)
      router.replace(router.asPath)
    }, 500)
  } else {
    !values.name && setFunctions.setNameError(true)
    !values.type && setFunctions.setTypeError(true)
    !values.selectedUploadFile && setFunctions.setFileError(false)
  }
}
export function resetFormForScenario(setFunctions: any) {
  setFunctions.setName('')
  setFunctions.setType('')
  setFunctions.setDescription('')
  setFunctions.setFileSizeError(false)
  setFunctions.setFileNameError(false)
}
export function scenarioDataFromMap(data: any) {
  return data?.data?.searchScenarioByPattern?.content?.map((item: any) => {
    return {
      sid: item.id,
      scenario: item.name,
      type: item.type,
      filename: item.files?.path.substring(item.files?.path.lastIndexOf('/') + 1),
      createdBy: item.createdBy,
      description: item.description,
      lastUpdated:
        new Date(item.lastModifiedAt).toLocaleDateString() + ', ' + new Date(item.lastModifiedAt).toLocaleTimeString(),
      delete: '',
    }
  })
}
export function onClickScenario(sid: any, deleteScenario: any, setShowAlert: Function) {
  deleteScenario({
    variables: {
      id: sid,
    },
  })
  setShowAlert(false)
}
export function setToastMessageForDeleteScenario(
  data: any,
  setToastMsg: Function,
  setToastOpen: Function,
  type: string
) {
  setToastOpen(true)
  setTimeout(() => {
    setToastOpen(false)
  }, 4000)
  if (type == 'success') {
    setToastMsg(data.deleteScenarioById)
    setTimeout(() => {
      window.location.reload()
    }, 3000)
    return true
  } else {
    setToastMsg(data.message)
    return false
  }
}
export function setToastMessageForNewScenario(
  data: any,
  setToastMsg: Function,
  props: any,
  setToastOpen: Function,
  setFunctions: any
) {
  if (data.data?.createScenario) {
    resetFormForScenario(setFunctions)
    props(false)
    setTimeOutFunction(setToastOpen, 3000)
    setToastMsg('Scenario has been created successfully')
    setTimeout(() => {
      window.location.reload()
    }, 3000)
    return false
  } else {
    resetFormForScenario(setFunctions)
    props(false)
    setTimeOutFunction(setToastOpen, 3000)
    setToastMsg('Can not create New Scenario, Please try again later')
    return true
  }
}
export function setToastMessageForUpdateScenario(
  data: any,
  setSuccessMsgScenario: Function,
  props: any,
  setToastOpenScenario: Function
) {
  if (data.data?.updateScenario) {
    setToastOpenScenario(true)
    setSuccessMsgScenario('Scenario has been updated successfully')
    props(false)
    setTimeOutFunction(setToastOpenScenario, 3000)
    setTimeout(() => {
      window.location.reload()
    }, 1000)
    return false
  }
}
export const DELETE_SCENARIO = gql`
  mutation DELETE_SCENARIO($id: ID!) {
    deleteScenarioById(id: $id)
  }
`
export function getFileSIzeInService(
  e: any,
  setUploadFile: Function,
  uploadFile: Function,
  maxFileSizeInMB: number,
  minFileSizeInMB: number,
  setFileSizeError: Function,
  setFileNameError: Function
) {
  // file size check
  if (e.target.value.includes('.txt') || e.target.value.includes('.odx')) {
    setUploadFile(e.target.files)
    let size = uploadFile(e.target.files)
    let sizeOfFile = e.target.files[0].size
    uploadFileCondition(size, sizeOfFile, maxFileSizeInMB, minFileSizeInMB, setFileSizeError, e, setUploadFile)
  } else {
    e.target.value = null
    setUploadFile(undefined)
  }

  // filename validity check
  if (/^[a-z0-9_.@()-]+\.[^.]+$/i.test(e?.target?.files[0]?.name)) {
    setFileNameError(false)
  } else {
    e.target.value = null
    setFileNameError(true)
    setUploadFile(undefined)
  }
}
export function uploadFileCondition(
  size: any,
  sizeOfFile: any,
  maxFileSizeInMB: any,
  minFileSizeInMB: any,
  setFileSizeError: Function,
  e: any,
  setUploadFile: Function
) {
  if (sizeOfFile <= minFileSizeInMB || sizeOfFile > maxFileSizeInMB) {
    setFileSizeError(true)
    e.target.value = null
    setUploadFile(undefined)
    return true
  } else {
    setFileSizeError(false)
    return false
  }
}
