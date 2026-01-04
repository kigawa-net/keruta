import PrivateRoute from "../components/PrivateRoute.tsx";
import {Outlet} from "react-router-dom";

export default function PrivateLayout(
    {}: {},
) {


    return (
        <PrivateRoute>
            <Outlet/>
        </PrivateRoute>
    )
}


