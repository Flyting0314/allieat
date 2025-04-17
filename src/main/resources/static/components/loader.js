

// 📁 /components/loader.js

function fetchComponent(componentId, url, callback) {
  fetch(url, {
    headers: { "Accept": "text/html; charset=UTF-8" }
  })
    .then(response => response.text())
    .then(html => {
      const container = document.getElementById(componentId);
      if (!container) return;
      container.innerHTML = html;

      // 處理 <script>
      container.querySelectorAll("script").forEach(oldScript => {
        const newScript = document.createElement("script");
        if (oldScript.src) {
          newScript.src = oldScript.src;
          newScript.async = false;
        } else {
          newScript.text = oldScript.text || oldScript.textContent;
        }
        document.body.appendChild(newScript);
      });

      // 處理 <style>
      container.querySelectorAll("style").forEach(oldStyle => {
        const newStyle = document.createElement("style");
        newStyle.textContent = oldStyle.textContent;
        document.head.appendChild(newStyle);
      });

      // 處理 <link>
      container.querySelectorAll("link[rel='stylesheet']").forEach(oldLink => {
        const newLink = document.createElement("link");
        newLink.rel = "stylesheet";
        newLink.href = oldLink.href;
        document.head.appendChild(newLink);
      });

      if (typeof callback === 'function') {
        callback(); // ✅ 呼叫回調函數
      }
    });
}

// 共通組件載入
fetchComponent("header-container", "/components/header.html");
fetchComponent("footer-container", "/components/footer.html");

// ✅ 這裡針對 bodySection 加登入狀態判斷
fetchComponent("bodySection-container", "/components/bodySection.html", () => {
  fetch("/registerAndLogin/api/login-status")
    .then(res => res.json())
    .then(data => {
      window.isMember = data.isMember;
      window.isStore = data.isStore;

      if (data.isMember || data.isStore) {
        document.querySelectorAll('.login-section').forEach(el => el.style.display = 'none');
        document.querySelectorAll('.logout-section').forEach(el => el.style.display = 'inline-block');
      } else {
        document.querySelectorAll('.login-section').forEach(el => el.style.display = 'inline-block');
        document.querySelectorAll('.logout-section').forEach(el => el.style.display = 'none');
      }
    });
});


//=====放法=====

//<head>
//<meta charset="utf-8">
//<script th:inline="javascript">
//  window.isMember = /*[[${session.member != null}]]*/ false;
//  window.isStore = /*[[${session.store != null}]]*/ false;
//</script>
//<style>

//======放法========
//</style>
//<script>
//  window.renderMode = "fetch";
//  window.isMember = false;
//  window.isStore = false;
//</script>
//</head>
//<body>
//
//<div id="header-container"></div>
//<div id="bodySection-container"></div>
//<!-- Navbar Start -->
//<!-- Hero Section End -->

//======放法========

//<!-- Footer Start -->
//<!-- Footer End -->
//<div id="footer-container"></div>
//<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
//<script src="/components/loader.js" defer></script>
//<!-- JavaScript Libraries -->

