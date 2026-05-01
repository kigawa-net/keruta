import {Links, Meta, Outlet, Scripts, ScrollRestoration} from "react-router";
import "./index.css";
import {Nav} from "./components/Nav";

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
                <Nav/>
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

export function ErrorBoundary() {
    return (
        <html lang="ja">
            <head>
                <meta charSet="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>kicl</title>
            </head>
            <body className="min-h-screen min-w-screen">
                <Nav/>
                <div className="p-8 text-red-600">
                    <h1 className="text-xl font-bold">エラーが発生しました</h1>
                    <p>ページを表示中に問題が発生しました。</p>
                </div>
            </body>
        </html>
    );
}
