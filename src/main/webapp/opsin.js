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
   let chemicalNameUrlEncoded = encodeURIComponent($("#chemicalName").val());
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
       $("#stdinchikey").html("<a href=\"http://www.google.com/search?q=&quot;"+ response.stdinchikey +"&quot;\" target=\"_blank\">" + response.stdinchikey + "</a>");
       $("#smiles").text(response.smiles);
     },
     error: function(xhr, textStatus, errorThrown){
       let errResp = JSON.parse(xhr.responseText)
       $("#messagetype").text("Error:");
       $("#message").text(errResp.message);
     }
   } );

   let depictUrl = "opsin/" + chemicalNameUrlEncoded + ".png";
   $("#depiction").attr("src", depictUrl);
   $("#depictionLink").attr("href", depictUrl);
   return false;
 });

 if (window.location.hash) {
   let name = window.location.hash.substring(1);
   $("#chemicalName").val(decodeURIComponent(name));
   $("#chemicalNameForm").submit();
 }
});

function copyText(btn, elName){
  if (document.queryCommandSupported && document.queryCommandSupported("copy")) {
    let r = document.createRange();
    r.selectNode(document.getElementById(elName));
    window.getSelection().removeAllRanges();
    window.getSelection().addRange(r);
    try {
      document.execCommand("copy");
      btn.textContent = "Copied";
      setInterval(function(){btn.textContent = "Copy"}, 5000);
    }
    catch (ex) {
      alert("Copy failed");
    }
    window.getSelection().removeAllRanges();
  }
  else {
     alert("Copy not supported by browser");
  }
}
