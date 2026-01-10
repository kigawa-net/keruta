import {Outlet} from "react-router";
import {KerutaTaskProvider} from "../components/KerutaTask";
import {Config} from "../Config";
import { WebsocketProvider } from "../components/Websocket";

// noinspection JSUnusedGlobalSymbols
export default function WebsocketLayout(
    {}: {},
) {


    return (
        <WebsocketProvider
            wsUrl={Config.websocketUrl}
        >
            <KerutaTaskProvider>
                <Outlet/>
            </KerutaTaskProvider>
        </WebsocketProvider>
    )
}


