const Msg = require('./msg');

let tree = require("./decisionTree");

class Agent {
    constructor(speed) {
        this.run = false;
    }

    msgGot(msg)
    {
        let data = msg.toString('utf8');
        this.processMsg(data);
        this.sendCmd()
    }

    setSocket(socket) { // Настройка сокета
        this.socket = socket
    }

    socketSend(cmd, value)
    {
        this.socket.sendMsg(`(${cmd} ${value})`)
    }

    processMsg(msg) { // Обработка сообщения
        let data = Msg.parseMsg(msg); // Разбор сообщения
        if (!data)
            throw new Error("Parse error\n" + msg);

        switch (data.cmd)
        {
            case "see":
                this.analyzeEnv(data.p); // Обработка
                break;
            case "hear":
                if (data.msg.includes('play_on'))
                    this.run = true;
                if (data.msg.includes('goal'))
                    tree.nextTNum = 0;
                if( data.msg.includes('before_kick_off') )
                    this.run = false;
                break;
            case "init":
                this.initAgent(data.p);//Инициализация
                break;
        }
    }

    initAgent(p) {
        if (p[0] === "r")
         this.position = "r"; // Правая половина поля
        if (p[1])
         this.id = p[1] // id игрока
    }

    analyzeEnv(p) { // Анализ сообщения
        if (this.run)
        {
           this.act = tree.getAction(p);
        }
    }

    sendCmd() {
        if (this.run) { // Игра начата
            if (this.act) { // Есть команда от игрока
                this.socketSend(this.act.n, this.act.v)
            }
            this.act = null // Сброс команды
        }
    }
}

module.exports = Agent; // Экспорт игрока
