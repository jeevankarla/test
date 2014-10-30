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
<div class="screenlet">
 <div class="screenlet-title-bar">
      <h3>${uiLabelMap.AccountingMainPage}</h3>
    </div>
<div class="lefthalf">
<table cellspacing="10" cellpadding="15">

	<tr>
    	<td><h1 class="h1">${uiLabelMap.AccountingPaymentsMenu}</h1></td>
     	<td/>
	 	<td style="padding-left:50px;"/>
     	<td><h1 class="h1">${uiLabelMap.AccountingInvoicesMenu}</h1></td>
     	<td/>
  	</tr>

	<tr>
		<td>
			<ul>
				<li><a href="<@ofbizUrl>findPayments?noConditionFind=Y&amp;lookupFlag=Y</@ofbizUrl>">${uiLabelMap.AccountingShowAllPayments}</a></li>
			</ul>
		</td>
		<td>
			<ul>
			<#list paymentStatus as status>
				<li style="margin-bottom:8px;"><a href="<@ofbizUrl>findPayments?lookupFlag=Y&amp;statusId=${status.statusId}</@ofbizUrl>">${uiLabelMap.AccountingShowPayments} ${status.get("description",locale)}</a></li>
			</#list>
			</ul>
		</td>
		<td style="padding-left:50px;"/>
		<td>
			<ul>
				<li><a href="<@ofbizUrl>findInvoices?noConditionFind=Y&amp;lookupFlag=Y</@ofbizUrl>">${uiLabelMap.AccountingShowAllInvoices}</a></li>
			</ul>
		</td>
		<td>
			<ul>
				<#list invoiceStatus as status>
					<#if status.get("description",locale) == "Rejected">
						<#if status.statusId == "INVOICE_RECEIVED">
							<#assign lstatus = uiLabelMap.AccountingShowAPInvoices>
						<#else>				
							<#assign lstatus = uiLabelMap.AccountingShowARInvoices>
						</#if>
					<#else>
						<#assign lstatus = uiLabelMap.AccountingShowInvoices+" "+status.get("description",locale)>
					</#if>
					<li style="margin-bottom:8px;">	
						<a href="<@ofbizUrl>findInvoices?lookupFlag=Y&amp;statusId=${status.statusId}</@ofbizUrl>">	${lstatus} </a>
					</li>
				</#list>
			</ul>
		</td>
	</tr>

</table>
</div>
</div>

<div class="righthalf">
<#if (apInvoiceListSize?exists && (apInvoiceListSize >0))>
<!--[if lte IE 8]><script language="javascript" type="text/javascript" src="../excanvas.min.js"></script><![endif]-->

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/jquery-1.6.1.min.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.js</@ofbizContentUrl>"></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.axislabels.js"</@ofbizContentUrl>></script>

<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.pie.js"</@ofbizContentUrl>></script>
<script  language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/flot/jquery.flot.tooltip.js"</@ofbizContentUrl>></script>
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/css/smoothness/jquery-ui-1.8.5.custom.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/examples/examples.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<link rel="stylesheet" href="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.css</@ofbizContentUrl>" type="text/css" media="screen" charset="utf-8" />
<style type="text/css">
	.cell-title {
		font-weight: normal;
	}
	.cell-title-right {
		font-weight: normal;
		text-align: right;		
	}	
	.cell-effort-driven {
		text-align: center;
	}
</style>			
			
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/firebugx.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery-ui-1.8.5.custom.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/lib/jquery.event.drag-2.0.min.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.core.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.editors.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangedecorator.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellrangeselector.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/plugins/slick.cellselectionmodel.js</@ofbizContentUrl>"></script>		
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.grid.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.groupitemmetadataprovider.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/slick.dataview.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.pager.js</@ofbizContentUrl>"></script>
<script language="javascript" type="text/javascript" src="<@ofbizContentUrl>/images/jquery/plugins/slickgrid/controls/slick.columnpicker.js</@ofbizContentUrl>"></script>

