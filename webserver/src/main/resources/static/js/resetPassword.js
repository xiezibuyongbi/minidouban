//# sourceURL=resetPassword.js

function resetPassword() {
    const promptElement = $("#prompt");
    const username = $("input[name=username]").val();
    const password = $("input[name=desiredPassword]").val()
    const repeatedPassword = $("input[name=repeatedPassword]").val()
    const email = $("input[name=email]").val();
    const verificationCode = $("input[name=verificationCode]").val()
    if (checkBlank(username, password, repeatedPassword, email, verificationCode)) {
        promptElement.text(fillBlankPrompt);
    } else {
        if (repeatedPassword !== password) {
            promptElement.text(passwordMismatchPrompt);
            return;
        }
        if (md5(verificationCode) !== verifyCode) {
            promptElement.text(verifyFailPrompt);
            return;
        }
        const list = {"username": username, "password": password, "email": email};
        $.ajax({
            url: "/reset_password",
            type: "POST",
            data: JSON.stringify(list),
            header: {
                Authorization: getToken()
            },
            success: (data) => {
                if (data !== "") {
                    promptElement.text(data);
                } else {
                    alert(resetPasswordSuccessPrompt);
                    window.location.href = "/login";
                }
            }
        });
    }
}