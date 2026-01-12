import {index, layout, route, RouteConfig} from "@react-router/dev/routes";

// noinspection JSUnusedGlobalSymbols
export default [
    layout("./layout/RootLayout.tsx", [
        route("about", "./routes/about.tsx"),
        layout("./layout/PrivateLayout.tsx", [
            index("./routes/index.tsx"),
            route("task", "./routes/task.tsx"),
        ]),
    ])
] satisfies RouteConfig;
