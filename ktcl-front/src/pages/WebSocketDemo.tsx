import {useState} from 'react';
import WebSocketStatus from '../../app/components/WebSocketStatus';
import WebSocketMessageInput from '../../app/components/WebSocketMessageInput';
import WebSocketMessageList from '../../app/components/WebSocketMessageList';

const WebSocketDemo = () => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState<string[]>([]);

    const handleSendMessage = () => {
        if (message.trim()) {
            setMessages(prev => [...prev, `Sent: ${message}`]);
            setMessage('');
        }
    };

    const handleReconnect = () => {
        setMessages(prev => [...prev, 'Manual reconnection triggered']);
    };

    return (
        <div style={{padding: '20px', maxWidth: '600px', margin: '0 auto'}}>
            <h2>WebSocket Demo</h2>

            <WebSocketStatus
                isConnected={false}
                connectionError={"connectionError"}
                lastMessage={"lastMessage"}
            />

            <WebSocketMessageInput
                message={message}
                setMessage={setMessage}
                onSendMessage={handleSendMessage}
                onReconnect={handleReconnect}
                isConnected={false}
            />

            <WebSocketMessageList
                messages={messages}
                lastMessage={"lastMessage"}
            />
        </div>
    );
};

export default WebSocketDemo;
