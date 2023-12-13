
function validateForm() {
  var email = document.getElementById("signEmail").value;
  var name = document.getElementById("signFname").value;
  var lname = document.getElementById("signLname").value;
  var phnumber = document.getElementById("signNumber").value;
  var password = document.getElementById("signPassword").value;
  var cpassword = document.getElementById("signCpassword").value;
  const emailRegex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;

  if (email == null || email.trim() == "") {
    document.getElementById("errorMsg").innerHTML = "Enter Email-Id";
    return false;
  }
  else if (!emailRegex.test(email)) {
    document.getElementById("errorMsg").innerHTML = "Enter Valid Email-id";
    return false;
  }
  else if (name == null || name.trim() == "") {
    document.getElementById("errorMsg").innerHTML = "Enter First Name";
    return false;
  }
  //  else if (name.length > 15) {
  //    document.getElementById("errorMsg").innerHTML = "Enter Valid First Name";
  //    return false;
  //  }
  //  else if (lname.length > 15) {
  //    document.getElementById("errorMsg").innerHTML = "Enter Valid Last Name";
  //    return false;
  //  }
  else if (phnumber == null || phnumber.trim() == "" || phnumber.length != 10) {
    document.getElementById("errorMsg").innerHTML = "Enter Valid Phone number";
    return false;
  }
  else if (password == null || password.trim() == "") {
    document.getElementById("errorMsg").innerHTML = "Enter Password";
    return false;
  }
  else if (password != cpassword) {
    document.getElementById("errorMsg").innerHTML = "Passwords do not match";
    return false;
  }
  else
    return true;

}