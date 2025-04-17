

// 📁 /components/loader.js
function fetchComponent(componentId, url) {
	fetch(url, {
	    headers: { "Accept": "text/html; charset=UTF-8" } 
	  })
    .then(response => response.text())
    .then(html => {
      const container = document.getElementById(componentId);
      if (!container) return;
      container.innerHTML = html;

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

      container.querySelectorAll("style").forEach(oldStyle => {
        const newStyle = document.createElement("style");
        newStyle.textContent = oldStyle.textContent;
        document.head.appendChild(newStyle);
      });

      container.querySelectorAll("link[rel='stylesheet']").forEach(oldLink => {
        const newLink = document.createElement("link");
        newLink.rel = "stylesheet";
        newLink.href = oldLink.href;
        document.head.appendChild(newLink);
      });
    });
}

// 全站共通組件載入
fetchComponent("header-container", "/components/header.html");
fetchComponent("bodySection-container", "/components/bodySection.html");
fetchComponent("footer-container", "/components/footer.html");



//=====放法=====

//<head>
//<meta charset="utf-8">
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


//======放法====自己的JS的最開頭====
//<script>
//fetchComponent("header-container", "/components/header.html");
//fetchComponent("bodySection-container", "/components/bodySection.html");
//fetchComponent("footer-container", "/components/footer.html");