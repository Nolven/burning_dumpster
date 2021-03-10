/**
 * For two players
 */
module.exports = {
    action: {n: "", v: ""}, ///< Final action
    FOV: "85",
    ball: {},
    player: {}, //leader is stored here
    parsed: {},
    target: {},
    teamName: "",
    highSpeed: "80",
    normalSpeed: "35",
    lowSpeed: "15",
    whenPlayerIsClose: "3",
    whenPlayerIsFar: "15",

    isPlayerMovingToUs: function()
    {
        let pDir = this.player.p[2];
        if( pDir < 0 )
        {
            console.log("to");
            this.action = {n: "dash", v:`${-this.lowSpeed} ${this.player.p[1]}`};
        }
        else
        {
            console.log("from");
            this.action = {n: "dash", v:`${this.player.p[0] + this.normalSpeed} ${this.player.p[1]}`};
        }
    },

    isPlayerMoving: function()
    {
        let pDist = this.player.p[2];
        let pDir = this.player.p[3];

        if( pDist || pDir )
        {
            this.isPlayerMovingToUs();
            console.log("player is moving");
        }
        else
            this.action = {n: "turn", v: this.player.p[1]};
    },

    isPlayerClose: function()
    {
        if( this.player.p[0] < this.whenPlayerIsClose )
            this.action = {n: "turn", v: this.player.p[1]};
        else
            this.isPlayerFar();

    },

    isPlayerFar: function()
    {
        if( this.player.p[0] > this.whenPlayerIsFar )
            this.action = {n: "dash", v: `${this.highSpeed} ${this.player.p[1]}`};
        else
            this.isPlayerMoving();
    },

    isPlayerVisible: function()
    {
        this.player = this.parsed.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === 'p')[0];

        console.log(this.player);

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

    getAction: function (parsed, teamName, player)
    {
        this.teamName = teamName;
        this.parsed = parsed;
        this.player = player;

        this.isPlayerClose();

        console.log("Following", this.action);

        return this.action;
    }
};