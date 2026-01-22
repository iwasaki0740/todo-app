const API_URL = "/api/schedules";

// ページ表示時に一覧取得
window.onload = loadSchedules;

// 一覧取得
function loadSchedules() {
    fetch(API_URL)
        .then(res => res.json())
        .then(data => {
            const list = document.getElementById("list");
            list.innerHTML = "";

            data.forEach(item => {
                const li = document.createElement("li");
                li.innerHTML = `${item.id}: ${item.title} (${item.date})
                    <button onclick="deleteSchedule(${item.id})">削除</button>`;
                list.appendChild(li);
            });
        });
}

// 追加
function addSchedule() {
    const title = document.getElementById("title").value;
    const date = document.getElementById("date").value;

    fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title, date })
    }).then(() => loadSchedules());
}

// 削除
function deleteSchedule(id) {
    fetch(`${API_URL}/${id}`, {
        method: "DELETE"
    }).then(() => loadSchedules());
}

async function login() {
    const username = document.getElementById('user').value;

    const res = await fetch("/api/user/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({username})
    });

    alert(await res.text());
}