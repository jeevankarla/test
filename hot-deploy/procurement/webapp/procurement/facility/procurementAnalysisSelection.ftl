<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jquery-1.6.1.min.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.axislabels.js"</@ofbizContentUrl>></script>

<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-title-right {
		font-weight: normal;
		text-align: right;		
	}	
	.cell-effort-driven {
		text-align: center;
	}
    .toggle {
      height: 9px;
      width: 9px;
      display: inline-block;
    }

    .toggle.expand {
      background: url(<@ofbizContentUrl>/images/jquery/plugins/slickgrid/images/expand.gif</@ofbizContentUrl>) no-repeat center center;
    }

    .toggle.collapse {
      background: url(<@ofbizContentUrl>/images/jquery/plugins/slickgrid/images/collapse.gif</@ofbizContentUrl>) no-repeat center center;
    }	
</style>			
			
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

<script type="text/javascript">

$(document).ready(function(){
		$( "#fromDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			onSelect: function( selectedDate ) {
				$( "#thruDate" ).datepicker( "option", "minDate", selectedDate );
			}
		});
		$( "#thruDate" ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 2,
			onSelect: function( selectedDate ) {
				$( "#fromDate" ).datepicker( "option", "maxDate", selectedDate );
			}
		});
		$('#ui-datepicker-div').css('clip', 'auto');	
		
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

	// fetch the charts for today
	        $.get(  
            "${ajaxUrl}",  
            { },  
            function(responseText){  
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself       			
       			$('#loader').hide();
     			$('#result').show(); 
       			jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );                
            },  
            "html"  
        );  
        
	// also set the click handler
  	$("#getCharts").click(function(){  
        $.get(  
            "${ajaxUrl}",  
            {fromDate: $("#fromDate").val(), thruDate: $("#thruDate").val(), facilityId: $("input[name=facilityId]").val()},  
            function(responseText){  
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself
       			$('#loader').hide();
     			$('#result').show();        			
       			jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );                
            },  
            "html"  
        );  
        return false;
    });		
		
	});
</script>

<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Period and Location</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="procurementAnalysisLocation" action="<@ofbizUrl>ProcurementAnalysisLocation</@ofbizUrl>">
		<table class="basic-table" cellspacing="0">
        	<tr>
        		<td align="left" width="3%"><span class='h3'>From: </span></td>
            	<td align="left" width="15%"><input class='h2' type="text" id="fromDate" name="fromDate"/></td>
				<td align="left" width="3%"><span class='h3'>To: </span></td>
				<td align="left" width="15%"><input class='h2' type="text" id="thruDate" name="thruDate"/></td>
				<td align="left" width="5%"><span class='h3'>Facility:</span></td>
				<td align="left" width="15%"><@htmlTemplate.lookupField value="${facilityId?if_exists}" formName="procurementAnalysisLocation" name="facilityId" id="facilityId" fieldFormName="ProcurementLookupFacility"/></td>
				<td><input type="submit" value="Submit" id="getCharts" class="smallSubmit"/></td>
			</tr>
    	</table> 
	</form>
	</div>
</div>

<div id="loader" > 
      <p align="center" style="font-size: large;">
        <img src="<@ofbizContentUrl>/images/ajax-loader64.gif</@ofbizContentUrl>">
      </p>
</div>

<div id="result"/>
