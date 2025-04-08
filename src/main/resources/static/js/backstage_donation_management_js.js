document.addEventListener("DOMContentLoaded", function () {
    const pageSizeSelect = document.getElementById("pageSizeSelect");
    const pagination = document.getElementById("pagination");
    const dataBody = document.getElementById("data-body");
    const baseURL = window.location.origin;
    const apiUri = `${baseURL}/backStage/donaManage`;
    let allData = [];
    let filteredData = [];
    let currentPage = 1;
    let pageSize = parseInt(pageSizeSelect.value);
    let currentSortField = null;
    let currentSortOrder = "asc";

    const toggle = document.querySelector(".collapsible-menu");
    const target = document.querySelector("#recipientSubMenu");
    toggle.addEventListener("click", function () {
        const isExpanded = toggle.getAttribute("aria-expanded") === "true";
        toggle.setAttribute("aria-expanded", !isExpanded);
        target.classList.toggle("collapse");
        target.classList.toggle("show");
    });

    document.querySelectorAll("th[data-sort-field]").forEach(th => {
        th.addEventListener("click", function () {
            const field = this.getAttribute("data-sort-field");
            sortData(field);
        });
    });

    function sortData(field) {
        if (currentSortField === field) {
            currentSortOrder = currentSortOrder === "asc" ? "desc" : "asc";
        } else {
            currentSortField = field;
            currentSortOrder = "asc";
        }

        const data = filteredData.length ? filteredData : allData;

        data.sort((a, b) => {
            let aValue = a[field];
            let bValue = b[field];
            if (typeof aValue === "string") aValue = aValue.toLowerCase();
            if (typeof bValue === "string") bValue = bValue.toLowerCase();
            if (aValue < bValue) return currentSortOrder === "asc" ? -1 : 1;
            if (aValue > bValue) return currentSortOrder === "asc" ? 1 : -1;
            return 0;
        });

        renderTable(getPageData(currentPage, pageSize));
        renderPagination();
    }


    async function fetchData() {
        try {
            const response = await authFetch(apiUri);
            if (!response.ok) throw new Error("HTTP 錯誤：" + response.status);
            const result = await response.json();

            allData = (result.donationList || []).map(item => ({
                id: item.donationRecordId,
                name: item.identityData,
                idNumber: item.idNum,
                identityType: item.identityType,
                amount: item.donationIncome
            }));

            renderTable(getPageData(currentPage, pageSize));
            renderPagination();
        } catch (err) {
            console.error("載入捐款資料失敗：", err);
        }
    }

    function getPageData(page, limit, data = filteredData.length ? filteredData : allData) {
        if (limit === "all") return data;
        const start = (page - 1) * limit;
        return data.slice(start, start + limit);
    }

    function renderTable(data) {
        dataBody.innerHTML = "";
        data.forEach(item => {
            const tr = document.createElement("tr");

            const tdName = document.createElement("td");
            tdName.textContent = item.name || "";
            tr.appendChild(tdName);

            const tdId = document.createElement("td");
            tdId.textContent = item.idNumber || "(無)";
            tr.appendChild(tdId);

            const tdAmount = document.createElement("td");
            tdAmount.textContent = item.amount != null ? item.amount.toLocaleString() : "0";
            tr.appendChild(tdAmount);

            const tdDetail = document.createElement("td");
            tdDetail.classList.add("text-center", "align-middle");
            const detailBtn = document.createElement("a");
            detailBtn.href = `./backstage_donation_detail.html?id=${item.id}`;
            detailBtn.className = "btn btn-sm btn-info";
            detailBtn.textContent = "查看";
            tdDetail.appendChild(detailBtn);
            tr.appendChild(tdDetail);

            dataBody.appendChild(tr);
        });
    }

    function renderPagination(data = filteredData.length ? filteredData : allData) {
        pagination.innerHTML = "";
        if (pageSize === "all") return;

        const totalPages = Math.ceil(data.length / pageSize);
        if (totalPages <= 1) return;

        let html = `
                <li class="page-item ${currentPage === 1 ? "disabled" : ""}">
                    <a class="page-link" href="#" data-page="${currentPage - 1}">&lt;</a>
                </li>`;

        for (let i = 1; i <= totalPages; i++) {
            html += `
                <li class="page-item ${i === currentPage ? "active" : ""}">
                    <a class="page-link" href="#" data-page="${i}">${i}</a>
                </li>`;
        }

        html += `
                <li class="page-item ${currentPage === totalPages ? "disabled" : ""}">
                    <a class="page-link" href="#" data-page="${currentPage + 1}">&gt;</a>
                </li>`;

        pagination.innerHTML = html;

        document.querySelectorAll("#pagination .page-link").forEach(link => {
            link.addEventListener("click", function (e) {
                e.preventDefault();
                const newPage = parseInt(this.dataset.page);
                if (newPage >= 1 && newPage <= totalPages) {
                    currentPage = newPage;
                    renderTable(getPageData(currentPage, pageSize));
                    renderPagination();
                }
            });
        });
    }

    pageSizeSelect.addEventListener("change", function () {
        pageSize = this.value === "all" ? "all" : parseInt(this.value);
        currentPage = 1;
        renderTable(getPageData(currentPage, pageSize));
        renderPagination();
    });

    document.getElementById("searchInput").addEventListener("input", function () {
        const keyword = this.value.trim().toLowerCase();
        filteredData = keyword
            ? allData.filter(item =>
                item.name.toLowerCase().includes(keyword) ||
                item.idNumber.toLowerCase().includes(keyword)
            )
            : [];

        currentPage = 1;
        renderTable(getPageData(currentPage, pageSize));
        renderPagination();
    });

    document.querySelector(".btn-logout").addEventListener("click", function () {
        if (confirm("確定要登出嗎？")) backstageAuth.logout();
    });

    fetchData();
});