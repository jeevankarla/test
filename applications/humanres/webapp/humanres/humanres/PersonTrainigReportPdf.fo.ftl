 <#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
                     margin-left="0.3in" margin-right="0.1in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="0.9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
			<#if finalMap?has_content>
				<#assign personTrainingsList=finalMap.entrySet()>
				<#assign sNo = 0>
			 	<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="10pt" font-family="Courier,monospace"  flow-name="xsl-region-before">  
        			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
        			<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>      
					<fo:block text-align="center" font-size="11pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;${reportHeader.description?if_exists}</fo:block>
        			<fo:block text-align="center" font-size="11pt" keep-together="always"  white-space-collapse="false" font-weight="bold" font-family="Helvetica">&#160;${reportSubHeader.description?if_exists}</fo:block>
        			<fo:block text-align="center" font-size="11pt" keep-together="always" white-space-collapse="false" font-weight="bold" font-family="Helvetica">Learning And Development</fo:block>
        			<fo:block>
        			<fo:table border-style="solid">
	                		<fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="80pt"/>
		                   <fo:table-column column-width="40pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="60pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="80pt"/>
	                		<fo:table-body>
								<fo:table-row >		
									<fo:table-cell border-style="solid" >
                        				<fo:block text-align="center" font-weight="bold" >S.No</fo:block>
                     				</fo:table-cell> 
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Topic</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Emp Code</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Name</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Designation</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Department</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Venue</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Start Date</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >End Date</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Duration</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Trg.Category</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Faculty Type</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Institution/Faculty Name</fo:block>
                     				</fo:table-cell>
                     				<fo:table-cell border-style="solid">
                        				<fo:block text-align="center" font-weight="bold" >Total Cost</fo:block>
                     				</fo:table-cell>
                     			</fo:table-row >
                     		</fo:table-body>
	                	</fo:table>
	                </fo:block>
        		</fo:static-content>	
        		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	                <fo:block font-family="Courier,monospace">
	                	<fo:table border-style="solid">
	                		<fo:table-column column-width="30pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="310pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="60pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="80pt"/>
	                		<fo:table-body>
                     			<#list personTrainingsList as personTrainingDetails>
                     				<#assign sNo = sNo+1>
									<fo:table-row>
										<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${sNo?if_exists}</fo:block>
                     					</fo:table-cell>
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getKey()?if_exists}</fo:block>
                     					</fo:table-cell>
                     				<fo:table-cell>
                     					<fo:table>
	                						<fo:table-column column-width="40pt"/>
		                    				<fo:table-column column-width="90pt"/>
		                    				<fo:table-column column-width="90pt"/>
		                    				<fo:table-column column-width="90pt"/>
		                    			<fo:table-body>
		                    			<#assign trainingList = personTrainingDetails.getValue().get("emplDetailsList")>
		                    			<#list trainingList as participantsDetails>
                     						<fo:table-row>	
			                     				<fo:table-cell border-style="solid">
			                        				<fo:block text-align="center" >${participantsDetails.get("partyId")?if_exists}</fo:block>
			                     				</fo:table-cell>	
			                     				<fo:table-cell border-style="solid">
			                        				<fo:block text-align="center" >${participantsDetails.get("partyName")?if_exists}</fo:block>
			                     				</fo:table-cell>	
			                     				<fo:table-cell border-style="solid">
			                        				<fo:block text-align="center" >${participantsDetails.get("designation")?if_exists}</fo:block>
			                     				</fo:table-cell>	
			                     				<fo:table-cell border-style="solid">
			                        				<fo:block text-align="center" >${participantsDetails.get("deptName")?if_exists}</fo:block>
			                     				</fo:table-cell>	
	                     					</fo:table-row>
	                     				</#list>
	                     			</fo:table-body>
	                				</fo:table>
	                     			</fo:table-cell>
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("trainingLocation")?if_exists}</fo:block>
                     					</fo:table-cell>	
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("fromDate")?if_exists}</fo:block>
                     					</fo:table-cell>	
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("thruDate")?if_exists}</fo:block>
                     					</fo:table-cell>	
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("duration")?if_exists}</fo:block>
                     					</fo:table-cell>	
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("trgCategory")?if_exists}</fo:block>
                     					</fo:table-cell>
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("facultyType")?if_exists}</fo:block>
                     					</fo:table-cell>	
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("nameOfInstitute")?if_exists}</fo:block>
                     					</fo:table-cell>	
                     					<fo:table-cell border-style="solid" padding-before="0.4cm">
                        					<fo:block text-align="center" >${personTrainingDetails.getValue().get("traingCost")?if_exists}</fo:block>
                     					</fo:table-cell>	
									</fo:table-row>	
								</#list>
	                		</fo:table-body>
	                	</fo:table>
          			</fo:block>
				</fo:flow>
			  </fo:page-sequence>
			<#else>
		  		<fo:page-sequence master-reference="main">
			    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			       		 <fo:block font-size="14pt">
			            	${uiLabelMap.NoEmployeeFound}.
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
			</#if>    
 
 	</fo:root>
</#escape>