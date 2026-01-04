import React from 'react';

interface WebSocketMessageListProps {
  messages: string[];
  lastMessage?: string;
}

const WebSocketMessageList: React.FC<WebSocketMessageListProps> = ({
  messages,
  lastMessage,
}) => {
  return (
    <div style={{
      border: '1px solid #ccc',
      borderRadius: '4px',
      padding: '10px',
      height: '300px',
      overflowY: 'auto',
      backgroundColor: '#f8f9fa'
    }}>
      <h3>Messages:</h3>
      {messages.length === 0 ? (
        <p style={{ color: '#666' }}>No messages yet...</p>
      ) : (
        messages.map((msg, index) => (
          <div key={index} style={{ marginBottom: '5px', padding: '5px', backgroundColor: 'white', borderRadius: '3px' }}>
            {msg}
          </div>
        ))
      )}
      {lastMessage && (
        <div style={{ marginTop: '10px', padding: '10px', backgroundColor: '#e9ecef', borderRadius: '4px' }}>
          <strong>Last received:</strong> {lastMessage}
        </div>
      )}
    </div>
  );
};

export default WebSocketMessageList;