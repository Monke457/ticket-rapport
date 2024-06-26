/* clears the filter form and refreshes the data if changes have been made. */
function clear_form() {
    var inputs = document.getElementsByClassName("filter-input");
    var selects = document.getElementsByClassName("filter-select");

    var changed = false;

    for (var i = 0; i < inputs.length; i++) {
        if (inputs[i].value == "") {
            continue;
        }
        inputs[i].value = "";
        changed = true;
    }

    for (var i = 0; i < selects.length; i++) {
        if (selects[i].selectedIndex == 0) {
            continue;
        }
        selects[i].selectedIndex = 0;
        changed = true;
    }

    if (changed) {
       submit_filter();
    }
}

function submit_filter() {
     document.getElementById("filter-form").submit();
}

