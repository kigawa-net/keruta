import {useKerutaTaskState} from "../../app/useAppState";


export default function WsStatus(
    {}: {},
) {
    const keruta = useKerutaTaskState()
    const isConnected = keruta.state == "connected"
    return (
        <div style={{
            padding: '10px',
            backgroundColor: isConnected ? '#d4edda' : '#f8d7da',
            color: isConnected ? '#155724' : '#721c24',
            borderRadius: '4px',
        }}>
            Status: {keruta.state}, {keruta.state == "connected" ? keruta.auth.state : "unauth"}
        </div>
    )
}


