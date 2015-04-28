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
       <fo:page-sequence master-reference="main">
		    <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">	 				       		
			   <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>			
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
                   <fo:table border-style="dotted" text-align="left">
                       <fo:table-column column-width="30pt"/>                      
					   <fo:table-column column-width="120pt"/>
					   <fo:table-column column-width="310pt"/>
					   <fo:table-column column-width="100pt"/>
					   <fo:table-column column-width="150pt"/>
					   <fo:table-column column-width="150pt"/>					   
						   <fo:table-body> 
						       <fo:table-row height="30pt">
						           <fo:table-cell border-style="dotted">
						               <fo:block text-align="center" padding-before="0.6cm" font-weight="bold" >SNO</fo:block>
						           </fo:table-cell>
						           <fo:table-cell border-style="dotted">
				                      <fo:block>
					                     <fo:table border-style="dotted">
										    <fo:table-column column-width="60pt"/>
										    <fo:table-column column-width="30pt"/>
										    <fo:table-column column-width="30pt"/>
										       <fo:table-body> 
										           <fo:table-row height="25pt">
										              <fo:table-cell border-style="dotted" number-columns-spanned="3">
										                 <fo:block text-align="center"  font-weight="bold"  >OPENING BALANCE</fo:block>
										              </fo:table-cell>
										          </fo:table-row>
										         <fo:table-row height="25pt">
										            <fo:table-cell border-style="dotted">
										                <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
										            </fo:table-cell>
										            <fo:table-cell border-style="dotted">
										                <fo:block text-align="left"  font-weight="bold"  >FAT</fo:block>
										            </fo:table-cell>
										            <fo:table-cell border-style="dotted">
										                <fo:block text-align="left"  font-weight="bold"  >SNF</fo:block>
										            </fo:table-cell>
										        </fo:table-row>
										      </fo:table-body>  
									     </fo:table>
								    </fo:block> 
				                </fo:table-cell>
						        <fo:table-cell border-style="dotted">
			                       <fo:block>
				                      <fo:table border-style="dotted">
									     <fo:table-column column-width="60pt"/>
									     <fo:table-column column-width="40pt"/>
									     <fo:table-column column-width="90pt"/>
										 <fo:table-column column-width="60pt"/>
										 <fo:table-column column-width="30pt"/>
										 <fo:table-column column-width="30pt"/>

									        <fo:table-body> 
									           <fo:table-row height="25pt">
									              <fo:table-cell border-style="dotted" number-columns-spanned="6">
									                  <fo:block text-align="center"  font-weight="bold"  >RECEIPTS</fo:block>
									              </fo:table-cell>
									           </fo:table-row>
									           <fo:table-row height="25pt">
										           <fo:table-cell border-style="dotted">
										              <fo:block text-align="left"  font-weight="bold"  >SOU</fo:block>
										           </fo:table-cell>
										           <fo:table-cell border-style="dotted">
										               <fo:block text-align="left"  font-weight="bold"  >DCNO.</fo:block>
										           </fo:table-cell>
										           <fo:table-cell border-style="dotted">
										              <fo:block text-align="left"  font-weight="bold"  >TANKER NO.</fo:block>
										           </fo:table-cell>
										           <fo:table-cell border-style="dotted">
										              <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
										           </fo:table-cell>
										           <fo:table-cell border-style="dotted">
										              <fo:block text-align="left"  font-weight="bold"  >FAT</fo:block>
										           </fo:table-cell>
									               <fo:table-cell border-style="dotted">
									                   <fo:block text-align="left"  font-weight="bold"  >SNF</fo:block>
									               </fo:table-cell>
									            </fo:table-row>
									     </fo:table-body>  
								      </fo:table>
							       </fo:block> 
						       </fo:table-cell>
					           <fo:table-cell border-style="dotted">
					               <fo:block text-align="center" padding-before="0.6cm" font-weight="bold"  >TOTAL</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="dotted">
				                  <fo:block>
					                 <fo:table border-style="dotted">
									    <fo:table-column column-width="60pt"/>
									    <fo:table-column column-width="30pt"/>
									    <fo:table-column column-width="30pt"/>
									    <fo:table-column column-width="30pt"/>
									       <fo:table-body> 
										       <fo:table-row height="25pt">
										           <fo:table-cell border-style="dotted" number-columns-spanned="4">
								                     <fo:block text-align="center"  font-weight="bold"  >ISSUES</fo:block>
										           </fo:table-cell>
										      </fo:table-row>
										      <fo:table-row height="25pt">
										          <fo:table-cell border-style="dotted">
								                     <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
										          </fo:table-cell>
										          <fo:table-cell border-style="dotted">
								                     <fo:block text-align="left"  font-weight="bold"  >FAT</fo:block>
										          </fo:table-cell>
										          <fo:table-cell border-style="dotted">
							                    	 <fo:block text-align="left"  font-weight="bold"  >SNF</fo:block>
										          </fo:table-cell>
										          <fo:table-cell border-style="dotted">
								                     <fo:block text-align="left"  font-weight="bold"  >TO</fo:block>
										          </fo:table-cell>
										       </fo:table-row>
										   </fo:table-body>  
									   </fo:table>
								   </fo:block> 
					            </fo:table-cell>
					            <fo:table-cell border-style="dotted">
					               <fo:block>
					                   <fo:table border-style="dotted">
										   <fo:table-column column-width="60pt"/>
										   <fo:table-column column-width="30pt"/>
										   <fo:table-column column-width="30pt"/>
										   <fo:table-column column-width="30pt"/>
										   <fo:table-body> 
										      <fo:table-row height="25pt">
									             <fo:table-cell border-style="dotted" number-columns-spanned="4">
									                <fo:block text-align="center"  font-weight="bold"  >CLOSING BALANCE</fo:block>
									             </fo:table-cell>
											     </fo:table-row>
											     <fo:table-row height="25pt">
											         <fo:table-cell border-style="dotted">
											            <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
											         </fo:table-cell>
											         <fo:table-cell border-style="dotted">
											            <fo:block text-align="left"  font-weight="bold"  >FAT</fo:block>
											         </fo:table-cell>
											         <fo:table-cell border-style="dotted">
											             <fo:block text-align="left"  font-weight="bold"  >SNF</fo:block>
											         </fo:table-cell>
											         <fo:table-cell border-style="dotted">
											            <fo:block text-align="left"  font-weight="bold"  >TYPE</fo:block>
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
		</fo:flow>	
       </fo:page-sequence>
</fo:root>
</#escape>	     
						           