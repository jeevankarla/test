
<input type="hidden" name="branchId1" id="branchId1" value="${branchId}">
<input type="hidden" name="partyId1" id="partyId1" value="${partyId}">
<input type="hidden" name="effectiveDate1" id="effectiveDate1" value="${effectiveDate}">
  

<script type="text/javascript">
	
	var branchProductSroreMap=${StringUtil.wrapString(branchProductSroreMap)!'[]'};
	
	//for Month Picker
$(document).ready(function(){ 
    $(".monthPicker").datepicker( {
       changeMonth: true,
       changeYear: true,
       showButtonPanel: true,
        minDate: new Date("04-01-2016"),
        maxDate: new Date("03-01-2017"),
       dateFormat: 'yy-mm',
       onClose: function(dateText, inst) { 
           var month = $("#ui-datepicker-div .ui-datepicker-month :selected").val();
           var year = $("#ui-datepicker-div .ui-datepicker-year :selected").val();
           $(this).datepicker('setDate', new Date(year, month, 1));
       }
});
$(".monthPicker").focus(function () {
       $(".ui-datepicker-calendar").hide();
       $("#ui-datepicker-div").position({
           my: "center top",
           at: "center bottom",
           of: $(this)
       });    
    });
});
	
<#--$(document).ready(function(){

var branchId = $("#branchId1").val();
var partyId = $("#partyId1").val();
var effectiveDate1 = $("#effectiveDate1").val();

  var branchList = "";
  <#list formatList as eachList>
	branchList = branchList + "<option value='${eachList.payToPartyId}' >${eachList.productStoreName}</option>";
  </#list>

     $("#branchId2").html(branchList);
     
     
     $("#branchId2").val(branchId);
     $("#partyId").val(partyId);
      $("#effectiveDate").val(effectiveDate1);
     
		
	});
	
	
	function autoCompletePartyId(){
	
	  var branchId2 = $("#branchId2").val();
	
	
			var productStoreId = branchProductSroreMap[branchId2];
			
			
		      $("#partyId").autocomplete({ 
		      
		      		
		      		source: function( request, response ) {
	        			$.ajax({
	          					url: "LookupBranchCustomers",
	          					dataType: "html",
	          					data: {
	            					ajaxLookup: "Y",
	            					term : request.term,
	            					productStoreId : productStoreId
	          					},
	          					success: function( data ) {
	          						var dom = $(data);
	        						dom.filter('script').each(function(){
	            						$.globalEval(this.text || this.textContent || this.innerHTML || '');
	        						});
	            					response($.map(autocomp, function(v,i){
	    								return {
	                						label: v.label,
	                						value: v.value
	               						};
									}));
									
	          					}
	        			});
	        			
	      			},
	      			select: function(e, ui) {
			        	//$('#partyTooltip').val('<label>+ui.item.label+</label>');
			        	
			        	$("#partyTooltip").html("<font size=5>"+ui.item.label+"</font>");
			        	
			        	 $("p label").hover(function(){
							           $(this).animate({fontSize: "15px"}, 300)
							          }, function() {
							         $(this).animate({fontSize: "10"}, 300)  
							          })
			        }
					
			  });	
		 }
	
	
	 function clearData(){
	 
	   $("#partyId").val('');
	   
	   
	   if(($("#partyId").val()).length == 0)
	   $("#partyTooltip").html('');
	 
	 }-->
	$(document).ready(function(){
		$('#spinner').hide(); 
		$('#BranchFilter').hide();
		$('#BranchFilterlabel').hide();
		$('#RegionFilterLabel').hide();
		$('#RegionFilter').hide();
		$('#StateFilterLabel').show();
		$('#StateFilter').show();
	});
    function showSearchFilter(obj){
       	var searchType=obj.value;
       	if(searchType=="BY_STATE"){
       		$('#BranchFilter').hide();
			$('#BranchFilterlabel').hide();
			$('#RegionFilterLabel').hide();
			$('#RegionFilter').hide();
			$('#StateFilterLabel').show();
			$('#StateFilter').show();
       	}else if (searchType=="BY_RO"){
        	$('#BranchFilter').hide();
			$('#BranchFilterlabel').hide();
			$('#RegionFilterLabel').show();
			$('#RegionFilter').show();
			$('#StateFilterLabel').hide();
			$('#StateFilter').hide();      		
       	}else{
       		$('#BranchFilter').show();
			$('#BranchFilterlabel').show();
			$('#RegionFilterLabel').hide();
			$('#RegionFilter').hide();
			$('#StateFilterLabel').hide();
			$('#StateFilter').hide();       		
       	}
    }	
    function callSpinner()
	{
		var branch=$("#branchId").val();  
        if(branch==""){
        	$("#dispComField").show();
        	$("#dispComField").delay(50000).fadeOut('slow'); 
        }
		$('#spinner').show();
		$('div#spinner').html('<img src="/images/ajax-loader64.gif">');
	}
	
</script>


