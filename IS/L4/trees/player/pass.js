module.exports = {
    //const
    passDist: 40,
    team: null,

    //constantly updated
    seen: [],
    ball: null,
    action: null,
    say: null,
    player: null,

    //state
    active: true,

    reset: function()
    {
        this.active = true;
        this.say = null;
        this.action = null;
    },

    isPlayerCloseEnough: function()
    {
        if( this.player.p[0] < this.passDist )
        {
            this.action = {n: "kick", v: `100 ${this.player.p[1]}`};
            this.say = "goo";
            console.log("GOGGOGOGOGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
            this.active = false;
        }
        else
            this.action = {n: "kick", v: `${15} ${this.player.p[1]}`}
    },

    isPlayerInFOV: function()
    {
        this.player = this.seen.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'p' && obj.cmd.p[1] === `"${this.team}"`)[0];

        if( this.player )
            this.isPlayerCloseEnough();
        else
            this.action = {n: "kick", v: `${10} ${80}`}

    },

    isBallClose: function()
    {
        if( this.ball.p[0] < 1 )
            this.isPlayerInFOV();
        else
            this.action = {n: "dash", v: `${100} ${this.ball.p[1]}`}
    },

    isBall: function()
    {
        this.ball = this.seen.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'b')[0];

        if( this.ball )
            this.isBallClose();
        else
            this.action = {n: "turn", v: `80`};
    },

    getAction: function (seen, team)
    {
        this.seen = seen;
        this.team = team;

        if( this.active )
            this.isBall();
        else
        {
            this.say = null;
            return null;
        }
        console.log("Passer tree", this.action);

        return this.action;
    }
};