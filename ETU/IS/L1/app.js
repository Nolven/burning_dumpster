const Agent = require('./agent'); // Импорт агента
const VERSION = 7; // Версия сервера
const readline = require('readline');
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

getCoords();

function createAgent(teamName, speed, x, y) {
    let agent = new Agent(speed);
    require('./socket')(agent, teamName, VERSION);
    setTimeout(()=>
        agent.sendCommand("move", `${x} ${y}`)
    , 100)
}

//Parses stdin and creates new agent (player)
function getCoords() {
    rl.question('X ', (firstX) =>
    {
        rl.question('Y ', (firstY) =>
        {
            rl.question('Period ', (speed) =>
            {
                rl.question('TeamName ', (teamName) =>
                {
                    createAgent(teamName, speed, firstX, firstY);
                })
            })
        })
    });
}
