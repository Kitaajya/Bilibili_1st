// ==================== 状态 ====================
let currentUser = JSON.parse(localStorage.getItem('bilibili_user') || 'null');
let currentVideoData = null;
let videoSearchKeyword = '';
let currentProfileUserId = null;
let detailReturn = 'home';
const actState = { like: false, coin: false, fav: false };

// ==================== 工具函数 ====================
function showToast(msg) {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.classList.remove('hidden');
    setTimeout(() => t.classList.add('hidden'), 2500);
}

function setUser(user) {
    currentUser = user;
    localStorage.setItem('bilibili_user', JSON.stringify(user));
}

// 需要登录的操作统一入口：未登录则提示并返回 false
function requireLogin() {
    if (!currentUser) {
        showToast('请先登录后再操作');
        return false;
    }
    return true;
}

// 打开登录页（游客点击“登录”时调用）
function showLoginPage() {
    document.getElementById('loginPage').classList.remove('hidden');
}

function qs(params) {
    return '?' + new URLSearchParams(params).toString();
}

// 统一解析后端 Result<T>：成功返回内层 data，失败抛错
async function unwrap(res) {
    if (!res.ok) throw new Error('请求失败：HTTP ' + res.status);
    const body = await res.json();
    if (body && body.success === false) {
        throw new Error(body.message || '操作失败');
    }
    return body && ('data' in body) ? body.data : body;
}

async function postJson(url, body) {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body),
    });
    return unwrap(res);
}

async function getJson(url) {
    const res = await fetch(url);
    return unwrap(res);
}

async function sendForm(url, params) {
    const res = await fetch(url + qs(params), { method: 'POST' });
    return unwrap(res);
}

async function sendDelete(url, params) {
    const res = await fetch(url + qs(params), { method: 'DELETE' });
    return unwrap(res);
}

function fmtMoney(n) {
    return n != null ? Number(n).toFixed(2) : '-';
}

