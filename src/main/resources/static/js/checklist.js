/* Updates the checklist data field as a JSON Array string. */
function update_data() {
    var dataField = document.getElementById('checklist-data');
    var items = document.getElementsByClassName('checklist-item');
    var data = [];

    var pos = 1;
    for (var i = 0; i < items.length; i++) {
        data.push({
            id: items[i].children[1].value,
            description: items[i].children[2].value,
            ordinal: pos++,
            completed: items[i].children[1].checked,
            templateItemId: items[i].children[3].value
        });
    }
    dataField.value = JSON.stringify(data);
}

/* Shows or hides the empty list alert. */
function update_alert() {
    var alert = document.getElementById('checklist-alert');
    var items = document.getElementsByClassName('checklist-item');

    if (items.length == 0) {
        alert.style.display = "block";
    } else {
        alert.style.display = "none";
    }
}

/* Adds a new empty item to the checklist. */
async function add(withCheckbox) {
   await fetch(`getEmptyChecklistItem?withCheckbox=${withCheckbox}`)
   .then(function (response) {
       return response.text();
   }).then(function (html) {
       document.getElementById("checklist-items").insertAdjacentHTML("beforeend", html);
   }).catch(function (err) {
       console.warn('Something went wrong.', err);
   });

   update_alert();
}

/* Removes an item from the checklist. */
function remove(el) {
    el.parentElement.remove(el);
    update_data();
    update_alert();
}

/* Moves an item up one position in the checklist. */
function move_up(el) {
    var index = Array.prototype.indexOf.call(el.parentElement.children, el);
    if (index == 0) {
        return;
    }
    el.parentElement.insertBefore(el, el.parentElement.children[index-1]);
    update_data();
}

/* Moves an item down one position in the checklist. */
function move_down(el) {
    var index = Array.prototype.indexOf.call(el.parentElement.children, el);
    if (index == el.parentElement.children.length-1) {
        return;
    }
    el.parentElement.insertBefore(el, el.parentElement.children[index+2]);
    update_data();
}

/* Fetches and displays checklist items from a template.
 * Replaces the current checklist with the new data.
 */
async function generate_list() {
   var templateId = document.getElementById('templateSelect').value;
   if (templateId == "") return;

   await fetch(`generateChecklist?id=${templateId}`)
   .then(function (response) {
       return response.text();
   }).then(function (html) {
       document.getElementById("checklist").outerHTML = html;
   }).catch(function (err) {
       console.warn('Something went wrong.', err);
   });

   update_data();
   update_alert();
}

update_data();
update_alert();