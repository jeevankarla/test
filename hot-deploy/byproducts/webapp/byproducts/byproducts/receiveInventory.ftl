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
<script language="JavaScript" type="text/javascript">
    function setNow(field) { eval('document.selectAllForm.' + field + '.value="${nowTimestamp}"'); }
</script>

	<br>
        <#-- Single Product Receiving -->
          <form method="post" action="<@ofbizUrl>byProdReceiveSingleInventoryProduct</@ofbizUrl>" name="selectAllForm">
            <table class="basic-table" cellspacing="0">
            <input type="hidden" name="quantityRejected" value="0"/>
            <input type="hidden" name="isReceipt" value="true"/>
              <tr>
                <td width="6%" align="right" nowrap="nowrap" class="label">Party Code</td>           
                <td width="74%">
                  <select name="facilityId" id="facilityId" size="1">
                    <#list storesList as store>
                      <option value="${store.inventoryFacilityId}">${store.inventoryFacilityId}</option>
                    </#list>
                  </select>
                </td>
              </tr>
              <tr>
                <td width="6%" align="right" nowrap="nowrap" class="label">Product Code</td>
                <td width="74%">
                  <@htmlTemplate.lookupField value="${requestParameters.productId?if_exists}" formName="selectAllForm" name="productId" id="productId" fieldFormName="LookupProduct"/>
                </td>
              </tr>
              <tr>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductDateReceived}</td>
                <td width="74%">
                  <@htmlTemplate.renderDateTimeField name="effectiveDate" event="" action="" value="${nowTimestamp}"  className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="22" maxlength="25" id="effectiveDate" dateType="date" shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName=""/>
                  <#-- <a href="#" onclick="setNow("datetimeReceived")" class="buttontext">[Now]</a> -->
                </td>
              </tr>
              <tr>
                <td width="6%" align="right" nowrap="nowrap" class="label">${uiLabelMap.ProductQuantityAccepted}</td>
                <td width="74%">
                  <input type="text" name="quantity" size="5" value="${defaultQuantity?default(1)?string.number}"/>
                </td>
              </tr>
              <tr>
                <td width="6%">&nbsp;</td>
                <td width="6%"><input type="submit" value="${uiLabelMap.CommonSubmit}" /></td>
              </tr>
         </table>
         <script language="JavaScript" type="text/javascript">
           document.selectAllForm.quantityAccepted.focus();
         </script>
    </form>

        
