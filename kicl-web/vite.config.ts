import {defineConfig} from 'vite'
import {reactRouter} from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import tsconfigPaths from "vite-tsconfig-paths";

// https://vite.dev/config/
export default defineConfig({
    plugins: [
        tailwindcss(),
        reactRouter(),
        tsconfigPaths()
    ],
    resolve: {
        preserveSymlinks: true,
    },
    optimizeDeps: {
        include: [
            "keruta-kicl-kicl-domain",
            "keruta-kicl-kicl-usecase",
            "keruta-kicp-kicp-domain",
            "keruta-kicp-kicp-usecase",
        ],
    },
    ssr: {
        external: [
            "keruta-kicl-kicl-domain",
            "keruta-kicl-kicl-usecase",
            "keruta-kicp-kicp-domain",
            "keruta-kicp-kicp-usecase",
        ],
    },
    build: {
        commonjsOptions: {
            transformMixedEsModules: true,
        },
    },
})
