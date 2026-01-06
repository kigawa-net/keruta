import {Outlet} from "react-router";
import {KerutaTaskProvider} from "../components/KerutaTask";

// noinspection JSUnusedGlobalSymbols
export default function WebsocketLayout(
    {}: {},
) {


    return (
        <KerutaTaskProvider>
            <Outlet/>
        </KerutaTaskProvider>
    )
}


