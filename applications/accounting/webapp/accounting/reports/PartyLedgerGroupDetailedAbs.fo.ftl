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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="12in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".1in">
        <fo:region-body margin-top="2in" margin-bottom=".6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>     
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "PartyLedgerGroupReport.pdf")}
 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
               <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" font-weight="bold" white-space-collapse="false">UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
               <#assign roId = parameters.division>
              	 <#assign roHeader = roId+"_HEADER">
              	 <#assign roSubHeader = roId+"_HEADER01">
              	 <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roHeader}, true)>
				<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : roSubHeader}, true)>
			    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD.</fo:block>
					<fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists} </fo:block>
				  	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
				<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MM-yyyy")}</fo:block>
				
				<#if reportName == "SUBLEDGER">
                <#assign glAccount=delegator.findOne("GlAccount",{"glAccountId",fromGlAccountId},true)>
                <fo:block  text-align="right"  keep-together="always"  white-space-collapse="false" font-weight="bold">GL ACCOUNT :${glAccount.description?if_exists}(${fromGlAccountId})                &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
                <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">SUB-LEDGER ABSTRACT FOR THE PERIOD ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
                <#else>
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-weight="bold"  font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>
				<fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">${reportName} FOR THE PERIOD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>
                </#if>
                
				<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block>
            </fo:static-content>		
            <#if partyMap?has_content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            	<fo:block font-size="11pt">
					<fo:table>
					    <fo:table-column column-width="7%"/>
				        <fo:table-column column-width="13%"/>
				        <fo:table-column column-width="18%"/>
				        <fo:table-column column-width="17%"/>
				         <fo:table-column column-width="23%"/>
				        <fo:table-column column-width="20%"/>
		                    <fo:table-body>
		                    	<fo:table-row>
		                    	   <fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false"></fo:block>  
		                			</fo:table-cell>
		                			<fo:table-cell>
		                			   
                                    	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">OPENING</fo:block>  
		                			</fo:table-cell>
		                			
									<fo:table-cell>
									<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">DURING THE PERIOD</fo:block>  
		                			</fo:table-cell>
                                    <fo:table-cell>
                                    <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
		                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="10pt" white-space-collapse="false">UNAPPLIED</fo:block>  
		                			</fo:table-cell>
		                		</fo:table-row>
		                	</fo:table-body>
		             </fo:table>   			
                </fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 	
                <fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<#assign partyWiseList = partyMap.entrySet()>
				<#assign grdOpenDebit=0>
                <#assign grdOpenCredit=0>
                <#assign grdCurrDebit=0>
                <#assign grdCurrCredit=0>
               <#assign grdUnAppCredit=0>
               <#assign grdUnAppDebit=0>
                <fo:block>
                    <fo:table>
				    <fo:table-column column-width="5%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="15%"/>
			        <fo:table-column column-width="10%"/>
			         <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
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
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">DEBIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">CREDIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">DEBIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">CREDIT</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">NET AMOUNT</fo:block>  
                			</fo:table-cell>
                		</fo:table-row>
                        <fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                <#assign sno=1>		
				<#list partyWiseList as partyList>
                <#assign acctgDetails=partyList.getValue()>
			    <#assign totDebit=0>
                <#assign totCredit=0>			
			    <#assign closingTot=0>
                <#assign totUnAppAmt=0>

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

				<#assign unAppAmt=0>
				<#assign unAppCredit=0>
                <#assign unAppDebit=0>
                <#if closingUnAppMap?has_content>
                <#assign unAppAmt=closingUnAppMap.get(partyList.getKey())>
	                <#if unAppAmt gte 0>
	                	<#assign unAppDebit=unAppAmt>
                	<#else>
                		<#assign unAppCredit=((-1)*unAppAmt)>
                	</#if> 
                </#if>
				<#if acctgDetails?has_content || openDebit !=0 || openCredit!=0 || unAppCredit!=0 || unAppDebit!=0>
                    <#list acctgDetails as acctgTrans>
                          <#assign totDebit=totDebit+acctgTrans.get("debit")>
                          <#assign totCredit=totCredit+acctgTrans.get("credit")>
						</#list>
                		<fo:table-row>
                			<fo:table-cell>
                    			 <fo:block  keep-together="always" text-align="left"   font-size="10pt" white-space-collapse="false">${sno}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left"   font-size="10pt" white-space-collapse="false">${partyList.getKey()}</fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block   text-align="left"   font-size="10pt" >${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyList.getKey(), false)}</fo:block>  
                			</fo:table-cell>
                			<#assign grdOpenDebit=grdOpenDebit+openDebit>
                            <#assign grdOpenCredit=grdOpenCredit+openCredit>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${openDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${openCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                            <#assign grdCurrDebit = grdCurrDebit+totDebit>
                            <#assign grdCurrCredit = grdCurrCredit+totCredit>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${totDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${totCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<#assign grdUnAppCredit=grdUnAppCredit+unAppCredit>
                            <#assign grdUnAppDebit=grdUnAppDebit+unAppDebit>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"   font-size="10pt" white-space-collapse="false">${unAppDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${unAppCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<#assign finalAmt=0>
                            <#assign finalAmt=(totDebit+openDebit+unAppDebit)-(openCredit+totCredit+unAppCredit)> 
                            <#if finalAmt gt 0>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${finalAmt?if_exists?string("##0.00")}(Dr)</fo:block>  
                			</fo:table-cell>
                            <#elseif finalAmt lt 0>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false">${((-1)*finalAmt)?if_exists?string("##0.00")}(Cr)</fo:block>  
                			</fo:table-cell> 
                             <#else>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right"  font-size="10pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             </#if>
                		</fo:table-row>
                		<#assign sno=sno+1>
                      </#if>
             	      </#list>
                         <fo:table-row>
                			<fo:table-cell>
                			<fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                			</fo:table-cell>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
             
	          <fo:block>
                    <fo:table>
				    <fo:table-column column-width="5%"/>
			        <fo:table-column column-width="8%"/>
			        <fo:table-column column-width="15%"/>
			        <fo:table-column column-width="10%"/>
			         <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="12%"/>
                    <fo:table-body>
                    	<fo:table-row>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="12pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="left" font-weight="bold"  font-size="11pt" white-space-collapse="false">TOTALS :</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grdOpenDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grdOpenCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                            <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grdCurrDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grdCurrCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                            <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grdUnAppDebit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false">${grdUnAppCredit?if_exists?string("##0.00")}</fo:block>  
                			</fo:table-cell>
							<#assign grdfinalAmt=0>
                            <#assign grdfinalAmt=(grdOpenDebit+grdCurrDebit+grdUnAppDebit)-(grdOpenCredit+grdCurrCredit+grdUnAppCredit)> 
                            <#if grdfinalAmt gt 0>
                			<fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${grdfinalAmt?if_exists?string("##0.00")}(Dr)</fo:block>  
                			</fo:table-cell>
                            <#elseif grdfinalAmt lt 0>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold"  font-size="10pt" white-space-collapse="false">${((-1)*grdfinalAmt)?if_exists?string("##0.00")}(Cr)</fo:block>  
                			</fo:table-cell> 
                             <#else>
                             <fo:table-cell>
                    			<fo:block  keep-together="always" text-align="right" font-weight="bold" font-size="10pt" white-space-collapse="false"></fo:block>  
                			</fo:table-cell>
                             </#if>
                		</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
              <fo:block font-size="10pt">------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			 </fo:flow>
			
			  <#else>
    	
		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			<fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
		</fo:flow>
	
    </#if>
     </fo:page-sequence>  
</fo:root>
</#escape>