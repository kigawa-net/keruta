# Keruta Project Purpose

Keruta is a Coder workspace management system designed to manage development environments and task execution. It consists of three main components:

1. **Spring Boot API Server** (Kotlin) - Main API service for session and workspace management
2. **Keruta Executor** (Kotlin/Spring Boot) - Coder workspace monitoring and management
3. **Keruta Agent** (Go) - Task execution runtime for external processes
4. **KTCP** (Kotlin Multiplatform) - Task Client Protocol for WebSocket-based communication between server and providers

The system manages the lifecycle of Coder workspaces, handles session management, and provides real-time task execution capabilities through WebSocket communication.