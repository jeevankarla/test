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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1.3in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "RoutewiseChequeValue.txt")}
<#if routeMap?has_content>
<#assign routeList = routeMap.entrySet()>
<#list routeList as eachRoute>
<#assign routeData = eachRoute.getValue()>
<#if routeData?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">  
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>		
					<fo:block  white-space-collapse="false" font-size="7pt" text-align="left" >&#160;                                                                 DATE: ${indentDate}</fo:block>				
					<fo:block text-align="left" font-size="7pt" white-space-collapse="false">&#160;                    ROUTE ABSTRACT FOR CHEQUE VALUE</fo:block>
					<fo:block white-space-collapse="false" font-size="7pt">ROUTE NO :: ${eachRoute.getKey()}</fo:block>              		
            		<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="7pt"  keep-together="always" text-align="left">SNO INVOICE  PARTY  PARTY                    SALE       CHEQUE           CHEQUE</fo:block>
            	<fo:block white-space-collapse="false" font-size="7pt"  keep-together="always" text-align="left">&#160;   NUMBER   CODE   NAME                     VALUE      NUMBER           AMOUNT</fo:block>
            	<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>	
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="14pt"/>
                    <fo:table-column column-width="38pt"/>  
               	    <fo:table-column column-width="32pt"/>
            		<fo:table-column column-width="120pt"/>
            		<fo:table-column column-width="17pt"/>
            		<fo:table-column column-width="65pt"/>
                    <fo:table-column column-width="65pt"/> 
		          	<#--<fo:table-header border-width="1pt" border-style="dotted">
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">SNO</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">INVOICE NO</fo:block></fo:table-cell>		                    	                  		            
		            	<fo:table-cell ><fo:block text-align="left" font-size="7pt">PARTY CODE</fo:block></fo:table-cell>
       					<fo:table-cell ><fo:block text-align="left" font-size="7pt">PARTY NAME</fo:block></fo:table-cell>
            			<fo:table-cell><fo:block text-align="left" font-size="7pt">SALE VALUE</fo:block></fo:table-cell>		                    	                  
		            	<fo:table-cell><fo:block text-align="left" font-size="7pt">CHEQUE NO</fo:block></fo:table-cell>		                    	                  		            
            		   	<fo:table-cell><fo:block text-align="left" font-size="7pt">CHEQUE AMOUNT</fo:block></fo:table-cell>
				    </fo:table-header>-->  
                    <fo:table-body>
                    	<#assign eachRouteValue = eachRoute.getValue()>
                    	<#assign serial = 1>
                    	<#assign totalDue = 0>
                    	<#list eachRouteValue as facilityDetail>
                    	<fo:table-row>
                    		<fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt">${serial?if_exists}</fo:block>        
	                        </fo:table-cell> 
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt">${facilityDetail.get('invoiceId')?if_exists}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt">${facilityDetail.get('facilityId')?if_exists}</fo:block>        
	                        </fo:table-cell>
	                        <#assign facility = delegator.findOne("Facility", {"facilityId" : facilityDetail.get('facilityId')}, true)>
                    		<fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facility.facilityName)),22)}</fo:block>        
	                        </fo:table-cell> 
	                        <fo:table-cell>
	                            <fo:block text-align="right" font-size="7pt" >${facilityDetail.get('salesValue')?if_exists?string("##0.00")}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt"></fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt"></fo:block>        
	                        </fo:table-cell>
                    	</fo:table-row> 
                    	<fo:table-row>
                    		<fo:table-cell>
	                            <fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>     
                    	<#assign serial = serial+1>
                    	<#assign totalDue = totalDue + facilityDetail.get('salesValue')>
                    	</#list>
                   	 	<fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>
	                    <fo:table-row>
                   	 		<fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt"></fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt" keep-together="always" white-space-collapse="false">&lt; &lt; TOTAL VALUE &gt; &gt;</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt"></fo:block>        
	                        </fo:table-cell>
	                        
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt"></fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="right" font-size="7pt">${totalDue?if_exists?string("##0.00")}</fo:block>        
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            <fo:block text-align="left" font-size="7pt"></fo:block>        
	                        </fo:table-cell>
	                     </fo:table-row>
	                     <fo:table-row>
	                        <fo:table-cell>
	                            	<fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>        
	                        </fo:table-cell>
	                    </fo:table-row>    	 
                    </fo:table-body>
                </fo:table>
                
               </fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">&#160;                       A B S T R A C T </fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">&#160;                      ----------------</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false" font-family="Courier,monospace">NO OF DEWS          : :                TOTAL NO OF CHEQUES : :</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">NO OF FROS          : :                DUES IF ANY         : :</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">NO OF MCCS          : :                DUES COLLECTED      : :</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">NO OF AVMFRO        : :</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">NO OF WSD           : :</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">NO OF PARLOURS      : :</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">OTHERS              : :</fo:block>
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
               <fo:block text-align="left" keep-together="always" font-size="7pt" font-family="Courier,monospace" white-space-collapse="false">TOTAL NO OF POINTS  : :</fo:block>
               <fo:block font-size="10pt">-------------------------------------------------------------------------------------</fo:block>
               <fo:block linefeed-treatment="preserve" font-size="7pt" font-family="Courier,monospace">&#xA;</fo:block>	
               <fo:block linefeed-treatment="preserve" font-size="7pt" font-family="Courier,monospace">&#xA;</fo:block>	
               <fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="7pt" white-space-collapse="false">&#160;                                    VERIFIED BY </fo:block>		
			 </fo:flow>
			 </fo:page-sequence>
			 </#if>
		</#list>	
	 <#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence> 
	</#if>						        	
</fo:root>
</#escape>