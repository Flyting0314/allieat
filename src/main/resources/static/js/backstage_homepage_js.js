const baseURL = window.location.origin;

const API_ENDPOINTS = {
    totalDonations: `${baseURL}/backStage/homePage/totalDonations`,
    totalDonors: `${baseURL}/backStage/homePage/totalDonors`,
    monthlyDonations: `${baseURL}/backStage/homePage/monthlyDonations`,
    newDonors: `${baseURL}/backStage/homePage/newDonors`,
    donationChart: `${baseURL}/backStage/homePage/donationChart`,
    usageChart: `${baseURL}/backStage/homePage/usageChart`,
    watch: `${baseURL}/backStage/homePage/watch` // 長輪詢 API
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

    // 啟動長輪詢監聽後端是否更新
    setupLongPollingForUpdate();
});

// 共用的取資料函式
function fetchData(elementId, apiUrl, unit, key, useFormat = true) {
    authFetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const value = data[key];
            if (value !== undefined) {
                const displayValue = useFormat ? formatNumber(value) + unit : value + unit;
                document.getElementById(elementId).innerText = displayValue;
            } else {
                console.error(`Key '${key}' not found in response from ${apiUrl}`);
            }
        })
        .catch(error => console.error(`Error fetching ${elementId} data:`, error));
}

// 共用的取資料函式（圖表）
function fetchChartData(chartId, apiUrl, label, borderColor) {
    authFetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            const chartKey = Object.keys(data)[0];
            const chartData = data[chartKey];

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
        })
        .catch(error => console.error(`Error fetching ${label} data:`, error));
}

// 加上千位逗號
function formatNumber(num) {
    return num.toLocaleString();
}

// 長輪詢監聽是否有後端資料更新
function setupLongPollingForUpdate() {
    function poll() {
        authFetch(API_ENDPOINTS.watch)
            .then(res => res.json())
            .then(data => {
                if (data.state) {
                    location.reload(); // 有更新就整頁重整
                } else {
                    poll(); // 沒有就繼續等
                }
            })
            .catch(err => {
                console.error("長輪詢錯誤:", err);
                setTimeout(poll, 5000); // 失敗時延遲重試
            });
    }

    poll(); // 初始啟動
}

// 驗證有沒有 token，若沒有 token 則回到首頁，前端驗證權限的方式。
window.backstageAuth.requireLogin();
