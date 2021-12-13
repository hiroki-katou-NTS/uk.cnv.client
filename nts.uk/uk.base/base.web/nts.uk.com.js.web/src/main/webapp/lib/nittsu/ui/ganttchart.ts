module nts.uk.ui.chart {
    
    export let warning = [], pDiv = document.createElement("div");
    export class Ruler {
        
        chartArea: HTMLElement;
        definedType: any = {};
        gcChart: any = {};
        lineLock: any = {};
        slideTrigger: any;
        snatchInterval: number = 1;
        mode: manipulationMode.Type = "normal";
        pasteBand: manipulationMode.PasteOptions;
        placeholder: any;
        metaholder: manipulationMode.Metaholder = {};
        metaresize: manipulationMode.Metaresize = {};
        
        constructor(chartArea: HTMLElement) {
            if (_.isNil(chartArea)) {
                warning.push(new Warn("chartArea is undefined."));
            }
            
            this.chartArea = chartArea;
            this._init();
        }
        
        loggable(val: any) {
            let __lg = () => { 
                return val;
            };
            
            return __lg;
        }
        
        addType(options: any) {
            let self = this;
            if (_.isNil(options.name)) {
                warning.push(new Warn("Set type name"));
                return;
            }
            
            self.definedType[options.name] = new DefinedType(options, self);
            if (options.locked) {
                self.lineLock[options.lineNo] = true;
            }
        }
        
        addChart(options?: any) {
            let self = this, chart = new GanttChart(options);
            if (chart.locked) {
                self.lineLock[chart.lineNo] = true;
            }
            
            if ((self.gcChart[options.lineNo] || {})[options.id]) {
                warning.push(new Warn("Chart id existed"));
                return;
            }
            
            if (chart.newChart()) return;
            if (_.isNil(self.gcChart[options.lineNo])) {
                self.gcChart[options.lineNo] = {};
            }
            
            self.gcChart[options.lineNo][options.id] = chart;
            let show = true;
            if (!_.isNil(options.parent)) {
                let parent = self.gcChart[options.lineNo][options.parent];
                if (parent) {
                    parent.children.push(chart);
                    if (chart.end <= parent.start || chart.start >= parent.end) show = false;
                    else if (parent.start > chart.start) {
                        chart.html.style.left = `${chart.origin[0] + parent.start * chart.unitToPx}px`;
                        chart.html.style.width = `${(chart.end - parent.start) * chart.unitToPx - 1}px`;
                    } else if (parent.end < chart.end) {
                        chart.html.style.width = `${(parent.end - chart.start) * chart.unitToPx - 1}px`;
                    }
                }
            }
            
            if (chart.title) {
//                let titleTag = pDiv.cloneNode(true);
//                titleTag.className = "chart-name";
//                titleTag.textContent = chart.title;
//                chart.html.appendChild(titleTag);
                chart.html.textContent = chart.title
            }
            
            if (show) {
                self.chartArea.appendChild(chart.html);
            }
            
            let docMove = () => {
                if (self.mode !== "normal") return;
                event.preventDefault();
                if (_.keys(self.slideTrigger).length === 0) return;
                let diff = event.pageX - self.slideTrigger.pageX, nearestLine, parentChart;
                if (!_.isNil(chart.parent)) {
                    parentChart = self.gcChart[chart.lineNo][chart.parent];
                }
                
                if (self.slideTrigger.holdPos === HOLD_POS.BODY) {
                    if (!chart.canSlide) return;
                    nearestLine = Math.round(self.slideTrigger.start + diff / chart.unitToPx);
                    if (nearestLine % self._getSnatchInterval(chart) !== 0) return;
                    let step = nearestLine - self.slideTrigger.start, 
                        pDec = { left: nearestLine * chart.unitToPx, start: nearestLine, end: self.slideTrigger.end + step };
                    if (chart.limitStartMin > pDec.start || chart.limitStartMax < pDec.start 
                        || chart.limitEndMin > pDec.end || chart.limitEndMax < pDec.end) return;
                    let lineCharts = self.gcChart[chart.lineNo];
                    if (_(lineCharts).keys().find(k => {
                        let sameLineChart: GanttChart = lineCharts[k];
                        if (sameLineChart.html.style.display === "none") return false;
                        return (sameLineChart.id !== chart.id && sameLineChart.parent === chart.parent 
                                && !sameLineChart.bePassedThrough
                                && ((diff > 0 && chart.end <= sameLineChart.start && pDec.end > sameLineChart.start) 
                                    || (diff < 0 && chart.start >= sameLineChart.end && pDec.start < sameLineChart.end)));
                    })) return;
                    
                    if (parentChart && ((diff > 0 && pDec.end > parentChart.end) || (diff < 0 && pDec.start < parentChart.start))) return;
                    
                    if (parentChart && _.find(parentChart.children, (child: GanttChart) => {
                        return child.id !== chart.id && !child.bePassedThrough && child.html.style.display !== "none"
                            && ((chart.start >= child.end && pDec.start < child.end) || (chart.end <= child.start && pDec.end > child.start));
                    })) return;
                    
                    _.forEach(chart.children, (child: GanttChart) => {
                        if (child.html.style.display === "none") return;
                        let childSlide;
                        if (child.followParent) {
                            childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                            if (!childSlide) return;
                            child.reposition({ start: childSlide.start + step, end: childSlide.end + step, left: childSlide.left + step * child.unitToPx });
                        } else if (!child.pin || (child.pin && child.pruneOnSlide)) {
                            let childStart = Math.max(child.initStart, pDec.start),
                                childEnd = Math.min(child.initEnd, pDec.end);
                            if (childStart < childEnd) {
                                let childLength = (childEnd - childStart) * child.unitToPx - 1;
                                if (child.pin && child.rollup) { 
                                    child.reposition({
                                        width: childLength,
                                        left: childStart * child.unitToPx
                                    });
                                } else {
                                    child.reposition({ 
                                        width: childLength, 
                                        start: childStart, 
                                        end: childEnd, 
                                        left: childStart * child.unitToPx 
                                    });
                                }
                                
                                if (childLength > 0 && !self.chartArea.contains(child.html)) {
                                    self.chartArea.appendChild(child.html);
                                }
                            } else child.reposition({ width: 0 });
                        }
                    });
                    
                    pDec.initStart = pDec.start;
                    pDec.initEnd = pDec.end;
                    chart.reposition(pDec);
                } else if (self.slideTrigger.holdPos === HOLD_POS.START) {
                    if (chart.fixed === CHART_FIXED.START || chart.fixed === CHART_FIXED.BOTH) return;
                    nearestLine = Math.round(self.slideTrigger.start + diff / chart.unitToPx);
                    if (nearestLine % self._getSnatchInterval(chart) !== 0 || nearestLine === chart.start) return;
                    let pDec = { width: self.slideTrigger.length + (self.slideTrigger.start - nearestLine) * chart.unitToPx, left: nearestLine * chart.unitToPx, start: nearestLine };
                    if (chart.limitStartMin > pDec.start || chart.limitStartMax < pDec.start) return;
                    if (pDec.start + self._getSnatchInterval(chart) > chart.end
                        || (parentChart && !self.slideTrigger.overlap && pDec.start < parentChart.start)) return;
                    self.slideTrigger.ltr = nearestLine > chart.start;
                    let lineCharts = self.gcChart[chart.lineNo];
                    if (_(lineCharts).keys().find(k => {
                        let sameLineChart: GanttChart = lineCharts[k];
                        if (sameLineChart.html.style.display === "none") return false;
                        return (sameLineChart.id !== chart.id && sameLineChart.parent === chart.parent 
                                && !sameLineChart.bePassedThrough
                                && (nearestLine < chart.start && pDec.start < sameLineChart.end && chart.end > sameLineChart.end));
                    })) return;
                    
                    self.slideTrigger.rangeMin = Math.min(nearestLine, self.slideTrigger.rangeMin || 0);
                    self.slideTrigger.rangeMax = Math.max(nearestLine, self.slideTrigger.rangeMax || 0);
                    _.forEach(chart.children, (child: GanttChart) => {
                        if (child.html.style.display === "none") return;
                        let childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                        if (!childSlide) return;
                        if (child.pin) {
                            if (child.rollup) {
                                if (nearestLine >= child.start && nearestLine < child.end) {
                                    let newWidth = (Math.min(childSlide.end, chart.end) - childSlide.start) * child.unitToPx - 1 + (childSlide.start - pDec.start) * child.unitToPx;
                                    child.reposition({ width: newWidth, left: pDec.start * child.unitToPx });
                                    if (!self.chartArea.contains(child.html)) {
                                        self.chartArea.appendChild(child.html);
                                    }
                                    
                                    if (!_.find(self.slideTrigger.edgeCharts, c => c.id === child.id)) {
                                        self.slideTrigger.edgeCharts.push(child);
                                    }
                                } else if (nearestLine < child.start) {
                                    if (!self.chartArea.contains(child.html)) {
                                        if (self.slideTrigger.start <= child.end
                                            && self.slideTrigger.rangeMax < child.end) return;
                                        self.chartArea.appendChild(child.html);
                                    }
                                    
                                    let maxWidth = (Math.min(child.end, chart.end) - child.start) * child.unitToPx - 1,
                                        currentWidth = parseFloat(child.html.style.width);
                                    if (currentWidth !== maxWidth) {
                                        child.reposition({ width: maxWidth, left: child.start * child.unitToPx });
                                    }
                                    
                                    if (self.slideTrigger.edgeCharts.length > 0) {
                                        _.remove(self.slideTrigger.edgeCharts, c => c.id === child.id);
                                    }
                                } else {
                                    child.reposition({ width: 0 });
                                    if (self.slideTrigger.edgeCharts.length > 0) {
                                        _.remove(self.slideTrigger.edgeCharts, c => c.id === child.id);
                                    }
                                }
                            }                               
                        }  
                            
//                            child.reposition({ width: childSlide.length + (childSlide.start - pDec.start) * child.unitToPx, left: pDec.start * child.unitToPx, start: pDec.start });
                         
                    });
                    
                    if (self.slideTrigger.overlap) {
                        parentChart.reposition({ width: self.slideTrigger.overlap.parentLength + (self.slideTrigger.start - nearestLine) * parentChart.unitToPx, left: pDec.left, start: pDec.start });
                    }
                    
                    chart.reposition(pDec);
                } else {
                    if (chart.fixed === CHART_FIXED.END || chart.fixed === CHART_FIXED.BOTH) return;
                    nearestLine = Math.round(self.slideTrigger.end + diff / chart.unitToPx);
                    if (nearestLine % self._getSnatchInterval(chart) !== 0 || nearestLine === chart.end) return;
                    let pDec = { width: self.slideTrigger.length + (nearestLine - self.slideTrigger.end) * chart.unitToPx, end: nearestLine };
                    if (chart.limitEndMax < pDec.end || chart.limitEndMin > pDec.end) return;
                    if (chart.start + self._getSnatchInterval(chart) > pDec.end
                        || (parentChart && !self.slideTrigger.overlap && pDec.end > parentChart.end)) return;
                    self.slideTrigger.ltr = nearestLine > chart.end;
                    let lineCharts = self.gcChart[chart.lineNo];
                    if (_(lineCharts).keys().find(k => {
                        let sameLineChart: GanttChart = lineCharts[k];
                        if (sameLineChart.html.style.display === "none") return false;
                        return (sameLineChart.id !== chart.id && sameLineChart.parent === chart.parent 
                                && !sameLineChart.bePassedThrough
                                && (nearestLine > chart.end && pDec.end > sameLineChart.start && chart.start < sameLineChart.start));
                    })) return;
                    
                    self.slideTrigger.rangeMin = Math.min(nearestLine, self.slideTrigger.rangeMin || 0);
                    self.slideTrigger.rangeMax = Math.max(nearestLine, self.slideTrigger.rangeMax || 0);
                    _.forEach(chart.children, (child: GanttChart) => {
                        if (child.html.style.display === "none") return;
                        let childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                        if (!childSlide) return;
                        if (child.pin) {
                            if (child.rollup) {
                                if (nearestLine > child.start && nearestLine <= child.end) {
                                    let newWidth = (childSlide.end - Math.max(childSlide.start, chart.start)) * child.unitToPx - 1 + (pDec.end - childSlide.end) * child.unitToPx;
                                    child.reposition({ width: newWidth });
                                    if (!self.chartArea.contains(child.html)) {
                                        self.chartArea.appendChild(child.html);
                                    }
                                    
                                    if (!_.find(self.slideTrigger.edgeCharts, c => c.id === child.id)) {
                                        self.slideTrigger.edgeCharts.push(child);
                                    }
                                } else if (nearestLine > child.end) {
                                    if (!self.chartArea.contains(child.html)) {
                                        if (self.slideTrigger.end > child.start 
                                            && self.slideTrigger.rangeMin > child.start) return;
                                        self.chartArea.appendChild(child.html);
                                    }
                                    
                                    let maxWidth = (child.end - Math.max(child.start, chart.start)) * child.unitToPx - 1,
                                        currentWidth = parseFloat(child.html.style.width);
                                    if (currentWidth !== maxWidth) {
                                        child.reposition({ width: maxWidth });
                                    }
                                    
                                    if (self.slideTrigger.edgeCharts.length > 0) {
                                        _.remove(self.slideTrigger.edgeCharts, c => c.id === child.id);
                                    }
                                } else {
                                    child.reposition({ width: 0 });
                                    if (self.slideTrigger.edgeCharts.length > 0) {
                                        _.remove(self.slideTrigger.edgeCharts, c => c.id === child.id);
                                    }
                                }
                            }                               
                        }
//                            child.reposition({ width: childSlide.length + (pDec.end - childSlide.end) * child.unitToPx, end: pDec.end });
                    });
                    
                    if (self.slideTrigger.overlap) {
                        parentChart.reposition({ width: self.slideTrigger.overlap.parentLength + (nearestLine - self.slideTrigger.end) * parentChart.unitToPx, end: pDec.end });
                    }
                    
                    chart.reposition(pDec);
                }
            };
            
            let docUp = () => {
                if (self.mode !== "normal") return;
                _.forEach(chart.children, (child: GanttChart) => {
                    if (child.pin && child.rollup && child.roundEdge) {
                        if (self.slideTrigger.holdPos === HOLD_POS.START
                            && chart.start > child.start && chart.start <= child.end) {
                            let delta = (child.end - chart.start) * chart.unitToPx,
                                chartWidth = parseFloat(chart.html.style.width) - delta,
                                chartLeft = parseFloat(chart.html.style.left) + delta;
                            chart.reposition({ width: chartWidth, left: chartLeft, start: child.end });
                            child.reposition({ width: 0 });
                            if (self.slideTrigger.edgeCharts.length > 0) {
                                self.slideTrigger.edgeCharts.forEach((c: GanttChart) => {
                                    if (c.id === child.id) return;
                                    c.reposition({ width: parseFloat(c.html.style.width) - delta, left: parseFloat(c.html.style.left) + delta });
                                });
                            }
                            
                            return false;
                        } else if (self.slideTrigger.holdPos === HOLD_POS.END
                            && chart.end > child.start && chart.end < child.end) {
                            let delta = (chart.end - child.start) * chart.unitToPx,
                                chartWidth = parseFloat(chart.html.style.width) - delta;
                            chart.reposition({ width: chartWidth, end: child.start });
                            child.reposition({ width: 0 });
                            if (self.slideTrigger.edgeCharts.length > 0) {
                                self.slideTrigger.edgeCharts.forEach((c: GanttChart) => {
                                    if (c.id === child.id) return;
                                    c.reposition({ width: parseFloat(c.html.style.width) - delta });  
                                });
                            }
                            
                            return false;
                        }
                    }
                });
                
                document.removeEventListener("mousemove", docMove);
                document.removeEventListener("mouseup", docUp);
                
                let e = document.createEvent('CustomEvent');
                if (self.slideTrigger.holdPos === HOLD_POS.BODY) {
                    if (_.isFunction(chart.dropFinished)) {
                        chart.dropFinished(chart.start, chart.end);
                    }
                    
                    e.initCustomEvent("gcdrop", true, true, [ chart.start, chart.end ]);
                } else {
                    if (_.isFunction(chart.resizeFinished)) {
                        chart.resizeFinished(chart.start, chart.end, self.slideTrigger.holdPos === HOLD_POS.START);
                    }
                    
                    e.initCustomEvent("gcresize", true, true, [ chart.start, chart.end, self.slideTrigger.holdPos === HOLD_POS.START ]);   
                }
                
                self.slideTrigger = {};
                chart.html.dispatchEvent(e);
            };
            
            chart.html.addEventListener("mousedown", () => {
                if (event.button !== 0) return;
                if (self.mode !== "normal") {
                    if (chart.canPasteResize) {
                        let rect = chart.html.getBoundingClientRect();
                        event.offsetXRecalculated = event.clientX - rect.left;
                        let holdPos = self.getHoldPos(chart);
                        if (_.isNil(chart.parent)) return;
                        if (holdPos === HOLD_POS.START) {
                            self.metaresize.chart = chart;
                            self.metaresize.graspPos = holdPos;
                            self.metaresize.start = chart.start;
                            self.metaresize.end = chart.end;
                            self.metaresize.left = parseFloat(chart.html.style.left);
                            self.metaresize.offsetLeft = chart.html.getBoundingClientRect().left;
                            self.metaresize.isPressed = true;
                            let parent = self.gcChart[chart.lineNo][chart.parent];
                            _.forEach(parent.children, (child: GanttChart) => {
                                if (child.html.style.display !== "none"
                                    && child.id !== chart.id && child.end === chart.start && child.canPaste) {
                                    self.metaresize.adjChart = child;
                                    return false;
                                }                                        
                            });
                            
                            document.addEventListenerNS("mousemove.pasteResize", manipulationMode.resizeMove.bind(self));
                            document.addEventListenerNS("mouseup.pasteResize", manipulationMode.resizeUp.bind(self));
                            return;
                        } else if (holdPos === HOLD_POS.END) {
                            self.metaresize.chart = chart;
                            self.metaresize.graspPos = holdPos;
                            self.metaresize.start = chart.start;
                            self.metaresize.end = chart.end;
                            self.metaresize.offsetLeft = chart.html.getBoundingClientRect().left;
                            self.metaresize.isPressed = true;
                            let parent = self.gcChart[chart.lineNo][chart.parent];
                            _.forEach(parent.children, (child: GanttChart) => {
                                if (child.html.style.display !== "none"
                                    && child.id !== chart.id && child.start === chart.end && child.canPaste) {
                                    self.metaresize.adjChart = child;
                                    return false;
                                }                                        
                            });
                            
                            document.addEventListenerNS("mousemove.pasteResize", manipulationMode.resizeMove.bind(self));
                            document.addEventListenerNS("mouseup.pasteResize", manipulationMode.resizeUp.bind(self));
                            return;
                        }
                    }
                    
                    if (!self.metaholder.hasOwnProperty("start")) return;
                    chart.html.style.cursor = "";
                    self.metaholder.id = `pgc${support.replaceAll(util.randomId(), '-', '')}`;
                    self.metaholder.ancestorChart = chart;
                    self.metaholder.isPressed = true;
                    self.addChartWithType(self.pasteBand.typeName, {
                        id: self.metaholder.id,
                        parent: chart.parent || chart.id,
                        start: self.metaholder.start,
                        end: self.metaholder.end,
                        lineNo: self.metaholder.lineNo,
                        color: self.metaholder.color,
                        zIndex: self.pasteBand.zIndex || 1000
                    });
                    
//                    if (self.mode === "paste") {
//                        self.metaholder.tempStart = self.metaholder.start;
//                        self.metaholder.tempEnd = self.metaholder.end;
//                    }
                    
                    document.addEventListenerNS("mousemove.paste", manipulationMode.pasteMove.bind(self));
                    document.addEventListenerNS("mouseup.paste", manipulationMode.pasteUp.bind(self));
                    return;
                }
                
                let holdPos = self.getHoldPos(chart);
                if (chart.pin) {
//                    if (chart.rollup) {
                        if (!_.isNil(chart.parent)) {
                            let parentChart = self.gcChart[chart.lineNo][chart.parent];
                            if (holdPos === HOLD_POS.START && parentChart.start >= chart.start) {
                                let pevt = new support.ChartEvent("mousedown");
                                pevt.pageX = event.pageX;
                                pevt.pageY = event.pageY;
                                pevt.offsetX = event.offsetX;
                                pevt.button = 0;
                                parentChart.html.dispatchEvent(pevt);
                                return;
                            } else if (holdPos === HOLD_POS.END && parentChart.end <= chart.end) {
                                let pevt = new support.ChartEvent("mousedown");
                                pevt.pageX = event.pageX;
                                pevt.pageY = event.pageY;
                                pevt.offsetX = parseFloat(parentChart.html.style.width);
                                pevt.button = 0;
                                parentChart.html.dispatchEvent(pevt);
                                return;
                            }
                        }
//                    }
                }
                
                if (holdPos === HOLD_POS.OUT) return;
                self.slideTrigger = { 
                    pageX: event.pageX,
                    holdPos: holdPos,
                    length: parseFloat(chart.html.style.width),
                    start: chart.start,
                    end: chart.end,
                    children: _.map(chart.children, c => 
                        ({ 
                            id: c.id, 
                            start: c.start, 
                            end: c.end, 
                            initStart: c.initStart, 
                            initEnd: c.initEnd,
                            length: parseFloat(c.html.style.width), 
                            left: parseFloat(c.html.style.left) 
                        })),
                    edgeCharts: []
                };
                
                if (!_.isNil(chart.parent)) {
                    let parentChart = self.gcChart[chart.lineNo][chart.parent];
                    if ((holdPos === HOLD_POS.START && parentChart.start === chart.start)
                        || (holdPos === HOLD_POS.END && parentChart.end === chart.end)) {
                        self.slideTrigger.overlap = { parentLength: parseFloat(parentChart.html.style.width) };
                    }
                }
                
                document.addEventListener("mousemove", docMove);
                document.addEventListener("mouseup", docUp);
            });
            
            chart.html.addEventListener("mousemove", () => {
                if (self.mode !== "normal") {
                    chart.html.style.cursor = "";
                    return;
                }
                
                let holdPos = self.getHoldPos(chart);
                if (holdPos === HOLD_POS.START) {
                    if (chart.fixed !== CHART_FIXED.START && chart.fixed !== CHART_FIXED.BOTH) {
                        chart.cursor = "col-resize";
                    } else if (chart.pin /*&& chart.rollup*/ && !_.isNil(chart.parent)) {
                        let parentChart = self.gcChart[chart.lineNo][chart.parent];
                        if (parentChart.start >= chart.start
                            && parentChart.fixed !== CHART_FIXED.START && parentChart.fixed !== CHART_FIXED.BOTH) {
                            chart.cursor = "col-resize";
                        }
                    }
                } else if (holdPos === HOLD_POS.END) {
                    if (chart.fixed !== CHART_FIXED.END && chart.fixed !== CHART_FIXED.BOTH) {
                        chart.cursor = "col-resize";
                    } else if (chart.pin /*&& chart.rollup*/ && !_.isNil(chart.parent)) {
                        let parentChart = self.gcChart[chart.lineNo][chart.parent];
                        if (parentChart.end <= chart.end
                            && parentChart.fixed !== CHART_FIXED.END && parentChart.fixed !== CHART_FIXED.BOTH) {
                            chart.cursor = "col-resize";
                        }
                    }
                } else if (holdPos === HOLD_POS.BODY && chart.canSlide) {
                    chart.cursor = "e-resize";
                } else chart.cursor = "";
                
                chart.html.style.cursor = chart.cursor;
            });
            
            return chart.html;
        }
        
        getHoldPos(chart: GanttChart) {
            let self = this;
            if (self.lineLock[chart.lineNo] && self.mode === "normal") return HOLD_POS.OUT;
            let parentChart;
            if (chart.parent) {
                parentChart = self.gcChart[chart.lineNo][chart.parent];
            }
            
            let offsetX = event.offsetXRecalculated || event.offsetX;
            if (chart.fixed === CHART_FIXED.BOTH && parentChart 
                && ((chart.start > parentChart.start && chart.end < parentChart.end)
                    || ((chart.start === parentChart.start || chart.end === parentChart.end) 
                        && (chart.end - chart.start) * chart.unitToPx <= chart.drawerSize + 2))
                && (offsetX < chart.drawerSize || parseFloat(chart.html.style.width) - chart.drawerSize < offsetX)) {
                return HOLD_POS.BODY;
            } else if (chart.fixed !== CHART_FIXED.START && offsetX < chart.drawerSize) {
                return HOLD_POS.START;
            } else if (chart.fixed !== CHART_FIXED.END
                && parseFloat(chart.html.style.width)/*(chart.end - chart.start) * chart.unitToPx*/ - chart.drawerSize < offsetX) {
                return HOLD_POS.END;
            } else {
                return HOLD_POS.BODY;
            }
        }
        
        addChartWithType(typeName: string, options?: any) {
            let self = this,
                chartType = self.definedType[typeName];
            if (_.isNil(options)) {
                options = {};
            }
            
            if (chartType) {
                _.forEach(_.keys(chartType), key => {
                    let assignedKey = key === "name" ? "definedType" : key; 
                    if (_.isNil(options[assignedKey]) && !_.isNil(chartType[key])) {
                        options[assignedKey] = chartType[key];
                    }
                });
            }
            
            let chart = self.addChart(options);
            if (!chartType.list) chartType.list = [ chart ];
            else chartType.list.push(chart);
            return chart;
        }
        
        setLock(lines: Array<any>, lock: any) {
            let self = this;
            _.forEach(lines, line => self.lineLock[line] = lock); 
        }
        
        setSnatchInterval(interval: number) {
            let self = this;
            self.snatchInterval = interval;
            if (!_.isNil(self.pasteBand)) {
                self.pasteBand.blockSize = interval;
            }
        }
        
        replaceAt(lineNo: number, charts: Array<{ type: string, options: any }>, id?: any) {
            if (_.isNil(lineNo) || _.isNil(charts) || charts.length === 0) return;
            let self = this;
            let lineChart = self.gcChart[lineNo];
            if (!lineChart) return;
            if (!_.isNil(id)) {
                let removed = _(lineChart).keys().map(c => lineChart[c]).find(c => c.id === id);
                if (removed) {
                    if (removed.html.parentNode) removed.html.parentNode.removeChild(removed.html);
                    let chart = charts[0];
                    if (_.has(chart, "type")) {
                        self.addChartWithType(chart.type, chart.options)
                    } else self.addChart(chart.options);
                }
                
                return;
            }
            
            _(lineChart).keys().map(c => lineChart[c]).forEach(c => {
                if (c.html.parentNode) c.html.parentNode.removeChild(c.html)
            });
            
            self.gcChart[lineNo] = {};
            charts.forEach(c => _.has(c, "type") ? self.addChartWithType(c.type, c.options) : self.addChart(c.options));
        }
        
        move(lineNo: number, id: any, start: any) {
            let self = this;
            if (_.isNil(lineNo) || _.isNil(id) || _.isNil(start)) return;
            let chart = (self.gcChart[lineNo] || {})[id];
            if (_.isNil(chart)) return;
            self.slideTrigger = {
                length: parseFloat(chart.html.style.width),
                start: chart.start,
                end: chart.end,
                children: _.map(chart.children, c => ({ id: c.id, start: c.start, end: c.end, length: parseFloat(c.html.style.width), left: parseFloat(c.html.style.left) }))
            };
            
            let diff = start - chart.start;
            let pDec = { left: start * chart.unitToPx, start: start, end: chart.end + start - chart.start };
            if (chart.limitStartMin > pDec.start || chart.limitStartMax < pDec.start 
                || chart.limitEndMin > pDec.end || chart.limitEndMax < pDec.end) return;
            let parentChart = (self.gcChart[lineNo] || {})[chart.parent],
                step = start - chart.start;
            if (parentChart && ((step > 0 && pDec.end > parentChart.end) || (step < 0 && pDec.start < parentChart.start))) return;
            
            _.forEach(chart.children, (child: GanttChart) => {
                let childSlide;
                if (child.followParent) {
                    childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                    if (!childSlide) return;
                    child.reposition({ start: childSlide.start + step, end: childSlide.end + step, left: childSlide.left + step * child.unitToPx });
                } else if (diff > 0 && child.start < pDec.start) {
                    childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                    if (!childSlide) return;
                    child.reposition({ width: childSlide.length + (childSlide.start - pDec.start) * child.unitToPx, left: pDec.start * child.unitToPx, start: pDec.start });
                } else if (diff < 0 && child.end > pDec.end) {
                    childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                    if (!childSlide) return;
                    child.reposition({ width: childSlide.length + (pDec.end - childSlide.end) * child.unitToPx, end: pDec.end });
                }
            });
            
            chart.reposition(pDec);
            self.slideTrigger = {};
        }
        
        extend(lineNo: number, id: any, start: any, end?: any) {
            let self = this;
            if (_.isNil(lineNo) || _.isNil(id) || (_.isNil(start) && _.isNil(end))) return;
            let chart = (self.gcChart[lineNo] || {})[id];
            if (_.isNil(chart)) return;
            let parentChart;
            if (!_.isNil(chart.parent)) {
                parentChart = (self.gcChart[lineNo] || {})[chart.parent];
            }
            
            if (!_.isNil(start) && start !== chart.start) {
                self.slideTrigger = {
                    length: parseFloat(chart.html.style.width),
                    start: chart.start,
                    end: chart.end,
                    children: _.map(chart.children, c => ({ id: c.id, start: c.start, end: c.end, length: parseFloat(c.html.style.width), left: parseFloat(c.html.style.left) }))
                };
                
                //if (start % self._getSnatchInterval(chart) !== 0) return;
                let pDec = { width: self.slideTrigger.length + (self.slideTrigger.start - start) * chart.unitToPx, left: start * chart.unitToPx, start: start };
                if (chart.limitStartMin > pDec.start || chart.limitStartMax < pDec.start) return;
                if (/*pDec.start + self._getSnatchInterval(chart) > chart.end
                    ||*/ (parentChart && !self.slideTrigger.overlap && pDec.start < parentChart.start)) return;
                self.slideTrigger.ltr = start > chart.start;
                    
                _.forEach(chart.children, (child: GanttChart) => {
                    let childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                    if (!childSlide) return;
                    if (child.pin) {
                        if (child.rollup) {
                            if (start >= child.start && start < child.end) {
                                let newWidth = (Math.min(childSlide.end, chart.end) - childSlide.start) * child.unitToPx - 1 + (childSlide.start - pDec.start) * child.unitToPx;
                                child.reposition({ width: newWidth, left: pDec.start * child.unitToPx });
                                if (!self.chartArea.contains(child.html)) {
                                    self.chartArea.appendChild(child.html);
                                }
                            } else if (start < child.start) {
                                if (!self.chartArea.contains(child.html) && child.end > chart.end) return;
                                let maxWidth = (Math.min(child.end, chart.end) - child.start) * child.unitToPx - 1,
                                    currentWidth = parseFloat(child.html.style.width);
                                if (currentWidth !== maxWidth) {
                                    child.reposition({ width: maxWidth, left: parseFloat(child.html.style.left) - parseFloat(maxWidth - currentWidth) });
                                }
                                
                                if (!self.chartArea.contains(child.html)) {
                                    self.chartArea.appendChild(child.html);
                                }   
                            } else {
                                child.reposition({ width: 0 });
                            }
                        }                               
                    }
                });
                
                chart.reposition(pDec);
            }
            
            if (!_.isNil(end) && end !== chart.end) {
                self.slideTrigger = {
                    length: parseFloat(chart.html.style.width),
                    start: chart.start,
                    end: chart.end,
                    children: _.map(chart.children, c => ({ id: c.id, start: c.start, end: c.end, length: parseFloat(c.html.style.width), left: parseFloat(c.html.style.left) }))
                };
                
                //if (end % self._getSnatchInterval(chart) !== 0) return;
                let pDec = { width: self.slideTrigger.length + (end - self.slideTrigger.end) * chart.unitToPx, end: end };
                if (chart.limitEndMax < pDec.end || chart.limitEndMin > pDec.end) return;
                if (/*chart.start + self._getSnatchInterval(chart) > pDec.end
                    ||*/ (parentChart && !self.slideTrigger.overlap && pDec.end > parentChart.end)) return;
                self.slideTrigger.ltr = end > chart.end;
                
                _.forEach(chart.children, (child: GanttChart) => {
                    let childSlide = _.find(self.slideTrigger.children, c => c.id === child.id);
                    if (!childSlide) return;
                    if (child.pin) {
                        if (child.rollup) {
                            if (end > child.start && end <= child.end) {
                                let newWidth = (childSlide.end - Math.max(childSlide.start, chart.start)) * child.unitToPx - 1 + (pDec.end - childSlide.end) * child.unitToPx;
                                child.reposition({ width: newWidth });
                                if (!self.chartArea.contains(child.html)) {
                                    self.chartArea.appendChild(child.html);
                                }
                            } else if (end > child.end) {
                                if (child.start < chart.start) return;
                                let maxWidth = (child.end - Math.max(child.start, chart.start)) * child.unitToPx - 1,
                                    currentWidth = parseFloat(child.html.style.width);
                                if (currentWidth !== maxWidth) {
                                    child.reposition({ width: maxWidth });
                                }
                                
                                if (!self.chartArea.contains(child.html)) {
                                    self.chartArea.appendChild(child.html);
                                }
                            } else {
                                child.reposition({ width: 0 });
                            }
                        }                               
                    }
                });
                
                chart.reposition(pDec);
            }
            
            self.slideTrigger = {};
        }
        
        removeBy(chartInfo: { no: any, id: string }, shallow?: boolean) {
            if (_.isNil(chartInfo)) return;
            let self = this,
                chart = self.gcChart[chartInfo.no][chartInfo.id];
            if (!shallow) {
                _.forEach(chart.children, (child: GanttChart) => {
                    self.remove(child, true);
                    delete self.gcChart[child.lineNo][child.id];        
                });
            }
            
            self.remove(chart);
        }
        
        remove(chart: GanttChart, shallow?: boolean) {
            if (_.isNil(chart)) return;
            let self = this;
            chart.reposition({ width: 0 });
            if (shallow) return;
            delete self.gcChart[chart.lineNo][chart.id];
            let parent = self.gcChart[chart.lineNo][chart.parent];
            if (!parent) return;
            _.remove(parent.children, (child: GanttChart) => {
                return child.lineNo === chart.lineNo && child.id === chart.id;
            });
        }
        
        setMode(modeName: manipulationMode.Type) {
            let self = this;
            self.mode = modeName;
        }
        
        pasteChart(options: manipulationMode.PasteOptions) {
            let self = this;
            self.pasteBand = options;
            if (_.isNil(options.blockSize)) {
                self.pasteBand.blockSize = self.snatchInterval;
            }
        }
        
        private _getSnatchInterval(chart: GanttChart) {
            let self = this;
            if (!_.isNil(self.snatchInterval)) return self.snatchInterval;
            return chart.snatchInterval;
        }
        
        private _init() {
            let self = this;
            if (_.isNil(self.chartArea)) return;
            self.addType({
                name: support.GGC,
                color: "#FFF",
                lineWidth: 30,
                canSlide: false,
                unitToPx: 4,
                canPaste: true,
                canPasteResize: true
            });
            
            self.chartArea.addEventListener("mousemove", () => {
                if (self.mode === "normal" || self.metaholder.isPressed || self.metaresize.isPressed) return;
                let pointElm = document.elementFromPoint(event.pageX, event.pageY);
                if (_.isNil(pointElm)) return;
                if (pointElm.classList.contains("gantt-holder")) {
                    let underlyChart = support.underElementFromPoint(pointElm, event.pageX, event.pageY);
                    if (underlyChart.classList.contains("nts-ganttchart")) {
                        pointElm = underlyChart;
                        let rect = underlyChart.getBoundingClientRect();
                        event.offsetXRecalculated = event.clientX - rect.left;
                    }
                }
                
                if (pointElm.classList.contains("chart-name")) {
                    pointElm = support.closest(pointElm, ".nts-ganttchart");
                } else if (!pointElm.classList.contains("nts-ganttchart")) {
                    if (self.chartArea.contains(self.placeholder)) {
                        self.placeholder.parentNode.removeChild(self.placeholder);
                    }
                    
                    return;
                }
                
                let id = pointElm.getAttribute("id");
                if (_.isNil(id)) return;
                let meta = id.split('-');
                if (meta.length < 2) return;
                self._tailor(event.pageX, self.gcChart[meta[0]][meta[1]]);
            });
            
            self.chartArea.addEventListener("mouseover", () => {
                if (self.mode !== "normal" && event.target && !event.target.classList.contains("nts-ganttchart")
                    && !event.target.classList.contains("gantt-holder")) {
                    self.chartArea.style.cursor = "not-allowed";
                } else {
                    self.chartArea.style.cursor = "";
                }
            });
        }
        
        private _tailor(posX: number, chart: GanttChart) {
            let self = this;
            if (self.mode === "normal" || _.isNil(self.pasteBand) || _.isNil(chart)) return;
            if (!chart.canPaste) {
                chart.html.style.cursor = "not-allowed";
                self.metaholder = {};
                return;
            }
            
            if (chart.canPasteResize) {
                let holdPos = self.getHoldPos(chart);
                if (holdPos === HOLD_POS.START || holdPos === HOLD_POS.END) {
                    chart.cursor = "col-resize";
                    chart.html.style.cursor = chart.cursor;
                    if (self.chartArea.contains(self.placeholder)) {
                        self.placeholder.parentNode.removeChild(self.placeholder);
                    }
                    
                    return;
                }
            }
            
            chart.html.style.cursor = "";
            if (_.isNil(self.placeholder)) {
                self.placeholder = pDiv.cloneNode(true);
                self.placeholder.className = "gantt-holder";
                let width = self.pasteBand.blockSize * chart.unitToPx - 1;
                let cssText = `; position: absolute; width: ${width}px; height: ${chart.chartWidth}px; user-select: none;
                    border: 1px solid #AAB7B8; z-index: 3000; background-color: #FFF; box-shadow: inset 1px 2px 3px 1px #AAB7B8;`;
                self.placeholder.style.cssText = cssText;
                self.placeholder.addEventListener("mousedown", () => {
                    if (!self.metaholder.hasOwnProperty("start") || event.button !== 0) return;
                    self.metaholder.id = `pgc${support.replaceAll(util.randomId(), '-', '')}`;
                    self.metaholder.isPressed = true;
                    self.addChartWithType(self.pasteBand.typeName, {
                        id: self.metaholder.id,
                        parent: self.metaholder.ancestorChart.parent || self.metaholder.ancestorChart.id,
                        start: self.metaholder.start,
                        end: self.metaholder.end,
                        lineNo: self.metaholder.lineNo,
                        color: self.pasteBand.color,
                        zIndex: self.pasteBand.zIndex || 1000
                    });
                    
//                    if (self.mode === "paste") {
//                        self.metaholder.tempStart = self.metaholder.start;
//                        self.metaholder.tempEnd = self.metaholder.end;
//                    }
                    
                    document.addEventListenerNS("mousemove.paste", manipulationMode.pasteMove.bind(self));
                    document.addEventListenerNS("mouseup.paste", manipulationMode.pasteUp.bind(self));
                    self.placeholder.parentNode.removeChild(self.placeholder);
                });
            }
            
            let parent = chart;
            if (chart.parent) {
                parent = self.gcChart[chart.lineNo][chart.parent];
            }
            
            let offsetLeft = parent.html.getBoundingClientRect().left + document.body.scrollLeft,
                nearestLine = Math.floor((event.pageX - offsetLeft) / chart.unitToPx) + parent.start;
            let startX = event.pageX - nearestLine % self._getSnatchInterval(parent) * parent.unitToPx,
                startY = startX + self.pasteBand.blockSize * parent.unitToPx - 1;
            let startOverElm = document.elementFromPoint(startX, event.pageY);
            let endOverElm = document.elementFromPoint(startY, event.pageY);
            if (startOverElm.classList.contains("gantt-holder")) {
                startOverElm = support.underElementFromPoint(startOverElm, startX, event.pageY);
            }
            
            if (endOverElm.classList.contains("gantt-holder")) {
                endOverElm = support.underElementFromPoint(endOverElm, startY, event.pageY);
            }
            
            let startId = startOverElm.getAttribute("id"),
                endId = endOverElm.getAttribute("id"),
                parentId = parent.html.getAttribute("id"), 
                startLine = nearestLine - nearestLine % self._getSnatchInterval(parent), 
                endLine = startLine + self.pasteBand.blockSize;
            if (startId !== endId) {
                startLine = Math.max(parent.start, startLine);
                if (startOverElm.classList.contains("nts-ganttchart") && startId !== parentId) {
                    let startOverChart = self.gcChart[parent.lineNo][startId.split('-')[1]];
                    if (!startOverChart.canPaste) {
                        startLine = startOverChart.end;   
                    } 
                }
                
                endLine = Math.min(parent.end, endLine);
                if (endOverElm.classList.contains("nts-ganttchart") && endId !== parentId) {
                    let endOverChart = self.gcChart[parent.lineNo][endId.split('-')[1]];
                    if (!endOverChart.canPaste) {
                        endLine = endOverChart.start;
                    }
                }
            } else {
                let startOverChart = self.gcChart[parent.lineNo][startId.split('-')[1]];
                if (!startOverChart.canPaste) {
                    chart.html.style.cursor = "not-allowed";
                    return;
                }
                
                startLine = Math.max(parent.start, startLine);
                endLine = Math.min(parent.end, endLine);
            }
            
            if (startLine === endLine) return;
            self.metaholder.ancestorChart = parent;
            self.metaholder.start = startLine;
            self.metaholder.end = endLine;
            self.metaholder.lineNo = parent.lineNo;
            let length = (endLine - startLine) * parent.unitToPx - 1,
                currentLength = parseFloat(self.placeholder.style.width || 0);
            if (length !== currentLength) {
                self.placeholder.style.width = `${length}px`;
            }
            
            let posTop = parent.origin[1] + parent.lineNo * parent.lineWidth + Math.floor((parent.lineWidth - parent.chartWidth) / 2),
                posLeft = parent.origin[0] + self.metaholder.start * chart.unitToPx;
            self.placeholder.style.top = `${posTop}px`;
            self.placeholder.style.left = `${posLeft}px`;
            if (!self.chartArea.contains(self.placeholder)) {
                self.chartArea.appendChild(self.placeholder);
            }
        }
        
    }
    
    class DefinedType {
        
        name: string;
        parent: string;
        title: string;
        lineNo: number;
        color: string;
        zIndex: number;
        hide: boolean;
        followParent: boolean;
        canSlide: boolean;
        cursor: string;
        limitStartMin: number;
        limitStartMax: number;
        limitEndMin: number;
        limitEndMax: number;
        fixed: CHART_FIXED;
        unitToPx: number;
        locked: boolean;
        chartWidth: number;
        lineWidth: number;
        snatchInterval: number;
        drawerSize: number;
        bePassedThrough: boolean;
        pin: boolean;
        pruneOneSlide: boolean;
        rollup: boolean;
        roundEdge: boolean;
        canPaste: boolean;
        canPasteResize: boolean;
        resizeFinished: any;
        dropFinished: any;
        pastingResizeFinished: any;
        
        constructor(options, ruler: Ruler) {
            let self = this;
            this.name = self.orElse(options.name);
            this.parent = self.orElse(options.parent);
            this.title = self.orElse(options.title);
            this.lineNo = self.orElse(options.lineNo);
            if (_.isFunction(options.color) && options.color.name === "__lg") {
                self.color = options.color();
                Object.defineProperty(self, "logColor", {
                    get() { return self.color; },
                    set(val) {
                        let list = (ruler.definedType[self.name] || {}).list;
                        _.forEach(list, c => {
                            c.style.backgroundColor = val;
                        });
                        
                        self.color = val;
                    }
                });
                
                options.color = (val) => {
                    self.logColor = val;
                };
            } else {
                this.color = options.color;
            }
            
            if (_.isFunction(options.zIndex) && options.zIndex.name === "__lg") {
                self.zIndex = options.zIndex();
                Object.defineProperty(self, "logZIndex", {
                    get() { return self.zIndex; },
                    set(val) {
                        let list = (ruler.definedType[self.name] || {}).list;
                        _.forEach(list, c => {
                            c.style.zIndex = val;
                        });
                        
                        self.zIndex = val;
                    }
                });
                
                options.zIndex = (val) => {
                    self.logZIndex = val;
                };
            } else {
                this.zIndex = options.zIndex;
            }
            
            if (_.isFunction(options.hide) && options.hide.name === "__lg") {
                self.hide = options.hide();
                Object.defineProperty(self, "logHide", {
                    get() { return self.hide; },
                    set(val) {
                        let list = (ruler.definedType[self.name] || {}).list;
                        _.forEach(list, c => {
                            c.style.display = (val ? "none" : "block"); 
                        });
                    }
                });
                
                options.hide = (val) => {
                    self.logHide = val;
                };
            } else {
                self.hide = options.hide;
            }
            
            this.followParent = self.orElse(options.followParent);
            this.canSlide = self.orElse(options.canSlide);
            this.cursor = self.orElse(options.cursor);
            this.limitStartMin = self.orElse(options.limitStartMin);
            this.limitStartMax = self.orElse(options.limitStartMax);
            this.limitEndMin = self.orElse(options.limitEndMin);
            this.limitEndMax = self.orElse(options.limitEndMax);
            this.unitToPx = self.orElse(options.unitToPx);
            this.fixed = self.orElse(options.fixed);
            this.locked = self.orElse(options.locked);
            this.chartWidth = self.orElse(options.chartWidth);
            this.lineWidth = self.orElse(options.lineWidth);
            this.snatchInterval = self.orElse(options.snatchInterval);
            this.drawerSize = self.orElse(options.drawerSize);
            this.bePassedThrough = self.orElse(options.bePassedThrough);
            this.pin = self.orElse(options.pin);
            this.pruneOnSlide = self.orElse(options.pruneOnSlide);
            this.rollup = self.orElse(options.rollup);
            this.roundEdge = self.orElse(options.roundEdge);
            this.canPaste = self.orElse(options.canPaste);
            this.canPasteResize = options.canPasteResize;
            this.resizeFinished = options.resizeFinished;
            this.dropFinished = options.dropFinished;
            this.pastingResizeFinished = options.pastingResizeFinished;
        }
        
        orElse(option: any) {
            return _.isFunction(option) ? option() : option;
        }
    }
    
    class GanttChart {
        
        lineNo: number;
        id: string;
        parent: string;
        children: Array<GanttChart> = [];
        title: string;
        definedType: string;
        maxArea: number = 1000;
        start: number;
        end: number;
        initStart: number;
        initEnd: number;
        zIndex: number = 1000;
        color: string = "#b8f441";
        origin: Array<any> = [0, 0];
        chartWidth: number = 15;
        lineWidth: number = 20;
        unitToPx: number = 10;
        snatchInterval: number = 1;
        canSlide: boolean = false;
        limitStartMin: number = 0;
        limitStartMax: number;
        limitEndMin: number = 0;
        limitEndMax: number;
        followParent: boolean = false;
        fixed: CHART_FIXED = CHART_FIXED.NONE;
        drawerSize: number = 3;
        cursor: string;
        bePassedThrough: boolean = true;
        locked: boolean = false;
        rollup: boolean = false;
        pin: boolean = false;
        pruneOnSlide: boolean = false;
        roundEdge: boolean = false;
        canPaste: boolean = false;
        canPasteResize: boolean = false;
        html: HTMLElement;
        resizeFinished: any;
        dropFinished: any;
        pastingResizeFinished: any;
        
        constructor(options: any) {
            let self = this;
            if (!_.keys(options).length) return;
            self.limitStartMax = options.limitStartMax || options.maxArea || self.maxArea;
            self.limitEndMax = options.limitEndMax || options.maxArea || self.maxArea;
            $.extend(self, options);
            self.initStart = self.start;
            self.initEnd = self.end;
        }
        
        newChart() {
            
            if (_.isNil(this.id)) {
                warning.push(new Warn("Not set id"));
                return 1;
            }
            
            if (_.isNil(this.lineNo)) {
                warning.push(new Warn("Not set lineNo"));
                return 1;
            }
            
            if (this.limitStartMin > this.start || this.limitStartMax < this.start) {
                warning.push(new Warn(`${this.lineNo}-${this.id} start is out of range.`));
                return 1;
            }
            
            if (this.limitEndMin > this.end || this.limitEndMax < this.end) {
                warning.push(new Warn(`${this.lineNo}-${this.id} end is out of range.`));
            }
            
            let self = this,
                posTop = self.origin[1] + self.lineNo * self.lineWidth + Math.floor((self.lineWidth - self.chartWidth) / 2),
                posLeft = self.origin[0] + self.start * self.unitToPx,
                chart = document.createElement("div");
            chart.setAttribute("id", `${self.lineNo}-${self.id}`);
            chart.className = "nts-ganttchart";
            chart.style.cssText = `; position: absolute; top: ${posTop}px; left: ${posLeft}px; z-index: ${self.zIndex}; text-overflow: ellipsis;
                overflow: hidden; white-space: nowrap; width: ${(self.end - self.start) * self.unitToPx - 1}px; height: ${self.chartWidth}px;
                line-height: ${self.chartWidth}px; background-color: ${self.color}; cursor: ${self.cursor}; border: 1px solid #AAB7B8; font-size: 13px;`;
            
            self.html = chart;
            self.html.onselectstart = () => { return false; };
        }
        
        reposition(style: any) {
            let self = this;
            
//            if ((_.has(style, "start") && style.start < self.limitStart)
//                || (_.has(style, "end") && style.end > self.limitEnd)) return;
            
            if (_.has(style, "start")) {
                self.start = style.start;    
            }
            
            if (_.has(style, "end")) {
                self.end = style.end;
            }
            
            if (_.has(style, "initStart")) {
                self.initStart = style.initStart;
            }
            
            if (_.has(style, "initEnd")) {
                self.initEnd = style.initEnd;
            }
            
            if (_.has(style, "top")) {
                self.html.style.top = `${style.top}px`;
            }
            
            if (_.has(style, "left")) {
                self.html.style.left = `${style.left}px`;
            }
            
            if (_.has(style, "width")) {
                if (style.width <= 0) {
                    if (self.html.parentNode) {
                        self.html.style.width = "0px";
                        self.html.parentNode.removeChild(self.html);
                    }
                } else {
                    self.html.style.width = `${style.width}px`;
                }
            }
            
        }
    }
    
    module support {
        
        export let GGC: string = "GHOSTCHART";
        document.addEventListenerNS = Element.prototype.addEventListenerNS = addEventListener;
        document.removeEventListenerNS = Element.prototype.removeEventListenerNS = removeEventListener;
        
        export function ChartEvent(name: string, params?: any) {
            let evt;
            params = params || { bubbles: false, cancelable: false, detail: null };
            try {
                evt = new Event(name, params);
            } catch(e) {
                evt = document.createEvent("CustomEvent");
                evt.initCustomEvent(name, params.bubbles, params.cancelable, params.detail);
            }
            
            return evt;
        }
        
        function addEventListener(event, cb, opts) {
            let self = this;
            if (!self.ns) self.ns = {};
            if (!self.ns[event]) self.ns[event] = [ cb ];
            else self.ns[event].push(cb);
            self.addEventListener(event.split(".")[0], cb, opts);  
        };
        
        function removeEventListener(event, cb?) {
            let self = this;
            if (!self.ns) return;
            if (cb) {
                let keys = Object.keys(self.ns).filter(function(k) {
                    return (k === event || k === event.split(".")[0])
                            && self.ns[k].indexOf(cb) > -1;
                });
                
                let key;
                if (keys.length > 0) {
                    key = keys[0];
                    self.ns[key].splice(self.ns[key].indexOf(cb), 1);
                    if (self.ns[key].length === 0) delete self.ns[key];
                }
                self.removeEventListener(event.split(".")[0], cb);
                return;
            }
            
            if (!self.ns[event]) return;
            self.ns[event].forEach(function(e) {
                self.removeEventListener(event.split(".")[0], e); 
            });
            delete self.ns[event];
        }
        
        export function underElementFromPoint(pointElm: HTMLElement, x: number, y: number) {
            let pe = pointElm.style.getPropertyValue("pointer-events"),
                priority = pointElm.style.getPropertyPriority("pointer-events");
            pointElm.style.setProperty("pointer-events", "none", "important");
            let under = document.elementFromPoint(x, y);
            pointElm.style.setProperty("pointer-events", pe, priority);
            return under;
        }
        
        export function replaceAll(text: string, sym: string, rep: string) {
            if (_.isNil(text) || _.isNil(sym)) return;
            if (typeof String.prototype.replaceAll === "function") {
                return text.replaceAll(sym, rep);
            }
            
            return text.replace(new RegExp(sym, "g"), rep);
        }
        
        export function elementsFromPoint(x: number, y: number) {
            if (typeof document.elementsFromPoint === "function") { 
                return document.elementsFromPoint(x, y);
            }
            
            if (typeof document.msElementsFromPoint === "function") {
                return document.msElementsFromPoint(x, y);
            }
        }
        
        export function nodeInsertedObserver(elm: HTMLElement, cb: any) {
            let MutationObserver = window.MutationObserver || window.WebKitMutationObserver;
            if (MutationObserver) {
                let observer = new MutationObserver((mutations) => {
                    let insertedNodes = [];
                    mutations.forEach(r => r.addedNodes.length && insertedNodes.push(...r.addedNodes));
                    if (_.isFunction(cb)) {
                        cb(insertedNodes);
                    }
                });
                
                observer.observe(elm, { childList: true, subtree: true });
                return;
            }
            
            elm.addEventListener("DOMNodeInserted", cb);
        }
        
        export function closest(el, selector) {
            let matches;
            ['matches', 'webkitMatchesSelector', 'mozMatchesSelector', 'msMatchesSelector', 'oMatchesSelector'].some(function(fn) {
                if (typeof document.body[fn] === 'function') {
                    matches = fn;
                    return true;
                }
                return false;
            });
        
            let parent;
            while (el) {
                parent = el.parentElement;
                if (parent && parent[matches](selector)) {
                    return parent;
                }
                el = parent;
            }
        }
        
        export function isInvisible(el: HTMLElement) {
            return el.style.display === "none" || el.style.width === "0px";
        }
    }
    
    module manipulationMode {
        export type Type = "normal" | "paste" | "pasteFlex";
        export class PasteOptions {
            typeName: string;
            color: string;
            blockSize: number;
            zIndex: number;
        }
        
        export class Metaholder {
            // A pointing chart (might not be the parent one) to place new chart into
            ancestorChart: GanttChart;
            parentChart: GanttChart;
            id: string;
            start: number;
            end: number;
            lineNo: number;
            isPressed: boolean;
        }
        
        export class Metaresize {
            graspPos: HOLD_POS;
            chart: GanttChart; 
            start: number;
            end: number;
            adjChart: GanttChart;
            offsetLeft: number;
            isPressed: boolean;
        }
        
        export function pasteMove() {
            let self: Ruler = this;
            if (!self.metaholder.isPressed /*|| self.mode === "paste"*/) return;
            let chart = self.metaholder.ancestorChart;
            let startLine = chart.start, endLine = chart.end;
            if (chart.parent) {
                let parent = self.gcChart[chart.lineNo][chart.parent];
                self.metaholder.parentChart = parent;
                if (chart.start < parent.start) {
                    startLine = parent.start;
                }
                
                if (chart.end > parent.end) {
                    endLine = parent.end;
                }
            }
            
            let offsetLeft = chart.html.getBoundingClientRect().left + document.body.scrollLeft,
                nearestLine = Math.round((event.pageX - offsetLeft) / chart.unitToPx) + startLine;
            if (nearestLine < self.metaholder.start) {
                nearestLine = nearestLine - nearestLine % self._getSnatchInterval(chart);
                let start = Math.max(nearestLine, startLine);
                self.extend(self.metaholder.lineNo, self.metaholder.id, start);
                self.metaholder.tempStart = start;
            } else if (nearestLine > self.metaholder.end) {
                nearestLine = nearestLine - nearestLine % self._getSnatchInterval(chart) + self._getSnatchInterval(chart);
                let end = Math.min(nearestLine, endLine);
                self.extend(self.metaholder.lineNo, self.metaholder.id, null, end);
                self.metaholder.tempEnd = end;
            } else {
                self.extend(self.metaholder.lineNo, self.metaholder.id, self.metaholder.start, self.metaholder.end);
                self.metaholder.tempStart = self.metaholder.start;
                self.metaholder.tempEnd = self.metaholder.end;
            }
        }
        
        export function pasteUp() {
            let self: Ruler = this;
            document.removeEventListenerNS("mousemove.paste");
            document.removeEventListenerNS("mouseup.paste");   
            let parent = self.metaholder.parentChart || self.metaholder.ancestorChart;
            let target: GanttChart = self.gcChart[self.metaholder.lineNo][self.metaholder.id], removed = [];
            _.forEach(parent.children, (child: GanttChart) => {
                if (child.id === self.metaholder.id || child.html.style.display === "none") return;
                if (target.start < child.start) {
                    if (target.end === child.start && child.definedType === target.definedType) {
                        target.reposition({ end: child.end, width: (child.end - target.start) * target.unitToPx - 1 });
                        self.remove(child, true);
                        removed.push(child.id);
                    } else if (child.start < target.end && target.end < child.end) {
                        if (!child.canPaste) {
                            target.reposition({ end: child.start, width: (child.start - target.start) * target.unitToPx - 1 });
                        } else if (child.definedType !== target.definedType) {
                            child.reposition({ start: target.end, left: target.end * child.unitToPx, width: (child.end - target.end) * child.unitToPx - 1 });
                        } else {
                            target.reposition({ end: child.end, width: (child.end - target.start) * child.unitToPx - 1 });
                            self.remove(child, true);
                            removed.push(child.id);
                        }  
                    } else if (target.end >= child.end && child.canPaste) {
                        self.remove(child, true);
                        removed.push(child.id);
                    }
                } else if (target.start === child.start) {
                    if (target.end < child.end) {
                        if (target.definedType !== child.definedType) {
                            child.reposition({ start: target.end, left: target.end * child.unitToPx, width: (child.end - target.end) * child.unitToPx - 1 });
                        } else {
                            self.remove(target, true);
                            removed.push(target.id);
                            return false;
                        }
                    } else {
                        self.remove(child, true);
                        removed.push(child.id);
                    }
                } else if (target.start < child.end) {
                    if (target.end < child.end) {
                        if (target.definedType !== child.definedType) {
                            let id = `pgc${support.replaceAll(util.randomId(), '-', '')}`;
                            self.addChartWithType(child.definedType, {
                                id: id,
                                parent: child.parent,
                                start: target.end,
                                end: child.end,
                                lineNo: child.lineNo,
                                color: child.color,
                                zIndex: child.zIndex
                            }); 
                            
                            child.reposition({ end: target.start, width: (target.start - child.start) * child.unitToPx - 1 });
                        } else {
                            self.remove(target, true);
                            removed.push(target.id);
                            return false;
                        }
                    } else {
                        if (!child.canPaste) {
                            let left = parseFloat(target.html.style.left) + (child.end - target.start) * target.unitToPx;
                            target.reposition({ start: child.end, left: left, width: (target.end - child.end) * target.unitToPx - 1 });
                        } else if (target.definedType !== child.definedType) {
                            child.reposition({ end: target.start, width: (target.start - child.start) * child.unitToPx - 1 });
                        } else {
                            child.reposition({ end: target.end, width: (target.end - child.start) * child.unitToPx - 1 });
                            self.remove(target, true);
                            removed.push(target.id);
                            target = child;
                        }
                    }
                } else if (target.start === child.end && target.definedType === child.definedType) {
                    child.reposition({ end: target.end, width: (target.end - child.start) * child.unitToPx - 1 });
                    self.remove(target, true);
                    removed.push(target.id);
                    target = child;
                }
            });
            
            if (removed.length > 0) {
                removed.forEach(r => {
                    delete self.gcChart[parent.lineNo][r];
                });
                
                _.remove(parent.children, (child: GanttChart) => {
                    return _.some(removed, c => c === child.id);
                });
            }
            
            if (typeof target.pastingResizeFinished === "function") {
                target.pastingResizeFinished(target.lineNo, target.definedType, self.metaholder.tempStart, self.metaholder.tempEnd);
            }
            
            self.metaholder = {};
        }
        
        export function resizeMove() {
            let self: Ruler = this;
            let chart = self.metaresize.chart;  
            let parent = self.gcChart[chart.lineNo][chart.parent];
            if (!parent) return;
            let offsetLeft = self.metaresize.offsetLeft + document.body.scrollLeft,
                nearestLine = Math.round((event.pageX - offsetLeft) / chart.unitToPx) + self.metaresize.start;
            if (nearestLine % self._getSnatchInterval(chart) !== 0) return;
            if (self.metaresize.graspPos === HOLD_POS.END) {
                if (nearestLine > self.metaresize.end) {
                    let cantPasteChart;
                    if (!self.metaresize.adjChart) {
                        let minStart = 9999;
                        _.forEach(parent.children, (child: GanttChart) => {
                            if (support.isInvisible(child.html)) return;
                            if (nearestLine >= child.start && self.metaresize.start < child.start && child.start < minStart
                                && child.id !== chart.id && child.canPaste) {
                                self.metaresize.adjChart = child;
                                minStart = child.start;   
                            } 
                            
                            if (!child.canPaste && nearestLine >= child.start && nearestLine <= child.end) {
                                cantPasteChart = child;
                            }
                        });
                    } else {
                        _.forEach(parent.children, (child: GanttChart) => {
                            if (support.isInvisible(child.html)) return;
                            if (!child.canPaste && nearestLine >= child.start && nearestLine <= child.end) {
                                cantPasteChart = child;
                                return false;
                            }
                        });
                    }
                    
                    if (!self.metaresize.adjChart) {
                        let end = Math.min(nearestLine, parent.end);
                        if (cantPasteChart) {
                            end = Math.min(end, cantPasteChart.start);
                        }
                        
                        chart.reposition({ end: end, width: (end - self.metaresize.start) * chart.unitToPx - 1 });
                        self.metaresize.tempEnd = end;
                    } else if (self.metaresize.chart.definedType !== self.metaresize.adjChart.definedType) {
                        let adjChart = self.metaresize.adjChart;
                        if (cantPasteChart && adjChart.start >= cantPasteChart.end) {
                            chart.reposition({ end: cantPasteChart.start, width: (cantPasteChart.start - self.metaresize.start) * chart.unitToPx - 1 });
                            self.metaresize.tempEnd = cantPasteChart.start;
                            adjChart.reposition({ 
                                start: cantPasteChart.end,
                                left: parseFloat(adjChart.html.style.left) + (cantPasteChart.end - adjChart.start) * adjChart.unitToPx,
                                width: (adjChart.end - cantPasteChart.end) * adjChart.unitToPx - 1
                            });
                        } else { 
                            let maxAdjStart;
                            if (adjChart.end % self._getSnatchInterval(adjChart) !== 0) {
                                maxAdjStart = adjChart.end - adjChart.end % self._getSnatchInterval(adjChart);
                            } else {
                                maxAdjStart = adjChart.end - self._getSnatchInterval(adjChart);
                            }
                            
                            let end = Math.min(nearestLine, maxAdjStart, parent.end);
                            chart.reposition({ end: end, width: (end - self.metaresize.start) * chart.unitToPx - 1 });
                            self.metaresize.tempEnd = end;
                            self.metaresize.adjChart.reposition({ 
                                start: end, 
                                left: parseFloat(adjChart.html.style.left) + (end - adjChart.start) * adjChart.unitToPx,
                                width: (adjChart.end - end) * adjChart.unitToPx - 1 
                            });
                        }
                    } else {
                        let adjChart = self.metaresize.adjChart;
                        let end = Math.min(Math.max(nearestLine, adjChart.end), parent.end);
                        chart.reposition({ end: end, width: (end - self.metaresize.start) * chart.unitToPx - 1 });
                        self.metaresize.tempEnd = end;
                        self.remove(adjChart);
                    }
                } else {
                    let minEnd, snatch = self._getSnatchInterval(chart), cantPasteChart;
                    _.forEach(parent.children, (child: GanttChart) => {
                        if (support.isInvisible(child.html)) return;
                        if (!child.canPaste && child.id !== chart.id && nearestLine >= child.start && nearestLine <= child.end) {
                            cantPasteChart = child;
                            return false;
                        }
                    });
                    
                    if (!_.isNil(cantPasteChart) && chart.end <= cantPasteChart.start) {
                        chart.reposition({ end: cantPasteChart.start, width: (cantPasteChart.start - self.metaresize.start) * chart.unitToPx - 1 });
                        self.metaresize.tempEnd = cantPasteChart.start;
                        if (self.metaresize.adjChart && self.metaresize.adjChart.id !== cantPasteChart.id) {
                            let adjChart = self.metaresize.adjChart;
                            self.metaresize.adjChart.reposition({
                                start: cantPasteChart.end,
                                left: parseFloat(adjChart.html.style.left) - (adjChart.start - cantPasteChart.end) * adjChart.unitToPx,
                                width: (adjChart.end - cantPasteChart.end) * adjChart.unitToPx - 1
                            });
                        }
                    } else {
                        if (self.metaresize.start % self._getSnatchInterval(chart) === 0) {
                            minEnd = self.metaresize.start + snatch;
                        } else {
                            minEnd = self.metaresize.start + (snatch - (self.metaresize.start % snatch));
                        }
                        
                        let end = Math.max(nearestLine, minEnd);
                        chart.reposition({ end: end, width: (end - self.metaresize.start) * chart.unitToPx - 1 });
                        self.metaresize.tempEnd = end;
                        if (self.metaresize.adjChart) {
                            let adjChart = self.metaresize.adjChart;
                            self.metaresize.adjChart.reposition({
                                start: end,
                                left: parseFloat(adjChart.html.style.left) - (adjChart.start - end) * adjChart.unitToPx,
                                width: (adjChart.end - end) * adjChart.unitToPx - 1    
                            });
                        }
                    }
                }
            } else if (self.metaresize.graspPos === HOLD_POS.START) {
                if (nearestLine < self.metaresize.start) {
                    let cantPasteChart;
                    if (!self.metaresize.adjChart) {
                        let maxEnd = 0;
                        _.forEach(parent.children, (child: GanttChart) => {
                            if (support.isInvisible(child.html)) return;
                            if (nearestLine <= child.end && child.start < self.metaresize.start && child.end > maxEnd
                                && child.id !== chart.id && child.canPaste) {
                                self.metaresize.adjChart = child;
                                maxEnd = child.end;
                            }
                            
                            if (!child.canPaste && nearestLine >= child.start && nearestLine <= child.end) {
                                cantPasteChart = child;
                            }
                        });
                    } else {
                        _.forEach(parent.children, (child: GanttChart) => {
                            if (support.isInvisible(child.html)) return;
                            if (!child.canPaste && nearestLine >= child.start && nearestLine <= child.end) {
                                cantPasteChart = child;
                                return false;
                            }
                        });
                    }
                    
                    if (!self.metaresize.adjChart) {
                        let start = Math.max(nearestLine, parent.start);
                        if (cantPasteChart) {
                            start = Math.max(start, cantPasteChart.end);
                        }
                        
                        chart.reposition({ 
                            start: start, 
                            left: self.metaresize.left - (self.metaresize.start - start) * chart.unitToPx,
                            width: (self.metaresize.end - start) * chart.unitToPx - 1
                        });
                        
                        self.metaresize.tempStart = start;
                    } else if (self.metaresize.chart.definedType !== self.metaresize.adjChart.definedType) {
                        let minAdjEnd, adjChart = self.metaresize.adjChart, snatch = self._getSnatchInterval(chart);
                        if (cantPasteChart && adjChart.end <= cantPasteChart.start) {
                            chart.reposition({ 
                                start: cantPasteChart.end, 
                                left: self.metaresize.left - (self.metaresize.start - cantPasteChart.end) * chart.unitToPx,
                                width: (self.metaresize.end - cantPasteChart.end) * chart.unitToPx - 1
                            });
                            
                            self.metaresize.tempStart = cantPasteChart.end;
                            adjChart.reposition({
                                end: cantPasteChart.start,
                                width: (cantPasteChart.start - adjChart.start) * adjChart.unitToPx - 1
                            });
                        } else {
                            if (adjChart.start % snatch === 0) {
                                minAdjEnd = adjChart.start + snatch;
                            } else {
                                minAdjEnd = adjChart.start + (snatch - (adjChart.start % snatch)); 
                            }
                            
                            let end = Math.max(nearestLine, minAdjEnd, parent.start);
                            chart.reposition({
                                start: end,
                                left: self.metaresize.left - (self.metaresize.start - end) * chart.unitToPx,
                                width: (self.metaresize.end - end) * chart.unitToPx - 1
                            });
                            
                            self.metaresize.tempStart = end;
                            adjChart.reposition({
                                end: end,
                                width: (end - adjChart.start) * adjChart.unitToPx - 1
                            });
                        }
                    } else {
                        let adjChart = self.metaresize.adjChart;
                        let start = Math.max(Math.min(nearestLine, adjChart.start), parent.start);
                        chart.reposition({ 
                            start: start, 
                            left: self.metaresize.left - (self.metaresize.start - start) * chart.unitToPx,
                            width: (self.metaresize.end - start) * chart.unitToPx - 1
                        });
                        
                        self.metaresize.tempStart = start;
                        self.remove(adjChart);
                    }
                } else {
                    let minStart, snatch = self._getSnatchInterval(chart), cantPasteChart;
                    _.forEach(parent.children, (child: GanttChart) => {
                        if (support.isInvisible(child.html)) return;
                        if (!child.canPaste && nearestLine >= child.start && nearestLine <= child.end) {
                            cantPasteChart = child;
                            return false;
                        }
                    });
                    
                    if (cantPasteChart && chart.start >= cantPasteChart.end) {
                        chart.reposition({
                            start: cantPasteChart.end,
                            left: self.metaresize.left + (cantPasteChart.end - self.metaresize.start) * chart.unitToPx,
                            width: (self.metaresize.end - cantPasteChart.end) * chart.unitToPx - 1
                        });
                        
                        self.metaresize.tempStart = cantPasteChart.end;
                        let adjChart = self.metaresize.adjChart;    
                        if (adjChart) {
                            adjChart.reposition({
                                end: cantPasteChart.start,
                                width: (cantPasteChart.start - adjChart.start) * adjChart.unitToPx - 1
                            });
                        }
                    } else {
                        if (self.metaresize.end % snatch === 0) {
                            minStart = self.metaresize.end - snatch;
                        } else {
                            minStart = self.metaresize.end - self.metaresize.end % snatch;
                        }
                        
                        let start = Math.min(nearestLine, minStart, parent.end);
                        chart.reposition({
                            start: start,
                            left: self.metaresize.left + (start - self.metaresize.start) * chart.unitToPx,
                            width: (self.metaresize.end - start) * chart.unitToPx - 1
                        });
                        
                        self.metaresize.tempStart = start;
                        if (self.metaresize.adjChart) {
                            let adjChart = self.metaresize.adjChart;
                            adjChart.reposition({
                                end: start,
                                width: (start - adjChart.start) * adjChart.unitToPx - 1
                            });
                        }
                    }
                }
            }
        }
        
        export function resizeUp() {
            let self: Ruler = this;
            document.removeEventListenerNS("mousemove.pasteResize");
            document.removeEventListenerNS("mouseup.pasteResize");
            let chart = self.metaresize.chart;
            if (typeof chart.pastingResizeFinished === "function") {
                chart.pastingResizeFinished(chart.lineNo, chart.definedType, self.metaresize.tempStart, self.metaresize.tempEnd);
            }
            
            self.metaresize = {};
        }
    }
    
    class Warn {
        message: string;
        constructor(msg: string) {
            this.message = msg;
        }
    }
    
    enum CHART_FIXED {
        NONE = "None",
        START = "Start",
        END = "End",
        BOTH = "Both"
    } 
    
    enum HOLD_POS {
        START = "Start",
        END = "End",
        BODY = "Body",
        OUT = "Out"
    }
}