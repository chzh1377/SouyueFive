//视频播放 author:马勘
$(function(){
    var angin_load=false; //是否需要重新刷新
    var countDuration=0  //默认总媒体时长
    var curr = 0;      //默认选择第一个视频播放地址
    var playedTime=0;  //默认已经播放过的时间
    var clickWidth = 0; //累计width
    var currentBuffer = 0; //首次缓冲的位置
    var overPlay=false;    //是否停止播放或者播放完成
    var clickUpdate=false //用户点击时，判断当前点击秒数
    var allPlayed= false;
    var loadPic = true;     //解决黑屏出现加载图
    var errorPos = 0;      //错误页面时从零开始播放
    var completeloaded = false;
    var currTime = 0;
    var initialTime=new Date().getTime() //获取时间戳
    var cookieWifi = getCookie('wifi');
    var isWifi = (cookieWifi!=="0");
    var isPlay = false;
    var video = $('#myVideo');
    var bgurl = "../images/none3.png"
    bgurl = $('#myVideo').attr('data-poster') || bgurl || "";
    var videoPoster = "../images/mask.png";
    var _src = video.attr("data-src") || "";
    // 视频是否来自于腾讯
    var qqFlag = -1;
    if($(".source-url-javascript a").html() != null){
        qqFlag = $(".source-url-javascript a").html().indexOf("v.qq.com")>-1;
    }
    var _duration =  video.attr("data-duration") || "";
    var arr_scr = _src.split(";") || "";
    var arr_duration = _duration.split(";") || "";
    // 删除视频请求地址数组为空的元素
    deleteNullOfArr(arr_scr);
    //删除视频媒体时长为空的元素；
    deleteNullOfArr(arr_duration);
    for (var i = 0; i < arr_duration.length; i++) {
            countDuration +=parseInt(arr_duration[i]);  //计算总时长
    };
    var _div = $("<div class='playbtn'  title='Play/Pause video'></div>");
     //设置播放器的宽和高，并移除controls属性
    if (bgurl.indexOf("none3")>0) {
        backgroundSize="74px auto";
    }else{
        backgroundSize="100% 100%";
    }
    video.parent('.playimg').css({
        'background': 'url('+bgurl+') center center no-repeat #e6e6e6',
        'background-size': backgroundSize
    });
    // 如果是ios设备 则隐藏控制条
    if(browser.versions.ios){
        $('.iosDuration').show().text(timeFormat(countDuration))
    }
    video.removeAttr('controls');
    video.css({'width': '100%','height':'100%','float':'left'});
    video.parent()
            .append(_div);
    _div.show();
    _div.on('click', function(e) {
        e.preventDefault();
        e.stopPropagation();
        if(!isWifi){
            //环境在3g下、
            //用户是通过缓存还是通过流量
            var beginIsInternet=true;  //默认
             //判断点击时候时候连接网络
            if(browser.versions.ios){
                var online = navigator.onLine;
                //ios不在网络状态时
                if (!online) {
                   $('.iosDuration').hide();
                   _div.hide(); 
                   $(".playimg").css({'background': 'url("../images/none3.png") center center no-repeat #e6e6e6','background-size': '74px auto'})
                   $('.playtxt').css('display', 'block').show().text('请检查网络，点击重新加载');
                   $('.souyue_video_lod').show();
                    setTimeout(function(){
                        $('.souyue_video_lod').hide();
                    },1000);
                    return false;
                }else{
                     _html(_div);
                }
            }else{
                beginIsInternet = true;
                var xhr = new ( window.ActiveXObject || XMLHttpRequest )( "Microsoft.XMLHTTP" );
                var status;
                xhr.open( "HEAD", "//" + window.location.hostname + "/?rand=" + Math.floor((1 + Math.random()) * 0x10000), false );
                try {
                    beginIsInternet =true;
                    xhr.send();
                }catch (error) {
                    _div.hide();
                    $(".playimg").css({'background': 'url("../images/none3.png") center center no-repeat #e6e6e6','background-size': '74px auto'})
                    $('.playtxt').css('display', 'block').show().text('请检查网络，点击重新加载');
                    $('.souyue_video_lod').show();
                    setTimeout(function(){
                        $('.souyue_video_lod').hide();
                    },1000);
                    beginIsInternet =false;
                    return false;
                }
                if (beginIsInternet) {
                     _html(_div);
                };
            }
        }else{
            //当用户正常进入页面时
            _div.hide();
            _init();
            video.trigger('click');       
        }
    });

    function _html(_div){
         var souyue_mask = $("<div class='souyue_mask'/>"),
                        souyue_video_cen = $("<div class='souyue_video_cen'/>"),
                        txt = $("<p class='txt'/>"),
                        btn = $("<p class='btn'/>"),
                        a_1 = $("<a href='javascript:void(0)' class='a_1' title='stop' />"),
                        a_2 = $("<a href='javascript:void(0)' class='a_2' title='play' />");
        txt.text('您当前正在使用移动网络，继续播放将消耗流量。');
        a_1.text('停止播放')
        a_2.text('继续播放')
        btn.append(a_1);
        btn.append(a_2);
        souyue_video_cen.append(txt);
        souyue_video_cen.append(btn);
        $('body').append(souyue_mask);
        $('body').append(souyue_video_cen);
        a_1.click(function(event) {
            isPlay=false;
            souyue_mask.hide();
            souyue_video_cen.hide();
        });
        a_2.click(function(e) {
            e.stopPropagation();
            souyue_mask.hide();
            souyue_video_cen.hide();
            _div.hide();
            _init();
            video.trigger('click');
        });

    }

function _init(){
            //设置各时长的width
             // <span class="bufferBar"></span>
             // <span class="timeBar"></span>
             // 如果加载视频出现地址为null的情况下
            if(!_src){
                 $(".playimg").css({'background': 'url("../images/none_video.png") center center no-repeat #e6e6e6','background-size': '80% 80%'})
                 $(".playimg").off('click');
                 $('.iosDuration').hide();
                 video.off();
             }else{
                if($(".spanduration").length == 0){
                        for (var a = 0;a < arr_duration.length; a++) {
                             var _p = $("<p  class='spanduration spanduration"+a+"'/>");
                             var _span_1 = $("<span class='bufferBar bufferBar"+a+"'/>");
                             var _span_2 = $("<span class='timeBar timeBar"+a+"' />");
                              var _span_div = $("<span class='timeBarDiv timeBarDiv"+a+"' />");
                             var _width = 100*(parseInt(arr_duration[a])/countDuration);
                             _p.css({
                                 'width': _width-1.5+'%',
                                 'float' : 'left',
                                 'margin-bottom' : '0px',
                                 'position': 'relative'
                             });
                             _p.append(_span_1);
                             _p.append(_span_2);
                             _p.append(_span_div);
                             $('.progress').append(_p);
                         };
                    }
                    $('.playimg').css({'background': '#000'});
                    video.show();
                    video.attr('src', arr_scr[curr]);
                    video.attr('poster', bgurl);
                    $('.timeBarDiv').hide("slow");
                    $('.timeBarDiv'+curr).show("fast");
                    $('.souyue_video_lod').show();
                    // 当视频的元数据加载完后
            }
            
}

video.on('loadeddata', function(event) {
        if(curr>0){
            for (var i = curr-1; i >=0; i--) {
                $('.timeBar'+i).css('width','100%');
            };
            $('.timeBarDiv').hide("slow");
            $('.timeBarDiv'+curr).show("fast");
        }
        $('.duration').text(timeFormat(countDuration));  
        $('.souyue_video_lod').show();
});

video.on('loadedmetadata', function(event) {
    event.preventDefault();
    // 如果是ios设备 则隐藏控制条
     if(browser.versions.ios){
        $('.iosDuration').css('color', '#fff');
        $('.control').hide();
    }else{
        $('.control').show();
        video.show();
        $(".playimg").css('background','#000');
    }
    //获取开始时候的buffer数据
    setTimeout(startBuffer, 150);
    if(overPlay){
            video[0].pause();
            $('.souyue_video_lod').hide();
            $('.pauseBtn').show();
            $('.current').text(timeFormat(0)); 
            video.attr('poster', videoPoster);
            if(allPlayed){
                allPlayed = false;
                video.attr('poster', bgurl);
            }
    }else{
            video[0].play();
    }
});
//播放秒数
video.on('timeupdate', function() {
    var currentPos =0;
    if(clickUpdate){
        currentPos = currTime;
        video[0].currentTime = parseInt(currTime);
        // alert(video[0].currentTime);
    }else{
        currentPos = video[0].currentTime;
    }
    var maxduration = video[0].duration;
    var perc = 100 * currentPos / maxduration;
    var countTime = parseInt(playedTime)+currentPos;
    if (curr !=0) {
        currentPos =playedTime+currentPos
    };
    $('.timeBar'+curr).css('width',perc+'%');
    $('.timeBarDiv'+curr).css('left',perc+'%');
    $('.current').text(timeFormat(currentPos)); 
    clickUpdate =false;
    loadPic=false;
});

video.on('progress', function(event) {
    event.preventDefault();
    errorPos = video[0].currentTime;
    
});    
//监听视频播放状态
video.on('error', function(event) {
   event.preventDefault();
   $('.souyue_video_lod').hide();
   $('.control').hide()
   video.hide();
   $('.iosDuration').hide();
   $('.pauseBtn').hide();
   $(".playimg").css({'background': 'url("../images/none3.png") center center no-repeat #e6e6e6','background-size': '74px auto'})
   $('.playtxt').css('display', 'block').show().text('请检查网络，点击重新加载');
   angin_load=true;
});

video.on('canplay', function(event) {
    event.preventDefault();
});
video.on('canplaythrough', function() {
    completeloaded = true;
    $('.souyue_video_lod').hide();
    $('.playtxt').hide();
});

video.on('waiting', function(event) {
    event.preventDefault();
    $('.souyue_video_lod').show();
});
   //video ended event
video.on('ended', function() {
		console.log("ended");
        playedTime = parseInt(arr_duration[curr]);
        curr++;
        var nowTime = new Date().getTime();
        var difTime = (nowTime-initialTime)/1000/60;
        //如果当前时间大于10分钟  difTime >10
        if(difTime>30){
            arr_scr="";
            arr_duration ="";
            _ajax()
        }else{
            if (curr >= arr_scr.length){    //播放完成
                if (browser.versions.ios) {
                	console.log("reload");
                    window.location.reload(true);
                }else{
                    curr = 0;
                    $(".bufferBar").css('width', '0');  //视频源播放完毕，缓冲条清零
                    $(".timeBar").css('width', '0'); //视频源播放完毕，缓冲条清零
                    $('.timeBarDiv').css('left','0%');
                    $('.timeBarDiv').hide("fast");
                    $('.current').text(timeFormat(0));      //当前播放时间清零
                    overPlay=true;
                    allPlayed= true;
                }
                

            }else{
                    overPlay = false;
            }
            console.log("set att");
            video.attr('src', arr_scr[curr]);
            $('.timeBarDiv'+curr).css('left','0%');
            $('.timeBarDiv'+curr).show();
//            video.attr('poster', videoPoster);
//            $('.souyue_video_lod').show();
        }
    // console.log(video[0].currentSrc)
});


 //video seeking event
video.on('seeking', function() {
        if(!completeloaded) { 
           $('.souyue_video_lod').show();
        }   
});

//video seeking event
video.on('seeked', function() {
        if(!completeloaded) { 
           $('.souyue_video_lod').show();
        }   
});

video.on('click',  function(e) { playpause(e); });
     var playpause = function(e) {
        e.preventDefault();
        e.stopPropagation();
        if(video[0].paused ||  video[0].ended) {
           video[0].play();
        }else {
            video[0].pause();
            if (completeloaded) {
                $('.pauseBtn').show()
            };
        }
    };

$('.pauseBtn').on('click',function(e){
    e.preventDefault();
    e.stopPropagation();
    $('.pauseBtn').hide();
    video[0].play();
})
//视频缓冲条点击加载
$('.progress').on('mousedown', function(e) {
    if(qqFlag){
        e.stopPropagation();
           overPlay =false;
           video.removeAttr('poster');
           $('.playimg').css({'background': '#000'});
            //将clickWidth清零
           clickWidth=0;
           $('.souyue_video_lod').show()
           $('.pauseBtn').hide();
            var distance = e.pageX-$(this).offset().left
            // console.log(distance)
            var _width = 100*distance/($('.progress').width());
            if(_width >=100){
                _width = 100;
            }
            // 用户拖动进度条到n%的位置
            var percWidth = _width/100;
            //计算总时长
            // countDuration;
            //拖动到那个时间点（相对于长）；
            var userTime =  countDuration*percWidth;
            //变量存储时间和
            var sum = 0;
            //循环存每一个时长的数值，从0累加，算出这个点是在哪一段
            for (var i = 0; i < arr_duration.length; i++) {
                sum += parseInt(arr_duration[i]);
                curr = i;
                if(userTime <=sum)  break;
            };
            //将其他的圆球隐藏
            $('.timeBarDiv').hide("slow");
            $('.timeBarDiv'+curr).show("fast");
            //判断出用户点击的是第几个视频源
            // curr
            // 拖动结束先把playedTime = 0 不然会造成累加
            playedTime = 0; 
            currTime = 0;
            //防止用户点击到视频播放结束断点
            if (curr == 0) {
                 $('.timeBar'+curr).css('width',distance);
                 $('.timeBarDiv'+curr).css('left',distance);
                _width = distance/$('.spanduration'+curr).width();
                if (distance >= $('.spanduration0'+curr).width()-5) {
                        distance = distance-5;
                    };
            }else{
                for (var a = 0; a < curr; a++) {
                        $('.timeBar'+a).css('width','100%');
                        clickWidth +=$('.spanduration'+a).width();
                        playedTime += parseInt(arr_duration[a]);
                        if (clickWidth>=distance) break;
                    };
                    var distanceWidth = distance-clickWidth
                     if (distanceWidth >= $('.spanduration0'+curr).width()-5) {
                        distance = distance-5;
                    };
                    $('.timeBar'+curr).css('width',distance-clickWidth);
                    $('.timeBarDiv'+curr).css('left',distance-clickWidth);
                    _width = (distance-clickWidth)/$('.spanduration'+curr).width();
            }
            // i 大于curr的视频源的timeBarwidth置为0
            for (var i = 0; i < arr_scr.length; i++) {
                if (i >curr) {
                    $('.timeBar'+i).css('width','0');
                    $('.bufferBar'+i).css('width','0');
                }
            };
            clickUpdate=true;
            loadPic = true;
            video.attr('src', arr_scr[curr]);
            video.attr('poster',videoPoster);
            var maxduration = parseInt(arr_duration[curr]);
            currTime=maxduration*_width
            // alert(video[0].currentTime);
    }
});



/*****************************************************实现左右拖动加载视频********************************************************/
// $('.progress')[0].addEventListener("touchmove",function(e){
//     e.stopPropagation();
//     var ele=e.target;
//     video[0].pause();
//     $('.countBar').show();
//     $('.timeBar').hide();
//     var distance = e.changedTouches[0].pageX;
//     var _width = 100*distance/($('.progress').width());
//     if(_width >=100){
//         _width = 100;
//     }
//     //用户拖动进度条到n%的位置
//     var percWidth = _width/100;
//     //计算总时长
//     // countDuration;
//     //拖动到那个时间点（相对于长）；
//     var userTime =  countDuration*percWidth;
//     //变量存储时间和
//     var sum = 0;
//     //循环存每一个时长的数值，从0累加，算出这个点是在哪一段
//     for (var i = 0; i < arr_duration.length; i++) {
//         sum += parseInt(arr_duration[i]);
//         curr = i;
//         if(userTime <=sum)  break;
//     };
//     $('.countBar').css('width',_width+'%');
//     $('.timeBar').css('width', '0');
//     $('.bufferBar').css('width', '0');
//     // updatebar(distance,ele);

// },false)


// $('.progress')[0].addEventListener("touchend",function(e){
//     e.stopPropagation();
//     var ele=e.target;
//     //先把timeBar清零
//     $('.countBar').hide();
//     $('.timeBar').show();
//     // 拖动结束先把playedTime = 0 不然会造成累加
//     playedTime = 0; 
//     video.attr('src', arr_scr[curr]);
//     //拖动结束，当curr>0将当前curr之前的timeBar的width设置成100%，
//     var distance = e.changedTouches[0].pageX;
//     var _width = parseInt(arr_duration[curr])/countDuration*distance;
//     $('.timeBar'+curr).css('width',_width+'%');
//     $('.bufferBar'+curr).css('width',_width+'%');
//     touchWidth = _width;
//     var maxduration = parseInt(arr_duration[curr]);
//     video[0].currentTime =maxduration*_width/100;
//     video[0].play();
// },false)
// 



/*****************************************************实现左右拖动加载视频********************************************************/

//display video buffering bar
function startBuffer() {
    currentBuffer = video[0].buffered.end(0);
    var maxduration = video[0].duration;
    var perc = 100 * currentBuffer / maxduration;
    // perc = touchWidth?touchWidth+perc:perc;
    $('.bufferBar'+curr).css('width',perc+'%');
    if(currentBuffer < maxduration) {
        setTimeout(startBuffer, 500);
    }
};  



//Time format converter - 00:00
function timeFormat(seconds){
    var m = Math.floor(seconds/60)<10 ? "0"+Math.floor(seconds/60) : Math.floor(seconds/60);
    var s = Math.floor(seconds-(m*60))<10 ? "0"+Math.floor(seconds-(m*60)) : Math.floor(seconds-(m*60));
    return m+":"+s;
};


//断网时，用户的操作
video.parent('.playimg').click(function(event) { 
    event.stopPropagation();
    var isInternet=true;  //默认
     //判断点击时候时候连接网络
    if(browser.versions.ios){
        var online = navigator.onLine;
        //ios不在网络状态时
        if (!online) {
            $('.souyue_video_lod').show();
            setTimeout(function(){
                $('.souyue_video_lod').hide();
            },1000);
            return false;
        }else{
            $('.playtxt').hide();
            $('.iosDuration').show();
            angin_load = false;
            _init();
            video.trigger('click');
        }
    }else{
        isInternet = true;
        var xhr = new ( window.ActiveXObject || XMLHttpRequest )( "Microsoft.XMLHTTP" );
        var status;
        xhr.open( "HEAD", "//" + window.location.hostname + "/?rand=" + Math.floor((1 + Math.random()) * 0x10000), false );
        try {
            isInternet =true;
            xhr.send();
        }catch (error) {
            $('.souyue_video_lod').show();
            setTimeout(function(){
                $('.souyue_video_lod').hide();
            },1000);
            isInternet =false;
            return false;
        }
        if (isInternet) {
            $('.playtxt').hide();
            $(".pauseBtn").hide();
            $(".playimg").css({'background': '#000'});
            angin_load = false;
            clickUpdate = true;
            currTime = errorPos;
            _init();
            video.trigger('click');
        };
    }
            

});

window.uiResume = function(){
    console.log("uiResume");
	}

window.uiPause = function(){
    console.log("uiPause");
    if(!(video == "" || video == undefined || video == null)){
        console.log("video is not null");
        video.click();
     }else{
        console.log("video is null");
     }
	}


//删除数组中元素为空的
function deleteNullOfArr(arr){
     for (var i = 0 ; i <arr.length; i++) {
        if(arr[i] == '' || typeof(arr[i]) == 'undefined'){
            arr.splice(i,1);
            i=i-1;
        }
     };
}


function _ajax(){
    $.ajax( {
            url:'http://mtest.zhongsou.com/newsdetail/videoSocket',// 跳转到 mtest.zhongsou.com/videoSocket
            data:{  
                   url:$('#myVideo').attr('data-url')
            },  
            type:'post',  
            cache:false,  
            dataType:'json',  
            success:function(data) {
                _src = data.video;
                _duration = data.video_length;
                _num = data.num;
                arr_scr = _src.split(";");
                arr_duration = _duration.split(";");
                // 删除视频请求地址数组为空的元素
                deleteNullOfArr(arr_scr);
                //删除视频媒体时长为空的元素；
                deleteNullOfArr(arr_duration);
                if (curr >= arr_scr.length){    //播放完成
                        curr = 0;
                        $(".bufferBar").css('width', '0');  //视频源播放完毕，缓冲条清零
                        $(".timeBar").css('width', '0'); //视频源播放完毕，缓冲条清零
                        $('.current').text(timeFormat(0));      //当前播放时间清零
                        video.attr('poster', bgurl);
                        overPlay=true;
                }else{
                        overPlay = false;
                }
                        video.attr('src', arr_scr[curr]);
                        video.attr('poster', videoPoster);
                        video.siblings('.souyue_video_lod').show();
            },  
            error : function() {  
                alert("异常！");  
            }  
     });
}

})

