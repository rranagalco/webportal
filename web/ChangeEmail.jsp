<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | Sign in to account page">
   <meta name="author" content="Nicholas Cirullo">
   <title>Create Account | Sign In</title>
   <!--#include virtual="/topscripts.inc"-->
   <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
   <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
   <!-- <script src="./signin_files/ie-emulation-modes-warning.js"></script> -->
   <style>

   .featherlight {
      background: rgba(0, 0, 0, 0.8);
   }

   </style>
</head>

<jsp:include page="/htdocs/topscripts.inc" />
<jsp:include page="/htdocs/topnav.inc" />

<body cz-shortcut-listen="true" class="create-account">
   <!--#include virtual="/topnav.inc"-->
   <div class="middle-sec">
      <main id="panel">
         <div class="container">
            <div class="row">
               <div class="col-xs-12 col-sm-6 col-md-8">
                  <h1>Change Email</h1>
               </div>
               <div class="col-xs-12 col-sm-6">
                  <form role="form" action="/portal/controller?formFunction=ChangeEmail" id="changeEmailForm" class="form-horizontal">
                     <h3 class="text-primary signin-header">Enter Your Email Address and Password</h3>
                     
					<c:if test="${messageToUser != null}">
						<div class="alert col-md-13 form-group alert-danger">
					  		${messageToUser}
						</div>
					</c:if>
					
					 <p>Note: Changing your email will change the user id you use to log into Galco Website. If you change your email, you need to confirm your new email.</p>
	                     
                     <div class="form-group">
                        <label for="userName" class="control-label col-md-4 visible-md visible-lg">New Email Address<i class="text-primary fa fa-envelope" aria-hidden="true"></i></label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-envelope"></i></div>
                           <input type="email" name="email" id="email" class="form-control input-lg" placeholder="Email Address" autofocus <c:choose><c:when test="${email != null}">value="${email}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="password" class="control-label col-md-4 visible-md visible-lg">Current Password<i class="text-primary fa fa-unlock-alt" aria-hidden="true"></i></label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-unlock-alt"></i></div>
                           <input type="password" name="password" id="password" class="form-control input-lg" placeholder="Password" <c:choose><c:when test="${password != null}">value="${password}"</c:when><c:otherwise></c:otherwise></c:choose>>
                        </div>
                     </div>
                     
                     <div class="form-group">
                        <div class="col-xs-12 col-sm-11 col-md-7 col-md-offset-4 col-lg-8 input-group">
                           <button id="changeEmailButton" class="btn btn-block btn-primary" type="submit">Change Email</button>
                        </div>
                     </div>
					 <input type="hidden" name="formFunction" value="ChangeEmail">	
                  </form>
               </div>
            </div>
         </div>
      </main>
   </div>

   <!--start of btm sec-->
   <!--#include virtual="/tfoot.inc"-->
   <!--end of btm sec-->
   <!--#include virtual="/bottomscripts.inc"-->
</body>

	<jsp:include page="/htdocs/tfoot.inc" />
	<jsp:include page="/htdocs/bottomscripts.inc" />
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/additional-methods.min.js"></script>
    
    <script>
    
    $( "#changeEmailForm" ).validate({
      rules: {
		email: {
			required: true,
			email: true,
			maxlength: 60
		},
		password: {
			required: true,
			minlength: 7,
			maxlength: 20			
		}
      },

	  messages: {			
			email: {
				required: "Please enter a valid email address",
				maxlength: "Email address can't have more than 60 characters"
			},
			password: {
				required: "Please provide a password",
				minlength: "Your password must be at least 7 characters long",
				maxlength: "Your password can't have more than than 20 characters"				
			}
	  }

      
    });
    </script>
    
    <script>
	$("#changeEmailButton").on('click', function() {
		if ($("#changeEmailForm").valid() == true) {
			$("#changeEmailButton").prop('disabled', true);
			// pausecomp(5000);
			// alert("submitting"); 
			$("#changeEmailForm").submit();
	    	return false;
		}
	});    
    </script>
	
</html>