function escapeHtml(s) {
    return String(s).replace(/[&<>"']/g, c =>
        ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

function formatNum(n) {
    n = Number(n) || 0;
    return n >= 10000 ? (n / 10000).toFixed(1).replace(/\.0$/, '') + '万' : n;
}

function skeletonHTML(n) {
    return Array.from({ length: n }, () => `
        <div class="skeleton">
            <div class="cover"></div>
            <div class="line"></div>
            <div class="line short"></div>
        </div>`).join('');
}

function emptyHTML(text) {
    return `<div class="empty"><div class="icon">📺</div><div>${escapeHtml(text)}</div></div>`;
}

// ==================== 登录 / 注册 ====================
function switchTab(tab) {
    document.getElementById('tabLogin').classList.toggle('active', tab === 'login');
    document.getElementById('tabRegister').classList.toggle('active', tab === 'register');
    document.getElementById('loginForm').classList.toggle('hidden', tab !== 'login');
    document.getElementById('registerForm').classList.toggle('hidden', tab !== 'register');
}

async function doRegister(e) {
    e.preventDefault();
    const f = new FormData(e.target);
    const body = {
        qqEmail: f.get('qqEmail'),
        phoneNumber: f.get('phoneNumber') || null,
        password: f.get('password'),
        realName: f.get('realName'),
        virtualName: f.get('virtualName'),
    };
    try {
        const data = await postJson('/api/log/in/register', body);
        setUser({ userId: data.userId, virtualName: data.virtualName, role: data.role || 'USER' });
        showToast('注册成功，已自动登录');
        enterMain();
    } catch (err) {
        showToast(err.message);
    }
}

async function doLogin(e) {
    e.preventDefault();
    const f = new FormData(e.target);
    try {
        const data = await postJson('/api/log/in/login', {
            qqEmail: f.get('qqEmail'),
            password: f.get('password'),
        });
        setUser({ userId: data.userId, virtualName: data.virtualName, role: data.role || 'USER' });
        showToast('登录成功');
        enterMain();
    } catch (err) {
        showToast(err.message);
    }
}

function renderUserArea() {
    const area = document.getElementById('userArea');
    if (!currentUser) {
        area.innerHTML = '<button class="btn-primary" onclick="showLoginPage()">登录 / 注册</button>';
        return;
    }
    const name = currentUser.virtualName || '用户';
    const badge = currentUser.role === 'MERCHANT'
        ? '<span class="merchant-badge">商家</span>' : '';
    area.innerHTML = `
        <div class="user-avatar" title="${escapeHtml(name)}">${escapeHtml(name.charAt(0))}</div>
        <span class="user-name">${escapeHtml(name)} ${badge}</span>
        <button class="logout-btn" onclick="logout()">退出登录</button>`;
}

function logout() {
    setUser(null);
    document.getElementById('videoDetail').classList.add('hidden');
    document.getElementById('userProfile').classList.add('hidden');
    // 退出后回到游客模式：仍留在主界面，仅刷新用户区
    document.getElementById('appMain').classList.remove('hidden');
    document.getElementById('header').classList.remove('hidden');
    renderUserArea();
    showToast('已退出登录');
    goHome();
}

function enterMain() {
    document.getElementById('loginPage').classList.add('hidden');
    document.getElementById('header').classList.remove('hidden');
    document.getElementById('appMain').classList.remove('hidden');
    document.getElementById('videoDetail').classList.add('hidden');
    renderUserArea();
    goHome();
    loadProducts();
    loadAllDynamics();
}

// ==================== 页面切换 ====================
function showPage(name) {
    document.querySelectorAll('.nav-li').forEach(li =>
        li.classList.toggle('active', li.dataset.page === name));
    document.querySelectorAll('.page').forEach(p => p.classList.add('hidden'));
    document.getElementById('page-' + name).classList.remove('hidden');
    if (name === 'order') loadOrders();
    if (name === 'admin') loadBuyers();
}

function goHome() {
    document.getElementById('searchInput').value = '';
    document.getElementById('videoListTitle').textContent = '全部视频';
    hideUserResults();
    showPage('video');
    loadAllVideos();
}

function hideUserResults() {
    const box = document.getElementById('userResultBox');
    box.classList.add('hidden');
    box.innerHTML = '';
}

//搜索（顶部搜索框，同时搜用户和视频）
async function doSearch() {
    const kw = document.getElementById('searchInput').value.trim();
    if (!kw) return goHome();
    videoSearchKeyword = kw;
    document.getElementById('videoListTitle').textContent = '搜索「' + kw + '」的结果';
    showPage('video');
    const grid = document.getElementById('videoGrid');
    grid.innerHTML = skeletonHTML(8);
    try {
        const [users, list] = await Promise.all([
            getJson('/api/video/find/user' + qs({ virtualName: kw })),
            getJson('/api/video/search' + qs({ keyword: kw })),
        ]);
        renderUserResults(users);
        renderVideoCards(list);
    } catch (err) {
        grid.innerHTML = '';
        document.getElementById('userResultBox').classList.add('hidden');
        showToast(err.message);
    }
}

function renderUserResults(users) {
    const box = document.getElementById('userResultBox');
    if (!users || !users.length) {
        box.classList.add('hidden');
        box.innerHTML = '';
        return;
    }
    box.classList.remove('hidden');
    box.innerHTML = '<h3 class="result-sub">用户</h3>';
    const list = document.createElement('div');
    list.className = 'user-result-list';
    list.innerHTML = `<span class="load-more" style="padding:0">共 ${users.length} 个结果</span>`;
    users.forEach(u => {
        const name = u.virtualName || 'UP' + u.id;
        const item = document.createElement('div');
        item.className = 'user-result-item';
        item.onclick = () => openProfile(u.id);
        item.innerHTML = `
            <div class="user-result-avatar">${escapeHtml(name.charAt(0))}</div>
            <div class="user-result-info">
                <div class="user-result-name">${escapeHtml(name)}</div>
                <div class="user-result-sub">用户ID：${u.id}</div>
            </div>
            <span class="user-result-go">查看主页 →</span>`;
        list.appendChild(item);
    });
    box.appendChild(list);
}

// ==================== 视频列表 ====================
async function loadAllVideos() {
    hideUserResults();
    const grid = document.getElementById('videoGrid');
    grid.innerHTML = skeletonHTML(8);
    try {
        const list = await getJson('/api/video/all');
        renderVideoCards(list);
    } catch (err) { grid.innerHTML = ''; showToast(err.message); }
}

async function loadMyVideos() {
    if (!requireLogin()) return;
    hideUserResults();
    document.getElementById('videoListTitle').textContent = '我的视频';
    const grid = document.getElementById('videoGrid');
    grid.innerHTML = skeletonHTML(4);
    try {
        const list = await getJson('/api/video/byUser' + qs({ userId: currentUser.userId }));
        renderVideoCards(list);
    } catch (err) { grid.innerHTML = ''; showToast(err.message); }
}

function renderVideoCards(list, gridId) {
    const target = gridId || 'videoGrid';
    const grid = document.getElementById(target);
    if (!list.length) {
        grid.innerHTML = emptyHTML(target === 'profileGrid'
            ? 'TA 还没有发布视频~'
            : '暂无视频，点击右上角「发布视频」上传第一个~');
        return;
    }
    grid.innerHTML = '';
    list.forEach(v => grid.appendChild(makeVideoCard(v)));
}

function makeVideoCard(v) {
    const card = document.createElement('div');
    card.className = 'video-card';
    card.onclick = () => openDetail(v);

    const upName = v.uploader || 'UP' + v.user_id;
    const hue = (Math.abs(Number(v.id)) * 37) % 360;
    const g1 = 'hsl(' + hue + ',45%,38%)';
    const g2 = 'hsl(' + ((hue + 70) % 360) + ',55%,58%)';
    const date = (v.create_time || '').replace('T', ' ').slice(0, 16);

    const mine = (currentUser && v.user_id == currentUser.userId) ? `
        <span style="margin-left:auto;display:flex;gap:10px">
            <a style="color:var(--primary)"
               onclick="event.stopPropagation();editVideoTitle(${v.id})" href="javascript:void(0)">改名</a>
            <a style="color:#f04134"
               onclick="event.stopPropagation();deleteVideo(${v.id})" href="javascript:void(0)">删除</a>
        </span>` : '';

    card.innerHTML = `
        <div class="cover" style="background:linear-gradient(135deg, ${g1}, ${g2})">
            <span class="play-badge">▶</span>
            <span class="stats">▶ ${formatNum(v.view_count)} · 👍 ${formatNum(v.like_count)}</span>
            <span class="duration">📅 ${escapeHtml(date || '')}</span>
        </div>
        <div class="title" title="${escapeHtml(v.title || '')}">${escapeHtml(v.title || '')}</div>
        <div class="up">
            <span class="up-avatar" onclick="event.stopPropagation();openProfile(${v.user_id})">${escapeHtml(upName.charAt(0))}</span>
            <span class="up-name" onclick="event.stopPropagation();openProfile(${v.user_id})">${escapeHtml(upName)}</span>
            ${mine}
        </div>`;
    return card;
}

// ==================== 视频上传 / 编辑 / 删除 ====================
function showUploadVideo() {
    if (!requireLogin()) return;
    openModal(`
        <h3 style="margin:0 0 10px">发布视频</h3>
        <input id="videoTitle" placeholder="视频标题" style="margin:10px 0">
        <input type="file" id="videoFile" accept="video/*" style="margin:6px 0">
        <button class="btn-primary" onclick="submitUploadVideo()">上传</button>
    `);
}

async function submitUploadVideo() {
    if (!requireLogin()) return;
    const title = document.getElementById('videoTitle').value.trim();
    const file = document.getElementById('videoFile').files[0];
    if (!title) return showToast('标题不能为空');
    if (!file) return showToast('请选择视频文件');

    const btn = document.querySelector('#modalMask .btn-primary');
    btn.disabled = true;
    btn.textContent = '上传中…';
    const fd = new FormData();
    fd.append('userId', currentUser.userId);
    fd.append('title', title);
    fd.append('file', file);
    try {
        const res = await fetch('/api/video/upload', { method: 'POST', body: fd });
        await unwrap(res);
        showToast('上传成功');
        closeModal();
        goHome();
    } catch (err) {
        btn.disabled = false;
        btn.textContent = '上传';
        showToast(err.message);
    }
}

function editVideoTitle(id) {
    if (!requireLogin()) return;
    openModal(`
        <h3 style="margin:0 0 10px">修改视频标题</h3>
        <input id="videoNewTitle" placeholder="新标题" style="margin:10px 0">
        <button class="btn-primary" onclick="submitEditVideoTitle(${id})">保存</button>
    `);
}

async function submitEditVideoTitle(videoId) {
    if (!requireLogin()) return;
    const title = document.getElementById('videoNewTitle').value.trim();
    if (!title) return showToast('标题不能为空');
    try {
        await sendForm('/api/video/edit/title', {
            userId: currentUser.userId, videoId, title,
        });
        showToast('修改成功');
        closeModal();
        goHome();
    } catch (err) { showToast(err.message); }
}

async function deleteVideo(videoId) {
    if (!requireLogin()) return;
    if (!confirm('确定删除该视频？')) return;
    try {
        await sendDelete('/api/video/delete', {
            userId: currentUser.userId, videoId,
        });
        showToast('删除成功');
        goHome();
    } catch (err) { showToast(err.message); }
}

// ==================== 视频详情页 ====================
function openDetail(v, from) {
    detailReturn = from || 'home';
    currentVideoData = v;
    document.getElementById('appMain').classList.add('hidden');
    document.getElementById('userProfile').classList.add('hidden');
    const detail = document.getElementById('videoDetail');
    detail.classList.remove('hidden');

    document.getElementById('detailTitle').textContent = v.title || '';
    const upName = v.uploader || 'UP' + v.user_id;
    document.getElementById('detailUp').innerHTML =
        'UP主：<span class="up-name" onclick="openProfile(' + v.user_id + ')">' + escapeHtml(upName) + '</span>';
    document.getElementById('detailTime').textContent =
        '发布于 ' + ((v.create_time || '').replace('T', ' ').slice(0, 16) || '-');

    const videoEl = document.getElementById('detailVideo');
    videoEl.src = '/api/video/play?id=' + v.id;
    videoEl.playbackRate = 1;

    resetActs();
    loadLikeStatus();
    loadComments(v.id);
    window.scrollTo({ top: 0 });
}

function backFromDetail() {
    document.getElementById('videoDetail').classList.add('hidden');
    document.getElementById('detailVideo').removeAttribute('src');
    if (detailReturn === 'profile' && currentProfileUserId != null) {
        openProfile(currentProfileUserId);
    } else {
        document.getElementById('appMain').classList.remove('hidden');
        goHome();
    }
    window.scrollTo({ top: 0 });
}

//点赞 / 投币 / 收藏（点赞已落库，投币收藏为本地状态）
function resetActs() {
    actState.like = actState.coin = actState.fav = false;
    const likeCount = currentVideoData ? (currentVideoData.like_count || 0) : 0;
    document.getElementById('btnLike').textContent = '👍 点赞 ' + formatNum(likeCount);
    document.getElementById('btnCoin').textContent = '🪙 投币';
    document.getElementById('btnFav').textContent = '⭐ 收藏';
    ['btnLike', 'btnCoin', 'btnFav'].forEach(id =>
        document.getElementById(id).classList.remove('active'));
}

async function loadLikeStatus() {
    if (!currentUser || !currentVideoData) return;
    try {
        const data = await getJson('/api/video/like/status' + qs({
            videoId: currentVideoData.id, userId: currentUser.userId,
        }));
        actState.like = !!data.liked;
        const b = document.getElementById('btnLike');
        b.classList.toggle('active', actState.like);
        b.textContent = (actState.like ? '👍 已赞 ' : '👍 点赞 ') + formatNum(data.likeCount);
        if (currentVideoData) currentVideoData.like_count = data.likeCount;
    } catch (err) { /* 点赞状态加载失败不打扰用户 */ }
}

async function toggleAct(kind) {
    if (kind === 'like') {
        if (!currentUser) return showToast('请先登录');
        try {
            const res = await sendForm('/api/video/like/toggle', {
                videoId: currentVideoData.id, userId: currentUser.userId,
            });
            actState.like = !!res.liked;
            const b = document.getElementById('btnLike');
            b.classList.toggle('active', actState.like);
            b.textContent = (actState.like ? '👍 已赞 ' : '👍 点赞 ') + formatNum(res.likeCount);
            if (currentVideoData) currentVideoData.like_count = res.likeCount;
        } catch (err) { showToast(err.message); }
        return;
    }
    // 投币 / 收藏仍为本地状态
    actState[kind] = !actState[kind];
    const map = {
        coin: ['btnCoin', '🪙 投币', '🪙 已投币'],
        fav: ['btnFav', '⭐ 收藏', '⭐ 已收藏'],
    };
    const [id, off, on] = map[kind];
    const b = document.getElementById(id);
    b.classList.toggle('active', actState[kind]);
    b.textContent = actState[kind] ? on : off;
}

function shareVideo() {
    if (!currentVideoData) return;
    const url = location.origin + '/api/video/play?id=' + currentVideoData.id;
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(url)
            .then(() => showToast('播放链接已复制，快去分享吧'))
            .catch(() => showToast('复制失败，请手动复制'));
    } else {
        showToast(url);
    }
}

// ==================== 用户主页 ====================
function openProfile(userId) {
    currentProfileUserId = userId;
    document.getElementById('appMain').classList.add('hidden');
    document.getElementById('videoDetail').classList.add('hidden');
    document.getElementById('detailVideo').removeAttribute('src');
    document.getElementById('userProfile').classList.remove('hidden');

    document.getElementById('profileName').textContent = '加载中…';
    document.getElementById('profileAvatar').textContent = '?';
    const grid = document.getElementById('profileGrid');
    grid.innerHTML = skeletonHTML(6);

    Promise.all([
        getJson('/api/user/profile' + qs({ userId })),
        getJson('/api/video/byUser' + qs({ userId })),
    ]).then(([profile, videos]) => {
        if (!profile) {
            showToast('用户不存在');
            backFromProfile();
            return;
        }
        const name = profile.virtualName || 'UP' + profile.id;
        const badge = profile.role === 'MERCHANT'
            ? '<span class="merchant-badge">商家</span>' : '';
        document.getElementById('profileName').innerHTML =
            escapeHtml(name) + ' ' + badge;
        document.getElementById('profileAvatar').textContent = escapeHtml(name.charAt(0));
        document.getElementById('profileVideoCount').textContent = profile.videoCount || 0;
        document.getElementById('profileTotalView').textContent = formatNum(profile.totalView);
        document.getElementById('profileTotalLike').textContent = formatNum(profile.totalLike);
        renderVideoCards(videos, 'profileGrid');
    }).catch(err => {
        grid.innerHTML = '';
        showToast(err.message);
    });
    window.scrollTo({ top: 0 });
}

function backFromProfile() {
    currentProfileUserId = null;
    document.getElementById('userProfile').classList.add('hidden');
    document.getElementById('appMain').classList.remove('hidden');
    goHome();
    window.scrollTo({ top: 0 });
}

// ==================== 视频评论 ====================
async function loadComments(videoId) {
    const box = document.getElementById('commentList');
    box.innerHTML = '<div class="load-more">评论加载中...</div>';
    document.getElementById('commentCount').textContent = '0';
    try {
        const list = await getJson('/api/video/comment/list' + qs({ videoId }));
        renderComments(list);
    } catch (err) { box.innerHTML = ''; showToast(err.message); }
}

function renderComments(list) {
    const box = document.getElementById('commentList');
    document.getElementById('commentCount').textContent = list.length;
    if (!list.length) {
        box.innerHTML = '<div class="empty"><div class="icon">💬</div><div>还没有评论，快来抢沙发~</div></div>';
        return;
    }
    box.innerHTML = '';
    list.forEach(c => {
        const name = c.name || 'UP' + c.user_id;
        const item = document.createElement('div');
        item.className = 'comment';
        item.innerHTML = `
            <div class="avatar">${escapeHtml(name.charAt(0))}</div>
            <div class="body">
                <div class="name">${escapeHtml(name)}</div>
                <div class="text">${escapeHtml(c.contents || '')}</div>
                <div class="actions"><span>${(c.create_time || '').replace('T', ' ').slice(0, 16)}</span></div>
            </div>`;
        box.appendChild(item);
    });
}

async function sendComment() {
    if (!currentUser) return showToast('请先登录');
    if (!currentVideoData) return;
    const input = document.getElementById('commentInput');
    const text = input.value.trim();
    if (!text) return showToast('评论不能为空');
    try {
        await sendForm('/api/video/comment/write', {
            videoId: currentVideoData.id, userId: currentUser.userId, contents: text,
        });
        input.value = '';
        showToast('评论发布成功');
        loadComments(currentVideoData.id);
    } catch (err) { showToast(err.message); }
}

// ==================== 商城 ====================
async function loadProducts() {
    try {
        const list = await getJson('/api/purchase/controller/select/all/product');
        renderProducts(list);
    } catch (err) { showToast(err.message); }
}

async function loadProductById(e) {
    e.preventDefault();
    try {
        const list = await getJson('/api/purchase/controller/select/all/product/id' + qs({ id: e.target[0].value }));
        renderProducts(list);
    } catch (err) { showToast(err.message); }
}

async function loadProductsByPrice(e) {
    e.preventDefault();
    const min = e.target[0].value, max = e.target[1].value;
    try {
        const list = await getJson('/api/purchase/controller/select/m/price' + qs({ minPrice: min, maxPrice: max }));
        renderProducts(list);
    } catch (err) { showToast(err.message); }
}

function renderProducts(list) {
    const tbody = document.querySelector('#productTable tbody');
    tbody.innerHTML = '';
    if (!list.length) {
        tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;color:var(--text-sub)">暂无商品</td></tr>';
        return;
    }
    list.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${p.id}</td>
            <td>${p.product_name}</td>
            <td>${fmtMoney(p.price)}</td>
            <td>${p.quantity}</td>
            <td>${fmtMoney(p.total)}</td>
            <td><div class="td-btns"><button class="btn" onclick="viewComments(${p.id})">评论</button></div></td>`;
        tbody.appendChild(tr);
    });
}

// ==================== 商品评论 ====================
async function viewComments(id) {
    try {
        const list = await getJson('/api/purchase/controller/select/id/comments' + qs({ id }));
        openModal(`
            <h3>商品 ${id} 的评论</h3>
            <div id="commentBox">
                ${list.length ? list.map((c, i) => `<p style="padding:6px 0;border-bottom:1px solid #f1f2f3">${i + 1}. ${c.contents || '（空）'}</p>`).join('')
                             : '<p style="color:var(--text-sub)">暂无评论</p>'}
            </div>
            <div style="margin-top:12px;display:flex;gap:8px">
                <input id="cmtText" placeholder="写下你的评价" style="flex:1">
                <button class="btn-primary" onclick="writeComment(${id})">发表</button>
            </div>
        `);
    } catch (err) { showToast(err.message); }
}

async function writeComment(productId) {
    if (!requireLogin()) return;
    const text = document.getElementById('cmtText').value.trim();
    if (!text) return showToast('评论不能为空');
    try {
        await sendForm('/api/purchase/controller/write/evaluation', {
            contents: text, userId: currentUser.userId, id: productId,
        });
        showToast('评论成功');
        viewComments(productId);
    } catch (err) { showToast(err.message); }
}

// ==================== 订单 ====================
async function loadOrders() {
    if (!requireLogin()) return;
    try {
        const list = await getJson('/api/purchase/controller/select/order');
        const tbody = document.querySelector('#orderTable tbody');
        tbody.innerHTML = '';
        if (!list.length) {
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:var(--text-sub)">暂无订单</td></tr>';
            return;
        }
        list.forEach(o => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${o.id}</td>
                <td>${o.user_id}</td>
                <td>${o.product_name}</td>
                <td>${fmtMoney(o.price)}</td>
                <td>${o.quantity}</td>
                <td>${fmtMoney(o.total)}</td>
                <td>${(o.contents || '').slice(0, 20) || '无'}</td>
                <td><div class="td-btns">
                    <button class="btn" onclick="editMyComment(${o.id})">改评</button>
                    <button class="btn" onclick="deleteMyComment(${o.id})">删评</button>
                </div></td>`;
            tbody.appendChild(tr);
        });
    } catch (err) { showToast(err.message); }
}

