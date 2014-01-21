<#if errorList?has_content>
<div style="color:red;">
<#list errorList as err>
${err},
</#list>
these booths does't have MicrNumber.. !!
</div>
</#if>
<div class="screenlet">
	<div class="screenlet-title-bar">
      <h3>ECS Report</h3>
	<div class="screenlet-body">
      	<textarea rows="180" cols="180">
${ecsBuffer}
      	</textarea>
    </div>  
 </div>
</div>

