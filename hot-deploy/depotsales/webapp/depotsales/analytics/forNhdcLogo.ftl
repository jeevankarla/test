

<style>
.button1 {
    background-color: grey;
    border: none;
    color: white;
    padding: 5px 5px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 10px;
    margin: 4px 2px;
    cursor: pointer;
}
input[type=button] {
	color: white;
    padding: .5x 7px;
    background:#008CBA;
    border: .8px solid green;
    border:0 none;
    cursor:pointer;
    -webkit-border-radius: 5px;
    border-radius: 5px; 
}
input[type=button]:hover {
    background-color: #3e8e41;
}
</style>

<script type="text/javascript">

$(document).ready(function(){

$("#counter").hide();

 var n = localStorage.getItem('on_load_counter');
    if (n === null) {
        n = 0;
    }
    n++;
    localStorage.setItem("on_load_counter", n);
    
    var divload = localStorage.getItem("divload");
    
    document.getElementById('counter').innerHTML = n;
    $( ".user" ).click(function() {
        localStorage.setItem("on_load_counter", 0);
});


if(n==1 || divload == "NOT"){
  $('div#orderSpinn1').html('<img src="/images/welcome nhdc3.jpg" height="500" width="800">');

   $('div#orderSpinn1').each(function() {
    if ($(this).find('img').length) {
        divload = "YES";
        divload = localStorage.setItem("divload" , divload);
    }else{
        location.reload();
        divload = "NOT";
        divload = localStorage.setItem("divload" , divload);
    }
});
  $("div#orderSpinn1").show().delay(3000).fadeOut();
  
  
}  
});

function blinker() {
	
	$('div.breadcrumbs-sep').html("SELECT CORRESPONDING ANALYTICS TO VIEW DATA");
	
	$('div.breadcrumbs-sep').css({"color":"blue"});
	
    $('div.breadcrumbs-sep').show();
    $('div.breadcrumbs-sep').fadeOut(800);
    $('div.breadcrumbs-sep').fadeIn(800);
} 
	
	$(document).ready(function(){
	
	setTimeout(function() { setInterval(blinker, 1000); }, 3000);
	
	});

</script>
 
  <div align='center' name ='displayMsg' id='orderSpinn1'/></div>
  
<div id="counter"></div>

