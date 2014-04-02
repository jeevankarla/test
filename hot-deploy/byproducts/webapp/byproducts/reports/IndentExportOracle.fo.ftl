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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="5in"
                     margin-left=".2in" margin-right=".3in" margin-top=".4in" margin-bottom=".3in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent=".4in"/>
                <fo:region-after extent=".4in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IndentExport.SQL")}
       <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false" font-size="11pt" font-weight="bold" keep-together="always"></fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body">
              <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:block font-size="11pt" font-weight="bold" linefeed-treatment="preserve">&#xA;
                           ${sqlString}        
                </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>