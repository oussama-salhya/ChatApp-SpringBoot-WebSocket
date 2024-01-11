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
const msgContainer = document.createElement('div');
msgContainer.classList.add('message-container');
msgContainer.id = 'message-container';
document.body.appendChild(msgContainer);
let username = null;
let auth = null;
let role = null;
let status = null;
// const csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');
// console.log(csrfToken);
window.addEventListener('DOMContentLoaded', fetchAuth);
function fetchAuth() {
    fetch('http://localhost:8088/api/authentication')
        .then(response => response.json())
        .then(authentication => {
            auth = authentication;
            username = authentication.name;

            document.querySelector('.line.user-line').innerHTML = ` <div class="title">
                    <i class="dot status"></i>
                    ${username}
                </div>
                <br />`;
        })
}
function fetchRole(users) {
    console.log(users)
    let testRole = users.find(user =>user.username===username && user.banned === true);

    if (testRole) {
        document.querySelector('.input-group').style.display = 'none';
        status = 'baned';
        document.querySelector('.line.user-line .dot').classList.remove('online');
        document.querySelector('.line.user-line .dot').classList.add('baned');
    }else{
        document.querySelector('.line.user-line .dot').classList.add('online');
        document.querySelector('.line.user-line .dot').classList.remove('baned');
        document.querySelector('.input-group').style.display = 'flex';
    }



    testRole = users.filter(user =>user.username===username &&  user.appRoles.find(roleAP => roleAP.role === 'ADMIN'));
    if (testRole.length === 1) {
            role = 'ROLE_ADMIN';
            console.log('User has the authority of ADMIN');
            return;
    }
    testRole = users.filter(user =>user.username===username && user.appRoles.find(roleAP => roleAP.role === 'MODERATOR'));
    if (testRole.length === 1) {
            role = 'ROLE_MODERATOR';
            console.log('User has the authority of MOD');
        document.querySelector('.middle').style.gridTemplateColumns = '1fr 2fr';
        document.querySelector('.input-group').style.borderBottomRightRadius = '1rem';
        document.querySelector('#chat-page').style.borderBottomRightRadius = '1rem';
        return;
    }
    document.querySelector('.middle').style.gridTemplateColumns = '1fr 2fr';
    document.querySelector('.input-group').style.borderBottomRightRadius = '1rem';
    document.querySelector('#chat-page').style.borderBottomRightRadius = '1rem';
    console.log('User has the authority of USER');
    role = 'ROLE_USER';
}
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
    stompClient.subscribe('/topic/changes', onBanNotificationReceived);

    connectingElement.classList.add('hidden');
    fetchUsers();
}
function onBanNotificationReceived(payload) {
    console.log("sss")
    var banNotification = JSON.parse(payload.body);
    // Handle the ban notification, e.g., show a notification to the user
    console.log('Ban Notification:', banNotification);
}

function fetchOldMessages() {
    // Make an API request to fetch old messages from the server
    // Use a unique endpoint or modify the existing endpoint to retrieve old messages
    fetch('http://localhost:8088/api/messages')
        .then(response => response.json())
        .then(oldMessages => {
            // if (oldMessages == null) return;
            // Process and display old messages
            oldMessages.forEach(message => {
                onMessageReceived({
                    body: JSON.stringify(message)
                });
            });
        })
        .catch(error => console.error('Error fetching old messages:', error));
}

function fetchUsers() {
    // Make an API request to fetch the list of users from the server
    fetch('http://localhost:8088/api/users')
        .then(response => response.json())
        .then(users => {
            fetchRole(users);

            console.log(users);
            // Process and display the list of users
            const usersListElement = document.querySelector('.dashboard-container .lines');
            // Clear the existing user list
            usersListElement.innerHTML = "<div class=\"line main-line\">\n" +
                "                    <div class=\"title\">\n" +
                "                        <i class=\"fa-solid fa-bars\"></i>\n" +
                "                        Dashboard\n" +
                "                    </div>\n" +
                "                </div>" + "<div class=\"line team-line\">\n" +
                "                    <div class=\"title\">\n" +
                "                        <i class=\"fa-solid fa-user\"></i>\n" +
                "                        Members\n" +
                "                    </div>\n" +
                "                    <!-- <a href=\"\"> <i class=\"fa-solid fa-xmark\"></i> </a> -->\n" +
                "                </div>";

            updateUsersList(users.filter(user => user.status === 'online' && user.banned === false),false);
            updateUsersList(users.filter(user => user.status === 'offline' && user.banned === false),false);
            if (role === 'ROLE_ADMIN' || role === 'ROLE_MODERATOR' ) {
            var newHTML = "</div>" +
                    "<div class=\"line team-line\">\n" +
                    "    <div class=\"title\">\n" +
                    "        <i class=\"fa-solid fa-user\"></i>\n" +
                    "        Banned\n" +
                    "    </div>\n" +
                    "    <!-- <a href=\"\"> <i class=\"fa-solid fa-xmark\"></i> </a> -->\n" +
                    "</div>";

// Append without overwriting existing content
                usersListElement.insertAdjacentHTML('beforeend', newHTML);
            updateUsersList(users.filter(user => user.banned === true),true);
            }
            if (role === 'ROLE_ADMIN'){
            console.log(users);
                const dahsbordModerator = document.querySelector('.dashboard-moderator');
                const usersListElement = dahsbordModerator.querySelector('.lines');

                // Clear the existing user list
                usersListElement.innerHTML = " <div class=\"line main-line\">\n" +
                    "                    <div class=\"title\">\n" +
                    "                        <i class=\"fa-solid fa-bars\"></i>\n" +
                    "                        Admin\n" +
                    "                    </div>\n" +
                    "                </div>";
                updateModeratorList(users.filter(user => user.appRoles.find(role => role.role === 'ADMIN')));
                let ModHtml = "<div class=\"line team-line\">\n" +
                    "                    <div class=\"title\">\n" +
                    "                        <i class=\"fa-solid fa-user\"></i>\n" +
                    "                        Moderator\n" +
                    "                    </div>\n" +
                    "                    <!-- <a href=\"\"> <i class=\"fa-solid fa-xmark\"></i> </a> -->\n" +
                    "                </div>";
                usersListElement.insertAdjacentHTML('beforeend', ModHtml);
                updateModeratorList(users.filter(user => user.appRoles.find(role => role.role === 'MODERATOR')));
            }
        })
        .catch(error => console.error('Error fetching users:', error));
}

