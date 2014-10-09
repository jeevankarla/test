<script type="text/javascript">
	
	<#if !unitId?has_content>
		<#if shedId?has_content>
			<#if !(unitMapsList?has_content)>
			setProcurementUnitsDropDown(shedId);
			</#if>
			$('[name=shedId]').val($('[name=shedId]').val());
		</#if>
	</#if>
	
</script>