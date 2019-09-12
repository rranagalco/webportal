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

   <title>Update Account info</title>

   <jsp:include page="/htdocs/topscripts.inc" />

   <!-- CSS -->
   <link rel="stylesheet" href="css/country_select.min.css">

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

<div class="container">

<form id="SignUpForm" action="/portal/controller?formFunction=UpdateAccountInfo">
	<c:if test="${messageToUser != null}">
		<div class="alert alert-danger">
	  		<strong>Error!</strong> ${messageToUser}
		</div>
	</c:if>
			
    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
            <h5 class="form-signin-heading">Company Name</h5>
            <label for="companyname" class="sr-only">Company Name</label>
            <input name="company" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.company}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row" >
        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">First Name</h5>
            <label for="fname" class="sr-only">First Name</label>
            <input name="firstName" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.firstName}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">Mid Name</h5>
            <label for="mname" class="sr-only">Mid Name</label>
            <input name="middleInitial" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.middleInitial}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">Last Name</h5>
            <label for="lname" class="sr-only">Last Name</label>
            <input name="lastName" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.lastName}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>
    
    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">Email Address</h5>
            <label for="email" class="sr-only">Email Address</label>
            <input name="email" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.email}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">Country</h5>
            <label for="country" class="sr-only">Country</label>
            <input name="country" id="country_selector" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.country}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="form-group row" >
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">Street Address</h5>
            <label for="address" class="sr-only">Street Address</label>
            <input name="address" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.address}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row">
        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">City</h5>
            <label for="lname" class="sr-only">City</label>
            <input name="city" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.city}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>

        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">State</h5>
            <label for="state" class="sr-only">State</label>
                <select name="state" class="form-control" disabled id="state" name="state">
                    <c:if test="${(userSignUpData != null)}"><option value="">${userSignUpData.stateName}</option></c:if>
                    <c:if test="${(userSignUpData == null)}"><option value="">N/A</option></c:if>
                </select>
        </div>

        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">Postal Code</h5>
            <label for="lname" class="sr-only">Postal Code</label>
            <input name="zip" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.zip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row">
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Phone</h5>
            <label for="phone" class="sr-only">Phone</label>
            <input name="phoneWork" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneWork}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Ext</h5>
            <label for="ext" class="sr-only">Ext</label>
            <input name="phoneWorkExt" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneWorkExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row">
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3">
            <h5 class="form-signin-heading">Cell Phone</h5>
            <label for="cellphone" class="sr-only">Phone</label>
            <input name="phoneCell" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCell}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3">
            <h5 class="form-signin-heading">Ext</h5>
            <label for="cellext" class="sr-only">Ext</label>
            <input name="phoneCellExt" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCellExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row" >
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Company Phone</h5>
            <label for="companyphone" class="sr-only">Phone</label>
            <input name="phoneCompany" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCompany}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Ext</h5>
            <label for="companycellext" class="sr-only">Ext</label>
            <input name="phoneCompanyExt" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCompanyExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">Fax</h5>
            <label for="fax" class="sr-only">Fax</label>
            <input name="fax" type="text" class="form-control" disabled placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.fax}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <button class="btn btn-sm btn-primary" type="submit">SUBMIT</button>
    
	<p/><p/>
    <div><a class="form-signin-heading" href="/portal/controller?formFunction=ChangePassword">Want to change your password?</a></div>
    <p/><p/>
    <div><a class="form-signin-heading">If you want to change your address, please call us at 248 542 9090.</a></div>    

	<input type="hidden" name="formFunction" value="UpdateAccountInfo">				    

   </div> <!-- /container -->
</form>

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="./signin_files/ie10-viewport-bug-workaround.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <script src="js/country_select.min.js"></script>
    <script>
        $("#country_selector").countrySelect({
            defaultCountry: "us",
            preferredCountries: ['us', 'gb', 'ca']
        });
    </script>


    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/additional-methods.min.js"></script>
    <script>

    
    // just for the demos, avoids form submit
    jQuery.validator.setDefaults({
      success: "valid"
    });

    jQuery.validator.addMethod("zipcode", function(value, element) {
    	return this.optional(element) || /^\d{5}(?:-\d{4})?$/.test(value);
    }, "Please provide a valid zipcode.");	    
    
    $( "#SignUpForm" ).validate({
      rules: {
    	company: {
			maxlength: 36
		},          
		firstName: {
			required: true,
			maxlength: 16
		},
		middleInitial: {
			maxlength: 2
		},
		lastName: {
			required: true,
			maxlength: 22
		},

/*
		address: {
			required: true,
			maxlength: 36
		},
		address2: {
			maxlength: 36
		},
		city: {
			required: true,
			maxlength: 15
		},
		state: "required",
		zip: {
			required: true,
			zipcode: true
		},		
		phoneWork: {
			required: true,
			phoneUS: true
		},
		phoneWorkExt: {
			digits: true
		},
		phoneCompany: {
			phoneUS: true
		},
		phoneCompanyExt: {
			digits: true
		},
		phoneCell: {
			phoneUS: true
		},
		phoneCellExt: {
			digits: true
		},
		fax: {
			phoneUS: true
		},
*/

		email: {
			required: true,
			email: true,
			maxlength: 60
		}
      },

      
	  messages: {
			company : {
				maxlength: "Company name can't have more than 36 characters"
			},
			firstName : {
				required: "Please enter your first name",
				maxlength: "First name can't have more than 16 characters"
			},
			middleInitial : {
					maxlength: "Middle initial can't have more than 2 characters"
			},
			lastName : {
				required: "Please enter your first name",
				maxlength: "Last name can't have more than 22 characters"
			},

/*
			address: {
				required: "Please enter a valid address",
				maxlength: "Address can't have more than 36 characters"
			},
			address2: {
				maxlength: "Address2 can't have more than 36 characters"
			},
			city: {
				required: "Please enter a valid city",
				maxlength: "City can't have more than 15 characters"
			},			
			state: "Please select a state",
			zip: "Please enter a valid postal code",
			
			phoneWork: "Please enter a valid phone number",
			phoneWorkExt: "Please enter digits only",
			phoneCompany: "Please enter a valid phone number",
			phoneCompanyExt: "Please enter digits only",
			phoneCell: "Please enter a valid phone number",
			phoneCellExt: "Please enter digits only",

			fax: "Please enter a valid fax number",
*/
			
			email: {
				required: "Please enter a valid email address",
				maxlength: "Email address can't have more than 60 characters"
			}
	  }

      
    });
    </script>

<jsp:include page="/htdocs/tfoot.inc" />
<jsp:include page="/htdocs/bottomscripts.inc" />
</body>

<jsp:include page="ChangeButtons.jsp" />

</html>
