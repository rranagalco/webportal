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
                  <h1>Create Account</h1>
               </div>
               <div class="col-xs-12 col-sm-6 col-md-4">
                  <div class="stepRow row create-login-step">
                     <div class="col-xs-4">
                        <p>1</p>
                        <div>Create Login</div>
                     </div>
                     <div class="col-xs-4">
                        <p>2</p>
                        <div>Billing</div>
                     </div>
                     <div class="col-xs-4">
                        <p>3</p>
                        <div>Shipping</div>
                     </div>
                  </div>
               </div>
               <div class="col-xs-12 col-sm-6">
                  <form role="form" action="/portal/controller?formFunction=SignUp" id="SignUpForm" class="form-horizontal">
                     <h3 class="text-primary signin-header">Enter Your Email Address and Password</h3>
                     
					<c:if test="${messageToUser != null}">
						<div class="alert col-md-13 form-group alert-danger">
					  		${messageToUser}
						</div>
					</c:if>
	                     
                     <div class="form-group">
                        <label for="userName" class="control-label col-md-4 visible-md visible-lg">Email Address<i class="text-primary fa fa-envelope" aria-hidden="true"></i></label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-envelope"></i></div>
                           <input type="email" name="email" id="email" class="form-control input-lg" placeholder="Email Address" autofocus <c:choose><c:when test="${email != null}">value="${email}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group companyAccountFormGroup" <c:choose><c:when test="${empty cust_num}">style="display: none;"</c:when><c:otherwise></c:otherwise></c:choose>>
                        <label for="companyAccountNumber" class="control-label col-md-4 visible-md visible-lg">Account Number<i class="text-primary fa fa-industry" aria-hidden="true"></i></label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-industry"></i></div>
                           <input type="text" name="cust_num" id="cust_num" class="form-control input-lg" placeholder="Account Number" autofocus <c:choose><c:when test="${cust_num != null}">value="${cust_num}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="password" class="control-label col-md-4 visible-md visible-lg">Password<i class="text-primary fa fa-unlock-alt" aria-hidden="true"></i></label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-unlock-alt"></i></div>
                           <input type="password" name="password" id="password" class="form-control input-lg" placeholder="Password" <c:choose><c:when test="${password != null}">value="${password}"</c:when><c:otherwise></c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="confirmPassword" class="control-label col-md-4 visible-md visible-lg">Confirm Password<i class="text-primary fa fa-unlock-alt" aria-hidden="true"></i></label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-unlock-alt"></i></div>
                           <input type="password" name="confirmPassword" id="confirmPassword" class="form-control input-lg" placeholder="Confirm Password" <c:choose><c:when test="${password != null}">value="${password}"</c:when><c:otherwise>disabled</c:otherwise></c:choose>>
                        </div>
                     </div>
                     

<%@page import="java.awt.image.BufferedImage"%>
<%@page import="javax.imageio.ImageIO"%>
<%@page import="java.io.*"%>

<%@page import="nl.captcha.Captcha"%>
<%@page import="nl.captcha.backgrounds.GradiatedBackgroundProducer"%>
<%@page import="nl.captcha.servlet.CaptchaServletUtil"%>

<%
	int _width = 200;
	int _height = 50;

	Captcha captcha = new Captcha.Builder(_width, _height)
		.addText()
		.addBackground(new GradiatedBackgroundProducer())
		.gimp()
		.addNoise()
		.addBorder()
		.build();

	// CaptchaServletUtil.writeImage(response, captcha.getImage());
	request.getSession().setAttribute(nl.captcha.Captcha.NAME, captcha);

	BufferedImage bImage = captcha.getImage();
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	ImageIO.write( bImage, "png", baos );
	baos.flush();
	byte[] imageInByteArray = baos.toByteArray();
	baos.close();
	String b64 = javax.xml.bind.DatatypeConverter.printBase64Binary(imageInByteArray);
	
	// <img src="https://www.galcotv.com/portal/controller/formFunction=GetCaptcha"><br />
