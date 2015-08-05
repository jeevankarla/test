<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
		<fo:layout-master-set>
			<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="0.3in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
				        <#if allDetailsRegisterMap?has_content>
		
       <fo:page-sequence master-reference="main">
		    <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" >	 				       		
               <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}  &#160;</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
                <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block text-align="center" white-space-collapse="false" font-size="12pt"  font-weight="bold" >&#160;   MILK PROCESSING REGISTER REPORT                                      </fo:block>                
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:block text-align="left">
                   <fo:table border-style="dotted" width="100%" align="right" table-layout="fixed"  font-size="12pt">
                       <fo:table-column column-width="20pt"/>                      
					   <fo:table-column column-width="70pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="30pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="70pt"/>
   					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="70pt"/>
   					   <fo:table-column column-width="30pt"/>
   					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="80pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="60pt"/>
						 <fo:table-body>
    		               <fo:table-row height="30pt">
						           <fo:table-cell border-style="solid" number-columns-spanned="20">
						               <fo:block keep-together="always" font-weight="bold" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt"  >MOTHER DAIRY : MILK PROCESSING REGISTER-UNPROCESSED MILK                  DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")}</fo:block>
						           </fo:table-cell>
						    </fo:table-row >
                            <fo:table-row height="30pt" border-style="solid">
						           <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >S NO</fo:block>
						           </fo:table-cell>
				                   <fo:table-cell border-style="dotted" number-columns-spanned="3">
						               <fo:block text-align="center"  font-weight="bold" > OPENING BALANCE</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted" number-columns-spanned="6">
						               <fo:block text-align="center"  font-weight="bold" >RECEIPTS</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >TOTAL</fo:block>
						           </fo:table-cell> 
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >GAIN/</fo:block>
						           </fo:table-cell>
						           <fo:table-cell border-style="dotted" number-columns-spanned="4">
						               <fo:block text-align="center"  font-weight="bold" >ISSUES</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted" number-columns-spanned="4">
						               <fo:block text-align="center"  font-weight="bold" >CLOSING BALANCE</fo:block>
						           </fo:table-cell>		           
						    </fo:table-row>
						    <fo:table-row height="20pt" border-style="solid">
						           <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" ></fo:block>
						           </fo:table-cell>
				                   <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell> 
						           <fo:table-cell border-style="dotted" >
						               <fo:block text-align="center"  font-weight="bold" >SOU</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >DC NO</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >TANKER</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>
						           	 <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>
						           	 <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell>	
						           <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" ></fo:block>
						           </fo:table-cell>	
						             <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >LOSS</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >TO</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell>
						           	 <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >TYP</fo:block>
						           </fo:table-cell>			           
						    </fo:table-row>
						      
					    </fo:table-body>  
				    </fo:table>
			   </fo:block>
			   
<fo:block font-family="Courier,monospace"  font-size="10pt">
<fo:table border-style="solid">
<fo:table-column column-width="20pt"/>
<fo:table-column column-width="170pt"/>
<fo:table-column column-width="300pt"/>
<fo:table-column column-width="70pt"/>
<fo:table-column column-width="30pt"/>
<fo:table-column column-width="220pt"/>
<fo:table-column column-width="240pt"/>

