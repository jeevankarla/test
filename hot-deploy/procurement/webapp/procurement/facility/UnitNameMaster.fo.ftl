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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-top=".3in"  margin-left=".5in" margin-right=".5in">
                <fo:region-body margin-top="0.65in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "UnitNameMaster.txt")}
      <#if UnitMasterList?has_content> 
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<fo:block text-align="left" font-size="7pt" keep-together="always"  white-space-collapse="false">&#160;                                             STATEMENT SHOWING THE UNIT-WISE DETAILS </fo:block>
        	                <fo:block font-size="7pt" text-align="left" white-space-collapse="false" >--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			            	<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;UC     UNIT NAME        UNIT    SCHEME      MANAGED   CAPA   AM       PM      DIST    MILK-SENT      DATE OF    BANK     BR.      BANK          NAME OF A/C </fo:block>                 
			            	<fo:block text-align="left" font-size="7pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">&#160;                        TYPE     TYPE         BY      CITY  OP-COST OP-COST           UNIT NAME      STARTED    CODE    CODE     AC/NO           HOLDER </fo:block>
			            	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" >--------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
        		<fo:block>
        	  		<fo:table>
		    	        <fo:table-column column-width="25pt"/>
		                <fo:table-column column-width="80pt"/> 
		           	    <fo:table-column column-width="33pt"/>
		        		<fo:table-column column-width="35pt"/> 	
		        		<fo:table-column column-width="48pt"/>	
		        		<fo:table-column column-width="20pt"/>
		        		<fo:table-column column-width="40pt"/>
		        		<fo:table-column column-width="25pt"/>
		        		<fo:table-column column-width="55pt"/>
		        		<fo:table-column column-width="55pt"/>
		        		<fo:table-column column-width="50pt"/>	
		        		<fo:table-column column-width="20pt"/>	
		        		<fo:table-column column-width="35pt"/>
		        		<fo:table-column column-width="55pt"/>
		        		<fo:table-column column-width="35pt"/>
		        		<fo:table-column column-width="5pt"/>
		        		<fo:table-column column-width="50pt"/>
		        		<fo:table-body>
		        		<#list UnitMasterList as UnitMaster>
		        		<fo:table-row>
				            <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left">${UnitMaster.get("UCODE")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(UnitMaster.get("UNAME"))),17)}</fo:block>                             
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left">${UnitMaster.get("UNITCATEGORY")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left">${UnitMaster.get("SCHEMEDESC")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left" text-indent="20pt">${UnitMaster.get("MNGBYDESC")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${UnitMaster.get("CAP")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${UnitMaster.get("OPCOST")?if_exists?string("#0.00")}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${UnitMaster.get("EOPCOST")?if_exists?string("#0.00")}</fo:block>                               
		                    </fo:table-cell>
		                   <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left" text-indent="30pt">${UnitMaster.get("DIST")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(UnitMaster.get("MILKSENTUNIT"))),12)}</fo:block>                                
		                   </fo:table-cell>
		                   <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(UnitMaster.get("DOP"), "dd/MM/yyyy")}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${UnitMaster.get("GBCODE")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${UnitMaster.get("BCODE")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">${UnitMaster.get("BANO")?if_exists}</fo:block>                               
		                    </fo:table-cell>
		                     <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="right">&#160;</fo:block>                               
		                    </fo:table-cell>
		                    <fo:table-cell >	
		                    	<fo:block font-size="7pt" text-align="left" keep-together="always">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(UnitMaster.get("PNAME"))),16)}</fo:block>                               
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
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
	</#if>
  </fo:root>
</#escape>