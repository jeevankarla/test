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

<script type="application/javascript">
	$(function() {
  		$("#boothId").focus();
	});
</script>
 
<!-- Sales Order Entry -->
<form method="post" name="changeindentinit" action="<@ofbizUrl>ChangeIndentMIS</@ofbizUrl>">     
      <table width="60%" border="0" cellspacing="0" cellpadding="0">     
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext h2'>
            
             <input type="hidden" size="22" maxlength="60" name="effectiveDate" id="effectiveDate" value="${defaultEffectiveDateTime!''}"/>
            	${defaultEffectiveDate}
            </div>
          </td>
        </tr> 
        <tr><td><br/></td></tr>             
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>Supply Type:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'> 
      		<select name="productSubscriptionTypeId" class='h2'>
                <#list prodSubTypes as prodSubType>    
                  	<#assign isDefault = false>                
                    <#if prodSubType.enumId = "CASH">
                      <#assign isDefault = true>
                    </#if>
                     <#if prodSubType.enumId != "CARD">                 		     
						<option value='${prodSubType.enumId}'<#if isDefault> selected="selected"</#if>>
	                    	${prodSubType.description}
	                  	</option>
                  	</#if>
      			</#list>            
			</select>
          </td>
        </tr>           
        <tr><td><br/></td></tr>             
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Booth}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>          
             <input class='h2' type="text" size="10" maxlength="10" name="boothId" id="boothId" value=""/>          
          </td>
        </tr>        
      </table>
 </form>
 

 