<fo:table-body>
<#assign allDetailsRegister = allDetailsRegisterMap.entrySet()>
     <#list allDetailsRegister as allDetailsRegisterDetails>
               <#assign openingBalSiloDetails = allDetailsRegisterDetails.getValue().get("openingBalSiloMap")?if_exists>                   
               <#assign receiptSiloDetails = allDetailsRegisterDetails.getValue().get("receiptSiloMap")?if_exists>  
               <#assign totInventoryQty = allDetailsRegisterDetails.getValue().get("totInventoryQty")?if_exists> 
               <#assign gainLossVariance = allDetailsRegisterDetails.getValue().get("gainLossVariance")?if_exists>                   
               <#assign IssuedSiloDetails = allDetailsRegisterDetails.getValue().get("IssuedSiloMap")?if_exists>
               <#assign closingBalance = allDetailsRegisterDetails.getValue().get("closingBalance")?if_exists>                   
 <fo:table-row border-style="solid">
	   <fo:table-cell >
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="30pt" />
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell >
								   <fo:block text-align="left" font-size="10pt"> ${allDetailsRegisterDetails.getKey()?if_exists} </fo:block>
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell>    
	    <fo:table-cell  border-style="dotted">
		 <fo:block text-align="left" >			   
						 <fo:table >
							  <fo:table-column column-width="70pt"/>
							  <fo:table-column column-width="50pt"/>										  
							  <fo:table-column column-width="50pt"/>
		                          <fo:table-body>
		                          <#if openingBalSiloDetails?has_content>
									   <fo:table-row>
								           <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt">${openingBalSiloDetails.openingQty?if_exists?string("##0.00")}</fo:block>  
										   </fo:table-cell>
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt">${openingBalSiloDetails.openingFat?if_exists}</fo:block>
										   </fo:table-cell >
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt">${openingBalSiloDetails.openingSnf?if_exists}</fo:block>
										   </fo:table-cell >							   
				                        </fo:table-row>  
				                        </#if>                                                                  
		                            </fo:table-body>   
	 	                      </fo:table>			 
				        </fo:block>
	</fo:table-cell>						   			
	<fo:table-cell  border-style="dotted">
	<fo:block text-align="left" >
                          <fo:table >
					<fo:table-column column-width="30pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="70pt"/>
   					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="50pt"/>
                     <fo:table-body>
                     <#if receiptSiloDetails?has_content>
                     <#assign receiptSiloDetail = receiptSiloDetails.entrySet()?if_exists>												
					  <#list receiptSiloDetail as receiptSiloData>
                                                       		                                                   
									   <fo:table-row>
								           <fo:table-cell >
											  <fo:block text-align="center" font-size="10pt">${receiptSiloData.getValue().get("partyId")?if_exists}</fo:block>  
										   </fo:table-cell>
										   <fo:table-cell >
											  <fo:block text-align="left" font-size="10pt">${receiptSiloData.getValue().get("dcNo")?if_exists}</fo:block>
										   </fo:table-cell >
										   <fo:table-cell >
											  <fo:block text-align="center" font-size="10pt">${receiptSiloData.getValue().get("containerId")?if_exists}</fo:block>
										   </fo:table-cell >
										    <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt">${receiptSiloData.getValue().get("receivedQuantity")?if_exists?string("##0.00")}</fo:block>
										   </fo:table-cell >
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt">${receiptSiloData.getValue().get("receivedFat")?if_exists}</fo:block>
										   </fo:table-cell >
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt">${receiptSiloData.getValue().get("receivedSnf")?if_exists}</fo:block>
										   </fo:table-cell >									   
				                        </fo:table-row> 
				                        </#list>  
				                        </#if>                                                                 
		                            </fo:table-body>   
	 	                      </fo:table>			 
				        </fo:block>	
	    </fo:table-cell>						   			
	 <fo:table-cell  border-style="dotted">
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="70pt"/>
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt"> ${totInventoryQty?if_exists?string("##0.00")} </fo:block>
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell> 
	     <fo:table-cell >
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="30pt"/>
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell>
								   <fo:block text-align="right" font-size="10pt"> ${gainLossVariance?if_exists} </fo:block>
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell>    						   			
	 <fo:table-cell  border-style="dotted">
 <fo:block text-align="left" >			   
		 <fo:table >
	   <fo:table-column column-width="60pt"/>
	   <fo:table-column column-width="50pt"/>
	   <fo:table-column column-width="50pt"/>
	   <fo:table-column column-width="60pt"/>
                  <fo:table-body>
                     <#if IssuedSiloDetails?has_content>
                    <#assign IssuedSiloDetail = IssuedSiloDetails.entrySet()?if_exists>											
  					  <#list IssuedSiloDetail as IssuedSiloData>
					   <fo:table-row>
				           <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"> <#if IssuedSiloData.getValue().get("qty")?has_content>${IssuedSiloData.getValue().get("qty")?if_exists?string("##0.00")}<#else>0</#if></fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell > 
						   <fo:table-cell >
							  <fo:block text-align="center" font-size="10pt">${IssuedSiloData.getKey()?if_exists}</fo:block>
						   </fo:table-cell >				   
                        </fo:table-row> 
                        </#list>
                        </#if>                                                                   
                    </fo:table-body>   
              </fo:table>			 
        </fo:block>
	</fo:table-cell>
	 <fo:table-cell  border-style="dotted">
 <fo:block text-align="left" >			   
		 <fo:table >
	   <fo:table-column column-width="80pt"/>
	   <fo:table-column column-width="50pt"/>
	   <fo:table-column column-width="50pt"/>
	   <fo:table-column column-width="60pt"/>
                  <fo:table-body>	
                  <#if closingBalance?has_content>
					   <fo:table-row>
				           <fo:table-cell  >
							  <fo:block text-align="right" font-size="10pt">${closingBalance.dayCloseBal?if_exists?string("##0.00")}</fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
						      <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell > 
						   <fo:table-cell  >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >				   
                        </fo:table-row>
                        </#if>
                    </fo:table-body>   
           </fo:table>			 
         </fo:block>
       </fo:table-cell>
     </fo:table-row>
   </#list>
  <#if allSilosTotalsMap?has_content>
  <fo:table-row border-style="solid">
	   <fo:table-cell >
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="30pt" />
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell >
								   <fo:block text-align="left" font-size="10pt">  </fo:block>
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell>    
	    <fo:table-cell>
		 <fo:block text-align="left" >			   
						 <fo:table >
							  <fo:table-column column-width="70pt"/>
							  <fo:table-column column-width="50pt"/>										  
							  <fo:table-column column-width="50pt"/>
		                          <fo:table-body>
									   <fo:table-row>
								      		<fo:table-cell >
											  <fo:block text-align="right" font-size="10pt">${allSilosTotalsMap.totOpeningQty?if_exists?string("##0.00")}</fo:block>  
										   </fo:table-cell>
										   	<fo:table-cell number-columns-spanned="2">
										   </fo:table-cell>
				                        </fo:table-row>  
		                            </fo:table-body>   
	 	                      </fo:table>			 
				        </fo:block>
	</fo:table-cell>						   			
	<fo:table-cell  border-style="dotted">
	<fo:block text-align="left" >
                          <fo:table >
					<fo:table-column column-width="30pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="70pt"/>
   					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="50pt"/>
   					   <fo:table-column column-width="50pt"/>
                     <fo:table-body>
						   <fo:table-row>
				                 <fo:table-cell  number-columns-spanned="3">
								   </fo:table-cell >
						           <fo:table-cell >
									  <fo:block text-align="right" font-size="10pt">${allSilosTotalsMap.totReceiptQty?if_exists?string("##0.00")}</fo:block>  
								   </fo:table-cell>
				                 <fo:table-cell  number-columns-spanned="2">
									  <fo:block text-align="left" font-size="10pt"></fo:block>
								   </fo:table-cell >
	                        </fo:table-row> 
		             </fo:table-body>   
	          </fo:table>			 
	     </fo:block>	
	    </fo:table-cell>						   			
	 <fo:table-cell  border-style="dotted">
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="70pt"/>
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell >
									<fo:block text-align="right" font-size="10pt">${allSilosTotalsMap.totOpenReceiptQty?if_exists?string("##0.00")}</fo:block>  
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell> 
	     <fo:table-cell >
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="30pt"/>
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell>
									 <fo:block text-align="right" font-size="10pt">${allSilosTotalsMap.totVarianceQty?if_exists}</fo:block>  
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell>    						   			
	 <fo:table-cell  border-style="dotted">
 <fo:block text-align="left" >			   
		 <fo:table >
	   <fo:table-column column-width="80pt"/>
	   <fo:table-column column-width="30pt"/>
	   <fo:table-column column-width="50pt"/>
	   <fo:table-column column-width="60pt"/>
                  <fo:table-body>
					   <fo:table-row>
				           <fo:table-cell >
								 <fo:block text-align="right" font-size="10pt">${allSilosTotalsMap.totIssueQty?if_exists?string("##0.00")}</fo:block>  
						   </fo:table-cell >
						    <fo:table-cell  number-columns-spanned="3">
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
                        </fo:table-row> 
                    </fo:table-body>   
              </fo:table>			 
        </fo:block>
	</fo:table-cell>
	 <fo:table-cell  border-style="dotted">
 <fo:block text-align="left" >			   
		 <fo:table >
	   <fo:table-column column-width="80pt"/>
	   <fo:table-column column-width="50pt"/>
	   <fo:table-column column-width="50pt"/>
	   <fo:table-column column-width="60pt"/>
                  <fo:table-body>	
                  <#if allSilosTotalsMap.totDayClosingQty?has_content>
					   <fo:table-row>
				           <fo:table-cell  >
								<fo:block text-align="right" font-size="10pt">${allSilosTotalsMap.totDayClosingQty?if_exists?string("##0.00")}</fo:block>  
						   </fo:table-cell >
						    <fo:table-cell  number-columns-spanned="3">
						      <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
                        </fo:table-row>
                        </#if>
                    </fo:table-body>   
           </fo:table>			 
         </fo:block>
       </fo:table-cell>
     </fo:table-row>
  </#if>
 </fo:table-body>   
 </fo:table>	
