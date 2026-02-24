import AuthButton from "./auth/AuthButton";
import WsStatus from "../util/net/websocket/WsStatus";

interface HeaderProps {
  onMenuClick: () => void;
}

/**
 * アプリケーションヘッダー
 * ハンバーガーメニュー、WebSocketステータス、認証ボタンを含む
 */
export function Header({ onMenuClick }: HeaderProps) {
  return (
    <header className="bg-white shadow-sm border-b" style={{ borderColor: "#dee2e6" }}>
      <div className="flex items-center justify-between px-4 py-3">
        <button
          onClick={onMenuClick}
          className="p-2 rounded hover:bg-gray-100 transition-colors"
          aria-label="Toggle sidebar"
        >
          <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        <div className="flex gap-4">
          <WsStatus />
          <AuthButton />
        </div>
      </div>
    </header>
  );
}
