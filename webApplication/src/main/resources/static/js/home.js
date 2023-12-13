
var addButtonGenerated1 = false;
var addButton;
var isDropdownVisible = false;
document.getElementById('buttonLoadGroup').style.visibility = 'hidden';
//document.getElementById('buttonLoadFriends').style.visibility = 'hidden';


function dropdown() {
   //   document.getElementById('buttonLoad').style.visibility = 'visible';

   if (!addButtonGenerated1) {
      var divEle = document.getElementById("addUser");
      addButton = document.createElement("button");
      addButton.textContent = "Add";
      addButton.setAttribute("class", "buttonStyle");
      addButton.onclick = addUser;
      addButton.style.visibility = "hidden";
      divEle.appendChild(addButton);
   }

   var sortOptions = document.getElementById("sortOptions");

   if (!isDropdownVisible) {

      sortOptions.style.visibility = "visible";
      addButton.style.visibility = "visible";
   } else {

      sortOptions.style.visibility = "hidden";
      addButton.style.visibility = "hidden";
   }

   sortOptions.innerHTML = "";

   var xhttp = new XMLHttpRequest();
   let url = new URL('http://localhost:8080/user-lists');
   xhttp.open("GET", url, true);
   xhttp.send();
   xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
         //         document.getElementById('buttonLoad').style.visibility = 'hidden';
         // console.log(this.responseText);
         var sel = document.getElementById("sortOptions");
         defaultOption = document.createElement("option");
         defaultOption.value = "";
         defaultOption.textContent = "Select User";
         defaultOption.disabled = true;
         defaultOption.selected = true;
         sel.appendChild(defaultOption);
         var optionsData = JSON.parse(this.responseText);
         optionsData.forEach(option => {
            const optionElement = document.createElement("option");
            optionElement.value = option.email;
            optionElement.text = option.firstName + " :" + option.email;
            sel.appendChild(optionElement);
         });
         addButtonGenerated1 = true;

      }
   }

   isDropdownVisible = !isDropdownVisible;
}



var addButtonGenerated = false;
var inputEle;
var addBtn;

const closeModalButton = document.getElementById("closeModal");
closeModalButton.addEventListener("click", () => { modal.style.display = "none"; });
function dropdownGroup() {
   const modal = document.getElementById("modal");
   modal.style.display = "block";
   //   document.getElementById('buttonLoad').style.visibility = 'visible';
   var selectOption = document.getElementById("sortOptionsGroup");
   var computedVisibility = window.getComputedStyle(selectOption).getPropertyValue("visibility");

   if (!addButtonGenerated) {
      // var divEle = document.getElementById("addGroup");
      var divEle = document.querySelector(".modal-content");
      inputEle = document.createElement("input");
      inputEle.setAttribute("class", "groupName");
      inputEle.setAttribute("maxlength", 15);
      inputEle.placeholder = "Enter Group Name";
      divEle.appendChild(inputEle);
      // inputEle.style.display = "none";

      addBtn = document.createElement("button");
      addBtn.textContent = "Create";
      addBtn.setAttribute("class", "buttonStyle");
      addBtn.onclick = addGroups;
      divEle.appendChild(addBtn);

      var iconBtn = document.createElement("button");
      iconBtn.setAttribute("id", "createLoadIcon");
      var loadIcon = document.createElement("i");
      loadIcon.className = "fa fa-refresh fa-spin";
      iconBtn.style.border = "none";
      iconBtn.appendChild(loadIcon);
      divEle.appendChild(iconBtn)
      document.getElementById("createLoadIcon").style.visibility = "hidden";



      // addBtn.style.display = "none";
   }

   //   if (computedVisibility !== "hidden") {
   //      selectOption.style.visibility = "hidden";
   //      inputEle.style.display = "none";
   //      addBtn.style.display = "none";
   //   } else {
   if (computedVisibility === "hidden") {
      selectOption.style.visibility = "visible";
      inputEle.style.display = "inline-block";
      addBtn.style.display = "inline-block";
   }

   selectOption.innerHTML = "";

   var xhttp = new XMLHttpRequest();
   let url = new URL('http://localhost:8080/user-lists');
   xhttp.open("GET", url, true);
   xhttp.send();
   xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
         //         document.getElementById('buttonLoad').style.visibility = 'hidden';
         //console.log(this.responseText);
         var optionsData = JSON.parse(this.responseText);

         var defaultOption = document.createElement("option");
         defaultOption.value = "";
         defaultOption.textContent = "Select Users";
         defaultOption.disabled = true;
         defaultOption.selected = true;
         selectOption.appendChild(defaultOption);

         optionsData.forEach(option => {
            const optionElement = document.createElement("option");
            optionElement.value = option.id;
            optionElement.text = option.firstName + " :" + option.email;
            selectOption.appendChild(optionElement);
         });

         addButtonGenerated = true;
      }
   }
}




