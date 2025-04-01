document.addEventListener("DOMContentLoaded", function () {
  const pageSizeSelect = document.getElementById("pageSizeSelect");
  const pagination = document.getElementById("pagination");
  const dataBody = document.getElementById("data-body");
  const baseURL = window.location.origin;
  const apiUri = `${baseURL}/backStage/orgManage`;
  let allData = [];  // 存放從後端獲取的所有資料
  let currentPage = 1;
  let pageSize = parseInt(pageSizeSelect.value);
  let currentSortField = null;
  let currentSortOrder = "asc"; // or "desc"
  const toggle = document.querySelector(".collapsible-menu");
  const target = document.querySelector("#recipientSubMenu");
 //側邊欄位切換
  toggle.addEventListener("click", function () {
    const isExpanded = toggle.getAttribute("aria-expanded") === "true";
    toggle.setAttribute("aria-expanded", !isExpanded);
    target.classList.toggle("collapse");
    target.classList.toggle("show");
  });


  ["name", "type", "createdTime", "status"].forEach(f => {
    const el = document.getElementById(`sort-${f}`);
    if (el) el.innerHTML = "▲▼";
  });


  document.querySelectorAll("th[data-sort-field]").forEach(th => {
    console.log("綁定點擊事件到：", th);
    th.addEventListener("click", function () {
      const field = this.getAttribute("data-sort-field");
      console.log("你點了欄位：", field); // ← 點了就會印出來
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

    allData.sort((a, b) => {
      let aValue = a[field];
      let bValue = b[field];

      // 對日期欄特別處理
      if (field === "createdTime") {
        aValue = new Date(aValue);
        bValue = new Date(bValue);
      }

      if (aValue < bValue) return currentSortOrder === "asc" ? -1 : 1;
      if (aValue > bValue) return currentSortOrder === "asc" ? 1 : -1;
      return 0;
    });

    renderTable(getPageData(currentPage, pageSize));
    renderPagination();
    // 清除所有欄位箭頭
    ["name", "type", "createdTime", "status"].forEach(f => {
      const el = document.getElementById(`sort-${f}`);
      if (el) {
        el.innerHTML = "▲▼"; // 重設所有欄位的箭頭
      }
    });

    const currentEl = document.getElementById(`sort-${field}`);
    if (currentEl) {
      currentEl.innerHTML = currentSortOrder === "asc"
          ? "<strong>▲</strong>▼"
          : "▲<strong>▼</strong>";
    }

  }

  async function fetchData() {
    try {
      const response = await authFetch( apiUri);//call api 位置
      if (!response.ok) {
        throw new Error("HTTP 錯誤，狀態碼：" + response.status);
      }

      const result = await response.json(); // 接收整體 JSON 物件
      allData = result.orgInitDataList || [];  // 取出陣列部分
      // renderTable(getPageData(currentPage, pageSize));
      // renderPagination();
      sortData("createdTime");
    } catch (error) {
      console.error("載入資料時發生錯誤：", error);
    }
  }


  function getPageData(page, limit) {
    if (limit === "all") return allData;  // 如果選擇 "全部" 則顯示所有資料
    const startIndex = (page - 1) * limit;
    return allData.slice(startIndex, startIndex + limit);
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
	  const formattedDate = `${date.getFullYear()}
	  						/${pad(date.getMonth() + 1)}
							/${pad(date.getDate())}  
							${pad(date.getHours())}
							: ${pad(date.getMinutes())}
							: ${pad(date.getSeconds())}`;
	  tdCreated.textContent = formattedDate;
      tr.appendChild(tdCreated);

      let tdStatus = document.createElement("td");
      let badge = document.createElement("span");
      badge.classList.add("badge", "badge-status");
      if (item.status === "1"||item.status ===1) {
        badge.classList.add("badge-success");
        badge.textContent = "啟用";
      } else {
        badge.classList.add("badge-secondary");
        badge.textContent = "停用";
      }
      tdStatus.appendChild(badge);
      tr.appendChild(tdStatus);

      dataBody.appendChild(tr);
    });
  }




  function renderPagination() {
    pagination.innerHTML = "";
    if (pageSize === "all") return; // 顯示全部時不需要分頁

    const totalPages = Math.ceil(allData.length / pageSize);
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
          renderTable(getPageData(currentPage, pageSize));
          renderPagination();
        }
      });
    });
  }

  pageSizeSelect.addEventListener("change", function () {
    pageSize = this.value === "all" ? "all" : parseInt(this.value);
    currentPage = 1; // 變更筆數後重置到第一頁
    renderTable(getPageData(currentPage, pageSize));
    renderPagination();

  });

  fetchData();
});