</fo:block>



<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="25pt" > &#160;&#160;  </fo:block>
<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="25pt" > &#160;&#160;  </fo:block>
<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="25pt" > &#160;&#160;  </fo:block>


 <fo:block text-align="left">
                   <fo:table border-style="dotted" width="100%" align="right" table-layout="fixed"  font-size="12pt">
                       <fo:table-column column-width="30pt"/>                      
					   <fo:table-column column-width="90pt"/>
   					   <fo:table-column column-width="30pt"/>
   					   <fo:table-column column-width="30pt"/>
   					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="80pt"/>
   					   <fo:table-column column-width="100pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="100pt"/>
   					   <fo:table-column column-width="80pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="80pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="40pt"/>
   					   <fo:table-column column-width="40pt"/>
						 <fo:table-body>
    		               <fo:table-row height="30pt">
						           <fo:table-cell border-style="solid" number-columns-spanned="18">
						               <fo:block keep-together="always" font-weight="bold" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt"  >MOTHER DAIRY : MILK PROCESSING REGISTER-PASTEURISED MILK                  DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")}</fo:block>
						           </fo:table-cell>
						    </fo:table-row >
                            <fo:table-row height="30pt" border-style="solid">
						           <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >S NO</fo:block>
						           </fo:table-cell>
				                   <fo:table-cell border-style="dotted" number-columns-spanned="4">
						               <fo:block text-align="center"  font-weight="bold" > OPENING BALANCE</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted" number-columns-spanned="4">
						               <fo:block text-align="center"  font-weight="bold" >RECEIPTS</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >TOTAL</fo:block>
						           </fo:table-cell> 
						           <fo:table-cell border-style="dotted" number-columns-spanned="4">
						               <fo:block text-align="center"  font-weight="bold" >ISSUES</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted" number-columns-spanned="4">
						               <fo:block text-align="center"  font-weight="bold" >CLOSING BALANCE</fo:block>
						           </fo:table-cell>		           
						    </fo:table-row>
						    <fo:table-row height="20pt" border-style="solid">
						           <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" ></fo:block>
						           </fo:table-cell>
				                   <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell>
						            <fo:table-cell border-style="dotted" >
						               <fo:block text-align="center"  font-weight="bold" >TYP</fo:block>
						           </fo:table-cell> 
						           <fo:table-cell border-style="dotted" >
						               <fo:block text-align="center"  font-weight="bold" >SOU</fo:block>
						           </fo:table-cell>
						            
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>
						           	 <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>
						           	 <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >TO</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >QTY</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >FAT</fo:block>
						           </fo:table-cell>	
						            <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >SNF</fo:block>
						           </fo:table-cell>
						           	 <fo:table-cell border-style="dotted">
						               <fo:block text-align="center"  font-weight="bold" >TYP</fo:block>
						           </fo:table-cell>			           
						    </fo:table-row>
						      
					    </fo:table-body>  
				    </fo:table>
			   </fo:block>
			   
