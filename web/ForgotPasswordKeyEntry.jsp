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

<form id="ForgotPasswordKeyEntry" action="/portal/controller?formFunction=SignUp">
    <h2 class="text-center">Forgot Password</h2>

	<c:if test="${messageToUser != null}">
		<div class="alert alert-danger">
	  		<strong>Error!</strong> ${messageToUser}
		</div>
	</c:if>

    <h4 class="text-center">Please enter the Key provided to you in your email, and click Continue.</h4>
    
    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">Key</h5>
            <label for="key" class="sr-only">Key</label>
            <input name="key" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${key != null}">value="${key}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>
    
    <button class="btn btn-sm btn-primary" type="submit">Continue</button>

	<input type="hidden" name="username" <c:choose><c:when test="${username != null}">value="${username}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>/>				    
	<input type="hidden" name="formFunction" value="ForgotPasswordKeyEntry"/>				    

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
    
    $( "#ForgotPasswordKeyEntry" ).validate({
      rules: {
    	username: {
			email: true,
			maxlength: 60
		},
		email: {
			email: true,
			maxlength: 60
		}		
      },

      
	  messages: {
		    username: {
				maxlength: "User ID can't have more than 60 characters"
			},
			email: {
				maxlength: "Email address can't have more than 60 characters"
			}
	  }

      
    });
    </script>


<jsp:include page="/htdocs/tfoot.inc" />
<jsp:include page="/htdocs/bottomscripts.inc" />
</body>
</html>