import {Links, Meta, Outlet, Scripts, ScrollRestoration} from "react-router";
import "./index.css";

// noinspection JSUnusedGlobalSymbols
export function Layout({children}: { children: React.ReactNode }) {
    return (
        <html lang="ja">
            <head>
                <meta charSet="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <Meta/>
                <Links/>
                <title>kicl</title>
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
