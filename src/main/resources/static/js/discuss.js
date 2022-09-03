function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        "/discussPost/like",
        {"entityType":entityType,"entityId":entityId, "entityUserId": entityUserId, "postId": postId},
        function(data) {
            data = $.parseJSON(data);
            if(data.code === 0) {
                $(btn).children("i").text(data.data.likeCount);
                $(btn).children("b").text(data.data.likeStatus===1?'已赞':"赞");
            } else {
                alert(data.message);
            }
        }
    );
}