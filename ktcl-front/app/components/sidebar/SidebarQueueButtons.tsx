import {Link} from "react-router-dom";
import {ClientQueueListedMsg, ServerQueueListMsg} from "../msg/queue";
import {useEffect, useState} from "react";
import {useWebsocketReceive} from "../../util/net/websocket/useWebsocketReceive";
import {useWebsocketState} from "../../util/net/websocket/WebsocketProvider";
import {useAuthedKtseState} from "../api/AuthedKtseProvider";

type Queue = ClientQueueListedMsg["queues"][0]
export default function SidebarQueueButtons(
    {onClose}: { onClose: () => void },
) {
    const [queues, setQueues] = useState<Queue[]>([])
    const wsState = useWebsocketState()
    const authedKtse = useAuthedKtseState()
    useWebsocketReceive(msg => {
        if (msg.type != "queue_listed") return
        setQueues(msg.queues)
    }, [])

    useEffect(() => {
        if (wsState.state != "open") return
        if (authedKtse.state != "loaded") return;
        const msg: ServerQueueListMsg = {
            type: "queue_list",
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [wsState, authedKtse.state]);
    return queues.map(value =>
        <li key={value.id}>
            <Link
                to={`/queue/${value.id}`}
                className="sidebar-link block px-4 py-2 rounded transition-colors"
                style={{color: '#0a58ca'}}
                onClick={onClose}
            >
                {value.name}
            </Link>
        </li>
    )
}


