'use strict';

var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var middle = document.querySelector('.middle');
var stompClient = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

let username = null;
let auth = null;
let role = null;
window.addEventListener('DOMContentLoaded', (event) => {
    fetch('http://localhost:8088/api/authentication')
        .then(response => response.json())
        .then(authentication => {
            auth = authentication;
            username = authentication.name;
            console.log(auth);
            for (let i = 0; i < authentication.authorities.length; i++) {
                if (authentication.authorities[i].authority === 'ROLE_ADMIN') return
            }
            for (let i = 0; i < authentication.authorities.length; i++) {
                if (authentication.authorities[i].authority === 'ROLE_MODERATOR') {
                    role = 'ROLE_MODERATOR';
                    document.querySelector('.middle').style.gridTemplateColumns = '1fr 2fr';
                    console.log('User has the authority of ADMIN');
                    break;
                }
            }
        })
});
function connect(event) {

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);


        stompClient.connect({}, onConnected, onError);
}


function onConnected() {


    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
    // Subscribe to the Public Topic
    fetchOldMessages();
    stompClient.subscribe('/topic/public', onMessageReceived);

    connectingElement.classList.add('hidden');
    fetchUsers();
}
function fetchOldMessages() {
    // Make an API request to fetch old messages from the server
    // Use a unique endpoint or modify the existing endpoint to retrieve old messages
    fetch('http://localhost:8088/api/messages')
        .then(response => response.json())
        .then(oldMessages => {
            if (oldMessages == null) return;
            // Process and display old messages
            oldMessages.forEach(message => {
                onMessageReceived({
                    body: JSON.stringify(message)
                });
            });
        })
    //     .catch(error => console.error('Error fetching old messages:', error));
}

function fetchUsers() {
    // Make an API request to fetch the list of users from the server
    fetch('http://localhost:8088/api/users')
        .then(response => response.json())
        .then(users => {
            // Process and display the list of users
            updateUsersList(users);
        })
        .catch(error => console.error('Error fetching users:', error));
}

function updateUsersList(users) {
    // Assuming you have a DOM element to display the user list
    const usersListElement = document.querySelector('.dashboard-container .lines');

    // Clear the existing user list
    // usersListElement.innerHTML = '';

    // Iterate through the fetched users and update the user list
    users.forEach(user => {
        if (user.username === username) return; // Skip the current user
        const userLineElement = document.createElement('div');
        userLineElement.classList.add('line', 'Person-line');

        const titleElement = document.createElement('div');
        titleElement.classList.add('title');

        const dotElement = document.createElement('i');
        dotElement.classList.add(user.status === 'online' ? 'online' : 'offline', 'dot');
        titleElement.appendChild(dotElement);

        const usernameElement = document.createTextNode(user.username);
        titleElement.appendChild(usernameElement);

        userLineElement.appendChild(titleElement);

        let xMarkLinkElement = document.createElement('a');
        xMarkLinkElement.href = `/addModerator/${user.username}`; // Set your actual link here
        let xMarkElement = document.createElement('i');
        xMarkElement.classList.add('fa-solid', 'fa-check');
        if (role == 'ROLE_ADMIN' ) {
        xMarkLinkElement.appendChild(xMarkElement);
        userLineElement.appendChild(xMarkLinkElement);
        }
        // Wrap the "xmark" icon inside a link
        xMarkLinkElement = document.createElement('a');
        xMarkLinkElement.href = `/BanneUser/${user.username}`; // Set your actual link here
        xMarkElement = document.createElement('i');
        xMarkElement.classList.add('fa-solid', 'fa-xmark');
        xMarkLinkElement.appendChild(xMarkElement);
        userLineElement.appendChild(xMarkLinkElement);
        // <i className="fa-solid fa-check"></i>

        usersListElement.appendChild(userLineElement);
    });
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload,messageDB) {
    var message;
    if (payload == null) {
        message = messageDB;
    }else{
        message = JSON.parse(payload.body);
    }

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined! at ' + getDate(message.date);
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left! at ' + getDate(message.date);
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('div');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        avatarElement.classList.add('letter');

        var usernameElement = document.createElement('div');
        usernameElement.classList.add('username');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);

        var messageHead = document.createElement('div');
        messageHead.classList.add('message-head');
        messageHead.appendChild(avatarElement);
        messageHead.appendChild(usernameElement);
        messageElement.appendChild(messageHead);

        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        var date = document.createElement('div');

        date.classList.add('time');
        var dateText = document.createTextNode(getDate(message.date));
        date.appendChild(dateText);
        // date.textContent = getDate(message.date);
        messageElement.appendChild(date);
        messageArea.appendChild(messageElement);


    }

    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}
function getDate(date) {
    const options = {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
    };

    const formattedDate = new Date(date).toLocaleDateString('en-US', options);
    return formattedDate;
}

// usernameForm.addEventListener('submit', connect, true)
window.addEventListener('DOMContentLoaded', connect, true)
messageForm.addEventListener('submit', sendMessage, true)

