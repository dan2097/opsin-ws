$(document).ready(function() {
 $("#chemicalNameForm").submit(function(e) {
   document.getElementById("opsininstructions").style.display="none";
   document.getElementById("results").style.display="";
   $("#depiction").attr("src", "");
   $("#message").text("");
   $("#messagetype").text("");
   $("#cml").text("");
   $("#inchi").text("");
   $("#stdinchikey").text("");
   $("#smiles").text("");
   var chemicalName = $("#chemicalName").val();
   $.ajax({
     beforeSend: function(req) {
       req.setRequestHeader("Accept", "application/json");
     },
     dataType: "text",
     type: "GET",
     url: "opsin/" +encodeURIComponent(chemicalName),
   cache: false,
     success: function(json){
       response = $.parseJSON(json);
       if (response.message.length > 0) {
				 $("#messagetype").text("Warning:");
         $("#message").text(response.message);
       }
       $("#cml").text(response.cml);
       $("#inchi").text(response.inchi);
       $("#stdinchikey").html("<a href=\"http://www.google.com/search?q="+ response.stdinchikey +"\" target=\"_blank\">" + response.stdinchikey + "</a> (Click to search the internet for this structure)");
       $("#smiles").text(response.smiles);
     },
     error: function(XMLHttpRequest, textStatus, errorThrown){
       $("#messagetype").text("Error:");
       $("#message").text(extractResponseText(XMLHttpRequest.responseText));
     }
   } );

   $("#depiction").attr("src", "opsin/" +encodeURIComponent(chemicalName) +".png");
   return false;
 });

 if (window.location.hash) {
   var name = window.location.hash.substring(1);
   $("#chemicalName").val(name);
   $("#chemicalNameForm").submit();
 }
});

var re = new RegExp('<h3>(.*)</h3>');

function extractResponseText(responseHTML) {
  var matcher = re.exec(responseHTML);
  if (matcher !=null){
    return matcher[1];
  }
  else{
    return "Problem retrieving server error message! Is this server running?";
  }
}
