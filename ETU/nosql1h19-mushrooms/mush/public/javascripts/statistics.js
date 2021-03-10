//Charts
let regChart;
let edChart;
let parChart;

//Canvases
let regionC;
let edibleC;
let paramC;

$(()=>{
    //Canvas
    regionC = $('#regionCanvas');
    edibleC = $('#edibleCanvas');
    paramC = $('#paramCanvas');

    regChart = new Chart(regionC[0].getContext('2d'), {type: 'doughnut', data: createChartData('region'),options: {
            legend: {
                position: 'left',
                display: false,
                labels: {
                    fontColor: 'white'
                }
            }
        }});
    edChart = new Chart(edibleC[0].getContext('2d'), {type: 'pie', data: createChartData('edible'), options: {
            legend: {
                labels: {
                    fontColor: 'white'
                }
            }
        }});
    parChart = new Chart(paramC[0].getContext('2d'), {type: 'pie', data: createChartData('region'), options: {
            legend: {
                labels: {
                    fontColor: 'white',
                }
            }
        }});

    //INIT
    //fill sidebar
    $.getJSON("/params", {},(data)=>{
        let d = $.parseJSON(data);
        for(let k in d) {
            $("#sidebarContent").append(twoComboLabel(k, d[k], "params params_correct"));
        }
        $(".params").change(updateChart);
    });
});

function exportDB() {
    $.getJSON('/db/query/main', {}, function (res) {
        res.forEach(function (item) {
            delete item._id;
        });
        console.log(res);
        let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(res));
        let downloadAnchorNode = document.createElement('a');
        downloadAnchorNode.setAttribute("href", dataStr);
        downloadAnchorNode.setAttribute("download", "backup.json");
        document.body.appendChild(downloadAnchorNode); // required for firefox
        downloadAnchorNode.click();
        downloadAnchorNode.remove();
    });
}

function createChartData(url, reqData = {}) {
    let chartData = {
        labels: [],
        datasets: [{
            backgroundColor: [],
            data: []
        }]
    };
    $.get('/db/stats/' + url, reqData, function (res) {
        console.log('gained', res);
        res.forEach(function (it) {
            chartData.labels.push(it._id);
            chartData.datasets[0].data.push(it.count);
            chartData.datasets[0].backgroundColor.push(getRandomColor());
        })
    });
    return chartData;
}

function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function updateChart() {
    let data = {};
    $(".params").each(function () {
        let val = $(this).val();
        if( val !== '0' )
            data[$(this).attr('name')] = val;
    });
    console.log(data);
    parChart.data = createChartData('params', data);
    parChart.update();
}

function statistics_sidebar_open() {
    document.getElementById("statisticsSidebar").style.width = "17%";
    document.getElementById("statisticsSidebar").style.display = "block";
    document.getElementById("openStatisticsSidebar").style.visibility = 'hidden';
}
function statistics_sidebar_close() {
    document.getElementById("statisticsSidebar").style.display = "none";
    document.getElementById("openStatisticsSidebar").style.visibility = "visible";
}