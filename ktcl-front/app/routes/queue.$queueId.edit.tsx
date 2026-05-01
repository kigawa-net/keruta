import {useParams} from "react-router";
import QueueEditPage from "../components/queue/QueueEditPage";

// noinspection JSUnusedGlobalSymbols
export default function QueueEditRoute() {
    const {queueId} = useParams<{ queueId: string }>();
    const id = parseInt(queueId ?? "");
    if (isNaN(id)) return <div>Invalid queue ID</div>;
    return <QueueEditPage queueId={id}/>;
}
