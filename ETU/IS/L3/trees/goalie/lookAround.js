module.exports =
{
    rotDir: 1, ///< count-clock 1, clock -1
    rotStep: 30,
    maxSteps: 4,

    current: 2,
    parsed: [],

    action: {},

    rotate: function()
    {
        if( this.current === this.maxSteps )
        {
            this.rotDir *= -1;
            this.current = 0;
        }
        this.current += 1;

        this.action = {n: "turn", v: `${this.rotDir * this.rotStep}`};
    },

    getAction: function (parsed)
    {
        this.parsed = parsed;

        this.rotate();
        console.log("look around", this.action);

        return this.action;
    }
};