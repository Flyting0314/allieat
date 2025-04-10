const baseURL = window.location.origin;
  const adminCreateAPI = `${baseURL}/backStage/admin`;

  document.addEventListener("DOMContentLoaded", () => {
      backstageAuth.requireLogin();

      const form = document.getElementById("adminForm");
      const passwordInput = document.getElementById("password");
      const confirmPasswordInput = document.getElementById("confirmPassword");
      const passwordError = document.getElementById("passwordError");

      form.addEventListener("submit", async (e) => {
          e.preventDefault();

          const account = document.getElementById("username").value.trim();
          const password = passwordInput.value;
          const confirmPassword = confirmPasswordInput.value;

          if (password !== confirmPassword) {
              passwordError.style.display = "block";
              confirmPasswordInput.focus();
              return;
          } else {
              passwordError.style.display = "none";
          }

          const payload = { account, password }; // 只傳後端需要的欄位

          try {
              const res = await backstageAuth.authFetch(adminCreateAPI, {
                  method: "POST",
                  body: payload
              });

              const result = await res.json();

              if (res.ok && result.status === "success") {
                  alert("新增成功！");
                  window.location.href = "./backstage_homepage.html";
                  return
              } else if (result.status === "帳號已存在") {
                  alert("此帳號已存在，請重新輸入！");
              } else if (result.status === "fail") {
                  alert("新增失敗，請稍後再試！");
              } else if (result.message) {
                  alert("錯誤：" + result.message);
              } else {
                  alert("新增失敗，請確認資料！");
              }

          } catch (err) {
              console.error("Error:", err);
              alert("新增過程發生錯誤，請稍後再試！");
          }
      });
  });