import {index, route, type RouteConfig} from "@react-router/dev/routes";

// noinspection JSUnusedGlobalSymbols
export default [
    index("./routes/index.tsx"),
    route("login", "./routes/login.tsx"),
    route("settings", "./routes/settings.tsx"),
    route("account", "./routes/account.tsx"),
    route("auth/callback", "./routes/auth.callback.tsx"),
] satisfies RouteConfig;
