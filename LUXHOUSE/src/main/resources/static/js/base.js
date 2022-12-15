// Khai báo rút gọn query seletor
const _$ = document.querySelector.bind(document)
const _URL_MAIN = 'http://localhost:8080'
const isUser = _$('#userId')

function formatVND(value) {
    return new Intl.NumberFormat().format(value) + ' ₫'
}

function getDateCurr() {
    let nowDate = new Date()
    const offset = nowDate.getTimezoneOffset()

    nowDate = new Date(nowDate.getTime() - (offset * 60 * 1000))
    return nowDate.toISOString().split('T')[0]

    nowDate = new Date(nowDate.getTime() - (offset))
    return nowDate.toISOString()
}

function getDateCurrStr(currentDate = '') {
    let currentdate = currentDate == '' ? new Date() : new Date(currentDate)
    return ((currentdate.getHours() < 10) ? ('0' + currentdate.getHours()) : currentdate.getHours()) + ":" +
        ((currentdate.getMinutes() < 10) ? ('0' + currentdate.getMinutes()) : currentdate.getMinutes()) + ":" +
        ((currentdate.getSeconds() < 10) ? ('0' + currentdate.getSeconds()) : currentdate.getSeconds()) + ", " +
        ((currentdate.getDate() < 10) ? ('0' + currentdate.getDate()) : currentdate.getDate()) + "-" +
        (((currentdate.getMonth() + 1) < 10) ? ('0' + (currentdate.getMonth() + 1)) : (currentdate.getMonth() + 1)) + "-" +
        ((currentdate.getFullYear() < 10) ? ('0' + currentdate.getFullYear()) : currentdate.getFullYear())
}

function getDateVN(timestamp = '') {
    let date = new Date((timestamp) * 1)
    let time_add = getDateCurrStr((timestamp) * 1)
    if (timestamp == '') {
        date = new Date();
        time_add = getDateCurrStr()
    }
    let current_day = date.getDay();
    let day_name = '';


    switch (current_day) {
        case 0:
            day_name = "Chủ Nhật";
            break;
        case 1:
            day_name = "Thứ Hai";
            break;
        case 2:
            day_name = "Thứ Ba";
            break;
        case 3:
            day_name = "Thứ Tư";
            break;
        case 4:
            day_name = "Thứ Năm";
            break;
        case 5:
            day_name = "Thứ sáu";
            break;
        case 6:
            day_name = "Thứ Bảy";
    }

    return day_name + ', ' +
        ((date.getDate() < 10) ? ('0' + date.getDate()) : date.getDate()) + "/" +
        (((date.getMonth() + 1) < 10) ? ('0' + (date.getMonth() + 1)) : (date.getMonth() + 1))
}