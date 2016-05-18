<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.axislabels.js"</@ofbizContentUrl>></script>
 
<#if ajaxUrl != "LMSChartsDayCashReceivablesInternal">
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.tooltip.js"</@ofbizContentUrl>></script>
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />


<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery.event.drag-2.0.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>
</#if>

<script type="text/javascript">

$(document).ready(function(){

// enter event handle
	$("input").keypress(function(e){
		if (e.which == 13 && e.target.name =="facilityId") {
				$("#getTreeGrid").click();
		}
	});
	
    $('#loader').hide();
	jQuery.ajaxSetup({
  		beforeSend: function() {
     		$('#loader').show();
     		$('#result').hide();
  		},
  		complete: function(){
     		$('#loader').hide();
     		$('#result').show();     
  		}
	});

	$( "#fromDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			onSelect: function( selectedDate ) {					
				date = $(this).datepicker('getDate');
				$( "#thruDate" ).datepicker( "option", {minDate: selectedDate, maxDate: maxDate}).datepicker('setDate', date);
			}
		}).datepicker("setDate",new Date(2016, 03, 01)) ;
		$( "#thruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			onSelect: function( selectedDate ) {
				$( "#fromDate" ).datepicker( "option", "maxDate", selectedDate );
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');

	// fetch the charts for today
	        $.get(  
            "${ajaxUrl}",  
            { fromDate: $("#fromDate").val()},  
            function(responseText){  
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself       			
       			$('#loader').hide();
     			$('#result').show(); 
            },  
            "html"  
        );  
        
	// also set the click handler
  	$("#getTreeGrid").click(function(){  
        $.post(  
            "${ajaxUrl}",  
            { fromDate: $("#fromDate").val() ,thruDate: $("#thruDate").val(),partyId:$("#partyId").val(),categoryId:$("#categoryId").val(),branchId:$("#branchId").val()},  
            function(responseText){
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself
       			$('#loader').hide();
     			$('#result').show();        			
            },  
            "html"  
        );  
    });
});

</script>

<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Select Date</h3>
    </div>
     <div class="screenlet-body">
		<span class='h3'>Branch: <select id="branchId" name="branchId">
        <#list branchList as branch>
          <option value="${branch.payToPartyId}">${branch.storeName}</option>
         </#list>
      </select> </span>
		<span class='h3'>Category: </span><select id="categoryId" name="categoryId">
        <#list categoryList as category>
          <option value="${category.categoryId}">${category.categoryName}</option>
         </#list>
      </select>
		<span class='h3'>Party Id: </span><input class='h2' type="text" id="partyId" name="partyId" value=""/>
        <br/>
		<span class='h3'>From Date: </span><input class='h2' type="text" id="fromDate" name="fromDate" value="${defaultEffectiveDate}" readonly="true"/>
		<span class='h3'>Thru Date: </span><input class='h2' type="text" id="thruDate" name="thruDate" value="${defaultEffectiveThruDate}" readonly="true"/>
		<input type="submit" value="Submit" id="getTreeGrid" class="smallSubmit" /> 		
    </div>
</div>


<div id="loader" > 
      <p align="center" style="font-size: large;">
        <img src="<@ofbizContentUrl>/images/ajax-loader64.gif</@ofbizContentUrl>">
      </p>
</div>
<div id="result"/>
