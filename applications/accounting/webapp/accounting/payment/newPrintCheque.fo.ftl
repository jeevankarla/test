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
<#if errorMessage?exists>
<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".8in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set> 
	<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${errorMessage}.
	       		 </fo:block>
	    	</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>
<#else>
 
<#if paymentMethodId?exists && (paymentMethodId == "PAYMENTMETHOD11" || paymentMethodId == "PAYMENTMETHOD6")>
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
                     margin-left=".2in" margin-right=".3in" margin-top=".2in" margin-bottom=".3in">
                <fo:region-body margin-top=".1in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chequeVoucher.pdf")}
        <#if paymentId?has_content>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block  keep-together="always" text-align="center" font-size = "13pt" font-family="Courier,monospace" white-space-collapse="false" font-weight= "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateStringBankFormat(paymentDate?if_exists, "ddMMyyyy")}</fo:block>
        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block font-size="12pt"> 
              <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:table>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-body> 
                    		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "13pt" font-weight = "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;A/C PAYEE</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${attrValue}</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                  <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paiseRupees", locale)>
			                  <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${StringUtil.wrapString(amountWords?default(""))}  Only</fo:block>
			                   </fo:table-cell>
			                   <fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${amountStr?if_exists}</fo:block>          
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</fo:root>
</#escape>
</#if>
<#if paymentMethodId?exists && paymentMethodId == "PAYMENTMETHOD8">
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
                     margin-left=".2in" margin-right=".17in" margin-top=".01in" margin-bottom=".3in">
                <fo:region-body margin-top=".2in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chequeVoucher.pdf")}
        <#if paymentId?has_content>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block linefeed-treatment="preserve">&#160;</fo:block>
        	<fo:block  keep-together="always" text-align="right" font-size = "12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight= "bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "dd MM yyyy")}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block font-size="12pt">                
                <fo:table>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-body> 
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;A/C PAYEE</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							
				       		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#160;</fo:block>
				       			</fo:table-cell>
				       		</fo:table-row>
				       		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#160;</fo:block>
				       			</fo:table-cell>
				       		</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${attrValue}</fo:block>     
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
			                  <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paiseRupees", locale)>
			                   <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${StringUtil.wrapString(amountWords?default(""))}  Only</fo:block>
			                   </fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
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
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${amountStr?if_exists}</fo:block>     
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</fo:root>
</#escape>
</#if>
<#if paymentMethodId?exists && paymentMethodId == "PAYMENTMETHOD4">
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
                     margin-left=".2in" margin-right=".10in" margin-top=".01in" margin-bottom=".3in">
                <fo:region-body margin-top=".08in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chequeVoucher.pdf")}
        <#if paymentId?exists>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block  keep-together="always" text-align="right" font-size = "12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight= "bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "dd MM yyyy")}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block font-size="12pt"> 
                <fo:table>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-body> 
                    		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "13pt" font-weight = "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;A/C PAYEE</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${attrValue}</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                  <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paiseRupees", locale)>
			                   <fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       		  </fo:table-cell>
			                  <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${StringUtil.wrapString(amountWords?default(""))}  Only</fo:block>
			                   </fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${amountStr?if_exists}</fo:block>          
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</fo:root>
</#escape>
</#if>
<#if paymentMethodId?exists && paymentMethodId == "PAYMENTMETHOD10">
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
                     margin-left=".2in" margin-right=".17in" margin-top=".2in" margin-bottom=".3in">
                <fo:region-body margin-top=".1in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chequeVoucher.pdf")}
        <#if paymentId?has_content>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block  keep-together="always" text-align="right" font-size = "13pt" font-family="Courier,monospace" white-space-collapse="false" font-weight= "bold">&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateStringBankFormat(paymentDate?if_exists, "ddMMyyyy")}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
              <fo:block font-size="12pt"> 
                <fo:table>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-body> 
                    		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "13pt" font-weight = "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;A/C PAYEE</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${attrValue}</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                  <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paiseRupees", locale)>
			                  <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${StringUtil.wrapString(amountWords?default(""))}  Only</fo:block>
			                   </fo:table-cell>
			                   <fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
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
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${amountStr?if_exists}</fo:block>          
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</fo:root>
</#escape>
</#if>
<#-- Indian Bank-->

<#if paymentMethodId?exists && paymentMethodId == "PAYMENTMETHOD3">
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
                     margin-left=".2in" margin-right=".3in" margin-top=".01in" margin-bottom=".3in">
                <fo:region-body margin-top=".1in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chequeVoucher.pdf")}
        <#if paymentId?has_content>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block  keep-together="always" text-align="center" font-size = "13pt" font-family="Courier,monospace" white-space-collapse="false" font-weight= "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateStringBankFormat(paymentDate?if_exists, "ddMMyyyy")}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block font-size="12pt"> 
              <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:table>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-body> 
                    		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "13pt" font-weight = "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;A/C PAYEE</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${attrValue}</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                  <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paiseRupees", locale)>
			                  <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${StringUtil.wrapString(amountWords?default(""))}  Only</fo:block>
			                   </fo:table-cell>
			                   <fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
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
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${amountStr?if_exists}</fo:block>          
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</fo:root>
</#escape>
</#if>
<#if paymentMethodId?exists && paymentMethodId == "PAYMENTMETHOD7">
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
                     margin-left=".2in" margin-right=".10in" margin-top=".01in" margin-bottom=".3in">
                <fo:region-body margin-top=".2in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chequeVoucher.pdf")}
        <#if paymentId?exists>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block  keep-together="always" text-align="right" font-size = "12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight= "bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "dd MM yyyy")}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block font-size="12pt"> 
                <fo:table>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="150pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-body> 
                    		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "13pt" font-weight = "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;A/C PAYEE</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${attrValue}</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
			                  <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paiseRupees", locale)>
			                 	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
			                  <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${StringUtil.wrapString(amountWords?default(""))}  Only</fo:block>
			                   </fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${amountStr?if_exists}</fo:block>          
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</fo:root>
</#escape>
</#if>


<#if paymentMethodId?exists && paymentMethodId == "PAYMENTMETHOD9">
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"
                     margin-left=".2in" margin-right=".10in" margin-top=".3in" margin-bottom=".3in">
                <fo:region-body margin-top=".08in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "chequeVoucher.pdf")}
        <#if paymentId?exists>  
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block  keep-together="always" text-align="right" font-size = "12pt" font-family="Courier,monospace" white-space-collapse="false" font-weight= "bold">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(paymentDate?if_exists, "dd MM yyyy")}</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block font-size="12pt"> 
                <fo:table>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-column column-width="75pt"/>
                    <fo:table-body> 
                    		<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "13pt" font-weight = "bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;A/C PAYEE</fo:block>     
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
				       			<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${attrValue}</fo:block>     
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
			                  <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(amount, "%indRupees-and-paiseRupees", locale)>
			                   <fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       		  </fo:table-cell>
			                  <fo:table-cell>
			                        	<fo:block keep-together="always" font-size="12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${StringUtil.wrapString(amountWords?default(""))}  Only</fo:block>
			                   </fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
				       			<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				       			</fo:table-cell>
								<fo:table-cell>
				            		<fo:block text-align="left" keep-together="always" font-size = "12pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${amountStr?if_exists}</fo:block>          
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
		            	${uiLabelMap.OrderNoOrderFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
		</#if>
		</fo:root>
</#escape>
</#if>
</#if>