<style type="text/css">

	<style type="text/css">
	 	.labelFontCSS {
	    	font-size: 13px;
		}
		.form-style-8{
		    max-width: 400px;
		    max-height: 280px;
		    max-right: 10px;
		    margin-top: 10px;
			margin-bottom: -5px;
		    padding: 15px;
		    box-shadow: 1px 1px 25px rgba(0, 0, 0, 0.35);
		    border-radius: 15px;
		    border: 1px solid #305A72;
		}
		
	</style>

</style>			


        
      <form method="post" name="delayedIndentPOAnalyticsListing" id="delayedIndentPOAnalyticsListing" action="<@ofbizUrl>delayedIndentPOAnalyticsListing</@ofbizUrl> " class="basic-form">
        
		  <table width="60%" border="0" cellspacing="0" cellpadding="0" class="form-style-8">
				 <tr>
        	 <td>Search By</td> 
        	 <td>
      		 	 <select name="searchType" id="searchType" onchange="javascript:showSearchFilter(this);">
					<option value='BY_STATE'>By State</option>
					<option value='BY_BO'>By Branch Office</option>
					<option value='BY_RO'>By Regional Office</option>
			  		</select> 
      		 	</td>
        	</tr>
			<tr>    
  			  <div>
  				  <td id="BranchFilterlabel">Branch</td>
	              <td id="BranchFilter">
					  <select name="branchId2" id="branchId">
		              <#if branchIdName?has_content>
			 	             <option value='${branchId?if_exists}'>${branchIdName?if_exists}</option> 
	 	              </#if>
					  <#if !branchIdName?has_content>
							 <option value=''>Select Branch</option>
					  </#if>
				      <#list  formatBList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
					 </select>
				      <div id="dispComField" style="color:red; font-stlye:bold; display:none">Please Select Branch</div>
		  		   </td>
  		       </div>
  		       <div >
  				  <td id="RegionFilterLabel">Regional Office</td>
	              <td id="RegionFilter">
					  <select name="regionId" id="regionId">
		              <#if regionIdName?has_content>
			 	             <option value='${regionId?if_exists}'>${regionIdName?if_exists}</option> 
	 	              </#if>
					  <#if !regionIdName?has_content>
							 <option value=''>Select Region</option>
					  </#if>
				      <#list  formatRList as formatList>
						<option value='${formatList.payToPartyId?if_exists}'>${formatList.productStoreName?if_exists}</option>
					 </#list> 
					 </select>
				      <div id="dispComField" style="color:red; font-stlye:bold; display:none">Please Select Branch</div>
		  		   </td>
  		       </div>
  		       <div>
  				  <td id="StateFilterLabel">State</td>
	              <td id="StateFilter">
					  <select name="stateId" id="stateId">
		              <#if stateIdName?has_content>
			 	             <option value='${stateId?if_exists}'>${stateIdName?if_exists}</option> 
	 	              </#if>
					  <#if !stateIdName?has_content>
							 <option value=''>Select State</option>
					  </#if>
				       <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					  </#list> 
					 </select>
				      <div id="dispComField" style="color:red; font-stlye:bold; display:none">Please Select Branch</div>
		  		   </td>
  		       </div>
		  </tr> 
				
				<#--<tr>
				  <td align='left' valign='middle' nowrap="nowrap">${uiLabelMap.Branch} :</td>
				  
				  <#if branchId?exists && branchId?has_content>  
								  	  		   
				  <td valign='middle'><font color="green">          
				    <select name="branchId2" id="branchId2" onchange="javascript:clearData();" >
				    <option value="${branchId}" >${branchIdName}</option>       		
				  </td>
				  
				  <#else>
				  <td valign='middle'><font color="green">          
				    <select name="branchId2" id="branchId2" onchange="javascript:clearData();" />         		
				  </td>
				   </#if>
				  
				  <td><br/></td>
				</tr>-->
				
				
				
				<tr>
				  <td align='left' valign='middle' nowrap="nowrap">Period :</td>
				  <td valign='middle'><font color="green">          
				  <input type="hidden" name="isFormSubmitted" id="isFormSubmitted" value="Y"  />     
				     <select name="period" id="periodName">
      		 	    <#if period?has_content>
      		 	    	<option value='${period}'>${periodName}</option>
      		 	    </#if>
					<option value='One_Month'>Last One Month</option>
					<option value='Three_Month'>Last Three  Months</option>
					<option value='Six_Month'>Last  Six Months</option>
			  </select>   		
				  </td>
				  <td><br/></td>
				</tr>
			 
			   <tr><td><br/></td></tr>
			   
			   
			   <tr>
				  <td align='left' valign='middle' nowrap="nowrap"></td>
				  <td valign='middle'><font color="green">          
				     <input type="submit" style="padding:.3em" value="Find" name="submit" id="submit" onclick= 'javascript:formSubmit(this);' />     		
				  </td>
				  <td><br/></td>
				</tr>
			  
		</table>
            </form>
 	

 