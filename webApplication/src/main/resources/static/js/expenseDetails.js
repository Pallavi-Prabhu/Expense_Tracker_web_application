document.getElementById('buttonLoad').style.visibility = 'hidden';
//document.getElementById('buttonLoadList').style.visibility = 'hidden';
function backToHome() {
   // console.log("backTohome")
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/home");
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            window.location.href = "/home";
        }
    }
}
var pendingAmount = 0;
var groupIdd = 0;
var userIdd = 0;
var type; //group or single
var creator = 0; //if creator of group 1 else 0
var expenseData;
var groupName;
var groupCreatorName;

async function getExpenseDetails() {
    document.getElementById('buttonLoad').style.visibility = 'visible';
    var xhttp = new XMLHttpRequest();
    let url = new URL('http://localhost:8080/expenseDetails');
    xhttp.open("GET", url, true);
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById('buttonLoad').style.visibility = 'hidden';
           // console.log(this.responseText);
            var mainDiv = document.getElementById("group-container")
            expenseData = JSON.parse(this.responseText);
            if (expenseData.length == 0)
                mainDiv.textContent = "No expenses Found";
            expenseData.forEach(option => {
                groupName = option.groupName;
                groupCreatorName = option.groupCreatorName

                var expenseCardDiv = document.createElement("div");
                expenseCardDiv.classList.add("expense-card");
                var divFlex = document.createElement("div");
                divFlex.classList.add("div-flex");


                var expenseNameDiv = document.createElement("div");
                expenseNameDiv.classList.add("expense-name");
                expenseNameDiv.textContent = option.expenseName;

                const inputHidden = document.createElement("input");
                inputHidden.setAttribute("type", "hidden");
                inputHidden.classList.add("groupId")
                inputHidden.setAttribute("value", option.groupId);

                var paidByDiv = document.createElement("div");
                paidByDiv.classList.add("paid-by");
                paidByDiv.textContent = "Paid by: ";
                var spanElement = document.createElement("span");
                spanElement.classList.add("paid-byy");
                spanElement.textContent = option.payerName;
                paidByDiv.appendChild(spanElement);

                var totalAmountDiv = document.createElement("div");
                totalAmountDiv.classList.add("total-amount");
                totalAmountDiv.textContent = "Total-Rs.";
                var spanElement1 = document.createElement("span");
                spanElement1.classList.add("total-amountt");
                spanElement1.textContent = + option.totalAmount.toFixed(2);
                totalAmountDiv.appendChild(spanElement1);

                //                expenseCardDiv.appendChild(expenseNameDiv);
                //                expenseCardDiv.appendChild(paidByDiv);
                //                expenseCardDiv.appendChild(totalAmountDiv);
                divFlex.appendChild(expenseNameDiv);
                divFlex.appendChild(paidByDiv);
                divFlex.appendChild(totalAmountDiv);
                expenseCardDiv.appendChild(divFlex);
                expenseCardDiv.appendChild(inputHidden)
                pendingAmount = option.pendingAmount;
               // console.log(pendingAmount)
                groupIdd = option.groupId;
                userIdd = option.userId;
                creator = option.creator;


                var count = 0;
                for (var userId in option.userData) {
                    const inputHidden = document.createElement("input");
                    inputHidden.setAttribute("type", "hidden");
                    inputHidden.classList.add("text-box")
                    inputHidden.setAttribute("value", userId);
                    expenseCardDiv.appendChild(inputHidden);
                    if (option.userData.hasOwnProperty(userId)) {
                        var personAmountDiv = document.createElement("div");
                        personAmountDiv.classList.add("person-amount");
                        var spanElement = document.createElement("div");
                        // spanElement.classList.add("personName");
                        spanElement.style.width = "25%";
                        spanElement.textContent = option.userData[userId][0] + ":";
                        personAmountDiv.appendChild(spanElement);
                        var spanElement1 = document.createElement("div");
                        spanElement1.style.width = "25%";
                        // spanElement1.classList.add("personAmnt");
                        spanElement1.textContent = "Rs." + option.userData[userId][1];
                        personAmountDiv.appendChild(spanElement1);
                        if (option.userData[userId][2] == "settled") {
                            count++;
                            var spanElement2 = document.createElement("div");
                            spanElement2.classList.add("paid");
                            spanElement2.style.color = "green";
                            spanElement2.textContent = "  Paid";
                            personAmountDiv.appendChild(spanElement2);
                        }
                        else {
                            var spanElement2 = document.createElement("div");
                            spanElement2.classList.add("paid");
                            spanElement2.style.color = "blue";
                            spanElement2.textContent = " Unpaid";
                            personAmountDiv.appendChild(spanElement2);
                        }

                        // personAmountDiv.textContent = option.userData[userId][0] + ": " + option.userData[userId][1];
                        expenseCardDiv.appendChild(personAmountDiv);
                    }
                }
                if (option.status == 1) {  //status 1 means settled
                    var status = document.createElement("div");
                    status.textContent = "settled";
                    status.style.color = "green";
                    status.style.fontWeight = "bold";
                    expenseCardDiv.appendChild(status)
                }
                else {
                    if (option.payerId == option.userId) {
                        if (option.groupType == 0 && count < 2 || option.groupType == 1) {
                            var modifyBtn = document.createElement("button");
                            modifyBtn.textContent = "Modify";
                            modifyBtn.classList.add("buttonStyle");
                            modifyBtn.onclick = modifyExpense;
                            modifyBtn.style.marginRight = "10px";
                            expenseCardDiv.appendChild(modifyBtn);
                        }
                        var deleteBtn = document.createElement("button");
                        deleteBtn.classList.add("buttonStyle");
                        deleteBtn.textContent = "Delete";
                        deleteBtn.onclick = deleteExpense;
                        expenseCardDiv.appendChild(deleteBtn);
                    }
                }
                type = option.groupType;
                mainDiv.appendChild(expenseCardDiv)

            });
           // console.log("here")
            if (pendingAmount < 0) {
                //console.log("pending amount" + pendingAmount);
                var lastDiv = document.getElementById("settleClear")
                var btn = document.createElement("button")
                btn.classList.add("buttonStyle");
                btn.textContent = "SettleAll";
                btn.onclick = settleAll;
                lastDiv.appendChild(btn);
            }
        }
    }

}
//getExpenseDetails();


