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
	<fo:simple-page-master master-name="main" page-height="8.27in" page-width="11.69in"  margin-bottom=".1in" margin-left="0.5in" margin-right=".3in">
        <fo:region-body margin-top="1.5in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "groupedReqDetailsReport.txt")}
<#if requirementDetailList?has_content>
<fo:page-sequence master-reference="main" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					<fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Arial" white-space-collapse="false">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
					<fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Arial" white-space-collapse="false">UNIT: MOTHER DAIRY: G.K.V.K POST: YELAHANKA: BANGALORE: 560065    </fo:block>
                    <fo:block  keep-together="always"  text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size = "10pt">Page No: <fo:page-number/>&#160;&#160;</fo:block>
                    <fo:block  keep-together="always"  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size = "10pt">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
            		<fo:block  keep-together="always" text-align="center" font-weight="bold" font-family="Arial" white-space-collapse="false">_______________________________________________________________________________________________________________________________</fo:block>
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
            <fo:block  keep-together="always" text-align="center" font-size = "12pt" font-family="Arial" white-space-collapse="false" font-weight= "bold">GROUP REQUIREMENTS TO BE SENT FOR ENQUIRY</fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <#assign size=listSize>
            <#list requirementDetailList as requirementDetails>
          		<fo:table  align="center">
					<fo:table-column column-width="25%"/>
               	    <fo:table-column column-width="25%"/>
               	    <fo:table-column column-width="25%"/>
               	    <fo:table-column column-width="25%"/>
                    <fo:table-body>
                     		<fo:table-row  font-family="Arial" font-weight="bold">	
                     		        <fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">REQUIREMENT GROUP ID: ${requirementGroupId?if_exists}</fo:block>  
	                       			</fo:table-cell>
                     		        <fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">REQUIREMENT ID: ${requirementDetails.requirementId?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       		   <fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">FACILITY:    ${requirementDetails.facilityId?if_exists}</fo:block>  
	                       			</fo:table-cell>
                                    <fo:table-cell>
                     				<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">UOM : ${requirementDetails.uom?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                         </fo:table-row>
	                </fo:table-body>
                </fo:table>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <#assign indentDetails=requirementDetails.get(requirementDetails.requirementId)>
                <#if indentDetails?has_content>
                <fo:table border-style="solid" align="center">
               	    <fo:table-column column-width="20%"/>
               	    <fo:table-column column-width="40%"/>
               	    <fo:table-column column-width="20%"/>
               	    <fo:table-column column-width="15%"/>
                    <fo:table-body>
                     		<fo:table-row border-style="solid" font-family="Arial" font-weight="bold">	
                     		       <fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">INDENT NO</fo:block>
	                       			</fo:table-cell>
	                       		   <fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">DEPARTMENT</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">INDENTED DATE</fo:block>
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">INDENTED QTY</fo:block>  
	                       			</fo:table-cell>
	                         </fo:table-row>
	                         <#list indentDetails as indent>
	                         <fo:table-row border-style="solid" font-family="Arial" font-weight="bold">	
                     		       <fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">${indent.indentNo?if_exists}</fo:block>
	                       			</fo:table-cell>
	                       		   <fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">${indent.department?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">${indent.indentDate?if_exists}</fo:block>
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">${indent.qtyIndented?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                         </fo:table-row>
                             </#list> 
	                </fo:table-body>
                </fo:table>
               </#if>
               
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block text-align="center"  font-size="10pt" white-space-collapse="false" font-family="Arial">
            The below mentioned items are out of stock/below reorder level, requested to give the approval for purchase.
            
            </fo:block>
<fo:block linefeed-treatment="preserve">&#xA;</fo:block><fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:table border-style="solid" align="center">
                    <fo:table-column column-width="5%"/>
                    <fo:table-column column-width="10%"/>
                    <fo:table-column column-width="30%"/>
               	    <fo:table-column column-width="10%"/>
               	    <fo:table-column column-width="10%"/>
               	    <fo:table-column column-width="10%"/>
               	    <fo:table-column column-width="10%"/>
               	    <fo:table-column column-width="10%"/>
                    <fo:table-body>
                     		<fo:table-row border-style="solid" font-family="Arial" font-weight="bold">	
                     				<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">Sl.No</fo:block>  
	                       			</fo:table-cell>
                     				<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">MATERIAL CODE</fo:block>  
	                       			</fo:table-cell>
	                       		   <fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">DESCRIPTION</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">LAST PO DATE</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">LAST PO RATE</fo:block> 			       					
									</fo:table-cell>
									<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">STOCK QTY</fo:block> 			       					
									</fo:table-cell>
									<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">UNIT PRICE</fo:block> 			       					
									</fo:table-cell>
									<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">REQD QTY FOR PURCHASE</fo:block> 			       					
									</fo:table-cell>
	                         </fo:table-row>	
	                           	<#assign rcount = 0>
	                         <fo:table-row border-style="solid" font-family="Arial">	
                     				<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false">${rcount+1}</fo:block>  
	                       			</fo:table-cell>
                     				<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false">${requirementDetails.materialCode?if_exists}</fo:block>  
	                       			</fo:table-cell>
	                       		   <fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false">${requirementDetails.description}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
	                            		<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false">${requirementDetails.lastPOdate}</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false"><#if requirementDetails.lastPOrate?has_content> ${requirementDetails.lastPOrate?string("#.00")?if_exists}</#if></fo:block> 			       					
									</fo:table-cell>
									<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false">${requirementDetails.stockQTY}</fo:block> 			       					
									</fo:table-cell>
									<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false"><#if requirementDetails.lastPOrate?has_content>${requirementDetails.lastPOrate?string("#.00")?if_exists}</#if></fo:block> 			       					
									</fo:table-cell>
									<fo:table-cell border-style="solid">
										<fo:block  text-align="center"  font-size="10pt" white-space-collapse="false">${requirementDetails.requiredQTY}</fo:block> 			       					
									</fo:table-cell>
	                         </fo:table-row>	
	                </fo:table-body>
                </fo:table>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block><fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:table  align="center">
               	    <fo:table-column column-width="33%"/>
               	    <fo:table-column column-width="33%"/>
               	    <fo:table-column column-width="33%"/>
                    <fo:table-body>
                     		<fo:table-row  font-family="Arial" font-weight="bold">	
                     				<fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">INDENTOR</fo:block>  
	                       			</fo:table-cell>
	                       		   <fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">MANAGER</fo:block>  
	                       			</fo:table-cell>
	                         </fo:table-row>	
	                </fo:table-body>
                </fo:table>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block><fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block><fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <#assign size=size-1>
                <fo:table  align="center">
               	    <fo:table-column column-width="33%"/>
               	    <fo:table-column column-width="33%"/>
               	    <fo:table-column column-width="33%"/>
                    <fo:table-body>
                     		<fo:table-row  font-family="Arial" font-weight="bold">	
                     				<fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
	                       		   <fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false">APPROVED BY DIRECTOR</fo:block>  
	                       			</fo:table-cell>
	                       			<fo:table-cell>
	                            		<fo:block  text-align="center"  font-size="9pt" white-space-collapse="false"></fo:block>  
	                       			</fo:table-cell>
	                         </fo:table-row>	
	                </fo:table-body>
                </fo:table>
                <#if size != 0>
                 <fo:block  page-break-after="always" ></fo:block>
                </#if> 
                </#list>
			 </fo:flow> 
		</fo:page-sequence>	
</#if>
	</fo:root>
</#escape>