function addGroups() {

   var input = document.querySelector(".groupName");
   var selectElement = document.getElementById("sortOptionsGroup");
   var selectedOptions = selectElement.selectedOptions;
   if (selectedOptions.length <= 1) {
      alert("select more than 1")
   }
   else if (input.value == '')
      {
         alert("enter group Name")
      }
   else {
      groupName = input.value;
     
      var selectedValues = Array.from(selectedOptions).map(option => option.value);
      var postData = {
         groupName: groupName,
         selectedOptions: selectedValues
      };
      var jsonData = JSON.stringify(postData);

      var xhttp = new XMLHttpRequest();
      let url = new URL('http://localhost:8080/addGroup');
      xhttp.open("POST", url, true);
      xhttp.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
      xhttp.send(jsonData);
      document.getElementById("createLoadIcon").style.visibility = "visible";
      xhttp.onreadystatechange = function () {
         if (this.readyState == 4 && this.status == 200) {
            document.getElementById("createLoadIcon").style.visibility = "hidden";
            window.location.href = "/home";
         }
      }
   }
}

function addUser() {
   //console.log("addUser")
   var selectElement = document.getElementById("sortOptions");

   if (selectElement.value === "") {
      alert("Please select an user.");
   }

   else {
      const selectedValue = selectElement.value;
      const xhttp = new XMLHttpRequest();
      xhttp.open("POST", "/setSelectedUser", true);
      xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
      xhttp.send("selectedUser=" + encodeURIComponent(selectedValue));
      xhttp.onreadystatechange = function () {
         if (this.readyState == 4 && this.status == 200) {
            window.location.href = "/expense"; // Redirect to the next page
         }
      }
   }



}




function getGroups() {

   document.getElementById('buttonLoadGroup').style.visibility = 'visible'
   var xhttp = new XMLHttpRequest();
   let url = new URL('http://localhost:8080/group-lists');
   xhttp.open("GET", url, true);
   xhttp.send();
   xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
         document.getElementById('buttonLoadGroup').style.visibility = 'hidden'
         //console.log(this.responseText);
         var div = document.getElementById("Group");//$("#"),$(".")
         div.innerHTML = "";
         var usersData = JSON.parse(this.responseText);
         usersData.forEach(option => {


            var viewIcon = document.createElement("i");
            viewIcon.className = "fa-solid fa-eye";
            viewIcon.style.cursor = "pointer";
            viewIcon.style.paddingTop = "5px";
            viewIcon.style.fontSize = "12px";
            viewIcon.style.color = "mediumblue";
            viewIcon.style.marginLeft = "3%";
            viewIcon.onclick = viewExpenseDetailsGroup;

            var mainDiv = document.createElement("div");
            mainDiv.classList.add("groupDetail");
            const groupElement = document.createElement("div");
            groupElement.classList.add("groupName");


            const inputHidden = document.createElement("input");
            inputHidden.setAttribute("type", "hidden");
            inputHidden.classList.add("text-box")
            inputHidden.setAttribute("value", option.groupID);

            const inputHidden1 = document.createElement("input");
            inputHidden1.setAttribute("type", "hidden");
            inputHidden1.classList.add("text-box1")
            inputHidden1.setAttribute("value", option.status);

            const nameElement = document.createElement("div");
            nameElement.style.width = "30%";
            nameElement.style.fontSize = "12px";
            nameElement.style.marginLeft = "1%";

            const textSpan = document.createElement("span");
            textSpan.textContent = "Group Name:";
            const br = document.createElement("br");
            const messageSpanEle = document.createElement("span");
            messageSpanEle.textContent = option.groupName;
            messageSpanEle.style.fontWeight = "bold";
            nameElement.appendChild(textSpan);
            nameElement.appendChild(br);
            nameElement.appendChild(messageSpanEle)


            const amountPosElement = document.createElement("div");
            amountPosElement.classList.add("amount");
            amountPosElement.style.width = "25%";
            amountPosElement.style.marginLeft = "1%";
            amountPosElement.classList.add("positiveAmount");
            const amountSpan = document.createElement("span");
            amountSpan.textContent = option.positiveAmount.toFixed(2);
            const br1 = document.createElement("br");
            const messageSpan1 = document.createElement("span");
            messageSpan1.textContent = "You get Rs:";

            amountPosElement.appendChild(messageSpan1);
            amountPosElement.appendChild(br1);
            amountPosElement.appendChild(amountSpan);

            const amountNegElement = document.createElement("div");
            amountNegElement.style.width = "25%"
            amountNegElement.style.marginLeft = "1%";
            amountNegElement.classList.add("amount");
            amountNegElement.classList.add("negativeAmount");

            const messageSpanNeg = document.createElement("span");
            messageSpanNeg.textContent = "You owe Rs:";
            amountNegElement.appendChild(messageSpanNeg);
            const brElementNeg = document.createElement("br");
            amountNegElement.appendChild(brElementNeg);
            const amountSpanNeg = document.createElement("span");
            amountSpanNeg.id = "amountNeg";
            amountSpanNeg.textContent = option.negativeAmount.toFixed(2);
            amountNegElement.appendChild(amountSpanNeg);


            mainDiv.appendChild(viewIcon);
            mainDiv.appendChild(nameElement);
            mainDiv.appendChild(inputHidden);
            mainDiv.appendChild(inputHidden1);
            mainDiv.appendChild(amountPosElement);
            mainDiv.appendChild(amountNegElement);
            //  const deleteImgElement = document.createElement("img");
            //deleteImgElement.style.visibility="hidden";
            if (option.status == 1) {
               //            <i class="fa-solid fa-trash-can"></i>

               var deleteIcon = document.createElement("i");
               deleteIcon.className = "fa-solid fa-trash";
               deleteIcon.style.cursor = "pointer";
               deleteIcon.style.paddingTop = "5px";
               deleteIcon.style.fontSize = "12px";
               deleteIcon.onclick = deleteGroup;


               mainDiv.appendChild(deleteIcon);
            }


            div.appendChild(mainDiv);
            //mainDiv.onclick = viewExpenseDetailsGroup;
         });


      }

   }
}