function editMyComment(id) {
    if (!requireLogin()) return;
    openModal(`
        <h3>修改我的评论（订单 ${id}）</h3>
        <textarea id="editCmt" placeholder="新的评价内容" style="width:100%;height:90px;margin:10px 0"></textarea>
        <button class="btn-primary" onclick="submitEditComment(${id})">保存</button>
    `);
}

async function submitEditComment(id) {
    if (!requireLogin()) return;
    const text = document.getElementById('editCmt').value.trim();
    if (!text) return showToast('评论不能为空');
    try {
        await sendForm('/api/purchase/controller/edit/comments', {
            contents: text, userId: currentUser.userId, id,
        });
        showToast('修改成功');
        closeModal();
        loadOrders();
    } catch (err) { showToast(err.message); }
}

async function deleteMyComment(id) {
    if (!requireLogin()) return;
    try {
        await sendDelete('/api/purchase/controller/delete/my/comments', {
            userId: currentUser.userId, id,
        });
        showToast('已删除评论');
        loadOrders();
    } catch (err) { showToast(err.message); }
}

// ==================== 动态 ====================
async function loadAllDynamics() {
    try {
        const list = await getJson('/api/dynamic/all');
        renderDynamics(list);
    } catch (err) { showToast(err.message); }
}

async function loadMyDynamics() {
    if (!requireLogin()) return;
    try {
        const list = await getJson('/api/dynamic/byUser' + qs({ userId: currentUser.userId }));
        renderDynamics(list);
    } catch (err) { showToast(err.message); }
}

