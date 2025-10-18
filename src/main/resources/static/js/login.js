document.getElementById('loginForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            credentials: 'include',
            body: JSON.stringify({ username, password })
        });
        if (res.status === 423) {
            // blocked account
            const body = await res.json().catch(() => ({}));
            const msg = body && body.message ? body.message : 'Your account is blocked.';
            showToast(msg);
            return;
        }
        if (res.ok) {
            const data = await res.json();
            if (data && data.token) {
                localStorage.setItem('authToken', data.token);
            }
            window.location.href = '/';
        } else if (res.status === 401) {
            showToast('Invalid username or password');
        } else {
            showToast('Login failed');
        }
    } catch (err) {
        showToast('Error logging in');
    }
});

function showToast(message) {
    const toast = document.getElementById('toast');
    const msg = document.getElementById('toastMessage');
    msg.textContent = message;
    toast.style.display = 'block';
    setTimeout(() => { toast.style.display = 'none'; }, 5000);
}
