import { Box, Table } from '@dco/sdv-ui'
import { useStoreActions } from 'easy-peasy'
import { useEffect, useState, useMemo } from 'react'
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
  const [forceRender, setForceRender] = useState(0) // Add force render state
  const [pageData, setPageData] = useState({
    rowData: [],
    isLoading: false,
    totalPages: 0,
    totalResults: 0,
  })

  // Memoize the table data to ensure proper re-rendering
  const tableData = useMemo(() => {
    // Create a deep copy to ensure the reference changes
    return pageData.rowData.map((row: any) => ({ ...row }))
  }, [pageData.rowData])

  useEffect(() => {
    setPageData((prevState) => ({
      ...prevState,
      rowData: [],
      isLoading: true,
    }))
    
    getResultsData(currentPage).then((info) => {
      setPageData({
        isLoading: false,
        rowData: resultsRowData(info) as any,
        totalPages: info?.data?.simulationReadByQuery?.pages,
        totalResults: info?.data?.simulationReadByQuery?.total,
      })
      setCount(info?.data?.simulationReadByQuery?.total || 0);
    }).catch((error) => {
      setPageData((prevState) => ({
        ...prevState,
        isLoading: false,
      }))
    })
  }, [currentPage])

  const handleViewResults = (simulationId: string) => {
    router.push(`/dco/simulation/results/${simulationId}`)
  }

  const handleViewSimulation = (simulationId: string) => {
    router.push(`/dco/simulation/${simulationId}`)
  }

  const handleDeleteSimulation = (simulationId: string, simulationName: string) => {

    
    const confirmDelete = window.confirm(
      `Are you sure you want to delete "${simulationName}"?`
    )
    
    if (confirmDelete) {

      console.log('� Current rowData before filter:', pageData.rowData);
      
      // Update the page data immediately
      setPageData((prevState) => {
        const filteredData = prevState.rowData.filter((row: any) => {
          return row.id !== simulationId; // Keep rows that DON'T match the ID to delete
        });
        
        return {
          ...prevState,
          rowData: filteredData,
          totalResults: Math.max(0, prevState.totalResults - 1)
        };
      });
      
      // Update count
      setCount((prev: number) => Math.max(0, prev - 1));
      
      // Force a re-render of the table
      setForceRender(prev => prev + 1);
      

    }
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
        const simulationName = cell?.row?.values?.name || cell?.row?.original?.name || 'Unknown Simulation'
        
        return (
          <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
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
            <button
              onClick={() => handleDeleteSimulation(simulationId, simulationName)}
              style={{
                background: '#dc3545',
                color: 'white',
                border: 'none',
                padding: '4px 8px',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '11px'
              }}
              onMouseOver={(e) => {
                const target = e.target as HTMLButtonElement
                target.style.background = '#c82333'
              }}
              onMouseOut={(e) => {
                const target = e.target as HTMLButtonElement
                target.style.background = '#dc3545'
              }}
            >
              Delete
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
            key={`table-${tableData.length}-${forceRender}`}
            columns={columns}
            data={tableData}
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
