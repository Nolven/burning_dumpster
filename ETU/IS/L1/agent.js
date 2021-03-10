const Msg = require('./msg');
// Подключение модуля разбора сообщений от сервера
const readline = require('readline');

// Подключение модуля ввода из командной строки
class Agent {
    constructor(speed) {
        this.position = "l"; // По умолчанию - левая половина поля
        this.speed = speed;
        this.run = false; // Игра начата
        this.act = {n: "turn", v: 36 / speed}; // Действия
        this.posX = null;
        this.posY = null;
    }

    msgGot(msg) {
        let data = msg.toString();
        this.processMsg(data);
        this.sendCmd()
    }

    setSocket(socket) {
        this.socket = socket
    }


    sendCommand(cmd, value) {
        this.socket.sendMsg(`(${cmd} ${value})`)
    }

    processMsg(msg) {
        let data = Msg.parseMsg(msg);
        if( !data )
            throw new Error("Parse error\n" + msg);
        switch (data.cmd)
        {
            case "hear":
                if (data.msg.includes('play_on'))
                    this.run = true;
                break;
            case "see":
                this.see(data.p);
                break;
            case "init":
                this.initAgent(data.p);
                break;
        }
    }

    initAgent(p) {
        if (p[0] === "r")
            this.position = "r"; // Правая половина поля
        if (p[1])
            this.id = p[1] // id игрока
    }

    see(parsed)
    {
        const FlagsCoords = {
            ftl50: {x: -50, y: 39}, ftl40: {x: -40, y: 39},
            ftl30: {x: -30, y: 39}, ftl20: {x: -20, y: 39},
            ftl10: {x: -10, y: 39}, ft0: {x: 0, y: 39},
            ftr10: {x: 10, y: 39}, ftr20: {x: 20, y: 39},
            ftr30: {x: 30, y: 39}, ftr40: {x: 40, y: 39},
            ftr50: {x: 50, y: 39}, fbl50: {x: -50, y: -39},
            fbl40: {x: -40, y: -39}, fbl30: {x: -30, y: -39},
            fbl20: {x: -20, y: -39}, fbl10: {x: -10, y: -39},
            fb0: {x: 0, y: -39}, fbr10: {x: 10, y: -39},
            fbr20: {x: 20, y: -39}, fbr30: {x: 30, y: -39},
            fbr40: {x: 40, y: -39}, fbr50: {x: 50, y: -39},
            flt30: {x: -57.5, y: 30}, flt20: {x: -57.5, y: 20},
            flt10: {x: -57.5, y: 10}, fl0: {x: -57.5, y: 0},
            flb10: {x: -57.5, y: -10}, flb20: {x: -57.5, y: -20},
            flb30: {x: -57.5, y: -30}, frt30: {x: 57.5, y: 30},
            frt20: {x: 57.5, y: 20}, frt10: {x: 57.5, y: 10},
            fr0: {x: 57.5, y: 0}, frb10: {x: 57.5, y: -10},
            frb20: {x: 57.5, y: -20}, frb30: {x: 57.5, y: -30},
            fglt: {x: -52.5, y: 7.01}, fglb: {x: -52.5, y: -7.01},
            gl: {x: -52.5, y: 0}, gr: {x: 52.5, y: 0}, fc: {x: 0, y: 0},
            fplt: {x: -36, y: 20.15}, fplc: {x: -36, y: 0},
            fplb: {x: -36, y: -20.15}, fgrt: {x: 52.5, y: 7.01},
            fgrb: {x: 52.5, y: -7.01}, fprt: {x: 36, y: 20.15},
            fprc: {x: 36, y: 0}, fprb: {x: 36, y: -20.15},
            flt: {x: -52.5, y: 34}, fct: {x: 0, y: 34},
            frt: {x: 52.5, y: 34}, flb: {x: -52.5, y: -34},
            fcb: {x: 0, y: -34}, frb: {x: 52.5, y: -34},
            distance(p1, p2)
            {
                return Math.sqrt((p1.x - p2.x) ** 2 + (p1.y - p2.y) ** 2)
            }
        };

        if (this.run)
        {
            const flags = parsed.filter( (obj) =>
                obj.cmd && (obj.cmd.p[0] === 'f' || obj.cmd.p[0] === 'g'));
            const players = parsed.filter( (obj) =>
                obj.cmd && (obj.cmd.p[0] === 'p'));

            if (flags.length < 2) {
                console.log('Нужно больше флагов')
            } else {
                let myPos = null;
                if (flags.length === 2)
                    myPos = this.twoFlags(flags, FlagsCoords);
                else
                    myPos = this.threeFlags(flags, FlagsCoords);

                this.posX = myPos.x;
                this.posY = myPos.y;
                console.log(this.posX, this.posY);

                players.forEach((p) =>
                {
                    const newFlags = this.distForOthers(p, flags);
                    const playerCoords = this.threeFlags(newFlags, FlagsCoords);

                    console.log('Opponent ', playerCoords.x, playerCoords.y)
                });
            }
        }
    }

