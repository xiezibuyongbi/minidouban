//# sourceURL=search.js

function addBookToList() {
    const selectedListName = $("#reading-lists").val();
    if (selectedListName === "" || typeof (selectedListName) === undefined) {
        alert("请选择书单！")
        return false;
    }
    $("#selected-list-name").val(selectedListName);
    $("#add-book-to-list").submit();
    return true;
}

function pageRequest(direction) {
    const keyword = getQueryVar("keyword");
    let pageNum = getQueryVar("pageNum");
    const pageForm = $("#page-info");
    if (pageNum === "") {
        if (direction === "0") {
            pageNum = 0;
        } else {
            pageNum = 1;
        }
    } else {
        pageNum = parseInt(pageNum);
        if (pageNum === 0 && direction === "0") {
            pageNum = 0;
        } else {
            pageNum = (direction === '0') ? (pageNum - 1) : (pageNum + 1);
        }
    }
    pageForm.children().eq(0).val(pageNum.toString());
    pageForm.children().eq(1).val(keyword);
    pageForm.submit();
}
