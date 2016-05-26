/*!
 * @author wanglong@zhongsou.com   ddd
 * @update hanguoliang@zhongsou.com 2014-8-18
 */
(function() {
	//	var browser.versions.isAndroid = (navigator.userAgent || "").toLowerCase().indexOf("android") >= 0
	//	if (typeof window.autoOpenSouyue == 'undefined') {
	//		window.autoOpenSouyue = true;
	//
	//
	//
	var environment = 'online',
		host="";
	//develop test preonline online
	switch(environment){
		case 'develop':
			host = "61.135.210.239:8888";
			break;
		case 'test':
			host = '103.29.134.224';
			break;
		case 'preonline':
			host = '103.29.134.225';
			break;
		case 'online':
			host = 'api2.souyue.mobi';
			break;
	}
    var browser = {
    	versions:function(){
    		var u = navigator.userAgent, app = navigator.appVersion;
    		return {
    			trident: u.indexOf('Trident') > -1, /*IE内核*/
    			presto: u.indexOf('Presto') > -1, /*opera内核*/
    			webKit: u.indexOf('AppleWebKit') > -1, /*苹果、谷歌内核*/
    			gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1,/*火狐内核*/
    			mobile: !!u.match(/AppleWebKit.*Mobile.*/), /*是否为移动终端*/
    			isApple: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), /*ios终端*/
    			isAndroid: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, /*android终端或者uc浏览器*/
    			iPhone: u.indexOf('iPhone') > -1 , /*是否为iPhone或者QQHD浏览器*/
    			iPad: u.indexOf('iPad') > -1, /*是否iPad*/
    			webApp: u.indexOf('Safari') == -1,/*是否web应该程序，没有头部与底部*/
    			isSouyue: u.indexOf('souyue')>-1
    		};
    	}(),
    	language:(navigator.browserLanguage || navigator.language).toLowerCase()
    };
	var addEventListener = window.addEventListener || function(n, f) {
			window.attachEvent('on' + n, f);
		};
	var removeEventListener = window.removeEventListener || function(n, f, b) {
			window.detachEvent('on' + n, f);
		};
	var cache = [];
	var addObservers = function() {
		addEventListener('scroll', throttledLoad);
		addEventListener('resize', throttledLoad);
	};

	var scrollTimeoutHolder = null;
	var throttledLoad = function() {
		clearTimeout(scrollTimeoutHolder);
		scrollTimeoutHolder = setTimeout(function() {
			loadVisibleImages();
		}, 300);
	};
	var removeObservers = function() {
		removeEventListener('scroll', throttledLoad, false);
		removeEventListener('resize', throttledLoad, false);
	};

	var _isLoadedAdv=false;//标记是否已经load过广告
	var ajax = function(url, callback) {
		var req = new XMLHttpRequest();
		req.onreadystatechange = function() {
			if (this.readyState == 4 && this.status == 200) {
				callback.apply(this, [ eval('(' + this.responseText + ')') ])
			} else {
				callback.apply(this, {
					code : 500
				})
			}
		};
		req.open("GET", url, true);
		req.send();
	};

	/*************************begin*************************/
	function getImageSrc(img) {
		var keys = [ "src", "source", "srcori" ];
		for ( var i in keys) {
			var val = img.getAttribute(keys[i]);
			if (val && !(val.match(/.+none.*\.png$/))) {
				return val.replace(/(^\s*)|(\s*$)/g,'');
			}
		}
		return ''
	}
	 function wrapImgWithLinkAfterLoadHtml(img, i, inSouyue, hasPic) {
		var needCreateA = img.parentNode.tagName.toUpperCase() != 'A';
		var a = needCreateA ? document.createElement("a") : img.parentNode;
		if (inSouyue) {
			a.setAttribute("href", "showimage://" + i)
		} else {
			a.setAttribute("href", getImageSrc(img));
			a.setAttribute("rel", "external")
		}
		if (needCreateA) {
			img.parentNode.insertBefore(a, img);
			img.parentNode.removeChild(img);
			a.appendChild(img);
		}
		a.onclick = function() {
			if (!hasPic) {
				var img = a.querySelector("img");
				var src = img.getAttribute("src");
				var srcori = img.getAttribute("srcori");
				if (src && srcori && src != srcori) {
					img.setAttribute("src", srcori);
					img.style['opacity'] = '1.0';
					return false;
				}
			}
			return true;
		}
	}
	/**
	 * 支持无图模式，点击后查看图片
	 * 在搜悦中用相册方式查看图片
	 * 在其他处用相册方式查看图片
	 */
	function doAfterLoadHtml() {
		//其他引用js的地方忽略操作
		if(window.inUrlContent != true) {
			var fontsize = getCookie("fontsize");
			var vc = getCookie("vc")||0;
			if(parseInt(vc)>=5.0){
				var setting = {"fontsize":fontsize};
				changeFontSize(setting);
			}
			return;
		}
		var imgs = document.querySelectorAll(".souyue-content img");
		if (window.inSouyue == true) {
			window.images = []; //搜悦内部看图
		}

		//gif图也显示大图
		for ( var i = 0; i < imgs.length; i++) {
			//
			var img = imgs[i];
			if (window.inSouyue == true) {
				images.push(getImageSrc(img))
			} else {
				img.setAttribute("alt", "(" + (i + 1) + "/" + imgs.length + ")"); //相册alt
			}
			wrapImgWithLinkAfterLoadHtml(img, i, window.inSouyue == true, window.hasPic == true);
		}
		if (window.inSouyue == true) {
			for(var i = 0,length=imgs.length;i<length;i++){
				var img = imgs[i];
				var index = i;
				if(isGif(img)){
					continue;
				}
				(function(index,img){
			                    $(img).on("tap",function(e){
			                        if(hasClass(img,'is-3g')){
			                        	return;
			                        }
			                        var str = JSON.stringify({
			                        	"category":"showimage",
			                        	"index": index,
						"imgs":images
			                        });
			                        if(browser.versions.isAndroid){
				                        if(JavascriptInterface && JavascriptInterface.onJSClick){
				                        	JavascriptInterface.onJSClick(str);
				                        }
			                        }else if(browser.versions.isApple){
			                        	window.location.href = 'souyue.onclick://'+charTranslation(encodeURIComponent(str));
			                        }else{
			                        	alert('error');
			                        }
			                        e.preventDefault();
			                        e.stopPropagation();
			                    })
			            })(i,img)
			}
		} else {
			var _imgs = window.document.querySelectorAll('.souyue-image a');
			if (_imgs && _imgs.length > 0) {
				instance = Code.PhotoSwipe.attach(_imgs, {});
			}
		}

		var fontsize = getCookie("fontsize");
		var vc = getCookie("vc");
		if(parseInt(vc)>=5.0){
			var setting = {"fontsize":fontsize};
			changeFontSize(setting);
		}
	}

	function charTranslation(str){
		return str.replace(/~/gi,escape('~')).replace(/!/gi,escape('!')).replace(/\*/gi,escape('*')).
			replace(/\(/gi,escape('(')).replace(/\)/gi,escape(')')).replace(/\'/gi,escape("'"));
	}


	/*************************end*************************/
	function initImageLazyLoader() {      //图片懒加载
		var imageNodes = document.querySelectorAll('img[source]');
		var _is3g = !isWifi(); //false 是wifi ，true 是3g
		for ( var i = 0; i < imageNodes.length; i++) {
			var imageNode = imageNodes[i];
			var _isgif = isGif(imageNode);
			if(!_isgif&&window.inSouyue&&_is3g){
				//3g下，对非gif图片不进行懒加载，绑定点击事件
				tapIn3g(imageNode);
				continue;
			}
			cache.push(imageNode); //cache 全局的懒加载图片
		}
		addObservers(); //给元素绑定懒加载事件
		loadVisibleImages(); //显示当前屏幕的图片
	}

	/**
	 * 判断当前的条件是否是3g下的
	 * @return {[type]} [description]
	 */
	function isWifi(){
		var cookieWifi = getCookie('wifi');
		var cookieHasPic = getCookie('hasPic');
		return (cookieWifi!=='0'||cookieHasPic!=='0');
	}

	window.loadMore = function(url) {
		var loader = document.getElementById("souyue-load-more");
		var content = document.getElementById("souyue-content");
		var loadImg = document.getElementById("souyue-loader");
		loadImg.setAttribute("src", "/d3api2/read/images/loading.gif");
		ajax("/d3api2/webdata/urlContentAjax.groovy?url=" + encodeURIComponent(url), function(json) {
			loadImg.setAttribute("src", "/d3api2/read/images/down.png");
			if (json.code == 200) {
				var html = json.html;
				if (window.hasPic != true) {
					html = html.replace(/\/d3api2\/read\/images\/none.png/g, "/d3api2/read/images/none-down.png")
				}
				content.innerHTML = content.innerHTML + html;
				/**/
				initImageLazyLoader();
				/**/
				doAfterLoadHtml();
				if (json.nextUrl) {
					loader.setAttribute("href", "javascript:loadMore('" + json.nextUrl + "')")
				} else {
					loader.parentNode.removeChild(loader)
				}
			} else {//error
			}
		});
	};
	var loadVisibleImages = function() {
		var scrollY = window.pageYOffset || document.documentElement.scrollTop;
		var pageHeight = window.innerHeight || document.documentElement.clientHeight;
		var min = scrollY - 200;
		var max = scrollY + pageHeight + 200;
		var i = 0;
		while (i < cache.length) {
			var image = cache[i];
			var imagePosition = getOffsetTop(image);
			var imageHeight = image.height || 0;
			if ((imagePosition >= min - imageHeight) && (imagePosition <= max)) {
				cache.splice(i, 1); //移除当前的东东。
				var source = image.getAttribute('source');
				//if(!isWifi()&&isGif(image)){
				if(isGif(image)){
					//执行gif的一套逻辑
					var _index = source.indexOf('!')===-1?source.length:source.indexOf('!');
					source = source.substr(0,_index) + '!detailmainj';
					image.className="lazy-loaded";
					try{
						removeClass(image.parentElement,'souyue-image-default');//删除掉默认背景图
						addClass(image.parentElement,'souyue-image-gifdefault')
					}catch (err){}
					//显示播放按钮
					showPlayContainer(image);
				}
				image.onload = function() {
					var _souyue = this.parentElement;
					var _span = this.parentNode.parentElement||0;
					this.className = 'lazy-loaded';
					var cssText = image.style.cssText;
					//"-webkit-transition":"opacity 600ms ease"
					cssText +='opacity:1;-webkit-transition:opacity 600ms ease';
					this.style.cssText = cssText;
					removeClass(_souyue,'souyue-image-default');//删除掉默认背景图
					if(_span){
						removeClass(_span,'souyue-image-default');//删除掉默认背景图
					}
					//兼容性处理，避免两个souyue-image样式出现
					if(_souyue&&_span&&hasClass(_souyue,"souyue-image")&&hasClass(_span,"souyue-image")){
						removeClass(_souyue,"souyue-image")
					}
					//添加情景电商锚点处理 2015-12-07
					//var picId = '';
					//if(source.indexOf("upaiyun")>-1){
					//	picId=source.substr(source.lastIndexOf("/")+1,19)
					//}else{
					//	picId=source.substr(source.lastIndexOf("=")+1,19)
					//}
					var picId = $(this).attr('src').split("!")[0];
					if(0){
					    showShowInfo(picId,this);
					}

				};
				image.onerror = function() {
					var cssText = this.style.cssText;
					cssText +='height:220px';
					this.style.cssText = cssText;
					//this.alt="图片加载失败！";
					//this.removeAttribute("src");
					this.removeAttribute("width");
					this.removeAttribute("height");

					if(isGif(image)){
						var _souyue = this.parentElement;
						removeClass(_souyue,'souyue-image-default');//删除掉默认背景图
						addClass(_souyue,'souyue-image-gifdefault');
						var _span = this.parentNode.parentElement||0;
						if(_span){
							removeClass(_span,'souyue-image-default');//删除掉默认背景图
						}
						cssText +='opacity:1;-webkit-transition:opacity 600ms ease;height:220px';
						this.style.cssText = cssText;
						this.className = 'lazy-loaded';
					}
					this.src = 'images/none.png';
				};
				if(($(image).attr('data-w')==null || $(image).attr('data-h')==null)&&!isGif(image)) {
					var _index = source.indexOf('!')===-1?source.length:source.indexOf('!');
					image.src = source.substr(0,_index);
				}else{
					image.src = source;
				}
				//image.removeAttribute('source');
				continue;
			}
			i++;
		}
		//滚动条滚动到广告位置，加载广告数据
		var advEle = document.getElementById("souyuead");
		var advPosition = getOffsetTop(advEle);
		var advHeight = advEle.height || 0;

		if ((advPosition >= min - advHeight) && (advPosition <= max)) {
			if(!_isLoadedAdv){
				//doGetAdvData();
				document.getElementById("souyuead").innerHTML="";//先去掉默认图
//				showAdImage(_Adobjs)
				_isLoadedAdv=true;
			}
		}

		//if (cache.length === 0) {
		//	removeObservers();
		//}
	};

	//情景商城  2015-12-13
//情景商城  update by  makan
	window.showShowInfo = function(picId,pic){
		$(pic).parent().addClass('souyue_position');
		// console.log($(pic).parent())
		var _width=$(pic).width(),
			_height=$(pic).height(),
			_dataW="",
			_dataH="";
		if($(pic).attr('data-w')==null || $(pic).attr('data-h')==null){
			_dataW=pic.naturalWidth;
			_dataH=pic.naturalHeight;
		}else{
			_dataW=parseInt($(pic).attr('data-w'));
			_dataH=parseInt($(pic).attr('data-h'));
		}

		var rateW=_width/_dataW,
			rateH=_height/_dataH;
		var anchors = shopImgInfo[picId] ? shopImgInfo[picId] :"";
		var picPar = "";
		if($(pic).parent('a').length == 1){
			picPar = $(pic).parent('a').parent();
		}else{
			picPar = $(pic).parent();
		}

		for(var i = 0;i < anchors.length; i++){
			var val = anchors[i];
			picPar.append($("<a />")
				.attr({
					"class" : "suibn_i",
					"data-id" : val.shopid+i
				}));
			create(i,val,pic,picId,rateW,rateH)
		}
	};
	function create(i,val,pic,picId,rateW,rateH){
		//获取各个点的坐标值
		// console.log(val)
		var arr_coordinate=val.coordinate.split(",");
		var coordinates = [{
				x:parseInt(arr_coordinate[0])*rateW,
				y:parseInt(arr_coordinate[1])*rateH
			},{
				x:parseInt(arr_coordinate[2])*rateW,
				y:parseInt(arr_coordinate[3])*rateH
			}],
		//获取锚点中间值
			left = (coordinates[1].x-coordinates[0].x)/2+coordinates[0].x,
			top = (coordinates[1].y-coordinates[0].y)/2+coordinates[0].y;
		//定位锚点
		var divSuibnObj = $(pic).parent().find(".suibn_i[data-id='"+val.shopid+i+"']").length>0?$(pic).parent().find(".suibn_i[data-id='"+val.shopid+i+"']"):$(pic).parent().siblings(".suibn_i[data-id='"+val.shopid+i+"']")
		divSuibnObj.css({
			position:'absolute',
			top: (top-22)+'px',//22是锚点的宽度和长度
			left:(left-22)+'px'//22是锚点的宽度和长度
			//border:"1px solid blue"
		}).tap(function(e) {
			e.stopPropagation();
			// console.log(this)
			$('.souyue-image').removeClass('active');
			$(this).parent('.souyue-image').addClass('active');
			$(".suibn_i").show();
			$(".active").find('.suibn_i').hide();
			$('.suiwrap_mask').hide();
			$('.suiwrap_xzinfo').hide();
			showContent(val,coordinates,this,picId,i);
		});
	};
//生成图片详情页矩形函数
	function showContent(val,coordinates,obj,picId,j){
		var obj=obj,
			spanElement = $(obj).parent(),
			ImgHeight = spanElement.height(),
			ImgWidth = spanElement.width();
		if(spanElement.find('.suiwrap_mask').length==0){
			var mask = $("<i />");
			mask.attr({
				'class': 'suiwrap_mask'
			});
			spanElement.append(mask);
		}
		else{
			spanElement.find('.suiwrap_mask').show();
		}
		if(spanElement.find('.suiwrap_xzinfo[data-id="'+val.shopid+j+'"]').length==0)
		{
			var spanParent = $("<span />"),
				content = '';
			spanParent.attr({
				'class' : 'suiwrap_xzinfo',
				'data-id': val.shopid+j
			});
			var spanImg=$("<span />");
			spanImg.attr({
				'class': 'suiwrap_xzimg'
			});
			var ach_img=$("<img />");
			ach_img.attr({
				'src': val.shopimg
			});
			content = '<a class="suit_xzcon " data-id="'+ val.shopid+'" data-link="'+ val.shoplink +'">'
			+'<span class="suit_xztitle"><i>详情：</i>' + val.shoptitle +'</span>'
			+'<span class="suit_xztxt">'
			+'<span class="suit_xzmoney">悦商城：<i>&yen;'+val.shopprice+'</i></span><br>';
			if(val.mkprice&&val.mkprice!="0"){
				content+='<span class="suit_xzmoneysc">市场价：<i>&yen;'+val.mkprice+'</i></span></span>'
			}
			content+='<span class="suit_xza"><i>去看看</i></span></a>';
			spanImg.append(ach_img);
			spanParent.append(spanImg).append(content);
			spanElement.append(spanParent);
			$(".suiwrap_xzinfo,.suit_xzcon,.suit_xza,.suiwrap_xzimg,.suiwrap_xzimg img").tap(function(e){
				e.stopPropagation();
				newLink('悦商城','悦商城',$(this).closest('.suit_xzcon').attr('data-link'));
			});
			$(".suit_xza").children('i').tap(function(e){
				e.stopPropagation();
				newLink('悦商城','悦商城',$(this).parent().parent().attr('data-link'));
			});
			$(".suiwrap_mask").tap(function(e) {
				e.stopPropagation();
				$(".suibn_i").show();
				$(".suiwrap_mask, .suiwrap_xzinfo").hide();
			});
		}else{
			spanElement.find('.suiwrap_xzinfo[data-id="'+val.shopid+j+'"]').show();
		}
	}

	window.getQueryString = function(url,name){
		var url = url.substring(url.indexOf("?"));
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
		var r = url.substring(1).match(reg);
		if (r != null) return decodeURIComponent(r[2]);
		return null;
	};

	//搜悦第三方页面接口
	window.newLink = function(keyword,title,url){
		var str = JSON.stringify({
			"category":"interactWeb",
			"keyword": keyword || decodeURIComponent(getQueryString(window.location.href,'keyword')),
			"title":title,
			"url":url
		});
		if(browser.versions.isAndroid){
			if(JavascriptInterface && JavascriptInterface.onJSClick){
				JavascriptInterface.onJSClick(str);
			}
		}else if(browser.versions.isApple){
			window.location.href = 'souyue.onclick://'+charTranslation(encodeURIComponent(str));
		}else{
			alert('error');
		}
	};

	/**
	 * 锚点
	 */
	var init = function() {
		//		if (autoOpenSouyue) {
		//			var params = getParams();
		//			if (params.isappinstalled == '1') {
		//				setTimeout(function() {
		//					location.href = "wx360a9785675a8653://" + encodeURIComponent("keyword=" + params.keyword + "&srpId=" + params.srpId + "&url=" + params.url);
		//				}, 500)
		//			}
		//		}
		onGotoSRP();
		if (!document.querySelectorAll) {
			document.querySelectorAll = function(selector) {
				var doc = document;
				var head = doc.documentElement.firstChild;
				var styleTag = doc.createElement('STYLE');
				head.appendChild(styleTag);
				doc.__qsaels = [];
				styleTag.styleSheet.cssText = selector + "{x:expression(document.__qsaels.push(this))}";
				window.scrollBy(0, 0);
				return doc.__qsaels;
			}
		}
		window._lazyLoaderInit  = function() {
			resetImageSize();
			doAfterLoadHtml();
			setTimeout(function() {
				initImageLazyLoader();
			}, 300);//延迟一段时间开始加载可视区图片
		};

	};
	var onGotoSRP = function() {
		var as = document.getElementsByTagName("a");
		for ( var i = 0; i < as.length; i++) {
			var _href = decodeURIComponent(as[i].getAttribute("href"));
			if(!_href||_href==null){
				continue;
			}
			if(_href.indexOf("javascript")<0&&_href.indexOf("souyue.onclick")<0&&_href.indexOf("showimage")<0&&_href.indexOf("d3api2")<0&&_href.indexOf("www.souyue.mobi")<0
				&&_href.indexOf("jsp")<0&&_href.indexOf("mall.zhongsou.com"&&_href.indexOf('m.zhongsou.com'))<0){//已经注册js方法的，不添加onclick事件
				as[i].onclick = (function(i,href) {
					return function(){
						if (href.indexOf("#keyword") == 0) {//处理srp词分享到圈子下数据
							var href2 = href.replace(/(#keyword=)|(srpId=)/g, '').split('&');
							var kid = href2.length >=2 && href2[1] ?href2[1]:'none';
							$.ajax({
								url: "http://"+host+"/d3api2/webdata/hemsKeywordAjax.groovy?kid="+kid+"&keyword="+href2[0]+"&datatype=jsonp",
								dataType: "jsonp",
								success:function(json){
									console.log("success");
									if (json.code == 200) {
										var str = '{"category":"srp","keyword":"'+href2[0]+'","srpId":"'+json.srpId+'","md5":""}';
										if(browser.versions.isAndroid){
											if (JavascriptInterface && JavascriptInterface.onJSClick) {
												JavascriptInterface.onJSClick(str);
												return false;
											}
										}else if(browser.versions.isApple){
											window.location.href ='souyue.onclick://' +charTranslation(encodeURIComponent(str));
										}
									} else {//error
									}
								}
							})
						}else{//处理普通的a标签数据，替换成原生的方式，前端去跳转
							var str = '{"category":"original","url":"'+href+'"}';
							if (browser.versions.isApple) {
								var finalStr ='souyue.onclick://' + encodeURIComponent(str);
								location.href = finalStr;
							} else if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
								JavascriptInterface.onJSClick(str)
							}
						}
						return true;
					}
				})(i,_href);
				as[i].href="javascript:void(0);";
			}
			//电商锚点
			if(_href.indexOf("mall.zhongsou.com")>-1){
				as[i].onclick = (function(i,href) {
					return function(){
						var str ='{"category":"interactWeb","url":"'+href+'"}'
						if (browser.versions.isApple) {
							var finalStr ='souyue.onclick://' + encodeURIComponent(str);
							location.href = finalStr;
						} else if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
							JavascriptInterface.onJSClick(str)
						}
						return true;//以此代码为准
					}
				})(i,_href);
				as[i].href="javascript:void(0);";
				as[i].className="mallAnchor";
				$(as[i]).attr('data-url',_href);
			}
			//详情页锚点
			if(_href.indexOf("m.zhongsou.com/newsdetail")>-1){
				var keyword = getQueryString(_href,'keyword'),
					srpId = getQueryString(_href,'srpId'),
					url = getQueryString(_href,'url');
				$(as[i]).attr({
					'onclick':'openNewsDetail("'+url+'","","","'+srpId+'","'+keyword+'","");',
					'href':'javascript:void(0);'
				})
				.addClass('detailAnchor')
				.removeAttr('target');
			}
		}

	};

	//通过kid获取srpId
	var getSrpIdById = function(kid){
		var srpId="";
		ajax("/d3api2/webdata/hemsKeywordAjax.groovy?kid="+kid, function(json) {
			if (json.code == 200) {
				srpId = json.srpId;
			} else {//error
			}
		});
		return srpId;
	};

	//	var getParams = function() {
	//		var params = {
	//			keyword : '',
	//			srpId : '',
	//			url : ''
	//		};
	//		var search = location.search;
	//		if (search && search.indexOf("?") == 0) {
	//			var ary = search.substring(1, search.length).split("&");
	//			for ( var i = 0; i < ary.length; i++) {
	//				var item = ary[i].split("=");
	//				if (item.length == 2) {
	//					params[item[0]] = decodeURIComponent(item[1]);
	//				} else if (item.length == 1) {
	//					params[item[0]] = ''
	//				}
	//			}
	//		}
	//		return params;
	//	}
	//	var setDownloadLind = function() {
	//		var ua = navigator.userAgent.toLowerCase()
	//		if (ua.indexOf("android") >= 0) {
	//			var as = document.getElementsByTagName("a")
	//			for ( var i = 0; i < as.length; i++) {
	//				var a = as[i]
	//				if (a.className == 'souyue-download') {
	//					a.href = 'http://android.myapp.com/android/down.jsp?appid=619070'
	//				}
	//			}
	//		}
	//	}

	//	}
	var getOffsetTop = function(el) {
		var val = 0;
		if (el.offsetParent) {
			do {
				val += el.offsetTop;
			} while (el = el.offsetParent);
			return val;
		}
	};
	init();

	//2 给每个预制大小了的img设置宽高
	function resetImageSize (){
		var obj = document.getElementById('souyue-content')||document.getElementById('ugc-content');
		var width = obj.offsetWidth-6;
		var _imgs = Array.prototype.slice.call(document.querySelectorAll('img'));
		_imgs.forEach(function(item,index){
			var _width = item.getAttribute('data-w')?item.getAttribute('data-w').replace('px',''):0;
			var _height = item.getAttribute('data-h')?item.getAttribute('data-h').replace('px',''):0;
			var _souyue = item.parentElement;
			if(hasClass(_souyue,"noClass")||hasClass(_souyue,"souyue-image")){
			}else{
				_souyue =item.parentElement.parentElement;
			}
			if(_width&&_height){
				var _finalHeight = (width*_height/_width)| 0;
				if(_finalHeight){
					var cssText = _souyue.style.cssText;
					cssText +='width:'+width+'px;height:'+_finalHeight+'px;';
					_souyue.style.cssText = cssText;
					//item.style.cssText = cssText;
				}
			}
			//有长有宽
			if(_width&&_height){
				//有长有宽
				var _finalHeight
				var cssText = _souyue.style.cssText;
				if(_width>=width){
					_finalHeight = (width*_height/_width)|0;
					if(_finalHeight){
						//marginBottom:16是为了原先的详情页的margin就是16px;
						cssText +='width:'+width+'px;height:'+_finalHeight+'px;';
						_souyue.style.cssText = cssText;
					}
				}else{
					//显示原有的尺寸宽度
					cssText +='_width:'+width+'px;height:'+_height+'px;';
					_souyue.style.cssText = cssText;
				}
			}
		})
	}

	//3 3g-绑定事件
	function tapIn3g(item){
		//var _imgs = Array.prototype.slice.call(document.querySelectorAll('img'));
		//_imgs.forEach(function(item,index){
		//添加样式is-3g(标识为未打开，不能通过搜悦相册查看器查看)
		addClass(item,'is-3g');
		item.addEventListener('click',function(e){
			if(item.className.indexOf('is-3g')===-1){
				return;
			}
			e.stopPropagation();
			e.preventDefault();
			removeClass(item,'is-3g');
			addClass(item,'lazy-loaded');
			var _souyue = item.parentElement;
			var _span = item.parentNode.parentElement||0;
			removeClass(_souyue,'souyue-image-default');//删除掉默认背景图
			if(_span){
				removeClass(_span,'souyue-image-default');//删除掉默认背景图
			}
			var retina = window.devicePixelRatio > 1;
			var attrib = retina ? "data-src-retina" : "data-original";
			var source = item.getAttribute(attrib);
			source = source || item.getAttribute("source") || item.getAttribute("data-src") || item.getAttribute("data-url");
			if (item.not_new == "yes") {
				return false
			}
			if (source) {
				item.jumei_bg = item.src;
				item.onerror = function() {
					item.not_new = "yes";
					item.setAttribute("src", this.jumei_bg);
					//modifyCss(item,{"opacity": 1, "-webkit-transition": "all 600ms ease"})
					var cssText = item.style.cssText;
					cssText +='"opacity":1,"-webkit-transition":"opacity 600ms ease"';
					item.style.cssText = cssText;
					//$(this).css({"opacity": 1, "-webkit-transition": "all 600ms ease"})
				};
				item.onload = function() {
					item.not_new = "yes";
					var cssText = item.style.cssText;
					cssText +='opacity:1;-webkit-transition:opacity 600ms ease';
					item.style.cssText = cssText;
					//兼容性处理，避免两个souyue-image样式出现
					if(_souyue&&_span&&hasClass(_souyue,"souyue-image")&&hasClass(_span,"souyue-image")){
						removeClass(_souyue,"souyue-image")
					}
				};
				item.setAttribute("src", source);
				if (typeof callback === "function") {
					callback.call(this);
				}
			}
		})
	}

	//获取cookie
	this.getCookie = function(name){
		var strCookie= window.location.hash.substr(1);
		var arrCookie=strCookie.split(/;\s*/);
		for(var i=0;i<arrCookie.length;i++){
			var arr=arrCookie[i].split("=");
			if(arr[0]==name) {
				return arr[1];
			}
		}
		return "";
	};

	//判断是否有这个样式
	function hasClass(obj, cls) {
		if(!obj.className) return false;
		return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
	}

	//添加样式
	function addClass(obj, cls) {
		if (!hasClass(obj, cls)) obj.className += " " + cls;
	}

	//删除样式
	function removeClass(obj, cls) {
		if (hasClass(obj, cls)) {
			var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
			obj.className = obj.className.replace(reg, '');
		}
	}

	//通过样式获取元素
	function getElementsByClassName (clsName) {
		var aResult=[];
		var aEle=document.getElementsByTagName('*');
		/*正则模式*/
		var re = new RegExp("\\b" + clsName + "\\b","g");
		for(var i=0;i<aEle.length;i++){
			/*字符串search方法判断是否存在匹配*/
			if(aEle[i].className.search(re) != -1){
				aResult.push(aEle[i]);
			}
		}
		return aResult;
	}

	//修改style样式
	function modifyCss(obj,css){
		for(var key in css){
			obj.style.key = css[key];
		}
	}
	//5.0添加的新功能============end========================

	//gif的逻辑判断==============begin======================

	//判断要加载的图片是否是gif格式的
	function isGif(img){
		var retina = window.devicePixelRatio > 1;
		var attrib = retina ? "data-src-retina" : "data-original";
		var source = img.getAttribute(attrib);
		source = source || img.getAttribute("source") || img.getAttribute("data-src") || img.getAttribute("data-url");
		return (source && /\.gif/.test(source));
	}

	//给img绑定第一个img事件
	function bindGifToImg(img){
		//给img绑定click事件~~
		img.addEventListener('click',function(e){
			if(!hasClass(img,'has-gif')){
				return;
			}
			var retina = window.devicePixelRatio > 1;
			var attrib = retina ? "data-src-retina" : "data-original";
			var source = img.getAttribute(attrib);
			source = source || img.getAttribute("source") || img.getAttribute("data-src") || img.getAttribute("data-url");
			if(source){
				var _img = document.createElement('img');
				_img.crossOrigin = "Anonymous";
				_img.onerror = function(){
					_img = null;
				};
				_img.onload = function(e){
					setStaticImg(_img,img);
				};
				_img.src = source;
			}
			e.preventDefault();
			e.stopPropagation();
			return false;
		});
	}
	//对请求的图片，绘制第一张静态图
	function drawStaticImg(img,srcImg){
		var canvas = document.createElement('canvas');
		//因为safe问题，所以直接用canvas显示
		srcImg.parentNode.appendChild(canvas);
		srcImg.style.display = 'none';
		canvas.width = window.innerWidth-30;
		canvas.height = canvas.width*img.height/img.width;
		cxt = canvas.getContext('2d');
		cxt.drawImage(img,0,0,canvas.width,canvas.height);
		return img.src;
	}
	//页面请求gif图片的回调函数
	function setStaticImg(img,srcImg){
		if(!hasClass(srcImg,'has-gif')){
			return;
		}
		removeClass(srcImg,'has-gif');
		try{
			drawStaticImg(img,srcImg);
		}catch(e){
			alert(e);
		}
		var _souyue = srcImg.parentElement;
		var _span = srcImg.parentNode.parentElement||0;
		removeClass(_souyue,'souyue-image-default');//删除掉默认背景图
		if(_span){
			removeClass(_span,'souyue-image-default');//删除掉默认背景图
		}
		var cssText = srcImg.style.cssText;
		cssText +='opacity:1;-webkit-transition:opacity 600ms ease';
		srcImg.style.cssText = cssText;

		//兼容性处理，避免两个souyue-image样式出现
		if(_souyue&&_span&&hasClass(_souyue,"souyue-image")&&hasClass(_span,"souyue-image")){
			removeClass(_souyue,"souyue-image")
		}
		//======================
		_div = document.createElement('div');
		_div.innerHTML = LOAD_GIF_TEMPLATE;
		_souyue.appendChild(_div);
		_div.addEventListener('click',function(e){
			e.preventDefault();
			e.stopPropagation();
			var playDom =  _div.querySelector('.play');
			removeClass(playDom,'play');
			addClass(playDom,'loading');
			srcImg.onload = function(e){
				var canvas = srcImg.nextElementSibling;
				_div.removeEventListener('click');
				_div.parentNode.removeChild(_div);
				removeClass(srcImg,'is-3g');
				addClass(srcImg,'lazy-loaded');
				srcImg.style.display = 'block';
				if(canvas){
					canvas.parentNode.removeChild(canvas);
				}
			};
			srcImg.src = img.src;
		});
	}
	/**
	 * 显示播放容器
	 * @param  {[type]} img [description]
	 * @return {[type]}     [description]
	 */
	function showPlayContainer(img){
		var _souyue = img.parentElement;
		var _div = document.createElement('div');
		_div.innerHTML = LOAD_GIF_TEMPLATE;
		_souyue.appendChild(_div);
		_div.addEventListener('click',function(e){
			e.preventDefault();
			e.stopPropagation();
			var playDom =  _div.querySelector('.play');
			removeClass(playDom,'play');
			addClass(playDom,'loading');
			img.onload = function(e){
				_div.removeEventListener('click');
				_div.parentNode.removeChild(_div);
			};
			if(img.src){
				img.src = img.src.replace('!detailmainj','');
			}else{
				if(img.getAttribute("source")){
					img.src = img.getAttribute("source").replace('!detailmainj','');
				}
			}

		});
	}
	var LOAD_GIF_TEMPLATE = [
		//'<i class="suiwrap_mask"></i>',
		'<a class="suic_play">',
		'<span class="play"><i></i></span>',
		'</a>'
		//'<span class="suit_gif">GIF</span>'
	].join('\n');

	//gif的逻辑判断==============end======================
	function goToDetail(option){
		var category = option.category || '',
			str = '';
		switch(category){
			case 'addCircleSub':
				str = '{"category":"'+option.category+'","interest_id":"'+option.interest_id+'","callback":"'+option.callback+'","interestLogo":"'+option.interestLogo+'","keyword":"'+option.keyword+'","srpId":"'+option.srpId+'"}';
				break;
			case 'addSrpSub':
				str = '{"category":"'+option.category+'","srpId":"'+option.srpId+'","keyword":"'+option.keyword+'","callback":"'+option.callback+'"}';
				break;
			case 'close':
				str = '{"category":"'+option.category+'"}';
				break;
			case 'interest':
				str = '{"category":"'+option.category+'","interest_id":"'+option.interest_id+'","interest_logo":"'+option.interest_logo+'","keyword":"'+option.keyword+'","srpId":"'+option.srpId+'","type":"'+option.type+'"}';
				break;
			case 'srp':
				str = '{"category":"'+option.category+'","keyword":"'+option.keyword+'","srpId":"'+option.srpId+'"}';
				break;
			case 'emptyWeb':
				str = '{"category":"'+option.category+'","url":"'+option.url+'"}';
				break;
			default:
				window.location.href = option.url;
				return;
		}
		if (browser.versions.isAndroid)
		{
			if (JavascriptInterface && JavascriptInterface.onJSClick) {
				JavascriptInterface.onJSClick(str);
			}
		}
		else if (browser.versions.isApple)
		{
			window.location.href = 'souyue.onclick://' + charTranslation(encodeURIComponent(str));
		}
	}
	window.addCircleSubCallback=function(callbackData){
		var orderBtn = $('div[class="quan_box"][data-interestid="'+callbackData.interest_id+'"]').find('.quan_btn1');
	    if(callbackData.result==1){
	        orderBtn.addClass('cur').text('进入');
	        orderBtn.parents('.quan_box').attr("data-issub",'1');
	    }else if(callbackData.result==2){
	    	orderBtn.removeClass('cur').text('订阅');
	        orderBtn.parents('.quan_box').attr("data-issub",'0');
	    }
	};

	window.addSrpSubCallback=function(callbackData){
	    var orderBtn = $('div[class="quan_box"][data-srpid="'+callbackData.srp_id+'"]').find('.quan_btn1');
	    if(callbackData.result==1){
	        orderBtn.addClass('cur').text('进入');
	        orderBtn.parents('.quan_box').attr("data-issub",'1');
	    }else if(callbackData.result==2){
	        orderBtn.removeClass('cur').text('订阅');
	        orderBtn.parents('.quan_box').attr("data-issub",'0');
	    }
	};

	//订阅处理
	$(document).on('tap','.quan_boxbg',function(e){
        var srpid = $(this).attr("data-srpid");
        var keyword = $(this).attr("data-keyword");
        var iscircle = $(this).attr("data-iscircle")=='1'?true:false;
        var str = "";
        if(!srpid){
        	return false;
        }
        if(iscircle){
        	//圈子
        	var interestid = $(this).attr("data-interestid");
        	var interestlogo = $(this).attr("data-interestlogo");
        	var obj ={
				category:'interest',
				interest_id:interestid,
				interest_logo:interestlogo,
				keyword:keyword,
				srpId:srpid,
				type:"home"
			};
			str = JSON.stringify(obj);
        }else{
        	//srp
        	var obj={
				category:'srp',
				keyword:keyword,
				srpId:srpid,
				md5:""
			};
			str = JSON.stringify(obj);
        }
        if(browser.versions.isAndroid){
            if(JavascriptInterface && JavascriptInterface.onJSClick){
            	JavascriptInterface.onJSClick(str);
            }
        }else if(browser.versions.isApple){
        	window.location.href = 'souyue.onclick://'+charTranslation(encodeURIComponent(str));
        }
    });
    $(document).on('tap','.quan_logo,.quan_title',function(e){
    	var srpid = $(this).parents('.quan_box').attr("data-srpid");
        var keyword = $(this).parents('.quan_box').attr("data-keyword");
        var iscircle = $(this).parents('.quan_box').attr("data-iscircle")=='1'?true:false;
        var str = '';
        if(!srpid){
        	return false;
        }
        if(iscircle){
        	var interestlogo = $(this).parents('.quan_box').attr("data-interestlogo");
	        var interestid = $(this).parents('.quan_box').attr("data-interestid");
        	str='{"category":"interest","interest_id":"'+interestid+'","interest_logo":"'+interestlogo+'","type":"home","keyword":"'+keyword+'","srpId":"'+srpid+'"}';
        }else{
        	str='{"category":"srp","keyword":"'+keyword+'","srpId":"'+srpid+'","md5":""}';
        }
        if(browser.versions.isAndroid){
            if(JavascriptInterface && JavascriptInterface.onJSClick){
                JavascriptInterface.onJSClick(str);
            }
        }else if(browser.versions.ios){
            window.location.href = 'souyue.onclick://'+ charTranslation(encodeURIComponent(str));
        }
    });
    $(document).on('tap','.quan_btn1',function(e){
        var srpid = $(this).parents('.quan_box').attr("data-srpid");
        var keyword = $(this).parents('.quan_box').attr("data-keyword");
        var iscircle = $(this).parents('.quan_box').attr("data-iscircle")=='1'?true:false;
        var issub = $(this).parents('.quan_box').attr("data-issub")=='1'?true:false;
        var str = '';
        if(!srpid){
        	return false;
        }
        if(iscircle){
        	var interestlogo = $(this).parents('.quan_box').attr("data-interestlogo");
	        var interestid = $(this).parents('.quan_box').attr("data-interestid");
        	if(issub){
        		//已订阅
        		str='{"category":"interest","interest_id":"'+interestid+'","interest_logo":"'+interestlogo+'","type":"home","keyword":"'+keyword+'","srpId":"'+srpid+'"}';
        	}
        	else{
        		//未订阅
        		str='{"category":"addCircleSub","interest_id":"'+interestid+'","callback":"addCircleSubCallback"}';
        	}
        }else{
        	if(issub){
        		//已订阅
        		str='{"category":"srp","keyword":"'+keyword+'","srpId":"'+srpid+'","md5":""}';
        	}
        	else{
        		//未订阅
        		str='{"category":"addSrpSub","srpId":"'+srpid+'","keyword":"'+keyword+'","callback":"addSrpSubCallback"}';
        	}
        }
        if(browser.versions.isAndroid){
            if(JavascriptInterface && JavascriptInterface.onJSClick){
                JavascriptInterface.onJSClick(str);
            }
        }else if(browser.versions.ios){
            window.location.href = 'souyue.onclick://'+ charTranslation(encodeURIComponent(str));
        }
    });

	//人物关系跳转
	$(document).on('tap','.rengwu', function () {
		var keyword = $(this).data('keyword'),
			srpId = $(this).data('srpid');
		goToDetail({
			category:'srp',
			keyword:keyword,
			srpId:srpId
		});
	});
/*
** author:makan
** des:updata souyue
** time:20160401
*/

window.openBlog = function(blogId){
    var data = JSON.stringify({
        category: 'pasePage',
        type: 'blog',
        blogId: blogId,
        blog_id: blogId
    });
    if (browser.versions.ios) {
        location.href = 'souyue.onclick://' + encodeURIComponent(data)
    } else if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
        JavascriptInterface.onJSClick(data)
    }
};

//<!-- 进入圈子首页 -->
window.openInterest = function(keyword,srpId,interestId,interestLogo) {
    var data = JSON.stringify({
        category: 'interest',
        interest_id:interestId,
        srpId:srpId,
        keyword:keyword,
        interest_logo: interestLogo,
        type:'home'
    });
    if (browser.versions.ios) {
        location.href = 'souyue.onclick://' + encodeURIComponent(data)
    } else if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
        JavascriptInterface.onJSClick(data)
    }
};

