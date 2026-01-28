import {Outlet} from "react-router";
import {useLocation} from "react-router-dom";
import AuthButton from "../components/AuthButton";
import {useEffect, useState} from "react";
import {KeycloakProvider} from "../components/Keycloak";
import {UserProfileProvider} from "../components/UserProfile";
import WsStatus from "../components/websocket/WsStatus";
import {WebsocketProvider} from "../components/websocket/Websocket";
import {KerutaTaskProvider} from "../components/KerutaTask";
import Sidebar from "../components/sidebar/Sidebar";
import Config from "../Config";


// noinspection JSUnusedGlobalSymbols
export default function Layout() {
    const loc = useLocation()
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);

    useEffect(() => {
        console.log("layout rendered")
    }, [loc]);

    return (
        <KeycloakProvider>
            <UserProfileProvider>
                <WebsocketProvider
                    wsUrl={Config.websocketUrl}
                >
                    <KerutaTaskProvider>
                <div className="flex h-screen bg-white">
                    {/* サイドバー */}
                    <Sidebar isOpen={isSidebarOpen} />

                    {/* メインコンテンツ */}
                    <div className="flex-1 flex flex-col overflow-hidden">
                        {/* トップバー */}
                        <header className="bg-white shadow-sm border-b" style={{borderColor: '#dee2e6'}}>
                            <div className="flex items-center justify-between px-4 py-3">
                                <button
                                    onClick={() => setIsSidebarOpen(!isSidebarOpen)}
                                    className="p-2 rounded hover:bg-gray-100 transition-colors"
                                    aria-label="Toggle sidebar"
                                >
                                    <svg
                                        className="w-6 h-6"
                                        fill="none"
                                        stroke="currentColor"
                                        viewBox="0 0 24 24"
                                    >
                                        <path
                                            strokeLinecap="round"
                                            strokeLinejoin="round"
                                            strokeWidth={2}
                                            d="M4 6h16M4 12h16M4 18h16"
                                        />
                                    </svg>
                                </button>
                                <div className={"flex gap-4"}>
                                    <WsStatus/>
                                    <AuthButton/>
                                </div>
                            </div>
                        </header>

                        {/* ページコンテンツ */}
                        <main className="flex-1 overflow-y-auto p-6">
                            <Outlet/>
                        </main>
                    </div>
                </div>
                    </KerutaTaskProvider>
                </WebsocketProvider>
            </UserProfileProvider>
        </KeycloakProvider>
    );
}


