module.exports = {
    ball: {},
    target: {},
    targets: ["fc", "gr", "fct"],
    nextTNum: 0,
    parsed: [],

    action: {},

    isBallCloseToTarget: function()
    {
        console.log(this.ball.p[0] - this.target.p[0]);
        if( Math.abs(this.ball.p[0] - this.target.p[0]) < 3 )
        {
            this.nextTNum += 1;
        }
        else
            this.action = {n: "kick", v: `${this.target.p[0]} ${this.target.p[1]}`};
    },

    isTargetInFOV: function()
    {
        this.target = this.parsed.filter(
            (obj) => obj.cmd && obj.cmd.p.join('') === this.targets[this.nextTNum])[0];
        if( this.target )
        {
            console.log(this.targets[this.nextTNum]);
            this.isBallCloseToTarget();
        }
        else
            this.action = {n: "kick", v: `15 80`};
    },

    isBallClose: function()
    {
        if( this.ball.p[0] < 1 )
            this.isTargetInFOV();
        else
            this.action = {n: "dash", v: `100 ${this.ball.p[1]}`}
    },

    ballInFrontOfUs: function()
    {
        if( this.ball.p[1] < 0.5 )
            this.isBallClose();
        else
            this.action = {n: "turn", v: `${this.ball.p[1]}`}
    },

    isBallSeen: function()
    {
        this.ball = this.parsed.filter(
            (obj) => obj.cmd && obj.cmd.p[0] === 'b')[0];

        if( this.ball )
            this.ballInFrontOfUs();
        else
            this.action = {n: "turn", v: "80"};
    },

    getAction: function (parsed)
    {
        this.parsed = parsed;
        this.isBallSeen();
        return this.action;
    }
};
