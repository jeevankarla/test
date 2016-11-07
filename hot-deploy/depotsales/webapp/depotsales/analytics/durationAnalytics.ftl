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
    <meta name="description" content="Column Formatting - JavaScript Tree Grid Demo | jQWidgets"> 
<style type="text/css">
	#treeGrid 
		{
			padding: 3px 5px;			
			opacity: .7;
			filter: alpha(opacity=70);
		}
		.fontSizeChange {
			 // color:#C70039;
			  background:#5499C7;
              font-size: 11px;
              font-weight:bold;
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
                    { name: 'avgDuration', type: 'string' },                                       
                    { name: 'slaeAvgDuration', type: 'string' },
                    //{ name: 'inProcess', type: 'string' },
                    //{ name: 'completed', type: 'string' },                    
                ],
                hierarchy:
                {
                    keyDataField: { name: 'partyId' },
                    parentDataField: { name: 'ReportsTo' } 
                },
                id: 'partyId',
                localData: indents
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            
            var cellsRenderer = function (row, column, value, rowData)
            {
                if (rowData.records !== undefined)
                {
                    return '<span style="font-weight: bold; color:#0B5345">' + value + '</span>';
                } else
                {
                    return '<span style="color:#0E6655">' + value + '</span>';
                }
            };            
            
            // create Tree Grid
            $("#treeGrid").jqxTreeGrid(
            {
                width: '98%',
                height: '600px',
                source: dataAdapter,
                sortable: true,
                ready: function()
                {
                
                     var rows = $("#treeGrid").jqxTreeGrid('getRows');
	                   for (var i = 0; i < rows.length; i++) {
	                       var key = $("#treeGrid").jqxTreeGrid('getKey', rows[i]);
	                       $("#treeGrid").jqxTreeGrid('expandRow', key);
	                   }
	                   
	                  // $("#treeGrid").jqxTreeGrid('expandAll');

                },    
                editable: true,
                showtoolbar: true,
                rendertoolbar: function (toolbar) {
                    var gridTitle = "<div style='width: 99%; text-align: left;'><h3>Duration Analytics From  ${defaultEffectiveDate?if_exists}  To ${defaultEffectiveThruDate?if_exists}</h3></div>";
                    toolbar.append(gridTitle);
                },           
                columns: [
                  { text: 'R.O.',  width:'20%', align: 'center', dataField: 'ro', cellsRenderer: cellsRenderer,className:'fontSizeChange' },
                  { text: 'Branch', width:'15%', align: 'center', dataField: 'branch',cellsalign: 'left', cellsRenderer: cellsRenderer,className:'fontSizeChange' },
                  { text: 'Indent to PO Avg. Duration (Days)', width:'12%', align: 'center', dataField: 'avgDuration', cellsalign: 'right', cellsRenderer: cellsRenderer,className:'fontSizeChange',
	                   renderer: function (text, align, height) {
			   					 var checkBox = "<table><tr><td style='text-align:center'>Indent to PO</td></tr><tr><td style='text-align:center'>Avg. Duration(Days)</td></tr> </table>";
			    				return checkBox;
							}
                   },
                  { text: 'Shipment to Sale Invoice Avg. Duration(Days)', width:'13%', align: 'center', dataField: 'slaeAvgDuration', cellsalign: 'right', cellsRenderer: cellsRenderer,className:'fontSizeChange' ,
	                  renderer: function (text, align, height) {
		   					 var checkBox = "<table><tr><td style='text-align:center'>Shipment to Sale Invoice </td></tr><tr><td style='text-align:center'>Avg. Duration(Days)</td></tr> </table>";
		    				return checkBox;
						}
                  },
                  //{ text: 'Pending Qty (Kgs in Lakhs)', width:'12%', align: 'center', dataField: 'inProcess', cellsalign: 'right', cellsRenderer: cellsRenderer,className:'fontSizeChange' },
                 // { text: 'Shipped Qty', width:'15%', align: 'center',  dataField: 'completed', cellsalign: 'right', cellsRenderer: cellsRenderer ,className:'fontSizeChange' },
                ],
              
               
            });
        });
    </script>
	
       <div id="treeGrid" style='height: 1200px'>
		</div>   		
 