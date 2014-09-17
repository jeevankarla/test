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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".8in" margin-bottom="0.5in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if facilityFixedDepositMap?has_content> 	    
        ${setRequestAttribute("OUTPUT_FILENAME", "NewOrTerminatedRetailerReport.txt")}
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>	
              	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">	
		        	<fo:block text-align="center"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
                    	<fo:block text-align="center"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">UNIT : MOTHER DAIRY , G.K.V.K POST : YELAHANKA, BANGALORE -560065.</fo:block>
                    	<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false">  FIXED DEPOSIT MATURING FOR THE MONTH OF - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM-yyyy")}</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${printDate?if_exists}</fo:block>
              			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">==============================================================================================</fo:block> 
		        		<fo:block text-align="left"  keep-together="always" font-size="12pt" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">SI  RETAILER	RETAILER	            REF		      BANK                        FD	       EXP    </fo:block> 
		        		<fo:block text-align="left"  keep-together="always"  font-size="12pt" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">NO  CODE		   NAME 	               NO		       NAME                        AMOUNT    DATE  </fo:block>
		        		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">==============================================================================================</fo:block> 
		        		
	           <fo:block>
                 	<fo:table>
                 	 <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="140pt"/> 
            		<fo:table-column column-width="90pt"/> 
            		<fo:table-column column-width="120pt"/> 
                    <fo:table-body>
                    <#assign facilityList=facilityFixedDepositMap.entrySet()>
					<#assign gTotalAmount=0>
                    <#list facilityList as facility>
                    <fo:table-row>
						<fo:table-cell>
		            	  		<fo:block  font-size="12pt" font-weight="bold" keep-together="always" text-align="left" white-space-collapse="false"><#if facility.getKey() == "SCT_RTLR">SACHET</#if>
		            	  		<#if facility.getKey() == "SHP_RTLR">BVB</#if></fo:block>  
                       	</fo:table-cell>
				    </fo:table-row>
					<#assign sachet=facility.getValue().entrySet()>
					<#assign sNo=0>
					<#assign totalAmount=0>
					<#list sachet as sachetfacility>
					<#assign amount=0>
					<#assign fdrNumber="">
					<#assign sNo=sNo+1>
					<fo:table-row>
					        <fo:table-cell>
                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">${sNo}</fo:block>  
                       		</fo:table-cell>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">${sachetfacility.getKey()}</fo:block>  
                       		</fo:table-cell>
                       		<fo:table-cell>
                       			<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(sachetfacility.getValue().get("name")?if_exists)),21)?if_exists}</fo:block>  
                   			</fo:table-cell>
                     		<fo:table-cell>
                       			<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">
		                       			<fo:table>
					            		<fo:table-column column-width="100pt"/> 		
					            		<fo:table-column column-width="150pt"/> 	
					            		<fo:table-column column-width="90pt"/> 
					            		<fo:table-column column-width="50pt"/> 	
		                                <fo:table-body>
		                                <#assign fdrList="">
		                                <#assign fdrList=sachetfacility.getValue().get("fDRLst")>
		                                <#list fdrList as fdrItem>
		                                <#assign totalAmount=totalAmount+fdrItem.get("amount")>
			                                <fo:table-row>
			                                   <fo:table-cell>
					                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">&#160;&#160;${fdrItem.get("fdrNumber")}</fo:block>  
					                       		</fo:table-cell>
												<fo:table-cell>
					                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(fdrItem.get("bankName")?if_exists)),25)?if_exists}</fo:block>  
					                       		</fo:table-cell>
					                       		<fo:table-cell>
					                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${fdrItem.get("amount")?string("0")}</fo:block>  
					                       		</fo:table-cell>
					                       		<fo:table-cell>
					                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">&#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fdrItem.get("thruDate"), "dd/MM/yy")}</fo:block>  
					                       		</fo:table-cell>
							             	</fo:table-row>
						             	</#list>
						             </fo:table-body>
						             </fo:table>
                       			</fo:block>  
                   			</fo:table-cell>
                     </fo:table-row>  
						</#list>
						 <fo:table-row>
							<fo:table-cell>
			            		<fo:block  keep-together="always">------------------------------------------------------------------------------------------------</fo:block>  
			            	</fo:table-cell>
				        </fo:table-row>
						 <fo:table-row>
							<fo:table-cell>
				            		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">TOTAL</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		      <fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false"></fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            			<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false"></fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            	<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">
		                       		<fo:table>
					            		<fo:table-column column-width="100pt"/> 		
					            		<fo:table-column column-width="150pt"/> 	
					            		<fo:table-column column-width="90pt"/> 	
					            		<fo:table-column column-width="50pt"/> 		
		                                <fo:table-body>
		                                 <fo:table-row>
			                                 <fo:table-cell>
							            		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">&#160;</fo:block> 
							            	</fo:table-cell>
								            <fo:table-cell>
							            		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">&#160;</fo:block>  
							            	</fo:table-cell>
		                                    <fo:table-cell>
				            		          <fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${totalAmount?string("0")}</fo:block> 
				            		         </fo:table-cell>
				            		         </fo:table-row>
								             </fo:table-body>
							                </fo:table>
							            </fo:block> 		
				            	</fo:table-cell>
				       </fo:table-row>
				          <#assign gTotalAmount=gTotalAmount+totalAmount>
				          <fo:table-row>
							<fo:table-cell>
			            		<fo:block  keep-together="always">------------------------------------------------------------------------------------------------</fo:block>  
			            	</fo:table-cell>
				        </fo:table-row>
						</#list>
						 <fo:table-row>
							<fo:table-cell>
				            		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">GRAND TOTAL</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		      <fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false"></fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            			<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false"></fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            	<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">
		                       		<fo:table>
					            		<fo:table-column column-width="100pt"/> 		
					            		<fo:table-column column-width="150pt"/> 	
					            		<fo:table-column column-width="90pt"/> 	
					            		<fo:table-column column-width="50pt"/> 		
		                                <fo:table-body>
		                                 <fo:table-row>
			                                 <fo:table-cell>
							            		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">&#160;</fo:block> 
							            	</fo:table-cell>
								            <fo:table-cell>
							            		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">&#160;</fo:block>  
							            	</fo:table-cell>
		                                    <fo:table-cell>
				            		          <fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${gTotalAmount?string("0")}</fo:block> 
				            		         </fo:table-cell>
				            		         </fo:table-row>
								             </fo:table-body>
							                </fo:table>
							            </fo:block> 		
				            	</fo:table-cell>
				       </fo:table-row>
				        <fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">------------------------------------------------------------------------------------------------</fo:block>  
		            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
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