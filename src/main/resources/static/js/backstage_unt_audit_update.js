const baseURL = window.location.origin;
  const unitUpdateAPI = `${baseURL}/backStage/orgManage/updateData`;
  const unitDetailAPI = `${baseURL}/backStage/orgManage/updateInit`;
  const imageAPI = `${baseURL}/backStage/orgAudit/image`;

  function getQueryParam(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
  }

  function formatDateTime(dateStr) {
    const date = new Date(dateStr);
    const pad = n => n.toString().padStart(2, '0');
    return `${date.getFullYear()}/${pad(date.getMonth() + 1)}/${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  }

  async function loadOrgData() {
    const orgId = getQueryParam("id");
    document.getElementById("orgId").value = orgId;

    try {
      const response = await authFetch(`${unitDetailAPI}?id=${orgId}`);
      if (!response.ok) throw new Error("無法取得資料");
      const data = await response.json();

      document.getElementById("orgName").value = data.name;
      document.getElementById("orgType").value = data.type;
      document.getElementById("orgStatus").value = data.status;
      document.getElementById("createdTime").value = formatDateTime(data.createdTime);

      await loadOrgFile(orgId);
    } catch (error) {
      console.error("載入失敗：", error);
    }
  }

  async function loadOrgFile(orgId) {
    const fileUrl = `${imageAPI}?orgId=${orgId}`;
    const previewContainer = document.getElementById("orgPreview");

    try {
      const fileResp = await authFetch(fileUrl);
      if (!fileResp.ok) {
        previewContainer.innerHTML = `<p class="text-danger">檔案載入失敗</p>`;
        return;
      }

      const contentType = fileResp.headers.get("Content-Type");
      const blob = await fileResp.blob();

      if (contentType && contentType.includes("pdf")) {
        const blobUrl = URL.createObjectURL(blob);
        previewContainer.innerHTML = `
          <p>此為 PDF 檔案，請點擊以下連結下載：</p>
          <a href="${blobUrl}" download class="btn btn-outline-primary">下載 PDF</a>
        `;
      } else {
        const reader = new FileReader();
        reader.onload = function () {
          previewContainer.innerHTML = `
            <img src="${reader.result}" alt="申請圖片" class="img-thumbnail" style="max-width: 300px;" />
          `;
        };
        reader.readAsDataURL(blob);
      }
    } catch (error) {
      console.error("檔案讀取失敗：", error);
      previewContainer.innerHTML = `<p class="text-danger">載入檔案時發生錯誤</p>`;
    }
  }

  async function submitUpdate() {
    const payload = {
      organizationId: document.getElementById("orgId").value,
      name: document.getElementById("orgName").value,
      type: document.getElementById("orgType").value,
      status: document.getElementById("orgStatus").value
    };

    try {
      const response = await authFetch(unitUpdateAPI, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(payload)
      });

      if (!response.ok) throw new Error("更新失敗");

      alert("資料更新成功！");
      window.location.href = "backstage_unit_audit.html";
    } catch (error) {
      console.error("送出失敗：", error);
      alert("更新失敗，請稍後再試。");
    }
  }

  function goBack() {
    window.history.back();
  }

  document.addEventListener("DOMContentLoaded", loadOrgData);