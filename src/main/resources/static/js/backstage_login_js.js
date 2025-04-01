// 動態抓取後端 API 根位置
const baseURL = window.location.origin;  // e.g., http://localhost:8090 或部署的網域
const passDataAPI = `${baseURL}/backStage/login`;
const loginPagePath = "/backstage_login.html";  // 請依實際登入頁面路徑調整

// ====== 登入頁專用 ======
function setupLoginPage() {
    // 頁面載入時，自動填入已記住的帳號
    document.addEventListener("DOMContentLoaded", function () {
        const savedUsername = localStorage.getItem("username");
        if (savedUsername) {
            document.getElementById("username").value = savedUsername;
            document.getElementById("rememberMe").checked = true;
        }
    });
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

        if (!response.ok || data.redirectUrl === null) {
            showError("帳號或密碼錯誤，請重新輸入");
            return;
        }

        if (data.loginState === "login ok" && data.redirectUrl) {
            // 儲存 JWT Token
            if (data.token) {
                localStorage.setItem("jwtToken", data.token);
            } else {
                console.warn("後端未回傳 JWT token");
            }

            // 記住帳號（如果有勾選）
            if (rememberMe) {
                localStorage.setItem("username", username);
            } else {
                localStorage.removeItem("username");
            }

            // 導向後端指定的頁面
            window.location.href = data.redirectUrl;
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
        alert(message);  // fallback
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
    window.location.href = loginPagePath;
}

// ====== 匯出到 global（直接用） ======
window.backstageAuth = {
    setupLoginPage,
    login,
    requireLogin,
    logout
};