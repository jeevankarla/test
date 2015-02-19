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
		<fo:layout-master-set>
			<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="0.8in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<#if supplierEvalMap?has_content> 
        <fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;-----------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
            </fo:static-content>            
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:block text-align="center" keep-together="always" font-weight="bold">                                              SUPPLIER EVALUATION REPORT                                                  </fo:block>
            <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;PO NO.:${orderId?if_exists}                          </fo:block>  	 			       
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>            
            <fo:block>
                <fo:table border-style="solid">
	                <fo:table-column column-width="60pt"/>
				    <fo:table-column column-width="210pt"/>
					<fo:table-column column-width="150pt"/>
				    <fo:table-column column-width="90pt"/>
				    <fo:table-column column-width="130pt"/>
					<fo:table-body> 
					    <fo:table-row height="30pt">
					        <fo:table-cell border-style="solid">
					           <fo:block text-align="center"  padding-before="0.6cm" font-weight="bold">SI NO.</fo:block>
					        </fo:table-cell>
					        <fo:table-cell border-style="solid">
					           <fo:block text-align="center"  padding-before="0.6cm" font-weight="bold">MATERIAL NAME</fo:block>
					        </fo:table-cell>
					        <fo:table-cell border-style="solid">
					           <fo:block text-align="center" padding-before="0.6cm" font-weight="bold">SUPPLIER NAME</fo:block>
					        </fo:table-cell>
					        <fo:table-cell border-style="solid">
					           <fo:block text-align="center"  font-weight="bold" padding-before="0.6cm">PO DATE</fo:block>
					        </fo:table-cell>					        
					        <fo:table-cell border-style="solid">
					           <fo:block text-align="center"  font-weight="bold" padding-before="0.6cm">EVALUTION STATUS</fo:block>
					        </fo:table-cell>					        
					    </fo:table-row>
				    </fo:table-body>
			   </fo:table>
		   </fo:block>
		   <fo:block>
              <fo:table border-style="solid">
                  <fo:table-column column-width="60pt"/>
			      <fo:table-column column-width="210pt"/>
				  <fo:table-column column-width="150pt"/>
				  <fo:table-column column-width="90pt"/>
				  <fo:table-column column-width="130pt"/>
			         <fo:table-body>
			            <#assign supplierList = supplierEvalMap.entrySet()>	
                        <#assign sno=1>  					          				                                                                               					          
			             <#list supplierList as supplierEntry>
		                   <#assign quoteId=supplierEntry.getKey()> 
                           <#assign supplierDetails=supplierEntry.getValue()>  			              						
              	           <fo:table-row height="30pt">
			                  <fo:table-cell border-style="solid">
					             <fo:block text-align="center" font-size="11pt" >${sno?if_exists}</fo:block>
					          </fo:table-cell>	
					          <fo:table-cell border-style="solid">
					              <fo:block text-align="left" font-size="11pt" >${supplierDetails.get("productName")?if_exists}</fo:block>
					          </fo:table-cell>
					          <fo:table-cell border-style="solid">
					              <fo:block text-align="left" font-size="11pt" >${supplierDetails.get("partyName")?if_exists}</fo:block>
					          </fo:table-cell>	
					          <fo:table-cell border-style="solid">
					              <fo:block text-align="center" font-size="11pt" >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(supplierDetails.get("orderDate"), "dd-MM-yyyy")?if_exists}</fo:block>
					          </fo:table-cell>	
					          <fo:table-cell border-style="solid">
					              <fo:block text-align="left" font-size="11pt" >${supplierDetails.get("statusId")?if_exists}</fo:block>
					          </fo:table-cell>		
			              </fo:table-row>
                        <#assign sno=sno+1> 
                      </#list> 
				     </fo:table-body>
			   </fo:table>
		  </fo:block> 			         
	   </fo:flow>	
    </fo:page-sequence> 
     <#else>
           <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
		  </fo:page-sequence>				
	    </#if>  
     </fo:root>
</#escape>	                
                 