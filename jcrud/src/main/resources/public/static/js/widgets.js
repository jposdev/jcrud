$(function() {
	$(".date_picker").datepicker({"dateFormat": "yy-mm-dd"});

	$(".picker").click(function() {
		var picker = this;
		var list_uri = $(this).next().val();
		var dialog = $("<div>")
				.insertAfter("body")
				.load(list_uri, function() {
					$(this)
						.find("tr")
						.slice(1)
						.click(function() {
							var tr = this;
							$(dialog.picker).val($(tr).find("input").val())
							dialog.dialog("close");
						});
				})
				.dialog();
		dialog.picker = picker;
	});

});
