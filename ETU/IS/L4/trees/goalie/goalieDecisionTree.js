let findGate = require("./findGates");
let lookAround = require("./lookAround");
let track = require("./trackBall");

module.exports =
{
    FOV: "80",
    gatesDist: 5,
    parsed: [],
    side: '',
    gates: {},
    centPFlag: {},
    activeZoneLength: 25,

    atGates: false,

    action: {},

    reset: function()
    {
        this.atGates = false;
        findGate.notAtGates();
    },

    calcCosT: function(a, b, alpha)
    {
        return Math.abs( Math.sqrt(a ** 2 + b ** 2 - 2 * a * b * Math.cos(alpha*Math.PI/180)) );
    },

    isBallFast: function()
    {
        if( Math.abs(this.ball.p[2]) > 3 || this.ball.p[2] === 0 )
        {
            this.action = {n: "kick", v: "200 0"};
            findGate.notAtGates();
            this.atGates = false;
        }
        else
            this.action = {n: "catch", v: `${this.ball.p[1]}`};
    },

    isWeFaceBall: function()
    {
        if( this.ball.p[1] < 0.5 )
            this.isBallFast();
        else
            this.action = {n: "turn", v: `${this.ball.p[1]}`};
    },

    areWeAreCloserToBall: function()
    {
        let closer = true;
        let distToBall = this.ball.p[0];

        this.parsed.forEach((val) =>
        {
            if( val.cmd && (val.cmd.p[0] === 'P' || val.cmd.p[0] === 'p') )
            {
                let angle;
                if( Math.sign(val.p[1]) !== Math.sign( this.ball.p[1]) )
                    angle = Math.abs( val.p[1] ) + Math.abs( this.ball.p[1] ); //if object on different side of you vision
                else
                    angle = Math.max( Math.abs(val.p[1]), Math.abs(this.ball.p[1]) ) - Math.min( Math.abs(val.p[1]), Math.abs(this.ball.p[1]) ); // on the same

                let P2BDist = this.calcCosT(distToBall, val.p[0], angle);

                if( P2BDist < this.ball.p[0] )
                    closer = false;
            }
        });

        if( closer )
        {
            console.log("we are closer to ball");
            this.action = {n: "dash", v: `100 ${this.ball.p[1] + this.ball.p[3]}`};
        }
        else
            this.action = track.getAction(this.parsed, this.side, this.ball);
    },

    isBallCloseEnough: function()
    {
        if( this.ball.p[0] < this.activeZoneLength )
            this.areWeAreCloserToBall();
        else
            this.action = {n: "turn", v: `${this.ball.p[1]}`};
    },

    isBallClose: function()
    {
        if( this.ball.p[0] < 1 )
            this.isWeFaceBall();
        else
            this.isBallCloseEnough();
    },

    isBall: function()
    {
        this.ball = this.parsed.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'b')[0];

        if( this.ball )
            this.isBallClose();
        else
            this.action = lookAround.getAction(this.parsed);
    },

    areAtGates: function()
    {
        console.log(this.atGates);
        if( this.atGates === false )
        {
            this.action = findGate.getAction(this.parsed, this.side);
            this.atGates = findGate.atGates;
        }
        else
            this.isBall()
    },

    getAction: function(parsed, side)
    {
        this.side = side;
        this.parsed = parsed;
        this.areAtGates();

        console.log("Goalie tree", this.action, "\n");

        return this.action;
    }
};