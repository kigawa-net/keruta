import Home from "../components/Home";
import {redirect} from "react-router-dom";

export async function loader() {
    return redirect("/queue/create")
}

export default function Index() {
    return <Home/>
}
