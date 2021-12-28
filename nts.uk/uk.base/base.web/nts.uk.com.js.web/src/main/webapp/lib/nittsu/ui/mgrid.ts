module nts.uk.ui.mgrid {
    
    const MGRID = "mgrid";
    const FIXED = "mgrid-fixed";
    const FREE = "mgrid-free";
    const HEADER = "mgrid-header";
    const BODY = "mgrid-body";
    const Default = "default";
    const SheetDef = "_sheetDef";
    const DISTANCE = 1;
    const BODY_ROW_HEIGHT = 29;
    const SUM_HEIGHT = 27;
    let defaultOptions = { columns: [], features: [] };
    let _scrollWidth, _maxFixedWidth = 0, _maxFreeWidth, _columnsMap = {}, _dataSource, _secColumn = {},
        _hasFixed, _validators = {}, _mDesc, _mEditor, _cloud, _hr, _direction, _errors = [], 
        _errorColumns, _errorsOnPage, _$grid, _pk, _pkType, _summaries, _objId, _getObjId, _idIntpl, _hasSum, _pageSize, _currentPage, _currentSheet, _start, _end, 
        _headerHeight, _zeroHidden, _paging = false, _sheeting = false, _copie = false, _mafollicle = {}, _vessel = () => _mafollicle[_currentPage][_currentSheet], 
        _cstifle = () => _mafollicle[SheetDef][_currentSheet].columns, _mafCurrent = () => _mafollicle[_currentPage], _specialColumn = {}, _specialLinkColumn = {}, _histoire = [], _flexFitWidth,
        _copieer, _collerer, _fixedHiddenColumns = [], _hiddenColumns = [], _fixedColumns, _selected = {}, _dirties = {}, _headerWrappers, _bodyWrappers, _sumWrappers, _linkage = [],
        _fixedControlMap = {}, _cellStates, _features, _leftAlign, _header, _rid = {}, _remainWidth = 240, _remainHeight = 190, _redimension = false,
        _prtDiv = document.createElement("div"), _prtCell = document.createElement("td");
    
    export class MGrid {
        
        $container: HTMLElement;
        headers: Array<any>;
        bodies: Array<any>;
        fixedHeader: any = { containerClass: FIXED };
        fixedBody: any = { containerClass: FIXED };
        header: any = { containerClass: FREE };
        body: any = { containerClass: FREE };
        fixedSummaries: any = { containerClass: FIXED + "-summaries" };
        summaries: any = { containerClass: FREE + "-summaries" };
        pload: any; 
        
        constructor($container: HTMLElement, options: any) {
            let self = this;
            self.$container = $container;
            _$grid = $($container);
            _pk = options.primaryKey;
            _pkType = options.primaryKeyDataType;
            _features = options.features;
            _objId = options.userId;
            _getObjId = options.getUserId;
            _idIntpl = options.idGen;
            _errorColumns = options.errorColumns;
            _errorsOnPage = options.errorsOnPage;
            _headerHeight = options.headerHeight;
            _zeroHidden = options.hideZero;
            if (options.useOptions) {
                _.assignIn(self, options);
            } else {
//            _.assignIn(self, _.cloneDeep(options));
                ti.cloneDeep(options, self);
            }
            self.makeDefault();  
        }
        
        /**
         * MakeDefault.
         */
        makeDefault() {
            let self = this;
            self.$container.tabIndex = -1;
            self.fixedHeader = _.assignIn(self.fixedHeader, _.cloneDeep(defaultOptions), { ntsControls: self.ntsControls });
            self.fixedBody = _.assignIn(self.fixedBody, _.cloneDeep(defaultOptions));
            self.header = _.assignIn(self.header, _.cloneDeep(defaultOptions), { ntsControls: self.ntsControls });
            self.body = _.assignIn(self.body, _.cloneDeep(defaultOptions));
            _$grid.mGrid({});
            self.compreOptions();
            if (self.enter) {
                _$grid.data("enterDirect", self.enter);
            }
            if (!_.isNil(self.subWidth)) {
                _remainWidth = parseFloat(self.subWidth);
            }
            if (!_.isNil(self.subHeight)) {
                _remainHeight = parseFloat(self.subHeight);
            }
        }
        
        /**
         * CompreOptions.
         */
        compreOptions() {
            let self = this;
            if (self.notice) {
                _$grid.mGrid("option", "notice", self.notice);
            }
            
            if (self.features) {
                let columnFixFt = tn.find(self.features, tn.COLUMN_FIX);
                let colParts;
                
                let tooltipFt = tn.find(self.features, tn.INFOBULLE);
                if (tooltipFt && tooltipFt.error) {
                    khl._infobulle = _prtDiv.cloneNode(true);
                    khl._infobulle.className = khl.ERR_MSG_CLS;
                }
                let pageFt = tn.find(self.features, tn.PAGING);
                if (pageFt) {
                    _paging = true;
                    _pageSize = pageFt.pageSize;
                    _currentPage = pageFt.currentPageIndex;
                    self.pload = pageFt.loaded;
                    let remainder, pageSources, size = self.dataSource.length; 
                    let noPage = Math.floor(size / _pageSize);
                    for (let i = 0; i < noPage; i++) {
                        let s = i * _pageSize;
                        let src = _.slice(self.dataSource, s, s + _pageSize);
                        _mafollicle[i] = { dataSource: src, origDs: ti.cloneDeep(src), voilRows: {} };
                    }
                    
                    if ((remainder = size % _pageSize) !== 0) {
                        let s = _pageSize * noPage;
                        let src = _.slice(self.dataSource, s, s + _pageSize);
                        _mafollicle[noPage] = { dataSource: src, origDs: ti.cloneDeep(src), voilRows: {} };
                    }
                    
                    if (_.keys(_mafollicle).length === 0) {
                        _mafollicle[0] = { dataSource: [], origDs: [], voilRows: {} };
                    }
                } else {
                    _currentPage = Default;
                    _mafollicle[_currentPage] = { dataSource: self.dataSource, origDs: ti.cloneDeep(self.dataSource), voilRows: {} };
                }
                
                let sheetFt = tn.find(self.features, tn.SHEET);
                let headerStyles, savingFt = tn.find(self.features, tn.WIDTH_SAVE);
                _$grid.mGrid("option", "widthMem", savingFt);
                let sheetDef = {};
                if (sheetFt) {
                    _sheeting = true;
                    _currentSheet = sheetFt.initialDisplay;
                    _.forEach(sheetFt.sheets, s => {
                        let sheetCols = [];
                        _.forEach(s.columns, c => {
                            let sc = _.find(self.columns, col => {
                                if (col.group) {
                                    _.forEach(col.group, gc => {
                                        if (_.isNil(_secColumn[gc.key])) {
                                            _secColumn[gc.key] = gc;
                                        }
                                    });
                                    return col.group[0].key === c;
                                } else return col.key === c;
                            });
                            
                            if (sc && !sc.group && !_secColumn[sc.key]) {
                                _secColumn[sc.key] = sc;
                            }
                            if (sc) sheetCols.push(sc);
                        });
                        
                        sheetDef[s.name] = { columns: sheetCols, text: s.text };
                    });
                } else {
                    _currentSheet = Default;
                    sheetDef[Default] = { columns: self.columns, text: "Sheet" };
                }
                
                _mafollicle[SheetDef] = sheetDef;
                let sortingFt = tn.find(self.features, tn.SORTING),
                    movingFt = tn.find(self.features, tn.MOVING);
                
                if (columnFixFt && columnFixFt.columnSettings) {
                    let fixedColumns = _.filter(columnFixFt.columnSettings, c => c.isFixed);
                    if (_sheeting) {
                        let fixedCols = _.filter(self.columns, c => {
                            if (c.group && _.some(fixedColumns, f => f.columnKey === c.group[0].key))
                                return true;
                            return _.some(fixedColumns, f => f.columnKey === c.key);
                        });
                        
                        colParts = [ fixedCols, _cstifle() ];
                    } else {
                        colParts = _.partition(self.columns, c => { 
                            if (c.group && _.some(fixedColumns, f => f.columnKey === c.group[0].key))
                                return true;
                            return _.some(fixedColumns, f => f.columnKey === c.key);
                        });
                    }
                    
                    _.forEach(colParts, (c, i) => kt.turfSurf(c, !i, savingFt));
                    _fixedColumns = colParts[0];
                    self.fixedHeader.columns = colParts[0];
                    self.fixedHeader.height = self.headerHeight;
                    self.fixedBody.columns = colParts[0];
                    self.header.columns = colParts[1];
                    self.header.height = self.headerHeight;
                    self.body.columns = colParts[1];
                    _hasFixed = true;
                    headerStyles = tn.find(self.features, tn.HEADER_STYLE);
                    if (headerStyles) {
                        let styleParts = _.partition(headerStyles.columns, c => {
                            return _.some(fixedColumns, f => f.columnKey === c.key);
                        });
                        
                        self.fixedHeader.features.push({ name: tn.HEADER_STYLE, columns: styleParts[0] });
                        self.header.features.push({ name: tn.HEADER_STYLE, columns: styleParts[1] });
                    }
                    if (sortingFt) {
                        self.fixedHeader.features.push(sortingFt);
                        self.header.features.push(sortingFt); 
                        if (_.find(sortingFt.columnSettings, s => s.columnKey === "rowNumber")) {
                            _.forEach(_.keys(_mafollicle), m => {
                                if (m === SheetDef || !_mafollicle[m].dataSource) return;
                                let rank = {};
                                _.forEach(_mafollicle[m].dataSource, (dt, i) => {
                                    rank[dt[_pk]] = i + 1;
                                });
                                _mafollicle[m].rank = rank;
                            });
                        }
                    }
                    if (movingFt) {
                        self.fixedHeader.features.push(movingFt);
                        self.header.features.push(movingFt);
                    }
                } else {
                    self.header.columns = self.columns;
                    self.body.columns = self.columns;
                    kt.turfSurf(self.columns, null, savingFt);
                    colParts = [ self.columns ];
                    headerStyles = tn.find(self.features, tn.HEADER_STYLE);
                    if (headerStyles) {
                        self.header.features.push({ name: tn.HEADER_STYLE, columns: headerStyles.columns });
                    }
                    if (sortingFt) {
                        self.header.features.push(sortingFt);
                        if (_.find(sortingFt.columnSettings, s => s.columnKey === "rowNumber")) {
                            _.forEach(_.keys(_mafollicle), m => {
                                if (m === SheetDef || !_mafollicle[m].dataSource) return;
                                let rank = {};
                                _.forEach(_mafollicle[m].dataSource, (dt, i) => {
                                    rank[dt[_pk]] = i + 1;
                                });
                                _mafollicle[m].rank = rank;
                            });
                        }
                    }
                    if (movingFt) {
                        self.header.features.push(movingFt);
                    }
                }
                
                let summaries = tn.find(self.features, tn.SUMMARIES);
                if (summaries) {
                    _summaries = {};
                    if (colParts.length > 1) {
                        self.fixedSummaries.columns = colParts[0];
                        self.fixedSummaries.height = SUM_HEIGHT + "px";
                        self.summaries.columns = colParts[1];
                        self.summaries.height = SUM_HEIGHT + "px";
                    } else {
                        self.summaries.columns = colParts[0];
                        self.summaries.height = SUM_HEIGHT + "px";
                    }
                    
                    _.forEach(summaries.columnSettings, s => {
                        let sum = { calculator: s.summaryCalculator, formatter: s.formatter };
                        if (s.summaryCalculator === "Time") {
                            sum[_currentPage] = moment.duration("0:00");
                        } else if (s.summaryCalculator === "Number") {
                            sum[_currentPage] = 0;
                        }
                        
                        _summaries[s.columnKey] = sum;
                    });
                }
                
                let resizing = tn.find(self.features, tn.RESIZING);
                if (resizing) _redimension = true;
                if (tn.isEnable(self.features, tn.COPY)) _copie = true;
                _$grid.mGrid("option", "errOccurred", self.errorOccurred);
                _$grid.mGrid("option", "errResolved", self.errorResolved);
                _$grid.mGrid("option", "errDismissed", self.errorDismissed);
            }
        }
        
        /**
         * Create.
         */
        create() {
            let self = this;
            let left: string = "0px";
            let top: string = "0px";
            let start = performance.now();
            self.headers = _.filter([ self.fixedHeader, self.header ], h => {
                return h && h.columns;
            });
            
            self.bodies = _.filter([ self.fixedBody, self.body ], b => {
                return b && b.columns;
            });
            
            self.$container.classList.add(MGRID);
            $.data(self.$container, MGRID, self);
            let pTable = $.data(self.$container, MGRID); 
            pTable.owner = { headers: [], bodies: [] };
            
            let scrollWidth = ti.getScrollWidth();
            let headerWrappers = [], bodyWrappers = [], sumWrappers = [], headerColGroup = [], 
                bodyColGroup = [], sumColGroup = [], $fixedHeaderTbl, painters = [], controlMap = {};
            let $frag = document.createDocumentFragment();
            let freeWrapperWidth;
            self.headers.forEach((headPart, i) => {
                if (!_.isNil(self.headers[i]) && headPart.columns.length > 0) {
                    headPart.overflow = "hidden";
                    if (headPart.containerClass === FREE) {
                        freeWrapperWidth = parseFloat(self.width) - _maxFixedWidth;
                        headPart.width = freeWrapperWidth + "px";
                    }
                    headPart.isHeader = true;
                    let $headerWrapper = v.createWrapper("0px", left, headPart);
                    pTable.owner.headers.push($headerWrapper); 
                    $headerWrapper.classList.add(HEADER);
//                    self.$container.appendChild($headerWrapper);
                    $frag.appendChild($headerWrapper);
                    let tablePart = v.process($headerWrapper, headPart);
                    let $tbl = tablePart.$table;
                    painters.push(tablePart.painter);
                    
                    if (headPart.containerClass === FIXED) {
                        left = (parseInt(left) + _maxFixedWidth + DISTANCE) + "px";
                        $fixedHeaderTbl = $tbl;
                        _fixedControlMap = tablePart.controlMap;
                        kt._fixedGroups.push(tablePart.cols);
                    } else {
                        if ($fixedHeaderTbl) $fixedHeaderTbl.style.height = self.headerHeight;
                        $tbl.style.height = self.headerHeight;
                        top = (parseFloat(self.headerHeight) + DISTANCE) + "px";
                        _mafollicle[_currentPage][_currentSheet] = {};
                        _vessel().$hGroup = $tbl.querySelector("colgroup");
                        _vessel().$hBody = $tbl.querySelector("tbody");
                        _mafollicle[SheetDef][_currentSheet].hColArr = tablePart.cols;
                    }
                    headerWrappers.push($headerWrapper);
                    headerColGroup.push(tablePart.cols);
                    _.assignIn(controlMap, tablePart.controlMap);
                }
                
                if (i === self.headers.length - 1) {
                    _header = headPart;
                    _mafollicle[SheetDef][_currentSheet].levelStruct = headPart.levelStruct; 
                }
            });
            
            _headerWrappers = headerWrappers;
            let bodyHeight = parseFloat(self.height) - parseFloat(self.headerHeight);
            self.bodies.forEach((bodyPart, i) => {
                let $bodyWrapper: HTMLElement, alignLeft = 0;
                if (!_.isNil(bodyPart) && bodyPart.columns.length > 0) {
                    bodyPart.rowHeight = BODY_ROW_HEIGHT + "px";
                    
                    if (bodyPart.containerClass === FIXED) {
                        bodyPart.height = bodyHeight + "px";
                        bodyPart.width = _maxFixedWidth + "px";
                    } else {
                        bodyPart.height = bodyHeight + "px";
                        bodyPart.width = (parseFloat(self.headers[i].width) + scrollWidth) + "px";
                        alignLeft = left;
                    }
                    
                    $bodyWrapper = v.createWrapper(top, alignLeft, bodyPart);
                    pTable.owner.bodies.push($bodyWrapper);
                    bodyWrappers.push($bodyWrapper);
                    $frag.appendChild($bodyWrapper);
                    
                    if (bodyPart.containerClass === FREE && !_.isNil($bodyWrapper)) {
                        bodyPart.overflow = "scroll";
                        tc.syncDoubDirVerticalScrolls(bodyWrappers);
                        if (!self.summaries || !self.summaries.columns) {
                            tc.syncHorizontalScroll(headerWrappers[i], $bodyWrapper);
                        }
                        tc.bindVertWheel($bodyWrapper, true);
                    } else {
                        bodyPart.overflowX = "scroll";
                        tc.bindVertWheel($bodyWrapper);   
                    }
                    
                    let result = v.table($bodyWrapper, bodyPart);
                    if (bodyPart.containerClass === FREE) {
                        _vessel().$bGroup = result.$table.querySelector("colgroup");
                        _mafollicle[SheetDef][_currentSheet].bColArr = result.cols;
                    } else {
                        kt._fixedGroups.push(result.cols);
                    }
                    bodyColGroup.push(result.cols);
                }
            });
            
            _hasSum = !_.isNil(self.summaries.columns);
            let artifactOptions = { primaryKey: self.primaryKey, controlMap: controlMap, features: self.features, hasSum: _hasSum };
            _dataSource = _mafollicle[_currentPage].dataSource;
            v._voilerRows = _mafollicle[_currentPage].voilRows;
            _mafollicle[SheetDef][_currentSheet].controlMap = controlMap;
            _mafollicle[SheetDef][_currentSheet].painters = painters;
            _mafollicle[SheetDef][_currentSheet].maxWidth = _maxFreeWidth;
            if (!_.isNil(self.maxRows) && self.maxRows >= 31) {
                artifactOptions.noBlocRangee = self.maxRows;
                artifactOptions.noGrappeBloc = 2;
            } 
            v.construe(self.$container, bodyWrappers, artifactOptions);
            _bodyWrappers = bodyWrappers;
            let dWrapper = _hasFixed ? bodyWrappers[1] : bodyWrappers[0];
            _vessel().$bBody = dWrapper.querySelector("tbody");
            
            top = parseFloat(self.height) + DISTANCE - scrollWidth - SUM_HEIGHT;
            ti.calcTotal();
            [ self.fixedSummaries, self.summaries ].filter(s => s && s.columns).forEach((sumPart, i) => {
                if (!sumPart.columns || sumPart.columns.length === 0) return;
                let alignLeft = i === 0 ? 0 : left;
                if (sumPart.containerClass === FREE + "-summaries") {
                    sumPart.width = self.headers[i].width;
                }
                
                let $sumDiv = v.createWrapper(top + "px", alignLeft, sumPart);
                $frag.appendChild($sumDiv);
                let $tbl = document.createElement("table");
                $sumDiv.appendChild($tbl);
                let $tbody = document.createElement("tbody");
                $tbl.appendChild($tbody);
                let $colGroup = document.createElement("colgroup");
                $tbl.insertBefore($colGroup, $tbody);
                let ptr, cols = [], $tr = document.createElement("tr");
                $tr.style.height = "27px";
                $tbody.appendChild($tr);
                
                if (sumPart.containerClass === FREE + "-summaries") {
                    _.forEach(bodyColGroup[bodyColGroup.length > 1 ? 1 : 0], c => {
                        let col = c.cloneNode(true);
                        $colGroup.appendChild(col);
                        cols.push(col);
                    });
                    
                    if (painters.length > 1) ptr = painters[1];
                    else ptr = painters[0];
                    tc.syncDoubDirHorizontalScrolls([ headerWrappers[i], bodyWrappers[i], $sumDiv ]);
                    _mafollicle[SheetDef][_currentSheet].sumColArr = cols;
                } else {
                    _.forEach(bodyColGroup[0], c => {
                        let col = c.cloneNode(true);
                        $colGroup.appendChild(col);
                        cols.push(col);
                    });
                    
                    ptr = painters[0];
                    kt._fixedGroups.push(cols);
                }
                
                sumColGroup.push(cols);
                _.forEach(ptr.columns, c => {
                    let sum = _summaries[c.key]; 
                    let $td = _prtCell.cloneNode();
                    if (!ptr.visibleColumnsMap[c.key]) {
                        $td.style.display = "none";
                    }
                    $tr.appendChild($td);
                    
                    if (!sum) return;
                    if (sum.calculator === "Time") {
                        $td.textContent = ti.momentToString(sum[_currentPage]);
                        sum[_currentSheet] = $td;
                    } else if (sum.calculator === "Number") {
                        if (sum.formatter === "Currency") {
                            $td.textContent = ti.asCurrency(sum[_currentPage]);
                        } else $td.textContent = sum[_currentPage];
                        sum[_currentSheet] = $td;
                    } else {
                        $td.textContent = sum.calculator;
                    }
                });
                
                _vessel().$sumGroup = $colGroup;
                _vessel().$sumBody = $tbody;
                sumWrappers.push($sumDiv);
            });
            
            _sumWrappers = sumWrappers;
            let btmw = Math.min(parseFloat(self.width), _maxFixedWidth + _maxFreeWidth);
            gp.imiPages($frag, top, btmw + "px", self.pload);
            gp.imiSheets($frag, _paging ? top + gp.PAGE_HEIGHT : top, btmw + "px");
            _leftAlign = left;
            
            if (_redimension) {
                let sizeUi = { headerWrappers: headerWrappers, bodyWrappers: bodyWrappers,
                                sumWrappers: sumWrappers, headerColGroup: headerColGroup,
                                bodyColGroup: bodyColGroup, sumColGroup: sumColGroup };
                let freeAdjuster = new kt.ColumnAdjuster([ _maxFixedWidth, freeWrapperWidth ], self.headerHeight, sizeUi, self.float);
                kt._adjuster = freeAdjuster;
                freeAdjuster.handle();
            }
            
            su.binding(self.$container, self.autoFitWindow, self.minRows, self.maxRows);
            lch.checkUp(self.$container);
            
            self.$container.appendChild($frag);
            kt.screenLargeur(self.minRows, self.maxRows);
        }
    }
    
    module tn {
        export const COLUMN_FIX = "ColumnFixing";
        export const SUMMARIES = "Summaries";
        export const PAGING = "Paging";
        export const SHEET = "Sheet";
        export const RESIZING = "Resizing";
        export const SORTING = "Sorting";
        export const MOVING = "ColumnMoving";
        export const HEADER_STYLE = "HeaderStyles";
        export const CELL_STYLE = "CellStyles";
        export const COPY = "Copy";
        export const WIDTH_SAVE = "WidthSaving";
        export const INFOBULLE = "Tooltip";
        
        /**
         * Is enable.
         */
        export function isEnable(features: any, name: string) {
            return _.find(features, function(feature: any) {
                return feature.name === name;
            }) !== undefined;
        }
        
        /**
         * Find.
         */
        export function find(features: any, name: string) {
            return _.find(features, function(feature: any) {
                return feature.name === name;
            });
        }
    }
    
    module v {
        export const CELL_CLS = "mcell";
        export const STT_CLS = "mgrid-no";
        export const DATA = "md";
        export const INIT_MAN_EDIT = "init-man-edit";
        export const FACON_BTN = "mgrid-facon-button";
        export const VFACON_ASC = "view-facon-asc";
        export const FACON_ASC = "facon-asc";
        export const FACON_DESC = "facon-desc";
        export const ALIGN_LEFT = "halign-left";
        export const ALIGN_RIGHT = "halign-right";
        export const ALIGN_CENTER = "halign-center";
        export const DefaultRowConfig = { css: { height: BODY_ROW_HEIGHT } };
        export let _voilerRows = {};
        export let _encarRows = [];
        export let _chasser = [];
        
        /**
         * Process.
         */
        export function process($container: HTMLElement, options: any, isUpdate?: boolean) {
            let levelStruct = synthesizeHeaders(options);
            options.levelStruct = levelStruct;
            
            if (isUpdate && !_.isNil($container.style.maxWidth) && !_.isEmpty($container.style.maxWidth)) {
                let maxWidth = calcWidth(options.columns);
                if (!options.isHeader && options.overflow === "scroll") {
                    $container.style.maxWidth = (maxWidth + ti.getScrollWidth()) + "px";
                } else {
                    $container.style.maxWidth = maxWidth + "px";
                }
            }
            
            if (options.isHeader) {
                if (Object.keys(levelStruct).length > 1) {
                    return groupHeader($container, options, isUpdate);
                }
            } else {
                options.float = options.float === false ? false : true;
            }
            
            let result = table($container, options, isUpdate);
            let painter = paint($container, options);
            
            return { $table: result.$table, cols: result.cols, controlMap: result.controlMap, painter: painter };
        }
        
        /**
         * Group header.
         */
        function groupHeader($container: HTMLElement, options: any, isUpdate: boolean) {
            let $table = selector.create("table").html("<tbody></tbody>").addClass(options.containerClass + "-table")
                        .css({ position: "relative", "table-layout": "fixed", width: "100%", 
//                                "border-collapse": "separate", 
                                "user-select": "none" }).getSingle();
            $container.appendChild($table);
            let $tbody = $table.getElementsByTagName("tbody")[0];
            if (!isUpdate) {
                $container.style.height = options.height;
                $container.style.width = options.width;
            }
            if (!util.isNullOrUndefined(options.overflow)) $container.style.overflow = options.overflow;
            else if (!util.isNullOrUndefined(options.overflowX) && !util.isNullOrUndefined(options.overflowY)) {
                $container.style.overflowX = options.overflowX;
                $container.style.overflowY = options.overflowY;
            }
            let $colGroup = document.createElement("colgroup"); 
            $table.insertBefore($colGroup, $tbody);
            let colGroup = generateColGroup($colGroup, options.columns, options.ntsControls);
            let painter: GroupHeaderPainter = new GroupHeaderPainter(options);
            painter.rows($tbody, tn.find(options.features, tn.MOVING));
            return { $table: $table, cols: colGroup.cols, controlMap: colGroup.controlMap, painter: painter };
        }
        
        /**
         * Generate column group.
         */
        function generateColGroup($colGroup: HTMLElement, columns: any, ntsControls?: Array<any>) {
            let cols = [], controlMap = {};
            _.forEach(columns, function(col: any) {
                if (!_.isNil(col.group)) {
                    let colGroup = generateColGroup($colGroup, col.group, ntsControls);
                    if (colGroup) {
                        colGroup.cols.forEach(c => cols.push(c));
                        _.assignIn(controlMap, colGroup.controlMap);
                    }
                    return;
                }
                let $col = document.createElement("col");
                $col.style.width = col.width;
                $colGroup.appendChild($col);
                cols.push($col);
                
                if (ntsControls && col.ntsControl) {
                    let foundControl = _.find(ntsControls, c => c.name === col.ntsControl);
                    if (foundControl) controlMap[col.key] = foundControl;
                }
                
                if (col.hidden === true) $col.style.display = "none";
            });
            
            return { cols: cols, controlMap: controlMap };
        }
        
        /**
         * Table.
         */
        export function table($container: HTMLElement, options: any, isUpdate?: boolean) {
            let $table = document.createElement("table");
            $table.innerHTML = "<tbody></tbody>";
            $table.className = options.containerClass + "-table";
            $container.appendChild($table);
            let $tbody = $table.getElementsByTagName("tbody")[0];
            if (!isUpdate) {
                $container.style.height = options.height;
                $container.style.width = options.width;
            }
            if (!_.isNil(options.overflow)) $container.style.overflow = options.overflow;
            else if (!_.isNil(options.overflowX)) {
                $container.style.overflowX = options.overflowX;
            } else if (!_.isNil(options.overflowY)) {
                $container.style.overflowY = options.overflowY;
            }
            let $colGroup = document.createElement("colgroup");
            $table.insertBefore($colGroup, $tbody);
            let colDef = generateColGroup($colGroup, options.columns, options.ntsControls);

            return { $table: $table, cols: colDef.cols, controlMap: colDef.controlMap };
        }
        
        /**
         * Paint.
         */
        export function paint($container: HTMLElement, options: any) {
            let dataSource;
            if (!_.isNil(options.dataSource)) {
                 dataSource = options.dataSource;   
            } else {
                let item = {};
                _.forEach(options.columns, function(col: any) {
                    item[col.key] = col.headerText;
                });
                dataSource = [item]; 
            }
            
            return normal($container, dataSource, options);
        }
        
        /**
         * Normal.
         */
        export function normal($container: HTMLElement, dataSource: Array<any>, options: any) {
            let painter: Painter = new Painter($container, options);
//            $.data($container, lo.CANON, { _origDs: ti.cloneDeep(dataSource), dataSource: dataSource, primaryKey: options.primaryKey, painter: painter });
            let $tbody = $container.querySelector("tbody");
            _.forEach(dataSource, function(item: any, index: number) {
                let $tr = painter.row(item, undefined, index);
                $tbody.appendChild($tr);
            });
            return painter;
        }
        
        /**
         * Construe.
         */
        export function construe($container: HTMLElement, containers: Array<HTMLElement>, options: any, single?: any, fails?: any) {
            if (options.features) {
                let cellStyleFt = tn.find(options.features, tn.CELL_STYLE);    
                if (cellStyleFt) {
                    if (_cellStates) options.states = _cellStates;
                    else {
                        [ "states" ].forEach(ft => {
                            if (cellStyleFt[ft]) {
                                let typeFt = _.groupBy(cellStyleFt[ft], "rowId");
                                _.forEach(typeFt, (value, key) => {
                                    typeFt[key] = _.groupBy(typeFt[key], (item) => {
                                        return item.columnKey;
                                    });
                                });
                                
                                _cellStates = typeFt;
                                options[ft] = _cellStates; 
                            }
                        });
                    }
                }
            }
            
            if (!_cloud) _cloud = new aho.Platrer(containers, options);
            let res = single ? _cloud.renderSideRows(true, fails) : _cloud.renderRows(true);
            if (!res) return;
            let start = res.start, end = res.end, cursor;
            if (_.isNil(_mDesc)) {
                _mDesc = {};
                $.data($container, lo.DESC, _mDesc);
                if (!_.isNil(res.fixedColIdxes)) {
                    _mDesc.fixedColIdxes = res.fixedColIdxes;
                }
                
                if (_.isNil(_mDesc.fixedRows)) { 
                    _mDesc.fixedRows = [];
                    _mDesc.fixedRowElements = [];
                }
                _mDesc.rows = [];
                _mDesc.rowElements = [];
            }
               
            if (!_.isNil(res.colIdxes) && (_.isNil(_mDesc.colIdxes) || _mDesc.colIdxes.length === 0)) {
                _mDesc.colIdxes = res.colIdxes;
            }
            
            for (let i = start; i <= end; i++) {
                cursor = i - start;
                if (!_mDesc.fixedRows[i] && res.fixedRows[cursor]) {
                    _mDesc.fixedRows[i] = res.fixedRows[cursor];
                    _mDesc.fixedRowElements[i] = res.fixedRowElements[cursor];
                }
                _mDesc.rows[i] = res.rows[cursor];
                _mDesc.rowElements[i] = res.rowElements[cursor];
            }
            
            if (!_vessel()) {
                _mafollicle[_currentPage][_currentSheet] = {};
            }
            _vessel().desc = _mDesc;
            _vessel().errors = _errors;
            _vessel().dirties = _dirties;
            _vessel().zeroHidden = _zeroHidden;
            _vessel().selected = _selected;
            _vessel().histoire = _histoire;
            
            if (!_.isNil(_currentPage)) {
                let openRange = _pageSize * _currentPage;
                let closeRange = _pageSize * (_currentPage + 1) - 1; 
            }
        }
        
        /**
         * Synthesize headers.
         */
        export function synthesizeHeaders(options: any) {
            let level: any = {};
            peelStruct(options.columns, level, 0);
            let rowCount = Object.keys(level).length; 
            if (rowCount > 1) {
                _.forEach(Object.keys(level), function(key: any) {
                    _.forEach(level[key], function(col: any) {
                        if (util.isNullOrUndefined(col.colspan)) {
                            col.rowspan = rowCount - parseInt(key);
                        }
                    });
                });
            }
            return level;
        }
        
        /**
         * Peel struct.
         */
        function peelStruct(columns: Array<any>, level: any, currentLevel: number, parent: any) {
            let colspan = 0, noGroup = 0;
            
            _.forEach(columns, function(col: any) {
                let clonedCol = _.clone(col);
                let colCount = 0;
                if (!_.isNil(col.group)) {
                    colCount = col.group.length;
                    noGroup++;
                    let ret: any = peelStruct(col.group, level, currentLevel + 1, col.headerText);
                    if (!util.isNullOrUndefined(ret)) {
                        colCount += ret;
                    }
                    clonedCol.colspan = colCount;
                }
                
                if (_.isNil(level[currentLevel])) {
                    level[currentLevel] = [];
                }
                
                level[currentLevel].push(clonedCol);
                colspan += colCount;
                
                if (col.constraint) {
                    let validator = new hpl.ColumnFieldValidator(parent, col.headerText, col.constraint.primitiveValue, col.constraint, col.key);
                    _validators[col.key] = validator;
                }
                
                let linkType = col.ntsType; 
                if (linkType) {
                    let parts = linkType.split("_");
                    if (!parts || parts.length !== 2) return; 
                    if (parts[0] === "comboCode") {
                        _specialColumn[col.key] = parts[1];
                        _specialColumn[parts[1]] = col.key;
                    } else {
                        _specialLinkColumn[col.key] = { column: parts[1], changed: col.onChange };
                    }
                }
                
            });
            
            return colspan !== 0 ? (colspan - noGroup) : undefined;
        }
        
        /**
         * Get constraint name.
         */
        export function getConstraintName(key: string) {
            let column = _columnsMap[key];
            if (!column) return;
            let constraint = column.constraint;
            return constraint.primitiveValue ? ui.validation.getConstraint(constraint.primitiveValue).valueType
                        : constraint.cDisplayType;
        }
        
        abstract class Conditional {
            options: any;
            columnsMap: {[ key: string ]: any };
            columns: Array<any>;
            visibleColumns: Array<any>;
            hiddenColumns: Array<any>;
            visibleColumnsMap: {[ key: string ]: any };
            hiddenColumnsMap: {[ key: string ]: any };
            constructor(options: any) {
                this.options = options;
                let clsColumns = ti.classifyColumns(options, true);
                this.columns = clsColumns.columns;
                this.visibleColumns = clsColumns.visibleColumns;
                this.hiddenColumns = clsColumns.hiddenColumns;
                this.visibleColumnsMap = ti.getColumnsMap(this.visibleColumns);
                this.hiddenColumnsMap = ti.getColumnsMap(this.hiddenColumns);
                _.assignIn(_columnsMap, this.visibleColumnsMap); 
            }
        }
        
        export class Painter extends Conditional {
            $container: HTMLElement;
            styles: any;
            sortSettings : any;
            moving: any;
            piston: any;
            fixed: any;
            
            constructor($container: HTMLElement, options: any) {
                super(options);
                this.$container = $container;
                this.fixed = options.containerClass === FIXED;
                
                if (options.features) {
                    let headerStyle = tn.find(options.features, tn.HEADER_STYLE);
                    if (headerStyle) {
                        this.styles = _.groupBy(headerStyle.columns, "key");
                    }
                    
                    let sortingFt = tn.find(options.features, tn.SORTING);
                    if (sortingFt && sortingFt.columnSettings) {
                        this.sortSettings = sortingFt.columnSettings;
                    }
                    
                    let moving = tn.find(options.features, tn.MOVING);
                    this.moving = moving;
                }
                
                if (!_.isNil(options.levelStruct)) {
                    this.columnsMap = ti.columnsMapFromStruct(options.levelStruct);
                } else {
                    this.columnsMap = _.groupBy(options.columns, "key");
                }
            }
            
            /**
             * BubColumn.
             */
            bubColumn(name: any, i: any) {
                let self = this;
                let col = _.remove(self.hiddenColumns, c => c.key === name);
                if (!col || col.length === 0) return;
                self.visibleColumns.push(col[0]);
                if (self.hiddenColumnsMap.hasOwnProperty(name)) delete self.hiddenColumnsMap[name];
                self.visibleColumnsMap[name] = col;
                _columnsMap[name] = col;
                _.forEach(_.keys(_mafollicle), k => {
                    if (k === SheetDef || k === String(_currentPage)) return;
                    let maf = _mafollicle[k];
                    _.forEach(_.keys(maf), s => { 
                        if (maf[s].hasOwnProperty("desc")) {
                            _.forEach(self.fixed ? maf[s].desc.fixedRows : maf[s].desc.rows, r => {
                                if (!r) return;
                                let a = r[i];
                                if (a && a.style.display === "none") {
                                    a.style.display = "";
                                }
                            });
                            return false;
                        }
                    });
                });
            }
            
            /**
             * UnbubColumn.
             */
            unbubColumn(name: any, i: any) {
                let self = this;
                let col = _.remove(self.visibleColumns, c => c.key === name);
                if (!col || col.length === 0) return;
                self.hiddenColumns.push(col[0]);
                if (self.visibleColumnsMap.hasOwnProperty(name)) {
                    delete self.visibleColumnsMap[name];
                    delete _columnsMap[name];
                }
                
                self.hiddenColumnsMap[name] = col;
                _.forEach(_.keys(_mafollicle), k => {
                    if (k === SheetDef || k === String(_currentPage)) return;
                    let maf = _mafollicle[k];
                    _.forEach(_.keys(maf), s => { 
                        if (maf[s].hasOwnProperty("desc")) {
                            _.forEach(self.fixed ? maf[s].desc.fixedRows : maf[s].desc.rows, r => {
                                if (!r) return;
                                let a = r[i];
                                if (a && a.style.display !== "none") {
                                    a.style.display = "none";
                                }
                            });
                            return false;
                        }
                    });
                });
            }
            
            /**
             * Cell.
             */
            cell(rData: any, rowIdx: number, key: string): HTMLElement {
                let self = this;
                let cData = rData[key]; 
                let data = cData;
                let column: any = self.columnsMap[key];
                if (util.isNullOrUndefined(column)) return;
                let ws = column.css && column.css.whiteSpace ? column.css.whiteSpace : "nowrap";
                let td = document.createElement("td");
                $.data(td, lo.VIEW, rowIdx + "-" + key);
                
                let tdStyle = "";
                tdStyle += "; border-width: 1px; overflow: hidden; ";
                if (self.options.isHeader) {
                    tdStyle += "word-break: break-all; vertical-align: top;";
                } else {
                    tdStyle += "white-space: " + ws + ";"; // position: relative;";
                }
                
                if (!self.visibleColumnsMap[key]) {
                    tdStyle += "; display: none;";
                    if (self.$container.classList.contains(FIXED)) _fixedHiddenColumns.push(key);
                    else _hiddenColumns.push(key);
                }
                let hStyle;
                if (self.styles && (hStyle = self.styles[key])) {
                    _.forEach(hStyle[0].colors, c => {
                        if (c.indexOf('#') === 0) {
                            tdStyle += "; background-color: " + c + ";";
                        } else td.classList.add(c);
                    });
                }
                
                if (column.checkbox) {
                    let $checkBoxLabel = document.createElement("label");
                    $checkBoxLabel.classList.add("ntsCheckBox");
                    let count = 0, $checkBox = document.createElement("input");
                    $checkBox.setAttribute("type", "checkbox");
                    $checkBoxLabel.addXEventListener(ssk.MOUSE_DOWN, evt => {
                        evt.stopPropagation();
                    });
                    
                    $checkBox.addXEventListener("change", function(evt) {
                        let allCheckKey = dkn.allCheck[key], checked = $checkBox.checked;
                        if (checked) {
                            _$grid.mGrid("checkAll", key, self.fixed);
                            allCheckKey.stt = true;
                            allCheckKey.count = allCheckKey.overall; 
                            allCheckKey.toggle = true;
                        } else {
                            _$grid.mGrid("uncheckAll", key, self.fixed);
                            allCheckKey.stt = false;
                            allCheckKey.count = 0;
                            allCheckKey.toggle = false;
                        }
                    });
                    
                    $checkBoxLabel.appendChild($checkBox);
                    let $box = document.createElement("span");
                    $box.classList.add("box");
                    $checkBoxLabel.appendChild($box);
                    td.appendChild($checkBoxLabel);
                    _.forEach(_.keys(_mafollicle), k => {
                        if (k === SheetDef) return;
                        if (_mafollicle[k].dataSource)
                            count += _mafollicle[k].dataSource.length; 
                    });
                    
                    dkn.allCheck[key] = { stt: null, cb: $checkBox, overall: count, count: 0 };
                } else td.innerHTML = data;
                td.style.cssText += tdStyle;
                
                let triSet = _.find(self.sortSettings, s => s.columnKey === key && s.allowSorting); 
                if (triSet) {
                    let downArr = _prtDiv.cloneNode(true);
                    downArr.classList.add(FACON_BTN);
                    downArr.addXEventListener(ssk.MOUSE_DOWN, evt => {
                        let desc;
                        if (down.classList.contains(VFACON_ASC)) {
                            down.classList.remove(VFACON_ASC);
                            down.classList.add(FACON_ASC);
                            downArr.style.top = "2px";
                        } else if (down.classList.contains(FACON_ASC)) {
                            down.classList.remove(FACON_ASC);
                            down.classList.add(FACON_DESC);
                            downArr.style.top = "8px";
                            desc = true;
                        } else {
                            down.classList.remove(FACON_DESC);
                            down.classList.add(FACON_ASC);
                            downArr.style.top = "2px";
                        }
                        ssk.trigger(_$grid[0], "falcon", [ triSet.columnKey, triSet.type, desc ]);
                    });
                    
                    td.appendChild(downArr);
                    let down = document.createElement("span");
                    down.className = "mg-icon " + VFACON_ASC;
                    downArr.appendChild(down);
                    td.addXEventListener(ssk.MOUSE_OVER, evt => {
                        downArr.style.display = "inline";
                        downArr.style.left = td.offsetLeft + td.offsetWidth - 20 + "px";
                    });
                    td.addXEventListener(ssk.MOUSE_OUT, evt => {
                        downArr.style.display = "none";
                    });
                }
                
                return td;
            }
            
            /**
             * Row.
             */
            row(data: any, config: any, rowIdx: number): HTMLElement {
                let self = this;
                let tr: any = document.createElement("tr");
                $.data(tr, lo.VIEW, rowIdx);
                
                if (config) {
                    tr.style.height = parseFloat(config.css.height) + "px";
                }
                
                _.forEach(Object.keys(data), function(key: any, index: number) {
                    if (!self.visibleColumnsMap[key] && !self.hiddenColumnsMap[key]) return;
                    let cell = self.cell(data, rowIdx, key);
                    tr.appendChild(cell);
                    
                    if (self.moving && !rowIdx) {
                        cell.removeXEventListener(ssk.MOUSE_DOWN + ".pis");
                        cell.addXEventListener(ssk.MOUSE_DOWN + ".pis", self.pisAction.bind(self, cell));
                    }
                });
                
                return tr;
            }
            
            pisAction($cell, evt) {
                let self = this, id = self.fixed ? 0 : 1;
                evt.preventDefault();
//                evt.stopPropagation();
                let chass = _chasser[id];
                if (_.isNil(chass)) {
                    chass = [ _prtDiv.cloneNode(true), _prtDiv.cloneNode(true) ];
                    chass[0].className = "mgrid-freinholder";
                    chass[1].className = "mgrid-placeholder";
                    _chasser[id] = chass;
                }
                
                let div = ti.closest($cell, "div"); 
                div.appendChild(chass[1]);
                div.appendChild(chass[0]);
                self.piston = { x: evt.pageX, y: evt.pageY, left: $cell.offsetLeft, top: $cell.offsetTop, index: selector.index($cell), cell: $cell };
                let width = $cell.offsetWidth, height = $cell.offsetHeight;
                chass[0].style.display = "block";
                chass[0].style.width = width + "px";
                chass[0].style.height = height + "px";
                chass[0].style.left = self.piston.left + "px";
                chass[0].style.top = self.piston.top + "px";
                chass[0].style.backgroundColor = getComputedStyle($cell).backgroundColor;
                chass[0].innerHTML = $cell.innerText;
                chass[1].style.display = "block";
                chass[1].style.width = width - 1 + "px";
                chass[1].style.height = height + "px";
                chass[1].style.left = self.piston.left + "px";
                chass[1].style.top = self.piston.top + "px";
                
                window.addXEventListener(ssk.MOUSE_MOVE + ".pis", evt => {
                    chass[0].style.left = self.piston.left + evt.pageX - self.piston.x + "px";
                });
                
                window.addXEventListener(ssk.MOUSE_UP + ".pis", self.fixed ? self.freinUp.bind(self, chass) : self.pisUp.bind(self, chass));
            }
            
            freinUp(chass: any, evt: any) {
                let self = this, gripCol = 0, targetCol = 0,
                    wrapperNo = _headerWrappers.length > 1 && !self.fixed ? 1 : 0,
                    $hContainer = _headerWrappers[wrapperNo], $bContainer = _bodyWrappers[wrapperNo], $sContainer = _sumWrappers[wrapperNo]; 
                
                window.removeXEventListener(ssk.MOUSE_MOVE + ".pis");
                window.removeXEventListener(ssk.MOUSE_UP + ".pis");
                ti.remove(chass[0]);
                ti.remove(chass[1]);
                chass[0].style.display = "none";
                let target = document.elementFromPoint(evt.clientX, evt.clientY);
                chass[0].style.display = "block";
                if (!selector.is(target, "td") || ti.closest(target, "div") !== $hContainer) {
                    return;
                }
                
                let row = ti.closest(target, "tr"), fhGroup, fbGroup, sGroup, sBody, sBodyRow,
                    updatedCols = {}, tbody = $hContainer.querySelector("tbody"),
                    line = tbody.querySelector("tr"), index = selector.index(target);
                
                if (index === self.piston.index) {
                    return;
                }
                
                let hgTarget, bgTarget, bbTarget, sgTarget, sbTarget, hGroup, bGroup, scGroup, gTarget;
                fhGroup = $hContainer.querySelector("colgroup");
                fbGroup = $bContainer.querySelector("colgroup");
                hgTarget = selector.findAt(fhGroup, "col", index + 1);
                bgTarget = selector.findAt(fbGroup, "col", index + 1);
                if ($sContainer) {
                    sGroup = $sContainer.querySelector("colgroup");
                    sBody = $sContainer.querySelector("tbody");
                    sBodyRow = sBody.querySelector("tr");
                    sgTarget = selector.findAt(sGroup, "col", index + 1);
                    sbTarget = selector.findAt(sBodyRow, "td", index + 1);
                }
                
                if (self.piston.index < index) {
                    selector.insertAfter(fhGroup, selector.findAt(fhGroup, "col", self.piston.index + 1), hgTarget);
                    _.forEach(_.keys(_mafollicle), pg => {
                        if (pg === SheetDef) return;
                        _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                            let pmaf = _mafollicle[pg][ash];
                            if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                            _.forEach(pmaf.desc.fixedRowElements, r => {
                                if (_.isNil(r)) return;
                                bbTarget = selector.findAt(r, "td", index + 1);
                                if (pmaf.desc.fixedColIdxes[ti.getCellCoord(bbTarget).columnKey] !== index) return;
                                selector.insertAfter(r, selector.findAt(r, "td", self.piston.index + 1), bbTarget);
                            });
                            _.forEach(pmaf.desc.fixedRows, (r, z) => {
                                if (_.isNil(r)) return;
                                bbTarget = r.splice(self.piston.index, 1);
                                r.splice(index, 0, bbTarget[0]);
                                if (z > 0 || (_.isNumber(_currentPage) && Number(pg) !== _currentPage) || ash !== _currentSheet) return;
                                for (let k = self.piston.index; k <= index; k++) {
                                    let coord = ti.getCellCoord(r[k]);
                                    updatedCols[coord.columnKey] = k;
                                }
                            });
                        });
                    });
                    hGroup = kt._fixedGroups[0];
                    gTarget = hGroup.splice(self.piston.index, 1);
                    hGroup.splice(index, 0, gTarget[0]);
                    bGroup = kt._fixedGroups[1];
                    gTarget = bGroup.splice(self.piston.index, 1);
                    bGroup.splice(index, 0, gTarget[0]);
                    scGroup = kt._fixedGroups[2];
                    if (scGroup) {
                        gTarget = scGroup.splice(self.piston.index, 1);
                        scGroup.splice(index, 0, gTarget[0]);
                    }
                     
                    _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                        if (k === _currentSheet) return;
                        let maf = _mafollicle[_currentPage][k];
                        
                    });
                    self.columns.splice(index, 0, self.columns.splice(self.piston.index, 1)[0]);
                    selector.insertAfter(fbGroup, selector.findAt(fbGroup, "col", self.piston.index + 1), bgTarget);
                    if ($sContainer) {
                        selector.insertAfter(sBodyRow, selector.findAt(sBodyRow, "td", self.piston.index + 1), sbTarget);
                        selector.insertAfter(sGroup, selector.findAt(sGroup, "col", self.piston.index + 1), sgTarget);
                    }
                    line.insertBefore(self.piston.cell, target.nextSibling);
                } else {
                    fhGroup.insertBefore(selector.findAt(fhGroup, "col", self.piston.index + 1), hgTarget);
                    _.forEach(_.keys(_mafollicle), pg => {
                        if (pg === SheetDef) return;
                        _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                            let pmaf = _mafollicle[pg][ash];
                            if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                            _.forEach(pmaf.desc.fixedRowElements, r => {
                                if (_.isNil(r)) return;
                                bbTarget = selector.findAt(r, "td", index + 1);
                                if (pmaf.desc.fixedColIdxes[ti.getCellCoord(bbTarget).columnKey] !== index) return;
                                r.insertBefore(selector.findAt(r, "td", self.piston.index + 1), bbTarget);
                            });
                            _.forEach(pmaf.desc.fixedRows, (r, z) => {
                                if (_.isNil(r)) return;
                                bbTarget = r.splice(self.piston.index, 1);
                                r.splice(index, 0, bbTarget[0]);
                                if (z > 0 || (_.isNumber(_currentPage) && Number(pg) !== _currentPage) || ash !== _currentSheet) return;
                                for (let k = index; k <= self.piston.index; k++) {
                                    let coord = ti.getCellCoord(r[k]);
                                    updatedCols[coord.columnKey] = k;
                                }
                            });
                        });
                    });
                    hGroup = kt._fixedGroups[0];
                    gTarget = hGroup.splice(self.piston.index, 1);
                    hGroup.splice(index, 0, gTarget[0]);
                    bGroup = kt._fixedGroups[1];
                    gTarget = bGroup.splice(self.piston.index, 1);
                    bGroup.splice(index, 0, gTarget[0]);
                    scGroup = kt._fixedGroups[2];
                    if (scGroup) {
                        gTarget = scGroup.splice(self.piston.index, 1);
                        scGroup.splice(index, 0, gTarget[0]);
                    }
                    self.columns.splice(index, 0, self.columns.splice(self.piston.index, 1)[0]);
                    fbGroup.insertBefore(selector.findAt(fbGroup, "col", self.piston.index + 1), bgTarget);
                    if ($sContainer) {
                        sBodyRow.insertBefore(selector.findAt(sBodyRow, "td", self.piston.index + 1), sbTarget);
                        sGroup.insertBefore(selector.findAt(sGroup, "col", self.piston.index + 1), sgTarget);
                    }
                    line.insertBefore(self.piston.cell, target);
                }
                
                if (_.keys(updatedCols).length > 0) {
                    _.forEach(_.keys(_mafollicle), pg => {
                        if (pg === SheetDef) return;
                        _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                            let pmaf = _mafollicle[pg][ash];
                            if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                            let val, idxes = pmaf.desc.fixedColIdxes;
                            _.forEach(_.keys(idxes), c => {
                                val = updatedCols[c];
                                if (!_.isNil(val)) {
                                    idxes[c] = val;
                                }
                            });
                        });
                    });
                }
                
                _cloud.painter.revive();
                if (kt._adjuster) {
                    kt._adjuster.nostal(hGroup, bGroup, scGroup, self.fixed);
                    kt._adjuster.handle();
                }
            }
            
            pisUp(chass: any, evt: any) {
                let self = this, gripCol = 0, wrapperNo = _headerWrappers.length > 1 && !self.fixed ? 1 : 0,
                    $hContainer = _headerWrappers[wrapperNo];
                
                window.removeXEventListener(ssk.MOUSE_MOVE + ".pis");
                window.removeXEventListener(ssk.MOUSE_UP + ".pis");
                ti.remove(chass[0]);
                ti.remove(chass[1]);
                chass[0].style.display = "none";
                let target = document.elementFromPoint(evt.clientX, evt.clientY);
                chass[0].style.display = "block";
                if (!selector.is(target, "td") || ti.closest(target, "div") !== $hContainer) {
                    return;
                }
                
                let row = ti.closest(target, "tr"), updatedCols = {}, //= $hContainer.querySelector("tbody"),
                    index = selector.index(target);
                
                if (index === self.piston.index) {
                    return;
                }
                
                _.forEach(_.keys(_mafollicle), pg => {
                    let hgTarget, bgTarget, bbTarget, sgTarget, sbTarget, sGroup, sBody, sBodyRow, line, tbody;
                    if (pg === SheetDef) return;
                    let pmaf = _mafollicle[pg][_currentSheet];
                    if (_.isNil(pmaf)) return;
                    if (_.isNumber(_currentPage)) pg = Number(pg);
                    tbody = pmaf.$hBody;
                    if (tbody) {
                        line = tbody.querySelector("tr");
                        hgTarget = selector.findAt(pmaf.$hGroup, "col", index + 1);
                        bgTarget = selector.findAt(pmaf.$bGroup, "col", index + 1);
                        sGroup = pmaf.$sumGroup;
                        sBody = pmaf.$sumBody;
                        if (sBody) {
                            sBodyRow = sBody.querySelector("tr");
                            sgTarget = selector.findAt(sGroup, "col", index + 1);
                            sbTarget = selector.findAt(sBodyRow, "td", index + 1);
                        }
                    }
                    
                    if (self.piston.index < index) {
                        if (tbody) {
                            selector.insertAfter(pmaf.$hGroup, selector.findAt(pmaf.$hGroup, "col", self.piston.index + 1), hgTarget);
                        }
                        
                        if (pmaf.desc) {
                            _.forEach(pmaf.desc.rowElements, r => {
                                if (_.isNil(r)) return;
                                bbTarget = selector.findAt(r, "td", index + 1);
                                selector.insertAfter(r, selector.findAt(r, "td", self.piston.index + 1), bbTarget);
                            });
                            _.forEach(pmaf.desc.rows, (r, z) => {
                                if (_.isNil(r)) return;
                                bbTarget = r.splice(self.piston.index, 1);
                                r.splice(index, 0, bbTarget[0]);
                                if (z > 0 || pg !== _currentPage) return;
                                for (let k = self.piston.index; k <= index; k++) {
                                    let coord = ti.getCellCoord(r[k]);
                                    updatedCols[coord.columnKey] = k;
                                }
                            });
                        }
                        
                        if (pg === _currentPage) {
                            _.forEach([ "b", "h", "sum" ], arrType => {
                                let arr = _mafollicle[SheetDef][_currentSheet][arrType + "ColArr"];
                                if (_.isNil(arr)) return;
                                bbTarget = arr.splice(self.piston.index, 1);
                                arr.splice(index, 0, bbTarget[0]);
                            });
                            
                            self.columns.splice(index, 0, self.columns.splice(self.piston.index, 1)[0]);
                        }
                        
                        if (tbody) {
                            selector.insertAfter(pmaf.$bGroup, selector.findAt(pmaf.$bGroup, "col", self.piston.index + 1), bgTarget);
                        }
                        if (sBody) {
                            selector.insertAfter(sBodyRow, selector.findAt(sBodyRow, "td", self.piston.index + 1), sbTarget);
                            selector.insertAfter(sGroup, selector.findAt(sGroup, "col", self.piston.index + 1), sgTarget);
                        }
                        if (tbody) {
                            line.insertBefore(self.piston.cell, target.nextSibling);
                        }
                    } else {
                        if (tbody) {
                            pmaf.$hGroup.insertBefore(selector.findAt(pmaf.$hGroup, "col", self.piston.index + 1), hgTarget);
                        }
                        
                        if (pmaf.desc) {
                            _.forEach(pmaf.desc.rowElements, r => {
                                if (_.isNil(r)) return;
                                bbTarget = selector.findAt(r, "td", index + 1);
                                r.insertBefore(selector.findAt(r, "td", self.piston.index + 1), bbTarget);
                            });
                            _.forEach(pmaf.desc.rows, (r, z) => {
                                if (_.isNil(r)) return;
                                bbTarget = r.splice(self.piston.index, 1);
                                r.splice(index, 0, bbTarget[0]);
                                if (z > 0 || pg !== _currentPage) return;
                                for (let k = index; k <= self.piston.index; k++) {
                                    let coord = ti.getCellCoord(r[k]);
                                    updatedCols[coord.columnKey] = k;
                                }
                            });
                        }
                        
                        if (pg === _currentPage) {
                            _.forEach([ "b", "h", "sum" ], arrType => {
                                let arr = _mafollicle[SheetDef][_currentSheet][arrType + "ColArr"];
                                if (_.isNil(arr)) return;
                                bbTarget = arr.splice(self.piston.index, 1);
                                arr.splice(index, 0, bbTarget[0]);
                            });
                            
                            self.columns.splice(index, 0, self.columns.splice(self.piston.index, 1)[0]);
                        }
                        
                        if (tbody) {
                            pmaf.$bGroup.insertBefore(selector.findAt(pmaf.$bGroup, "col", self.piston.index + 1), bgTarget);
                        }
                        if (sBody) {
                            sBodyRow.insertBefore(selector.findAt(sBodyRow, "td", self.piston.index + 1), sbTarget);
                            sGroup.insertBefore(selector.findAt(sGroup, "col", self.piston.index + 1), sgTarget);
                        }
                        if (tbody) {
                            line.insertBefore(self.piston.cell, target);
                        }
                    }
                    
                    if (_.keys(updatedCols).length > 0) {
                        let val, idxes = pmaf.desc.colIdxes;
                        _.forEach(_.keys(idxes), c => {
                            val = updatedCols[c];
                            if (!_.isNil(val)) {
                                idxes[c] = val;
                            }
                        });
                    } 
                });
                
                _cloud.painter.revive();
                if (kt._adjuster) {
                    let csd = _mafollicle[SheetDef][_currentSheet];
                    kt._adjuster.nostal(csd.hColArr, csd.bColArr, csd.sumColArr, self.fixed);
                    kt._adjuster.handle();
                }
            }
        }
        
        export class GroupHeaderPainter extends Conditional {
            levelStruct: any;
            styles: any;
            sortSettings: any;
            piston: any;
            fixed: any;
            
            constructor(options: any) {
                super(options);
                this.levelStruct = options.levelStruct;
                this.columnsMap = ti.columnsMapFromStruct(this.levelStruct);
                this.fixed = options.containerClass === FIXED;
                if (options.features) {
                    let styleFt = tn.find(options.features, tn.HEADER_STYLE);
                    if (styleFt) {
                        this.styles = _.groupBy(styleFt.columns, "key");
                    }
                    
                    let sortingFt = tn.find(options.features, tn.SORTING);
                    if (sortingFt && sortingFt.columnSettings) {
                        this.sortSettings = sortingFt.columnSettings;
                    }
                } 
            }
            
            /**
             * Cell.
             */
            cell(text: any, rowIdx: number, cell: any): HTMLElement {
                let self = this;
                let $td = document.createElement("td");
                $.data($td, lo.VIEW, rowIdx + "-" + cell.key);
                let tdStyle = "; border-width: 1px; overflow: hidden; word-break: break-all; vertical-align: top; border-collapse: collapse;";
                if (!_.isNil(cell.rowspan) && cell.rowspan > 1) $td.setAttribute("rowspan", cell.rowspan);
                if (!_.isNil(cell.colspan) && cell.colspan > 1) $td.setAttribute("colspan", cell.colspan);
                else if (_.isNil(cell.colspan) && !self.visibleColumnsMap[cell.key]) {
                    tdStyle += "; display: none;";
                    if (self.options.containerClass === FIXED) _fixedHiddenColumns.push(cell.key);
                    else _hiddenColumns.push(cell.key);
                }
                
                let column = self.columnsMap[cell.key];
                let hStyle;
                if (self.styles && ((hStyle = self.styles[cell.key])
                    || (cell.group && (hStyle = self.styles[cell.group[0].key])))) {
                    _.forEach(hStyle[0].colors, c => {
                        if (c.indexOf('#') === 0) {
                            tdStyle += "; background-color: " + c + ";";
                        } else $td.classList.add(c);
                    });
                }
                
                if (column && column.checkbox) {
                    let $checkBoxLabel = document.createElement("label");
                    $checkBoxLabel.classList.add("ntsCheckBox");
                    let count = 0, $checkBox = document.createElement("input");
                    $checkBox.setAttribute("type", "checkbox");
                    $checkBoxLabel.addXEventListener(ssk.MOUSE_DOWN, evt => {
                        evt.stopPropagation();
                    });
                    
                    $checkBox.addXEventListener("change", function(evt) {
                        let allCheckKey = dkn.allCheck[cell.key], checked = $checkBox.checked;
                        if (checked) {
                            _$grid.mGrid("checkAll", cell.key, self.fixed);
                            allCheckKey.stt = true;
                            allCheckKey.count = allCheckKey.overall; 
                            allCheckKey.toggle = true;
                        } else {
                            _$grid.mGrid("uncheckAll", cell.key, self.fixed);
                            allCheckKey.stt = false;
                            allCheckKey.count = 0;
                            allCheckKey.toggle = false;
                        }
                    });
                    
                    $checkBoxLabel.appendChild($checkBox);
                    let $box = document.createElement("span");
                    $box.classList.add("box");
                    $checkBoxLabel.appendChild($box);
                    $td.appendChild($checkBoxLabel);
                    _.forEach(_.keys(_mafollicle), k => {
                        if (k === SheetDef) return;
                        if (_mafollicle[k].dataSource)
                            count += _mafollicle[k].dataSource.length; 
                    });
                    
                    dkn.allCheck[cell.key] = { stt: null, cb: $checkBox, overall: count, count: 0 };
                } else $td.innerHTML = text;
                $td.style.cssText += tdStyle;
                
                let triSet = _.find(self.sortSettings, s => s.columnKey === cell.key && s.allowSorting); 
                if (triSet) {
                    let downArr = _prtDiv.cloneNode(true);
                    downArr.classList.add(FACON_BTN);
                    downArr.addXEventListener(ssk.MOUSE_DOWN, evt => {
                        let desc;
                        if (down.classList.contains(VFACON_ASC)) {
                            down.classList.remove(VFACON_ASC);
                            down.classList.add(FACON_ASC);
                            downArr.style.top = $td.offsetTop + 2 + "px";
                        } else if (down.classList.contains(FACON_ASC)) {
                            down.classList.remove(FACON_ASC);
                            down.classList.add(FACON_DESC);
                            downArr.style.top = $td.offsetTop + 8 + "px";
                            desc = true;
                        } else {
                            down.classList.remove(FACON_DESC);
                            down.classList.add(FACON_ASC);
                            downArr.style.top = $td.offsetTop + 2 + "px";
                        }
                        ssk.trigger(_$grid[0], "falcon", [ triSet.columnKey, triSet.type, desc ]);
                        evt.stopPropagation();
                    });
                    
                    $td.appendChild(downArr);
                    let down = document.createElement("span");
                    down.className = "mg-icon " + VFACON_ASC;
                    downArr.appendChild(down);
                    $td.addXEventListener(ssk.MOUSE_OVER, evt => {
                        downArr.style.left = $td.offsetLeft + $td.offsetWidth - 20 + "px";
                        downArr.style.display = "inline";
                        downArr.style.top = $td.offsetTop + 
                            (down.classList.contains(FACON_ASC) || down.classList.contains(VFACON_ASC) ? 2 : 8) + "px";
                    });
                    $td.addXEventListener(ssk.MOUSE_OUT, evt => {
                        downArr.style.display = "none";
                    });
                }
                
                return $td;
            }
            
            /**
             * Rows.
             */
            rows($tbody: HTMLElement, moving?: any) {
                let self = this;
                _.forEach(Object.keys(self.levelStruct), function(rowIdx: any) {
                    
                    let $tr = document.createElement("tr");
                    rowIdx = Number(rowIdx);
                    $.data($tr, lo.VIEW, rowIdx);
                    let oneLevel = self.levelStruct[rowIdx];
                    _.forEach(oneLevel, function(cell: any, index: any) {
                        if (!self.visibleColumnsMap[cell.key] && !self.hiddenColumnsMap[cell.key]
                            && _.isNil(cell.colspan)) return;
                        let $cell = self.cell(cell.headerText, rowIdx, cell);
                        $tr.appendChild($cell);
                        if (moving && !rowIdx) {
                            $cell.removeXEventListener(ssk.MOUSE_DOWN + ".pis");
                            $cell.addXEventListener(ssk.MOUSE_DOWN + ".pis", self.pisAction.bind(self, $cell));
                        }
                    });
                    
                    $tbody.appendChild($tr);
                });
            }
            
            pisAction($cell, evt) {
                let self = this, id = self.fixed ? 0 : 1;
//                evt.stopPropagation();
                let chass = _chasser[id];
                if (!chass) {
                    chass = [ _prtDiv.cloneNode(true), _prtDiv.cloneNode(true) ];
                    chass[0].className = "mgrid-freinholder";
                    chass[1].className = "mgrid-placeholder";
                    _chasser[id] = chass;
                }
                
                let div = ti.closest($cell, "div"), width = $cell.offsetWidth, height = $cell.offsetHeight;
                div.appendChild(chass[1]);
                div.appendChild(chass[0]);
                self.piston = { x: evt.pageX, y: evt.pageY, left: $cell.offsetLeft, top: $cell.offsetTop, index: selector.index($cell), cell: $cell };
                chass[0].style.display = "block";
                chass[0].style.width = width + "px";
                chass[0].style.height = height + "px";
                chass[0].style.left = self.piston.left + "px";
                chass[0].style.top = self.piston.top + "px";
                chass[0].style.backgroundColor = getComputedStyle($cell).backgroundColor;
                chass[0].innerHTML = $cell.innerText;
                chass[1].style.display = "block";
                chass[1].style.width = width - 1 + "px";
                chass[1].style.height = height + "px";
                chass[1].style.left = self.piston.left + "px";
                chass[1].style.top = self.piston.top + "px";
                
                window.addXEventListener(ssk.MOUSE_MOVE + ".pis", evt => {
                    chass[0].style.left = self.piston.left + evt.pageX - self.piston.x + "px";
                });
                
                window.addXEventListener(ssk.MOUSE_UP + ".pis", self.fixed ? self.freinUp.bind(self, chass) : self.pisUp.bind(self, chass));
            }
            
            freinUp(chass: any, evt: any) {
                let self = this, index, gripCol = 0, headInsertCol = 0, tailInsertCol = 0,
                    colspan = parseInt(self.piston.cell.getAttribute("colspan")), headInsertIdx = 0, tailInsertIdx = 0, nlay = 0,
                    wrapperNo = _headerWrappers.length > 1 && !self.fixed ? 1 : 0,
                    $hContainer = _headerWrappers[wrapperNo], $bContainer = _bodyWrappers[wrapperNo], $sContainer = _sumWrappers[wrapperNo];
                
                window.removeXEventListener(ssk.MOUSE_MOVE + ".pis");
                window.removeXEventListener(ssk.MOUSE_UP + ".pis");
                ti.remove(chass[0]);
                ti.remove(chass[1]);
                chass[0].style.display = "none";
                let target = document.elementFromPoint(evt.clientX, evt.clientY);
                chass[0].style.display = "block";
                if (!selector.is(target, "td") || ti.closest(target, "div") !== $hContainer) {
                    return;
                }
                
                let row = ti.closest(target, "tr"), fhGroup, fbGroup, sGroup, sBody, sBodyRow,
                    updatedCols = {}, tbody = $hContainer.querySelector("tbody"),
                    firstLine = tbody.querySelector("tr:nth-of-type(1)");
                if (selector.index(row) > 0) {
                    let coord = ti.getCellCoord(target);
                    _.forEach(self.levelStruct[0], (c, i) => {
                        if (c.group && _.find(c.group, g => g.key === coord.columnKey)) {
                            index = i;
                        } 
                    });
                    
                    if (!_.isNil(index)) {
                        target = firstLine.querySelector("td:nth-of-type(" + (index + 1) + ")");
                    }
                } else {
                    index = selector.index(target);
                }
                
                if (index === self.piston.index) {
                    return;
                }
                
                _.forEach(self.levelStruct[0], (c, i) => {
                    if (i < self.piston.index) {
                        if (!_.isNil(c.colspan)) {
                            nlay += c.colspan;
                            gripCol += c.colspan;
                        }
                        
                        if (c.rowspan > 1 && _.isNil(c.colspan)) gripCol += 1;
                    }
                    
                    if (i < index) {
                        if (i === self.piston.index) return;
                        
                        if (!_.isNil(c.colspan)) {
                            headInsertIdx += c.colspan;
                            headInsertCol += c.colspan;
                        }
                        
                        if (c.rowspan > 1 && _.isNil(c.colspan)) headInsertCol += 1;
                    }
                    
                    if (i > index && i > self.piston.index) return false;
                });
                
                let tColspan = target.getAttribute("colspan"), cspan, hgTarget, bgTarget, bbTarget, sgTarget, sbTarget, colTargetIdx, colGripIdx,
                    gTarget, hGroup, bGroup, scGroup;
                if (!_.isNil(tColspan) && tColspan !== "") {
                    tailInsertIdx = headInsertIdx + Number(tColspan) - 1;
                    tailInsertCol = headInsertCol + Number(tColspan) - 1;
                } else {
                    tailInsertIdx = headInsertIdx;
                    tailInsertCol = headInsertCol;
                }
                
                if (self.piston.index < index) {
                    cspan = isNaN(colspan) ? 1 : colspan;
                    colTargetIdx = tailInsertCol + cspan + 1;
                } else {
                    colTargetIdx = headInsertCol + 1;
                }
                
                fhGroup = $hContainer.querySelector("colgroup");
                fbGroup = $bContainer.querySelector("colgroup");
                hgTarget = selector.findAt(fhGroup, "col", colTargetIdx);
                bgTarget = selector.findAt(fbGroup, "col", colTargetIdx);
                
                if ($sContainer) {
                    sGroup = $sContainer.querySelector("colgroup");
                    sBody = $sContainer.querySelector("tbody");
                    sBodyRow = sBody.querySelector("tr:nth-of-type(1)");
                    sgTarget = selector.findAt(sGroup, "col", colTargetIdx);
                    sbTarget = selector.findAt(sBodyRow, "td", colTargetIdx);
                }
                
                if (!_.isNil(colspan) && colspan > 1) {
                    let secondLine = tbody.querySelector("tr:nth-of-type(2)"), sTarget, moveTd;
                    for (let i = 0; i < colspan; i++) {
                        if (self.piston.index < index) {
                            sTarget = secondLine.querySelector("td:nth-of-type(" + (tailInsertIdx + colspan - i + 1) + ")");
                            moveTd = selector.findAt(secondLine, "td", nlay + colspan - i);
                            if (tailInsertIdx === headInsertIdx) {
                                secondLine.insertBefore(moveTd, sTarget);
                            } else {
                                selector.insertAfter(secondLine, moveTd, sTarget);
                            }
                            self.levelStruct[1].splice(tailInsertIdx + colspan, 0, self.levelStruct[1].splice(nlay, 1)[0]);
                            
                            colGripIdx = gripCol + colspan - i;
                            
                            selector.insertAfter(fhGroup, selector.findAt(fhGroup, "col", colGripIdx), hgTarget);
                            _.forEach(_.keys(_mafollicle), pg => {
                                if (pg === SheetDef) return;
                                _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                                    let pmaf = _mafollicle[pg][ash];
                                    if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                                    _.forEach(pmaf.desc.fixedRowElements, r => {
                                        if (_.isNil(r)) return;
                                        bbTarget = selector.findAt(r, "td", colTargetIdx - i);
                                        if (pmaf.desc.fixedColIdxes[ti.getCellCoord(bbTarget).columnKey] !== colTargetIdx - 1) return;
                                        selector.insertAfter(r, selector.findAt(r, "td", colGripIdx), bbTarget);
                                    });
                                    _.forEach(pmaf.desc.fixedRows, (r, z) => {
                                        if (_.isNil(r)) return;
                                        bbTarget = r.splice(colGripIdx - 1, 1);
                                        r.splice(colTargetIdx - 1 - i, 0, bbTarget[0]);
                                        if (z > 0 || (_.isNumber(_currentPage) && Number(pg) !== _currentPage) || ash !== _currentSheet) return;
                                        for (let k = colGripIdx - 1; k < colTargetIdx - i; k++) {
                                            let coord = ti.getCellCoord(r[k]);
                                            updatedCols[coord.columnKey] = k;
                                        }
                                    });
                                });
                            });
                            hGroup = kt._fixedGroups[0];
                            gTarget = hGroup.splice(colGripIdx - 1, 1);
                            hGroup.splice(colTargetIdx - 1 - i, 0, gTarget[0]);
                            bGroup = kt._fixedGroups[1];
                            gTarget = bGroup.splice(colGripIdx - 1, 1);
                            bGroup.splice(colTargetIdx - 1 - i, 0, gTarget[0]);
                            scGroup = kt._fixedGroups[2];
                            if (scGroup) {
                                gTarget = scGroup.splice(colGripIdx - 1, 1);
                                scGroup.splice(colTargetIdx - 1 - i, 0, gTarget[0]);
                            }
                            self.columns.splice(colTargetIdx - 1 - i, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                            selector.insertAfter(fbGroup, selector.findAt(fbGroup, "col", colGripIdx), bgTarget);
                            if ($sContainer) {
                                selector.insertAfter(sBodyRow, selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                                selector.insertAfter(sGroup, selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                            }
                        } else {
                            sTarget = secondLine.querySelector("td:nth-of-type(" + (headInsertIdx + 1 + i) + ")");
                            secondLine.insertBefore(secondLine.querySelector("td:nth-of-type(" + (nlay + 1 + i) + ")") , sTarget);
                            self.levelStruct[1].splice(headInsertIdx, 0, self.levelStruct[1].splice(nlay + colspan - 1, 1)[0]);
                            
                            colGripIdx = gripCol + 1 + i;
                            fhGroup.insertBefore(selector.findAt(fhGroup, "col", colGripIdx), hgTarget);
                            _.forEach(_.keys(_mafollicle), pg => {
                                if (pg === SheetDef) return;
                                _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                                    let pmaf = _mafollicle[pg][ash];
                                    if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                                    _.forEach(pmaf.desc.fixedRowElements, r => {
                                        if (_.isNil(r)) return;
                                        bbTarget = selector.findAt(r, "td", colTargetIdx + i);
                                        if (pmaf.desc.fixedColIdxes[ti.getCellCoord(bbTarget).columnKey] !== colTargetIdx - 1) return;
                                        r.insertBefore(selector.findAt(r, "td", colGripIdx), bbTarget);
                                    });
                                    _.forEach(pmaf.desc.fixedRows, (r, z) => {
                                        if (_.isNil(r)) return;
                                        bbTarget = r.splice(colGripIdx - 1, 1);
                                        r.splice(colTargetIdx - 1 + i, 0, bbTarget[0]);
                                        if (z > 0 || (_.isNumber(_currentPage) && Number(pg) !== _currentPage) || ash !== _currentSheet) return;
                                        for (let k = colTargetIdx - 1 + i; k < colGripIdx; k++) {
                                            let coord = ti.getCellCoord(r[k]);
                                            updatedCols[coord.columnKey] = k;
                                        }
                                    });
                                });
                            });
                            hGroup = kt._fixedGroups[0];
                            gTarget = hGroup.splice(colGripIdx - 1, 1);
                            hGroup.splice(colTargetIdx - 1 + i, 0, gTarget[0]);
                            bGroup = kt._fixedGroups[1];
                            gTarget = bGroup.splice(colGripIdx - 1, 1);
                            bGroup.splice(colTargetIdx - 1 + i, 0, gTarget[0]);
                            scGroup = kt._fixedGroups[2];
                            if (scGroup) {
                                gTarget = scGroup.splice(colGripIdx - 1, 1);
                                scGroup.splice(colTargetIdx - 1 + i, 0, gTarget[0]);
                            }
                            self.columns.splice(colTargetIdx - 1 + i, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                            fbGroup.insertBefore(selector.findAt(fbGroup, "col", colGripIdx), bgTarget);
                            if ($sContainer) {
                                sBodyRow.insertBefore(selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                                sGroup.insertBefore(selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                            }
                        }
                    }
                } else {
                    headInsertIdx = -1;
                    nlay = -1;
                    colGripIdx = gripCol + 1;
                    
                    if (self.piston.index < index) {
                        selector.insertAfter(fhGroup, selector.findAt(fhGroup, "col", colGripIdx), hgTarget);
                        _.forEach(_.keys(_mafollicle), pg => {
                            if (pg === SheetDef) return;
                            _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                                let pmaf = _mafollicle[pg][ash];
                                if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                                _.forEach(pmaf.desc.fixedRowElements, r => {
                                    if (_.isNil(r)) return;
                                    bbTarget = selector.findAt(r, "td", colTargetIdx);
                                    if (pmaf.desc.fixedColIdxes[ti.getCellCoord(bbTarget).columnKey] !== colTargetIdx - 1) return;
                                    selector.insertAfter(r, selector.findAt(r, "td", colGripIdx), bbTarget);
                                });
                                _.forEach(pmaf.desc.fixedRows, (r, z) => {
                                    if (_.isNil(r)) return;
                                    bbTarget = r.splice(colGripIdx - 1, 1);
                                    r.splice(colTargetIdx - 1, 0, bbTarget[0]);
                                    if (z > 0 || (_.isNumber(_currentPage) && Number(pg) !== _currentPage) || ash !== _currentSheet) return;
                                    for (let k = colGripIdx - 1; k < colTargetIdx; k++) {
                                        let coord = ti.getCellCoord(r[k]);
                                        updatedCols[coord.columnKey] = k;
                                    }
                                });
                            });
                        });
                        hGroup = kt._fixedGroups[0];
                        gTarget = hGroup.splice(colGripIdx - 1, 1);
                        hGroup.splice(colTargetIdx - 1, 0, gTarget[0]);
                        bGroup = kt._fixedGroups[1];
                        gTarget = bGroup.splice(colGripIdx - 1, 1);
                        bGroup.splice(colTargetIdx - 1, 0, gTarget[0]);
                        scGroup = kt._fixedGroups[2];
                        if (scGroup) {
                            gTarget = scGroup.splice(colGripIdx - 1, 1);
                            scGroup.splice(colTargetIdx - 1, 0, gTarget[0]);
                        }
                        self.columns.splice(colTargetIdx - 1, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                        selector.insertAfter(fbGroup, selector.findAt(fbGroup, "col", colGripIdx), bgTarget);
                        
                        if ($sContainer) {
                            selector.insertAfter(sBodyRow, selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                            selector.insertAfter(sGroup, selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                        }
                    } else {
                        fhGroup.insertBefore(selector.findAt(fhGroup, "col", colGripIdx), hgTarget);
                        _.forEach(_.keys(_mafollicle), pg => {
                            if (pg === SheetDef) return;
                            _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                                let pmaf = _mafollicle[pg][ash];
                                if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                                _.forEach(pmaf.desc.fixedRowElements, r => {
                                    if (_.isNil(r)) return;
                                    bbTarget = selector.findAt(r, "td", colTargetIdx);
                                    if (pmaf.desc.fixedColIdxes[ti.getCellCoord(bbTarget).columnKey] !== colTargetIdx - 1) return;
                                    r.insertBefore(selector.findAt(r, "td", colGripIdx), bbTarget);
                                });
                                _.forEach(pmaf.desc.fixedRows, (r, z) => {
                                    if (_.isNil(r)) return;
                                    bbTarget = r.splice(colGripIdx - 1, 1);
                                    r.splice(colTargetIdx - 1, 0, bbTarget[0]);
                                    if (z > 0 || (_.isNumber(_currentPage) && Number(pg) !== _currentPage) || ash !== _currentSheet) return;
                                    for (let k = colTargetIdx - 1; k < colGripIdx; k++) {
                                        let coord = ti.getCellCoord(r[k]);
                                        updatedCols[coord.columnKey] = k;
                                    }
                                });
                            });
                        });
                        hGroup = kt._fixedGroups[0];
                        gTarget = hGroup.splice(colGripIdx - 1, 1);
                        hGroup.splice(colTargetIdx - 1, 0, gTarget[0]);
                        bGroup = kt._fixedGroups[1];
                        gTarget = bGroup.splice(colGripIdx - 1, 1);
                        bGroup.splice(colTargetIdx - 1, 0, gTarget[0]);
                        scGroup = kt._fixedGroups[2];
                        if (scGroup) {
                            gTarget = scGroup.splice(colGripIdx - 1, 1);
                            scGroup.splice(colTargetIdx - 1, 0, gTarget[0]);
                        }
                        self.columns.splice(colTargetIdx - 1, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                        fbGroup.insertBefore(selector.findAt(fbGroup, "col", colGripIdx), bgTarget);
                        
                        if ($sContainer) {
                            sBodyRow.insertBefore(selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                            sGroup.insertBefore(selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                        }
                    }
                }
                
                if (self.piston.index < index) {
                    firstLine.insertBefore(self.piston.cell, target.nextSibling);
                } else {
                    firstLine.insertBefore(self.piston.cell, target);
                }
                
                if (_.keys(updatedCols).length > 0) {
                    _.forEach(_.keys(_mafollicle), pg => {
                        if (pg === SheetDef) return;
                        _.forEach(_.keys(_mafollicle[SheetDef]), ash => {
                            let pmaf = _mafollicle[pg][ash];
                            if (_.isNil(pmaf) || _.isNil(pmaf.desc)) return;
                            let val, idxes = pmaf.desc.fixedColIdxes;
                            _.forEach(_.keys(idxes), c => {
                                val = updatedCols[c];
                                if (!_.isNil(val)) {
                                    idxes[c] = val;
                                }
                            });
                        });
                    });
                } 
                
                self.levelStruct[0].splice(index, 0, self.levelStruct[0].splice(self.piston.index, 1)[0]);
                _cloud.painter.revive();
                if (kt._adjuster) {
                    kt._adjuster.nostal(hGroup, bGroup, scGroup, self.fixed);
                    kt._adjuster.handle();
                }
            }
            
            pisUp(chass: any, evt: any) {
                let self = this, index, gripCol = 0, headInsertCol = 0, tailInsertCol = 0,
                    colspan = parseInt(self.piston.cell.getAttribute("colspan")), headInsertIdx = 0, tailInsertIdx = 0, nlay = 0,
                    wrapperNo = _headerWrappers.length > 1 && !self.fixed ? 1 : 0, colTargetIdx,
                    $hContainer = _headerWrappers[wrapperNo];
                
                window.removeXEventListener(ssk.MOUSE_MOVE + ".pis");
                window.removeXEventListener(ssk.MOUSE_UP + ".pis");
                ti.remove(chass[0]);
                ti.remove(chass[1]);
                chass[0].style.display = "none";
                let target = document.elementFromPoint(evt.clientX, evt.clientY);
                chass[0].style.display = "block";
                if (!selector.is(target, "td") || ti.closest(target, "div") !== $hContainer) {
                    return;
                }
                
                let row = ti.closest(target, "tr"),
                    updatedCols = {}, tbody = $hContainer.querySelector("tbody"),
                    firstLine = tbody.querySelector("tr:nth-of-type(1)");
                if (selector.index(row) > 0) {
                    let coord = ti.getCellCoord(target);
                    _.forEach(self.levelStruct[0], (c, i) => {
                        if (c.group && _.find(c.group, g => g.key === coord.columnKey)) {
                            index = i;
                        } 
                    });
                    
                    if (!_.isNil(index)) {
                        target = firstLine.querySelector("td:nth-of-type(" + (index + 1) + ")");
                    }
                } else {
                    index = selector.index(target);
                }
                
                if (index === self.piston.index) {
                    return;
                }
                
                _.forEach(self.levelStruct[0], (c, i) => {
                    if (i < self.piston.index) {
                        if (!_.isNil(c.colspan)) {
                            nlay += c.colspan;
                            gripCol += c.colspan;
                        }
                        
                        if (c.rowspan > 1 && _.isNil(c.colspan)) gripCol += 1;
                    }
                    
                    if (i < index) {
                        if (i === self.piston.index) return;
                        
                        if (!_.isNil(c.colspan)) {
                            headInsertIdx += c.colspan;
                            headInsertCol += c.colspan;
                        }
                        
                        if (c.rowspan > 1 && _.isNil(c.colspan)) headInsertCol += 1;
                    }
                    
                    if (i > index && i > self.piston.index) return false;
                });
                
                let tColspan = target.getAttribute("colspan"), cspan;
                if (!_.isNil(tColspan) && tColspan !== "") {
                    tailInsertIdx = headInsertIdx + Number(tColspan) - 1;
                    tailInsertCol = headInsertCol + Number(tColspan) - 1;
                } else {
                    tailInsertIdx = headInsertIdx;
                    tailInsertCol = headInsertCol;
                }
                
                if (self.piston.index < index) {
                    cspan = isNaN(colspan) ? 1 : colspan;
                    colTargetIdx = tailInsertCol + cspan + 1;
                } else {
                    colTargetIdx = headInsertCol + 1;
                }
                
                _.forEach(_.keys(_mafollicle), pg => {
                    if (pg === SheetDef) return;
                    let hgTarget, bgTarget, bbTarget, sgTarget, sbTarget, colGripIdx, sGroup, 
                        sBody, sBodyRow, $hGroup, pmaf = _mafollicle[pg][_currentSheet];
                    if (_.isNil(pmaf)) return;
                    if (_.isNumber(_currentPage)) pg = Number(pg);
                    if (pg === _currentPage) {
                        tbody = pmaf.$hBody;
                        if (tbody) {
                            firstLine = tbody.querySelector("tr:nth-of-type(1)");
                            $hGroup = _vessel().$hGroup;
                            hgTarget = selector.findAt($hGroup, "col", colTargetIdx);
                            bgTarget = selector.findAt(_vessel().$bGroup, "col", colTargetIdx);
                            sGroup = _vessel().$sumGroup;
                            sBody = _vessel().$sumBody;
                        }
                    } else {
                        tbody = pmaf.$hBody;
                        if (tbody) {
                            firstLine = tbody.querySelector("tr:nth-of-type(1)");
                            target = firstLine.querySelector("td:nth-of-type(" + (index + 1) + ")");
                            $hGroup = pmaf.$hGroup;
                            hgTarget = selector.findAt($hGroup, "col", colTargetIdx);
                            bgTarget = selector.findAt(pmaf.$bGroup, "col", colTargetIdx);
                            sGroup = pmaf.$sumGroup;
                            sBody = pmaf.$sumBody;
                        }
                    }
                    
                    if (sBody) {
                        sBodyRow = sBody.querySelector("tr:nth-of-type(1)");
                        sgTarget = selector.findAt(sGroup, "col", colTargetIdx);
                        sbTarget = selector.findAt(sBodyRow, "td", colTargetIdx);
                    }
                    
                    if (!_.isNil(colspan) && colspan > 1) {
                        let secondLine, sTarget, moveTd;
                        if (tbody) {
                            secondLine = tbody.querySelector("tr:nth-of-type(2)");
                        }
                        
                        for (let i = 0; i < colspan; i++) {
                            if (self.piston.index < index) {
                                if (tbody) {
                                    sTarget = secondLine.querySelector("td:nth-of-type(" + (tailInsertIdx + colspan - i + 1) + ")");
                                    moveTd = selector.findAt(secondLine, "td", nlay + colspan - i);
                                    if (tailInsertIdx === headInsertIdx) {
                                        secondLine.insertBefore(moveTd, sTarget);
                                    } else {
                                        selector.insertAfter(secondLine, moveTd, sTarget);
                                    }
                                }
                                
                                if (pg === _currentPage) {
                                    self.levelStruct[1].splice(tailInsertIdx + colspan, 0, self.levelStruct[1].splice(nlay, 1)[0]);
                                }
                                
                                colGripIdx = gripCol + colspan - i;
                                if ($hGroup) {
                                    selector.insertAfter($hGroup, selector.findAt($hGroup, "col", colGripIdx), hgTarget);
                                }
                                
                                if (pmaf.desc) {
                                    _.forEach(pmaf.desc.rowElements, r => {
                                        if (_.isNil(r)) return;
                                        bbTarget = selector.findAt(r, "td", colTargetIdx - i);
                                        selector.insertAfter(r, selector.findAt(r, "td", colGripIdx), bbTarget);
                                    });
                                    _.forEach(pmaf.desc.rows, (r, z) => {
                                        if (_.isNil(r)) return;
                                        bbTarget = r.splice(colGripIdx - 1, 1);
                                        r.splice(colTargetIdx - 1 - i, 0, bbTarget[0]);
                                        if (z > 0 || pg !== _currentPage) return;
                                        for (let k = colGripIdx - 1; k < colTargetIdx - i; k++) {
                                            let coord = ti.getCellCoord(r[k]);
                                            updatedCols[coord.columnKey] = k;
                                        }
                                    });
                                }
                                
                                if (pg === _currentPage) {
                                    _.forEach([ "b", "h", "sum" ], arrType => {
                                        let arr = _mafollicle[SheetDef][_currentSheet][arrType + "ColArr"];
                                        if (_.isNil(arr)) return;
                                        bbTarget = arr.splice(colGripIdx - 1, 1);
                                        arr.splice(colTargetIdx - 1 - i, 0, bbTarget[0]);
                                    });
                                    
                                    self.columns.splice(colTargetIdx - 1 - i, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                                }
                                
                                if (tbody) {
                                    selector.insertAfter(pmaf.$bGroup, selector.findAt(pmaf.$bGroup, "col", colGripIdx), bgTarget);
                                }
                                if (sBody) {
                                    selector.insertAfter(sBodyRow, selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                                    selector.insertAfter(sGroup, selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                                }
                            } else {
                                if (tbody) {
                                    sTarget = secondLine.querySelector("td:nth-of-type(" + (headInsertIdx + 1 + i) + ")");
                                    secondLine.insertBefore(secondLine.querySelector("td:nth-of-type(" + (nlay + 1 + i) + ")") , sTarget);
                                }
                                
                                if (pg === _currentPage) {
                                    self.levelStruct[1].splice(headInsertIdx, 0, self.levelStruct[1].splice(nlay + colspan - 1, 1)[0]);
                                }
                                
                                colGripIdx = gripCol + 1 + i;
                                if (pmaf.$hGroup) {
                                    pmaf.$hGroup.insertBefore(selector.findAt(pmaf.$hGroup, "col", colGripIdx), hgTarget);
                                }
                                
                                if (pmaf.desc) {
                                    _.forEach(pmaf.desc.rowElements, r => {
                                        if (_.isNil(r)) return;
                                        bbTarget = selector.findAt(r, "td", colTargetIdx + i);
                                        r.insertBefore(selector.findAt(r, "td", colGripIdx), bbTarget);
                                    });
                                    _.forEach(pmaf.desc.rows, (r, z) => {
                                        if (_.isNil(r)) return;
                                        bbTarget = r.splice(colGripIdx - 1, 1);
                                        r.splice(colTargetIdx - 1 + i, 0, bbTarget[0]);
                                        if (z > 0 || pg !== _currentPage) return;
                                        for (let k = colTargetIdx - 1 + i; k < colGripIdx; k++) {
                                            let coord = ti.getCellCoord(r[k]);
                                            updatedCols[coord.columnKey] = k;
                                        }
                                    });
                                }
                                    
                                if (pg === _currentPage) {
                                    _.forEach([ "b", "h", "sum" ], arrType => {
                                        let arr = _mafollicle[SheetDef][_currentSheet][arrType + "ColArr"];
                                        if (_.isNil(arr)) return;
                                        bbTarget = arr.splice(colGripIdx - 1, 1);
                                        arr.splice(colTargetIdx - 1 + i, 0, bbTarget[0]);
                                    });
                                    
                                    self.columns.splice(colTargetIdx - 1 + i, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                                }
                                
                                if (tbody) {
                                    pmaf.$bGroup.insertBefore(selector.findAt(pmaf.$bGroup, "col", colGripIdx), bgTarget);
                                }
                                if (sBody) {
                                    sBodyRow.insertBefore(selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                                    sGroup.insertBefore(selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                                }
                            }
                        }
                    } else {
                        headInsertIdx = -1;
                        nlay = -1;
                        colGripIdx = gripCol + 1;
                        
                        if (self.piston.index < index) {
                            if (pmaf.$hGroup) {
                                selector.insertAfter(pmaf.$hGroup, selector.findAt(pmaf.$hGroup, "col", colGripIdx), hgTarget);
                            }
                            
                            if (pmaf.desc) {
                                _.forEach(pmaf.desc.rowElements, r => {
                                    if (_.isNil(r)) return;
                                    bbTarget = selector.findAt(r, "td", colTargetIdx);
                                    selector.insertAfter(r, selector.findAt(r, "td", colGripIdx), bbTarget);
                                });
                                _.forEach(pmaf.desc.rows, (r, z) => {
                                    if (_.isNil(r)) return;
                                    bbTarget = r.splice(colGripIdx - 1, 1);
                                    r.splice(colTargetIdx - 1, 0, bbTarget[0]);
                                    if (z > 0 || pg !== _currentPage) return;
                                    for (let k = colGripIdx - 1; k < colTargetIdx; k++) {
                                        let coord = ti.getCellCoord(r[k]);
                                        updatedCols[coord.columnKey] = k;
                                    }
                                });
                            }
                            
                            if (pg === _currentPage) {
                                _.forEach([ "b", "h", "sum" ], arrType => {
                                    let arr = _mafollicle[SheetDef][_currentSheet][arrType + "ColArr"];
                                    if (_.isNil(arr)) return;
                                    bbTarget = arr.splice(colGripIdx - 1, 1);
                                    arr.splice(colTargetIdx - 1, 0, bbTarget[0]);
                                });
                                
                                self.columns.splice(colTargetIdx - 1, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                            }
                            
                            if (tbody) {
                                selector.insertAfter(pmaf.$bGroup, selector.findAt(pmaf.$bGroup, "col", colGripIdx), bgTarget);
                            }
                            if (sBody) {
                                selector.insertAfter(sBodyRow, selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                                selector.insertAfter(sGroup, selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                            }
                        } else {
                            if (pmaf.$hGroup) {
                                pmaf.$hGroup.insertBefore(selector.findAt(pmaf.$hGroup, "col", colGripIdx), hgTarget);
                            }
                            
                            if (pmaf.desc) {
                                _.forEach(pmaf.desc.rowElements, r => {
                                    if (_.isNil(r)) return;
                                    bbTarget = selector.findAt(r, "td", colTargetIdx);
                                    r.insertBefore(selector.findAt(r, "td", colGripIdx), bbTarget);
                                });
                                _.forEach(pmaf.desc.rows, (r, z) => {
                                    if (_.isNil(r)) return;
                                    bbTarget = r.splice(colGripIdx - 1, 1);
                                    r.splice(colTargetIdx - 1, 0, bbTarget[0]);
                                    if (z > 0 || pg !== _currentPage) return;
                                    for (let k = colTargetIdx - 1; k < colGripIdx; k++) {
                                        let coord = ti.getCellCoord(r[k]);
                                        updatedCols[coord.columnKey] = k;
                                    }
                                });
                            }
                            
                            if (pg === _currentPage) {
                                _.forEach([ "b", "h", "sum" ], arrType => {
                                    let arr = _mafollicle[SheetDef][_currentSheet][arrType + "ColArr"];
                                    if (_.isNil(arr)) return;
                                    bbTarget = arr.splice(colGripIdx - 1, 1);
                                    arr.splice(colTargetIdx - 1, 0, bbTarget[0]);
                                });
                                
                                self.columns.splice(colTargetIdx - 1, 0, self.columns.splice(colGripIdx - 1, 1)[0]);
                            }
                            
                            if (tbody) {
                                pmaf.$bGroup.insertBefore(selector.findAt(pmaf.$bGroup, "col", colGripIdx), bgTarget);
                            }   
                            if (sBody) {
                                sBodyRow.insertBefore(selector.findAt(sBodyRow, "td", colGripIdx), sbTarget);
                                sGroup.insertBefore(selector.findAt(sGroup, "col", colGripIdx), sgTarget);
                            }
                        }
                    }
                    
                    if (self.piston.index < index && tbody) {
                        firstLine.insertBefore(self.piston.cell, target.nextSibling);
                    } else if (tbody) {
                        firstLine.insertBefore(self.piston.cell, target);
                    }
                    
                    if (_.keys(updatedCols).length > 0) {
                        let val, idxes = pmaf.desc.colIdxes;
                        _.forEach(_.keys(idxes), c => {
                            val = updatedCols[c];
                            if (!_.isNil(val)) {
                                idxes[c] = val;
                            }
                        });
                    } 
                });
                
                self.levelStruct[0].splice(index, 0, self.levelStruct[0].splice(self.piston.index, 1)[0]);
                _cloud.painter.revive();
                if (kt._adjuster) {
                    let csd = _mafollicle[SheetDef][_currentSheet];
                    kt._adjuster.nostal(csd.hColArr, csd.bColArr, csd.sumColArr, self.fixed);
                    kt._adjuster.handle();
                }
            }
            
        }
        
        export class ConcurrentPainter {
            painter: any;
            painters: Array<Painter>;
            controlMap: any;
            primaryKey: string;
            columns: Array<string> = [];
            states: any;
            protoCell: HTMLElement;
            protoRow: HTMLElement;
            
            constructor(ui: any) {
                this.revive();
                this.primaryKey = ui.primaryKey;
                this.states = ui.states;
                this.protoRow = document.createElement("tr");
                this.protoCell = document.createElement("td");
            }
            
            /**
             * Revive.
             */
            revive() {
                this.painters = _mafollicle[SheetDef][_currentSheet].painters;
                this.columns = [];
                _.forEach(this.painters, p => _.forEach(p.columns, c => {
                    this.columns.push(c.key);
                }));
                
                this.controlMap = _mafollicle[SheetDef][_currentSheet].controlMap;
            }
            
            /**
             * Cell.
             */
            cell(rData: any, rowIdx: number, key: string, fixed: boolean, numText?: any) {
                let self = this;
                let cData = rData[key]; 
                let data = cData, columnsMap, visibleColumnsMap, paint;
                if (fixed) {
                    paint = self.painters[0];
                } else if (self.painters.length > 1) {
                    paint = self.painters[1];
                } else {
                    paint = self.painters[0];
                }
                
                columnsMap = paint.columnsMap;
                visibleColumnsMap = paint.visibleColumnsMap;
                
                let column: any = columnsMap[key];
                if (_.isNil(column)) return;
                let ws = column.css && column.css.whiteSpace ? column.css.whiteSpace : "nowrap";
//                let td = document.createElement("td");
                let td = self.protoCell.cloneNode(true);
                td.classList.add(CELL_CLS);
                td.tabIndex = -1;
                $.data(td, lo.VIEW, rowIdx + "-" + key);
                
                let tdStyle = "";
                tdStyle += "; border-width: 1px; overflow: hidden; white-space: " 
                            + ws + "; padding: 0px 2px; ";//position: relative;";
                
                let col = visibleColumnsMap[key];
                if (!col) tdStyle += "; display: none;";
                else if (!_.isNil(col[0].columnCssClass)) {
                    col[0].columnCssClass.split(' ').forEach(clz => {
                        if (clz === hpl.CURRENCY_CLS || clz === v.ALIGN_RIGHT || clz === v.ALIGN_CENTER) {
                            td.classList.add(clz);
                        }
                    });
                }
                
                if (key === "rowNumber") {
                    td.innerHTML = cData; //!_.isNil(numText) ? numText : rowIdx + 1;
                    //tdStyle += "; background-color: #CFF1A5; ";
                    td.style.cssText += tdStyle;
                    td.classList.add(STT_CLS);
                    return td;
                }
                
                let controlDef = self.controlMap[key];
                let id = rData[self.primaryKey];
                let rState, cState, disabled;
                if (self.states && (rState = self.states[id]) && (cState = rState[key])) {
                    _.forEach(cState, s => {
                        _.forEach(s.state, st => {
                            if (st.indexOf('#') === 0) {
                                tdStyle += "; color: " + cState + ";";
                            } else if (st === color.ManualEditTarget || st === color.ManualEditOther) {
                                td.classList.add(st);
                                if (!s.suivant) $.data(td, INIT_MAN_EDIT, st);
                            } else {
                                if (st === color.Disable) disabled = true;
                                td.classList.add(st);
                            }
                        });
                    });
                    rState = null;
                    cState = null;
                }
                
                if (_vessel().checkedErrors) {
                    let ei = _vessel().checkedErrors.length - 1;
                    while (ei >= 0) {
                        let info = _vessel().checkedErrors[ei];
                        if (rData[_pk] === info.id && key === info.columnKey) {
                            info.element = td;
                            khl.set(info, info.message, 1, true);
                            _vessel().checkedErrors.splice(ei, 1);
                            break;
                        }
                        ei--;
                    }
                }
                
                if (td.classList.contains(color.Lock)) {
                    td.style.cssText += tdStyle;
                    if (controlDef && controlDef.controlType === dkn.COMBOBOX) {
                        dkn.getControl(controlDef.controlType)({
                            rowIdx: rowIdx,
                            rowId: id,
                            columnKey: key,
                            controlDef: controlDef,
                            update: (v, i, r, p) => {
                                su.wedgeCell(_$grid[0], { rowIdx: (_.isNil(i) ? rowIdx : i), columnKey: key }, v, r, null, p);
                                if (_.isFunction(controlDef.onChange)) {
                                    let rObj = _dataSource[i];
                                    controlDef.onChange(rObj[_pk], key, v, rObj);
                                }
                            },
                            deleteRow: su.deleteRow,
                            initValue: data,
                            rowObj: rData,
                            enable: !td.classList.contains(color.Disable)
                        });
                    }
                    
                    return td;
                }
                
                if (column.ntsControl === dkn.LABEL) {
                    td.classList.add(dkn.LABEL_CLS);
                    td.innerHTML = _.isNil(data) || td.classList.contains(color.Disable) ? "" : data;
                    $.data(td, DATA, data);
                    dkn.controlType[key] = dkn.LABEL;
                } else if (controlDef) {
                    let allCheckKey, ui: any = {
                        rowIdx: rowIdx,
                        rowId: id,
                        columnKey: key,
                        controlDef: controlDef,
                        update: (v, i, r, p) => {
                            su.wedgeCell(_$grid[0], { rowIdx: (_.isNil(i) ? rowIdx : i), columnKey: key }, v, r, null, p);
                            if (_.isFunction(controlDef.onChange)) {
                                let rObj = _dataSource[i];
                                controlDef.onChange(rObj[_pk], key, v, rObj);
                            }
                        },
                        deleteRow: su.deleteRow,
                        initValue: data,
                        rowObj: rData,
                        enable: !td.classList.contains(color.Disable)
                    };
                    
                    if (column.checkbox && (allCheckKey = dkn.allCheck[key])) {
                        if (disabled) allCheckKey.overall--;
                        if (allCheckKey.stt === false && _.isNil(allCheckKey.toggle) && !disabled
                            && allCheckKey.count < allCheckKey.overall && data) allCheckKey.count++;
                    }
                    
                    let res, control = dkn.getControl(controlDef.controlType);
                    if (control) {
                        if (controlDef.controlType === dkn.CHECKBOX && ui.enable) {
                            let origVal = _mafollicle[_currentPage].origDs[rowIdx][key];
                            if (allCheckKey && dkn.allCheck[key].toggle === true) {
                                ui.initValue = true;
                                res = su.wedgeCell(_$grid[0], { rowIdx: rowIdx, columnKey: key }, true);
                                if (res) td.classList.add(res);
                            } else if (allCheckKey && dkn.allCheck[key].toggle === false) {
                                ui.initValue = false;
                                res = su.wedgeCell(_$grid[0], { rowIdx: rowIdx, columnKey: key }, false);
                                if (res) td.classList.add(res);
                            }
                        }
                        
                        let $control = control(ui);
                        if (controlDef.controlType === dkn.COMBOBOX) {
                            td.innerHTML = $control[controlDef.optionsText];
                            $.data(td, "code", $control[controlDef.optionsValue]);
                        } else if (controlDef.controlType === dkn.DATE_PICKER) {
                            td.innerHTML = data instanceof moment && !data.isValid() ? data._i : $control;
                        } else if (controlDef.controlType === dkn.FLEX_IMAGE || controlDef.controlType === dkn.IMAGE) {
                            tdStyle += "; text-align: center; ";
                            td.appendChild($control);
                        } else {
                            td.appendChild($control);
                        }
                    }
                    $.data(td, DATA, data);
                } else if (_zeroHidden && ti.isZero(data, key)) {
                    td.textContent = "";
                    dkn.textBox(key);
                    let formatted = su.format(column, data);
                    let disFormat = su.formatSave(column, data); 
                    $.data(td, DATA, disFormat);
                } else {
                    let formatted = su.format(column, data);
                    td.innerHTML = _.isNil(formatted) ? "" : formatted;
                    dkn.textBox(key);
                    let disFormat = su.formatSave(column, data);
                    $.data(td, DATA, disFormat);
                }
                td.style.cssText += tdStyle;
                
//                let sum = _summaries[key];
//                if (sum) {
//                    switch (sum.calculator) {
//                        case "Time":
//                            sum.total.add(moment.duration(data));
//                            break;
//                        case "Number": 
//                            sum.total += (!_.isNil(data) ? parseFloat(data) : 0);
//                            break;
//                    } 
//                }
                
                return td;
            }
            
            /**
             * Row.
             */
            row(data: any, config: any, rowIdx: number, numText?: any) {
                let self = this;
                let fixedColumns, fixedCount = 0;
                if (rowIdx === 0 && self.painters.length > 1) {
                    fixedColumns = self.painters[0].columns;
                    fixedCount = fixedColumns.length;
                }
                
                let fixedVColumnsMap, dVColumnsMap, hiddenFixed, hiddenDV;
                if (self.painters.length > 1) {
                    fixedVColumnsMap = self.painters[0].visibleColumnsMap;
                    hiddenFixed = self.painters[0].hiddenColumnsMap;
                    dVColumnsMap = self.painters[1].visibleColumnsMap; 
                    hiddenDV = self.painters[1].hiddenColumnsMap;
                } else {
                    dVColumnsMap = self.painters[0].visibleColumnsMap;
                    hiddenDV = self.painters[0].hiddenColumnsMap;
                }
                
                let fixedColIdxes = {}, colIdxes = {},
                    fixedElements = [], elements = [],
                    fixedTr, tr: any = self.protoRow.cloneNode(true); //document.createElement("tr");
                $.data(tr, lo.VIEW, rowIdx);
                if (fixedVColumnsMap && _.keys(fixedVColumnsMap).length > 0) {
//                    fixedTr = document.createElement("tr");
                    fixedTr = self.protoRow.cloneNode(true);
                    $.data(fixedTr, lo.VIEW, rowIdx);
                }
                
                if (config) {
                    if (fixedTr) {
                        fixedTr.style.height = parseFloat(config.css.height) + "px";
                    }
                    tr.style.height = parseFloat(config.css.height) + "px";
                }
                
                _.forEach(self.columns, function(key: any, index: number) {
                    let cell;
                    if (dVColumnsMap[key] || hiddenDV[key]) {
                        cell = self.cell(data, rowIdx, key, null, numText);
                        tr.appendChild(cell);
                        elements.push(cell);
                        if (rowIdx === 0) colIdxes[key] = index - fixedCount;
                    } else if (fixedVColumnsMap[key] || hiddenFixed[key]) {
                        cell = self.cell(data, rowIdx, key, true, numText);
                        fixedTr.appendChild(cell);
                        fixedElements.push(cell);
                        if (rowIdx === 0) fixedColIdxes[key] = index;
                    }
                });
                
                if (fixedTr) {
                    fixedTr.addXEventListener(ssk.MOUSE_OVER, evt => {
                        self.hoover(evt);
                    });
                    
                    fixedTr.addXEventListener(ssk.MOUSE_OUT, evt => {
                        self.hoover(evt, true);
                    });
                }
                
                tr.addXEventListener(ssk.MOUSE_OVER, evt => {
                    self.hoover(evt);
                });
                
                tr.addXEventListener(ssk.MOUSE_OUT, evt => {
                    self.hoover(evt, true);
                });
                
                if (_.keys(_voilerRows).length > 0
                    && _.includes(_voilerRows[Math.floor(rowIdx / (aho._bloc * 2 - 1))], rowIdx)) {
                    fixedTr.style.display = "none";
                    tr.style.display = "none";
                }
                
                let ret = { fixedRow: fixedTr, row: tr, fixedElements: fixedElements, elements: elements }; 
                if (rowIdx === 0) {
                    ret.fixedColIdxes = fixedColIdxes;
                    ret.colIdxes = colIdxes;
                }
                
                return ret;
            }
            
            /**
             * Hoover.
             */
            hoover(evt: any, out?: boolean) {
                let $tCell = evt.target;
                if (!selector.is($tCell, "td." + CELL_CLS)) return;
                let coord = ti.getCellCoord($tCell);
                if (!coord) return;
                let elms;
                if (_mDesc.fixedRows && (elms = _mDesc.fixedRows[coord.rowIdx])) {
                    _.forEach(elms, c => {
                        if (!c.classList.contains(color.HOVER) && !out) {
                            c.classList.add(color.HOVER);
                            _hr = coord.rowIdx;
                        } else if (c.classList.contains(color.HOVER) && out) {
                            c.classList.remove(color.HOVER);
                        }
                    });
                }
                
                if (_mDesc.rows && (elms = _mDesc.rows[coord.rowIdx])) {
                    _.forEach(elms, c => {
                        if (!c.classList.contains(color.HOVER) && !out) {
                            c.classList.add(color.HOVER);
                            _hr = coord.rowIdx;
                        } else if (c.classList.contains(color.HOVER) && out) {
                            c.classList.remove(color.HOVER);
                        }
                    });
                }
                
                if (!khl._infobulle || !$tCell.classList.contains(khl.ERROR_CLS)) return;
                if (!out) {
                    document.body.appendChild(khl._infobulle);
                    khl._infobulle.innerHTML = $.data($tCell, "msg");
                    dkn.openDD(khl._infobulle, $tCell, true);
                } else {
                    ti.remove(khl._infobulle);
                    dkn.closeDD(khl._infobulle, true);
                }
            }
        }
        
        export class SidePainter {
            columnsMap: any;
            columns: Array<any>;
            visibleColumns: Array<any>;
            visibleColumnsMap: any;
            controlMap: any;
            primaryKey: string;
            states: any;
            fails: any;
            protoCell: HTMLElement;
            protoRow: HTMLElement;
            
            constructor(ui: any) {
                this.revive();
                this.primaryKey = ui.primaryKey;
                this.states = ui.states;
                this.protoRow = document.createElement("tr");
                this.protoCell = document.createElement("td");
            }
            
            /**
             * Revive.
             */
            revive(sht?: any) {
                let cst = !_.isNil(sht) ? _mafollicle[SheetDef][sht].columns : _cstifle(),
                    colCls = ti.classifyColumns({ columns: cst });
                this.columns = colCls.columns;
                this.visibleColumns = colCls.visibleColumns;
                this.visibleColumnsMap = ti.getColumnsMap(this.visibleColumns);
                this.controlMap = _mafollicle[SheetDef][!_.isNil(sht) ? sht : _currentSheet].controlMap;
                let levelStruct = _mafollicle[SheetDef][!_.isNil(sht) ? sht : _currentSheet].levelStruct;
                
                if (!_.isNil(levelStruct)) {
                    this.columnsMap = ti.columnsMapFromStruct(levelStruct);
                } else {
                    this.columnsMap = _.groupBy(cst, "key");
                }
            }
            
            /**
             * Cell.
             */
            cell(rData: any, rowIdx: number, key: string, fixed: boolean) {
                let self = this;
                let cData = rData[key]; 
                let data = cData;
                
                let column = self.columnsMap[key];
                if (_.isNil(column)) return;
                let ws = column.css && column.css.whiteSpace ? column.css.whiteSpace : "nowrap";
//                let td = document.createElement("td");
                let td = self.protoCell.cloneNode(true);
                td.classList.add(CELL_CLS);
                td.tabIndex = -1;
                $.data(td, lo.VIEW, rowIdx + "-" + key);
                
                let tdStyle = "";
                tdStyle += "; border-width: 1px; overflow: hidden; white-space: " 
                            + ws + "; padding: 0px 2px;"; // position: relative";
                
                let col = self.visibleColumnsMap[key];
                if (!col) tdStyle += "; display: none;";
                else if (!_.isNil(col[0].columnCssClass)) {
                    col[0].columnCssClass.split(' ').forEach(clz => {
                        if (clz === hpl.CURRENCY_CLS || clz === v.ALIGN_RIGHT || clz === v.ALIGN_CENTER) {
                            td.classList.add(clz);
                        }
                    });
                }
                let controlDef = self.controlMap[key];
                
                let id = rData[self.primaryKey];
                let rState, cState, found, disabled;
                if (self.states && (rState = self.states[id]) && (cState = rState[key])) {
                    _.forEach(cState, s => {
                        _.forEach(s.state, st => {
                            if (st.indexOf('#') === 0) {
                                tdStyle += "; color: " + cState + ";";
                            } else if (st === color.ManualEditTarget || st === color.ManualEditOther) {
                                td.classList.add(st);
                                if (!s.suivant) $.data(td, INIT_MAN_EDIT, st);
                            } else {
                                if (st === color.Disable) disabled = true;
                                td.classList.add(st);
                            }
                        });
                    });
                    rState = null;
                    cState = null;
                }
                
                if (_vessel().checkedErrors) {
                    let ei = _vessel().checkedErrors.length - 1;
                    while (ei >= 0) {
                        let info = _vessel().checkedErrors[ei];
                        if (rData[_pk] === info.id && key === info.columnKey) {
                            info.element = td;
                            khl.set(info, info.message, null, true);
                            _vessel().checkedErrors.splice(ei, 1);
                            break;
                        }
                        ei--;
                    }
                }
                
                if (td.classList.contains(color.Lock)) {
                    td.style.cssText += tdStyle;
                    if (controlDef && controlDef.controlType === dkn.COMBOBOX) {
                        dkn.getControl(controlDef.controlType)({
                            rowIdx: rowIdx,
                            rowId: id,
                            columnKey: key,
                            controlDef: controlDef,
                            update: (v, i, r, p) => {
                                su.wedgeCell(_$grid[0], { rowIdx: (_.isNil(i) ? rowIdx : i), columnKey: key }, v, r, null, p);
                                if (_.isFunction(controlDef.onChange)) {
                                    controlDef.onChange(id, key, v, rData);
                                }
                            },
                            deleteRow: su.deleteRow,
                            initValue: data,
                            rowObj: rData,
                            enable: !td.classList.contains(color.Disable)
                        });
                    }
                    
                    return td;
                }
                
                if (self.fails) {
                    found = _.remove(self.fails[rowIdx], f => f.columnKey === key);
                    if (found.length > 0) {
                        td.classList.add(khl.ERROR_CLS);
                        $.data(td, "msg", found[0].message);
                    }
                    let list = self.fails[rowIdx];
                    if (list && list.length === 0) delete self.fails[rowIdx];
                }
                
                if (column.ntsControl === dkn.LABEL) {
                    td.classList.add(dkn.LABEL_CLS);
                    td.innerHTML = _.isNil(data) || td.classList.contains(color.Disable) ? "" : data;
                    $.data(td, DATA, data);
                    dkn.controlType[key] = dkn.LABEL;
                } else if (controlDef) {
                    let allCheckKey, ui: any = {
                        rowIdx: rowIdx,
                        rowId: id,
                        columnKey: key,
                        controlDef: controlDef,
                        update: (v, i, r) => {
                            su.wedgeCell(_$grid[0], { rowIdx: (_.isNil(i) ? rowIdx : i), columnKey: key }, v, r)
                            if (_.isFunction(controlDef.onChange)) {
                                controlDef.onChange(id, key, v, rData);
                            }
                        },   
                        deleteRow: su.deleteRow,
                        initValue: data,
                        rowObj: rData,
                        enable: !td.classList.contains(color.Disable)
                    };
                    
                    if (column.checkbox && (allCheckKey = dkn.allCheck[key])) {
                        if (disabled) allCheckKey.overall--;
                        if (allCheckKey.stt === false && _.isNil(allCheckKey.toggle) && !disabled
                            && allCheckKey.count < allCheckKey.overall && data) allCheckKey.count++;
                    }
                    
                    let control = dkn.getControl(controlDef.controlType);
                    if (control) {
                        if (controlDef.controlType === dkn.CHECKBOX && ui.enable) {
                            let origVal = _mafollicle[_currentPage].origDs[rowIdx][key];
                            if (allCheckKey && dkn.allCheck[key].toggle === true) {
                                ui.initValue = true;
                                res = su.wedgeCell(_$grid[0], { rowIdx: rowIdx, columnKey: key }, true);
                                if (res) td.classList.add(res);
                            } else if (allCheckKey && dkn.allCheck[key].toggle === false) {
                                ui.initValue = false;
                                res = su.wedgeCell(_$grid[0], { rowIdx: rowIdx, columnKey: key }, false);
                                if (res) td.classList.add(res);
                            }
                        }
                        
                        let $control = control(ui);
                        if (controlDef.controlType === dkn.COMBOBOX) {
                            td.innerHTML = $control[controlDef.optionsText];
                            $.data(td, "code", $control[controlDef.optionsValue]);
                        } else if (controlDef.controlType === dkn.DATE_PICKER) {
                            td.innerHTML = $control;
                        } else if (controlDef.controlType === dkn.FLEX_IMAGE || controlDef.controlType === dkn.IMAGE) {
                            tdStyle += "; text-align: center;";
                            td.appendChild($control);
                        } else {
                            td.appendChild($control);
                        }
                    }
                    $.data(td, DATA, data);
                } else if (_zeroHidden && ti.isZero(data, key)) {
                    td.textContent = "";
                    dkn.textBox(key);
                    let formatted = su.format(column, data);
                    let disFormat = su.formatSave(column, data);
                    $.data(td, DATA, disFormat);
                } else {
                    let formatted = su.format(column, data);
                    td.innerHTML = _.isNil(formatted) ? "" : formatted;
                    dkn.textBox(key);
                    let disFormat = su.formatSave(column, data);
                    $.data(td, DATA, disFormat);
                }
                td.style.cssText += tdStyle;
                
//                let sum = _summaries[key];
//                if (sum) {
//                    switch (sum.calculator) {
//                        case "Time":
//                            sum.total.add(moment.duration(data));
//                            break;
//                        case "Number": 
//                            sum.total += (!_.isNil(data) ? parseFloat(data) : 0);
//                            break;
//                    } 
//                }
                
                return td;
            }
            
            /**
             * Row.
             */
            row(data: any, config: any, rowIdx: number) {
                let self = this; 
                let colIdxes = {}, elements = [], 
                    tr: any = self.protoRow.cloneNode(true); //document.createElement("tr");
                
                $.data(tr, lo.VIEW, rowIdx);
                if (config) {
                    tr.style.height = parseFloat(config.css.height) + "px";
                }
                
                _.forEach(self.columns, function(col: any, index: number) {
                    let cell, key = col.key;
                    cell = self.cell(data, rowIdx, key);
                    tr.appendChild(cell);
                    elements.push(cell);
                    if (rowIdx === _start) colIdxes[key] = index;
                });
                
                tr.addXEventListener(ssk.MOUSE_OVER, evt => {
                    self.hoover(evt);
                });
                
                tr.addXEventListener(ssk.MOUSE_OUT, evt => {
                    self.hoover(evt, true);
                });
                
                if (_.keys(_voilerRows).length > 0
                    && _.includes(_voilerRows[Math.floor(rowIdx / (aho._bloc * 2 - 1))], rowIdx)) {
                    tr.style.display = "none";
                }
                
                let ret = { row: tr, elements: elements }; 
                if (rowIdx === _start) {
                    ret.colIdxes = colIdxes;
                }
                
                return ret;
            }
            
            /**
             * Hoover.
             */
            hoover(evt: any, out?: boolean) {
                let $tCell = evt.target;
                if (!selector.is($tCell, "td." + CELL_CLS)) return;
                let coord = ti.getCellCoord($tCell);
                if (!coord) return;
                let elms;
                if (_mDesc.fixedRows && (elms = _mDesc.fixedRows[coord.rowIdx])) {
                    _.forEach(elms, c => {
                        if (!c.classList.contains(color.HOVER) && !out) {
                            c.classList.add(color.HOVER);
                            _hr = coord.rowIdx;
                        } else if (c.classList.contains(color.HOVER) && out) {
                            c.classList.remove(color.HOVER);
                        }
                    });
                }
                
                if (_mDesc.rows && (elms = _mDesc.rows[coord.rowIdx])) {
                    _.forEach(elms, c => {
                        if (!c.classList.contains(color.HOVER) && !out) {
                            c.classList.add(color.HOVER);
                            _hr = coord.rowIdx;
                        } else if (c.classList.contains(color.HOVER) && out) {
                            c.classList.remove(color.HOVER);
                        }
                    });
                }
                
                if (!khl._infobulle || !$tCell.classList.contains(khl.ERROR_CLS)) return;
                if (!out) {
                    document.body.appendChild(khl._infobulle);
                    khl._infobulle.innerHTML = $.data($tCell, "msg");
                    dkn.openDD(khl._infobulle, $tCell, true);
                } else {
                    ti.remove(khl._infobulle);
                    dkn.closeDD(khl._infobulle, true);
                }
            }
        }
        
        /**
         * Extra.
         */
        export function extra(className: string, height: number) {
            let element = document.createElement("tr");
            element.style.height = height + "px";
            ti.addClass(element, "mgrid-" + className);
            return element;
        }
        
        /**
         * Wrapper styles.
         */
        export function wrapperStyles(top: string, left: string, width: string, maxWidth?: string, height: string) {
            let style: any = { 
                position: "absolute",
                overflow: "hidden",
                top: top,
                left: left,
                width: width,
                border: "solid 1px #CCC"
            };
            
            if (maxWidth) {
                style.maxWidth = maxWidth;
                if (parseFloat(maxWidth) < parseFloat(width)) style.width = maxWidth;
            }
            
            if (height) {
                style.height = height;
            }
            
            return style;   
        }
        
        /**
         * Calc width.
         */
        export function calcWidth(columns: Array<any>) {
            let width = 0;
            columns.forEach(function(c, i) {
                if (c.hidden === true) return;
                if (c.group) {
                    width += calcWidth(c.group);
                    return;
                }
                width += parseFloat(c.width);
            });
            return width;
        }
        
        /**
         * Create wrapper.
         */
        export function createWrapper(top: string, left: string, options: any, newOpt?: any) {
            let style, width, maxWidth;
            if (options.containerClass === FREE) {
                if (!_maxFreeWidth || newOpt) {
                    _maxFreeWidth = calcWidth(options.columns);
                } 
                maxWidth = options.isHeader ? _maxFreeWidth : _maxFreeWidth + ti.getScrollWidth();
                style = wrapperStyles(top, left, options.width, maxWidth + "px", options.height);
                style["background-color"] = "#F3F3F3"; 
                style["padding-right"] = "1px";
            } else if (options.containerClass === FIXED) {
                if (!_maxFixedWidth || newOpt) {
                    _maxFixedWidth = calcWidth(options.columns);
                }
                style = wrapperStyles(top, left, _maxFixedWidth + "px", undefined, options.height);
                style["background-color"] = "#F3F3F3";
                style["padding-right"] = "1px";
            } else if (options.containerClass === gp.PAGING_CLS) {
                style = wrapperStyles(top, left, options.width, undefined, options.height);
                style["background-color"] = "#E9E9E9";
                style["border"] = "1px solid #dddddd";
                style["color"] = "#333333";
            } else if (options.containerClass === gp.SHEET_CLS) {
                style = wrapperStyles(top, left, options.width, undefined, options.height);
//                style["background-color"] = "#E9E9E9";
                style["border"] = "0px solid #dddddd";
                style["color"] = "#333333";
            } else {
                width = options.containerClass === FIXED + "-summaries" ? _maxFixedWidth + "px" : options.width;
                maxWidth = options.containerClass !== FIXED + "-summaries" ? _maxFreeWidth + "px" : undefined;
                style = wrapperStyles(top, left, width, maxWidth, options.height);
                style["z-index"] = 1; 
                style["background-color"] = "#F6F6F6";
            }
            
            return selector.create("div").data(lo.MPART, options.containerClass)
                        .addClass(options.containerClass)
                        .css(style).getSingle();
        }
        
        export function voilerRow(idx: any) {
            if (_.isNil(idx)) return;
            let nama = Math.floor(idx / (aho._bloc * 2 - 1));
            
            if (!_voilerRows[nama]) _voilerRows[nama] = [];
            _voilerRows[nama].push(idx);
            
            if (idx <=_end && idx >= _start) {
                idx -= _start;
                _.forEach(_bodyWrappers, b => {
                    let r = b.querySelector("tr:nth-of-type(" + (idx + 2) + ")");
                    if (r) r.style.display = "none";
    //                let last = b.querySelector("tr:last-child");
    //                if (last) last.style.height = parseFloat(last.style.height) - BODY_ROW_HEIGHT + "px";
                });
                
                _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let maf = _mafollicle[_currentPage][k];
                    if (k === _currentSheet || !maf || !maf.$bBody) return;
                    let r = maf.$bBody.querySelector("tr:nth-of-type(" + (idx + 2) + ")");
                    if (r) r.style.display = "none";
                });
            } else {
                _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let maf = _mafollicle[_currentPage][k];
                    if (!maf || !maf.desc) return;
                    let r = maf.desc.rowElements[idx];
                    if (r) r.style.display = "none";
                    let fr = maf.desc.fixedRowElements[idx];
                    if (fr) fr.style.display = "none";
                });
            }
        }
        
        export function encarterRow(idx: any, copy: any, cssClass: any, numText: any) {
            let newData = {}, data = _dataSource[idx];
            if (!data) return;
            _.forEach(_.keys(data), k => {
                if (k === _pk) {
                    newData[k] = _.isFunction(_idIntpl) ? _idIntpl(data[_pk]) : util.randomId();
                    if (copy === COPY.DATA_AND_LOOK && _cellStates) {
                        let gotStt = _cellStates[data[k]];
                        if (!gotStt) return;
                        _cellStates[newData[k]] = _.cloneDeep(gotStt);
                    }
                } else if (k === "rowNumber") {
                    newData[k] = data[k] + 1;
                } else if (copy === COPY.DATA || copy === COPY.DATA_AND_LOOK) {
                    newData[k] = data[k];
                } else newData[k] = null;
            });
            
            _dataSource.splice(idx + 1, 0, newData); 
            _mafollicle[_currentPage].origDs.splice(idx + 1, 0, _.cloneDeep(newData));
            let no, noc, config = { css: { height: BODY_ROW_HEIGHT } }, ret = _cloud.painter.row(newData, config, idx + 1, numText);
            if (!ret || !_mDesc) return;
            if (_mDesc.fixedRowElements) {
                _mDesc.fixedRowElements.splice(idx + 1, 0, ret.fixedRow);
                if (cssClass && ret.fixedRow) {
                    _.forEach(_.split(cssClass, ' '), pcls => {
                        ret.fixedRow.classList.add(pcls);
                    });
                }
            }
            if (_mDesc.fixedRows) {
                if (_.has(_mDesc.fixedColIdxes, "rowNumber")) {
                    no = _mDesc.fixedColIdxes.rowNumber;
                    let tRow = _mDesc.fixedRows[idx][no];
                    for (let i = /*idx + 2*/0; i < _mDesc.fixedRows.length; i++) {
                        noc = _mDesc.fixedRows[i];
                        if (noc && (noc = noc[no]) && parseInt(noc.innerHTML) > parseInt(tRow.innerHTML)) {
                            noc.innerHTML = parseInt(noc.innerHTML) + 1;
                        }
                        
                        let pcur = _dataSource[i]; 
                        if (i !== idx + 1 && pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data.rowNumber) {
                            pcur.rowNumber += 1;
                        }
                    }
                    
                    if (_mDesc.fixedRows.length < _dataSource.length) {
                        for (let i = _mDesc.fixedRows.length; i < _dataSource.length; i++) {
                            let pcur = _dataSource[i]; 
                            if (i !== idx + 1 && pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data.rowNumber) {
                                pcur.rowNumber += 1;
                            }
                        }
                    }
                }
                _mDesc.fixedRows.splice(idx + 1, 0, ret.fixedElements);
            }
            if (_mDesc.rowElements) {
                _mDesc.rowElements.splice(idx + 1, 0, ret.row);
                if (cssClass && ret.row) {
                    _.forEach(_.split(cssClass, ' '), pcls => {
                        ret.row.classList.add(pcls);
                    });
                }
            }
            if (_mDesc.rows) {
                if (_.has(_mDesc.colIdxes, "rowNumber")) {
                    no = _mDesc.colIdxes.rowNumber;
                    let tRow = _mDesc.rows[idx][no];
                    for (let i = /*idx + 2*/0; i < _mDesc.rows.length; i++) {
                        noc = _mDesc.rows[i];
                        if (noc && (noc = noc[no]) && parseInt(noc.innerHTML) > parseInt(tRow.innerHTML)) {
                            noc.innerHTML = parseInt(noc.innerHTML) + 1;
                        }
                        
                        let pcur = _dataSource[i]; 
                        if (i !== idx + 1 && pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data.rowNumber) {
                            pcur.rowNumber += 1;
                        }
                    }
                    
                    if (_mDesc.rows.length < _dataSource.length) {
                        for (let i = _mDesc.rows.length; i < _dataSource.length; i++) {
                            let pcur = _dataSource[i];
                            if (i !== idx + 1 && pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data.rowNumber) {
                                pcur.rowNumber += 1;
                            }
                        }
                    }
                }
                _mDesc.rows.splice(idx + 1, 0, ret.elements);
            }
            
            _.forEach(_bodyWrappers, (b, y) => {
                if (_bodyWrappers.length > 1) {
                    b.querySelector("tbody").insertBefore(y ? ret.row : ret.fixedRow, y ? _mDesc.rowElements[idx + 2] : _mDesc.fixedRowElements[idx + 2]);
                } else {
                    b.querySelector("tbody").insertBefore(ret.row, _mDesc.rowElements[idx + 2]);
                }
                
                let rList;
                if (b.classList.contains(FIXED)) {
                    rList = _mDesc.fixedRowElements;    
                } else {
                    rList = _mDesc.rowElements;
                }
                
                _.forEach(rList, (r, i) => {
                    if (i <= idx + 1 || !r) return;
                    $.data(r, lo.VIEW, parseInt($.data(r, lo.VIEW)) + 1);
                });
            });
            
            if (_mafCurrent().rank) {
                let rank = _mafCurrent().rank[data[_pk]];
                if (!_.isNil(rank)) {
                    _mafCurrent().rank[newData[_pk]] = rank + 0.0001;
                }
            }
            
            _.forEach(_errors, e => {
                if (e.index > idx) {
                    e.index += 1;
                }
            });
            
            _.forEach(_histoire, h => {
                _.forEach(h.o, o => {
                    if (o.coord.rowIdx > idx) {
                        o.coord.rowIdx += 1;
                    }
                });
            });
            
            let t = {};
            _.forEach(_.keys(_selected), r => {
                if (parseInt(r) > idx) {
                    t[r] = _selected[r];
                    delete _selected[r];
                }
            });
            
            _.forEach(_.keys(t), k => {
                _selected[parseInt(k) + 1] = t[k];
            });
            
            _.forEach(_.keys(v._voilerRows), pt => {
                _.forEach(v._voilerRows[pt], (vr, i) => {
                    if (vr > idx) {
                        v._voilerRows[pt][i]++;
                    }
                });
            });
            
            for (let ri = 0; ri < _encarRows.length; ri++) {
                if (_encarRows[ri] > idx) _encarRows[ri] += 1;
            }
            
            _encarRows.push(idx + 1);
            _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                let maf = _mafollicle[_currentPage][k];
                if (k === _currentSheet || !maf || !maf.desc) return;
                let painter = _mafollicle[SheetDef][k].ltrlPainter;
                if (!painter) {
                    painter = _.cloneDeep(_cloud.sidePainter);
                    painter.revive(k);
                    _mafollicle[SheetDef][k].ltrlPainter = painter;
                }
                let res = painter.row(newData, config, idx + 1);
                if (!res) return;
                if (maf.desc.fixedRowElements) {
                    maf.desc.fixedRowElements.splice(idx + 1, 0, ret.fixedRow);
                }
                if (maf.desc.fixedRows) {
                    maf.desc.fixedRows.splice(idx + 1, 0, ret.fixedElements);
                }
                if (maf.desc.rowElements) {
                    maf.desc.rowElements.splice(idx + 1, 0, res.row);
                }
                if (maf.desc.rows) {
                    maf.desc.rows.splice(idx + 1, 0, res.elements);
                }
                
                if (maf.$bBody) {
                    maf.$bBody.insertBefore(res.row, maf.desc.rowElements[idx + 2]);
                    let rList = maf.desc.rowElements;
                    _.forEach(rList, (r, i) => {
                        if (i <= idx + 1 || !r) return;
                        $.data(r, lo.VIEW, parseInt($.data(r, lo.VIEW)) + 1);
                    });
                }
                
                _.forEach(maf.errors, e => {
                    if (e.index > idx) {
                        e.index += 1;
                    }
                });
                
                _.forEach(maf.histoire, h => {
                    _.forEach(h.o, o => {
                        if (o.coord.rowIdx > idx) {
                            o.coord.rowIdx += 1;
                        }
                    });
                });
                
                t = {};
                _.forEach(_.keys(maf.selected), r => {
                    if (parseInt(r) > idx) {
                        t[r] = maf.selected[r];
                        delete maf.selected[r];
                    }
                });
                
                _.forEach(_.keys(t), k => {
                    maf.selected[parseInt(k) + 1] = t[k];
                });
            });
        }
        
        export function demoRows() {
            _.forEach(_.keys(_mafollicle), g => {
                let v;
                if (g === SheetDef || _.isNil(v = _mafollicle[g].voilRows)) return;
                _.forEach(_.keys(v), p => {
                    _.forEach(v[p], r => {
                        _.forEach(_.keys(_mafollicle[SheetDef]), d => {
                            let e, maf = _mafollicle[g][d];
                            if (!maf || !maf.desc) return;
                            if (maf.desc.fixedRowElements && (e = maf.desc.fixedRowElements[r])) {
                                e.style.display = "";
                            }
                            
                            if (maf.desc.rowElements && (e = maf.desc.rowElements[r])) {
                                e.style.display = "";
                            }
                        });
                    });
                });
                
                v = {};
            });
        }
        
        export function eliminRows(positions: any) {
            _.forEach(positions, i => {
                let data = _dataSource.splice(i, 1); 
                _mafollicle[_currentPage].origDs.splice(i, 1);
                if (!_mDesc || data.length === 0) return;
                let no, noc, fixedRow, row;
                if (_mDesc.fixedRowElements) {
                    fixedRow = _mDesc.fixedRowElements.splice(i, 1);
                    _.forEach(_mDesc.fixedRowElements, (r, y) => {
                        if (y < i || !r) return;
                        $.data(r, lo.VIEW, parseInt($.data(r, lo.VIEW)) - 1);
                    });
                }
                
                if (_mDesc.fixedRows) {
                    let tRow = _mDesc.fixedRows.splice(i, 1);
                    if (_.has(_mDesc.fixedColIdxes, "rowNumber")) {
                        no = _mDesc.fixedColIdxes.rowNumber;
                        for (let z = 0; z < _mDesc.fixedRows.length; z++) {
                            noc = _mDesc.fixedRows[z];
                            if (noc && (noc = noc[no]) && parseInt(noc.innerHTML) > parseInt((tRow && tRow[0])[no].innerHTML)) {
                                noc.innerHTML = parseInt(noc.innerHTML) - 1;
                            }
                            
                            let pcur = _dataSource[z];
                            if (pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data[0].rowNumber) {
                                pcur.rowNumber -= 1;
                            }
                        }
                        
                        if (_mDesc.fixedRows.length < _dataSource.length) {
                            for (let z = _mDesc.fixedRows.length; z < _dataSource.length; z++) {
                                let pcur = _dataSource[z];
                                if (pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data[0].rowNumber) {
                                    pcur.rowNumber -= 1;
                                }
                            }
                        }
                    }
                }
                
                if (_mDesc.rowElements) {
                    row = _mDesc.rowElements.splice(i, 1);
                    _.forEach(_mDesc.rowElements, (r, y) => {
                        if (y < i || !r) return;
                        $.data(r, lo.VIEW, parseInt($.data(r, lo.VIEW)) - 1);
                    });
                }
                
                if (_mDesc.rows) {
                    let tRow = _mDesc.rows.splice(i, 1);
                    if (_.has(_mDesc.colIdxes, "rowNumber")) {
                        no = _mDesc.colIdxes.rowNumber;
                        for (let z = 0; z < _mDesc.rows.length; z++) {
                            noc = _mDesc.rows[z];
                            if (noc && (noc = noc[no]) && parseInt(noc.innerHTML) > parseInt((tRow && tRow[0])[no].innerHTML)) {
                                noc.innerHTML = parseInt(noc.innerHTML) - 1;
                            }
                            
                            let pcur = _dataSource[z];
                            if (pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data[0].rowNumber) {
                                pcur.rowNumber -= 1;
                            }
                        }
                        
                        if (_mDesc.rows.length < _dataSource.length) {
                            for (let z = _mDesc.rows.length; z < _dataSource.length; z++) {
                                let pcur = _dataSource[z];
                                if (pcur && !_.isNil(pcur.rowNumber) && pcur.rowNumber > data[0].rowNumber) {
                                    pcur.rowNumber -= 1;
                                }
                            }
                        }
                    }
                }
                
                _.forEach(_bodyWrappers, (b, y) => {
                    if (_bodyWrappers.length > 1) {
                        ti.remove(y ? row && row[0] : fixedRow && fixedRow[0]);
                    } else {
                        ti.remove(row && row[0]);
                    }
                });
                
                if (_mafCurrent().rank) {
                    delete _mafCurrent().rank[data[0][_pk]];
                }
                
                let abols = [];
                _.forEach(_errors, (e, z) => {
                    if (e.index > i) {
                        e.index -= 1;
                    } else if (e.index === i) {
                        abols.push(z);
                    }
                });
                
                for (let z = abols.length - 1; z >= 0; z--) {
                    _errors.splice(abols[z], 1);
                }
                
                _.forEach(_histoire, h => {
                    abols = [];
                    _.forEach(h.o, (o, z) => {
                        if (o.coord.rowIdx > i) {
                            o.coord.rowIdx -= 1;
                        } else if (o.coord.rowIdx === i) {
                            abols.push(z);
                        }
                    });
                    
                    for (let z = abols.length - 1; z >= 0; z--) {
                        h.o.splice(abols[z], 1);
                    }
                });
                
                let t = {};
                _.forEach(_.keys(_selected), r => {
                    if (parseInt(r) > i) {
                        t[r] = _selected[r];
                        delete _selected[r];
                    } else if (parseInt(r) === i) {
                        delete _selected[r];
                    }
                });
                
                _.forEach(_.keys(t), k => {
                    _selected[parseInt(k) - 1] = t[k];
                });
                
                _.forEach(_.keys(v._voilerRows), pt => {
                    abols = [];
                    _.forEach(v._voilerRows[pt], (vr, z) => {
                        if (vr > i) {
                            v._voilerRows[pt][z]--;
                        } else if (vr === i) {
                            abols.push(z);
                        }
                    });
                    
                    for (let z = abols.length - 1; z >= 0; z--) {
                        v._voilerRows[pt].splice(abols[z], 1);
                    }
                });
                
                let delIn;
                for (let ri = 0; ri < _encarRows.length; ri++) {
                    if (_encarRows[ri] > i) _encarRows[ri] -= 1;
                    else if (_encarRows[ri] === i) delIn = ri;
                }
                
                if (!_.isNil(delIn)) _encarRows.splice(delIn, 1);
                _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let maf = _mafollicle[_currentPage][k];
                    if (k === _currentSheet || !maf || !maf.desc) return;
                    if (maf.desc.fixedRowElements) {
                        maf.desc.fixedRowElements.splice(i, 1);
                        _.forEach(maf.desc.fixedRowElements, (r, y) => {
                            if (y < i || !r) return;
                            $.data(r, lo.VIEW, parseInt($.data(r, lo.VIEW)) - 1);
                        });
                    }
                    if (maf.desc.fixedRows) {
                        maf.desc.fixedRows.splice(i, 1);
                    }
                    if (maf.desc.rowElements) {
                        row = maf.desc.rowElements.splice(i, 1);
                        _.forEach(maf.desc.rowElements, (r, y) => {
                            if (y < i || !r) return;
                            $.data(r, lo.VIEW, parseInt($.data(r, lo.VIEW)) - 1);
                        });
                    }
                    if (maf.desc.rows) {
                        maf.desc.rows.splice(i, 1);
                    }
                    
                    if (maf.$bBody) {
                        ti.remove(row && row[0]);
//                        let rList = maf.$bBody.querySelectorAll("tr");
//                        _.forEach(rList, (r, z) => {
//                            if (z < i + 1) return;
//                            $.data(r, lo.VIEW, parseInt($.data(r, lo.VIEW)) - 1);
//                        });
                    }
                    
                    abols = [];
                    _.forEach(maf.errors, (e, z) => {
                        if (e.index > i) {
                            e.index -= 1;
                        } else if (e.index === i) {
                            abols.push(z);
                        }
                    });
                    
                    for (let z = abols.length - 1; z >= 0; z--) {
                        maf.errors.splice(abols[z], 1);
                    }
                    
                    _.forEach(maf.histoire, h => {
                        abols = [];
                        _.forEach(h.o, (o, z) => {
                            if (o.coord.rowIdx > i) {
                                o.coord.rowIdx -= 1;
                            } else if (o.coord.rowIdx === i) {
                                abols.push(z);
                            }
                        });
                        
                        for (let z = abols.length - 1; z >= 0; z--) {
                            h.o.splice(abols[z], 1);
                        }
                    });
                    
                    t = {};
                    _.forEach(_.keys(maf.selected), r => {
                        if (parseInt(r) > i) {
                            t[r] = maf.selected[r];
                            delete maf.selected[r];
                        } else if (parseInt(r) === i) {
                            delete maf.selected[r];
                        }
                    });
                    
                    _.forEach(_.keys(t), k => {
                        maf.selected[parseInt(k) - 1] = t[k];
                    });
                });
            });
        }
        
        export enum COPY {
            NONE = 0,
            DATA = 1,
            DATA_AND_LOOK = 2
        }
    }
    
    module aho {
        export const TOP_SPACE = "top-space";
        export const BOTTOM_SPACE = "bottom-space";
        export const NULL = null;
        export let _bloc = 0;
        
        export class Platrer {
            $fixedContainer: HTMLElement;
            $container: HTMLElement;
            options: any;
            rowsOfBlock: number;
            blocksOfCluster: number;
            rowHeight: number;
            blockHeight: number;
            clusterHeight: number;
            primaryKey: string;
            topOffset: number;
            bottomOffset: number;
            currentCluster: number;
            startIndex: number;
            endIndex: number;
            hasSum: boolean;
            painter: v.ConcurrentPainter;
            sidePainter: v.SidePainter;
            
            constructor(containers: Array<any>, options: any) {
                if (containers && containers.length > 1) {
                    this.$fixedContainer = containers[0];
                    this.$container = containers[1];
                } else this.$container = containers[0];
                this.options = options;
                this.primaryKey = options.primaryKey;
                this.rowsOfBlock = options.noBlocRangee || 30;
                _bloc = this.rowsOfBlock;
                this.blocksOfCluster = options.noGrappeBloc || 3;
                this.rowHeight = parseInt(BODY_ROW_HEIGHT);
                this.blockHeight = this.rowsOfBlock * this.rowHeight;
                this.clusterHeight = this.blockHeight * this.blocksOfCluster;
                this.hasSum = options.hasSum;
                let ui = { 
                            primaryKey: this.primaryKey, 
                            states: options.states,
                            levelStruct: options.levelStruct
                        };
                this.painter = new v.ConcurrentPainter(ui);
                this.sidePainter = new v.SidePainter(ui);
                this.onScroll();
            }
            
            /**
             * Get cluster no.
             */
            getClusterNo(dir?: any) {
                let self = this;
                let count = 0, no = self.currentCluster || 0;
                if (dir > 0) no++;
                for (let i = 0; i < no; i++) {
                    let part = v._voilerRows[i];
                    if (part) count += part.length;
                }  
                
                return Math.max(Math.floor(this.$container.scrollTop / (this.clusterHeight - this.blockHeight)), 
                                Math.floor(Math.floor(this.$container.scrollTop / BODY_ROW_HEIGHT + count) / (_bloc * 2)));
            }
            
            /**
             * Render rows.
             */
            renderRows(manual?: boolean) {
                let self = this;
                let clusterNo = self.getClusterNo();
                if (manual) self.currentCluster = clusterNo;
                if (_dataSource.length < self.rowsOfBlock) {
                    self.topOffset = 0;
                    self.bottomOffset = 0;
                }
                
                let rowsOfCluster = self.blocksOfCluster * self.rowsOfBlock;
                let startRowIdx = _start = self.startIndex = Math.max((rowsOfCluster - self.rowsOfBlock) * clusterNo, 0);
                let p, count = 0, endRowIdx = self.endIndex = startRowIdx + rowsOfCluster;
                _end = endRowIdx - 1;
                for (let i = 0; i < clusterNo; i++) {
                    p = v._voilerRows[i]; 
                    if (p) count += p.length;
                }
                
                self.topOffset = Math.max(startRowIdx * self.rowHeight - count * BODY_ROW_HEIGHT, 0);
                self.bottomOffset = Math.max((_dataSource.length - endRowIdx) * self.rowHeight, 0);
                let rowConfig = { css: { height: self.rowHeight } };
                
                let containerElm = self.$container, containerTbl = containerElm.querySelector("table");
                let fixedTbody, tbody = document.createElement("tbody");
                if (_dataSource.length === 0) {
                    self.topOffset = 1;
                }
                
                let topSpace = v.extra(TOP_SPACE, self.topOffset);
                if (self.$fixedContainer) {
                    fixedTbody = document.createElement("tbody");
                    fixedTbody.appendChild(topSpace.cloneNode(true));
                }
                
                if (_dataSource.length === 0 && (ti.isIE() || ti.isEdge())) {
                    _.forEach(containerTbl.querySelectorAll("colgroup col"), cg => {
                        if (cg.style.display !== "none") {
                            topSpace.appendChild(_prtCell.cloneNode(true));
                        }
                    });
                }
                
                tbody.appendChild(topSpace);
                let res = {}, fixedRows = [], rows = [], fixedRowElements = [], rowElements = [], min, max;
                for (let i = startRowIdx; i < endRowIdx; i++) {
                    if (_.isNil(_dataSource[i])) continue;
                    let rElm, rDup;
                    
                    if (_mDesc && _mDesc.rowElements && (rElm = _mDesc.rowElements[i])) {
                        tbody.appendChild(rElm);
                        rowElements.push(rElm);
                        rows.push(_mDesc.rows[i]);
                        if (self.$fixedContainer) {
                            fixedTbody.appendChild(_mDesc.fixedRowElements[i]);
                            fixedRows.push(_mDesc.fixedRows[i]);
                            fixedRowElements.push(_mDesc.fixedRowElements[i]);
                        }
                        continue;
                    }
                    
                    if (_.isNil(min)) min = i;
                    max = Math.max(_.isNil(min) ? startRowIdx : min, i);
                    let rowElms = self.painter.row(_dataSource[i], rowConfig, i);
                    tbody.appendChild(rowElms.row);
                    rowElements.push(rowElms.row);
                    
                    if (self.$fixedContainer) {
                        if (_mDesc && _mDesc.fixedRowElements && (rElm = _mDesc.fixedRowElements[i])) {
                            fixedTbody.appendChild(rElm);
                            fixedRowElements.push(rElm);
                            fixedRows.push(_mDesc.fixedRows[i]);
                        } else {
                            let pass;
                            _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                                if (k === _currentSheet) return;
                                let ash = _mafCurrent()[k];
                                if (!_.isNil(ash) && !_.isNil(ash.desc) && ash.desc.hasOwnProperty("fixedRowElements") 
                                    && !_.isNil(rDup = ash.desc.fixedRowElements[i])) {
                                    pass = true;
                                    fixedTbody.appendChild(rDup);
                                    fixedRowElements.push(rDup);
                                    fixedRows.push(_.cloneDeep(ash.desc.fixedRows[i]));
                                    return false;
                                }
                            });
                            
                            if (!pass) {
                                fixedTbody.appendChild(rowElms.fixedRow);
                                fixedRowElements.push(rowElms.fixedRow);
                                fixedRows.push(rowElms.fixedElements);
                            }
                        }
                    }
                    
                    // Assure equilibrium
                    rows.push(rowElms.elements);
                    if (i === 0) {
                        res.fixedColIdxes = rowElms.fixedColIdxes;
                        res.colIdxes = rowElms.colIdxes;
                    }
                } 
                
                let bottomSpace = v.extra(BOTTOM_SPACE, self.hasSum ? self.bottomOffset + SUM_HEIGHT + 2 : self.bottomOffset);
                tbody.appendChild(bottomSpace);
                containerTbl.replaceChild(tbody, containerElm.getElementsByTagName("tbody")[0]);
                
                if (self.$fixedContainer) {
                    fixedTbody.appendChild(bottomSpace.cloneNode(true));
                    self.$fixedContainer.querySelector("table").replaceChild(fixedTbody, self.$fixedContainer.getElementsByTagName("tbody")[0]);
                }
                
                if (rows.length === 0) return;
                
                res.fixedRows = fixedRows;
                res.rows = rows;
                res.fixedRowElements = fixedRowElements;
                res.rowElements = rowElements;
                res.start = _start; //!_.isNil(min) ? min : startRowIdx;
                res.end = _end; //max;
                
                setTimeout(() => {
                    ssk.trigger(self.$container, ssk.RENDERED);
                }, 0);
                
                return res;
            }
            
            /**
             * OnScroll.
             */
            onScroll() {
                let self = this, lastScroll = self.$container.scrollTop;
                self.$container.removeXEventListener(ssk.SCROLL_EVT + ".detail");
                self.$container.addXEventListener(ssk.SCROLL_EVT + ".detail", function() {
                    let inClusterNo = self.getClusterNo(self.$container.scrollTop - lastScroll);
                    setTimeout(() => { lastScroll = self.$container.scrollTop; }, 1);
                    if (self.currentCluster !== inClusterNo) {
                        self.currentCluster = inClusterNo;
                        let res = self.renderRows();
                        let hCols;
                        if (!_.isNil(_hr) && (hCols = _mDesc.rows[_hr])) {
                            _.forEach(hCols, c => {    
                                if (c.classList.contains(color.HOVER)) {
                                    c.classList.remove(color.HOVER);
                                }
                            });
                        }
                        
                        if (!_.isNil(_hr) && (hCols = _mDesc.fixedRows[_hr])) {
                            _.forEach(hCols, c => {
                                if (c.classList.contains(color.HOVER)) {
                                    c.classList.remove(color.HOVER);
                                }
                            });
                        }
                        
                        if (!res) return;
                        let start = res.start, end = res.end, cursor;
                        for (let i = start; i <= end; i++) {
                            cursor = i - start;
                            if (!_mDesc.fixedRows[i]) {
                                _mDesc.fixedRows[i] = res.fixedRows[cursor];
                                _mDesc.fixedRowElements[i] = res.fixedRowElements[cursor];
                            }
                            
                            _mDesc.rows[i] = res.rows[cursor];
                            _mDesc.rowElements[i] = res.rowElements[cursor];
                        }
                    }
                });
            }
            
            /**
             * RenderSideRows.
             */
            renderSideRows(manual?: boolean, fails?: any) {
                let self = this;
                let clusterNo = self.getClusterNo();
                if (manual) self.currentCluster = clusterNo;
                if (_dataSource.length < self.rowsOfBlock) {
                    self.topOffset = 0;
                    self.bottomOffset = 0;
                }
                
                let rowsOfCluster = self.blocksOfCluster * self.rowsOfBlock;
                let startRowIdx = _start = self.startIndex = Math.max((rowsOfCluster - self.rowsOfBlock) * clusterNo, 0);
                let endRowIdx = self.endIndex = startRowIdx + rowsOfCluster;
                _end = endRowIdx - 1;
                self.topOffset = Math.max(startRowIdx * self.rowHeight, 0);
                self.bottomOffset = Math.max((_dataSource.length - endRowIdx) * self.rowHeight, 0);
                let rowConfig = { css: { height: self.rowHeight } };
                if (fails && fails.length > 0) {
                    self.sidePainter.fails = _.groupBy(fails, "index");
                }
                
                let containerElm = self.$container;
                let tbody = document.createElement("tbody");
                let topSpace = v.extra(TOP_SPACE, self.topOffset);
                
                tbody.appendChild(topSpace);
                let res = {}, rows = [], rowElements = [], min, max;
                for (let i = startRowIdx; i < endRowIdx; i++) {
                    if (_.isNil(_dataSource[i])) continue;
                    let rElm;
                    
                    if (_mDesc && _mDesc.rowElements && (rElm = _mDesc.rowElements[i])) {
                        tbody.appendChild(rElm);
                        continue;
                    }
                    
                    if (_.isNil(min)) min = i;
                    max = Math.max(_.isNil(min) ? startRowIdx : min, i);
                    let rowElms = self.sidePainter.row(_dataSource[i], rowConfig, i);
                    tbody.appendChild(rowElms.row);
                    rowElements.push(rowElms.row);
                    rows.push(rowElms.elements);
                    
                    if (i === _start) {
                        res.colIdxes = rowElms.colIdxes;
                    }
                } 
                
                let bottomSpace = v.extra(BOTTOM_SPACE, self.hasSum ? self.bottomOffset + SUM_HEIGHT + 2 : self.bottomOffset);
                tbody.appendChild(bottomSpace);
                containerElm.querySelector("table").replaceChild(tbody, containerElm.getElementsByTagName("tbody")[0]);
                
                if (rows.length === 0) return;
                
                res.rows = rows;
                res.rowElements = rowElements;
                res.start = !_.isNil(min) ? min : startRowIdx;
                res.end = max;
                
                setTimeout(() => {
                    ssk.trigger(self.$container, ssk.RENDERED);
                }, 0);
                
                return res;
            }
        }
    }
    
    module tc {
        export let SCROLL_SYNCING = "scroll-syncing";
        export let VERT_SCROLL_SYNCING = "vert-scroll-syncing";
        
        /**
         * Bind vertWheel.
         */
        export function bindVertWheel($container: HTMLElement, showY?: boolean, abnorm?: boolean) {
            let $_container = $($container);
            $container.addXEventListener(ssk.MOUSE_WHEEL, function(event: any) {
                let delta = event.deltaY;
                let direction = delta < 0 ? -1 : 1;
                let value = $_container.scrollTop();
//                $container.stop().animate({ scrollTop: value }, 10);
                let os = ti.isIE() ? 25 : 50;
                if (!abnorm && ((direction < 0 && value === 0)
                    || (direction > 0 && $container.scrollHeight - value + ti.getScrollWidth() === $_container.height()))) {
                    let $contents = document.getElementById("contents-area");
                    if ($contents) {
                        $contents.scrollTop += direction * os;
                    }
                }
                
                $_container.scrollTop(value + direction * os);
                event.preventDefault();
                event.stopImmediatePropagation();
                if (!_mEditor) return;
                
                if (_mEditor.type === dkn.COMBOBOX) {
                    let cbx = dkn.controlType[_mEditor.columnKey];
                    let $combo = cbx.my.querySelector("." + dkn.CBX_CLS);
                    if (cbx.dropdown && cbx.dropdown.style.top !== "-99999px") {
                        dkn.closeDD(cbx.dropdown);
                        $combo.classList.remove(dkn.CBX_ACTIVE_CLS);
                    }
                } else if (_mEditor.type === dkn.DATE_PICKER) {
                    su.endEdit(_$grid);
                }
                
            });
            
            if (!showY && $container.style.overflowY !== "hidden") {
                $container.style.overflowY = "hidden";
            }
        }
        
        /**
         * Unbind vertWheel.
         */
        export function unbindVertWheel($container: HTMLElement) {
            $container.removeXEventListener(ssk.MOUSE_WHEEL);
            $container.style.overflowY = "scroll";
        }
        
        /**
         * Sync scrolls.
         */
        export function syncDoubDirHorizontalScrolls(wrappers: Array<HTMLElement>) {
            _.forEach(wrappers, function($main: HTMLElement, index: number) {
                if (!$main) return;
                $main.addXEventListener(ssk.SCROLL_EVT, function() {
                    _.forEach(wrappers, function($depend: HTMLElement, i: number) {
                        if (i === index || !$depend) return;
                        let mainSyncing = $.data($main, SCROLL_SYNCING);
                        if (!mainSyncing) {
                            $.data($depend, SCROLL_SYNCING, true);
                            $depend.scrollLeft = $main.scrollLeft;
                        }
                    });
                    $.data($main, SCROLL_SYNCING, false);
                });
            });
        }
        
        /**
         * Sync scrolls.
         */
        export function syncDoubDirVerticalScrolls(wrappers: Array<HTMLElement>) {
            _.forEach(wrappers, function($main: HTMLElement, index: number) {
                    $main.addXEventListener(ssk.SCROLL_EVT, function(event: any) {
                        _.forEach(wrappers, function($depend: HTMLElement, i: number) {
                            if (i === index) return;
                            let mainSyncing = $.data($main, VERT_SCROLL_SYNCING);
                            if (!mainSyncing) {
                                $.data($depend, VERT_SCROLL_SYNCING, true);
                                $depend.scrollTop = $main.scrollTop;
                            }
                        });
                        $.data($main, VERT_SCROLL_SYNCING, false);
                    });
            });
        }
        
        /**
         * Sync scroll.
         */
        export function syncHorizontalScroll($headerWrap: HTMLElement, $bodyWrap: HTMLElement) {
            $bodyWrap.addXEventListener(ssk.SCROLL_EVT, function() {
                $headerWrap.scrollLeft = $bodyWrap.scrollLeft;
            });
        }
        
        /**
         * Sync scroll.
         */
        export function syncVerticalScroll($pivotBody: HTMLElement, bodyWraps: Array<HTMLElement>) {
            $pivotBody.addXEventListener(ssk.SCROLL_EVT, function() {
                _.forEach(bodyWraps, function(body: HTMLElement) {
                    body.scrollTop = $pivotBody.scrollTop;
                });
            });
        }
        
        /**
         * VisualJumpTo.
         */
        export function visualJumpTo($grid: HTMLElement, index: any) {
            if (index >= _cloud.startIndex && index < _cloud.endIndex) return;
            let tbl = $grid.querySelector("." + FREE + ":not(.mgrid-header)");
            tbl.scrollTop = index * BODY_ROW_HEIGHT;
            return true;
        }
    }
    
    module kt {
        export const AGENCY = "mgrid-agency";
        export const FIXED_LINE = "mgrid-fixed-line";
        export const LINE = "mgrid-line";
        export const RESIZE_COL = "resizeColumn";
        export const RESIZE_NO = "resizeNo";
        export const AREA_AGENCY = "mgrid-area-agency";
        export const RESIZE_AREA = "resize-area";
        export const AREA_LINE = "mgrid-area-line";
        export const STAY_CLS = "mgrid-stay";
        
        export let _adjuster;
        export let _fixedGroups = [];
        export let _widths = {};
        export let _columnWidths = {};
        
        export class ColumnAdjuster {
            headerWrappers: Array<HTMLElement>;
            bodyWrappers: Array<HTMLElement>;
            sumWrappers: Array<HTMLElement>;
            headerColGroup: Array<HTMLElemenet> = [];
            bodyColGroup: Array<HTMLElement> = [];
            sumColGroup: Array<HTMLElement> = [];
            widths: Array<string>;
            height: string;
            unshiftRight: any;
            dir: any
            
            $fixedAgency: HTMLElement;
            $agency: HTMLElement;
            fixedLines: Array<HTMLElement> = [];
            lines: Array<HTMLElement> = []; 
            $ownerDoc: HTMLElement;
            actionDetails: any;
            
            constructor(widths: Array<string>, height: any, sizeUi: any, unshift?: any) {
                this.headerWrappers = sizeUi.headerWrappers;
                this.bodyWrappers = sizeUi.bodyWrappers;
                this.sumWrappers = sizeUi.sumWrappers;
                this.unshiftRight = unshift;
                _.forEach(sizeUi.headerColGroup, g => {
                    if (g) {
                        let vCols = g.filter(c => c.style.display !== "none");
                        this.headerColGroup.push(vCols);
                    }
                });
                
                _.forEach(sizeUi.bodyColGroup, g => {
                    if (g) {
                        let vCols = g.filter(c => c.style.display !== "none");
                        this.bodyColGroup.push(vCols);
                    }
                });
                
                _.forEach(sizeUi.sumColGroup, g => {
                    if (g) {
                        let vCols = g.filter(c => c.style.display !== "none");
                        this.sumColGroup.push(vCols);
                    }
                });
                this.widths = widths;
                this.height = height;
                this.$ownerDoc = this.headerWrappers[0].ownerDocument;
                if (widths.length > 1) {
                    _widths._fixed = parseFloat(widths[0]);
                    _widths._unfixed = parseFloat(widths[1]); 
                } else _widths._unfixed = parseFloat(widths[0]);
            }
            
            /**
             * Nostal.
             */
            nostal(headerColGroup: any, bodyColGroup: any, sumColGroup: any, fixed?: any) {
                let i = _hasFixed && !fixed ? 1 : 0;
                this.headerColGroup[i] = headerColGroup.filter(c => c.style.display !== "none");
                this.bodyColGroup[i] = bodyColGroup.filter(c => c.style.display !== "none");
                if (sumColGroup) {
                    this.sumColGroup[i] = sumColGroup.filter(c => c.style.display !== "none");
                }
                this.widths = [ kt._widths._fixed, kt._widths._unfixed ];
                let agency;
                if (_hasFixed) {
                    agency = this.headerWrappers[0].querySelector("." + AGENCY);
                    if (agency) ti.remove(agency);
                }
                
                agency = this.headerWrappers[1].querySelector("." + AGENCY);
                if (agency) ti.remove(agency);
                this.fixedLines = [];
                this.lines = [];
            }
            
            /**
             * Handle.
             */
            handle() {
                let self = this;
                if (self.headerColGroup.length > 1) {
                    self.$fixedAgency = document.createElement("div"); 
                    self.$fixedAgency.className = AGENCY;
                    self.$fixedAgency.style.cssText = "; position: relative; width: " + self.widths[0];
                    let $fixedHeaderTable = self.headerWrappers[0].querySelector("table");
                    $fixedHeaderTable.insertAdjacentElement("beforebegin", self.$fixedAgency);
                    let left = 0, hiddenCount = 0;
                    
                    _.forEach(self.headerColGroup[0], ($targetCol, i) => {
                        if ($targetCol.style.display === "none") {
                            hiddenCount++;
                            return;
                        }
                        let $line = document.createElement("div");
                        $line.className = FIXED_LINE;
                        $.data($line, RESIZE_COL, $targetCol); 
                        $.data($line, RESIZE_NO, i - hiddenCount);
                        self.$fixedAgency.appendChild($line);
                        
                        left += (i === self.headerColGroup[0].length ? DISTANCE : 0) + parseFloat($targetCol.style.width);
                        $line.style.left = left + "px";
                        $line.style.height = self.height;
                        self.fixedLines.push($line);
                    });
                    
                    self.fixedHiddenCount = hiddenCount;
                    left = 0;
                    self.$agency = document.createElement("div");
                    self.$agency.className = AGENCY;
                    self.$agency.style.cssText = "; position: relative; width: " + self.widths[1];
                    let $headerTable = self.headerWrappers[1].querySelector("table");
                    $headerTable.insertAdjacentElement("beforebegin", self.$agency);
                    hiddenCount = 0;
                    _.forEach(self.headerColGroup[1], ($targetCol, i) => {
//                        if (i === self.headerColGroup[1].length - 1) return;
                        if ($targetCol.style.display === "none") {
                            hiddenCount++;
                            return;
                        }
                        let $line = document.createElement("div");
                        $line.className = LINE;
                        $.data($line, RESIZE_COL, $targetCol);
                        $.data($line, RESIZE_NO, i - hiddenCount);
                        self.$agency.appendChild($line);
                        left += parseFloat($targetCol.style.width);
                        $line.style.left = left + "px";
                        $line.style.height = self.height;
                        self.lines.push($line);
                    });
                    
                    self.hiddenCount = hiddenCount;
                    self.$fixedAgency.removeXEventListener(ssk.MOUSE_DOWN);
                    self.$fixedAgency.addXEventListener(ssk.MOUSE_DOWN, self.cursorDown.bind(self));  
                    self.$agency.removeXEventListener(ssk.MOUSE_DOWN);
                    self.$agency.addXEventListener(ssk.MOUSE_DOWN, self.cursorDown.bind(self));
                } else {
                    let left = 0, hiddenCount = 0;
                    self.$agency = document.createElement("div");
                    self.$agency.className = AGENCY;
                    self.$agency.style.cssText = "; position: relative; width: " + self.widths[0];
                    let $headerTable = self.headerWrappers[0].querySelector("table");
                    $headerTable.insertAdjacentElement("beforebegin", self.$agency);
                    _.forEach(self.headerColGroup[0], ($targetCol, i) => {
                        if (i === self.headerColGroup[0].length - 1) return;
                        if ($targetCol.style.display === "none") {
                            hiddenCount++;
                            return;
                        }
                        
                        let $line = document.createElement("div");
                        $line.className = LINE;
                        $.data($line, RESIZE_COL, $targetCol);
                        $.data($line, RESIZE_NO, i - hiddenCount);
                        self.$agency.appendChild($line);
                        left += parseFloat($targetCol.style.width);
                        $line.style.left = left + "px";
                        $line.style.height = self.height;
                        self.lines.push($line);
                    });
                    
                    self.hiddenCount = hiddenCount;  
                    self.$agency.removeXEventListener(ssk.MOUSE_DOWN);
                    self.$agency.addXEventListener(ssk.MOUSE_DOWN, self.cursorDown.bind(self));
                }
            }
            
            /**
             * Cursor down.
             */
            cursorDown(event: any, trg?: any) {
                let self = this;
                if (self.actionDetails) {
                    self.unshiftRight ? self.cursorUp(event) : self.cursorUpShift(event);
                }
                let $targetGrip = event.target;
                if (!selector.is($targetGrip, "." + LINE)
                    && !selector.is($targetGrip, "." + FIXED_LINE)) return;
                let gripIndex = $.data($targetGrip, RESIZE_NO);
                let $leftCol = $.data($targetGrip, RESIZE_COL);
                let headerGroup, isFixed = false;
                if ($targetGrip.classList.contains(FIXED_LINE)) {
                    headerGroup = self.headerColGroup[0];
                    isFixed = true;
                } else if (self.headerColGroup.length > 1) {
                    headerGroup = self.headerColGroup[1];
                } else {
                    headerGroup = self.headerColGroup[0];
                }
                
                let breakArea, wrapperLeft, wrapperRight, maxWrapperRight, leftAlign;
                if (isFixed && self.headerColGroup.length > 1 && gripIndex === self.headerColGroup[0].length - 1) {
                    breakArea = true;
                }
                
                if (self.headerWrappers.length > 1) {
                    wrapperLeft = self.headerWrappers[0].style.width;
                    wrapperRight = self.headerWrappers[1].style.width;
                    maxWrapperRight = self.headerWrappers[1].style.maxWidth;
                    leftAlign = self.headerWrappers[1].style.left;
                }   
                
                let $rightCol = headerGroup[gripIndex + 1];
                let leftWidth = $leftCol.style.width;
                let rightWidth;
                if ($rightCol) rightWidth = $rightCol.style.width;
                self.actionDetails = {
                    $targetGrip: $targetGrip,
                    gripIndex: gripIndex,
                    $leftCol: $leftCol,
                    $rightCol: $rightCol,
                    xCoord: getCursorX(event),
                    isFixed: isFixed,
                    breakArea: breakArea,
                    leftAlign: parseFloat(leftAlign),
                    widths: {
                        left : parseFloat(leftWidth),
                        right: rightWidth ? parseFloat(rightWidth) : undefined,
                        wrapperLeft: parseFloat(wrapperLeft),
                        wrapperRight: parseFloat(wrapperRight),
                        maxWrapperRight: parseFloat(maxWrapperRight)
                    },
                    changedWidths: {
                        left: parseFloat(leftWidth),
                        right: rightWidth ? parseFloat(rightWidth) : undefined
                    }
                };
                
                self.$ownerDoc.addXEventListener(ssk.MOUSE_MOVE, self.unshiftRight ? self.cursorMove.bind(self) : self.cursorMoveShift.bind(self));
                self.$ownerDoc.addXEventListener(ssk.MOUSE_UP, self.unshiftRight ? self.cursorUp.bind(self) : self.cursorUpShift.bind(self));
                if (!trg) {
                    event.preventDefault();
                    event.stopPropagation();
                }
            }
            
            /**
             * Cursor move shift.
             */
            cursorMoveShift(event: any) {
                let self = this;
                if (!self.actionDetails) return;
                let evt, distance = getCursorX(event) - self.actionDetails.xCoord;
                if (distance === 0) return;
                else if (distance > 0) {
                    if (_.isNil(self.dir)) {
                        self.dir = 1;   
                    } else if (self.dir === -1) {
                        evt = { target: self.actionDetails.$targetGrip };
                        self.cursorUpShift(event);
                        evt.pageX = event.pageX;
                        self.cursorDown(evt, true);
                    }
                } else if (_.isNil(self.dir)) {
                    self.dir = -1;
                } else if (self.dir === 1) {
                    evt = { target: self.actionDetails.$targetGrip };
                    self.cursorUpShift(event);
                    evt.pageX = event.pageX;
                    self.cursorDown(evt, true);
                }
                
                let leftWidth, leftAreaWidth, rightAreaWidth, leftAlign;
                leftWidth = self.actionDetails.widths.left + distance;
                
                if (leftWidth <= 20) return;
                if (self.actionDetails.breakArea || self.actionDetails.isFixed) {
                    leftAreaWidth = self.actionDetails.widths.wrapperLeft + distance;
                    _maxFixedWidth = leftAreaWidth;
                    rightAreaWidth = self.actionDetails.widths.wrapperRight - distance;
                    leftAlign = self.actionDetails.leftAlign + distance;
                    let $header = _$grid[0].querySelector("." + FREE + "." + HEADER);
                    let sWrap = _$grid[0].querySelector("." + gp.SHEET_CLS);
                    let pWrap = _$grid[0].querySelector("." + gp.PAGING_CLS);
                    let btmw = (Math.min(parseFloat($header.style.width), parseFloat($header.style.maxWidth)) 
                        + _maxFixedWidth + ti.getScrollWidth()) + "px";
                    if (sWrap) sWrap.style.width = btmw;
                    if (pWrap) pWrap.style.width = btmw;
                }
                
                self.actionDetails.changedWidths.left = leftWidth;
                let bodyGroup, sumGroup;
                if (self.actionDetails.isFixed) {
                    bodyGroup = self.bodyColGroup[0];
                    if (self.sumWrappers.length > 0) sumGroup = self.sumColGroup[0];
                } else { 
                    let i = self.bodyColGroup.length > 1 ? 1 : 0;
                    bodyGroup = self.bodyColGroup[i];
                    self.bodyWrappers[i].style.maxWidth = (self.actionDetails.widths.maxWrapperRight + distance + ti.getScrollWidth()) + "px";
                    self.headerWrappers[i].style.maxWidth = (self.actionDetails.widths.maxWrapperRight + distance) + "px";
                    if (self.sumWrappers.length > 0) {
                        sumGroup = self.sumColGroup[i];
                        self.sumWrappers[i].style.maxWidth = (self.actionDetails.widths.maxWrapperRight + distance) + "px";
                    }
                }
                
                if (self.actionDetails.$leftCol) {
                    self.setWidth(self.actionDetails.$leftCol, leftWidth);
                    let $contentLeftCol = bodyGroup[self.actionDetails.gripIndex];
                    self.setWidth($contentLeftCol, leftWidth);
                    if (self.sumWrappers.length > 0) {
                        let $sumLeftCol = sumGroup[self.actionDetails.gripIndex];
                        self.setWidth($sumLeftCol, leftWidth);
                    }
                    
                    if (leftAreaWidth) {
                        self.setWidth(self.headerWrappers[0], leftAreaWidth);
                        self.setWidth(self.bodyWrappers[0], leftAreaWidth);
                        if (self.sumWrappers.length > 0) self.setWidth(self.sumWrappers[0], leftAreaWidth);
                        _widths._fixed = leftAreaWidth;
                    }
                }
                
                if (rightAreaWidth) {
                    self.setWidth(self.headerWrappers[1], rightAreaWidth);
                    self.setWidth(self.bodyWrappers[1], rightAreaWidth + ti.getScrollWidth());
                    self.headerWrappers[1].style.left = leftAlign + "px";
                    self.bodyWrappers[1].style.left = leftAlign + "px";
                    if (self.sumWrappers.length > 0) {
                        self.setWidth(self.sumWrappers[1], rightAreaWidth);
                        self.sumWrappers[1].style.left = leftAlign + "px";
                    }
                    _widths._unfixed = rightAreaWidth;
                }
                
                let i = self.bodyWrappers.length > 1 ? 1 : 0;
                if (!self.actionDetails.isFixed && distance < 0) {
                    let width = parseFloat(self.bodyWrappers[i].style.width),
                        maxWidth = parseFloat(self.bodyWrappers[i].style.maxWidth);
                    
                    if (maxWidth < width) {
                        let pageDiv = _$grid[0].querySelector("." + gp.PAGING_CLS),
                            sheetDiv = _$grid[0].querySelector("." + gp.SHEET_CLS),
                            btw = _maxFixedWidth + maxWidth;
                        if (pageDiv) {
                            self.setWidth(pageDiv, btw);
                        }
                        if (sheetDiv) {
                            self.setWidth(sheetDiv, btw);
                        }
                        _widths._unfixed = maxWidth - ti.getScrollWidth();
                    }
                }
 
                if (_hasFixed && distance > 0 && !self.actionDetails.isFixed) {
                    let width = parseFloat(self.bodyWrappers[1].style.width),
                        maxWidth = parseFloat(self.bodyWrappers[1].style.maxWidth),
                        pageDiv = _$grid[0].querySelector("." + gp.PAGING_CLS),
                        sheetDiv = _$grid[0].querySelector("." + gp.SHEET_CLS),
                        ws = Math.min(maxWidth, width),
                        btw = _maxFixedWidth + ws;
                    if (pageDiv && parseFloat(pageDiv.style.width) !== btw) {
                        self.setWidth(pageDiv, btw);
                    }
                    if (sheetDiv && parseFloat(sheetDiv.style.width) !== btw) {
                        self.setWidth(sheetDiv, btw);
                    }
                    _widths._unfixed = ws;
                } 
            }
            
            /**
             * Cursor up shift.
             */
            cursorUpShift(event: any) {
                let self = this;
                self.$ownerDoc.removeXEventListener(ssk.MOUSE_MOVE);
                self.$ownerDoc.removeXEventListener(ssk.MOUSE_UP);
                self.syncLines();
                let leftCol, tidx = self.actionDetails.gripIndex;
                if (!_vessel() || !_vessel().desc) {
                    self.actionDetails = null;
                    return;
                }
                 
                if (self.actionDetails.isFixed) {
                    _.forEach(_fixedHiddenColumns, c => {
                        let idx = _vessel().desc.fixedColIdxes[c];
                        if (parseFloat(idx) <= self.actionDetails.gripIndex) {
                            tidx++;
                        }
                    });
                    
                    _.forEach(_.keys(_vessel().desc.fixedColIdxes), k => {
                        let i = parseFloat(_vessel().desc.fixedColIdxes[k]);
                        if (i === tidx) {
                            if (_.find(_fixedHiddenColumns, c => c === k)) {
                                tidx++;
                                return;
                            }
                            
                            leftCol = k;
                            if (self.actionDetails.breakArea || leftCol) return false;
                            return;
                        }
                    });
                    
                    replenLargeur(leftCol, self.actionDetails.changedWidths.left, "reparer");
                } else {
                    _.forEach(_hiddenColumns, c => {
                        let idx = _vessel().desc.colIdxes[c];
                        if (parseFloat(idx) <= self.actionDetails.gripIndex) {
                            tidx++;
                        }
                    });
                    
                    _.forEach(_.keys(_vessel().desc.colIdxes), k => {
                        let i = parseFloat(_vessel().desc.colIdxes[k]);
                        if (i === tidx) {
                            if (_.find(_hiddenColumns, c => c === k)) {
                                tidx++;
                                return;
                            }
                            
                            leftCol = k;
                            return false;
                        }
                    });
                    
                    replenLargeur(leftCol, self.actionDetails.changedWidths.left);
                }
                self.actionDetails = null;
                self.dir = null;
            }
            
            /**
             * Cursor move.
             */
            cursorMove(event: any) {
                let self = this;
                if (!self.actionDetails) return;
                let distance = getCursorX(event) - self.actionDetails.xCoord;
                if (distance === 0) return;
                let leftWidth, rightWidth, leftAreaWidth, rightAreaWidth, leftAlign;
                leftWidth = self.actionDetails.widths.left + distance;
                rightWidth = self.actionDetails.widths.right - distance;
                
                if (leftWidth <= 20 || rightWidth <= 20) return;
                if (self.actionDetails.breakArea) {
                    leftAreaWidth = self.actionDetails.widths.wrapperLeft + distance;
                    _maxFixedWidth = leftAreaWidth;
                    rightAreaWidth = self.actionDetails.widths.wrapperRight - distance;
                    leftAlign = self.actionDetails.leftAlign + distance;
                    let $header = _$grid[0].querySelector("." + FREE + "." + HEADER);
                    let sWrap = _$grid[0].querySelector("." + gp.SHEET_CLS);
                    let pWrap = _$grid[0].querySelector("." + gp.PAGING_CLS);
                    let btmw = (Math.min(parseFloat($header.style.width), parseFloat($header.style.maxWidth)) 
                        + _maxFixedWidth + ti.getScrollWidth()) + "px";
                    if (sWrap) sWrap.style.width = btmw;
                    if (pWrap) pWrap.style.width = btmw;
                }
                
                self.actionDetails.changedWidths.left = leftWidth;
                self.actionDetails.changedWidths.right = rightWidth;
                let bodyGroup, sumGroup;
                if (self.actionDetails.isFixed) {
                    bodyGroup = self.bodyColGroup[0];
                    if (self.sumWrappers.length > 0) sumGroup = self.sumColGroup[0];
                } else { 
                    bodyGroup = self.bodyColGroup[1];
                    if (self.sumWrappers.length > 0) sumGroup = self.sumColGroup[1];
                }
                
                if (self.actionDetails.$leftCol) {
                    self.setWidth(self.actionDetails.$leftCol, leftWidth);
                    let $contentLeftCol = bodyGroup[self.actionDetails.gripIndex];
                    self.setWidth($contentLeftCol, leftWidth);
                    if (self.sumWrappers.length > 0) {
                        let $sumLeftCol = sumGroup[self.actionDetails.gripIndex];
                        self.setWidth($sumLeftCol, leftWidth);
                    }
                    
                    if (leftAreaWidth) {
                        self.setWidth(self.headerWrappers[0], leftAreaWidth);
                        self.setWidth(self.bodyWrappers[0], leftAreaWidth);
                        if (self.sumWrappers.length > 0) self.setWidth(self.sumWrappers[0], leftAreaWidth);
                        _widths._fixed = leftAreaWidth;
                    }
                }
                
                if (self.actionDetails.$rightCol) {
                    self.setWidth(self.actionDetails.$rightCol, rightWidth);
                    let $contentRightCol = bodyGroup[self.actionDetails.gripIndex + 1];
                    self.setWidth($contentRightCol, rightWidth);
                    if (self.sumWrappers.length > 0) {
                        let $sumRightCol = sumGroup[self.actionDetails.gripIndex + 1];
                        self.setWidth($sumRightCol, rightWidth);
                    }
                }
                
                if (rightAreaWidth) {
                    self.setWidth(self.headerWrappers[1], rightAreaWidth);
                    self.setWidth(self.bodyWrappers[1], rightAreaWidth + ti.getScrollWidth());
                    self.headerWrappers[1].style.left = leftAlign + "px";
                    self.bodyWrappers[1].style.left = leftAlign + "px";
                    if (self.sumWrappers.length > 0) {
                        self.setWidth(self.sumWrappers[1], rightAreaWidth);
                        self.sumWrappers[1].style.left = leftAlign + "px";
                    }
                    _widths._unfixed = rightAreaWidth;
                }
            }
            
            /**
             * Cursor up.
             */
            cursorUp(event: any) {
                let self = this;
                self.$ownerDoc.removeXEventListener(ssk.MOUSE_MOVE);
                self.$ownerDoc.removeXEventListener(ssk.MOUSE_UP);
                self.syncLines();
                let leftCol, rightCol, tidx = self.actionDetails.gripIndex;
                if (!_vessel() || !_vessel().desc) {
                    self.actionDetails = null;
                    return;
                }
                 
                if (self.actionDetails.isFixed) {
                    _.forEach(_fixedHiddenColumns, c => {
                        let idx = _vessel().desc.fixedColIdxes[c];
                        if (parseFloat(idx) <= self.actionDetails.gripIndex) {
                            tidx++;
                        }
                    });
                    
                    _.forEach(_.keys(_vessel().desc.fixedColIdxes), k => {
                        let i = parseFloat(_vessel().desc.fixedColIdxes[k]);
                        if (i === tidx) {
                            leftCol = k;
                            if (self.actionDetails.breakArea || (leftCol && rightCol)) return false;
                            return;
                        }
                        
                        if (!self.actionDetails.breakArea && i === tidx + 1) {
                            rightCol = k;
                            if (leftCol && rightCol) return false;
                        }
                    });
                    
                    replenLargeur(leftCol, self.actionDetails.changedWidths.left, "reparer");
                    if (!self.actionDetails.breakArea) {
                        replenLargeur(rightCol, self.actionDetails.changedWidths.right, "reparer");
                    }
                } else {
                    _.forEach(_.keys(_vessel().desc.colIdxes), k => {
                        let i = parseFloat(_vessel().desc.colIdxes[k]);
                        if (i === self.actionDetails.gripIndex) {
                            leftCol = k;
                        } else if (i === self.actionDetails.gripIndex + 1) {
                            rightCol = k;
                        }
                        
                        if (leftCol && rightCol) return false;
                    });
                    
                    replenLargeur(leftCol, self.actionDetails.changedWidths.left);
                    if (rightCol) {
                        replenLargeur(rightCol, self.actionDetails.changedWidths.right);
                    }
                }
                self.actionDetails = null;
            }
            
            /**
             * Set width.
             */
            setWidth($col: HTMLElement, width: any) {
                $col.style.width = parseFloat(width) + "px";
            }
            
            /**
             * Sync lines.
             */
            syncLines() {
                let i, self = this; 
                if (self.actionDetails.isFixed) i = 0;
                else if (self.headerWrappers.length > 1) i = 1;
                else i = 0;
                self.$agency.style.width = self.headerWrappers[i].style.width;
                let left = 0, group = self.headerColGroup[i];
                _.forEach(group, function($td: HTMLElement, index: number) {
                    if ($td.style.display === "none" /*|| (!self.actionDetails.isFixed && index === group.length - 1)*/) return;
                    left += parseFloat($td.style.width);
                    if (index < self.actionDetails.gripIndex) return;
                    if (self.unshiftRight && index > self.actionDetails.gripIndex) return false;
                    let lineArr = self.actionDetails.isFixed ? self.fixedLines : self.lines;
                    let div = lineArr[index];
                    div.style.left = left + "px";
                });
            }
        }
        
        /**
         * Get cursorX.
         */
        function getCursorX(event: any) {
            return event.pageX;
        }
        
        /**
         * ReplenLargeur.
         */
        export function replenLargeur(column: any, width: any, sht?: any) {
            let storeKey = getStoreKey(), wdec = uk.localStorage.getItem(storeKey);
            if (!wdec.isPresent()) return;
            wdec = JSON.parse(wdec.get()); 
            if (!wdec) return;
            wdec[_.isNil(sht) ? _currentSheet : sht][column] = parseFloat(width);
            uk.localStorage.setItemAsJson(storeKey, wdec);
        }
        
        /**
         * TurfSurf.
         */
        export function turfSurf(cols: Array<any>, reparer: any, saving: any) {
            let newly, size, key = reparer ? "reparer" : _currentSheet, storeKey = getStoreKey(), 
                wdef = uk.localStorage.getItem(storeKey), reset = saving ? saving.reset : false;
            
            if (!wdef.isPresent()) {
                wdef = {};
                wdef[key] = {};
                _.forEach(cols, c => {
                    wdef[key][c.key] = parseFloat(c.width); 
                });
                
                uk.localStorage.setItemAsJson(storeKey, wdef);
                return;
            }
            
            let setLargeur = function(columns, data) {
                _.forEach(columns, c => {
                    if (c.group) {
                        setLargeur(c.group, data);
                        return;
                    }
                    
                    let largeur = data[c.key];
                    if (!_.isNil(largeur)) {
                        if (!reset) c.width = parseFloat(largeur) + "px";
                        else {
                            data[c.key] = parseFloat(c.width);
                            newly = true;
                        }
                    } else {
                        data[c.key] = parseFloat(c.width);
                        newly = true;
                    }
                });
            };
            
            let setNewLargeur = function(columns, data) {
                _.forEach(columns, c => {
                    if (c.group) {
                        setNewLargeur(c.group, data);
                    } else {
                        data[c.key] = parseFloat(c.width);
                    }
                });
            };
            
            wdef = JSON.parse(wdef.get());
            if ((size = wdef[key])) {
                setLargeur(cols, size);
                if (newly) {
                    uk.localStorage.setItemAsJson(storeKey, wdef);
                }
            } else {
                wdef[key] = {};
                setNewLargeur(cols, wdef[key]);
                uk.localStorage.setItemAsJson(storeKey, wdef);
            }
        }
        
        /**
         * ScreenLargeur.
         */
        export function screenLargeur(noRowsMin: any, noRowsMax: any) {
            if (!_headerWrappers || _headerWrappers.length === 0) return;
            let width, height = window.innerHeight - _remainHeight - parseFloat(_headerHeight), btmw;
            let pageDiv = _$grid[0].querySelector("." + gp.PAGING_CLS);
            let sheetDiv = _$grid[0].querySelector("." + gp.SHEET_CLS);
            if (_headerWrappers.length > 1) {
                width = window.innerWidth - _remainWidth - _maxFixedWidth;
                _flexFitWidth = Math.min(width + ti.getScrollWidth(), parseFloat(_bodyWrappers[1].style.maxWidth));
                btmw = _maxFixedWidth + _flexFitWidth + 2;
                _headerWrappers[1].style.width = width + "px";
                _bodyWrappers[1].style.width = (width + ti.getScrollWidth()) + "px";
                height -= ((pageDiv ? gp.PAGE_HEIGHT : 0) + (sheetDiv ? gp.SHEET_HEIGHT : 0));
                if (!_.isNil(noRowsMin) && !_.isNil(noRowsMax)) {
                    noRowsMin = parseFloat(noRowsMin);
                    noRowsMax = parseFloat(noRowsMax);
                    let size = _dataSource.length,
                        no = Math.min(Math.max(size, noRowsMin), noRowsMax);
                    height = no * BODY_ROW_HEIGHT + 19;
                }
                let vari = height - parseFloat(_bodyWrappers[0].style.height);
                if (_sumWrappers && _sumWrappers.length > 1) {
                    _sumWrappers[1].style.width = width + "px";
                    height += SUM_HEIGHT;
                    vari += SUM_HEIGHT;
                    if (height >= 0) {
                        _sumWrappers[0].style.top = (parseFloat(_sumWrappers[0].style.top) + vari) + "px";
                        _sumWrappers[1].style.top = (parseFloat(_sumWrappers[1].style.top) + vari) + "px";
                    }
                }
                if (pageDiv) {
                    pageDiv.style.width = btmw + "px";
                    if(height >= 0) {
                        pageDiv.style.top = (parseFloat(pageDiv.style.top) + vari) + "px";
                    }
                }
                if (sheetDiv) {
                    sheetDiv.style.width = btmw + "px";
                    if (height >= 0) {
                        sheetDiv.style.top = (parseFloat(sheetDiv.style.top) + vari) + "px";
                    }
                    
                    let sheetBtn = sheetDiv.querySelector(".mgrid-sheet-buttonlist");
                    let scrollbar = sheetDiv.querySelector(".mgrid-sheet-scrollbar");
                    if (sheetBtn.offsetHeight <= gp.SHEET_HEIGHT) {
                        scrollbar.classList.add("ui-state-disabled");
                    } else scrollbar.classList.remove("ui-state-disabled"); 
                }
                _bodyWrappers[0].style.height = height + "px";
                _bodyWrappers[1].style.height = height + "px";
                return;
            }
            
            width = window.innerWidth - _remainWidth;
            btmw = Math.min(width + ti.getScrollWidth(), parseFloat(_bodyWrappers[0].style.maxWidth));
            _flexFitWidth = btmw;
            _headerWrappers[0].style.width = width + "px";
            _bodyWrappers[0].style.width = (width + ti.getScrollWidth()) + "px";
            height -= ((pageDiv ? gp.PAGE_HEIGHT : 0) + (sheetDiv ? gp.SHEET_HEIGHT : 0));
            if (!_.isNil(noRowsMin) && !_.isNil(noRowsMax)) {
                noRowsMin = parseFloat(noRowsMin);
                noRowsMax = parseFloat(noRowsMax);
                let size = _dataSource.length,
                    no = Math.min(Math.max(size, noRowsMin), noRowsMax);
                height = no * BODY_ROW_HEIGHT + 19;
            }
            let vari = height - parseFloat(_bodyWrappers[0].style.height);
            if (_sumWrappers && _sumWrappers.length > 0) {
                _sumWrappers[0].style.width = width + "px";
                height += SUM_HEIGHT;
                vari += SUM_HEIGHT;
                _sumWrappers[0].style.top = (parseFloat(_sumWrappers[0].style.top) + vari) + "px";
            }
            if (pageDiv) {
                pageDiv.style.width = btmw + "px";
                pageDiv.style.top = (parseFloat(pageDiv.style.top) + vari) + "px";
            }
            if (sheetDiv) {
                sheetDiv.style.width = btmw + "px";
                sheetDiv.style.top = (parseFloat(sheetDiv.style.top) + vari) + "px";
            }
            _bodyWrappers[0].style.height = height + "px";
        }
        
        /**
         * Get storeKey.
         */
        function getStoreKey() {
            return request.location.current.rawUrl + "/" + _$grid.attr("id");
        }
    }
    
    module lo {
        export const MPART = "mPart";
        export const VIEW = "mView";
        export const LAST_SELECT = "mLastSelect";
        export const DESC = "mDescription";
        export const CBX_SELECTED = "selectedValue";
        export const CBX_SELECTED_TD = "code";
        export const CBX_ITEM_VALUE = "value";
        
        $.widget("md.mGrid", {
            options: {
                widthMem: null
            },
            _create: function() {    
                this.element[0].addXEventListener("falcon", evt => {
                    if (!evt.detail) {
                        extraireErrors();
                        return;
                    }
                    trier(evt.detail[0], evt.detail[1], evt.detail[2]);
                });
            },
            directEnter: function(direct) {
                this.element.data("enterDirect", direct);
            },
            dataSource: function(a) {
                let ds = _.cloneDeep(_dataSource);
                if (a) {
                    if (!ds) ds = [];
                    _.forEach(_.keys(_mafollicle), k => {
                        if (k === SheetDef || Number(k) === _currentPage || k === _currentPage) return;
                        _.forEach(_mafollicle[k].dataSource, s => {
                            ds.push(_.cloneDeep(s));
                        });
                    });
                }
                
                return ds;
            },
            insertions: function() {
                return _.cloneDeep(v._encarRows);
            },
            errors: function(a) {
                let res = _.cloneDeep(_errors);
                if (a) {
                    _.forEach(_.keys(_mafollicle), k => {
                        if (k === SheetDef) return;
                        _.forEach(_.keys(_mafollicle[SheetDef]), d => {
                            let f = _mafollicle[k][d];
                            if (!f || ((Number(k) === _currentPage || k === _currentPage) && d === _currentSheet)) return;
                            _.forEach(f.errors, e => {
                                res.push(_.cloneDeep(e));
                            });
                        });
                    });
                }
                
                return res;
            },
            disableNtsControlAt: function(id, key, $cell, hidden) {
                let dc = [];
                if (!$cell) {
                    let idx = _.findIndex(_dataSource, r => r[_pk] === id);
                    if (_.isNil(idx)) return;
                    _.forEach(_.keys(_mafollicle), k => {
                        if (k === SheetDef) return;
                        _.forEach(_.keys(_mafollicle[SheetDef]), d => {
                            let f = _mafollicle[k][d], c;
                            if (f) {
                                c = lch.cellAt(_$grid[0], idx, key, f.desc, hidden);
                                if (c) dc.push(c);
                            }
                        });
                        
                        if (dc.length > 0) return false;
                    });
                } else dc.push($cell);
                
                if (dc.length === 0) {
//                    if (_.find(_cstifle(), c => c.key === key)) {
                        color.pushState(id, key, color.Disable);
//                    }
                    return;
                }
                
                dc.forEach($cell => {
                    if ($cell.classList.contains(color.Disable)) return;
                    $cell.classList.add(color.Disable);
                    switch (dkn.controlType[key]) {
                        case dkn.LABEL:
                            $cell.innerHTML = "";
                            break;
                        case dkn.LINK_LABEL:
                            let link = $cell.querySelector(".mlink-button");
                            if (link) {
                                link.removeXEventListener(ssk.CLICK_EVT);
                                link.style.color = "#333";
                                link.style.cursor = "default";
                            }
                            break;
                        case dkn.BUTTON:
                        case dkn.DELETE_BUTTON:
                        case dkn.REFER_BUTTON:
                            let btn = $cell.querySelector(".mbutton");
                            if (btn) btn.disabled = true;
                            break;
                        case dkn.FLEX_IMAGE:
                        case dkn.IMAGE:
                            let img = $cell.querySelector("span");
                            if (img) {
                                img.removeXEventListener(ssk.CLICK_EVT);
                                img.style.cursor = "default";
                            }
                            break;
                        case dkn.CHECKBOX:
                            let check = $cell.querySelector("input");
                            if (check) {
                                check.setAttribute("disabled", "disabled");
                            }
                            break;
                    }
                });
                
                color.pushState(id, key, color.Disable);
            },
            enableNtsControlAt: function(id, key, $cell, hidden) {
                let dc = [];
                if (!$cell) {
                    let idx = _.findIndex(_dataSource, r => r[_pk] === id);
                    if (_.isNil(idx)) return;
                    _.forEach(_.keys(_mafollicle), k => {
                        if (k === SheetDef) return;
                        _.forEach(_.keys(_mafollicle[SheetDef]), d => {
                            let f = _mafollicle[k][d], c;
                            if (f) {
                                c = lch.cellAt(_$grid[0], idx, key, f.desc, hidden);
                                if (c) dc.push(c);
                            }
                        });
                        
                        if (dc.length > 0) return false;
                    });
                } else dc.push($cell);
                
                if (dc.length === 0) {
//                    if (_.find(_cstifle(), c => c.key === key)) {
                        color.popState(id, key, color.Disable);
//                    }    
                    return;
                }
                
                dc.forEach($cell => {
                    if (!$cell.classList.contains(color.Disable)) return;
                    $cell.classList.remove(color.Disable);
                    switch (dkn.controlType[key]) {
                        case dkn.LABEL:
                            let label = $.data($cell, v.DATA);
                            $cell.innerHTML = _.isNil(label) ? "" : label;
                            break;
                        case dkn.LINK_LABEL:
                            let link = $cell.querySelector(".mlink-button");
                            if (link) {
                                link.addXEventListener(ssk.CLICK_EVT, $.data(link, ssk.CLICK_EVT));
                                link.style.color = "#0066CC";
                                link.style.cursor = "pointer";
                            }
                            break;
                        case dkn.BUTTON:
                        case dkn.DELETE_BUTTON:
                        case dkn.REFER_BUTTON:
                            let btn = $cell.querySelector(".mbutton");
                            if (btn) {
                                btn.disabled = false;
                                let hdl = $.data(btn, ssk.CLICK_EVT);
                                if (hdl) {
                                    btn.removeXEventListener(ssk.CLICK_EVT);
                                    btn.addXEventListener(ssk.CLICK_EVT, hdl);
                                }
                            }
                            break;
                        case dkn.FLEX_IMAGE:
                        case dkn.IMAGE:
                            let img = $cell.querySelector("span");
                            if (img) {
                                img.addXEventListener(ssk.CLICK_EVT, $.data(img, ssk.CLICK_EVT));
                                img.style.cursor = "pointer";
                            }
                            break;
                        case dkn.CHECKBOX:
                            let check = $cell.querySelector("input");
                            if (check) {
                                check.removeAttribute("disabled");
                            }
                            break;
                    }
                });
                
                color.popState(id, key, color.Disable);
            },
            setState: function(id, key, states) {
                let self = this;
                let idx = _.findIndex(_dataSource, r => r[_pk] === id);
                if (_.isNil(idx)) return;
                let $cell = lch.cellAt(_$grid[0], idx, key);
                let ftPrint = false, cloneStates = _.cloneDeep(states),
                    setShtCellState = function($c) {
                    let disabled;
                    if (states && states.length !== cloneStates.length) {
                        states = _.cloneDeep(cloneStates);
                    }
                        
                    _.forEach(states, s => {
                        if (s === color.Disable) {
                            self.disableNtsControlAt(id, key, $c);
                            disabled = true;
                            return;
                        } else if (s === color.ManualEditTarget || s === color.ManualEditOther) {
                            $.data($c, v.INIT_MAN_EDIT, s);
                        }
                        
                        if (!$c.classList.contains(s))
                            $c.classList.add(s);
                    });
                    
                    if (disabled) _.remove(states, s => s === color.Disable);
                    if (!ftPrint) {
                        color.pushState(id, key, states);
                        ftPrint = true;   
                    }   
                };
                
                if ($cell) {
                    setShtCellState($cell);
                }
                _.forEach(_.keys(_mafollicle[SheetDef]), s => {
                    if (s === _currentSheet) return;
                    let tidx, maf = _mafollicle[_currentPage][s];
                    if (maf && maf.desc && maf.desc.fixedColIdxes 
                        && !_.isNil(tidx = maf.desc.fixedColIdxes[key])) {
                        $cell = maf.desc.fixedRows[idx][tidx];
                        if ($cell) setShtCellState($cell);
                    } else if (maf && maf.desc && maf.desc.colIdxes
                        && !_.isNil(tidx = maf.desc.colIdxes[key])) {
                        $cell = maf.desc.rows[idx][tidx];
                        if ($cell) setShtCellState($cell);
                    } 
                });
                
                if (!ftPrint) color.pushState(id, key, states); 
            },
            clearState: function(idArr: Array<any>) {
                let self = this;
                
                let cleanOthShtCellElm = function(id, c, states) {
                    if (!c) return;
                    let coord = ti.getCellCoord(c);
                    if (!states) states = color.ALL;
                    states.forEach(s => {
                        if (c.classList.contains(s)) {
                            if (s === color.Disable) {
                                self.enableNtsControlAt(id, coord.columnKey, c);
                            }
                            c.classList.remove(s);    
                        }
                    });
                    
                    color.popState(id, coord.columnKey, states); 
                };
                
                let clean = function(id, key, states) {
                    let idx = _.findIndex(_dataSource, r => r[_pk] === id); 
                    if (!_.isNil(key)) {
                        let c = lch.cellAt(_$grid[0], idx, key);
                        cleanOthShtCellElm(id, c, states);
                    } else {
                        let row = lch.rowAt(_$grid[0], idx);
                        _.forEach(row, c => {
                            cleanOthShtCellElm(id, c);
                        });
                    }
                    
                    _.forEach(_.keys(_mafollicle[SheetDef]), s => {
                        if (s === _currentSheet) return;
                        let maf = _mafollicle[_currentPage][s];
                        if (maf && maf.desc) {
                            if (!_.isNil(key)) {
                                let c = lch.cellAt(_$grid[0], idx, key, maf.desc);
                                cleanOthShtCellElm(id, c, states);
                            } else {
                                let othShtRow = lch.rowAt(_$grid[0], idx, maf.desc); 
                                _.forEach(othShtRow, c => {
                                    cleanOthShtCellElm(id, c);
                                }); 
                            }
                        } else cleanOthSht(id, _mafollicle[SheetDef][s].columns, states);
                    });
                };
                
                let cleanOthSht = function(id, cols, states) {
                    if (!states) {
                        states = color.ALL;
                    }
                    
                    _.forEach(cols, c => {
                        if (c.group) {
                            cleanOthSht(id, c.group, states);
                            return;
                        }
                        color.popState(id, c.key, states);
                    });
                };
                
                if (arguments.length > 1) {
                    clean(idArr, arguments[1], arguments[2]);
                } else {
                    if (idArr && !_.isArray(idArr)) {
                        clean(idArr);
                        return;
                    }
                    
                    _.forEach(idArr, id => {
                        clean(id);
                    });
                }
            },
            hideZero: function(val) {
                if (changeZero(val)) {
                    _zeroHidden = val;
                    if (_vessel()) _vessel().zeroHidden = val;
                }
            },
            updatedCells: function(a) {
                let arr = [];
                let toNumber = false, column = _columnsMap[_pk];
                if ((column && _.toLower(column[0].dataType) === "number")
                    || _.toLower(_pkType) === "number") {
                    toNumber = true;
                }
                
                _.forEach(Object.keys(_dirties), r => {
                    _.forEach(Object.keys(_dirties[r]), c => {
                        arr.push({ rowId: (toNumber ? parseFloat(r) : r), columnKey: c, value: _dirties[r][c] });
                    });
                });
                
                if (a) {
                    _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                        if (k === _currentSheet) return;
                        let maf = _mafollicle[_currentPage][k];
                        if (!maf || !maf.dirties) return;
                        _.forEach(_.keys(maf.dirties), r => {
                            _.forEach(_.keys(maf.dirties[r]), c => {
                                arr.push({ rowId: (toNumber ? parseFloat(r) : r), columnKey: c, value: maf.dirties[r][c] });
                            });
                        });
                    });    
                }
                
                return arr;
            },
            showColumn: function(col, ufx) {
                if (!_vessel() || !_vessel().desc) return;
                let $col, i = !ufx ? _vessel().desc.fixedColIdxes[col] : _vessel().desc.colIdxes[col];
                if (_.isNil(i)) return;
                let hCols, bCols, sCols, header = _$grid[0].querySelector("." + (!ufx ? FIXED : FREE) + "." + HEADER);
                if (header) {
                    hCols = header.querySelectorAll("col");
                    $col = hCols[i];
                    if ($col && $col.style.display === "none") {
                        $col.style.display = "";
                    } else return;
                    
                    let headerCols = header.querySelectorAll("td");
                    $col = headerCols[i];
                    if ($col && $col.style.display === "none") {
                        $col.style.display = "";
                    }
                    _.remove(!ufx ? _fixedHiddenColumns : _hiddenColumns, c => c === col);
                }
                
                let body = _$grid[0].querySelector("." + (!ufx ? FIXED : FREE) + ":not(." + HEADER + ")");
                if (body) {
                    bCols = body.querySelectorAll("col");
                    $col = bCols[i];
                    if ($col && $col.style.display === "none") {
                        $col.style.display = "";
                    }
                    _.forEach(!ufx ? _vessel().desc.fixedRows : _vessel().desc.rows, r => {
                        if (!r) return;
                        let a = r[i];
                        if (a && a.style.display === "none") {
                            a.style.display = "";
                        }
                    });
                    
                    let colWidth = parseFloat($col.style.width);
                    if (!ufx) {
                        _maxFixedWidth += colWidth;
                        kt._widths._fixed = _maxFixedWidth;
                        _.forEach(_.slice(_$grid[0].querySelectorAll("." + FREE)), t => {
                            if (!t) return;
                            let width = parseFloat(t.style.width),
                                left = parseFloat(t.style.left);
                            t.style.width = (width - colWidth) + "px";
                            t.style.left = (left + colWidth) + "px";
                        });
                        
                        let sum = _$grid[0].querySelector("." + FIXED + "-summaries");
                        if (sum) {
                            sCols = sum.querySelectorAll("col");
                            $col = sCols[i];
                            if ($col && $col.style.display === "none") {
                                $col.style.display = "";
                            }
                            
                            let cols = sum.querySelectorAll("td");
                            $col = cols[i];
                            if ($col && $col.style.display === "none") {
                                $col.style.display = "";
                            }
                            let dSum = _$grid[0].querySelector("." + FREE + "-summaries");
                            if (dSum) {
                                let width = parseFloat(dSum.style.width),
                                    left = parseFloat(dSum.style.left);
                                dSum.style.width = (width - colWidth) + "px";
                                dSum.style.left = (left + colWidth) + "px";
                            }
                            sum.style.width = _maxFixedWidth + "px";
                        }
                        header.style.width = _maxFixedWidth + "px";
                        body.style.width = _maxFixedWidth + "px";
                    } else {
                        _maxFreeWidth += colWidth;
                        kt._widths._unfixed = _maxFreeWidth;
                        _.forEach(_.slice(_$grid[0].querySelectorAll("." + FREE)), t => {
                            if (!t) return;
                            let width = parseFloat(t.style.maxWidth);
                            t.style.maxWidth = (width + colWidth) + "px";
                        });
                        let sum = _$grid[0].querySelector("." + FREE + "-summaries");
                        if (sum) {
                            sCols = sum.querySelectorAll("col");
                            $col = sCols[i];
                            if ($col && $col.style.display === "none") {
                                $col.style.display = "";
                            }
                            
                            let cols = sum.querySelectorAll("td");
                            $col = cols[i];
                            if ($col && $col.style.display === "none") {
                                $col.style.display = "";
                            }
                            let width = parseFloat(sum.style.maxWidth);
                            sum.style.maxWidth = (width + colWidth) + "px";
                        }
                    }
                    
                    if (kt._adjuster) {
                        kt._adjuster.nostal(_.slice(hCols), _.slice(bCols), _.slice(sCols), !ufx ? true : false);
                        kt._adjuster.handle();
                    }
                    _cloud.painter.painters[!_hasFixed || !ufx ? 0 : 1].bubColumn(col, i);
                }
            },
            hideColumn: function(col, ufx) {
                if (!_vessel() || !_vessel().desc) return;
                let $col, i = !ufx ? _vessel().desc.fixedColIdxes[col] : _vessel().desc.colIdxes[col];
                if (_.isNil(i)) return;
                let hCols, bCols, sCols, header = _$grid[0].querySelector("." + (!ufx ? FIXED : FREE) + "." + HEADER);
                if (header) {
                    hCols = header.querySelectorAll("col");
                    $col = hCols[i];
                    if ($col && $col.style.display !== "none") {
                        $col.style.display = "none";
                    } else return;
                    
                    let headerCols = header.querySelectorAll("td");
                    $col = headerCols[i];
                    if ($col && $col.style.display !== "none") {
                        $col.style.display = "none";
                    }
                    !ufx ? _fixedHiddenColumns.add(col) : _hiddenColumns.add(col);
                }
                
                let body = _$grid[0].querySelector("." + (!ufx ? FIXED : FREE) + ":not(." + HEADER + ")");
                if (body) {
                    bCols = body.querySelectorAll("col");
                    $col = bCols[i];
                    if ($col && $col.style.display !== "none") {
                        $col.style.display = "none";
                    }
                    _.forEach(!ufx ? _vessel().desc.fixedRows : _vessel().desc.rows, (r, y) => {
                        if (!r) return;
                        let a = r[i];
                        if (a && a.style.display !== "none") {
                            a.style.display = "none";
                            if (a.classList.contains(lch.CELL_SELECTED_CLS)) {
                                a.classList.remove(lch.CELL_SELECTED_CLS);
                                _.remove(_selected && _selected[y], cc => cc === col);
                            }
                        }
                    });
                    
                    let colWidth = parseFloat($col.style.width);
                    if (!ufx) {
                        _maxFixedWidth -= colWidth;
                        kt._widths._fixed = _maxFixedWidth;
                        _.forEach(_.slice(_$grid[0].querySelectorAll("." + FREE)), t => {
                            if (!t) return;
                            let width = parseFloat(t.style.width),
                                left = parseFloat(t.style.left);
                            t.style.width = (width + colWidth) + "px";
                            t.style.left = (left - colWidth) + "px";
                        });
                        
                        let sum = _$grid[0].querySelector("." + FIXED + "-summaries");
                        if (sum) {
                            sCols = sum.querySelectorAll("col");
                            $col = sCols[i];
                            if ($col && $col.style.display !== "none") {
                                $col.style.display = "none";
                            }
                            
                            let cols = sum.querySelectorAll("td");
                            $col = cols[i];
                            if ($col && $col.style.display !== "none") {
                                $col.style.display = "none";
                            }
                            let dSum = _$grid[0].querySelector("." + FREE + "-summaries");
                            if (dSum) {
                                let width = parseFloat(dSum.style.width),
                                    left = parseFloat(dSum.style.left);
                                dSum.style.width = (width + colWidth) + "px";
                                dSum.style.left = (left - colWidth) + "px";
                            }
                            sum.style.width = _maxFixedWidth + "px";
                        }
                        header.style.width = _maxFixedWidth + "px";
                        body.style.width = _maxFixedWidth + "px";
                    } else {
                        _maxFreeWidth -= colWidth;
                        kt._widths._unfixed = _maxFreeWidth;
                        _.forEach(_.slice(_$grid[0].querySelectorAll("." + FREE)), t => {
                            if (!t) return;
                            let width = parseFloat(t.style.maxWidth);
                            t.style.maxWidth = (width - colWidth) + "px";
                        });
                        
                        let sum = _$grid[0].querySelector("." + FREE + "-summaries");
                        if (sum) {
                            sCols = sum.querySelectorAll("col");
                            $col = sCols[i];
                            if ($col && $col.style.display !== "none") {
                                $col.style.display = "none";
                            }
                            
                            let cols = sum.querySelectorAll("td");
                            $col = cols[i];
                            if ($col && $col.style.display !== "none") {
                                $col.style.display = "none";
                            }
                            let width = parseFloat(sum.style.maxWidth);
                            sum.style.maxWidth = (width - colWidth) + "px";
                        }
                    }
                    
                    if (kt._adjuster) {
                        kt._adjuster.nostal(_.slice(hCols), _.slice(bCols), _.slice(sCols), !ufx ? true : false);
                        kt._adjuster.handle();
                    }
                    _cloud.painter.painters[!_hasFixed || !ufx ? 0 : 1].unbubColumn(col, i);
                }
            },
            hideRow: function(idx) {
                v.voilerRow(idx);    
            },
            updateCell: function(id, key, val, reset, ackDis, ls) {
                let idx = _.findIndex(_dataSource, r => r[_pk] === id);
                if (idx === -1 || _.isNil(idx)) return;
                let $cell = lch.cellAt(_$grid[0], idx, key);
                if (_.isNil($cell)) {
                    if (dkn.controlType[key] === dkn.TEXTBOX) {
                        let col = _columnsMap[key];
                        if (!col || col.length === 0) return;
                        su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, su.formatSave(col[0], val), reset);
                    } else su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, val, reset);
                    return idx;
                }
                if ((!ackDis && $cell.classList.contains(color.Disable)) || $cell.classList.contains(color.Lock)) return idx;
                if (dkn.controlType[key] === dkn.TEXTBOX) {
                    let col = _columnsMap[key];
                    if (!col || col.length === 0) return;
                    let formatted = su.format(col[0], val);
                    $cell.innerHTML = _.isNil(formatted) ? "" : formatted;
                    let disFormat = su.formatSave(col[0], val);
                    su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, disFormat, reset);
                    $.data($cell, v.DATA, disFormat);
                    if (_zeroHidden && ti.isZero(disFormat, key)) {
                        $cell.innerHTML = "";
                    }
                } else if (dkn.controlType[key] === dkn.CHECKBOX) {
                    let check = $cell.querySelector("input[type='checkbox']");
                    if (!check) return;
                    if (val) { //&& check.getAttribute("checked") !== "checked") {
                        check.setAttribute("checked", "checked");
                        check.checked = true;
                        let evt = document.createEvent("HTMLEvents");
                        evt.initEvent("change", false, true);
                        evt.resetValue = reset;
                        evt.checked = val;
                        check.dispatchEvent(evt);
                    } else if (!val) { // && check.getAttribute("checked") === "checked") {
                        check.removeAttribute("checked");
                        check.checked = false;
                        let evt = document.createEvent("HTMLEvents");
                        evt.initEvent("change", false, true);
                        evt.resetValue = reset;
                        evt.checked = val;
                        check.dispatchEvent(evt);
                    }
                } else if (dkn.controlType[key] === dkn.LINK_LABEL) {
                    let link = $cell.querySelector("a");
                    link.innerHTML = val;
                    su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, val, reset);
                } else if (dkn.controlType[key] === dkn.FLEX_IMAGE) {
                    let $image;
                    if (!_.isNil(val) && val !== "") {
                        let controlDef, controlMap = _mafollicle[SheetDef][_currentSheet].controlMap;
                        if (!controlMap || !(controlDef = controlMap[key])) return;
                        $image = document.createElement("span");
                        $image.className = controlDef.source;
                        if (controlDef.click && _.isFunction(controlDef.click)) {
                            let clickHandle = controlDef.click.bind(null, key, id);
                            $image.addXEventListener(ssk.CLICK_EVT, clickHandle);
                            $image.style.cursor = "pointer";
                            $.data($image, ssk.CLICK_EVT, clickHandle);
                        }
                        
                        $cell.innerHTML = "";
                        $cell.appendChild($image);
                    } else {
                        $image = $cell.querySelector("span");
                        if ($image) ti.remove($image);
                    }
                    su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, val, reset);
                } else if (dkn.controlType[key] === dkn.REFER_BUTTON) {
                    let content = val, txt = $cell.querySelector(".mgrid-refer-text");
                    let controlDef, controlMap = _mafollicle[SheetDef][_currentSheet].controlMap;
                    if (!controlMap || !(controlDef = controlMap[key])) return;
                    if (ls) {
                        (controlDef.pattern || {})[(controlDef.list || {})[id]] = val;
                    } else if (txt) {
                        if (controlDef.pattern && controlDef.list) {
                            let itemList = controlDef.pattern[controlDef.list[id]];
                            if (!itemList) itemList = controlDef.pattern[controlDef.list["null"]];
                            let item = _.find(itemList, i => i[controlDef.optionsValue || "code"] === val);
                            if (item) content = item[controlDef.optionsText || "name"];
                        }
                        
                        txt.innerHTML = _.isNil(content) ? "" : content;
                        su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, val, reset);
                        $.data($cell, v.DATA, val);
                    }
                } else if (dkn.controlType[key] === dkn.LABEL) {
                    $cell.innerHTML = val;
                    su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, val, reset);
                    $.data($cell, v.DATA, val);
                } else {
                    let cbx = dkn.controlType[key];
                    if (!_.isObject(cbx)) return;
                    if (cbx.type === dkn.COMBOBOX) {
                        if (ls) {
                            let panelz, listType, maxHeight = 0, itemList = [], $itemHolder = document.createElement("ul"), controlDef, controlMap = _mafollicle[SheetDef][_currentSheet].controlMap;
                            $itemHolder.classList.add("mcombo-listitemholder");
                            if (!controlMap || !(controlDef = controlMap[key])) return;
                            if (cbx.optionsMap && !_.isNil(listType = cbx.optionsMap[id])) {
                                panelz = listType + 1;
                                cbx.optionsList[listType] = _.cloneDeep(val);
                            } else {
                                panelz = 0;
                                cbx.options = _.cloneDeep(val);
                            }
                            
                            let found, currentVal = (_dataSource[idx] || {})[key];
                            _.forEach(val, i => {
                                let $item = document.createElement("li");
                                $item.classList.add("mcombo-listitem");
                                $item.classList.add("ui-state-default");
                                let vali = i[controlDef.optionsValue];
                                $.data($item, "value", vali);
                                if (currentVal === vali) found = i;
                                let $comboItem = dkn.createItem(vali, i[controlDef.optionsText], $item, controlDef.displayMode),
                                    $comboValue = cbx.my.querySelector(".mcombo-value");
                                
                                $item.addXEventListener(ssk.CLICK_EVT, evt => {
                                    let $combo = cbx.my.querySelector("." + dkn.CBX_CLS);
                                    $comboValue.innerHTML = "";
                                    $comboValue.appendChild($comboItem.cloneNode(true));
                                    _.forEach(itemList, i => {
                                        if (i.classList.contains("selecteditem")) {
                                            i.classList.remove("selecteditem");
                                        } 
                                    });
                                    
                                    let value = $.data($item, lo.CBX_ITEM_VALUE);
                                    $.data(cbx.my, lo.CBX_SELECTED, value); 
                                    $item.classList.add("selecteditem");
                                    let $cbxCell = ti.closest(cbx.my, "." + v.CELL_CLS);
                                    if ($cbxCell) {
                                        let bVal = $.data($cbxCell, lo.CBX_SELECTED_TD);
                                        if (bVal !== value && _.isFunction(controlDef.inputProcess)) {
                                            controlDef.inputProcess(value, id, _dataSource[idx]);
                                        }
                                        $.data($cbxCell, lo.CBX_SELECTED_TD, value);
                                    }
                                    dkn.closeDD(cbx.dropdown);
                                    $combo.classList.remove(dkn.CBX_ACTIVE_CLS);
                                    let coord = ti.getCellCoord($cbxCell);
                                    su.wedgeCell(_$grid[0], { rowIdx: coord.rowIdx, columnKey: key }, value);
                                    khl.clear({ id: _dataSource[coord.rowIdx][_pk], columnKey: coord.columnKey, element: $cbxCell });
                                    let sCol = _specialColumn[key];
                                    if (sCol) {
                                        let $cCell = lch.cellAt(_$grid[0], coord.rowIdx, sCol);
                                        if ($cCell) {
                                            let column = _columnsMap[sCol];
                                            let formatted = su.format(column[0], value); 
                                            $cCell.textContent = formatted;
                                            su.wedgeCell(_$grid[0], { rowIdx: coord.rowIdx, columnKey: sCol }, value);
                                            $.data($cCell, v.DATA, value);
                                            khl.clear({ id: _dataSource[coord.rowIdx][_pk], columnKey: sCol, element: $cCell });
                                        }
                                    }
                                });
                                
                                $itemHolder.appendChild($item);
                                itemList.add($item);
                                maxHeight += 26;
                            });
                            
                            if (!val || val.length === 0) {
                                $itemHolder.innerHTML = nts.uk.resource.getMessage("Msg_37");
                                maxHeight += 26;
                            }
                            
                            let panel = cbx.panel[panelz];
                            cbx.panel[panelz] = $itemHolder;
                            cbx.maxHeight[panelz] = Math.min(104, maxHeight);
                            if (!found) {
                                $cell.textContent = "";
                            } else if ($cell.textContent === "") {
                                $cell.textContent = found[controlDef.optionsText];
                            }
                            
                            // Reload combo list
                            if (_mEditor && _mEditor.type === dkn.COMBOBOX && _mEditor.columnKey === key && _mEditor.rowIdx === idx) {
                                let comboList = cbx.dropdown.querySelector(".mcombo-list");
                                comboList.replaceChild($itemHolder, panel);
                                cbx.dropdown.style.maxHeight = cbx.maxHeight[panelz] + "px";
                                let items = cbx.dropdown.querySelectorAll(".mcombo-listitem");
                                let $comboValue = cbx.my.querySelector(".mcombo-value");
                                let selected, code = $.data($cell, lo.CBX_SELECTED_TD);
                                _.forEach(items, i => {
                                    let value = $.data(i, lo.CBX_ITEM_VALUE);
                                    
                                    if (i.classList.contains("selecteditem")) {
                                        i.classList.remove("selecteditem");
                                    }
                                    
                                    if (code === value) {
                                        let $item = i.querySelector(".mcombo-item");
                                        if ($item) {
                                            $comboValue.innerHTML = "";
                                            $comboValue.appendChild($item.cloneNode(true));
                                            i.classList.add("selecteditem");
                                            selected = true;
                                        }
                                    }
                                });
                                
                                if (!selected) {
                                    $comboValue.innerHTML = "";
                                    let empty = _prtDiv.cloneNode(true);
                                    empty.style.display = "inline-block";
                                    $comboValue.appendChild(empty);
                                }
                                
                                dkn.openDD(cbx.dropdown, cbx.my);
                            }
                        } else {
                            let y, options;
                            if (cbx.optionsMap && !_.isNil(y = cbx.optionsMap[id])) {
                                options = cbx.optionsList[y];
                            } else options = cbx.options;
                            
                            let sel = _.find(options, o => o[cbx.optionsValue] === val);
                            if (_.isNil(val)) val = null;
                            su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, val, reset);
                            $.data($cell, lo.CBX_SELECTED_TD, val);
                            $cell.textContent = sel ? sel[cbx.optionsText] : "";
                        }
                    } else if (cbx.type === dkn.DATE_PICKER) {
                        let txt, mDate = moment.utc(val, cbx.format, true);
                        if (cbx.formatType !== "ymd") txt = mDate.format(cbx.format[0]);
                        let date = _.isNil(txt) ? (mDate.isValid() ? mDate.toDate() : mDate._i) : txt;
                        if (_.isNil(date)) date = null;
                        su.wedgeCell(_$grid[0], { rowIdx: idx, columnKey: key }, date, reset);
                        $.data($cell, v.DATA, date);
                        $cell.innerHTML = _.isNil(txt) ? (mDate.isValid() ? mDate.format(cbx.format[0]) : (_.isNil(mDate._i) ? "" : mDate._i)) : txt;
                    }
                }
                
                return idx;
            },
            optionsList: function(id, key) {
                let control = dkn.controlType[key], controlDef, listType, controlMap = _mafollicle[SheetDef][_currentSheet].controlMap;
                if (!control || !controlMap || !(controlDef = controlMap[key])) return null;
                if (control === dkn.REFER_BUTTON) {
                    return (controlDef.pattern || {})[(controlDef.list || {})[id]] || null;
                } else if (_.isObject(control) && control.type === dkn.COMBOBOX) {
                    if (control.optionsMap && !_.isNil(listType = control.optionsMap[id])) {
                        return control.optionsList[listType] || null;
                    } else {
                        return control.options || null;
                    }
                } 
                
                return null;
            },
            checkAll: function(key, fixed) {
                let idxes, rows; 
                if (fixed) {
                    idxes = _vessel().desc.fixedColIdxes;
                } else {
                    idxes = _vessel().desc.colIdxes;
                }
                
                let i = idxes[key];
                if (_.isNil(i)) return;
                _.forEach(_.keys(_mafollicle), k => {
                    if (k === SheetDef) return;
                    let st = _mafollicle[k][_currentSheet];
                    if (!st) return;
                    _.forEach(st.desc[fixed ? "fixedRows" : "rows"], r => {
                        if (!r) return;
                        let cell = r[i];
                        if (cell) {
                            let check = cell.querySelector("input[type='checkbox']");
                            if (!cell.classList.contains(color.Disable) && !cell.classList.contains(color.Lock) 
                                && check && check.getAttribute("checked") !== "checked") {
                                check.setAttribute("checked", "checked");
                                check.checked = true;
                                let evt = document.createEvent("HTMLEvents");
                                evt.initEvent("change", false, true);
                                evt.checked = true;
                                evt.pg = k;
                                check.dispatchEvent(evt);
                            }
                        }
                    });
                });
                
                if (!dkn.allCheck[key]) {
                    dkn.allCheck[key] = { stt: true };
                }
            },
            uncheckAll: function(key, fixed) {
                let idxes, rows; 
                if (fixed) {
                    idxes = _vessel().desc.fixedColIdxes;
                } else {
                    idxes = _vessel().desc.colIdxes;
                }
                
                let i = idxes[key];
                if (_.isNil(i)) return;
                _.forEach(_.keys(_mafollicle), k => {
                    if (k === SheetDef) return;
                    let st = _mafollicle[k][_currentSheet];
                    if (!st) return;
                    _.forEach(st.desc[fixed ? "fixedRows" : "rows"], r => {
                        if (!r) return;
                        let cell = r[i];
                        if (cell) {
                            let check = cell.querySelector("input[type='checkbox']");
                            if (!cell.classList.contains(color.Disable) && !cell.classList.contains(color.Lock) 
                                && check && check.getAttribute("checked") === "checked") {
                                check.removeAttribute("checked");
                                check.checked = false;
                                let evt = document.createEvent("HTMLEvents");
                                evt.initEvent("change", false, true);
                                evt.checked = false;
                                evt.pg = k;
                                check.dispatchEvent(evt);
                            }
                        }
                    });
                });
                
                if (!dkn.allCheck[key]) {
                    dkn.allCheck[key] = { stt: false };
                }
            },
            headerText: function(key, text, parent) {
                let rename = function(cols) {
                    let ret;
                    _.forEach(cols, c => {
                        if (parent) {
                            if (c.group && c.headerText === key) {
                                c.headerText = text;
                                ret = c;
                                return false;
                            }
                            return;
                        }
                        
                        if (c.group && (ret = rename(c.group, key, text))) {
                            return false;    
                        }
                        
                        if (c.key === key) {
                            c.headerText = text;
                            ret = c;
                            return false;
                        }
                    });
                    
                    return ret;
                }
                
                let found;
                _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let cols = _mafollicle[SheetDef][k].columns;
                    if (cols) {
                        found = rename(cols);
                    }
                });
                
                let colspan, tdList = _$grid[0].querySelectorAll("." + HEADER + "." + FREE + " td");
                let replace = function(td) {
                    let done, coord = ti.getCellCoord(td);
                    colspan = td.getAttribute("colspan");
                    if (!parent && coord && coord.columnKey === key) {
                        td.innerHTML = text;
                        done = true;
                    } else if (parent && !_.isNil(colspan) && td.textContent === key) {
                        td.innerHTML = text;
                        done = true;
                    }
                    
                    return done;
                };
                
                _.forEach(tdList, td => {
                    if (replace(td)) return false;
                });
                
                _.forEach(_.keys(_mafollicle), k => {
                    if (k === SheetDef) return;
                    _.forEach(_.keys(_mafollicle[k]), s => {
                        if (s === _currentSheet) return;
                        let body;
                        if ((body = _mafollicle[k][s].$hBody)) {
                            _.forEach(body.querySelectorAll("td"), td => {
                                if (replace(td)) return false;
                            });
                        }
                    });
                });
            },
            replace: function(key, condition, value, dr) {
                if (_.isNil(key) || !_.isFunction(condition) || !_.isFunction(value)) return;
                _.forEach(_.keys(_mafollicle), k => {
                    if (k === SheetDef || (_.isNumber(_currentPage) && Number(k) !== _currentPage)) return;
                    _.forEach(_mafollicle[k].dataSource, (d, i) => {
                        if (!condition(d[key], d)) return;
                        let setVal = value(d[key], d);
                        let maf = _mafollicle[k][_currentSheet], r;
                        if (maf && maf.desc && (r = maf.desc.rows[i])) {
                            khl.clear({ id: d[_pk], columnKey: key, element: r[maf.desc.colIdxes[key]] }); 
                        }
                        
                        _$grid.mGrid("updateCell", d[_pk], key, setVal, false, !dr);
                    });
                });
            },
            showHiddenRows: function() {
                v.demoRows();
            },
            setErrors: function(errs: any, s: any, lockNotSet?: any) {
                if (!errs) return;
                let z = errs.length - 1;
                s = !_.isNil(s) ? s : _currentSheet;
                while (z >= 0) {
                    let e = _.cloneDeep(errs[z]), i, pi = Math.floor(e.index / _pageSize), y = e.index - (_.isString(_currentPage) ? 0 : pi * _pageSize),
                        pmaf = _mafollicle[_.isString(_currentPage) ? _currentPage : pi], maf = pmaf[s], data = (pmaf.dataSource || [])[y];
                    
                    if (data && lockNotSet 
                        && _.find(((_cellStates[data[_pk]] || {})[e.columnKey] || [{ state: [] }])[0].state, st => st === color.Lock || st === color.Disable)) {
                        z--;
                        continue;
                    }
                    
                    if (maf && maf.desc) {
                        i = maf.desc.fixedColIdxes[e.columnKey];
                        if (_.isNil(i)) {
                            i = maf.desc.colIdxes[e.columnKey];
                            if (!_.isNil(maf.desc.rows[y])) {
                                e.element = maf.desc.rows[y][i];
                            }
                        } else {
                            if (!_.isNil(maf.desc.fixedRows[y])) {
                                e.element = maf.desc.fixedRows[y][i];
                            }
                        }
                        
                        if (e.element) {
                            e.index = y;
                            khl.set(e, e.message);
                            errs.splice(z, 1);
                            z--;
                            continue;
                        }
                    }
                    
                    let p = _.isString(_currentPage) ? _currentPage : pi;
                    if (!maf) {
                        _mafollicle[p][s] = {};
                        maf = _mafollicle[p][s];
                    } 
                    
                    if (!maf.checkedErrors) {
                        _mafollicle[p][s].checkedErrors = [e];
                    } else _mafollicle[p][s].checkedErrors.push(e);
                    khl.set(e, e.message, 2);
                    z--;
                }
            },
            clearErrors: function(errs: any, s: any) { 
                if (!errs) return;
                s = !_.isNil(s) ? s : _currentSheet;
                _.forEach(errs, e => {
                    _.forEach(_.keys(_mafollicle), p => {
                        if (p === SheetDef) return;
                        let maf = _mafollicle[p][s];
                        if (maf && maf.desc) {
                            let y = _.findIndex(_mafollicle[p].dataSource, d => d[_pk] === e.id),
                                i = maf.desc.fixedColIdxes[e.columnKey];
                            if (_.isNil(i)) {
                                i = maf.desc.colIdxes[e.columnKey];
                                if (!_.isNil(maf.desc.rows[y])) {
                                    e.element = maf.desc.rows[y][i];
                                }
                            } else if (!_.isNil(maf.desc.fixedRows[y])) {
                                e.element = maf.desc.fixedRows[y][i];
                            }
                            
                            if (e.element) {
                                khl.clear(e);
                            }
                        } else {
                            _.remove(maf && maf.checkedErrors, ce => ce.columnKey === e.columnKey && ce.id === e.id);
                        } 
                    });
                });
            },
            removeInsertions: function() {
                v.eliminRows(_.cloneDeep(v._encarRows).sort((a, b) => b - a));
            },
            validate: function(lock, check) {
                let errors = [];
                _.forEach(_.keys(_mafollicle), k => {
                    if (k === SheetDef) return;
                    _.forEach(_mafollicle[k].dataSource, (data, i) => {
                        _.forEach(_cstifle(), c => {
                            let validator = _validators[c.key];
                            if (!validator || _.find(_hiddenColumns, hidden => hidden === c.key) || (_.isFunction(check) && !check(data))
                                || (!lock && _.find(((_cellStates[data[_pk]] || {})[c.key] || [{ state: [] }])[0].state, st => st === color.Lock || st === color.Disable))) return; 
                            let res = validator.probe(data[c.key], data[_pk]);
                            if (res && !res.isValid) {
                                let err = { id: data[_pk], index: i, columnKey: c.key, message: res.errorMessage };
                                errors.push(err);
                            }
                        });
                    });
                });
                
                if (errors.length > 0) {
                    this.setErrors(errors);
                }
            },
            columnOrder: function() {
                let order = [];
                if (_vessel().desc) {
                    let fixedLength = 0;
                    [ "fixedColIdxes", "colIdxes" ].forEach((col, ord) => {
                        let idx = _vessel().desc[col];
                        _.forEach(_.keys(idx), i => order[idx[i] + fixedLength] = i);
                        if (!ord) fixedLength = _.keys(idx).length;
                    });
                }
                
                return order;
            },
            getCellValue: function(id, key) {
                let idx = _.findIndex(_dataSource, r => r[_pk] === id);
                if (_.isNil(idx)) return;
                return _dataSource[idx][key];
            },
            selectedSheet: function() {
                return _currentSheet;
            },
            selectedPage: function() {
                return _currentPage;
            },
            columnWidth: function(col, stt) {
                let item = uk.localStorage.getItem(request.location.current.rawUrl + "/" + _$grid.attr("id"));  
                if (item.isPresent() && col) {
                    let obj = JSON.parse(item.get()); 
                    let w = obj[stt ? "reparer" : _currentSheet][col]; 
                    return !_.isNil(w) ? w : -1;
                }
                return -1;
            },
            viewErrors: function() {
                ssk.trigger(this.element[0], "falcon");
            },
            destroy: function() {
                _maxFixedWidth = 0; _maxFreeWidth = null; _columnsMap = {}; _dataSource = null; _secColumn = {};
                _hasFixed = null; _validators = {}; _mDesc = null; _mEditor = null; _cloud = null;
                _hr = null; _direction = null; _errors = []; _errorColumns = null; _errorsOnPage = null; kt._widths = {}; kt._columnWidths = {};
                _$grid = null; _pk = null; _pkType = null; _summaries = null; _objId = null; _getObjId = null; dkn.allCheck = {}; khl._infobulle = null;
                _hasSum = null; _pageSize = null; _currentPage = null; _currentSheet = null; _start = null; _end = null; v._voilerRows = {}; v._encarRows = []; dkn.controlType = {};
                _headerHeight = null; _zeroHidden = null; _paging = false; _sheeting = false; _copie = false; _mafollicle = {}; kt._adjuster = null; kt._fixedGroups = [];
                _specialColumn = {}; _specialLinkColumn = {}; _fixedHiddenColumns = []; _hiddenColumns = []; _fixedColumns = null; _selected = {}; _dirties = {}; 
                _rid = {}, _headerWrappers = null; _bodyWrappers = null; _sumWrappers = null; _fixedControlMap = {}; _cellStates = null; _features = null; 
                _leftAlign = null; _header = null; _flexFitWidth = null; this.element.html(""); this.element.removeData(); _histoire = []; _linkage = [];
                this.element[0].parentNode.replaceChild(this.element[0].cloneNode(), this.element[0]);
            }  
        });
        
        /**
         * Change zero.
         */
        export function changeZero(hide: any) {
            let ves, desc, realVal;
            if (_zeroHidden === hide) return false;
            if ((ves = _vessel()) && (desc = ves.desc)) {
                _.forEach(desc.rows, r => {
                    _.forEach(r, c => {
                        let key = ti.getCellCoord(c).columnKey;
                        let control = dkn.controlType[key];
                        if (control !== dkn.TEXTBOX) return;
                        let content = $.data(c, v.DATA);
                        if (hide && ti.isZero(content, key)) {
                            c.textContent = "";
                        } else if (!hide && c.textContent === "" && content !== "") {
                            let format = su.format(_columnsMap[key][0], content);
                            c.textContent = format;
                        }
                    });
                });
            }
            
            return true;
        }
        
        /**
         * Trier.
         */
        function trier(name: any, dType?: any, desc?: any) {
            let media = [], data = [], i = 0;
            while (_dataSource.length > 0) {
                let d = _dataSource.shift();
                let v = d[name];
                if (dType === "FullDate" && !_.isNil(v)) {
                    v = moment(v).format("YYYY/MM/DD");
                }
//                if (name === "rowNumber" && _mafCurrent().rank) {
//                    v = _mafCurrent().rank[d[_pk]];
//                }
                
                let item = { ca: _mDesc.rows.shift(), r: _mDesc.rowElements.shift(), d: d, od: _mafollicle[_currentPage].origDs.shift() };
                if (!_.isNil(_mDesc.fixedRows)) {
                    item.fca = _mDesc.fixedRows.shift();
                    item.fr = _mDesc.fixedRowElements.shift();
                }
                
                _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let maf = _mafollicle[_currentPage][k];
                    if (k === _currentSheet || !maf || !maf.desc) return;
                    item[k] = { r: maf.desc.rowElements.shift(), ca: maf.desc.rows.shift() };
                    
                    if (!_.isNil(maf.desc.fixedRows)) {
                        item[k].fca = maf.desc.fixedRows.shift();
                        item[k].fr = maf.desc.fixedRowElements.shift();
                    }
                });
                
                media[i] = item;
                data.push({ i: i++, v: v });
            }
            
            if (data.length === 0) return;
            let tfn, mi, coeff = desc ? -1 : 1, oton = {};
            switch (dType) {
                case "FullDate":
                case "YearMonth":
                default:
                    tfn = (a, b) => { 
                        if (_.isNil(a.v) && _.isNil(b.v)) return 0;
                        if (_.isNil(a.v)) return -1 * coeff;
                        if (_.isNil(b.v)) return 1 * coeff;
                        return a.v.compareTo(b.v) * coeff;
                    }; 
                    break;
                case "Time":
                    tfn = (a, b) => {
                        if (_.isNil(a.v) && _.isNil(b.v)) return 0;
                        if (_.isNil(a.v)) return -1 * coeff;
                        if (_.isNil(b.v)) return 1 * coeff;
                        return coeff * (ti.timeToMinutes(a.v) - ti.timeToMinutes(b.v));
                    };
                    break;
                case "Number":
                    tfn = (a, b) => {
                        if (_.isNil(a.v) && _.isNil(b.v)) return 0;
                        if (_.isNil(a.v)) return -1 * coeff;
                        if (_.isNil(b.v)) return 1 * coeff;
                        return coeff * (a.v - b.v);
                    };
                    break;
            }
            
            data.sort(tfn);
            
            _.forEach(data, (d, i) => {
                mi = media[d.i];
                _dataSource[i] = mi.d;
                _mafollicle[_currentPage].origDs[i] = mi.od;
                if (d.i !== i) oton[d.i] = i; 
                
                if (_.isNil(mi.r)) {
                    if (i >= _start && i <= _end) {
                        let res = _cloud.painter.row(mi.d, v.DefaultRowConfig, i);
                        if (res.fixedRow) {
                            _mDesc.fixedRowElements[i] = res.fixedRow;
                            _mDesc.fixedRows[i] = res.fixedElements;
                        }
                        _mDesc.rowElements[i] = res.row;
                        _mDesc.rows[i] = res.elements;
                    }
                } else {
                    if (mi.fr) {
                        $.data(mi.fr, lo.VIEW, i);
                        _mDesc.fixedRowElements[i] = mi.fr;
                        _mDesc.fixedRows[i] = mi.fca;
                    }
                    $.data(mi.r, lo.VIEW, i);
                    _mDesc.rowElements[i] = mi.r;
                    _mDesc.rows[i] = mi.ca;
                }
                
                _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let maf = _mafollicle[_currentPage][k];
                    if (k === _currentSheet || !maf || !maf.desc) return;
                    if (_.isNil(mi[k].r)) {
                        if (i >= _start && i <= _end) {
                            let painter = _mafollicle[SheetDef][k].ltrlPainter;
                            if (!painter) {
                                painter = _.cloneDeep(_cloud.sidePainter);
                                painter.revive(k);
                                _mafollicle[SheetDef][k].ltrlPainter = painter;
                            }
                            let res = painter.row(mi.d, v.DefaultRowConfig, i);
                            if (_mDesc.fixedRowElements) {
                                maf.desc.fixedRowElements[i] = _mDesc.fixedRowElements[i];
                                maf.desc.fixedRows[i] = _mDesc.fixedRows[i];
                            }
                            maf.desc.rowElements[i] = res.row;
                            maf.desc.rows[i] = res.elements;
                        }
                    } else {
                        if (mi[k].fr) {
                            maf.desc.fixedRowElements[i] = mi[k].fr;
                            maf.desc.fixedRows[i] = mi[k].fca;
                        }
                        $.data(mi[k].r, lo.VIEW, i);
                        maf.desc.rowElements[i] = mi[k].r;
                        maf.desc.rows[i] = mi[k].ca;
                    }
                });
            });
            
            _cloud.renderRows(true);
            _vessel().$bBody = _bodyWrappers[_hasFixed ? 1 : 0].querySelector("tbody");
            if (_.keys(oton).length > 0) {
                _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let maf = _mafollicle[_currentPage][k];
                    if (!maf || !maf.desc) return; 
                    _.forEach(maf.errors, e => {
                        let z = oton[e.index];
                        if (!_.isNil(z)) {
                            e.index = z;
                        }
                    });
                    
                    _.forEach(maf.histoire, h => {
                        _.forEach(h.o, o => {
                            let z = oton[o.coord.rowIdx];
                            if (!_.isNil(z)) {
                                o.coord.rowIdx = z;
                            } 
                        });
                    });
                    
                    let vars = [], t = _.keys(maf.selected), c = 0;
                    while (t.length > 0) {
                        let sk = t.shift(), z = oton[sk];
                        if (!_.isNil(z)) {
                            maf.selected[z] = maf.selected[sk];
                            vars.push(z);
                            if (!_.includes(vars, sk)) {
                                delete maf.selected[sk];
                            }
                        }
                    }
                });
                
                if (v._voilerRows) {
                    let copyVoil = _.cloneDeep(v._voilerRows);
                    _.forEach(_.keys(v._voilerRows), p => {
                        v._voilerRows[p] = [];
                    });
                    
                    _.forEach(_.keys(copyVoil), p => {
                        _.forEach(copyVoil[p], l => {
                            let z = oton[l];
                            if (!_.isNil(z)) {
                                let nama = Math.floor(z / (aho._bloc * 2 - 1));
                                if (_.isNil(v._voilerRows[nama])) {
                                    v._voilerRows[nama] = [];
                                }
                                
                                v._voilerRows[nama].push(z);
                            } else v._voilerRows[p].push(l);
                        });
                    });
                }
                
                if (v._encarRows && v._encarRows.length > 0) {
                    for (let ri = 0; ri < v._encarRows.length; ri++) {
                        let z = oton[v._encarRows[ri]];
                        if (!_.isNil(z)) {
                            v._encarRows[ri] = z;
                        }
                    }
                }
            }
        }
        
        function extraireErrors() {
            if (!_errors || _errors.length === 0 || !_dataSource) return;
            let sorted = _.sortBy(_errors, "index"), rangee, length = sorted.length, 
                y = length - 1, to = sorted.pop(), ordonnerList = {}, view, nv;
            for (let i = to.index; i >= 0; i--) {
                let fr, ni = i + length - y - 1, r = _mDesc.rowElements[ni];
                if (i === to.index) {
                    let data = _dataSource.splice(ni, 1)[0];
                    _dataSource.splice(0, 0, data);
                    _mafCurrent().origDs.splice(0, 0, _mafCurrent().origDs.splice(ni, 1)[0]);
                    
                    if (!r) {
                        let res = _cloud.painter.row(data, v.DefaultRowConfig, y);
                        if (res.fixedRow) {
                            _mDesc.fixedRowElements.splice(0, 0, res.fixedRow);
                            _mDesc.fixedRows.splice(0, 0, res.fixedElements);
                        }
                        _mDesc.rowElements.splice(0, 0, res.row);
                        _mDesc.rows.splice(0, 0, res.elements);
                    } else {
                        if (_mDesc.fixedRows) {
                            rangee = _mDesc.fixedRowElements.splice(ni, 1)[0];
                            $.data(rangee, lo.VIEW, y);
                            _mDesc.fixedRowElements.splice(0, 0, rangee);
                            _mDesc.fixedRows.splice(0, 0, _mDesc.fixedRows.splice(ni, 1)[0]);
                        }
                        rangee = _mDesc.rowElements.splice(ni, 1)[0];
                        $.data(rangee, lo.VIEW, y);
                        _mDesc.rowElements.splice(0, 0, rangee);
                        _mDesc.rows.splice(0, 0, _mDesc.rows.splice(ni, 1)[0]);
                    }
                    ordonnerList[i] = y;
                    
                    _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                        let maf = _mafCurrent()[k];
                        if (_currentSheet === k || !maf || !maf.desc) return;
                        r = maf.desc.rowElements[ni];
                        if (!r) {
                            let painter = _mafollicle[SheetDef][k].ltrlPainter;
                            if (!painter) {
                                painter = _.cloneDeep(_cloud.sidePainter);
                                painter.revive(k);
                                _mafollicle[SheetDef][k].ltrlPainter = painter;
                            }
                            let res = painter.row(data, v.DefaultRowConfig, y);
                            if (res.fixedRow) {
                                maf.desc.fixedRowElements.splice(0, 0, res.fixedRow);
                                maf.desc.fixedRows.splice(0, 0, res.fixedElements);
                            }
                            maf.desc.rowElements.splice(0, 0, res.row);
                            maf.desc.rows.splice(0, 0, res.elements);
                        } else {
                            if (maf.desc.fixedRows) {
                                rangee = maf.desc.fixedRowElements.splice(ni, 1)[0];
                                $.data(rangee, lo.VIEW, y);
                                maf.desc.fixedRowElements.splice(0, 0, rangee);
                                maf.desc.fixedRows.splice(0, 0, maf.desc.fixedRows.splice(ni, 1)[0]);
                            }
                            rangee = maf.desc.rowElements.splice(ni, 1)[0];
                            $.data(rangee, lo.VIEW, y);
                            maf.desc.rowElements.splice(0, 0, rangee);
                            maf.desc.rows.splice(0, 0, maf.desc.rows.splice(ni, 1)[0]);
                        }
                    });
                    
                    y--;
                    if (sorted.length > 0) to = sorted.pop();
                } else {
                    if (r) { 
                        if (_mDesc.fixedRowElements) {
                            fr = _mDesc.fixedRowElements[ni];
                            $.data(fr, lo.VIEW, $.data(fr, lo.VIEW) + length - y - 1);
                        }
                        view = $.data(r, lo.VIEW) + length - y - 1;
                        $.data(r, lo.VIEW, view);
                        ordonnerList[i] = view;
                    }
                    
                    _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                        let maf = _mafCurrent()[k];
                        if (_currentSheet === k || !maf || !maf.desc) return;
                        r = maf.desc.rowElements[ni];
                        if (!r) return;
                        if (maf.desc.fixedRowElements) {
                            fr = maf.desc.fixedRowElements[ni];
                            $.data(fr, lo.VIEW, $.data(fr, lo.VIEW) + length - y - 1);    
                        }
                        view = $.data(r, lo.VIEW) + length - y - 1;
                        $.data(r, lo.VIEW, view);
                        if (_.isNil(ordonnerList[i])) ordonnerList[i] = view;
                    });
                }
            }
            
            _bodyWrappers[0].scrollTop = 0;
            _cloud.renderRows(true);
            
            if (!_.keys(ordonnerList).length) return;
             _.forEach(_.keys(_mafollicle[SheetDef]), k => {
                    let maf = _mafCurrent()[k];
                    if (!maf || !maf.desc) return; 
                    _.forEach(maf.errors, e => {
                        nv = ordonnerList[e.index];
                        if (!_.isNil(nv)) {
                            e.index = nv;
                        }
                    });
                    
                    _.forEach(maf.histoire, h => {
                        _.forEach(h.o, o => {
                            nv = ordonnerList[o.coord.rowIdx];
                            if (!_.isNil(nv)) {
                                o.coord.rowIdx = nv;
                            }
                        });
                    });
                    
                    let vars = [], t = _.keys(maf.selected), c = 0;
                    while (t.length > 0) {
                        let sk = t.shift(), z = ordonnerList[sk];
                        if (!_.isNil(z)) {
                            maf.selected[z] = maf.selected[sk];
                            vars.push(z);
                            if (!_.includes(vars, sk)) {
                                delete maf.selected[sk];
                            }
                        }
                    }
             });
        }
    }
    
    module su {
        
        export const EDITOR = "meditor";
        export let _copieMode;
        export let afterCollertar;
        
        /**
         * Binding.
         */
        export function binding($grid: HTMLElement, fitWindow: any, noRowsMin: any, noRowsMax: any) {
            
            $grid.addXEventListener(ssk.MOUSE_DOWN, evt => {
                let $tCell = evt.target;
                if (!$tCell || !selector.is($tCell, "." + v.CELL_CLS)
                    || $tCell.classList.contains(color.Disable)
                    || $tCell.classList.contains(color.Lock)
                    || $tCell.classList.contains(dkn.LABEL_CLS)) return;
                
                if (_.keys(ssk.KeyPressed).length > 0) {
                    evt.preventDefault();
                    return;
                }
                
                let coord = ti.getCellCoord($tCell);
                let control = dkn.controlType[coord.columnKey];
                let cEditor = _mEditor;
                
                if (!control || ti.isEqual(coord, cEditor, [ "rowIdx", "columnKey" ])
                    || (control === dkn.TEXTBOX && !$tCell.classList.contains(lch.CELL_SELECTED_CLS))) return;
                let txtBox = dkn.controlType[dkn.TEXTBOX];
                if (!txtBox) {
                    dkn.textBox();
                    txtBox = dkn.controlType[dkn.TEXTBOX];
                }
                
                let $editor = txtBox.my;
                let $input = $editor.querySelector("input.medit");
                let cType = {};
                if (control === dkn.TEXTBOX && $tCell.classList.contains(lch.CELL_SELECTED_CLS)) {
                    endEdit($grid);
                    if ($tCell.classList.contains(hpl.CURRENCY_CLS)) {
                        $tCell.classList.remove(hpl.CURRENCY_CLS);
                        $editor.classList.add(hpl.CURRENCY_CLS);
                    }
                    
                    $tCell.textContent = "";
                    $tCell.classList.add(dkn.CONTROL_CLS);
                    $tCell.appendChild($editor);
                    let data = $.data($tCell, v.DATA);
                    $input.value = !_.isNil(data) ? data : "";
                    cType.type = dkn.TEXTBOX;
                    setTimeout(() => {
                        $input.select();
                    }, 0);
                    
                    if ($tCell.classList.contains(v.ALIGN_RIGHT)) {
                        $input.classList.remove(v.ALIGN_LEFT);
                    } else {
                        $input.classList.add(v.ALIGN_LEFT);
                    }
                    
                    let coord = ti.getCellCoord($tCell);
                    $input.style.imeMode = "inactive";
                    if (coord) {
                        let column = _columnsMap[coord.columnKey];
                        if (column && column[0].japanese) {
                            $input.style.imeMode = "active";
                        }
                    }
                } else if (control.type === dkn.COMBOBOX && !$tCell.querySelector(".mcombo-wrapper")) {
                    endEdit($grid, true);
                    $tCell.textContent = "";
                    $tCell.classList.add(dkn.CONTROL_CLS);
                    let stt, panel, comboList, itemHolder, height;
                    if (control.optionsList) {
                        if (!_.isNil(stt = control.optionsMap[_dataSource[coord.rowIdx][_pk]])) {
                            panel = control.panel[stt + 1];
                            height = control.maxHeight[stt + 1];
                        } else {
                            panel = control.panel[0];
                            height = control.maxHeight[0];
                        }
                        
                        comboList = control.dropdown.querySelector(".mcombo-list");
                        itemHolder = comboList.querySelector(".mcombo-listitemholder");
                        if (itemHolder !== panel) {
                            comboList.replaceChild(panel, itemHolder);
                            control.dropdown.style.maxHeight = height + "px";
                        }
                    }
                    
                    let $combo = control.my.querySelector("." + dkn.CBX_CLS);
                    let $comboValue = control.my.querySelector(".mcombo-value");
                    let items = control.dropdown.querySelectorAll(".mcombo-listitem");
                    let selected, code = $.data($tCell, lo.CBX_SELECTED_TD);
                    _.forEach(items, i => {
                        let value = $.data(i, lo.CBX_ITEM_VALUE);
                        
                        if (i.classList.contains("selecteditem")) {
                            i.classList.remove("selecteditem");
                        }
                        
                        if (code === value) {
                            let $item = i.querySelector(".mcombo-item");
                            if ($item) {
                                $comboValue.innerHTML = "";
                                $comboValue.appendChild($item.cloneNode(true));
                                i.classList.add("selecteditem");
                                selected = true;
                            }
                        }
                    });
                    
                    if (!selected) {
                        $comboValue.innerHTML = "";
                        let empty = _prtDiv.cloneNode(true);
                        empty.style.display = "inline-block";
                        $comboValue.appendChild(empty);
                    }
                    
                    $tCell.appendChild(control.my);
                    $.data(control.my, lo.CBX_SELECTED, code);
                    dkn.openDD(control.dropdown, control.my);
                    $combo.classList.add(dkn.CBX_ACTIVE_CLS);
                    cType.type = dkn.COMBOBOX;
                } else if (control.type === dkn.DATE_PICKER && !$tCell.querySelector("input")) {
                    endEdit($grid, true);
                    $tCell.textContent = "";
                    $tCell.classList.add(dkn.CONTROL_CLS);
                    if ($tCell.classList.contains(v.ALIGN_RIGHT)) {
                        $input.classList.remove(v.ALIGN_LEFT);
                    } else {
                        $input.classList.add(v.ALIGN_LEFT);
                    }
                    
                    $tCell.appendChild($editor);
                    let data = $.data($tCell, v.DATA), mDate = moment(data, control.format, true),
                        mDisplayDate = mDate.isValid() ? mDate : moment();
                    $input.value = !_.isNil(data) && data !== "" ? (mDate.isValid() ? mDate.format(control.format[0]) : mDate._i) : "";
                    cType.type = dkn.DATE_PICKER;
                    cType.format = control.format;
                    cType.formatType = control.formatType;
                    let $daysPick = dkn._ramass[control.formatType].querySelector("div[data-view='days picker']"),
                        $monthsPick = dkn._ramass[control.formatType].querySelector("div[data-view='months picker']"),
                        $yearsPick = dkn._ramass[control.formatType].querySelector("div[data-view='years picker']");
                    if ($daysPick) {
                        $daysPick.classList.remove(dkn.PICKER_HIDE);
                        $monthsPick.classList.add(dkn.PICKER_HIDE);
                        $yearsPick.classList.add(dkn.PICKER_HIDE);
                        ssk.trigger(dkn._ramass[control.formatType], "set", [ mDisplayDate ]);
                    } else if ($monthsPick) {
                        $monthsPick.classList.remove(dkn.PICKER_HIDE);
                        $yearsPick.classList.add(dkn.PICKER_HIDE);
                        ssk.trigger(dkn._ramass[control.formatType], "set", [ mDisplayDate, 0, 1 ]);
                    } else {
                        ssk.trigger(dkn._ramass[control.formatType], "set", [ mDisplayDate, 0, 2 ]);
                    }
                    dkn.openDD(dkn._ramass[control.formatType], $tCell, true);
                } else if (control === dkn.FLEX_IMAGE || control === dkn.CHECKBOX
                        || control === dkn.LINK_LABEL || control === dkn.SWITCH_BUTTONS
                        || control === dkn.REFER_BUTTON || control === dkn.IMAGE) {
                    endEdit($grid);
                }
                
                _mEditor = _.assignIn(coord, cType);
                evt.stopPropagation();
            });
            
            document.addXEventListener(ssk.MOUSE_DOWN, evt => {
                if (!evt.target) return;
                if (_.keys(ssk.KeyPressed).length > 0) {
                    evt.preventDefault();
                    return;
                }
                
                if (!selector.is(evt.target, "input.medit")
                    && !selector.is(evt.target, "div[class*='mcombo']")) {
                    endEdit($grid, true);
                }
            });
            
            $grid.addXEventListener(ssk.KEY_DOWN, evt => {
                let $grid = evt.currentTarget, $tCell = evt.target;
                if (!$grid) return;
                if (!ti.isEnterKey(evt) && !ti.isTabKey(evt) && !evt.ctrlKey
                    && ((evt.keyCode >= 46 && evt.keyCode <= 111) || (evt.keyCode >= 160 && evt.keyCode <= 223))) {
                    ssk.KeyPressed[evt.keyCode] = true;
                }
                
                if (!_(ssk.KeyPressed).keys().filter(k => k !== "13" && k !== "9").size()) {
                    if (ti.isEnterKey(evt)) {
                        let direct = $.data($grid, "enterDirect");
                        if (evt.shiftKey) {
                            lch.selectPrev($grid, direct);
                        } else {
                            lch.selectNext($grid, direct);        
                        }
                    } else if (ti.isTabKey(evt)) {
                        evt.preventDefault();
                        if (evt.shiftKey) {
                            lch.selectPrev($grid);
                        } else {
                            lch.selectNext($grid);
                        }
                    } else if (ti.isArrowLeft(evt)) {
                        evt.preventDefault();
                        lch.selectPrev($grid);
                    } else if (ti.isArrowRight(evt)) {
                        evt.preventDefault();
                        lch.selectNext($grid);
                    } else if (ti.isArrowUp(evt)) {
                        evt.preventDefault();
                        lch.selectPrev($grid, "below");
                    } else if (ti.isArrowDown(evt)) {
                        evt.preventDefault();
                        lch.selectNext($grid, "below");
                    }
                }
                
                // Get input
                if (!evt.ctrlKey && ti.isAlphaNumeric(evt) || ti.isMinusSymbol(evt) || ti.isDeleteKey(evt)
                    || evt.keyCode === 113 || ti.isSpaceKey(evt)) {
                    if (!$tCell || !selector.is($tCell, "." + v.CELL_CLS)
                        || $tCell.classList.contains(color.Disable)
                        || $tCell.classList.contains(color.Lock)
                        || $tCell.classList.contains(dkn.LABEL_CLS)) return;
                    let coord = ti.getCellCoord($tCell);
                    let control = dkn.controlType[coord.columnKey];
                    let cEditor = _mEditor;
                    
                    if (control === dkn.CHECKBOX && ti.isSpaceKey(evt) && !$tCell.classList.contains(color.Hide)) {
                        let check = $tCell.querySelector("input[type='checkbox']");
                        if (!check) return;
                        let checked;
                        if (check.getAttribute("checked") === "checked") {
                            check.removeAttribute("checked");
                            check.checked = checked = false;
                        } else {
                            check.setAttribute("checked", "checked");
                            check.checked = checked = true;
                        }
                        let changeEvt = document.createEvent("HTMLEvents");
                        changeEvt.initEvent("change", false, true);
                        changeEvt.checked = checked;
                        check.dispatchEvent(changeEvt);
                        evt.preventDefault();
                        return;
                    }
                    
                    if (!control || ti.isEqual(coord, cEditor, [ "rowIdx", "columnKey" ])
                        || (control === dkn.TEXTBOX && !$tCell.classList.contains(lch.CELL_SELECTED_CLS))) return;
                    let $editor = dkn.controlType[dkn.TEXTBOX].my;
                    let $input = $editor.querySelector("input.medit");
                    let cType = {};
                    if (control === dkn.TEXTBOX && $tCell.classList.contains(lch.CELL_SELECTED_CLS)) {
                        endEdit($grid);
                        if ($tCell.classList.contains(hpl.CURRENCY_CLS)) {
                            $tCell.classList.remove(hpl.CURRENCY_CLS);
                            $editor.classList.add(hpl.CURRENCY_CLS);
                        }
                        
                        $tCell.textContent = "";
                        $tCell.classList.add(dkn.CONTROL_CLS);
                        $tCell.appendChild($editor);
//                        $input.value = evt.key;
                        if (ti.isDeleteKey(evt) && cEditor === null) {
                            $input.value = "";
                        } else if (evt.keyCode === 113) {
                            let data = $.data($tCell, v.DATA);
                            $input.value = !_.isNil(data) ? data : "";
                            $input.select();
                        }
                        
                        if ($tCell.classList.contains(v.ALIGN_RIGHT)) {
                            $input.classList.remove(v.ALIGN_LEFT);
                        } else {
                            $input.classList.add(v.ALIGN_LEFT);
                        }
                        
                        cType.type = dkn.TEXTBOX;
                        let coord = ti.getCellCoord($tCell);
                        $input.style.imeMode = "inactive";
                        if (coord) {
                            let column = _columnsMap[coord.columnKey];
                            if (column && column[0].japanese) {
                                $input.style.imeMode = "active";
                            }
                        }
                        
                        $input.focus();
                    }
                    
                    _mEditor = _.assignIn(coord, cType);
                }
                
                if (!_copie) return;
                if (evt.ctrlKey && evt.keyCode === 86 && _collerer) {
                    afterCollertar = document.activeElement;
                    _collerer.focus();
                } else if (evt.ctrlKey && evt.keyCode === 67) {
                    copieData();
                } else if (evt.ctrlKey && evt.keyCode === 88) {
                    copieData(true);
                } else if (evt.ctrlKey && evt.keyCode === 90) {
                    annuler();
                }
            });
            
            $grid.addXEventListener(ssk.KEY_UP, evt => {
                delete ssk.KeyPressed[evt.keyCode];
            });
            
            document.addXEventListener(ssk.KEY_DOWN, evt => {
                if (!ti.isEnterKey(evt) && !ti.isTabKey(evt) && !evt.ctrlKey
                    && ((evt.keyCode >= 46 && evt.keyCode <= 111) || (evt.keyCode >= 160 && evt.keyCode <= 223))) {
                    ssk.KeyPressed[evt.keyCode] = true;
                }
            });
            
            document.addXEventListener(ssk.KEY_UP, evt => {
                delete ssk.KeyPressed[evt.keyCode];
            });
            
            if (_copie) {
                $grid.addXEventListener(ssk.FOCUS_IN, evt => {
                    if (_collerer && _copieer) return;
                    _collerer = document.createElement("textarea");
                    _collerer.setAttribute("id", "mgrid-collerer");
                    _collerer.style.opacity = "0";
                    _collerer.style.overflow = "hidden";
                    _collerer.addXEventListener(ssk.PASTE, collerData.bind(null));
                    _copieer = document.createElement("textarea"); 
                    _copieer.setAttribute("id", "mgrid-copieer");
                    _copieer.style.opacity = "0";
                    _copieer.style.overflow = "hidden";
                    let $div = document.createElement("div");
                    $div.style.position = "fixed";
                    $div.style.top = "-10000px";
                    $div.style.left = "-10000px";
                    document.body.appendChild($div);
                    $div.appendChild(_collerer);
                    $div.appendChild(_copieer);
                });
            }
            
            if (fitWindow) {
                window.addXEventListener(ssk.RESIZE, evt => {
                    kt.screenLargeur(noRowsMin, noRowsMax);
                });
            }
        }
        
        /**
         * EndEdit.
         */
        export function endEdit($grid: HTMLElement, fromDoc?: any) {
            let editor = _mEditor;
            if (!editor) return;
            let $bCell = lch.cellAt($grid, editor.rowIdx, editor.columnKey);
            if (editor.type === dkn.TEXTBOX) {
                let $editor = dkn.controlType[dkn.TEXTBOX].my;
                let $input = $editor.querySelector("input.medit");
                let inputVal = $input.value;
                if ($bCell) {
                    let spl = {}, column = _columnsMap[editor.columnKey];
                    if (!column) return;
                    if (inputVal === "") {
                        ssk.trigger($input, ssk.MS_BEFORE_COMPL);
                    }
                    
                    let failed = khl.any({ element: $bCell }), 
                        formatted = failed ? inputVal : (_zeroHidden && ti.isZero(inputVal, editor.columnKey) ? "" : format(column[0], inputVal, spl));
                    $bCell.textContent = formatted;
                    let disFormat = inputVal === "" || failed ? inputVal : ((spl.padded || spl.toKana) ? formatted : formatSave(column[0], inputVal));
                    wedgeCell($grid, editor, disFormat, null, $bCell.classList.contains(khl.ERROR_CLS));
                    $.data($bCell, v.DATA, disFormat);
                    
                    if ($editor.classList.contains(hpl.CURRENCY_CLS)) {
                        $editor.classList.remove(hpl.CURRENCY_CLS);
                        $bCell.classList.add(hpl.CURRENCY_CLS);
                    }
                    $input.value = "";
                    
                    let inputRidd = function() {
                        if ($bCell.classList.contains(khl.ERROR_CLS)) return;
                        let ridd = column[0].inputProcess; 
                        if (ridd) {
                            let rData = _dataSource[parseFloat(editor.rowIdx)];
                            let rId;
                            if (rData) rId = rData[_pk];
                            ridd(rId, editor.columnKey, disFormat, rData).done((sData) => {
                                _.forEach(sData, sd => {
                                    let res = _$grid.mGrid("updateCell", sd.id, sd.item, sd.value);
                                    if (!_.isNil(res) && res >= 0) {
                                        let sht = _.filter(_.keys(_mafollicle[SheetDef]), k => {
                                            if (k === _currentSheet) return;
                                            let sCols = _mafollicle[SheetDef][k].columns;
                                            return _.find(sCols, c => c.key === sd.item);
                                        });
                                        
                                        _.forEach(sht, s => {
                                            wedgeShtCell(res, sd.item, sd.value, s); 
                                        });
                                    }
                                });
                            });
                        }
                    };
                    
                    let sCol = _specialColumn[editor.columnKey];
                    if (sCol) {
                        let cbx = dkn.controlType[sCol];
                        if (_.toLower(column[0].dataType) === "number") {
                            inputVal = parseFloat(inputVal);
                        }
                        wedgeCell($grid, { rowIdx: editor.rowIdx, columnKey: sCol }, inputVal);
                        let selectedOpt = _.find(cbx.options, o => o.code === inputVal); 
                        if (!_.isNil(selectedOpt)) {
                            let $cbxCell = lch.cellAt($grid, editor.rowIdx, sCol);
                            $cbxCell.textContent = selectedOpt ? selectedOpt.name : "";
                            $.data($cbxCell, lo.CBX_SELECTED_TD, inputVal);
                        }
                        
                        inputRidd();
                    } else if ((sCol = _specialLinkColumn[editor.columnKey]) && sCol.changed) {
                        let data = _mafollicle[_currentPage].origDs[editor.rowIdx];
                        sCol.changed(editor.columnKey, data[_pk], formatted, data[editor.columnKey]).done(res => {
                            let $linkCell = lch.cellAt($grid, editor.rowIdx, sCol.column);
                            if ($linkCell) {
                                $linkCell.querySelector("a").textContent = res;
                                wedgeCell($grid, { rowIdx: editor.rowIdx, columnKey: sCol.column }, res);
                            }
                            inputRidd();
                        });
                    } else inputRidd();
                }
            } else if (editor.type === dkn.COMBOBOX) {
                let cbx = dkn.controlType[editor.columnKey];
                let bSelectedValue = $.data(cbx.my, lo.CBX_SELECTED);
                let $combo = cbx.my.querySelector("." + dkn.CBX_CLS), options;
                if (cbx.optionsMap && !_.isNil(stt = cbx.optionsMap[_dataSource[editor.rowIdx][_pk]])) {
                    options = cbx.optionsList[stt];
                } else {
                    options = cbx.options;
                }
                
                wedgeCell($grid, editor, bSelectedValue);
                let selectedOpt = _.find(options, o => o[cbx.optionsValue] === bSelectedValue); 
                $bCell.textContent = selectedOpt ? selectedOpt[cbx.optionsText] : ""; 
                if (cbx.dropdown && cbx.dropdown.style.top !== "-99999px") {
                    dkn.closeDD(cbx.dropdown);
                    $combo.classList.remove(dkn.CBX_ACTIVE_CLS);
                }
            } else if (editor.type === dkn.DATE_PICKER) {
                let date, picker = dkn.controlType[editor.columnKey],
                    $editor = dkn.controlType[dkn.TEXTBOX].my,
                    $input = $editor.querySelector("input.medit"),
                    mDate = moment.utc($input.value, editor.format, true);
                if ($input.value === "") {
                    ssk.trigger($input, ssk.MS_BEFORE_COMPL);
                }
                
                if (mDate.isValid()) {
                    date = editor.formatType === "ymd" ? mDate.toDate() : mDate.format(editor.format[0]);
                    wedgeCell($grid, editor, date);
                    $bCell.textContent = mDate.format(editor.format[0]);
                    $.data($bCell, v.DATA, date);
                } else {
                    date = editor.formatType === "ymd" ? mDate : mDate._i;
                    wedgeCell($grid, editor, date);
                    $bCell.textContent = mDate._i;
                    $.data($bCell, v.DATA, date);
                }
                
                if (fromDoc && picker && _.isFunction(picker.inputProcess)) {
                    picker.inputProcess(mDate, _dataSource[parseFloat(editor.rowIdx)]);
                }
                
                $input.value = "";
                dkn.closeDD(dkn._ramass[editor.formatType], true);
            }
            
            $bCell.classList.remove(dkn.CONTROL_CLS);
            _mEditor = null;
        }
        
        /**
         * WedgeCell.
         */
        export function wedgeCell($grid: HTMLElement, coord: any, cellValue: any, reset?: any, ng?: any, pg?: any) {
            let res, valueType = hpl.getValueType($grid, coord.columnKey);
            if (!_.isNil(cellValue) && !_.isEmpty(cellValue)) { 
                if (valueType === "TimeWithDay" || valueType === "Clock") {
                    try {
                        cellValue = time.minutesBased.clock.dayattr.create(
                            time.minutesBased.clock.dayattr.parseString(String(cellValue)).asMinutes).shortText;
                    } catch(e) {}
                } else if (valueType === "Time") {
                    cellValue = nts.uk.time.minutesBased.duration.parseString(String(cellValue)).format();
                }
            }
            
            let dataSource = !_.isNil(pg) ? _mafollicle[pg].dataSource : _dataSource, rData = dataSource[coord.rowIdx],
                currentPage = !_.isNil(pg) ? pg : _currentPage;
            if (_.isNil(rData)) return;
            let id = rData[_pk];
            
            let origDs = _mafollicle[currentPage].origDs;
            if (!origDs) return;
            let column = _columnsMap[coord.columnKey];
            
            if (column && _.toLower(column[0].dataType) === "number") {
                cellValue = (_.isNil(cellValue) || cellValue === "") ? null : parseFloat(cellValue);
            }
           
            if (reset) {
                origDs[coord.rowIdx][coord.columnKey] = cellValue;
            }
            let $cell, sumDone, origVal = origDs[coord.rowIdx][coord.columnKey],
                cmaf = !_.isNil(pg) ? _mafollicle[pg][_currentSheet] : _vessel();
            
            let transe = function(sheet, zeroHidden, dirties, desc, main) {
                let colour, before, after, total, calcCell = lch.cellAt(_$grid[0], coord.rowIdx, coord.columnKey, desc), sum; 
                if (_summaries) {
                    sum = _summaries[coord.columnKey];
                }
                if (sum && sum.calculator === "Time" && calcCell) {
                    if (!sumDone) {
                        after = moment.duration(cellValue);
                        before = moment.duration($.data(calcCell, v.DATA));
                        let diff = after.subtract(before);
                        sum[currentPage].add(diff);
                        sumDone = true;
                    }
                    sum[sheet].textContent = ti.momentToString(sum[currentPage]);
                } else if (sum && sum.calculator === "Number" && calcCell) {
                    if (!sumDone) {
                        after = parseFloat(cellValue);
                        before = parseFloat($.data(calcCell, v.DATA));
                        total = sum[currentPage] + ((isNaN(after) ? 0 : after) - (isNaN(before) ? 0 : before));
                        sum[currentPage] = parseFloat(total).toFixed(5).toDecimal();
                        sumDone = true;
                    }
                    sum[sheet].textContent = sum.formatter === "Currency" ? ti.asCurrency(sum[currentPage]) : sum[currentPage];
                }
                
                if (zeroHidden && ti.isZero(origVal, coord.columnKey)
                    && (cellValue === "" || _.isNil(cellValue) || ti.isZero(cellValue, coord.columnKey))) {
                    $cell = lch.cellAt($grid, coord.rowIdx, coord.columnKey, desc);
                    if (!$cell) {
                        if (!_.isNil(dirties[id]) && !_.isNil(dirties[id][coord.columnKey])) {
                            delete dirties[id][coord.columnKey];
                        }
                        return { c: calcCell };
                    }
                    
                    $cell.classList.remove(color.ManualEditTarget);
                    $cell.classList.remove(color.ManualEditOther);
                    if (main) {
                        color.popState(id, coord.columnKey, [ color.ManualEditTarget, color.ManualEditOther ]);
                    }
                    
                    let initManEdit = $.data($cell, v.INIT_MAN_EDIT);
                    if (initManEdit) {
                        $cell.classList.add(initManEdit);
                        if (main) color.pushState(id, coord.columnKey, [ initManEdit ]);
                    }
                    if (!_.isNil(dirties[id]) && !_.isNil(dirties[id][coord.columnKey])) {
                        delete dirties[id][coord.columnKey];
                    }
                    return { c: calcCell };
                } else {
                    if (cellValue === origVal || ((_.isNil(cellValue) || cellValue === "" || (cellValue instanceof moment && cellValue._i === "")) && ((origVal instanceof moment && origVal._i === "") || _.isNil(origVal) || origVal === "")) 
                        || (cellValue instanceof Date && origVal instanceof Date && !_.isNil(cellValue) && !_.isNil(origVal) && cellValue.getTime() === origVal.getTime())) {
                        $cell = lch.cellAt($grid, coord.rowIdx, coord.columnKey, desc);
                        if (!$cell) {
                            if (!_.isNil(dirties[id]) && !_.isNil(dirties[id][coord.columnKey])) {
                                delete dirties[id][coord.columnKey];
                            }
                            
                            rData[coord.columnKey] = cellValue;
                            return { c: calcCell };
                        }
                        $cell.classList.remove(color.ManualEditTarget);
                        $cell.classList.remove(color.ManualEditOther);
                        if (main) {
                            color.popState(id, coord.columnKey, [ color.ManualEditTarget, color.ManualEditOther ]);
                        }
                        
                        rData[coord.columnKey] = cellValue;
                        let initManEdit = $.data($cell, v.INIT_MAN_EDIT);
                        if (initManEdit) {
                            $cell.classList.add(initManEdit);
                            if (main) {
                                color.pushState(id, coord.columnKey, [ initManEdit ]);
                            }
                        }
                        if (!_.isNil(dirties[id]) && !_.isNil(dirties[id][coord.columnKey])) {
                            delete dirties[id][coord.columnKey];
                        }
                        return { c: calcCell };
                    }
                    
                    if (!dirties[id]) {
                        dirties[id] = {};
                        dirties[id][coord.columnKey] = cellValue;
                    } else {
                        dirties[id][coord.columnKey] = cellValue;
                    }
                    
                    if (!_.find(_linkage, lc => lc === coord.columnKey)) {
                        _.forEach(_linkage, lc => {
                            _$grid.mGrid("updateCell", id, lc, true); 
                        });
                    }
                    
                    rData[coord.columnKey] = cellValue;
                    if (!_.isNil(_objId) && !_.isNil(_getObjId) && _.isFunction(_getObjId)) {
                        let cId = _getObjId(id);
                        let $cell = lch.cellAt($grid, coord.rowIdx, coord.columnKey, desc);
                        if (cId === _objId) {
                            colour = color.ManualEditTarget;
                        } else {
                            colour = color.ManualEditOther;
                        }
                        
                        if (main) {
                            color.popState(id, coord.columnKey, [ color.ManualEditTarget, color.ManualEditOther ]);
                            color.pushState(id, coord.columnKey, [ colour ], true);
                        }
                        if (!$cell) return { colour: colour };
                        $cell.classList.remove(color.ManualEditTarget);
                        $cell.classList.remove(color.ManualEditOther);
                        $cell.classList.add(colour);
                        return { c: calcCell, colour: colour };
                    }
                }
            };
            
            let some = function(arr) {
                let exist = false;
                _.forEach(arr, c => {
                    if (c.group) {
                        exist = some(c.group);
                        if (exist) return false;
                    }
                    
                    if (c.key === coord.columnKey) {
                        exist = true;
                        return false;
                    }
                });
                
                return exist;
            };
            
            let osht = function(inoth) {
                _.forEach(_.keys(_mafollicle[SheetDef]), s => {
                    if (s === _currentSheet) return;
                    let maf = _mafollicle[currentPage][s];
//                    if (maf && _.find(_fixedColumns, fc => fc.key === coord.columnKey)) {
//                        if (maf.zeroHidden && ti.isZero(origVal, coord.columnKey)
//                            && (cellValue === "" || _.isNil(cellValue) || ti.isZero(cellValue, coord.columnKey))
//                            && !_.isNil(maf.dirties[id]) && !_.isNil(maf.dirties[id][coord.columnKey])) {
//                            delete maf.dirties[id][coord.columnKey];
//                        } else if (cellValue === origVal
//                            && !_.isNil(maf.dirties[id]) && !_.isNil(maf.dirties[id][coord.columnKey])) {
//                            delete maf.dirties[id][coord.columnKey];
//                        }
//                        
//                        return;
//                    }
                    
                    if (!some(_mafollicle[SheetDef][s].columns) && !_.find(_fixedColumns, fc => fc.key === coord.columnKey)) return;
                    let t, formatted, disFormat, errDetail;
                    if (maf && maf.desc) {
                        t = transe(s, maf.zeroHidden, maf.dirties, maf.desc);
                        if (!t || !t.c || _.find(_fixedColumns, fc => fc.key === coord.columnKey)) return;
                        let control = dkn.controlType[coord.columnKey];
                        if (control === dkn.LINK_LABEL) {
                            let link = t.c.querySelector("a");
                            link.innerHTML = cellValue;
                        } else if (control === dkn.CHECKBOX) {
                            let check = t.c.querySelector("input[type='checkbox']");
                            if (!check) return;
                            if (cellValue) {
                                check.setAttribute("checked", "checked");
                                check.checked = true;
                                let evt = document.createEvent("HTMLEvents");
                                evt.initEvent("change", false, true);
                                evt.resetValue = reset;
                                evt.checked = cellValue;
                                evt.stopUpdate = true;
                                check.dispatchEvent(evt);
                            } else if (!cellValue) {
                                check.removeAttribute("checked");
                                check.checked = false;
                                let evt = document.createEvent("HTMLEvents");
                                evt.initEvent("change", false, true);
                                evt.resetValue = reset;
                                evt.checked = cellValue;
                                evt.stopUpdate = true;
                                check.dispatchEvent(evt);
                            }
                        } else if (_.isObject(control) && control.type === dkn.COMBOBOX) {
                            let sel = _.find(control.options, o => o.code === cellValue);
                            if (sel) { 
                                $.data(t.c, lo.CBX_SELECTED_TD, cellValue);
                                t.c.textContent = sel.name; 
                            }
                        } else {
                            formatted = !_.isNil(column) ? format(column[0], cellValue) : cellValue;
                            if (ng) {
                                t.c.classList.add(khl.ERROR_CLS);
                                errDetail = _.find(_errors, err => err.index === coord.rowIdx && err.columnKey === coord.columnKey);
                                if (errDetail) {
                                    khl.addCellError(errDetail, maf);
                                    $.data(t.c, "msg", errDetail.message);
                                }
                            }
                            else {
                                t.c.classList.remove(khl.ERROR_CLS);
                                errDetail = _.find(maf.errors, err => err.index === coord.rowIdx && err.columnKey === coord.columnKey);
                                if (errDetail) khl.removeCellError(errDetail.rowId, errDetail.columnKey, maf);
                            }
                            disFormat = cellValue === "" || _.isNil(column) ? cellValue : formatSave(column[0], cellValue);
                            $.data(t.c, v.DATA, disFormat);
                            if (maf.zeroHidden && ti.isZero(disFormat, coord.columnKey)) {
                                t.c.textContent = "";
                            } else {
                                t.c.textContent = formatted;
                            }
                        }
                        
                        if (t.colour) t.c.classList.add(t.colour);
                    } else if (ng) {
                        errDetail = _.find(_errors, err => err.index === coord.rowIdx && err.columnKey === coord.columnKey);
                        if (errDetail) {
                            if (!maf) {
                                _mafollicle[currentPage][s] = { errors: [] };
                                maf = _mafollicle[currentPage][s];
                            } else if (_.isNil(maf.errors)) {
                                maf.errors = [];
                            }
                            
                            khl.addCellError(errDetail, maf);
                        }
                    }
                    
                    if (maf && maf.zeroHidden && ti.isZero(origVal, coord.columnKey)
                        && (cellValue === "" || _.isNil(cellValue) || ti.isZero(cellValue, coord.columnKey))
                        && !_.isNil(maf.dirties[id]) && !_.isNil(maf.dirties[id][coord.columnKey])) {
                        delete maf.dirties[id][coord.columnKey];
                    } else if (maf && cellValue === origVal
                        && !_.isNil(maf.dirties[id]) && !_.isNil(maf.dirties[id][coord.columnKey])) {
                        delete maf.dirties[id][coord.columnKey];
                    } else if (cellValue !== origVal) {
                        if (!maf) {
                            _mafollicle[currentPage][s] = { dirties: {} };
                            maf = _mafollicle[currentPage][s];
                        }
                        
                        if (!maf.dirties) maf.dirties = {};
                        if (!maf.dirties[id]) maf.dirties[id] = {};
                        maf.dirties[id][coord.columnKey] = cellValue;
                    } else if (inoth && cellValue === origVal) {
                        rData[coord.columnKey] = cellValue;
                    }
                });
            };
            
            if (!column) {
                osht(true);
                return;
            }
            
            res = transe(_currentSheet, cmaf.zeroHidden, cmaf.dirties, cmaf.desc, true);
            osht();
            
            return res ? res.colour : null;
        }
        
        /**
         * WedgeShtCell.
         */
        export function wedgeShtCell(rowIdx: any, key: any, value, sht: any) {
            let rd = _dataSource[rowIdx];
            if (!rd) return;
            let stt, id = rd[_pk];
            if (_cellStates && _cellStates[id] && (stt = _cellStates[id][key]) && (stt = stt[0].state)
                && _.find(stt, s => s === color.Disable || s === color.Lock)) return;
             
            let maf = _mafollicle[_currentPage][sht];
            if (maf && maf.desc) {
                let i = maf.desc.colIdxes[key];
                if (_.isNil(i)) return;
                let c = maf.desc.rows[rowIdx][i];
                if (!c) return;
                let retCol = _columnsMap[key];
                if (!retCol) return;
                let formatted = format(retCol[0], value);
                c.textContent = formatted;
                let disFormat = formatSave(retCol[0], value);
                wedgePrelimShtCell(c, rowIdx, key, value, sht);
                $.data(c, v.DATA, disFormat);
            } else {
                wedgePrelimShtCell(null, rowIdx, key, value, sht);
            }
        }
        
        /**
         * WedgePrelimShtCell.
         */
        export function wedgePrelimShtCell($cell: HTMLElement, rowIdx: any, key: any, value: any, sht: any, reset?: any) {
            let res, valueType = hpl.getValueType(_$grid[0], key);
            if (!_.isNil(value) && !_.isEmpty(value)) { 
                if (valueType === "TimeWithDay" || valueType === "Clock") {
                    try {
                        value = time.minutesBased.clock.dayattr.create(
                            time.minutesBased.clock.dayattr.parseString(String(value)).asMinutes).shortText;
                    } catch(e) {}
                } else if (valueType === "Time") {
                    value = nts.uk.time.minutesBased.duration.parseString(String(value)).format();
                }
            }
            
            let rData = _dataSource[rowIdx];
            if (_.isNil(rData)) return;
            let id = rData[_pk];
            
            let origDs = _mafollicle[_currentPage].origDs;
            if (!origDs) return;
            if (reset) {
                origDs[rowIdx][key] = value;
            }
            let origVal = origDs[rowIdx][key];
            let column = _columnsMap[key];
            if (column && _.toLower(column[0].dataType) === "number") {
                value = parseFloat(value);
            }
            
            let before, after, total, sum, ohsht = _mafollicle[_currentPage][sht];
            if (_summaries) {
                sum = _summaries[key]; 
            }
            if (sum && sum.calculator === "Time") {
                after = moment.duration(value);
                before = moment.duration(rData[key]);
                let diff = after.subtract(before);
                sum[_currentPage].add(diff);
                if (sum[sht]) sum[sht].textContent = ti.momentToString(sum[_currentPage]);
            } else if (sum && sum.calculator === "Number") {
                after = parseFloat(value);
                before = parseFloat(rData[key]);
                total = sum[_currentPage] + ((isNaN(after) ? 0 : after) - (isNaN(before) ? 0 : before));
                sum[_currentPage] = total;
                if (sum[sht]) {
                    sum[sht].textContent = sum.formatter === "Currency" ? ti.asCurrency(sum[_currentPage]) : sum[_currentPage];
                }
            }
            
            if (_zeroHidden && ti.isZero(origVal, key)
                && (value === "" || _.isNil(value) || parseFloat(value) === 0)) {
                if (ohsht && !_.isNil(ohsht.dirties[id]) && !_.isNil(ohsht.dirties[id][key])) {
                    delete dirties[id][coord.columnKey];
                }
                
                color.popState(id, key, [ color.ManualEditTarget, color.ManualEditOther ]);
                if (!$cell) return;
                $cell.classList.remove(color.ManualEditTarget);
                $cell.classList.remove(color.ManualEditOther);
            } else {
                if (value === origVal) {
                    if (ohsht && !_.isNil(ohsht.dirties[id]) && !_.isNil(ohsht.dirties[id][key])) {
                        delete ohsht.dirties[id][key];
                    }
                    
                    color.popState(id, key, [ color.ManualEditTarget, color.ManualEditOther ]);
                    if (!$cell) return;
                    $cell.classList.remove(color.ManualEditTarget);
                    $cell.classList.remove(color.ManualEditOther);
                    return;
                }
                
                let dirties;
                if (!ohsht) {
                    _mafollicle[_currentPage][sht] = { dirties: {} };
                    dirties = _mafollicle[_currentPage][sht].dirties;
                    dirties[id] = {};
                } else if (!ohsht.dirties) {
                    _mafollicle[_currentPage][sht].dirties = {};
                    dirties = _mafollicle[_currentPage][sht].dirties;
                    dirties[id] = {};
                } else if (!ohsht.dirties[id]) {
                    _mafollicle[_currentPage][sht].dirties[id] = {};
                    dirties = _mafollicle[_currentPage][sht].dirties;
                } else dirties = _mafollicle[_currentPage][sht].dirties;
                
                dirties[id][key] = value;
                rData[key] = value;
                if (!_.isNil(_objId) && !_.isNil(_getObjId) && _.isFunction(_getObjId)) {
                    let cId = _getObjId(id);
                    if (cId === _objId) {
                        res = color.ManualEditTarget;
                    } else {
                        res = color.ManualEditOther;
                    }
                    
                    color.pushState(id, key, res, true);
                    if (!$cell) return res;
                    $cell.classList.add(res);
                }
            }
        }
        
        /**
         * Format.
         */
        export function format(column: any, value: any, spl?: any) {
            if (util.isNullOrEmpty(_.trim(value))) return value;
            if (column.constraint) {
                let contrainte, valueType, constraint = column.constraint;
                if (constraint.primitiveValue) {
                    contrainte = ui.validation.getConstraint(constraint.primitiveValue);
                    valueType = contrainte && contrainte.valueType;
                } else valueType = constraint.cDisplayType;
                
                if (!_.isNil(value) && value !== "") {
                    if (valueType === "TimeWithDay") {
                        let minutes = time.minutesBased.clock.dayattr.parseString(String(value)).asMinutes;
                        let timeOpts = { timeWithDay: true };
                        let formatter = new text.TimeWithDayFormatter(timeOpts);
                        if (!util.isNullOrUndefined(minutes)) {
                            try {
                                value = formatter.format(minutes);
                            } catch(e) {}
                        }
                    } else if (valueType === "Clock") {
                        let minutes = time.minutesBased.clock.dayattr.parseString(String(value)).asMinutes;
                        let timeOpts = { timeWithDay: false };
                        let formatter = new text.TimeWithDayFormatter(timeOpts);
                        if (!util.isNullOrUndefined(minutes)) {
                            try {
                                value = formatter.format(minutes);
                            } catch(e) {}
                        }
                    } else if (valueType === "Time") {
                        let parsed = uk.time.minutesBased.duration.parseString(String(value));
                        if (parsed.success) value = parsed.format();
                    } else if (valueType === "Currency") { 
                        let currencyOpts: any = new ui.option.CurrencyEditorOption();
                        currencyOpts.grouplength = constraint.groupLength | 3;
                        currencyOpts.decimallength = _.isNil(constraint.decimalLength) ? 0 : constraint.decimalLength;
                        currencyOpts.currencyformat = constraint.currencyFormat ? constraint.currencyFormat : "JPY";
                        let groupSeparator = constraint.groupSeparator || ",";
                        let rawValue = text.replaceAll(String(value), groupSeparator, "");
                        let formatter = new uk.text.NumberFormatter({ option: currencyOpts });
                        let numVal = Number(rawValue);
                        if (!isNaN(numVal)) value = formatter.format(numVal);
                        else value = rawValue;
                    } else if (valueType === "HalfInt") {
                        value = nts.uk.ntsNumber.formatNumber(value, { decimallength: 1 });
                    } else if (valueType === "String" && contrainte && contrainte.maxLength && contrainte.isZeroPadded) {
                        value = uk.text.padLeft(value, '0', parseInt(contrainte.maxLength));
                        if (spl) spl.padded = true;
                    } else if (valueType === "String" && contrainte && contrainte.charType === "Kana") {
                        value = uk.text.hiraganaToKatakana(uk.text.oneByteKatakanaToTwoByte(value));
                        if (spl) spl.toKana = true;
                    } else if (valueType === "String") {
                        if (constraint.primitiveValue === "StampNumber" && contrainte.formatOption && contrainte.formatOption.autoFill) {
                            value = _[contrainte.formatOption.fillDirection === "right" ? "padEnd" : "padStart"](value, contrainte.maxLength, contrainte.formatOption.fillCharacter);
                        }
                    }
                }
            }
            
            return value;
        }
        
        /**
         * Format save.
         */
        export function formatSave(column: any, value: any) {
            if (column.constraint && !util.isNullOrEmpty(value)) {
                let parsed, constraint = column.constraint, constr;
                let valueType = constraint.primitiveValue ? ((constr = ui.validation.getConstraint(constraint.primitiveValue)) && constr.valueType)
                            : constraint.cDisplayType;
                if (!_.isNil(value) && value !== "") {
                    if (valueType === "Time") {
                        parsed = uk.time.minutesBased.duration.parseString(String(value));
                        if (parsed.success) value = parsed.format();
                    } else if (valueType === "TimeWithDay" || valueType === "Clock") {
                        let minutes = time.minutesBased.clock.dayattr.parseString(String(value)).asMinutes;
                        if (_.isNil(minutes)) return value;
                        try {
                            value = time.minutesBased.clock.dayattr.create(minutes).shortText;
                        } catch (e) {}   
                    } else if (valueType === "String") {
                        if (constraint.primitiveValue === "StampNumber" && constr.formatOption && constr.formatOption.autoFill) {
                            value = _[constr.formatOption.fillDirection === "right" ? "padEnd" : "padStart"](value, constr.maxLength, constr.formatOption.fillCharacter);
                        }
                    }
                }
            }
            
            return value;
        }
        
        /**
         * CollerData.
         */
        export function collerData(evt: any) {
            let data;
            let key, keys = _.keys(_selected);
            if (keys.length !== 1 || _selected[keys[0]].length !== 1) return; 
            key = _selected[keys[0]][0];
            let target = lch.cellAt(_$grid[0], keys[0], key);
            if (!target) return; 
            if (window.clipboardData) {
                window.event.returnValue = false;
                data = window.clipboardData.getData("text");
            } else {
                data = evt.clipboardData.getData("text/plain");
            }
            
            if (_mEditor && _mEditor.type === dkn.TEXTBOX) {
                let $editor = dkn.controlType[dkn.TEXTBOX].my;
                let $input = $editor.querySelector("input.medit");
                $input.value = data;
                evt.preventDefault();
                setTimeout(() => $input.focus());
                return;
            }
            
            if (afterCollertar) setTimeout(() => afterCollertar.focus({ preventScroll: true }), 1);
            let formatted, disFormat, coord = ti.getCellCoord(target), col = _columnsMap[coord.columnKey];
            
            let inputRidd = function($t, rowIdx, columnKey, dFormat) {
                if ($t.classList.contains(khl.ERROR_CLS)) return;
                let ridd = _columnsMap[columnKey][0].inputProcess; 
                if (ridd) {
                    let rData = _dataSource[rowIdx];
                    let rId;
                    if (rData) rId = rData[_pk];
                    ridd(rId, columnKey, dFormat, rData).done((sData) => {
                        _.forEach(sData, sd => {
                            let res = _$grid.mGrid("updateCell", sd.id, sd.item, sd.value);
                            if (!_.isNil(res) && res >= 0) {
                                let sht = _.filter(_.keys(_mafollicle[SheetDef]), k => {
                                    if (k === _currentSheet) return;
                                    let sCols = _mafollicle[SheetDef][k].columns;
                                    return _.find(sCols, c => c.key === sd.item);
                                });
                                
                                _.forEach(sht, s => {
                                    wedgeShtCell(res, sd.item, sd.value, s); 
                                });
                            }
                        });
                    });
                }
            };
            
            let collerRidd = function(rowIdx, columnKey, value) {
                let sCol = _specialColumn[columnKey];
                if (sCol) {
                    let cbx = dkn.controlType[sCol];
                    wedgeCell(_$grid[0], { rowIdx: rowIdx, columnKey: sCol }, value);
                    let selectedOpt = _.find(cbx.options, o => o.code === value); 
                    if (!_.isNil(selectedOpt)) {
                        let $cbxCell = lch.cellAt(_$grid[0], rowIdx, sCol);
                        $cbxCell.textContent = selectedOpt ? selectedOpt.name : "";
                        $.data($cbxCell, lo.CBX_SELECTED_TD, value);
                    }
                } else if ((sCol = _specialLinkColumn[columnKey]) && sCol.changed) {
                    let data = _mafollicle[_currentPage].origDs[rowIdx];
                    sCol.changed(columnKey, data[_pk], value, data[columnKey]).done(res => {
                        let $linkCell = lch.cellAt(_$grid[0], rowIdx, sCol.column);
                        if ($linkCell) {
                            $linkCell.querySelector("a").textContent = res;
                            wedgeCell(_$grid[0], { rowIdx: rowIdx, columnKey: sCol.column }, res);
                        }
                        
                        let $t = lch.cellAt(_$grid[0], rowIdx, columnKey);
                        inputRidd($t, rowIdx, columnKey, value);
                    });
                } else {
                    let $t = lch.cellAt(_$grid[0], rowIdx, columnKey); 
                    inputRidd($t, rowIdx, columnKey, value);
                }
            };
            
            if (_copieMode === 0) {
                if (dkn.controlType[coord.columnKey] !== dkn.TEXTBOX || target.classList.contains(color.Disable)
                    || target.classList.contains(color.Lock) || !col || col.length === 0 || data === "null") return;
                let result, validator = _validators[coord.columnKey];
                if (validator) {
                    let idt = _dataSource[coord.rowIdx][_pk];
                    result = validator.probe(data, idt); 
                    let cell = { id: idt, index: coord.rowIdx, columnKey: coord.columnKey, element: target };
                    khl.clear(cell);
                    
                    if (!result.isValid) {
                        khl.set(cell, result.errorMessage);
                    }
                    
                    if (khl._infobulle) {
                        ti.remove(khl._infobulle);
                        dkn.closeDD(khl._infobulle, true);
                    }
                }
                
                formatted = su.format(col[0], data);
                target.innerHTML = formatted;
                disFormat = su.formatSave(col[0], data);
                _histoire.push({ tx: util.randomId(), o: [{ coord: coord, value: _dataSource[coord.rowIdx][coord.columnKey] }]});
                su.wedgeCell(_$grid[0], coord, disFormat, null, !result ? false : !result.isValid);
                $.data(target, v.DATA, disFormat);
                collerRidd(coord.rowIdx, coord.columnKey, disFormat);
                return;
            }
                
            let dataRows = _.map(data.split("\n"), function(row) {
                return row.split("\t");
            });
            
            let rowsCount = dataRows.length;
            if ((dataRows[rowsCount - 1].length === 1 && dataRows[rowsCount - 1][0] === "")
                || (dataRows.length === 1 && dataRows[0].length === 1 
                    && (dataRows[0][0] === "" || dataRows[0][0] === "\r"))) {
                dataRows.pop();
            }
            
            let cArr, e, pointCoord, pointCol, cPoint, rPoint = keys[0], 
                fixedCount = _.keys(_mDesc.fixedColIdxes).length, eIdx = _mDesc.fixedColIdxes[key];
            if (_.isNil(eIdx)) {
                eIdx = _mDesc.colIdxes[key];
                if (!_.isNil(eIdx)) eIdx += fixedCount;
            }
            
            if (_.isNil(eIdx)) return;
            let sess = { tx: util.randomId(), o: [] };
            _.forEach(dataRows, r => {
                cArr = lch.rowAt(_$grid[0], rPoint++);
                cPoint = eIdx;
                if (!cArr) return;
                
                _.forEach(r, (c, i) => {
                    e = cArr[cPoint++];
                    if (e.style.display === "none" && cArr.length > cPoint + 1) e = cArr[cPoint++];
                    if (!e) return false;
                    c = _.trim(c);
                    if (c === "null") return;
                    pointCoord = ti.getCellCoord(e);
                    pointCol = _columnsMap[pointCoord.columnKey];
                    if (dkn.controlType[pointCoord.columnKey] !== dkn.TEXTBOX || e.classList.contains(color.Disable)
                        || e.classList.contains(color.Lock) || !pointCol || pointCol.length === 0) {
                        return;
                    }
                    
                    let result, validator = _validators[pointCoord.columnKey];
                    if (validator) {
                        let idt = _dataSource[pointCoord.rowIdx][_pk];
                        result = validator.probe(c, idt); 
                        let cell = { id: idt, index: pointCoord.rowIdx, columnKey: pointCoord.columnKey, element: e };
                        khl.clear(cell);
                        
                        if (!result.isValid) {
                            khl.set(cell, result.errorMessage);
                        }
                        
                        if (khl._infobulle) {
                            ti.remove(khl._infobulle);
                            dkn.closeDD(khl._infobulle, true);
                        }
                    }
                    
                    formatted = su.format(pointCol[0], c);
                    e.innerHTML = formatted;
                    disFormat = su.formatSave(pointCol[0], c);
                    sess.o.push({ coord: pointCoord, value: _dataSource[pointCoord.rowIdx][pointCoord.columnKey] });
                    su.wedgeCell(_$grid[0], pointCoord, disFormat, null, !result ? false : !result.isValid);
                    $.data(e, v.DATA, disFormat);
                    collerRidd(pointCoord.rowIdx, pointCoord.columnKey, disFormat);
                });
            });
            
            _histoire.push(sess);
        }
        
        /**
         * CopieData.
         */
        export function copieData(coupe?: any) {
            let keys = Object.keys(_selected);
            if (!_selected || keys.length === 0) return;
            let coord, key, struct = "", ds = _dataSource, sess, onetar;
            if (coupe) {
                sess = { tx: util.randomId(), o: [] };
            }
            if (keys.length === 1 && _selected[keys[0]].length === 1) {
                _copieMode = 0;
                key = _selected[keys[0]][0];
                let struct, cell = lch.cellAt(_$grid[0], keys[0], key);
                if (cell.classList.contains(color.Disable) || cell.classList.contains(color.Lock)) {
                    struct = "null";
                } else {
                    struct = ds[parseFloat(keys[0])][key];
                }
                
                if (coupe) {
                    if (cell && !cell.classList.contains(color.Disable) && !cell.classList.contains(color.Lock)) {
                        coord = ti.getCellCoord(cell);
                        cell.innerHTML = "";
                        sess.o.push({ coord: coord, value: _dataSource[coord.rowIdx][coord.columnKey] });
                        su.wedgeCell(_$grid[0], coord, "");
                        $.data(cell, v.DATA, "");
                        _histoire.push(sess);
                    }
                }
                
                if (_copieer) {
                    onetar = document.activeElement;
                    _copieer.value = struct;
                    _copieer.select();
                    document.execCommand("copy");
                    onetar.focus({ preventScroll: true });
                }
                return;
            }
            
            let sortedKeys = keys.sort((o, t) => o - t);
            let fixedCount = 0, colIdx, elms, e, desc = _mDesc, min, max, value;
            _copieMode = 1;
            if (desc.fixedColIdxes) {
                fixedCount = Object.keys(desc.fixedColIdxes).length;
            }
            
            _.forEach(_.keys(_selected), r => {
                let idxArr = _.map(_selected[r], c => {
                    let idx, isFixed = true;
                    if (desc.fixedColIdxes) {
                        idx = desc.fixedColIdxes[c];
                    }
                    
                    if (_.isNil(idx)) {
                        idx = desc.colIdxes[c];
                        isFixed = false;
                    }
                    
                    if (coupe) {
                        sess.o.push({ coord: { rowIdx: r, columnKey: c }, value: _dataSource[r][c] });
                    }
                    return isFixed ? idx : idx + fixedCount;
                });
                
                let minVal = _.min(idxArr);
                let maxVal = _.max(idxArr);
                if (_.isNil(min) || min > minVal) min = minVal; 
                if (_.isNil(max) || max < maxVal) max = maxVal;
            });
            
            if (coupe && sess.o.length > 0) {
                _histoire.push(sess);
            }
            
            for (let i = parseFloat(sortedKeys[0]); i <= parseFloat(sortedKeys[sortedKeys.length - 1]); i++) {
                elms = lch.rowAt(_$grid[0], i);
                for (let c = min; c <= max; c++) {
                    e = elms[c];
                    if (!e || e.style.display === "none") return;
                    coord = ti.getCellCoord(e);
                    value = ds[i][coord.columnKey];
                    if (_.isNil(value) || value === "" || !e.classList.contains(lch.CELL_SELECTED_CLS)
                        || e.classList.contains(color.Disable) || e.classList.contains(color.Lock)) {
                        struct += "null"; 
                    } else { 
                        struct += value;
                        if (coupe) {
                            e.innerHTML = "";
                            su.wedgeCell(_$grid[0], coord, "");
                            $.data(e, v.DATA, "");
                        }
                    }
                    
                    if (c === max) struct += "\n";
                    else struct += "\t";
                } 
            }
            
            if (_copieer) {
                onetar = document.activeElement;
                _copieer.value = struct;
                _copieer.select();
                document.execCommand("copy");
                onetar.focus({ preventScroll: true });
            }
        }
        
        /**
         * Annuler.
         */
        export function annuler() {
            if (!_histoire || _histoire.length === 0) return;
            let sess = _histoire.pop();
            if (sess) {
                let afterAnnulerRidd = function(el, c, column, disFormat) {
                    if (el.classList.contains(khl.ERROR_CLS)) return;
                    let ridd = column[0].inputProcess; 
                    if (ridd) {
                        let rData = _dataSource[parseFloat(c.rowIdx)];
                        let rId;
                        if (rData) rId = rData[_pk];
                        ridd(rId, c.columnKey, disFormat, rData).done((sData) => {
                            _.forEach(sData, sd => {
                                let res = _$grid.mGrid("updateCell", sd.id, sd.item, sd.value);
                                if (!_.isNil(res) && res >= 0) {
                                    let sht = _.filter(_.keys(_mafollicle[SheetDef]), k => {
                                        if (k === _currentSheet) return;
                                        let sCols = _mafollicle[SheetDef][k].columns;
                                        return _.find(sCols, c => c.key === sd.item);
                                    });
                                    
                                    _.forEach(sht, s => {
                                        wedgeShtCell(res, sd.item, sd.value, s); 
                                    });
                                }
                            });
                        });
                    }
                };
                
                sess.o.forEach(c => {
                    let failed, spl = {},
                        el = lch.cellAt(_$grid[0], c.coord.rowIdx, c.coord.columnKey),
                        column = _columnsMap[c.coord.columnKey], 
                        formatted = failed ? c.value : (_zeroHidden && ti.isZero(c.value, c.coord.columnKey) ? "" : format(column[0], c.value, spl));
                    let validator = _validators[c.coord.columnKey];
                    if (validator) {
                        let idt = _dataSource[c.coord.rowIdx][_pk], result = validator.probe(c.value, idt); 
                        let cell = { id: idt, index: c.coord.rowIdx, columnKey: c.coord.columnKey, element: el };
                        khl.clear(cell);
                        
                        if (!result.isValid) {
                            khl.set(cell, result.errorMessage);
                            failed = true;
                        }
                    }
                    
                    el.textContent = formatted;
                    let disFormat = c.value === "" || failed ? c.value : ((spl.padded || spl.toKana) ? formatted : formatSave(column[0], c.value));
                    wedgeCell(_$grid[0], c.coord, disFormat);
                    $.data(el, v.DATA, disFormat);
                    
                    let sCol = _specialColumn[c.coord.columnKey];
                    if (sCol) {
                        let cbx = dkn.controlType[sCol];
                        wedgeCell(_$grid[0], { rowIdx: c.coord.rowIdx, columnKey: sCol }, c.value);
                        let selectedOpt = _.find(cbx.options, o => o.code === c.value); 
                        if (!_.isNil(selectedOpt)) {
                            let $cbxCell = lch.cellAt(_$grid[0], c.coord.rowIdx, sCol);
                            $cbxCell.textContent = selectedOpt ? selectedOpt.name : "";
                            $.data($cbxCell, lo.CBX_SELECTED_TD, c.value);
                        }
                    } else if ((sCol = _specialLinkColumn[c.coord.columnKey]) && sCol.changed) {
                        let data = _mafollicle[_currentPage].origDs[c.coord.rowIdx];
                        sCol.changed(c.coord.columnKey, data[_pk], formatted, data[c.coord.columnKey]).done(res => {
                            let $linkCell = lch.cellAt(_$grid[0], c.coord.rowIdx, sCol.column);
                            if ($linkCell) {
                                $linkCell.querySelector("a").textContent = res;
                                wedgeCell(_$grid[0], { rowIdx: c.coord.rowIdx, columnKey: sCol.column }, res);
                            }
                            
                            afterAnnulerRidd(el, c.coord, column, disFormat);
                        });
                    } else afterAnnulerRidd(el, c.coord, column, disFormat);
                });
            }
        }
    }
    
    module ssk {
        export let SCROLL_EVT = "scroll";
        export let CLICK_EVT = "click";
        export let MOUSE_DOWN = "mousedown";
        export let MOUSE_MOVE = "mousemove";
        export let MOUSE_UP = "mouseup";
        export let MOUSE_OVER = "mouseover";
        export let MOUSE_ENTER = "mouseenter";
        export let MOUSE_OUT = "mouseout";
        export let MOUSE_LEAVE = "mouseleave";
        export let FOCUS_IN = "focusin";
        export let PASTE = "paste";
        export let MOUSE_WHEEL = "wheel";
        export let RESIZE = "resize";
        export let KEY_DOWN = "keydown";
        export let KEY_UP = "keyup";
        export let RENDERED = "mgridrowsrendered";
        export let MS = "mgridms";
        export let MS_BEFORE_COMPL = "mgridmsbeforecompletion";
        export let KeyPressed = {};
        
        window.addXEventListener = document.addXEventListener = Element.prototype.addXEventListener = addEventListener;
        window.removeXEventListener = document.removeXEventListener = Element.prototype.removeXEventListener = removeEventListener;
        
        /**
         * Trigger.
         */
        export function trigger($target: HTMLElement, eventName: string, args?: any) {
            let event;
            if (window.CustomEvent) {
                event = new CustomEvent(eventName, { detail: args });
            } else {
                event = document.createEvent('CustomEvent');
                event.initCustomEvent(eventName, true, true, args);
            }
            $target.dispatchEvent(event);
        }
        
        /**
         * Add event listener.
         */
        function addEventListener(event, cb, opts) {
            let self = this;
            if (!self.ns) self.ns = {};
            if (!self.ns[event]) self.ns[event] = [ cb ];
            else self.ns[event].push(cb);
            self.addEventListener(event.split(".")[0], cb, opts);  
        };
        
        /**
         * Remove event listener.
         */
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
    }
    
    export module gp {
        export const PAGING_CLS = "mgrid-paging";
        export const SHEET_CLS = "mgrid-sheet";
        export const PAGE_HEIGHT = 44;
        export const SHEET_HEIGHT = 45;
        export let $sheetArea;
        
        /**
         * ImiPages.
         */
        export function imiPages($container: HTMLElement, top: any, width: any, load: any) {
            if (!_paging) return;
            let $pageArea = v.createWrapper(top + ti.getScrollWidth() + SUM_HEIGHT + "px", 0, 
                { width: parseFloat(width) + ti.getScrollWidth() + "px", height: PAGE_HEIGHT + "px", containerClass: PAGING_CLS });
            $container.appendChild($pageArea);
            let $recDesc = document.createElement("span");
            $recDesc.classList.add("mgrid-pagerecordlabel");
            $recDesc.textContent = _pageSize + " レコード";
            $pageArea.appendChild($recDesc);
            let $gridPaging = _prtDiv.cloneNode();
            $gridPaging.classList.add("mgrid-paging-nav");
            $pageArea.appendChild($gridPaging);
            let $firstPage = _prtDiv.cloneNode();
            $firstPage.classList.add("mgrid-firstpage");
            $firstPage.classList.add("mgrid-paging-item");
            $firstPage.classList.add("ui-state-default");
            $gridPaging.appendChild($firstPage);
            let $arrowStopImg = document.createElement("span");
            $arrowStopImg.classList.add("mgrid-pageimg");
            $arrowStopImg.classList.add("ui-icon");
            $arrowStopImg.classList.add("ui-icon-arrowstop-1-w");
            $firstPage.appendChild($arrowStopImg);
            
            let $buttons = document.createElement("ul");
            $buttons.classList.add("mgrid-page-buttonlist");
            $gridPaging.appendChild($buttons);
            
            let pageList = _.filter(Object.keys(_mafollicle), p => p !== SheetDef).sort((p1, p2) => parseFloat(p1) - parseFloat(p2));
            $firstPage.addXEventListener(ssk.CLICK_EVT, evt => {
                if (parseInt(pageList[0]) === _currentPage) return;
                let btns = $buttons.querySelectorAll("li");
                _.forEach(btns, li => {
                    if (li.classList.contains("ui-state-active")) {
                        li.classList.remove("ui-state-active");
                    }
                });
                
                lungeto(0);
                btns[0].classList.add("ui-state-active");
                if (_.isFunction(load)) load(1);
            });
            
            _.forEach(pageList, p => {
                if (p !== SheetDef) {
                    let $pageBtn = document.createElement("li");
                    $pageBtn.classList.add("mgrid-page-button");
                    $pageBtn.classList.add("ui-state-default");
                    $pageBtn.textContent = parseInt(p) + 1;
                    if (parseInt(p) === _currentPage) $pageBtn.classList.add("ui-state-active");
                    $pageBtn.addXEventListener(ssk.CLICK_EVT, evt => {
                        if ($pageBtn.classList.contains("ui-state-active")) return;
                        lungeto(parseInt(p));
                        _.forEach($buttons.querySelectorAll("li"), li => {
                            if (li.classList.contains("ui-state-active")) {
                                li.classList.remove("ui-state-active");
                            }
                        });
                        
                        $pageBtn.classList.add("ui-state-active");
                        if (_.isFunction(load)) load(parseInt(p) + 1);
                    });
                    
                    $buttons.appendChild($pageBtn);
                }
            });
            
            let $lastPage = _prtDiv.cloneNode();
            $lastPage.classList.add("mgrid-lastpage");
            $lastPage.classList.add("mgrid-paging-item");
            $lastPage.classList.add("ui-state-default");
            $gridPaging.appendChild($lastPage);
            $lastPage.addXEventListener(ssk.CLICK_EVT, evt => {
                if (pageList.length - 1 === _currentPage) return;
                let btns = $buttons.querySelectorAll("li");
                _.forEach(btns, li => {
                    if (li.classList.contains("ui-state-active")) {
                        li.classList.remove("ui-state-active");
                    }
                });
                
                lungeto(pageList.length - 1);
                btns[btns.length - 1].classList.add("ui-state-active");
                if (_.isFunction(load)) load(pageList.length);
            });
            
            let $arrowStopEImg = document.createElement("span");
            $arrowStopEImg.classList.add("mgrid-pageimg");
            $arrowStopEImg.classList.add("ui-icon");
            $arrowStopEImg.classList.add("ui-icon-arrowstop-1-e");
            $lastPage.appendChild($arrowStopEImg);
        }
        
        /**
         * ImiSheets.
         */
        export function imiSheets($container: HTMLElement, top: any, width: any) {
            if (!_sheeting) return;
            $sheetArea = v.createWrapper(top + ti.getScrollWidth() + SUM_HEIGHT + "px", 0, 
                { width: parseFloat(width) + ti.getScrollWidth() + "px", height: SHEET_HEIGHT + "px", containerClass: SHEET_CLS });
            $container.appendChild($sheetArea);
            let $scrollBar = document.createElement("ul");
            $scrollBar.classList.add("mgrid-sheet-scrollbar");
            $sheetArea.appendChild($scrollBar);
            let $up = document.createElement("li");
            $up.className = "ui-icon-triangle-1-n ui-icon";
            $scrollBar.appendChild($up);
            let $down = document.createElement("li");
            $down.className = "ui-icon-triangle-1-s ui-icon";
            $scrollBar.appendChild($down);
            let $gridSheet = _prtDiv.cloneNode();
            $gridSheet.classList.add("mgrid-sheet-nav");
            $sheetArea.appendChild($gridSheet);
            let $buttons = document.createElement("ul");
            $buttons.classList.add("mgrid-sheet-buttonlist");
            $gridSheet.appendChild($buttons);
            
            _.forEach(Object.keys(_mafollicle[SheetDef]), s => {
                let $btn = document.createElement("li");
                $btn.classList.add("mgrid-sheet-button");
//                $btn.classList.add("ui-state-default");
                $btn.textContent = _mafollicle[SheetDef][s].text;
                if (s === _currentSheet) $btn.classList.add("ui-state-active");
                $btn.addXEventListener(ssk.CLICK_EVT, evt => {
                    if ($btn.classList.contains("ui-state-active")
                        || !_dataSource || _dataSource.length === 0) return;
                    hopto(s);
                    _.forEach($buttons.querySelectorAll("li"), li => {
                        if (li.classList.contains("ui-state-active")) {
                            li.classList.remove("ui-state-active");
                        }
                    });
                    
                    $btn.classList.add("ui-state-active");
                });
                
                $buttons.appendChild($btn);
            });
            
            let sheetNav = $($gridSheet);
            $up.addXEventListener(ssk.MOUSE_DOWN, evt => {
                sheetNav.scrollTop(sheetNav.scrollTop() - SHEET_HEIGHT);
            });
            
            $down.addXEventListener(ssk.MOUSE_DOWN, evt => {
                sheetNav.scrollTop(sheetNav.scrollTop() + SHEET_HEIGHT);
            });
        }
        
        /**
         * Lungeto.
         */
        export function lungeto(index: any) {
            let sheetDef = _mafollicle[SheetDef][_currentSheet];
//            _mafollicle[_currentPage].dataSource = _.cloneDeep(_dataSource);
            _currentPage = index;
            _dataSource = _mafollicle[_currentPage].dataSource;
            v._voilerRows = _mafollicle[_currentPage].voilRows;
            _hr = null;
            lch.clearAll(_$grid[0]);
            
            _.filter(_bodyWrappers, w => w.classList.contains(FREE))[0].scrollTop = 0;
            
            if (!_vessel()) {
                _mafollicle[_currentPage][_currentSheet] = { errors: [], desc: {}, dirties: {}, zeroHidden: _zeroHidden, selected: {}, histoire: [] };
            }
            
            _mDesc = _vessel().desc;
            _errors = _vessel().errors;
            _dirties = _vessel().dirties;
            _selected = _vessel().selected;
            _histoire = _vessel().histoire;
            let sum, res = _cloud.renderRows(true);
            ti.calcTotal();
            _.forEach(_.keys(_summaries), k => {
                sum = _summaries[k];
                if (!sum[_currentSheet]) return;
                if (sum.calculator === "Number") {
                    sum[_currentSheet].textContent = sum.formatter === "Currency" ? ti.asCurrency(sum[_currentPage]) : sum[_currentPage]; 
                } else if (sum.calculator === "Time") {
                    sum[_currentSheet].textContent = ti.momentToString(sum[_currentPage]);
                }
            });
            
            if (!res) {
                let tmp = _zeroHidden;
                _zeroHidden = _vessel().zeroHidden;
                _vessel().zeroHidden = tmp;
                if (lo.changeZero(_vessel().zeroHidden)) _zeroHidden = _vessel().zeroHidden;
                return;
            }
            
            let start = res.start, end = res.end, cursor;
            if (_.isNil(_mDesc) || Object.keys(_mDesc).length === 0) {
                $.data(_$grid[0], lo.DESC, _mDesc);
                if (!_.isNil(res.fixedColIdxes) || !_.isNil(res.colIdxes)) {
                    _mDesc.fixedColIdxes = res.fixedColIdxes;
                    _mDesc.colIdxes = res.colIdxes;
                } 
                
                _mDesc.fixedRows = [];
                _mDesc.rows = [];
                _mDesc.fixedRowElements = [];
                _mDesc.rowElements = [];
            }
            
            for (let i = start; i <= end; i++) {
                cursor = i - start;
                if (!_mDesc.fixedRows[i]) {
                    _mDesc.fixedRows[i] = res.fixedRows[cursor];
                    _mDesc.fixedRowElements[i] = res.fixedRowElements[cursor];
                }
                _mDesc.rows[i] = res.rows[cursor];
                _mDesc.rowElements[i] = res.rowElements[cursor];
            }
            
            let tmp = _zeroHidden;
            _zeroHidden = _vessel().zeroHidden;
            _vessel().zeroHidden = tmp;
            if (lo.changeZero(_vessel().zeroHidden)) _zeroHidden = _vessel().zeroHidden;
        }
        
        /**
         * Hopto.
         */
        export function hopto(place: any) {
            let bfPainter;
            if (_currentSheet === place) return;
            if (_hasFixed) {
                bfPainter = _.cloneDeep(_mafollicle[SheetDef][_currentSheet].painters[0]);
                if (_summaries) {
                    _.forEach(_fixedColumns, c => {    
                        let sum = _summaries[c.key];
                        if (!sum || !sum[_currentSheet]) return;
                        sum[place] = sum[_currentSheet];    
                    });
                }
            }
            _currentSheet = place;
            
            if (!_vessel()) {
                let desc = { 
                        fixedColIdxes: _.cloneDeep(_mDesc.fixedColIdxes), 
                        fixedRows: _.cloneDeep(_mDesc.fixedRows),
                        fixedRowElements: _.cloneDeep(_mDesc.fixedRowElements),
                        colIdxes: [],
                        rows: [],
                        rowElements: []
                };
                
                let dirties = {}, selected = {};
                if (_selected) {
                    _.forEach(_.keys(_selected), r => {
                        let selectArr = _.filter(_selected[r], c => _.some(_fixedColumns, fc => fc.key === c));
                        if (selectArr.length > 0) {
                            selected[r] = selectArr;
                        }
                    });
                }
                
                if (_dirties) {
                    _.forEach(_.keys(_dirties), r => {
                        let cols = {};
                        _.forEach(_.keys(_dirties[r]), c => {
                            if (_.some(_fixedColumns, fc => fc.key === c)) {
                                cols[c] = _dirties[r][c];
                            }
                        });
                        
                        if (_.keys(cols).length > 0) {
                            dirties[r] = cols;
                        }
                    });
                }
                _mafollicle[_currentPage][_currentSheet] = { desc: desc, errors: [], dirties: dirties, zeroHidden: _zeroHidden, selected: selected, histoire: [] };
            } else if (!_vessel().desc) {
                let desc = { 
                        fixedColIdxes: _.cloneDeep(_mDesc.fixedColIdxes), 
                        fixedRows: _.cloneDeep(_mDesc.fixedRows),
                        fixedRowElements: _.cloneDeep(_mDesc.fixedRowElements),
                        colIdxes: [],
                        rows: [],
                        rowElements: []
                };
                
                let selected = {};
                if (_selected) {
                    _.forEach(_.keys(_selected), r => {
                        let selectArr = _.filter(_selected[r], c => _.some(_fixedColumns, fc => fc.key === c));
                        if (selectArr.length > 0) {
                            selected[r] = selectArr;
                        }
                    });
                }
                
                if (_dirties && _vessel().dirties) {
                    _.forEach(_.keys(_dirties), r => {
                        let cols = {};
                        _.forEach(_.keys(_dirties[r]), c => {
                            if (_.some(_fixedColumns, fc => fc.key === c)) {
                                cols[c] = _dirties[r][c];
                            }
                        });
                        
                        if (_.keys(cols).length > 0) {
                            _vessel().dirties[r] = cols;
                        }
                    });
                }
                
                _vessel().desc = desc;
                _vessel().selected = selected;
                _vessel().zeroHidden = _zeroHidden;
                if (!_vessel().errors) _vessel().errors = [];
                _vessel().histoire = [];
            } else {
                _.forEach(_.keys(_selected), r => {
                    _.forEach(_selected[r], c => {
                        if (_.some(_fixedColumns, fc => fc.key === c)) {
                            if (!_vessel().selected[r]) {
                                _vessel().selected[r] = [ c ];
                            } else {
                                _vessel().selected[r].push(c);
                            }
                        }
                    });
                });
                
                _.forEach(_.keys(_dirties), r => {
                    _.forEach(_.keys(_dirties[r]), c => {
                        if (_.some(_fixedColumns, fc => fc.key === c)) {
                            if (!_vessel().dirties[r]) {
                                _vessel().dirties[r] = {};
                            }
                            _vessel().dirties[r][c] = _dirties[r][c];
                        }
                    });
                });
            }
            
            _mDesc = _vessel().desc;
            _errors = _vessel().errors;
            _dirties = _vessel().dirties;
            _selected = _vessel().selected;
            _histoire = _vessel().histoire;
            let $header = _$grid[0].querySelector("." + FREE + "." + HEADER);
            let $headerTbl = $header.querySelector("table");
            let bhGroup = $header.querySelector("colgroup");
            let bhBody = $header.querySelector("tbody");
            let dTable = _bodyWrappers[1].querySelector("table");
            let bbGroup = dTable.querySelector("colgroup");
            let sumGroupArr, sumTbl, bSumGroup, bSumBody, sumWrap = _$grid[0].querySelector("." + FREE + "-summaries");
            if (sumWrap) {
                sumTbl = sumWrap.querySelector("table");
                bSumGroup = sumWrap.querySelector("colgroup");
                bSumBody = sumWrap.querySelector("tbody");
            }
            let sWrap = _$grid[0].querySelector("." + gp.SHEET_CLS);
            let pWrap = _$grid[0].querySelector("." + gp.PAGING_CLS);
            
            if (!_vessel().$hGroup) {
                kt.turfSurf(_cstifle(), null, _$grid.mGrid("option", "widthMem"));
                _header.columns = _cstifle();
                let $wrapper = v.createWrapper("0px", _leftAlign, _header, true);
                _mafollicle[SheetDef][_currentSheet].maxWidth = _maxFreeWidth;
                $header.style.maxWidth = _maxFreeWidth + "px";
                let bw = (_maxFreeWidth + ti.getScrollWidth()) + "px";
                _bodyWrappers[1].style.maxWidth = bw;
                let btmw = (Math.min(parseFloat($header.style.width), parseFloat($header.style.maxWidth)) + _maxFixedWidth + ti.getScrollWidth() + 2) + "px";
                if (sumWrap) {
                    sumWrap.style.maxWidth = _maxFreeWidth + "px";
                    sumWrap.style.width = $header.style.width;
                }
                
                if (sWrap) {
                    sWrap.style.width = btmw;
                }
                
                if (pWrap) {
                    pWrap.style.width = btmw;
                }
                $wrapper.classList.add(HEADER);
                let table = v.process($wrapper, _header);
                table.$table.style.height = _header.height;
                let hGroup = $wrapper.querySelector("colgroup");
                let hBody = $wrapper.querySelector("tbody"); 
                $headerTbl.replaceChild(hGroup, bhGroup);
                $headerTbl.replaceChild(hBody, bhBody);
                _vessel().$hGroup = hGroup;
                _vessel().$hBody = hBody;
                _mafollicle[SheetDef][_currentSheet].hColArr = table.cols; 
                let artifactOptions = { primaryKey: _pk, controlMap: table.controlMap,  
                    columns: _header.columns, features: _features, hasSum: _hasSum };
                _mafollicle[SheetDef][_currentSheet].controlMap = _.assignIn(_fixedControlMap, table.controlMap);
                let painters = _hasFixed ? [ bfPainter, table.painter ] : [ table.painter ];
                _mafollicle[SheetDef][_currentSheet].painters = painters;
                let colGroup = document.createElement("colgroup");
                let bodyGroupArr = [];
                _.forEach(table.cols, c => {
                    let col = c.cloneNode();
                    colGroup.appendChild(col);
                    bodyGroupArr.push(col);
                });
                _vessel().$bGroup = colGroup;
                _mafollicle[SheetDef][_currentSheet].bColArr = bodyGroupArr;
                dTable.replaceChild(colGroup, bbGroup);
                _mafollicle[SheetDef][_currentSheet].levelStruct = _.cloneDeep(_header.levelStruct); 
                _cloud.painter.revive();
                _cloud.sidePainter.revive();
                v.construe(_$grid[0], _bodyWrappers, artifactOptions, true, _vessel().errors);
                _vessel().$bBody = dTable.querySelector("tbody");
                
                if (sumWrap) {
                    let $sumBody = document.createElement("tbody");
                    sumGroupArr = [], $sumGroup = document.createElement("colgroup");
                    let $tr = document.createElement("tr");
                    $tr.style.height = "27px";
                    $sumBody.appendChild($tr);
                    
                    _.forEach(table.cols, c => {
                        let col = c.cloneNode(true);
                        $sumGroup.appendChild(col);
                        sumGroupArr.push(col);
                    });
                    
                    _.forEach(table.painter.visibleColumns, c => {
                        if (c.hidden) return;
                        let sum = _summaries[c.key]; 
                        let $td = _prtCell.cloneNode();
                        $tr.appendChild($td);
                        
                        if (!sum) return;
                        if (sum.calculator === "Time") {
                            $td.textContent = ti.momentToString(sum[_currentPage]);
                            sum[_currentSheet] = $td;
                        } else if (sum.calculator === "Number") {
                            $td.textContent = sum.formatter === "Currency" ? ti.asCurrency(sum[_currentPage]) : sum[_currentPage];
                            sum[_currentSheet] = $td;
                        } else {
                            $td.textContent = sum.calculator;
                        }
                    });
                    
                    sumTbl.replaceChild($sumGroup, bSumGroup);
                    sumTbl.replaceChild($sumBody, bSumBody);
                    _vessel().$sumGroup = $sumGroup;
                    _vessel().$sumBody = $sumBody;
                    _mafollicle[SheetDef][_currentSheet].sumColArr = sumGroupArr;
                }
                
                if (kt._adjuster) {
                    kt._adjuster.nostal(table.cols, bodyGroupArr, sumGroupArr);
                    kt._adjuster.handle(); 
                }
                
                let tmp = _vessel().zeroHidden;
                _vessel().zeroHidden = _zeroHidden;
                _zeroHidden = tmp;
                if (lo.changeZero(_vessel().zeroHidden)) _zeroHidden = _vessel().zeroHidden;         
                return;
            }
            
            _maxFreeWidth = _mafollicle[SheetDef][_currentSheet].maxWidth;
            $header.style.maxWidth = _maxFreeWidth + "px";
            let bw = (_maxFreeWidth + ti.getScrollWidth()) + "px";
            _bodyWrappers[1].style.maxWidth = bw;
            let btmw = (Math.min(parseFloat($header.style.width), parseFloat($header.style.maxWidth)) + _maxFixedWidth + ti.getScrollWidth() + 2) + "px";
            if (sumWrap) {
                sumWrap.style.maxWidth = _maxFreeWidth + "px";
                sumWrap.style.width = $header.style.width;
            }
            
            if (sWrap) {
                sWrap.style.width = btmw;
            }
            
            if (pWrap) {
                pWrap.style.width = btmw;
            }
            _cloud.painter.revive();
            _cloud.sidePainter.revive();
            $headerTbl.replaceChild(_vessel().$hGroup, bhGroup);
            $headerTbl.replaceChild(_vessel().$hBody, bhBody);
            dTable.replaceChild(_vessel().$bGroup, bbGroup);
//            dTable.replaceChild(_vessel().$bBody, dTable.querySelector("tbody"));
            _cloud.renderSideRows(true);
            _vessel().$bBody = dTable.querySelector("tbody");
            if (sumWrap) {
                sumTbl.replaceChild(_vessel().$sumGroup, bSumGroup);
                sumTbl.replaceChild(_vessel().$sumBody, bSumBody);
            }
            if (kt._adjuster) {
                kt._adjuster.nostal(_mafollicle[SheetDef][_currentSheet].hColArr, 
                    _mafollicle[SheetDef][_currentSheet].bColArr, _mafollicle[SheetDef][_currentSheet].sumColArr);
                kt._adjuster.handle();
            }
            
            let tmp = _vessel().zeroHidden;
            _vessel().zeroHidden = _zeroHidden;
            _zeroHidden = tmp;
            if (lo.changeZero(_vessel().zeroHidden)) _zeroHidden = _vessel().zeroHidden;
        }
    }
    
    export module dkn {
        export let LABEL: string = 'Label';
        export let LINK_LABEL: string = 'LinkLabel';
        export let CHECKBOX: string = 'CheckBox';
        export let SWITCH_BUTTONS: string = 'SwitchButtons';
        export let COMBOBOX: string = 'ComboBox'; 
        export let BUTTON: string = 'Button';
        export let DELETE_BUTTON = 'DeleteButton';
        export let REFER_BUTTON = 'ReferButton';
        export let DATE_PICKER = 'DatePicker';
        export let TEXTBOX = 'TextBox';
        export let TEXT_EDITOR = 'TextEditor';
        export let FLEX_IMAGE = 'FlexImage';
        export let IMAGE = 'Image';
        export let HEIGHT_CONTROL = "27px";
        export let controlType = {};
        export let allCheck = {};
        export let _ramass = {};
        
        export const CONTROL_CLS = "nts-control";
        export const LABEL_CLS = "mlabel";
        export const CBX_CLS = "mcombo";
        export const CBX_ACTIVE_CLS = "mcombo-state-active";
        export const PICKER_HIDE = "datepicker-hide";
        export const PICKER_PANEL = "datepicker-panel";
        export const MUTED = "muted";
        export const PICKED = "picked";
        export const YM = "YYYY年MM月";
        export const Y = "YYYY年";
        export const WEEK_DAYS = toBeResource.weekDaysShort;

        /**
         * Get control.
         */
        export function getControl(name: string): any {
            switch (name) {
                case TEXTBOX:
                    return textBox();
                case CHECKBOX:
                    return checkBox;
//                case SWITCH_BUTTONS:
//                    return new SwitchButtons;
                case COMBOBOX:
                    return comboBox;
                case BUTTON:
                    return button;
                case DELETE_BUTTON:
                    return deleteButton;
                case REFER_BUTTON:
                    return refButton;
                case DATE_PICKER:
                    return ramass;
//                case TEXT_EDITOR:
//                    return new TextEditor;
                case LINK_LABEL:
                    return linkLabel;
                case FLEX_IMAGE:
                    return flexImage;
                case IMAGE:
                    return image;
            }
        }
        
        /**
         * Textbox.
         */
        export function textBox(key: string) {
            let control = controlType[TEXTBOX];
            if (!_.isNil(key)) controlType[key] = TEXTBOX;
            if (control) {
                return;
            }
            let $editContainer = document.createElement("div"); 
            $editContainer.classList.add("medit-container");
            $editContainer.style.height = (BODY_ROW_HEIGHT - 3) + "px";
            let $editor = document.createElement("input");
            $editor.classList.add("medit");
            $editContainer.appendChild($editor);
            controlType[TEXTBOX] = { my: $editContainer, type: TEXTBOX };
            $editor.addXEventListener(ssk.KEY_DOWN, evt => {
                if (ti.isEnterKey(evt) || ti.isTabKey(evt)) {
                    if (!_(ssk.KeyPressed).keys().filter(k => k !== "13" && k !== "9").size()) {
                        let d = ti.closest($editor, "td." + v.CELL_CLS);
                        let coord = ti.getCellCoord(d), ctrl = controlType[coord.columnKey];
                        if (!ctrl || ctrl.type !== DATE_PICKER) {
                            su.endEdit(_$grid[0]);
                            return;
                        }
                        
                        let data = $.data(d, v.DATA);
                        if (data instanceof Date) {
                            data = data.toLocaleDateString("ja-JP", { year: "numeric", month: "2-digit", day: "2-digit" });
                        }
                        
                        let tDate = moment.utc($editor.value, ctrl.format, true);
                        if (/*data !== tDate && !d.classList.contains(khl.ERROR_CLS) &&*/ _.isFunction(ctrl.inputProcess)) {
                            ctrl.inputProcess(tDate.isValid() ? tDate.format(ctrl.format[0]) : $editor.value, _dataSource[coord.rowIdx]);
                        }
                        su.endEdit(_$grid[0]);
                    }
                } else if (!evt.ctrlKey && ((evt.keyCode >= 46 && evt.keyCode <= 111) || (evt.keyCode >= 160 && evt.keyCode <= 223))) {
                    ssk.KeyPressed[evt.keyCode] = true;
                }
                
                if (ti.isArrowLeft(evt) || ti.isArrowRight(evt) || ti.isArrowUp(evt) || ti.isArrowDown(evt)) {
                    evt.stopPropagation();
                }
            });
            
            let ms = function() {
                let $td = ti.closest($editor, "td." + v.CELL_CLS);
                if ($td) {
                    let coord = ti.getCellCoord($td);
                    let validator = _validators[coord.columnKey];
                    if (!validator) return;
                    let idt = _dataSource[coord.rowIdx][_pk], result = validator.probe($editor.value, idt); 
                    let cell = { id: idt, index: coord.rowIdx, columnKey: coord.columnKey, element: $td };
                    khl.clear(cell);
                    
                    if (result && !result.isValid) {
                        khl.set(cell, result.errorMessage);
                        return;
                    }
                    
                    if (khl._infobulle) {
                        ti.remove(khl._infobulle);
                        dkn.closeDD(khl._infobulle, true);
                    }
                    
                    return $td;
                }
            };
            
            $editor.addXEventListener(ssk.KEY_UP, evt => {
                delete ssk.KeyPressed[evt.keyCode];
                ms();
            });
            
            $editor.addXEventListener(ssk.MS_BEFORE_COMPL, () => {
                ms();
            });
            
            $editor.addXEventListener(ssk.MS, evt => {
                let d = ms();
                if (!d || !evt.detail || !evt.detail[0]) return;
                let data = $.data(d, v.DATA);
                if (data instanceof Date) {
                    data = data.toLocaleDateString("ja-JP", { year: "numeric", month: "2-digit", day: "2-digit" });
                }
                
                let tDate = moment.utc($editor.value, evt.detail[1]).format(evt.detail[1][0]); 
                if (data !== tDate) {
                    evt.detail[0]();
                }
            });
        }

        /**
         * Checkbox.
         */
        function checkBox(data: any): HTMLElement {
            let checkBoxText: string;
            let setChecked = data.update;
            let initValue = data.initValue;
            let $wrapper = document.createDocumentFragment();
            let text = data.controlDef.options[data.controlDef.optionsText];
            checkBoxText = text;
            
            let $checkBoxLabel = document.createElement("label");
            $checkBoxLabel.classList.add("ntsCheckBox");
            let $checkBox = document.createElement("input");
            $checkBox.setAttribute("type", "checkbox");
            $checkBox.addXEventListener("change", function(evt) {
                let checked = $checkBox.checked || evt.checked ? true : false;
                let allCheckKey = dkn.allCheck[data.columnKey];
                if (checked) {
                    $checkBox.setAttribute("checked", "checked");
                    if (allCheckKey) {
                        if (!_.isNil(allCheckKey.count)) allCheckKey.count++;
                        if (allCheckKey.cb && allCheckKey.overall === allCheckKey.count) {
                            allCheckKey.stt = true;
                            allCheckKey.cb.checked = true;
                            allCheckKey.cb.setAttribute("checked", "checked");
                        }
                    }
                } else {
                    $checkBox.removeAttribute("checked");
                    if (allCheckKey) {
                        if (!_.isNil(allCheckKey.count)) allCheckKey.count--;
                        if (allCheckKey.cb && allCheckKey.cb.checked) {
                            allCheckKey.stt = false;
                            allCheckKey.cb.checked = false;
                            allCheckKey.cb.removeAttribute("checked");
                        }
                    }
                }
                
                let r = ti.closest($checkBox, "tr");
                if (r && !evt.stopUpdate) {
                    setChecked(checked, parseFloat($.data(r, lo.VIEW)), evt.resetValue, evt.pg);
                }
            });
            
            $checkBoxLabel.appendChild($checkBox);
            let $box = document.createElement("span");
            $box.classList.add("box");
            $checkBoxLabel.appendChild($box);
            
            if (checkBoxText && checkBoxText.length > 0) {
                let span = document.createElement("span");
                span.classList.add("label");
                span.innerHTML = checkBoxText;
                $checkBoxLabel.appendChild(span);
            }
            
            $wrapper.appendChild($checkBoxLabel);
            let checked = initValue !== undefined ? initValue : true;

            if (checked === true) $checkBox.setAttribute("checked", "checked");
            else $checkBox.removeAttribute("checked");
            if (data.enable === true) $checkBox.removeAttribute("disabled");
            else $checkBox.setAttribute("disabled", "disabled");
            if (!controlType[data.columnKey]) {
                controlType[data.columnKey] = CHECKBOX;
            }
            
            return $wrapper;
        }
        
        /**
         * Combobox.
         */
        export function comboBox(data: any): HTMLElement {
            let result = {}, control = controlType[data.columnKey];
            result[data.controlDef.optionsValue] = null;
            result[data.controlDef.optionsText] = "";
            if (control) {
                let options, panel, listType;
                if (data.controlDef.list && !_.isNil(listType = data.controlDef.list[data.rowId])) {
                    options = data.controlDef.pattern[listType];
                    panel = control.panel[listType + 1];
                } else {
                    options = data.controlDef.options;
                    panel = control.panel[0];
                }
                
                if (!panel) {
                    result = stuffList(data, control.my, control.dropdown);
                } else {
                    _.forEach(options, i => {
                        let val = i[data.controlDef.optionsValue];
                        if (val === data.initValue) {
                            result[data.controlDef.optionsValue] = val;
                            result[data.controlDef.optionsText] = i[data.controlDef.optionsText];
    //                        result = createItem(_prtDiv, val, i[data.controlDef.optionsText]);
                            return false;
                        }
                    });
                }
                
                return result;
            }
            
            let comboDiv = document.createElement("div");
            let $comboWrapper = comboDiv.cloneNode();
            $comboWrapper.classList.add("mcombo-wrapper");
            if (!data.controlDef.width) {
                $comboWrapper.style.width = "100%";
//                $comboWrapper.style.top = "-1px";
            } else {
                $comboWrapper.style.width = data.controlDef.width;
            }
            let $combo = comboDiv.cloneNode();
            $combo.classList.add(CBX_CLS);
            $combo.classList.add("ui-state-default");
            $comboWrapper.appendChild($combo);
            
            let $comboBtn = comboDiv.cloneNode();
            $comboBtn.classList.add("mcombo-button");
            $comboBtn.classList.add("ui-state-default");
            let $comboBtnIcon = comboDiv.cloneNode();
            $comboBtnIcon.classList.add("mcombo-buttonicon");
            $comboBtnIcon.classList.add("ui-icon-triangle-1-s");
            $comboBtnIcon.classList.add("ui-icon");
            $comboBtn.appendChild($comboBtnIcon);
            
            let $comboValue = comboDiv.cloneNode();
            $comboValue.classList.add("mcombo-value");
            $combo.appendChild($comboBtn);
            $combo.appendChild($comboValue);
            
            let $comboDropdown = comboDiv.cloneNode();
            $comboDropdown.classList.add("mcombo-dropdown");
            document.body.appendChild($comboDropdown);
            
            $combo.addXEventListener(ssk.CLICK_EVT, function(evt) {
                if ($comboDropdown.style.height === "" || $comboDropdown.style.height === "0px") {
                    openDD($comboDropdown, $comboWrapper);
                    $combo.classList.add(CBX_ACTIVE_CLS);
                } else {
                    closeDD($comboDropdown);
                    $combo.classList.remove(CBX_ACTIVE_CLS);
                }
            });
            
            let $comboList = comboDiv.cloneNode();
            $comboList.classList.add("mcombo-list");
            $comboDropdown.appendChild($comboList);
            controlType[data.columnKey] = { my: $comboWrapper, dropdown: $comboDropdown, options: data.controlDef.options, panel: [], 
                optionsValue: data.controlDef.optionsValue, optionsText: data.controlDef.optionsText, maxHeight: [], 
                optionsList: data.controlDef.pattern, optionsMap: data.controlDef.list, type: COMBOBOX };
            result = stuffList(data, $comboWrapper, $comboDropdown);
            
            return result;
        }
        
        export function stuffList(data: any, $comboWrapper: any, $comboDropdown: any) {
            let $itemHolder = document.createElement("ul");
            $itemHolder.classList.add("mcombo-listitemholder");
            let val, textContent, maxHeight = 0, itemList = [], controlDef = data.controlDef, result = {},
                listType, options, panelz;
            result[controlDef.optionsValue] = null;
            result[controlDef.optionsText] = "";
            if (data.controlDef.list && !_.isNil(listType = data.controlDef.list[data.rowId])) {
                options = controlDef.pattern[listType];
                panelz = listType + 1;
            } else {
                options = controlDef.options;
                panelz = 0;
            }
            
            _.forEach(options, i => {
                let $item = document.createElement("li");
                $item.classList.add("mcombo-listitem");
                $item.classList.add("ui-state-default");
                val = i[controlDef.optionsValue];
                $.data($item, "value", val);
//                textContent = text.padRight(val, ' ', 6) + i[controlDef.optionsText];
//                $item.textContent = textContent;
                // Create columns
                let $comboItem = createItem(val, i[controlDef.optionsText], $item, controlDef.displayMode),
                    $comboValue = $comboWrapper.querySelector(".mcombo-value");
                
                if (data.initValue === val) {
                    let initItem = $comboItem.cloneNode(true);
                    $comboValue.appendChild(initItem);
                    $item.classList.add("selecteditem");
                    result[controlDef.optionsValue] = val;
                    result[controlDef.optionsText] = i[controlDef.optionsText];
                }
                
                $item.addXEventListener(ssk.CLICK_EVT, evt => {
                    let $combo = $comboWrapper.querySelector("." + CBX_CLS);
                    $comboValue.innerHTML = "";
                    $comboValue.appendChild($comboItem.cloneNode(true));
                    _.forEach(itemList, i => {
                        if (i.classList.contains("selecteditem")) {
                            i.classList.remove("selecteditem");
                        } 
                    });
                    
                    let value = $.data($item, lo.CBX_ITEM_VALUE);
                    $.data($comboWrapper, lo.CBX_SELECTED, value); 
                    $item.classList.add("selecteditem");
                    let $cbxCell = ti.closest($comboWrapper, "." + v.CELL_CLS);
                    let coord = ti.getCellCoord($cbxCell);
                    if ($cbxCell) {
                        let bVal = $.data($cbxCell, lo.CBX_SELECTED_TD);
                        if (bVal !== value && _.isFunction(controlDef.inputProcess)) {
                            let rowObj = _dataSource[coord.rowIdx];
                            controlDef.inputProcess(value, rowObj && rowObj[_pk], rowObj);
                        }
                        $.data($cbxCell, lo.CBX_SELECTED_TD, value);
                    }
                    closeDD($comboDropdown);
                    $combo.classList.remove(CBX_ACTIVE_CLS);
                    data.update(value, coord.rowIdx);
                    khl.clear({ id: _dataSource[coord.rowIdx][_pk], columnKey: coord.columnKey, element: $cbxCell });
                    let sCol = _specialColumn[data.columnKey];
                    if (sCol) {
                        let $cCell = lch.cellAt(_$grid[0], coord.rowIdx, sCol);
                        if ($cCell) {
                            let column = _columnsMap[sCol];
                            let formatted = su.format(column[0], value); 
                            $cCell.textContent = formatted;
                            su.wedgeCell(_$grid[0], { rowIdx: coord.rowIdx, columnKey: sCol },  value);
                            $.data($cCell, v.DATA, value);
                            khl.clear({ id: _dataSource[coord.rowIdx][_pk], columnKey: sCol, element: $cCell });
                        }
                    }
                });
                
                $itemHolder.appendChild($item);
                itemList.add($item);
                maxHeight += 26;
            });
            
            if (!options || options.length === 0) {
                $itemHolder.innerHTML = nts.uk.resource.getMessage("Msg_37");
                $itemHolder.style.background = "#f6f6f6";
                maxHeight += 26;
            }
            
            let control = controlType[data.columnKey];
            control.panel[panelz] = $itemHolder;
            control.maxHeight[panelz] = Math.min(104, maxHeight);
            
            if (!$comboDropdown.querySelector(".mcombo-listitemholder")) {
                let $comboList = $comboDropdown.querySelector(".mcombo-list");
                $comboList.appendChild($itemHolder);
                let max = Math.min(104, maxHeight) + "px";
                $comboDropdown.style.maxHeight = max; //(maxHeight + 2) + "px";
                $comboList.style.maxHeight = max;
            }
            
            return result;
        }
        
        /**
         * OpenDD.
         */
        export function openDD($dd, $w, f) {
            let offset = selector.offset($w), adj = !f ? 2 : -1;
            $dd.style.top = (offset.top + BODY_ROW_HEIGHT - adj) + "px";
            $dd.style.left = offset.left + "px";
            if (selector.is($dd, ".mcombo-dropdown")) {
                let list = $dd.querySelector(".mcombo-list");
                let items = list.querySelectorAll("li");
                if (items) {
                    let top = 0, selected;
                    _.forEach(items, li => {
                        if (li.classList.contains("selecteditem")) {
                            selected = true;
                            return false;
                        }
                        
                        top += 26;
                    });
                    
                    if (top >= 0 && selected) list.scrollTop = top;
                    else list.scrollTop = 0;
                }
            }
            
            if (!f) {
                $dd.style.width = $w.offsetWidth + "px";
                $dd.style.height = $dd.style.maxHeight;
            }
        };
        
        /**
         * CloseDD.
         */
        export function closeDD($dd, f) {
            if (!f) $dd.style.height = "0px";
//            setTimeout(() => {
                $dd.style.top = "-99999px";
                $dd.style.left = "-99999px";
//            }, 120);
        }
        
        /**
         * Create item.
         */
        export function createItem(code: any, name: any, $item?: HTMLElement, mode?: any) {
            let $comboItem = _prtDiv.cloneNode();
            $comboItem.classList.add("mcombo-item");
            if ($item) $item.appendChild($comboItem);
            if (mode === "codeName") {
                let $itemCode = _prtDiv.cloneNode();
                $itemCode.classList.add("mcombo-item-column");
                $itemCode.style.width = "25%";
                $itemCode.style.float = "left";
                $itemCode.textContent = code;
                $comboItem.appendChild($itemCode);
            }
            
            let $itemName = _prtDiv.cloneNode();
            $itemName.classList.add("mcombo-item-column");
            $itemName.style.width = mode === "codeName" ? "75%" : "100%";
            $itemName.textContent = name;
            $comboItem.appendChild($itemName);
            return $comboItem;
        }
        
        /**
         * Ramass
         */
        function ramass(data: any): HTMLElement {
            let format = _.toLower(data.controlDef.format), formats = ti.dateFormat(format);
            
            if (!controlType[data.columnKey]) {
                controlType[data.columnKey] = { type: DATE_PICKER, format: formats, formatType: format, inputProcess: data.controlDef.inputProcess };
            }
            
            if (_ramass[format]) {
                if (!data.initValue || data.initValue === "") return "";
                let momentObj = moment(data.initValue, formats, true);
                return momentObj.isValid() ? momentObj.format(formats[0]) : data.initValue;
            }
            
            _ramass[format] = _prtDiv.cloneNode();
            _ramass[format].classList.add("mdatepicker-container");
            _ramass[format].classList.add("mdatepicker-dropdown");
            document.body.appendChild(_ramass[format]);
            let $yearsPick = _prtDiv.cloneNode(),
                $monthsPick = _prtDiv.cloneNode(),
                $daysPick = _prtDiv.cloneNode();
            $yearsPick.classList.add(PICKER_PANEL);
            $yearsPick.setAttribute("data-view", "years picker");
            let ul = document.createElement("ul"), li = document.createElement("li"),
                $yearsNav = ul.cloneNode(), $years = ul.cloneNode();
            $yearsPick.appendChild($yearsNav);
            let $yearsPrev = li.cloneNode(), $yearsCurrent = li.cloneNode(), $yearsNext = li.cloneNode(),
                $monthsNav, $months, $yearPrev, $yearCurrent, $yearNext;
            $yearsPrev.setAttribute("data-view", "years prev");
            $yearsPrev.innerHTML = "‹";
            $yearsPrev.addXEventListener(ssk.MOUSE_DOWN, evt => {
                let mDate = $.data(_ramass[format], "date");
                mDate.subtract(10, "y");
                ssk.trigger(_ramass[format], "set", [ mDate, 1, 2 ]);
                evt.stopPropagation();
            });
            $yearsNav.appendChild($yearsPrev);
            $yearsCurrent.setAttribute("data-view", "years current");
            $yearsCurrent.classList.add("disabled");
            $yearsNav.appendChild($yearsCurrent);
            $yearsNext.setAttribute("data-view", "years next");
            $yearsNext.innerHTML = "›";
            $yearsNext.addXEventListener(ssk.MOUSE_DOWN, evt => {
                let mDate = $.data(_ramass[format], "date");
                mDate.add(10, "y");
                ssk.trigger(_ramass[format], "set", [ mDate, 1, 2 ]);
                evt.stopPropagation();
            });
            $yearsNav.appendChild($yearsNext);
            $years.setAttribute("data-view", "years");
            $yearsPick.appendChild($years);
            for (let i = 0; i < 12; i++) {
                let $year = li.cloneNode();
                $year.setAttribute("data-view", "year");
                $year.addXEventListener(ssk.MOUSE_DOWN, evt => {
                    let value = $.data($year, "value"),
                        $input = dkn.controlType[dkn.TEXTBOX].my.querySelector("input.medit");
                    evt.stopPropagation();
                    
                    if (format === "y") {                       
                        $input.value = value;
                        let pickerType = controlType[_mEditor.columnKey];
                        if (!pickerType) return;
                        ssk.trigger($input, ssk.MS, [ _.isFunction(pickerType.inputProcess) && pickerType.inputProcess.bind(null, value, _dataSource[_mEditor.rowIdx]), pickerType.format ]);
                        su.endEdit(_$grid[0]);
                        return;
                    }
                    
                    let mDate = $.data(_ramass[format], "date");
                    mDate.year(value);
                    $input.value = mDate.format(formats[0]);
                    ssk.trigger($input, ssk.MS);
                    $yearsPick.classList.add(PICKER_HIDE);
                    $monthsPick.classList.remove(PICKER_HIDE);
                    ssk.trigger(_ramass[format], "set", [ mDate, 0, 1 ]);
                });
                
                $years.appendChild($year);
                if (i == 0 || i == 11) {
                    $year.classList.add("muted");
                }
            }
            
            if (format !== "y") {
                $yearsPick.classList.add(PICKER_HIDE);
            }
            _ramass[format].appendChild($yearsPick);
            
            if (format === "ym" || format === "ymd") {
                $monthsPick.classList.add(PICKER_PANEL);
                $monthsPick.setAttribute("data-view", "months picker"); 
                $monthsNav = ul.cloneNode(); $months = ul.cloneNode();
                $monthsPick.appendChild($monthsNav);
                $monthsPick.appendChild($months);
                $yearPrev = li.cloneNode(); $yearCurrent = li.cloneNode(); $yearNext = li.cloneNode();
                $yearPrev.setAttribute("data-view", "year prev");
                $yearPrev.innerHTML = "‹";
                $yearPrev.addXEventListener(ssk.MOUSE_DOWN, evt => {
                    let mDate = $.data(_ramass[format], "date");
                    mDate.subtract(1, "y");
                    ssk.trigger(_ramass[format], "set", [ mDate, 1, 1 ]);
                    evt.stopPropagation();
                });
                $monthsNav.appendChild($yearPrev);
                $yearCurrent.setAttribute("data-view", "year current");
                $yearCurrent.addXEventListener(ssk.MOUSE_DOWN, evt => {
                    $monthsPick.classList.add(PICKER_HIDE);
                    $yearsPick.classList.remove(PICKER_HIDE);
                    let mDate = $.data(_ramass[format], "date");
                    ssk.trigger(_ramass[format], "set", [ mDate, 1, 2 ]);
                    evt.stopPropagation();
                });
                $monthsNav.appendChild($yearCurrent);
                $yearNext.setAttribute("data-view", "year next");
                $yearNext.innerHTML = "›";
                $yearNext.addXEventListener(ssk.MOUSE_DOWN, evt => {
                    let mDate = $.data(_ramass[format], "date");
                    mDate.add(1, "y");
                    ssk.trigger(_ramass[format], "set", [ mDate, 1, 1 ]);
                    evt.stopPropagation();
                });
                $monthsNav.appendChild($yearNext);
                $months.setAttribute("data-view", "months");
                
                for (let i = 1; i < 13; i++) {
                    let $month = li.cloneNode();
                    $month.setAttribute("data-view", "month");
                    $month.innerHTML = i + "月";
                    $month.addXEventListener(ssk.MOUSE_DOWN, evt => {
                        let value = $.data($month, "value"),
                            $input = dkn.controlType[dkn.TEXTBOX].my.querySelector("input.medit");
                        evt.stopPropagation();
                        let mDate = $.data(_ramass[format], "date");
                        mDate.month(value - 1);
                        
                        if (format === "ym") {
                            $input.value = mDate.format(formats[0]);
                            let pickerType = controlType[_mEditor.columnKey];
                            if (!pickerType) return;
                            ssk.trigger($input, ssk.MS, [ _.isFunction(pickerType.inputProcess) && pickerType.inputProcess.bind(null, $input.value, _dataSource[_mEditor.rowIdx]), pickerType.format ]); 
                            su.endEdit(_$grid[0]);
                            return;
                        }
                        
                        $input.value = mDate.format(formats[0]);
                        ssk.trigger($input, ssk.MS);
                        $monthsPick.classList.add(PICKER_HIDE);
                        $daysPick.classList.remove(PICKER_HIDE);
                        ssk.trigger(_ramass[format], "set", [ mDate, 0 ]);
                    });
                    
                    $.data($month, "value", i);
                    $months.appendChild($month);
                }
                
                if (format === "ymd") {
                    $monthsPick.classList.add(PICKER_HIDE);
                }
                _ramass[format].appendChild($monthsPick);
            }    
                
            if (format === "ymd") {
                $daysPick.classList.add(PICKER_PANEL);
                $daysPick.setAttribute("data-view", "days picker");
                _ramass[format].appendChild($daysPick);
                let $daysNav = ul.cloneNode(), $week = ul.cloneNode(), $days = ul.cloneNode();
                $week.setAttribute("data-view", "week");
                $days.setAttribute("data-view", "days");
                $daysPick.appendChild($daysNav);
                $daysPick.appendChild($week);
                $daysPick.appendChild($days);
                let $monthPrev = li.cloneNode(), $monthCurrent = li.cloneNode(), $monthNext = li.cloneNode();
                $monthPrev.setAttribute("data-view", "month prev");
                $monthPrev.innerHTML = "‹";
                $monthPrev.addXEventListener(ssk.MOUSE_DOWN, evt => {
                    let mDate = $.data(_ramass[format], "date");
                    if (mDate) {
                        ssk.trigger(_ramass[format], "set", [ mDate.subtract(1, "M"), 1 ]);
                        evt.stopPropagation();
                    }
                });
                $monthCurrent.setAttribute("data-view", "month current");
                $monthCurrent.addXEventListener(ssk.MOUSE_DOWN, evt => {
                    $daysPick.classList.add(PICKER_HIDE);
                    $monthsPick.classList.remove(PICKER_HIDE);
                    let mDate = $.data(_ramass[format], "date");
                    ssk.trigger(_ramass[format], "set", [ mDate, 1, 1]);
                    evt.stopPropagation();
                });
                $monthNext.setAttribute("data-view", "month next");
                $monthNext.innerHTML = "›";
                $monthNext.addXEventListener(ssk.MOUSE_DOWN, evt => {
                    let mDate = $.data(_ramass[format], "date");
                    if (mDate) {
                        ssk.trigger(_ramass[format], "set", [ mDate.add(1, "M"), 1 ]);
                        evt.stopPropagation();
                    }
                });
                $daysNav.appendChild($monthPrev);
                $daysNav.appendChild($monthCurrent);
                $daysNav.appendChild($monthNext);
                _.forEach(WEEK_DAYS, d => {
                    let $day = li.cloneNode();
                    $day.innerHTML = d;
                    $week.appendChild($day);
                });
                
                for (let i = 0; i < 42; i++) {
                    let $day = li.cloneNode();
                    $days.appendChild($day);
                    $day.addXEventListener(ssk.MOUSE_DOWN, evt => {
                        let value = $.data($day, "value"),
                            $input = dkn.controlType[dkn.TEXTBOX].my.querySelector("input.medit"),
                            mDate = $.data(_ramass[format], "date"), view = $day.getAttribute("data-view");
                        evt.stopPropagation();
                        if (_.includes(view, "prev")) {
                            mDate.subtract(1, "M");
                            mDate.date(value);
                            $input.value = mDate.format(formats[0]);
                            ssk.trigger($input, ssk.MS);
                            ssk.trigger(_ramass[format], "set", [ mDate ]);
                        } else if (_.includes(view, "next")) {
                            mDate.add(1, "M");
                            mDate.date(value);
                            $input.value = mDate.format(formats[0]);
                            ssk.trigger($input, ssk.MS);
                            ssk.trigger(_ramass[format], "set", [ mDate ]);
                        } else {
                            mDate.date(value);
                            $input.value = mDate.format(formats[0]);
                            let pickerType = controlType[_mEditor.columnKey];
                            ssk.trigger($input, ssk.MS, [ _.isFunction(pickerType.inputProcess) && pickerType.inputProcess.bind(null, $input.value, _dataSource[_mEditor.rowIdx]), pickerType.format ]);
                            su.endEdit(_$grid[0]);
                        }
                    });
                }
            }
            
            closeDD(_ramass[format], true);
            _ramass[format].addXEventListener("set", evt => {
                let mDisplayDate = evt.detail[0], onlyDisplay = evt.detail[1], board = evt.detail[2];
                if (!onlyDisplay) $.data(_ramass[format], "dateSet", mDisplayDate.clone());
                if (board > 1) {
                    let mDateSet = $.data(_ramass[format], "dateSet"),
                        begin = mDisplayDate.year() - 5, end;
                    _.forEach($years.querySelectorAll("li"), (li, i) => {
                        end = begin + i;
                        li.innerHTML = end;
                        $.data(li, "value", end);
                        if (mDateSet.year() === end) {
                            li.classList.add(PICKED);
                            li.setAttribute("data-view", "year picked");
                        } else li.classList.remove(PICKED);
                    });
                    $yearsCurrent.innerHTML = begin + "年 - " + end + "年";
                } else if (board) {
                    let mDateSet = $.data(_ramass[format], "dateSet");
                    $yearCurrent.innerHTML = mDisplayDate.format(Y);
                    if (!mDateSet) return;
                    _.forEach($months.querySelectorAll("li"), li => {
                        if (li.classList.contains(PICKED)) {
                            li.classList.remove(PICKED);
                            li.setAttribute("data-view", "month");
                        }
                    });
                    
                    if (mDateSet.year() === mDisplayDate.year()) {
                        let li = $months.querySelector("li:nth-of-type(" + (mDateSet.month() + 1) + ")");
                        if (li) {
                            li.classList.add(PICKED);
                            li.setAttribute("data-view", "month picked");
                        }
                    }
                } else {
                    let days = ti.daysBoard(mDisplayDate);
                    let $dayItems = $days.querySelectorAll("li"), raise = 0;
                    _.forEach($dayItems, ($d, i) => {
                        if (days[i] === 1) raise++;
                        $d.innerHTML = days[i];
                        $.data($d, "value", days[i]);
                        if (!raise) {
                            $d.classList.remove(dkn.PICKED);
                            $d.classList.add(dkn.MUTED);
                            $d.setAttribute("data-view", "day prev");
                        } else if (raise > 1) {
                            $d.classList.remove(dkn.PICKED);
                            $d.classList.add(dkn.MUTED);
                            $d.setAttribute("data-view", "day next");
                        } else if (days[i] === mDisplayDate.date()) {
                            $d.classList.remove(dkn.MUTED);
                            if (!onlyDisplay || $.data(_ramass[format], "dateSet").month() === mDisplayDate.month()) {
                                $d.classList.add(dkn.PICKED);
                            }
                            $d.setAttribute("data-view", "day picked");
                        } else {
                            $d.classList.remove(dkn.MUTED);
                            $d.classList.remove(dkn.PICKED);
                            $d.setAttribute("data-view", "day");
                        }
                    });
                    
                    $monthCurrent.innerHTML = mDisplayDate.format(YM);
                }
                
                $.data(_ramass[format], "date", mDisplayDate);
            });
            
            if (data.initValue && data.initValue !== "") {
                let momentObj = moment(data.initValue, formats, true); 
                return momentObj.isValid() ? momentObj.format(formats[0]) : data.initValue;
            }
            
            return "";
        }

        /**
         * Button.
         */
        function button(data: any, click?: any): HTMLElement {
            let $container = document.createDocumentFragment();
            let $button = document.createElement("button");
            $button.classList.add("mbutton");
            $container.appendChild($button);
            $button.textContent = data.controlDef.text || data.initValue;
            $button.setAttribute("tabindex", -1)
            $.data($button, "enable", data.enable);
            let clickHandle = click ? click.bind(null) : data.controlDef.click.bind(null, data.rowObj, data.rowId, data.columnKey);
            $.data($button, ssk.CLICK_EVT, clickHandle);
            if (data.enable) $button.addXEventListener("click", clickHandle);
            $button.disabled = !data.enable;
            if (!controlType[data.columnKey]) {
                controlType[data.columnKey] = BUTTON;
            }
            
            return $container;
        }
        
        /**
         * Delete button.
         */
        function deleteButton(data: any): HTMLElement {
            let btnContainer = button(data);
            let btn = btnContainer.querySelector("button");
            btn.removeXEventListener("click", data.controlDef.click);
            btn.addXEventListener("click", data.deleteRow);
            return btnContainer;
        }
        
        /**
         * Refer button.
         */
        function refButton(data: any) {
            let click, text, item;
            if (data.controlDef.pattern && data.controlDef.list) {
                let itemList = data.controlDef.pattern[data.controlDef.list[data.rowId]];
                click = (arg) => {
                    let td = ti.closest(arg.target, "td");
                    if (!td) return;
                    let txt = td.querySelector(".mgrid-refer-text");
                    if (!txt) return;
                    let args = { value: $.data(td, v.DATA), rowId: data.rowId, rowValue: data.rowObj, itemList: data.controlDef.pattern[data.controlDef.list[data.rowId]],
                                relatedItemList: (nama) => {
                                    let ctrl = _mafollicle[SheetDef][_currentSheet].controlMap && _mafollicle[SheetDef][_currentSheet].controlMap[nama];
                                    if (ctrl && ctrl.pattern && ctrl.list) {
                                        return ctrl.pattern[ctrl.list[data.rowId]];
                                    }            
                                } 
                        };
                    
                    if (data.controlDef.click) {
                        data.controlDef.click(args);
                    }
                };
                
                if (itemList && data.controlDef.optionsValue) {
                    item = _.find(itemList, i => i[data.controlDef.optionsValue] === data.initValue);
                    if (item) {
                        text = item[data.controlDef.optionsText];
                    }
                }
            } else {
                click = (arg) => {
                    let td = ti.closest(arg.target, "td");
                    if (!td) return;
                    let txt = td.querySelector(".mgrid-refer-text");
                    if (!txt) return;
                    if (data.controlDef.click) {
                        data.controlDef.click({ value: $.data(td, v.DATA), rowId: data.rowId, rowValue: data.rowObj });
                    }
                };
            }
            
            let btnContainer = button(data, click);
            controlType[data.columnKey] = REFER_BUTTON;
            let selected = _prtDiv.cloneNode(true);
            selected.classList.add("mgrid-refer-text");
            if (_.isNil(text) && !_.isNil(data.initValue)) {
                text = _.isNil(data.controlDef.notFound) ? data.initValue : data.controlDef.notFound;
            }
            
            selected.textContent = text || "";
            if (data.controlDef.labelPosition === "before") {
                btnContainer.insertBefore(selected, btnContainer.querySelector("button"));
                selected.classList.add("mg-right-margin");
            } else {
                btnContainer.appendChild(selected);
            }
            return btnContainer;
        }
            
        /**
         * LinkLabel.
         */
        function linkLabel(data: any): HTMLElement {
            let $container = document.createDocumentFragment();
            let $link = document.createElement("a");
            $link.classList.add("mlink-button");
            $link.textContent = data.initValue;
            let clickHandle = data.controlDef.click.bind(null, data.rowId, data.columnKey);
            
            if (data.enable) {
                $link.addXEventListener("click", clickHandle);
            } else $link.style.color = "#333";
            $.data($link, "click", clickHandle);
            $container.appendChild($link);
            
            if (!controlType[data.columnKey]) {
                controlType[data.columnKey] = LINK_LABEL;
            }
            
            return $container;
        }
            
        /**
         * FlexImage.
         */
        function flexImage(data: any): HTMLElement {
            let $container = document.createDocumentFragment();
            if (_.isNil(data.initValue) || _.isEmpty(data.initValue)) {
                if (!controlType[data.columnKey]) {
                    controlType[data.columnKey] = FLEX_IMAGE;
                }
                return $container;
            }
            let $image = document.createElement("span");
            $image.className = data.controlDef.source;
            
            if (data.controlDef.click && _.isFunction(data.controlDef.click)) {
                let clickHandle = data.controlDef.click.bind(null, data.columnKey, data.rowId);
                if (data.enable) {
                    $image.addXEventListener(ssk.CLICK_EVT, clickHandle);
                    $image.style.cursor = "pointer";
                }
                $.data($image, ssk.CLICK_EVT, clickHandle);
            }
            
            $container.appendChild($image);
            if (!controlType[data.columnKey]) {
                controlType[data.columnKey] = FLEX_IMAGE;
            }
            
            return $container;
        }
            
        /**
         * Image.
         */
        function image(data: any): HTMLElement {
            let $container = document.createDocumentFragment();
            let $span = document.createElement("span");
            $span.className = data.controlDef.source;
            $container.appendChild($span);
            
            let clickHandle;
            if (data.controlDef.source === "hidden-button") {
                clickHandle = evt => {
                    let r = ti.closest($span, "tr");
                    if (r) {
                        let view = parseFloat($.data(r, lo.VIEW));
                        v.voilerRow(view);
                        if (_.isFunction(data.controlDef.click)) {
                            data.controlDef.click(view);    
                        }
                    }
                };
            } else if (_.includes(data.controlDef.source, "plus-button")) {
                clickHandle = evt => {
                    let r = ti.closest($span, "tr");
                    let noTd = r.querySelector("td." + v.STT_CLS);
                    if (r) v.encarterRow(parseFloat($.data(r, lo.VIEW)), data.controlDef.copy, data.controlDef.cssClass, noTd && noTd.innerHTML && parseInt(noTd.innerHTML) + 1);
                };    
            }
            
            if (clickHandle) {
                if (data.enable) {
                    $span.addXEventListener(ssk.CLICK_EVT, clickHandle);
                    $span.style.cursor = "pointer";
                }
                
                $.data($span, ssk.CLICK_EVT, clickHandle);
            }   
            
            if (!controlType[data.columnKey]) {
                controlType[data.columnKey] = IMAGE;
            }
            
            return $container;
        }
    }
    
    module lch {
        
        export const CELL_SELECTED_CLS = "cell-selected";
        
        export class Cell {
            rowIndex: any;
            columnKey: any;
            value: any;
            
            constructor(rowIdx: any, columnKey: any, value: any) {
                this.rowIndex = rowIdx;
                this.columnKey = columnKey;
                this.value = value;
            }
        }
        
        /**
         * Checkup.
         */
        export function checkUp($grid: HTMLElement) {
            let isSelecting;
            
            $grid.addXEventListener(ssk.MOUSE_DOWN, function(evt: any) {
                let $target = evt.target;
                if (!selector.is($target, ".mcell")
                    || _.chain(ssk.KeyPressed).keys().filter(k => k !== "16" && k !== "17").value().length > 0) return;
                isSelecting = true;
                
                window.addXEventListener(ssk.MOUSE_UP + ".block", function(evt: any) {
                    isSelecting = false;
                    $grid.onselectstart = null;
                    window.removeXEventListener(ssk.MOUSE_UP + ".block");
                });
                
                if (evt.shiftKey) {
                    selectRange($grid, $target);
                    $grid.onselectstart = function() {
                        return false;
                    };
                    return;
                }
                
                if (!evt.ctrlKey) {
                    clearAll($grid);
                }
                selectCell($grid, $target);
            });
            
            $grid.addXEventListener(ssk.MOUSE_UP, function(evt: any) {
                isSelecting = false;
                $grid.onselectstart = null;
            });
            
            $grid.addXEventListener(ssk.MOUSE_MOVE, function(evt: any) {
                if (isSelecting) {
                    selectRange($grid, evt.target);
                }
            });
        }
        
        /**
         * Select range.
         */
        function selectRange($grid: HTMLElement, $cell: HTMLElement) {
            if (_.isNil($cell) || !selector.is($cell, "td.mcell")) return;
            let lastSelected = $.data($grid, lo.LAST_SELECT);
            if (!lastSelected) { 
                selectCell($grid, $cell);
                return;
            }
            
            clearAll($grid);
            let toCoord = ti.getCellCoord($cell); 
            let minRowIdx = Math.min(lastSelected.rowIdx, toCoord.rowIdx);
            let maxRowIdx = Math.max(lastSelected.rowIdx, toCoord.rowIdx);
            for (let i = minRowIdx; i < maxRowIdx + 1; i++) { 
                cellInRange($grid, i, lastSelected.columnKey, toCoord.columnKey);
            }
        }
        
        /**
         * Mark cell.
         */
        export function markCell($cell: HTMLElement) {
            if ($cell && selector.is($cell, "td.mcell")) {
                $cell.classList.add(CELL_SELECTED_CLS);
                return true;
            }
            return false;
        }
        
        /**
         * Select cell.
         */
        export function selectCell($grid: HTMLElement, $cell: HTMLElement, notLast?: boolean) {
            if (!markCell($cell)) return;
            let coord = ti.getCellCoord($cell);
            addSelect($grid, coord.rowIdx, coord.columnKey, notLast);
            
            if (ti.isChrome() && (!_fixedColumns || !_.some(_fixedColumns, c => c.key === coord.columnKey))) {
                if (!_bodyWrappers || _bodyWrappers.length === 0) return;
                $grid.focus({ preventScroll: true });
                let wrapper = _bodyWrappers[_bodyWrappers.length > 1 ? 1 : 0];
                let offsetLeft = $cell.offsetLeft, left = offsetLeft + $cell.offsetWidth,
                    scrollLeft = wrapper.scrollLeft, width = scrollLeft  + parseFloat(wrapper.style.width); 
//                let scroll = function() {
//                    wrapper.addXEventListener(ssk.SCROLL_EVT + ".select", e => {
//                        setTimeout(() => {
//                            $cell.focus();
//                        }, 100);
//                        wrapper.removeXEventListener(ssk.SCROLL_EVT + ".select");
//                    });
//                };
                
                if (left > width) {
//                    scroll();
                    wrapper.scrollLeft += (left - width + 100);
                } else if (offsetLeft < scrollLeft) {
//                    scroll();
                    wrapper.scrollLeft -= (scrollLeft - offsetLeft + 100)
                } else {
                    $cell.focus({ preventScroll: true });
                }
            } else $cell.focus();
        }
        
        /**
         * Add select.
         */
        export function addSelect($grid: HTMLElement, rowIdx: any, columnKey: any, notLast?: boolean) {
            let selectedCells = _selected;
            if (!notLast) $.data($grid, lo.LAST_SELECT, { rowIdx: rowIdx, columnKey: columnKey });
            if (!selectedCells) {
                selectedCells = {};
                selectedCells[rowIdx] = [ columnKey ];
                _selected = selectedCells;
                return;
            }
            
            if (!selectedCells[rowIdx]) {
                selectedCells[rowIdx] = [ columnKey ];
                return;
            }
            
            if (_.find(selectedCells[rowIdx], function(key) {
                return key === columnKey;
            }) === undefined) {
                selectedCells[rowIdx].push(columnKey);
            }
        }
        
        /**
         * Clear.
         */
        export function clear($grid: HTMLElement, rowIdx: any, columnKey: any) {
            let selectedCells = _selected;
            if (!selectedCells) return;
            let row = selectedCells[rowIdx];
            if (!row || row.length === 0) return;
            let colIdx;
            _.forEach(row, function(key: any, index: number) {
                if (key === columnKey) {
                    colIdx = index;
                    return false;
                }
            });
            
            if (_.isNil(colIdx)) return;
            row.splice(colIdx, 1);
            let selectedCell = cellAt($grid, rowIdx, columnKey);
            if (selectedCell === null) return;
            if (selectedCell) {
                ti.removeClass(selectedCell, CELL_SELECTED_CLS);
            }
        }
        
        /**
         * Clear all.
         */
        export function clearAll($grid: HTMLElement) {
            let selectedCells = _selected;
            if (!selectedCells) return;
            _.forEach(Object.keys(selectedCells), function(rowIdx: any, index: number) {
                _.forEach(selectedCells[rowIdx], function(col: any, i: number) {
                    let $cell = cellAt($grid, rowIdx, col);
                    if ($cell) {
                        ti.removeClass($cell, CELL_SELECTED_CLS);
                    }
                });
            });
            
            if (!_selected) return;
            _.forEach(Object.keys(_selected), p => {
                if (_selected.hasOwnProperty(p)) delete _selected[p];
            });
        }
        
        /**
         * Cell at.
         */
        export function cellAt($grid: HTMLElement, rowIdx: any, columnKey: any, desc?: any, hidden?: any) {
            let rowArr = rowAt($grid, rowIdx, desc);
            return getCellInRow(rowArr, columnKey, hidden);
        }
        
        /**
         * Row at.
         */
        export function rowAt($grid: HTMLElement, rowIdx: any, desc?: any) : Array<HTMLElement> {
            if (!desc) desc = _mDesc;
            let fixed, row;
            if (!desc || !desc.rows || !(row = desc.rows[rowIdx])) {
                return null;
            }
            
            if (desc.fixedRows && (fixed = desc.fixedRows[rowIdx])) {
                row = _.concat(fixed, row);
            } 
            return row;
        }
        
        /**
         * Cell in range.
         */
        export function cellInRange($grid: HTMLElement, rowIdx: any, startKey: any, endKey: any) {
            let range = [];
            let rowArr = rowAt($grid, rowIdx);
            let desc = _mDesc;
            if (_.isNil(rowArr) || _.isNil(desc)) return;
            let start, end, fixedCount = 0;
            if (desc.fixedColIdxes) {
                start = desc.fixedColIdxes[startKey];
                end = desc.fixedColIdxes[endKey];
                fixedCount = Object.keys(desc.fixedColIdxes).length;
            }
            
            if (_.isNil(start)) {
                start = desc.colIdxes[startKey] + fixedCount;
            }
            
            if (_.isNil(end)) {
                end = desc.colIdxes[endKey] + fixedCount;
            }
            
            if (_.isNil(start) || _.isNil(end)) return;
            let min = Math.min(start, end);
            let max = Math.max(start, end);
            _.forEach(rowArr, (c, index) => {
                if (c.style.display !== "none" && index >= min && index <= max) {
                    ti.addClass(c, CELL_SELECTED_CLS);
                    let coord = ti.getCellCoord(c);
                    
                    if (coord) {
                        addSelect($grid, rowIdx, coord.columnKey, true);
                        range.push(c);
                    }
                } else if (index > max) return;
            });
            
            return range;
        }
        
        /**
         * Get cell in row.
         */
        export function getCellInRow(rowArr: any, columnKey: any, hidden: any) {
            if (_.isNil(rowArr)) return null;
            
            return _.find(rowArr, c => {
                if (!hidden && c.style.display === "none") return false;
                let coord = ti.getCellCoord(c);    
                if (coord.columnKey === columnKey) return true;
            });
        }
        
        /**
         * Get selected cells.
         */
        export function getSelectedCells($grid: HTMLElement) {   
            let selectedCells = _selected;
            let desc = _mDesc;
            let dataSource = _dataSource;
            let cells = [];
            let arr = _.sortBy(_.keys(selectedCells), r => parseFloat(r));
            _.forEach(arr, function(rowIdx: any) {
                _.forEach(selectedCells[rowIdx], function(colKey: any) {
                    cells.push(new Cell(rowIdx, colKey, dataSource[rowIdx][colKey]));
                });
            });
            return cells;
        }
        
        /**
         * Select next.
         */
        export function selectNext($grid: HTMLElement, direct?: boolean) {
            let selectedCells = _selected;
            let keys = Object.keys(selectedCells);
            if (!selectedCells || keys.length === 0) return;
            let sortedKeys = keys.sort((o, t) => o - t);
            let cell, nCell, nRow, colElms, key, fixedCount = 0, dCount = 0, colIdx, desc = _mDesc, ds = _dataSource, tRowIdx = parseFloat(sortedKeys[0]);
            if (_.isNil(tRowIdx)) return;
            
            if (desc.fixedColIdxes) {
                fixedCount = Object.keys(desc.fixedColIdxes).length;
            }
            
            colIdx = _.min(_.map(selectedCells[tRowIdx], c => {
                let idx, isFixed = true;
                if (desc.fixedColIdxes) {
                    idx = desc.fixedColIdxes[c];
                }
                
                if (_.isNil(idx)) {
                    idx = desc.colIdxes[c];
                    isFixed = false;
                }
                
                return isFixed ? idx : idx + fixedCount;
            }));
            
            dCount = Object.keys(desc.colIdxes).length;
            
            if (direct === "below") {
                if (ds && parseInt(tRowIdx) === ds.length - 1) {
                    tRowIdx = 0;
                    if (fixedCount + dCount === colIdx + 1) {
                        colIdx = 0;
                    } else colIdx++;
                } else {
                    tRowIdx++;
                }
            } else if (fixedCount + dCount === colIdx + 1) {
                colIdx = 0;
                if (ds && parseInt(tRowIdx) === ds.length - 1) {
                    tRowIdx = 0;
                } else {
                    tRowIdx++;
                }
            } else {
                colIdx++;
            }
                
            colElms = rowAt($grid, tRowIdx);
            nCell = colElms[colIdx];
            clearAll($grid);
            
            if (tc.visualJumpTo($grid, tRowIdx)) {
                setTimeout(() => {
                    selectCell($grid, nCell)
                    if (nCell.style.display === "none") {
                        selectNext($grid, direct);
                        nCell.classList.remove(CELL_SELECTED_CLS);
                    }
                }, 1);
                return;
            }
            
            selectCell($grid, nCell);
            nRow = ti.closest(nCell, "tr");
            if (nCell.style.display === "none" || (nRow && nRow.style.display === "none")) {
                selectNext($grid, direct);
                nCell.classList.remove(CELL_SELECTED_CLS);
            }
        }
        
        /**
         * Select prev.
         */
        export function selectPrev($grid: HTMLElement, direct?: boolean) {
            let selectedCells = _selected;
            let keys = Object.keys(selectedCells);
            if (!selectedCells || keys.length === 0) return;
            let sortedKeys = keys.sort((o, t) => o - t);
            let cell, nCell, nRow, colElms, key, fixedCount = 0, dCount = 0, colIdx, desc = _mDesc, ds = _dataSource, tRowIdx = parseFloat(sortedKeys[0]);
            if (_.isNil(tRowIdx)) return;
            
            if (desc.fixedColIdxes) {
                fixedCount = Object.keys(desc.fixedColIdxes).length;
            }
            
            colIdx = _.min(_.map(selectedCells[tRowIdx], c => {
                let idx, isFixed = true;
                if (desc.fixedColIdxes) {
                    idx = desc.fixedColIdxes[c];
                }
                
                if (_.isNil(idx)) {
                    idx = desc.colIdxes[c];
                    isFixed = false;
                }
                
                return isFixed ? idx : idx + fixedCount;
            }));
            
            dCount = Object.keys(desc.colIdxes).length;
            
            if (direct === "below") {
                if (tRowIdx === 0) {
                    tRowIdx = ds.length - 1;
                    if (colIdx === 0) {
                        colIdx = fixedCount + dCount - 1;
                    } else colIdx--;
                } else {
                    tRowIdx--;
                }
            } else if (colIdx === 0) {
                colIdx = fixedCount + dCount - 1;
                if (tRowIdx === 0) {
                    tRowIdx = ds.length - 1;
                } else {
                    tRowIdx--;
                }
            } else {
                colIdx--;
            }
                
            colElms = rowAt($grid, tRowIdx);
            nCell = colElms[colIdx];
            clearAll($grid);
            
            if (tc.visualJumpTo($grid, tRowIdx)) {
                setTimeout(() => {
                    selectCell($grid, nCell);
                    if (nCell.style.display === "none") {
                        selectPrev($grid, direct);
                        nCell.classList.remove(CELL_SELECTED_CLS);
                    }
                }, 1);
                return;
            }
            
            selectCell($grid, nCell);
            nRow = ti.closest(nCell, "tr");
            if (nCell.style.display === "none" || (nRow && nRow.style.display === "none")) {
                selectPrev($grid, direct);
                nCell.classList.remove(CELL_SELECTED_CLS);
            }
        }
    }
    
    module hpl {
        export const VALIDATORS = "mValidators"; 
        export const CURRENCY_CLS = "currency-symbol";
        let H_M_MAX: number = 60;
        
        export class ColumnFieldValidator {
            key: string;
            parentName: string;
            name: string;
            primitiveValue: string;
            options: any;
            constructor(parentName: string, name: string, primitiveValue: string, options: any, key: any) {
                this.key = key;
                this.parentName = parentName;
                this.name = name;
                this.primitiveValue = primitiveValue;
                this.options = options;
            }
            
            probe(value: string, id: any) {
                let constraint, valueType = this.primitiveValue ? ((constraint = ui.validation.getConstraint(this.primitiveValue)) && constraint.valueType)
                                : this.options.cDisplayType;
                switch (valueType) {
                    case "String":
                        if (this.primitiveValue === "StampNumber") {
                            this.options.required = constraint && constraint.required;
                            return new nts.uk.ui.validation.PunchCardNoValidator(this.name, this.primitiveValue, this.options)
                                .validate(value, this.options);
                        }
                        return new nts.uk.ui.validation.StringValidator(this.name, this.primitiveValue, this.options)
                                .validate(value, this.options);
                    case "Integer":
                    case "HalfInt":
                        return new NumberValidator(this.name, valueType, this.primitiveValue, this.options, this.parentName) 
                               .validate(value);
                    case "Decimal":
                        if (_.isNil(this.options.decimallength) && !_.isNil(constraint.mantissaMaxLength)) {
                            this.options.decimallength = constraint.mantissaMaxLength;
                        }
                        return new NumberValidator(this.name, valueType, this.primitiveValue, this.options, this.parentName) 
                               .validate(value);
                    case "Currency":
                        let opts: any = new ui.option.CurrencyEditorOption();
                        opts.grouplength = this.options.groupLength | 3;
                        opts.decimallength = _.isNil(this.options.decimalLength) ? 0 : this.options.decimalLength;
                        opts.currencyformat = this.options.currencyFormat ? this.options.currencyFormat : "JPY";
                        if (!_.isNil(this.options.min)) opts.min = this.options.min;
                        if (!_.isNil(this.options.max)) opts.max = this.options.max;
                        if (!_.isNil(this.options.required)) opts.required = this.options.required;
                        return new NumberValidator(this.name, valueType, this.primitiveValue, opts)
                                .validate(value);
                    case "Date":
                        return new DateValidator(this.name, this.primitiveValue, this.options).validate(value);
                    case "Time":
                        this.options.mode = "time";
                        return new nts.uk.ui.validation.TimeValidator(this.name, this.primitiveValue, this.options)
                                .validate(value);
                    case "Clock":
                        // Don't merge with time type.
                        this.options.mode = "time";
                        return new nts.uk.ui.validation.TimeValidator(this.name, this.primitiveValue, this.options)
                                .validate(value);
                    case "StandardTimeWithDay":
                        this.options.timeWithDay = true;
                        let result = new TimeWithDayValidator(this.name, this.primitiveValue, this.options)
                                        .validate(value);
                        if (result.isValid) {
                            let formatter = new text.TimeWithDayFormatter(this.options);
                            result.parsedValue = formatter.format(result.parsedValue);
                        }
                        return result;
                    case "TimeWithDay":
                        this.options.timeWithDay = true;
                        if (this.primitiveValue) {
                            let constraint = nts.uk.ui.validation.getConstraint(this.primitiveValue);
                            if (constraint && _.isNil(this.options.required)) {
                                this.options.required = constraint.required;
                            }
                        }
                        
                        let result = new nts.uk.ui.validation.TimeWithDayValidator(this.name, this.primitiveValue, this.options)
                                        .validate(value);
                        if (result.isValid) {
                            let formatter = new text.TimeWithDayFormatter(this.options);
                            result.parsedValue = formatter.format(result.parsedValue);
                        }
                        return result;
                    case "Selection":
                    case "Radio":
                        let control = dkn.controlType[this.key],
                            result = new ui.validation.ValidationResult();
                        if (control && control.type === dkn.COMBOBOX) {
                            let stt, options;
                            if (control.optionsMap && !_.isNil(stt = control.optionsMap[id])) {
                                options = control.optionsList[stt];
                            } else {
                                options = control.options;
                            }
                        
                            if (constraint.required && !_.find(options, opt => opt[control.optionsValue] === value)) {
                                result.fail(nts.uk.resource.getMessage("FND_E_REQ_SELECT", [ this.name ]), "FND_E_REQ_SELECT");
                            } else result.success();
                        }
                        
                        return result;
                }
            }
        }
        
        class NumberValidator {
            parentName: string;
            name: string;
            displayType: string;
            primitiveValue: string;
            options: any;
            constructor(name: string, displayType: string, primitiveValue?: string, options?: any, parentName?: any) {
                this.parentName = parentName;
                this.name = name;
                this.displayType = displayType;
                this.primitiveValue = primitiveValue;
                this.options = options;
            }
            
            validate(text: string) {
                let self = this;
                if (typeof text !== "string" && !_.isNil(text)) text = String(text);
                if (self.primitiveValue) {
                    return new nts.uk.ui.validation.NumberValidator(self.name, self.primitiveValue, self.options).validate(text);
                }
                
                if (self.displayType === "Currency") {
                    text = uk.text.replaceAll(text, self.options.groupseperator, "");
                }
                
                let result = new ui.validation.ValidationResult();
                if ((util.isNullOrUndefined(text) || text.length === 0)) {
                    if (self.options && self.options.required) {
                        result.fail(nts.uk.resource.getMessage('MsgB_1', [ self.name ]), 'MsgB_1');
                        return result;
                    }
                    if (!self.options || (self.options && !self.options.required)) {
                        result.success(text);
                        return result;
                    }
                }
                let message: any = {};
                let isValid;
                if (self.displayType === "HalfInt") {
                     isValid = ntsNumber.isHalfInt(text, message);
                } else if (self.displayType === "Integer") {
                    isValid = ntsNumber.isNumber(text, false, self.options, message);
                } else if (self.displayType === "Decimal") {
                    isValid = ntsNumber.isNumber(text, true, self.options, message);
                } else if (self.displayType === "Currency") {
                    isValid = ntsNumber.isNumber(text, false, self.options, message);
                    if (_.indexOf(text, ".") > -1) isValid = false;
                }
                
                let min = 0, max = 999999999;
                let value = parseFloat(text);
                
                if (self.options.values && !_.some(self.options.values, v => v === value)) {
                    result.fail(resource.getMessage("Msg_1443", [ self.parentName ]), "Msg_1443");
                    return result;
                }
                if (!util.isNullOrUndefined(self.options.min)) {
                    min = self.options.min;
                    if (value < min) isValid = false;
                }
                if (!util.isNullOrUndefined(self.options.max)) {
                    max = self.options.max;
                    if (value > max) isValid = false;
                }
                
                if (!isValid) {
                    result.fail(resource.getMessage(message.id, [ self.name, min, max ]), message.id);
                    return result;
                }
                
                let formatter = new uk.text.NumberFormatter({ option: self.options });
                let formatted = formatter.format(text);
                result.success(self.displayType === "Currency" ? formatted : value + "");
                return result;
            }
        }
        
        export const MIN_DATE = moment.utc("1900/01/01", "YYYY/MM/DD", true);
        export const MAX_DATE = moment.utc("9999/12/31", "YYYY/MM/DD", true);
        
        class DateValidator {
            name: string;
            constraint: any;
            required: boolean;
            msgId: string;
            formats: any;
            constructor(name: string, primitiveValueName: string, option?: any) {
                this.name = name;
                this.constraint = ui.validation.getConstraint(primitiveValueName);
                if (_.isNil(this.constraint)) {
                    this.constraint = {};
                    this.constraint.min = option && !_.isNil(option.min) ? option.min : MIN_DATE;
                    this.constraint.max = option && !_.isNil(option.max) ? option.max : MAX_DATE;
                } else {
                    if (this.constraint.min === "" || _.isNil(this.constraint.min)) {
                        this.constraint.min = MIN_DATE;
                    } 
                    if (this.constraint.max === "" || _.isNil(this.constraint.max)) {
                        this.constraint.max = MAX_DATE;
                    }
                }
                
                this.msgId = "FND_E_DATE_" + _.toUpper(option.type);
                this.formats = ti.dateFormat(_.toLower(option.type));
                this.required = (option && option.required) || this.constraint.required;
            }
            
            validate(date: any) {
                let self = this,
                    result = new ui.validation.ValidationResult();
                
                if (_.isNil(date) || date === "" || (date instanceof moment && date._i === "")) {
                     if (this.required) {
                        result.fail(nts.uk.resource.getMessage('FND_E_REQ_INPUT', [ self.name ]), 'FND_E_REQ_INPUT');
                    } else result.success("");
                    return result;
                }
                
                let mDate = moment.utc(date, self.formats, true);
                if (!mDate.isValid() || mDate.isBefore(self.constraint.min) || mDate.isAfter(self.constraint.max)) {
                    let min = self.constraint.min, max = self.constraint.max;
                    if (!(self.constraint.min instanceof moment)) min = moment(min, self.formats, true);
                    if (!(self.constraint.max instanceof moment)) max = moment(max, self.formats, true); 
                    result.fail(nts.uk.resource.getMessage(self.msgId, [self.name, min.format(self.formats[0]), max.format(self.formats[0])]), self.msgId);
                } else {
                    result.success(mDate.format(self.formats[0]));
                }
                
                return result;
            }
        }
        
        let MAX_VALUE = time.minutesBased.duration.parseString("71:59"),
            MIN_VALUE = time.minutesBased.duration.parseString("-12:00");
        
        class TimeWithDayValidator {
            name: string;
            constraint: any;
            required: boolean; 
            constructor(name: string, primitiveValueName: string, option?: any) {
                this.name = name;
                this.constraint = ui.validation.getConstraint(primitiveValueName);
                if (_.isNil(this.constraint)) {
                    this.constraint = {};
                    if (option && !_.isNil(option.min)) {
                        this.constraint.min = option.min;
                    }
                    if (option && !_.isNil(option.max)) {
                        this.constraint.max = option.max;
                    }
                }
                this.required = (option && option.required) ? option.required : false;
            }
    
            validate(inputText: string): any {
                let self = this;
                let result = new ui.validation.ValidationResult();
                
                if (util.isNullOrEmpty(inputText)) {
                    if (this.required) {
                        result.fail(nts.uk.resource.getMessage('MsgB_1', [ this.name ]), 'MsgB_1');
                    } else result.success("");
                    return result;
                }
                
                let minValue, maxValue, minParsed, maxParsed, parsedValue;
                if (!_.isNil(self.constraint.min)) { 
                    minParsed = time.minutesBased.duration.parseString(self.constraint.min);
                    if (minParsed.success) {
                        minValue = minParsed.toValue();
                    }
                } else minValue = MIN_VALUE.toValue();
                
                if (!_.isNil(self.constraint.max)) {
                    maxParsed = time.minutesBased.duration.parseString(self.constraint.max);
                    if (maxParsed.success) {
                        maxValue = maxParsed.toValue();
                    }      
                } else maxValue = MAX_VALUE.toValue();
                
                let parsed = time.minutesBased.duration.parseString(inputText);
                if (!parsed.success || (parsedValue = parsed.toValue()) !== Math.round(parsedValue) 
                    || parsedValue < minValue || parsedValue > maxValue) {
                    result.fail(nts.uk.resource.getMessage("MsgB_16", [ self.name, minParsed.format(), maxParsed.format() ]), "MsgB_16");
                } else {
                    result.success(parsedValue);
                }
                
                return result;
            }
        }
        
        export class Result {
            isValid: boolean;
            formatted: any;
            errorMessageId: string;
            onSuccess: any = $.noop;
            onFail: any = $.noop;
            constructor(isValid: boolean, formatted?: any, messageId?: string) {
                this.isValid = isValid;
                this.formatted = formatted;
                this.errorMessageId = messageId;
            }
            
            static OK(formatted: any) {
                return new Result(true, formatted);
            }
            
            static invalid(msgId: string) {
                return new Result(false, null, msgId);
            }
            
            success(cnt: any) {
                this.onSuccess = cnt;
                return this;
            }
            
            fail(cnt: any) {
                this.onFail = cnt;
                return this;
            }
            
            terminate() {
                let self = this;
                if (self.isValid && self.onSuccess && _.isFunction(self.onSuccess)) {
                     self.onSuccess(self.formatted);
                } else if (!self.isValid && self.onFail && _.isFunction(self.onFail)) {
                    self.onFail(self.errorMessageId);
                }
            }
        }
        
        /**
         * Parse time.
         */
        export function parseTime(value: any, format?: any): Result {
            if (uk.ntsNumber.isNumber(value, false)) {
                if (value <= H_M_MAX) return Result.OK(value);
                let hh = Math.floor(value / 100);
                let mm = value % 100;
                if (mm >= H_M_MAX) return Result.invalid("NEED_MSG_INVALID_TIME_FORMAT");
                return Result.OK(hh + ":" + mm.toLocaleString("en-US", { minimumIntegerDigits: 2, useGrouping: false }));
            }
            
            let formatRes = uk.time.applyFormat(format, value, undefined);
            if (!formatRes) return Result.invalid("NEED_MSG_INVALID_TIME_FORMAT");
            return Result.OK(formatRes);
        }
        
        /**
         * Get value type.
         */
        export function getValueType(columnKey: any) {
            if (!_validators || !_validators[columnKey]) return;
            let column = _validators[columnKey];
            return column.primitiveValue ? ui.validation.getConstraint(column.primitiveValue).valueType
                                : column.options.cDisplayType
        }
        
        /**
         * Get group separator.
         */
        export function getGroupSeparator(columnKey: any) {
            if (!_validators || !_validators[columnKey]) return;
            return _validators[columnKey].options.groupseperator;
        }
    }
    
    module khl {
        export let ERROR_CLS = "merror";
        export let ERR_MSG_CLS = "mgrid-error-message";
        export let _infobulle;
        
        export class GridCellError {
            grid: JQuery = _$grid;
            index: any;
            rowId: any;
            columnKey: string;
            columnName: string;
            message: string;
            
            constructor(index: any, rowId: any, columnKey: any, message: string) {
                this.index = index;
                this.rowId = rowId;
                this.columnKey = columnKey;
                this.message = message;
                let col = _columnsMap[this.columnKey];
                if (col) this.columnName = col[0].headerText; 
            }
            
            equals(err: GridCellError) {
                if (this.index !== err.index) return false;
                if (this.rowId !== err.rowId) return false;
                if (this.columnKey !== err.columnKey) return false;
                return true;
            }
        }
        
        /**
         * Add error.
         */
        export function addCellError(error: any, genre: any) {
            let errors = genre ? genre.errors : _errors;
            if (errors.some(function(e: GridCellError) {
                return e.equals(error);
            })) return;
            
            errors.push(error);
            let occurred = _$grid.mGrid("option", "errOccurred");
            if (_.isFunction(occurred)) {
                occurred();
            }
        }
    
        /**
         * Remove error.
         */
        export function removeCellError(rowId: any, key: any, genre: any) {
            let errors = genre ? genre.errors : _errors;
            let removed = _.remove(errors, function(e) {
                return rowId === e.rowId && key === e.columnKey;
            });
            
            if (removed.length > 0) {
                if (errors.length === 0) {
                    let resolved = _$grid.mGrid("option", "errResolved");
                    if (_.isFunction(resolved)) {
                        resolved();
                    }
                } else {
                    let dismiss = _$grid.mGrid("option", "errDismissed");
                    if (_.isFunction(dismiss)) {
                        dismiss();
                    }
                }
            }
        }
        
        /**
         * Set.
         */
        export function set(cell: any, message: string, setType?: any, rendered?: any) {
            if (!cell || ((!setType || setType === 1) && (!cell.element || any(cell)))) return;
            if (!setType || setType === 1) {
                let $cell = cell.element;
                $cell.classList.add(ERROR_CLS);
                if (_infobulle) {
                    $.data($cell, "msg", message); 
                }
            }
            
            if (!setType || setType === 2) {
                let errorDetails = createErrorInfos(cell, message);
                if (_errorsOnPage) {
                    ui.errors.addCell(errorDetails);
                }
                addCellError(errorDetails);
            }
            
            let notice = _$grid.mGrid("option", "notice");
            if (_.isFunction(notice)) {
                if (_.isNil(cell.index)) {
                    let index = _.findIndex(_dataSource, d => d[_pk] === cell.id);
                    if (index !== -1) notice(cell.id, cell.columnKey, _dataSource[index], rendered);
                } else {
                    notice(cell.id, cell.columnKey, _dataSource[cell.index], rendered);
                }
            }
        }
        
        /**
         * Create error infos.
         */
        function createErrorInfos(cell: any, message: string): any {
            let record: any = _dataSource[cell.index];
            let error: any = new GridCellError(cell.index, cell.id, cell.columnKey, message);
            // Error column headers
            let headers;
            if (_errorsOnPage) {
                let columns = ko.toJS(ui.errors.errorsViewModel().option().headers());
                if (columns) {
                    headers = columns.filter(c => c.visible).map(c => c.name); 
                }
            } else { 
                headers = _errorColumns;
            }
            _.forEach(headers, function(header: any) {
                if (_.isNil(record[header]) 
                    || !_.isNil(error[header])) return;
                error[header] = record[header];
            });
            return error;
        } 
        
        /**
         * Clear.
         */
        export function clear(cell: any) {
            if (!cell || !cell.element || !any(cell)) return;
            let $cell = cell.element;
            $cell.classList.remove(ERROR_CLS);
            if (_infobulle) {
                $.data($cell, "msg", null);
            }
            if (_errorsOnPage) {
                ui.errors.removeCell(_$grid, cell.id, cell.columnKey);
            }
            removeCellError(cell.id, cell.columnKey);
            
            let notice = _$grid.mGrid("option", "notice");
            if (_.isFunction(notice)) {
                if (_.isNil(cell.index)) {
                    let index = _.findIndex(_dataSource, d => d[_pk] === cell.id);
                    if (index !== -1) notice(true, cell.id, cell.columnKey, _dataSource[index]);
                } else {
                    notice(true, cell.id, cell.columnKey, _dataSource[cell.index]);
                }
            }
        }
        
        /**
         * Any.
         */
        export function any(cell: any) {
            return cell.element && cell.element.classList.contains(ERROR_CLS);
        }
        
        /**
         * Cell equals.
         */
        function cellEquals(one: any, other: any) {
            if (one.columnKey !== other.columnKey) return false;
            if (one.id !== other.id) return false;
            return true;
        }
    }
    
    module selector {
        
        /**
         * Find.
         */
        export function find(p: HTMLElement, sel: any) {
            return new Manipulator().addNodes(p.querySelectorAll(sel));
        }
        
        /**
         * Create.
         */
        export function create(str: any) {
            return new Manipulator().addElement(document.createElement(str));
        }
        
        /**
         * Is.
         */
        export function is(el, sel) {
            let matches = el.matches || el.matchesSelector || el.msMatchesSelector || el.mozMatchesSelector || el.webkitMatchesSelector || el.oMatchesSelector;
            if (matches) return matches.call(el, sel);
            return $(el).is(sel);
        }
        
        /**
         * Index.
         */
        export function index(el) {
            return Array.prototype.slice.call(el.parentNode.children).indexOf(el);
        }
        
        /**
         * Query all.
         */
        export function queryAll(el, sel): Array<HTMLElement> {
            return Array.prototype.slice.call(el.querySelectorAll(sel));
        }
        
        /**
         * Find at.
         */
        export function findAt(el, sel, i) {
            if (!el || !sel || _.isNil(i)) return;
            return el.querySelector(sel + ":nth-of-type(" + i + ")");
        }
        
        /**
         * Insert after.
         */
        export function insertAfter(parent, el, tar) {
            if (!parent || !el || !tar) return;
            parent.insertBefore(el, tar.nextSibling);
        } 
        
        /**
         * Offset.
         */
        export function offset(el) {
            let rect = el.getBoundingClientRect();
            return {
              top: rect.top + document.body.scrollTop,
              left: rect.left + document.body.scrollLeft
            }
        }
        
        /**
         * Class siblings.
         */
        export function classSiblings(node: any, clazz: any) {
            let parent = node.parentElement;
            if (!parent) return;
            let children = parent.children;
            let results = [];
            for (let i = 0; i < children.length; i++) {
                if (children[i] === node) continue;
                let classList = children[i].classList;
                for (let j = 0; j < classList.length; j++) {
                    if (classList.item(j) === clazz) {
                        results.push(children[i]);
                        break;
                    }
                }
            }
            
            return results;
        }
        
        /**
         * Sibling lt.
         */
        export function siblingsLt(el, index) {
            let parent = el.parentNode;
            if (!parent) return;
            let children = parent.children;
            let results = [];
            for (let i = 0; i < children.length; i++) {
                if (i < index) {
                    if (children[i] !== el) results.push(children[i]);
                } else return results;
            }
        }
        
        export class Manipulator {
            elements: Array<HTMLElement>;
                
            addNodes(nodes: NodeList) {
                if (!nodes || nodes.length === 0) return;
                this.elements = Array.prototype.slice.call(self.elements);
                return this;
            }
            
            addElements(elements: Array<HTMLElement>) {
                this.elements = elements;
                return this;
            }
            
            addElement(element: HTMLElement) {
                if (!this.elements) this.elements = [];
                this.elements.push(element);
                return this;
            }
            
            html(str) {
                this.elements.forEach(function(e) {
                    e.innerHTML = str;
                });
                return this;
            }
            
            width(w) {
                let self = this;
                this.elements.forEach(function(e) {
                    e.style.width = parseInt(w) + "px";
                });
                return this;
            }
            
            height(h) {
                let self = this;
                this.elements.forEach(function(e) {
                    e.style.height = parseInt(h) + "px";
                });
                return this;
            }
            
            data(name: any, value: any) {
                this.elements.forEach(function(e) {
                    $.data(e, name, value);
                });
                return this;
            }
            
            addClass(clazz: any) {
                this.elements.forEach(function(e) {
                    e.classList.add(clazz);
                });
                return this;
            }
            
            css(style: any) {
                let text = "; ";
                Object.keys(style).forEach(function(k) {
                    if (k === "maxWidth") {
                        text += ("max-width: " + style[k] + "; ");
                        return;
                    }
                    text += k + ": " + style[k] + "; ";
                });
                
                this.elements.forEach(function(e) {
                    e.style.cssText = text;
                });
                
                return this;
            }
            
            getSingle() {
                return this.elements[0];
            }
            
            get() {
                return this.elements;
            }
        }
    }
    
    export module color {
        export const Error = "mgrid-error";
        export const Alarm = "mgrid-alarm";
        export const ManualEditTarget = "mgrid-manual-edit-target";
        export const ManualEditOther = "mgrid-manual-edit-other";
        export const Reflect = "mgrid-reflect";
        export const Calculation = "mgrid-calc";
        export const Disable = "mgrid-disable";
        export const Lock = "mgrid-lock";
        export const Hide = "mgrid-hide";
        export const HOVER = "ui-state-hover";
        export const ALL = [ Error, Alarm, ManualEditTarget, ManualEditOther, Reflect, Calculation, Disable ];
        
        /**
         * Push state.
         */
        export function pushState(id: any, key: any, state: any, suivant?: any) {
            if (!_cellStates) {
                _cellStates = {};
            }
            
            if (!_cellStates[id]) {
                _cellStates[id] = {};
                _cellStates[id][key] = [{ rowId: id, columnKey: key, state: _.concat([], state), suivant: suivant }]
                return;
            }
            
            if (!_cellStates[id][key]) {
                _cellStates[id][key] = [{ rowId: id, columnKey: key, state: _.concat([], state), suivant: suivant }];
                return;
            }
            
            if (_.isArray(state)) {
                _.forEach(state, s => {
                    _cellStates[id][key][0].state.push(s);
                });
            } else _cellStates[id][key][0].state.push(state);
            
            if (suivant) {
                _cellStates[id][key][0].suivant = suivant;
            }
        }
        
        /**
         * Pop state.
         */
        export function popState(id: any, key: any, states: any) {
            if (!states) return;
            if (!_cellStates || !_cellStates[id] || !_cellStates[id][key]) return;
            _.remove(_cellStates[id][key][0].state, s => {
                if (_.isArray(states)) {
                    return _.some(states, state => state === s); 
                } else return s === states;
            });
        }
    }
    
     module ti {
        
        /**
         * Is IE.
         */
        export function isIE() {
            return window.navigator.userAgent.indexOf("MSIE") > -1 || window.navigator.userAgent.match(/trident/i);
        }
        
        /**
         * Is Chrome.
         */
        export function isChrome() {
            return window.chrome;
        }
        
        /**
         * Is Edge.
         */
        export function isEdge() {
            return window.navigator.userAgent.indexOf("Edge") > -1;
        }
         
        export function isArrowKey(evt: any) {
            return evt.keyCode >= 37 && evt.keyCode <= 40;
        }
         
        export function isArrowLeft(evt: any) {
            return evt.keyCode === 37;
        }
        
        export function isArrowRight(evt: any) {
            return evt.keyCode === 39;
        }
         
        export function isArrowUp(evt: any) {
            return evt.keyCode === 38;
        }
         
        export function isArrowDown(evt: any) {
            return evt.keyCode === 40;
        } 
         
        export function isAlphaNumeric(evt: any) {
            return (evt.keyCode >= 48 && evt.keyCode <= 90) 
                    || (evt.keyCode >= 96 && evt.keyCode <= 105);
        }
         
        export function isMinusSymbol(evt: any) {
            return evt.keyCode === 189 || evt.keyCode === 109;
        }
         
        export function isTabKey(evt: any) {
            return evt.keyCode === 9;
        }
         
        export function isEnterKey(evt: any) {
            return evt.keyCode === 13;
        }
         
        export function isSpaceKey(evt: any) {
            return evt.keyCode === 32;
        }
         
        export function isDeleteKey(evt: any) {
            return evt.keyCode === 46;
        }
         
        export function isPasteKey(evt: any) {
            return evt.keyCode === 86;
        }
         
        export function isCopyKey(evt: any) {
            return evt.keyCode === 67;
        }
         
        export function isCutKey(evt: any) {
            return evt.keyCode === 88;
        }
         
        export function cloneDeep(obj: any, cloneObject?: any) {
            if (Array.isArray(obj)) return cloneArray(obj);
            if (typeof obj !== "object" || _.isNil(obj)) return obj;
            cloneObject = cloneObject || Object.create(Object.getPrototypeOf(obj));
            
            function clone(cloneObj, obj) {
                for (var i in obj) {
                    if (_.isNil(obj[i]) || obj[i] instanceof Date || obj[i] instanceof moment) {
                        cloneObj[i] = obj[i];
                        continue;
                    }
                    
                    if (Array.isArray(obj[i])) {
                        cloneObj[i] = cloneArray(obj[i]);
                    } else if (typeof obj[i] === "object") {
                        cloneObj[i] = clone(Object.create(Object.getPrototypeOf(obj[i])), obj[i]);
                    } else {
                        cloneObj[i] = obj[i];
                    }
                }
                
                return cloneObj;
            }
            
            clone(cloneObject, obj);
            return cloneObject;
        }
         
        export function cloneArray(arr: Array<any>) {
            if (_.isNil(arr)) return arr;
            let cloneArr = [];
            for (let i = 0; i < arr.length; i++) {
                cloneArr.push(cloneDeep(arr[i]));
            }
            
            return cloneArr;
        }
         
        export function forEach(arr: Array<any>, loop: any) {
            if (_.isNil(arr)) return;
            if (_.isObject(arr) && !_.isArray(arr)) {
                let keys = _.keys(arr);
                for (let i = 0; i < keys.length; i++) {
                    if (loop(arr[keys[i]], keys[i]) === false) break;    
                }
                
                return;
            }
            
            for (let i = 0; i < arr.length; i++) {
                if (loop(arr[i], i) === false) break;
            }
        }
         
        export function isZero(value: any, name: any) {
            let col = _secColumn[name];
            if (col && ((col.constraint && col.constraint.cDisplayType === "TimeWithDay")
                || !col.grant)) return false;
            return Number(value) === 0 || value === "0:00" || value === "00:00";
        }
         
        export function isTableCell(obj: any) {
            return obj.constructor === HTMLTableCellElement 
                || obj.constructor === HTMLTableDataCellElement;
        }
         
        /**
         * Equal.
         */
        export function isEqual(one: any, two: any, fields?: Array<any>) {
            if (_.isObject(one) && _.isObject(two)) {
                return (fields && fields.length > 0) 
                        ? _.isEqual(_.omitBy(one, (d, p) => fields.every(f => f !== p)),
                                    _.omitBy(two, (d, p) => fields.every(f => f !== p)))
                        : _.isEqual(_.omit(one, _.isFunction), _.omit(two, _.isFunction));
            }
            return _.isEqual(one, two);
        }
        
        /**
         * Get scroll width.
         */
        export function getScrollWidth() {
            if (_scrollWidth) return _scrollWidth;
            let $outer = document.body.appendChild(selector.create("div").css({ visibility: 'hidden', width: "100px", overflow: 'scroll' }).getSingle());
            let $inner = selector.create("div").css({ width: '100%' }).getSingle();
            $outer.appendChild($inner);
            let widthWithScroll = $inner.offsetWidth;
            $outer.parentNode.removeChild($outer);
            _scrollWidth = 100 - widthWithScroll;
            return _scrollWidth;
        }
         
        /**
         * First sibling.
         */
        export function firstSibling(node: any, clazz: any) {
            let parent = node.parentElement;
            if (!parent) return;
            let children = parent.children;
            for (let i = 0; i < children.length; i++) {
                if (node !== children[i] && children[i].classList.contains(clazz)) {
                    return children[i];
                }
            }
        }
        
        /**
         * Class siblings.
         */
        export function classSiblings(node: any, partialClass: any) {
            let parent = node.parentElement;
            if (!parent) return;
            let children = parent.children;
            let results = [];
            for (let i = 0; i < children.length; i++) {
                if (children[i] === node) continue;
                let classList = children[i].classList;
                for (let j = 0; j < classList.length; j++) {
                    if (classList.item(j).indexOf(partialClass) >= 0) {
                        results.push(children[i]);
                    }
                }
            }
            
            return results;
        }
        
        /**
         * Consume siblings.
         */
        export function consumeSiblings(node: any, op: any) {
            let parent = node.parentElement;
            if (!parent) return;
            let children = parent.children;
            for (let i = 0; i < children.length; i++) {
                if (node !== children[i]) {
                    op(children[i]);
                }
            }
        }
        
        /**
         * Closest.
         */
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
         
        /**
         * Add class.
         */
        export function addClass1n(node, clazz) {
            if (node && node.constructor !== HTMLCollection) {
                let children = node.querySelectorAll("." + v.CHILD_CELL_CLS); 
                if (children.length > 0) addClass(children, clazz);
                else addClass(node, clazz);
                return;
            }
            
            for (let i = 0; i < node.length; i++) {
                let children = node[i].querySelectorAll("." + v.CHILD_CELL_CLS);
                if (children.length > 0) addClass(children, clazz);
                else addClass(node[i], clazz);
            }
        }
        
        /**
         * Remove class.
         */
        export function removeClass1n(node, clazz) {
            if (node && node.constructor !== HTMLCollection) {
                let children = node.querySelectorAll("." + v.CHILD_CELL_CLS);
                if (children.length > 0) removeClass(children, clazz);
                else removeClass(node, clazz);
                return;
            }
            
            for (let i = 0; i < node.length; i++) {
                let children = node[i].querySelectorAll("." + v.CHILD_CELL_CLS);
                if (children.length > 0) removeClass(children, clazz);
                else removeClass(node[i], clazz);
            }
        }
        
        /**
         * Add class.
         */
        export function addClass(node, clazz) {
            if (node && node.constructor !== HTMLCollection && node.constructor !== NodeList) {
                node.classList.add(clazz);
                return;
            }
            
            for (let i = 0; i < node.length; i++) {
                if (!node[i].classList.contains(clazz)) {
                    node[i].classList.add(clazz);
                }
            }
        }
        
        /**
         * Remove class.
         */
        export function removeClass(node, clazz) {
            if (node && node.constructor !== HTMLCollection && node.constructor !== NodeList) {
                node.classList.remove(clazz);
                return;
            }
            
            for (let i = 0; i < node.length; i++) {
                if (node[i].classList.contains(clazz)) {
                    node[i].classList.remove(clazz);
                }
            }
        }
        
        /**
         * Remove.
         */
        export function remove(node) {
            if (isIE()) {
                if (node && node.parentNode) node.parentNode.removeChild(node);
                return;
            }
            
            node.remove();
        }
         
        /**
         * Classify columns.
         */
        export function classifyColumns(options: any, link?: any) {
            let visibleColumns = [];
            let hiddenColumns = [];
            let columns = filterColumns(options.columns, visibleColumns, hiddenColumns, link);
            
            return {
                        visibleColumns: visibleColumns,
                        hiddenColumns: hiddenColumns,
                        columns: columns
                   };
        }
         
        /**
         * Get columns map.
         */
        export function getColumnsMap(columns: any) {
            return _.groupBy(columns, "key");
        }
         
        /**
         * Filter columns.
         */
        function filterColumns(columns: Array<any>, visibleColumns: Array<any>, hiddenColumns: Array<any>, link?: any) {
            let cols = [];
            _.forEach(columns, function(col: any) {
                if (link && col.bound) _linkage.push(col.key);
                if (!_.isNil(col.hidden) && col.hidden === true) {
                    hiddenColumns.push(col);
                    cols.push(col);
                    return;
                }
                
                if (!util.isNullOrUndefined(col.group) && col.group.length > 0) { 
                    cols = _.concat(cols, filterColumns(col.group, visibleColumns, hiddenColumns, link));
                } else {
                    visibleColumns.push(col);
                    cols.push(col);
                }
            });
            
            return cols;
        }
         
        /**
         * Columns map.
         */ 
        export function columnsMapFromStruct(levelStruct: any) {
            let map = {};
            _.forEach(Object.keys(levelStruct), function(nth: any) {
                _.forEach(levelStruct[nth], function(col: any) {
                    if (!util.isNullOrUndefined(col.key)) {
                        map[col.key] = col;
                    }
                });
            });
            return map; 
        }
         
         /**
          * Calc total.
          */
         export function calcTotal() {
            _.forEach(_.keys(_summaries), k => {
                let sum = _summaries[k];
                if ((sum.calculator === "Time" && sum[_currentPage] && sum[_currentPage].asHours() > 0)
                    || (sum.calculator === "Number" && sum[_currentPage] > 0)) return;
                
                _.forEach(_dataSource, d => {
                    switch (sum.calculator) {
                        case "Time":
                            if (_.isNil(sum[_currentPage])) {
                                sum[_currentPage] = moment.duration("0:00");
                            }
                            sum[_currentPage].add(moment.duration(d[k]));
                            break;
                        case "Number": 
                            if (_.isNil(sum[_currentPage])) {
                                sum[_currentPage] = 0;
                            }
                            sum[_currentPage] += (!_.isNil(d[k]) && d[k] !== "" ? parseFloat(d[k]) : 0);
                            break;
                    }
                });     
            });
         }
         
         /**
          * Time to minutes.
          */
         export function timeToMinutes(time) {
            if (_.isNil(time) || time.constructor !== String) return;
            let parts = time.split(":");
            let hour = Math.abs(Number(parts[0])), minute = Number(parts[1]);
            return (_.indexOf(parts[0], "-") === 0 ? -1 : 1) * ((isNaN(hour) ? 0 : hour) * 60 + (isNaN(minute) ? 0 : minute));
         }
         
         /**
          * Moment to string.
          */
         export function momentToString(total) {
            let minus = "", time = total.asHours();
            if (time < 0) {
                time = Math.abs(time);
                minus = "-";    
            }
             
            let hour = Math.floor(time),
                minute = (time - hour) * 60,
                roundMin = Math.round(minute),
                minuteStr = roundMin < 10 ? ("0" + roundMin) : String(roundMin);
            return minus + hour + ":" + minuteStr;
         }
         
         /**
          * As currency.
          */
         export function asCurrency(value) {
            let currencyOpts: any = new ui.option.CurrencyEditorOption();
            currencyOpts.grouplength = 3;
            currencyOpts.decimallength = 0;
            currencyOpts.currencyformat = "JPY";
            let formatter = new uk.text.NumberFormatter({ option: currencyOpts });
            if (!isNaN(value)) return formatter.format(value);
            return value;
         }
         
         /**
          * Date format.
          */
         export function dateFormat(format) {
            let formats;
            if (format === "y") {
                formats = [ "YYYY" ];
            } else if (format === "ym") {
                formats = [ "YYYY/MM", "YYYYMM" ];
            } else {
                formats = [ "YYYY/MM/DD", "YYYY/M/D", "YYYYMMDD" ];
            }
            
            return formats;
         }
         
         /**
          * Days board.
          */
         export function daysBoard(date) {
            let days = []; 
            if (date.date() > 1) {
                date = moment({ y: date.year(), M: date.month(), d: 1 });
            }    
            
            let weekday = date.isoWeekday(), monthdays = date.daysInMonth(), prevDays,
                last = monthdays + weekday;
            if (date.month() === 0) {
                prevDays = moment({ y: date.year() - 1, M: 11, d: 1 }).daysInMonth();
            } else prevDays = moment({ y: date.year(), M: date.month() - 1, d: 1 }).daysInMonth();
            
            for (let i = weekday - 1; i >= 0; i--) {
                days[i] = prevDays - weekday + 1 + i;             
            }
             
            for (let i = weekday; i < last; i++) {
                days[i] = i - weekday + 1;
            }    
             
            for (let i = last; i < 42; i++) {
                days[i] = i - last + 1;
            }
            return days;
         }
         
         /**
          * Get cell coord.
          */
         export function getCellCoord($cell: HTMLElement) {
            if (!$cell) return;
            let $td = $cell;
            if (selector.is($cell, "div")) {
                $td = closest($cell, "td");
            }
             
            let view = $.data($td, lo.VIEW),
                $tr = closest($td, "tr");
            if (!view || !$tr) return;
            let coord = view.split("-");
            if (util.isNullOrUndefined(coord[0]) || util.isNullOrUndefined(coord[1])) return;
             
            return {
                rowIdx: parseFloat($.data($tr, lo.VIEW)),
                columnKey: coord[1]
            };
        }
     }
}