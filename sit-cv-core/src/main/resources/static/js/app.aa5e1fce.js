(function(e){function t(t){for(var r,i,o=t[0],u=t[1],s=t[2],l=0,d=[];l<o.length;l++)i=o[l],Object.prototype.hasOwnProperty.call(a,i)&&a[i]&&d.push(a[i][0]),a[i]=0;for(r in u)Object.prototype.hasOwnProperty.call(u,r)&&(e[r]=u[r]);f&&f(t);while(d.length)d.shift()();return c.push.apply(c,s||[]),n()}function n(){for(var e,t=0;t<c.length;t++){for(var n=c[t],r=!0,i=1;i<n.length;i++){var o=n[i];0!==a[o]&&(r=!1)}r&&(c.splice(t--,1),e=u(u.s=n[0]))}return e}var r={},i={app:0},a={app:0},c=[];function o(e){return u.p+"js/"+({}[e]||e)+"."+{"chunk-2d21a3d2":"e6b1f586","chunk-3ccac2b2":"005f67cf","chunk-5e28fd86":"fd5b307b"}[e]+".js"}function u(t){if(r[t])return r[t].exports;var n=r[t]={i:t,l:!1,exports:{}};return e[t].call(n.exports,n,n.exports,u),n.l=!0,n.exports}u.e=function(e){var t=[],n={"chunk-3ccac2b2":1,"chunk-5e28fd86":1};i[e]?t.push(i[e]):0!==i[e]&&n[e]&&t.push(i[e]=new Promise((function(t,n){for(var r="css/"+({}[e]||e)+"."+{"chunk-2d21a3d2":"31d6cfe0","chunk-3ccac2b2":"d56e02f5","chunk-5e28fd86":"9ef5c51f"}[e]+".css",a=u.p+r,c=document.getElementsByTagName("link"),o=0;o<c.length;o++){var s=c[o],l=s.getAttribute("data-href")||s.getAttribute("href");if("stylesheet"===s.rel&&(l===r||l===a))return t()}var d=document.getElementsByTagName("style");for(o=0;o<d.length;o++){s=d[o],l=s.getAttribute("data-href");if(l===r||l===a)return t()}var f=document.createElement("link");f.rel="stylesheet",f.type="text/css",f.onload=t,f.onerror=function(t){var r=t&&t.target&&t.target.src||a,c=new Error("Loading CSS chunk "+e+" failed.\n("+r+")");c.code="CSS_CHUNK_LOAD_FAILED",c.request=r,delete i[e],f.parentNode.removeChild(f),n(c)},f.href=a;var p=document.getElementsByTagName("head")[0];p.appendChild(f)})).then((function(){i[e]=0})));var r=a[e];if(0!==r)if(r)t.push(r[2]);else{var c=new Promise((function(t,n){r=a[e]=[t,n]}));t.push(r[2]=c);var s,l=document.createElement("script");l.charset="utf-8",l.timeout=120,u.nc&&l.setAttribute("nonce",u.nc),l.src=o(e);var d=new Error;s=function(t){l.onerror=l.onload=null,clearTimeout(f);var n=a[e];if(0!==n){if(n){var r=t&&("load"===t.type?"missing":t.type),i=t&&t.target&&t.target.src;d.message="Loading chunk "+e+" failed.\n("+r+": "+i+")",d.name="ChunkLoadError",d.type=r,d.request=i,n[1](d)}a[e]=void 0}};var f=setTimeout((function(){s({type:"timeout",target:l})}),12e4);l.onerror=l.onload=s,document.head.appendChild(l)}return Promise.all(t)},u.m=e,u.c=r,u.d=function(e,t,n){u.o(e,t)||Object.defineProperty(e,t,{enumerable:!0,get:n})},u.r=function(e){"undefined"!==typeof Symbol&&Symbol.toStringTag&&Object.defineProperty(e,Symbol.toStringTag,{value:"Module"}),Object.defineProperty(e,"__esModule",{value:!0})},u.t=function(e,t){if(1&t&&(e=u(e)),8&t)return e;if(4&t&&"object"===typeof e&&e&&e.__esModule)return e;var n=Object.create(null);if(u.r(n),Object.defineProperty(n,"default",{enumerable:!0,value:e}),2&t&&"string"!=typeof e)for(var r in e)u.d(n,r,function(t){return e[t]}.bind(null,r));return n},u.n=function(e){var t=e&&e.__esModule?function(){return e["default"]}:function(){return e};return u.d(t,"a",t),t},u.o=function(e,t){return Object.prototype.hasOwnProperty.call(e,t)},u.p="",u.oe=function(e){throw console.error(e),e};var s=window["webpackJsonp"]=window["webpackJsonp"]||[],l=s.push.bind(s);s.push=t,s=s.slice();for(var d=0;d<s.length;d++)t(s[d]);var f=l;c.push([0,"chunk-vendors"]),n()})({0:function(e,t,n){e.exports=n("cd49")},"9ce9":function(e,t,n){"use strict";var r=n("d225"),i=n("b0b4"),a=n("fa87"),c=(n("96cf"),n("3b8d")),o=(n("ac6a"),n("5df3"),n("f400"),n("c6e1")),u=n("cc7d"),s=n.n(u),l=n("b2f7"),d=function(){function e(){Object(r["a"])(this,e),this.stompClientSubject=new l["a"],this.subscriptions=new Map}return Object(i["a"])(e,[{key:"connect",value:function(){var e=this;this.socket=new s.a("".concat(a["a"].endpoint,"/gs-guide-websocket")),this.stompClient=o["over"](this.socket),this.stompClient.debug=function(){},this.stompClient.connect({},(function(t){t&&(e.stompClientSubject.next(t),e.stompClientSubject.complete())}))}},{key:"subscribe",value:function(e,t,n,r){var i=this;this.stompClientSubject.subscribe((function(){var a=i.stompClient.subscribe(e,(function(e){t(e.body)}));i.subscriptions.set(e,a),n&&i.send(n,r)}))}},{key:"unsubscribe",value:function(e){var t=this;this.stompClientSubject.subscribe((function(){var n=t.subscriptions.get(e);n&&(n.unsubscribe(),t.subscriptions.delete(e))}))}},{key:"send",value:function(e,t){var n=this;this.stompClientSubject.subscribe((function(){n.stompClient.send(e,t)}))}}],[{key:"instance",get:function(){return this.INSTANCE||(this.INSTANCE=new e,a["a"].isServerMode&&this.INSTANCE.connect()),this.INSTANCE}}]),e}(),f=d.instance,p=n("bc3a"),h=n.n(p),v=function(){function e(){Object(r["a"])(this,e)}return Object(i["a"])(e,[{key:"fetchMenuItems",value:function(e){f.subscribe("/topic/designdoc/list",(function(t){return e(JSON.parse(t))}),"/app/designdoc/list")}},{key:"fetchFunctionModelDetail",value:function(e,t){var n="/topic/designdoc/function/"+e;this.currentDestination&&f.unsubscribe(this.currentDestination),this.currentDestination=n,f.subscribe(n,(function(e){return t(JSON.parse(e))}),"/app/designdoc/function",e)}},{key:"getCrudModel",value:function(){var e=Object(c["a"])(regeneratorRuntime.mark((function e(){var t;return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.next=2,h.a.get("".concat(a["a"].endpoint,"/designdoc/data/crud"));case 2:return t=e.sent,e.abrupt("return",t.data);case 4:case"end":return e.stop()}}),e)})));function t(){return e.apply(this,arguments)}return t}()}],[{key:"instance",get:function(){return this.INSTANCE||(this.INSTANCE=new e),this.INSTANCE}}]),e}(),b=v.instance,m=function(){function e(){Object(r["a"])(this,e),this.callbacks=new Map}return Object(i["a"])(e,[{key:"load",value:function(e,t){var n=document.createElement("script");n.onload=function(){document.body.removeChild(n)},n.src=e,document.body.appendChild(n),this.callbacks.set(e,t)}},{key:"setMessageListener",value:function(){var e=this;addEventListener("message",(function(t){if(t.source==window){var n=t.data;console.log("Receive postMessage ",n);var r=e.callbacks.get(n.path);r&&(console.log(r),r(n.content),e.callbacks.delete(n.path))}}))}}],[{key:"incetance",get:function(){return this.INSTANCE||(this.INSTANCE=new e,a["a"].isServerMode||this.INSTANCE.setMessageListener()),this.INSTANCE}}]),e}(),g=m.incetance,y=function(){function e(){Object(r["a"])(this,e),this.detailPathMapSubject=new l["a"]}return Object(i["a"])(e,[{key:"loadFunctionModelDetailPathMap",value:function(){var e=this;g.load("functionmodel/detail-path-map.js",(function(t){e.detailPathMap=t,e.detailPathMapSubject.next(!0),e.detailPathMapSubject.complete()}))}},{key:"fetchMenuItems",value:function(e){g.load("assets/designdoc-list.js",e)}},{key:"fetchFunctionModelDetail",value:function(e,t){var n=this;this.detailPathMapSubject.subscribe((function(){g.load(n.detailPathMap[e],(function(n){t(n.detailMap[e])}))}))}},{key:"getCrudModel",value:function(){var e=Object(c["a"])(regeneratorRuntime.mark((function e(){return regeneratorRuntime.wrap((function(e){while(1)switch(e.prev=e.next){case 0:return e.abrupt("return",new Promise((function(e){g.load("datamodel/crud/crud.js",(function(t){e(t)}))})));case 1:case"end":return e.stop()}}),e)})));function t(){return e.apply(this,arguments)}return t}()}],[{key:"instance",get:function(){return this.INSTANDE||(this.INSTANDE=new e,a["a"].isServerMode||this.INSTANDE.loadFunctionModelDetailPathMap()),this.INSTANDE}}]),e}(),k=y.instance;n.d(t,"a",(function(){return N}));var N=function(){function e(){Object(r["a"])(this,e)}return Object(i["a"])(e,null,[{key:"getService",value:function(){return a["a"].isServerMode?b:k}}]),e}()},acbd:function(e,t,n){"use strict";n.d(t,"a",(function(){return o}));var r=n("d225"),i=n("b0b4"),a=n("d442"),c=n.n(a),o=function(){function e(){Object(r["a"])(this,e)}return Object(i["a"])(e,null,[{key:"addEventListenerOnce",value:function(e,t,n,r){var i="attr_"+c()(n);e.hasAttribute(i)||(e.setAttribute(i,"true"),e.addEventListener(t,n,r))}}]),e}()},cd49:function(e,t,n){"use strict";n.r(t);n("aef6"),n("f559"),n("cadf"),n("551c"),n("f751"),n("097d");var r=n("2b0e"),i=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("v-app",[n("v-navigation-drawer",{attrs:{app:"",temporary:"",width:"400"},model:{value:e.drawer,callback:function(t){e.drawer=t},expression:"drawer"}},[n("doc-tree")],1),n("v-app-bar",{attrs:{app:""}},[n("v-app-bar-nav-icon",{on:{click:function(t){t.stopPropagation(),e.drawer=!e.drawer}}}),n("v-toolbar-title",[e._v("Code Visualizer")])],1),n("v-content",[n("v-container",{attrs:{fluid:""}},[n("router-view")],1)],1)],1)},a=[],c=n("d225"),o=n("308d"),u=n("6bb5"),s=n("4e2b"),l=n("9ab4"),d=function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("v-treeview",{attrs:{items:e.menuItems,dense:"","open-on-click":"",open:e.openNodes,"item-key":"id"},on:{"update:open":function(t){e.openNodes=t}},scopedSlots:e._u([{key:"label",fn:function(t){var r=t.item,i=t.open;return[r.endpoint?n("router-link",{attrs:{to:r.endpoint}},[e._v(e._s(r.name))]):n("span",{staticClass:"nodeLabel",attrs:{"data-item-id":r.id,"data-node-open":i}},[e._v(e._s(r.name))])]}}]),model:{value:e.selection,callback:function(t){e.selection=t},expression:"selection"}})},f=[],p=(n("ac6a"),n("b0b4")),h=function(){function e(){Object(c["a"])(this,e)}return Object(p["a"])(e,null,[{key:"collectIdsHavingSingleChild",value:function(e){var t=[e.id];return 1==e.children.length&&(t=t.concat(this.collectIdsHavingSingleChild(e.children[0]))),t}},{key:"findByIdRecursively",value:function(e,t){var n,r=this;return e===t.id?t:t.children?(t.children.forEach((function(t){n||(n=r.findByIdRecursively(e,t))})),n):void 0}}]),e}(),v=n("60a3"),b=n("9ce9"),m=n("acbd"),g=function(e){function t(){var e;return Object(c["a"])(this,t),e=Object(o["a"])(this,Object(u["a"])(t).apply(this,arguments)),e.designDocService=b["a"].getService(),e.menuItems=[],e.openNodes=[],e.selection=[],e}return Object(s["a"])(t,e),Object(p["a"])(t,[{key:"created",value:function(){var e=this;this.designDocService.fetchMenuItems((function(t){return e.menuItems=t}))}},{key:"updated",value:function(){this.addTreeviewRecursiveOpenFunctionality()}},{key:"addTreeviewRecursiveOpenFunctionality",value:function(){var e=this;document.querySelectorAll(".v-treeview-node__root, .v-treeview-node__toggle").forEach((function(t){m["a"].addEventListenerOnce(t,"click",e.onTreeviewNodeClick)}))}},{key:"onTreeviewNodeClick",value:function(e){if(e.target&&e.target instanceof HTMLElement){var t,n=e.target;switch(n.tagName){case"I":t=n.parentElement;break;case"DIV":t=n;default:return}var r=t.querySelector(".nodeLabel");r.dataset.nodeOpen&&this.openNodeRecursively(r.dataset.itemId)}}},{key:"openNodeRecursively",value:function(e){var t=this;this.menuItems.forEach((function(n){var r=h.findByIdRecursively(e,n);if(r){var i=h.collectIdsHavingSingleChild(r);i.filter((function(e){return-1===t.openNodes.indexOf(e)})).forEach((function(e){return t.openNodes.push(e)}))}}))}}]),t}(v["b"]);g=Object(l["a"])([v["a"]],g);var y=g,k=y,N=n("2877"),O=n("6544"),j=n.n(O),w=n("eb2a"),E=Object(N["a"])(k,d,f,!1,null,null,null),S=E.exports;j()(E,{VTreeview:w["a"]});var C=function(e){function t(){var e;return Object(c["a"])(this,t),e=Object(o["a"])(this,Object(u["a"])(t).apply(this,arguments)),e.drawer=null,e}return Object(s["a"])(t,e),t}(v["b"]);C=Object(l["a"])([Object(v["a"])({components:{DocTree:S}})],C);var I=C,T=I,A=n("7496"),M=n("40dc"),_=n("5bc1"),P=n("a523"),D=n("a75b"),x=n("f774"),L=n("2a7f"),R=Object(N["a"])(T,i,a,!1,null,null,null),V=R.exports;j()(R,{VApp:A["a"],VAppBar:M["a"],VAppBarNavIcon:_["a"],VContainer:P["a"],VContent:D["a"],VNavigationDrawer:x["a"],VToolbarTitle:L["a"]});var B=n("8c4f");r["a"].use(B["a"]);var F=new B["a"]({routes:[{path:"/designdoc",name:"designdoc",component:function(){return n.e("chunk-2d21a3d2").then(n.bind(null,"bb51"))}},{path:"/designdoc/data/crud",name:"crud",component:function(){return n.e("chunk-3ccac2b2").then(n.bind(null,"f527"))}},{path:"/designdoc/function/:functionId",name:"function",component:function(){return n.e("chunk-5e28fd86").then(n.bind(null,"e2e5"))}},{path:"*",redirect:"designdoc"}]}),H=n("f309");r["a"].use(H["a"]);var U=new H["a"]({icons:{iconfont:"mdi"}});r["a"].config.productionTip=!1,r["a"].config.errorHandler=function(e,t,n){e instanceof TypeError&&e.message.startsWith("Cannot create property 'isRootInsert' on string")&&location.href.endsWith("crud")?location.reload():console.error(e)},new r["a"]({router:F,vuetify:U,render:function(e){return e(V)}}).$mount("#app")},fa87:function(e,t,n){"use strict";(function(e){var r=n("d225"),i=n("b0b4"),a=n("3e8f"),c=function(){function t(){Object(r["a"])(this,t)}return Object(i["a"])(t,[{key:"isServerMode",get:function(){try{return Object(a["statSync"])("assets"),!0}catch(e){if("ENOENT"===e.code)return!1}}},{key:"endpoint",get:function(){return e&&Object({NODE_ENV:"production",BASE_URL:""}).VUE_APP_ENDPOINT?Object({NODE_ENV:"production",BASE_URL:""}).VUE_APP_ENDPOINT:"".concat(location.protocol,"//").concat(location.host)}}],[{key:"instance",get:function(){return this.INCETANCE||(this.INCETANCE=new t),this.INCETANCE}}]),t}();t["a"]=c.instance}).call(this,n("f28c"))}});