getGroups();


function viewExpenseDetailsGroup(event) {
   var parent = event.currentTarget.parentNode;
   var inputElement = parent.querySelector('.text-box');
   var inputValue = inputElement.value;
   var amountElement = parent.querySelector('#amountNeg');
   var amountValue = amountElement.textContent;
   var http = new XMLHttpRequest();
   var creatorElement = parent.querySelector('.text-box1');
   var creator = creatorElement.value;

   var param = "paramId=" + encodeURIComponent(inputValue) + "&paramAmnt=" + encodeURIComponent(amountValue) + "&paramCreator=" + encodeURIComponent(creator);
   http.open("POST", "/selectedGroupIDGroup", true);
   http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
   http.send(param);
   http.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
         window.location.href = "/expenseDetailPage";
      }
   }
}


function viewExpenseDetails(event) {
   var parent = event.currentTarget.parentNode;
   //console.log("inside viewExpenseDetails");
   var inputElement = parent.querySelector('.text-box');
   var inputValue = inputElement.value;
   var amountElement = parent.querySelector('#amountt');
   var amountValue = amountElement.textContent;
   //console.log(amountValue);
   //console.log("Input value:", inputValue);
   var http = new XMLHttpRequest();
   var param = "param1=" + encodeURIComponent(inputValue) + "&param2=" + encodeURIComponent(amountValue);
   http.open("POST", "/selectedGroupID", true);
   http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
   http.send(param);
   http.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
         window.location.href = "/expenseDetailPage";
      }
   }
}


function deleteGroup(event) {
   var iconBtn = document.createElement("button");
   iconBtn.setAttribute("id", "deleteLoadIcon");
   var loadIcon = document.createElement("i");
   loadIcon.className = "fa fa-refresh fa-spin";
   iconBtn.style.border = "none";
   iconBtn.appendChild(loadIcon);
   var ele = document.getElementById('addGroup');
   ele.appendChild(iconBtn);
   document.getElementById("deleteLoadIcon").style.visibility = "hidden";
   //console.log("inside deleteGroup");
   event.stopPropagation();
   var parentDiv = this.parentElement;
   var inputElement = parentDiv.querySelector('.text-box');
   var inputValue = inputElement.value;
   //console.log("Input value:", inputValue);
   var confirmed = confirm("Are you sure you want to delete?");
   if (confirmed) {
      document.getElementById("deleteLoadIcon").style.visibility = "visible";
      var http = new XMLHttpRequest();
      var param = "postData=" + encodeURIComponent(inputValue); // Properly formatted data
      http.open("POST", "/deleteUserGroup", true);
      http.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
      http.send(param);
      http.onreadystatechange = function () {
         if (this.readyState == 4 && this.status == 200) {
            document.getElementById("deleteLoadIcon").style.visibility = "hidden";
            window.location.href = "/home";
         }
      }
   }

}


function history() {
   var xhttp = new XMLHttpRequest();
   let url = new URL('http://localhost:8080/history');
   xhttp.open("GET", url, true);
   xhttp.send();
   xhttp.onreadystatechange = function () {
      if (xhttp.readyState === 4) {
         if (xhttp.status === 200) {

            window.location.href = "history";
         }
      }
   }
}

