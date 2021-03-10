var tileSize = 20;
var height;
var width;
var map = [];
let lx = 0;
let ly = 0;

const canvas = $("#area");
const rpanel  = $("#rpanel");
const area = canvas[0].getContext("2d");
const rect = canvas[0].getBoundingClientRect();

const table = $("<table></table>");
table.width = tileSize + 20;
table.height = window.innerHeight;
console.log(table.height);

$.fn.center = function () {
    this.css("position","absolute");
    this.css("top", Math.max(0, (($(window).height() - $(this).outerHeight()) / 2) +
        $(window).scrollTop()) + "px");
    this.css("left", Math.max(0, (($(window).width() - $(this).outerWidth()) / 2) +
        $(window).scrollLeft()) + "px");
    return this;
}

function imageLabel(el, cl) {
    let label;
    let input = $("<input>").attr({'type': 'file',
        'name': el, 'class': cl + ' inputFile'});
    let img = $("<img>");
    img.css({
        'max-width': '180px',
        'max-height': '180px',
        'margin': 'auto',
    });
    input.on('change', function () {
        let file = this.files[0];
        let reader = new FileReader();
        reader.onload = function(){
            img.attr('src', this.result);
            label.empty();
            label.append(img);
        };
        reader.readAsDataURL(file);
    });
    input.addClass('new inputFile');
    label = $("<label>").attr('for', 'newFile');
    label.text('Select an image');
    console.log(input);
}

function uploadProps() {
    let selected = $("input[name='object']:checked").val()
    let data = new FormData();
    $('input.propUpd').each(function () {
        data.append($(this).attr('name'), $(this).val());
        console.log($(this).attr('name'), $(this).val());
    });
    $.each($("input[type='file'].upd.inputFile")[0].files, function(i, file) {
        data.append('img', file);
    });
    $.ajax({
        url: 'updateProps/name=' + selected,
        type: 'post',

    })
}

function openWindow() {
    $("#draggable").center();
    $("#draggable").css("visibility", "visible");
}

function closeWindow() {
    $("#draggable").css("visibility", "hidden");
}

function updateObjectTypes() {
    console.log('updating');
   $.ajax('/objTypes',{
       type:'GET',
       success: function (data) {
           let wrap = $("#objects");
           wrap.empty();
           Object.keys(data).forEach(function (key) {
               let imageLabel = $("<label>", {class: 'imageLabel'});
               let radio = $("<input>",{type: 'radio', name: 'object', 'value': key});
               radio.on('click', function () {
                   if( this.checked )
                       updateProperties();
               });
               let img = $("<img>",{src: 'img/' + data[key].img, id: key + '.img', 'class': 'miniature'}) ;
               let name = $('<label>', {'id': key}).text(key);
               imageLabel.append(radio);
               imageLabel.append(img);
               imageLabel.append(name);
               wrap.append(imageLabel);
           })
       }
   });



}

function updateProperties() {
    let selected = $("input[name='object']:checked").val();
    console.log(selected);
    $.ajax({
        url: '/objTypes?name=' + selected,
        type: 'GET',
        success: function (data) {
            let wrapper = $("#properties");
            wrapper.empty();
            Object.keys(data).forEach(function (key) {
                let lWrap = $("<div/>", {'class':'line'});
                let label = $("<label>").attr('for', key).text(key);
                let input = $("<input>").attr({
                    'type':'text',
                    'name': key,
                    'id': key + '.upd',
                    'class': 'propUpd' + (key === 'img' ? ' inputFile' : '')});
                if( key === 'img' )
                    input.attr('type', 'file');
                else
                    input.val(data[key]);

                lWrap.append(label);
                lWrap.append(input);
                wrapper.append(lWrap);
            });
            console.log(data);
        }
    })
}