function renderDynamics(list) {
    const box = document.getElementById('dynamicList');
    box.innerHTML = '';
    if (!list.length) {
        box.innerHTML = '<p style="color:var(--text-sub);text-align:center;padding:60px 0">暂无动态</p>';
        return;
    }
    list.forEach(d => {
        const card = document.createElement('div');
        card.className = 'dynamic-card';
        card.innerHTML = `
            <div class="d-title">${d.title || '（无标题）'}</div>
            <div class="d-content">${escapeHtml(d.contents || '')}</div>
            <div class="d-meta">用户ID：${d.user_id} · ${(d.create_time || '').replace('T', ' ')}</div>
            <div class="d-actions">
                ${(currentUser && d.user_id == currentUser.userId) ? `
                    <button class="btn" onclick="editDynamic(${d.id})">编辑</button>
                    <button class="btn-danger" onclick="deleteDynamic(${d.id})">删除</button>` : ''}
            </div>`;
        box.appendChild(card);
    });
}

function showWriteDynamic() {
    if (!requireLogin()) return;
    openModal(`
        <h3>发布动态</h3>
        <input id="dynTitle" placeholder="标题" style="margin:10px 0">
        <textarea id="dynContent" placeholder="内容" style="width:100%;height:120px"></textarea>
        <button class="btn-primary" onclick="submitDynamic()">发布</button>
    `);
}

