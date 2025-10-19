import { gql } from '@apollo/client'
import axios from 'axios'
import { Link } from '../libs/apollo'
import { GET_SCENARIO } from './queries'
import { setTimeOutFunction } from './functionShared'
import { mockScenariosData, SCENARIOS_MOCK_CONFIG } from './mockData/scenarios.mock'

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
  if (searchval != '') {
    pageNo = 1
  }

  // Use comprehensive mock data
  if (SCENARIOS_MOCK_CONFIG.USE_MOCK_DATA) {
    // Simulate network delay
    if (SCENARIOS_MOCK_CONFIG.MOCK_DELAY > 0) {
      await new Promise(resolve => setTimeout(resolve, SCENARIOS_MOCK_CONFIG.MOCK_DELAY))
    }
    
    // Transform mock data to match expected format
    const transformedData = {
      data: {
        searchScenarioByPattern: {
          content: mockScenariosData.data.scenarioReadByQuery.content.map(scenario => ({
            id: scenario.id,
            name: scenario.name,
            type: scenario.type,
            description: scenario.description,
            createdBy: `${scenario.type.toLowerCase()}@dco.com`,
            lastModifiedAt: scenario.lastModified,
            file: {
              id: `file-${scenario.id}`,
              path: `/scenarios/${scenario.file.name}`,
              size: scenario.file.size,
              checksum: `hash-${scenario.id}`,
              updatedBy: `${scenario.type.toLowerCase()}@dco.com`,
              updatedOn: scenario.file.uploadDate
            }
          })),
          empty: false,
          first: pageNo === 1,
          last: pageNo >= mockScenariosData.data.scenarioReadByQuery.pages,
          page: pageNo - 1,
          size: 10,
          pages: mockScenariosData.data.scenarioReadByQuery.pages,
          elements: mockScenariosData.data.scenarioReadByQuery.content.length,
          total: mockScenariosData.data.scenarioReadByQuery.total
        }
      }
    }

    // Filter by search if provided
    let filteredContent = transformedData.data.searchScenarioByPattern.content
    if (searchval && searchval.trim() !== '') {
      filteredContent = filteredContent.filter(scenario =>
        scenario.name.toLowerCase().includes(searchval.toLowerCase()) ||
        scenario.type.toLowerCase().includes(searchval.toLowerCase()) ||
        scenario.description.toLowerCase().includes(searchval.toLowerCase())
      )
    }

  // Simulate pagination
  const startIndex = (pageNo - 1) * 10
  const endIndex = startIndex + 10
  const paginatedContent = filteredContent.slice(startIndex, endIndex)

  // Return mock data
  return Promise.resolve({
    data: {
      searchScenarioByPattern: {
        ...mockScenarioData.data.searchScenarioByPattern,
        content: paginatedContent,
        total: filteredContent.length,
        pages: Math.ceil(filteredContent.length / 10)
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
      query: GET_SCENARIO,
      variables: {
        scenarioPattern: searchval,
        page: pageNo - 1,
        size: 10,
      },
    }),
  })
    .then((res) => res.json())
    .then((result) => result)
    .catch((error) => {
      console.log('Error fetching data:::', error.message)
    })
  */
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
    let flag: any = callUploadAxiosAPIForNewScenario(values, sessionUser)
    flag.then((e: any) => {
      setFunctions.setUploadFile()
      setToastMessageForNewScenario(e.data, setFunctions.setToastMsg, onClose, setToastOpen, setFunctions)
      setToastOpen(true)
    })
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
    let flag: any = callUploadAxiosAPIForUpdateScenario(values, sessionUser)
    flag.then((e: any) => {
      setFunctions.setUploadFile()
      setToastMessageForUpdateScenario(e.data, setFunctions.setSuccessMsgScenario, onClose, setToastOpenScenario)
      setToastOpenScenario(true)
      router.replace(router.asPath)
    })
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