function updateUsersList(users,isBanned) {
    let mark= 'fa-xmark';
    let link = 'ban';
    let message = 'ban user';
    if (isBanned){
        mark = 'fa-check';
        link = 'unban';
        message = 'unban user';
    }
    // Assuming you have a DOM element to display the user list
    const usersListElement = document.querySelector('.dashboard-container .lines');
    users.forEach(user => {
        if (user.username === username || user.appRoles.find(role => role.role === 'ADMIN')) return; // Skip the current user

        const userLineElement = document.createElement('div');
        userLineElement.classList.add('line', 'Person-line');

        const titleElement = document.createElement('div');
        titleElement.classList.add('title');

        const dotElement = document.createElement('i');
        if (isBanned) {
            dotElement.classList.add('baned', 'dot');
        }else {
            dotElement.classList.add(user.status === 'online' ? 'online' : 'offline', 'dot');
        }
        titleElement.appendChild(dotElement);

        const usernameElement = document.createTextNode(user.username);
        titleElement.appendChild(usernameElement);

        userLineElement.appendChild(titleElement);

        if (role === 'ROLE_ADMIN' && !isBanned && !user.appRoles.find(role => role.role === 'MODERATOR')) {
        let xMarkLinkElement = document.createElement('a');
        let xMarkElement = document.createElement('i');
        xMarkElement.classList.add('fa-solid', 'fa-plus','hover-icon');
            xMarkLinkElement.onmouseenter = () => { showMessage('add Moderator') };
            xMarkLinkElement.onmouseleave = () => { hideMessage() };
        xMarkLinkElement.appendChild(xMarkElement);
        userLineElement.appendChild(xMarkLinkElement);
            xMarkElement.addEventListener('click', (event) => {
                event.preventDefault();
                fetch('http://localhost:8088/api/addModerator', {
                    method: 'PUT',
                    headers: {
                        // 'X-XSRF-TOKEN': csrfToken,
                        'Content-Type': 'application/json'
                        // '_csrf': csrfToken
                        // 'X-CSRF-TOKEN': csrfToken  // Include the CSRF token in the headers
                    },
                    body: JSON.stringify({ username: user.username })
                })
                    .then(response => {
                        if (response.status === 405) {
                            window.location.href = './echec';
                            return
                        }
                        response.text();
                    })
                    .then(data => {
                        changes(username, 'MOD', user.username)
                        fetchUsers(users);
                    })
                    .catch(error => console.error('Error banning user:', error));
            });
        }
        if (role === 'ROLE_ADMIN' || role === 'ROLE_MODERATOR' ) {
            let xMarkLinkElement = document.createElement('a');
            // xMarkLinkElement.href = `/deleteModerator/${user.username}`; // Set your actual link here
            let xMarkElement = document.createElement('i');

        xMarkElement.classList.add('fa-solid', mark,'hover-icon');

        xMarkLinkElement.onmouseenter = () => { showMessage(message) };
        xMarkLinkElement.onmouseleave = () => { hideMessage() };
        xMarkLinkElement.appendChild(xMarkElement);
        userLineElement.appendChild(xMarkLinkElement);

        xMarkElement.addEventListener('click', (event) => {
            event.preventDefault();
                    fetch('http://localhost:8088/api/' + link, {
                        method: 'PUT',
                        headers: {
                            // 'X-XSRF-TOKEN': csrfToken,
                            'Content-Type': 'application/json'
                            // '_csrf': csrfToken
                            // 'X-CSRF-TOKEN': csrfToken  // Include the CSRF token in the headers
                        },
                                body: JSON.stringify({ username: user.username })
                    })
                        .then(response =>{
                            if (response.status === 405)  {window.location.href = './echec';return}
                            response.text()

                        })
                        .then(data =>{
                            changes(username, link.toUpperCase(), user.username)
                            fetchUsers(users);
                        })
                        .catch(error => console.error('Error banning user:', error));
        });
        }
        usersListElement.appendChild(userLineElement);
    });

}
function updateModeratorList(users) {
    // Assuming you have a DOM element to display the user list
    const dahsbordModerator = document.querySelector('.dashboard-moderator');
    const usersListElement = dahsbordModerator.querySelector('.lines');

    // Iterate through the fetched users and update the user list
    users.forEach(user => {
        if (user.username === username) return;



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
        // usersListElement.appendChild(userLineElement);
        let xMarkLinkElement = document.createElement('a');
        // xMarkLinkElement.href = `/deleteModerator/${user.username}`; // Set your actual link here
        let xMarkElement = document.createElement('i');
        xMarkElement.classList.add('fa-solid', 'fa-xmark','hover-icon');
        xMarkLinkElement.onmouseenter = () => { showMessage('delete Moderator') };
        xMarkLinkElement.onmouseleave = () => { hideMessage() };

        xMarkLinkElement.appendChild(xMarkElement);
        userLineElement.appendChild(xMarkLinkElement);
        usersListElement.appendChild(userLineElement);
        xMarkElement.addEventListener('click', (event) => {
            event.preventDefault();
            fetch('http://localhost:8088/api/deleteModerator', {
                method: 'PUT',
                headers: {
                    // 'X-XSRF-TOKEN': csrfToken,
                    'Content-Type': 'application/json'
                    // '_csrf': csrfToken
                    // 'X-CSRF-TOKEN': csrfToken  // Include the CSRF token in the headers
                },
                body: JSON.stringify({ username: user.username })
            })
                .then(response =>{
                    if (response.status === 405)  {window.location.href = './echec';return}
                 response.text();})
                .then(data =>{
                    changes(username, 'UNMOD', user.username)
                    fetchUsers();
                })
                .catch(error => console.error('Error banning user:', error));
        });
    });
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event,isAction,user,action,actionedUser) {
    console.log(action)
    if (isAction) {

            var chatMessage = {
                sender: username,
                content: actionedUser,
                type: action
            };
    console.log(chatMessage)
            stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));

    }else{
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
}
function changes(user,action,actionedUser) {
    console.log('changes')
    sendMessage(null,true,user,action,actionedUser);
    // stompClient.send("/app/chat.changes", {}, JSON.stringify({sender: username, type: 'JOIN'}));
}


