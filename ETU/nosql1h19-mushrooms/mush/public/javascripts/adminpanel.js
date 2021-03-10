//Sidebar fuctions
function admin_sidebar_open() { //открытие сайдбара с параметрами
    document.getElementById("adminSidebar").style.width = "17%";
    document.getElementById("adminSidebar").style.display = "block";
    document.getElementById("openAdminSidebar").style.visibility = 'hidden';
}
function admin_sidebar_close() { //закрытие
    document.getElementById("adminSidebar").style.display = "none";
    document.getElementById("openAdminSidebar").style.visibility = "visible";
}

var importData;

function send() { //отправка данных 'по одному'
    if( importData.length > 0 ) //В цикле пока есть данные, отправляем первый элемент в БД, удаляем его из массива
        $.post('/db/import', importData[0], function () {
            importData.shift();
            console.log('sent');
        });
}

function myLoop () { //цикл для импора бд, каждые 200мс по одной записи, чтобы не было переполнения
    setTimeout(function () {
        if( importData.length > 0)
        {
            console.log(importData.length);
            send();
            myLoop();
        }
    }, 200)
}

function exportDB() { //экспорт бд - получаем полный список через аякс
    $.getJSON('/db/query/main', {}, function (res) {
        res.forEach(function (item) {
            delete item._id; //удаляем айдишники
        });
        console.log(res);

    });
}

function importDB() { //добавляем скрытый инпут
    fileIn = $("<input />", {
        type: 'file',
        style: 'display: none',
    });

    fileIn.change(function () { //при нажатии юзверю открывается окно
        let reader = new FileReader();
        reader.onload = function (event) { //по загрузке файла
            importData = JSON.parse(event.target.result); //отправляем записи по одной

            myLoop();

            queryToDb(fillAdmin);
            alert("Go take a brake, it's gonna take like 10*n sec")
        };
        reader.readAsText(event.target.files[0]);
    });
    fileIn.click();
}

function fillSuggestions(data){
    let list = $('#userList');
    list.empty();
    if( data.length === 0)
    {
        list.append("Suggestion list is empty");
        return;
    }
    data.forEach(function (sugggestion) {
        li = $("<li />");
        sugggestion.ttype = 'user';
        li.text(sugggestion.name);
        li.on('click', function () {
            usermodal_open(sugggestion);
        });
        list.append(li);
    })
}

function fillAdmin(data){
    let list = $('#adminList');
    list.empty();
    if( data.length === 0)
    {
        list.append("Your DB is empty");
        return;
    }
    data.forEach(function (sugggestion) {
        li = $("<li />");
        sugggestion.ttype = 'admin';
        li.text(sugggestion.name);
        li.on('click', function () {
            usermodal_open(sugggestion);
        });
        list.append(li);
    })
}

function collectData(){ //сбор данных в объект для поиска по бд
    let data = {};
    //Sidebar
    if( $("#adminList").is(":visible") ) //если открыт сайдбар, то собираем оттуда значения с инпутов класса .params
    {
        $(".params").each(function () {
            let val = $("option:selected", this).val();
            if( val !== '0' )
                data[$("option:first", this).text()] = val;
        });
    }
    //name,region
    let name = $("#query").val(); // получение имени с поискового поля
    let region = $("#openMap").val();
    if( name !== "" )
        data.name = name; //если имя не путое, то добавляем его в объект данных
    return data;
}

function queryToDb(callback = false){ //запрос в основную БД по параметрам, собранным через collectData()
    $.getJSON('/db/query/main', collectData(), function (data) {
        if( callback )
            callback(data);
    } );
}

//Filling sidebar
$.getJSON("/params", {},(data) => { // заполнение сайд-бара (чтобы руками не прописывать 22 параметра)
    d = $.parseJSON(data);
    for(let k in d) { // получаем список параметров и добавляем инпуты
        if( k !== 'name' && k !== 'description' && k !== 'region' && k!=='_id' ){ // если параметра - не регион/имя/описание
            $("#adminSidebarWrapper").append(twoComboLabel(k, d[k], "params params_correct")); //то добавляем инпут
        }
    }
    $(".params").change(function () { //биндим, чтобы при изменении инпута, обновлялась админская колонка
        queryToDb(fillAdmin);
    });
});

//INIT
$(()=>{
    document.getElementById('adminMush').onclick = function (){ //при нажатии на создание гриба открываем модальное с определённым параметром
        usermodal_open({ttype:'admin'});
    };

    //Query to suggestions
    $.getJSON('/db/query/suggestions', {}, function (data) { //при открытии страницы заполняем колонку предложку
        console.log('querying to suggestions');
        fillSuggestions(data);
    } );

    //On change of query/params string
    $("#query").bind('input', function () { //при вводе в поисковое поле будет обновляться колонка основной бд
        queryToDb(fillAdmin);
    });

    queryToDb(fillAdmin); //при открытии страницы заполняем колонку основной бд
});
