$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();

	//ajax请求
	$.post(
		"/message/send",
		{"toName":toName,"content":content},
		function(data) {
			data = $.parseJSON(data);
			console.log(data);
			if(data.code === 0) {
				$("#hintBody").text(data.data);
			} else {
				$("#hintBody").text(data.message);
			}

			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				if(data.code === 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}