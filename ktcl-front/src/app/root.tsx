import {Outlet} from "react-router";
import {Link} from "react-router-dom";
import AuthButton from '../../app/components/AuthButton'
import '../../app/App.css'
import type {Route} from "../../.react-router/types/app/routes/+types";

export async function loader(
    {}: Route.LoaderArgs
) {
}

export default function Root() {
    return (
        <div className="App">
            <nav>
                <ul>
                    <li>
                        <Link to="/">Home</Link>
                    </li>
                    <li>
                        <Link to="/about">About</Link>
                    </li>
                    <li>
                        <Link to="/contact">Contact</Link>
                    </li>
                    <li>
                        <Link to="/websocket">WebSocket Demo</Link>
                    </li>
                </ul>
                <AuthButton/>
            </nav>

            <Outlet/>
        </div>
    );
}
