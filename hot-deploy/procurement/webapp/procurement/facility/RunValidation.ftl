
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
					checkTimePeriods();
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
	function test(shedId){
	  setOutPutEntryShedUnitsDropDownByValue(shedId);
	}
	function showRunValidationForm() {	
		var message = "";
		message += "<form action='validatePeriodEntries' method='post' onsubmit='return disableGenerateButton();'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";
				message +="<tr class='h3'><td align='left' class='h3' width='50%'>Shed Name:</td><td align='left' width='80%'><#if shedName?has_content><h1>${shedName}</h1><input type='hidden' name='shedId' id='sshedId' value='${shedId}'/><#else><select name='shedId' onchange='javascript:test(this.value);' id='shedId' class='h3' allow-empty='true'>"+
	              		"<option value=''> </option><#list shedList as shed><option value='${shed.facilityId}' >${shed.facilityName?if_exists}</option></#list>"+            
						"</select></#if></td></tr>";
				message +="<tr class='h3'><td align='left' class='h3' width='50%'>Unit Name:</td><td align='left' width='80%'><select name='facilityId' id='facilityId' class='h3'>"+
	              		"<option value=''></option><#if unitMapsList?has_content><#list unitMapsList as units> <option value='${units.facilityId}' >${units.facilityCode} ${units.facilityName?if_exists}</option></#list><#else><#list unitsList as units> <option value='${units.facilityId}' >${units.facilityCode} ${units.facilityName?if_exists}</option></#list></#if>"+            
						"</select></td></tr>";		
				message +="<tr class='h3'><td align='left' class='h3' width='50%'>Time Period :</td><td align='left' width='80%'><select name='customTimePeriodId' id='rCustomTimePeriodId' class='h3'>"+
	              		"</select></td></tr>";
							
			message += "<tr class='h3'><td align='right'><span align='right'><input type='submit' value='Run' id='validatePeriodEntries' class='smallSubmit'/></span></td></td><td width='10%' align='center' class='h3'><span align='right'><button class='styled-button' value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallbutton'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		
		message += "</table></form>";				
		var title = "Run Validation ";
		Alert(message, title);
		
	};	
	function checkTimePeriods(){
		getTimePeriodsByUnitValueForValidation($('[name=shedId]').val(), $('[name=facilityId]').val());
	}
</script>

