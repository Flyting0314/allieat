const baseURL = window.location.origin;

const API_ENDPOINTS = {
    totalDonations: `${baseURL}/backStage/homePage/totalDonations`,
    totalDonors: `${baseURL}/backStage/homePage/totalDonors`,
    monthlyDonations: `${baseURL}/backStage/homePage/monthlyDonations`,
    newDonors: `${baseURL}/backStage/homePage/newDonors`,
    donationChart: `${baseURL}/backStage/homePage/donationChart`,
    usageChart: `${baseURL}/backStage/homePage/usageChart`,
    watch: `${baseURL}/backStage/homePage/watch`
};

document.addEventListener("DOMContentLoaded", function () {
    // 數值卡片資料
    fetchData("totalDonations", API_ENDPOINTS.totalDonations, "元", "totalDonations", true);          // 金額（加逗號）
    fetchData("totalDonors", API_ENDPOINTS.totalDonors, "人", "totalDonors", false);                   // 人數（不加逗號）
    fetchData("monthlyDonations", API_ENDPOINTS.monthlyDonations, "元", "monthlyDonations", true);    // 金額（加逗號）
    fetchData("newDonors", API_ENDPOINTS.newDonors, "人", "newDonors", false);                         // 人數

    // 圖表資料
    fetchChartData('donationChart', API_ENDPOINTS.donationChart, '累積捐款', '#3a6186');
    fetchChartData('usageChart', API_ENDPOINTS.usageChart, '愛心點領用', 'rgba(54, 162, 235, 1)');

    // 資料變動偵測
    setupLongPollingForUpdate();

    // 側邊選單收合功能
    const toggle = document.querySelector(".collapsible-menu");
    const target = document.querySelector("#recipientSubMenu");

    if (toggle && target) {
        toggle.addEventListener("click", function () {
            const isExpanded = toggle.getAttribute("aria-expanded") === "true";
            toggle.setAttribute("aria-expanded", !isExpanded);
            target.classList.toggle("collapse");
            target.classList.toggle("show");
        });
    }

    // 綁定登出按鈕功能
    document.querySelector(".btn-logout").addEventListener("click", function () {
        if (confirm("確定要登出嗎？")) {
            backstageAuth.logout();
        }
    });
});

// 共用的取資料函式
function fetchData(elementId, apiUrl, unit, key, useFormat = true) {
    authFetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const value = data[key];
            if (value !== undefined) {
                updateElement(elementId, value, unit, useFormat);
            } else {
                console.error(`Key '${key}' not found in response from ${apiUrl}`);
            }
        })
        .catch(error => console.error(`Error fetching ${elementId} data:`, error));
}

// 共用的取圖表函式
function fetchChartData(chartId, apiUrl, label, borderColor) {
    authFetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const chartKey = Object.keys(data)[0];
            renderChart(chartId, data[chartKey], label, borderColor);
        })
        .catch(error => console.error(`Error fetching ${label} data:`, error));
}

// 數字格式化
function formatNumber(num) {
    return num.toLocaleString(); // 加上千位逗號
}

// 更新卡片數值欄位
function updateElement(elementId, value, unit, useFormat = true) {
    const displayValue = useFormat ? formatNumber(value) + unit : value + unit;
    document.getElementById(elementId).innerText = displayValue;
}

// 繪製圖表
function renderChart(chartId, chartData, label, borderColor) {
    const ctx = document.getElementById(chartId).getContext('2d');
    new Chart(ctx, {
        type: 'line',
        data: {
            labels: chartData.labels,
            datasets: [{
                label: label,
                data: chartData.data,
                borderColor: borderColor,
                backgroundColor: 'transparent',
                borderWidth: 2,
                tension: 0.3,
                pointRadius: 3
            }]
        },
        options: {
            responsive: true,
            plugins: { legend: { display: false } },
            scales: {
                y: { beginAtZero: true }
            }
        }
    });
}

// 長輪詢機制：即時監聽變動並更新資料（直接使用推播資料）
function setupLongPollingForUpdate() {
    function poll() {
        authFetch(API_ENDPOINTS.watch)
            .then(response => response.json())
            .then(data => {
                // 直接使用推播回傳資料更新畫面
                if (data.totalDonations !== undefined)
                    updateElement("totalDonations", data.totalDonations, "元", true);
                if (data.totalDonors !== undefined)
                    updateElement("totalDonors", data.totalDonors, "人", false);
                if (data.monthlyDonations !== undefined)
                    updateElement("monthlyDonations", data.monthlyDonations, "元", true);
                if (data.newDonors !== undefined)
                    updateElement("newDonors", data.newDonors, "人", false);
                if (data.donationChart)
                    renderChart("donationChart", data.donationChart, "累積捐款", "#3a6186");
                if (data.usageChart)
                    renderChart("usageChart", data.usageChart, "愛心點領用", "rgba(54, 162, 235, 1)");

                poll(); // 持續輪詢
            })
            .catch(err => {
                console.warn("Polling error, retrying in 3s...", err);
                setTimeout(poll, 3000);
            });
    }

    poll(); // 啟動輪詢
}

// 驗證登入狀態
window.backstageAuth.requireLogin();
