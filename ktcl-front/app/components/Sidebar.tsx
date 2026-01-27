import {Link} from "react-router-dom";

interface SidebarProps {
    isOpen: boolean;
}

export default function Sidebar({isOpen}: SidebarProps) {
    return (
        <aside className={`${isOpen ? 'w-64' : 'w-0'} transition-all duration-300 overflow-hidden`}
               style={{backgroundColor: '#f8f9fa'}}>
            <div className="p-4">
                <h1 className="text-2xl font-bold mb-8" style={{color: '#0a58ca'}}>Keruta</h1>
                <nav>
                    <ul className="space-y-2">
                        <li>
                            <Link
                                to="/"
                                className="sidebar-link block px-4 py-2 rounded transition-colors"
                                style={{color: '#0a58ca'}}
                            >
                                Home
                            </Link>
                        </li>
                        <li>
                            <Link
                                to="/queue/create"
                                className="sidebar-link block px-4 py-2 rounded transition-colors"
                                style={{color: '#0a58ca'}}
                            >
                                Create Queue
                            </Link>
                        </li>
                        <li>
                            <Link
                                to="/task"
                                className="sidebar-link block px-4 py-2 rounded transition-colors"
                                style={{color: '#0a58ca'}}
                            >
                                タスク管理
                            </Link>
                        </li>
                        <li>
                            <Link
                                to="/provider"
                                className="sidebar-link block px-4 py-2 rounded transition-colors"
                                style={{color: '#0a58ca'}}
                            >
                                Providers
                            </Link>
                        </li>
                    </ul>
                </nav>
            </div>
        </aside>
    );
}
