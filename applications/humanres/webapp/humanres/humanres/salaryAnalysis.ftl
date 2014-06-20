
	
<script type="text/javascript">

$(document).ready(function(){
  	    $('#loader').show();
     	$('#result').hide(); 
  	    
        $.get(  
            "${ajaxUrl}",  
            { customTimePeriodId: $("select[name=customTimePeriodId]").val()},  
            function(responseText){  
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself     			
       			jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );       
       			$('#loader').hide();
     			$('#result').show();            
            },  
            "html"  
        );  
        
// also set the click handler
  	$("#getCharts").click(function(){  
  	    $('#loader').show();
     	$('#result').hide(); 
  	    
        $.get(  
            "${ajaxUrl}",  
            { customTimePeriodId: $("select[name=customTimePeriodId]").val()},  
            function(responseText){  
                $("#result").html(responseText); 
				var reponse = jQuery(responseText);
       			var reponseScript = reponse.filter("script");
       			// flot does not work well with hidden elements, so we unhide here itself     			
       			jQuery.each(reponseScript, function(idx, val) { eval(val.text); } );       
       			$('#loader').hide();
     			$('#result').show();            
            },  
            "html"  
        );  
        return false;   
    });     
});

</script>


<div class="screenlet">
	<div class="screenlet-title-bar">
      	<h3>Select Period</h3>	
     </div>
    <div class="screenlet-body">
	  <form name="salaryAnalysis">
		<table class="basic-table" cellspacing="0">
			<tr>
				<td width="10%"><span class='h3'>Time Period: </span></td>
				<td align="left" width="10%">
				<select name="customTimePeriodId" class='h4'>
                		<#list customTimePeriods as customTimePeriod>    
                			 <#assign fromDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "MMMdd")/>
                			 <#assign thruDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "MMMdd yyyy")/>
                  	    	<option value='${customTimePeriod.customTimePeriodId}' >
	                    		${fromDate}-${thruDate}
	                  		</option>
                		</#list>           
					</select>
				</td>
				<td><input type="submit" value="Submit" id="getCharts" class="smallSubmit" /></td>				
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