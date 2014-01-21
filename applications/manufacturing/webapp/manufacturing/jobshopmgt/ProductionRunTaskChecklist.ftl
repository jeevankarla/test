	<script type="text/javascript"> 
	
jQuery(document).ready(function() {
	
	/* see if anything is previously checked and reflect that in the view*/
	jQuery(".checklist input:checked").parent().addClass("selected");
	
	/* handle the user selections */
	jQuery(".checklist .checkbox-select").click(
		function(event) {
			event.preventDefault();
			jQuery(this).parent().addClass("selected");
			jQuery(this).parent().find(":checkbox").attr("checked","checked");
			jQuery(this).parent().find(":checkbox").attr("value", "Y");								
		}
	);
	
	jQuery(".checklist .checkbox-deselect").click(
		function(event) {
			event.preventDefault();
			jQuery(this).parent().removeClass("selected");
			jQuery(this).parent().find(":checkbox").removeAttr("checked");		
			jQuery(this).parent().find(":checkbox").attr("value", "N");					
		}
	);
	
	jQuery("#ProductionRunTaskChecklist").submit(
		function() {
			jQuery(".checklist input[type=checkbox]:not(:checked)").each(function() {
            	var addHiddenField = jQuery("<input>")
					.attr("class", '_temp')
					.attr("type", 'hidden')
					.attr("name", jQuery(this).attr("name"))
					.val('N')
            	jQuery("#ProductionRunTaskChecklist").append($(addHiddenField));			
			});
		}
	);
});
	
	</script> 
	
	
<style type="text/css"> 
 

 
.checklist {
	list-style: none;
	margin: 0;
	padding: 0;
}
 
.checklist li {
	float: left;
	margin-right: 10px;
	background: url(/images/jquery/plugins/prettycheckboxes/checkboxbg.gif) no-repeat 0 0;
	width: 105px;
	height: 150px;
	position: relative;
	font: normal 11px/1.3 "Lucida Grande","Lucida","Arial",Sans-serif;
}
 
.checklist li.selected {
	background-position: -105px 0;
}
 
.checklist li.selected .checkbox-select {
	display: none;
}
 
.checkbox-select {
	display: block;
	float: left;
	position: absolute;
	top: 118px;
	left: 10px;
	width: 85px;
	height: 23px;
	background: url(/images/jquery/plugins/prettycheckboxes/select.gif) no-repeat 0 0;
	text-indent: -9999px;
}
 
.checklist li input {
	display: none;	
}
 
a.checkbox-deselect {
	display: none;
	color: white;
	font-weight: bold;
	text-decoration: none;
	position: absolute;
	top: 120px;
	right: 10px;
}
 
.checklist li.selected a.checkbox-deselect {
	display: block;
}
 
.checklist li label {
	display: block;
	text-align: center;
	padding: 8px;
}
 
.sendit {
	display: block;
	float: left;
	top: 118px;
	left: 10px;
	width: 115px;
	height: 34px;
	border: 0;
	cursor: pointer;
	background: url(/images/jquery/plugins/prettycheckboxes/sendit.gif) no-repeat 0 0;
	text-indent: -9999px;
	margin: 20px 0;
}
 
</style> 
	
 
 
	<form id="ProductionRunTaskChecklist" action="<@ofbizUrl>updateRoutingTaskChecklist</@ofbizUrl>" method="POST"> 
        <input name="productionRunId" type="hidden" value="${productionRunId}"/>
        <input name="workEffortId" type="hidden" value="${startTaskId}"/>        	
		<fieldset> 
			<ul class="checklist"> 
        	<#list taskAttributes as taskAttribute>		
        		<#if taskAttribute.attrValue?exists>
        			<#assign inputValue=taskAttribute.attrValue>
        		<#else>
					<#assign inputValue='N'>
        		</#if>        		
				<li> 
                	<input name="attrName_o_${taskAttribute_index}" value="${taskAttribute.attrName}" type="hidden"/>
					<input name="attrValue_o_${taskAttribute_index}" value="${inputValue}" type="checkbox" <#if inputValue == 'Y'>checked="checked"</#if>/> 
					<label for="choice_a">${taskAttribute.attrName}</label> 
				<#if !isChecklistComplete>
					<a class="checkbox-select" href="#">Select</a> 
					<a class="checkbox-deselect" href="#">Cancel</a> 
				</#if>
				</li> 
			</#list>				
			</ul> 
			<div style="clear: both;"></div> 
		<#if !isChecklistComplete>
			<input type="submit" style="margin: 20px 0 0 20px;" name="submitButton" value="Submit">
		</#if>
		</fieldset> 
	</form>