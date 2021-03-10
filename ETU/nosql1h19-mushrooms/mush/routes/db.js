express = require('express');
mongoose = require('mongoose');
fs = require('fs');

router = express.Router();
Schema = mongoose.Schema;

var sch = new Schema({
    bruises: {type: String, default: '0'} ,
    cap_color: {type: String, default: '0'} ,
    cap_shape: {type: String, default: '0'} ,
    cap_surface: {type: String, default: '0'} ,
    gill_attachment: {type: String, default: '0'} ,
    gill_color: {type: String, default: '0'} ,
    gill_size: {type: String, default: '0'} ,
    gill_spacing: {type: String, default: '0'} ,
    habitat: {type: String, default: '0'} ,
    name: {type: String, default: 'Anton'},
    odor: {type: String, default: '0'} ,
    population: {type: String, default: '0'} ,
    ring_number: {type: String, default: '0'} ,
    ring_type: {type: String, default: '0'} ,
    spore_print_color: {type: String, default: '0'} ,
    stalk_color_above_ring: {type: String, default: '0'} ,
    stalk_color_below_ring: {type: String, default: '0'} ,
    stalk_root: {type: String, default: '0'} ,
    stalk_shape: {type: String, default: '0'} ,
    stalk_surface_above_ring: {type: String, default: '0'} ,
    stalk_surface_below_ring: {type: String, default: '0'} ,
    veil_color: {type: String, default: '0'} ,
    veil_type: {type: String, default: '0'} ,
    description: {type: String, default: 'KABUUUUUM'},
    region: {type: Array, default: ['RU','UK','AFG']},
    img: {type: String, default: ''},
    edible: {type: String, default: 'n'}
});

//TODO add auth for every action

//Connection for models
const connection = mongoose.createConnection('mongodb+srv://Totem:12345@db-4mje1.gcp.mongodb.net/Mushrooms', {useNewUrlParser: true});
connection.on('open', function () {
    console.log('Connection to db established');
});
connection.on('error', console.error.bind(console, 'connection error:'));

//Models
var mushroom = connection.model('Mushroom', sch, 'Mushrooms');
var suggestion = connection.model('Suggestion', sch, 'Suggestions');

//Querying to main db
router.get('/query/main', function (req, res, next) {
    console.log('query');
    if( 'name' in req.query )
    {
        req.query.name = {$regex: req.query.name};
    }
    mushroom.find(req.query, function (err, mush) {
        if( Object.entries(req.query).length === 0 && req.query.constructor === Object ) {
            console.log('wirting');
            let writer = fs.createWriteStream('out.json');
            writer.write(JSON.stringify(mush));
            console.log(mush);
            res.send(mush);
        }
        else
        {
            res.send(mush);
        }
    });
});

//Querying to suggestions
router.get('/query/suggestions', function (req, res, next) {
    suggestion.find({}, function (err, sugg) {
        res.send(sugg);
    })
});

//Add new suggestion
router.post('/add/main', function (req, res, next) {
    req.body.region = req.body.region.split(',');
    let sugg = new suggestion(req.body);
    sugg.save(function (err) {
        if( err )
            console.log('NANIII');
    });
    res.render('search', {query: JSON.stringify({name: ""})}); //TODO Duct Tape
});

//Add new/update mushroom
router.post('/adminPressedTheBlackButton', function (req, res, next) {
    let data = req.body;
    data.region = data.region.split(','); // , in the region into []
    if( 'ttype' in data ){
        if( data['ttype'] === 'admin' ) //update an existing mush
        {
            delete data.ttype;
            if( '_id' in data )
            {
                mushroom.updateOne({'_id': data._id},{$set: data}, function (err) {
                    if(err){
                        console.log("can't update");
                    }
                } )
            }
            else{
                let m = new mushroom(data);
                m.save(function (err) {
                    if( err )
                        console.log(err);
                    else{
                        console.log("admin's mush")
                    }
                })
            }

        }
        else //add new one from suggestions
        {
            delete data.ttype;
            let id = data._id;
            delete data._id;

            let mush = new mushroom(data);
            mush.save(function (err) {
                if( err ){
                    console.log('smth wrong, can"t save')
                }
                else{
                    suggestion.findOneAndDelete({_id: id}, function (err) {
                        if( err )
                            console.log("can't delete, HE IS TOO POWERFUL");
                    })
                }
            })
        }
    }
    else{ //From layout
        console.log(req.body);
        aMush = new suggestion(req.body);
        aMush.save(function (err) {
            if(err){
                console.log("Kirito, it's 2 dangerous 2 go alone, take this stiletto and stab the Administrator");
            }
        })
    }
    res.status(200);
    res.send({'admin': 'nechelovek'});
});

