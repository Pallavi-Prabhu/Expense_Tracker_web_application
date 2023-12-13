//const activityBtn = document.getElementById('activity');
//activityBtn.style.backgroundColor = "rgb(122, 121, 121)";
//activityBtn.style.fontWeight = "bold";
var selectLabel = document.querySelector(".dropdownn");
selectLabel.style.display = "none";
document.getElementById('buttonLoad').style.visibility = 'hidden';


function clearDashboardContent() {
  const dashboardPage = document.getElementById('dashboardPage');
  const existingDescriptionPara = dashboardPage.querySelector('p');
  if (existingDescriptionPara) {
    existingDescriptionPara.remove();
  }
}
function dashboard() {
  document.getElementById('buttonLoad').style.visibility = 'visible';

  var xhttp = new XMLHttpRequest();
  let url = new URL('http://localhost:8080/dashboard');
  xhttp.open("GET", url, true);
  xhttp.send();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      //console.log(this.responseText);
      var data = JSON.parse(this.responseText);
      clearDashboardContent();
      if (data[1] == 0 && data[2] == 0) {
        document.getElementById('buttonLoad').style.visibility = 'hidden';

        var descriptionPara = document.createElement('p');
        descriptionPara.textContent = "No Activities/Pending Transactions Found";
        document.getElementById('dashboardChart').style.display = 'none';
        document.getElementById('dashboardPage').appendChild(descriptionPara);
      }
      else {

        document.getElementById('dashboardChart').style.display = 'block';
        var xValues = ["Total Debt", "Total receivables"];
        var yValues = [data[2], data[1]];
        var barColors = [
          "#b91d47",
          "#00aba9"
        ];

        new Chart("dashboardChart", {
          type: "pie",
          data: {
            labels: xValues,
            datasets: [{
              backgroundColor: barColors,
              data: yValues
            }]
          },
          options: {
            title: {
              display: true,
              text: "Status of pending Amount"
            }
          }
        });
        dashBoard2();
      }
    }
  }
}


var data;
var barChart;
function dashBoard2() {
  //  selectLabel.style.display = "block";
  var xhttp = new XMLHttpRequest();
  let url = new URL('http://localhost:8080/expenseHistory');
  xhttp.open("GET", url, true);
  xhttp.send();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      data = JSON.parse(this.responseText);
      document.getElementById('buttonLoad').style.visibility = 'hidden';
      selectLabel.style.display = "block";
      updateDropdownLabels(data);

      const positiveData = {
        label: 'You get',
        data: [],
        backgroundColor: 'rgba(0, 99, 132, 0.6)'
      };
      const negativeData = {
        label: 'You owe',
        data: [],
        backgroundColor: 'rgb(212, 110, 110)'
      };

      var labels = [];
      data.forEach(item => {
        labels.push(item.name);
        positiveData.data.push(item.pending);
        negativeData.data.push(item.lent);
      });


      const groupData = {
        labels: labels,
        datasets: [positiveData, negativeData]
      };

      var chartOptions = {
        title: {
          display: true,
          text: "Status of pending Amount GroupWise"
        },
        scales: {
          xAxes: [{
            barPercentage: 1,
            categoryPercentage: 0.5
          }]
        }
      };

      barChart = new Chart(groupBalanceChart, {
        type: 'bar',
        data: groupData,
        options: chartOptions
      });



    }
  }
}

function updateDropdownLabels(data) {
  const dropdownContent = document.getElementById('dropdownContent');
  dropdownContent.innerHTML = '';

  const maxVisibleCheckboxes = 4;


  const allLabel = document.createElement('label');
  const allCheckbox = document.createElement('input');
  allCheckbox.type = 'checkbox';
  allCheckbox.value = 'all';
  //allCheckbox.checked = true;
  allLabel.appendChild(allCheckbox);
  allLabel.appendChild(document.createTextNode('All'));
  dropdownContent.appendChild(allLabel);

  data.forEach(item => {
    const label = document.createElement('label');
    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.value = item.name;
    label.appendChild(checkbox);
    label.appendChild(document.createTextNode(item.name));
    dropdownContent.appendChild(label);

  });
  //console.log(data.length + "data")
  //console.log(maxVisibleCheckboxes + "max")


  if (data.length > maxVisibleCheckboxes) {
    dropdownContent.style.maxHeight = '150px';
    dropdownContent.style.overflowY = 'auto';
  }

}


function toggleDropdown() {
  var dropdownContent = document.getElementById('dropdownContent');
  if (dropdownContent.style.display == "block")
    dropdownContent.style.display = "none";
  else
    dropdownContent.style.display = "block";

}


const dropdownContent = document.getElementById('dropdownContent');
dropdownContent.addEventListener('change', function (event) {
  if (event.target && event.target.type === 'checkbox') {
    toggleGraphVisibility();
  }
});


function toggleGraphVisibility() {
  const allCheckbox = document.querySelector('.dropdownn-content input[value="all"]');
  //console.log(allCheckbox + "All")
  const checkboxes = document.querySelectorAll('.dropdownn-content input[type="checkbox"]:not([value="all"])');
  //console.log("check" + checkboxes)

  if (allCheckbox.checked) {
    checkboxes.forEach(checkbox => {
      checkbox.checked = false;
    });
  }

  const selectedLabels = [];
  checkboxes.forEach(checkbox => {
    if (checkbox.checked) {
      selectedLabels.push(checkbox.value);
    }
  });

  const onlyAllSelected = allCheckbox.checked && selectedLabels.length === 0;

  const filteredData = onlyAllSelected ? data : data.filter(item => selectedLabels.includes(item.name));


  const labels = filteredData.map(item => item.name);
  const positiveData = {
    label: 'You get',
    data: filteredData.map(item => item.pending),
    backgroundColor: 'rgba(0, 99, 132, 0.6)'
  };
  const negativeData = {
    label: 'You owe',
    data: filteredData.map(item => item.lent),
    backgroundColor: 'rgb(212, 110, 110)'
  };

  const groupData = {
    labels: labels,
    datasets: [positiveData, negativeData]
  };

  barChart.data = groupData;
  barChart.update();
}
dashboard();
