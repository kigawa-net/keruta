import {useWebsocketState} from "./WebsocketProvider";
import {useAuthedKtseState} from "../../../components/api/AuthedKtseProvider";


export default function WsStatus(
    {}: {},
) {
    const websocket = useWebsocketState()
    const authedKtse = useAuthedKtseState()
    const isConnected = websocket.state == "open"
    return (
        <div className={`text-xs md:text-sm px-2 md:px-3 py-1 md:py-2 rounded ${isConnected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
            <span className="hidden md:inline">Status: </span>{websocket.state}, <span className="hidden md:inline">{authedKtse.state}</span>
        </div>
    )
}


