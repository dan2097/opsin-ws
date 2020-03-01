$(document).ready(function() {
 $("#chemicalNameForm").submit(function(e) {
   document.getElementById("opsininstructions").style.display="none";
   document.getElementById("results").style.display="";
   $("#depiction").attr("src", "");
   $("#depictionLink").attr("href", "");
   $("#message").text("");
   $("#messagetype").text("");
   $("#cml").text("");
   $("#stdinchi").text("");
   $("#stdinchikey").text("");
   $("#smiles").text("");
   var chemicalNameUrlEncoded = encodeURIComponent($("#chemicalName").val());
   $.ajax({
     beforeSend: function(req) {
       req.setRequestHeader("Accept", "application/json");
     },
     dataType: "text",
     type: "GET",
     url: "opsin/" + chemicalNameUrlEncoded,
   cache: false,
     success: function(json){
       response = $.parseJSON(json);
       if (response.message.length > 0) {
				 $("#messagetype").text("Warning:");
         $("#message").text(response.message);
       }
       $("#cml").text(response.cml);
       $("#stdinchi").text(response.stdinchi);
       $("#stdinchikey").html("<a href=\"http://www.google.com/search?q=&quot;"+ response.stdinchikey +"&quot;\" target=\"_blank\">" + response.stdinchikey + "</a> (Click to search the internet for this structure)");
       $("#smiles").text(response.smiles);
     },
     error: function(XMLHttpRequest, textStatus, errorThrown){
       $("#messagetype").text("Error:");
       $("#message").text(extractResponseText(XMLHttpRequest.responseText));
     }
   } );

   var depictUrl = "opsin/" + chemicalNameUrlEncoded + ".png";
   $("#depiction").attr("src", depictUrl);
   $("#depictionLink").attr("href", depictUrl);
   return false;
 });

 if (window.location.hash) {
   var name = window.location.hash.substring(1);
   $("#chemicalName").val(decodeURIComponent(name));
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
