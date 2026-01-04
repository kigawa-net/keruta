import React from 'react';

interface WebSocketStatusProps {
  isConnected: boolean;
  connectionError?: string;
  lastMessage?: string;
}

const WebSocketStatus: React.FC<WebSocketStatusProps> = ({
  isConnected,
  connectionError,
  lastMessage,
}) => {
  return (
    <div style={{
      padding: '10px',
      backgroundColor: isConnected ? '#d4edda' : '#f8d7da',
      color: isConnected ? '#155724' : '#721c24',
      borderRadius: '4px',
      marginBottom: '10px'
    }}>
      <div><strong>Status:</strong> {isConnected ? 'Connected' : 'Disconnected'}</div>
      <div><strong>URL:</strong> ws://localhost:8081/ws?sessionId=demo</div>
      {connectionError && <div><strong>Error:</strong> {connectionError}</div>}
      {lastMessage && <div><strong>Last Message:</strong> {lastMessage}</div>}
    </div>
  );
};

export default WebSocketStatus;