async function peopleList() {
    // if (type == 0) {
    //            document.getElementById('buttonLoadList').style.visibility = 'visible';
    var http = new XMLHttpRequest();
    // var param = "param1=" + encodeURIComponent(groupIdd) + "&param2=" + encodeURIComponent(userIdd);
    // http.open("POST", "/sendGroupDetails", true);
    //http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    // http.send(param);
    http.open("GET", "/sendGroupDetails", true);
    http.send();

    http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            //                       document.getElementById('buttonLoadList').style.visibility = 'hidden';
            //console.log(this.responseText);


            var peopleData = JSON.parse(this.responseText);
            if (peopleData.groupName != null) {

                var peopleListContainer = document.querySelector(".people-list");
                peopleListContainer.style.border = "2px solid #181717";
                peopleListContainer.style.borderRadius = "5px";
                peopleListContainer.style.boxShadow = "0px 0px 10px rgba(0, 0, 0, 0.2)";

                var groupInfoDiv = document.createElement("div");
                groupInfoDiv.className = "group-info";


                var groupNameElement = document.createElement("h5");
                groupNameElement.style.textAlign = "center";
                groupNameElement.style.margin = "5px";
                groupNameElement.textContent = peopleData.groupName;

                var createdByElement = document.createElement("p");
                createdByElement.style.textAlign = "center";
                createdByElement.style.margin = "0px";
                createdByElement.textContent = "(Creator:" + peopleData.groupCreatorName + ")";

                groupInfoDiv.appendChild(groupNameElement);
                groupInfoDiv.appendChild(createdByElement);

                peopleListContainer.appendChild(groupInfoDiv);
                groupIdd = peopleData.groupId;
                creator = peopleData.creator;
                var ul = document.createElement("ul");
                ul.style.overflow = "auto";
                for (var key in peopleData.data) {
                    if (peopleData.data.hasOwnProperty(key)) {
                        (function (key) {
                            var li = document.createElement("li");

                            if (creator == 1) {
                                var minusIcon = document.createElement("i");
                                minusIcon.className = "fas fa-minus-circle";
                                minusIcon.style.paddingRight = "5px";
                                minusIcon.style.cursor = "pointer";


                                minusIcon.addEventListener("click", function (event) {
                                    removeUser(event, peopleData.data[key][1], key, groupIdd);
                                });

                                li.appendChild(minusIcon);
                                // var minusIcon = document.createElement("img");
                                // minusIcon.src = "/images/minus.jpg";
                                // minusIcon.id = "addIcon";
                                // minusIcon.alt = "-";
                                // minusIcon.style.height = "20px";
                                // minusIcon.style.width = "20px";
                                // minusIcon.style.cursor = "pointer";

                                // minusIcon.addEventListener("click", function (event) {
                                //     removeUser(event, peopleData[key][1], key, groupIdd);
                                // });

                                // li.appendChild(minusIcon);
                            }
                            else {
                                li.style.listStyleType = "circle";
                                ul.style.padding = "20px";
                            }
                            var span = document.createElement("span");
                            span.textContent = key + ": ";
                            li.appendChild(span);
                            var br = document.createElement("br");
                            li.appendChild(br);
                            var span1 = document.createElement("span");
                            span1.textContent = peopleData.data[key][0];
                            li.appendChild(span1);
                            ul.appendChild(li);
                        })(key);
                    }
                }
            }
            var http1 = new XMLHttpRequest();
            var param1 = "param3=" + encodeURIComponent(groupIdd);
            http1.open("POST", "/getNonGroupMembers", true);
            http1.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            http1.send(param1);

            http1.onreadystatechange = function () {
                if (this.readyState == 4 && this.status == 200) {

                   // console.log(this.responseText);
                    var optionsData = JSON.parse(this.responseText);

                    var peopleDropdown = document.createElement("select");
                    peopleDropdown.id = "peopleDropdown";
                    peopleDropdown.multiple = true;

                    for (var key in optionsData) {
                        var optionElement = document.createElement("option");
                        optionElement.value = key;
                        optionElement.text = optionsData[key];
                        peopleDropdown.appendChild(optionElement);
                    }

                    var addButton = document.createElement("button");
                    addButton.id = "buttonStyle";

                    addButton.textContent = "Add";

                    var dropdownContainer = document.createElement("div");
                    dropdownContainer.id = "dropdownContainer";
                    dropdownContainer.className = "hidden";
                    dropdownContainer.appendChild(peopleDropdown);
                    dropdownContainer.appendChild(addButton);

                    // var addIcon = document.createElement("img");
                    // addIcon.src = "/images/add.png";
                    // addIcon.id = "addIcon";
                    // addIcon.alt = "+";
                    // addIcon.style.cursor = "pointer";
                    var addIcon = document.createElement("i");
                    addIcon.className = "fa-solid fa-circle-plus";
                    addIcon.style.cursor = "pointer";
                    addIcon.style.fontSize = "25px";
                    addIcon.style.padding = "5px";
                    addIcon.addEventListener("click", function () {
                        dropdownContainer.classList.toggle("hidden");
                    });

                      var loadIcon = document.createElement("i");
                      loadIcon.className = "fa fa-refresh fa-spin";
                      loadIcon.setAttribute("id", "createLoadIcon");



                    addButton.addEventListener("click", function () {
                        var selectedOptions = Array.from(peopleDropdown.selectedOptions);
                        var selectedPeople = selectedOptions.map(option => option.text);
                       // console.log("Selected People:", selectedPeople);
                        dropdownContainer.classList.add("hidden");
                        var data = {
                            groupId: groupIdd,
                            selectedPeople: selectedPeople
                        };
                        var http = new XMLHttpRequest();
                        http.open("POST", "addNewPeople", true);
                        http.setRequestHeader('Content-Type', 'application/json');
                        http.send(JSON.stringify(data));
                        document.getElementById("createLoadIcon").style.visibility = "visible";

                        http.onreadystatechange = function () {
                            if (this.readyState == 4 && this.status == 200) {
                             document.getElementById("createLoadIcon").style.visibility = "hidden";
                               // console.log("ok");
                                window.location.href = "/expenseDetailPage";
                            }
                        }
                    });
                    if (creator == 1) {
                        peopleListContainer.appendChild(addIcon);
                        peopleListContainer.appendChild(loadIcon);
                        document.getElementById("createLoadIcon").style.visibility = "hidden";
                        peopleListContainer.appendChild(dropdownContainer);
                    }
                    peopleListContainer.appendChild(ul);
                }
            };
        }
    };



    // }
}
//peopleList();
async function main() {
  try {
    await getExpenseDetails();
    peopleList();
  } catch (error) {
   // console.error("An error occurred:", error);
  }
}

