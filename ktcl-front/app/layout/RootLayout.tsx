import {Outlet} from "react-router";
import {Link, useLocation} from "react-router-dom";
import AuthButton from "../components/AuthButton";
import {useEffect} from "react";
import {KeycloakProvider} from "../components/Keycloak";
import {UserProfileProvider} from "../components/UserProfile";


// noinspection JSUnusedGlobalSymbols
export default function Layout() {
    const loc = useLocation()
    useEffect(() => {
        console.log("layout rendered")
    }, [loc]);
    return (
        <KeycloakProvider>
            <UserProfileProvider>
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
            </UserProfileProvider>
        </KeycloakProvider>
    );
}


