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
<#assign iaGtot = 0>
<#assign returnGAmount = 0>
<#assign caGtot = 0>
<#assign cashGTot = 0>
<#assign chqGTot = 0>
<#assign chqRetnGTot = 0>
<#assign challanGTot = 0>
<#assign totalCatPaidGAmnt = 0>
<#assign eaGtot = 0>
<#assign saGtot = 0>
<#assign netBalGrandTot = 0>                	
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
         <fo:region-body margin-top="0.9in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              ${uiLabelMap.KMFDairySubHeader}</fo:block>				
              		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              DAIRY PARTICULARS FOR DATE/MONTH :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd,MMM yy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd,MMM yy")}                                    ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
            		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                 	<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="60pt"/> 
               	    <fo:table-column column-width="110pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="72pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		  <fo:table-body>
            		  <fo:table-row>
                    		<fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">SNO</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">RETAILER</fo:block>  
	                             <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">CODE</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">RETAILER</fo:block> 
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">NAME</fo:block>   
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">INVOICE</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">RETURN</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">CASH</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">CHEQUE</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">CHALLAN</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">CHQ-RETN</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell> 
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">TOT-PAID</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                        <#--
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">EXCESS</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">SHORT</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>-->
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">BALANCE</fo:block> 
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">AMOUNT</fo:block>   
	                        </fo:table-cell>
	                 </fo:table-row>
	                </fo:table-body>
	               </fo:table>       
	               </fo:block>
              		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
                    <#if saleDate?has_content>
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="30pt"/> 
               	    <fo:table-column column-width="120pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="68pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<#else>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="60pt"/> 
               	    <fo:table-column column-width="110pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="72pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		</#if>
                    <fo:table-body>
	                <#assign temp = 1>
	                <#list categorysList as category>
	                	
	                	<#assign invTot = 0>
	                	<#assign returnCatAmount = 0>
	                	<#assign cashTot = 0>
	                	<#assign chqTot = 0>
	                	<#assign chqRetnTot = 0>
	                	<#assign challanTot = 0>
	                	<#assign totalCatPaidAmnt = 0>
	                	<#assign netBalTot = 0>
	                	<#assign eatot = 0>
	                	<#assign satot = 0>
	                	<#assign batot = 0>
	                	
	                	<#list categoryTotalMap.get(category) as duedata>
                    	<#assign boothDetails = delegator.findOne("Facility", {"facilityId" : duedata.get("facilityId")?if_exists}, true)>
                    	<#assign invoiceAmount = duedata.get("invoiceAmount")>
                    	<#assign returnAmount = duedata.get("returnAmount")>
                    	<#assign cashAmount = duedata.get("cashAmount")>
                    	<#assign chequeAmount = duedata.get("chequeAmount")>
                    	<#assign challanAmount = duedata.get("challanAmount")>
                    	<#assign chequeRetAmount = duedata.get("chequeRetnAmount")>
                    	<#assign totalPaid = duedata.get("totalPaid")>
						<#assign netAmount = duedata.get("netAmount")>
						<#if !(invoiceAmount == 0)>
	                	<fo:table-row>
                    		<fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${temp?if_exists}</fo:block>  
	                        </fo:table-cell>
	                        <#if saleDate?has_content>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${supplyDate?if_exists?string("dd/MM/yyyy")}</fo:block>  
	                        </fo:table-cell>
	                        </#if>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${duedata.get("facilityId")?if_exists}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(boothDetails.get("facilityName")?if_exists)),29)}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${invoiceAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign ia = invoiceAmount>
	                        	<#assign invTot = invTot+ia>
	                        	<#assign ia = 0>
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${returnAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign invRetn = returnAmount>
	                        	<#assign returnCatAmount = returnCatAmount+invRetn>
	                        	<#assign invRetn = 0>
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${cashAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign cash = cashAmount>
	                        	<#assign cashTot = cashTot+cash>
	                        	<#assign cash = 0>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chequeAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign chq = chequeAmount>
	                        	<#assign chqTot = chqTot+chq>
	                        	<#assign chq = 0>
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${challanAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign cha = challanAmount>
	                        	<#assign challanTot = challanTot+cha>
	                        	<#assign cha = 0>
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chequeRetAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign chequeRet = chequeRetAmount>
	                        	<#assign chqRetnTot = chqRetnTot+chequeRet>
	                        	<#assign chequeRet = 0>
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalPaid?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign totalPaidAmnt = totalPaid>
	                        	<#assign totalCatPaidAmnt = totalCatPaidAmnt+totalPaidAmnt>
	                        	<#assign totalPaidAmnt = 0>
	                        </fo:table-cell>
	                         <#--
	                        <fo:table-cell>
	                        <#if netAmount &lt; 0 >
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netAmount?if_exists?string("##0.00")}</fo:block>  
								<#assign ea = netAmount>
	                        	<#assign eatot = eatot+ea>
	                        	<#assign ea = 0>
							<#else>
								<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>
							</#if>	            
	                        </fo:table-cell>
	                        
	                        <fo:table-cell>
	                        <#if netAmount &gt; 0 >
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netAmount?if_exists?string("##0.00")}</fo:block>  
								<#assign sa = netAmount>
	                        	<#assign satot = satot+sa>
	                        	<#assign sa = 0>
							<#else>
								<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>
							</#if>  
	                        </fo:table-cell>-->
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netAmount?if_exists?string("##0.00")}</fo:block>  
	                       		<#assign balNetamnt = netAmount>
	                        	<#assign netBalTot = netBalTot+balNetamnt>
	                        	<#assign balNetamnt = 0>
	                        </fo:table-cell>
	                    </fo:table-row> 
	                    <#assign temp = temp+1>
	                    </#if>
	                </#list>
	                <#if invTot!= 0>
	                <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                	
                	<fo:table-row>
               	     	<fo:table-cell>
                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"> ${category}  TOTAL </fo:block>        
                        </fo:table-cell>
	                    <fo:table-cell/>
	                    <fo:table-cell/>
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${invTot?if_exists?string("##0.00")}</fo:block>  
	                   			<#assign iaGtot = iaGtot+invTot>
	                   			<#assign invTot = 0>
	                    </fo:table-cell>
	                      <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${returnCatAmount?if_exists?string("##0.00")}</fo:block>  
	                   			<#assign returnGAmount = returnGAmount+returnCatAmount>
	                   			<#assign returnCatAmount = 0>
	                    </fo:table-cell>
	                    
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${cashTot?if_exists?string("##0.00")}</fo:block>  
	                   			<#assign cashGTot = cashGTot+cashTot>
	                   			<#assign cashTot = 0>
	                    </fo:table-cell>
	                     <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chqTot?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign chqGTot = chqGTot+chqTot>
	                   			<#assign chqTot = 0>
	                    </fo:table-cell>
	                     <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${challanTot?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign challanGTot = challanGTot+challanTot>
	                   			<#assign challanTot = 0>
	                    </fo:table-cell>
	                     <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chqRetnTot?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign chqRetnGTot = chqRetnGTot+chqRetnTot>
	                   			<#assign chqRetnTot = 0>
	                    </fo:table-cell>
	                     <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalCatPaidAmnt?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign totalCatPaidGAmnt = totalCatPaidGAmnt+totalCatPaidAmnt>
	                   			<#assign totalCatPaidAmnt = 0>
	                    </fo:table-cell>
	                    
	                    <#--
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${eatot?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign eaGtot = eaGtot+eatot>
	                   			<#assign eatot = 0>
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${satot?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign saGtot = saGtot+satot>
	                   			<#assign satot = 0>
	                    </fo:table-cell> -->
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netBalTot?if_exists?string("##0.00")}</fo:block>  
	                   			<#assign netBalGrandTot = netBalGrandTot+netBalTot>
	                   			<#assign netBalTot = 0>
	                    </fo:table-cell>    
                	</fo:table-row>
                	<fo:table-row>
               	     	<fo:table-cell>
                           <fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                	</#if>
	                </#list>
	                <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"> GRAND TOTAL </fo:block>        
                        </fo:table-cell>
	                    <fo:table-cell/>
	                    <fo:table-cell/>
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${iaGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                      <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${returnGAmount?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${cashGTot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chqGTot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                     <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${challanGTot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                     <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chqRetnGTot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                     <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${totalCatPaidGAmnt?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                    <#--
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${eaGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                      <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${saGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell> -->
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netBalGrandTot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>    
                	</fo:table-row>
                	<fo:table-row>
               	     	<fo:table-cell>
                           <fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>