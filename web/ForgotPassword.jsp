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

<div class="middle-sec">
 <main id="panel">
 <div class="container">
 <div class="row-fluid">
 <div class="col-sm-12" style="padding-top: 45px; padding-bottom: 45px; padding-right: 0px; padding-left:0px;">

<div class="col-sm-6 col-sm-offset-3">

<form id="ForgotPasswordUsernameSelectionForm" action="/portal/controller?formFunction=SignUp" style="border: 1px solid #ccc">

    <h2 class="text-center">Forgot Password</h2>

	<c:if test="${messageToUser != null}">
		<div class="alert alert-danger">
	  		<strong>Error!</strong> ${messageToUser}
		</div>
	</c:if>

 <div class="row">
        <div class="form-group text-center col-sm-8 col-sm-offset-2" >
          <h5 class="text-center">Please enter User ID, or Email Address, and click Continue.</h5>
           <div class="input-group">
            <span class="input-group-addon text-center"><i class="glyphicon glyphicon-user"></i> </span>
            <input name="username" type="text" class="form-control" placeholder="user id" autofocus="" <c:choose><c:when test="${username != null}">value="${username}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
          </div>
          <div class="input-group">
            <span class="input-group-addon text-center"><i class="glyphicon glyphicon-envelope"></i> </span>
	        <input name="email" type="text" class="form-control" placeholder="email" autofocus="" <c:choose><c:when test="${email != null}">value="${email}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
          </div>
          <div><br>
            <button class="btn btn-block btn-primary btn-block" type="submit">Continue</button><br>           
          </div>
      </div>
      <div class="col-sm-2"></div>
      </div>
    </div>


	<input type="hidden" name="formFunction" value="ForgotPassword"/>				    

</form>

</div>
<div class="col-sm-3">
</>
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
    
    $( "#ForgotPasswordUsernameSelectionForm" ).validate({
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
