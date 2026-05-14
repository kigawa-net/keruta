import {NavLink} from 'react-router';
import {useAuth} from '../context/AuthContext';

export function Nav() {
    const {user, isLoading} = useAuth();
    const linkClass = ({isActive}: { isActive: boolean }) =>
        `text-sm ${isActive ? 'text-blue-600 font-medium' : 'text-gray-600 hover:text-gray-900'}`;

    return (
        <nav className="bg-white border-b border-gray-200 px-4 py-3">
            <div className="max-w-4xl mx-auto flex items-center gap-6">
                <span className="font-bold text-gray-900">kicl</span>
                <NavLink to="/" className={linkClass} end>ホーム</NavLink>
                <NavLink to="/account" className={linkClass}>アカウント</NavLink>
                <NavLink to="/settings" className={linkClass}>設定</NavLink>
                {!isLoading && user && (
                    <span className="ml-auto text-sm text-gray-600">
                        {user.profile.preferred_username ?? user.profile.email}
                    </span>
                )}
            </div>
        </nav>
    );
}
