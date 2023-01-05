const _URL_MAIN = 'http://localhost:8080'
const _ADMIN_DIR = '/admin'
const loadingUrl = 'https://img.pikbest.com/png-images/20190918/cartoon-snail-loading-loading-gif-animation_2734139.png'

function formatVND(value) {
    return new Intl.NumberFormat().format(value) + ' ₫'
}

const uploadToImbb = (e, output = false, outputSrc = false, callback = false) => {

    let files = e.target.files

    if (files) {
        if (outputSrc && output == '') { // nhiều ảnh
            if (outputSrc.classList.contains('hasImgs')) {
                let wrapHtml = outputSrc.innerHTML
                outputSrc.innerHTML = wrapHtml.substr(0, wrapHtml.lastIndexOf('<button'))
            } else {
                outputSrc.innerHTML = ''
            }
        }
        files.forEach(file => {
            if (file.size > $(this).data('max-size') * 1024) {

                return {
                    data: 'Vui lòng chọn file có dung lượng nhỏ hơn!',
                    error: 'Max size upload',
                    status: 19
                }
            }

            if (outputSrc && output) {
                outputSrc.src = loadingUrl
            }
            if (outputSrc && output == '') {
                outputSrc.innerHTML = outputSrc.innerHTML + `<div class="col-sm-4 mt-4">
                        <div class="review__imgs-product">
                            <img src="${loadingUrl}" alt="IMG" class="imgs__product-item loadding">
                            <button onclick="removeProductImgs(this)" type="button" class="btn-close imgs__product-btn--close" ></button>
                        </div>
                    </div>`
            }

            console.log('Đang upload hình ảnh lên imgbb...')

            let apiUrl = 'https://cf-kodoku.imgbb.com/json'
            let auth_token = 'fe8b474e191692f1877c6ae57603bb0b0473e296'
            let options = {
                async: false,
                crossDomain: true,
                processData: false,
                contentType: false,
                method: 'POST',
                headers: {
                    Accept: 'application/json',
                },
                mimeType: 'multipart/form-data',
            }

            let formData = new FormData()

            formData.append('source', file)
            formData.append('type', 'file')
            formData.append('action', 'upload')
            formData.append('timestamp', (+new Date()) * 1)
            formData.append('auth_token', auth_token)

            options.body = formData

            fetch(apiUrl, options)
                .then((response) => {
                    return response.json()
                })
                .then((response) => {

                    let obj = response
                    let linkRS = obj.image.display_url
                    console.log("Link: " + linkRS)

                    if (output) {
                        output.value = linkRS
                    }

                    if (callback != false) {
                        callback(obj)
                    }

                })
        })
    }
}

Circles.create({
    id: 'circles-1',
    radius: 45,
    value: 60,
    maxValue: 100,
    width: 7,
    text: 5,
    colors: ['#f1f1f1', '#FF9E27'],
    duration: 400,
    wrpClass: 'circles-wrp',
    textClass: 'circles-text',
    styleWrapper: true,
    styleText: true
})

Circles.create({
    id: 'circles-2',
    radius: 45,
    value: 70,
    maxValue: 100,
    width: 7,
    text: 36,
    colors: ['#f1f1f1', '#2BB930'],
    duration: 400,
    wrpClass: 'circles-wrp',
    textClass: 'circles-text',
    styleWrapper: true,
    styleText: true
})

Circles.create({
    id: 'circles-3',
    radius: 45,
    value: 40,
    maxValue: 100,
    width: 7,
    text: 12,
    colors: ['#f1f1f1', '#F25961'],
    duration: 400,
    wrpClass: 'circles-wrp',
    textClass: 'circles-text',
    styleWrapper: true,
    styleText: true
})

var totalIncomeChart = document.getElementById('totalIncomeChart').getContext('2d');

var mytotalIncomeChart = new Chart(totalIncomeChart, {
    type: 'bar',
    data: {
        labels: ["S", "M", "T", "W", "T", "F", "S", "S", "M", "T"],
        datasets: [{
            label: "Total Income",
            backgroundColor: '#ff9e27',
            borderColor: 'rgb(23, 125, 255)',
            data: [6, 4, 9, 5, 4, 6, 4, 3, 8, 10],
        }],
    },
    options: {
        responsive: true,
        maintainAspectRatio: false,
        legend: {
            display: false,
        },
        scales: {
            yAxes: [{
                ticks: {
                    display: false //this will remove only the label
                },
                gridLines: {
                    drawBorder: false,
                    display: false
                }
            }],
            xAxes: [{
                gridLines: {
                    drawBorder: false,
                    display: false
                }
            }]
        },
    }
});

$('#lineChart').sparkline([105, 103, 123, 100, 95, 105, 115], {
    type: 'line',
    height: '70',
    width: '100%',
    lineWidth: '2',
    lineColor: '#ffa534',
    fillColor: 'rgba(255, 165, 52, .14)'
});

$(document).ready(function () {
    $('#basic-datatables').DataTable({


    });

    $('#multi-filter-select').DataTable({
        "pageLength": 5,
        initComplete: function () {
            this.api().columns().every(function () {
                var column = this;
                var select = $(
                        '<select class="form-control"><option value=""></option></select>'
                    )
                    .appendTo($(column.footer()).empty())
                    .on('change', function () {
                        var val = $.fn.dataTable.util.escapeRegex(
                            $(this).val()
                        );

                        column
                            .search(val ? '^' + val + '$' : '', true, false)
                            .draw();
                    });

                column.data().unique().sort().each(function (d, j) {
                    select.append('<option value="' + d + '">' + d +
                        '</option>')
                });
            });
        }
    });

    // Add Row
    $('#add-row').DataTable({
        "pageLength": 5,
    });

    var action =
        '<td> <div class="form-button-action"> <button type="button" data-toggle="tooltip" title="" class="btn btn-link btn-primary btn-lg" data-original-title="Edit Task"> <i class="fa fa-edit"></i> </button> <button type="button" data-toggle="tooltip" title="" class="btn btn-link btn-danger" data-original-title="Remove"> <i class="fa fa-times"></i> </button> </div> </td>';

    $('#addRowButton').click(function () {
        $('#add-row').dataTable().fnAddData([
            $("#addName").val(),
            $("#addPosition").val(),
            $("#addOffice").val(),
            action
        ]);
        $('#addRowModal').modal('hide');

    });
});