//TODO REWORK THIS SHIT AS SOON AS YOU<FUCKER> CAN
function addObject() {
    //Clear previous
    $("#dHeader").empty();
    $("#dBody").empty();
    $("#dFooter").empty();
    openWindow();

    //Header
    let header = $("#dHeader");
    header.append("New");
    const chose = $("<Select/>Type", {id:"opt"});
    header.append(chose);
    const label = ($("<label/>"));
    header.append(label);

    //Body fields
    let body = $("#dBody");
    let props = $("<div/>", {id:"dProps"});
    body.append(props);
    $(Object.getOwnPropertyNames(ojbects)).each(function () {
        chose.append($("<option>").text(this));
    });

    //Generating params fields
    chose.on('change', function () {
       props.empty();
       let currParams  = Object.getOwnPropertyNames(new ojbects[ $('option:selected',this).text() ]); //Long string to get fields of the chosen opt >XD

      currParams.forEach(function (el) {
        let lineWrap = $("<div>",{'class':'line'});
        let label = $("<label>").attr('for', el + '.new').text(el);
        let input;

        if( el === 'img' ){//Image
            input = $("<input>").attr({'type': 'file',
                'name': el, 'id' : 'newFile'});
            let img = $("<img>");
            img.css({
                'max-width': '180px',
                'max-height': '180px',
                'margin': 'auto',
            });
            input.on('change', function () {
                let file = this.files[0];
                let reader = new FileReader();
                reader.onload = function(){
                    img.attr('src', this.result);
                    label.empty();
                    label.append(img);
                };
                reader.readAsDataURL(file);
            });
            input.addClass('new inputFile');
            label = $("<label>").attr('for', 'newFile');
            label.text('Select an image');
            console.log(input);
        }
        //Text
        else{
            input = $("<input>").attr({'type': 'text',
                'name': el, 'class' : 'new', 'id' : el + '.new'});
        }

        lineWrap.append(label);
        lineWrap.append(input);
        props.append(lineWrap);
      })
    });

    //Buttons
    let add = $("<button>Add</button>", {id:"add"});
    let cancel = $("<button>Cancel</button>", {id:"cancel"});
    cancel.on('click', closeWindow);
    add.on('click', function () {
       let formData = new FormData();
        $('input.new').each(function () {
            formData.append($(this).attr('name'), $(this).val());
        });
        $.each($("input[type='file'].new.inputFile")[0].files, function(i, file) {
            formData.append('img', file);
        });

        $.ajax('/submitObj',
           {
               data: formData,
               cache: false,
               contentType: false,
               processData: false,
               type: "POST",
               error: function () {
                   console.log("failed");
                    label.text("The name is already taken");
               },
               success: function () {
                   console.log("succeed");
                   updateObjectTypes();
                   closeWindow();
               }
           }
       )
    });
    body.append(add);
    body.append(cancel);
}

function deleteObject() {
    $.ajax({
        url: '/del/name=' + $("input[name='object']:checked").val(),
        type: 'delete',
        success: function () {
            updateObjectTypes();
            updateProperties();
        }
    })
}

function generateMap(h, w) {
    height = h;
    width = w;
    for (i = 0; i < h; ++i)
        map.push(Array.from({length: w}, () => 0))
    canvas[0].width = w * tileSize;
    canvas[0].height = h * tileSize;
}

function enableGrid(){
    area.lineWidth = 1;
    for( let i = 0; i < height; ++i ){
        area.moveTo(0, i*tileSize);
        area.lineTo(width*tileSize, i*tileSize );
        area.stroke();
    }
    for( let j = 0; j < width; ++j ){
        area.moveTo(j*tileSize, 0 );
        area.lineTo(j*tileSize, height*tileSize );
        area.stroke();
    }



}

canvas.click(function(e){
    area.clearRect(lx, ly, tileSize, tileSize);
    lx = e.clientX - rect.left;
    ly = e.clientY - rect.top;
    area.fillRect(lx, ly, tileSize, tileSize);
});

generateMap(20,20);
updateObjectTypes();
$( function() {
    $( "#draggable" ).draggable();
} );
