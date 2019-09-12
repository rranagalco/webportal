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

   <title>Create User</title>
   
   <jsp:include page="/htdocs/topscripts.inc" />

   <!-- qqq -->
   <script src="countries.js"></script>

   <!-- CSS -->
   <!-- qqq
   <link rel="stylesheet" href="css/country_select.min.css">
   -->
 
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

<form id="SignUpForm" action="/portal/controller?formFunction=SignUp">
	<c:if test="${messageToUser != null}">
		<div class="alert alert-danger">
	  		<strong>Error!</strong> ${messageToUser}
		</div>
	</c:if>
			
    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6">
            <h5 class="form-signin-heading">Company Name</h5>
            <label for="companyname" class="sr-only">Company Name</label>
            <input name="company" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.company}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
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
        <div class="col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">Country</h5>
            <label for="country" class="sr-only">Country</label>


            <!-- qqq -->
            <!-- 
            <input name="country" id="country_selector" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.country}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
 			-->
            <select name="country" class="form-control" id="country" name="country">
            </select> 			

        </div>
    </div>

    <div class="form-group row" >
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">Street Address</h5>
            <label for="address" class="sr-only">Street Address</label>
            <input name="address" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.address}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row">
        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">City</h5>
            <label for="lname" class="sr-only">City</label>
            <input name="city" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.city}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>

        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">State</h5>
            <label for="state" class="sr-only">State</label>
	            <!-- qqq -->

                <select name="state" class="form-control" id="state" name="state">
                </select>

				<!-- 

                <select name="state" class="form-control" id="state" name="state">
                    <option value="" <c:if test="${(userSignUpData != null) && (userSignUpData.state == '')}">selected="selected"</c:if>>N/A</option>
                    <option value="AK" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'AK')}">selected="selected"</c:if>>Alaska</option>
                    <option value="AL" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'AL')}">selected="selected"</c:if>>Alabama</option>
                    <option value="AR" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'AR')}">selected="selected"</c:if>>Arkansas</option>
                    <option value="AZ" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'AZ')}">selected="selected"</c:if>>Arizona</option>
                    <option value="CA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'CA')}">selected="selected"</c:if>>California</option>
                    <option value="CO" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'CO')}">selected="selected"</c:if>>Colorado</option>
                    <option value="CT" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'CT')}">selected="selected"</c:if>>Connecticut</option>
                    <option value="DC" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'DC')}">selected="selected"</c:if>>District of Columbia</option>
                    <option value="DE" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'DE')}">selected="selected"</c:if>>Delaware</option>
                    <option value="FL" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'FL')}">selected="selected"</c:if>>Florida</option>
                    <option value="GA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'GA')}">selected="selected"</c:if>>Georgia</option>
                    <option value="HI" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'HI')}">selected="selected"</c:if>>Hawaii</option>
                    <option value="IA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'IA')}">selected="selected"</c:if>>Iowa</option>
                    <option value="ID" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'ID')}">selected="selected"</c:if>>Idaho</option>
                    <option value="IL" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'IL')}">selected="selected"</c:if>>Illinois</option>
                    <option value="IN" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'IN')}">selected="selected"</c:if>>Indiana</option>
                    <option value="KS" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'KS')}">selected="selected"</c:if>>Kansas</option>
                    <option value="KY" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'KY')}">selected="selected"</c:if>>Kentucky</option>
                    <option value="LA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'LA')}">selected="selected"</c:if>>Louisiana</option>
                    <option value="MA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'MA')}">selected="selected"</c:if>>Massachusetts</option>
                    <option value="MD" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'MD')}">selected="selected"</c:if>>Maryland</option>
                    <option value="ME" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'ME')}">selected="selected"</c:if>>Maine</option>
                    <option value="MI" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'MI')}">selected="selected"</c:if>>Michigan</option>
                    <option value="MN" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'MN')}">selected="selected"</c:if>>Minnesota</option>
                    <option value="MO" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'MO')}">selected="selected"</c:if>>Missouri</option>
                    <option value="MS" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'MS')}">selected="selected"</c:if>>Mississippi</option>
                    <option value="MT" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'MT')}">selected="selected"</c:if>>Montana</option>
                    <option value="NC" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'NC')}">selected="selected"</c:if>>North Carolina</option>
                    <option value="ND" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'ND')}">selected="selected"</c:if>>North Dakota</option>
                    <option value="NE" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'NE')}">selected="selected"</c:if>>Nebraska</option>
                    <option value="NH" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'NH')}">selected="selected"</c:if>>New Hampshire</option>
                    <option value="NJ" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'NJ')}">selected="selected"</c:if>>New Jersey</option>
                    <option value="NM" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'NM')}">selected="selected"</c:if>>New Mexico</option>
                    <option value="NV" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'NV')}">selected="selected"</c:if>>Nevada</option>
                    <option value="NY" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'NY')}">selected="selected"</c:if>>New York</option>
                    <option value="OH" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'OH')}">selected="selected"</c:if>>Ohio</option>
                    <option value="OK" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'OK')}">selected="selected"</c:if>>Oklahoma</option>
                    <option value="OR" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'OR')}">selected="selected"</c:if>>Oregon</option>
                    <option value="PA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'PA')}">selected="selected"</c:if>>Pennsylvania</option>
                    <option value="PR" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'PR')}">selected="selected"</c:if>>Puerto Rico</option>
                    <option value="RI" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'RI')}">selected="selected"</c:if>>Rhode Island</option>
                    <option value="SC" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'SC')}">selected="selected"</c:if>>South Carolina</option>
                    <option value="SD" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'SD')}">selected="selected"</c:if>>South Dakota</option>
                    <option value="TN" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'TN')}">selected="selected"</c:if>>Tennessee</option>
                    <option value="TX" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'TX')}">selected="selected"</c:if>>Texas</option>
                    <option value="UT" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'UT')}">selected="selected"</c:if>>Utah</option>
                    <option value="VA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'VA')}">selected="selected"</c:if>>Virginia</option>
                    <option value="VT" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'VT')}">selected="selected"</c:if>>Vermont</option>
                    <option value="WA" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'WA')}">selected="selected"</c:if>>Washington</option>
                    <option value="WI" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'WI')}">selected="selected"</c:if>>Wisconsin</option>
                    <option value="WV" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'WV')}">selected="selected"</c:if>>West Virginia</option>
                    <option value="WY" <c:if test="${(userSignUpData != null) && (userSignUpData.state == 'WY')}">selected="selected"</c:if>>Wyoming</option>
                </select>
 				-->                
        </div>

        <div class="form-group col-xs-12 col-sm-12 col-md-2 col-lg-2">
            <h5 class="form-signin-heading">Postal Code</h5>
            <label for="lname" class="sr-only">Postal Code</label>
            <input name="zip" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.zip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row">
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Phone</h5>
            <label for="phone" class="sr-only">Phone</label>
            <input name="phoneWork" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneWork}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Ext</h5>
            <label for="ext" class="sr-only">Ext</label>
            <input name="phoneWorkExt" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneWorkExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row">
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3">
            <h5 class="form-signin-heading">Cell Phone</h5>
            <label for="cellphone" class="sr-only">Phone</label>
            <input name="phoneCell" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCell}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3">
            <h5 class="form-signin-heading">Ext</h5>
            <label for="cellext" class="sr-only">Ext</label>
            <input name="phoneCellExt" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCellExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="row" >
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Company Phone</h5>
            <label for="companyphone" class="sr-only">Phone</label>
            <input name="phoneCompany" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCompany}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
        <div class="form-group col-xs-12 col-sm-12 col-md-3 col-lg-3" >
            <h5 class="form-signin-heading">Ext</h5>
            <label for="companycellext" class="sr-only">Ext</label>
            <input name="phoneCompanyExt" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.phoneCompanyExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">Fax</h5>
            <label for="fax" class="sr-only">Fax</label>
            <input name="fax" type="text" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.fax}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
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
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6" >
            <h5 class="form-signin-heading">Password</h5>
            <label for="password" class="sr-only">Password</label>
            <input name="password" id="password" type="password" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.password}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <div class="form-group row">
        <div class="col-xs-12 col-sm-12 col-md-6 col-lg-6"  >
            <h5 class="form-signin-heading">Re-Type Password</h5>
            <label for="retrypassword" class="sr-only">Re-Type Password</label>
            <input name="confirmPassword"  id="confirmPassword" type="password" class="form-control" placeholder="" autofocus="" <c:choose><c:when test="${userSignUpData != null}">value="${userSignUpData.password}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
        </div>
    </div>

    <button class="btn btn-sm btn-primary" type="submit">SUBMIT</button>

	<input type="hidden" name="formFunction" value="SignUp">				    

   </div> <!-- /container -->
