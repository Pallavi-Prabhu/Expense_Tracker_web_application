document.getElementById('buttonLoad').style.visibility = 'hidden';
function backToHome() {
    //console.log("backTohome")
    const xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/expenseDetailPage");
    xhttp.send();
    xhttp.onreadystatechange = function () {
        if (this.readyState == 4 && this.status == 200) {
            window.location.href = "/expenseDetailPage";
        }
    }
}

async function displayUser() {
//    console.log("displayUser")
    return new Promise((resolve, reject) => {
        var xhttp = new XMLHttpRequest();
        let url = new URL("http://localhost:8080/expense-userList");
        xhttp.open("GET", url, true);
        xhttp.send();
        xhttp.onreadystatechange = function () {
            if (this.readyState == 4 && this.status == 200) {
                // console.log(this.responseText);
                var paidName = document.getElementById("paidByName")
                var sel = document.getElementById("payer");//$("#"),$(".")
                var optionsData = JSON.parse(this.responseText);
                for (var key in optionsData.usersList) {
                    var value = optionsData.usersList[key];

                    const optionElement = document.createElement("option");
                    optionElement.value = key;
                    optionElement.text = value;

                    if (key === paidName.value || optionsData.userId == key) {
                        optionElement.selected = true;
                    }

                    sel.appendChild(optionElement);
                }


                var ulEle = document.getElementById("ulId");
                for (var key in optionsData.usersList) {
                    var liElement = document.createElement("li");

                    var checkboxElement = document.createElement("input");
                    checkboxElement.setAttribute("type", "checkbox");
                    checkboxElement.setAttribute("checked", "");
                    checkboxElement.setAttribute("id", key); // Use the value as ID
                    checkboxElement.setAttribute("name", "On" + key);

                    var labelElement = document.createElement("label");
                    labelElement.setAttribute("for", key); // Use the value as the for attribute
                    labelElement.textContent = optionsData.usersList[key];

                    var colonSpanElement = document.createElement("span");
                    colonSpanElement.className = "person-label";
                    //colonSpanElement.textContent = ":";

                    var hiddenInputElement = document.createElement("input");
                    hiddenInputElement.setAttribute("type", "hidden");
                    hiddenInputElement.setAttribute("class", "person-label-input");
                    hiddenInputElement.setAttribute("name", key);
                    // hiddenInputElement.setAttribute("value", "");

                    var numberInputElement = document.createElement("input");
                    numberInputElement.setAttribute("type", "number");
                    numberInputElement.setAttribute("class", "person-input");
                    numberInputElement.setAttribute("min", "0");
                    numberInputElement.setAttribute("step", "0.01");
                    numberInputElement.setAttribute("max", "10000");
                    numberInputElement.setAttribute("name", "personInput" + key);

                    liElement.appendChild(checkboxElement);
                    liElement.appendChild(labelElement);
                    liElement.appendChild(hiddenInputElement);
                    liElement.appendChild(colonSpanElement);
                    liElement.appendChild(numberInputElement);

                    ulEle.appendChild(liElement);

                };

                resolve();
            }


        }

    });

}

document.addEventListener("DOMContentLoaded", function () {
    initialize();
});



