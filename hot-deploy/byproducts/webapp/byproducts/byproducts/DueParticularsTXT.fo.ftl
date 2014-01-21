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
<#assign caGtot = 0>
<#assign eaGtot = 0>
<#assign saGtot = 0>
<#assign baGtot = 0>	                	
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top=".7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              ${uiLabelMap.KMFDairySubHeader}</fo:block>				
              		<#if parloursOnly == "Y">
              			<#if parameters.customTimePeriodId?has_content>
	              		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              PARLOUR COLLECTION STATEMENT FOR THE MONTH: ${parameters.customTimePeriodId}                            ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
	            		<fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">SNO    PARTY          PARTY                                 INVOICE         COLLN.           EXCESS            SHORT           BALANCE</fo:block>
	              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">&#160;      CODE           NAME                                  AMOUNT          AMOUNT           AMOUNT            AMOUNT          AMOUNT</fo:block>
	              		
	            		<#else>
	            		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              PARLOUR COLLECTION STATEMENT FOR THE DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(saleDate, "dd/MM/yyyy")}                            ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
	            		<fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">SNO   INVOICE   PARTY     PARTY                              INVOICE         COLLN.           EXCESS            SHORT           BALANCE</fo:block>
	              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">&#160;     DATE      CODE      NAME                               AMOUNT          AMOUNT           AMOUNT            AMOUNT          AMOUNT</fo:block>
	              		
	            		</#if>
            		<#else>
              		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                              DAIRY PARTICULARS FOR DATE/MONTH : ${parameters.customTimePeriodId}                                     ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
            		<fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">SNO    PARTY          PARTY                                   INVOICE         CHEQUE           EXCESS            SHORT        BALANCE</fo:block>
              		<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">&#160;      CODE           NAME                                    AMOUNT          AMOUNT           AMOUNT            AMOUNT       AMOUNT</fo:block>
              		</#if>
              		<fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
            		<#else>
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="60pt"/> 
               	    <fo:table-column column-width="120pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="72pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		</#if>
                    <fo:table-body>
	                <#assign temp = 1>
	                <#list categorysList as category>
	                	
	                	<#assign iatot = 0>
	                	<#assign catot = 0>
	                	<#assign eatot = 0>
	                	<#assign satot = 0>
	                	<#assign batot = 0>
	                	
	                	<#list categoryTotalMap.get(category) as duedata>
                    	<#assign boothDetails = delegator.findOne("Facility", {"facilityId" : duedata.get("facilityId")?if_exists}, true)>
                    	<#assign invoiceAmount = duedata.get("invoiceAmount")>
                    	<#assign chequeAmount = duedata.get("chequeAmount")>
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
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${boothDetails.get("facilityName")?if_exists}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${invoiceAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign ia = invoiceAmount>
	                        	<#assign iatot = iatot+ia>
	                        	<#assign ia = 0>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chequeAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign ca = chequeAmount>
	                        	<#assign catot = catot+ca>
	                        	<#assign ca = 0>
	                        </fo:table-cell>
	                        
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
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netAmount?if_exists?string("##0.00")}</fo:block>  
	                       		<#assign ba = netAmount>
	                        	<#assign batot = batot+ba>
	                        	<#assign ba = 0>
	                        </fo:table-cell>
	                    </fo:table-row> 
	                    <#assign temp = temp+1>
	                    </#if>
	                </#list>
	                <#if iatot!= 0>
	                <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                	
                	<fo:table-row>
               	     	<fo:table-cell>
                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"> ${category}  TOTAL </fo:block>        
                        </fo:table-cell>
	                    <fo:table-cell/>
	                    <#if saleDate?has_content>
	                        <fo:table-cell>
	                        </fo:table-cell>
	                    </#if>
	                    <fo:table-cell/>
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${iatot?if_exists?string("##0.00")}</fo:block>  
	                   			<#assign iaGtot = iaGtot+iatot>
	                   			<#assign iatot = 0>
	                    </fo:table-cell>
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${catot?if_exists?string("##0.00")}</fo:block>  
	                   			<#assign caGtot = caGtot+catot>
	                   			<#assign catot = 0>
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${eatot?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign eaGtot = eaGtot+eatot>
	                   			<#assign eatot = 0>
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${satot?if_exists?string("##0.00")}</fo:block>  
	                    		<#assign saGtot = saGtot+satot>
	                   			<#assign satot = 0>
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${batot?if_exists?string("##0.00")}</fo:block>  
	                   			<#assign baGtot = baGtot+batot>
	                   			<#assign batot = 0>
	                    </fo:table-cell>    
                	</fo:table-row>
                	<fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                	</#if>
	                </#list>
	                <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"> GRAND TOTAL </fo:block>        
                        </fo:table-cell>
	                    <fo:table-cell/>
	                    <#if saleDate?has_content>
	                        <fo:table-cell>
	                        </fo:table-cell>
	                    </#if>
	                    <fo:table-cell/>
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${iaGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                    <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${caGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${eaGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${saGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>
	                    <fo:table-cell>
	                           <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${baGtot?if_exists?string("##0.00")}</fo:block>  
	                    </fo:table-cell>    
                	</fo:table-row>
                	<fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>