</form>

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <script src="./signin_files/ie10-viewport-bug-workaround.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>

    <!-- qqq
    <script src="jscripts/country_select.min.js"></script>
    -->
    
    <!-- qqq -->
	<script language="javascript">
		/*
	    $("form").submit(function(){
	        alert($( "#state" ).val());
	    });
	    */
	    	
		populateCountries("country", "state"); // first parameter is id of country drop-down and second parameter is id of state drop-down
	</script>

	<c:if test="${(userSignUpData != null)}">
	<script language="javascript">
		$('#country option[value="${userSignUpData.country}"]').prop('selected', 'selected').change();            	      
		$('#state option[value="${userSignUpData.state}"]').prop('selected', 'selected').change();
	</script>			
	</c:if>

    <!-- qqq
    <script>
        $("#country_selector").countrySelect({
            defaultCountry: "us",
            preferredCountries: ['us', 'gb', 'ca']
        });
    </script>
 	-->

    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/jquery.validate.min.js"></script>
    <script src="js/additional-methods.min.js"></script>
    <script>

    
    // just for the demos, avoids form submit
    jQuery.validator.setDefaults({
      success: "valid"
    });

    jQuery.validator.addMethod("zipcode", function(value, element) {
    	if ($("#country option:selected" ).text() == "United States") {
    		return this.optional(element) || /^\d{5}(?:-\d{4})?$/.test(value);
    	} else if ($("#country option:selected" ).text() == "Canada") {
    		return this.optional(element) || /[a-zA-Z][0-9][a-zA-Z](-| |)[0-9][a-zA-Z][0-9]/.test(value);
    		// return value.match(/[a-zA-Z][0-9][a-zA-Z](-| |)[0-9][a-zA-Z][0-9]/);
    	} else {
    		if (value != "") {
                return true;
            } else {
                return false;
            }
    	}
    	
    }, "Please provide a valid zipcode.");
    
    jQuery.validator.addMethod("country_name", function(value, element) {
   		if ($("#country option:selected" ).text() != "Select Country") {
        	return true;
        } else {
            return false;
    	}
    	
    }, "Please select a country.");

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
		country: {
			required: true,
			country_name: true
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
			country: "Please select a country",
			state: "Please select a state",
			zip: "Please enter a valid postal code",
			
			phoneWork: "Please enter a valid phone number",
			phoneWorkExt: "Please enter digits only",
			phoneCompany: "Please enter a valid phone number",
			phoneCompanyExt: "Please enter digits only",
			phoneCell: "Please enter a valid phone number",
			phoneCellExt: "Please enter digits only",

			fax: "Please enter a valid fax number",
			
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

	<jsp:include page="/htdocs/tfoot.inc" />
	<jsp:include page="/htdocs/bottomscripts.inc" />

</body>
</html>