main();




function removeUser(event, userId, name, groupId) {
   // console.log("inside Remove User")
    //console.log(userId)
    var li = event.target.closest("li");
    //console.log(li);
    var confirmed = confirm("Are you sure you want to delete " + name + " ?");
    if (confirmed) {
        document.getElementById("createLoadIcon").style.visibility = "visible";
        var userIdToSearch = userId;
        var isUserSettled = true;
        dataLoop: for (var i = 0; i < expenseData.length; i++) {
            var group = expenseData[i];

            if (group.payerId === userIdToSearch && group.status === 0) {
                isUserSettled = false;
                break dataLoop;
            }

            for (var key in group.userData) {
                if (group.userData.hasOwnProperty(key)) {
                    var user = group.userData[key];
                    if (user[3] === userIdToSearch && user[2] === "not settled") {
                        isUserSettled = false;
                        break dataLoop;
                    }
                }

            }
        }


        if (isUserSettled) {

            var http = new XMLHttpRequest();
            var param = "paramUsrId=" + encodeURIComponent(userId) + "&paramGrpId=" + encodeURIComponent(groupId);
            http.open("POST", "/removeUser", true);
            http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            http.send(param);
            http.onreadystatechange = function () {
            document.getElementById("createLoadIcon").style.visibility = "hidden";
             if (li) {
                            li.remove();
                        }
                // if (this.readyState == 4 && this.status == 200) {
                //     window.location.href = "/addNewExpenseGet";
                // }
            }
           // console.log("User with ID " + userIdToSearch + " is settled across all groups.");
        } else {
        document.getElementById("createLoadIcon").style.visibility = "hidden";
            alert("User  " + name + " has not settled.Hence can't be removed");
        }
    }


}