%>

                     <div class="form-group">
                        <label class="control-label col-md-4 visible-md visible-lg">    </label>                     
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
						   <img src="data:image/png;base64, <%=b64%>" alt="Visruth.jpg not found" />
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="name" class="control-label col-md-4 visible-md visible-lg">Please enter text shown in the above image here</label>
                        <div class="col-xs-12 col-sm-11 col-md-7 col-lg-8 input-group">
                           <div class="input-group-addon hidden-md hidden-lg"><i class="text-primary fa fa-unlock-alt"></i></div>
                           <input type="text" name="answer" id="answer" class="form-control input-lg">
                        </div>
                     </div>
                     
                     
                     <div class="form-group">
                        <div class="col-xs-12 col-sm-11 col-md-7 col-md-offset-4 col-lg-8 input-group">
                           <button id="createAccountButton" class="btn btn-block btn-primary" type="submit">Create Account</button>
                        </div>
                     </div>
					 <input type="hidden" name="formFunction" value="SignUp">	
                  </form>
               </div>
               <div class="col-xs-12 col-sm-6 col-md-5 col-lg-5 col-lg-offset-1 create-account">
                  <h3 class="text-primary create-account-header">Benefits of Creating an Account</h3>
                  <p>When you create an account with us you can:</p>
                  <ul>
                     <li>Take advantage of faster ordering options</li>
                     <li>View your customized account information</li>
                     <li>Track your order status and order history</li>
                  </ul>
               </div>
            </div>
         </div>
      </main>
   </div>
   <div id="accountQuestionHolder" class="hide">
      <div id="accountQuestion" class="container">
         <h1>Does your company/organization have an account number with Galco?</h1>
         <div class="row">
            <div class="col-xs-6 col-sm-3">
               <input type="radio" name="hasAccount" value="yes" id="hasAccountYes">
               <label for="hasAccountYes">Yes</label>
               <br>
               <input type="radio" name="hasAccount" value="no" id="hasAccountNo">
               <label for="hasAccountNo">No</label>
               <br>
            </div>
            <div class="col-xs-6 col-sm-9">
               <label for="modalAccountInput">Enter Account Number</label>
               <input type="text" name="modalAccountInput" id="modalAccountInput">
            </div>
         </div>
         <button type="button" class="btn btn-primary continueButton">Continue</button>
      </div>
   </div>
   <script>
   jQuery(document).ready(function($) {
      var $accountModal;
      var lastPass = $("#password").val();
      $("#password").on('change keyup', function(event) {
         if ($(this).val() !== lastPass) {
            console.log((($(this).val().length > 0) !== true));
            lastPass = $(this).val();
            $("#confirmPassword").prop('disabled', (($(this).val().length > 0) !== true)).val("");
         }
      });
      $("#confirmPassword").on('change keyup', function(event) {
         if ($(this).val() === $("#password").val()) {
            $(this).addClass('verified');
         } else {
            $(this).removeClass('verified');
         }
      });

<c:choose>
<c:when test="${cust_num == null}">

      $("body").on('click', '#accountQuestion button.continueButton', function(event) {
         $.featherlight.current().close();
      });

      $accountModal = $("#accountQuestion").detach();
      $.featherlight($accountModal, {
         beforeClose : function() {
            var hasCompanyAccount = $("input[name=hasAccount]:checked").val();
            console.log(hasCompanyAccount);
            if (hasCompanyAccount && hasCompanyAccount === "yes") {
               $(".companyAccountFormGroup").show().find("input").val($("#modalAccountInput").val());
            }
         },
         afterClose : function() {
            $accountModal.appendTo( $("#accountQuestionHolder") );
         }
      });
      
</c:when>
</c:choose>



      
      
   });

   </script>

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
    
    $( "#SignUpForm" ).validate({
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
		},
		confirmPassword: {
			required: true,
			minlength: 7,
			equalTo: "#password"			
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
    
    <script>

	function pausecomp(millis) {
    	var date = new Date();
    	var curDate = null;
    	do { 
    		curDate = new Date(); 
    	} while(curDate-date < millis);
	}

	$("#createAccountButton").on('click', function() {
		if ($("#SignUpForm").valid() == true) {
			$("#createAccountButton").prop('disabled', true);
			// pausecomp(5000);
			// alert("submitting"); 
			$("#SignUpForm").submit();
	    	return false;
		}
	});    
    
    </script>
	
</html>

