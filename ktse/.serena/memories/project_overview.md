# Keruta Task Server (ktse) Project Overview

## Purpose
Keruta Task Server is a Ktor-based WebSocket task server that implements the Kotlin TCP protocol (ktcp) for handling remote task execution. It provides secure WebSocket communication with JWT authentication for task management and execution.

## Tech Stack
- **Language**: Kotlin
- **Framework**: Ktor (WebSocket, Authentication, Routing)
- **Protocol**: ktcp (internal Kotlin TCP protocol implementation)
- **Authentication**: JWT with Keycloak integration
- **Logging**: kodel logging framework
- **Build Tool**: Gradle with Kotlin DSL

## Key Components
- `KerutaTaskServer`: Main server object handling WebSocket connections and message routing
- `WebsocketModule`: WebSocket configuration (ping/pong, timeouts, frame size)
- `JwtModule`: JWT authentication with Keycloak integration
- `WebsocketConnection`: WebSocket connection wrapper for ktcp protocol

## Architecture
- Uses Ktor's WebSocket plugin for real-time communication
- Implements custom message serialization via `JsonMsgSerializer`
- Routes messages through `KtcpServer.ktcpServerEntrypoints`
- Supports authenticated WebSocket connections at `/ws/ktcp` endpoint

## Configuration
- Runs on port 8080 (configurable via application.yaml)
- JWT issuer: https://user.kigawa.net/realms/develop
- Audience: backend-b
- Realm: develop