    distForOthers(player, flags) {
        const newFlags = [];
        newFlags.push(player);
        flags.forEach((flag) => {
            const distanceForFlag = flag.p[0];
            const distanceForPlayer = player.p[0];
            const distanceBetweenFlagAndPlayer = Math.sqrt(Math.abs(
                distanceForFlag**2 + distanceForPlayer**2
                - 2 * distanceForPlayer * distanceForFlag * Math.cos(Math.abs(flag.p[1] - player.p[1]) * Math.PI / 180)
            ));
            newFlags.push(flag);
            newFlags[newFlags.length - 1].p[0] = distanceBetweenFlagAndPlayer
        });
        return newFlags
    }

    twoFlags(p, Flags) {
        let coords = [];
        let distance = [];
        p.forEach((q) => {
            if (q.cmd)
            {
                coords.push(Flags[q.cmd.p.join('')]);
                distance.push(q.p[0])
            }
        });

        let answer;
        if (coords[0].x === coords[1].x) {
            answer = this.byX(coords, distance, 0, 1)
        } else if (coords[0].y === coords[1].y) {
            answer = this.byY(coords, distance, 0, 1)
        } else {
            const alpha = (coords[0].y - coords[1].y)
                / (coords[1].x - coords[0].x);
            const beta = (this.getPow2(coords[1].y) - this.getPow2(coords[0].y)
                + this.getPow2(coords[1].x) - this.getPow2(coords[0].x)
                + this.getPow2(distance[0]) - this.getPow2(distance[1]))
                / (2 * (coords[1].x - coords[0].x));
            const a = this.getPow2(alpha) + 1;
            const b = -2 * (alpha * (coords[0].x - beta) + coords[0].y);
            const c = this.getPow2(coords[0].x - beta) + this.getPow2(coords[0].y)
                - this.getPow2(distance[0]);
            const ys = [];
            ys.push((-b + Math.sqrt(this.getPow2(b) - 4 * a * c)) / (2 * a));
            ys.push((-b - Math.sqrt(this.getPow2(b) - 4 * a * c)) / (2 * a));
            const xs = [];
            xs.push(coords[0].x + Math.sqrt(this.getPow2(distance[0]) - this.getPow2(ys[0] - coords[0].y)));
            xs.push(coords[0].x - Math.sqrt(this.getPow2(distance[0]) - this.getPow2(ys[0] - coords[0].y)));
            xs.push(coords[0].x + Math.sqrt(this.getPow2(distance[0]) - this.getPow2(ys[1] - coords[0].y)));
            xs.push(coords[0].x - Math.sqrt(this.getPow2(distance[0]) - this.getPow2(ys[1] - coords[0].y)));
            answer = this.checkForTwo(xs, ys)
        }
        return answer
    }

    checkForTwo(xs, ys) {
        let answerX = null;
        let answerY = null;
        xs.forEach((x, index) => {
            const ind = (index < 2) ? 0 : 1;
            if (Math.abs(x) <= 54 && Math.abs(ys[ind]) <= 32) {
                answerX = x;
                answerY = ys[ind]
            }
        });
        return {x: answerX, y: answerY}
    }

    byX(coords, distance, q0, q1, q2) {
        const y = (this.getPow2(coords[q1].y) - this.getPow2(coords[q0].y)
            + this.getPow2(distance[q0]) - this.getPow2(distance[q1]))
            / (2 * (coords[q1].y - coords[q0].y));
        const xs = [];
        xs.push(coords[q0].x + Math.sqrt(Math.abs(this.getPow2(distance[q0])
            - this.getPow2(y - coords[q0].y))));
        xs.push(coords[q0].x - Math.sqrt(Math.abs(this.getPow2(distance[q0])
            - this.getPow2(y - coords[q0].y))));
        let answer = null;
        if (q2)
        {
            const forX1 = Math.abs(this.getPow2(xs[0] - coords[q2].x)
                + this.getPow2(y - coords[q2].y) - this.getPow2(distance[q2]));
            const forX2 = Math.abs(this.getPow2(xs[1] - coords[q2].x)
                + this.getPow2(y - coords[q2].y) - this.getPow2(distance[q2]));
            if (forX1 - forX2 > 0) {
                answer = {x: xs[1], y}
            } else {
                answer = {x: xs[0], y}
            }
        } else {
            if (Math.abs(xs[0]) <= 54) {
                answer = {x: xs[0], y}
            } else {
                answer = {x: xs[1], y}
            }
        }
        return answer
    }