function onMessageReceived(payload,messageDB) {

    var message;
    if (payload == null) {
        message = messageDB;
    }else{
        message = JSON.parse(payload.body);
    }

    var messageElement = document.createElement('li');
    if (message.type ==='MOD'){
        fetchUsers();
        messageElement.classList.add('event-message');
        message.content = message.content + ' Become a Moderator! at ' + getDate(message.date) + ' by ' + message.sender;
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
    }else if (message.type ==='UNMOD'){
        fetchUsers();
        messageElement.classList.add('event-message');
        message.content = message.content + ' deleted from Moderators! at ' + getDate(message.date) + ' by ' + message.sender;
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
    }else if (message.type ==='UNBAN'){
        fetchUsers();

        messageElement.classList.add('event-message');
        message.content = message.content + ' Unbanned! at ' + getDate(message.date) + ' by ' + message.sender;
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
    }else if (message.type ==='BAN'){
        fetchUsers();

        messageElement.classList.add('event-message');
        message.content = message.content + ' Banned! at ' + getDate(message.date) + ' by ' + message.sender;
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
    }else if(message.type === 'JOIN') {
        fetchUsers();
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined! at ' + getDate(message.date);
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);

    } else if (message.type === 'LEAVE') {
        fetchUsers();
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left! at ' + getDate(message.date);
        var textElement = document.createElement('p');
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageElement.appendChild(textElement);

        messageArea.appendChild(messageElement);
    } else if (message.type === 'CHAT') {
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
    };

    const formattedDate = new Date(date).toLocaleDateString('en-US', options);
    return formattedDate;
}

// usernameForm.addEventListener('submit', connect, true)
window.addEventListener('DOMContentLoaded', connect, true)
messageForm.addEventListener('submit', sendMessage, true)

function showMessage(message) {
    const messageContainer = document.getElementById('message-container');
    messageContainer.innerHTML = message;
    messageContainer.style.display = 'block';

    // Set the position of the message container based on mouse coordinates
    // const x = event.clientX;
    // const y = event.clientY
    // messageContainer.style.left = `${x}px`;
    // messageContainer.style.top = `${y}px`;
    messageContainer.style.left = `0px`;
    messageContainer.style.top = `0px`;
}

function hideMessage() {
    const messageContainer = document.getElementById('message-container');
    messageContainer.style.display = 'none';
}
