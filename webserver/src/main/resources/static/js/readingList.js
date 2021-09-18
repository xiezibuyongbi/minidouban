//# sourceURL=readingList.js

function removeBookFromList() {
    const selectedListName = $("#reading-lists").val();
    if (selectedListName === "") {
        alert("请先选择书单");
        return;
    }
    if (confirm("确认移除选中的书？")) {
        $("#list-name-delete-from").val(selectedListName)
        $.ajax({
            url: "/remove-book",
            header: {
                Authorization: getToken()
            },
            type: "POST",
            data: $("#remove-book-from-list").serialize()
        });
    }
}

function deleteReadingList() {
    const selectedListName = $("#reading-lists").val();
    if (selectedListName === "") {
        alert("请先选择书单");
        return false;
    }
    if (confirm("确认删除该书单？")) {
        $("#list-name-to-delete").val(selectedListName);
        return true;
    }
    return false;
}

function deleteAllList() {
    return confirm("确认删除所有书单？");
}

function createReadingList() {
    let listNameToCreate = prompt("请输入书单名：", "");
    if (listNameToCreate === "") {
        return false;
    }
    $("#list-name-to-create").val(listNameToCreate);
    return true;
}

function renameReadingList() {
    let oldListNameElement = $("#old-list-name");
    oldListNameElement.val($("#reading-lists").val());
    if (oldListNameElement.val() === "") {
        alert("请先选择要修改的书单")
        return false;
    }
    let desiredListName = prompt("请输入书单的新名称", "");
    if (desiredListName === "") {
        return false;
    }
    $("#desired-list-name").val(desiredListName);
    return true;
}

function getReadingList() {
    $.ajax({
        url: "/reading_list",
        type: "GET",
        data: $("#reading-lists").serialize(),
        header: {
            Authorization: getToken()
        },
        success: (booksInList, status, xhr) => {
            console.log(booksInList);
        }
    });
}