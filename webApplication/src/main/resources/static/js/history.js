//const activityBtn = document.getElementById('activity');
//activityBtn.style.backgroundColor = "rgb(122, 121, 121)";
//activityBtn.style.fontWeight = "bold";
//var selectLabel = document.querySelector(".dropdown");
//selectLabel.style.display = "none";
document.getElementById('buttonLoad').style.visibility = 'hidden';


function clearActivityContent() {
  const activityPage = document.querySelector('.transaction-list');
  const existingDescriptionPara = activityPage.querySelector('p');
  if (existingDescriptionPara) {
    existingDescriptionPara.remove();
  }
}

function formatDateTime(datetimeString) {
  const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
  const formattedDateTime = new Date(datetimeString).toLocaleDateString('en-US', options);
  return formattedDateTime;
}


var pageNumber = 1;
function history(pageNumber) {
  //document.getElementById('buttonLoad').style.visibility = 'visible';
  var xhttp = new XMLHttpRequest();
  let url = new URL('http://localhost:8080/historyDetails');
  //console.log(pageNumber)
  var param = "postPageNo=" + encodeURIComponent(pageNumber);
  xhttp.open("POST", url, true);
  xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
  xhttp.send(param);
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      //    document.getElementById('buttonLoad').style.visibility = 'hidden';
      var data = JSON.parse(this.responseText);
     // console.log((data.length))

      var transactionListContainer = document.querySelector('.transaction-list');
      //      clearDashboardContent();
      transactionListContainer.innerHTML = "";
      if (data.length === 0) {

        clearActivityContent();

        const descriptionParagraph = document.createElement('p');
        descriptionParagraph.textContent = "No Activities Found";
        transactionListContainer.appendChild(descriptionParagraph);
      }

      data.forEach(transaction => {
        var transactionElement = document.createElement('div');
        transactionElement.classList.add('transaction');

        var descriptionParagraph = document.createElement('p');
        descriptionParagraph.textContent = transaction.description;

        var dateTimeParagraph = document.createElement('p');
        dateTimeParagraph.classList.add('dateTime');
        dateTimeParagraph.textContent = formatDateTime(transaction.dateTime);

        transactionElement.appendChild(descriptionParagraph);
        transactionElement.appendChild(dateTimeParagraph);

        transactionListContainer.appendChild(transactionElement);
      });

    }
  }
}
history(pageNumber);
var totalRecords;
function getTotalRecords() {
  var xhttp = new XMLHttpRequest();
  let url = new URL('http://localhost:8080/getRecords');
  xhttp.open("GET", url, true);
  xhttp.send();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {

      //console.log(this.responseText);
      totalRecords = JSON.parse(this.responseText);
      if (totalRecords > 6)
        document.querySelector('.pagination').style.display = "block";
      //console.log("total" + totalRecords);
      generatePaginationLinks(pageNumber);

    }
  }
}
getTotalRecords();



var pageSize = 6;

const paginationList = document.getElementById('paginationList');

function generatePaginationLinks(currentPage, event) {
  //console.log(event);
  //console.log(currentPage)
  pageNumber = currentPage;
  history(pageNumber);
 // console.log("total insideFn" + totalRecords);
  const totalPages = Math.ceil(totalRecords / pageSize);
  paginationList.innerHTML = '';

  if (currentPage > 1) {
    addPaginationLink('< Previous', currentPage - 1, currentPage);
  }
  addPaginationLink(1, 1, currentPage);

  if (currentPage > 3) {
    addEllipsis();
  }

  for (let page = Math.max(2, currentPage - 1); page <= Math.min(currentPage + 1, totalPages - 1); page++) {
    addPaginationLink(page, page, currentPage);
  }

  if (currentPage < totalPages - 2) {
    addEllipsis();
  }

  addPaginationLink(totalPages, totalPages, currentPage);

  if (currentPage < totalPages) {
    addPaginationLink('Next >', currentPage + 1, currentPage);
  }
}

function addPaginationLink(text, page, currentPage) {
  const li = document.createElement('li');
  const link = document.createElement('a');
  link.textContent = text;
  link.href = '#';
  if (page == currentPage) {
    link.classList.add("active");
  }
  link.addEventListener('click', function () {
    //const allLinks = paginationList.querySelectorAll('a');
    // allLinks.forEach(link => {
    //   link.style.backgroundColor = '';
    // });
    // this.style.backgroundColor = 'blue';
    generatePaginationLinks(page, event);
  });

  li.appendChild(link);
  paginationList.appendChild(li);
}

function addEllipsis() {
  const li = document.createElement('li');
  li.classList.add('ellipsis');
  li.textContent = '...';
  paginationList.appendChild(li);
}





