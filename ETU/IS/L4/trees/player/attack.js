module.exports = {

    //const
    team: null,
    speedToFlag: 40,
    flag: "fprb",

    //constantly updated
    seen: [],
    ball: null,
    action: null,
    f: null,
    g: null,

    //state
    flagReached: false,
    heard: false,

    reset: function()
    {
        console.log("attacker tree reset");
        this.flagReached = false;
        this.heard = false;
        this.action = null;
    },

    areGatesClose: function()
    {
        if( this.g.p[0] < 15 )
            this.action = {n: "kick", v: `100 ${this.g.p[1]}`};
        else
            this.action = {n: "kick", v: `40 ${this.g.p[1]}`};
    },

    areGatesInFOV: function()
    {
        this.g = this.seen.filter( (obj) =>
            obj.cmd && obj.cmd.p.join('') === 'gr')[0];
        if( this.g )
            this.areGatesClose();
        else
            this.action = {n: "kick", v: `5 80`};
    },

    isBallClose: function()
    {
        if( this.ball.p[0] < 1 )
            this.areGatesInFOV();
        else
            this.action = {n: "dash", v: `80 ${this.ball.p[1]}`};
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

    isFlagClose: function()
    {
        if( this.f.p[0] < 3 )
        {
            this.flagReached = true;
            this.isBall();
        }
        else
            this.action = {n: "dash", v: `${this.speedToFlag} ${this.f.p[1]}`};
    },

    isFlag: function()
    {
        this.f = this.seen.filter( (obj) =>
            obj.cmd && obj.cmd.p.join('') === this.flag)[0];

        if( this.f )
            this.isFlagClose();
        else
            this.action = {n: "turn", v: "80"};
    },

    isActive: function(){
        if( this.flagReached || this.heard )
            this.isBall();
        else
            this.isFlag();
    },

    getAction: function (seen, heard)
    {
        this.seen = seen;
        this.heard = heard;

        this.isActive();
        console.log("Attacker tree", this.action);

        return this.action;
    }
};