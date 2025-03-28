// 動態抓取後端 API 根位置
const baseURL = window.location.origin;  // e.g., http://localhost:8090 或部署的網域
const passDataAPI = `${baseURL}/backStage/login`;

// 頁面載入時，自動填入已記住的帳號
document.addEventListener("DOMContentLoaded", function () {
    const savedUsername = localStorage.getItem("username");
    if (savedUsername) {
        document.getElementById("username").value = savedUsername;
        document.getElementById("rememberMe").checked = true;
    }
});

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

        if (!response.ok || data.redirectUrl === null) {
            showError("帳號或密碼錯誤，請重新輸入");
            return;
        }

        if (data.loginState === "login ok" && data.redirectUrl) {
            if (rememberMe) {
                localStorage.setItem("username", username);
            } else {
                localStorage.removeItem("username");
            }
            window.location.href = data.redirectUrl;  // 由後端決定轉跳路徑
        } else {
            showError("登入失敗，請稍後再試");
        }
    } catch (error) {
        showError("登入服務異常，請稍後再試");
        console.error("Login error:", error);
    }
}

function showError(message) {
    document.getElementById("error-msg").innerText = message;
}