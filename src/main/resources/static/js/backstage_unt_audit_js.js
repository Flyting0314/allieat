// JavaScript：僅顯示 status 為 2 的資料，移除搜尋功能，排序固定為註冊時間

document.addEventListener("DOMContentLoaded", function () {
  const pageSizeSelect = document.getElementById("pageSizeSelect");
  const pagination = document.getElementById("pagination");
  const dataBody = document.getElementById("data-body");
  const baseURL = window.location.origin;
  const apiUri = `${baseURL}/backStage/orgManage`;
  let allData = [];
  let currentPage = 1;
  let pageSize = parseInt(pageSizeSelect.value);
  const toggle = document.querySelector(".collapsible-menu");
  const target = document.querySelector("#recipientSubMenu");

  toggle.addEventListener("click", function () {
    const isExpanded = toggle.getAttribute("aria-expanded") === "true";
    toggle.setAttribute("aria-expanded", !isExpanded);
    target.classList.toggle("collapse");
    target.classList.toggle("show");
  });

  async function fetchData() {
    try {
      const response = await authFetch(apiUri);
      if (!response.ok) throw new Error("HTTP 錯誤，狀態碼：" + response.status);
      const result = await response.json();
      allData = result.orgInitDataList || [];
      allData = allData.filter(item => item.status === 2);
      allData.sort((a, b) => new Date(a.createdTime) - new Date(b.createdTime));
      renderTable(getPageData(currentPage, pageSize, allData));
      renderPagination(allData);
    } catch (error) {
      console.error("載入資料時發生錯誤：", error);
    }
  }

  function getPageData(page, limit, data) {
    if (limit === "all") return data;
    const startIndex = (page - 1) * limit;
    return data.slice(startIndex, startIndex + limit);
  }

  function pad(n) {
    return n.toString().padStart(2, '0');
  }

  function renderTable(data) {
    dataBody.innerHTML = "";
    data.forEach(item => {
      const tr = document.createElement("tr");

      let tdName = document.createElement("td");
      tdName.textContent = item.name;
      tr.appendChild(tdName);

      let tdType = document.createElement("td");
      tdType.textContent = item.type;
      tr.appendChild(tdType);

      let tdCreated = document.createElement("td");
      const date = new Date(item.createdTime);
      tdCreated.textContent = `${date.getFullYear()}/${pad(date.getMonth() + 1)}/${pad(date.getDate())}  ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
      tr.appendChild(tdCreated);

      let tdStatus = document.createElement("td");
      let badge = document.createElement("span");
      badge.classList.add("badge", "badge-warning", "badge-status");
      badge.textContent = "待審核";
      tdStatus.appendChild(badge);
      tr.appendChild(tdStatus);

      let tdAction = document.createElement("td");
      tdAction.classList.add("text-center", "align-middle");

      let editBtn = document.createElement("a");
	  editBtn.href = `./backstage_unit_audit_update.html?id=${item.organizationId}`;
      editBtn.classList.add("btn", "btn-sm", "btn-primary");
      editBtn.textContent = "資料審核";

      tdAction.appendChild(editBtn);
      tr.appendChild(tdAction);
      dataBody.appendChild(tr);
    });
  }

  function renderPagination(data) {
    pagination.innerHTML = "";
    if (pageSize === "all") return;

    const totalPages = Math.ceil(data.length / pageSize);
    if (totalPages <= 1) return;

    let paginationHtml = `<li class="page-item ${currentPage === 1 ? "disabled" : ""}">
      <a class="page-link" href="#" data-page="${currentPage - 1}">Previous</a>
    </li>`;

    for (let i = 1; i <= totalPages; i++) {
      paginationHtml += `<li class="page-item ${i === currentPage ? "active" : ""}">
        <a class="page-link" href="#" data-page="${i}">${i}</a>
      </li>`;
    }

    paginationHtml += `<li class="page-item ${currentPage === totalPages ? "disabled" : ""}">
      <a class="page-link" href="#" data-page="${currentPage + 1}">Next</a>
    </li>`;

    pagination.innerHTML = paginationHtml;

    document.querySelectorAll("#pagination .page-link").forEach(link => {
      link.addEventListener("click", function (event) {
        event.preventDefault();
        const newPage = parseInt(this.dataset.page);
        if (newPage >= 1 && newPage <= totalPages) {
          currentPage = newPage;
          renderTable(getPageData(currentPage, pageSize, data));
          renderPagination(data);
        }
      });
    });
  }

  pageSizeSelect.addEventListener("change", function () {
    pageSize = this.value === "all" ? "all" : parseInt(this.value);
    currentPage = 1;
    renderTable(getPageData(currentPage, pageSize, allData));
    renderPagination(allData);
  });

  fetchData();

  document.querySelector(".btn-logout").addEventListener("click", function () {
    if (confirm("確定要登出嗎？")) {
      backstageAuth.logout();
    }
  });
});
