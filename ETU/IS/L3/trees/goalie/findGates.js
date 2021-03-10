module.exports =
{
    FOV: "60",
    gatesDist: 5,
    seen: [],
    side: '',
    gates: {},
    centPFlag: {},

    atGates: false,
    rotation: false,

    action: {},

    notAtGates: function()
    {
        this.atGates = false;
        this.rotation = false;
    },

    areGatesClose: function()
    {
        if( this.gates.p[0] < this.gatesDist )
        {
            this.action = {n: "turn", v: "360"};
            this.rotation = true;
        }
        else
            this.action = {n: "dash", v: `200  ${this.gates.p[1]}`};
    },

    areGatesInFOV: function()
    {
        this.gates = this.seen.filter( (obj) =>
            obj.cmd && obj.cmd.p[0] === `g` && obj.cmd.p[1] === `${this.side}`)[0];

        if( this.gates || this.atGates )
            this.areGatesClose();
        else
            this.action = {n: "turn", v: this.FOV};
    },

    wasCentralSeen: function()
    {
        let cp = this.seen.filter(value => value.cmd && value.cmd.p.join('') === `fp${this.side}c`)[0];
        if( cp )
        {
            this.atGates = true;
            this.rotation = false;
            this.action = {n: "turn", v: cp.p[1]}
        }
        else
            this.action = {n: "turn", v: this.FOV}
    },

    isRotation: function()
    {
        if (this.rotation)
            this.wasCentralSeen();
        else
            this.areGatesInFOV();
    },

    getAction: function (seen, side)
    {
        this.side = side;
        this.seen = seen;

        this.isRotation();

        console.log("findGatesTree", this.action);

        return this.action;
    }
};