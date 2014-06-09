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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-bottom=".3in" margin-top=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "prdctRetrnReport.txt")}
 <#if fieldStaffMap?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			    <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">${uiLabelMap.KMFDairySubHeader}</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">FIELD STAFF WISE SALES(QTY) STATEMENT</fo:block>
                    <fo:block  text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">From :: ${froDate?if_exists}  To:: ${toDate?if_exists}</fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block font-weight="bold" font-size="10pt">SINO     &#160;FIELD    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;RETAILER 		          &#160;&#160; &#160;&#160;RETAILER   								&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160; &#160;                MILK         &#160;&#160;&#160;&#160;&#160;  CURD   &#160;&#160;&#160;&#160;&#160;  OTHER&#160;&#160;&#160;&#160;&#160;&#160;&#160;TOTAL</fo:block>
              		<fo:block font-weight="bold" font-size="10pt">&#160;&#160;&#160;&#160;&#160; STAFF	&#160; &#160; &#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;CODE	&#160; &#160;&#160;&#160;&#160;&#160;&#160; NAME   					&#160;&#160;&#160;&#160;&#160; &#160;  &#160;&#160;&#160;&#160;&#160; &#160; &#160; &#160;                 &#160;         &#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;PRODUCTS&#160;&#160;&#160;&#160;</fo:block>
            		<fo:block font-size="10pt">-------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                    <fo:table>
				    <fo:table-column column-width="6%"/>
			        <fo:table-column column-width="19%"/>
			        <fo:table-column column-width="12%"/>
			        <fo:table-column column-width="25%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="10%"/>
			        <fo:table-column column-width="12%"/>
                    <fo:table-body>
                    <#assign serialNo = 1>
                    <#assign grandMilkTot = 0>
                    <#assign grandCurdTot = 0>
                     <#assign grandTot = 0>
                    	<#assign fieldStaffMapDetails = fieldStaffMap.entrySet()>
                    	<#list fieldStaffMapDetails as fieldStaffDetails>
                    	       <fo:table-row>
                    	       <fo:table-cell>
                    				<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="10pt" white-space-collapse="false">${serialNo}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" font-weight="bold" text-align="left" font-size="10pt" white-space-collapse="false">${fieldStaffNamesMap.get(fieldStaffDetails.getKey()?if_exists)}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
                        	<#assign milkTotal = 0>
                        	<#assign curdTotal = 0>
                        	<#assign tot = 0>
                    		<#assign facilityDetails = fieldStaffDetails.getValue().entrySet()>
                    	<#list facilityDetails as prod>
                    		<#assign milkTot = prod.getValue().get("Milk")?if_exists>
                    		<#assign curdTot = prod.getValue().get("Curd")?if_exists>
                    		<#assign  total=prod.getValue().get("Total")?if_exists>
                    		<#if milkTot?has_content>
                    			<#assign milkTotal=milkTotal+milkTot>
                    		</#if>
                    		<#if curdTot?has_content>
                    		     <#assign curdTotal=curdTotal+curdTot>
                    		</#if>
                    		<#if total?has_content>
                    		     <#assign tot=tot+total>
                    		</#if>
                    		prod.getValue().get("Total")?if_exists
                			<fo:table-row>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false"></fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always"  text-align="left" font-size="10pt" white-space-collapse="false">${prod.getKey()?if_exists}</fo:block>  
                    			</fo:table-cell>
                                <fo:table-cell>
                        			<fo:block  text-align="left" font-size="10pt" white-space-collapse="false">${facilityNamesMap.get(prod.getKey())?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${prod.getValue().get("Milk")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${prod.getValue().get("Curd")?if_exists}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			<fo:block keep-together="always" text-align="left"    white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always"  text-align="left" font-size="10pt" white-space-collapse="false">${prod.getValue().get("Total")?if_exists}</fo:block>  
                    			</fo:table-cell>
                        	</fo:table-row>
                        </#list>
                        	
                            <fo:table-row>
                                 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">---------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
                             <fo:table-row>
	                            <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			     <fo:block  keep-together="always" font-weight="bold" text-align="left"  white-space-collapse="false">TOTAL</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<#assign grandMilkTot = grandMilkTot+milkTotal>
                            	<#assign grandCurdTot = grandCurdTot+curdTotal>
                            	<#assign grandTot =grandTot+tot>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${milkTotal}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${curdTotal}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${tot}</fo:block>  
                    			</fo:table-cell>
                    	     </fo:table-row>
                    	    <fo:table-row>
                    	        <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">---------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
	                        <#assign serialNo =serialNo+1>
	                    </#list>
	                   <fo:table-row>
                                 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">---------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
                             <fo:table-row>
	                            <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">GRAND TOTAL</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${grandMilkTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                        			<fo:block  keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${grandCurdTot}</fo:block>  
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			<fo:table-cell>
                    			      <fo:block keep-together="always" text-align="left" font-size="10pt" white-space-collapse="false">${grandTot}</fo:block> 
                    			</fo:table-cell>
                    	     </fo:table-row>
                    	    <fo:table-row>
                    	        <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
                    			 <fo:table-cell>
                    			      <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
                    			</fo:table-cell>
	                             <fo:table-cell>
	                                  <fo:block font-size="10pt">---------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                         </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block> 		
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