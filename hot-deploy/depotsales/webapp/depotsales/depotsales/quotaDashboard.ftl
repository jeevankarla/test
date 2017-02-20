
<input type="hidden" name="effectiveDate1" id="effectiveDate1" value="${effectiveDate}">


<script type="text/javascript">
	var stateJSON=${StringUtil.wrapString(stateJSON)!'[]'};
	
	var branchByState1=${StringUtil.wrapString(branchByState1)!'[]'};
	
	var branchProductSroreMap = ${StringUtil.wrapString(branchProductSroreMap)!'[]'};
	//for Month Picker
$(document).ready(function(){

   //getbranchesByState();

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
	
	function getbranchesByState(){
       	var stateId=$("#stateWise").val();
       	var optionList = '';
			var list= stateJSON[stateId];
			if (list && list.length>0) {	
				optionList += "<option value = " + "All" + " >" +"All "+ "</option>";	       				        	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	     
	                optionList += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
	      		}//end of main list for loop
	      	}
	      	jQuery("#branchId2").html(optionList);
       }	

  
  function getbranchesByRO(){
       	var stateId=$("#roWise").val();
       	
       	
       	var optionList = '';
			var list= branchByState1[stateId];
			if (list && list.length>0) {	
				optionList += "<option value = " + "All" + " >" +"All "+ "</option>";	       				        	
	        	for(var i=0 ; i<list.length ; i++){
					var innerList=list[i];	     
	                optionList += "<option value = " + innerList['value'] + " >" +innerList['label']+" </option>";          			
	      		}//end of main list for loop
	      	}
	      	jQuery("#branchId2").html(optionList);
       }	
	
	
$(document).ready(function(){

var effectiveDate1 = $("#effectiveDate1").val();

$("#branchId2").parent().parent().parent().hide();
$("#roWise").parent().parent().parent().hide();
$("#partyClassificationId2").parent().parent().parent().hide();

<#if findData?has_content>
<#if findData?has_content && lom=='stateRadio'>
	 $('#stateRadio').attr('checked', 'checked'); 
	 $("#branchId2").parent().parent().parent().hide();
	 $("#stateWise").parent().parent().parent().show();
	 $("#roWise").parent().parent().parent().hide();
	  $("#partyId").parent().parent().parent().show();
	  $("#partyClassificationId2").parent().parent().parent().hide();

<#else>
 
	  $('#RoRadio').attr('checked', 'checked'); 
	  $("#roWise").parent().parent().parent().show();
	  $("#branchId2").parent().parent().parent().show();
	  $("#partyId").parent().parent().parent().show();
	  $("#partyClassificationId2").parent().parent().parent().show();
	  $("#stateWise").parent().parent().parent().hide();
 
 </#if>
 
 </#if>

 $('input:radio').change(function() {
     
     var selected = $(this).val();
     
     if(selected == 'stateRadio'){
	     $("#branchId2").parent().parent().parent().hide();
	     $("#stateWise").parent().parent().parent().show();
	      $("#roWise").parent().parent().parent().hide();
	      $("#partyId").parent().parent().parent().show();
	      $("#partyClassificationId2").parent().parent().parent().hide();
	      $("#partyId").val('');
	      getbranchesByRO();
	      
     }else  if(selected == 'RoRadio'){
          getbranchesByRO();
          $("#roWise").parent().parent().parent().show();
	      $("#branchId2").parent().parent().parent().show();
	      $("#stateWise").parent().parent().parent().hide();
	      $("#partyClassificationId2").parent().parent().parent().show();
	       $("#partyId").parent().parent().parent().show();
	        
	         $("#partyId").val('');
      }
      
    });
    
    
    
    
    
    var regionalList = "";
  
  <#list formatROList as eachList>
	regionalList = regionalList + "<option value='${eachList.payToPartyId}' >${eachList.productStoreName}</option>";
  </#list>
    
    
  var branchList = "";
  branchList = "<option value='All' >All</option>";
  
   
  <#list formatList as eachList>
	branchList = branchList + "<option value='${eachList.payToPartyId}' >${eachList.productStoreName}</option>";
  </#list>
  
     $("#branchId2").html(branchList);
     $("#roWise").html(regionalList);
     
     
     <#if findData?has_content>
     $("#partyId").val(localStorage.getItem("partyId"));
     $("#stateWise").val(localStorage.getItem("state"));
     $("#branchId2").val(localStorage.getItem("branch"));
     $("#roWise").val(localStorage.getItem("rovar"));
     $("#partyClassificationId2").val(localStorage.getItem("partyClassificationId"));
     </#if>
      $("#effectiveDate").val(effectiveDate1);
     
		    
		
	});
	
	
	function autoCompletePartyId(){
	
	   var branchId2 = $("#branchId2").val();
	   if(branchId2 == "All")
	    branchId2 = "";
	   var stateWise = $("#stateWise").val();
	   var roWise = $("#roWise").val();
	   var partyClassificationId = $("#partyClassificationId2").val();
	   
	   
	   var stateRadio = $("#stateRadio").val();
	   var RoRadio = $("#RoRadio").val();
	   
	    var selected = $('input[name=lom]:checked').val();
	   
	   
	   
	   if(selected == "RoRadio"){
	     stateWise = "";
	   }else if(selected == "stateRadio"){
	      branchId2 = "";
	      partyClassificationId = "";
	      roWise = "";
	   }
	   
			//var productStoreId = branchProductSroreMap[branchId2];
			
			
		      $("#partyId").autocomplete({ 
		      
		      		
		      		source: function( request, response ) {
	        			$.ajax({
	          					url: "LookupBranchCustomers",
	          					dataType: "html",
	          					data: {
	            					ajaxLookup: "Y",
	            					term : request.term,
	            					state:stateWise,
	            					roWise:roWise,
	            					partyIdFrom:branchId2,
	            					partyClassificationGroupId : partyClassificationId
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
	
	
	 function eanableCustomer(){
	 
	 if($("#branchId2").val() != "All"){
	       $("#partyId").parent().parent().parent().show();
	 }else{
	 
	    $("#partyId").parent().parent().parent().hide();
	 }
	 
	 }
	 function setData(){
	 
	 	 localStorage.setItem("partyId", $("#partyId").val());
	 	 
	 	 localStorage.setItem("state", $("#stateWise").val());
	 	 
	 	 localStorage.setItem("branch", $("#branchId2").val());
	 	 
	 	 localStorage.setItem("rovar", $("#roWise").val());
	 	  
	 	 localStorage.setItem("partyClassificationId", $("#partyClassificationId2").val());
	 	
	 	  $("#findData").val("Y");
	 
	 }
	 
	
	
</script>


<style type="text/css">

	<style type="text/css">
	 	.labelFontCSS {
	    	font-size: 13px;
		}
		.form-style-8{
		    max-width: 600px;
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


        
      <form method="post" name="QuotaDashboard" id="QuotaDashboard" action="<@ofbizUrl>QuotaDashboardAuth</@ofbizUrl> " class="basic-form">
          
		  <table width="60%" border="0" cellspacing="0" cellpadding="0" class="form-style-8">
		  
		         <input type="hidden" name="findData" id="findData" /> 
				<tr>
				<td width="40%">
				 <tr>
				  <td align='left' valign='middle' nowrap="nowrap">Search By:</td>
				  <td valign='middle'><font color="green"> 
				  <input type="radio" name="lom" id="stateRadio" value="stateRadio" checked> State
                  <input type="radio" name="lom" id="RoRadio" value="RoRadio"> Regional Offices   		
				  </td>
				  <td><br/></td>
				</tr>
				 <tr><td><br/></td></tr>
				<tr>
				
				<tr>
				<td width="40%">
				 <tr>
				  <td align='left' valign='middle' nowrap="nowrap">State :</td>
				  
				  <td valign='middle'><font color="green">          
				    <select name="stateWise" id="stateWise" >
				     <option value="IN-TN">TAMILNADU</option>
				     <#list  stateListJSON as stateListJSON>
						<option value='${stateListJSON.value?if_exists}'>${stateListJSON.label?if_exists}</option>
					 </#list> 
				  </select>       		
				  </td>
				  
				  <td><br/></td>
				</tr>
				
				<tr>
				
				  <td align='left' valign='middle' nowrap="nowrap">Regional Offices :</td>
				  <td valign='middle'><font color="green">          
				    <select name="roWise" id="roWise" onchange="javascript:getbranchesByRO();"/>         		
				  </td>
				  
				</tr>
				<tr><td><br/></td></tr>
				<tr>
				
				  <td align='left' valign='middle' nowrap="nowrap">${uiLabelMap.Branch} :</td>
				  <td valign='middle'><font color="green">          
				    <select name="branchId2" id="branchId2" onchange="javascript:eanableCustomer();" />         		
				  </td>
				  
				</tr>
				
				<tr><td><br/></td></tr>
				
				 <tr>
				  <td align='left' valign='middle' nowrap="nowrap">Party Classification :</td>
				  <td valign='middle'><font color="green"> 
				    <select name="partyClassificationId2" id="partyClassificationId2" >
				    <option value=""></option>     
				     <#list  partyClassificationList as partyClassificationList>
						<option value='${partyClassificationList.partyClassificationGroupId?if_exists}'>${partyClassificationList.description?if_exists}</option>
					 </#list> 
				  </select>       		
				  </td>
				  <td><br/></td>
				</tr>
			   
				<tr><td><br/></td></tr>
				
				<tr>
				  <td align='left' valign='middle' nowrap="nowrap">Customer Name:</td>
				  <td valign='middle'><font color="green">          
				     <input type='text' id='partyId' name='partyId' placeholder="Enter Customer Name" onfocus='javascript:autoCompletePartyId();' size='20'/><p><label  align="left" id="partyTooltip" style="color: blue"></label><p>  		
				  </td>
				  <td><br/></td>
				</tr> 
				
			 
			   <tr><td><br/></td></tr>
			  
			   <tr>
				  <td align='left' valign='middle' nowrap="nowrap">Month :</td>
				  <td valign='middle'><font color="green">          
				    <input  type="text" size="18pt" id="effectiveDate" readonly  name="effectiveDate" onmouseover='monthPicker()' class="monthPicker" /> 		
				  </td>
				  <td><br/></td>
				</tr>
			   
			   <tr><td><br/></td></tr>
			   
			   <tr>
				  <td align='left' valign='middle' nowrap="nowrap"></td>
				  <td valign='middle'><font color="green">  
				     <input type="submit" style="padding:.3em" value="Find" name="submit" id="submit" onclick= 'javascript:setData();' />     		
				  </td>
				  <td><br/></td>
				</tr>
			  
		</table>
            </form>
 	

 