<script type="text/javascript">
	
	<#if !unitId?has_content>
		<#if shedId?has_content>
			setShedFacUnitsDropDown(shedId);
			getUnitRouteByValue($('[name=unitId]').val());
			$('[name=shedId]').val($('[name=shedId]').val());
			$('[name=unitId]').val($('[name=unitId]').val());
			
		</#if>
	<#else>
		<#if unitId?has_content>
			getUnitRouteByValue(${unitId});
			$('[name=shedId]').val($('[name=shedId]').val());
			$('[name=unitId]').val($('[name=unitId]').val());
		</#if>
	</#if>
	
</script>