module.exports = {
    seen: [],
    ball: null,
    action: null,
    team: null,
    role: null,
    player: null,

    calcCosT: function(a, b, alpha)
    {
        return Math.abs( Math.sqrt(a ** 2 + b ** 2 - 2 * a * b * Math.cos(alpha*Math.PI/180)) );
    },

    isPlayerInFOV: function()
    {
        this.player = this.seen.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'p' && obj.cmd.p[1] === `"${this.team}"`)[0];

        if( this.player )
        {
            let angle;
            if( Math.sign(this.player.p[1]) !== Math.sign(this.ball.p[1]) )
                angle = Math.abs( this.player.p[1] ) + Math.abs(this.ball.p[1]); //if object on different side of you vision
            else
                angle = Math.max( Math.abs(this.player.p[1]), Math.abs(this.ball.p[1]) ) - Math.min( Math.abs(this.player.p[1]), Math.abs(this.ball.p[1]) ); // on the same

            let P2BDist = this.calcCosT( this.ball.p[0], this.player.p[0], angle);

            if ( P2BDist < this.ball.p[0] )
            {
                this.role = "a";
                this.action = {n: "turn", v: 80};
            }
        }
        else
            this.role = "p";
    },

    isBall: function()
    {
        this.ball = this.seen.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'b')[0];

        if( this.ball )
            this.isPlayerInFOV();
        else
            this.action = {n: "turn", v: 80};
    },

    getAction: function (seen, team)
    {
        this.seen = seen;
        this.team = team;

        this.isBall();
        console.log("Base tree", this.action);

        return this.action;
    }
};