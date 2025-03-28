const baseURL = window.location.origin;

const API_ENDPOINTS = {
    totalDonations: `${baseURL}/backStage/homePage/totalDonations`,
    totalDonors: `${baseURL}/backStage/homePage/totalDonors`,
    monthlyDonations: `${baseURL}/backStage/homePage/monthlyDonations`,
    newDonors: `${baseURL}/backStage/homePage/newDonors`,
    donationChart: `${baseURL}/backStage/homePage/donationChart`,
    usageChart: `${baseURL}/backStage/homePage/usageChart`
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
});

function fetchData(elementId, apiUrl, unit, key, useFormat = true) {
    fetch(apiUrl)
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

function fetchChartData(chartId, apiUrl, label, borderColor) {
    fetch(apiUrl)
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

function formatNumber(num) {
    return num.toLocaleString(); // 加上千位逗號
}
