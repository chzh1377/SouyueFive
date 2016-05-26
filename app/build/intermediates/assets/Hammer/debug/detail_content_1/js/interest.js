(function() {
        //是否显示圈主工具  "更多操作"
	    window.showMoreOpera = function(flag){
        	var html = "<span class='interest_more_oper'><a href='javascript:adminMoreOper();'>更多操作</a></span>";
        	if(parseInt(flag)){
        		$(".souyue-header").append(html)
        	}
        };
})();