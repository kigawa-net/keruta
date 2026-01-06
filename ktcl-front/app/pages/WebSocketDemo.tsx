import {useState} from 'react';
import WebSocketStatus from '../components/WebSocketStatus';
import WebSocketMessageInput from '../components/WebSocketMessageInput';
import WebSocketMessageList from '../components/WebSocketMessageList';
import {useKerutaTaskState} from "../components/KerutaTask";

const WebSocketDemo = () => {
    const [message, setMessage] = useState('');
    const [messages, setMessages] = useState<string[]>([]);
    const kerutaState = useKerutaTaskState()
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
                isConnected={kerutaState.state == "connected"}
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
