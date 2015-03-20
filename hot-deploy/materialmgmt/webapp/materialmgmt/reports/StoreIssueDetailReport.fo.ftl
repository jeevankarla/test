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
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.8in" >
				<fo:region-body margin-top="1.8in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
			<#if prodDeptMap?has_content>		             		
        <#assign storeList = prodDeptMap.entrySet()> 
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block  keep-together="always" text-align="left" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;                ${uiLabelMap.KMFDairyHeader} &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Page - <fo:page-number/></fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;---------------------------------------------------------------</fo:block>
                <fo:block text-align="center" white-space-collapse="false">&#160;   TOTAL STORE ISSUE REGISTER FOR THE PERIOD BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}</fo:block>                 
             <#--   <fo:block text-align="left" keep-together="always"  >&#160;&#160;&#160;&#160;&#160;----------------------------------------------------------------------------------</fo:block> -->				
                <fo:block text-align="left" keep-together="always"  >&#160;---------------------------------------------------------------------------------------</fo:block>				   	
                <fo:block font-family="Courier,monospace">
				   <fo:table>
						<fo:table-column column-width="90pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="70pt"/>
						<fo:table-column column-width="200pt"/>
						<fo:table-column column-width="100pt"/>
						<fo:table-column column-width="60pt"/>
						<fo:table-column column-width="70pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-column column-width="80pt"/>
					        <fo:table-body>
						       <fo:table-row >
						            <fo:table-cell>
										<fo:block text-align="center" keep-together="always">DATE</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="left" keep-together="always">INDENT</fo:block>
										<fo:block text-align="left" keep-together="always">NO.</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="left" keep-together="always">ITEM</fo:block>
										<fo:block text-align="left" keep-together="always">CODE</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="left" keep-together="always" >DESCRPTION</fo:block>
									</fo:table-cell>
									<fo:table-cell>
										<fo:block text-align="left" keep-together="always">UNIT</fo:block>
									</fo:table-cell>
									
									<fo:table-cell>
										<fo:block text-align="left" keep-together="always">ISS.</fo:block>
										<fo:block text-align="left" keep-together="always">QTY</fo:block>
									</fo:table-cell>
								<#--	<fo:table-cell>
									    <fo:block text-align="left" keep-together="always">UNIT</fo:block>
									    <fo:block text-align="left" keep-together="always">RATE</fo:block>
									</fo:table-cell> -->
	                                <fo:table-cell>
									    <fo:block text-align="left" keep-together="always">TOT</fo:block>
									    <fo:block text-align="left" keep-together="always">VALUE</fo:block>
									</fo:table-cell>
							 </fo:table-row>
						     <fo:table-row >
								 <fo:table-cell >
									<fo:block text-align="left" keep-together="always"  >&#160;---------------------------------------------------------------------------------------</fo:block>
							     </fo:table-cell>
							</fo:table-row>
					  </fo:table-body>
				  </fo:table>
			</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
			<fo:block text-align="center">			 
			 <fo:table>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="200pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-body> 
                  <#assign lineNo=0>
				   <#if storeList?has_content>
             	     <#list storeList as storeEntry>   
                     <#assign deptName=storeEntry.getKey()>  
               <fo:table-row >
					     <fo:table-cell><fo:block  text-align="left" keep-together="always" font-weight="bold">${deptName?if_exists}</fo:block>  </fo:table-cell>
					     <fo:table-cell><fo:block text-align="left" keep-together="always"></fo:block> </fo:table-cell>                         							
				     </fo:table-row>
                    <#assign prodCatNames=storeEntry.getValue()>
             	    <#assign prodCatName = prodCatNames.entrySet()>  
                   <#if prodCatName?has_content>
                   <#assign lineNo=lineNo+1>
                   <#list prodCatName as Category>  
                   	   <#assign ProductCatDesc = delegator.findOne("ProductCategory", {"productCategoryId" : Category.getKey()}, false)>                   
			          <#assign total=0> 
                    <fo:table-row >
					     <fo:table-cell><fo:block text-align="left" keep-together="always" font-weight="bold">TYPE   ${ProductCatDesc.description?if_exists}</fo:block>  </fo:table-cell>
					     <fo:table-cell><fo:block text-align="left"></fo:block> </fo:table-cell>  		
		                <fo:table-cell><fo:block text-align="left"></fo:block> </fo:table-cell>                         							
				     </fo:table-row>
			     <#assign prodNames=Category.getValue()> 
                <#if prodNames?has_content>
                  		     <#assign lineNo=lineNo+1>
                   <#list prodNames as products> 
                    <#assign dateWiseMap = products.get("dateWiseMap")?if_exists>
                    <#assign dayWiseMap =dateWiseMap.entrySet()> 
                      <#list dayWiseMap as eachDay>
                      <#assign eachDayValue=eachDay.getValue()>
                  <#if eachDayValue.get("itemQuantity") gt 0>
                    <#assign amount=eachDayValue.get("totalValue")?if_exists>
                 <#assign lineNo=lineNo+1>
                  <fo:table-row>
					    <fo:table-cell><fo:block text-align="left"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(eachDayValue.get("itemIssuedDate"), "dd/MM/yyyy")} </fo:block>  </fo:table-cell>
		                <fo:table-cell><fo:block text-align="left" >${eachDayValue.get("itemCustId")} </fo:block> </fo:table-cell> 
		                <fo:table-cell><fo:block text-align="left" >${eachDayValue.get("internalCode")} </fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="left" > ${eachDayValue.get("itemDescription")}</fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="center" >${eachDayValue.get("itemUnit")} </fo:block> </fo:table-cell>                         							
		                <fo:table-cell><fo:block text-align="right" >${eachDayValue.get("itemQuantity")?if_exists?string("##0.00")} </fo:block> </fo:table-cell>                         							
		          <#--   <fo:table-cell><fo:block text-align="right"> ${products.get("totunitCost")?if_exists?string("##0.00")}</fo:block> </fo:table-cell>   -->                       							
		                <fo:table-cell><fo:block text-align="right" >${amount?if_exists?string("##0.00")} </fo:block> </fo:table-cell>          
				  </fo:table-row>
             
            <#if  amount?has_content> 
            <#assign total=total+amount> 	                            
            </#if>  
            </#if>            
          </#list>  
		 </#list> 
	   </#if>   
             <fo:table-row >
				  <fo:table-cell >                               
					 <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;------------------------------</fo:block>
				  </fo:table-cell>
			   </fo:table-row> 
               <fo:table-row>
	    	       <fo:table-cell><fo:block text-align="left">  </fo:block>  </fo:table-cell>
	    		   <fo:table-cell><fo:block text-align="left" > </fo:block> </fo:table-cell> 
	    		   <fo:table-cell><fo:block text-align="left" ></fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="left" > </fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="left" ></fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="right" > TOTAL</fo:block> </fo:table-cell>                         							
	    		   <#--  <fo:table-cell><fo:block text-align="right"> </fo:block> </fo:table-cell>    -->                     							
	    		   <fo:table-cell><fo:block text-align="right" > ${total?if_exists?string("##0.00")}</fo:block> </fo:table-cell>          
			 </fo:table-row>  
             <fo:table-row >
			
                  <fo:table-cell >
					 <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;------------------------------</fo:block>
			    </fo:table-cell>
               
			</fo:table-row>   
		  </#list>                              
        </#if>                          
     </#list>
    </#if>	                          
    </fo:table-body>
   </fo:table>				      
 </fo:block>
 
    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                     STORE OFFICER </fo:block> 
	<fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                     MOTHER DAIRY</fo:block>      
