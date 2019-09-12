<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | Payment and Billing">
   <meta name="author" content="Nicholas Cirullo">
   <title>Create Account | Payment and Billing</title>
   <!--#include virtual="/topscripts.inc"-->
   <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
   <!--[if lt IE 9]><script src="../../assets/js/ie8-responsive-file-warning.js"></script><![endif]-->
   <!-- <script src="./signin_files/ie-emulation-modes-warning.js"></script> -->
</head>

<jsp:include page="/htdocs/topscripts.inc" />
<jsp:include page="/htdocs/topnav.inc" />
<script src="countries.js"></script>
<script src="address-verification-portal.js"></script>

<style>
.form-group.subbrequired .control-label:before {
  content:"*";
  color:red;
}
</style>

<body cz-shortcut-listen="true" class="create-account create-billing">
   <!--#include virtual="/topnav.inc"-->
   <div class="middle-sec">
      <main id="panel">
         <div class="container">
            <div class="row">
               <div class="col-xs-12 col-sm-6 col-md-8">
                  <h1>Create Account</h1>
               </div>
               <div class="col-xs-12 col-sm-6 col-md-4">
                  <div class="stepRow row billing">
                     <div class="col-xs-4">
                        <p>1</p>
                        <div>Create Login</div>
                     </div>
                     <div class="col-xs-4">
                        <p>2</p>
                        <div>Shipping</div>
                     </div>
                     <div class="col-xs-4">
                        <p>3</p>
                        <div>Billing</div>
                     </div>
                  </div>
               </div>
               <div class="col-xs-12 col-sm-6">
                  <form role="form" action="/portal/controller?formFunction=SignUpBill" id="SignUpBillForm" class="form-horizontal">
                     <h3 class="text-primary signin-header">Enter Payment &amp; Billing Information</h3>
                     
					 <c:if test="${messageToUser != null}">
						<div class="alert col-md-13 form-group alert-danger">
					  		<strong>Error!</strong> ${messageToUser}
						</div>
					 </c:if>
					                     
                     <div class="form-group">
                        <label for="company" class="control-label col-md-4 visible-md visible-lg">Company</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <input type="text" name="company" id="company" class="form-control input-lg" placeholder="Company" autofocus <c:choose><c:when test="${company != null}">value="${company}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                     </div>                     
                     <div class="form-group subbrequired inline-form-control">
                        <label for="firstName" class="control-label col-md-4 visible-md visible-lg">First Name</label>
                        
                        <div class="col-xs-9 col-md-6">
                            <div class="input-group">
                                <input type="text" name="firstName" id="billNameFirst" class="form-control input-lg" placeholder="First Name" <c:choose><c:when test="${firstName != null}">value="${firstName}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                        
                        <div class="col-xs-3 col-sm-2">
                            <div class="input-group">
                                <input type="text" name="middleInitial" id="billNameMiddle" class="form-control input-lg force-placeholder" placeholder="MI" <c:choose><c:when test="${middleInitial != null}">value="${middleInitial}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                        
                     </div>
                     <div class="form-group subbrequired">
                        <label for="lastName" class="control-label col-md-4 visible-md visible-lg">Last Name</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <input type="text" name="lastName" id="billNameLast" class="form-control input-lg" placeholder="Last Name" <c:choose><c:when test="${lastName != null}">value="${lastName}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="address" class="control-label col-md-4 visible-md visible-lg">Address 1</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <input type="text" name="address" id="address" class="form-control input-lg" placeholder="Address 1" <c:choose><c:when test="${address != null}">value="${address}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="address2" class="control-label col-md-4 visible-md visible-lg">Address 2</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <input type="text" name="address2" id="address2" class="form-control input-lg" placeholder="Address 2" <c:choose><c:when test="${address2 != null}">value="${address2}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="city" class="control-label col-md-4 visible-md visible-lg">City</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <input type="text" name="city" id="city" class="form-control input-lg" placeholder="City" <c:choose><c:when test="${city != null}">value="${city}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                     </div>
                     
                     <div class="form-group">
                        <label for="country" class="control-label col-md-4 visible-md visible-lg">Country</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <select name="country" id="country" class="form-control">
                                </select>
                            </div>
                        </div>
                     </div>
                     
                     <div class="form-group subbrequired">
                        <label for="state" class="control-label col-md-4 visible-md visible-lg">State/Province</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <select name="state" id="state" class="form-control">
                                </select>
                            </div>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="zip" class="control-label col-md-4 visible-md visible-lg">Postal Code</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <input type="text" name="zip" id="zip" class="form-control input-lg" placeholder="Postal Code" <c:choose><c:when test="${zip != null}">value="${zip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                     </div>
                     
                     <div class="form-group subbrequired inline-form-control">
                        <label for="phoneWork" class="control-label col-md-4 visible-md visible-lg">Phone</label>

                        <div class="col-xs-9 col-md-6">
                            <div class="input-group">
                                <input type="text" name="phoneWork" id="phoneWork" class="form-control input-lg" placeholder="Phone" <c:choose><c:when test="${phoneWork != null}">value="${phoneWork}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                        
                        <div class="col-xs-3 col-sm-2">
                            <div class="input-group">
                                <input type="text" name="phoneWorkExt" id="phoneWorkExt" class="form-control input-lg force-placeholder" placeholder="Ext." <c:choose><c:when test="${phoneWorkExt != null}">value="${phoneWorkExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>

                     </div>
                     <div class="form-group inline-form-control">
                        <label for="phoneCell" class="control-label col-md-4 visible-md visible-lg">Mobile Phone</label>

                        <div class="col-xs-9 col-md-6">
                            <div class="input-group">
                                <input type="text" name="phoneCell" id="phoneCell" class="form-control input-lg" placeholder="Mobile Phone" <c:choose><c:when test="${phoneCell != null}">value="${phoneCell}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                        
                        <div class="col-xs-3 col-sm-2">
                            <div class="input-group">
                                <input type="text" name="phoneCellExt" id="phoneCellExt" class="form-control input-lg force-placeholder" placeholder="Ext." <c:choose><c:when test="${phoneCellExt != null}">value="${phoneCellExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>

                     </div>
                     <div class="form-group inline-form-control">
                        <label for="phoneCompany" class="control-label col-md-4 visible-md visible-lg">Company Phone</label>

                        <div class="col-xs-9 col-md-6">
                            <div class="input-group">
                                <input type="text" name="phoneCompany" id="companyphone" class="form-control input-lg" placeholder="Company Phone" <c:choose><c:when test="${phoneCompany != null}">value="${phoneCompany}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                        
                        <div class="col-xs-3 col-sm-2">
                            <div class="input-group">
                                <input type="text" name="phoneCompanyExt" id="companyphoneExt" class="form-control input-lg force-placeholder" placeholder="Ext." <c:choose><c:when test="${phoneCompanyExt != null}">value="${phoneCompanyExt}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>

                     </div>
                     <div class="form-group inline-form-control">
                        <label for="fax" class="control-label col-md-4 visible-md visible-lg">Fax</label>
                        <div class="col-xs-12 col-sm-11 col-md-8">
                            <div class="col-md-12 input-group">
                                <input type="text" name="fax" id="fax" class="form-control input-lg" placeholder="Fax" <c:choose><c:when test="${fax != null}">value="${fax}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                            </div>
                        </div>
                     </div>
                     <div class="row requiredFieldNoteRow">
                        <div class="col-xs-12 col-md-9 col-md-offset-3">
                           <span class="requiredFieldNote">This indicates a required field.</span>
                           <br>
                        </div>
                     </div>
                     <div class="form-group">
                        <div class="col-xs-12 col-sm-11 col-md-7 col-md-offset-4 col-lg-8 input-group">
                           <button id="createAccountButton" class="btn btn-block btn-primary">Create Account</button>
                        </div>
                     </div>

					 <input type="hidden" name="formFunction" value="SignUpBill">
                     <input type="hidden" name="cust_num" <c:choose><c:when test="${cust_num != null}">value="${cust_num}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
                     <input type="hidden" name="email" <c:choose><c:when test="${email != null}">value="${email}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
                     <input type="hidden" name="password" <c:choose><c:when test="${password != null}">value="${password}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
                     
                  </form>
               </div>
            </div>
         </div>
      </main>
   </div>

	<script language="javascript">
		populateCountries("country", "state"); // first parameter is id of country drop-down and second parameter is id of state drop-down
	</script>

	<c:if test="${(address != null)}">
	<script language="javascript">
		$('#country option[value="${country}"]').prop('selected', 'selected').change();            	      
		$('#state option[value="${state}"]').prop('selected', 'selected').change();
	</script>			
	</c:if>	

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

    $( "#SignUpBillForm" ).validate({
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

			fax: "Please enter a valid fax number"
	  }

      
    });
    </script>
    
    
   <script>
   
	$("#createAccountButton").on('click', function() {
		if ($("#SignUpBillForm").valid() == true) {
			$("#createAccountButton").prop('disabled', true);
			callAddressModal();
	    	return false;
		}
	});

	var callAddressModal = function() {	    	
	   var formData = {
	      name    : document.getElementById("company"),
	      addr1   : document.getElementById("address"),
	      addr2   : document.getElementById("address2"),
	      city    : document.getElementById("city"),
	      state   : document.getElementById("state"),
	      zip     : document.getElementById("zip"),
	      country : document.getElementById("country"),
	      callback : function( verifyResult ) {
	         if(typeof verifyResult === "string") {
	         	if ($("#SignUpBillForm").valid()) {
					if ((verifyResult == "corrected") ||
						(verifyResult == "entered"  )    ) {
						// alert("Ready to submit 333. " + verifyResult);
						
						$("#SignUpBillForm").submit();
					} else if (verifyResult == "edit") {
						// alert("Some more editing. " + verifyResult); 
						$("#createAccountButton").prop('disabled', false);					
					} else {
						$("#SignUpBillForm").submit();
					}
	         	} else {
	            	// alert("Form is not valid. verifyResult: " + verifyResult);
	         	}
	         }
	      }
	   }
	   if( typeof verifyShippingAddress === "function" ) {
	      verifyShippingAddress(formData);
	   }
	};

   </script>
    

</html>

