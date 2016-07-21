function spatial() {
  //alert($("#id_textarea").val());
  //console.log($("#id_textarea").val());
  $.ajax({
      url: "api/v0/geotag",
      type: "POST",
      contentType: "text/plain",
      data: $("#text").val()
    })
    .done(function(data) {
      console.log(data);
      $("#results").text(JSON.stringify(data, null, 2));

      // add results to map
      var bounds = [];
      $.each(data.resolvedLocations, (function(idx, val) {
        //console.log(val);
        bounds.push([val.geoname.latitude, val.geoname.longitude]);
        L.marker([val.geoname.latitude, val.geoname.longitude]).addTo(map)
          .bindPopup(val.geoname.name).openPopup();
      }));

      // zoom to bounds of markers
      // map.fitBounds(bounds);

    });
}


function temporal() {
  $.ajax({
      url: "api/v0/temporal",
      type: "POST",
      contentType: "text/plain",
      data: $("#text").val()
    })
    .done(function(data) {
      console.log(data);
      $("#results").text(data);

      // for (var line : data.split("\n")) {}

    });
}
