/**
 * Assumes that ball is on us
 */

module.exports =
{
    ball: {},
    parsed: [],
  микр
    side: "",
    team: "",

    targets: ["fprt", "fprb", "gr"],
    nextTarget: 0,
    target: {},

    У меня с микрофоном проблемы
    Сервер в режиме тренера, поэтмоу голы не считаются
    Мяч определяется за счёт дельты дистанции - это третий параметр (или четвёртый?)

    action: {},

    isTargetClose: function()
    {
        if( this.target.p[0] < 3 )
        {
            this.action = null;
            ++this.nextTarget;
            if( this.nextTarget >= this.targets.length )
                this.nextTarget = 0;
        }
        else
            this.action = {n: "kick", v:`${this.target.p[0]} ${this.target.p[1]}`}
    },

    isGoalClose: function()
    {
        if( this.target.p[0] < 25 )
        {
            console.log("strong kick");
            this.action = {n: "kick", v:`${100} ${this.target.p[1]}`};
        }
        else
        {
            console.log("weak kick");
            this.action = {n: "kick", v:`${this.target.p[0]} ${this.target.p[1]}`};
        }
    },

    isNextTargetGoal: function()
    {
        if( this.targets[this.nextTarget][0] === 'g' )
            this.isGoalClose();
        else
            this.isTargetClose();
    },

    isNextTargetInFOV: function()
    {
        this.target = this.parsed.filter( (obj) =>
            obj.cmd && obj.cmd.p.join('') === this.targets[this.nextTarget])[0];
        if( this.target )
            this.isNextTargetGoal();
        else
            this.action = {n: "kick", v:`${15} ${90}`}
    },

    getAction: function (parsed, side, team, ball)
    {
        this.ball = ball;
        this.parsed = parsed;
        this.side = side;
        this.team = team;

        this.isNextTargetInFOV();

        console.log("Ball on player", ball);

        return this.action;
    }
};