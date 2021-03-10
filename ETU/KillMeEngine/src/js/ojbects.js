//Stable
class Ojbect{
    constructor(name = "", width = 0, height = 0, god = 1, health = 1, img = ""){
        this.name = name;
        this.width = width;
        this.height = height;
        this.god = god;
        this.health = health;
        this.img = img;
    }
}

class Solid extends Ojbect{
    constructor(name = "", width = 0, height = 0, god = 1, health = 1, img = "", collision = false){
        super(name, width, height, god, health, img);
        this.collision = collision;
    }
}

class Turret extends Solid{
    constructor(name = "", width = 0, height = 0, god = 1, health = 1, img = "", collision = false, damage = 10, range = 3){
        super(name, width, height, god, health, img, collision, collision);
        this.range = range;
        this.damage = damage;
    }
}

//Walky
class Creature {
    constructor(name, hp, speed, dmg, img){
        this.name = name;
        this.hp = hp;
        this.speed = speed;
        this.damage = dmg;
        this.img = img;
    }
}

class Range extends Creature{
    constructor(name, hp, speed, dmg, range, img){
        super(name, hp, speed, dmg, img);
        this.range = range;
    }
}

class Player extends Range{
    constructor(name, hp, speed, dmg, range, img){
        super(name, hp, speed, dmg, range, img);
    }
}

var ojbects = {
    Ojbect: Ojbect,
    Solid: Solid,
    Turret: Turret,
    Creature: Creature,
    Player: Player
}
