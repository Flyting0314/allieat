const baseURL = window.location.origin;
const donationDetailAPI = `${baseURL}/backStage/donaRecord`;
const urlParams = new URLSearchParams(window.location.search);
const recordId = urlParams.get("id");

function formatDateTime(input) {
    const date = new Date(input);
    if (isNaN(date.getTime())) return "";
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    const hh = String(date.getHours()).padStart(2, '0');
    const mi = String(date.getMinutes()).padStart(2, '0');
    const ss = String(date.getSeconds()).padStart(2, '0');
    return `${yyyy}/${mm}/${dd} ${hh}:${mi}:${ss}`;
}

document.addEventListener("DOMContentLoaded", async () => {
    backstageAuth.requireLogin();

    if (!recordId) {
        alert("缺少捐款紀錄 ID");
        window.history.back();
        return;
    }

    try {
        const res = await backstageAuth.authFetch(`${donationDetailAPI}?id=${recordId}`);
        if (!res.ok) throw new Error("載入資料失敗");

        const data = await res.json();
        if (data.errorMsg) {
            alert(data.errorMsg);
            return;
        }

        document.getElementById("donorName").value = data.donorName;
        document.getElementById("identityNumber").value = data.identityNumber || "";
        document.getElementById("identityType").value = data.identityType === false ? "法人" : "自然人";
        document.getElementById("donationAmount").value = data.donationAmount;
        document.getElementById("donationTime").value = formatDateTime(data.donationTime);
        document.getElementById("contactEmail").value = data.contactEmail;
        document.getElementById("contactPhone").value = data.contactPhone;
        document.getElementById("address").value = data.address;
    } catch (err) {
        console.error(err);
        alert("無法載入捐款資料");
    }
});