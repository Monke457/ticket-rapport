// Track the amount of new items added to the list (in order to assign temporary ids to new items).
var count = 1;

/* Updates the data field with selected item data as a JSON Array string. */
function update_data() {
    var options = document.getElementsByName('item-option');
    var dataInput = document.getElementById('items-data');
    var data = [];

    var pos = 1;

    for (var i = 0; i < options.length; i++) {
        if (!options[i].checked) {
            continue;
        }
        data.push({
            id: options[i].id,
            description: options[i].value,
            ordinal: pos++
        });
    }
    dataInput.value = JSON.stringify(data);
}

/* Shows or hides the empty list alert. */
function update_alert() {
    var options = document.getElementsByName('item-option');
    var alert = document.getElementById('multiselect-alert');
    var showAlert = true;
    for (var i = 0; i < options.length; i++) {
        if (options[i].parentElement.style.display != "none") {
            showAlert = false;
            break;
        }
    }
    alert.style.display = showAlert ? "block" : "none";
}

/* Adds a new item to the beginning of the list. */
async function add_item() {
    var descriptionInput = document.getElementById('description-input');
    var description = descriptionInput.value;
    if (description == "") return false;

    await fetch(`getItemOption?description=${description}&id=${count++}`)
        .then(function (response) {
            return response.text();
        }).then(function (html) {
            document.getElementById('multiselect-items').insertAdjacentHTML("afterbegin", html);
        }).catch(function (err) {
            console.warn('Something went wrong.', err);
        });

    descriptionInput.value = "";
    update_data();
    filter();
    return false;
}

/* Filters the list items */
function filter(key) {
    var searchInput = document.getElementById('search');
    var search = searchInput.value.trim();
    var selectedOnly = document.getElementById('selected-only').checked;
    var options = document.getElementsByName('item-option');

    var show;
    for (var i = 0; i < options.length; i++) {
        show = options[i].value.toLowerCase().includes(search.toLowerCase()) && (!selectedOnly || options[i].checked);
        options[i].parentElement.style.display = show ? "flex" : "none"
    }
    update_alert();
    return key != 'Enter';
}

/* Removes all text from the search input. */
function clear_search() {
    document.getElementById('search').value = "";
    filter();
}

/* Moves an item up one position in the list. */
function move_up(el) {
    var val = set_val(el);
    if (val.index == 0) return;
    val.items.insertBefore(el, val.children[val.index-1]);
    update_data();
}

/* Moves an item down one position in the list. */
function move_down(el) {
    var val = set_val(el);
    if (val.index == val.children.length-1) return;
    val.children[val.index+1].after(el);
    update_data();
}

/* Sets the values needed to move a list item.
 * returns  items: the list item elements.
            children: the item elements that are currently being displayed (after filter).
            index: the current position of the item to move.
 */
function set_val(el) {
    var items = document.getElementById("multiselect-items");
    var children = [];
    for (var i = 0; i < items.children.length; i++) {
        if (items.children[i].style.display == "none") continue;
        children.push(items.children[i]);
    }
    var index = Array.prototype.indexOf.call(children, el);

    return {items: items, children: children, index: index};
}

update_data();
update_alert();