<script type="application/javascript">   


	function setupGrid1() {
		var grid;
		var data = [
        <#if apInvoiceReportList?exists> 
	        <#list apInvoiceReportList as apReport>				
				{id:"${apReport.statusId}" , statusId:"${apReport.statusId}", totalAmount:"${apReport.totalAmount}"},
			</#list>
			{id:"Total" , statusId:"Total", totalAmount:"${totalApAmount}"}			
		</#if>		
		];
		var dataView;

		var columns = [
			{id:"status", name:"Invoice Status", field:"statusId", width:100, minWidth:70, cssClass:"cell-title", sortable:false},	
			{id:"totalAmount", name:"Total Amount (Rs)", field:"totalAmount", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		var options = {
			editable: false,		
			forceFitColumns: true,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: false,
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid1", dataView, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
		
		// wire up model events to drive the grid
		dataView.onRowCountChanged.subscribe(function(e,args) {
			grid.updateRowCount();
            grid.render();
		});
		dataView.onRowsChanged.subscribe(function(e,args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

        
		// initialize the model after all the events have been hooked up
		dataView.beginUpdate();
		dataView.setItems(data);
		dataView.endUpdate();

	}	
	
	
	function setupGrid2() {
		var grid;
		var data = [
        <#if arInvoiceReportList?exists> 
	        <#list arInvoiceReportList as arReport>	
	        	{id:"${arReport.statusId}" , statusId:"${arReport.statusId}",  totalAmount:"${arReport.totalAmount}" } ,
			</#list>
			{id:"Total" , statusId:"Total", totalAmount:"${totalArAmount}" }
		</#if>		
		];
		var dataView;
		
		var columns = [
			{id:"status", name:"InvoiceStatus", field:"statusId", width:100, minWidth:100, cssClass:"cell-title", sortable:false},
			{id:"total", name:"Total Amount (Rs)", field:"totalAmount", width:100, minWidth:70, cssClass:"cell-title-right", sortable:false}	
		];
		
		
		var options = {
			editable: false,		
			forceFitColumns: true,
			enableCellNavigation: true,
			asyncEditorLoading: false,			
			autoEdit: false,
            secondaryHeaderRowHeight: 25
		};
		
        var groupItemMetadataProvider = new Slick.Data.GroupItemMetadataProvider();
		dataView = new Slick.Data.DataView({
        	groupItemMetadataProvider: groupItemMetadataProvider
        });
		grid = new Slick.Grid("#myGrid2", dataView, columns, options);
        grid.setSelectionModel(new Slick.CellSelectionModel());
		var columnpicker = new Slick.Controls.ColumnPicker(columns, grid, options);
		
		// wire up model events to drive the grid
		dataView.onRowCountChanged.subscribe(function(e,args) {
			grid.updateRowCount();
            grid.render();
		});
		dataView.onRowsChanged.subscribe(function(e,args) {
			grid.invalidateRows(args.rows);
			grid.render();
		});

        
		// initialize the model after all the events have been hooked up
		dataView.beginUpdate();
		dataView.setItems(data);
		dataView.endUpdate();

	}

$(document).ready(function(){
  var data = [
<#list apInvoiceReportList as apReport>
    {label: "${StringUtil.wrapString(apReport.statusId?default(""))}", data: ${apReport.totalAmount?if_exists}}<#if apReport_has_next>,</#if>
</#list>    
  ];
  
	jQuery.plot($("#chart"), data, 
	{
		series: {
			pie: { 
				show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2/3,
                    formatter: function(label, series){
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1
                    
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},
		legend: {
			show: true,
			margin: [-220, 120]
		}
	});
	
		setupGrid1();
		
<#if arInvoiceReportList?exists>		
	var data2 = [
<#list arInvoiceReportList as arReport>
    {label: "${StringUtil.wrapString(arReport.statusId?default(""))}", data: ${arReport.totalAmount?if_exists}}<#if arReport_has_next>,</#if>
</#list>    
  ];            
	jQuery.plot($("#chart2"), data2, 
	{
		series: {
			pie: { 
				show: true,
                radius: 1,
                label: {
                    show: true,
                    radius: 2/3,
                    formatter: function(label, series){
                        return '<div style="font-size:9pt;text-align:center;padding:2px;color:white;">'+label+'<br/>'+Math.round(series.percent)+'%</div>';
                    },
                    threshold: 0.1,
                }				
			}
		},
		grid: {
				hoverable: true 
		},
		tooltip: true,
		tooltipOpts: {
			content: "%s %p.2%", // show percentages, rounding to 2 decimal places
			shifts: {
				x: 20,
				y: 0
			},
			defaultTheme: false
		},			
		legend: {
			show: true,
			noColumns: 1,
			margin: [-300, 5]
		}
	});
	
		setupGrid2();
	
</#if>
        jQuery(".grid-header .ui-icon")
            .addClass("ui-state-default ui-corner-all")
            .mouseover(function(e) {
                jQuery(e.target).addClass("ui-state-hover")
            })
            .mouseout(function(e) {
                jQuery(e.target).removeClass("ui-state-hover")
            });	 
                       
});
</script>
	<style type="text/css">
		div.graph
		{
			width: 300px;
			height: 300px;
		}
		label
		{
			display: block;
			margin-left: 400px;
			padding-left: 1em;
		}
		
	</style>


<div class="screenlet">
    <div class="screenlet-title-bar">
      <h3>Payable Invoice Status Amount Trend <#if organizationPartyId?exists> For Organization : ${organizationPartyId} </#if></h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
  
		<div id="myGrid1" style="width:300px;height:250px;margin-left:20px;"></div>   		
    </div>
</div>
</div>
<div class="righthalf">
<div class="screenlet">
    <div class="screenlet-title-bar">
       <h3>Recevable Invoice Status Amount Trend <#if organizationPartyId?exists> For Organization : ${organizationPartyId}</#if></h3>
    </div>
    <div class="screenlet-body">
   		<div id="chart2" class="graph" style="margin-left:20px;margin-top:10px;"></div>
   		<br><br>
  
		<div id="myGrid2" style="width:300px;height:250px;margin-left:20px;"></div>   		
    </div>
</div>

<#else>
  <h3>No orders found for!</h3>
</#if>
</div>

