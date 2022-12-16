//Change profile
function userInvalid() {
    let fullname = _$('#staticT');
    let dob = _$('#birthday');
    let updated_at = getDateCurr();

    if (!fullname || fullname.value.replaceAll(' ', '') == '') {
        Swal.fire('Message', 'Vui lòng nhập tên của bạn', 'info')
        return false
    }

    let result = {
        id: isUser.innerHTML,
        fullname: fullname.value,
        dob: dob.value,
        gender: _$('#nam').checked ? true : false,
        updated_at
    }
    return result
}

const luuProfile = () => {
    let profile = userInvalid()

    if (!profile) {
        return false
    }

    http.put(`${_URL_MAIN}/Users/update`, profile)
        .then(
            Swal.fire("Oke", 'Lưu thành công', "success")
            .then(rs => {
                location.reload()
            })
        )
        .catch(err => console.log(err))
}

//change password
function passwordInvalid() {
    let currencePass = _$('#currencePass')
    let newPass = _$('#newPass')
    let confirmPass = _$('#confirmPass')

    if (!currencePass || currencePass.value.replaceAll(' ', '') == '') {
        Swal.fire('Message', 'Vui lòng nhập mật khẩu hiện tại!', "info")
        return false
    }

    

    if (!confirmPass || confirmPass.value.replaceAll(' ', '') == '' || confirmPass.value != newPass.value) {
        Swal.fire('Message', 'Nhập lại mật khẩu không chính xác!', "info")
        return false
    }

    return {
        currencePassword: currencePass.value,
        newPassword: newPass.value
    }

}

const changePassword = () => {
    let newPassword = passwordInvalid()

    if (!newPassword) return

    http.post(`${_URL_MAIN}/Users/changepassword`, newPassword)
        .then(data => {
            if (data.status == 200) {
                Swal.fire("Oke", data.data, "success")
                    .then(rs => {
                        location.reload()
                    })
            } else if (data.status == 401) {
                Swal.fire("Oke", data.data, "error")
                    .then(rs => {
                        location.reload()
                    })
            } else {
                Swal.fire('Message', data.data, 'error')
            }
        })
        .catch(err => console.log(err))
}