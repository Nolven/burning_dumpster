const dgram = require('dgram'); // Модуль для работы с UDP
module.exports = function (agent) {
    // Создание сокета
    const socket = dgram.createSocket({
        type: 'udp4', reuseAddr:
            true
    });

    agent.setSocket(socket);

    socket.on('message', (msg, info) => {
        agent.msgGot(msg)
    });

    socket.sendMsg = function (msg)
    {
        socket.send(Buffer.from(msg), 6000, 'localhost', (err, bytes) => {
            if (err)
             throw err
        })
    };
};
