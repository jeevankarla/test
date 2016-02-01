<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	
<link type="text/css" href="<@ofbizContentUrl>/images/jquery/plugins/multiSelect/jquery.multiselect.css</@ofbizContentUrl>" rel="Stylesheet" />


<style type="text/css">
 .labelFontCSS {
    font-size: 13px;
}

</style>

<script type="text/javascript">



	$(document).ready(function(){
		//jQuery("input[name='ply']").parent().parent().hide();
		//jQuery("input[name='count']").parent().parent().hide();
	});
	
	function getCategoryMembers() {
		var productCategoryId = $('#productCategoryId').val();
		if((productCategoryId)){
			$.ajax({
				 type: "POST",
	             url: 'getCategoryMembersAjax',
	             data: {productCategoryId : productCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						productCategoryMembers = result["productCategoryMembers"];
						var paramName = 'childProductCategoryId';
						var optionList = '';   		
						var list= productCategoryMembers;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.productId + " >" + innerList.productName + "</option>";          			
				      		}
				      	}		
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	
	<#--
	function getChildCategories() {
		var productCategoryId = $('#productCategoryId').val();
		
		if((productCategoryId)){

			$.ajax({
				 type: "POST",
	             url: 'getChildCategoriesAjax',
	             data: {primaryParentCategoryId : productCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						childCategoriesList = result["childCategoriesList"];
						var paramName = 'childProductCategoryId';
						var optionList = '';   		
						var list= childCategoriesList;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.productCategoryId + " >" + innerList.description + "</option>";          			
				      		}
				      	}		
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	
	function getSubChildCategories() {
		var childProductCategoryId = $('#childProductCategoryId').val();
		
		if((childProductCategoryId)){

			$.ajax({
				 type: "POST",
	             url: 'getChildCategoriesAjax',
	             data: {primaryParentCategoryId : childProductCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						childCategoriesList = result["childCategoriesList"];
						var paramName = 'subChildProductCategoryId';
						var optionList = '';   		
						var list= childCategoriesList;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.productCategoryId + " >" + innerList.description + "</option>";          			
				      		}
				      	}		
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	-->
	
	function getProductFeatures() {
		var childProductCategoryId = $('#childProductCategoryId').val();
		if((childProductCategoryId)){
			$.ajax({
				 type: "POST",
	             url: 'getProductFeaturesAjax',
	             data: {productCategoryId : childProductCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						featuresByCategoryMap = result["featuresByCategoryMap"];
						productFeatureCategoryList = result["productFeatureCategoryList"];
						productFeatureCategoryIdsList = result["productFeatureCategoryIdsList"]
						
						if (productFeatureCategoryList) {	
				        	for(var i=0 ; i<productFeatureCategoryList.length ; i++){
								var featureCategory = productFeatureCategoryList[i];	          
								
								var tableElement = '<tr><td align="right" class="label">'+featureCategory.description+':</td>';
								var tableElement = tableElement + '<td width="10%">';	
								
								if((featureCategory.productFeatureCategoryId == "COUNT") || (featureCategory.productFeatureCategoryId == "PLY")){
									tableElement = tableElement + '<input type="text" name="'+featureCategory.productFeatureCategoryId+'" id="'+featureCategory.productFeatureCategoryId+'" size="30" maxlength="60" class="alphaonly" autocomplete="off" />';
								}
								else{
									var tableElement = tableElement + '<select name="'+featureCategory.productFeatureCategoryId+'" id="'+featureCategory.productFeatureCategoryId+'"> ';	
									var featuresList = featuresByCategoryMap[featureCategory.productFeatureCategoryId];
											
									for(var j=0 ; j<featuresList.length ; j++){
										var feature = featuresList[j];
										tableElement = tableElement + '<option value = " + feature.productFeatureId + " >'+ feature.productFeatureId+'</option>';
									}	
									
									tableElement = tableElement + '</select>';	
								}
								
								tableElement = tableElement + '</td>';		
								tableElement = tableElement + '</tr>';	
										 
								$('#features tr:last').after(tableElement);		             
				      		}
				      		
				      	}	
						
						$('#features tr:last').after('<tr><td></td><td><input type="submit" class="smallSubmit" value="Submit"/></td></tr>');
						
						
						
						<#--
						var paramName = 'subChildProductCategoryId';
						var optionList = '';   		
						var list= childCategoriesList;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.productCategoryId + " >" + innerList.description + "</option>";          			
				      		}
				      	}		
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	-->
				      	
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	
	<#--
	function getAttributeTypes() {
		var subChildProductCategoryId = $('#subChildProductCategoryId').val();
		
		if((subChildProductCategoryId)){

			$.ajax({
				 type: "POST",
	             url: 'getAttributeTypesAjax',
	             data: {productCategoryId : subChildProductCategoryId},
	             dataType: 'json',
		            
				 success:function(result){
					if(result["_ERROR_MESSAGE_"] || result["_ERROR_MESSAGE_LIST_"]){
	                    alert('Error Fetching available types');
					}else{
						packingTypes = result["packingTypes"];
						spinningTypes = result["spinningTypes"];
						processingTypes = result["processingTypes"];
						plyandcountTypes = result["plyandcountTypes"];
						uomTypes = result["uomTypes"];
						
						var paramName = 'packingTypes';
						var optionList = '';   		
						var list= packingTypes;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.attrName + " >" + innerList.attrName + "</option>";          			
				      		}
				      	}	
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
				      	var paramName = 'spinningTypes';
				      	var optionList = '';   		
						var list= spinningTypes;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.attrName + " >" + innerList.attrName + "</option>";          			
				      		}
				      	}	
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
				      	var paramName = 'processingTypes';
				      	var optionList = '';   		
						var list= processingTypes;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.attrName + " >" + innerList.attrName + "</option>";          			
				      		}
				      	}	
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
				      	var paramName = 'plyandcountTypes';
				      	var optionList = '';   		
						var list= plyandcountTypes;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.attrName + " >" + innerList.attrName + "</option>";          			
				      		}
				      	}	
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
				      	jQuery("input[name='ply']").parent().parent().show();
						jQuery("input[name='count']").parent().parent().show();
				      	
				      	var paramName = 'uomTypes';
				      	var optionList = '';   		
						var list= uomTypes;
						if (list) {	
							optionList += "<option value=''></option>";		       				        	
				        	for(var i=0 ; i<list.length ; i++){
								var innerList=list[i];	              			             
				                optionList += "<option value = " + innerList.attrName + " >" + innerList.attrName + "</option>";          			
				      		}
				      	}	
				      	if(paramName){
				      		jQuery("[name='"+paramName+"']").html(optionList);
				      	}
				      	
				      	
				      	
				      	
				      	
				      	
					}								 
				},
				error: function(){
					alert("record not found");
				}							
			});
			
		}		
	}
	-->
	
	
	
	
	


 
</script>

<div class="full">
      
    <div class="screenlet-body">
    	<form method="post" name="CreateNewProduct" action="<@ofbizUrl>createNewProduct</@ofbizUrl>" id="CreateNewProduct">  
	
      		<table id="features" name="features" width="100%" border="0" cellspacing="0" cellpadding="0">
        		 <tr>
			         <td align='right' class='label'>Product Category:</td>
			         <td valign='middle'>
	             		<select name="productCategoryId" id="productCategoryId" onchange="getCategoryMembers()">
			          		<option value=''></option>
			                <#list productCategoryList as productCategory>    
								<option value='${productCategory.productCategoryId}'>${productCategory.categoryName}</option>                  	
			      			</#list>            
						</select>	
			         </td>
			      </tr>
			      
			      <tr>
			         <td align='right' class='label'>Product Type:</td>
			         <td valign='middle'>
	             		<select name="childProductCategoryId" id="childProductCategoryId" onchange="getProductFeatures()">
						</select>	
			         </td>
			      </tr> 
			      <tr>
			       	 <td>
			       	 	&nbsp;
			       	 </td>
			      </tr>
			      <tr>
			       	 <td>
			       	 	<FONT COLOR="#04B431"><b>
			       	 		Product Features
			       	 	</FONT>
			       	 </td>
			      </tr>
			      
			      <#--
			      <tr>
			         <td align='right' class='label'>Sub Child Type:</td>
			         <td valign='middle'>
	             		<select name="subChildProductCategoryId" id="subChildProductCategoryId" onchange="getAttributeTypes()">
						</select>	
			         </td>
			      </tr>
			      
			      <tr>
			         <td align='right' class='label' width="10%">Select:</td>
			         <td width="10%">
	             		<select name="packingTypes" id="packingTypes">
						</select>	
			         </td>
			         <td width="10%">
	             		<select name="spinningTypes" id="spinningTypes">
						</select>	
			         </td>
			         <td width="10%">
	             		<select name="processingTypes" id="processingTypes">
						</select>	
			         </td>
			         <td width="10%">
	             		<select name="uomTypes" id="uomTypes">
						</select>	
			         </td>
			         <td  width="50%"></td>
			      </tr> 
			      
		      		<tr>
					    <td class="label">Ply</td>
					    <td>
					      	<input type="text" name="ply" id="ply" size="30" maxlength="60" class="alphaonly" autocomplete="off" />
					    </td>
					</tr> 
					<tr>
					    <td class="label">Count</td>
					    <td>
					      	<input type="text" name="count" id="count" size="30" maxlength="60" class="alphaonly" autocomplete="off" />
					    </td>
					</tr>
				-->	 
					
			      
			      
      		</table>
      		<#--
	     		<table id="features" name="features" cellpadding="0" cellspacing="0">
					<tr>
					    <td class="label">Count</td>
					    <td>
					      	<input type="text" name="count" id="count" size="30" maxlength="60" class="alphaonly" autocomplete="off" />
					    </td>
					</tr>			
				</table>
			-->
		</form>

	 </div> 
    
</div>
