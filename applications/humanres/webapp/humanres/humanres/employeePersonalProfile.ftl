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
    <fo:table table-layout="fixed" width="100%" space-before="0.2in" border-style="solid">  
    <fo:table-header height="14px" >
    
		<fo:table-row >
        <fo:table-cell/>
        <fo:table-cell/>
         <fo:table-cell/>
        <fo:table-cell/>
        <fo:table-cell/>
        <fo:table-cell text-align="right"  font-size="14pt">
          <fo:block font-weight="bold" height="16px" text-align="right" keep-together="always">EMPLOYEE PERSONAL PROFILE</fo:block>
        </fo:table-cell>             
        
      </fo:table-row>  
      <fo:table-row border-bottom-style="solid" border-bottom-width="thick" border-bottom-color="black">
        <fo:table-cell/>
        <fo:table-cell/>
        <fo:table-cell/>
        <fo:table-cell/>
      </fo:table-row>    
      <fo:table-row border-bottom-style="solid" border-bottom-width="thick" border-bottom-color="black">
        <fo:table-cell text-align="right" number-columns-spanned="2">
          <fo:block font-weight="bold" height="16px" font-size="12pt" text-align="left">Field</fo:block>
        </fo:table-cell>  
        <fo:table-cell/>
        <fo:table-cell/>
        <fo:table-cell/>            
        <fo:table-cell text-align="left" number-columns-spanned="2">
          <fo:block font-weight="bold" height="16px" font-size="12pt" text-align="left">${uiLabelMap.CommonDescription}</fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-header>
        <fo:table-body font-size="10pt" border-style="solid">       
       
        <#list employeeProfileList as employeeProfile>        	                   
                <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">EmployeeId</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.partyIdTo?has_content>${employeeProfile.partyIdTo}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">EmployeeName</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.firstName?has_content>${employeeProfile.firstName}</#if> <#if employeeProfile.middleName?has_content>${employeeProfile.middleName}</#if> <#if employeeProfile.lastName?has_content>${employeeProfile.lastName}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">gender</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.gender?has_content>${employeeProfile.gender}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">maritalStatus</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.maritalStatus?has_content>${employeeProfile.maritalStatus}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">birthDate</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.birthDate?has_content>${employeeProfile.birthDate}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">passportNumber</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.passportNumber?has_content>${employeeProfile.passportNumber}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">passportExpireDate</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.passportExpireDate?has_content>${employeeProfile.passportExpireDate}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">totalYearsWorkExperience</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.totalYearsWorkExperience?has_content>${employeeProfile.totalYearsWorkExperience}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">phoneNumber</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.phoneNumber?has_content>${employeeProfile.phoneNumber}</#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">emailAddress</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.emailAddress?has_content><#noescape>${employeeProfile.emailAddress}</#noescape></#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                 <fo:table-row height="14px" space-start=".15in">
                    <fo:table-cell number-columns-spanned="2">
                        <fo:block text-align="left">postalAddress</fo:block>
                    </fo:table-cell>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell/>
                    <fo:table-cell text-align="left" number-columns-spanned="2">
                        <fo:block><#if employeeProfile.postalAddress?has_content><#noescape>${employeeProfile.postalAddress}</#noescape></#if></fo:block>
                    </fo:table-cell>
                </fo:table-row>               
                   
        </#list>

        <#-- blank line -->
        <fo:table-row height="7px">
            <fo:table-cell number-columns-spanned="5"><fo:block><#-- blank line --></fo:block></fo:table-cell>
            <fo:table-cell number-columns-spanned="5"><fo:block><#-- blank line --></fo:block></fo:table-cell>            
        </fo:table-row>      
    </fo:table-body>
 </fo:table> 
</#escape>
