import {index, layout, route, RouteConfig} from "@react-router/dev/routes";

// noinspection JSUnusedGlobalSymbols
export default [
    layout("./layout/RootLayout.tsx", [
        route("about", "./routes/about.tsx"),
        layout("./layout/PrivateLayout.tsx", [
            index("./routes/index.tsx"),
            route("task", "./routes/task.tsx"),
            route("task/create", "./routes/task.create.tsx"),
        ]),
    ]),
    route(".well-known/jwks.json", "./routes/jwks.ts"),
    route("api/token", "./routes/token.ts"),
] satisfies RouteConfig;
