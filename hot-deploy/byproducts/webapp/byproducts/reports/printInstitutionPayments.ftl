<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="text/javascript">
function dialogue(content, title) {
		/* 
		 * Since the dialogue isn't really a tooltip as such, we'll use a dummy
		 * out-of-DOM element as our target instead of an actual element like document.body
		 */
		$('<div />').qtip(
		{
			content: {
				text: content,
				title: title
			},
			position: {
				my: 'center', at: 'center', // Center it...
				target: $(window) // ... in the window
			},
			show: {
				ready: true, // Show it straight away
				modal: {
					on: true, // Make it modal (darken the rest of the page)...
					blur: false // ... but don't close the tooltip when clicked
				}
			},
			hide: false, // We'll hide it maunally so disable hide events
			style: {name : 'cream'}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
			events: {
				// Hide the tooltip when any buttons in the dialogue are clicked
				render: function(event, api) {
				//populateDate();
				//getAllIndentRejects();
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}

function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
		dialogue(message, title );		
		
	}
		function disableButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}

	function cancelForm(){		 
		return false;
	}
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
function submitForm(){		 
   return;
	}
function submitFormParam(){
var comments = $("#cancelComments").val();
		if(comments == undefined || comments == ""){
		alert("Please enter the Reason for to Void The Payment");
		return false;
		}
		
	 $("#cancelPaymentDesc").submit();	
	
	}


//<![CDATA[	
    function togglePaymentId(master) {
        var facilities = jQuery("#listBooths :checkbox[name='paymentIds']");

        jQuery.each(facilities, function() {
            this.checked = master.checked;
        });
        //getBoothRunningTotal();
    }
    
    function setVoidPaymentParameters(currentPayment){
    	jQuery(currentPayment).attr( "disabled", "disabled");
    	var currentEle = jQuery(currentPayment);
    	
    	formName=document.forms['cancelPayment'];
    	var domObj = $(currentEle).parent().parent();
        var rowObj = $(domObj).html();
        var method = $(domObj).find("#paymentMethodTypeId");
        var payment = $(domObj).find("#paymentId");
        var tabItemObj = $(domObj).find("#tabItem");
        var methodValue = $(method).val();
        var payId = $(payment).val(); 
        var tabValue = $(tabItemObj).val(); 
        
          var message = "";
                message += "<html><head></head><body><form  method='post' id='cancelPaymentDesc' action='cancelInstPayment' onsubmit='return didsableGenerateButton();'><table cellspacing=10 cellpadding=10 width=400>";
      			message += "<input type='hidden' value='"+methodValue+"' name='paymentMethodTypeId'/><input type='hidden' value='"+tabValue+"' name='subTabItem'/><input type='hidden' value='"+payId+"' name='paymentId'/><tr class='h2'><td align='left' class='h5' width='60%'>paymentId:</td><td align='left'  width='90%'>"+payId+"</td></tr>";
      			message += "<tr class='h3'><td align='left' class='h3' width='60%'>Comments:</td><td align='left' width='60%'><input class='h4' type='text' id='cancelComments' name='cancelComments'  /></td></tr>";
     		    message += "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Submit' class='smallSubmit' onclick='javascript: return submitFormParam();' /></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='cancel' onclick='return cancelForm();' class='smallSubmit'>cancel</button></span></td></tr>";
			    message +=	"</table></form></body></html>";
	var title = "Reason for cancel payment";
    Alert(message, title);		
	   
    
    }
    function setPrintPaymentParameters(currentPayment){
    	jQuery(currentPayment).attr( "disabled", "disabled");
    	var currentEle = jQuery(currentPayment);
    	formName=document.forms['listBooths'];	
		var hiddenpaymentId = document.createElement("input");
	    hiddenpaymentId.setAttribute("type", "hidden");
	    hiddenpaymentId.setAttribute("name", "paymentId");	   
	    hiddenpaymentId.setAttribute("value", currentEle.parent().siblings(":nth-child(1)").children(":first-child").val());
	    formName.appendChild(hiddenpaymentId);
	    formName.submit();		  
    
    }
    
//]]>
</script>

<#if boothPaymentsList?has_content>
  <form name="cancelPayment" id="cancelPayment"  method="post" action="cancelInstPayment">
  </form>
  <form name="listBooths" id="listBooths"  method="post" action="cashReceiptReport.pdf" target="_blank">
    <div align="right">
      <#--<input id="submitButton" type="button"  onclick="javascript:jQuery('#listBooths').submit();" value="Print Receipt"/>-->
    </div>

    <table class="basic-table hover-bar" cellspacing="0">
      <thead>
        <tr class="header-row-2">
          <td/>
          <td>Institution Code</td>
          <td>Name</td>
          <td>Payment Date</td>
          <td>${uiLabelMap.paymentLocation}</td>
          <td>${uiLabelMap.Amount}</td>
          <td>PaymentMethod Type</td>
          <td>Receipt</td>
          <td>Cancel</td>          
          <#--<td align="right">${uiLabelMap.CommonSelectAll} <input type="checkbox" id="checkAllFacilities" name="checkAllFacilities" onchange="javascript:togglePaymentId(this);" checked="true"/></td>-->
        </tr>
      </thead>
      <tbody>
        <#assign alt_row = false>
        <#list boothPaymentsList as payment>
        	       
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
            	<input type="hidden" name="paymentMethodTypeId" id="paymentMethodTypeId" value="${payment.paymentMethodTypeId?if_exists}">   
        		<input type="hidden" name="tabItem" id="tabItem" value="${parameters.subTabItem?if_exists}"> 
            	<td><input id="paymentId" type="hidden"  value="${payment.paymentId?if_exists}"/></td>            
              <td>${(payment.facilityId)?if_exists}</td>
              <#assign facility = delegator.findOne("Facility", {"facilityId" : payment.facilityId}, false) />
              <td>${(facility.facilityName)?if_exists}</td>
              <td><#if payment.paymentDate?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(payment.paymentDate ,"dd/MM/yyyy HH:mm:ss")}</#if></td> 
               <td>               
                ${(payment.paymentLocation)?if_exists}
              </td>
              <td><@ofbizCurrency amount=payment.amount isoCode=defaultOrganizationPartyCurrencyUomId/></td>
              <td>  
              	<#if (payment.paymentMethodTypeId)?has_content>
              		<#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true) />
              		 ${(paymentMethodType.description)?if_exists}
              	</#if>
              </td>
              <#if payment.statusId?has_content>
              <td><#if payment.statusId == "PMNT_RECEIVED"><a class="buttontext" target="_BLANK" href="<@ofbizUrl>printReceipt.pdf?paymentIds=${payment.paymentId}</@ofbizUrl>">Print Receipt</a></#if></td>
              <#else>
               <td align="center"></td>
               </#if>
              <td><#if hasPaymentCancelPermission && (payment.paymentMethodTypeId!="AXISHTOH_PAYIN")>
              			 <input id="submitButton" type="button"  onclick="javascript:setVoidPaymentParameters(this);" value="Cancel"/> 
              	  </#if>
              </td>                     
              <#--<td><input type="checkbox" id="paymentId_${payment_index}" name="paymentIds" value="${payment.paymentId?if_exists}"  checked="true"/></td>-->
            </tr>
            <#-- toggle the row color -->
            <#assign alt_row = !alt_row>
        </#list>
      </tbody>
    </table>
  </form>
<#else>
  <h3>No Payments Found</h3>
</#if>