<fo:block font-family="Courier,monospace"  font-size="10pt">
<fo:table border-style="solid">
<fo:table-column column-width="30pt"/>
<fo:table-column column-width="210pt"/>
<fo:table-column column-width="280pt"/>
<fo:table-column column-width="100pt"/>
<fo:table-column column-width="200pt"/>
<fo:table-column column-width="200pt"/>

<fo:table-body>
<#-->
  <#assign allDetailsRegister = allDetailsRegisterMap.entrySet()>
     <#list allDetailsRegister as allDetailsRegisterDetails>
               <#assign openingBalSiloDetails = allDetailsRegisterDetails.getValue().get("openingBalSiloMap")?if_exists>                   
               <#assign receiptSiloDetails = allDetailsRegisterDetails.getValue().get("receiptSiloMap")?if_exists>  
               <#assign totInventoryQty = allDetailsRegisterDetails.getValue().get("totInventoryQty")?if_exists>                   
               <#assign IssuedSiloDetails = allDetailsRegisterDetails.getValue().get("IssuedSiloMap")?if_exists>
               <#assign closingBalance = allDetailsRegisterDetails.getValue().get("closingBalance")?if_exists>     -->              
 <fo:table-row border-style="solid">
	   <fo:table-cell >
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="30pt" />
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell >
								   <fo:block text-align="center" font-size="10pt"> <#-->${allDetailsRegisterDetails.getKey()?if_exists} --></fo:block>
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell>    
	    <fo:table-cell  border-style="dotted">
		 <fo:block text-align="left" >			   
						 <fo:table >
							  <fo:table-column column-width="90pt"/>
							  <fo:table-column column-width="30pt"/>										  
							  <fo:table-column column-width="30pt"/>
  							  <fo:table-column column-width="60pt"/>
		                          <fo:table-body>
		                     <#-->     <#if openingBalSiloDetails?has_content>-->
									   <fo:table-row>
								           <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt"><#-->${openingBalSiloDetails.openingQty?if_exists}--></fo:block>  
										   </fo:table-cell>
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt"><#-->${openingBalSiloDetails.openingFat?if_exists}--></fo:block>
										   </fo:table-cell >
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt"><#-->${openingBalSiloDetails.openingSnf?if_exists}--></fo:block>
										   </fo:table-cell >
										    <fo:table-cell >
											  <fo:block text-align="center" font-size="10pt"></fo:block>
										   </fo:table-cell >								   
				                        </fo:table-row>  
				                <#-->        </#if>  -->                                                                  
		                            </fo:table-body>   
	 	                      </fo:table>			 
				        </fo:block>
	</fo:table-cell>						   			
	<fo:table-cell  border-style="dotted">
	<fo:block text-align="left" >
                          <fo:table >
					   <fo:table-column column-width="60pt"/>
   					   <fo:table-column column-width="80pt"/>
   					   <fo:table-column column-width="100pt"/>
   					   <fo:table-column column-width="40pt"/>
                     <fo:table-body>
        <#-->             <#if receiptSiloDetails?has_content>
                     <#assign receiptSiloDetail = receiptSiloDetails.entrySet()?if_exists>												
					  <#list receiptSiloDetail as receiptSiloData> -->
                                                       		                                                   
									   <fo:table-row>
								           <fo:table-cell >
											  <fo:block text-align="center" font-size="10pt"></fo:block>  
										   </fo:table-cell>
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt"></fo:block>
										   </fo:table-cell >
										   <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt"></fo:block>
										   </fo:table-cell >
										    <fo:table-cell >
											  <fo:block text-align="right" font-size="10pt"></fo:block>
										   </fo:table-cell >
				                        </fo:table-row> 
				            <#-->       </#list>  
				                        </#if>      -->                                                           
		                            </fo:table-body>   
	 	                      </fo:table>			 
				        </fo:block>	
	    </fo:table-cell>						   			
	 <fo:table-cell >
			<fo:block text-align="left" >
						 <fo:table>
						  <fo:table-column column-width="100pt"/>
	                      <fo:table-body>
						   <fo:table-row>
					           <fo:table-cell >
								   <fo:block text-align="right" font-size="10pt">  </fo:block>
							   </fo:table-cell>
	                          </fo:table-row>
						</fo:table-body>   
					</fo:table>	
	       </fo:block>
	    </fo:table-cell>    						   			
	 <fo:table-cell  border-style="dotted">
 <fo:block text-align="left" >			   
		 <fo:table >
	   <fo:table-column column-width="80pt"/>
	   <fo:table-column column-width="40pt"/>
	   <fo:table-column column-width="40pt"/>
	   <fo:table-column column-width="40pt"/>
                  <fo:table-body>
                                                                  
					   <fo:table-row>
				           <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell > 
						   <fo:table-cell >
							  <fo:block text-align="center" font-size="10pt"></fo:block>
						   </fo:table-cell >				   
                        </fo:table-row> 
                    </fo:table-body>   
              </fo:table>			 
        </fo:block>
	</fo:table-cell>
	 <fo:table-cell  border-style="dotted">
 <fo:block text-align="left" >			   
		 <fo:table >
	   <fo:table-column column-width="80pt"/>
	   <fo:table-column column-width="40pt"/>
	   <fo:table-column column-width="40pt"/>
	   <fo:table-column column-width="40pt"/>
                  <fo:table-body>	
					   <fo:table-row>
				           <fo:table-cell  >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
						      <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell >
						    <fo:table-cell >
							  <fo:block text-align="right" font-size="10pt"></fo:block>
						   </fo:table-cell > 
						   <fo:table-cell  >
							  <fo:block text-align="center" font-size="10pt"></fo:block>
						   </fo:table-cell >				   
                        </fo:table-row>
                    </fo:table-body>   
             </fo:table>			 
          </fo:block>
        </fo:table-cell>
      </fo:table-row>
  <#-->  </#list> -->
  </fo:table-body>   
 </fo:table>	
</fo:block>

</fo:flow>
       </fo:page-sequence>	
	<#else>
	       <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="16pt" text-align="center">
	            		 No Records Found....!  			   
	            			
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>				
	</#if>
</fo:root>
</#escape>				           