    byY(coords, distance, q0, q1, q2) {
        const x = (this.getPow2(coords[q1].x) - this.getPow2(coords[q0].x)
            + this.getPow2(distance[q0]) - this.getPow2(distance[q1]))
            / (2 * (coords[q1].x - coords[q0].x));
        const ys = [];
        ys.push(coords[q0].y + Math.sqrt(Math.abs(this.getPow2(distance[q0])
            - this.getPow2(x - coords[q0].x))));
        ys.push(coords[q0].y - Math.sqrt(Math.abs(this.getPow2(distance[q0])
            - this.getPow2(x - coords[q0].x))));
        let answer = null;
        if (q2) {
            const forY1 = Math.abs(this.getPow2(x - coords[q2].x)
                + this.getPow2(ys[0] - coords[q2].y) - this.getPow2(distance[q2]));
            const forY2 = Math.abs(this.getPow2(x - coords[q2].x)
                + this.getPow2(ys[1] - coords[q2].y) - this.getPow2(distance[q2]));
            if (forY1 - forY2 > 0) {
                answer = {x, y: ys[1]}
            } else {
                answer = {x, y: ys[0]}
            }
        } else {
            if (Math.abs(ys[0]) <= 32) {
                answer = {x, y: ys[0]}
            } else {
                answer = {x, y: ys[1]}
            }
        }
        return answer
    }

    threeFlags(p, Flags) {
        let coords = [];
        let distance = [];
        p.forEach((q) => {
            if (q.cmd && coords.length < 3) {
                if (q.cmd.p && Flags[q.cmd.p.join('')]
                    && coords.filter((c) => c.x === Flags[q.cmd.p.join('')].x).length < 2
                    && coords.filter((c) => c.y === Flags[q.cmd.p.join('')].y).length < 2) {
                    coords.push(Flags[q.cmd.p.join('')]);
                    distance.push(q.p[0])
                }
            }
        });
        if (coords.length < 3) {
            return this.twoFlags(p, Flags)
        } else {
            let answer;
            if (coords[0].x === coords[1].x)
                answer = this.byX(coords, distance, 0, 1, 2);
            else
                if (coords[0].x === coords[2].x)
                    answer = this.byX(coords, distance, 0, 2, 1);
                else
                    if (coords[1].x === coords[2].x)
                        answer = this.byX(coords, distance, 1, 2, 0);
                    else
                        if (coords[0].y === coords[1].y)
                            answer = this.byY(coords, distance, 0, 1, 2);
                        else
                            if (coords[0].y === coords[2].y)
                                answer = this.byY(coords, distance, 0, 2, 1);
                            else
                                if (coords[1].y === coords[2].y) {
                                    answer = this.byY(coords, distance, 1, 2, 0);
            } else {
                const alpha1 = (coords[0].y - coords[1].y)
                    / (coords[1].x - coords[0].x);
                const beta1 = (this.getPow2(coords[1].y) - this.getPow2(coords[0].y)
                    + this.getPow2(coords[1].x) - this.getPow2(coords[0].x)
                    + this.getPow2(distance[0]) - this.getPow2(distance[1]))
                    / (2 * (coords[1].x - coords[0].x));
                const alpha2 = (coords[0].y - coords[2].y)
                    / (coords[2].x - coords[0].x);
                const beta2 = (this.getPow2(coords[2].y) - this.getPow2(coords[0].y)
                    + this.getPow2(coords[2].x) - this.getPow2(coords[0].x)
                    + this.getPow2(distance[0]) - this.getPow2(distance[2]))
                    / (2 * (coords[2].x - coords[0].x));
                let y = ((beta1 - beta2) / (alpha2 - alpha1));
                let x = alpha1 * y + beta1;
                answer = {x, y}
            }
            return answer
        }
    }

    getPow2(x) {
        return x * x
    }

    sendCmd() {
        if (this.run) { // Игра начата
            if (this.act) { // Есть команда от игрока
                if (this.act.n === "kick") // Пнуть мяч
                    this.sendCommand(this.act.n, this.act.v + " 0");
                else // Движение и поворот
                    this.sendCommand(this.act.n, this.act.v)
            }
            // this.act = null // Сброс команды
        }
    }
}

module.exports = Agent;
