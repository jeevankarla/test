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
      <h3>Facility Locations <#if geoChart?has_content && geoChart.points?has_content>[Displaying ${geoChart.points.size()} points]</#if></h3>
    </div>
    <div class="screenlet-body">
<#if geoChart?has_content>
    <#if geoChart.dataSourceId?has_content>
      <#if geoChart.dataSourceId == "GEOPT_GOOGLE">
      
        <div id="<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>" style="border:1px solid #979797; background-color:#e5e3df; width:${geoChart.width}; height:${geoChart.height}; margin:2em auto;">
          <div style="padding:1em; color:gray;">${uiLabelMap.CommonLoading}</div>
        </div>
    <style type="text/css">
    .style1 {background-color:#ffffff;font-weight:bold;border:1px #006699 solid;}
    .style2 {background-color:yellow;}
    </style>        
        <#assign defaultUrl = "https." + request.getServerName()>
        <#assign defaultGogleMapKey = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", defaultUrl)>
        <script src="https://maps.google.com/maps?file=api&amp;v=2&amp;key=${defaultGogleMapKey}" type="text/javascript"></script>
		<script src="/images/elabel.js" type="text/javascript"></script>
        <script type="text/javascript"><!--
          if (GBrowserIsCompatible()) {

      // === The basis of the arrow icon information ===
      var arrowIcon = new GIcon();
      arrowIcon.iconSize = new GSize(24,24);
      arrowIcon.shadowSize = new GSize(1,1);
      arrowIcon.iconAnchor = new GPoint(12,12);
      arrowIcon.infoWindowAnchor = new GPoint(0,0);
      
      // === Function to create a marker arrow ===
      function createMarker(point,icon) {
        var marker = new GMarker(point,icon);
        map.addOverlay(marker)
      }
                
      // === Returns the bearing in degrees between two points. ===
      // North = 0, East = 90, South = 180, West = 270.
      var degreesPerRadian = 180.0 / Math.PI;
      function bearing( from, to ) {
        // See T. Vincenty, Survey Review, 23, No 176, p 88-93,1975.
        // Convert to radians.
        var lat1 = from.latRadians();
        var lon1 = from.lngRadians();
        var lat2 = to.latRadians();
        var lon2 = to.lngRadians();

        // Compute the angle.
        var angle = - Math.atan2( Math.sin( lon1 - lon2 ) * Math.cos( lat2 ), Math.cos( lat1 ) * Math.sin( lat2 ) - Math.sin( lat1 ) * Math.cos( lat2 ) * Math.cos( lon1 - lon2 ) );
        if ( angle < 0.0 )
	 angle  += Math.PI * 2.0;

        // And convert result to degrees.
        angle = angle * degreesPerRadian;
        angle = angle.toFixed(1);

        return angle;
      }
       
      // === A function to create the arrow head at the end of the polyline ===
      function arrowHead(points) {
        // == obtain the bearing between the last two points
        var p1=points[points.length-1];
        var p2=points[points.length-2];
        var dir = bearing(p2,p1);
        // == round it to a multiple of 3 and cast out 120s
        var dir = Math.round(dir/3) * 3;
        while (dir >= 120) {dir -= 120;}
        // == use the corresponding triangle marker 
        arrowIcon.image = "http://www.google.com/intl/en_ALL/mapfiles/dir_"+dir+".png";
        createMarker(p1, arrowIcon);
      }
      
      // === A function to put arrow heads at intermediate points
      function midArrows(points) {
        for (var i=1; i < points.length-1; i++) {  
          var p1=points[i-1];
          var p2=points[i+1];
          var dir = bearing(p1,p2);
          // == round it to a multiple of 3 and cast out 120s
          var dir = Math.round(dir/3) * 3;
          while (dir >= 120) {dir -= 120;}
          // == use the corresponding triangle marker 
          arrowIcon.image = "http://www.google.com/intl/en_ALL/mapfiles/dir_"+dir+".png";
          createMarker(points[i], arrowIcon);
        }
      }
      
      // === A function to put arrow heads in the middle of lines
      function midLineArrows(points, routeLabel) {
        for (var i=0; i<points.length-1; i++) {  
          var p1=points[i];
          var p2=points[i+1];
          var p3=new GLatLng((p1.lat()+p2.lat())/2,(p1.lng()+p2.lng())/2)
          var dir = bearing(p1,p2);
          // == round it to a multiple of 3 and cast out 120s
          var dir = Math.round(dir/3) * 3;
          while (dir >= 120) {dir -= 120;}
          // == use the corresponding triangle marker 
          arrowIcon.image = "http://www.google.com/intl/en_ALL/mapfiles/dir_"+dir+".png";
          createMarker(p3, arrowIcon);
          var label = new ELabel(p3, routeLabel, "style2");
          map.addOverlay(label);
        }
      }          
        
      function loadStats () {
		var marker = this;
    	marker.openInfoWindow('<div id="marker-info" style="width:200px; height:120px">Loading location stats...</div>');
        	var $contentDiv = $("#marker-info");
        	//alert (dataJSON.name);
        	var htmlData = marker.time + "<br><br>" + "<u>" + marker.noteName + "</u>" + "<br>" + marker.noteInfo + "<br>";
        	$contentDiv.html(htmlData);
        	
        	marker.openInfoWindow($contentDiv);
        	
      }   
          
            var map = new GMap2(document.getElementById("<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>"));
     		 map.addControl(new GMapTypeControl());
            <#if geoChart.center?has_content>
              map.setCenter(new GLatLng(${geoChart.center.lat?c}, ${geoChart.center.lon?c}), ${geoChart.center.zoom});
            <#else>
              <#if geoChart.points?has_content>
                var latlng = [
                <#list geoChart.points as point>
                  new GLatLng(${point.lat?c}, ${point.lon?c})<#if point_has_next>,</#if>
                </#list>
                ];
                var latlngbounds = new GLatLngBounds();
                for (var i = 0; i < latlng.length; i++) {
                  latlngbounds.extend(latlng[i]);
                }
                map.setCenter(latlngbounds.getCenter(), Math.min (15, map.getBoundsZoomLevel(latlngbounds)));//reduce bounds zoom level to see all markers
              <#else>
                map.setCenter(new GLatLng(0, 0), 1);
                map.setZoom(15); // 0=World, 19=max zoom in
              </#if>
            </#if>
            <#if geoChart.controlUI?has_content && geoChart.controlUI == "small">
              map.addControl(new GSmallMapControl());
            <#else>
              map.setUIToDefault();
            </#if>
            <#if geoChart.points?has_content>
                <#list geoChart.points as point>
                  var markerOptions = {title:"${StringUtil.wrapString(point.title)}"};
                  <#if point.iconLink?has_content>
                  	  var customIcon = new GIcon(G_DEFAULT_ICON);
        			  customIcon.image = "${StringUtil.wrapString(point.iconLink)}";
        			  customIcon.iconSize = new GSize(12,20);
					  markerOptions.icon = customIcon;
                  </#if>                   
                  var marker_${point_index} = new GMarker(new GLatLng(${point.lat?c}, ${point.lon?c}), markerOptions);
                  map.addOverlay(marker_${point_index});
                  var label = new ELabel(new GLatLng(${point.lat?c}, ${point.lon?c}), "${StringUtil.wrapString(point.shortName)}", "style1");
                  map.addOverlay(label);
				  marker_${point_index}.facilityId = '${point.facilityId}';
				  marker_${point_index}.noteName = '${point.noteName}';
				  marker_${point_index}.noteInfo = '${point.noteInfo}';
				  marker_${point_index}.time = '${point.title}';
				  
                  GEvent.addListener(marker_${point_index}, 'click', loadStats);
                </#list>
            </#if>
            <#if geoChart.lines?has_content >
            
                <#assign lineNum = 0/>
                <#list geoChart.lines as line> 
                	var colors = ["#ff00ff","#00FFFF","#ff0000"];    
                    var latlng = [
                	<#list line as point>
                  		new GLatLng(${point.lat?c}, ${point.lon?c})<#if point_has_next>,</#if>
                	</#list>
                	];       
					var polyline = new GPolyline(latlng, colors[${lineNum%3}], 3);
					map.addOverlay(polyline);
					var routeLabel = "";
					<#if geoChart.routeLabels?has_content>
						routeLabel = '${geoChart.routeLabels[lineNum]}';
					</#if>
					midLineArrows(latlng, routeLabel);
					<#assign lineNum = lineNum + 1/>
				</#list>
			</#if>
          }
       --></script>
      <#elseif  geoChart.dataSourceId == "GEOPT_YAHOO">
      <#elseif  geoChart.dataSourceId == "GEOPT_MICROSOFT">
      <#elseif  geoChart.dataSourceId == "GEOPT_MAPTP">
      <#elseif  geoChart.dataSourceId == "GEOPT_ADDRESS_GOOGLE">
        <div id="<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>" style="border:1px solid #979797; background-color:#e5e3df; width:${geoChart.width}px; height:${geoChart.height}px; margin:2em auto;">
          <div style="padding:1em; color:gray;">${uiLabelMap.CommonLoading}</div>
        </div>
        <#assign defaultUrl = "https." + request.getServerName()>
        <#assign defaultGogleMapKey = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", defaultUrl)>
        <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=${defaultGogleMapKey}" type="text/javascript"></script>
        <script type="text/javascript"><!--
          if (GBrowserIsCompatible()) {
            var geocoder = new GClientGeocoder();
            var map = new GMap2(document.getElementById("<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>"));
            geocoder.getLatLng("${pointAddress}", function(point) {
              if (!point) { showErrorAlert("${uiLabelMap.CommonErrorMessage2}","${uiLabelMap.CommonAddressNotFound}");}
              map.setUIToDefault();
              map.setCenter(point, 13);
              map.addOverlay(new GMarker(point));
              map.setZoom(15); // 0=World, 19=max zoom in
            });
          }
        --></script>
      <#elseif geoChart.dataSourceId == "GEOPT_OSM">
        <div id="<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>" style="border:1px solid #979797; background-color:#e5e3df; width:${geoChart.width}; height:${geoChart.height}; margin:2em auto;">
        </div>
        <script src="http://www.openlayers.org/api/OpenLayers.js"></script>
        <script>
          map = new OpenLayers.Map("<#if geoChart.id?has_content>${geoChart.id}<#else>map_canvas</#if>");
          map.addLayer(new OpenLayers.Layer.OSM());
          <#if geoChart.center?has_content>
            var zoom = ${geoChart.center.zoom};
            var center= new OpenLayers.LonLat(${geoChart.center.lon?c},${geoChart.center.lat?c})
              .transform(new OpenLayers.Projection("EPSG:4326"), // transform from WGS 1984
                         map.getProjectionObject() // to Spherical Mercator Projection
                         );
          </#if>
          var markers = new OpenLayers.Layer.Markers( "Markers" );
          map.addLayer(markers);
          <#if geoChart.points?has_content>
            <#list geoChart.points as point>
              markers.addMarker(new OpenLayers.Marker(new OpenLayers.LonLat(${point.lon?c} ,${point.lat?c}).transform(
                new OpenLayers.Projection("EPSG:4326"), map.getProjectionObject())));
            </#list>
          </#if>
          map.setCenter(center, zoom);
          map.setZoom(15); // 0=World, 19=max zoom in
        </script>
      </#if>
    </#if>
<#else>
  <h2>${uiLabelMap.CommonNoGeolocationAvailable}</h2>
</#if>

</div>
</div>