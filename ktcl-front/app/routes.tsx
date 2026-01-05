import {index, layout, route, RouteConfig} from "@react-router/dev/routes";

// noinspection JSUnusedGlobalSymbols
export default [
    layout("./layout/RootLayout.tsx", [
        index("./routes/index.tsx"),
        route("about", "./routes/about.tsx"),
        route("contact", "./routes/contact.tsx"),
        layout("./layout/PrivateLayout.tsx", [
            route("websocket", "./routes/websocket.tsx"),
        ])
    ])
] satisfies RouteConfig;
