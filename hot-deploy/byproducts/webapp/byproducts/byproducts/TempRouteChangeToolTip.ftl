
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
				target: $('#tempRouteDiv') // ... in the window
			},
			show: {
				ready: true, // Show it straight away
				modal: {
					on: false, // Make it modal (darken the rest of the page)...
					blur: false // ... but don't close the tooltip when clicked
				}
			},
			hide: false, // We'll hide it maunally so disable hide events
			style: {name : 'cream'}, //'ui-tooltip-light ui-tooltip-rounded ui-tooltip-dialogue', // Add a few styles
			events: {
				// Hide the tooltip when any buttons in the dialogue are clicked
				render: function(event, api) {
					populateTempRouteField();
					 $("#fromRouteId").val(data[0]["seqRouteId"]); 
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
	     $('#fromRouteId').focus();
		 $('#fromRouteId').focus(function (e) {    	 	
			$("#fromRouteId").autocomplete({ source: routesList });	
	 }); 
	 
	 
     $('#toRouteId').focus(function (e) {    	 	
			$("#toRouteId").autocomplete({ source: routesList });	
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
		var fromRouteId = $("#fromRouteId").val();
		var toRouteId = $("#toRouteId").val();
		for (i = 0; i < data.length; i++) {
		        var rowData = data[i];
		        if(data[i]["seqRouteId"] == fromRouteId && toRouteId !=""){
		            data[i]["seqRouteId"] =toRouteId;
		            for (j = 0; j < data.length; j++) {
				       if((i !=j)&& (data[j]["seqRouteId"] == rowData["seqRouteId"]) && (data[j]["cProductName"] == rowData["cProductName"])){
			            data[j]["cQuantity"] =  data[j]["cQuantity"]+data[i]["cQuantity"] ;
			            data[j]["quantity"] =  data[j]["quantity"]+data[i]["quantity"] ;
			            _grid.updateRow(j);
			            _grid.invalidateRow(i);
			            _grid.updateRowCount();
			            data.splice(i, 1);
			          } 
			          
		          }
		           _grid.setData(data);
			       _grid.render();    
			       _grid.updateRowCount();  
		          _grid.updateRow(i);
		     }
		        
		       
		}
		$(_grid.getCellNode(0,1)).click();
		$('button').click();
		return false;	
	}
	//handle cancel event
	function cancelForm(){		 
		return false;
	}
	
	function showRouteToChange() {
		var message = "";
		message += "<form action='' method='post' onsubmit='return appendTempRouteToForm();'><table cellspacing=10 cellpadding=10>" ; 		
		
			//message += "<br/><br/>";
			message +="<tr class='h3'><td align='left' class='h3' width='40%'>From Route:</td><td align='right' width='60%'><input class='h3' type='text' id='fromRouteId' name='fromRouteId' size='13' onblur='this.value=this.value.toUpperCase()'/><span class='tooltipbold' id='fromRouteTooltip'></span></td></tr>"+ 
						"<tr class='h3'><td align='left' class='h3' width='40%'>To Route:</td><td align='right' width='60%'><input class='h3' type='text' id='toRouteId' name='toRouteId' size='13' onblur='this.value=this.value.toUpperCase()'/><span class='tooltipbold' id='toRouteTooltip'></span></td></tr>"+
						"<tr class='h3'><td align='right'><span align='right'><input type='submit' value='${uiLabelMap.Shift}' id='routeChange' class='smallSubmit'/></span></td><td class='h3' width='100%' align='center'><span align='right'><button value='${uiLabelMap.CommonCancel}' onclick='return cancelForm();' class='smallSubmit'>${uiLabelMap.CommonCancel}</button></span></td></tr>";
		message += "</table></form>";				
		var title = "<h2><center>Full Route Shift</center></h2>";
		Alert(message, title);
	};
	
	
	 // route auto Complete
	 
    
</script>
