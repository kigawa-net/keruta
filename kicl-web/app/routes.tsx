import {index, route, type RouteConfig} from "@react-router/dev/routes";

// noinspection JSUnusedGlobalSymbols
export default [
    index("./routes/index.tsx"),
    route("settings", "./routes/settings.tsx"),
] satisfies RouteConfig;
