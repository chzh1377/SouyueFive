// JavaScript Document
/**
 *version: 1.0
 *author: luckyzhangxf@163.com
 *date: 2014/11/03
 *describe: 搜悦 三搜优化 小说 离线目录页
 *
 */ 
 var browser={
    versions:function(){
        var u = navigator.userAgent, app = navigator.appVersion;
        return {
            trident: u.indexOf('Trident') > -1, /*IE内核*/
            presto: u.indexOf('Presto') > -1, /*opera内核*/
            webKit: u.indexOf('AppleWebKit') > -1, /*苹果、谷歌内核*/
            gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,/*火狐内核*/
            mobile: !!u.match(/AppleWebKit.*Mobile.*/), /*是否为移动终端*/
            ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), /*ios终端*/
            android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, /*android终端或者uc浏览器*/
            iPhone: u.indexOf('iPhone') > -1 , /*是否为iPhone或者QQHD浏览器*/
            iPad: u.indexOf('iPad') > -1, /*是否iPad*/
            webApp: u.indexOf('Safari') == -1 /*是否web应该程序，没有头部与底部*/
        };
    }(),
    language:(navigator.browserLanguage || navigator.language).toLowerCase()
};
var directory=null,
	url = null,
	urlParam = null;
$(function(){
  	url = window.location.href;
	urlParam = urlParamToObj(url);
 	getFictionIndex(urlParam.nid);//小说正序 目录数组
});

/**
 * 1. 将数组逆序后返回
 *   @param  arr : 逆序前的数组（array）
 *   @return reArr : 逆序后的数组（array）
 */	
function getReverse(arr){
	var reArr = [];
	for(var i=arr.length-1; i>=0; i--){
		reArr.push(arr[i]);
		}
	return reArr;
	}
/**
 * 2. 将数组中的全部元素 分堆（每num个构成一个小数组）
 *   @param  arr : 分堆前的数组（array）
 *   @param  num : 每多少个 分一堆 (number)
 *   @return reArr : 分堆后的数组（array）
 */	
function pilesArr(arr,num){
	var reArr = [],
		pileNo = Math.ceil(arr.length/num);
	for(var i=1;i<=pileNo; i++){
		var smallArr = [],
			maxJ = i*num>arr.length ? arr.length : i*num;
		for(var j=(i-1)*num; j<maxJ; j++){
			smallArr.push(arr[j]);
			}
		reArr.push(smallArr);
		}
	return reArr;
	}

/**
 * 3. 构建目录区域html
 *   @param  arr : 小堆目录数组（array）
 *   @param  nowS : 当前排序
 *   @param  p : 当前页码
 *   @return html : 由传入数据构成的 html代码片段
 */	
function getChapterHtml(arr,nowS,p,ccid){
	var html = '<ul class="ch_'+nowS+'_'+p+'">';
	for(var i=0; i<arr.length; i++){
		html += '<li><a ';
		if(ccid==arr[i].id){
			html += ' class="on" ';
			}
		html += 'href="javascript:void(0);" data-id="'+arr[i].id+'">'+arr[i].t+'</a> </li>';
		}
	return html+'</ul>';
	}
function urlParamToObj(url){
	var reg_url =/^[^\?]+\?([\w\W]+)$/,
        reg_para=/([^&=]+)=([\w\W]*?)(&|$)/g, //g is very important
        arr_url = reg_url.exec( url ),
        ret        = {};
    if( arr_url && arr_url[1] ){
        var str_para = arr_url[1],result;
        while((result = reg_para.exec(str_para)) != null){
            ret[result[1]] = result[2];
        }
    }
    return ret;
	}
function goto_url(u){
	if(urlParamToObj(u).online == "true"){
		var obj={"category":"original","url":u};
		var str=JSON.stringify(obj);
		if(browser.versions.android){
			window.JavascriptInterface.onJSClick(str);
		}else if(browser.versions.ios){
			var url="souyue.onclick://"+str;
			document.location=encodeURI(url);
		}
	}else{
		window.location.href=u;
	}
	
}

// 离线阅读
function getFictionIndex(id){
    if(browser.versions.android){
        var result=window.JavascriptInterface.getFictionIndex(id);
        setFictionIndex(result);
    } else if(browser.versions.ios) {
        var url="JavascriptInterface.getFictionIndex:"+id;
        document.location=encodeURI(url)
    }
}
// 离线请求目录
function setFictionIndex(str){
    var index_data=null;
    if(browser.versions.android){
        index_data=str;
    } else if(browser.versions.ios) {
        index_data = window.localStorage.getItem(str);
    }
    if(typeof index_data == 'string'){
        try{
            index_data = JSON.parse(index_data);
        }catch(e){}
    }
    initDirectoy(index_data);
}

