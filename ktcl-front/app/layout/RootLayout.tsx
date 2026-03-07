import {useLocation} from "react-router-dom";
import {useEffect, useState} from "react";
import {RootProviders} from "./RootProviders";
import {AppLayout} from "./AppLayout";

/**
 * ルートレイアウトコンポーネント
 * Provider設定とレイアウト構造を管理
 */
// noinspection JSUnusedGlobalSymbols
export default function RootLayout() {
  const loc = useLocation();
  const [isSidebarOpen, setIsSidebarOpen] = useState(() =>
    typeof window !== "undefined" && window.innerWidth >= 768
  );

  useEffect(() => {
    console.log("layout rendered");
  }, [loc]);

  return (
    <RootProviders>
      <AppLayout
        isSidebarOpen={isSidebarOpen}
        onToggleSidebar={() => setIsSidebarOpen(!isSidebarOpen)}
      />
    </RootProviders>
  );
}
