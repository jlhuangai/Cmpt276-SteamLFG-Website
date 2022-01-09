window.onload = myFunction();
function myFunction() {
    var otherusername1 = document.getElementById("otherusername1");
    document.getElementById("nousers").style.display = "none";
    if(!otherusername1){
        document.getElementById("bigtable").style.display = "none";
        document.getElementById("nousers").style.display = "block";
    }
}