//Delete mushroom
router.post('/adminPressedTheRedButton', function (req, res, next) {
    let data = req.body;
    if( 'ttype' in data ){
        if( data.ttype === 'admin' ){ //form main db
            mushroom.deleteOne({"_id": data._id}, function (err) {
                if( err ){
                    console.log('RED BUTTON DOESN"T WORK, HE IS UNSTOPPABLE');
                }
            });
        }
        else{ //from suggestions
            suggestion.deleteOne({"_id": data._id}, function (err) {
                if( err ){
                    console.log('RED BUTTON DOESN"T WORK, HE IS UNSTOPPABLE');
                }
            })
        }
    }
    res.status(200);
    res.send({'ti':'pidor'});
});

//Import
router.post('/import', function (req,res,next) {
    console.log('importing');
    let item = req.body;
    if( !('name' in item) )
    {
        console.log('Appending names');
        item.name = randomEl(adjectives)+' '+randomEl(nouns)
    }
    if( !('region' in item) )
    {
        console.log('Appending region');
        item.region = randomEl(Object.values(country_names));
    }
    mushroom.create(item, function (err) {
        if(err){
            console.log('smth went wrong');
        }
    });
    res.send({});

});

//Statistics
router.get('/stats/region', function (req, res, next) {
    mushroom.aggregate([
        {
            $unwind: "$region"
        },
        {
            $group: {
                _id: "$region",
                count: {$sum: 1}
            }
        }
    ], function (err, result) {
        if( err )
            console.log(err);
        else{
            console.log('region');
            res.send(result);
        }
    })
});

router.get('/stats/edible', function (req, res, next) {
    mushroom.aggregate([
        {
            $group: {
                _id: "$edible",
                count: {$sum: 1}
            }
        }
    ], function (err, result) {
        if( err )
            console.log(err);
        else{
            res.send(result);
        }
    })
});

router.get('/stats/params', function (req, res, next) {
    console.log('params');
    console.log(req.query);
    if( Object.keys(req.query).length === 1)
    {
        mushroom.aggregate([
            {
                $group:
                    {
                        _id: "$" + Object.keys(req.query)[0],
                        count: {$sum: 1}
                    }
            }
        ], function (err, result) {
            if( err )
                console.log(err);
            else{
                console.log(result);
                res.send(result);
            }
        })
    }
    else{
        let all;
        mushroom.aggregate([{$count: "count"}], function (e,r){all = r[0].count});
        mushroom.aggregate([
            {
                $match: req.query,
            },
            {
                $count: "count"
            }
        ], function (err, result) {
            if( err )
                console.log(err);
            else{
                console.log(result);
                if( typeof result === "undefined" )
                    result = {};
                result[0]._id = 'Same';
                result.push({_id: 'Different', count: all-result[0].count});
                res.send(result);

            }
        })
    }
});

function randomEl(list) {
    var i = Math.floor(Math.random() * list.length);
    return list[i];
}

var adjectives = ["adamant", "adroit", "amatory", "animistic", "antic", "arcadian", "baleful", "bellicose", "bilious", "boorish", "calamitous", "caustic", "cerulean", "comely", "concomitant", "contumacious", "corpulent", "crapulous", "defamatory", "didactic", "dilatory", "dowdy", "efficacious", "effulgent", "egregious", "endemic", "equanimous", "execrable", "fastidious", "feckless", "fecund", "friable", "fulsome", "garrulous", "guileless", "gustatory", "heuristic", "histrionic", "hubristic", "incendiary", "insidious", "insolent", "intransigent", "inveterate", "invidious", "irksome", "jejune", "jocular", "judicious", "lachrymose", "limpid", "loquacious", "luminous", "mannered", "mendacious", "meretricious", "minatory", "mordant", "munificent", "nefarious", "noxious", "obtuse", "parsimonious", "pendulous", "pernicious", "pervasive", "petulant", "platitudinous", "precipitate", "propitious", "puckish", "querulous", "quiescent", "rebarbative", "recalcitant", "redolent", "rhadamanthine", "risible", "ruminative", "sagacious", "salubrious", "sartorial", "sclerotic", "serpentine", "spasmodic", "strident", "taciturn", "tenacious", "tremulous", "trenchant", "turbulent", "turgid", "ubiquitous", "uxorious", "verdant", "voluble", "voracious", "wheedling", "withering", "zealous"];

