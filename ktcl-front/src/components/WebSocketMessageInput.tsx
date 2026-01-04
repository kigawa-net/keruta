import React from 'react';

interface WebSocketMessageInputProps {
  message: string;
  setMessage: (message: string) => void;
  onSendMessage: () => void;
  onReconnect: () => void;
  isConnected: boolean;
}

const WebSocketMessageInput: React.FC<WebSocketMessageInputProps> = ({
  message,
  setMessage,
  onSendMessage,
  onReconnect,
  isConnected,
}) => {
  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      onSendMessage();
    }
  };

  return (
    <div style={{ marginBottom: '20px' }}>
      <input
        type="text"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        onKeyDown={handleKeyPress}
        placeholder="Type a message..."
        style={{
          padding: '10px',
          width: '70%',
          marginRight: '10px',
          border: '1px solid #ccc',
          borderRadius: '4px'
        }}
      />
      <button
        onClick={onSendMessage}
        disabled={!isConnected}
        style={{
          padding: '10px 20px',
          backgroundColor: isConnected ? '#007bff' : '#ccc',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: isConnected ? 'pointer' : 'not-allowed'
        }}
      >
        Send
      </button>
      <button
        onClick={onReconnect}
        style={{
          padding: '10px 20px',
          backgroundColor: '#28a745',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          marginLeft: '10px'
        }}
      >
        Reconnect
      </button>
    </div>
  );
};

export default WebSocketMessageInput;
