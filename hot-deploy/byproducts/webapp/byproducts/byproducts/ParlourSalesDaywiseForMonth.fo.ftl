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
        <fo:region-body margin-top="1.1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "ParlourSalesDaywisecollection.txt")}
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                ${uiLabelMap.aavinDairyMsg}</fo:block>
					<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                              MARKETING UNIT: METRO PRODUCTS: NANDANAM: CHENNAI=35</fo:block>				
	              	<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                           PARLOUR SALES DATEWISE COLLECTION DETAILS FOR THE MONTH: ${parameters.customTimePeriodId}      ${uiLabelMap.CommonPage}:<fo:page-number/></fo:block>
	            	<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
	              	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">SNO    INVOICE                   INVOICE          COLLN.           EXCESS           SHORT           BALANCE</fo:block>
	              	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">&#160;      DATE                      AMOUNT           AMOUNT           AMOUNT           AMOUNT          AMOUNT</fo:block>
              		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
	              	<fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">&#160; ** PARTY CD :: ${parlourCode?if_exists}                   PARTY NAME :: ${parlourName?if_exists}</fo:block>
              		<fo:block font-size="7pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table border-width="1pt" border-style="dotted">
            		<fo:table-column column-width="30pt"/>
            		<fo:table-column column-width="70pt"/> 
               	    <fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/> 		
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="68pt"/>
                    <fo:table-body>
	                <#assign temp = 1>
	                <#list dayTotalsList as dayTots>
	                <#assign parlourDataEntry = dayTots.entrySet()>
       				  <#list parlourDataEntry as parlourEntry>
       				  <#assign invoiceAmount = parlourEntry.getValue().get("invoiceAmount")>
                    	<#assign chequeAmount = parlourEntry.getValue().get("chequeAmount")>
						<#assign netAmount = parlourEntry.getValue().get("netAmount")>
						<fo:table-row>
							<fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${temp?if_exists}</fo:block>  
	                        </fo:table-cell>
                    		<fo:table-cell>
	                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(parlourEntry.getKey(), "dd.MM.yyyy")?if_exists}</fo:block>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${invoiceAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign ia = invoiceAmount>
	                        	<#assign iaGtot = iaGtot+ia>
	                        	<#assign ia = 0>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${chequeAmount?if_exists?string("##0.00")}</fo:block>  
	                        	<#assign ca = chequeAmount>
	                        	<#assign caGtot = caGtot+ca>
	                        	<#assign ca = 0>
	                        </fo:table-cell>
	                        
	                        <fo:table-cell>
	                        <#if netAmount &lt; 0 >
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netAmount?if_exists?string("##0.00")}</fo:block>  
								<#assign ea = netAmount>
	                        	<#assign eaGtot = eaGtot+ea>
	                        	<#assign ea = 0>
							<#else>
								<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>
							</#if>	            
	                        </fo:table-cell>
	                        
	                        <fo:table-cell>
	                        <#if netAmount &gt; 0 >
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netAmount?if_exists?string("##0.00")}</fo:block>  
								<#assign sa = netAmount>
	                        	<#assign saGtot = saGtot+sa>
	                        	<#assign sa = 0>
							<#else>
								<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">0.00</fo:block>
							</#if>  
	                        </fo:table-cell>
	                        <fo:table-cell>
	                        	<#assign ba = netAmount>
	                        	<#assign baGtot = baGtot+ba>
	                        	<#assign ba = 0>
	                        	<#if baGtot == 0>
	                            <fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${netAmount?if_exists?string("##0.00")}</fo:block>  
	                       		<#else>
	                       		<fo:block  keep-together="always" text-align="right" font-size="7pt" white-space-collapse="false">${baGtot?if_exists?string("##0.00")}</fo:block>
	                       		</#if>
	                       		
	                        </fo:table-cell>
	                    </fo:table-row> 
	                    <#assign temp = temp+1>
	                  </#list>
	                </#list>
	                <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
	                <fo:table-row>
               	     	<fo:table-cell>
                            <fo:block  keep-together="always" text-align="left" font-size="7pt" white-space-collapse="false"> GRAND TOTAL </fo:block>        
                        </fo:table-cell>
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
                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------</fo:block>        
                        </fo:table-cell>
                	</fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
			 </fo:flow>
			 </fo:page-sequence>	
</fo:root>
</#escape>