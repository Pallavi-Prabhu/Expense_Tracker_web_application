document.addEventListener("DOMContentLoaded", function () {
    const profileIcon = document.querySelector(".profile-icon");
    const dropdown = document.getElementById("dropdown");
    const greeting = document.getElementById("greeting");
    const logoutButton = document.getElementById("logout-button");

    profileIcon.addEventListener("click", function () {
        dropdown.style.display = dropdown.style.display === "block" ? "none" : "block";
    });

    logoutButton.addEventListener("click", function () {

        const xhttp = new XMLHttpRequest();
        xhttp.onload = function () {
            if (xhttp.status === 200) {
                window.location.href = "/login";
            }
        }
        xhttp.open("GET", "/logout");
        xhttp.send();

    });

    document.addEventListener("click", function (event) {
        if (!dropdown.contains(event.target) && !profileIcon.contains(event.target)) {
            dropdown.style.display = "none";
        }
    });
});
function activeLink() {
    const navItems = document.querySelectorAll("nav ul li");
    const currentURL = window.location.pathname;

    navItems.forEach(item => {
        const link = item.querySelector(".nav-link");
        if (link.getAttribute('href') === currentURL) {
            item.classList.add("active");
            link.classList.add("active");
        }
    });


    navItems.forEach(item => {
        item.addEventListener("click", event => {
            navItems.forEach(item => {
                item.classList.remove("active");
                link.classList.remove("active");
            });
            item.classList.add("active");
            link.classList.add("active");
        });
    });
}


activeLink();

var userId;
function dashBoard() {

    var xhttp = new XMLHttpRequest();
    let url = new URL('http://localhost:8080/dashboard');
    xhttp.open("GET", url, true);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {

           // console.log(this.responseText);
            var data = JSON.parse(this.responseText);
            var nameP = document.getElementById("nameEle");
            nameP.textContent = data[0];
            document.getElementById("greeting").textContent="Hello," +data[4];
            userId = data[3];
            createHistoryEventSource(userId);

        }
    }

}
dashBoard();

function createHistoryEventSource(userId) {
    //console.log("userId" + userId);
    const historyEventSource = new EventSource('/sse-history?userId=' + userId);

    historyEventSource.onmessage = function (event) {
        //console.log("Received SSE History Event:", event.data);
        var div = document.querySelector(".noticationDiv");
        div.innerHTML = "";
        var p = document.createElement("p");
        p.classList.add("notification")
        p.textContent = event.data;
        div.appendChild(p);

    };

    historyEventSource.onerror = function (event) {
        //console.error("SSE History Error:", event);
        historyEventSource.close();
    };
}