  	<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/styles/jqx.base.css</@ofbizContentUrl>" type="text/css" />
    <script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxcore.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxdata.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxbuttons.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxscrollbar.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxdatatable.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxtreegrid.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/scripts/demos.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/globalization/globalize.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxtooltip.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxdatetimeinput.js</@ofbizContentUrl>"></script>
    <script type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jqwidgets/jqwidgets/jqxcalendar.js</@ofbizContentUrl>"></script>
<style type="text/css">
	#treeGrid 
		{
			padding: 3px 5px;			
			opacity: .7;
			filter: alpha(opacity=70);
		}
</style>
<style type="text/css">
	.smallfont 
		{
			font-size: 12px;
		}
</style>
<script type="text/javascript">
$(document).ready(function () {  
		 var indents=${StringUtil.wrapString(dataJSON)};
		   // prepare the data          
            var source =
            {
                dataType: "json",
                unboundmode: true,
                dataFields: [
                    { name: 'partyId', type: 'string' },                
                    { name: 'branch', type: 'string' },
                    { name: 'ReportsTo', type: 'string' },                                    
                    { name: 'ro', type: 'string' },
                    { name: 'avgTAT', type: 'string' },                    
                    { name: 'totalIndents', type: 'string' },
                    { name: 'created', type: 'string' },
                    { name: 'ordersPlaced', type: 'string' },
                    { name: 'dispatched', type: 'string' },
                    { name: 'docsReceived', type: 'string' },
                    { name: 'docsEndorsed', type: 'string' },
                    { name: 'accepted', type: 'string' },                    
                ],
                hierarchy:
                {
                    keyDataField: { name: 'partyId' },
                    parentDataField: { name: 'ReportsTo' } 
                },
                id: 'branch',
                localData: indents
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            // create Tree Grid
            $("#treeGrid").jqxTreeGrid(
            {
                width: '99%',
                height: '800px',
                source: dataAdapter,
                sortable: true,
                ready: function()
                {
                     var rows = $("#treeGrid").jqxTreeGrid('getRows');
	                   for (var i = 0; i < rows.length; i++) {
	                       var key = $("#treeGrid").jqxTreeGrid('getKey', rows[i]);
	                       $("#treeGrid").jqxTreeGrid('expandRow', key);
	                   }

                },    
                editable: true,
                showtoolbar: true,
                rendertoolbar: function (toolbar) {
                    var gridTitle = "<div style='width: 99%; text-align: left;'><h3>Indent Analytics From  ${defaultEffectiveDate?if_exists}  To ${defaultEffectiveThruDate?if_exists}</h3></div>";
                    toolbar.append(gridTitle);
                },           
                columns: [
                  { text: 'R.O.',  width:'10%', align: 'center', dataField: 'ro', cellclassname:'smallfont'},
                  { text: 'Branch', width:'10%', align: 'center', dataField: 'branch',cellsalign: 'center', cellclassname:'smallfont'},
                  { text: 'Avg TAT (days)', width:'10%', align: 'center', dataField: 'avgTAT', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Total Indents', width:'10%', align: 'center', dataField: 'totalIndents', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Created', width:'10%', align: 'center', dataField: 'created', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Orders Placed', width:'10%', align: 'center', dataField: 'ordersPlaced', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Dispatched', width:'10%', align: 'center',  dataField: 'dispatched', cellsalign: 'right', cellclassname:'smallfont' },
                  { text: 'Docs Received',  width:'10%', align: 'center', dataField: 'docsReceived', cellsalign: 'right', cellclassname:'smallfont' },
                  { text: 'Docs Endorsed',  width:'10%', align: 'center', dataField: 'docsEndorsed', cellsalign: 'right', cellclassname:'smallfont' },
                  { text: 'Accepted',  width:'10%', align: 'center', dataField: 'accepted', cellsalign: 'right', cellclassname:'smallfont' },                 
                ],
              
               
            });
        });
    </script>
	
       <div id="treeGrid" style='height: 800px'>
		</div>   		
 