var splitTypeSelect, personLabels, personInputs, personLabelsInput, checkboxes, amountInput, error, totalAmount;
async function initialize() {
    await displayUser();
    splitTypeSelect = document.getElementById('splitType');
    personLabels = document.querySelectorAll('.person-label');
    //console.log(personLabels)
    personLabelsInput = document.querySelectorAll('.person-label-input');
    //console.log(personLabelsInput)
    personInputs = document.querySelectorAll('.person-input');
    //console.log(personInputs)
    checkboxes = document.querySelectorAll('input[type="checkbox"]');
    amountInput = document.getElementById('amount');
    error = document.getElementById("error");
    totalAmount = 0;

    splitTypeSelect.addEventListener('change', function () {
       // console.log("type of split")
        if (splitTypeSelect.value === 'custom' || splitTypeSelect.value === 'percentage') {
            personLabels.forEach((label, index) => {
                //console.log("custom or percent Split")
                if (checkboxes[index].checked) {
                    label.textContent = '';
                    personLabelsInput.forEach((input, inputIndex) => {
                        if (inputIndex === index) {
                            input.value = '';
                        }
                    });
                    personInputs[index].style.display = 'inline-block';
                }
            });
        } else { //for equal split no textbox and div
            personLabels.forEach((label, index) => {
                label.textContent = '';
                personLabelsInput.forEach((input, inputIndex) => {
                    if (inputIndex === index) {
                        input.value = '';
                    }
                });
                personInputs[index].style.display = 'none';
            });
        }
        if (splitTypeSelect.value === 'percentage')
            updatePercentageLabels();
        else if (splitTypeSelect.value === 'custom')
            updateCustomLabels();
        else
            updateEqualSplitLabels();
    });

    checkboxes.forEach(checkbox => {  //everytime  checkbox is changed
        checkbox.addEventListener('change', function () {
            if (splitTypeSelect.value === 'percentage')
                updatePercentageLabels();
            else if (splitTypeSelect.value === 'custom')
                updateCustomLabels();
            else
                updateEqualSplitLabels();
        })
    });

    personInputs.forEach(input => {  //everytime input box is updated
        input.addEventListener('input', function () {
            if (splitTypeSelect.value === 'percentage')
                updatePercentageLabels();
            else
                updateCustomLabels();
        });
    });



    function updateEqualSplitLabels() {
        //console.log("Equal split function")
        const numSelected = Array.from(checkboxes).reduce((count, checkbox) => count + (checkbox.checked ? 1 : 0), 0);
        const equalSplit = (amountInput.value / numSelected).toFixed(2);
        personLabels.forEach((label, index) => {
            if (checkboxes[index].checked) {
                label.textContent = `Rs:${equalSplit}`;
                personLabelsInput[index].value = `${equalSplit}`;
            } else {
                label.textContent = '';
                personLabelsInput.forEach((input, inputIndex) => {
                    if (inputIndex === index) {
                        input.value = '';
                    }
                });
            }
        });
    }


    function updatePercentageLabels() {
        //console.log("percent split function")
        totalAmount = parseFloat(amountInput.value);
        const percentages = Array.from(personInputs).map(input => parseFloat(input.value) || 0);
        //console.log(percentages)
        var totalPercentage = percentages.reduce((sum, percentage) => sum + percentage, 0);
        if (totalPercentage <= 100) {
            error.innerHTML = ""
           // console.log("percent split function1")
            personLabels.forEach((label, index) => {
               // console.log("percent split function2")
                if (checkboxes[index].checked) {
                    var splitAmount = (totalAmount * (percentages[index] / 100)).toFixed(2);
                    if (isNaN(splitAmount)) splitAmount = 0.00;
                    personInputs[index].style.display = 'inline-block';
                    label.textContent = `Rs: ${splitAmount}`;
                    personLabelsInput[index].value = `${splitAmount}`;
                } else {
                    //console.log("percent split function3")
                    personInputs[index].value = 0;
                    label.textContent = '';
                    personLabelsInput.forEach((input, inputIndex) => {
                        if (inputIndex === index) {
                            input.value = '';
                        }
                    });
                    personInputs[index].style.display = 'none';
                }
            });
        } else {
            //console.log("percent split function4")
            personLabels.forEach((label, index) => {
                if (checkboxes[index].checked) {
                   // console.log("percent split function5")
                    error.style.color = "red";
                    error.innerHTML = "Total percentage exceeds 100%"
                }
                else {
                    //console.log("percent split function6")
                    totalPercentage = totalPercentage - percentages[index];
                    //console.log(totalPercentage)
                    personInputs[index].value = 0;
                    if (totalPercentage <= 100) {
                        error.innerHTML = ""
                        label.textContent = '';
                        personLabelsInput.forEach((input, inputIndex) => {
                            if (inputIndex === index) {
                                input.value = '';
                            }
                        });
                        personInputs[index].style.display = 'none';

                    }
                }

            });
        }
    }

    function updateCustomLabels() {
        totalAmount = parseFloat(amountInput.value);
        const customAmounts = Array.from(personInputs).map(input => parseFloat(input.value) || 0);
        const totalCustomAmount = customAmounts.reduce((sum, amount) => sum + amount, 0);

        if (totalCustomAmount <= totalAmount) {
            error.innerHTML = '';
            //console.log("customsplit1")
            personLabels.forEach((label, index) => {
                if (checkboxes[index].checked) {
                    //console.log("customsplit2")
                    personInputs[index].style.display = 'inline-block';
                    label.textContent = `Rs: ${customAmounts[index].toFixed(2)}`;
                    personLabelsInput[index].value = `${customAmounts[index].toFixed(2)}`
                } else {
                    //console.log("customsplit3")
                    // personInputs[index].value = 0;
                    label.textContent = "";
                    personLabelsInput.forEach((input, inputIndex) => {
                        if (inputIndex === index) {
                            input.value = '';
                        }
                    });
                    personInputs[index].style.display = 'none';
                }
            });
        } else {
           // console.log("customsplit4")
            personLabels.forEach((label, index) => {
                if (checkboxes[index].checked) {
                    //console.log("customsplit5")
                    error.style.color = "red";
                    error.innerHTML = "Total custom amount exceeds total expense "
                }
                else {
                   // console.log("customsplit6")
                    personInputs[index].value = 0;
                    label.textContent = "";
                    personLabelsInput.forEach((input, inputIndex) => {
                        if (inputIndex === index) {
                            input.value = '';
                        }
                    });
                    error.innerHTML = '';
                    personInputs[index].style.display = 'none';
                }
            });
        }

    }

    amountInput.addEventListener('input', function () {
       // console.log("amount calculation")
        if (splitTypeSelect.value === 'equal') {
            updateEqualSplitLabels();
        } else if (splitTypeSelect.value === 'percentage') {
            updatePercentageLabels();
        }
    });
}
var error1 = document.getElementById("error1");

