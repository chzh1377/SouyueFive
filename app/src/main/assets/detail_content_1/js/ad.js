/*!
 * wanglong@zhongsou.com
 */
/*
 * Lightweight JSONP fetcher
 * Copyright 2010-2012 Erik Karlsson. All rights reserved.
 * BSD licensed
 */
/*
 * Usage:
 *
 * JSONP.get( 'someUrl.php', {param1:'123', param2:'456'}, function(data){
 *   //do something with data, which is the JSON object you should retrieve from someUrl.php
 * });
 */
(function () {
    var userAgent = navigator.userAgent ? navigator.userAgent : ''
    var isAndroid = userAgent.match(/(android|adr)/ig)
    var isApple = !isAndroid && userAgent.match(/(iPhone|iPod|iPad)/ig)
    var trim = function (text) {
        return ((text || "") + "").replace(/^(\s|\u00A0)+|(\s|\u00A0)+$/g, "");
    }
    var cookie = function (name, value) {
        if (typeof value != 'undefined') { // name and value given, set cookie
            var date = new Date();
            date.setTime(date.getTime() + (365 * 10 * 24 * 60 * 60 * 1000));
            document.cookie = [name, '=', encodeURIComponent(value), '; expires=' + date.toUTCString(), '; path=/'].join('');
        } else {
            if (document.cookie && document.cookie != '') {
                var cookies = document.cookie.split(';');
                for (var i = 0; i < cookies.length; i++) {
                    var cookie = trim(cookies[i]);
                    if (cookie.indexOf(name + '=') == 0) {
                        return decodeURIComponent(cookie.substring(name.length + 1));
                    }
                }
            }
            return null;
        }
    };
    var hexcase = 0;
    /* hex output format. 0 - lowercase; 1 - uppercase        */
    function hex_md5(s) {
        return rstr2hex(rstr_md5(str2rstr_utf8(s)));
    }

    function rstr_md5(s) {
        return binl2rstr(binl_md5(rstr2binl(s), s.length * 8));
    }

    function rstr2hex(input) {
        try {
            hexcase
        } catch (e) {
            hexcase = 0;
        }
        var hex_tab = hexcase ? "0123456789ABCDEF" : "0123456789abcdef";
        var output = "";
        var x;
        for (var i = 0; i < input.length; i++) {
            x = input.charCodeAt(i);
            output += hex_tab.charAt((x >>> 4) & 0x0F) + hex_tab.charAt(x & 0x0F);
        }
        return output;
    }

    function str2rstr_utf8(input) {
        var output = "";
        var i = -1;
        var x, y;
        while (++i < input.length) {
            x = input.charCodeAt(i);
            y = i + 1 < input.length ? input.charCodeAt(i + 1) : 0;
            if (0xD800 <= x && x <= 0xDBFF && 0xDC00 <= y && y <= 0xDFFF) {
                x = 0x10000 + ((x & 0x03FF) << 10) + (y & 0x03FF);
                i++;
            }
            if (x <= 0x7F)
                output += String.fromCharCode(x);
            else if (x <= 0x7FF)
                output += String.fromCharCode(0xC0 | ((x >>> 6) & 0x1F), 0x80 | (x & 0x3F));
            else if (x <= 0xFFFF)
                output += String.fromCharCode(0xE0 | ((x >>> 12) & 0x0F), 0x80 | ((x >>> 6) & 0x3F), 0x80 | (x & 0x3F));
            else if (x <= 0x1FFFFF)
                output += String.fromCharCode(0xF0 | ((x >>> 18) & 0x07), 0x80 | ((x >>> 12) & 0x3F), 0x80 | ((x >>> 6) & 0x3F), 0x80 | (x & 0x3F));
        }
        return output;
    }

    function rstr2binl(input) {
        var output = Array(input.length >> 2);
        for (var i = 0; i < output.length; i++)
            output[i] = 0;
        for (var i = 0; i < input.length * 8; i += 8)
            output[i >> 5] |= (input.charCodeAt(i / 8) & 0xFF) << (i % 32);
        return output;
    }

    function binl2rstr(input) {
        var output = "";
        for (var i = 0; i < input.length * 32; i += 8)
            output += String.fromCharCode((input[i >> 5] >>> (i % 32)) & 0xFF);
        return output;
    }

    function binl_md5(x, len) {
        x[len >> 5] |= 0x80 << ((len) % 32);
        x[(((len + 64) >>> 9) << 4) + 14] = len;
        var a = 1732584193;
        var b = -271733879;
        var c = -1732584194;
        var d = 271733878;
        for (var i = 0; i < x.length; i += 16) {
            var olda = a;
            var oldb = b;
            var oldc = c;
            var oldd = d;
            a = md5_ff(a, b, c, d, x[i + 0], 7, -680876936);
            d = md5_ff(d, a, b, c, x[i + 1], 12, -389564586);
            c = md5_ff(c, d, a, b, x[i + 2], 17, 606105819);
            b = md5_ff(b, c, d, a, x[i + 3], 22, -1044525330);
            a = md5_ff(a, b, c, d, x[i + 4], 7, -176418897);
            d = md5_ff(d, a, b, c, x[i + 5], 12, 1200080426);
            c = md5_ff(c, d, a, b, x[i + 6], 17, -1473231341);
            b = md5_ff(b, c, d, a, x[i + 7], 22, -45705983);
            a = md5_ff(a, b, c, d, x[i + 8], 7, 1770035416);
            d = md5_ff(d, a, b, c, x[i + 9], 12, -1958414417);
            c = md5_ff(c, d, a, b, x[i + 10], 17, -42063);
            b = md5_ff(b, c, d, a, x[i + 11], 22, -1990404162);
            a = md5_ff(a, b, c, d, x[i + 12], 7, 1804603682);
            d = md5_ff(d, a, b, c, x[i + 13], 12, -40341101);
            c = md5_ff(c, d, a, b, x[i + 14], 17, -1502002290);
            b = md5_ff(b, c, d, a, x[i + 15], 22, 1236535329);
            a = md5_gg(a, b, c, d, x[i + 1], 5, -165796510);
            d = md5_gg(d, a, b, c, x[i + 6], 9, -1069501632);
            c = md5_gg(c, d, a, b, x[i + 11], 14, 643717713);
            b = md5_gg(b, c, d, a, x[i + 0], 20, -373897302);
            a = md5_gg(a, b, c, d, x[i + 5], 5, -701558691);
            d = md5_gg(d, a, b, c, x[i + 10], 9, 38016083);
            c = md5_gg(c, d, a, b, x[i + 15], 14, -660478335);
            b = md5_gg(b, c, d, a, x[i + 4], 20, -405537848);
            a = md5_gg(a, b, c, d, x[i + 9], 5, 568446438);
            d = md5_gg(d, a, b, c, x[i + 14], 9, -1019803690);
            c = md5_gg(c, d, a, b, x[i + 3], 14, -187363961);
            b = md5_gg(b, c, d, a, x[i + 8], 20, 1163531501);
            a = md5_gg(a, b, c, d, x[i + 13], 5, -1444681467);
            d = md5_gg(d, a, b, c, x[i + 2], 9, -51403784);
            c = md5_gg(c, d, a, b, x[i + 7], 14, 1735328473);
            b = md5_gg(b, c, d, a, x[i + 12], 20, -1926607734);
            a = md5_hh(a, b, c, d, x[i + 5], 4, -378558);
            d = md5_hh(d, a, b, c, x[i + 8], 11, -2022574463);
            c = md5_hh(c, d, a, b, x[i + 11], 16, 1839030562);
            b = md5_hh(b, c, d, a, x[i + 14], 23, -35309556);
            a = md5_hh(a, b, c, d, x[i + 1], 4, -1530992060);
            d = md5_hh(d, a, b, c, x[i + 4], 11, 1272893353);
            c = md5_hh(c, d, a, b, x[i + 7], 16, -155497632);
            b = md5_hh(b, c, d, a, x[i + 10], 23, -1094730640);
            a = md5_hh(a, b, c, d, x[i + 13], 4, 681279174);
            d = md5_hh(d, a, b, c, x[i + 0], 11, -358537222);
            c = md5_hh(c, d, a, b, x[i + 3], 16, -722521979);
            b = md5_hh(b, c, d, a, x[i + 6], 23, 76029189);
            a = md5_hh(a, b, c, d, x[i + 9], 4, -640364487);
            d = md5_hh(d, a, b, c, x[i + 12], 11, -421815835);
            c = md5_hh(c, d, a, b, x[i + 15], 16, 530742520);
            b = md5_hh(b, c, d, a, x[i + 2], 23, -995338651);
            a = md5_ii(a, b, c, d, x[i + 0], 6, -198630844);
            d = md5_ii(d, a, b, c, x[i + 7], 10, 1126891415);
            c = md5_ii(c, d, a, b, x[i + 14], 15, -1416354905);
            b = md5_ii(b, c, d, a, x[i + 5], 21, -57434055);
            a = md5_ii(a, b, c, d, x[i + 12], 6, 1700485571);
            d = md5_ii(d, a, b, c, x[i + 3], 10, -1894986606);
            c = md5_ii(c, d, a, b, x[i + 10], 15, -1051523);
            b = md5_ii(b, c, d, a, x[i + 1], 21, -2054922799);
            a = md5_ii(a, b, c, d, x[i + 8], 6, 1873313359);
            d = md5_ii(d, a, b, c, x[i + 15], 10, -30611744);
            c = md5_ii(c, d, a, b, x[i + 6], 15, -1560198380);
            b = md5_ii(b, c, d, a, x[i + 13], 21, 1309151649);
            a = md5_ii(a, b, c, d, x[i + 4], 6, -145523070);
            d = md5_ii(d, a, b, c, x[i + 11], 10, -1120210379);
            c = md5_ii(c, d, a, b, x[i + 2], 15, 718787259);
            b = md5_ii(b, c, d, a, x[i + 9], 21, -343485551);
            a = safe_add(a, olda);
            b = safe_add(b, oldb);
            c = safe_add(c, oldc);
            d = safe_add(d, oldd);
        }
        return Array(a, b, c, d);
    }

    function md5_cmn(q, a, b, x, s, t) {
        return safe_add(bit_rol(safe_add(safe_add(a, q), safe_add(x, t)), s), b);
    }

    function md5_ff(a, b, c, d, x, s, t) {
        return md5_cmn((b & c) | ((~b) & d), a, b, x, s, t);
    }

    function md5_gg(a, b, c, d, x, s, t) {
        return md5_cmn((b & d) | (c & (~d)), a, b, x, s, t);
    }

    function md5_hh(a, b, c, d, x, s, t) {
        return md5_cmn(b ^ c ^ d, a, b, x, s, t);
    }

    function md5_ii(a, b, c, d, x, s, t) {
        return md5_cmn(c ^ (b | (~d)), a, b, x, s, t);
    }

    function safe_add(x, y) {
        var lsw = (x & 0xFFFF) + (y & 0xFFFF);
        var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
        return (msw << 16) | (lsw & 0xFFFF);
    }

    function bit_rol(num, cnt) {
        return (num << cnt) | (num >>> (32 - cnt));
    }

    var JSONP = (function () {
        var head;

        function load(url) {
            var script = document.createElement('script'), done = false;
            script.src = url;
            script.async = true;
            script.onload = script.onreadystatechange = function () {
                if (!done && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
                    done = true;
                    script.onload = script.onreadystatechange = null;
                    if (script && script.parentNode) {
                        script.parentNode.removeChild(script);
                    }
                }
            };
            if (!head) {
                head = document.getElementsByTagName('head')[0];
            }
            head.appendChild(script);
        }

        function encode(str) {
            return encodeURIComponent(str);
        }

        function jsonp(url, params, callback, callbackName) {
            var query = (url || '').indexOf('?') === -1 ? '?' : '&', key;
            var uniqueName = "jsonp" + new Date().getTime();
            params = params || {};
            for (key in params) {
                if (params.hasOwnProperty(key)) {
                    query += encode(key) + "=" + encode(params[key]) + "&";
                }
            }
            window[uniqueName] = function (data) {
                callback(data);
                try {
                    delete window[uniqueName];
                } catch (e) {
                }
                window[uniqueName] = null;
            };
            load(url + query + 'callback=' + uniqueName);
            return uniqueName;
        }

        return {
            get: jsonp
        };
    }());
    /*
     k           ：广告词,对应srp keyword（必填）
     kid         ：广告词对应的唯一ID，对应srpid（可选）
     size        ：320x50，广告宽度和高度（可选）
     sid         ：会话ID，由客户端生成一次并永久保存在cookie或客户端，iOS，通过MD5（UUID），安卓，参考搜悦的取DeviceID方法返回的串后MD5。（必填）
     version     ：客户端SDK版本，由于今后解决兼容问题。（可选,默认1）
     device      ：设备名称值，例如iPod，iTouch（可选）
     osName      ：操作系统名称，例如apple，android（可选）
     osVersion   ：操作系统版本，例如5.1.1，2.3.4（可选）
     appId       ：广告系统为每个应用分配的ID：45EAFA2（必填）
     appName     ：应用名称：搜悦（可选）
     appVersion  ：应用版本：2.2.1（可选）
     said        ：广告位ID：1（可选，默认1）
     s           ：加密串：34534WAF5（必填）,MD5(keyword+session+appId+time)
     callback    ：JSONP跨域请求函数名：callback234234（可选）
     t           ：客户端当前时间，13245245245（必填）
     */
    /*
     超级app的
     * 	appId       ：2
     appName     ：超级app
     */
    var extend = function () {
        var result = {};
        for (var i = 0; i < arguments.length; i++) {
            var arg = arguments[i];
            for (var key in arg) {
                if (arg.hasOwnProperty(key)) {
                    result[key] = arg[key];
                }
            }
        }
        return result;
    };
    //	var check = function(params) {
    //		return trim(params.k) && trim(params.appId) && trim(params.parent)
    //	}
    var byId = function (id) {
        return document.getElementById(id);
    }
    var needPreloadImage = true;
    var showAdImage = function (parentId, Adobjs, i) {
        var Adobj = Adobjs[i];
        if (needPreloadImage && i + 1 <= Adobjs.length - 1) {
            new Image().src = Adobjs[i + 1].image;
        } else if (needPreloadImage) {
            needPreloadImage = false;
        }
        if (window.JavascriptInterface && (JavascriptInterface.openAd || JavascriptInterface.openAd2)) {
            var a = document.createElement('a');
            a._url = Adobj.url;
            a._download = Adobj.type == 'download';
            a._event = Adobj.event;
            //a.href = Adobj.url;
            a.onclick = function (e) {
                if (JavascriptInterface.openAd) {
                    JavascriptInterface.openAd(this._url, this._download);
                } else if (JavascriptInterface.openAd2) {
                    JavascriptInterface.openAd2('{"url":"' + this._url + '","download":' + this._download + ',"event":"' + this._event + '"}');
                }
                return false;
            }
            var img = document.createElement('img');
            img.src = Adobj.image;
            a.appendChild(img);
            var e = byId(parentId);
            e.innerHTML = "";
            e.appendChild(a);
        } else {
            byId(parentId).innerHTML = "<a href='" + Adobj.url + "'><img src='" + Adobj.image + "'/></a>"
        }
    }
    var params = extend({
        version: 1,
        said: 1,
        size: document.body.clientWidth + 'x50',
        t: new Date().getTime(),
        _r: Math.random()
    }, AdParams);
    if (window.JavascriptInterface && JavascriptInterface.getNetworkType) {
        params.network = JavascriptInterface.getNetworkType();
    } else if (window.networkType) {
        params.network = networkType
    }
    if (userAgent.match(/superapp/ig)) {
        if (isAndroid && window.JavascriptInterface && JavascriptInterface.getAppInfo) {
            var info = JavascriptInterface.getAppInfo()
            if (info) {
                info = eval('(' + info + ')')
                if (info.appId && info.appName) {
                    params.appId = info.appId
                    params.appName = info.appName
                }
            }
        } else if (isApple && window.appId && window.appName) {
            params.appId = window.appId
            params.appName = window.appName
        }
    }
    var sid = cookie("_zsadsid");
    if (sid) {
        params.sid = sid;
    } else {
        if (isAndroid && window.JavascriptInterface && JavascriptInterface.getSouyueInfo) {
            var info = JavascriptInterface.getSouyueInfo()
            if (info) {
                info = eval('(' + info + ')')
                if (info.imei) {
                    params.sid = hex_md5(info.imei)
                }
            }
        } else if (isApple && window.imei) {
            params.sid = hex_md5(imei)
        }
        if (!params.sid) {
            params.sid = hex_md5("JS://" + (function () {
                var r = '';
                for (var i = 0; i < 4; i++) {
                    r += (Math.random() + "").replace(/\./, '');
                }
                return r;
            })())
        }
        cookie("_zsadsid", params.sid);
    }
    params.s = hex_md5(params.k + params.sid + params.appId + params.t);
    if (isAndroid) {
        params.osName = 'android'
    } else if (isApple) {
        params.osName = 'apple'
    }
    //	if (check(params)) {
    delete params['parent'];
    var hostAppend = (//
    location.hostname == 'api2.souyue.mobi' || //搜悦正式
    location.hostname == 'm.zhongsou.com' || //m版正式
    location.hostname == 'moltest.zhongsou.com'//m版预上线
    ) ? '' : '-test';
    window.showAd = function (appId, appName, sid) {
        if (appId && appName && sid) {
            params.appId = appId
            params.appName = appName
            params.sid = sid
            params.s = hex_md5(params.k + params.sid + params.appId + params.t);
        }
        JSONP.get('http://unmonitor' + hostAppend + '.zhongsou.com/ad/lists', params, function (resp) {
            if (resp.head.status == 200 && resp.body.length > 0) {
                byId(AdParams.parent).style.height = "50px"
                var i = 0;
                showAdImage(AdParams.parent, resp.body, i);
                setInterval(function () {
                    i++;
                    if (i > resp.body.length - 1) {
                        i = 0;
                    }
                    showAdImage(AdParams.parent, resp.body, i)
                }, resp.head.duration || 5000)
            }
        }, 'callback' + new Date().getTime());
    }
    if ((navigator.userAgent.match(/souyue/ig) || location.search.indexOf('client=souyue') > 0) && AdParams && (AdParams.k || AdParams.kid)) {
        //do nothing
        //	} else {
        showAd(false, false, false);
        //	}
    }
})()