/*
* 共用驗證頁面，共用
* 1.登入狀態檢查
* 2.登出並導向登入頁面
* 3.發送Token的功能，使用方式將原本用的Fetch Api
*   改使用為封裝過後的authFetch Api
*   使用時請import這支js
*/

const loginPagePath = "/backstage_login.html";

// 檢查登入狀態，未登入則導向登入頁
function requireLogin() {
    document.addEventListener("DOMContentLoaded", function () {
        const token = localStorage.getItem("jwtToken");
        if (!token) {
            window.location.href = loginPagePath;
        }
    });
}

// 登出並導向登入頁
function logout() {
    localStorage.removeItem("jwtToken");
    window.location.href = loginPagePath;
}

// 自動附帶 JWT Token 的 fetch 封裝
let hasRedirected = false;// 用於 判斷是否轉跳過
async function authFetch(url, options = {}) {
    const token = localStorage.getItem("jwtToken");
    const headers = options.headers || {};

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    // 如果有 JSON 物件就自動轉換
    if (options.body && typeof options.body === "object") {
        headers["Content-Type"] = "application/json";
        options.body = JSON.stringify(options.body);
    }

    const response = await fetch(url, {
        ...options,
        headers
    });

    // 接收後端 token 過期後的轉跳
    if ((response.status === 401 || response.status === 403) && !hasRedirected) {
        hasRedirected = true;
        alert("登入已過期，請重新登入");
        localStorage.removeItem("jwtToken"); // 清掉舊 token
        window.location.href = loginPagePath;
        return;
    }

    return response;
}

// 匯出到 global（其他頁面直接使用）
window.backstageAuth = {
    requireLogin,
    logout,
    authFetch
};
