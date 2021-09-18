//# sourceURL=common.js

let switcher = false

function fullCheckSwitch(checkBoxName) {
    $("input[name=" + checkBoxName + "]").each(function () {
        this.checked = !switcher;
    });
    switcher = !switcher;
}

function checkBlank() {
    for (let argument of arguments) {
        if (argument === "")
            return true;
    }
    return false;
}

const fillBlankPrompt = "请填写完整！";
const verifyFailPrompt = "验证码错误！"
const passwordMismatchPrompt = "两次输入的密码不一致！";
const emailNotExistPrompt = "邮件验证码发送失败！";
const fillEmailBlankPrompt = "请输入邮箱！";
const registerSuccessPrompt = "注册成功！";
const invalidEmailAddressPrompt = "邮箱格式非法！";
const resetPasswordSuccessPrompt = "修改成功！";

const promptRemainSecond = 3;

const validEmailPattern = /[0-9a-zA-Z_]{1,19}@[0-9a-zA-Z_]{0,19}.*\.(com|cn|net)/;
let verifyCode = null;

function getCookie(name) {
    const cookies = document.cookie.split(";");
    cookies.forEach((cookie) => {
        if (cookie.indexOf(name) === 0) {
            return cookie.substring(name.length + 1, cookie.length);
        }
    })
    return "";
}

function getToken() {
    return getCookie("token");
}

// only work as 30 seconds counter
// input label for email in html must have attribute `id` named email
function getVerifyCode() {
    const email = $("#email").val();
    const promptLabel = $("#prompt");
    promptLabel.text("");
    if (email === "") {
        promptForSecond(promptLabel, fillEmailBlankPrompt, promptRemainSecond);
        return;
    }
    if (!validEmailPattern.test(email)) {
        promptForSecond(promptLabel, invalidEmailAddressPrompt, promptRemainSecond);
        return;
    }
    countDown($("#get-verify-code-button"), 30);
    $.ajax({
        url: "/get-verify-code",
        type: "POST",
        contentType: "application/json;charset=UTF-8",
        data: "email=" + email,
        success:
            (data) => {
                verifyCode = data;
                if (verifyCode === "") {
                    promptLabel.text(emailNotExistPrompt);
                }
            },
        header: {
            Authorization: getToken()
        }
    });
}

function countDown(element, total) {
    const originText = element.text();
    element.attr("disabled", "disabled");
    let id = setInterval(() => {
        if (total > 0) {
            total--;
            element.text(total.toString() + "秒后重新发送");
        } else {
            element.text(originText);
            element.attr("disabled", false);
            clearInterval(id);
        }
    }, 1000);
}

function backToTop() {
    scroll(0, 0);
}

function getQueryVar(variable) {
    let queryParams = decodeURIComponent(window.location.search).substring(1);
    queryParams = queryParams.split("&");
    for (let i = 0; i < queryParams.length; i++) {
        const param = queryParams[i].split("=");
        if (param[0] === variable) {
            return param[1];
        }
    }
    return "";
}

function promptForSecond(element, prompt, second) {
    element.text(prompt);
    promptCountdown(element, second);
}

function promptCountdown(element, second) {
    let id = setInterval(() => {
        if (second > 0) {
            second--;
        } else {
            element.text("");
            clearInterval(id);
        }
    }, 1000);
}

function addCookie(name, value, timestamp) {
    document.cookie = name + "=" + value + "; " + timestamp;
}

//TODO login与其他url在request时作区分
function authorize() {
    let token = getToken();

}