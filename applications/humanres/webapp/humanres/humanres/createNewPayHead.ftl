<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<script type="text/javascript">

	function makeDatePicker(fromDateId ,thruDateId){
		$( "#"+fromDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function(selectedDate) {
			date = $(this).datepicker('getDate');
			var maxDate = new Date(date.getTime());
	        	//maxDate.setDate(maxDate.getDate() + 31);
				$("#"+thruDateId).datepicker( "option", {setDate: '0',minDate: '-1y', maxDate: '+1y'}).datepicker('setDate', date);
				//$( "#"+thruDateId ).datepicker( "option", "minDate", selectedDate );
			}
		});
		$( "#"+thruDateId ).datepicker({
			dateFormat:'MM d, yy',
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				//$( "#"+fromDateId ).datepicker( "option", "maxDate", selectedDate );
			}
		});
	}

	$(document).ready(function(){
		makeDatePicker("ruleFromDate","ruleFromDate");
		makeDatePicker("ruleThruDate","ruleThruDate");
		makeDatePicker("actionFromDate","actionFromDate");
		makeDatePicker("actionThruDate","actionThruDate");
		makeDatePicker("actFromDate","actFromDate");
		makeDatePicker("actThruDate","actThruDate");
		$('#ui-datepicker-div').css('clip', 'auto');		
	});
	