async function submitDynamic() {
    if (!requireLogin()) return;
    const title = document.getElementById('dynTitle').value.trim();
    const contents = document.getElementById('dynContent').value.trim();
    if (!contents) return showToast('内容不能为空');
    try {
        await sendForm('/api/dynamic/write', { userId: currentUser.userId, title, contents });
        showToast('发布成功');
        closeModal();
        loadAllDynamics();
    } catch (err) { showToast(err.message); }
}

function editDynamic(id) {
    if (!requireLogin()) return;
    const card = [...document.querySelectorAll('.dynamic-card')].find(c =>
        c.querySelector('.d-actions')?.firstElementChild?.onclick?.toString().includes(String(id)));
    const title = card?.querySelector('.d-title')?.textContent.replace('（无标题）', '') || '';
    const content = card?.querySelector('.d-content')?.textContent || '';
    openModal(`
        <h3>编辑动态</h3>
        <input id="dynTitle" placeholder="标题" value="${escapeHtml(title)}" style="margin:10px 0">
        <textarea id="dynContent" placeholder="内容" style="width:100%;height:120px">${escapeHtml(content)}</textarea>
        <button class="btn-primary" onclick="submitEditDynamic(${id})">保存</button>
    `);
}

async function submitEditDynamic(dynamicId) {
    if (!requireLogin()) return;
    const title = document.getElementById('dynTitle').value.trim();
    const contents = document.getElementById('dynContent').value.trim();
    try {
        await sendForm('/api/dynamic/edit', {
            userId: currentUser.userId, dynamicId, title, contents,
        });
        showToast('修改成功');
        closeModal();
        loadAllDynamics();
    } catch (err) { showToast(err.message); }
}

