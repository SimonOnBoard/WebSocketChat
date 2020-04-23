<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <meta http-equiv="Cache-Control" content="no-cache">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="css/bootstrap.min.css" type="text/css ">
    <link href="css/style.css" rel="stylesheet" type="text/css">
    <title>Чат</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@9"></script>
    <script>
        var actualSocket;
        var webSocket;
        var base = "http://localhost:8080";

        function connect() {
            webSocket = new SockJS("http://localhost:8080/rootMenu");
            actualSocket = webSocket;
            webSocket.onopen = function (resp) {
                console.log(resp);
                console.log("opened");
            };
            webSocket.onclose = function (resp) {
                console.log(resp);
                console.log("closed");
                if (resp.code === 1003) {
                    startChatSocket();
                    console.log("redirected to chat")
                }
            };
            webSocket.onerror = function (resp) {
                console.log(resp);
                console.log("error");
            };
            webSocket.onmessage = function receiveMessage(response) {
                let data = response['data'];
                let json = JSON.parse(data);
                if (json['startChat'] !== undefined) {
                    webSocket.close();
                    actualSocket = undefined;
                    startChatSocket();
                } else {
                    if (json['status'] === "200") {
                        webSocket.close();
                        $('#content').html('');
                        $('#content').html(json['content']);
                        actualSocket = undefined;
                    }
                }
            }
        }

        var formWebSocket;

        function startAnotherSocket(address) {
            console.log(base + address);
            formWebSocket = new SockJS(base + address);
            actualSocket = formWebSocket;
            formWebSocket.onopen = function (resp) {
                console.log(resp);
                console.log("opened");
            };
            formWebSocket.onclose = function (resp) {
                console.log(resp);
                console.log("closed");
            };
            formWebSocket.onerror = function (resp) {
                console.log(resp);
                console.log("error");
            };
            formWebSocket.onmessage = function receiveMessage(response) {
                let data = response['data'];
                let json = JSON.parse(data);
                if (json['status'] === "403") {
                    $('#content').append(json['message']);
                } else {
                    formWebSocket.close();
                    actualSocket = undefined;
                    if (json['socketRedirect'] !== undefined) {
                        if (json['content'] !== undefined) {
                            $('#content').html('');
                            $('#content').html(json['content']);
                        } else {
                            document.cookie = "auth" + "=" + json['token'] + ";";
                            startChatSocket();
                        }
                    }
                }
            }
        }

        var chatSocket;
        var pageId;
        var chatId;
        var chatName;
        var messageForm = '<label for="message">Текст сообщения</label>\n' +
            '    <input name="message" id="message" placeholder="Сообщение">\n' +
            '    <button onclick="sendMessage($(\'#message\').val())">Отправить</button>';
        var chatForm = '<label for="name">Введите название чата</label>\n' +
            '    <input name="name" id="name" placeholder="Название чата">\n' +
            '    <button onclick="startChat($(\'#name\').val())">Отправить</button>';
        var joinForm = '<label for="code">Введите код для присоединения</label>\n' +
            '    <input name="code" id="code" placeholder="Код для присоединения">\n' +
            '    <button onclick="joinChat($(\'#code\').val())">Отправить</button>';
        var buttons = ' <button type="button" onclick="getForm(\'login\')">Залогиниться</button>\n' +
            '    <button type="button" onclick="getForm(\'registration\')">Зарегистрироваться</button>';
        var loggout = '<button type="button" onclick="logout()">Выйти</button>';
        var chatsButton = '<button type="button" onclick="getAllChats()">Показать чаты</button>';
        function startChatSocket() {
            chatSocket = new SockJS(base + "/chat");
            actualSocket = chatSocket;
            $("#logout").html(loggout);
            chatSocket.onopen = function (resp) {
                console.log(resp);
                console.log("opened");
            };
            chatSocket.onclose = function (resp) {
                console.log(resp);
                console.log("closed");
            };
            chatSocket.onerror = function (resp) {
                console.log(resp);
                console.log("error");
            };
            chatSocket.onmessage = function receiveMessage(response) {
                let data = response['data'];
                let json = JSON.parse(data);
                if (json["pageId"] !== undefined) {
                    pageId = json["pageId"];
                    chatId = json["chatId"];
                }
                if (json['chats'] !== undefined) {
                    $('#content').html('');
                    var html = '';
                    for (var i = 0; i < json['chats'].length; i++) {
                        html += '<a id=\"' + json['chats'][i].id + '\"' + ' href="#"' + ' onclick=\"' + 'getChatPage(\'' + json['chats'][i].id + '\'' + ',\'' +  json['chats'][i].name + '\');' + '\">' + json['chats'][i].name + '</a><br/>';
                    }
                    console.log(html);
                    $('#content').html(html);
                    $('#joinForm').html(joinForm);
                    $('#chatForm').html(chatForm);
                    $("#messageForm").html('');
                }
                if (json["messages"] !== undefined) {
                    $('#content').html('');
                    var html = '';
                    for (var i = 0; i < json['messages'].length; i++) {
                        html += '<br/><p>' + json['messages'][i].ownerName + '</p>';
                        html += '<p>' + json['messages'][i].text + '</p>';
                        html += '<p>' + json['messages'][i].time + '</p>';
                    }
                    console.log(html);
                    $('#content').html(html);
                    $('#joinForm').html('');
                    $('#chatForm').html('');
                    $("#messageForm").html(messageForm);
                    $("#messageForm").append("Current chat name:" + chatName);
                    $("#messageForm").append(chatsButton);
                }
                if (json['chat'] !== undefined) {
                    $('#chatCode').remove();
                    Swal.fire({
                        timer: 7000,
                        text: json['message'] + ": " + json['chat'].code
                    });
                    var x = document.createElement("INPUT");
                    x.setAttribute("type", "text");
                    x.setAttribute("value", json['chat'].code);
                    x.setAttribute("id", "chatCode");
                    $('#chatForm').append(x);
                    html = '';
                    html += '<a id=\"' + json['chat'].id + '\"' + ' href="#"' + ' onclick=\"' + 'getChatPage(\'' + json['chat'].id + '\'' + ',\''+ json['chat'].name +'\');' + '\">' + json['chat'].name + '</a><br/>';
                    $('#content').append(html);
                } else {
                    if (json['message'] !== undefined) {
                        $('#content').append("<br/><p>" + json['message'].ownerName + "</p>");
                        $('#content').append("<p>" + json['message'].text + "</p>");
                        $('#content').append("<p>" + json['message'].time + "</p>");
                    }
                }
                if (json['info'] !== undefined) {
                    Swal.fire({
                        timer: 7000,
                        text: json['info']
                    });
                }
                $("#message").val('');
                $("#name").val('');
                $("#code").val('');
                scrollBottom();
            }

        }

        var height = 15;

        function scrollBottom() {
            var attempt = 25;
            var intS = 0;

            function scrollToEndPage() {
                console.log("hight:" + height + " scrollHeight:" + document.body.scrollHeight + " att:" + attempt);

                if (height < document.body.scrollHeight) {
                    height = document.body.scrollHeight;
                    window.scrollTo(0, height);
                    attempt++;
                    height = parseInt(height) + attempt;
                } else {
                    clearInterval(intS);
                }
            }

            intS = setInterval(scrollToEndPage, 100);
        }

        function startChat(name) {
            var data = {
                "option": 'startChat',
                "name": name
            };
            actualSocket.send(JSON.stringify(data));
        }

        function joinChat(code) {
            var data = {
                "option": 'joinChat',
                "code": code
            };
            actualSocket.send(JSON.stringify(data));
        }

        function sendMessage(message) {
            var data = {
                "option": 'sendMessage',
                "pageId": pageId,
                "chatId": chatId,
                "message": message
            };
            actualSocket.send(JSON.stringify(data));
        }
        function getAllChats() {
            let data = {
                "option": 'getChats',
                "pageId": pageId,
                "chatId": chatId
            };
            actualSocket.send(JSON.stringify(data));
            pageId = undefined;
            chatId = undefined;
            console.log('asking for chats');
        }
        function getChatPage(id,name) {
            let data = {
                "option": 'chatPage',
                "chatId": id,
            };
            chatName = name;
            actualSocket.send(JSON.stringify(data));
        }

        function getFormData(form) {
            var unindexed_array = form.serializeArray();
            var indexed_array = {};

            $.map(unindexed_array, function (n, i) {
                indexed_array[n['name']] = n['value'];
            });

            return indexed_array;
        }

        function x() {
            var form = $("#form");
            if (actualSocket === undefined || actualSocket.readyState === 3) {
                startAnotherSocket(form.attr('action').toString());
            }
            var data = getFormData(form);
            waitForSocketConnection(actualSocket, function () {
                console.log("message sent!!!");
                actualSocket.send(JSON.stringify(data));
            });
        }

        // Make the function wait until the connection is made...
        function waitForSocketConnection(socket, callback) {
            setTimeout(
                function () {
                    if (socket.readyState === 1) {
                        console.log("Connection is made");
                        if (callback != null) {
                            callback();
                        }
                    } else {
                        if (socket.readyState !== 2 && socket.readyState !== 3) {
                            console.log("wait for connection...");
                            waitForSocketConnection(socket, callback);
                        }
                    }

                }, 5); // wait 5 milisecond for the connection...
        }

        function getForm(status) {
            if (actualSocket === undefined) {
                connect();
            }
            waitForSocketConnection(actualSocket, function () {
                console.log("message sent!!!");
                let message = {"status": status.toString()};
                actualSocket.send(JSON.stringify(message));
            });
        }

        function logout() {
            clearCookies();
            var data = {
                "option": 'logout',
                "pageId": pageId,
                "chatId": chatId
            };
            actualSocket.send(JSON.stringify(data));
            actualSocket.close();
            actualSocket = undefined;

            $("#logout").html('');
            $("#content").html('');
            $('#joinForm').html('');
            $('#chatForm').html('');
            $('#messageForm').html('');
            $("#content").html(buttons);
        }

        function clearCookies() {

            var allCookies = document.cookie.split(';');
            // The "expire" attribute of every cookie is
            // Set to "Thu, 01 Jan 1970 00:00:00 GMT"
            for (var i = 0; i < allCookies.length; i++)
                document.cookie = allCookies[i] + "=;expires="
                    + new Date(0).toUTCString();
        }
    </script>
</head>
<body onload="connect()">
<div id="content">
    <button type="button" onclick="getForm('login')">Залогиниться</button>
    <button type="button" onclick="getForm('registration')">Зарегистрироваться</button>
</div>
<div id="messageForm">
</div>
<div id="chatForm">
</div>
<div id="joinForm">
</div>
<div id="logout">
</div>
</body>
</html>