var pageNumber = 1;
var amount = 0;
function getUserGroup(pageNumber) {
   var iconBtn = document.createElement("button");
   iconBtn.setAttribute("id", "buttonLoadFriends");
   var loadIcon = document.createElement("i");
   loadIcon.className = "fa fa-refresh fa-spin";
   iconBtn.style.border = "none";
   iconBtn.appendChild(loadIcon);
   var ele = document.getElementById('friendsGroup');
   ele.appendChild(iconBtn);
   document.getElementById('buttonLoadFriends').style.visibility = 'visible';

   var xhttp = new XMLHttpRequest();
   let url = new URL('http://localhost:8080/user-group-lists');
   var param = "postPageNo=" + encodeURIComponent(pageNumber);
   xhttp.open("POST", url, true);
   xhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
   xhttp.send(param);
   xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
         document.getElementById('buttonLoadFriends').style.visibility = 'hidden';
         document.querySelector('.pagination').style.visibility = "visible";

         //console.log(this.responseText);
         var div = document.getElementById("friendsGroup");//$("#"),$(".")
         div.innerHTML = "";
         var usersData = JSON.parse(this.responseText);
         usersData.forEach(option => {

            var viewIcon = document.createElement("i");
            viewIcon.className = "fa-solid fa-eye";
            viewIcon.style.cursor = "pointer";
            viewIcon.style.paddingTop = "5px";
            viewIcon.style.color = "mediumblue";
            viewIcon.style.fontSize = "12px";
            viewIcon.onclick = viewExpenseDetails;

            var mainDiv = document.createElement("div");
            mainDiv.classList.add("groupDetail");
            mainDiv.style.justifyContent = "space-around";
            const groupElement = document.createElement("div");
            groupElement.classList.add("groupName");


            const inputHidden = document.createElement("input");
            inputHidden.setAttribute("type", "hidden");
            inputHidden.classList.add("text-box")
            inputHidden.setAttribute("value", option.groupID);

            const nameElement = document.createElement("div");
            const textSpan = document.createElement("span");
            nameElement.style.fontSize = "12px";
            textSpan.textContent = "User Name:";
            const br = document.createElement("br");
            const messageSpan = document.createElement("span");
            messageSpan.textContent = option.groupName;
            messageSpan.style.fontWeight = "bold";
            nameElement.appendChild(textSpan);
            nameElement.appendChild(br);
            nameElement.appendChild(messageSpan);




            //            const amountElement = document.createElement("div");
            //            amountElement.classList.add("amount")
            //            amountElement.textContent = option.totalAmount.toFixed(2);
            //            amount = option.totalAmount;
            //            if (option.totalAmount < 0) {
            //               amountElement.classList.add("negativeAmount");
            //            }
            //            else
            //               amountElement.classList.add("positiveAmount");
            //            const deleteImgElement = document.createElement("img");
            //            deleteImgElement.src = "/images/delete.png";
            //            deleteImgElement.alt = "delete";
            //            deleteImgElement.classList.add("deleteImg");
            //            deleteImgElement.onclick = deleteGroup;


            const amountElement = document.createElement("div");
            amountElement.classList.add("amount");

            const amountSpan = document.createElement("span");
            amountSpan.id = "amountt"
            amountSpan.textContent = option.totalAmount.toFixed(2);

            if (option.totalAmount < 0) {
               amountElement.classList.add("negativeAmount");
               const brElementNeg = document.createElement("br");

               const messageSpanNeg = document.createElement("span");
               messageSpanNeg.textContent = "You owe Rs:";
               amountElement.appendChild(messageSpanNeg);
               amountElement.appendChild(brElementNeg);
            } else {
               amountElement.classList.add("positiveAmount");
               const brElementPos = document.createElement("br");

               const messageSpanPos = document.createElement("span");
               messageSpanPos.textContent = "You get Rs:";
               amountElement.appendChild(messageSpanPos);
               amountElement.appendChild(brElementPos);
            }
            amountElement.appendChild(amountSpan);



            mainDiv.appendChild(viewIcon);
            mainDiv.appendChild(nameElement);
            mainDiv.appendChild(inputHidden);
            mainDiv.appendChild(amountElement);
            // mainDiv.appendChild(deleteImgElement);
            // mainDiv.onclick = viewExpenseDetails;

            div.appendChild(mainDiv);
         });

      }

   }
}

/*getUserGroup(pageNumber);*/




var totalRecords;
function getTotalRecords() {
   var xhttp = new XMLHttpRequest();
   let url = new URL('http://localhost:8080/getTotalUserRecords');
   xhttp.open("GET", url, true);
   xhttp.send();
   xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {

         //console.log(this.responseText);
         totalRecords = JSON.parse(this.responseText);
         if (totalRecords > 6)
            document.querySelector('.pagination').style.display = "block";
         document.querySelector('.pagination').style.visibility = "hidden";
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
   getUserGroup(pageNumber);
   //console.log("total insideFn" + totalRecords);
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
