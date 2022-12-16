function forgotpasswordInvalid() {
    let yourEmail = _$('#yourEmail')

    if (!yourEmail || yourEmail.value.replaceAll(' ', '') == '') {
        Swal.fire('Message', 'Vui lòng nhập email của bạn', 'info')
        return false
    }

    let result = {
        email : yourEmail.value
    }

    return result
}

const forgotPassword = () => {
    let forgotpass = forgotpasswordInvalid()

    if (!forgotpass) {
        return false
    }

    http.post(`${_URL_MAIN}/Users/forgot-password`, forgotpass)
        .then(data => {
            if(data.status == 200){
                Swal.fire("Oke", 'Đã gửi tin nhắn đến email của bạn', "success")
                .then(rs => {
                    if (rs.isConfirmed) window.location.href = '/users/login'
                })
            }else{
                Swal.fire('Message', data.data, 'error')
            }
        }
        )
        .catch(err => console.log(err))
}