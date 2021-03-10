const Agent = require('./agent'); // Импорт агента
const VERSION = 7; // Версия сервера
const readline = require('readline');
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

getCoords();

function createAgent(teamName, x, y, goalie) {
    let agent = new Agent(goalie); // Создание экземпляра агента
    require('./socket')(agent); //Настройка сокета
    agent.init(teamName, VERSION);
    setTimeout(() => {
        agent.socketSend("move", `${x} ${y}`)
    }, 100)
}

function getCoords() {
    rl.question('X ', (x) =>
    {
        rl.question('Y ', (y) =>
        {
            rl.question('TeamName ', (teamName) =>
            {
                rl.question('goalie? (y/n) ', (goalie) => {
                    createAgent(teamName, x, y, goalie);
                    rl.close();
                })
            })
        })
    })
}