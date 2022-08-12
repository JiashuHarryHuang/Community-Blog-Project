$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	// 获取标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	// 发送异步请求(POST)
	$.post(
		"/discussPost/add",
		{"title":title,"content":content},
		function(data) {
			data = $.parseJSON(data);
			console.log(data);
			// 在提示框中显示返回发布成功/失败消息
			if (data.code === 0) {
				$("#hintBody").text(data.data);
			} else {
				$("#hintBody").text(data.message);
			}

			// 显示提示框
			$("#hintModal").modal("show");
			// 2秒后,自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 刷新页面
				if(data.code === 0) {
					window.location.reload();
				}
			}, 2000);
		}
	);
}