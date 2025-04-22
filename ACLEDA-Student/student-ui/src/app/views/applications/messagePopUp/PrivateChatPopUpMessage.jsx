import React, { useEffect, useRef, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import useAuth from "../../../hooks/useAuth";
import useHttp from "../../../c1hooks/http";

// Function to refresh access token
const refreshAccessToken = async (refreshToken) => {
    console.log("refreshToken", refreshToken)
    const response = await fetch('http://localhost:8080/refresh-token', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ refreshToken }),
    });

    if (response.ok) {
        const data = await response.json();
        console.log("data", data)
        localStorage.setItem('accessToken', data.accessToken);
        localStorage.setItem('accessTokenExpiry', data.expiredAt); // Store expiry time
        return data.accessToken;
    } else {
        console.error('Failed to refresh access token');
        return null;
    }
};

const getValidToken = async () => {
    const accessToken = localStorage.getItem('accessToken');
    const refreshToken = localStorage.getItem('refreshToken');
    const expiry = localStorage.getItem('expiredAt');
    const currentTime = new Date().getTime() / 1000; // Current time in seconds

    // Log the expiration time
    console.log('Access token expired', expiry);
    console.log('refreshToken', refreshToken);

    // If there's no expiry or token is expired, try refreshing
    if (accessToken && (expiry === null || currentTime > expiry)) {
        console.log('Access token expired or no expiry found, trying to refresh it...');
        const newToken = await refreshAccessToken(refreshToken);
        console.log("newToken", newToken)

        // If refresh is successful, store the new access token and expiry
        if (newToken) {
            localStorage.setItem('accessToken', newToken.token);
            localStorage.setItem('accessTokenExpiry', newToken.expiredAt);
            return newToken.token;
        }
    }

    return accessToken; // Return the current access token if not expired or refreshing failed
};


const PrivateChatPopUpMessage = ({ sender, usrMessage, popUpUsername }) => {
    const authUser = useAuth();
    let username = authUser?.user?.username;
    const isStudent = authUser?.user?.roles?.some(role => role?.name === 'ROLE_STUDENT')
    console.log("isStudent", isStudent)
    console.log("sender", sender)
    console.log("usrMessage", usrMessage)
    // if (isStudent){
    //     username = usrMessage;
    // }
    console.log("username", username)
    console.log("authUser", authUser)
    const [messages, setMessages] = useState([]);
    const [isConnected, setIsConnected] = useState(false);
    const [input, setInput] = useState('');
    const stompClientRef = useRef(null);
    const { isLoading, isFormSubmission, res, validation, error, urlId, sendRequest } = useHttp();
    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState({});


    useEffect(() => {
        console.log("Messages state updated:", messages);
    }, [messages]);

    useEffect(() => {
        if (!username) return;

        const connectWebSocket = async () => {
            const token = await getValidToken(); // ✅ await here

            if (!token) {
                console.error('❌ No valid token available');
                return;
            }

            const socket = new SockJS(`http://localhost:8080/ws?token=${token}`);

            const stompClient = new Client({
                webSocketFactory: () => socket,
                connectHeaders: {
                    Authorization: `Bearer ${token}`,
                },
                reconnectDelay: 5000,
                onConnect: () => {
                    console.log('✅ Connected');
                    setIsConnected(true);

                    // stompClient.subscribe(`/user/${username}/queue/messages`, (message) => {
                    //     const msg = JSON.parse(message.body);
                    //     setMessages((prev) => [...prev, msg]);
                    // });
                    stompClient.subscribe(`/user/${username}/queue/messages`, (message) => {
                        const data = JSON.parse(message.body);

                        if (Array.isArray(data)) {
                            setMessages(data.reverse()); // ← This makes "Hi" show up at the top visually
                        } else {
                            setMessages((prev) => [...prev, data]);
                        }
                    });


                    stompClient.publish({
                        destination: '/app/chat.getMessages',
                        body: JSON.stringify({
                            sender: username,
                            receiver: isStudent ? sender : popUpUsername,
                        }),
                    });
                },
                onStompError: (frame) => {
                    console.error('❌ STOMP Error', frame);
                },
            });

            stompClient.activate();
            stompClientRef.current = stompClient;
        };

        connectWebSocket(); // 🚀 Launch the async function

        return () => {
            if (stompClientRef.current) {
                stompClientRef.current.deactivate();
            }
        };
    }, [username, popUpUsername]);
    const sendMessage = () => {
        if (!input.trim() || !popUpUsername || !stompClientRef.current?.connected) return;

        const message = {
            sender: username,
            receiver: isStudent ? sender : popUpUsername,
            content: input,
            type: 'CHAT',
        };

        stompClientRef.current.publish({
            destination: '/app/chat.sendPrivate',
            body: JSON.stringify(message),
        });

        setMessages((prev) => [...prev, message]);
        setInput('');
    };

    return (
        <div
            style={{
                padding: '20px',
                margin: '0 auto',
                fontFamily: 'Arial, sans-serif',
            }}
        >
            <h3 style={{ textAlign: 'center', marginBottom: '15px' }}>
                💬 Chat with <strong>{isStudent ? sender : popUpUsername}</strong>
            </h3>

            <div
                style={{
                    height: '300px',
                    overflowY: 'auto',
                    border: '1px solid #ddd',
                    borderRadius: '10px',
                    padding: '10px',
                    background: '#f9f9f9',
                    marginBottom: '10px',
                }}
            >
                {messages.map((msg, idx) => {
                    let senderUsername = msg.userSender?.username || msg.sender;
                    let isSender = senderUsername === username;

                    console.log("msg", msg)
                    console.log("msg.userSender?.username", msg.userSender?.username)
                    console.log("isSender", isSender)
                    return (
                        <div
                            key={idx}
                            style={{
                                display: 'flex',
                                flexDirection: isSender ? 'row-reverse' : 'row',
                                alignItems: 'flex-end',
                                marginBottom: '10px',
                            }}
                        >
                            <div
                                style={{
                                    fontSize: '20px',
                                    margin: '0 8px',
                                }}
                            >
                                {isSender ? '👤' : '💬'}
                            </div>
                            <div
                                style={{
                                    background: isSender ? '#dcf8c6' : 'rgb(211,199,199)',
                                    padding: '10px 15px',
                                    borderRadius: '20px',
                                    maxWidth: '70%',
                                    wordBreak: 'break-word',
                                    alignItems: 'flex-end',
                                }}
                            >
                                <div style={{ fontWeight: 'bold', fontSize: '12px', marginBottom: '2px' }}>
                                    {msg.sender}
                                </div>
                                <div style={{ fontSize: '14px' }}>{msg.content}</div>
                            </div>
                        </div>
                    );
                })}
            </div>

            <div style={{ display: 'flex' }}>
                <input
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    placeholder="Type a message"
                    style={{
                        flex: 1,
                        padding: '10px',
                        border: '1px solid #ccc',
                        borderRadius: '20px',
                        marginRight: '10px',
                    }}
                    onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
                />
                <button
                    onClick={sendMessage}
                    style={{
                        padding: '10px 16px',
                        background: '#007bff',
                        color: '#fff',
                        border: 'none',
                        borderRadius: '20px',
                        cursor: 'pointer',
                    }}
                >
                    Send
                </button>
            </div>
        </div>
    );
};

export default PrivateChatPopUpMessage;
