<link type="text/css" href="<@ofbizContentUrl>/images/jquery/ui/css/ui-lightness/jquery-ui-1.8.13.custom.css</@ofbizContentUrl>" rel="Stylesheet" />	

<div class="screenlet">
	 <div class="screenlet-title-bar">
	 <#-->
    <ul>
      <li class="h3"></li>    
    </ul>-->
    <br class="clear"/>
  </div>
    <div class="screenlet-body">
    
    <form  action="${action1?if_exists}" method="post"  name="payuForm">
      <input type="hidden" name="key" value="${merchant_key?if_exists}" />
      <input type="hidden" name="hash" value="${hash?if_exists}"/>
      <input type="hidden" name="txnid" value="${txnid?if_exists}" />
      <table>
        <tr>
          <td><b>Mandatory Parameters</b></td>
        </tr>  
        <tr>
          <td>Amount: </td>
          <td><input name="amount" value="${params.get("amount")?if_exists}" /></td>
          <td>First Name: </td>
          <td><input name="firstname" id="firstname" value="${params.get("firstname")?if_exists}" /></td>
        </tr>
        <tr>
          <td>Email: </td>
          <td><input name="email" id="email" value="${params.get("email")?if_exists}" /></td>
          <td>Phone: </td>
          <td><input name="phone" value="${params.get("phone")?if_exists}" /></td>
        </tr>
        <tr>
          <td>Product Info: </td>
          <td colspan="3"><input name="productinfo" value="${params.get("productinfo")?if_exists}" size="64" /></td>
        </tr>
        <tr>
          <td>Success URI: </td>
          <td colspan="3"><input name="surl" readonly value="${params.get("surl")?if_exists}" size="64" /></td>
        </tr>
        <tr>
          <td>Failure URI: </td>
          <td colspan="3"><input name="furl" readonly value="${params.get("furl")?if_exists}" size="64" /></td>
        </tr>
        <#--
        <tr>
          <td><b>Optional Parameters</b></td>
        </tr>
        <tr>
          <td>Last Name: </td>
          <td><input name="lastname" id="lastname" value="${params.get("lastename")?if_exists}" /></td>
          <td>Cancel URI: </td>
          <td><input name="curl" value="" /></td>
        </tr>
        <tr>
          <td>Address1: </td>
          <td><input name="address1" value="${params.get("address1")?if_exists}" /></td>
          <td>Address2: </td>
          <td><input name="address2" value="${params.get("address2")?if_exists}" /></td>
        </tr>
        <tr>
          <td>City: </td>
          <td><input name="city" value="${params.get("city")?if_exists}" /></td>
          <td>State: </td>
          <td><input name="state" value="${params.get("state")?if_exists}" /></td>
        </tr>
        <tr>
          <td>Country: </td>
          <td><input name="country" value="${params.get("country")?if_exists}" /></td>
          <td>Zipcode: </td>
          <td><input name="zipcode" value="${params.get("zipcode")?if_exists}" /></td>
        </tr>
        <tr>
          <td>UDF1: </td>
          <td><input name="udf1" value="${params.get("udf1")?if_exists}" /></td>
          <td>UDF2: </td>
          <td><input name="udf2" value="${params.get("udf2")?if_exists}" /></td>
        </tr>
        <tr>
          <td>UDF3: </td>
          <td><input name="udf3" value="${params.get("udf3")?if_exists}" /></td>
          <td>UDF4: </td>
          <td><input name="udf4" value="${params.get("udf4")?if_exists}" /></td>
        </tr>
        <tr>
          <td>UDF5: </td>
          <td><input name="udf5" value="${params.get("udf5")?if_exists}" /></td>
          <td>PG: </td>
          <td><input name="pg" value="${params.get("pg")?if_exists}" /></td>
        </tr> -->
        <tr>
          <#if  hash="" >
            <td colspan="4"><input type="submit" value="Submit" /></td>
          </#if>
        </tr>
      </table>
    </form>
<br/>
  </div>
</div>
<script type="text/javascript">

$(document).ready(function(){
		
			submitPayuForm();
	});
	
	
	
var hash='${hash?if_exists}';


function submitPayuForm() {
	
	if (hash == '')
		return;
      var payuForm = document.forms.payuForm;
      payuForm.submit();
    }
   
</script>