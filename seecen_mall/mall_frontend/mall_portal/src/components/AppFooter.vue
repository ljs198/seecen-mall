<template>
  <footer class="app-footer">
    <!-- 远山月亮剪影装饰 -->
    <div class="footer-mountain" aria-hidden="true">
      <svg viewBox="0 0 1200 100" preserveAspectRatio="none" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <radialGradient id="footerMoonGrad" cx="50%" cy="50%">
            <stop offset="0%" stop-color="#fff8c4"/>
            <stop offset="100%" stop-color="#ffd700"/>
          </radialGradient>
        </defs>
        <circle cx="950" cy="35" r="22" fill="url(#footerMoonGrad)" opacity="0.9"/>
        <circle cx="950" cy="35" r="30" fill="none" stroke="#ffd700" stroke-width="0.5" opacity="0.4"/>
        <path d="M0,100 L0,60 L100,40 L200,55 L320,30 L450,50 L580,35 L720,55 L860,40 L1000,60 L1100,45 L1200,55 L1200,100 Z" fill="#1a1b3a" opacity="0.7"/>
        <path d="M0,100 L0,75 L150,60 L280,70 L400,55 L520,68 L650,58 L780,72 L900,62 L1050,75 L1200,68 L1200,100 Z" fill="#0f1444" opacity="0.9"/>
      </svg>
    </div>

    <div class="footer-content">
      <p class="footer-title">花好月圆夜 <span class="moon"></span> 好礼伴佳节</p>
      <p class="footer-sub">智能商城教学项目 · 中秋礼遇版</p>
      <p class="footer-tech">用户端浏览 · Vue 3 + Element Plus</p>
    </div>
  </footer>
</template>

<script setup>
// 中秋版底部，加远山月亮剪影 + 祝福语
</script>

<style scoped>
.app-footer {
  margin-top: 32px;
  padding: 0;
  background: linear-gradient(180deg, #fdf6e3 0%, #f5ebd5 100%);
  border-top: 1px solid #d4b996;
  position: relative;
  overflow: hidden;
}

.footer-mountain {
  width: 100%;
  height: 80px;
  line-height: 0;
}

.footer-mountain svg {
  width: 100%;
  height: 100%;
  display: block;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 20px 24px;
  text-align: center;
  color: #5d4037;
  font-size: 14px;
}

.footer-title {
  font-family: 'STKaiti', 'KaiTi', serif;
  font-size: 18px;
  color: #c8901b;
  margin: 8px 0;
  letter-spacing: 4px;
}

.footer-sub {
  color: #8b6f47;
  font-size: 13px;
  margin: 4px 0;
}

.footer-tech {
  color: #a89678;
  font-size: 12px;
  margin: 4px 0;
}

.moon {
  display: inline-block;
  width: 12px;
  height: 12px;
  background: radial-gradient(circle, #fff 30%, #ffd700 70%);
  border-radius: 50%;
  box-shadow: 0 0 8px #ffd700;
  vertical-align: middle;
  margin: 0 4px;
}
</style>
