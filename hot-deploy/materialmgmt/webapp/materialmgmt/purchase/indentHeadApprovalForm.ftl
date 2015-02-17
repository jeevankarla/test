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
  <#if (security.hasEntityPermission("MASSAPPROVAL", "_VIEW", session) )>
  <form name="submitBulkStatus" id="ListIndentSubmit"  method="post" action="makeMassApproval">
    <div align="right" >  
      <input type="checkbox" id="bulkCheckBox" name="submitBulkStatus" onchange="javascript:checkAllIndentApprovalStatus(this);"/>   
      <input id="submitButton" type="button"  onclick="javascript:getAllIndentApprovals();" value="Accept"/>
    </div>
    
  </form>
   <form name="submitBulkRejectStatus" id="ListIndentReject"  method="post" action="makeMassReject">
    <div align="right" >  
      <input type="checkbox" id="bulkCheckBox" name="submitBulkStatus" onchange="javascript:checkAllIndentApprovalStatus(this);"/>   
      <input id="submitButton" type="button"  onclick="javascript:getAllIndentRejects();" value="Reject"/>
    </div>
  </form>
</#if>
