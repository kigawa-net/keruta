import {Link} from "react-router-dom";
import SidebarQueueButtons from "./SidebarQueueButtons";

interface SidebarProps {
    isOpen: boolean;
    onClose: () => void;
}

export default function Sidebar({isOpen, onClose}: SidebarProps) {
    return (
        <aside
            className={`
                fixed md:static inset-y-0 left-0 z-50 md:z-auto
                w-64 transition-all duration-300 overflow-hidden
                ${isOpen ? 'translate-x-0 md:w-64' : '-translate-x-full md:translate-x-0 md:w-0'}
            `}
            style={{backgroundColor: '#f8f9fa'}}>
            <div className="p-4">
                <h1 className="text-2xl font-bold mb-8" style={{color: '#0a58ca'}}>Keruta</h1>
                <nav>
                    <ul className="space-y-2">
                        <li>
                            <Link
                                to="/queue/create"
                                className="sidebar-link block px-4 py-2 rounded transition-colors"
                                style={{color: '#0a58ca'}}
                                onClick={onClose}
                            >
                                Create Queue
                            </Link>
                        </li>
                        <SidebarQueueButtons onClose={onClose}/>
                        <li>
                            <Link
                                to="/provider"
                                className="sidebar-link block px-4 py-2 rounded transition-colors"
                                style={{color: '#0a58ca'}}
                                onClick={onClose}
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
