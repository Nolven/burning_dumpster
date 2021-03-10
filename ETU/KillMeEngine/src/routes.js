const express = require("express");
const router = express.Router();
const fs = require('fs');

let filename = "";

const multer = require('multer');
let storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, './img')
    },
    filename: function (req, file, cb) {
        console.log(file);
        filename = Date.now() + '.' + file.originalname.split('.')[1];
        cb(null, filename);
    }
})
const upload = multer({ storage: storage })

module.exports = router;

try{
    var objTypes = require('./data/objTypes.json');
}
catch (e) {
    console.log("Fucked up");
    var objTypes = {}
}

router.get("/", (req, res, next)=>{
    res.render('main');
    next();
});

router.get("/objTypes", (req, res, next)=>{
    if( Object.keys(req.query).length === 0 ){
        res.json(objTypes);
    }
    else{
        res.json(objTypes[req.query['name']]);
    }
});

router.post('/submitObj', upload.single('img'), (req, res, next)=>{
    if( objTypes[req.body.name] === undefined ){
        let obj = req.body;
        obj.img = filename;
        let name = obj['name'];
        delete obj['name'];
        objTypes[name] = obj;
        fs.writeFileSync('data/objTypes.json', JSON.stringify(objTypes), ()=>{return true});
        res.status(200).send();
    }
    else
        res.status(400).send();

});

router.delete('/del/name=:name', (req, res, next)=>{
    let name = req.params.name;
    if( objTypes[name] !== undefined ){
        delete objTypes[name];
        fs.writeFileSync('data/objTypes.json', JSON.stringify(objTypes), ()=>{return true});
        res.status(200).send();
    }
    else{
        res.status(400).send();
    }

});

router.post('updateProps/name=:name', (req, res, next)=>{

});