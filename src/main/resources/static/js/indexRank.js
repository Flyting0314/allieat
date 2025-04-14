/**
 * 
 */

let currentAmount = 0;

function animateDonationAmount(target) {
    const element = document.getElementById('donationAmount');
    const duration = 1000; // 總動畫時間（毫秒）
    const frameRate = 60;
    const totalFrames = Math.round(duration / (1000 / frameRate));
    const increment = (target - currentAmount) / totalFrames;
    let frame = 0;

    function updateNumber() {
        frame++;
        currentAmount += increment;

        // 加上 scale 動畫類
        element.classList.add('animate');
        element.textContent = 'NT$ ' + Math.floor(currentAmount).toLocaleString();

        if (frame < totalFrames) {
            requestAnimationFrame(updateNumber);
        } else {
            currentAmount = target;
            element.textContent = 'NT$ ' + target.toLocaleString();
            element.classList.remove('animate');
        }
    }

    requestAnimationFrame(updateNumber);
}

function fetchDonationTotal() {
    fetch('/api/donation/total-amount')
        .then(response => response.json())
        .then(data => {
            const target = data.totalAmount || 0;
            if (target > currentAmount) {
                animateDonationAmount(target);
            }
        })
        .catch(error => {
            console.error('取得捐款金額失敗:', error);
        });
}

// 頁面載入與定時更新
document.addEventListener('DOMContentLoaded', () => {
    fetchDonationTotal(); // 初始加載
    setInterval(fetchDonationTotal, 60000); // 每分鐘更新一次
});