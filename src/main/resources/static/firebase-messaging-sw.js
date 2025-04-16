/**
 * Firebase推播用
 */

// 引入 Firebase 所需套件（使用 compat 版本以支援瀏覽器）
importScripts('https://www.gstatic.com/firebasejs/10.8.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.8.0/firebase-messaging-compat.js');

// 初始化 Firebase（請自行替換下方設定為你的 Firebase 專案 config）
firebase.initializeApp({
  apiKey: "AIzaSyA68z34B-2gMTza5XihaqtGUeNlKi3-pVU",
  authDomain: "allieat-notification.firebaseapp.com",
  projectId: "allieat-notification",
  messagingSenderId: "613984391786",
  appId: "1:613984391786:web:f5fc9510d0ef5f972d3013"
});

// 初始化 Firebase Messaging 實例
const messaging = firebase.messaging();

// 當接收到背景訊息（例如瀏覽器沒開或在別的頁面）
messaging.onBackgroundMessage(function(payload) {
  console.log('[firebase-messaging-sw.js] 背景接收到推播：', payload);

  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
    icon: '/img/logo3.png'  // 你可以放品牌 LOGO 或自訂圖片
  };

  // 顯示通知
  self.registration.showNotification(notificationTitle, notificationOptions);
});
