﻿/*!@license
 * Infragistics.Web.ClientUI Grid Column Resizing 16.2.20162.2040
 *
 * Copyright (c) 2011-2016 Infragistics Inc.
 *
 * http://www.infragistics.com/
 *
 * Depends on:
 *	jquery-1.9.1.js
 *	jquery.ui.core.js
 *	jquery.ui.widget.js
 *	infragistics.ui.grid.framework.js
 *	infragistics.ui.shared.js
 *	infragistics.dataSource.js
 *	infragistics.util.js
 *	infragistics.ui.grid.shared.js
 *	infragistics.ui.grid.featurechooser.js
 */
(function(factory){if(typeof define==="function"&&define.amd){define(["jquery","jquery-ui","./infragistics.util","./infragistics.ui.grid.framework","./infragistics.ui.grid.featurechooser"],factory)}else{factory(jQuery)}})(function($){$.widget("ui.igGridResizing",{options:{allowDoubleClickToResize:true,deferredResizing:false,columnSettings:[{columnKey:null,columnIndex:null,allowResizing:true,minimumWidth:20,maximumWidth:null}],handleThreshold:5,inherit:false},css:{columnResizeLine:"ui-iggrid-resize-line",resizingHandleCursor:"ui-iggrid-resizing-handle-cursor",resizingHandle:"ui-iggrid-resizing-handle"},events:{columnResizing:"columnResizing",columnResizingRefused:"columnResizingRefused",columnResized:"columnResized"},_createWidget:function(){this.options.columnSettings=[];$.Widget.prototype._createWidget.apply(this,arguments)},_setOption:function(key){throw new Error($.ig.Grid.locale.optionChangeNotSupported.replace("{optionName}",key))},destroy:function(){this._clearResizingHandles();this.grid.element.unbind(".resizing");$.Widget.prototype.destroy.call(this);return this},resize:function(column,width){var columnIndex,gridWidth;if(typeof column==="number"){columnIndex=column}else{$.each(this.grid._visibleColumns(),function(index,col){if(col.key===column){columnIndex=index}});if(columnIndex===undefined){throw new Error($.ig.GridResizing.locale.noSuchVisibleColumn)}}if(width===undefined||width===null||width==="*"){this._autoResize(columnIndex,false,null)}else{if($.type(width)==="string"&&width.indexOf("%")>0){gridWidth=this.grid.element.width();width=parseInt(width,10)*gridWidth/100}this._resizeColumn(columnIndex,width,false,null)}},_headerRendered:function(){if(this.grid.element.igGridColumnFixing!==undefined){this._columnFixing=this.grid.element.data("igGridColumnFixing")}this._renderResizingHandles()},_columnsCollectionModified:function(){this._renderResizingHandles()},_columnsMoved:function(e,args){var ths,grid=this.grid,len=args.len,startIndex=args.start,endIndex=args.index,ind,after=endIndex-startIndex>0,$th;if(args.owner.id()!==this.grid.id()||!startIndex&&!len&&!endIndex){return}this._populateMultiColumnHeadersLevel0();if(after){endIndex-=len}if(grid._isMultiColumnGrid){ths=$(grid._headerCells)}else{if(grid.options.virtualization===true||grid.options.rowVirtualization===true){ths=grid.headersTable().find("> thead > tr").eq(0).children("th").not("[data-skip=true]")}else{ths=grid.headersTable().find("> thead > tr[data-header-row]").eq(0).children("th").not("[data-skip=true]")}}function funcCalibrateResizingHandle(ind){if(ths&&ths[ind]){$th=$(ths[ind]);$th.find('span[data-nonpaddedindicator="right"]').css("margin-right",-parseInt($th.css("padding-right"),10)+"px")}}if(after){ind=endIndex-1}else{ind=startIndex+len-1}funcCalibrateResizingHandle(ind);funcCalibrateResizingHandle(endIndex+len-1)},_hiddenColumnIndicatorsRendered:function(ths){if(this.grid._isMultiColumnGrid&&ths.length){ths=ths[0].closest("thead").find(">tr>th").not("[data-skip=true]")}ths.find('span[data-nonpaddedindicator="right"]').each(function(ind,span){var $span=$(span),$th=$span.closest("th");$span.css("margin-right",-parseInt($th.css("padding-right"),10)+"px")})},_fixedColumnsChanged:function(){this._renderResizingHandles()},_renderResizingHandles:function(){var self=this,i,ths,visibleColumns=this.grid._visibleColumns(),thsMultiHeader,gridId=this.grid.element.attr("id"),hasFixedColumns=this.grid.hasFixedColumns();this._clearResizingHandles();if(visibleColumns.length===0){return}if(this.grid._isMultiColumnGrid){thsMultiHeader=this.grid.headersTable().find("> thead > tr th").not("[data-skip=true]").not("[data-isheadercell=true]");if(hasFixedColumns){$.each(this.grid.fixedHeadersTable().find("> thead > tr th").not("[data-skip=true]").not("[data-isheadercell=true]"),function(index,th){thsMultiHeader.push($(th))})}this._populateMultiColumnHeadersLevel0();thsMultiHeader.each(function(){var a,th=$(this),mchId=th.data("mchId"),i,cs=self._oColumnSettings;for(i=0;i<cs.length;i++){if(cs[i].columnKey===mchId&&cs[i].allowResizing===false){return true}}a=self._renderResizingHandle(th);self._bindMultipleResizingHandle(th,a.find("span"))});ths=$(this.grid._headerCells)}else{if(this.grid.options.virtualization===true||this.grid.options.rowVirtualization===true){if(hasFixedColumns){ths=$();for(i=0;i<visibleColumns.length;i++){ths.push($("#"+gridId+"_"+visibleColumns[i].key))}}else{ths=this.grid.headersTable().find("> thead > tr").eq(0).children("th").not("[data-skip=true]")}}else{if(hasFixedColumns){ths=$();for(i=0;i<visibleColumns.length;i++){ths.push($("#"+gridId+"_"+visibleColumns[i].key))}}else{ths=this.grid.headersTable().find("> thead > tr[data-header-row]").eq(0).children("th").not("[data-skip=true]")}}}ths.each(function(index){var a,cs,th=$(this),col=visibleColumns[index];if(col===null||col===undefined){return true}cs=self._findColumnSettingsByKey(col.key);if(cs.allowResizing){a=self._renderResizingHandle(th);self._bindResizingHandle(th,a.find("span"),col)}})},_renderResizingHandle:function(th){var div,a;if(!this._resizingHandles){this._resizingHandles=[]}div=$('<div data-resizinghandle="true" />').css("position","relative").css("width","100%").css("height","0px").css("top","0px").css("left","0px").prependTo(th);a=$("<a />").attr("title","").prependTo(div);if($.ig.util.isIE9){a.addClass(this.css.resizingHandleCursor)}$('<span data-nonpaddedindicator="right"></span>').attr("title","").css("position","absolute").css("margin-right",-parseInt(th.css("padding-right"),10)+"px").css("right","0px").css("width",this.options.handleThreshold+"px").addClass(this.css.resizingHandleCursor).addClass(this.css.resizingHandle).appendTo(a);this._resizingHandles.push(div);return a},_populateMultiColumnHeadersLevel0:function(){var i,j,self=this,cols=this.grid._oldCols||[],colsLength=cols.length,ths=$(this.grid._headerCells),level0=[],level0Length,visibleColumns=this.grid._visibleColumns();for(i=0;i<colsLength;i++){if(cols[i].level===0){level0.push(cols[i])}else{for(j=0;j<cols[i].children.length;j++){level0.push(cols[i].children[j])}}}level0Length=level0.length;ths.each(function(index){var th=$(this),col=visibleColumns[index],cs;if(col===null||col===undefined){return true}cs=self._findColumnSettingsByKey(col.key);if(cs.allowResizing){for(i=0;i<level0.length;i++){if(level0[i].key===col.key){break}}if(i<level0Length){level0[i].allowResizing=true;level0[i].visibleIndex=index;level0[i].settings=cs}th.col=level0[i]}})},_bindMultipleResizingHandle:function($th,button){var self=this,id=$th.attr("data-mch-id"),column;column=this.grid._getMultiHeaderColumnById(id);button.mouseWrapper({distance:5,start:function(){return self._startResizing($th)},drag:function(event){return self._doResizingMultiColumnHeader(event.originalEvent,$th,column)},stop:function(event){return self._stopResizingMiltiColumnHeader(event.originalEvent,$th,column)}}).bind("dblclick.resizing",function(event){self._handleMouseMultiColumnHeaderDbClick(event,$th,column)}).bind("click.resizing",function(event){event.preventDefault();event.stopPropagation()})},_bindResizingHandle:function(th,button,column){var self=this;button.mouseWrapper({distance:5,start:function(){return self._startResizing(th)},drag:function(event){return self._doResizing(event.originalEvent,th,column)},stop:function(event){return self._stopResizing(event.originalEvent,th,column)}}).bind("dblclick.resizing",function(event){self._handleMouseDbClick(event,th,column)}).bind("click.resizing",function(event){event.preventDefault();event.stopPropagation()})},_clearResizingHandles:function(){var i;if(this._resizingHandles){for(i=0;i<this._resizingHandles.length;i++){this._resizingHandles[i].remove()}this._resizingHandles=[]}},_handleMouseDbClick:function(event,th,column){if(!this.options.allowDoubleClickToResize){return}this._autoResize($.inArray(column,this.grid._visibleColumns()),true,event)},_handleMouseMultiColumnHeaderDbClick:function(event,$th,column){var i,children=column.children,childrenLength=children.length;if(!this.options.allowDoubleClickToResize){return}for(i=0;i<childrenLength;i++){if(children[i].allowResizing===true){this._autoResize(children[i].visibleIndex,true,event)}}},_autoResize:function(columnIndex,fireEvents,event){var maxWidth=this.grid.calculateAutoFitColumnWidth(columnIndex);if(maxWidth>-1){return this._resizeColumn(columnIndex,maxWidth,fireEvents,event)}},_cancelHoveringEffects:function(cancel){var topmostGrid=this.grid.element.closest(".ui-iggrid-root").data("igGrid");if(topmostGrid===undefined||topmostGrid===null){topmostGrid=this.grid}topmostGrid._cancelHoveringEffects=cancel},_startResizing:function(th){var body=$(document.body),resizeLineTop,heightOffset;this._resizing=true;this._cancelHoveringEffects(true);if(!$.ig.util.isIE10){$(document.activeElement).blur()}else{body.focus()}body.addClass(this.css.resizingHandleCursor);if(this.grid._isMultiColumnGrid){heightOffset=th.offset().top-this._gridContainerPositioningOffset().top}resizeLineTop=th.offset().top+this._gridContainerPositioningOffset().top;if(this.grid.options.height!==null&&this.grid.options.showHeader===true&&this.grid.options.fixedHeaders===false){resizeLineTop+=this.grid.scrollContainer().scrollTop()}this._resizeLine=$("<div></div>").addClass(this.css.columnResizeLine).data("efh","1").css({height:this._calculateGridResizableHeight(heightOffset),top:resizeLineTop,visibility:"hidden"}).appendTo(this.grid.container());if($.ig.util.isIE8){this.grid.container().attr("tabIndex",this.grid.container().attr("tabIndex"))}return true},_doResizingMultiColumnHeader:function(event,$th,column){var i,width,resizeCellRange,resizeResult,range,vI,offsetLeft,offsetHeight,children=column.children,childrenLength=children.length,child=null,cellToResize;width=event.pageX-$th.offset().left;if(width<0){width=0}offsetHeight=$th.offset().top-this.grid.headersTable().offset().top;if(!this.options.deferredResizing){for(i=childrenLength-1;i>=0;i--){if(children[i].allowResizing&&!children[i].hidden){child=children[i];child.resized=!child.resized;if(child.resized){break}}}if(child!==null&&!child.hidden){cellToResize=$("#"+this.grid.element[0].id+"_"+child.key);vI=$.inArray(child,this.grid._visibleColumns());width=cellToResize[0].offsetWidth-$th[0].offsetWidth+width;resizeResult=this._resizeColumn(vI,width,true,event,vI)}if(!resizeResult){return true}if(!resizeResult.canceled){this._resizeLine.css("height",this._calculateGridResizableHeight(offsetHeight));this._resizeLine.css({left:$th.offset().left+$th.outerWidth()+this._gridContainerPositioningOffset().left,visibility:"visible"})}}else{offsetLeft=$th.offset().left;range={min:offsetLeft,max:offsetLeft};for(i=0;i<childrenLength;i++){child=children[i];if(child.hidden){continue}if(child.allowResizing){resizeCellRange=this._getRange(this.options.columnSettings[child.visibleIndex]);range.min+=resizeCellRange.min;if(resizeCellRange.max===Infinity){range.max=Infinity}else if(range.max!==Infinity){range.max+=resizeCellRange.max}}else{range.min+=$("#"+this.grid.element[0].id+"_"+child.key)[0].offsetWidth}}this._resizeLine.css("height",this._calculateGridResizableHeight(offsetHeight));this._resizeLine.css({left:this._coerceRange(range,event.pageX)+this._gridContainerPositioningOffset().left,visibility:"visible"})}return true},_doResizing:function(event,th,column){var width,resizeResult,columnIndex=$.inArray(column,this.grid._visibleColumns()),range,offsetLeft,offsetHeight;if(!this.options.deferredResizing){width=event.pageX-th.offset().left;if(width<0){width=0}if(this.grid._isMultiColumnGrid){offsetHeight=th.offset().top-this.grid.headersTable().offset().top}resizeResult=this._resizeColumn(columnIndex,width,true,event);if(!resizeResult.canceled){this._resizeLine.css("height",this._calculateGridResizableHeight(offsetHeight));this._resizeLine.css({left:th.offset().left+th.outerWidth()+this._gridContainerPositioningOffset().left,visibility:"visible"})}}else{range=this._getRange(this.options.columnSettings[columnIndex]);offsetLeft=th.offset().left;range.min+=offsetLeft;range.max+=offsetLeft;this._resizeLine.css({left:this._coerceRange(range,event.pageX)+this._gridContainerPositioningOffset().left,visibility:"visible"})}return true},_stopResizingMiltiColumnHeader:function(event,th,column){var i,width,cs,columnKey,self=this,children,childrenLength,childrenLengthAllowResizing=0,childrenToResize;if(this.options.deferredResizing){width=event.pageX-th.offset().left;children=column.children;childrenLength=children.length;childrenToResize=[];for(i=0;i<childrenLength;i++){columnKey=children[i].key;cs=this._findColumnSettingsByKey(columnKey);if(children[i].allowResizing===false||cs&&cs.allowResizing===false){width-=$("#"+this.grid.element[0].id+"_"+columnKey)[0].offsetWidth;continue}childrenToResize.push(children[i]);childrenLengthAllowResizing++}if(childrenLengthAllowResizing>0){this._resizeMCHDeffered(width,childrenToResize)}}$("body").removeClass(this.css.resizingHandleCursor);setTimeout(function(){self._resizing=false},0);this._cancelHoveringEffects(false);this._resizeLine.remove();this._resizeLine=undefined;return true},_resizeMCHDeffered:function(width,columns){var i,column,columnIndex,columnsLength=columns.length,visibleColumns=[],gridVC=this.grid._visibleColumns(),resizeInfo,newWidth=width,newColumnsToResize=[],avgWidth;if(columnsLength===0){return}for(i=0;i<columnsLength;i++){columnIndex=$.inArray(columns[i],gridVC);if(columnIndex===-1||this.options.columnSettings[columnIndex].allowResizing===false){continue}visibleColumns.push({column:column,columnIndex:columnIndex})}columnsLength=visibleColumns.length;if(columnsLength>0){avgWidth=parseInt(width/columnsLength,10);for(i=0;i<columnsLength;i++){column=visibleColumns[i].column;columnIndex=visibleColumns[i].columnIndex;resizeInfo=this._resizeColumn(columnIndex,avgWidth,true);if(resizeInfo.newWidth!==avgWidth){newWidth-=resizeInfo.newWidth}else{newColumnsToResize.push(column)}}}if(newWidth>5&&newWidth!==width&&newColumnsToResize.length>0){this._resizeMCHDeffered(newWidth,newColumnsToResize)}},_stopResizing:function(event,th,column){var width,self=this,columnIndex=$.inArray(column,this.grid._visibleColumns());if(this.options.deferredResizing){width=event.pageX-th.offset().left;width=this._coerceRange(this._getRange(this.options.columnSettings[columnIndex]),width);this._resizeColumn(columnIndex,width,true,event)}$("body").removeClass(this.css.resizingHandleCursor);setTimeout(function(){self._resizing=false},0);this._cancelHoveringEffects(false);this._resizeLine.remove();this._resizeLine=undefined;return true},_resizeColumn:function(columnIndex,width,fireEvents,originalEvent,startIndex){var gridWidth,minimalVisibleAreaWidth=0,widthFixedContainer,visibleColumns=this.grid._visibleColumns(),columnKey=visibleColumns[columnIndex].key,typeW,hasWidthPx,w,tmpW,visibleIndex=columnIndex,headersTable=this.grid.options.showHeader?this.grid.headersTable():this.grid.element,cs,isFixed,headers,headerWidth,headerColumns,columnSettings=this.options.columnSettings,hasFixedCols=this.grid.hasFixedColumns(),columnsLength,headerColStyleWidth,hasPercentageWidth,actualColumnStyleWidths=[],actualColumnWidths=[],requiredColumnPercentageWidths=[],newColumnStyleWidths=[],i,widthToDistribute,shrinkColumns,widthDistributed,widthUsed,coercedWidth,widthPerColumn,range,totalWidth,readyColumns,readyColumnsCount,finalPixelWidth,allColumnsHaveWidth,noCancel,containerWidth,isResized=true,columnWithAllowedResizing,currColSettings;delete visibleColumns[columnIndex].oWidth;if(hasFixedCols){visibleIndex=this.grid.getVisibleIndexByKey(columnKey);isFixed=visibleColumns[columnIndex].fixed===true;if(isFixed){headersTable=this.grid.fixedHeadersTable()}}if(this.grid._isMultiColumnGrid){headers=$(this.grid._headerCells);headerWidth=headers.length>0?$(headers[columnIndex])[0].offsetWidth:headerWidth}else{if(this.grid.options.virtualization===true||this.grid.options.rowVirtualization===true){headers=headersTable.find("> thead > tr").first().children("th").not("[data-skip=true]")}else{headers=headersTable.find("> thead > tr[data-header-row]").first().children("th").not("[data-skip=true]")}if(!this.grid.options.showHeader){headers=headersTable.find("tbody>tr:not([data-container='true'],[data-grouprow='true']):first").children("td").not("[data-skip=true]")}headerWidth=headers.length>0?headers.get(visibleIndex).offsetWidth:headerWidth}headerColumns=headersTable.find("> colgroup > col").not("[data-skip=true]");columnSettings=this.options.columnSettings;columnsLength=headerColumns.length;columnWithAllowedResizing=headerColumns.length;headerColStyleWidth=headerColumns[visibleIndex].style.width;hasPercentageWidth=/%$/.test(headerColStyleWidth);$(headerColumns).each(function(ind){if(!columnSettings[ind].allowResizing){columnWithAllowedResizing--}});if(headerColStyleWidth===""){hasPercentageWidth=true;headerColumns.each(function(ind,col){if(col.style.width!==""){hasPercentageWidth=false;return false}})}if(fireEvents){noCancel=this._trigger(this.events.columnResizing,originalEvent,{owner:this,columnIndex:columnIndex,columnKey:columnKey,desiredWidth:width});if(!noCancel){return{canceled:true,originalWidth:width,newWidth:width}}}if(columnKey!==undefined){cs=this._findColumnSettingsByKey(columnKey)}else{cs=columnSettings[columnIndex]}range=this._getRange(cs);width=this._coerceRange(range,width);width=Math.floor(width);if(width===range.min||width===range.max){isResized=false}if(isFixed){widthToDistribute=headerWidth-width;gridWidth=parseInt(this.grid.options.width,10);if(isNaN(gridWidth)||this.grid._gridHasWidthInPercent()){gridWidth=this.grid.container().outerWidth()}if(widthToDistribute<0){minimalVisibleAreaWidth=this._columnFixing.options.minimalVisibleAreaWidth;if(this._columnFixing._isVirtualGrid()){widthFixedContainer=this.grid._virtualcontainer().find("colgroup:first>col[data-fixed-col]").width()}else{widthFixedContainer=this.grid.fixedContainer().outerWidth()}if(widthFixedContainer-widthToDistribute>gridWidth-this.grid._scrollbarWidth()-minimalVisibleAreaWidth){if(fireEvents){this._trigger(this.events.columnResizingRefused,originalEvent,{owner:this,columnIndex:columnIndex,columnKey:columnKey,desiredWidth:width})}return{canceled:true,originalWidth:width,newWidth:width}}}}if($.ig.util.isWebKit&&hasPercentageWidth){totalWidth=headersTable[0].offsetWidth;for(i=0;i<columnsLength;i++){w=headerColumns[i].style.width;if(w===""){actualColumnWidths[i]=parseFloat(this.grid._isMultiColumnGrid?headers[i][0].offsetWidth:headers[i].offsetWidth)}else{actualColumnWidths[i]=w.indexOf("%")>0?parseFloat(w)/100*totalWidth:parseFloat(w)}}}else if(headers.length>0){for(i=0;i<columnsLength;i++){actualColumnStyleWidths[i]=headerColumns[i].style.width;actualColumnWidths[i]=this.grid._isMultiColumnGrid?headers[i][0].offsetWidth:headers[i].offsetWidth}}if(hasPercentageWidth){widthToDistribute=headerWidth-width;shrinkColumns=widthToDistribute<0;readyColumns=[];readyColumnsCount=0;widthDistributed=0;if(startIndex===undefined||startIndex===null){startIndex=0}while(readyColumnsCount<columnsLength-1-startIndex&&(shrinkColumns&&widthToDistribute<-.05||!shrinkColumns&&widthToDistribute>.05)){widthPerColumn=widthToDistribute/(columnWithAllowedResizing-1);for(i=startIndex;i<columnsLength;i++){if(i!==columnIndex&&!readyColumns[i]){currColSettings=this._findColumnSettingsByKey(this.grid._visibleColumns()[i].key);if(!currColSettings.allowResizing){readyColumnsCount++;continue}if(shrinkColumns){widthUsed=Math.max(widthPerColumn,widthToDistribute)}else{widthUsed=Math.min(widthPerColumn,widthToDistribute)}actualColumnWidths[i]+=widthUsed;range=this._getRange(currColSettings);coercedWidth=this._coerceRange(range,actualColumnWidths[i]);if(Math.abs(actualColumnWidths[i]-coercedWidth)>5e-6){widthUsed-=actualColumnWidths[i]-coercedWidth;actualColumnWidths[i]=coercedWidth;readyColumns[i]=true;readyColumnsCount++}widthDistributed+=widthUsed;widthToDistribute-=widthUsed}}}actualColumnWidths[columnIndex]-=widthDistributed;totalWidth=0;for(i=0;i<columnsLength;i++){totalWidth+=actualColumnWidths[i]}for(i=0;i<columnsLength;i++){requiredColumnPercentageWidths[i]=100*actualColumnWidths[i]/totalWidth}for(i=0;i<columnsLength;i++){newColumnStyleWidths[i]=requiredColumnPercentageWidths[i]+"%";visibleColumns[i].width=newColumnStyleWidths[i]}finalPixelWidth=actualColumnWidths[columnIndex];this._applyToEachGridCOL(function(index,col){col.css("width",newColumnStyleWidths[index])},isFixed)}else{allColumnsHaveWidth=true;for(i=0;i<columnsLength;i++){if(i===visibleIndex){newColumnStyleWidths[i]=width+"px";finalPixelWidth=width;typeW=$.type(visibleColumns[columnIndex].width);hasWidthPx=typeW==="string"&&visibleColumns[columnIndex].width.indexOf("px")>0;if(typeW==="number"){visibleColumns[columnIndex].width=width}else if(typeW==="string"){if(!hasWidthPx){visibleColumns[columnIndex].width=String(width)}else{visibleColumns[columnIndex].width=newColumnStyleWidths[i]}}else{visibleColumns[columnIndex].width=newColumnStyleWidths[i]}}else{newColumnStyleWidths[i]=actualColumnStyleWidths[i];allColumnsHaveWidth=allColumnsHaveWidth&&parseInt(actualColumnStyleWidths[i],10)>0}}this._applyToEachGridCOL(function(index,col){col.css("width",newColumnStyleWidths[index])},isFixed);containerWidth=this.grid._calculateContainerWidth(false);if(allColumnsHaveWidth){if(this.grid.options.width&&parseInt(this.grid.options.width,10)>0){if(!isFixed){this.grid._updateGridContentWidth()}$("#"+this.grid.element[0].id+"_horizontalScrollContainer").children("div").css("width",containerWidth);if(this.grid.options.virtualization===true||this.grid.options.rowVirtualization===true){this.grid._oldScrollLeft=$("#"+this.grid.id()+"_horizontalScrollContainer").scrollLeft()}if(this.grid.options.rowVirtualization||this.grid.options.virtualization===true){tmpW=containerWidth-this.grid._scrollbarWidth();$("#"+this.grid.element[0].id+"_headers").css("width",containerWidth).css("max-width",containerWidth);this.grid.element.css("width",tmpW).css("max-width",tmpW);$("#"+this.grid.id()+"_footers").css("width",containerWidth).css("max-width",containerWidth)}}else{if(hasFixedCols&&!isFixed){this.grid._updateGridContentWidth()}this.grid._setContainerWidth($("#"+this.grid.id()+"_container"));if(this.grid.options.rowVirtualization||this.grid.options.virtualization===true){tmpW=containerWidth+this.grid._scrollbarWidth();$("#"+this.grid.element[0].id+"_headers_v").css("width",tmpW).css("max-width",tmpW);$("#"+this.grid.element[0].id+"_displayContainer").css("width",containerWidth).css("max-width",containerWidth);$("#"+this.grid.element[0].id+"_virtualContainer > colgroup > col").first().attr("width",containerWidth);$("#"+this.grid.id()+"_footer_container").css("width",tmpW).css("max-width",tmpW)}}}}if(hasFixedCols){this._columnFixing._containerResized(isFixed,widthToDistribute)}if(fireEvents){this._trigger(this.events.columnResized,originalEvent,{owner:this,columnIndex:columnIndex,columnKey:columnKey,originalWidth:headerWidth,newWidth:finalPixelWidth})}return{canceled:false,originalWidth:headerWidth,newWidth:finalPixelWidth,isResized:isResized}},_applyToEachGridCOL:function(appliedFunction,isFixed){var headersTable,footersTable;if(this.grid.options.showHeader){if(isFixed){headersTable=this.grid.fixedHeadersTable()}else{headersTable=this.grid.headersTable()}headersTable.find("> colgroup > col").not("[data-skip=true]").each(function(i){appliedFunction(i,$(this))})}if(this.grid.options.fixedHeaders===true&&this.grid.options.height!==null||this.grid.options.showHeader===false){if(isFixed){$("#"+this.grid.id()+"_fixed").find("> colgroup > col").not("[data-skip=true]").each(function(i){appliedFunction(i,$(this))})}else{this.grid.element.find("> colgroup > col").not("[data-skip=true]").each(function(i){appliedFunction(i,$(this))})}}if(this.grid.options.fixedFooters===true&&this.grid.options.height!==null){if(isFixed){footersTable=this.grid.fixedFootersTable()}else{footersTable=this.grid.footersTable()}footersTable.find("> colgroup > col").not("[data-skip=true]").each(function(i){appliedFunction(i,$(this))})}},_getRange:function(column){var min=column.minimumWidth,max=column.maximumWidth,gridWidth;if($.type(min)==="string"&&min.indexOf("%")>0){gridWidth=this.grid.element.width();min=parseInt(min,10)*gridWidth/100}if($.type(max)==="string"&&max.indexOf("%")>0){gridWidth=this.grid.element.width();max=parseInt(max,10)*gridWidth/100}min=isNaN(min)?0:min;min=Math.max(0,min);max=isNaN(max)?Infinity:max;return{min:min,max:max}},_coerceRange:function(range,value){value=Math.max(range.min,value);value=Math.min(range.max,value);return value},_gridContainerPositioningOffset:function(){var gridContainer=this.grid.container(),containerPosition=gridContainer.css("position"),gridContainerOffsetParent=gridContainer.offsetParent(),gridContainerPosition=gridContainer.position(),gridContainerOffset=gridContainer.offset(),offsetParentScrollTop,offsetParentScrollLeft;if(containerPosition==="relative"||containerPosition==="absolute"){return{top:-gridContainerOffset.top,left:-gridContainerOffset.left}}if(gridContainerOffsetParent.is("body")){offsetParentScrollTop=0;offsetParentScrollLeft=0}else{offsetParentScrollTop=gridContainerOffsetParent.scrollTop();offsetParentScrollLeft=gridContainerOffsetParent.scrollLeft()}return{top:offsetParentScrollTop+gridContainerPosition.top-gridContainerOffset.top,left:offsetParentScrollLeft+gridContainerPosition.left-gridContainerOffset.left}},_calculateGridResizableHeight:function(heightOffset){var height,caption,headersTable,footersTable,scrollerContainer,hasVirtualization=this.grid.options.virtualization===true||this.grid.options.rowVirtualization===true||this.grid.options.columnVirtualization===true,hasWidthOrHeight=this.grid.options.height!==null||this.grid.options.width!==null;if(hasVirtualization){height=$("#"+this.grid.element[0].id+"_displayContainer").height()}else if(hasWidthOrHeight){height=this.grid.scrollContainer().height()}else{height=this.grid.element.height()}if(hasVirtualization||hasWidthOrHeight){headersTable=this.grid.headersTable();footersTable=this.grid.footersTable();if(this.grid.options.fixedHeaders===true&&this.grid.options.showHeader===true){if(headersTable.length!==0&&this.grid.element[0].id!==headersTable[0].id){height+=headersTable.height()}caption=headersTable.children("#"+this.grid.element[0].id+"_caption");if(caption.length!==0){if(!$.ig.util.isFF){height-=caption.outerHeight(true)}}}scrollerContainer=$("#"+this.element[0].id+"_hscroller_container");if(scrollerContainer.is(":visible")){height+=scrollerContainer.height()}if(this.grid.options.fixedFooters===true&&this.grid.options.showFooter===true&&footersTable.length!==0&&this.grid.element[0].id!==footersTable[0].id){height+=footersTable.height()}}if(heightOffset){height-=heightOffset}return height},_findColumnSettingsByKey:function(key,settings){var i;if(!settings){settings=this.options.columnSettings}for(i=0;i<settings.length;i++){if(settings[i].columnKey===key){return settings[i]}}},_initDefaultSettings:function(){var settings=[],key,cs=this.options.columnSettings,i,j,mch,s;if(this.grid.options.columns&&this.grid.options.columns.length>0){for(i=0;i<this.grid.options.columns.length;i++){settings[i]={columnIndex:i,columnKey:this.grid.options.columns[i].key,allowResizing:true,minimumWidth:20}}}for(i=0;i<cs.length;i++){for(j=0;j<settings.length;j++){if(settings[j].columnKey===cs[i].columnKey||settings[j].columnIndex===cs[i].columnIndex){break}}if(j===settings.length){if(this.grid._isMultiColumnGrid&&cs[i].allowResizing===false){mch=this.grid._getMultiHeaderColumnById(cs[i].columnKey);if(mch&&mch.children){mch.allowResizing=false;for(j=0;j<mch.children.length;j++){s=this._findColumnSettingsByKey(mch.children[j].key,settings);if(s){s.allowResizing=false}mch.children[j].allowResizing=false}}}continue}for(key in cs[i]){if(cs[i].hasOwnProperty(key)&&key!=="columnIndex"&&key!=="columnKey"){settings[j][key]=cs[i][key]}}}this._oColumnSettings=this.options.columnSettings;this.options.columnSettings=settings},_injectGrid:function(gridInstance){this.grid=gridInstance;this._checkGridNotSupportedFeatures();this.grid.element.unbind(".resizing");this._initDefaultSettings();this.grid.element.bind("iggridheaderrendered.resizing",$.proxy(this._headerRendered,this));this.grid.element.bind("iggridcolumnscollectionmodified.resizing",$.proxy(this._columnsCollectionModified,this));this.grid.element.bind("iggrid_columnsmoved.resizing",$.proxy(this._columnsMoved,this))},_checkGridNotSupportedFeatures:function(){var gridOptions=this.grid.options;if((gridOptions.virtualization===true||gridOptions.columnVirtualization===true)&&gridOptions.virtualizationMode==="fixed"){throw new Error($.ig.GridResizing.locale.resizingAndFixedVirtualizationNotSupported)}}});$.extend($.ui.igGridResizing,{version:"16.2.20162.2040"});return $.ui.igGridResizing});