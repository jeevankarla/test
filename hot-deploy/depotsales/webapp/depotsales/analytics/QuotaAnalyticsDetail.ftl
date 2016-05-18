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
                    { name: 'categoryId', type: 'string' },
                    { name: 'looms', type: 'string' },                                    
                    { name: 'categoryQuotaPerMonth', type: 'string' },
                    { name: 'openingQuotaBalance', type: 'string' }, 
                    { name: 'closingQuotaBalance', type: 'string' },                                       
                    { name: 'usedQuota', type: 'string' },
                              
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
                height: '350px',
                source: dataAdapter,
                sortable: true,
                pageable:true,
                pageSize: 10,
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
                    var gridTitle = "<div style='width: 99%; text-align: left;'><h3>Quota Analytics From  ${defaultEffectiveDate?if_exists}  To ${defaultEffectiveThruDate?if_exists}</h3></div>";
                    toolbar.append(gridTitle);
                },           
                columns: [
                  { text: 'PartyId',  width:'10%', align: 'center', dataField: 'partyId',cellsalign: 'center', cellclassname:'smallfont'},
                  { text: 'Category', width:'15%', align: 'center', dataField: 'categoryId',cellsalign: 'center', cellclassname:'smallfont'},
                   { text: 'No Looms', width:'15%', align: 'center', dataField: 'looms', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Quota P.Month', width:'15%', align: 'center', dataField: 'categoryQuotaPerMonth', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Opening Balance', width:'15%', align: 'center', dataField: 'openingQuotaBalance', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Closing Balance', width:'15%', align: 'center', dataField: 'closingQuotaBalance', cellsalign: 'right', cellclassname:'smallfont'},
                  { text: 'Used Quota', width:'15%', align: 'center', dataField: 'usedQuota', cellsalign: 'right', cellclassname:'smallfont'},
                ],
              
               
            });
        });
    </script>
	
       <div id="treeGrid" style='height: 800px'>
		</div>   		
 