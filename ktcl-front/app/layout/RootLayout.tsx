import {Outlet} from "react-router";
import {Link, useLocation} from "react-router-dom";
import AuthButton from "../components/AuthButton";
import {useEffect, useState} from "react";
import {KeycloakProvider} from "../components/Keycloak";
import {UserProfileProvider} from "../components/UserProfile";


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
                <div className="flex h-screen bg-gray-100">
                    {/* サイドバー */}
                    <aside className={`${isSidebarOpen ? 'w-64' : 'w-0'} bg-gray-900 text-white transition-all duration-300 overflow-hidden`}>
                        <div className="p-4">
                            <h1 className="text-2xl font-bold mb-8">KTCL</h1>
                            <nav>
                                <ul className="space-y-2">
                                    <li>
                                        <Link
                                            to="/"
                                            className="block px-4 py-2 rounded hover:bg-gray-700 transition-colors"
                                        >
                                            Home
                                        </Link>
                                    </li>
                                    <li>
                                        <Link
                                            to="/about"
                                            className="block px-4 py-2 rounded hover:bg-gray-700 transition-colors"
                                        >
                                            About
                                        </Link>
                                    </li>
                                    <li>
                                        <Link
                                            to="/contact"
                                            className="block px-4 py-2 rounded hover:bg-gray-700 transition-colors"
                                        >
                                            Contact
                                        </Link>
                                    </li>
                                    <li>
                                        <Link
                                            to="/websocket"
                                            className="block px-4 py-2 rounded hover:bg-gray-700 transition-colors"
                                        >
                                            WebSocket Demo
                                        </Link>
                                    </li>
                                </ul>
                            </nav>
                        </div>
                    </aside>

                    {/* メインコンテンツ */}
                    <div className="flex-1 flex flex-col overflow-hidden">
                        {/* トップバー */}
                        <header className="bg-white shadow-sm border-b border-gray-200">
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
                                <AuthButton/>
                            </div>
                        </header>

                        {/* ページコンテンツ */}
                        <main className="flex-1 overflow-y-auto p-6">
                            <Outlet/>
                        </main>
                    </div>
                </div>
            </UserProfileProvider>
        </KeycloakProvider>
    );
}


