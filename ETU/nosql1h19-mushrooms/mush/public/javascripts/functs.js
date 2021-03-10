function collectData(){
    data = {};
    //Sidebar
    if( $("#leftSidebar").is(":visible") )
    {
        $(".params").each(function () {
            let val = $("option:selected", this).val();
            if( val !== '0' )
                data[$("option:first", this).text()] = val;
        });
    }
    //name,region
    let name = $("#query").val();
    let region = $("#openMap").val();
    if( region !== "" )
        data.region = region;
    if( name !== "" )
        data.name = name;
    return data;
}

function queryToDb(callback = false){
    $.getJSON('/db/query/main', collectData(), function (data) {
        console.log('querying');
        console.log(data);
        if( callback )
            callback(data);
    } );
}

function updateRegion(code = "") {
    let button = $("#openMap");
    if( code === "" )
    {
        button.text("Select Region");
        $("#resetRegion").css("display", "none");
    }
    else
    {
        button.text(code);
        $("#resetRegion").css("display", "block");
    }
    console.log("region updated")
    button.val(code);
    $("#region").val(code).trigger('change');
    Cookies.set('region', code);
    $("#modal").css("display", "none");
}

function twoComboLabel(key, val, classs){
    let d = $('<div class="d-inline" />');
    s = $('<select />', {class: classs, name: key, id: key});
    $('<option />', {value: 0, text: key}).appendTo(s);
    for( let k of val )
    {
        $('<option />', {value: Object.values(k)[0], text: Object.keys(k)[0]}).appendTo(s);
    }
    d.append(s);
    return d;
}