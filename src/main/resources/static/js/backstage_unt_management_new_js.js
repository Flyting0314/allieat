const baseURL = window.location.origin;
const unitAddAPI = `${baseURL}/backStage/orgManage/newOrgData`;

document.addEventListener("DOMContentLoaded", () => {
	backstageAuth.requireLogin(); // 確保登入驗證
});

function submitNew() {
	const payload = {
		name: document.getElementById("orgName").value.trim(),
		type: document.getElementById("orgType").value.trim(),
		status: parseInt(document.getElementById("orgStatus").value)
	};

	if (!payload.name || !payload.type) {
		alert("請填寫完整資料！");
		return;
	}

	backstageAuth.authFetch(unitAddAPI, {
		method: "PUT",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify(payload)
	}).then(res => {
		if (res.ok) {
			alert("新增成功！");
			window.location.href = "./backstage_unit_management.html";
		} else {
			res.json().then(data => {
				alert(data.message || "新增失敗！");
			});
		}
	}).catch(err => {
		console.error(err);
		alert("新增過程發生錯誤");
	});
}

function goBack() {
	window.location.href = "./backstage_unit_management.html";
}