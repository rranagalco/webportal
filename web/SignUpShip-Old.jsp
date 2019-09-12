<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | Enter Shipping Address">
   <meta name="author" content="Nicholas Cirullo">
   <title>Create Account | Shipping Address</title>
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

<body cz-shortcut-listen="true" class="create-account create-shipping">
   <!--#include virtual="/topnav.inc"-->
   <div class="middle-sec">
      <main id="panel">
         <div class="container">
            <div class="row">
               <div class="col-xs-12 col-sm-6 col-md-8">
                  <h1>Create Account</h1>
               </div>
               <div class="col-xs-12 col-sm-6 col-md-4">
                  <div class="stepRow row shipping">
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
                        <div>Shipping</div>
                     </div>
                  </div>
               </div>
               <div class="col-xs-12 col-sm-6">
                  <form role="form" action="/portal/controller?formFunction=SignUpShip" id="SignUpShipForm" class="form-horizontal">
                     <h3 class="text-primary signin-header">Enter Shipping Address</h3>
                     
					 <c:if test="${messageToUser != null}">
						<div class="alert col-md-13 form-group alert-danger">
					  		<strong>Error!</strong> ${messageToUser}
						</div>
					 </c:if>

				     <div class="form-group">
                        <label for="sameAsBilling" class="control-label col-md-4 visible-md visible-lg"></label>
                        <div class="col-xs-10 col-xs-offset-2 col-sm-11 col-sm-offset-0 col-md-8 input-group" style="padding-top: 8px;">
                           <input type="checkbox" name="sameAsBilling" id="sameAsBilling" value="yes">
                           <label for="sameAsBilling">Same as billing address</label>
                        </div>
                     </div>
                     <div class="form-group subbrequired inline-form-control">
                        <label for="firstNameShip" class="control-label col-md-4 visible-md visible-lg">First Name</label>
                        <div class="col-xs-9 col-md-6 input-group">
                           <input type="text" name="firstNameShip" id="firstNameShip" class="form-control input-lg" placeholder="First Name" <c:choose><c:when test="${firstNameShip != null}">value="${firstNameShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                        <div class="col-xs-3 col-sm-2 input-group">
                           <input type="text" name="middleInitialShip" id="middleInitialShip" class="form-control input-lg force-placeholder" placeholder="MI" <c:choose><c:when test="${middleInitialShip != null}">value="${middleInitialShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="lastNameShip" class="control-label col-md-4 visible-md visible-lg">Last Name</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="lastNameShip" id="lastNameShip" class="form-control input-lg" placeholder="Last Name" <c:choose><c:when test="${lastNameShip != null}">value="${lastNameShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="addressShip" class="control-label col-md-4 visible-md visible-lg">Address 1</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="addressShip" id="addressShip" class="form-control input-lg" placeholder="Address 1" <c:choose><c:when test="${addressShip != null}">value="${addressShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="address2Ship" class="control-label col-md-4 visible-md visible-lg">Address 2</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="address2Ship" id="address2Ship" class="form-control input-lg" placeholder="Address 2" <c:choose><c:when test="${address2Ship != null}">value="${address2Ship}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="cityShip" class="control-label col-md-4 visible-md visible-lg">City</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="cityShip" id="cityShip" class="form-control input-lg" placeholder="City" <c:choose><c:when test="${cityShip != null}">value="${cityShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="stateShip" class="control-label col-md-4 visible-md visible-lg">State/Province</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <select name="stateShip" id="stateShip" class="form-control">

                           </select>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="zipShip" class="control-label col-md-4 visible-md visible-lg">Postal Code</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="zipShip" id="zipShip" class="form-control input-lg" placeholder="Postal Code" <c:choose><c:when test="${zipShip != null}">value="${zipShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="countryShip" class="control-label col-md-4 visible-md visible-lg">Country</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <select name="countryShip" id="countryShip" class="form-control">
                           </select>
                        </div>
                     </div>
                     <div class="form-group subbrequired inline-form-control">
                        <label for="phoneShip" class="control-label col-md-4 visible-md visible-lg">Phone</label>
                        <div class="col-xs-9 col-md-6 input-group">
                           <input type="text" name="phoneShip" id="phoneShip" class="form-control input-lg" placeholder="Phone" <c:choose><c:when test="${phoneShip != null}">value="${phoneShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                        <div class="col-xs-3 col-sm-2 input-group">
                           <input type="text" name="phoneExtShip" id="phoneExtShip" class="form-control input-lg force-placeholder" placeholder="Ext." <c:choose><c:when test="${phoneExtShip != null}">value="${phoneExtShip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
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
                           <button id="createAccountButton" class="btn btn-block btn-primary">Add Shipping Address</button>
                        </div>
                     </div>

					 <input type="hidden" name="formFunction" value="SignUpShip">
                     <input type="hidden" name="company" id="company" <c:choose><c:when test="${company != null}">value="${company}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
                     <input type="hidden" name="cust_num" <c:choose><c:when test="${cust_num != null}">value="${cust_num}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
                     <input type="hidden" name="email" <c:choose><c:when test="${email != null}">value="${email}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
                     <input type="hidden" name="password" <c:choose><c:when test="${password != null}">value="${password}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
                     
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
		$(function() {
			// Handler for .ready() called.

			populateCountries("countryShip", "stateShip"); // first parameter is id of country drop-down and second parameter is id of state drop-down
			<c:if test="${(addressShip != null)}">
				$('#countryShip option[value="${countryShip}"]').prop('selected', 'selected').change();            	      
				$('#stateShip option[value="${stateShip}"]').prop('selected', 'selected').change();
			</c:if>	
			
			$('#sameAsBilling').change	(
				function() {
			        if ($(this).is(':checked')) {
                        
                        
                        
                        // $('#firstNameShip').val("${firstName}");
                        
                        
                        document.getElementById("firstNameShip").value = "${firstName}";
                        
                        
                        
                        $('#lastNameShip').val("${lastName}");
                        $('#addressShip').val("${address}");
                        $('#address2Ship').val("${address2}");
                        $('#cityShip').val("${city}");
           				$('#countryShip option[value="${country}"]').prop('selected', 'selected').change();            	      
						$('#stateShip option[value="${state}"]').prop('selected', 'selected').change();
                        $('#zipShip').val("${zip}");
                        $('#phoneShip').val("${phoneWork}");
                        $('#phoneExtShip').val("${phoneWorkExt}");
                        
						$("#firstNameShip").prop( "disabled", true );
						$("#lastNameShip").prop( "disabled", true );
						$("#addressShip").prop( "disabled", true );
						$("#address2Ship").prop( "disabled", true );
						$("#cityShip").prop( "disabled", true );
						$("#countryShip").prop( "disabled", true );
						$("#stateShip").prop( "disabled", true );
						$("#zipShip").prop( "disabled", true );
						$("#phoneShip").prop( "disabled", true );
						$("#phoneExtShip").prop( "disabled", true );                        
                    } else {
                    	/*
                        $('#firstNameShip').val("");
                        $('#lastNameShip').val("");                    
                        $('#addressShip').val("");
                        $('#address2Ship').val("");
                        $('#cityShip').val("");
                        $('#countryShip').val("");
                        $('#stateShip').val("");
                        $('#zipShip').val("");
                        $('#phoneShip').val("");
                        $('#phoneExtShip').val("");
                        */
                        
						$("#firstNameShip").prop( "disabled", false );
						$("#lastNameShip").prop( "disabled", false );
						$("#addressShip").prop( "disabled", false );
						$("#address2Ship").prop( "disabled", false );
						$("#cityShip").prop( "disabled", false );
						$("#countryShip").prop( "disabled", false );
						$("#stateShip").prop( "disabled", false );
						$("#zipShip").prop( "disabled", false );
						$("#phoneShip").prop( "disabled", false );
						$("#phoneExtShip").prop( "disabled", false );
			        }
			    }
			    						);  
		});    
    </script>
    
    
    <script>
    // just for the demos, avoids form submit
    jQuery.validator.setDefaults({
      success: "valid"
    });

    jQuery.validator.addMethod("zipcode", function(value, element) {
    	if ($("#countryShip option:selected" ).text() == "United States") {
    		return this.optional(element) || /^\d{5}(?:-\d{4})?$/.test(value);
    	} else if ($("#countryShip option:selected" ).text() == "Canada") {
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
   		if ($("#countryShip option:selected" ).text() != "Select Country") {
        	return true;
        } else {
            return false;
    	}
    	
    }, "Please select a country.");

    $( "#SignUpShipForm" ).validate({
      rules: {
		firstNameShip: {
			required: true,
			maxlength: 16
		},
		middleInitialShip: {
			maxlength: 2
		},
		lastNameShip: {
			required: true,
			maxlength: 22
		},
		addressShip: {
			required: true,
			maxlength: 36
		},
		address2Ship: {
			maxlength: 36
		},
		cityShip: {
			required: true,
			maxlength: 15
		},
		countryShip: {
			required: true,
			country_name: true
		},		
		stateShip: "required",
		zipShip: {
			required: true,
			zipcode: true
		},		
		phoneShip: {
			required: true,
			phoneUS: true
		},
		phoneExtShip: {
			digits: true
		}
      },

      
	  messages: {
			firstNameShip : {
				required: "Please enter your first name",
				maxlength: "First name can't have more than 16 characters"
			},
			middleInitialShip : {
					maxlength: "Middle initial can't have more than 2 characters"
			},
			lastNameShip : {
				required: "Please enter your first name",
				maxlength: "Last name can't have more than 22 characters"
			},

			addressShip: {
				required: "Please enter a valid address",
				maxlength: "Address can't have more than 36 characters"
			},
			address2Ship: {
				maxlength: "Address2 can't have more than 36 characters"
			},
			cityShip: {
				required: "Please enter a valid city",
				maxlength: "City can't have more than 15 characters"
			},			
			countryShip: "Please select a country",
			stateShip: "Please select a state",
			zipShip: "Please enter a valid postal code",
			
			phoneShip: "Please enter a valid phone number",
			phoneExtShip: "Please enter digits only"
	  }

      
    });
    </script>

   <script>
   
	$("#createAccountButton").on('click', function() {
		if ($("#SignUpShipForm").valid() == true) {
			if ($('#sameAsBilling').is(':checked')) {
				// alert("sameAsBilling is checked");
				// alert("firstNameShip: " + $('#firstNameShip').val());

				$("#SignUpShipForm").submit();
			} else {
				// alert("sameAsBilling is NOT checked");
			
				callAddressModal();
		    	return false;
			}
		}
	});

	var callAddressModal = function() {                           
	   var formData = {
	      name    : document.getElementById("company"),
	      addr1   : document.getElementById("addressShip"),
	      addr2   : document.getElementById("address2Ship"),
	      city    : document.getElementById("cityShip"),
	      state   : document.getElementById("stateShip"),
	      zip     : document.getElementById("zipShip"),
	      country : document.getElementById("countryShip"),
	      callback : function( verifyResult ) {
	         if(typeof verifyResult === "string") {
	         	if ($("#SignUpShipForm").valid()) {
					if ((verifyResult == "corrected") ||
						(verifyResult == "entered"  )    ) {
						// alert("Ready to submit 333. " + verifyResult);
						$("#SignUpShipForm").submit();
					} else if (verifyResult == "edit") {
						// alert("Some more editing. " + verifyResult);					
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

