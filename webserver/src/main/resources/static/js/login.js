//# sourceURL=login.js

function login() {
    $.ajax({
        url: "/login",
        type: "POST",
        data: $("#loginForm").serialize(),
        header: {
            Authorization: getToken()
        },
        success: (xhr) => {
            const token = xhr.getResponseHeader("Authorization");
            addCookie("token", token, token.parseJSON().timestamp);
        }
    });
}
