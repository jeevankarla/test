
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>

<script type="application/javascript">
	
	var facilityData = ${StringUtil.wrapString(periodFacilityJSON)}
	var facilityValuesList ;
	var facilityList;
	
	var timePeriodData = ${StringUtil.wrapString(periodCustomTimeJSON)}
	var timePeriodValuesList ;
	var timePeriodList;
	var timePeriodLabel = ${StringUtil.wrapString(customTimePeriodLabelJSON)}
	
	var facilityBilling;
	var cancelDom;
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
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
					populateData();
					setDropDown();
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
	function setDropDown(){
		
		
		var cancelParentDomObj = $(cancelDom).parent().parent();
		
		var dropdownDom = $(cancelParentDomObj).find( "[name='"+"periodBillingId"+"']");
		
		$(dropdownDom).clone().appendTo($("#billingId"));
		$("#billingId").html($(dropdownDom).html());
	}
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	function setDropDownDetails(selection){
		 	 setDropDownDetailsByValue(selection.value);
	}
	function setDropDownDetailsByValue(selectionValue){
		 var optionList = '';
		 optionList += "<option value = " + "AllInstitutions" + " >" +"AllInstitutions"+ "</option>";
	 	 facilityValuesList = facilityData[selectionValue];
	 	 if(facilityValuesList != undefined && facilityValuesList != ""){
	 	 	for(var i=0 ; i<facilityValuesList.length ; i++){
				var innerList=facilityValuesList[i];
                optionList += "<option value = "+innerList+">" + innerList + "</option>";          			
      		}
	 	 }
	 	 facilityList = optionList;
	 	 jQuery("[name='"+"facilityId"+"']").html(facilityList); 
	 	 
	 	 var periodOptionList = '';
		 optionList += "<option value = " + "AllInstitutions" + " >" +"AllInstitutions"+ "</option>";
	 	 timePeriodValuesList = timePeriodData[selectionValue];
	 	 if(timePeriodValuesList != undefined && timePeriodValuesList != ""){
	 	 	for(var i=0 ; i<timePeriodValuesList.length ; i++){
				var innerList=timePeriodValuesList[i];	        
                var label = timePeriodLabel[innerList];
                periodOptionList += "<option value = "+innerList+">" + label + "</option>";          			
      		}
	 	 }
	 	 timePeriodList = periodOptionList;
	 	 jQuery("[name='"+"customTimePeriodId"+"']").html(timePeriodList); 	 
	}
	
	function showBillingGenerateForm() {	
		var message = "";
		message += "<form action='processInstitutionBilling' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='left' class='h3' width='50%'>Billing Type :</td><td align='left' width='80%'><select name='periodTypeId' allow-empty='true' id='periodTypeId' class='h3' onchange='javascript:setDropDownDetails(this);'>"+
						"<#list periodTypes as periodType><option value='${periodType.periodTypeId}'>${periodType.description?if_exists}</option></#list></td></tr>";
						
			message +="<tr class='h3'><td align='left' class='h3' width='50%'>Period :</td><td align='left' width='80%'><select name='customTimePeriodId' id='customTimePeriodId' class='h3'>"+
					  "<option value='${customTimePeriodId?if_exists}'>${customTimePeriodId?if_exists}</option></td></tr>"+
					  "<tr class='h3'><td align='left' class='h3' width='50%'>Institution :</td><td align='left' width='80%'><select name='facilityId' id='facilityId' class='h3'>"+
						"<option value='${institution?if_exists}'>${institution?if_exists}</option></td></tr>";
			message +="<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='generateCustomBilling' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
					  
			
		message += "</table></form>";				
		var title = "<h2><center>Generate Institution Billing</center></h2>";
		Alert(message, title);
	};
	
	var customTimePeriod;	
	var periodType;
	function setCancelDomObj(thisObj){
		cancelDom = thisObj;
	}
	function showCancelBilling(customTimePeriodId, periodTypeId, facilityBillingList) {
		
		var message = "";
		
		customTimePeriod = customTimePeriodId;
		periodType = periodTypeId;
		facilityBilling = facilityBillingList;
		message += "<form action='cancelBillingPeriod' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
			
			//message += "<br/><br/>";	
			message += "<tr class='h3'><td align='left' class='h3' width='40%'><input type='hidden' name='customTimePeriodId' id='customTimePeriodId'/><input type='hidden' name='periodTypeId' id='periodTypeId'/></td></tr>"+
						"<tr class='h3'><td align='left' class='h3' width='50%'>Institution :</td><td align='left' width='80%'><select name='periodBillingId' id='billingId' class='h3'>"+
						"</td></tr>";
		message += "<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='cancelCustomBilling' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr></table></form>";
		var title = "<h2><center>Cancel Period Billing</center></h2>";
		Alert(message, title);
		
		
	};
	
	function populateData(){
		jQuery("#customTimePeriodId").val(customTimePeriod);
		jQuery("#periodTypeId").val(periodType);
		
		var periodType = jQuery("#periodTypeId").val();
		setDropDownDetailsByValue(periodType);
	}
	
</script>
