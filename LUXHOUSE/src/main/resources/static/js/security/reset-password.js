function resetpasswordInvalid () {
    let newpass = _$('#newPass')
    let confirmpass = _$('#confirmPass')

    if (!newpass || newpass.value.replaceAll(' ', '') == '') {
        Swal.fire('Message', 'Vui lòng nhập mật khẩu mới của bạn', 'info')
        return false
    }

    if (!confirmpass || confirmpass.value.replaceAll(' ', '') == '' || confirmpass.value != newpass.value) {
        Swal.fire('Message', 'Vui lòng nhập lại mật khẩu của bạn', 'info')
        return false
    }

    let result = {
        newPass : newpass.value
    }

    return result
}
const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
  });

const url = params.token

const resetPassword = () => {
    let resetpass = resetpasswordInvalid()

    if (!resetpass) {
        return false
    }
    

    http.post(`${_URL_MAIN}/Users/reset-password?token=${url}`, resetpass)
        .then(data => {
            if (data.status == 200) {
                Swal.fire("Oke", data.data, "success")
                    .then(rs => {
                        if (rs.isConfirmed) window.location.href = '/users/login'
                    })
            } else if (data.status == 401) {
                Swal.fire("Oke", data.data, "error")
                    .then(rs => {
                        console.log(data.data)
                    })
            } else {
                Swal.fire('Message', data.data, 'error')
            }
        })
        .catch(err => console.log(err))
}