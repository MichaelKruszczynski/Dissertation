$( document ).ready(function() {
	var tabs = $(".tab");
	var contents = $(".content-tab");
      
            tabs.first().addClass("on");
			contents.first().addClass("on");
                   
        $(".menu").on("click", "li", function () {
            showTab($(this));
        });

         function showTab(menuItem) {
			 tabs.removeClass("on");
            contents.removeClass("on");
			menuItem.addClass("on");
			var tabId = menuItem.attr("id");
			var content = $("#"+tabId+"-menu");
			content.addClass("on");
        }
	
	});