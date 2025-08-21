import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

function App() {
  const [count, setCount] = useState(0)
  const [message, setMessage] = useState('')
  const [health, setHealth] = useState('unknown')
  const [name, setName] = useState('React Learner')

  async function fetchGreeting() {
    const res = await fetch(`/api/greeting?name=${encodeURIComponent(name)}`)
    const data = await res.json()
    setMessage(data.message)
  }

  async function checkHealth() {
    const res = await fetch('/api/health')
    const data = await res.json()
    setHealth(data.status)
  }

  return (
    <>
      <div>
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>Vite + React</h1>
      <div className="card">
        <button onClick={() => setCount((count) => count + 1)}>
          count is {count}
        </button>
        <div style={{ marginTop: 12 }}>
          <input value={name} onChange={(e) => setName(e.target.value)} />
          <button onClick={fetchGreeting} style={{ marginLeft: 8 }}>Fetch Greeting</button>
          <div style={{ marginTop: 8 }}>Message: {message || '(none yet)'}</div>
        </div>
        <div style={{ marginTop: 12 }}>
          <button onClick={checkHealth}>Check Backend Health</button>
          <div>Health: {health}</div>
        </div>
        <p style={{ marginTop: 12 }}>
          Edit <code>src/App.jsx</code> and save to test HMR
        </p>
      </div>
      <p className="read-the-docs">
        Click on the Vite and React logos to learn more
      </p>
    </>
  )
}

export default App
