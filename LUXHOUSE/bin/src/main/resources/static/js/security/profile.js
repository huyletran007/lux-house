//Change profile
function userInvalid() {
    let fullname = _$('#staticT');
    let dob = _$('#birthday');
    let updated_at = getDateCurr();

    if(!fullname || fullname.value.replaceAll(' ', '') == '') {
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

}

const changePassword = () => {
    
}