var nouns = ["ninja", "chair", "pancake", "statue", "unicorn", "rainbows", "laser", "senor", "bunny", "captain", "nibblets", "cupcake", "carrot", "gnomes", "glitter", "potato", "salad", "toejam", "curtains", "beets", "toilet", "exorcism", "stick figures", "mermaid eggs", "sea barnacles", "dragons", "jellybeans", "snakes", "dolls", "bushes", "cookies", "apples", "ice cream", "ukulele", "kazoo", "banjo", "opera singer", "circus", "trampoline", "carousel", "carnival", "locomotive", "hot air balloon", "praying mantis", "animator", "artisan", "artist", "colorist", "inker", "coppersmith", "director", "designer", "flatter", "stylist", "leadman", "limner", "make-up artist", "model", "musician", "penciller", "producer", "scenographer", "set decorator", "silversmith", "teacher", "auto mechanic", "beader", "bobbin boy", "clerk of the chapel", "filling station attendant", "foreman", "maintenance engineering", "mechanic", "miller", "moldmaker", "panel beater", "patternmaker", "plant operator", "plumber", "sawfiler", "shop foreman", "soaper", "stationary engineer", "wheelwright", "woodworkers"];

const country_names = {'Afghanistan': 'AF','Albania': 'AL','Algeria': 'DZ','American Samoa': 'AS','Andorra': 'AD','Angola': 'AO','Anguilla': 'AI','Antarctica': 'AQ',
    'Antigua and Barbuda': 'AG','Argentina': 'AR','Armenia': 'AM','Aruba': 'AW','Australia': 'AU','Austria': 'AT','Azerbaijan': 'AZ','Bahamas': 'BS',
    'Bahrain': 'BH','Bangladesh': 'BD','Barbados': 'BB','Belarus': 'BY','Belgium': 'BE','Belize': 'BZ','Benin': 'BJ','Bermuda': 'BM','Bhutan': 'BT',
    'Bolivia, Plurinational State of': 'BO','Bonaire, Sint Eustatius and Saba': 'BQ','Bosnia and Herzegovina': 'BA','Botswana': 'BW','Bouvet Island': 'BV',
    'Brazil': 'BR','British Indian Ocean Territory': 'IO','Brunei Darussalam': 'BN','Bulgaria': 'BG','Burkina Faso': 'BF','Burundi': 'BI','Cambodia': 'KH',
    'Cameroon': 'CM','Canada': 'CA','Cape Verde': 'CV','Cayman Islands': 'KY','Central African Republic': 'CF','Chad': 'TD','Chile': 'CL','China': 'CN',
    'Christmas Island': 'CX','Cocos (Keeling) Islands': 'CC','Colombia': 'CO','Comoros': 'KM','Congo': 'CG','Congo, the Democratic Republic of the': 'CD',
    'Cook Islands': 'CK','Costa Rica': 'CR','Croatia': 'HR','Cuba': 'CU','Curaçao': 'CW','Cyprus': 'CY','Czech Republic': 'CZ',"Côte d'Ivoire": 'CI',
    'Denmark': 'DK','Djibouti': 'DJ','Dominica': 'DM','Dominican Republic': 'DO','Ecuador': 'EC','Egypt': 'EG','El Salvador': 'SV','Equatorial Guinea': 'GQ',
    'Eritrea': 'ER','Estonia': 'EE','Ethiopia': 'ET','Falkland Islands (Malvinas)': 'FK','Faroe Islands': 'FO','Fiji': 'FJ','Finland': 'FI','France': 'FR',
    'French Guiana': 'GF','French Polynesia': 'PF','French Southern Territories': 'TF','Gabon': 'GA','Gambia': 'GM','Georgia': 'GE','Germany': 'DE',
    'Ghana': 'GH','Gibraltar': 'GI','Greece': 'GR','Greenland': 'GL','Grenada': 'GD','Guadeloupe': 'GP','Guam': 'GU','Guatemala': 'GT','Guernsey': 'GG',
    'Guinea': 'GN','Guinea-Bissau': 'GW','Guyana': 'GY','Haiti': 'HT','Heard Island and McDonald Islands': 'HM','Holy See (Vatican City State)': 'VA',
    'Honduras': 'HN','Hong Kong': 'HK','Hungary': 'HU','Iceland': 'IS','India': 'IN','Indonesia': 'ID','Iran, Islamic Republic of': 'IR','Iraq': 'IQ',
    'Ireland': 'IE','Isle of Man': 'IM','Israel': 'IL','Italy': 'IT','Jamaica': 'JM','Japan': 'JP','Jersey': 'JE','Jordan': 'JO','Kazakhstan': 'KZ',
    'Kenya': 'KE','Kiribati': 'KI',"Korea, Democratic People's Republic of": 'KP','Korea, Republic of': 'KR','Kuwait': 'KW','Kyrgyzstan': 'KG',
    "Lao People's Democratic Republic": 'LA','Latvia': 'LV','Lebanon': 'LB','Lesotho': 'LS','Liberia': 'LR','Libya': 'LY','Liechtenstein': 'LI',
    'Lithuania': 'LT','Luxembourg': 'LU','Macao': 'MO','Macedonia, the former Yugoslav Republic of': 'MK','Madagascar': 'MG','Malawi': 'MW',
    'Malaysia': 'MY','Maldives': 'MV','Mali': 'ML','Malta': 'MT','Marshall Islands': 'MH','Martinique': 'MQ','Mauritania': 'MR','Mauritius': 'MU',
    'Mayotte': 'YT','Mexico': 'MX','Micronesia, Federated States of': 'FM','Moldova, Republic of': 'MD','Monaco': 'MC','Mongolia': 'MN','Montenegro': 'ME',
    'Montserrat': 'MS','Morocco': 'MA','Mozambique': 'MZ','Myanmar': 'MM','Namibia': 'NA','Nauru': 'NR','Nepal': 'NP','Netherlands': 'NL','New Caledonia': 'NC',
    'New Zealand': 'NZ','Nicaragua': 'NI','Niger': 'NE','Nigeria': 'NG','Niue': 'NU','Norfolk Island': 'NF','Northern Mariana Islands': 'MP','Norway': 'NO',
    'Oman': 'OM','Pakistan': 'PK','Palau': 'PW','Palestine, State of': 'PS','Panama': 'PA','Papua New Guinea': 'PG','Paraguay': 'PY','Peru': 'PE',
    'Philippines': 'PH','Pitcairn': 'PN','Poland': 'PL','Portugal': 'PT','Puerto Rico': 'PR','Qatar': 'QA','Romania': 'RO','Russian Federation': 'RU',
    'Rwanda': 'RW','Réunion': 'RE','Saint Barthélemy': 'BL','Saint Helena, Ascension and Tristan da Cunha': 'SH','Saint Kitts and Nevis': 'KN','Saint Lucia': 'LC',
    'Saint Martin (French part)': 'MF','Saint Pierre and Miquelon': 'PM','Saint Vincent and the Grenadines': 'VC','Samoa': 'WS','San Marino': 'SM',
    'Sao Tome and Principe': 'ST','Saudi Arabia': 'SA','Senegal': 'SN','Serbia': 'RS','Seychelles': 'SC','Sierra Leone': 'SL','Singapore': 'SG',
    'Sint Maarten (Dutch part)': 'SX','Slovakia': 'SK','Slovenia': 'SI','Solomon Islands': 'SB','Somalia': 'SO','South Africa': 'ZA',
    'South Georgia and the South Sandwich Islands': 'GS','South Sudan': 'SS','Spain': 'ES','Sri Lanka': 'LK','Sudan': 'SD','Suriname': 'SR',
    'Svalbard and Jan Mayen': 'SJ','Swaziland': 'SZ','Sweden': 'SE','Switzerland': 'CH','Syrian Arab Republic': 'SY','Taiwan, Province of China': 'TW',
    'Tajikistan': 'TJ','Tanzania, United Republic of': 'TZ','Thailand': 'TH','Timor-Leste': 'TL','Togo': 'TG','Tokelau': 'TK','Tonga': 'TO','Trinidad and Tobago': 'TT',
    'Tunisia': 'TN','Turkey': 'TR','Turkmenistan': 'TM','Turks and Caicos Islands': 'TC','Tuvalu': 'TV','Uganda': 'UG','Ukraine': 'UA','United Arab Emirates': 'AE',
    'United Kingdom': 'GB','United States': 'US','United States Minor Outlying Islands': 'UM','Uruguay': 'UY','Uzbekistan': 'UZ','Vanuatu': 'VU',
    'Venezuela, Bolivarian Republic of': 'VE','Viet Nam': 'VN','Virgin Islands, British': 'VG','Virgin Islands, U.S.': 'VI','Wallis and Futuna': 'WF',
    'Western Sahara': 'EH','Yemen': 'YE','Zambia': 'ZM','Zimbabwe': 'ZW','Åland Islands': 'AX'}

module.exports = router;