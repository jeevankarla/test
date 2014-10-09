<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<script type="application/javascript">
   <#if !unitName?has_content>
		<#if shedName?has_content>
			<#if !(unitMapsList?has_content)>
				setOutPutEntryShedUnitsDropDownByValue(jQuery('[name=shedId]').val());
			</#if>
			getTimePeriodsByUnit(jQuery('[name=shedId]').val(),jQuery('[name=facilityId]').val())
		</#if>
		if(jQuery('[name=shedId]').val()){
			getTimePeriodsByUnit(jQuery('[name=shedId]').val());
		}
   <#else>
	<#if unitName?has_content>
		getTimePeriodsByUnit($('#shedId').val(),${unitId});
	</#if>
  </#if>	
</script>