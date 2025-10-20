import React, { useState } from 'react'
import { useRouter } from 'next/router'

// Basic newScenario component to prevent build errors
const NewScenario = () => {
  const router = useRouter()
  const [isLoading, setIsLoading] = useState(false)

  return (
    <div style={{ padding: '20px' }}>
      <h2>Create New Scenario</h2>
      <p>This is a placeholder for the new scenario creation functionality.</p>
      <div style={{ marginTop: '20px' }}>
        <button 
          onClick={() => router.back()}
          style={{
            padding: '10px 20px',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Back
        </button>
      </div>
    </div>
  )
}

export default NewScenario
