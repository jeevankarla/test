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
				<fo:region-body margin-top="1in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<#if prodDeptMap?has_content>		             		
<#assign storeList = prodDeptMap.entrySet()> 
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;    -----------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				<fo:block text-align="center" white-space-collapse="false">&#160; TOAL STORE ISUE REGISTER FOR THE PERIOD BETWEN  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}                 
				<fo:block text-align="center" keep-together="always"  >&#160;-----------------------------------------------------------------------------------</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="left" keep-together="always"  >&#160;----------------------------------------------------------------------------------------------------------------</fo:block>				
				<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="60pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="200pt"/>
				<fo:table-column column-width="50pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="90pt"/>
				
					<fo:table-body>
					       <fo:table-row >
					            <fo:table-cell>
									<fo:block text-align="center" keep-together="always">DATE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">INDENT</fo:block>
									<fo:block text-align="left" keep-together="always">/DC NO.</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">ITEM</fo:block>
									<fo:block text-align="left" keep-together="always">CODE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" >DESCRIPTION</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">UNIT</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">QTY</fo:block>
									<fo:block text-align="left" keep-together="always">ISSUED</fo:block>
								</fo:table-cell>
								<fo:table-cell>
									<fo:block text-align="left" keep-together="always">UNIT</fo:block>
									<fo:block text-align="left" keep-together="always">RATE</fo:block>
								</fo:table-cell>
								<fo:table-cell>
								    <fo:block text-align="left" keep-together="always">TOT</fo:block>
								    <fo:block text-align="left" keep-together="always">VALUE</fo:block>
								</fo:table-cell>
						 </fo:table-row>
					     <fo:table-row >
							 <fo:table-cell >
								<fo:block text-align="left" keep-together="always" >&#160;----------------------------------------------------------------------------------------------------------------</fo:block>
						     </fo:table-cell>
						</fo:table-row>
					</fo:table-body>
				</fo:table>
			</fo:block>
		    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" >ISSUE TYPE  <fo:inline font-weight="bold">STORE ISSUES</fo:inline></fo:block>			
			<fo:block text-align="center" keep-together="always">
			<fo:table>
				<fo:table-column column-width="90pt"/>
				<fo:table-column column-width="60pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="200pt"/>
				<fo:table-column column-width="30pt"/>
				<fo:table-column column-width="70pt"/>
				<fo:table-column column-width="60pt"/>
				<fo:table-column column-width="80pt"/>
				<fo:table-body> 
				   <#if storeList?has_content>
             	     <#list storeList as storeEntry>   
                     <#assign deptName=storeEntry.getKey()>                                                
					 <fo:table-row >
					     <fo:table-cell><fo:block text-align="left" keep-together="always">${deptName?if_exists}</fo:block>  </fo:table-cell>
					     <fo:table-cell><fo:block text-align="left" keep-together="always"></fo:block> </fo:table-cell>                         							
				     </fo:table-row>
                    <#assign prodCatNames=storeEntry.getValue()>
             		<#assign prodCatName = prodCatNames.entrySet()> 
                   <#if prodCatName?has_content>
                   <#list prodCatName as Category>   
			      <#assign total=0> 
					 <fo:table-row >
					     <fo:table-cell><fo:block text-align="left">TYPE   ${Category.getKey()?if_exists}</fo:block>  </fo:table-cell>
					     <fo:table-cell><fo:block text-align="left"></fo:block> </fo:table-cell>  		
		                <fo:table-cell><fo:block text-align="left"></fo:block> </fo:table-cell>                         							
				     </fo:table-row>
			     <#assign prodNames=Category.getValue()>
                <#if prodNames?has_content>
		         <#list prodNames as products>   
                  <fo:table-row>
					    <fo:table-cell><fo:block text-align="left">  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(products.get("custRequestDate"), "dd/MM/yyyy")?if_exists}</fo:block>  </fo:table-cell>
		                <fo:table-cell><fo:block text-align="left" > ${products.get("custRequestId")?if_exists}</fo:block> </fo:table-cell> 
		                <fo:table-cell><fo:block text-align="left" > ${products.get("productId")?if_exists}</fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="left" > ${products.get("description")?if_exists}</fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="left" > ${products.get("unit")?if_exists}</fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="right" > ${products.get("totQty")?if_exists}</fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="right"> ${products.get("unitPrice")?if_exists?string("##0.00")}</fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="right" > ${products.get("totVal")?if_exists?string("##0.00")}</fo:block> </fo:table-cell>          
				  </fo:table-row>
             <#assign amount=products.get("totVal")?if_exists>
            <#if  amount?has_content> 
            <#assign total=total+amount> 	                            
            </#if>                                    
		 </#list> 
	 </#if>    <fo:table-row >
				  <fo:table-cell >                               
					 <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;-------------------------------</fo:block>
				  </fo:table-cell>
			   </fo:table-row> 
               <fo:table-row>
	    	       <fo:table-cell><fo:block text-align="left">  </fo:block>  </fo:table-cell>
	    		   <fo:table-cell><fo:block text-align="left" > </fo:block> </fo:table-cell> 
	    		   <fo:table-cell><fo:block text-align="left" ></fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="left" > </fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="left" ></fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="right" > TOTAL</fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="right"> </fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="right" > ${total?if_exists?string("##0.00")}</fo:block> </fo:table-cell>          
			 </fo:table-row>  
             <fo:table-row >
				 <fo:table-cell >
					 <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;-------------------------------</fo:block>
			    </fo:table-cell>
			</fo:table-row>   
		</#list>                              
     </#if>                          
 </#list>
</#if>	                          
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