</fo:flow>	 	
	</fo:page-sequence>
<#--	<fo:page-sequence master-reference="main">
	<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block  keep-together="always" text-align="left" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">&#160;                ${uiLabelMap.KMFDairyHeader} &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Page - <fo:page-number/></fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;---------------------------------------------------------------</fo:block>
                <fo:block text-align="center" white-space-collapse="false">&#160;   TOTAL STORE ISSUE REGISTER FOR THE PERIOD BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}</fo:block>                 
             <#--   <fo:block text-align="left" keep-together="always"  >&#160;&#160;&#160;&#160;&#160;----------------------------------------------------------------------------------</fo:block> -->				
         <#--       <fo:block text-align="left" keep-together="always"  >&#160;---------------------------------------------------------------------------------------</fo:block>				   	
            </fo:static-content>
	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
		<fo:block break-before="page"/>
 <#if facilityId?has_content>
		<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="13pt">                                             ${facilityId?if_exists} ABSTRACT                                                  </fo:block>
	<#else>
	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="13pt">                                              ABSTRACT                                                  </fo:block>
</#if>
<fo:block text-align="center">
   <#if deptMap?has_content>		             		
        <#assign storeAbsList = deptMap.entrySet()> 			 
    <fo:table>
		<fo:table-column column-width="60pt"/>
		<fo:table-column column-width="260pt"/>
		<fo:table-column column-width="230pt"/>
		   <fo:table-body>
		   <#assign grandTotal=0>  
		      <#if storeAbsList?has_content>
               <#assign sno=1>   
                <#list storeAbsList as storeAbsEntry>   
                  <#assign deptName=storeAbsEntry.getKey()>                                                 
					 <fo:table-row >
					     <fo:table-cell><fo:block text-align="left" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;${deptName?if_exists}</fo:block>  </fo:table-cell>
					     <fo:table-cell><fo:block text-align="left" keep-together="always"></fo:block> </fo:table-cell>
                         <fo:table-cell><fo:block text-align="left" keep-together="always"></fo:block> </fo:table-cell>                         							                                                 							
				     </fo:table-row>
                    <#assign absPrimaryCat = storeAbsEntry.getValue()>
             		<#assign absProdCatName = absPrimaryCat.entrySet()> 
                   <#if absProdCatName?has_content>
                   <#list absProdCatName as absCategory>  
                        <#assign ProductCatDesc = delegator.findOne("ProductCategory", {"productCategoryId" : absCategory.getKey()}, false)>                    			                                                
					 <fo:table-row >                         
                         <fo:table-cell><fo:block text-align="right">&#160;&#160;${sno?if_exists}.</fo:block> </fo:table-cell>                         							  
					     <fo:table-cell><fo:block text-align="left" keep-together="always" font-weight="bold">&#160;${ProductCatDesc.description?if_exists}</fo:block>
                             <fo:block text-align="left" keep-together="always"  >&#160;&#160;----------------------------------------------------------------------------------</fo:block>				   	
                             <fo:block font-family="Courier,monospace">
				                <fo:table>
                                   <fo:table-column column-width="10pt"/>
									<fo:table-column column-width="90pt"/>
									<fo:table-column column-width="200pt"/>
									<fo:table-column column-width="80pt"/>
									<fo:table-column column-width="60pt"/>
									<fo:table-column column-width="80pt"/>
									<fo:table-column column-width="100pt"/>
					                    <fo:table-body>
						                   <fo:table-row >	
                                               	<fo:table-cell>
													<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt"></fo:block>
												</fo:table-cell>				            									
												<fo:table-cell>
													<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">PRODUCTID</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">PRODUCT NAME</fo:block>
												</fo:table-cell>
												<fo:table-cell>
													<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">UNIT</fo:block>
												</fo:table-cell>									
												<fo:table-cell>
													<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">ISS.</fo:block>
													<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">QTY</fo:block>
												</fo:table-cell>									
				                                <fo:table-cell>
												    <fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">TOT</fo:block>
												    <fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">VALUE</fo:block>
												</fo:table-cell>
												<fo:table-cell>
												    <fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">AVERAGE</fo:block>
												    <fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">COST(P.U)</fo:block>
												</fo:table-cell>
							               </fo:table-row>
						                   <fo:table-row >
												 <fo:table-cell >
													<fo:block text-align="left" keep-together="always" >&#160;&#160;----------------------------------------------------------------------------------</fo:block>
											     </fo:table-cell>
							               </fo:table-row>
					                   </fo:table-body>
				                  </fo:table>
			                 </fo:block> 
			                 <fo:block>
			                     <fo:table>
                                    <fo:table-column column-width="10pt"/>   
									<fo:table-column column-width="90pt"/>
									<fo:table-column column-width="200pt"/>
									<fo:table-column column-width="40pt"/>
									<fo:table-column column-width="60pt"/>
									<fo:table-column column-width="90pt"/>
									<fo:table-column column-width="80pt"/>
					                    <fo:table-body>
					                    <#assign prodAbsNames= absCategory.getValue()>
                                        <#assign absProducts = prodAbsNames.entrySet()>
                                        <#list absProducts as productDetails> 
                                          	<#assign absProductDetails= productDetails.getValue()>
                                            <#assign qty = absProductDetails.get("totQty")?if_exists> 
                                            <#assign totVal = absProductDetails.get("totVal")?if_exists>
                                            <#assign avgCost = totVal / qty>                                                      
					                       <fo:table-row height="30pt">
     								           <fo:table-cell><fo:block text-align="left" font-size="11pt"> </fo:block></fo:table-cell>                         							
								               <fo:table-cell><fo:block text-align="left" font-size="11pt">${absProductDetails.get("internalName")}</fo:block> </fo:table-cell> 
                                               <fo:table-cell><fo:block text-align="left" font-size="11pt"> ${absProductDetails.get("productName")?if_exists}</fo:block> </fo:table-cell>                         							
		                                       <fo:table-cell><fo:block text-align="left" font-size="11pt"> ${absProductDetails.get("unit")?if_exists}</fo:block> </fo:table-cell>                         							
		                                       <fo:table-cell><fo:block text-align="right" font-size="11pt"> ${absProductDetails.get("totQty")?if_exists}</fo:block> </fo:table-cell>                         							
		                                       <fo:table-cell><fo:block text-align="right" font-size="11pt"> ${absProductDetails.get("totVal")?if_exists?string("##0.00")}</fo:block> </fo:table-cell>
		                                       <fo:table-cell><fo:block text-align="right" font-size="11pt"> ${avgCost?if_exists?string("##0.00")}</fo:block> </fo:table-cell>                                  							
		                                                                         							
										  </fo:table-row>
                                       </#list>
									 </fo:table-body>
                                 </fo:table>				      
                             </fo:block>	  										  										  					                       
					     </fo:table-cell>                            
					   <fo:table-cell><fo:block text-align="right" ></fo:block> </fo:table-cell>		
		     </fo:table-row> 
		     <#if absProducts?has_content>
                <#assign total=0>  
                <#list absProducts as productDetails> 
                   <#assign absProductDetails= productDetails.getValue()>  
                   <#assign amount = absProductDetails.get("totVal")?if_exists>
                   <#if  amount?has_content> 
                        <#assign total = total+amount>                                  	                            
                   </#if>  
               </#list>                                
			   <#assign grandTotal = grandTotal+total> 
			   <#assign sno=sno+1>                  
	           </#if>
               <fo:table-row >
				  <fo:table-cell >                               
					 <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;------------------------------</fo:block>
				  </fo:table-cell>
			   </fo:table-row> 
               <fo:table-row>
	    		   <fo:table-cell><fo:block text-align="left" ></fo:block> </fo:table-cell> 
	    		   <fo:table-cell><fo:block text-align="right" > TOTAL</fo:block> </fo:table-cell>                         							
	    		   <fo:table-cell><fo:block text-align="right" > ${total?if_exists?string("##0.00")}</fo:block> </fo:table-cell>          
			 </fo:table-row>  
             <fo:table-row >
				 <fo:table-cell >
					 <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;------------------------------</fo:block>
			    </fo:table-cell>
			</fo:table-row>                                                                                                                              
           </#list>                              
          </#if>                                      
       </#list>
        </#if>
           <fo:table-row >
				        <fo:table-cell >                               
					        <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;----------------------------------------------------------------------------------</fo:block>
				        </fo:table-cell>
			        </fo:table-row>
                     <fo:table-row>
		    		    <fo:table-cell><fo:block text-align="left" ></fo:block> </fo:table-cell>                         							
		    		    <fo:table-cell><fo:block text-align="left" >&#160;&#160;&#160;&#160;&#160;GRAND TOTAL:</fo:block> </fo:table-cell>                         							
		    		    <fo:table-cell><fo:block text-align="right" >&#160;&#160;${grandTotal?if_exists?string("##0.00")}</fo:block> </fo:table-cell>          
			        </fo:table-row>  
                    <fo:table-row >
				        <fo:table-cell >                               
					        <fo:block text-align="left">&#160;&#160;&#160;&#160;&#160;&#160;&#160;----------------------------------------------------------------------------------</fo:block>
				        </fo:table-cell>
			        </fo:table-row>    	        
		   </fo:table-body>
        </fo:table>	
       </#if> 			      
    </fo:block>
    </fo:flow>	 	
	</fo:page-sequence>  -->
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