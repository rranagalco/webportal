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

   <title>Reset forgotten password</title>
   
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

<body cz-shortcut-listen="true">

<jsp:include page="/htdocs/topnav.inc" />

<div class="container">

<form id="ForgotPasswordChangePasswordForm" action="/portal/controller?formFunction=SignUp">
    <h2 class="text-center">Change Password</h2>

	<h4 class="text-center">Please type your new password, confirm it, and press Submit.</h4>

	<c:if test="${messageToUser != null}">
		<div class="alert alert-danger">
	  		<strong>Error!</strong> ${messageToUser}
		</div>
	</c:if>
			
    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">New Password</h5>
            <label for="password" class="sr-only">New Password</label>
            <input name="password" id="password" type="password" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${password != null}">value="${password}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6"  >
            <h5 class="form-signin-heading">Re-Type New Password</h5>
            <label for="retrypassword" class="sr-only">Re-Type New Password</label>
            <input name="confirmPassword"  id="confirmPassword" type="password" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${confirmPassword != null}">value="${confirmPassword}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <button class="btn btn-sm btn-primary" type="submit">SUBMIT</button>

	<input type="hidden" name="username" <c:choose><c:when test="${username != null}">value="${username}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>/>				    
	<input type="hidden" name="key" <c:choose><c:when test="${key != null}">value="${key}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>/>				    
		
	<input type="hidden" name="formFunction" value="ForgotPasswordChangePassword"/>				    

</form>

</div> <!-- /container -->

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="./signin_files/ie10-viewport-bug-workaround.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="js/countrySelect.min.js"></script>

    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/additional-methods.min.js"></script>
    <script>
    
    $( "#ForgotPasswordChangePasswordForm" ).validate({
      rules: {
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
</html>