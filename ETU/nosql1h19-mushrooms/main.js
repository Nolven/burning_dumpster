var mongoose = require('mongoose');
mongoose.connect('mongodb+srv://Totem:1234@db-4mje1.gcp.mongodb.net/test', { useNewUrlParser: true } )

var db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));

db.once('open', function() {
    console.log("Connection Successful!");

    var BookSchema = mongoose.Schema({
        name: String,
        ed: String,
    });

    var Mush = mongoose.model('Mushroom', BookSchema, "test");

    var whiteMush = new Mush({name: "White", ed: "Yep"});

    // save model to database
    whiteMush.save(function (err, mush) {
        if (err) return console.error(err);
        console.log(mush);
    });

});

