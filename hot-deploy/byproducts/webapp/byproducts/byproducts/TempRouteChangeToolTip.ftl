
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
	
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	 var boothsList =  ${StringUtil.wrapString(boothsJSON)}
	 var routesList =  ${StringUtil.wrapString(routesJSON)}
	 function setTempRouteBoothsDropDown(selection){
	 	routesList = routesList;
	 }
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
					populateTempRouteField();
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
	function populateTempRouteField(){
		var availableTags = ${StringUtil.wrapString(routesJSON)};
		$('#newTempRouteId').keypress(function (e) { 
			$("#newTempRouteId").autocomplete({					
				source:  availableTags
			});
		});
	}
	function datepick()
	{		
		$( "#estimatedDeliveryDate" ).datepicker({
			dateFormat:'MM dd, yy',
			changeMonth: false,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
	}
	
	//disable the generate button once the form submited
	function disableGenerateButton(){			
		   $("input[type=submit]").attr("disabled", "disabled");
		  	
	}
	function appendTempRouteToForm(){
		var tempRouteId = $("#newTempRouteId").val();
		var tempTripId = $("#tempTripId").val();
		$("#tempRouteId").val(tempRouteId);
		$("#tripId").val(tempTripId);
		$("#routeChangeFlag").val("routeChange");
		$('input[name=changeSave]').click();	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	function showRouteToChange() {
		var message = "";
		message += "<form action='' method='post' onsubmit='return appendTempRouteToForm();'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";
			message += "<tr class='h3'><td align='left' class='h3' width='40%'>Temp Route:</td><td align='right' width='60%'><input class='h3' type='text' id='newTempRouteId' name='newTempRouteId' size='13'/><span class='tooltipbold' id='tempRouteTooltip'></span></td></tr>"+
				"<tr class='h3'><td align='left' class='h3' width='40%'>Trip:</td><td><select name='tempTripId' class='h2' id='tempTripId'>"+
					"<#list prodSubTrips as eachTrip><option value='${eachTrip.enumId}'>${eachTrip.description}</option></#list></select></td><tr/>"+
						"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.CommonSubmit}' id='routeChange' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";				
		var title = "<h2><center>Temporary Route Change</center></h2>";
		Alert(message, title);
	};
	
</script>
