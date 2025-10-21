import { Box, Table } from '@dco/sdv-ui'
import { useStoreActions } from 'easy-peasy'
import { useEffect, useState } from 'react'
import { useRouter } from 'next/router'
import Dco from '..'
import { getResultsData, resultsRowData } from '../../../services/functionResults.service'
import CounterWithToolTip from '../../shared/counterWithToolTip'
import Pagination from '../../shared/paginationTable'
import Status from '../../shared/status'

// Results table showing all simulation results
const Results = () => {
  const setCount = useStoreActions((actions: any) => actions.setCount)
  const router = useRouter()
  const [currentPage, setCurrentPage] = useState(1)
  const [pageData, setPageData] = useState({
    rowData: [],
    isLoading: false,
    totalPages: 0,
    totalResults: 0,
  })

  useEffect(() => {
    console.log('🚀 Results page useEffect triggered, currentPage:', currentPage)
    setPageData((prevState) => ({
      ...prevState,
      rowData: [],
      isLoading: true,
    }))
    
    console.log('📞 About to call getResultsData...')
    getResultsData(currentPage).then((info) => {
      console.log('✅ getResultsData response:', info)
      setPageData({
        isLoading: false,
        rowData: resultsRowData(info) as any,
        totalPages: info?.data?.simulationReadByQuery?.pages,
        totalResults: info?.data?.simulationReadByQuery?.total,
      })
      setCount(info?.data?.simulationReadByQuery?.total || 0);
    }).catch((error) => {
      console.error('❌ Error loading results:', error);
      setPageData((prevState) => ({
        ...prevState,
        isLoading: false,
      }))
    })
  }, [currentPage, setCount])

  const handleViewResults = (simulationId: string) => {
    router.push(`/dco/simulation/results/${simulationId}`)
  }

  const handleViewSimulation = (simulationId: string) => {
    router.push(`/dco/simulation/${simulationId}`)
  }

  const columns = [
    {
      Header: 'Simulation Name',
      accessor: 'name',
    },
    {
      Header: 'Platform',
      accessor: 'platform',
    },
    {
      Header: 'Environment',
      accessor: 'environment',
    },
    {
      Header: 'Scenario Type',
      accessor: 'scenarioType',
    },
    {
      Header: 'Status',
      accessor: 'status',
      formatter: (value: any, cell: any) => <Status status={cell?.row?.values?.status} type={'SS'}></Status>
    },
    {
      Header: 'Created By',
      accessor: 'createdBy',
    },
    {
      Header: 'Start Date',
      accessor: 'startDate',
    },
    {
      Header: 'Actions',
      accessor: 'actions',
      formatter: (value: any, cell: any) => {
        const simulationId = cell?.row?.values?.id || cell?.row?.original?.id
        return (
          <div style={{ display: 'flex', gap: '8px' }}>
            <button
              onClick={() => handleViewResults(simulationId)}
              style={{
                background: '#0088cc',
                color: 'white',
                border: 'none',
                padding: '4px 8px',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '11px'
              }}
              onMouseOver={(e) => {
                const target = e.target as HTMLButtonElement
                target.style.background = '#006699'
              }}
              onMouseOut={(e) => {
                const target = e.target as HTMLButtonElement
                target.style.background = '#0088cc'
              }}
            >
              Results
            </button>
            <button
              onClick={() => handleViewSimulation(simulationId)}
              style={{
                background: '#28a745',
                color: 'white', 
                border: 'none',
                padding: '4px 8px',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '11px'
              }}
              onMouseOver={(e) => {
                const target = e.target as HTMLButtonElement
                target.style.background = '#1e7e34'
              }}
              onMouseOut={(e) => {
                const target = e.target as HTMLButtonElement
                target.style.background = '#28a745'
              }}
            >
              View
            </button>
          </div>
        )
      }
    }
  ]

  return (
    <Dco>
      {pageData.isLoading ? (
        <div style={{ padding: '20px' }}>
          <p>Loading results...</p>
        </div>
      ) : (
        <>
          {/* @ts-ignore */}
          <Table 
            columns={columns}
            data={pageData.rowData}
          />
          <Box align='right' padding='small'>
            <Pagination 
              totalRows={pageData.totalResults} 
              pageChangeHandler={setCurrentPage} 
              rowsPerPage={10} 
            />
          </Box>
        </>
      )}
    </Dco>
  )
}

export default Results
