<!DOCTYPE html>
<html lang="en-us">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <meta charset="utf-8">
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <meta name="description" content="Galco Industrial Electronics | Payment and Shiping">
   <meta name="author" content="Nicholas Cirullo">
   <title>Add or Update Shiping Address</title>
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

<body cz-shortcut-listen="true" class="create-account create-shiping">
   <!--#include virtual="/topnav.inc"-->
   <div class="middle-sec">
      <main id="panel">
         <div class="container addShipingContent">
            <div class="row">
               <div class="col-xs-12 col-sm-8">
                  <form role="form" action="/portal/controller?formFunction=<c:choose><c:when test="${addAddress != null}">AddCustship</c:when><c:otherwise>EditCustship</c:otherwise></c:choose>" id="SignUpShipForm" class="form-horizontal">
                  	 <h2 class="text-primary signin-header">Add a Shiping Address</h2>

					 <c:if test="${messageToUser != null}">
						<div class="alert col-md-13 form-group alert-danger">
					  		<strong>Error!</strong> ${messageToUser}
						</div>
					 </c:if>
					                                         
                     <div class="form-group">
                        <label for="name" class="control-label col-md-4 visible-md visible-lg">Name</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="name" id="name" class="form-control input-lg" placeholder="Name" <c:choose><c:when test="${name != null}">value="${name}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="address" class="control-label col-md-4 visible-md visible-lg">Address 1</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="address" id="address" class="form-control input-lg" placeholder="Address 1" <c:choose><c:when test="${address != null}">value="${address}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group">
                        <label for="address2" class="control-label col-md-4 visible-md visible-lg">Address 2</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="address2" id="address2" class="form-control input-lg" placeholder="Address 2" <c:choose><c:when test="${address2 != null}">value="${address2}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="city" class="control-label col-md-4 visible-md visible-lg">City</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="city" id="city" class="form-control input-lg" placeholder="City" <c:choose><c:when test="${city != null}">value="${city}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>

                     <div class="form-group subbrequired">
                        <label for="country" class="control-label col-md-4 visible-md visible-lg">Country</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <select name="country" id="country" class="form-control">
                           </select>
                        </div>
                     </div>

                     <div class="form-group subbrequired">
                        <label for="state" class="control-label col-md-4 visible-md visible-lg">State/Province</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <select name="state" id="state" class="form-control">

                           </select>
                        </div>
                     </div>
                     <div class="form-group subbrequired">
                        <label for="zip" class="control-label col-md-4 visible-md visible-lg">Postal Code</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="zip" id="zip" class="form-control input-lg" placeholder="Postal Code" <c:choose><c:when test="${zip != null}">value="${zip}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     
                     <div class="form-group subbrequired inline-form-control">
                        <label for="phoneWork" class="control-label col-md-4 visible-md visible-lg">Phone</label>
                        <div class="col-xs-12 col-sm-11 col-md-8 input-group">
                           <input type="text" name="phoneWork" id="phoneWork" class="form-control input-lg" placeholder="Phone" <c:choose><c:when test="${phoneWork != null}">value="${phoneWork}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>
                        </div>
                     </div>
                     
	                 <div class="form-group">
	                     <label for="makeDefault" class="control-label col-md-4 visible-md visible-lg">Default Address</label>
	                     <div class="col-xs-10 col-xs-offset-2 col-sm-11 col-sm-offset-0 col-md-8 input-group" style="padding-top: 8px;">
	                        <input type="checkbox" name="makeDefault" id="makeDefault" value="y">
	                        <label for="makeDefault">Make this my default shiping address</label>
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
                           <button id="addOrUpdateAddressButton" class="btn btn-block btn-primary"><c:choose><c:when test="${addAddress != null}">Add Shiping Address</c:when><c:otherwise>Update Shiping Address</c:otherwise></c:choose></button>
                        </div>
                     </div>

					 <input type="hidden" name="formFunction" value="<c:choose><c:when test="${addAddress != null}">AddCustship</c:when><c:otherwise>EditCustship</c:otherwise></c:choose>">
                     <input type="hidden" name="shipto_num" <c:choose><c:when test="${shipto_num != null}">value="${shipto_num}"</c:when><c:otherwise>value=""</c:otherwise></c:choose>>				    		    
					 
					 <c:if test="${profileEditing != null}">
                        <input type="hidden" name="profileEditing" value="y">				    		    
					 </c:if>      
					                      
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

    $( "#SignUpShipForm" ).validate({
      rules: {
		name: {
			maxlength: 30
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
		}
      },

      
	  messages: {
			name : {
				maxlength: "Name can't have more than 30 characters"
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
			
			phoneWork: "Please enter a valid phone number"
	  }

      
    });
    </script>
    
    
   <script>
   
	$("#addOrUpdateAddressButton").on('click', function() {
		if ($("#SignUpShipForm").valid() == true) {
			callAddressModal();
	    	return false;
		}
	});

	var callAddressModal = function() {	    	
	   var formData = {
	      name    : document.getElementById("name"),
	      addr1   : document.getElementById("address"),
	      addr2   : document.getElementById("address2"),
	      city    : document.getElementById("city"),
	      state   : document.getElementById("state"),
	      zip     : document.getElementById("zip"),
	      country : document.getElementById("country"),
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
						$("#SignUpShipForm").submit();
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