async function deleteDynamic(dynamicId) {
    if (!requireLogin()) return;
    try {
        await sendDelete('/api/dynamic/delete', {
            userId: currentUser.userId, dynamicId,
        });
        showToast('删除成功');
        loadAllDynamics();
    } catch (err) { showToast(err.message); }
}

// ==================== 管理权限 ====================
async function addProduct(e) {
    if (!requireLogin()) return;
    e.preventDefault();
    const f = new FormData(e.target);
    try {
        await sendForm('/api/purchase/controller/add/product', {
            productName: f.get('productName'), price: f.get('price'),
            quantity: f.get('quantity'), total: f.get('total'),
        });
        showToast('添加成功');
        e.target.reset();
        loadProducts();
    } catch (err) { showToast(err.message); }
}

async function editPrice(e) {
    if (!requireLogin()) return;
    e.preventDefault();
    const f = new FormData(e.target);
    try {
        await sendForm('/api/purchase/controller/edit/price', {
            id: f.get('id'), name: f.get('name'), newPrice: f.get('newPrice'),
        });
        showToast('修改成功');
        loadProducts();
    } catch (err) { showToast(err.message); }
}

async function editName(e) {
    if (!requireLogin()) return;
    e.preventDefault();
    const f = new FormData(e.target);
    try {
        await sendForm('/api/purchase/controller/edit/product/name', {
            id: f.get('id'), newName: f.get('newName'),
        });
        showToast('修改成功');
        loadProducts();
    } catch (err) { showToast(err.message); }
}

