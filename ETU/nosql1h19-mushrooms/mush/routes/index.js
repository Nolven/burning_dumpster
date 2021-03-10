passport = require('passport');
express = require('express');

router = express.Router();

//TODO move db work to another file
//Data for queries
//structure: {param: [{label:value}, ..]}
var params = {
    cap_shape : [{ bell:"b"}, {conical:"c"},
        {convex:"x"}, {flat:"f"}, { knobbed:"k"}, {sunken:"s"}],
    cap_surface : [{ fibrous:"f"}, {grooves:"g"}, {scaly:"y"}, {smooth:"s"}],
    cap_color : [{ brown:"n"}, {buff:"b"}, {cinnamon:"c"}, {gray:"g"},
        {green:"r"}, { pink:"p"}, {purple:"u"}, {red:"e"}, {white:"w"}, {yellow:"y"}],
    bruises: [{ bruises:"t"}, {no:"f"}],
    odor : [{ almond:"a"}, {anise:"l"}, {creosote:"c"},
        {fishy:"y"}, {foul:"f"}, { musty:"m"}, {none:"n"}, {pungent:"p"}, {spicy:"s"}],
    gill_attachment : [{ attached:"a"}, {descending:"d"}, {free:"f"}, {notched:"n"}],
    gill_spacing : [{ close:"c"}, {crowded:"w"}, {distant:"d"}],
    gill_size : [{ broad:"b"}, {narrow:"n"}],
    gill_color : [{ black:"k"}, {brown:"n"}, {buff:"b"},
        {chocolate:"h"}, {gray:"g"}, { green:"r"}, {orange:"o"}, {pink:"p"}, {purple:"u"}, {red:"e"}, { white:"w"}, {yellow:"y"}],
    stalk_shape : [{ enlarging:"e"}, {tapering:"t"}],
    stalk_root : [{ bulbous:"b"}, {club:"c"}, {cup:"u"},
        {equal:"e"}, { rhizomorphs:"z"}, {rooted:"r"}, {missing:"?"}],
    stalk_surface_above_ring : [{ fibrous:"f"}, {scaly:"y"}, {silky:"k"}, {smooth:"s"}],
    stalk_surface_below_ring : [{ fibrous:"f"}, {scaly:"y"}, {silky:"k"}, {smooth:"s"}],
    stalk_color_above_ring : [{ brown:"n"}, {buff:"b"}, {cinnamon:"c"}, {gray:"g"}, {orange:"o"}, { pink:"p"}, {red:"e"}, {white:"w"}, {yellow:"y"}],
    stalk_color_below_ring : [{ brown:"n"}, {buff:"b"}, {cinnamon:"c"}, {gray:"g"}, {orange:"o"}, { pink:"p"}, {red:"e"}, {white:"w"}, {yellow:"y"}],
    veil_type : [{ partial:"p"}, {universal:"u"}],
    veil_color : [{ brown:"n"}, {orange:"o"}, {white:"w"}, {yellow:"y"}],
    ring_number : [{ none:"n"}, {one:"o"}, {two:"t"}],
    ring_type : [{ cobwebby:"c"}, {evanescent:"e"}, {flaring:"f"}, {large:"l"}, { none:"n"}, {pendant:"p"}, {sheathing:"s"}, {zone:"z"}],
    spore_print_color : [{ black:"k"}, {brown:"n"}, {buff:"b"}, {chocolate:"h"}, {green:"r"}, { orange:"o"}, {purple:"u"}, {white:"w"}, {yellow:"y"}],
    population : [{ abundant:"a"}, {clustered:"c"}, {numerous:"n"}, { scattered:"s"}, {several:"v"}, {solitary:"y"}],
    habitat : [{ grasses:"g"}, {leaves:"l"}, {meadows:"m"}, {paths:"p"}, { urban:"u"}, {waste:"w"}, {woods:"d"}],
    edible: [{yes: "y"}, {no: "n"}]
}; //параметры для заполнения сайд-бара

//Routes
router.get('/', function(req, res, next) { //основная страница
  res.render('index');
});

router.get('/out', function (req, res, next) {
    res.sendFile('D:\\Un\\6\\БД\\nosql1h19-mushrooms\\mush\\out.json'); //файл экспорта
})

//Get sidebar filler
router.get('/params', function (req, res, next) { //запрос на получение параметров в виде жсона
   res.json(JSON.stringify(params));
});

//Get search page
router.get('/search', function (req, res, next) { //поисковая страница
    console.log("Someone is trying to search our secrets");
    if( req.query.name === "" ) //если имя пустое - удаляем
        delete req.query.name;
    res.render('search', {query: JSON.stringify(req.query)});
});

//Admins
router.get('/adminauth', function(req, res) { //админская аутентификация
    res.render('adminauth', { title: 'Admin Panel'});
});

router.get('/adminpanel', passport.authenticationMiddleware(), function(req, res) { //админская страница, аутентификация через passport
    res.render('adminpanel', { title: 'Admin Panel'});
});

router.post('/login', passport.authenticate('local', { //к этой штуке происходит запрос после аутентификации /adminpanel
    successRedirect: '/adminpanel',
    failureRedirect: '/',
    failureFlash: true
}));

router.get('/statistics', function (req, res) { //получение статистики
   res.render('statistics');
});

module.exports = router;