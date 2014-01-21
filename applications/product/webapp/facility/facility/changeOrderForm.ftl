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



 
<!-- Sales Order Entry -->
<form method="post" name="salesentryform" action="<@ofbizUrl>initsimpleorderentry</@ofbizUrl>">     
      <input name="effectiveDate" value="${defaultEffectiveDateTime!''}" type="hidden"/>

      <table width="60%" border="0" cellspacing="0" cellpadding="0">     
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.SupplyDate}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext h2'>
               	${defaultEffectiveDate}<!--<@htmlTemplate.renderDateTimeField name="effectiveDate" id="effectiveDate" value="${value!''}" className="required" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="18" maxlength="30" id="item1" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/> -->               
            </div>
          </td>
        </tr> 
        <tr><td><br/></td></tr>             
        <tr>
          <td>&nbsp;</td>
          <td align='left' valign='middle' nowrap="nowrap"><div class='h2'>${uiLabelMap.Booth}:</div></td>
          <td>&nbsp;</td>
          <td valign='middle'>
            <div class='tabletext'>
              <@htmlTemplate.lookupField value='${thisOriginFacilityId?if_exists}' formName="salesentryform" name="originFacilityId" id="originFacilityId" className="required" fieldFormName="LookupFacility" size="15"/>
            </div>
          </td>
        </tr>        
      </table>
 </form>
 

 

