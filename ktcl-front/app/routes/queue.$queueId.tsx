import {Route} from "../../.react-router/types/app/+types/root";
import LoaderArgs = Route.LoaderArgs;
import ComponentProps = Route.ComponentProps;

export async function loader({params}: LoaderArgs) {
    return {
        queueId: params.queueId
    }
}

// noinspection JSUnusedGlobalSymbols
export default function Route({loaderData}: ComponentProps) {
    return (
        <div>a{loaderData.queueId}</div>
    )
}
