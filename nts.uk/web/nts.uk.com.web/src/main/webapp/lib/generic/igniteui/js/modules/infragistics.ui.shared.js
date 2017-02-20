﻿/*!@license
 * Infragistics.Web.ClientUI jQuery Shared 16.2.20162.2040
 *
 * Copyright (c) 2011-2016 Infragistics Inc.
 *
 * http://www.infragistics.com/
 *
 * Depends on:
 *	jquery-1.9.1.js
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *	infragistics.util.js
 */
(function(factory){if(typeof define==="function"&&define.amd){define(["jquery","jquery-ui","./infragistics.util"],factory)}else{factory(jQuery)}})(function($){/*!@license
* Infragistics.Web.ClientUI shared localization resources 16.2.20162.2040
*
* Copyright (c) 2011-2016 Infragistics Inc.
*
* http://www.infragistics.com/
*
*/
(function($){$.ig=$.ig||{};if(!$.ig.SharedLocale){$.ig.SharedLocale={};$.extend($.ig.SharedLocale,{locale:{}})}})(jQuery);$.widget("ui.igLoading",{options:{cssClass:null,includeVerticalOffset:true},_indicator:null,_create:function(){var offset,css;css=this.options.cssClass===null?"ui-igloadingmsg":this.options.cssClass;this._hgrid=this.element.closest(".ui-iggrid-root").closest(".ui-iggrid");if(this.element.children("."+css).length===0){this._indicator=$("<span></span>").appendTo(this.element).attr("id",this.element[0].id+"_loading").addClass(css);offset=this.element.offset();this.refreshPos()}this._yShift=this._indicator?Math.round(this._indicator.height()/2):0},indicatorElement:function(){return this._indicator},indicator:function(){return this},show:function(refresh){if(refresh!==false){this.refreshPos()}if(this._resId){clearInterval(this._resId)}this._resId=setInterval($.proxy(this._resizeContainer,this),300);this._indicator.css("display","").css("visibility","visible")},hide:function(){this._indicator.css("display","none").css("visibility","hidden");clearInterval(this._resId);this._resId=null},_resizeContainer:function(){var offset=this.element.offset();if(offset.top+this.element.innerHeight()/2-this._yShift!==this._indicator.css("top")){this.refreshPos()}},refreshPos:function(){var offset=this.element.offset(),top=offset.top+this.element.innerHeight()/2-this._yShift,left=offset.left+this.element.innerWidth()/2,verticalOffset,relativeOffset;if(this._hgrid.length>0&&top>this._hgrid.offset().top+this._hgrid.height()){return}if(this._hgrid.length>0&&left>this._hgrid.offset().left+this._hgrid.width()){return}if(this.options.includeVerticalOffset&&top>$(window).height()){verticalOffset=($(window).height()-offset.top-$(window).scrollTop())/2;top=$(window).height()-verticalOffset<offset.top?offset.top+verticalOffset:$(window).height()-verticalOffset}relativeOffset=$.ig.util.getRelativeOffset(this._indicator);top-=relativeOffset.top;left-=relativeOffset.left;this._indicator.css("left",left).css("top",top)},destroy:function(){clearInterval(this._resId);this._indicator.remove()}});$.extend($.ui.igLoading,{version:"16.2.20162.2040"});$.widget("ui.igSlider",$.ui.mouse,{options:{animate:false,max:100,min:0,orientation:"horizontal",step:1,value:0,bookmarks:[{value:0,title:"",disabled:false,css:""}],showBookmarkTitle:true,syncHandleWithBookmark:true},css:{baseClasses:"ui-igslider ui-widget ui-widget-content ui-corner-all",horizontalOrientationClass:"ui-igslider-horizontal",verticalOrientationClass:"ui-igslider-vertical",sliderDisabledClass:"ui-igslider-disabled ui-disabled",handleClass:"ui-igslider-handle",bookmarkClass:"ui-igslider-bookmark",bookmarkDisabledClass:"ui-igslider-bookmark-disabled",bookmarkTooltipClass:"ui-igslider-bookmark-tooltip"},events:{start:"start",slide:"slide",stop:"stop",change:"change",bookmarkHit:"bookmarkhit",bookmarkClick:"bookmarkclick"},_numpages:5,widget:function(){return this.element},_createWidget:function(){this.options.bookmarks=[];$.Widget.prototype._createWidget.apply(this,arguments)},_create:function(){var o=this.options,self=this,css=this.css;this._keySliding=false;this._mouseSliding=false;this._animateOff=true;this._handleIndex=null;this._detectOrientation();this._mouseInit();this.element.addClass(css.baseClasses);if(o.disabled){this.element.addClass(css.sliderDisabledClass)}if($(".ui-igslider-handle",this.element).length===0){$("<a href='#'></a>").appendTo(this.element).addClass(css.handleClass)}this.handles=$(".ui-igslider-handle",this.element).addClass("ui-state-default"+" ui-corner-all").bind({click:function(event){event.preventDefault()},mouseover:function(){if(!o.disabled){$(this).addClass("ui-state-hover")}},mouseout:function(){$(this).removeClass("ui-state-hover")},focus:function(){if(!o.disabled){$(".ui-igslider .ui-state-focus").removeClass("ui-state-focus");$(this).addClass("ui-state-focus")}else{$(this).blur()}},blur:function(){$(this).removeClass("ui-state-focus")},keydown:function(event){var ret=true,index=$(this).data("index.ui-igslider-handle"),allowed,curVal,newVal,step;if(self.options.disabled){return}switch(event.keyCode){case $.ui.keyCode.HOME:case $.ui.keyCode.END:case $.ui.keyCode.PAGE_UP:case $.ui.keyCode.PAGE_DOWN:case $.ui.keyCode.UP:case $.ui.keyCode.RIGHT:case $.ui.keyCode.DOWN:case $.ui.keyCode.LEFT:ret=false;if(!self._keySliding){self._keySliding=true;$(this).addClass("ui-state-active");allowed=self._start(event,index);if(allowed===false){return}}break}step=self.options.step;curVal=newVal=self.value();switch(event.keyCode){case $.ui.keyCode.HOME:newVal=self.options.min;break;case $.ui.keyCode.END:newVal=self.options.max;break;case $.ui.keyCode.PAGE_UP:newVal=self._trimValue(curVal+(self.options.max-self.options.min)/this._numpages);break;case $.ui.keyCode.PAGE_DOWN:newVal=self._trimValue(curVal-(self.options.max-self.options.min)/this._numpages);break;case $.ui.keyCode.UP:case $.ui.keyCode.RIGHT:if(curVal===self.options.max){return}newVal=self._trimValue(curVal+step);break;case $.ui.keyCode.DOWN:case $.ui.keyCode.LEFT:if(curVal===self.options.min){return}newVal=self._trimValue(curVal-step);break}self._slide(event,index,newVal);return ret},keyup:function(event){var index=$(this).data("index.ui-igslider-handle");if(self._keySliding){self._keySliding=false;self._stop(event,index);self._change(event,index);$(this).removeClass("ui-state-active")}}}).each(function(i){$(this).data("index.ui-igslider-handle",i)});this.handle=this.handles.eq(0);this._renderBookmarks();this._refreshValue();this._animateOff=false},_renderBookmarks:function(){if(this.options.bookmarks&&this.options.bookmarks.length>0){var len=this.options.bookmarks.length,i=0,o=this.options,css=this.css,mark,self=this;for(i;i<len;i++){mark=o.bookmarks[i];$("<a href='#'></a>").appendTo(this.element).data("index.ui-igslider-bookmark",i).addClass(mark.disabled?css.bookmarkDisabledClass:css.bookmarkClass).addClass(mark.css&&mark.css.length>0?mark.css:"").css("left",o.min!==o.max?(mark.value-o.min)/(o.max-o.min)*100+"%":"0%")}this.bookmarks=$(".ui-igslider-bookmark",this.element).addClass("ui-state-default").bind({mousedown:function(event){var noCancel=true,bookmarkIndex=$(this).data("index.ui-igslider-bookmark");event.preventDefault();event.stopPropagation();noCancel=self._bookmarkClicked(event,bookmarkIndex);if(self.options.syncHandleWithBookmark&&noCancel){self._slide(event,0,self.options.bookmarks[bookmarkIndex].value)}},mouseover:function(event){if(!o.disabled){$(this).addClass("ui-state-hover");if(self.options.showBookmarkTitle){self._showBookmarkTitle($(this),event)}}},mouseout:function(){if(!o.disabled){$(this).removeClass("ui-state-hover");if(self.options.showBookmarkTitle){self._hideBookmarkTitle($(this))}}},click:function(event){event.preventDefault()}});this._createBookmarkTooltip();this._buildBookmarkHit()}},destroy:function(){this.handles.remove();this.clearBookmarks();this.element.removeClass(this.css.baseClasses+" ui-igslider-horizontal"+" ui-igslider-vertical"+" ui-igslider-disabled").removeData("slider").unbind(".slider");this._mouseDestroy();return this},_id:function(suffix){return this.element[0].id+suffix},_showBookmarkTitle:function(bookmark,browserEvent){var tooltip=$("#"+this._id("_tooltip")),title=this.options.bookmarks[bookmark.data("index.ui-igslider-bookmark")].title;if(title&&title.length>0){tooltip.igTooltip("option","text",title);tooltip.css("top",browserEvent.pageY-tooltip.outerHeight()-5).css("left",browserEvent.pageX-tooltip.width()/2+bookmark.width()/2).show()}},_hideBookmarkTitle:function(){$("#"+this._id("_tooltip")).hide()},_createBookmarkTooltip:function(){var html='<div id="'+this._id("_tooltip")+'" class="'+this.css.bookmarkTooltipClass+'"></div>';$(html).appendTo($(document.body)).igTooltip({arrowLocation:"bottom"}).hide()},clearBookmarks:function(){if(this.bookmarks){this.bookmarks.remove()}},_mouseCapture:function(event){var o=this.options,position,normValue,index=0,handle=this.handles.eq(index),self=this,offset,mouseOverHandle;if(o.disabled){return false}this.elementSize={width:this.element.outerWidth(),height:this.element.outerHeight()};this.elementOffset=this.element.offset();position={x:event.pageX,y:event.pageY};normValue=this._normValueFromMouse(position);this._mouseSliding=true;self._handleIndex=index;handle.addClass("ui-state-active");if(!$.ig.util.isOpera){handle.focus()}offset=handle.offset();mouseOverHandle=!$(event.target).parents().andSelf().is(".ui-igslider-handle");this._clickOffset=mouseOverHandle?{left:0,top:0}:{left:event.pageX-offset.left-handle.width()/2,top:event.pageY-offset.top-handle.height()/2-(parseInt(handle.css("borderTopWidth"),10)||0)-(parseInt(handle.css("borderBottomWidth"),10)||0)+(parseInt(handle.css("marginTop"),10)||0)};this._slide(event,index,normValue);this._animateOff=true;this._mouseUpHandler=function(event){return self._mouseStop(event)};$(document).bind("mouseup."+this.widgetName,this._mouseUpHandler);return true},_mouseStart:function(event){$(document).unbind("mouseup."+this.widgetName,this._mouseUpHandler);return this._start(event,this._handleIndex)},_mouseDrag:function(event){var position={x:event.pageX,y:event.pageY},normValue=this._normValueFromMouse(position);this._slide(event,this._handleIndex,normValue);return false},_mouseStop:function(event){var self=this;this.handles.removeClass("ui-state-active");setTimeout(function(){self._mouseSliding=false},1e3);this._stop(event,this._handleIndex);this._change(event,this._handleIndex);this._handleIndex=null;this._clickOffset=null;this._animateOff=false;return false},_detectOrientation:function(){var o=this.options,css=this.css;if(o.orientation==="vertical"){this.orientation="vertical";this.element.removeClass(css.horizontalOrientationClass).addClass(css.verticalOrientationClass)}else{this.orientation="horizontal";this.element.removeClass(css.verticalOrientationClass).addClass(css.horizontalOrientationClass)}},_normValueFromMouse:function(position){var pixelTotal,pixelMouse,percentMouse,valueTotal,valueMouse;if(this.orientation==="horizontal"){pixelTotal=this.elementSize.width;pixelMouse=position.x-this.elementOffset.left-(this._clickOffset?this._clickOffset.left:0)}else{pixelTotal=this.elementSize.height;pixelMouse=position.y-this.elementOffset.top-(this._clickOffset?this._clickOffset.top:0)}percentMouse=pixelMouse/pixelTotal;if(percentMouse>1){percentMouse=1}if(percentMouse<0){percentMouse=0}if(this.orientation==="vertical"){percentMouse=1-percentMouse}valueTotal=this.options.max-this.options.min;valueMouse=this.options.min+percentMouse*valueTotal;return this._trimValue(valueMouse)},_start:function(event,index){var uiHash={handle:this.handles[index],value:this.value()};return this._trigger(this.events.start,event,uiHash)},_slide:function(event,index,newVal){var allowed;if(newVal!==this.value()){allowed=this._trigger(this.events.slide,event,{handle:this.handles[index],value:newVal});if(allowed!==false){this.value(newVal)}}},_stop:function(event,index){var uiHash={handle:this.handles[index],value:this.value()};this._trigger(this.events.stop,event,uiHash)},_change:function(event,index){if(!this._keySliding&&!this._mouseSliding){var uiHash={handle:this.handles[index],value:this.value()};this._trigger(this.events.change,event,uiHash);this._checkBookmarkHit(uiHash.value,event);if(uiHash.value===this.options.max&&this._marksHit&&this._marksHit.length===0){this._buildBookmarkHit()}}},_checkBookmarkHit:function(currentValue,event){if(this._marksHit&&this._marksHit.length>0&&currentValue>=this._marksHit[0].value){var index=this._marksHit[0].index;this._marksHit.shift();this._bookmarkHit(event,index)}},_buildBookmarkHit:function(){var b=this.options.bookmarks||[],i=b.length-1;this._marksHit=[];for(i;i>=0;i--){if(!b[i].disabled){this._marksHit.push({value:b[i].value,index:i})}}this._marksHit.sort(this._sortBookmarksJSON);this._marksHit=$.extend(true,[],this._marksHit)},_sortBookmarksJSON:function(a,b){return a.value-b.value},_bookmarkClicked:function(event,index){var uiHash={bookmarkElement:this.bookmarks[index],bookmark:this.options.bookmarks[index]};return this._trigger(this.events.bookmarkClick,event,uiHash)},_bookmarkHit:function(event,index){var uiHash={bookmarkElement:this.bookmarks[index],bookmark:this.options.bookmarks[index]};if(uiHash.bookmarkElement){this._trigger(this.events.bookmarkHit,event,uiHash)}},value:function(newValue){if(arguments.length){this.options.value=this._trimValue(newValue);this._refreshValue();this._change(null,0)}return this._value()},_setOption:function(key,value){$.Widget.prototype._setOption.apply(this,arguments);switch(key){case"disabled":if(value){this.handles.filter(".ui-state-focus").blur();this.handles.removeClass("ui-state-hover");this.handles.attr("disabled","disabled");this.element.addClass("ui-disabled")}else{this.handles.removeAttr("disabled");this.element.removeClass("ui-disabled")}break;case"orientation":this._detectOrientation();this._refreshValue();break;case"value":if(this._mouseSliding===true){return}this._animateOff=true;this._refreshValue();this._change(null,0);this._animateOff=false;break;case"bookmarks":this.clearBookmarks();this._renderBookmarks();break}},_value:function(){return this._trimValue(this.options.value)},_trimValue:function(val){if(val<this.options.min){return this.options.min}if(val>this.options.max){return this.options.max}var step=this.options.step>0?this.options.step:1,valModStep=val%step,alignValue=val-valModStep;if(Math.abs(valModStep)*2>=step){alignValue+=valModStep>0?step:-step}return parseFloat(alignValue.toFixed(5))},_normPercentValue:function(val){var decrease=0,retVal=val;if(this.orientation==="vertical"){decrease=(this.handle.outerHeight()/this.element.outerHeight()).toFixed(2)*100}if(val-decrease>0){retVal=val-decrease}return retVal},_refreshValue:function(){var o=this.options,control=this,animate=!this._animateOff?o.animate:false,_set={},value=this.value(),valueMin=this.options.min,valueMax=this.options.max,valPercent=valueMax!==valueMin?(value-valueMin)/(valueMax-valueMin)*100:0;_set[control.orientation==="horizontal"?"left":"bottom"]=this._normPercentValue(valPercent)+"%";this.handle.stop(1,1)[animate?"animate":"css"](_set,o.animate)}});$.extend($.ui.igSlider,{version:"16.2.20162.2040"});$.widget("ui.igProgressBar",{options:{animate:false,animateTimeout:100,max:100,min:0,orientation:"horizontal",value:0,width:"0px",height:"0px",range:false,queue:true,endValue:100},css:{baseClasses:"ui-igprogressbar ui-widget ui-widget-content ui-corner-all",horizontalOrientationClass:"ui-igprogressbar-horizontal",verticalOrientationClass:"ui-igprogressbar-vertical",disabledClass:"ui-igprogressbar-disabled ui-disabled",progressRangeClass:"ui-igprogressbar-range ui-widget-header ui-corner-all"},events:{change:"change"},_animationOff:true,widget:function(){return this.element},_detectOrientation:function(){var css=this.css;if(this.options.orientation==="vertical"){this.orientation="vertical";this.element.removeClass(css.horizontalOrientationClass).addClass(css.verticalOrientationClass)}else{this.orientation="horizontal";this.element.removeClass(css.verticalOrientationClass).addClass(css.horizontalOrientationClass)}},_id:function(suffix){return this.element[0].id+suffix},_create:function(){var o=this.options,css=this.css;this._detectOrientation();this.element.addClass(css.baseClasses);if(o.disabled){this.element.addClass(css.disabledClass)}if(o.width!=="0px"){this.element.css("width",o.width)}if(o.height!=="0px"){this.element.css("height",o.height)}if(o.orientation==="horizontal"){$('<div id="'+this._id("_progress")+'" class="'+css.progressRangeClass+'" style="height:100%; top:0px; left:0%; width:0%;"></div>').appendTo(this.element)}else{$('<div id="'+this._id("_progress")+'" class="'+css.progressRangeClass+'" style="height:0%; bottom:0%; left:0px; width:100%;"></div>').appendTo(this.element)}this._refreshValue()},destroy:function(){this.element.removeClass("ui-igprogressbar"+" ui-igprogressbar-horizontal"+" ui-igprogressbar-vertical"+" ui-igprogressbar-disabled"+" ui-widget"+" ui-widget-content"+" ui-corner-all").removeData("igProgressBar").unbind(".igProgressBar");$("#"+this._id("_progress")).remove();return this},_change:function(event){var uiHash={value:this.value()};this._trigger(this.events.change,event,uiHash)},value:function(newValue){if(arguments.length){this.options.value=this._trimValue(newValue);this._refreshValue();this._change(null)}return this.options.value},_setOption:function(key,value){$.Widget.prototype._setOption.apply(this,arguments);var o=this.options;switch(key){case"disabled":if(value){this.element.addClass(this.css.disabledClass)}else{this.element.removeClass(this.css.disabledClass)}break;case"orientation":this._detectOrientation();this._refreshValue();break;case"value":this._animationOff=true;o.value=this._trimValue(value);this._refreshValue();this._change(null);this._animationOff=false;break;case"endValue":o.endValue=this._trimValue(value);this._refreshValue();break;case"max":if(o.endValue>o.max){o.endValue=o.max;this._refreshValue()}break;case"width":this.element.css("width",value);break;case"height":this.element.css("height",value);break;case"animate":o.animate=value;break;case"animateTimeout":o.animateTimeout=value;break;default:break}},_trimValue:function(val){if(val<this.options.min){return this.options.min}if(val>this.options.max){return this.options.max}return parseInt(val,10)},_refreshValue:function(){var o=this.options,additionalOptions={duration:o.animateTimeout,queue:o.queue},value=o.value,valueMin=o.min,valueMax=o.max,valueEnd=o.endValue,valPercent=valueMax!==valueMin?(value-valueMin)/(valueMax-valueMin)*100:0,valueEndPercent=value!==valueEnd?(valueEnd-value)/(valueMax-valueMin)*100:0,progressBar=$("#"+this._id("_progress"));if(o.range){if(o.orientation==="horizontal"){progressBar.css("left",valPercent+"%").css("width",valueEndPercent+"%")}else{progressBar.css("bottom",valPercent+"%").css("height",valueEndPercent+"%")}}else{if(o.animate===true){if(o.orientation==="horizontal"){if(progressBar[0].style.width==="0%"){progressBar.css({width:"0px"})}if(progressBar.is(":hidden")===false){progressBar.animate({width:valPercent+"%"},additionalOptions)}else{progressBar.css("width",valPercent+"%")}}else{if(progressBar[0].style.height==="0%"){progressBar.css({height:"0px"})}if(progressBar.is(":hidden")===false){progressBar.animate({height:valPercent+"%"},o.animateTimeout)}else{progressBar.animate({height:valPercent+"%"},o.animateTimeout)}}}else{if(o.orientation==="horizontal"){progressBar.css("width",valPercent+"%")}else{progressBar.css("height",valPercent+"%")}}}}});$.extend($.ui.igProgressBar,{version:"16.2.20162.2040"});$.widget("ui.igButton",{options:{width:null,height:null,link:{href:null,target:null,title:null},labelText:"",centerLabel:false,css:null,onlyIcons:false,icons:{primary:null,secondary:null},title:false},_id:function(suffix){return this.element[0].id+suffix},_create:function(){var self=this,o=self.options,e=this.element,inputType,css={buttonClassIE6:"ui-ie6",buttonClasses:"ui-button ui-igbutton ui-widget "+"ui-widget-content ui-corner-all ui-state-default",buttonHoverClasses:"ui-state-hover",buttonActiveClasses:"ui-state-active",buttonFocusClasses:"ui-state-focus",buttonLabelClass:"ui-button-text",buttonDisabledClass:"ui-state-disabled",buttonPrimaryIconClass:"ui-button-icon-primary ui-icon",buttonMainElementPrimaryIconClass:" ui-button-text-icon-primary",buttonMainElementSecondaryIconClass:" ui-button-text-icon-secondary",buttonSecondaryIconClass:"ui-button-icon-secondary ui-icon",buttonIconsOnly:"ui-button-icons-only",buttonIconOnly:"ui-button-icon-only",buttonIcons:"ui-button-text-icons",buttonTextOnlyClass:"ui-button-text-only"};this._attached=false;o.css=$.extend(css,o.css);self._getInitValues();if(e.is("div")){self._renderDivButton()}else if(e.is("a")){self._renderAHref()}else if(e.is("input")){inputType=e.attr("type").toUpperCase();if(inputType==="BUTTON"||inputType==="SUBMIT"||inputType==="RESET"){self._renderInput()}else{return}}else if(e.is("button")){self._renderButton()}else{return}if(o.width!==null){self._setWidth(o.width)}if(o.height!==null){self._setHeight(o.height)}e.addClass(o.css.buttonClasses).addClass(o.css.buttonDefaultClasses);if(o.centerLabel===true){self._centerLabel()}if(o.disabled){self._disableButton()}else{self._enableButton()}self._setOnlyIcons();e.attr("role","button");e.attr("aria-disabled","false");self._attachButtonEvents();self.setTitle(o.title);return this},setTitle:function(title){var e=this.element;if(title===false){e.removeAttr("title")}else{e.attr("title",title)}},widget:function(){return this.element},_isRedirect:function(){return this.options.link.href!==null},_renderDivButton:function(){this._setLabel()},_renderAHref:function(){this._setLabel()},_renderInput:function(){var e=this.element;e.attr("value",this.options.labelText)},_renderButton:function(){this._setLabel()},_disableButton:function(){var self=this,e=self.element;e.addClass(self.options.css.buttonDisabledClass);if(e.is("a")===true){e.removeAttr("href");e.removeAttr("target");e.removeAttr("title")}else{e.attr("disabled","true")}},_enableButton:function(){var self=this,e=self.element,o=this.options;if(e.hasClass(o.css.buttonDisabledClass)){e.removeClass(o.css.buttonDisabledClass)}e.removeAttr("disabled");if(e.is("a")===true){self._setLinkOptions(true)}},_setLabel:function(){var e=this.element,o=this.options,icons=o.icons,css=o.css,html="",isSetPrimary=this._isSetPrimaryIcon(),isSetSecondary=this._isSetSecondaryIcon(),labelText=o.labelText===null||o.labelText===""?e.text():o.labelText;e.attr("title",o.labelText);if(e.is("input")){e.attr("value",o.labelText);return}if(isSetPrimary){html+='<span class="'+css.buttonPrimaryIconClass+" "+icons.primary+'" id="'+this._id("_picn")+'"></span>'}html+='<span class="'+o.css.buttonLabelClass+'" id="'+this._id("_lbl")+'">'+labelText+"</span>";if(isSetSecondary){html+='<span class="'+css.buttonSecondaryIconClass+" "+icons.secondary+'" id="'+this._id("_sicn'")+'"></span>'}if(isSetPrimary&&isSetSecondary&&!o.onlyIcons){e.addClass(css.buttonIcons)}else if(o.onlyIcons){this._setOnlyIcons()}else if(isSetPrimary){e.addClass(css.buttonMainElementPrimaryIconClass)}else if(isSetSecondary){e.addClass(css.buttonMainElementSecondaryIconClass)}else{e.addClass(css.buttonTextOnlyClass)}e.html(html)},_setOnlyIcons:function(){var e=this.element,css=this.options.css;if(this.options.onlyIcons===false){if(e.hasClass(css.buttonIconsOnly)){e.removeClass(css.buttonIconsOnly)}if(e.hasClass(css.buttonIconOnly)){e.removeClass(css.buttonIconOnly)}if(this._isSetPrimaryIcon()&&this._isSetSecondaryIcon()){e.addClass(css.buttonIcons)}}else{if(this._isSetPrimaryIcon()&&this._isSetSecondaryIcon()){e.addClass(css.buttonIconsOnly)}else{e.addClass(css.buttonIconOnly)}}},_setLinkOptions:function(isAHref){var self=this,e=self.element,o=this.options;if(isAHref){if(o.link!==null&&o.link.href!==null){e.attr("href",o.link.href)}else if(e.attr("href")!==undefined){e.removeAttr("href")}if(o.link!==null&&o.link.target!==null){e.attr("target",o.link.target)}else if(e.attr("target")!==undefined){e.removeAttr("target")}if(o.link!==null&&o.link.title!==null){e.attr("title",o.link.title)}else if(e.attr("title")!==undefined){e.removeAttr("title")}}},_setWidth:function(value){var e=this.element;if(value===null){e.css("width","")}else{e.css("width",value)}},_setHeight:function(value){var e=this.element;if(value===null){e.css("height","")}else{e.css("height",value)}},_setOption:function(key,val){var e=this.element,self=this;$.Widget.prototype._setOption.apply(this,[key,val]);switch(key){case"width":self._setWidth(val);break;case"height":self._setHeight(val);break;case"link":self._setLinkOptions(e.is("a")===true);break;case"disabled":val=Boolean(val);if(val===true){self._disableButton()}else{self._enableButton()}break;case"labelText":self._setLabel();break;case"centerLabel":val=Boolean(val);if(val){self._centerLabel()}else{self._removeCenterLabel()}break;case"onlyIcons":val=Boolean(val);self._setOnlyIcons();break;case"icons":self._setLabel();break;case"title":self.setTitle(val);break;default:break}},_centerLabel:function(){var self=this,e=self.element,l=$("#"+self._id("_lbl")),left=(e.width()-l.width())/2,top=(e.height()-l.height())/2;l.css({position:"relative",top:top+"px",left:left+"px"})},_removeCenterLabel:function(){var self=this,l=$("#"+self._id("_lbl"));if(l.length>0){l.css({position:"",top:"",left:""})}},_dettachEvents:function(){this._attached=false;this.element.unbind(this._events)},_onMouseOver:function(event){var noCancel;if(this.options.disabled===true){return}noCancel=this._trigger("mouseover",event);if(noCancel){this.element.addClass(this.options.css.buttonHoverClasses)}},_onMouseOut:function(event){var self=this,e=self.element,o=this.options,noCancel;if(o.disabled===true){return}noCancel=self._trigger("mouseout",event);if(noCancel){e.removeClass(o.css.buttonHoverClasses)}},_onClick:function(event){var e=this.element,self=this,o=this.options,noCancel;if(o.disabled===true){return}noCancel=self._trigger("click",event);if(noCancel){this.element.removeClass(o.css.buttonHoverClasses);if(self._isRedirect()===true&&e.is("a")===false){if(o.link.target==="_blank"){window.open(o.link.href)}else{window.location=o.link.href}}}},_onMouseDown:function(event){var e=this.element,o=this.options,noCancel;if(o.disabled===true){return}noCancel=this._trigger("mousedown",event);if(noCancel){e.removeClass(o.css.buttonHoverClasses);e.addClass(o.css.buttonActiveClasses)}},_onMouseUp:function(event){var o=this.options,noCancel;if(o.disabled===true){return}noCancel=this._trigger("mouseup",event);if(noCancel){this.element.removeClass(o.css.buttonHoverClasses);this.element.removeClass(o.css.buttonActiveClasses)}},_onFocus:function(event){var o=this.options,noCancel;if(o.disabled===true){return}noCancel=this._trigger("focus",event);if(noCancel){this.element.addClass(o.css.buttonFocusClasses)}},_onBlur:function(event){var o=this.options,noCancel;if(o.disabled===true){return}noCancel=this._trigger("blur",event);if(noCancel){this.element.removeClass(o.css.buttonActiveClasses);this.element.removeClass(o.css.buttonFocusClasses)}},_onKeyDown:function(event){var o=this.options;if(o.disabled===true){return}this._trigger("keydown",event)},_attachButtonEvents:function(){var self=this,e=self.element;if(this._attached===true){return}this._attached=true;this._events={mouseover:function(e){self._onMouseOver(e,self)},click:function(e){self._onClick(e)},mouseout:function(e){self._onMouseOut(e)},mousedown:function(e){self._onMouseDown(e)},mouseup:function(e){self._onMouseUp(e)},focus:function(e){self._onFocus(e)},blur:function(e){self._onBlur(e)},keydown:function(e){self._onKeyDown(e)}};e.bind(this._events)},_isSetPrimaryIcon:function(){var primary=this.options.icons.primary,isSet=false;if(primary!==undefined&&primary!==null){isSet=true}return isSet},_isSetSecondaryIcon:function(){var secondary=this.options.icons.secondary,isSet=false;if(secondary!==undefined&&secondary!==null){isSet=true}return isSet},destroy:function(){this._dettachEvents();this._rollbackInitValues();this._events=null;this._innerHTML=null;this._initialAttributes=null;$.Widget.prototype.destroy.apply(this,arguments)},_getInitValues:function(){var e=this.element,attr,i;this._innerHTML=e.html();this._initialAttributes=[];attr=e[0].attributes;for(i=0;i<attr.length;i++){if(attr[i].name!=="id"&&attr[i].name!=="type"){this._initialAttributes.push({name:attr[i].name,value:attr[i].value})}}},_rollbackInitValues:function(){var e=this.element,attr,a,i;if(this._innerHTML!==undefined){e.html(this._innerHTML)}attr=e[0].attributes;a=[];for(i=0;i<attr.length;i++){if(attr[i].name!=="id"&&attr[i].name!=="type"){a.push(attr[i].name)}}for(i=0;i<a.length;i++){e.removeAttr(a[i])}if(this._initialAttributes){for(i=0;i<this._initialAttributes.length;i++){if(this._initialAttributes[i].name!=="id"){e.attr(this._initialAttributes[i].name,this._initialAttributes[i].value)}}}}});$.extend($.ui.igButton,{version:"16.2.20162.2040"});$.widget("ui.igTooltip",{css:{baseClasses:"ui-widget ui-igpopover ui-igplayer-tooltip",arrowImageBaseClass:"ui-igpopover-arrow-"},options:{text:"",arrowLocation:"top"},_setOption:function(key,value){$.Widget.prototype._setOption.apply(this,arguments);switch(key){case"text":$("div.ui-widget-content",this.element).html(value);break;case"arrowLocation":$("div",this.element).remove();this._renderTooltip();break}},_create:function(){if(this.element.is("div")){this._renderTooltip();this.element.addClass(this.css.baseClasses)}},_renderTooltip:function(){switch(this.options.arrowLocation){case"top":this._createArrowDiv();this._createContentDiv();break;case"bottom":this._createContentDiv();this._createArrowDiv();break;case"left":break;case"right":break}},_createContentDiv:function(){var t=this.options.text&&this.options.text.length>0?this.options.text:"";$('<div class="ui-widget-content ui-corner-all">'+t+"</div>").appendTo(this.element)},_createArrowDiv:function(){$('<div class="'+this.css.arrowImageBaseClass+this.options.arrowLocation+'"></div>').appendTo(this.element)},destroy:function(){this.element.children().remove();this.element.removeClass(this.css.baseClasses)}});$.extend($.ui.igTooltip,{version:"16.2.20162.2040"});$.widget("ui.mouseWrapper",$.ui.mouse,{options:{cancel:":input,option",distance:1,delay:0},events:{start:"start",drag:"drag",stop:"stop",capture:"capture"},_create:function(){var self=this,originalMouseDown=this._mouseDown;if($.ig.util.isIE9){this._mouseDown=function(event){var originalPreventDefault=event.preventDefault,result;event.preventDefault=function(){};result=originalMouseDown.apply(self,[event]);event.preventDefault=originalPreventDefault;return result}}this._mouseInit()},destroy:function(){this._mouseDestroy();return this},_mouseStart:function(event){return this._trigger(this.events.start,event)},_mouseDrag:function(event){return this._trigger(this.events.drag,event)},_mouseStop:function(event){return this._trigger(this.events.stop,event)},_mouseCapture:function(event){return this._trigger(this.events.capture,event)}});$.widget("ui.igResponsiveContainer",{options:{pollingInterval:500},_createWidget:function(options,element){this._callbacks=[];this._element=element;this._pollerHandler=$.proxy(this._pollerTick,this);this._startPoller();this._width=this._getCurrentWidth();this._height=this._getCurrentHeight();$.Widget.prototype._createWidget.apply(this,arguments)},destroy:function(){this._stopPoller();this._callbacks=[];$.Widget.prototype.destroy.call(this);return this},startPoller:function(){this._startPoller()},stopPoller:function(){this._stopPoller()},removeCallback:function(callbackId){if(callbackId>=0&&callbackId<this._callbacks.length){this._callbacks.splice(callbackId,1)}},addCallback:function(callback,owner,reactionStep,reactionDirection){return this._callbacks.push({callback:callback,owner:owner,step:reactionStep,left:reactionDirection==="xy"||reactionDirection==="x",top:reactionDirection==="xy"||reactionDirection==="y",cWidth:this._getCurrentWidth(),cHeight:this._getCurrentHeight()})-1},_startPoller:function(){this._intervalId=setInterval(this._pollerHandler,this.options.pollingInterval)},_stopPoller:function(){if(this._intervalId){clearInterval(this._intervalId);delete this._intervalId}},_pollerTick:function(){var nw=this._getCurrentWidth(),nh=this._getCurrentHeight(),cb,i,called=false;for(i=0;i<this._callbacks.length;i++){cb=this._callbacks[i];if(cb.left===true){if(Math.abs(cb.cWidth-nw)>cb.step){cb.callback.apply(cb.owner,[nw,nh]);cb.cWidth=nw;cb.cHeight=nh;called=true}}if(cb.top===true&&called===false){if(Math.abs(cb.cHeight-nh)>cb.step){cb.callback.apply(cb.owner,[nw,nh]);cb.cWidth=nw;cb.cHeight=nh}}}this._width=nw;this._height=nh},_getCurrentWidth:function(){return this._element.offsetWidth},_getCurrentHeight:function(){return this._element.offsetHeight}});$.extend($.ui.igResponsiveContainer,{version:"16.2.20162.2040"})
});