//# sourceURL=register.js


function requestRegister() {
    const promptElement = $("#prompt");
    const username = $("input[name=username]").val();
    const password = $("input[name=password]").val()
    const repeatedPassword = $("input[name=repeatedPassword]").val()
    const email = $("input[name=email]").val();
    const verificationCode = $("input[name=verificationCode]").val()
    if (checkBlank(username, password, repeatedPassword, email, verificationCode)) {
        promptForSecond(promptElement, fillBlankPrompt, promptRemainSecond);
    } else {
        if (repeatedPassword !== password) {
            promptForSecond(promptElement, passwordMismatchPrompt, promptRemainSecond);
            return;
        }
        if (md5(verificationCode) !== verifyCode) {
            promptForSecond(promptElement, verifyFailPrompt, promptRemainSecond);
            return;
        }
        const form = {"username": username, "password": password, "email": email};
        $.ajax({
            url: "/register",
            data: JSON.stringify(form),
            header: {
                Authorization: getToken()
            },
            type: "POST",
            success: (data) => {
                if (data !== "") {
                    promptForSecond(promptElement, data, promptRemainSecond);
                } else {
                    alert(registerSuccessPrompt);
                    window.location.href = "/login";
                }
            }
        });
    }
}

