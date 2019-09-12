if (!String.prototype.encodeHTML) {
   String.prototype.encodeHTML = function() {
      return this.replace(/&/g, '&amp;')
         .replace(/</g, '&lt;')
         .replace(/>/g, '&gt;')
         .replace(/"/g, '&quot;')
         .replace(/'/g, '&apos;');
   };
}

var highlightAddressDifference = function(element) {
   var correctElement, diffHTML;
   if (element) {
      correctElement = document.getElementById(element.getAttribute('data-linked'));
   }
   if (correctElement) {
      diffHTML = diffString(element.innerHTML.toUpperCase(), correctElement.innerHTML.toUpperCase());
      correctElement.setAttribute("data-orig", correctElement.innerHTML);
      correctElement.innerHTML = diffHTML;
   }
}
var logAddressError = function(message) {
   console.log("Address Verification Error: ", message);
   if (typeof ga === "function") {
      ga('send', 'event', 'addressVerificationError', message || "");
   }
}
var correctedData, verifyResult;
var verifyShippingAddress = function(formData) {
   // alert("subb");
   
   var jsonRequest, zipMatchesArray,  jqxhr;

   verifyResult = "incomplete";
   jsonRequest = {
      name: "",
      address: "",
      city: "",
      state: "",
      zip5: "",
      zip4: "",
      urbani: "",
      country: ""
   };

/*
alert(formData.name.value);
alert(formData.addr1.value);
alert(formData.addr2.value);
alert(formData.city.value);
alert(formData.state.value);
alert(formData.zip.value);
alert(formData.country.value);
*/

   // alert("subb77");

   if (!formData || typeof formData !== "object" || !formData.addr1 || !formData.callback || typeof formData.callback !== "function") {
      logAddressError("JavaScript: Bad formData");
      alert("Invalid formData. Please contact an administrator at webadmin@galco.com");
      return;
   } else {
      jsonRequest.name = formData.name.value.encodeHTML();
      
      // alert("Address Verification, company name: " + jsonRequest.name);
      
      jsonRequest.address = formData.addr1.value.encodeHTML();
      jsonRequest.address2 = formData.addr2.value.encodeHTML();
      jsonRequest.city = formData.city.value.encodeHTML();
      jsonRequest.state = formData.state.value.encodeHTML();
      jsonRequest.zip5 = formData.zip.value.encodeHTML();
      jsonRequest.country = formData.country.value.encodeHTML();
   }
   
   // alert("subb88");

   if (jsonRequest.country === "United States") {
      jsonRequest.country = "US";
   }  
   if (jsonRequest.country === "USA") {
      jsonRequest.country = "US";
   }
   if (jsonRequest.state === "PR") {
      jsonRequest.country = "US";
      jsonRequest.urbani = jsonRequest.address2;
   }

   if (jsonRequest.zip5.search(/\d{5}[- ]\d{4}/) !== -1) {
      zipMatchesArray = jsonRequest.zip5.match(/(\d{5})[- ](\d{4})/);
      jsonRequest.zip5 = zipMatchesArray[1];
      jsonRequest.zip4 = zipMatchesArray[2];
   } else if (jsonRequest.zip5.search(/\d{5}/) !== -1) {
      // jsonRequest.zip5 = zip;
      jsonRequest.zip4 = "";
   } else {
      if (jsonRequest.zip5 !== "") {
         // alert("Zipcode is in incorrect format. Zipcode must be either ##### or #####-####");
      }
      jsonRequest.zip5 = "";
      jsonRequest.zip4 = "";
   }
   if (jsonRequest.address === "" && jsonRequest.address2 !== "") {
      jsonRequest.address = jsonRequest.address2;
      jsonRequest.address2 = "";
   }

   if (jsonRequest.address === "" || jsonRequest.country == "") {
      // alert("Insufficient information for address lookup.");
      verifyResult = "fail";
   } else if (jsonRequest.zip5 === "" && (jsonRequest.city === "" || jsonRequest.state === "")) {
      // alert("Insufficient information for address lookup.");
      verifyResult = "fail";
   }

   if (jsonRequest.country !== "US") {
      // Street address verification is only available for the United States and Puerto Rico
      verifyResult = "fail";
   }
   if (verifyResult === "fail") {
	  // alert("subban fail");
      verifyAddressCallback(formData.callback, verifyResult);
      return;
   }
   

   /*
   $.ajax({
   // url: '/scripts/cgiip.exe/wa/lib/ups-address-ajax.htm',
      url: '/portal/controller?formFunction=UPSValidation',
        
      type: 'POST',
      cache: false,
      // dataType: 'json',
      // contentType: 'text/json~; charset=iso-8859-1',
      data: jsonRequest,
      success: function( response ) {
        alert("success");
        alert(response);
        console.log(response);
      },
      error: function AjaxFailed (XMLHttpRequest, textStatus) {
      		alert("failed-88");
      		console.log(XMLHttpRequest);
      		alert(textStatus);
        		}
      });
      
   return;
   */
      
   // alert("Subban 888");

   $("body").addClass("loading");
   // jqxhr = $.post("/scripts/cgiip.exe/wa/lib/ups-address-ajax.htm",
   jqxhr = $.post("/portal/controller?formFunction=UPSValidation",
      jsonRequest,
      function(data, textStatus, xhr) {
	     // alert("Subban success");

	     var fields, $modalContent, $userData, $responseData, $upsDiv;

         $upsDiv = $("<div/>", { "class": "col-xs-12" })
            .append($("<table/>", { "class": "upsLogo" })
               .append($("<tr/>")
                  .append($("<td/>", { "class": "upsLogoImage" })
                     .append($("<img/>", { "src": "/images/ups_logo/logo_s.gif", "alt": "UPS Logo" })))
                  .append($("<td/>", {
                     "class": "upsLogoText",
                     "text": "Address Verification provided by UPS<br>UPS, the UPS brand mark, and the Color Brown are trademarks of United Parcel Service of America, Inc. All Rights Reserved"
                  }))));
         $userData = $("<div/>", { "id": "enteredAddress", "class": "col-xs-12 col-sm-6 col-md-5" })
            .append($("<h2/>", { "text": "Address Entered" }))
            .append($("<div/>", {
               "class": "userData",
               "id": "userAddr",
               "data-linked": "correctAddr",
               "text": formData.addr1.value
            }))
            .append($("<div/>", {
               "class": "userData",
               "id": "userCity",
               "data-linked": "correctCity",
               "text": formData.city.value
            }))
            .append($("<div/>", {
               "class": "userData",
               "id": "userState",
               "data-linked": "correctState",
               "text": formData.state.value
            }))
            .append($("<div/>", {
               "class": "userData",
               "id": "userZip",
               "data-linked": "correctZip",
               "text": formData.zip.value
            }))
            .append($("<a/>", {
               "class": "useOriginal btn",
               "href": "#",
               "text": "Use Address Entered"
            }));
         $responseData = $("<div/>", { "id": "recommendedAddress", "class": "col-xs-12 col-sm-6 col-md-offset-1" })
            .append($("<h2/>", { "text": "Recommended Address" }));
         if (data.length < 0 || (data.error === undefined && data.XAVResponse === undefined)) {
            // No valid response from UPS
            logAddressError("UPS: ???");
            verifyResult = "fail";
            return;
         } else if (data.error) {
            // WDS Error
            logAddressError("WDS: " + data.error);
            verifyResult = "fail";
            return;
         } else if (data.XAVResponse.Response && data.XAVResponse.Response.ResponseStatus.Code !== "1") {
            // UPS Error
            // data.XAVResponse.Response.ResponseStatus.Description
            logAddressError("UPS:" + data.XAVResponse.Response.ResponseStatus.Description)
            verifyResult = "fail";
         } else if (data.XAVResponse.NoCandidatesIndicator !== undefined) {
            // No Addresses found
            $("<div/>", { "class": "message" })
               .append("<span>We can\'t confirm the address</span>")
               .append("Please double-check the address you entered or continue with the <u>Address Entered</u>.")
               .append($("<a/>", { "class": "editEntered btn", "href": "#", "text": "Edit entered address" }))
               .appendTo($responseData);
            $("a.useOriginal", $userData).addClass("btn-primary");
         } else if (data.XAVResponse.AmbiguousAddressIndicator !== undefined) {
            // Multiple Addresses Found
            $("<div/>", { "class": "message" })
               .append("<span>We can\'t confirm the address</span>")
               .append("Please double-check the address you entered or continue with the <u>Address Entered</u>.")
               .append($("<a/>", { "class": "editEntered btn", "href": "#", "text": "Edit entered address" }))
               .appendTo($responseData);
            $("a.useOriginal", $userData).addClass("btn-primary");
         } else if (data.XAVResponse.ValidAddressIndicator !== undefined && data.XAVResponse.Candidate) {
            // Single good address found
            correctedData = data.XAVResponse.Candidate.AddressKeyFormat;
            $responseData.append($("<div/>", {
                  "class": "correctData",
                  "id": "correctAddr",
                  "text": correctedData.AddressLine
               }))
               .append($("<div/>", {
                  "class": "correctData",
                  "id": "correctCity",
                  "text": correctedData.PoliticalDivision2
               }))
               .append($("<div/>", {
                  "class": "correctData",
                  "id": "correctState",
                  "text": correctedData.PoliticalDivision1
               }))
               .append($("<div/>", {
                  "class": "correctData",
                  "id": "correctZip",
                  "text": correctedData.PostcodePrimaryLow + '-' + correctedData.PostcodeExtendedLow
               }))
               .append($("<a/>", {
                  "class": "useCorrected btn btn-primary",
                  "href": "#",
                  "text": "Use Recommended Address"
               }));
            if (correctedData.Urbanization !== undefined) {
               $("<div/>", {
                  "class": "userData",
                  "id": "userUrban",
                  "data-linked": "correctUrban",
                  "text": formData.addr2.value
               }).insertAfter($("#userAddr", $userData));
               $("<div/>", {
                  "class": "correctData",
                  "id": "correctUrban",
                  "text": correctedData.Urbanization
               }).insertAfter($("#correctAddr", $responseData));
            }
         } else {
            // Serious error in JavaScript Logic
            logAddressError("JavaScript: ???");
            verifyResult = "fail";
            return;
         }
         $modalContent = $("<div/>", { "id": "verifyAddressModal" })
            .append($("<h1/>", { "class": "col-xs-12", "text": "Address Verification" }))
            .append($responseData)
            .append($userData)
            .append($upsDiv);
         
         $.featherlight($modalContent, { afterClose: function() { verifyAddressCallback(formData.callback, verifyResult); } });

         // alert("subb aaa");

         $(".userData", "#verifyAddressModal").each(function(index, el) {
            highlightAddressDifference(el);
         });

         $("#verifyAddressModal a.editEntered").on('click', function(event) {
            event.preventDefault()
            $.featherlight.current().close();
            verifyResult = "edit";
            return true;
         });
         $("#verifyAddressModal a.useOriginal").on('click', function(event) {
            event.preventDefault()
            $.featherlight.current().close();
            verifyResult = "entered";
            return true;
         });
         $("#verifyAddressModal a.useCorrected").on('click', function(event) {
            event.preventDefault()
               // if(correctedData.ConsigneeName)
               // {
               //    formData.name.value = correctedData.ConsigneeName;
               // }
            formData.addr1.value = correctedData.AddressLine;
            formData.city.value = correctedData.PoliticalDivision2;
            formData.state.value = correctedData.PoliticalDivision1;
            if (correctedData.Urbanization) {
               formData.addr2.value = correctedData.Urbanization;
            }
            if (correctedData.PostcodeExtendedLow) {
               formData.zip.value = correctedData.PostcodePrimaryLow + '-' + correctedData.PostcodeExtendedLow;
            } else {
               formData.zip.value = correctedData.PostcodePrimaryLow;
            }
            $.featherlight.current().close();
            verifyResult = "corrected";
            return true;
         });
      },
      "json"
   ).always(function() {
	  // alert("subban444");
	  
      $("body").removeClass("loading");
      
      // alert(verifyResult);

      if (verifyResult === "fail") {
         verifyAddressCallback(formData.callback, verifyResult);
      }
   });
}

var verifyAddressCallback = function(callbackFunc, verifyResult) {
   if (typeof callbackFunc === "function") {
      callbackFunc(verifyResult);
   }
}

/*
 * Javascript Diff Algorithm
 *  By John Resig (http://ejohn.org/)
 *  Modified by Chu Alan "sprite"
 *
 * Released under the MIT license.
 *
 * More Info:
 *  http://ejohn.org/projects/javascript-diff-algorithm/
 */

var escape = function(s) {
   var n = new String(s);
   n = n.replace(/&/g, "&amp;");
   n = n.replace(/</g, "&lt;");
   n = n.replace(/>/g, "&gt;");
   n = n.replace(/"/g, "&quot;");

   return n;
}

var diffString = function(o, n) {
   o = o.replace(/\s+$/, '');
   n = n.replace(/\s+$/, '');

   var out = diff(o == "" ? [] : o.split(/\s+/), n == "" ? [] : n.split(/\s+/));
   var str = "";

   var oSpace = o.match(/\s+/g);
   if (oSpace == null) {
      oSpace = ["\n"];
   } else {
      oSpace.push("\n");
   }
   var nSpace = n.match(/\s+/g);
   if (nSpace == null) {
      nSpace = ["\n"];
   } else {
      nSpace.push("\n");
   }

   if (out.n.length == 0) {
      for (var i = 0; i < out.o.length; i++) {
         str += '<del>' + escape(out.o[i]) + oSpace[i] + "</del>";
      }
   } else {
      if (out.n[0].text == null) {
         for (n = 0; n < out.o.length && out.o[n].text == null; n++) {
            str += '<del>' + escape(out.o[n]) + oSpace[n] + "</del>";
         }
      }

      for (var i = 0; i < out.n.length; i++) {
         if (out.n[i].text == null) {
            str += '<ins>' + escape(out.n[i]) + nSpace[i] + "</ins>";
         } else {
            var pre = "";

            for (n = out.n[i].row + 1; n < out.o.length && out.o[n].text == null; n++) {
               pre += '<del>' + escape(out.o[n]) + oSpace[n] + "</del>";
            }
            str += " " + out.n[i].text + nSpace[i] + pre;
         }
      }
   }

   return str;
}

var randomColor = function() {
   return "rgb(" + (Math.random() * 100) + "%, " +
      (Math.random() * 100) + "%, " +
      (Math.random() * 100) + "%)";
}

var diffString2 = function(o, n) {
   o = o.replace(/\s+$/, '');
   n = n.replace(/\s+$/, '');

   var out = diff(o == "" ? [] : o.split(/\s+/), n == "" ? [] : n.split(/\s+/));

   var oSpace = o.match(/\s+/g);
   if (oSpace == null) {
      oSpace = ["\n"];
   } else {
      oSpace.push("\n");
   }
   var nSpace = n.match(/\s+/g);
   if (nSpace == null) {
      nSpace = ["\n"];
   } else {
      nSpace.push("\n");
   }

   var os = "";
   var colors = new Array();
   for (var i = 0; i < out.o.length; i++) {
      colors[i] = randomColor();

      if (out.o[i].text != null) {
         os += '<span style="background-color: ' + colors[i] + '">' +
            escape(out.o[i].text) + oSpace[i] + "</span>";
      } else {
         os += "<del>" + escape(out.o[i]) + oSpace[i] + "</del>";
      }
   }

   var ns = "";
   for (var i = 0; i < out.n.length; i++) {
      if (out.n[i].text != null) {
         ns += '<span style="background-color: ' + colors[out.n[i].row] + '">' +
            escape(out.n[i].text) + nSpace[i] + "</span>";
      } else {
         ns += "<ins>" + escape(out.n[i]) + nSpace[i] + "</ins>";
      }
   }

   return { o: os, n: ns };
}

var diff = function(o, n) {
      var ns = new Object();
      var os = new Object();

      for (var i = 0; i < n.length; i++) {
         if (ns[n[i]] == null)
            ns[n[i]] = { rows: new Array(), o: null };
         ns[n[i]].rows.push(i);
      }

      for (var i = 0; i < o.length; i++) {
         if (os[o[i]] == null)
            os[o[i]] = { rows: new Array(), n: null };
         os[o[i]].rows.push(i);
      }

      for (var i in ns) {
         if (ns[i].rows.length == 1 && typeof(os[i]) != "undefined" && os[i].rows.length == 1) {
            n[ns[i].rows[0]] = { text: n[ns[i].rows[0]], row: os[i].rows[0] };
            o[os[i].rows[0]] = { text: o[os[i].rows[0]], row: ns[i].rows[0] };
         }
      }

      for (var i = 0; i < n.length - 1; i++) {
         if (n[i].text != null && n[i + 1].text == null && n[i].row + 1 < o.length && o[n[i].row + 1].text == null &&
            n[i + 1] == o[n[i].row + 1]) {
            n[i + 1] = { text: n[i + 1], row: n[i].row + 1 };
            o[n[i].row + 1] = { text: o[n[i].row + 1], row: i + 1 };
         }
      }

      for (var i = n.length - 1; i > 0; i--) {
         if (n[i].text != null && n[i - 1].text == null && n[i].row > 0 && o[n[i].row - 1].text == null &&
            n[i - 1] == o[n[i].row - 1]) {
            n[i - 1] = { text: n[i - 1], row: n[i].row - 1 };
            o[n[i].row - 1] = { text: o[n[i].row - 1], row: i - 1 };
         }
      }

      return { o: o, n: n };
   }
   /* END OF Diff Algorithm code */