async function deleteProduct(e) {
    if (!requireLogin()) return;
    e.preventDefault();
    const f = new FormData(e.target);
    if (!confirm('确定删除该商品？')) return;
    try {
        await sendDelete('/api/purchase/controller/delete/product', {
            id: f.get('id'), name: f.get('name'),
        });
        showToast('删除成功');
        loadProducts();
    } catch (err) { showToast(err.message); }
}

async function loadBuyers() {
    const table = document.getElementById('buyerTable');
    try {
        const list = await getJson('/api/purchase/controller/select/user');
        table.classList.remove('hidden');
        const tbody = table.querySelector('tbody');
        tbody.innerHTML = '';
        if (!list.length) {
            tbody.innerHTML = '<tr><td colspan="4" style="text-align:center;color:var(--text-sub)">暂无数据</td></tr>';
            return;
        }
        list.forEach(b => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${b.user_id}</td><td>${b.product_name}</td><td>${b.quantity}</td><td>${fmtMoney(b.total)}</td>`;
            tbody.appendChild(tr);
        });
    } catch (err) { showToast(err.message); }
}

// ==================== 弹窗 ====================
function openModal(html) {
    closeModal();
    const mask = document.createElement('div');
    mask.id = 'modalMask';
    mask.className = 'modal-mask';
    mask.innerHTML = `<div class="modal">${html}</div>`;
    mask.addEventListener('click', e => { if (e.target === mask) closeModal(); });
    document.body.appendChild(mask);
}

function closeModal() {
    document.getElementById('modalMask')?.remove();
}

// ==================== 初始化 ====================
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.nav-li').forEach(li => {
        li.addEventListener('click', () => {
            const p = li.dataset.page;
            if (p === 'video') return goHome();
            showPage(p);
        });
    });

    // 无论是否登录，都进入主界面（未登录为游客模式，可浏览但不可互动）
    enterMain();
});

// 完毕
