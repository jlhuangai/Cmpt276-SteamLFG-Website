document.getElementById("profile").style.display = "none";
document.getElementById("logout").style.display = "none";
window.onload = test();
function test(){
    document.getElementById("userid").style.display = "none";
    userId = document.getElementById("userid").innerHTML;
    console.log(userId);
    if (userId != 0){
        document.getElementById("logout").style.display = "block";
        document.getElementById("profile").style.display = "block";
        document.getElementById("submit").style.display = "none";
        document.getElementById("login").style.display = "none";
    }
}
