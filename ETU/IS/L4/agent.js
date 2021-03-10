const Msg = require('./msg');

let goalieTree = require('./trees/goalie/goalieDecisionTree');
let baseTree = require("./trees/player/base");
let attackTree = require("./trees/player/attack");
let passerTree = require("./trees/player/pass");

// Подключение модуля ввода из командной строки
class Agent {
    constructor(goalie) {
        this.run = false;

        this.said = null;
        this.heard = false;

        if( goalie === 'y' )
        {
            this.goalie = true;
            this.side = 'r';
            this.role = 'g';
        }
        else
        {
            this.side = "l";
            this.goalie = false;
            this.role = null;
        }
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
        if( this.said )
        {
            let temp = this.act;
            this.act = {n: "say", v: `${this.said}`};
            this.sendCmd();

            this.act = temp;
            this.said = null;
        }
        this.sendCmd();
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
            case "hear":
                console.log(data);
                if (data.msg.includes('play_on'))
                    this.run = true;
                if ( data.msg.includes('goal') )
                {
                    attackTree.reset();
                    passerTree.reset();
                    goalieTree.reset();

                    if( this.role !== "g" )
                        this.role = null;

                    this.said = null;
                    this.heard = false;

                    this.run = false;

                    this.act = {n: "move", v: "1 1"}
                }
                if ( data.msg.includes("goo") )
                    this.heard = true;
                if( !data.msg.includes("self") )
                    if( data.msg.includes('passer') && this.role !== "g")
                        this.role = "a";
                if ( data.msg.includes('before_kick_off') )
                    this.run = false;
                break;

            case "see":
                if( this.run )
                    this.analyzeEnv(data.p); // Обработка
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
        switch (this.role)
        {
            case "g":
                this.act = goalieTree.getAction(p, this.side);
                break;

            case "a":
                this.act = attackTree.getAction(p, this.heard);
                break;

            case "p":
                this.act = passerTree.getAction(p, this.teamName);
                this.said = passerTree.say;
                break;

            default:
                this.act = baseTree.getAction(p, this.teamName);
                this.role = baseTree.role;
                if( this.role === "p" )
                    this.said = "passer";
        }
    }

    sendCmd()
    {
        if (this.run && this.act)
        {
            this.socketSend(this.act.n, this.act.v);
            this.act = null;
        }
    }
}

module.exports = Agent;
