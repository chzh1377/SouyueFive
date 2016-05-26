$(
function(){
   //修改部分图片右边距为0的情况
        $(function(){
                    var p = document.createElement('p');
                    if($('.souyue-image').parent('p').length==0){
                        $('.souyue-image').wrap(p);
                    }
                });
}
);
