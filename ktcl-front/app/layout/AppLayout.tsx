import { ReactNode } from "react";
import { Outlet } from "react-router";
import Sidebar from "../components/sidebar/Sidebar";
import { Header } from "../components/Header";

interface AppLayoutProps {
  isSidebarOpen: boolean;
  onToggleSidebar: () => void;
  children?: ReactNode;
}

/**
 * アプリケーションのメインレイアウト
 * サイドバー、ヘッダー、メインコンテンツエリアを含む
 */
export function AppLayout({ isSidebarOpen, onToggleSidebar, children }: AppLayoutProps) {
  return (
    <div className="flex h-screen bg-white">
      {/* サイドバーオーバーレイ（モバイル用） */}
      {isSidebarOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 md:hidden"
          onClick={onToggleSidebar}
        />
      )}

      {/* サイドバー */}
      <Sidebar isOpen={isSidebarOpen} />

      {/* メインコンテンツエリア */}
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header onMenuClick={onToggleSidebar} />
        <main className="flex-1 overflow-y-auto p-3 md:p-6">
          {children ?? <Outlet />}
        </main>
      </div>
    </div>
  );
}
