<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

	


<script type="text/javascript">
function setOrgPartyId() {

	$(".commonPartyId").each(function() {
		$(this).val($("#partyId").val());
    });
}	
</script>


	<div>
		<div class="screenlet">
			<div class="screenlet-title-bar">
				<h3></h3>
			</div>
			<div class="screenlet-body">
				<table class="basic-table hover-bar h3" style="border-spacing: 0 10px;">
					
				   <tr class="alternate-row">
						<form id="paySlipEmployeewise" name="paySlipEmployeewise" mothed="post" action="<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>" target="_blank">
							<table class="basic-table" cellspacing="5">
								<tr class="alternate-row">
									<td width="15%"><span class='h3'>Pay Slip</span></td>
									<td><span class='h3'>Period Id</span>
										<select name="customTimePeriodId" id="customTimePeriodId" class='h5'>
											<#assign customTimePeriodList=customTimePeriodList?sort>
											<#list customTimePeriodList as customTimePeriod>
											 <#if defaultTimePeriodId?exists && (defaultTimePeriodId == customTimePeriod.customTimePeriodId)>
						      					<option value='${customTimePeriod.customTimePeriodId?if_exists}' selected="selected">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
						      					<#else>
						      						<option value='${customTimePeriod.customTimePeriodId?if_exists}' >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.fromDate, "dd MMMMM, yyyy")} -${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(customTimePeriod.thruDate, "dd MMMMM, yyyy")}</option>
						                  		</option>
						      				</#if>
									      </#list>
										</select>
									<input type="hidden" name="OrganizationId" class="commonPartyId">
									<input  type="hidden"  id="partyId"   name="partyId" value = "${userLogin.partyId}"/>
									<span class='h3'><input type="submit" value="PDF" onClick="javascript:appendParams('paySlipEmployeewise', '<@ofbizUrl>PrintPaySlipsPdf.pdf</@ofbizUrl>');" class="buttontext"/></span></td>
								</tr>
							</table>
						</form>
				   	</tr>
							</table>
						</form>
					</tr>
			   	</table>
			</div>
		</div>
	</div>


 