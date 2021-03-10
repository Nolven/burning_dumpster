function miniature(item){
    console.log(item);
    upWrap = "";
    cap = "";
    others1 = "";
    gill = "";
    stalk = "";
    veil = "";
    ring = "";
    others2 = "";
    var i = 0;
    for( k in item ) {
        if( k !== "name" && k !== 'img' && k !== '_id' && k!== "__v" )
        {
            if (i < 2) {
                ++ i;
                continue;
            }
            if(i < 5) {
                cap = cap.concat("<div class=\"par\">", k + ' : ' + item[k], "</div>");
                ++i;
            }
            else if(i < 7) {
                others1 = others1.concat("<div class=\"par\">", k + ' : ' + item[k], "</div>");
                ++i;
            }
            else if(i < 11) {
                gill = gill.concat("<div class=\"par\">", k + ' : ' + item[k], "</div>");
                ++i;
            }
            else if(i < 17) {
                stalk = stalk.concat("<div class=\"par\">", k + ' : ' + item[k], "</div>");
                ++i;
            }
            else if(i < 19) {
                veil = veil.concat("<div class=\"par\">", k + ' : ' + item[k], "</div>");
                ++i;
            }
            else if(i < 21) {
                ring = ring.concat("<div class=\"par\">", k + ' : ' + item[k], "</div>");
                ++i;
            }
            else if(i < 25) {
                others2 = others2.concat("<div class=\"par\">", k + ' : ' + item[k], "</div>");
                ++i;
            }
        }
    }

    mush_modal = "<div class=\"w3-modal\" id=\"" + "modal" + item.name + "\">\n" +
        "  <div class=\"w3-modal-content\"><span id=\"close_modal" + item.name + "\" class=\"w3-button w3-col s1\">&times;</span></div>\n" +
        " <img src=\"" + item.img + "\"/>" +
        "</div>";
    upWrap = upWrap.concat(mush_modal);

    upWrap = upWrap.concat("<div class=\"mushroom__wrapper d-flex\">\n" +
        "  <div class=\"mushroom__image-wrapper\">\n" +
        "    <div class=\"mushroom__image\"><img id=\"img" + item.name +"\" class=\"mush-img\" src=\"" + item.img + "\"/></div>\n" +
        "    <div class=\"mushroom__name\">" + item.name + "</div>\n" +
        "  </div>\n" +
        "  <div class=\"mushroom__params-wrappers d-flex\">\n" +
        "    <div class=\"mushroom__params-wrapper-one d-flex\">\n" +
        "      <div class=\"mushroom__description\"> Description: " + item.description + "</div>\n" +
        "      <div class=\"mushroom__region\"> Region: " + item.region + "</div>\n" +
        "    </div>\n" +
        "    <div class=\"mushroom__params-wrapper-two d-flex\">\n" +
        "      <div class=\"mushroom__params-part\">\n" +
        "        <h4 class=\"mushroom__params-cap-h\">Cap</h4>\n" +
        "        <div class=\"mushroom__params-cap-li\">\n" + cap +
        "        </div>\n" + others1 +
        "      </div>\n" +
        "      <div class=\"mushroom__params-part\">\n" +
        "        <h4 class=\"mushroom__params-gill-h\">Gill</h4>\n" +
        "        <div class=\"mushroom__params-gill-li\">\n" + gill +
        "        </div>\n" +
        "      </div>\n" +
        "      <div class=\"mushroom__params-part\">\n" +
        "        <h4 class=\"mushroom__params-stalk-h\">Stalk</h4>\n" +
        "        <div class=\"mushroom__params-stalk-li\">\n" + stalk +
        "        </div>\n" +
        "      </div>\n" +
        "      <div class=\"mushroom__params-part\">\n" +
        "        <h4 class=\"mushroom__params-veil-h\">Veil</h4>\n" +
        "        <div class=\"mushroom__params-veil-li\">\n" + veil +
        "        </div>\n" +
        "        <h4 class=\"mushroom__params-ring-h\">Ring</h4>\n" +
        "        <div class=\"mushroom__params-ring-li\">\n" + ring +
        "        </div>\n" +
        "      </div>\n" +
        "      <div class=\"mushroom__params-part\">\n" +
        "        <h4 class=\"mushroom__params-others-h\">Others</h4>\n" +
        "        <div class=\"mushroom__params-others-li\">\n" + others2 +
        "        </div>\n" +
        "      </div>\n" +
        "    </div>\n" +
        "  </div>\n" +
        "</div>");

    $('#img' + item.name).on('click', function () {
        console.log("Modal should be here");
        console.log(item.name);
        $('#modal' + item.name).css("display", "block");
    });

    $('#close_modal' + item.name).on('click', function () {
        $('#modal' + item.name).css("display", "none");
    });

    $('#search__mushrooms-wrap').append(upWrap);


    /*upWrap = $("<div />", {
        style: "width: 20%; height: auto; margin: 10px; text-align: center;",
        class: "w3-card popup",
    });
    upWrap.append($('<img />', {
        src: item.img,
        style: "max-width: 100%"
    }));
    lwrap = $("<div />", {
        style: "width: 100%; height: 100%; text-align: center;", text: item.name});
    upWrap.append(lwrap);

    popup = $("<div />", {
        class: "popuptext",
        id: item.name,
    });

    upWrap.append(popup);
    $('#search__mushrooms-wrap').append(upWrap);
    upWrap.on('click', function () {
        console.log("Pop up should be here");
        console.log(item.name);
        document.getElementById(item.name).classList.toggle("show");
    });
    for( k in item )
    {
        if( k !== "name" && k !== 'img' && k !== '_id' && k!== "__v" )
        {
            popup.append($('<label />',
                {
                    text: k + ' : ' + item[k],
                    style: "display: block"
                }));
        }
    }*/
}

function fillContent(data) {
    $("#search__mushrooms-wrap").empty();
    data.forEach(miniature);
}

$(()=> {
    console.log("search");
    w3_close_left();
    if (Object.keys(q).length > 2){
        console.log('open sidebar');
        w3_open_left();
    } //invent some clever way to check if sidebar was open on the previous page

    $("#query").val(q.name);
    for (let k in q) {
        if (k === "name") continue;
        $(`select[name=${k}]`).val(q[k]);
    }
    $("#topBarWrapper").addClass("w3-card");

    //On change of query string
    $("#query").bind('input', function () {
        queryToDb(fillContent);
    } );
    //On change of params
    $(".params").change(function () {
        queryToDb(fillContent);
    });

    $("#region").change(function () {
        console.log("STILL FUCK THIS STUPID LANGUAGE");
        queryToDb(fillContent);
    });

    $("#resetRegion").on('click', function () {
        updateRegion();
    });

    queryToDb(fillContent);
});