<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
                     margin-left="0.3in" margin-right="0.2in"  margin-top="0.1in" margin-bottom="0.2in" >
                <fo:region-body margin-top="0.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "EmployeeMisPunchData.pdf")}
        <#if errorMessage?has_content>
			<fo:page-sequence master-reference="main">
			   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			      <fo:block font-size="14pt">
			              ${errorMessage}.
			   	  </fo:block>
			   </fo:flow>
			</fo:page-sequence> 
		<#else>
			<#if EmplWiseDetailsMap?has_content>
				<#assign noofLines=1>
				<#assign SNo=1>
				<#assign noOfLines=1>
				<fo:page-sequence master-reference="main">
	        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">   
						
	          		</fo:static-content>
	          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		            	<fo:block font-family="Courier,monospace">
		            		<fo:table table-layout="fixed" width="50%">
		                		<fo:table-column column-width="670pt"/>
		                		<fo:table-body> 
		                     		<fo:table-row >
		                     			<fo:table-cell >
		                     				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt"> [ FORM 6 ] </fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">RETURN OF CONTRIBUTIONS </fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt"> ( In quadruplicate ) </fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">  [Regulation 26] </fo:block>  
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">Employer's Code No   : _____________________</fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">&#160;</fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">Name of Local Office : _____________________</fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">&#160;</fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">${uiLabelMap.KMFDairySubHeader}</fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">&#160;</fo:block>
											<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">Name and address of the factory or establishment:____________________</fo:block>
											<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">Particulars of the principal employer </fo:block>
											<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt">  (a)  Name                : NARASIMHA REDDY.C. </fo:block>
											<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt">  (b)  Designation.        : DIRECTOR  </fo:block>	
											<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt">  (c)  Residential address : NO.1 ,2ND  CROSS , B.LORE DAIRY  QTRSDR, M.H.MARIGOWDA ROAD,BANGALORE560029.</fo:block>	
											<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">Period : From.  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM,yyyy")}  to  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMMM,yyyy")}</fo:block>		
			                            	<fo:block text-align="left" font-family="Courier,monospace"  font-weight="bold" font-size="10pt"> &#160;</fo:block>
			                            	<fo:block text-align="left" font-family="Courier,monospace"  font-size="10pt">&#160;&#160;&#160;&#160;I furnish  below the details of the employer's share of contributions in respect of the under mentioned </fo:block>
			                            	<fo:block text-align="left" font-family="Courier,monospace"  font-size="10pt">insured persons.I here by declare that the return includes every employee,employed directly or through work</fo:block>
			                            	<fo:block text-align="left" font-family="Courier,monospace"  font-size="10pt">of the factory / establishment or purchase of raw materials ,sale or distribution of finished products etc,</fo:block>
			                            	<fo:block text-align="left" font-family="Courier,monospace"  font-size="10pt">to whom the contribution period to which this return relates,applies and that the contributions in respect </fo:block>
			                            	<fo:block text-align="left" font-family="Courier,monospace"  font-size="10pt">of employer's and employee's share have been correctly paid in  connection with the provisions of the Act</fo:block>
			                            	<fo:block text-align="left" font-family="Courier,monospace"  font-size="10pt">and regulations relating to the  payment of contributions, vide challans detailed below: </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-weight="bold" font-size="10pt"> &#160;</fo:block>  
			                            	
			                            	<#assign periodTotMapValues=periodTotMap.entrySet()>
			                            	<#list periodTotMapValues as periodTotDetails>
			                            		<#if periodTotDetails.getKey()=="Contribution">
			                            			<#assign totemployeeContribtn=periodTotDetails.getValue().get("totemployeeContribtn")>
			                            			<#assign totemployerContribtn=periodTotDetails.getValue().get("totemployerContribtn")>
			                            			<#assign totalContribution=periodTotDetails.getValue().get("totalContribution")>
			                            		</#if>
			                            	</#list>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt"> &#160;&#160;&#160;&#160;Total contribution amounting to Rs : <#if (totalContribution < 0)>${((-1)*(totalContribution))?if_exists}<#else>${totalContribution?if_exists}</#if>, comprising of Rs: ${totemployerContribtn?if_exists} as employer's share and </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt">Rs:${((-1)*totemployeeContribtn)?if_exists} As employee' s share ( Total of col.6 of the return ) paid as under: </fo:block>
			                            	<#if periodTotMap?has_content>
				                            	<#assign periodTotMapValues=periodTotMap.entrySet()>
				                            	<#list periodTotMapValues as periodValueslDetails>
				                            		<#if periodValueslDetails.getKey()!="Contribution">
						                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-weight="bold" font-size="10pt"> &#160;</fo:block>
						                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt"> (1)  Challan dated <fo:inline font-weight="bold"> ${periodValueslDetails.getKey()}</fo:inline> for      Rs : <fo:inline font-weight="bold"> <#if (periodValueslDetails.getValue() < 0)> ${((-1)*periodValueslDetails.getValue())}<#else>${periodValueslDetails.getValue()?if_exists}</#if></fo:inline>.</fo:block>
				                            		</#if>
				                            	</#list>
				                            </#if>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-weight="bold" font-size="10pt"> &#160;</fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt">Total&#160;&#160;&#160;&#160; :  Rs. ${totalContribution?if_exists}</fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt">Place&#160;&#160;&#160;&#160; :  Bengalore     </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt">Signature&#160;:___________________ </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt">Designation. Manager (Fin). </fo:block>
			                            	
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt"> &#160;</fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">Important Instructions </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace"  font-size="10pt">1.  If any I.P is appointed for the first time and  / or leaves service during  the contribution period,</fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">  &#160; indicate "A.......(date)" and / or "L.........(date)" , in the remarks column (No. 8). (Please indicate</fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt"> &#160; the name of the dispensary to which the insured </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">2.  Please indicate insurance numbers in chronological ascending order. </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">3.  Figures in columns 4,5 and 6 shall be respect of wage periods ended during the contribution  period.. </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">4.  Invariably strike totals of column 4,5 and 6 of the return. </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">5.  No overwriting  shall be made.Any corrections should be signed by the employer. </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">6.  Every page of this return should have full signature ad rubber stamp of the employer. </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">7.  'Daily wages' in the column 7 of the return shal be calculated by dividing figures in column 5 by figures </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">  &#160; in column 4,to two decimal places.</fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">  &#160; </fo:block>
			                            	<fo:block text-align="left" keep-together="always" font-family="Courier,monospace" font-size="10pt">  &#160; </fo:block>
			                            	<fo:block text-align="right" keep-together="always" font-family="Courier,monospace" font-size="10pt">  Signature : __________________ </fo:block>
			                            </fo:table-cell>
			                       	</fo:table-row>
	                           </fo:table-body>
	                     	</fo:table>
	                 	</fo:block>
					</fo:flow>
	  			</fo:page-sequence>
	  			<fo:page-sequence master-reference="main">
	        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">   
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt"> [ FORM 6 ] </fo:block>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">RETURN OF CONTRIBUTIONS </fo:block>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt"> ( In quadruplicate ) </fo:block>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="10pt">  [Regulation 26] </fo:block>  
	          		</fo:static-content>
	          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		            	<fo:block font-family="Courier,monospace">
		            		<#assign sNo=1>
		            		<#assign noofLines=1>
		            		<#assign pagetotPayableDays=0>
			               	<#assign pagetotWages=0>
			               	<#assign pagetotContribn=0>
			               	<#assign totPayableDays=0>
	                     	<#assign totWages=0>
	                     	<#assign totContribn=0>
		            		<fo:table table-layout="fixed" width="50%">
		                		<fo:table-column column-width="35pt"/>
		                		<fo:table-column column-width="75pt"/>
		                		<fo:table-column column-width="135pt"/>
		                		<fo:table-column column-width="70pt"/>
		                		<fo:table-column column-width="85pt"/>
		                		<fo:table-column column-width="85pt"/>
		                		<fo:table-column column-width="85pt"/>
		                		<fo:table-column column-width="100pt"/>
		                		<fo:table-column column-width="100pt"/>
		                		<fo:table-body> 
		                			<fo:table-row >
                 						<fo:table-cell >	
			                        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                        	</fo:table-cell>
	                     			</fo:table-row>
	                     			<fo:table-row >
                 						<fo:table-cell >	
			                        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                        	</fo:table-cell>
	                     			</fo:table-row>
	                     			<fo:table-row >
                 						<fo:table-cell >	
			                        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			                        	</fo:table-cell>
	                     			</fo:table-row>
		                			<fo:table-row >
                 						<fo:table-cell >	
			                        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" border-style="solid" white-space-collapse="false" font-size="10pt" font-weight="bold" >S.No.</fo:block>
			                        	</fo:table-cell>
                     					<fo:table-cell >	
			                        		<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-weight="bold" font-size="10pt">Insurance Number</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >	
			                        		<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-weight="bold" font-size="10pt">Name of Insured Person</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >	
			                        		<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-weight="bold" font-size="10pt">Present days</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >	
			                        		<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-weight="bold" font-size="10pt">Total amount of wages paid</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >	
			                        		<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-weight="bold" font-size="10pt">Employees' Contribution</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >	
			                        		<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-weight="bold" font-size="10pt">Average daily wages</fo:block>
			                        	</fo:table-cell>
			                        	<fo:table-cell >	
			                        		<fo:block text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-weight="bold" font-size="10pt">Name of the Dispensary</fo:block>
			                        	</fo:table-cell>
	                     			</fo:table-row>
	          						<#assign emplValues=EmplWiseDetailsMap.entrySet()>
	                            	<#list emplValues as emplDetails>
	                            		<#if emplDetails.getKey()!="totalWages">
	                     					<fo:table-row >
	                     						<fo:table-cell >	
					                        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt">${sNo}</fo:block>
					                        		<#assign sNo=sNo+1>
					                        	</fo:table-cell>
		                     					<fo:table-cell >	
					                        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt">&#160;</fo:block>
					                        	</fo:table-cell>
					                        	<#assign EmplNameList = EmplNameMap.entrySet()>
					                            <#list EmplNameList as EmplName>
					                            	<#if EmplName.getKey()==emplDetails.getKey()>
							                            <fo:table-cell >	
							                            	<fo:block keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt">&#160;${EmplName.getValue()}</fo:block>
							                            </fo:table-cell>
							                       	</#if>
						                     	</#list>
						                     	<fo:table-cell >	
					                            	<fo:block keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt">${emplDetails.getValue().get("totpayableDays")?if_exists?string("#0.00")}</fo:block>
					                            	<#assign pagetotPayableDays=pagetotPayableDays+emplDetails.getValue().get("totpayableDays")>
					                            	<#assign totPayableDays=totPayableDays+emplDetails.getValue().get("totpayableDays")>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt">${emplDetails.getValue().get("totWages")?if_exists?string("#0.00")}</fo:block>
					                            	<#assign pagetotWages=pagetotWages+emplDetails.getValue().get("totWages")>
							                       	<#assign totWages=totWages+emplDetails.getValue().get("totWages")>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt">${((-1)*emplDetails.getValue().get("employeeContributn"))?if_exists?string("#0.00")}</fo:block>
					                            	<#assign pagetotContribn=pagetotContribn+emplDetails.getValue().get("employeeContributn")>
							                       	<#assign totContribn=totContribn+emplDetails.getValue().get("employeeContributn")>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt"><#if emplDetails.getValue().get("totpayableDays")!=0>${((emplDetails.getValue().get("totWages"))/emplDetails.getValue().get("totpayableDays"))?if_exists?string("#0.00")}<#else>0.00</#if></fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                        		<fo:block keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" border-style="solid" font-size="10pt">&#160;</fo:block>
					                        	</fo:table-cell>
					                        	<#assign noOfLines=noOfLines+1>
			                     			</fo:table-row>
			                     			<#if (noOfLines>45) >
			                    				<#assign noOfLines=1>
			                    				<fo:table-row >
						                    		<fo:table-cell >	
						                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt">&#160;</fo:block>
						                            </fo:table-cell>
						                    		<fo:table-cell >	
						                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" >Page Total</fo:block>
						                            </fo:table-cell>
						                    		<fo:table-cell >	
						                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt">&#160;</fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell >	
						                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold">${pagetotPayableDays?if_exists?string("#0.00")}</fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell >	
						                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" >${pagetotWages?if_exists?string("#0.00")}</fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell >	
						                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" >${((-1)*pagetotContribn)?if_exists?string("#0.00")}</fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell >	
						                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" ><#if pagetotPayableDays!=0>${(pagetotWages/pagetotPayableDays)?if_exists?string("#0.00")}<#else>0.00</#if></fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell >	
						                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt">&#160;</fo:block>
						                            </fo:table-cell>
						                    	</fo:table-row>
						                    	<#assign pagetotPayableDays=0>
			                     				<#assign pagetotWages=0>
			                     				<#assign pagetotContribn=0>
		                    					<fo:table-row >
				                    				<fo:table-cell >	
						                        		<fo:block page-break-after="always"></fo:block>
						                        	</fo:table-cell>
				                     			</fo:table-row>
				                     			<fo:table-row >
			                 						<fo:table-cell >	
						                        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
						                        	</fo:table-cell>
				                     			</fo:table-row>
				                     			<fo:table-row >
			                 						<fo:table-cell >	
						                        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
						                        	</fo:table-cell>
				                     			</fo:table-row>
				                     			<fo:table-row >
			                 						<fo:table-cell >	
						                        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
						                        	</fo:table-cell>
				                     			</fo:table-row>
			                    			</#if>
	                     				</#if>
	                     			</#list>
	                     			<fo:table-row >
			                    		<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt">&#160;</fo:block>
			                            </fo:table-cell>
			                    		<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" >Grand Total</fo:block>
			                            </fo:table-cell>
			                    		<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold">${totPayableDays?if_exists?string("#0.00")}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" >${totWages?if_exists?string("#0.00")}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" >${((-1)*totContribn)?if_exists?string("#0.00")}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="10pt" font-weight="bold" ><#if totPayableDays!=0>${(totWages/totPayableDays)?if_exists?string("#0.00")}<#else>0.00</#if></fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="10pt">&#160;</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
	          					</fo:table-body>
	                     	</fo:table>
	                     	<fo:table>
	                     		<fo:table-column column-width="650pt"/>
	                     		<fo:table-body> 
	                     			<fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                             <fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                             <fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                             <fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                     		<fo:table-row >
			                            <fo:table-cell >	
			                            	<fo:block keep-together="always" text-align="right" font-size="10pt">Signature:_________________</fo:block>
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
			            	${uiLabelMap.NoDetailsFound}.
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
	  		</#if>
	  	</#if>
     </fo:root>
</#escape>