function initDirectoy(index_data) {
	 var num = 9,//每一页显示 的章数
    	 pos_d = index_data.item ,
	 	 pos_arr = pilesArr(pos_d,num),//正序分堆数组
	 	 totalPage = Math.ceil(pos_d.length/num),
		 rev_d = getReverse(pos_d),//小说倒序 目录数组
		 rev_arr = pilesArr(rev_d,num),//倒序分堆数组
		 pageHtml = '',//分页弹窗 的html
		 pagePop = $(".novel_fytc"),//页码弹窗
		 listCont = $(".novel_zjlist"),//目录显示区域
		 nowS = 0,//排序标志 0 正序 1 倒序
		 touchIng = false,//页码弹窗是否处在滑动中 
		 p = 1;

	
	<!----------------------------------内部公用方法 start----------------------------------------->
	var initListPos = function(){//打开弹窗 前 初始化 页码列表位置
		if(p>5){
			nowMargin = (5-p)*liH;
			targetMargin = nowMargin<maxMargin ? maxMargin :nowMargin;
			pagePop.children("ul").css({"margin-top":targetMargin});
			}
		else pagePop.children("ul").css({"margin-top":0});
		pagePop.find("ul li a").removeClass("on");
		pagePop.find("ul li").eq(p-1).children("a").addClass("on");
		}	
	var hidePop = function(){//隐藏 页码列表弹窗
		pagePop.hide();
		$(".novel_mba").hide();
		}
	var showNowPage = function(){//显示 指定页的目录
		$(".novel_pre").removeClass("novel_noclick");
		$(".novel_next").removeClass("novel_noclick");
		if(p==totalPage) $(".novel_next").addClass("novel_noclick");
		if(p==1) $(".novel_pre").addClass("novel_noclick");
		$(".lcur").text(p);
		listCont.children("ul").hide();
		if($('.ch_'+nowS+'_'+p)[0]) $('.ch_'+nowS+'_'+p).eq(0).show();
		else{
			var nowArr = nowS==0 ? pos_arr : rev_arr;
			listCont.append(getChapterHtml(nowArr[p-1],nowS,p,urlParam.ccid));//目录区域追加 当前页 目录	 
			}
		}
	<!----------------------------------内部公用方法 end----------------------------------------->
		
	for(var i=1; i<=totalPage; i++){
		pageHtml += '<li><a href="javascript:void(0);">第'+i+'页</a></li>';
		}
	$(".novel_fytc ul").html(pageHtml);//初始化 分页弹窗
	var ccN=0;
	for (var i = 0; i < pos_d.length; i++) {
		 	if(pos_d[i].id==urlParam.ccid){
		 		ccN=Math.floor(i/num);
		 	}
	};	 
	p = ccN+1;
	$(".lcur").text(p);
	$("#t_page").text(totalPage);
	listCont.append(getChapterHtml(pos_arr[ccN],nowS,p,urlParam.ccid));//显示 第N页 目录
	if(!urlParam.ccid){listCont.find("a:first").addClass("on");}
	$('.novel_zjlist li a').live('click',function(e){
		var u=window.location.href.split('chapterList.html')[0]+'nreaderOff.html?nid='+urlParam.nid+'&uid='+urlParam.userinfo+'&ccid='+$(this).attr("data-id")+'&online=false&zs_version='+urlParam.zs_version;
		goto_url(u);
	});	 
	//正序、倒序 点击	 
	$(".px a").live("click",function(){
		if(nowS==0){//当前是正序显示 需要切换至倒序 
			$(this).html('<em></em>倒序');
			nowS=1;
			}
		else{
			$(this).html('<em></em>正序');
			nowS=0;
			}
		p = 1;
		$(".lcur").text(1);
		showNowPage();
		});
	$(".novel_next").live("click",function(){//下一页	
		if(p<totalPage){
			p++;
			showNowPage();
			}
		});
	$(".novel_pre").live("click",function(){//上一页	
		if(p>1){
			p--;
			showNowPage();
			}
		});
	$(".novel_fytc").css("overflow-y","hidden");	
	$(".novel_pagenum").live("click",function(){//点击页码区域 弹出页码弹窗	
		initListPos();
		$(document).bind("touchmove",function(e){
			e.preventDefault();
			});
		window.scroll(0,0);
		$(".novel_mba").show();
		pagePop.show();
		});
	$(".novel_mba").live("click",function(){//点击遮罩 隐藏页码弹窗	
		hidePop();
		});
	pagePop.find("ul li").live("click",function(){//点击页码弹窗 具体某页 跳转
		p = $(this).index()+1;
		showNowPage();
		hidePop();
		$(document).unbind("touchmove");
		});
	
	var startY=endY=0,
		liH = parseInt(pagePop.find("li").css("height")) + 1,
		maxMargin = - (totalPage-num) * liH,
		nowMargin = targetMargin = 0;
	if(totalPage>9){
		if(!touchIng){
			pagePop[0].addEventListener('touchstart',function(e){
				startY = e.targetTouches[0].pageY;
				touchIng = true;
				return false;
				});
			pagePop[0].addEventListener('touchend',function(e){
				endY = e.changedTouches[0].pageY;
				if(Math.abs(endY-startY)>10){
					var oldMargin = parseInt(pagePop.children("ul").css("margin-top"));
					if(endY-startY<0){//上滑
						nowMargin = oldMargin-(num*liH);
						}
					else{
						nowMargin = oldMargin+(num*liH);
						}
					targetMargin = nowMargin>0 ? 0 : (nowMargin <= maxMargin ? maxMargin : nowMargin);
					pagePop.children("ul").animate({"margin-top":targetMargin},100);
					touchIng = false;
					}
				else touchIng = false;
				});
			}
		}
}