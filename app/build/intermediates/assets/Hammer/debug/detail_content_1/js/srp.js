(function() {
	//处理来源字符串,如果字符串过长使用引号代替部分字符
	var $url = $('.source-url-javascript'),
        $url_tmp = $('.source-url-javascript').find('a').length? $url.find('a'):$url;
    if($url.length){
        var _lineHeight = $url.css('line-height').replace('px','');
        _lineHeight = _lineHeight ==='normal'?'18':_lineHeight;
        var _height = $url.height();
        if(_height<=_lineHeight){
            //没有超过一行
            return
        }
        var _url = $.trim($url_tmp.html()),
            _urlArry = _url.split('/').filter(function(item){return $.trim(item)}),
            _length = _urlArry.length,
            _finalFile = _urlArry[_length-1],
            _lastTypeArr = _finalFile.split('.');

        //判断_url是http(s):还是普通的目录
        if(/^http(s)?/.test(_url)){
            _urlArry[0] = _urlArry[0]+'//'+_urlArry[1];
        }else{
            _urlArry[0] = _url.indexOf('/')===0?'/'+_urlArry[0]:_urlArry[0];
        }
        //如果需要折叠目录
        if(_length>1){
            _url = _urlArry[0]+'/.../'+_finalFile;
            if($url_tmp.html(_url).height()<=_lineHeight){
                //一次优化已经可以了
                return;
            }
        }
        var _lastFiveWord = _lastTypeArr[0].match(/\S{5}$/);
        if(_lastFiveWord&&_lastFiveWord.length){
            //最后的名字太长了
            _finalFile = '...'+_lastFiveWord[0] + (_lastTypeArr[1]?'.'+_lastTypeArr[1]:'');
        }
        _url = '/'+_finalFile;
        if(_length>1){
            _url = _urlArry[0]+'/.../'+_finalFile;
        }
        $url_tmp.html(_url);
    }
})();