//<!-- 展开更多 -->
window.adminMoreOper = function() {
    var data = JSON.stringify({
        category: 'adminMoreOper'
    });
    if (browser.versions.ios) {
        location.href = 'souyue.onclick://' + encodeURIComponent(data)
    } else if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
        JavascriptInterface.onJSClick(data)
    }
};

/************************客户端调用方法，渲染页面**********************************/
//5.0添加的新功能============begin======================

/**********************************分享红包begin*******************************************/
    $("#an1").on('tap',function(){
        var data = JSON.stringify({
            category: 'shareToWX',
            callback: 'shareToWXCallback'
        });
        if (browser.versions.isSouyue) {
            if (browser.versions.isApple) {
                location.href = 'souyue.onclick://' + encodeURIComponent(data)
            }
            if (browser.versions.isAndroid) {
                if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
                    JavascriptInterface.onJSClick(data)
                }
            }
        }
    });
    $("#an2").on('tap',function(){
        var data = JSON.stringify({
            category: 'shareToWXFriend',
            callback: 'shareToWXFriendCallback'
        });
        if (browser.versions.isSouyue) {
             if (browser.versions.isApple) {
                 location.href = 'souyue.onclick://' + encodeURIComponent(data)
             }
             if (browser.versions.isAndroid) {
                 if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
                     JavascriptInterface.onJSClick(data)
                 }
             }
        }
    });
    $("#an3").on('tap',function(){
         var data = JSON.stringify({
             category: 'getSharePrize',
             callback: 'getSharePrizeCallback'
         });
         if (browser.versions.isSouyue) {
             if (browser.versions.isApple) {
                 location.href = 'souyue.onclick://' + encodeURIComponent(data)
             }
             if (browser.versions.isAndroid) {
                 if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
                     JavascriptInterface.onJSClick(data)
                 }
             }
         }
    });
    window.getSharePrizeCallback = function(data) {
        var newNode = document.getElementById("redbackInsert");
        if (1 == data) {
            newNode.innerHTML = '<a href="javascript:void(0);" class="an3"><i></i><span>已领取</span></a>';
        } else {
            newNode.innerHTML = '<a href="javascript:getShareRed();" class="an3"><i></i><span>领取红包</span></a>';
        }
    };

	/***********************************分享红包end********************************************/

	//1设置详情页字体大小
	window.changeFontSize = function(setting){
		if(setting&&setting.fontsize){
			var classArry = ['big','middle','small'];
			var _fontSize = setting.fontsize;
			var otherArry = classArry.filter(function(item){return item!==_fontSize});
			var _el = document.querySelector('.souyue-body')||document.querySelector('.main');
			if(!_el){return;}
			var _classList = _el.className.split(/\s+/g);
			var newClass = [_fontSize];
			_classList.forEach(function(klass){
				//有相同的class,不做处理
				if(klass===setting.fontsize){
					return;
				}else {
					if (otherArry.indexOf(klass)===-1) {
						//没有其它的值
						newClass.push(klass);
					}
				}
			});
			_el.className = newClass.join(' ');
		}
	};

    //处理订阅
    window.handlerStatusEvent = function (data) {
        window.handlerInterestSubEvent(data);
    };
       window.render = function(options){
            $(".souyue_foot").append(options);
            if(!$('.sy_sct_b .quan_box').length||!$('.mallAnchor').length){
            	return false;
            }
            var keyword=$('.mallAnchor').eq(0).text();
            $.ajax({
            	url: 'http://'+host+'/d3api2/detail/template.util.data.groovy?keyword='+keyword+'&type=shopImg&datatype=jsonp',
            	type: 'GET',
            	dataType: 'jsonp',
            	success:function(data){
            		var img = data.img,
            			desc = data.description;
            		if(!isEmpty(img) && !isEmpty(desc)){
                		var html = '<div class="quan_box">'
                           +'<ul class="quan_info">'
                                +'<li class="quan_logo">'+(!!img?'<img src="'+img+'">':'')+'</li>'
                                +'<li class="quan_title">'
                                    +'<h3>'+keyword+'</h3>'
                                    +'<p class="txt">'+(!!desc?desc:'')+'</p>'
                                 +'</li>'
                                 +'<a class="quan_btn1 aniu" href="javascript:void(0);" onclick="newLink(\'\',\'\',\''+$(".mallAnchor").eq(0).attr("data-url")+'\')">去看看</a>'
                            +'</ul>'
                        +'</div>';
                        $('.sy_sct_b').append(html)
                    }
            	}
            });
        };

    window.adminMoreOper = function() {
        var data = JSON.stringify({
            category: 'adminMoreOper'
        });
        if (browser.versions.isApple) {
            location.href = 'souyue.onclick://' + encodeURIComponent(data)
        } else if (window.JavascriptInterface && JavascriptInterface.onJSClick) {
            JavascriptInterface.onJSClick(data)
        }
    };
    $(document).ready(function(){
        _lazyLoaderInit();
        //页面渲染完毕，再添加相关数据中圈子的图片
        var image = document.getElementById("_mInterestLogo");
        if(image){
        	setTimeout(function() {
        		image.src=image.getAttribute("source2")+"!submid";
        	}, 250);//延迟一段时间开始加载可视区图片
        }
    });

    //锚点、相关推荐，进入详情
	window.openNewsDetail = function(url,img_url,title,srpid,keyword,description){
		var str = '{"category":"pasePage","keyword":"'+decodeURIComponent(keyword)+'","srpId":"'+srpid+'","title":"'+decodeURIComponent(title)+'","url":"'+encodeURIComponent(url)+'","images":"'+img_url+'","md5":"","description":"'+description+'"}';
		if(browser.versions.isAndroid){
			if(JavascriptInterface && JavascriptInterface.onJSClick){
		        JavascriptInterface.onJSClick(str);
		    }
		}
		else if(browser.versions.ios){
		    window.location.href = 'souyue.onclick://'+charTranslation(encodeURIComponent(str));
		}
	};

    function isEmpty(str){
        if (str == null || str == undefined || str == '') {
            return true;
        }
        return false;
    }
})();