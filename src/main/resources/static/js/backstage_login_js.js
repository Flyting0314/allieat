// 動態抓取後端 API 根位置
const baseURL = window.location.origin;
const passDataAPI = `${baseURL}/backStage/login`;
const loginPagePath = "/backstage_login.html";  // 請依實際登入頁面路徑調整

// ====== 登入頁專用 ======
function setupLoginPage() {
    function fillRememberedInfo() {
        const savedUsername = localStorage.getItem("username");
        const rememberMe = localStorage.getItem("rememberMe");
        const usernameInput = document.getElementById("username");
        const rememberCheckbox = document.getElementById("rememberMe");

        if (!usernameInput || !rememberCheckbox) return;

        // 先設定 checkbox 勾選狀態
        rememberCheckbox.checked = (rememberMe === "true");

        // 如果有勾選記住我，才填入帳號
        if (rememberCheckbox.checked && savedUsername) {
            usernameInput.value = savedUsername;
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", fillRememberedInfo);
    } else {
        fillRememberedInfo();
    }
}

async function login() {
    const username = document.getElementById("username").value.trim();
    const password = document.getElementById("password").value.trim();
    const rememberMe = document.getElementById("rememberMe").checked;

    if (!username || !password) {
        showError("請輸入帳號與密碼");
        return;
    }

    const requestData = {
        account: username,
        password: password
    };

    try {
        const response = await fetch(passDataAPI, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        });

        const data = await response.json();

        if (!response.ok) {
            showError("帳號或密碼錯誤，請重新輸入");
            return;
        }

        if (data.loginState === "login ok") {
            if (data.token) {
                localStorage.setItem("jwtToken", data.token);

                const payload = parseJwt(data.token);
                if (payload.sub) {
                    localStorage.setItem("loginAccount", payload.sub);
                }
            }

            localStorage.setItem("username", username);
            localStorage.setItem("rememberMe", rememberMe.toString());

            window.location.href = "../backstage_homepage.html";
        } else {
            showError("登入失敗，請稍後再試");
        }
    } catch (error) {
        showError("登入服務異常，請稍後再試");
        console.error("Login error:", error);
    }
}

// 錯誤訊息顯示 function（共用）
function showError(message) {
    const errorMsgElement = document.getElementById("error-msg");
    if (errorMsgElement) {
        errorMsgElement.innerText = message;
    } else {
        alert(message);
    }
}

// ====== 其他頁面專用 ======
function requireLogin() {
    document.addEventListener("DOMContentLoaded", function () {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
            window.location.href = loginPagePath;
        }
    });
}

// ====== 登出功能（可選） ======
function logout() {
    localStorage.removeItem("jwtToken");
    localStorage.removeItem("loginAccount");
    window.location.href = loginPagePath;
}

// ====== 解析 JWT Payload ======
function parseJwt(token) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
        atob(base64).split('').map(c => {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join('')
    );
    return JSON.parse(jsonPayload);
}

// ====== 匯出到 global（直接用） ======
window.backstageAuth = {
    setupLoginPage,
    login,
    requireLogin,
    logout
};

window.addEventListener("load", function () {
       backstageAuth.setupLoginPage();});
