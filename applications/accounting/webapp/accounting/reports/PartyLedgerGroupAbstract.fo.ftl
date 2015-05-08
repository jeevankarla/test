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

<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".3in">
        <fo:region-body margin-top="1.3in" margin-bottom=".6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>     
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "PartyLedgerGroupReport.pdf")}
 <#if partyMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
				 <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="9pt" font-weight="bold" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;            MOTHER DAIRY, YALAHANKA KMF UNIT : GKVK POST.BANGALORE-560 065     Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
				<#if reportName == "SUBLEDGER">
                <#assign glAccount=delegator.findOne("GlAccount",{"glAccountId",fromGlAccountId},true)>
                <fo:block  text-align="right"  keep-together="always"  white-space-collapse="false" font-weight="bold">GL ACCOUNT :${glAccount.description?if_exists}(${fromGlAccountId})                &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
                <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">SUB-LEDGER ABSTRACT FOR THE PERIOD ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
                <#else>
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold"  font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
				<fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">${reportName} FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
                </#if>
				<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<#assign partyWiseList = partyMap.entrySet()>
				<#assign grdDebit=0>
                <#assign grdCredit=0>
                <fo:block>
                    <fo:table>
				    <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="35%"/>
			        <fo:table-column column-width="20%"/>
			        <fo:table-column column-width="20%"/>
                    <fo:table-body>
                    	<fo:table-row>
                    	   <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">SL.NO</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">PARTY ID</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="center" font-weight="bold"  font-size="10pt" white-space-collapse="false">PARTY NAME</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">DEBIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">CREDIT</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                        <fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                <#assign sno=1>		
				<#list partyWiseList as partyList>
                <#assign acctgDetails=partyList.getValue()>
			    <#assign totDebit=0>
                <#assign totCredit=0>			
			    <#assign closingTot=0>

                <#assign openBal=0>
				<#assign openCredit=0>
                <#assign openDebit=0>
                <#if openingBalMap?has_content>
                <#assign openBal=openingBalMap.get(partyList.getKey())>
                <#if openBal gte 0>
                <#assign openDebit=openBal>
                <#else>
                <#assign openCredit=((-1)*openBal)>
                </#if> 
                </#if>
				<#if acctgDetails?has_content || openDebit !=0 || openCredit!=0>
                    <#list acctgDetails as acctgTrans>
                          <#assign totDebit=totDebit+acctgTrans.get("debit")>
                          <#assign totCredit=totCredit+acctgTrans.get("credit")>
						</#list>
                			<#assign totDebit=totDebit+openDebit>
                            <#assign totCredit=totCredit+openCredit>
                        <#assign closingTot=totDebit-totCredit>
                        <#assign closingDebit=0>
                        <#assign closingCredit=0>   
                        <#if closingTot gte 0>
                        <#assign closingDebit=closingTot>
                        <#else>
                        <#assign closingCredit=closingTot>
                        </#if>
                        <#assign grdDebit=grdDebit+closingDebit>
                        <#assign grdCredit=grdCredit+((-1)*closingCredit)>
                      <#if closingDebit !=0 || closingCredit !=0>
                		<fo:table-row>
                			<fo:table-cell>
                    			 <fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false">${sno}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left"   font-size="12pt" white-space-collapse="false">${partyList.getKey()}</fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block   text-align="left"   font-size="12pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyList.getKey(), false)}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${closingDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="12pt" white-space-collapse="false">${((-1)*closingCredit)?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<#assign sno=sno+1>
                        </#if>
                      </#if>
             	      </#list>
                         <fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
             
	          <fo:block>
                    <fo:table>
				    <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="35%"/>
			        <fo:table-column column-width="20%"/>
			        <fo:table-column column-width="20%"/>
                    <fo:table-body>
                    	<fo:table-row>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">TOTALS :</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grdDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${grdCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                		<fo:table-row>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false">NET AMOUNT :</fo:block>  
                			</fo:table-cell>
                			<#assign clsGrdDebit=0>
                            <#assign clsGrdCredit=0>
                            <#assign balance=grdDebit-grdCredit> 
                            <#if balance gte 0>
                             <#assign clsGrdDebit=balance> 
                            <#else>
                              <#assign clsGrdCredit=balance>
                            </#if>
                            <#if clsGrdDebit !=0> 
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${clsGrdDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<#else>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                            </#if>
                            <#if clsGrdCredit !=0>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false">${((-1)*clsGrdCredit)?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                            <#else>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                            </#if>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
              <fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------</fo:block>
			 </fo:flow>
			 </fo:page-sequence>
			  <#else>
    	<fo:page-sequence master-reference="main">
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	</fo:page-sequence>	
    </#if>  
</fo:root>
</#escape>