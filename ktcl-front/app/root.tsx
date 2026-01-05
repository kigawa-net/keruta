import {Links, Meta, Scripts, ScrollRestoration} from "react-router-dom";

import {Outlet} from "react-router";
import {Route} from "../.react-router/types/app/+types/root";
import "./App.css";
// noinspection JSUnusedGlobalSymbols
export const links: Route.LinksFunction = () => [];

// noinspection JSUnusedGlobalSymbols
export function Layout({children}: { children: React.ReactNode }) {
    return (
        <html lang="ja">
            <head>
                <meta charSet="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <Meta/>
                <Links/>
                <title>keruta</title>
            </head>
            <body className="min-h-screen min-w-screen">
                {children}
                <ScrollRestoration/>
                <Scripts/>
            </body>
        </html>
    );
}

export default function App() {
    return <Outlet/>;
}
