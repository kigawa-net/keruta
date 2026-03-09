import {index, layout, route, RouteConfig} from "@react-router/dev/routes";

// noinspection JSUnusedGlobalSymbols
export default [
    layout("./layout/RootLayout.tsx", [
        layout("./layout/PrivateLayout.tsx", [
            index("./routes/index.tsx"),
            route("provider", "./routes/providers.tsx"),
            route("provider/add", "./routes/provider.add.tsx"),
            route("provider/complete", "./routes/provider.complete.tsx"),
            route("task", "./routes/task.tsx"),
            route("queue/create", "./routes/queue.create.tsx"),
            route("queue/:queueId", "./routes/queue.$queueId.tsx"),
            route("queue/:queueId/edit", "./routes/queue.$queueId.edit.tsx"),
        ]),
    ]),
    route(".well-known/jwks.json", "./routes/jwks.ts"),
    route("api/token", "./routes/token.ts"),
] satisfies RouteConfig;
