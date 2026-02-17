import { Link } from "react-router-dom";
import { useWsState } from "../useServiceHooks";
import { useKerutaTaskState } from "../KerutaTask";
import useWsReceive from "../websocket/useWsReceive";
import { ClientQueueListedMsg, ServerQueueListMsg } from "../../msg/queue";
import { useEffect, useState } from "react";

type Queue = ClientQueueListedMsg["queues"][0]
export default function SidebarQueueButtons(
    {}: {},
) {
    const [queues, setQueues] = useState<Queue[]>([])
    const wsState = useWsState()
    const keruta = useKerutaTaskState()
    useWsReceive(wsState, msg => {
        if (msg.type != "queue_listed") return
        setQueues(msg.queues)
    }, [])
    useEffect(() => {
        if (wsState.state != "open") return
        if (keruta.state != "connected") return;
        if (keruta.auth.state != "authenticated") return;
        const msg: ServerQueueListMsg = {
            type: "queue_list",
        }
        wsState.websocket.send(JSON.stringify(msg))
    }, [wsState, keruta.state == "connected" && keruta.auth.state]);
    return queues.map(value =>
        <li key={value.id}>
            <Link
                to={`/queue/${value.id}`}
                className="sidebar-link block px-4 py-2 rounded transition-colors"
                style={{color: '#0a58ca'}}
            >
                {value.name}
            </Link>
        </li>
    )
}


