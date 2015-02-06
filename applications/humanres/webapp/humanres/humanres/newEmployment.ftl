<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />

<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/qtip/jquery.qtip.js</@ofbizContentUrl>"></script>


<script type="application/javascript">
					 
	 
	/*
	 * Common dialogue() function that creates our dialogue qTip.
	 * We'll use this method to create both our prompt and confirm dialogues
	 * as they share very similar styles, but with varying content and titles.
	 */
	 
	var organisationList;
	var partyName;
	var partyIdVal;
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
				  getEmploymentDetails(partyIdVal);
				   $('div#pastDues_spinner').html('<img src="/images/ajax-loader64.gif">');
					$('button', api.elements.content).click(api.hide);
				},
				
				// Destroy the tooltip once it's hidden as we no longer need it!
				hide: function(event, api) { api.destroy(); }
			}
		});
	}
	
	function disableSubmitButton(){			
		$("input[type=submit]").attr("disabled", "disabled");
	}
	
	function Alert(message, title)
	{
		// Content will consist of the message and an ok button
		
		dialogue( message, title );
	}
	
	function getEmploymentDetails(partyId){
		$.ajax({
             type: "POST",
             url: 'getEmploymentDetails',
             data: {partyId : partyId},
             dataType: 'json',
             success: function(result) {
               if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
            	   //populateError(result["_ERROR_MESSAGE_"]+result["_ERROR_MESSAGE_LIST_"]);
               }else{               			
            	    partyName = result["partyName"];
            	    organisationList = result["organisationList"];
            	    var paramName = 'partyIdFrom';
					var optionList = '';   		
					var list= organisationList;
					if (list) {		       				        	
			        	for(var i=0 ; i<list.length ; i++){
							var innerList=list[i];	              			             
			                optionList += "<option value = " + innerList.partyId + " >" + innerList.groupName + "</option>";          			
			      		}
			      	}		
			      	if(paramName){
			      		jQuery("[name='"+paramName+"']").html(optionList);
			      	}
			      	$("#partyName").val(partyName);
               }
             } 
        });
	}
	
	function datepick()
	{		
		$( "#effectiveDate" ).datepicker({
			dateFormat:'dd MM, yy',
			changeMonth: true,
			numberOfMonths: 1});
		$( "#fromDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1});
			$( "#reportingDate" ).datepicker({
			dateFormat:'dd/mm/yy',
			changeMonth: true,
			numberOfMonths: 1});		
		$('#ui-datepicker-div').css('clip', 'auto');
		
	}
 	function cancelForm(){                 
         return false;
 	}
 	
	function newEmployment(partyId) {
		partyIdVal = partyId;
		var message = "";
		message += "<form action='createNewEmployment' method='post' onsubmit='return disableSubmitButton();'><table cellspacing=10 cellpadding=10>";
		
		message +=  "<tr class='h3'><td align='left' class='h3' width='60%'>PartyId:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='partyIdTo' name='partyIdTo' value='${partyId?if_exists}'/></td></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='60%'>Cur.Department:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='partyName' name='partyName'/></td></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='60%'>New Department :</td><td align='left' width='60%'><select name='partyIdFrom' id='partyIdFrom'  class='h4'>"+
					"<#if organisationList?has_content><#list organisationList as organisation><option value='${organisation.partyId?if_exists}' >${organisation.groupName?if_exists}</option></#list></#if>"+            
					"</select></td></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='60%'>From Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='fromDate' name='fromDate' onmouseover='datepick()'/></td></tr>";
		
		message +=	"<tr class='h3'><td align='left' class='h3' width='60%'>Reporting Date:</td><td align='left' width='60%'><input class='h4' type='text' readonly id='reportingDate' name='reportingDate' onmouseover='datepick()'/></td></tr>";
		
		message +=  "<tr class='h3'><td align='center'><span align='right'><input type='submit' value='Create' class='smallSubmit'/></span></td><td class='h3' width='100%' align='left'><span align='left'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		title = "<center>Create New Employment<center><br />";
		message += "</table></form>";
		Alert(message, title);
	};
</script>
