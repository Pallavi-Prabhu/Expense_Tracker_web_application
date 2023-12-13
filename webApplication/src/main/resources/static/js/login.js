function validateData() {
  var email = document.getElementById("logEmail").value;
  var password = document.getElementById("logPassword").value;
  if (email == null || email.trim() == "") {
    document.getElementById("errorMsg").innerHTML = "Enter Email-Id";
    return false;
  } else if (password == null || password.trim() == "") {
    document.getElementById("errorMsg").innerHTML = "Enter your Password";
    return false;
  }
  else
    return true;
}