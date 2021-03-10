let mcls = "modalParams";
let params = [];

//Image (oh god, i spent too much time on it)
function readURL(input) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            $('#userimg').attr('src', e.target.result);
            $("#hmeh").val(e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function collectModal(){
    let data = {};
    params.each(function () {
        let val = $(this).val();
        let name = $(this).attr('name');
        data[name] = val;
    });
    return data;
}

function usermodal_open(data) {
    $('#usertrash_modal').css("display", "block");
    let form = $('#modalForm');
    form.append($('<input />', { //save img
        type: 'hidden',
        name: 'img',
        id: "hmeh",
        class: mcls
    }));

    if( data ){
        if( Object.keys(data)[0] === 'ttype' ) //for admin's button
        {
            form.append($('<input />', { //save doc typ
                type: 'hidden',
                name: 'ttype',
                value: data.ttype,
                class: mcls
            }));
        }
        else{
            params.each(function () {
                $(this).val(data[this.name])
            });
            $('#userimg').attr('src', data.img); //setting img source

            form.append($('<input />', { //save id
                type: 'hidden',
                name: '_id',
                value: data._id,
                class: mcls
            }));

            form.append($('<input />', { //save doc typ
                type: 'hidden',
                name: 'ttype',
                value: data.ttype,
                class: mcls
            }));

            $("#hmeh").val(data.img);
        }
    }
    else{
        $(".modalParams[type=text]").each(function () {
            $(this).val("");
        });
        $(".modalParams:not([type=text],[type=hidden])").each(function () {
            $(this).val('0');
        });
        $("#userimg").attr("src", "");
    }
    params = $('.'+ mcls);
}
function usermodal_close() {
    $('#usertrash_modal').css("display", "none");
    $("input[name=_id]").remove();
    $("input[name=ttype]").remove();
    $("#hmeh").remove();
}

function red() {
    $.ajax({
        type: 'POST',
        async: true,
        url: '/db/adminPressedTheRedButton',
        data: collectModal(),
        success: function (msg) {
            usermodal_close();
            queryToDb(fillAdmin);
            $.getJSON('/db/query/suggestions', {}, function (data) {
                console.log('querying to suggestions');
                fillSuggestions(data);
            } );
        },
        error: function (err) {
            if( err )
                console.log("seems like red doesn't work", err);
        }
    });
}
function black(search) {
    $.ajax({
        type: 'POST',
        async: true,
        url: '/db/adminPressedTheBlackButton',
        data: collectModal(),
        success: function (msg) {
            console.log(search);
            usermodal_close();
            if( !search ){
                console.log('there');
                queryToDb(fillAdmin);
                $.getJSON('/db/query/suggestions', {}, function (data) {
                    console.log('querying to suggestions');
                    fillSuggestions(data);
                } );
            }
        },
        error: function (err) {
            if( err )
                console.log("seems like red doesn't work", err);
        }
    });
}

$(()=>{
    $.getJSON("/params", {},(data)=>{
        d = $.parseJSON(data);
        for(let k in d) {
            if( k !== 'name' && k !== 'description' && k !== 'region' && k!=='_id' ){
                $("#userWrapper").append(twoComboLabel(k, d[k], "modalParams params_correct"));
            }
        }
        params = $('.'+ mcls);
    }); //fill values
    $("#imgInput").change(function() {
        readURL(this);
    }); //on img change
});