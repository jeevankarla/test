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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in" margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top=".4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
       
        <#if routeWiseMap?has_content> 
        <#assign routeWiseList= routeWiseMap.entrySet()>
        <#list routeWiseList as routeBoothsMap>
  
		        <fo:page-sequence master-reference="main">		        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		        	      <fo:block   font-family="Courier,monospace">
		        	     <#assign  boothWiseSaleList=routeBoothsMap.getValue().entrySet()>
		        	     <#list   boothWiseSaleList as boothSaleMap>
		        	                  <#assign facilityId = boothSaleMap.getKey()>
		        	                    <#assign boothSaleDetails = boothSaleMap.getValue()>
		        	                    
		        	                     <#assign amSaleDetails =boothSaleDetails.get("AM")>
		        	                      <#assign pmSaleDetails = boothSaleDetails.get("PM")>
		        	            <#assign totalValue= amSaleDetails.get("saleVal")+pmSaleDetails.get("saleVal")>
		        	            
							    <fo:block >
        							<fo:table  table-layout="fixed" width="100%" space-before="0.2in">
		        				    <fo:table-column column-width="40%"/>
        						    <fo:table-column column-width="60%"/>
        						    <fo:table-body>
        						     <fo:table-row> 
		    						   <fo:table-cell border-style="solid">
        						         <fo:block >  
        								  <fo:table   table-layout="fixed" width="100%" space-before="0.2in">
							        	<fo:table-column column-width="100%"/>
							        		<fo:table-body>		
		    							 <fo:table-row> 
		    								<fo:table-cell>
		    								  <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> RETAILER COPY	</fo:block>
							                  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="16pt" keep-together="always"> MOTHER DAIRY, KMF UNIT	</fo:block>
		    								<fo:block text-align="center" font-weight="bold" font-size="16pt" white-space-collapse="false" keep-together="always">BANGALORE - 560065.</fo:block>
		    								
		    								<fo:block  font-size="10pt">  
        										<fo:table   table-layout="fixed" width="100%" space-before="0.2in">
							        				<fo:table-column column-width="50%"/>
					        						<fo:table-column column-width="50%"/>
					        					   <fo:table-body>
					        					  <fo:table-row> 
				    								<fo:table-cell>
				    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> CASH PAYMENT	</fo:block>
				    								</fo:table-cell>
				    								<fo:table-cell>
				    								  <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always">PAN:</fo:block>
				    								</fo:table-cell>
			    							     </fo:table-row>
				    							<fo:table-row> 
					    							<fo:table-cell>
					    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> PaybleDate: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")}</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    						   <fo:table-row> 
					    							<fo:table-cell>
					    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always">  A/C NO :</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    							 <fo:table-row> 
					    							<fo:table-cell>
					    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always">  RETAILER NAME:</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    							 <fo:table-row> 
					    							<fo:table-cell>
					    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always">  RETAILER CODE:${facilityId}</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    							
				    							
				    							 <fo:table-row> 
			        								<fo:table-cell><fo:block text-align="left" white-space-collapse="false" keep-together="always"> &#160;</fo:block></fo:table-cell>
			        							</fo:table-row> 
					        					</fo:table-body>
					        					</fo:table>
        									</fo:block>
		    								</fo:table-cell>
		    							 </fo:table-row>
		    							 
        							<fo:table-row width="100%">
        								<fo:table-cell > 
        									<fo:block  font-size="10pt">  
        										<fo:table   table-layout="fixed" width="100%" space-before="0.2in">
							        				<fo:table-column column-width="190pt"/>
					        						<fo:table-column column-width="85pt"/>
					        						<fo:table-column column-width="40pt"/>
					        						<fo:table-header>
						        						<fo:table-row border-style="solid">
						        							<fo:table-cell >
						        								<fo:block font-size="10pt"  white-space-collapse="false" >PARTICULARS</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell >
						        								<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always">DATE</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell >
						        						  <fo:block text-align="right" font-size="10pt" white-space-collapse="false" keep-together="always">AMOUNT</fo:block>
						        							</fo:table-cell>
						        						</fo:table-row>	
					        						</fo:table-header>
					        						<fo:table-body>
					        						 <#assign boothAmPmList = boothSaleDetails.entrySet()>
					        						
										        		 <#list boothAmPmList as boothAmPmMap>
										        		 <#assign innerMapDetails=boothAmPmMap.getValue()>
					        							<fo:table-row>
					        							   <fo:table-cell >
					        									<fo:block text-align="left" font-size="9pt"><#if boothAmPmMap.getKey()=="AM"> NET VALUE OF MILK &amp; MILK PRODUCTS SOLD ON......</#if></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell >
					        									<fo:block text-align="left" font-size="10pt" >${innerMapDetails.get("date")}</fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="right" font-size="10pt">${innerMapDetails.get("saleVal")?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							</#list>
					        							<fo:table-row border-style="solid" >
					        							   <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt">Total</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt" ></fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="right" font-size="10pt">${totalValue?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row>
					        							  <fo:table-cell number-columns-spanned="3"  >
					        									<fo:block text-align="left" wrap-option="wrap"  font-size="10pt"> TOTAL AMT TO BE PAID:  ${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							 <fo:table-row>
					        							   <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"> </fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"></fo:block>
					        								</fo:table-cell>
					        								 <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"></fo:block>
					        								</fo:table-cell>
					        								
					        							</fo:table-row>
					        							
					        							<fo:table-row>
					        							   <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"></fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt">Cashier</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							
					        						   </fo:table-body>
					        						   </fo:table>
					        						   
					        						   </fo:block>
					        						   
					        						   </fo:table-cell>
					        						   
					        						 </fo:table-row>
					        				   </fo:table-body>
					        			   </fo:table>
					        		     </fo:block>
					        	       </fo:table-cell>
					        						   <#-- end of  RetailerCopy-->
					        						   
					        			<fo:table-cell  border-style="solid">
        						         <fo:block >  
        								  <fo:table   table-layout="fixed" width="100%" space-before="0.2in">
							        	<fo:table-column column-width="100%"/>
							        		<fo:table-body>		
		    							 <fo:table-row> 
		    								<fo:table-cell>
		    								  <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> BANK COPY	</fo:block>
							                  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="16pt" keep-together="always"> MOTHER DAIRY, KMF UNIT	,BANGALORE - 560065.</fo:block>
		    								<fo:block  font-size="10pt">  
        										<fo:table   table-layout="fixed" width="100%" space-before="0.2in">
							        				<fo:table-column column-width="50%"/>
					        						<fo:table-column column-width="50%"/>
					        					   <fo:table-body>
					        					  <fo:table-row> 
				    								<fo:table-cell>
				    								  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold"   font-size="12pt" keep-together="always"> CASH PAYMENT CHALLAN	</fo:block>
				    								</fo:table-cell>
			    							     </fo:table-row>
				    							<fo:table-row> 
				    							    <fo:table-cell>
				    								  <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"></fo:block>
				    								</fo:table-cell>
					    							<fo:table-cell>
					    								  <fo:block text-align="center" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> PaybleDate: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd/MM/yyyy")}</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    							
				    						   <fo:table-row> 
					    							<fo:table-cell>
					    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> A/C NO :</fo:block>
					    							</fo:table-cell>
					    							<fo:table-cell>
					    								  <fo:block text-align="right" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> PAN:</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    							 <fo:table-row> 
					    							<fo:table-cell>
					    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> RETAILER NAME:</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    							 <fo:table-row> 
					    							<fo:table-cell>
					    								  <fo:block text-align="left" white-space-collapse="false" font-family="Courier,monospace"  font-size="10pt" keep-together="always"> RETAILER CODE:${facilityId}</fo:block>
					    							</fo:table-cell>
				    							</fo:table-row>
				    							
				    							 <fo:table-row> 
			        								<fo:table-cell><fo:block text-align="left" white-space-collapse="false" keep-together="always"> &#160;</fo:block></fo:table-cell>
			        							</fo:table-row> 
					        					</fo:table-body>
					        					</fo:table>
        									</fo:block>
		    								</fo:table-cell>
		    							 </fo:table-row>
		    							 
        							<fo:table-row width="100%">
        								<fo:table-cell> 
        									<fo:block  font-size="10pt">  
        										<fo:table   table-layout="fixed" width="100%" space-before="0.2in">
							        				<fo:table-column column-width="210pt"/>
					        						<fo:table-column column-width="85pt"/>
					        						<fo:table-column column-width="50pt"/>
					        						<fo:table-column column-width="60pt"/>
					        						<fo:table-header>
						        						<fo:table-row border-style="solid">
						        							<fo:table-cell >
						        								<fo:block font-size="10pt"  white-space-collapse="false" >  PARTICULARS</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell >
						        								<fo:block text-align="left" font-size="10pt" white-space-collapse="false" keep-together="always">DATE</fo:block>
						        							</fo:table-cell>
						        						   <fo:table-cell >
						        								<fo:block text-align="center" font-size="10pt" white-space-collapse="false" keep-together="always">SHIFT</fo:block>
						        							</fo:table-cell>
						        							<fo:table-cell >
						        						  <fo:block text-align="right" font-size="10pt" white-space-collapse="false" keep-together="always"> AMOUNT</fo:block>
						        							</fo:table-cell>
						        						</fo:table-row>	
					        						</fo:table-header>
					        						<fo:table-body>
					        						 <#assign boothAmPmList = boothSaleDetails.entrySet()>
					        						
										        		 <#list boothAmPmList as boothAmPmMap>
										        		 <#assign innerMapDetails=boothAmPmMap.getValue()>
					        							<fo:table-row>
					        							   <fo:table-cell >
					        									<fo:block text-align="left" font-size="9pt"><#if boothAmPmMap.getKey()=="AM"> NET VALUE OF MILK &amp; MILK PRODUCTS SOLD ON......</#if></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell >
					        									<fo:block text-align="left" font-size="10pt" >${innerMapDetails.get("date")}</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt" >${innerMapDetails.get("shift")}</fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="right" font-size="10pt">${innerMapDetails.get("saleVal")?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							</#list>
					        							<fo:table-row border-style="solid">
					        							   <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt">Total</fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt" ></fo:block>
					        								</fo:table-cell>
					        								<fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt" ></fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="right" font-size="10pt">${totalValue?string("#0.00")}</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							<fo:table-row>
		        	                                        <fo:table-cell number-columns-spanned="4"  >
					        									<fo:block text-align="left" wrap-option="wrap"  font-size="10pt">   TOTAL AMT TO BE PAID:${Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(Static["java.lang.Double"].parseDouble(totalValue?string("#0")), "%rupees-and-paise", locale).toUpperCase()} ONLY</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							
					        							
					        							 <fo:table-row>
					        							   <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"> </fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"></fo:block>
					        								</fo:table-cell>
					        								 <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"></fo:block>
					        								</fo:table-cell>
					        								 <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"></fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							
					        						    <fo:table-row>
					        							   <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt">Retailer's Signature</fo:block>
					        								</fo:table-cell>
		        	                                        <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt">Cashier</fo:block>
					        								</fo:table-cell>
					        								 <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt"></fo:block>
					        								</fo:table-cell>
					        								 <fo:table-cell >
					        									<fo:block text-align="center" font-size="10pt">Manager</fo:block>
					        								</fo:table-cell>
					        							</fo:table-row>
					        							
					        						   </fo:table-body>
					        						   </fo:table>
					        						   
					        						   </fo:block>
					        						   
					        						   </fo:table-cell>
					        						   
					        						 </fo:table-row>
					        						 
					        						   </fo:table-body>
					        						   </fo:table>
					        						   </fo:block>
					        						   </fo:table-cell>
					        						   </fo:table-row>
					        						   </fo:table-body>
					        						   </fo:table>
					        						   </fo:block>
					        						   
					        						   
					        						   
		        			</#list>
		        		</fo:block>	
		        		<#--
	        		    <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
	        		    <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
	        		    <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
	        	        <fo:block font-size="10pt">Net Balance as on :<fo:inline font-weight="bold" text-decoration="underline" >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yyyy")}&#160;</fo:inline></fo:block>
                        <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
                        <#assign openingBal = booths.OpeningBAL>
                         <#assign totalAmount = openingBal+totalValue>
                       
                        <fo:block  font-size="10pt">&#160;Total Amount : <fo:inline font-weight="bold" text-decoration="underline" >${totalAmount?string("#0.00")}&#160;</fo:inline></fo:block> 
                  
                        <fo:block font-size="10pt" >**(Opening Balance(<fo:inline font-weight="bold" >${openingBal}</fo:inline>)+ Pesent Bill Amount(<fo:inline font-weight="bold" >${totalValue}</fo:inline>) = Total(<fo:inline font-weight="bold" >${totalAmount}</fo:inline>))</fo:block> 
                        <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
                        <fo:block font-size="10pt" >email:info@suprajadairy.in</fo:block> 
                        <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
                        <fo:block text-align="center" white-space-collapse="false" font-weight="bold" font-size="10pt" keep-together="always">&#160;</fo:block>
                        <fo:block >&#160;</fo:block>
                        <fo:block >&#160;</fo:block>
                        <fo:block >&#160;</fo:block>
                        <fo:block font-size="10pt" text-align="left" white-space-collapse="false" keep-together="always" >&#160;Prepared By&#160;                  &#160;Asst.Finance Controller                 &#160;A/C Department</fo:block>
	        			 -->             
		          	</fo:flow>
		        </fo:page-sequence>
		      	</#list>
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