</script>

	<div class="screenlet">
  		<div class="screenlet-title-bar">
    		<h3>${uiLabelMap.PayrollRulesEditScreen}</h3>
  		</div>
  		<#-- ======================= Rules ======================== -->
  		<div class="screenlet-body">
    		<table cellspacing="0" class="basic-table">
      			<tr class="header-row">
			        <td width="10%"><b>${uiLabelMap.PayrollRuleId}</b></td>
			        <td width="80%"><b>${uiLabelMap.PayrollRuleName}</b></td>
			        <td width="10%"><b>&nbsp;</b></td>
			 	</tr>
  				<tr valign="middle">
    				<td class="label"><b> ${uiLabelMap.PayrollRule} :</b></td>
    				<td>
    					<form method="post" name="createPayHeadRules" action="<@ofbizUrl>createOrUpdatePayHeadRules</@ofbizUrl>">
    						<#if (PayrollBenDedRules.size() != 0 )>
    							<#list PayrollBenDedRules as PayrollBenDedRule>
	    							<input type="text" size="20" name="ruleName" value="${(PayrollBenDedRule.ruleName)?if_exists}"/>
							     	<b>${uiLabelMap.PayHeadTypeId}:</b>
							     	<select name="payHeadTypeId" size="1">
							     		<#list BenefitTypes as benefit>
								     		<#if PayrollBenDedRule.payHeadTypeId?exists && ((PayrollBenDedRule.payHeadTypeId) == (benefit.benefitTypeId))>
						      					<option value='${benefit.benefitTypeId?if_exists}' selected="selected">${(benefit.get("description",locale))?if_exists}</option>
						      				<#else>
						      					<option value="${(benefit.benefitTypeId)?if_exists}">${(benefit.get("description",locale))?if_exists}</option>
						      				</#if>
						      			</#list>
										<#list DeductionTypes as deduction>
											<#if PayrollBenDedRule.payHeadTypeId?exists && ((PayrollBenDedRule.payHeadTypeId) == (deduction.deductionTypeId))>
					      						<option value='${deduction.deductionTypeId?if_exists}' selected="selected">${(deduction.get("description",locale))?if_exists}</option>
					      					<#else>
					      						<option value="${(deduction.deductionTypeId)?if_exists}">${(deduction.get("description",locale))?if_exists}</option>
					      					</#if>
										</#list>
			  						</select>
			  						<b>From Date :</b> <input  type="text" size="10pt" id="ruleFromDate"   name="ruleFromDate" value="${(PayrollBenDedRule.fromDate)?if_exists?string("MMMM dd, yyyy")}"/>
									<b>Thru Date :</b> <input  type="text" size="10pt" id="ruleThruDate"   name="ruleThruDate" <#if PayrollBenDedRule.thruDate?has_content>value="${(PayrollBenDedRule.thruDate)?if_exists?string("MMMM dd, yyyy")}</#if>"/>
			  						<form method="post" action="<@ofbizUrl>createOrUpdatePayHeadRules</@ofbizUrl>">
						            	<input type="hidden" name="payrollBenDedRuleId" value="${(PayrollBenDedRule.payrollBenDedRuleId)?if_exists}" />
						            	<input type="submit" value="${uiLabelMap.CommonUpdate}" />
						     		</form>
						     	</#list>
    						<#else>
    							<input type="text" size="20" name="ruleName"/>
						     	<b>${uiLabelMap.PayHeadTypeId}:</b>
						     	<select name="payHeadTypeId" size="1">
									<#list BenefitTypes as benefit>
		        						<option value="${(benefit.benefitTypeId)?if_exists}">${(benefit.get("description",locale))?if_exists}</option>
									</#list>
									<#list DeductionTypes as deduction>
		        						<option value="${(deduction.deductionTypeId)?if_exists}">${(deduction.get("description",locale))?if_exists}</option>
									</#list>
		  						</select>
		  						<b>From Date :</b> <input  type="text" size="10pt" id="ruleFromDate"   name="ruleFromDate" />
								<b>Thru Date :</b> <input  type="text" size="10pt" id="ruleThruDate"   name="ruleThruDate" />
		  						<input type="submit" value="${uiLabelMap.CommonCreate}" />
    						</#if>
	  					</form>
        			</td>
    			</tr>
    			<#-- ======================= Conditions ======================== -->
    			<tr><td><hr /></td><td colspan="2"></td></tr>
    			<tr valign="top">
    				<td align="right" class="label">${uiLabelMap.ConditionsForRule}  : <#list PayrollBenDedRules as PayrollBenDedRule>${(PayrollBenDedRule.payrollBenDedRuleId)?if_exists}</#list></td>
    				<td colspan="2">
          				<table cellspacing="0" class="basic-table">
            				<tr class="row-level-two">
              					<!-- if cur seq id is a number and is greater than max, set new max for input box prefill below -->
	              				<td></td>
	              				<td>
	              					<#if (PayrollBenDedConds.size() != 0 )>
	              						<#list PayrollBenDedConds as PayrollBenDedCond>
	              							<form method="post" name="createOrUpdatePayHeadCondition" action="<@ofbizUrl>createOrUpdatePayHeadCondition</@ofbizUrl>">
		                						<b>${uiLabelMap.PayrollCondition} </b><br />
						                  		<input type="hidden" name="payrollBenDedRuleId" value="${(PayrollBenDedCond.payrollBenDedRuleId)?if_exists}"/>
						                  		<select name="inputParamEnumId" size="1">
			      									<#list inputParamEnums as inputParamEnum>
			      										<#if PayrollBenDedCond.inputParamEnumId?exists && ((PayrollBenDedCond.inputParamEnumId) == (inputParamEnum.enumId))>
									      					<option value='${inputParamEnum.enumId?if_exists}' selected="selected">${(inputParamEnum.get("description",locale))?if_exists}</option>
									      				<#else>
									      					<option value="${(inputParamEnum.enumId)?if_exists}">${(inputParamEnum.get("description",locale))?if_exists}</option>
									      				</#if>
			      									</#list>
			              						</select>
			              						<select name="operatorEnumId" size="1">
			      									<#list condOperEnums as condOperEnum>
			      										<#if PayrollBenDedCond.operatorEnumId?exists && ((PayrollBenDedCond.operatorEnumId) == (condOperEnum.enumId))>
									      					<option value='${condOperEnum.enumId?if_exists}' selected="selected">${(condOperEnum.get("description",locale))?if_exists}</option>
									      				<#else>
									      					<option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.get("description",locale))?if_exists}</option>
									      				</#if>
			      									</#list>
			                  					</select>
			              						<b>${uiLabelMap.ConditionValue}:</b>
			              						<input type="text" size="20" name="condValue" value="${(PayrollBenDedCond.condValue)?if_exists}" />
			              						<form method="post">
									            	<input type="hidden" name="payrollBenDedCondSeqId" value="${(PayrollBenDedCond.payrollBenDedCondSeqId)?if_exists}" />
									            	<input type="submit" value="${uiLabelMap.CommonUpdate}" />
									     		</form>
			              					</form>
	              						</#list>
	              						<form method="post" name="createPayHeadCondition" action="<@ofbizUrl>createOrUpdatePayHeadCondition</@ofbizUrl>">
	                						<b>${uiLabelMap.NewPayrollCondition} </b><br />
					                  		<#list PayrollBenDedRules as PayrollBenDedRule>
				                  				<input type="hidden" name="payrollBenDedRuleId" value="${(PayrollBenDedRule.payrollBenDedRuleId)?if_exists}"/>
				                  			</#list>
					                  		<select name="inputParamEnumId" size="1">
		      									<#list inputParamEnums as inputParamEnum>
		                    						<option value="${(inputParamEnum.enumId)?if_exists}">${(inputParamEnum.get("description",locale))?if_exists}</option>
		      									</#list>
		              						</select>
		              						<select name="operatorEnumId" size="1">
		      									<#list condOperEnums as condOperEnum>
		                    						<option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.get("description",locale))?if_exists}</option>
		      									</#list>
		                  					</select>
		              						<b>${uiLabelMap.ConditionValue}:</b>
		              						<input type="text" size="20" name="condValue" />
		              						<input type="submit" value="${uiLabelMap.CommonCreate}" />
		              					</form>	
	              					<#else>
	              						<form method="post" name="createPayHeadCondition" action="<@ofbizUrl>createOrUpdatePayHeadCondition</@ofbizUrl>">
	                						<b>${uiLabelMap.NewPayrollCondition} </b><br />
	                						<#list PayrollBenDedRules as PayrollBenDedRule>
				                  				<input type="hidden" name="payrollBenDedRuleId" value="${(PayrollBenDedRule.payrollBenDedRuleId)?if_exists}"/>
				                  			</#list>
					                  		<select name="inputParamEnumId" size="1">
		      									<#list inputParamEnums as inputParamEnum>
		                    						<option value="${(inputParamEnum.enumId)?if_exists}">${(inputParamEnum.get("description",locale))?if_exists}</option>
		      									</#list>
		              						</select>
		              						<select name="operatorEnumId" size="1">
		      									<#list condOperEnums as condOperEnum>
		                    						<option value="${(condOperEnum.enumId)?if_exists}">${(condOperEnum.get("description",locale))?if_exists}</option>
		      									</#list>
		                  					</select>
		              						<b>${uiLabelMap.ConditionValue}:</b>
		              						<input type="text" size="20" name="condValue" />
		              						<input type="submit" value="${uiLabelMap.CommonCreate}" />
		              					</form>
	              					</#if>
					          	</td>
					        	<td></td>
					      	</tr>
	          			</table>
	        		</td>
	      		</tr>
	      		<#-- ======================= Actions ======================== -->
	      		<tr><td><hr /></td><td colspan="2"></td></tr>
	      		<tr valign="top">
        			<td align="right" class="label">${uiLabelMap.ActionForRule}  : <#list PayrollBenDedRules as PayrollBenDedRule>${(PayrollBenDedRule.payrollBenDedRuleId)?if_exists}</#list></td>
        			<td colspan="2">
	          			<table cellspacing="0" class="basic-table">
		    				<#assign actionClass = "2">
		            		<tr>
				              	<td>
				                	<div>
				                		<#if (PayHeadPriceActions.size() != 0 )>
	              							<#list PayHeadPriceActions as PayHeadPriceAction>
	              							<tr>
				                				<form method="post" name="createOrUpdatePayHeadPriceAction" action="<@ofbizUrl>createOrUpdatePayHeadPriceAction</@ofbizUrl>">
						                			<td>
							                			<b>${uiLabelMap.ActionType}:</b>
							                  			<input type="hidden" name="payrollBenDedRuleId" value="${(PayHeadPriceAction.payrollBenDedRuleId)?if_exists}"/>
									                    <select name="PriceActionTypeId">
									    					<#list PayHeadPriceActionTypes as PriceActionType>
									    						<#if PayHeadPriceAction.payHeadPriceActionTypeId?exists && ((PayHeadPriceAction.payHeadPriceActionTypeId) == (PriceActionType.payHeadPriceActionTypeId))>
											      					<option value='${PriceActionType.payHeadPriceActionTypeId?if_exists}' selected="selected">${(PriceActionType.get("payHeadPriceActionTypeId",locale))?if_exists}</option>
											      				<#else>
											      					<option value="${(PriceActionType.payHeadPriceActionTypeId)?if_exists}">${(PriceActionType.get("payHeadPriceActionTypeId",locale))?if_exists}</option>
											      				</#if>
									    					</#list>
									                    </select>
									                     </td>
									                    <td>
									                    <b>Acctng Formula Id:</b></td>
									                    <td><@htmlTemplate.lookupField formName="createPayHeadAction" name="acctgFormulaId" id="acctgFormulaId" size="10pt" fieldFormName="LookupAcctgFormula" value = "${(PayHeadPriceAction.acctgFormulaId)?if_exists}"/>
									                    <b>${uiLabelMap.ServiceName}:&nbsp;</b><input type="text" size="10pt" name="serviceName" value = "${PayHeadPriceAction.customPriceCalcService?if_exists}"/>
									                    </td>
									                     <td>
									                    <b>Amount : </b><input type="text" size="10pt" name="amount" value="${(PayHeadPriceAction.amount)?if_exists}"/>
									                    <b>From Date : </b><input  type="text" size="10pt" id="actionFromDate"   name="actionFromDate" value="${(PayHeadPriceAction.fromDate)?if_exists?string("MMMM dd, yyyy")}"/>
														<b>Thru Date : </b><input  type="text" size="10pt" id="actionThruDate"   name="actionThruDate" <#if PayHeadPriceAction.thruDate?has_content>value="${(PayHeadPriceAction.thruDate)?if_exists?string("MMMM dd, yyyy")}"</#if>/>
					              						<form method="post">
											            	<input type="hidden" name="payHeadPriceActionSeqId" value="${(PayHeadPriceAction.payHeadPriceActionSeqId)?if_exists}" />
											            	<input type="submit" value="${uiLabelMap.CommonUpdate}" />
											     		</form>
			              						 	</td>
			              						</form>
				                			</#list>
				                			</tr>
				                			<tr>
				                			<form method="post" name="createPayHeadAction" action="<@ofbizUrl>createOrUpdatePayHeadPriceAction</@ofbizUrl>">
					                			<td>
						                			<b>${uiLabelMap.NewActionType}:</b>
						                			<#list PayrollBenDedRules as PayrollBenDedRule>
						                  				<input type="hidden" name="payrollBenDedRuleId" value="${(PayrollBenDedRule.payrollBenDedRuleId)?if_exists}"/>
						                  			</#list>
								                    <select name="PriceActionTypeId">
								    					<#list PayHeadPriceActionTypes as PriceActionType>
								                      		<option value="${(PriceActionType.payHeadPriceActionTypeId)?if_exists}">${(PriceActionType.get("payHeadPriceActionTypeId",locale))?if_exists}</option>
								    					</#list>
								                    </select>
								                     </td>
								                    <td>
								                    <b>Acctng Formula Id:</b></td>
								                    <td><@htmlTemplate.lookupField formName="createPayHeadAction" name="acctgFormulaId" id="acctgFormulaId" size="10pt" fieldFormName="LookupAcctgFormula"/>
								                    <b>${uiLabelMap.ServiceName}:&nbsp;</b><input type="text" size="10pt" name="serviceName" />
								                    </td>
								                     <td>
								                    <b>Amount : </b><input type="text" size="10pt" name="amount" value=""/>
								                    <b>From Date : </b><input  type="text" size="10pt" id="actFromDate"   name="actionFromDate"/>
													<b>Thru Date : </b><input  type="text" size="10pt" id="actThruDate"   name="actionThruDate"/>
				              						<input type="submit" value="${uiLabelMap.CommonCreate}" />
		              						 	</td>
		              						</form>
		              						</tr>
				                		<#else>
				                			<form method="post" name="createPayHeadAction" action="<@ofbizUrl>createOrUpdatePayHeadPriceAction</@ofbizUrl>">
					                			<td>
						                			<b>${uiLabelMap.NewActionType}:</b>
						                			<#list PayrollBenDedRules as PayrollBenDedRule>
						                  				<input type="hidden" name="payrollBenDedRuleId" value="${(PayrollBenDedRule.payrollBenDedRuleId)?if_exists}"/>
						                  			</#list>
								                    <select name="PriceActionTypeId">
								    					<#list PayHeadPriceActionTypes as PriceActionType>
								                      		<option value="${(PriceActionType.payHeadPriceActionTypeId)?if_exists}">${(PriceActionType.get("payHeadPriceActionTypeId",locale))?if_exists}</option>
								    					</#list>
								                    </select>
								                     </td>
								                    <td>
								                    <b>Acctng Formula Id:</b></td>
								                    <td><@htmlTemplate.lookupField formName="createPayHeadAction" name="acctgFormulaId" id="acctgFormulaId" size="10pt" fieldFormName="LookupAcctgFormula"/>
								                    <b>${uiLabelMap.ServiceName}:&nbsp;</b><input type="text" size="10pt" name="serviceName" />
								                    </td>
								                     <td>
								                    <b>Amount : </b><input type="text" size="10pt" name="amount" value=""/>
								                    <b>From Date : </b><input  type="text" size="10pt" id="actionFromDate"   name="actionFromDate"/>
													<b>Thru Date : </b><input  type="text" size="10pt" id="actionThruDate"   name="actionThruDate"/>
				              						<input type="submit" value="${uiLabelMap.CommonCreate}" />
		              						 	</td>
		              						</form>
				                		</#if>
				                	</div>
				              	</td>
			            	</tr>
	          			</table>
	        		</td>
	      		</tr>
	      		<tr><td>&nbsp;</td></tr>
	      	</table>
		</div>
	</div>

	
