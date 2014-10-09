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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left=".3in"  margin-right=".3in" margin-top=".2in" margin-bottom=".3in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="0.5in"/>
                <fo:region-after extent="0.5in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       ${setRequestAttribute("OUTPUT_FILENAME", "shortagesExcel.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>       
       <#if mpfAcknoledgeList?has_content>
       <fo:page-sequence master-reference="main">
            <fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
                <fo:block font-size="6pt">VST_ASCII-015 VST_ASCII-027VST_ASCII-077</fo:block>
                <fo:block text-align="left" white-space-collapse="false" font-size="6pt" keep-together="always">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;STATEMENT SHOWING THE MIXED MILK DESPATCH TO MPF HYDERABAD FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">&#160;     MILK SHED NAME    :    ${facility.facilityName}																											Page No:<fo:page-number/> Of <fo:page-number-citation ref-id="theEnd"/></fo:block>
                <fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="6pt">   UNIT    NAME OF THE UNIT     								        AS PER MILK REQUISITION        																											  ACKNOWLEDGENENT					           													     	                 SHORTAGE </fo:block>
                <fo:block font-size="6pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;   -----------------------------------------------------------------&#160;&#160;&#160;&#160;-----------------------------------------------------------------------&#160;&#160;&#160;-----------------</fo:block>
                <fo:block keep-together="always" white-space-collapse="false" font-size="6pt" text-align="left">CODE          			        PROC-TYPE    				    	QTY-LTRS	  QTY-KGS    KG-FAT      KG-SNF   			  ACK-TYPE    	      QTY-LTRS	     QTY-KGS  	  KG-FAT      KG-SNF            KG-FAT     KG-SNF</fo:block>
                <fo:block font-size="6pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="6pt">
        		<#assign noOfUnits =0>                           
                   <#list mpfAcknoledgeList as valuesList>
                   	<#if noOfUnits==9>
						<fo:block font-size="5pt" page-break-after="always"/>
						<#assign noOfUnits = 0>
					</#if>
					<#assign sQtyLtrs = 0>
				    <#assign sQtyKgs = 0>
                    <fo:block font-size="6pt" keep-together="always">
                       <fo:table>
                            <fo:table-column column-width="25pt"/>
                            <fo:table-column column-width="65pt"/>
                            <fo:table-column column-width="250pt"/>                
                            <fo:table-column column-width="280pt"/>
                            <fo:table-column column-width="10pt"/>
                            <fo:table-column column-width="40pt"/>
                          <fo:table-body>
	                           <fo:table-row>
		                           <fo:table-cell>
		                           		<fo:block font-size="6pt">${valuesList.facilityCode}</fo:block>
		                           		<#assign noOfUnits = noOfUnits+1>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           			<fo:block font-size="6pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(valuesList.facilityName?if_exists)),13)}</fo:block>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           <fo:block>
			                           	<fo:table>
				                           <fo:table-column column-width="50pt"/>
				                           	<fo:table-column column-width="60pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-body>
					                           	<#assign input = valuesList.input>
					                           	<#assign inputKeys = input.keySet()>
					                           	<#list inputKeys as key>
					                           	<#assign inputValues = input.get(key)>
				                           		<#if inputValues.kgFat !=0 || inputValues.kgSnf !=0 ||inputValues.qtyKgs !=0>
						                           	
						                           	<fo:table-row>
						                           		<fo:table-cell>
						                           		 	<#if key.indexOf("IUT")!=-1>
						                           				<fo:block font-size="6pt" text-align="left" font-weight="bold">${key}</fo:block>
						                           			<#else>
						                           				<fo:block font-size="6pt" text-align="left">${key}</fo:block>
						                           			</#if>
						                           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<#if key== "PROCUREMENT">
						                           				<#assign qtyLtrs = 0>
						                           				<#assign sQtyLtrs = sQtyLtrs + inputValues.sQtyLtrs>
						                           				<#assign qtyLtrs = (inputValues.qtyLtrs) + (inputValues.sQtyLtrs) >
						                           				<fo:block font-size="6pt" text-align="right">${qtyLtrs?if_exists?string('#0.0')}</fo:block>
						                           			<#else>
						                           					<fo:block font-size="6pt" text-align="right">${inputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>	
						                           			</#if>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<#if key== "PROCUREMENT">
					            	               				<#assign qtyKgs =0>
					            	               				<#assign sQtyKgs = sQtyKgs + (inputValues.sQtyKgs) >
						                           				<#assign qtyKgs = (inputValues.qtyKgs) + (inputValues.sQtyKgs) >
						                           				<fo:block font-size="6pt" text-align="right">${qtyKgs?if_exists?string('#0.0')}</fo:block>
						                           			<#else>
						                           					<fo:block font-size="6pt" text-align="right">${inputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>	
						                           			</#if>
					            	               			
					                	           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right">${inputValues.kgFat?if_exists?string('#0.000')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right">${inputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
					                	           		</fo:table-cell>
					                    	       	</fo:table-row>
					                           	</#if>
					                           	</#list>
				                           	</fo:table-body>
			                           	</fo:table>
		                           	</fo:block>
		                           	</fo:table-cell>
		                           	<fo:table-cell>
		                           <fo:block>
			                           	<fo:table>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="65pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-body>
					                           	<#assign output = valuesList.output>
					                           	<#assign outputKeys = output.keySet()>
					                           	<#list outputKeys as key>
					                           	<#assign outputValues = output.get(key)>
					                           	<#if outputValues.kgFat !=0 || outputValues.kgSnf !=0 ||outputValues.qtyKgs !=0>
						                           	<fo:table-row>
						                           		<fo:table-cell>
						                           		 	<#if key=="CLOSING_BALANCE">
						                           		 		<fo:block font-size="6pt" text-align="left" font-weight="bold">${key.replace("_"," ")}</fo:block>
						                           		 	<#else>
						                           				<fo:block font-size="6pt" text-align="left">${key.replace("_"," ")}</fo:block>
						                           			</#if>
						                           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right">${outputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right">${outputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>
					                	           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right">${outputValues.kgFat?if_exists?string('#0.000')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right">${outputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
					                	           		</fo:table-cell>
					                    	       	</fo:table-row>
					                         	</#if>
					                           	</#list>
				                           	</fo:table-body>
			                           	</fo:table>
		                           	</fo:block>
		                           	</fo:table-cell>
	                           	</fo:table-row>
	                           	<fo:table-row>
		                           <fo:table-cell>
		                           		<fo:block font-size="6pt">&#160;</fo:block>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           			<fo:block font-size="6pt">&#160;</fo:block>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           <fo:block>
			                           	<fo:table>
				                           <fo:table-column column-width="50pt"/>
				                           	<fo:table-column column-width="60pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-body>
					                           	<#assign input = valuesList.totInput>
					                           	<#assign inputKeys = input.keySet()>
					                           	<#list inputKeys as key>
					                           	<#assign inputValues = input.get(key)>
				                           		<#if (inputValues.kgFat !=0) || (inputValues.kgSnf !=0) ||( inputValues.qtyKgs !=0)>
						                           	<fo:table-row>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="left" font-weight="bold">${key}</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${((inputValues.qtyLtrs)+sQtyLtrs)?if_exists?string('#0.0')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${((inputValues.qtyKgs)+sQtyKgs)?if_exists?string('#0.0')}</fo:block>
					                	           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.kgFat?if_exists?string('#0.000')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
					                	           		</fo:table-cell>
					                    	       	</fo:table-row>
					                           	</#if>
					                           	</#list>
				                           	</fo:table-body>
			                           	</fo:table>
		                           	</fo:block>
		                           	</fo:table-cell>
		                           	<fo:table-cell>
		                           <fo:block>
			                           	<fo:table>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="65pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-body>
					                           	<#assign output = valuesList.totOutput>
					                           	<#assign outputKeys = output.keySet()>
					                           	<#list outputKeys as key>
					                           	<#assign outputValues = output.get(key)>
					                           	<#if (outputValues.kgFat !=0) ||(outputValues.kgSnf !=0) || (outputValues.qtyKgs !=0)>
						                           	<fo:table-row>
						                           		<fo:table-cell>
						                           				<fo:block font-size="6pt" text-align="left" font-weight="bold">${key}</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>
					                	           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.kgFat?if_exists?string('#0.000')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
					                	           		</fo:table-cell>
					                    	       	</fo:table-row>
					                         	</#if>
					                           	</#list>
				                           	</fo:table-body>
			                           	</fo:table>
		                           	</fo:block>
		                           	</fo:table-cell>
		                           	<#assign shortage = valuesList.totShrt>
		                           	<fo:table-cell>
		                           		<fo:block font-size="6pt" text-align="right">${shortage.kgFat?if_exists?string('#0.000')}</fo:block>
		                           	</fo:table-cell>
		                            <fo:table-cell>
		                           		<fo:block font-size="6pt" text-align="right">${shortage.kgSnf?if_exists?string('#0.000')}</fo:block>
		                           </fo:table-cell>
	                           	</fo:table-row>
	                           	
	                           	<fo:table-row>
	                           		<fo:table-cell>
	                           			<fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
	                           	</fo:table-body>
	                           	</fo:table>
	                         </fo:block>  	
                           </#list>
                           <fo:block linefeed-treatment="preserve" font-size="10pt">&#xA;</fo:block>
                           <fo:block linefeed-treatment="preserve" font-size="10pt">&#xA;</fo:block>
                          <#if grandTotalsMap?has_content>
                          <fo:block font-size="5pt" page-break-before="always"/>
                          <fo:block font-size="15pt" font-weight="bold" text-align="left"> ABSTRACT</fo:block>
                           <fo:block font-size="7pt" keep-together="always">
                            <fo:table>
                                <fo:table-column column-width="25pt"/>
	                            <fo:table-column column-width="65pt"/>
	                            <fo:table-column column-width="250pt"/>                
	                            <fo:table-column column-width="280pt"/>
	                            <fo:table-column column-width="10pt"/>
	                            <fo:table-column column-width="40pt"/>
                          	<fo:table-body>
                           
                           <fo:table-row>
		                           <fo:table-cell>
		                           		<fo:block font-size="6pt">&#160;</fo:block>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           			<fo:block font-size="6pt">&#160;</fo:block>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           <fo:block>
			                           	<fo:table>
				                           <fo:table-column column-width="50pt"/>
				                           	<fo:table-column column-width="60pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-body>
					                           	<#assign input = grandTotalsMap.input>
					                           	<#assign inputKeys = input.keySet()>
					                           	<#list inputKeys as key>
						                           	<#assign inputValues = input.get(key)>
					                           		<#if inputValues.kgFat !=0 ||inputValues.kgSnf !=0 >
							                           <#if (key =="OB") || (key.indexOf("IUT")!=-1)>
							                           	<fo:table-row>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="left" font-weight="bold">${key}</fo:block>
							                           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>
						                	           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.kgFat?if_exists?string('#0.000')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
						                	           		</fo:table-cell>
						                    	       	</fo:table-row>
						                    	       <#else>
						                    	       	<fo:table-row>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="left">${key}</fo:block>
							                           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right">${inputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right">${inputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>
						                	           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right">${inputValues.kgFat?if_exists?string('#0.000')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right">${inputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
						                	           		</fo:table-cell>
						                    	       	</fo:table-row>
						                    	      </#if> 	
						                           	</#if>
					                           	</#list>
				                           	</fo:table-body>
			                           	</fo:table>
		                           	</fo:block>
		                           	</fo:table-cell>
		                           	<fo:table-cell>
		                           <fo:block>
			                           	<fo:table>
				                           	<fo:table-column column-width="50pt"/>
				                           	<fo:table-column column-width="60pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-column column-width="40pt"/>
				                           	<fo:table-body>
					                           	<#assign output = grandTotalsMap.output>
					                           	<#assign outputKeys = output.keySet()>
					                           	<#list outputKeys as key>
						                           <#if key!="CLOSING_BALANCE">
						                           	<#assign outputValues = output.get(key)>
						                           	<#if outputValues.kgFat !=0 ||outputValues.kgSnf !=0 >
							                           	<fo:table-row>
							                           		<fo:table-cell>
																	<#if key == "LOCAL_SALES">
																		<fo:block font-size="6pt" text-align="left">${key.replace("_"," ")}(MM)</fo:block>
																	<#else>
																		<fo:block font-size="6pt" text-align="left">${key.replace("_"," ")}</fo:block>
							                           				</#if>						                           				
							                           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right">${outputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right">${outputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>
						                	           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right">${outputValues.kgFat?if_exists?string('#0.000')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right">${outputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
						                	           		</fo:table-cell>
						                    	       	</fo:table-row>
						                         	</#if>
						                          </#if>	
					                           	</#list>
					                           	<#assign outputValuesClosing = output.get("CLOSING_BALANCE")>
					                           	   <#if outputValuesClosing?has_content>
						                           	<fo:table-row>
						                           		<fo:table-cell>
															<fo:block font-size="6pt" text-align="left" font-weight="bold">CLOSING BALANCE</fo:block>
						                           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValuesClosing.qtyLtrs?if_exists?string('#0.0')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValuesClosing.qtyKgs?if_exists?string('#0.0')}</fo:block>
					                	           		</fo:table-cell>
						                           		<fo:table-cell>
						                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValuesClosing.kgFat?if_exists?string('#0.000')}</fo:block>
					    	                       		</fo:table-cell>
					        	                   		<fo:table-cell>
					            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValuesClosing.kgSnf?if_exists?string('#0.000')}</fo:block>
					                	           		</fo:table-cell>
					                    	       	</fo:table-row>
					                    	       	</#if>
				                           	</fo:table-body>
			                           	</fo:table>
		                           	</fo:block>
		                           	</fo:table-cell>
		                           	<#assign shortages = grandTotalsMap.shortages>
		                           	<fo:table-cell>
		                           		<fo:block font-size="6pt" text-align="right" font-weight="bold">${shortages.shrtKgFat?if_exists?string('#0.000')}</fo:block>
		                           		<fo:block font-size="6pt" text-align="right" font-weight="bold">${shortages.excesKgFat?if_exists?string('#0.000')}</fo:block>
		                           	</fo:table-cell>
		                            <fo:table-cell>
		                           		<fo:block font-size="6pt" text-align="right" font-weight="bold">${shortages.shrtKgSnf?if_exists?string('#0.000')}</fo:block>
		                           		<fo:block font-size="6pt" text-align="right" font-weight="bold">${shortages.excesKgSnf?if_exists?string('#0.000')}</fo:block>
		                           </fo:table-cell>
	                           	</fo:table-row>
                          </fo:table-body>
                       </fo:table>
                    </fo:block>
                    </#if>
                       <fo:block linefeed-treatment="preserve" font-size="8pt">&#xA;</fo:block>
                       <fo:block font-size="6pt" keep-together="always">
                       <fo:table>
                            <fo:table-column column-width="25pt"/>
                            <fo:table-column column-width="65pt"/>
                            <fo:table-column column-width="250pt"/>                
                            <fo:table-column column-width="280pt"/>
                            <fo:table-column column-width="10pt"/>
                            <fo:table-column column-width="40pt"/>
                          <fo:table-body>                      	
	                           	<fo:table-row>
		                           <fo:table-cell>
		                           		<fo:block font-size="6pt">&#160;</fo:block>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           			<fo:block font-size="6pt">&#160;</fo:block>
		                           </fo:table-cell>
		                           <fo:table-cell>
		                           		<fo:block>
				                           	<fo:table>
					                           <fo:table-column column-width="50pt"/>
					                           	<fo:table-column column-width="60pt"/>
					                           	<fo:table-column column-width="40pt"/>
					                           	<fo:table-column column-width="40pt"/>
					                           	<fo:table-column column-width="40pt"/>
					                           	<fo:table-body>
						                           	<#assign input = grandTotalsMap.totInput>
						                           	<#assign inputKeys = input.keySet()>
						                           	<#list inputKeys as key>
						                           	<#assign inputValues = input.get(key)>
					                           		<#if inputValues.kgFat !=0 ||inputValues.kgSnf !=0 >
							                           	<fo:table-row>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="left" font-weight="bold">${key}</fo:block>
							                           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>
						                	           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.kgFat?if_exists?string('#0.000')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${inputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
						                	           		</fo:table-cell>
						                    	       	</fo:table-row>
						                           	</#if>
						                           	</#list>
					                           	</fo:table-body>
				                           	</fo:table>
		                           		</fo:block>
		                           	</fo:table-cell>
		                           	<fo:table-cell>
		                           		<fo:block>
			                           		<fo:table>
					                           	<fo:table-column column-width="50pt"/>
					                           	<fo:table-column column-width="60pt"/>
					                           	<fo:table-column column-width="40pt"/>
					                           	<fo:table-column column-width="40pt"/>
					                           	<fo:table-column column-width="40pt"/>
					                           	<fo:table-body>
						                           	<#assign output = grandTotalsMap.totOutput>
						                           	<#assign outputKeys = output.keySet()>
						                           	<#list outputKeys as key>
						                           	<#assign outputValues = output.get(key)>
						                           	<#if outputValues.kgFat !=0 ||outputValues.kgSnf !=0 >
							                           	<fo:table-row>
							                           		<fo:table-cell>
							                           				<fo:block font-size="6pt" text-align="left" font-weight="bold">${key}</fo:block>
							                           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.qtyLtrs?if_exists?string('#0.0')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.qtyKgs?if_exists?string('#0.0')}</fo:block>
						                	           		</fo:table-cell>
							                           		<fo:table-cell>
							                           			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.kgFat?if_exists?string('#0.000')}</fo:block>
						    	                       		</fo:table-cell>
						        	                   		<fo:table-cell>
						            	               			<fo:block font-size="6pt" text-align="right" font-weight="bold">${outputValues.kgSnf?if_exists?string('#0.000')}</fo:block>
						                	           		</fo:table-cell>
						                    	       	</fo:table-row>
						                         	</#if>
						                           	</#list>
					                           	</fo:table-body>
			                           		</fo:table>
		                           		</fo:block>
		                           	</fo:table-cell>
		                           	<#assign shortages = grandTotalsMap.shortages>
		                           	<#if shortages?has_content>
		                           	<#assign totKgFat = shortages.shrtKgFat + shortages.excesKgFat>
		                           	<#assign totKgSnf = shortages.shrtKgSnf + shortages.excesKgSnf>
		                           	<fo:table-cell>
		                           		<fo:block font-size="6pt" text-align="right" font-weight="bold">${totKgFat?if_exists?string('#0.000')}</fo:block>
		                           	</fo:table-cell>
		                            <fo:table-cell>
		                           		<fo:block font-size="6pt" text-align="right" text-indent="0.2" font-weight="bold">${totKgSnf?if_exists?string('#0.000')}</fo:block>
		                           </fo:table-cell>
		                           </#if>
	                           	</fo:table-row>
                           		<fo:table-row>
                           		<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
                           		<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
                           		<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
                           		
                           			<fo:table-cell text-align="right">
                           				<#assign place = (facility.facilityName).toUpperCase()>
                           				<fo:block linefeed-treatment="preserve" font-size="10pt">&#xA;</fo:block>
                           				<fo:block linefeed-treatment="preserve" font-size="10pt">&#xA;</fo:block>
                           				<fo:block linefeed-treatment="preserve" font-size="10pt">&#xA;</fo:block>
	                           			<fo:block font-size="10pt" font-weight="bold" >Deputy Director (DD),</fo:block>
	                           			<fo:block font-size="10pt" font-weight="bold" >${place},</fo:block>
	                           			<#if place.indexOf("MILK")!= -1>
	                           				<#assign place = place.substring(0, place.indexOf("MILK"))>
	                           			</#if>
	                           			<fo:block font-size="10pt" font-weight="bold" >${place}.</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell><fo:block>&#160;</fo:block></fo:table-cell>
                           		</fo:table-row>
                           		<fo:table-row>
	                           		<fo:table-cell>
	                           			<fo:block linefeed-treatment="preserve" font-size="10pt">&#xA;</fo:block>
	                           		</fo:table-cell>
	                           	</fo:table-row>
                           		</fo:table-body>
                           	</fo:table>
                          </fo:block>
                           <fo:block id="theEnd"/>		
            </fo:flow>
       </fo:page-sequence>
    <#else>
                <fo:page-sequence master-reference="main">
                    <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
                        <fo:block font-size="14pt">
                            ${uiLabelMap.NoOrdersFound}.
                        </fo:block>
                    </fo:flow>
                </fo:page-sequence>
     </#if>
     </#if>
   </fo:root> 
 </#escape>     