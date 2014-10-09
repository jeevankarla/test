
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">		



			 
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
					//checkTimePeriods();
					$('button', api.elements.content).click(api.hide);
				},
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});		
	}
 //handle cancel event
	function cancelForm(){		 
		return false;
	}
	function Alert(message, title)
	{
		// Content will consist of the message and an cancel and submit button
		var message = $('<p />', { html: message }),
			cancel = $('<button />', { text: 'cancel', 'class': 'full' });
 
		dialogue(message, title );		
		
	}
	
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	function appendParams(formName, action) {
	var formId = "#" + formName;
	jQuery(formId).attr("action", action);	
	jQuery(formId).submit();
	}
	function setTimePeriods(selection){
		getTimePeriodsByUnitValue("generate",$('[name=shedId]').val(),selection.value);
	}
	
	function showRunValidationForm() {	
		var message = "";
		message += "<form action='populateProcurementPeriodBilling' method='post'><table cellspacing=10 cellpadding=10>" ; 		

			//message += "<br/><br/>";
				message +="<tr class='h3'><td align='left' class='h3' width='55%'><input type='hidden' name='isTimePeriodClosed' value='Y' id='isTimePeriodClosed'>Shed Code:</td><td align='left' width='80%'><#if shedName?has_content><h1>${shedName}</h1><input type='hidden' name='shedId' id='sShedId' value='${shedId}'/><#else><select name='shedId' onchange='javascript:setBillingShedUnitsDropDown(this);' id='shedId' class='h3' allow-empty='true'>"+
	              		"<option value=''></option><#list shedList as shed><option value='${shed.facilityId}' >${shed.facilityName?if_exists}</option></#list>"+            
						"</select></#if></td></tr>";
				
				message +="<tr class='h3'><td align='left' class='h3' width='55%'>Unit:</td><td align='left' width='80%'><#if unitId?has_content><h1>${unitCode}</h1><input type='hidden' name='facilityId' id='facilityId' value='${unitId}'/><#else><#assign elementName= "facilityId"><select name='facilityId' onchange='javascript:setTimePeriods(this);' id='facilityId' class='h3' allow-empty='true'>"+
              				"<option value=''></option><#if unitMapsList?has_content><#list unitMapsList as units> <option value='${units.facilityId}' >${units.facilityCode} ${units.facilityName?if_exists}</option></#list><#else><#list unitsList as units> <option value='${units.facilityId}' >${units.facilityCode} ${units.facilityName?if_exists}</option></#list></#if>"+            
							"</select></#if></td></tr>";	
				
				message +="<tr class='h3'><td align='left' class='h3' width='55%'>Custom Time Period :</td><td align='left' width='80%'><select name='customTimePeriodId' id='customTimePeriodId' class='h3'><#list timePeriodList as timePeriod><#if !timePeriodId?exists><#assign isDefault = false></#if><#if timePeriodId?exists><#if timePeriodId == timePeriod.customTimePeriodId> 
							<option  value="${timePeriodId}" selected="selected">${timePeriod.periodName}</option></#if><#else><option value='${timePeriod.customTimePeriodId}'<#if isDefault>selected="selected"</#if>>${timePeriod.periodName}</option></#if></#list></select></td></tr>";
				
				message += "<tr class='h3'><td align='right'><span align='right'><input type='submit' value='Generate' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
	
		message += "</table></form>";				
		var title = "Billing Generation ";
		Alert(message, title);
	};	
<#if !unitName?has_content>
		<#if shedName?has_content>
			setBillingShedUnitsDropDownByValue($('[name=shedId]').val());
			getTimePeriodsByUnitValue("generate",$('[name=shedId]').val(),$('[name=facilityId]').val());
		</#if>
	<#else>
		<#if unitName?has_content>
			getTimePeriodsByUnitValue("generate",$('[name=shedId]').val(),$('[name=facilityId]').val()); 
		</#if>
	</#if>
	<#if !shedName?has_content>
		var options={};
		jQuery("[name='"+"facilityId"+"']").html(options);
	</#if>		
</script>

