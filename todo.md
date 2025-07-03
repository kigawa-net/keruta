# Keruta Todo List

## WebSocket Connection Issues

- [x] Fix WebSocket URL construction in `NewWebSocketClient` to properly handle different URL formats
  - Currently, if the baseURL is something like "keruta-svc.kigawa-net-keruta.svc.cluster.local:8080", it won't be properly parsed
  - Ensure proper handling of URLs with or without scheme (http/https)

- [x] Add authentication headers to WebSocket connection in `Connect()` method
  - Currently, no headers are included in the WebSocket connection request
  - Add proper authentication headers to ensure successful connection

- [x] Improve error handling for WebSocket connection failures
  - Make the system more resilient when WebSocket connections fail
  - Ensure proper fallback to HTTP API when WebSocket is unavailable

## Configuration Validation

- [x] Add validation for KERUTA_TASK_ID environment variable in config.validate()
  - Currently, only API URL and token are validated, but TASK_ID is also required
  - Add proper validation and error message for missing TASK_ID

## Error Handling

- [ ] Improve error logging for API connection failures
  - Add more detailed error messages to help diagnose connection issues
  - Include request details in error logs

- [ ] Add retry mechanism with exponential backoff for API calls
  - Implement a more robust retry strategy for failed API calls
  - Use exponential backoff to avoid overwhelming the server

## Documentation

- [ ] Update documentation with troubleshooting information
  - Add a section on common connection issues and how to resolve them
  - Include information on required environment variables and their format

## Testing

- [ ] Add tests for connection handling
  - Test different URL formats and authentication scenarios
  - Test fallback mechanisms when primary connection methods fail
