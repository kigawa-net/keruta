import {redirect} from "react-router-dom";

export async function loader() {
    return redirect("/queue/create")
}

// noinspection JSUnusedGlobalSymbols
export default function Index() {
    return <div>index</div>
}