function addNewExpense() {


    var parentContainer = document.getElementById("group-container");
    var groupId;
    var expenseCards = parentContainer.getElementsByClassName("expense-card");
    for (var i = 0; i < expenseCards.length; i++) {
        var expenseCard = expenseCards[i];
        var inputHidden = expenseCard.querySelector(".groupId");

        if (inputHidden) {
            groupId = inputHidden.value;
          //  console.log("Found groupId:", groupId);

        }
    }

    //var parent = document.querySelector("expense-card")
    // var groupID = parent.querySelector(".groupId");
    if (groupId) {
       // console.log(groupId.value);
    } else {
       // console.log("Element with class 'groupId' not found.");
    }
    var http = new XMLHttpRequest();
    var param = "param1=" + encodeURIComponent(groupId);
    http.open("POST", "/addNewExpense", true);
    http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    http.send(param);
    document.getElementById('buttonLoad').style.visibility = 'visible';
    http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById('buttonLoad').style.visibility = 'hidden';
            window.location.href = "/addNewExpenseGet";
        }
    }

}

function deleteExpense(event) {
    var parent = event.target.parentNode;
    var textBoxes = parent.querySelectorAll('.text-box');
    var textBoxValues = [];
    textBoxes.forEach(function (textBox) {
        var inputValue = textBox.value;
        textBoxValues.push(inputValue);
       // console.log(inputValue);
    });
    var groupID = parent.querySelector(".groupId");

    var http = new XMLHttpRequest();
    var param = "param=" + encodeURIComponent(groupID.value);
    for (var i = 0; i < textBoxValues.length; i++) {
        param += "&param" + i + "=" + encodeURIComponent(textBoxValues[i]);
    }
    http.open("POST", "/deleteExpenseDetails", true);
    document.getElementById('buttonLoad').style.visibility = 'visible';
    http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    http.send(param);
    http.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById('buttonLoad').style.visibility = 'hidden';
            window.location.href = "/deleteExpense";
        }
    }
}

function settleAll() {
document.getElementById('buttonLoad').style.visibility = 'visible';
    const xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/settleAll", true);
    xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xhttp.send("selectedGroupId=" + encodeURIComponent(groupIdd));
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
        document.getElementById('buttonLoad').style.visibility = 'hidden';
            window.location.href = "/home";
        }
    }

}

function modifyExpense(event) {
   // console.log("modify");
    var parent = event.target.parentNode;
   // console.log(parent)
    var groupID = parent.querySelector(".groupId");

    var totAmt = parent.querySelector(".total-amountt");

    var expenseName = parent.querySelector(".expense-name");

    var paidBy = parent.querySelector(".paid-byy");

    var textBoxes = parent.querySelectorAll('.text-box');
    var textBoxValues = [];
    textBoxes.forEach(function (textBox) {
        var inputValue = textBox.value;
        textBoxValues.push(inputValue);
        //console.log(inputValue);

    });

    if (groupID && totAmt && expenseName && paidBy) {
        document.getElementById('buttonLoad').style.visibility = 'visible';
        var http = new XMLHttpRequest();
        var param = "param1GroupId=" + encodeURIComponent(groupID.value) + "&param2Amnt=" +
            encodeURIComponent(totAmt.textContent) + "&param3Expense=" + encodeURIComponent(expenseName.textContent) +
            "&param4PaidBy=" + encodeURIComponent(paidBy.textContent);
        var j = 5;
        for (var i = 0; i < textBoxValues.length; i++) {
            param += "&param" + j + "=" + encodeURIComponent(textBoxValues[i]);
            j++;
        }
        http.open("POST", "/modifyExpenseDetails", true);
        http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        http.send(param);
        http.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                document.getElementById('buttonLoad').style.visibility = 'hidden';
                window.location.href = "/expenseModify";
            }
        }
    } else {
        //console.log("One or more elements not found.");
    }


}