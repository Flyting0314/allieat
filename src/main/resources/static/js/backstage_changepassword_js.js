const baseURL = window.location.origin;
const changePasswordAPI = `${baseURL}/backStage/admin/changePassword`;

document.addEventListener("DOMContentLoaded", () => {
    // 檢查是否登入
    backstageAuth.requireLogin();

    // 顯示帳號（從 localStorage）
    const account = localStorage.getItem("username") || "尚未登入";
    document.getElementById("account").value = account;

    const form = document.getElementById("changePasswordForm");
    const passwordError = document.getElementById("passwordError");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        const oldPassword = document.getElementById("oldPassword").value;
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        // 驗證新密碼與確認密碼一致
        if (newPassword !== confirmPassword) {
            passwordError.style.display = "block";
            return;
        } else {
            passwordError.style.display = "none";
        }

        const payload = {
            account,
            oldPassword,
            newPassword
        };

        try {
            const res = await backstageAuth.authFetch(changePasswordAPI, {
                method: "PUT",
                body: JSON.stringify(payload),
                headers: {
                    "Content-Type": "application/json"
                }
            });

            const result = await res.json();

            // 判斷後端回傳的狀態
            if (result.status === "success") {
                alert("密碼修改成功！");
                window.location.href = "./backstage_homepage.html";
            } else {
                alert(result.status || "密碼修改失敗！");
            }
        } catch (err) {
            console.error("修改錯誤", err);
            alert("發生錯誤，請稍後再試");
        }
    });
});
