import PrivateRoute from "../components/auth/PrivateRoute";
import {Outlet} from "react-router";
// noinspection JSUnusedGlobalSymbols
export default function PrivateLayout(
    {}: {},
) {
    return (
        <PrivateRoute>
            <Outlet/>
        </PrivateRoute>
    )
}