function validate() {
    hasError = false;
    error1.innerHTML = '';
    finalAmount = 0;

    if (splitTypeSelect.value === 'percentage' || splitTypeSelect.value === 'custom') {

        personLabels.forEach((label, index) => {
            //console.log("here");
            if (checkboxes[index].checked) {
                const inputValue = parseFloat(personInputs[index].value);
                //console.log("input value" + inputValue)
                if (!isNaN(inputValue)) {
                    finalAmount += inputValue;
                }

            }
        });

       // console.log("Final Amount:", finalAmount);

        personLabels.forEach((label, index) => {
           // console.log("here");
            if (checkboxes[index].checked) {
                if (personInputs[index].value == 0) {
                   // console.log("if value is 0");
                    error1.style.color = "red";
                    error1.innerHTML = "Enter money for all checked individuals";
                    hasError = true;
                }
            }

        });
    }
    const personLabelsArray = Array.from(personLabels);
    const allLabelsAreEmpty = personLabelsArray.every(label => label.textContent === '');

    if (allLabelsAreEmpty) {
       // console.log('All labels are empty.');
        error1.style.color = "red";
        error1.innerHTML = "Enter the changed amount";
        hasError = true;

    }

    // checkboxes.forEach(checkbox => {
    //     if (!checkbox.checked) {
    //         error.style.color = "red";
    //         error.innerHTML = "Select individuals";
    //         hasError = true;
    //     }
    // });
    var selectedVal = document.getElementById("payer").value;
    var selectedCount = 0;
    var selectedVal2 = 0;
    personLabels.forEach((label, index) => {
        if (checkboxes[index].checked) {
            selectedCount++;
            selectedVal2 = checkboxes[index].id;
           // console.log(selectedVal2);
        }
    });
    if (selectedCount == 1 && selectedVal == selectedVal2) {
        error1.style.color = "red";
        error1.innerHTML = "Select other individual";
        hasError = true;
    }


    if ((finalAmount != totalAmount && splitTypeSelect.value === 'custom') ||
        (finalAmount != 100 && splitTypeSelect.value === 'percentage')) {
        error1.style.color = "red";
        error1.innerHTML = "Entered amount is not equal to share";
        hasError = true;
    }
    else if (error.innerHTML != '' || error1.innerHTML != '') {
        //console.log("has error")
        hasError = true;
    }
    if (hasError) {
        return false;
    }
    else
    {
    document.getElementById('buttonLoad').style.visibility = 'visible';
    return true;
    }




}





