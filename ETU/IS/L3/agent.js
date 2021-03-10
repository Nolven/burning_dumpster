const Msg = require('./msg');
const goalieTree = require('./trees/goalie/goalieDecisionTree');
let playerTree = require('./trees/player/tandemTree');

// Подключение модуля ввода из командной строки
class Agent {
    constructor(goalie) {
        this.side = "l";
        this.run = false;
        if( goalie === 'y' )
        {
            this.goalie = true;
            this.side = 'r';
        }
        else
            this.goalie = false;
    }

    init(teamName, version)
    {
        if( this.goalie )
            this.socket.sendMsg(`(init ${teamName} (version ${version}) (goalie))`);
        else
            this.socket.sendMsg(`(init ${teamName} (version ${version}))`);
        this.teamName = teamName;
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

        console.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        console.log(data);
        console.log("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        switch (data.cmd)
        {
            case "see":
                this.analyzeEnv(data.p); // Обработка
                break;
            case "hear":
                if (data.msg.includes('play_on'))
                    this.run = true;
                if (data.msg.includes('goal')){}
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
        if( this.goalie )
            this.act = goalieTree.getAction(p, this.side);
        else
            this.act = playerTree.getAction(p, this.teamName);

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
