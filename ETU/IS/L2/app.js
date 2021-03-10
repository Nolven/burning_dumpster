const Agent = require('./agent'); // Импорт агента
const VERSION = 7; // Версия сервера
const readline = require('readline');
const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

getCoords();

function createAgent(teamName, speed, x, y) {
    let agent = new Agent(speed); // Создание экземпляра агента
    require('./socket')(agent, teamName, VERSION); //Настройка сокета
    setTimeout(() => {
        agent.socketSend("move", `${x} ${y}`)
    }, 20)
}

function getCoords() {
    rl.question('X ', (firstX) => {
        rl.question('Y ', (firstY) => {
            rl.question('TeamName ', (teamName) => {
                createAgent(teamName, 0, firstX, firstY);
                rl.close();
            })
        })
    })
}