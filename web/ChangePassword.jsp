<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
   <meta name="description" content="">
   <meta name="author" content="">

   <title>Change Password</title>

   <jsp:include page="/htdocs/topscripts.inc" />
   <!-- CSS -->
   <link rel="stylesheet" href="css/countrySelect.min.css">

   <!-- Custom styles for this template -->
   <link href="css/signin.css" rel="stylesheet">

   <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
   <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script>
   <![endif]-->

   <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
   <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
   <![endif]-->
</head>

<body cz-shortcut-listen="true" onload="setSignInOutButtons();">

<jsp:include page="/htdocs/topnav.inc" />

<div class="middle-sec">
 <main id="panel">
 <div class="container">
 <div class="row-fluid">
 <div class="col-sm-12" style="padding-top: 45px; padding-bottom: 45px; padding-right: 0px; padding-left:0px;">

<div class="col-sm-6 col-sm-offset-3">
<form style="border: 1px solid #ccc; " id="ChangePasswordForm" action="/portal/controller?formFunction=SignUp">
    <h2 class="text-center">Change Password</h2>
        <c:if test="${messageToUser != null}">
                <div class="alert alert-danger">
                        <strong>Error!</strong> ${messageToUser}
                </div>
        </c:if>

    <div class="row">
        <div class="form-group text-center col-sm-8 col-sm-offset-2" >
           <h5 class="text-center">New Passwords require a combination of uppercase letters, numbers, and special characters.</h5>
	   <div class="input-group">
              <span class="input-group-addon">Current </span>
              <input name="oldPassword" id="oldPassword" type="password" class="form-control" placeholder="Old Password"<c:choose><c:when test="${oldPassword != null}">value="${oldPassword}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
          </div>
          <div class="input-group">
           <span class="input-group-addon">New &nbsp; &nbsp; &nbsp;</span>
            <input name="password" id="password" type="password" class="form-control" placeholder="New Password"<c:choose><c:when test="${password != null}">value="${password}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
          </div>
          <div class="input-group">
           <span class="input-group-addon">Confirm </span>
            <input name="confirmPassword"  id="confirmPassword" type="password" class="form-control" placeholder="Confirm new Password" <c:choose><c:when test="${confirmPassword != null}">value="${confirmPassword}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
          </div>
	  <div><br>
            <button class="btn btn-block btn-primary" type="submit">Save Changes</button><br><br>
          </div>
      </div>
      <div class="col-sm-2"></div>
      </div>
    </div>   
 
        <input type="hidden" name="formFunction" value="ChangePassword"/>

</form>
</div>
<div class="col-sm-3">
<div/>
</div>
</main>
</div>
</div>
</div>


    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="./signin_files/ie10-viewport-bug-workaround.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="js/countrySelect.min.js"></script>

    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/additional-methods.min.js"></script>
    <script>
    
    // just for the demos, avoids form submit
    jQuery.validator.setDefaults({
      success: "valid"
    });

    $( "#ChangePasswordForm" ).validate({
      rules: {
    	  oldPassword: {
			required: true
		},
		password: {
			required: true,
			minlength: 7,
			maxlength: 20			
		},
		confirmPassword: {
			required: true,
			minlength: 7,
			equalTo: "#password"			
		}
      },

      
	  messages: {
			password: {
				required: "Please provide a password"
			},
			password: {
				required: "Please provide a password",
				minlength: "Your password must be at least 7 characters long",
				maxlength: "Your password can't have more than than 20 characters"				
			},
			confirmPassword: {
				required: "Please provide a password",
				minlength: "Your password must be at least 7 characters long",
				maxlength: "Your password can't have more than than 20 characters",				
				equalTo: "Please enter the same password as above"
			}
	  }

      
    });
    </script>


<jsp:include page="/htdocs/tfoot.inc" />
<jsp:include page="/htdocs/bottomscripts.inc" />
</body>

<jsp:include page="ChangeButtons.jsp" />

</html>
