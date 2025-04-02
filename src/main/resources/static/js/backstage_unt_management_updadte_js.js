// ========== API 設定 ==========
const baseURL = window.location.origin;
const unitUpdateAPI = `${baseURL}/backStage/orgManage/updateData`;
const unitDetailAPI = `${baseURL}/backStage/orgManage/updateInit`;

// ========== 解析網址參數 ==========
const urlParams = new URLSearchParams(window.location.search);
const orgId = urlParams.get("id");

// ========== 頁面載入：查詢資料 ==========
document.addEventListener("DOMContentLoaded", async () => {
	backstageAuth.requireLogin();

	if (!orgId) {
		alert("缺少 OrganizationId");
		window.history.back();
		return;
	}

	try {
		const res = await backstageAuth.authFetch(`${unitDetailAPI}?id=${orgId}`);
		if (!res.ok) throw new Error("查詢資料失敗");

		const data = await res.json();

		// 載入欄位資料
		document.getElementById("orgId").value = data.organizationId ?? data.OrganizationId;
		document.getElementById("orgName").value = data.name;
		document.getElementById("orgType").value = data.type;
		document.getElementById("orgStatus").value = data.status;

		// 顯示註冊時間（只顯示、不傳送）
		const rawTime = data.createdTime ?? data.CreatedTime;
		document.getElementById("createdTime").value = formatDateTime(rawTime);

	} catch (err) {
		console.error(err);
		alert("載入資料失敗！");
	}
});

// ========== 格式化日期時間（YYYY/MM/DD HH:mm:ss） ==========
function formatDateTime(input) {
	const date = new Date(input);
	if (isNaN(date.getTime())) return "無效日期";

	const yyyy = date.getFullYear();
	const mm = String(date.getMonth() + 1).padStart(2, '0');
	const dd = String(date.getDate()).padStart(2, '0');
	const hh = String(date.getHours()).padStart(2, '0');
	const mi = String(date.getMinutes()).padStart(2, '0');
	const ss = String(date.getSeconds()).padStart(2, '0');
	return `${yyyy}/${mm}/${dd} ${hh}:${mi}:${ss}`;
}

// ========== 提交更新 ==========
function submitUpdate() {
	const payload = {
		organizationId: parseInt(document.getElementById("orgId").value),
		name: document.getElementById("orgName").value,
		type: document.getElementById("orgType").value,
		status: parseInt(document.getElementById("orgStatus").value)
		// 不送 createdTime，避免格式不符造成錯誤
	};

	backstageAuth.authFetch(unitUpdateAPI, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify(payload)
	}).then(res => {
		if (res.ok) {
			alert("修改成功！");
			// 成功後重新整理頁面（轉跳到自己）
			//            window.location.href = window.location.pathname + "?id=" + document.getElementById("orgId").value;
			window.location.href = "./backstage_unit_management.html"
		} else {
			alert("修改失敗！");
		}
	}).catch(err => {
		console.error(err);
		alert("更新過程發生錯誤");
	});
}

// ========== 返回列表頁 ==========
function goBack() {
	window.location.href = "./backstage_unit_management.html";
}
