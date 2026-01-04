import {Link, Route, Routes} from 'react-router-dom'
import Home from './pages/Home'
import About from './pages/About'
import Contact from './pages/Contact'
import WebSocketDemo from './pages/WebSocketDemo'
import AuthButton from './components/AuthButton'
import './App.css'
import PrivateLayout from "./layout/PrivateLayout.tsx";

function App() {
    return (
        <div className="App">
            <nav>
                <ul>
                    <li>
                        <Link to="/">Home</Link>
                    </li>
                    <li>
                        <Link to="/about">About</Link>
                    </li>
                    <li>
                        <Link to="/contact">Contact</Link>
                    </li>
                    <li>
                        <Link to="/websocket">WebSocket Demo</Link>
                    </li>
                </ul>
                <AuthButton/>
            </nav>

            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/about" element={<About/>}/>
                <Route path="/contact" element={<Contact/>}/>
                <Route element={<PrivateLayout/>}>
                    <Route path="/websocket" element={
                        <WebSocketDemo/>
                    }/>
                </Route>
            </Routes>
        </div>
    )
}

export default App
