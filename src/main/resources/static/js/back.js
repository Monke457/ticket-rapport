/* Navigates back through browser history */
function redirect(ret) {
    var ref = document.referrer;

    // redirect to home if coming from outside the app (this should never happen)
    if (ref.indexOf("localhost") == -1 && ref.indexOf("127.0.0.") == -1) {
        window.location.replace("http://127.0.0.1:8080");
        return;
    }
    if (ret == "") ret = 1;
    window.history.go(-1 * ret);
}