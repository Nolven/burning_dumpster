/**
 * For two players
 */

let ballControl = require("./ballOnPlayer");
let follow = require("./followPlayer");


module.exports = {
    action: {n: "", v: ""}, ///< Final action
    targets: ["fprc"], ///< goals to run near
    FOV: "85",
    nextTarget: 0,
    ball: {},
    player: {}, //leader is stored here
    parsed: {},
    target: {},
    teamName: "",

    calcCosT: function(a, b, alpha)
    {
        return Math.abs( Math.sqrt(a ** 2 + b ** 2 - 2 * a * b * Math.cos(alpha*Math.PI/180)) );
    },

    isBallClose: function()
    {
        if( this.ball.p[0] < 1 )
            this.action = ballControl.getAction(this.parsed, this.side, this.team, this.ball);
        else
            this.action = {n: "dash", v:`${200} ${this.ball.p[1]}`}
    },

    isPlayerCloserToBall: function()
    {
        let angle;
        if( Math.sign(this.player.p[1]) !== Math.sign(this.ball.p[1]) )
            angle = Math.abs( this.player.p[1] ) + Math.abs(this.ball.p[1]); //if object on different side of you vision
        else
            angle = Math.max( Math.abs(this.player.p[1]), Math.abs(this.ball.p[1]) ) - Math.min( Math.abs(this.player.p[1]), Math.abs(this.ball.p[1]) ); // on the same

        let P2BDist = this.calcCosT( this.ball.p[0], this.player.p[0], angle);

        if( P2BDist < this.ball.p[0] )
            this.action = follow.getAction(this.parsed, this.teamName, this.player);
        else
            this.isBallClose();
    },

    isPlayerVisible: function()
    {
        this.player = this.parsed.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'p' && obj.cmd.p[1] === `"${this.teamName}"`)[0];

        if( this.player )
            this.isPlayerCloserToBall();
        else
            this.isBallClose();
    },

    ballIsVisible: function()
    {
        this.ball = this.parsed.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'b')[0];

        if( this.ball )
            this.isPlayerVisible();
        else
            this.action = {n: "turn", v: this.FOV}
    },

    getAction: function (parsed, teamName)
    {
        this.teamName = teamName;
        this.parsed = parsed;

        this.ballIsVisible();

        console.log("Tandem", this.action);

        return this.action;
    }
};