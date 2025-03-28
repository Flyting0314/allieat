document.addEventListener("DOMContentLoaded", function () {
  const pageSizeSelect = document.getElementById("pageSizeSelect");
  const pagination = document.getElementById("pagination");
  const dataBody = document.getElementById("data-body");

  let allData = [];  // 存放從後端獲取的所有資料
  let currentPage = 1;
  let pageSize = parseInt(pageSizeSelect.value);

  async function fetchData() {
    try {
      const response = await fetch("http://localhost:8080/backStage/test"); // 請替換為實際 API
      if (!response.ok) {
        throw new Error("HTTP 錯誤，狀態碼：" + response.status);
      }
      allData = await response.json();  // 取得完整 JSON 陣列
      renderTable(getPageData(currentPage, pageSize));
      renderPagination();
    } catch (error) {
      console.error("載入資料時發生錯誤：", error);
    }
  }

  function getPageData(page, limit) {
    if (limit === "all") return allData;  // 如果選擇 "全部" 則顯示所有資料
    const startIndex = (page - 1) * limit;
    return allData.slice(startIndex, startIndex + limit);
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
      tdCreated.textContent = item.createdTime;
      tr.appendChild(tdCreated);

      let tdStatus = document.createElement("td");
      let badge = document.createElement("span");
      badge.classList.add("badge", "badge-status");
      if (item.status === "True") {
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
