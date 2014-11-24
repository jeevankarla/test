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
<form id="glReconciledFinAccountTrans" name="glReconciledFinAccountTransForm" method="post" action="<@ofbizUrl>callReconcileFinAccountTrans?clearAll=Y</@ofbizUrl>">
  <input name="_useRowSubmit" type="hidden" value="Y"/>
  <input name="finAccountId" type="hidden" value="${finAccountId}"/>
  <input name="glReconciliationId" type="hidden" value="${glReconciliationId}"/>
  <div class="screenlet"> 
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">${uiLabelMap.FinAccountReconciliationCurrent}</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
    <!--
      <#if	hasFinRecEditPermission>
      	<a href="<@ofbizUrl>EditFinAccountReconciliations?finAccountId=${finAccountId}&amp;glReconciliationId=${glReconciliationId}</@ofbizUrl>" class="buttontext">${uiLabelMap.CommonEdit}</a>
      </#if>
      -->
      <#assign finAcctTransCondList = delegator.findByAnd("FinAccountTrans", {"glReconciliationId" : glReconciliationId, "statusId" : "FINACT_TRNS_CREATED"})>
      <#if finAcctTransCondList?has_content && hasFinRecEditPermission>
        <a href="javascript:document.CancelBankReconciliationForm.submit();" class="buttontext">${uiLabelMap.AccountingCancelBankReconciliation}</a>
      </#if>
      <#if currentGlReconciliation?has_content>
        <table>
          <tr>
            <td><span class="label">${uiLabelMap.FinAccountReconciliationName}</span></td>
            <td>${currentGlReconciliation.glReconciliationName?if_exists}</td>
          </tr>
          <#if currentGlReconciliation.statusId?exists>
            <tr>
              <td><span class="label">${uiLabelMap.CommonStatus}</span></td>
              <#assign currentStatus = currentGlReconciliation.getRelatedOneCache("StatusItem")>  
              <td>${currentStatus.description?if_exists}</td>
            </tr>
          </#if>
          <tr>
            <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledDate}</span></td>
            <td>${currentGlReconciliation.reconciledDate?if_exists}</td>
          </tr>
          <tr>
            <td><span class="label">${uiLabelMap.AccountingOpeningBalance}</span></td>
            <td><@ofbizCurrency isoCode=finAccount.currencyUomId amount=currentGlReconciliation.openingBalance?default('0')/></td>
          </tr>
          <#if currentGlReconciliation.reconciledBalance?exists>
            <tr>
              <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledBalance}</span></td>
              <td><@ofbizCurrency isoCode=finAccount.currencyUomId amount=currentGlReconciliation.reconciledBalance?default('0')/></td>
            </tr>
          </#if>
          <#if currentClosingBalance?exists>
            <tr>
              <td><span class="label">${uiLabelMap.FormFieldTitle_closingBalance}</span></td>
              <td><@ofbizCurrency isoCode=finAccount.currencyUomId amount=currentClosingBalance/></td>
            </tr>
          </#if>
        </table>
      </#if>
    </div>
  </div>
  <div class="screenlet"> 
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">${uiLabelMap.FinAccountReconciliationPrevious}</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
      <#if previousGlReconciliation?has_content>
        <table>
          <tr>
            <td><span class="label">${uiLabelMap.FinAccountReconciliationName}</span></td>
            <td>${previousGlReconciliation.glReconciliationName?if_exists}</td>
          </tr>
          <#if previousGlReconciliation.statusId?exists>
            <tr>
              <td><span class="label">${uiLabelMap.CommonStatus}</span></td>
              <#assign previousStatus = previousGlReconciliation.getRelatedOneCache("StatusItem")> 
              <td>${previousStatus.description?if_exists}</td>
            </tr>
          </#if>
          <tr>
            <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledDate}</span></td>
            <td>${previousGlReconciliation.reconciledDate?if_exists}</td>
          </tr>
          <tr>
            <td><span class="label">${uiLabelMap.AccountingOpeningBalance}</span></td>
            <td><@ofbizCurrency isoCode=finAccount.currencyUomId amount=previousGlReconciliation.openingBalance?default('0')/></td>
          </tr>
          <#if previousGlReconciliation.reconciledBalance?exists>
            <tr>
              <td><span class="label">${uiLabelMap.FormFieldTitle_reconciledBalance}</span></td>
              <td><@ofbizCurrency isoCode=finAccount.currencyUomId amount=previousGlReconciliation.reconciledBalance?default('0')/></td>
            </tr>
          </#if>
          <#if previousClosingBalance?exists>
            <tr>
              <td><span class="label">${uiLabelMap.FormFieldTitle_closingBalance}</span></td>
              <td><@ofbizCurrency isoCode=finAccount.currencyUomId amount=previousClosingBalance/></td>
            </tr>
          </#if>
        </table>
      </#if>
    </div>
      

    
   
  </div>
  <div class="section_right">
      <span class="label">${uiLabelMap.AccountingTotalCapital} </span><@ofbizCurrency amount=transactionTotalAmount.grandTotal isoCode=defaultOrganizationPartyCurrencyUomId/> 
      <#if isReconciled == false>
        <input type="submit" value="${uiLabelMap.AccountingReconcile}"/>
      </#if>
    </div>
  
  
  <div class="screenlet"> 
    <div class="screenlet-title-bar">
      <ul>
        <li class="h3">${uiLabelMap.FinAccounttTransAssociatedToReconciliation}</li>
      </ul>
      <br class="clear"/>
    </div>
    <div class="screenlet-body">
      <#if finAccountTransList?has_content>
        <table class="basic-table hover-bar" cellspacing="0">
          <tr class="header-row-2">
            <th>FinTransId</th>
            <th>${uiLabelMap.FormFieldTitle_finAccountTransType}</th>
            <th>${uiLabelMap.PartyParty}</th>
             <th>PaymentParty</th>
            <th>${uiLabelMap.FormFieldTitle_transactionDate}</th>
            <th>InstrumentNo.</th>
           
            <th>${uiLabelMap.CommonAmount}</th>
            <th>${uiLabelMap.FormFieldTitle_paymentId}</th>
            <th>${uiLabelMap.OrderPaymentType}</th>
            <th>${uiLabelMap.FormFieldTitle_paymentMethodTypeId}</th>
            <th>${uiLabelMap.CommonStatus}</th>
            <th>${uiLabelMap.CommonComments}</th>
              <#--<th>${uiLabelMap.FormFieldTitle_glTransactions}</th>-->
          </tr>
          <#assign alt_row = false/>
          <#list finAccountReconciliationList as finAccountTrans>
            <#assign payment = "">
            <#assign payments = "">
            <#assign status = "">
            <#assign paymentType = "">
            <#assign paymentMethodType = "">
            <#assign partyName = "">
            
           <#--   <field name="finAccountTransTypeId" title="FinAcntTransType"><display-entity entity-name="FinAccountTransType"  description="${description}"/></field>
        <field name="partyId" title="Owner Party"><display></display></field>
        <field name="paymentPartyName" title="PaymentParty"><display></display></field>
        <field name="transactionDate"><display></display></field>
        <field name="instrumentNo" title="InstrumentNo"><display></display></field>
    	<field name="amount" title="Amount(Rs)"><display/></field>
        <field name="paymentId" title="Payment Id" widget-style="buttontext"><display></display></field>
        <field name="paymentType" title="Payment Type"><display size="20"  /></field>
        <field name="paymentMethodTypeId" title="PaymentMethodType"><display/></field>
        <field name="statusId" title="Status"><display-entity entity-name="StatusItem"  description="${description}"/></field>
        <field name="comments" title="comments"><display/></field>-->
          
            <#assign finAccountTransType = delegator.findOne("FinAccountTransType", {"finAccountTransTypeId" : finAccountTrans.finAccountTransTypeId}, true)>
            <#if finAccountTrans.statusId?has_content>
              <#assign status = delegator.findOne("StatusItem", {"statusId" : finAccountTrans.statusId}, true)>
            </#if>
            <#if finAccountTrans.partyId?has_content>
              <#assign partyName = (delegator.findOne("PartyNameView", {"partyId" : finAccountTrans.partyId}, true))>
            </#if>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
              <td>
                  <input name="finAccountTransId_o_${finAccountTrans_index}" type="hidden" value="${finAccountTrans.finAccountTransId}"/>
                  <input name="organizationPartyId_o_${finAccountTrans_index}" type="hidden" value="${defaultOrganizationPartyId}"/>
                  <input id="finAccountTransId_${finAccountTrans_index}" name="_rowSubmit_o_${finAccountTrans_index}" type="hidden" value="Y"/>
                  ${finAccountTrans.finAccountTransId?if_exists}</td>
              <td>${finAccountTransType.description?if_exists}</td>
              <td><#if partyName?has_content>${(partyName.firstName)!} ${(partyName.lastName)!} ${(partyName.groupName)!}<a href="/partymgr/control/viewprofile?partyId=${partyName.partyId}">[${(partyName.partyId)!}]</a></#if></td>
                   <td><#if finAccountTrans.paymentPartyName?has_content> ${finAccountTrans.paymentPartyName?if_exists}<a href="/partymgr/control/viewprofile?partyId=${finAccountTrans.paymentPartyId?if_exists}">[${finAccountTrans.paymentPartyId?if_exists}]</a></#if></td>
              <td>${finAccountTrans.transactionDate?if_exists}</td>
              <td>${finAccountTrans.instrumentNo?if_exists}</td>
              <td><@ofbizCurrency amount=finAccountTrans.amount isoCode=finAccount.currencyUomId/></td>
              <td>
                <#if finAccountTrans.paymentId?has_content>
                  <a href="<@ofbizUrl>paymentOverview?paymentId=${finAccountTrans.paymentId?if_exists}</@ofbizUrl>">${finAccountTrans.paymentId?if_exists}</a>
                </#if>
              </td>
              <td><#if finAccountTrans.paymentType?has_content>${finAccountTrans.paymentType?if_exists}</#if></td>
              <td><#if finAccountTrans.paymentMethodTypeId?has_content>${finAccountTrans.paymentMethodTypeId?if_exists}</#if></td>
              <td><#if status?has_content>${status.description?if_exists}</#if></td>
              <td><#if finAccountTrans.comments?has_content>${finAccountTrans.comments?if_exists}</#if></td>
              <#if finAccountTrans.statusId == "FINACT_TRNS_CREATED" && hasFinRecEditPermission>
                <td align="center"><a href="javascript:document.reomveFinAccountTransAssociation_${finAccountTrans.finAccountTransId}.submit();" class="buttontext">${uiLabelMap.CommonRemove}</a></td>
              <#else>
                <td/>
              </#if>
              <#if finAccountTrans.paymentId?has_content && hasFinRecEditPermission>
                <td align="center">
                  <a id="toggleGlTransactions_${finAccountTrans.finAccountTransId}" href="javascript:void(0)" class="buttontext">${uiLabelMap.FormFieldTitle_glTransactions}</a>
                  <#include "ShowGlTransactions.ftl"/>
                  <script type="text/javascript">
                       jQuery(document).ready( function() {
                            jQuery("#displayGlTransactions_${finAccountTrans.finAccountTransId}").dialog({autoOpen: false, modal: true,
                                    buttons: {
                                    '${uiLabelMap.CommonClose}': function() {
                                        jQuery(this).dialog('close');
                                        }
                                    }
                               });
                       jQuery("#toggleGlTransactions_${finAccountTrans.finAccountTransId}").click(function(){jQuery("#displayGlTransactions_${finAccountTrans.finAccountTransId}").dialog("open")});
                       });
                  </script>
                </td>
              </#if>
            </tr>
            <#assign alt_row = !alt_row/>
          </#list>
        </table>
      </#if>
    </div>
    <div class="right">
      <span class="label">${uiLabelMap.AccountingTotalCapital} </span><@ofbizCurrency amount=transactionTotalAmount.grandTotal isoCode=defaultOrganizationPartyCurrencyUomId/> 
      <#if isReconciled == false>
        <input type="submit" value="${uiLabelMap.AccountingReconcile}"/>
      </#if>
    </div>
  </div>
</form>
<form name="CancelBankReconciliationForm" method="post" action="<@ofbizUrl>cancelBankReconciliation</@ofbizUrl>">
  <input name="finAccountId" type="hidden" value="${finAccountId}"/>
  <input name="glReconciliationId" type="hidden" value="${glReconciliationId}"/>
</form>
<#list finAccountTransList as finAccountTrans>
  <form name="reomveFinAccountTransAssociation_${finAccountTrans.finAccountTransId}" method="post" action="<@ofbizUrl>reomveFinAccountTransAssociation</@ofbizUrl>">
    <input name="finAccountTransId" type="hidden" value="${finAccountTrans.finAccountTransId}"/>
    <input name="finAccountId" type="hidden" value="${finAccountTrans.finAccountId}"/>
    <input name="glReconciliationId" type="hidden" value="${glReconciliationId}"/>
  </form>
</#list>
