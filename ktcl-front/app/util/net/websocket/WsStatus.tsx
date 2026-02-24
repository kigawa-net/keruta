import {useWebsocketState} from "./WebsocketProvider";
import {useAuthedKtseState} from "../../../components/api/AuthedKtseProvider";


export default function WsStatus(
    {}: {},
) {
    const websocket = useWebsocketState()
    const authedKtse = useAuthedKtseState()
    const isConnected = websocket.state == "open"
    return (
        <div style={{
            padding: '10px',
            backgroundColor: isConnected ? '#d4edda' : '#f8d7da',
            color: isConnected ? '#155724' : '#721c24',
            borderRadius: '4px',
        }}>
            Status: {websocket.state}, {authedKtse.state}
        </div>
    )
}


