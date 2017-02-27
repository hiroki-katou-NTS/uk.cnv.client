﻿/*!@license
 * Infragistics.Web.ClientUI BulletGraph 16.2.20162.2040
 *
 * Copyright (c) 2011-2016 Infragistics Inc.
 *
 * http://www.infragistics.com/
 *
 * Depends on:
 * jquery.js
 * jquery-ui.js
 * infragistics.util.js
 * infragistics.ext_core.js
 * infragistics.ext_collections.js
 * infragistics.ext_ui.js
 * infragistics.dv_core.js
 * infragistics.dv_geometry.js
 * infragistics.bulletgraph.js
 */
(function(factory){if(typeof define==="function"&&define.amd){define(["jquery","jquery-ui","./infragistics.util","./infragistics.bulletgraph"],factory)}else{factory(jQuery)}})(function($){$.widget("ui.igBulletGraph",{css:{unsupportedBrowserClass:"ui-html5-non-html5-supported-message ui-helper-clearfix ui-html5-non-html5",bulletGraph:"ui-bulletgraph",tooltip:"ui-bulletgraph-tooltip ui-corner-all",bulletGraphValue:"ui-bulletgraph-value",bulletGraphTargetValue:"ui-bulletgraph-targetvalue",bulletGraphBacking:"ui-bulletgraph-backing",bulletGraphTick:"ui-bulletgraph-tick",bulletGraphMinorTick:"ui-bulletgraph-minortick",bulletGraphLabel:"ui-bulletgraph-label",bulletGraphPalette:"ui-bulletgraph-range-palette-n",bulletGraphFillPalette:"ui-bulletgraph-range-fill-palette-n",bulletGraphOutlinePalette:"ui-bulletgraph-range-outline-palette-n"},events:{formatLabel:null,alignLabel:null},options:{width:null,height:null,ranges:[{name:null,brush:null,outline:null,startValue:0,endValue:0,innerStartExtent:0,innerEndExtent:0,outerStartExtent:0,outerEndExtent:0,strokeThickness:1}],rangeToolTipTemplate:null,valueToolTipTemplate:null,targetValueToolTipTemplate:null,orientation:"horizontal",rangeBrushes:null,rangeOutlines:null,minimumValue:0,maximumValue:100,targetValue:0,targetValueName:null,value:0,valueName:null,rangeInnerExtent:.2,rangeOuterExtent:.95,valueInnerExtent:.5,valueOuterExtent:.65,interval:0,ticksPostInitial:0,ticksPreTerminal:0,labelInterval:0,labelExtent:0,labelsPostInitial:0,labelsPreTerminal:0,minorTickCount:3,tickStartExtent:.1,tickEndExtent:.2,tickStrokeThickness:2,tickBrush:null,fontBrush:null,valueBrush:null,valueOutline:null,valueStrokeThickness:1,minorTickStartExtent:.06,minorTickEndExtent:.2,minorTickStrokeThickness:1,minorTickBrush:null,isScaleInverted:false,backingBrush:null,backingOutline:null,backingStrokeThickness:2,backingInnerExtent:0,backingOuterExtent:1,scaleStartExtent:.05,scaleEndExtent:.95,targetValueBrush:null,targetValueBreadth:3,targetValueInnerExtent:.3,targetValueOuterExtent:.85,targetValueOutline:null,targetValueStrokeThickness:1,transitionDuration:0,showToolTipTimeout:0,showToolTip:false,font:null,pixelScalingRatio:0},_setOption:function(key,value,checkPrev){var bulletGraph=this._bulletGraph,o=this.options;if(checkPrev&&o[key]===value){return}$.Widget.prototype._setOption.apply(this,arguments);if(this._set_option(bulletGraph,key,value)){return this}this._set_generated_option(bulletGraph,key,value);return this},_set_generated_option:function(bulletGraph,key,value){switch(key){case"orientation":switch(value){case"horizontal":bulletGraph.orientation(0);break;case"vertical":bulletGraph.orientation(1);break}return true;case"rangeBrushes":var isRGB=true,val=value?value[0]:null;if(typeof val=="string"&&val=="HSV"||val=="RGB"){if(value[0]=="HSV"){isRGB=false}value=value.slice(1)}var $tempBrushCollection=new $.ig.BrushCollection;for(var i=0;value&&i<value.length;i++){var $tempBrush=$.ig.Brush.prototype.create(value[i]);$tempBrushCollection.add($tempBrush)}bulletGraph.rangeBrushes($tempBrushCollection);return true;case"rangeOutlines":var isRGB=true,val=value?value[0]:null;if(typeof val=="string"&&val=="HSV"||val=="RGB"){if(value[0]=="HSV"){isRGB=false}value=value.slice(1)}var $tempBrushCollection=new $.ig.BrushCollection;for(var i=0;value&&i<value.length;i++){var $tempBrush=$.ig.Brush.prototype.create(value[i]);$tempBrushCollection.add($tempBrush)}bulletGraph.rangeOutlines($tempBrushCollection);return true;case"minimumValue":bulletGraph.minimumValue(value);return true;case"maximumValue":bulletGraph.maximumValue(value);return true;case"targetValue":bulletGraph.targetValue(value);return true;case"targetValueName":bulletGraph.targetValueName(value);return true;case"value":bulletGraph.value(value);return true;case"valueName":bulletGraph.valueName(value);return true;case"rangeInnerExtent":bulletGraph.rangeInnerExtent(value);return true;case"rangeOuterExtent":bulletGraph.rangeOuterExtent(value);return true;case"valueInnerExtent":bulletGraph.valueInnerExtent(value);return true;case"valueOuterExtent":bulletGraph.valueOuterExtent(value);return true;case"interval":bulletGraph.interval(value);return true;case"ticksPostInitial":bulletGraph.ticksPostInitial(value);return true;case"ticksPreTerminal":bulletGraph.ticksPreTerminal(value);return true;case"labelInterval":bulletGraph.labelInterval(value);return true;case"labelExtent":bulletGraph.labelExtent(value);return true;case"labelsPostInitial":bulletGraph.labelsPostInitial(value);return true;case"labelsPreTerminal":bulletGraph.labelsPreTerminal(value);return true;case"minorTickCount":bulletGraph.minorTickCount(value);return true;case"tickStartExtent":bulletGraph.tickStartExtent(value);return true;case"tickEndExtent":bulletGraph.tickEndExtent(value);return true;case"tickStrokeThickness":bulletGraph.tickStrokeThickness(value);return true;case"tickBrush":if(value==null){bulletGraph.tickBrush(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.tickBrush($tempBrush)}return true;case"fontBrush":if(value==null){bulletGraph.fontBrush(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.fontBrush($tempBrush)}return true;case"valueBrush":if(value==null){bulletGraph.valueBrush(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.valueBrush($tempBrush)}return true;case"valueOutline":if(value==null){bulletGraph.valueOutline(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.valueOutline($tempBrush)}return true;case"valueStrokeThickness":bulletGraph.valueStrokeThickness(value);return true;case"minorTickStartExtent":bulletGraph.minorTickStartExtent(value);return true;case"minorTickEndExtent":bulletGraph.minorTickEndExtent(value);return true;case"minorTickStrokeThickness":bulletGraph.minorTickStrokeThickness(value);return true;case"minorTickBrush":if(value==null){bulletGraph.minorTickBrush(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.minorTickBrush($tempBrush)}return true;case"isScaleInverted":bulletGraph.isScaleInverted(value);return true;case"backingBrush":if(value==null){bulletGraph.backingBrush(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.backingBrush($tempBrush)}return true;case"backingOutline":if(value==null){bulletGraph.backingOutline(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.backingOutline($tempBrush)}return true;case"backingStrokeThickness":bulletGraph.backingStrokeThickness(value);return true;case"backingInnerExtent":bulletGraph.backingInnerExtent(value);return true;case"backingOuterExtent":bulletGraph.backingOuterExtent(value);return true;case"scaleStartExtent":bulletGraph.scaleStartExtent(value);return true;case"scaleEndExtent":bulletGraph.scaleEndExtent(value);return true;case"targetValueBrush":if(value==null){bulletGraph.targetValueBrush(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.targetValueBrush($tempBrush)}return true;case"targetValueBreadth":bulletGraph.targetValueBreadth(value);return true;case"targetValueInnerExtent":bulletGraph.targetValueInnerExtent(value);return true;case"targetValueOuterExtent":bulletGraph.targetValueOuterExtent(value);return true;case"targetValueOutline":if(value==null){bulletGraph.targetValueOutline(null)}else{var $tempBrush=$.ig.Brush.prototype.create(value);bulletGraph.targetValueOutline($tempBrush)}return true;case"targetValueStrokeThickness":bulletGraph.targetValueStrokeThickness(value);return true;case"transitionDuration":bulletGraph.transitionDuration(value);return true;case"showToolTipTimeout":bulletGraph.showToolTipTimeout(value);return true;case"showToolTip":bulletGraph.showToolTip(value);return true;case"font":bulletGraph.font(value);return true;case"pixelScalingRatio":bulletGraph.pixelScalingRatio(value);return true}},_set_option:function(bulletGraph,key,value){var currentKey,templ;switch(key){case"formatLabel":case"alignLabel":bulletGraph.refresh();return true;case"ranges":var count=value.length;for(i=0;i<count;i++){if(!value[i].name){throw new Error("Range name is missing for range: "+i)}if(this._rangesColl.hasOwnProperty(value[i].name)){this._removeCollValue(bulletGraph.ranges(),this._rangesColl,value[i]);this._updateCollValue(bulletGraph.ranges(),this._rangesColl,value[i],this._setRangeOption)}else this._addCollValue(bulletGraph.ranges(),this._rangesColl,value[i],this._setRangeOption,function(){return new $.ig.XamLinearGraphRange})}return true;case"width":this._setSize(bulletGraph,"width",value);return true;case"height":this._setSize(bulletGraph,"height",value);return true;case"rangeToolTipTemplate":this._tooltipTemplates["range"]=this._resolveTemplate(value);return true;case"valueToolTipTemplate":this._tooltipTemplates["value"]=this._resolveTemplate(value);return true;case"targetValueToolTipTemplate":this._tooltipTemplates["targetvalue"]=this._resolveTemplate(value);return true;case"showToolTip":if(value===true){var tooltip=$("<div class='"+this.css.tooltip+"' style='white-space: nowrap; z-index: 10000;'></div>");this._addTooltip(bulletGraph,tooltip,"range")}if(value===false){this._removeTooltip(bulletGraph)}bulletGraph.showToolTip(value);return true}},_resolveTemplate:function(value){var templ;if($.ig.tmpl){if(this._htmlCheckExpr.test(value)){templ=value}else{if($("#"+value).length>0){templ=$("#"+value).text()}else if($(value).length>0){templ=$(value).text()}else{templ=value}}return templ}},_htmlCheckExpr:/^[^<]*(<[\w\W]+>)[^>]*$/,_addTooltip:function(bulletGraph,tooltip,name){this._removeTooltipEvents(bulletGraph,tooltip);this._bindTooltipEvents(bulletGraph,tooltip);bulletGraph.toolTip(tooltip)},_removeTooltip:function(bulletGraph,name){this._removeTooltipEvents(bulletGraph,bulletGraph.toolTip());bulletGraph.toolTip(null)},_bindTooltipEvents:function(chart,tooltip){tooltip.updateToolTip=$.ig.Delegate.prototype.combine(tooltip.updateToolTip,jQuery.proxy(this._fireToolTip_updateToolTip,this));tooltip.hideToolTip=$.ig.Delegate.prototype.combine(tooltip.hideToolTip,jQuery.proxy(this._fireToolTip_hideToolTip,this))},_removeTooltipEvents:function(chart,tooltip){delete tooltip.updateToolTip;delete tooltip.hideToolTip},_resolveTemplateContext:function(args,name){var e={};e.itemName=args.itemName();e.itemBrush=args.brush().fill();e.outline=args.outline().fill();e.thickness=args.thickness();e.label=args.label();switch(name){case"range":e.item={name:args.item().name(),startValue:args.item().startValue(),endValue:args.item().endValue()};break;case"value":e.item={name:e.itemName,value:args.item()};break;case"targetvalue":e.item={name:e.itemName,value:args.item()};break}return e},_fireToolTip_updateToolTip:function(args,name){var e,noCancel=true,template;template=this._tooltipTemplates[name];if(template===undefined){template=this._tooltipDefaultTemplates[name];this._tooltipTemplates[name]=template}this._bulletGraph.toolTip().children().remove();e=this._resolveTemplateContext(args,name);if(e.item===null){noCancel=false}if(noCancel){var templ=$.ig.tmpl(template,e);this._bulletGraph.toolTip().html(templ)}},getRangeNames:function(){var rangeNames="";for(var key in this._rangesColl){rangeNames+=key+"\n"}return rangeNames},addRange:function(value){this._addCollValue(this._bulletGraph.ranges(),this._rangesColl,value,this._setRangeOption,function(){return new $.ig.XamLinearGraphRange})},_addCollValue:function(target,source,value,setItemOptionCallback,createItemCallback){if(!value||value.remove==true||!source)return;if(!source.hasOwnProperty(value.name)){var range=createItemCallback();for(currentKey in value){if(value.hasOwnProperty(currentKey)){setItemOptionCallback(range,currentKey,value[currentKey])}}source[value.name]=range;target.add(range)}},removeRange:function(value){this._removeCollValue(this._bulletGraph.ranges(),this._rangesColl,value)},_removeCollValue:function(target,source,value){if(!value||!source)return;if(source.hasOwnProperty(value.name)){var range=source[value.name];if(range&&value.remove==true){delete source[value.name];if(target.contains(range))target.remove(range)}}},updateRange:function(value){this._updateCollValue(this._bulletGraph.ranges(),this._rangesColl,value,this._setRangeOption)},_updateCollValue:function(target,source,value,setItemOptionCallBack){if(!value||!source)return;if(source.hasOwnProperty(value.name)){var range=source[value.name];if(range&&!value.remove){for(currentKey in value){if(value.hasOwnProperty(currentKey)){setItemOptionCallBack(range,currentKey,value[currentKey])}}source[value.name]=range}}},_creationOptions:null,_bulletGraph:null,_bulletGraphId:null,_createWidget:function(options,element,widget){this._creationOptions=options;$.Widget.prototype._createWidget.apply(this,[options,element])},_create:function(){var key,v,size,bulletGraph,width,height,i=-1,self=this,elem=self.element,style=elem[0].style,o=this._creationOptions;self._old_state={style:{position:style.position,width:style.width,height:style.height},css:elem[0].className,elems:elem.find("*")};if(!$.ig.util._isCanvasSupported()){$.ig.util._renderUnsupportedBrowser(this);return}bulletGraph=this._createBulletGraph();self._bulletGraph=bulletGraph;bulletGraph.formatLabel=$.ig.Delegate.prototype.combine(bulletGraph.formatLabel,jQuery.proxy(this._fireBulletGraph_formatLabel,this));bulletGraph.alignLabel=$.ig.Delegate.prototype.combine(bulletGraph.alignLabel,jQuery.proxy(this._fireBulletGraph_alignLabel,this));this._bulletGraphId=Date.now();this._tooltipTemplates={};this._rangesColl={};this._tooltipDefaultTemplates={};this._tooltipDefaultTemplates["range"]="<div class='ui-bulletgraph-range-tooltip' style='border-color: ${itemBrush};'><span>${label}</span></div>";this._tooltipDefaultTemplates["value"]="<div class='ui-bulletgraph-value-tooltip' style='border-color: ${itemBrush};'><span>${label}</span></div>";this._tooltipDefaultTemplates["targetvalue"]="<div class='ui-bulletgraph-targetvalue-tooltip' style='border-color: ${itemBrush};'><span>${label}</span></div>";if(o.hasOwnProperty("width"))elem[0].style.width=o["width"];if(o.hasOwnProperty("height"))elem[0].style.height=o["height"];bulletGraph.provideContainer(elem[0]);elem.mousemove(function(eventData){var point=$.ig.APIFactory.prototype.createPoint(eventData.pageX,eventData.pageY);bulletGraph.onMouseOver(point,true)});elem.mouseleave(function(){bulletGraph.onMouseLeave()});for(key in o){if(o.hasOwnProperty(key)){v=o[key];if(v!==null){this._setOption(key,v,false)}}}while(i++<1){key=i===0?"width":"height";if(o[key]){size=key;v=o[key]}else{v=elem[0].style[key]}if(v&&typeof v==="string"&&v.indexOf("%")>0){self._setSize(bulletGraph,size=key,v)}}if(!size){self._setSize(bulletGraph,"width")}if(self.css&&self.css.bulletGraph){elem.addClass(self.css.bulletGraph)}},_createBulletGraph:function(){return new $.ig.XamBulletGraph},_fireBulletGraph_formatLabel:function(bulletGraph,evt){var opts={};opts.actualMinimumValue=evt.actualMinimumValue;opts.actualMaximumValue=evt.actualMaximumValue;opts.value=evt.value;opts.label=evt.label;opts.owner=this;var event=this.options["formatLabel"];if(event!==undefined)this._trigger("formatLabel",null,opts);else opts.label=Math.round(opts.value*100)/100;evt.value=opts.value;evt.label=opts.label},_fireBulletGraph_alignLabel:function(bulletGraph,evt){var opts={};opts.actualMinimumValue=evt.actualMinimumValue;opts.actualMaximumValue=evt.actualMaximumValue;opts.value=evt.value;opts.label=evt.label;opts.width=evt.width;opts.height=evt.height;opts.offsetX=evt.offsetX;opts.offsetY=evt.offsetY;opts.owner=this;this._trigger("alignLabel",null,opts);evt.value=opts.value;evt.label=opts.label;evt.offsetX=opts.offsetX;evt.offsetY=opts.offsetY;evt.width=opts.width;evt.height=opts.height},_setSize:function(bulletGraph,key,val){$.ig.util.setSize(this.element,key,val,bulletGraph,this._getNotifyResizeName())},_getNotifyResizeName:function(){return"containerResized"},_setRangeOption:function(range,key,value){switch(key){case"name":range.name(value);break;case"brush":range.brush($.ig.Brush.prototype.create(value));break;case"outline":range.outline($.ig.Brush.prototype.create(value));break;case"startValue":range.startValue(value);break;case"endValue":range.endValue(value);break;case"innerStartExtent":range.innerStartExtent(value);break;case"innerEndExtent":range.innerEndExtent(value);break;case"outerStartExtent":range.outerStartExtent(value);break;case"outerEndExtent":range.outerEndExtent(value);break;case"strokeThickness":range.strokeThickness(value);break}},exportVisualData:function(){if(this._bulletGraph)return this._bulletGraph.exportVisualData()},flush:function(){if(this._bulletGraph&&this._bulletGraph.view())this._bulletGraph.view().flush()},destroy:function(){var key,style,bulletGraph=this._bulletGraph,old=this._old_state,elem=this.element;if(!old){return}elem.find("*").not(old.elems).remove();if(this.css.bulletGraph){elem.removeClass(this.css.bulletGraph)}old=old.style;style=elem[0].style;for(key in old){if(old.hasOwnProperty(key)){if(style[key]!==old[key]){style[key]=old[key]}}}if(bulletGraph){this._setSize(bulletGraph)}elem.off("mousemove");elem.off("mouseleave");$.Widget.prototype.destroy.apply(this,arguments);if(bulletGraph&&bulletGraph.destroy){bulletGraph.destroy()}delete this._bulletGraph;delete this._old_state},styleUpdated:function(){if(this._bulletGraph){this._bulletGraph.styleUpdated()}}});$.extend($.ui.igBulletGraph,{version:"16.2.20162.2040"});return $.ui.igBulletGraph});