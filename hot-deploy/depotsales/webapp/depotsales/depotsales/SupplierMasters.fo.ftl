<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
           <fo:simple-page-master master-name="main" page-height="15in" page-width="15in"
					 margin-left="0.5in" margin-right="0.4in"  margin-top="0.4in" margin-bottom="0.2in" >
               <fo:region-body margin-top="0.8in"/>
                <fo:region-before extent="0.7in"/>
				<fo:region-after extent="1.9in"/>

            </fo:simple-page-master>
           
        </fo:layout-master-set>
		      <#if supplierMastersList?has_content> 
		          
 			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="20pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
        			
					<fo:block text-align="center" keep-together="always" white-space-collapse="false">SUPPLIER MASTER REPORT</fo:block>

	     			<fo:table width="100%">    
	     			
	     					

	     					<fo:table-column column-width="5%"/>	            
	                   		<fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="4%"/>                
		                    <fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="5%"/>
		                    <fo:table-column column-width="8%"/>
		                   	<fo:table-column column-width="6%"/>
	                   		<#--<fo:table-column column-width="5%"/>
		                    <fo:table-column column-width="5%"/>-->              
		                    <fo:table-column column-width="12%"/>
		                    <fo:table-column column-width="12%"/>
		                    <fo:table-column column-width="9%"/>
		                   <fo:table-column column-width="4%"/>
		                    <fo:table-column column-width="4%"/>                
		                    <fo:table-column column-width="4%"/>
		                    <fo:table-column column-width="3%"/>
		                    <#--<fo:table-column column-width="2%"/>-->
		                   
		                     
		                    <fo:table-body> 
	                       	<fo:table-row>
	                       		
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="15pt">Party Id</fo:block>
		                            </fo:table-cell>

									<fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="15pt">Name</fo:block>
		                            </fo:table-cell>
									
									<fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">contact no</fo:block>
		                            </fo:table-cell>
																																								
									<fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="13pt">Address1</fo:block>
		                            </fo:table-cell>
				
									<fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">Address 2</fo:block>
		                            </fo:table-cell>
		                            
									<fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="14pt">City</fo:block>
		                            </fo:table-cell>

		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="13pt">Post code</fo:block>
		                            </fo:table-cell>
		                            
		                            <#--<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">State</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">Country</fo:block>
		                            </fo:table-cell>-->
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="14pt">CST No</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="14pt">TAN No</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="13pt">PAN No</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">TIN No</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Acc name</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Acc no</fo:block>
		                            </fo:table-cell>
		                            
									<fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">ifsc code</fo:block>
		                            </fo:table-cell>
		                            <#--<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">Branch Code</fo:block>
		                            </fo:table-cell>-->
							</fo:table-row>
							</fo:table-body>
	     			</fo:table>
				</fo:static-content>
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
      				<fo:block font-family="Courier,monospace">
                		<fo:table width="100%">             
	                		<fo:table-column column-width="5%"/>
	                   		<fo:table-column column-width="11%"/>
		                    <fo:table-column column-width="4%"/>                
		                    <fo:table-column column-width="13%"/>
		                    <fo:table-column column-width="5%"/>
		                    <fo:table-column column-width="8%"/>
		                   	<fo:table-column column-width="6%"/>
	                   		<#--<fo:table-column column-width="5%"/>
		                    <fo:table-column column-width="5%"/>-->              
		                    <fo:table-column column-width="12%"/>
		                    <fo:table-column column-width="12%"/>
		                    <fo:table-column column-width="9%"/>
		                   <fo:table-column column-width="4%"/>
		                    <fo:table-column column-width="4%"/>                
		                    <fo:table-column column-width="4%"/>
		                    <fo:table-column column-width="3%"/>
		                    <#--<fo:table-column column-width="2%"/>-->
		                   
		                     
		                    <fo:table-body>
	                       	<#--<fo:table-row>
	                     
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">PartyId</fo:block>
		                            </fo:table-cell>

									<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">Name</fo:block>
		                            </fo:table-cell>
									
									<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">contact no</fo:block>
		                            </fo:table-cell>
																																								
									<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">Address1</fo:block>
		                            </fo:table-cell>
				
									<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="10pt">Address2</fo:block>
		                            </fo:table-cell>
		                            
									<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">City</fo:block>
		                            </fo:table-cell>

		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">Post code</fo:block>
		                            </fo:table-cell>
		                            
		                            <#--<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">State</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">Country</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">CST No</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">TAN No</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="12pt">PAN No</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Tin no</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Acc name</fo:block>
		                            </fo:table-cell>
		                            
		                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">Acc no</fo:block>
		                            </fo:table-cell>
		                            
									<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-weight="bold" font-size="11pt">ifsc code</fo:block>
		                            </fo:table-cell>
		                            <#--<fo:table-cell border-right-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">Branch Code</fo:block>
		                            </fo:table-cell>
							</fo:table-row>-->   
							<#list supplierMastersList as eachDetails>
							
				
			 			 <fo:table-row>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid" border-left-style = "solid">	
		                            	<fo:block text-align="center" font-size="12pt">${eachDetails.get("partyId")?if_exists}</fo:block> 
		                            </fo:table-cell>

									<fo:table-cell border-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-size="12pt">${eachDetails.get("name")?if_exists}</fo:block>
		                            </fo:table-cell>
									
									<fo:table-cell border-style = "solid" border-top-style = "solid">	
		                            	<fo:block text-align="center" font-size="12pt">${eachDetails.get("contactNo")?if_exists}</fo:block>
		                            </fo:table-cell>
									
									<fo:table-cell border-style = "solid" border-top-style = "solid">	
		                         		<fo:block text-align="center" font-size="12pt">${eachDetails.get("paAddress1")?if_exists}</fo:block>
		                            </fo:table-cell>
									
									<fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("paAddress2")?if_exists}</fo:block>
		                            </fo:table-cell>
									
									<fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("city")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("postCode")?if_exists}</fo:block>
		                            </fo:table-cell>
		                           <#--<fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">${eachDetails.get("state")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">${eachDetails.get("country")?if_exists}</fo:block>
		                            </fo:table-cell>--> 
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("cstNo")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("tanNo")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("panNo")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("tinNo")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("bankAccountName")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("bankAccountCode")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" font-size="12pt">${eachDetails.get("ifscCode")?if_exists}</fo:block>
		                            </fo:table-cell>
		                            <#--<fo:table-cell border-style = "solid" border-top-style = "solid">	
		   		                    	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">${eachDetails.get("branchCode")?if_exists}</fo:block>
		                            </fo:table-cell>-->
							</fo:table-row>							
						</#list>		
	                      </fo:table-body>
                     </fo:table>
                 </fo:block>
			</fo:flow>
  		</fo:page-sequence>
		</#if>
	</fo:root>
</#escape>



