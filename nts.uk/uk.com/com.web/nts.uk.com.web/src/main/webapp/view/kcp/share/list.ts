module kcp.share.list {
    export interface UnitModel {
        id?: string;
        code: string;
        name?: string;
        affiliationName?: string;
        isAlreadySetting?: boolean;
        optionalColumn?: any;
    }
    
    export interface OptionalColumnDataSource {
        empId: string;
        content: any;
    }

    export interface UnitAlreadySettingModel {
        code: string;
        isAlreadySetting: boolean;
    }
    
    /**
     * Component option.
     */
    export interface ComponentOption {
        /**
         * is Show Already setting.
         */
        isShowAlreadySet: boolean;
        
        /**
         * is Multi use (複数使用区分). Setting use multiple components?
         */
        isMultipleUse: boolean;
        
        /**
         * is Multi select (選択モード). Setting multiple selection in grid.
         */
        isMultiSelect: boolean;
        
        /**
         * list type.
         * 1. Employment list.
         * 2. Classification.
         * 3. Job title list.
         * 4. Employee list.
         * 5. Workplace list.
         */
        listType: ListType;
        
        /**
         * selected value.
         * May be string or Array<string>.
         * Note: With job title list (KCP003), this is selected job title id.
         */
        selectedCode: KnockoutObservable<any>;
        
        /**
         * baseDate. Available for job title list only.
         */
        baseDate?: KnockoutObservable<Date>;
        
        /**
         * is dialog, if is main screen, set false,
         */
        isDialog: boolean;

        /**
         * Default padding of KCPs
         */
        hasPadding?: boolean;
        
        /**
         * Select Type.
         * 1 - Select by selected codes.
         * 2 - Select All (Cannot select all while single select).
         * 3 - Select First item.
         * 4 - No select.
         */
        selectType: SelectType;
        
        /**
         * Check is show no select row in grid list.
         */
        isShowNoSelectRow: boolean;
        
        /**
         * check is show select all button or not. Available for employee list only.
         */
        isShowSelectAllButton?: boolean;
        
        /**
         * check is show work place column. Available for employee list only.
         */
        isShowWorkPlaceName?: boolean;
        
        /**
         * Already setting list code. structure: {code: string, isAlreadySetting: boolean}
         * ignore when isShowAlreadySet = false.
         * Note: With job title list (KCP003), structure: {id: string, isAlreadySetting: boolean}.
         */
        alreadySettingList?: KnockoutObservableArray<UnitAlreadySettingModel>;
        
        /**
         * Employee input list. Available for employee list only.
         * structure: {code: string, name: string, workplaceName: string}.
         */
        employeeInputList?: KnockoutObservableArray<UnitModel>;
        
        /**
         * Max rows to visible in list component.
         */
        maxRows: number;
        
        /**
         * Set width for component;
         */
        width?: number;
        
        /**
         * Set max width for component.Min is 350px;
         */
        maxWidth?: number;
        
        /**
         * Set tabindex attr for controls in component.
         * If not set, tabindex will same as spec of KCPs.
         */
        tabindex?: number;
        
        /**
         * Set to display Closure Selection of not. Available for employment list only.
         */
        isDisplayClosureSelection?: boolean;
        
        /**
         * Set to display FullClosure Option in Closure Selection of not. Available for employment list only.
         */
        isDisplayFullClosureOption?: boolean;
        
        /**
         * Closure select type. Available for employment list only.
         * 1. Select FullClosure option.
         * 2. Select by selected closure code.
         * 3. No select.
         */
        closureSelectionType?: ClosureSelectionType; 
        
        /**
         * Selected closure code. Available for employment list only.
         */
        selectedClosureId?: KnockoutObservable<any>;

        /**
         * Show optional column property. Default = false
         */
        showOptionalColumn?: boolean;

        /**
         * Optional column name.
         */
        optionalColumnName?: string;

        /**
         * Optional column datasource
         */
        optionalColumnDatasource?: KnockoutObservableArray<OptionalColumnDataSource>;

        subscriptions?: Array<KnockoutSubscription>;
        
        /**
         * in the select-all case, disableSelection is true. Else false
         */
        disableSelection?: boolean;

        /**
         * Select all item after reload.
         */
        isSelectAllAfterReload?: boolean;
        
        /**
         * when reload gridList, check to remove filter value
         */
        isRemoveFilterWhenReload?: boolean;
        
        backupSelectedCode?: any;
    }
    
    export class ClosureSelectionType {
        static SELECT_FULL_OPTION = 1;
        static SELECT_BY_SELECTED_CODE = 2;
        static NO_SELECT = 3;
    }
    
    export class SelectType {
        static SELECT_BY_SELECTED_CODE = 1;
        static SELECT_ALL = 2;
        static SELECT_FIRST_ITEM = 3;
        static NO_SELECT = 4;
    }
    
    /**
     * List Type
     */
    export class ListType {
        static EMPLOYMENT = 1;
        static Classification = 2;
        static JOB_TITLE = 3;
        static EMPLOYEE = 4;
        static WORKPLACE = 5;
    }
    
    /**
     * Grid style
     */
    export interface GridStyle {
        codeColumnSize: any;
        totalColumnSize: number;
        totalComponentSize: number;
        totalHeight: number;
        rowHeight: number;
        nameColumnSize: any;
        workplaceColumnSize: any;
        alreadySetColumnSize: any;
        optionalColumnSize: any;
    }
    
    /**
     * Tab index.
     */
    export interface TabIndex {
        searchBox: number;
        table: number;
        baseDateInput?: number;
        decideButton?: number;
        selectAllButton?: number;
    }
    
    export interface ClosureItem {
        id: number;
        name: string;
    }
    
    /**
     * Screen Model.
     */
    export class ListComponentScreenModel {
        itemList: KnockoutObservableArray<UnitModel>;
        selectedCodes: KnockoutObservable<any>;
        listComponentColumn: Array<any>;
        isMultipleUse: boolean;
        isMultipleSelect: boolean;
        isDialog: boolean;
        hasPadding: boolean;
        hasBaseDate: boolean;
        baseDate: KnockoutObservable<Date>;
        gridStyle: GridStyle;
        listType: ListType;
        selectType: SelectType;
        componentGridId: string;
        alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
        targetKey: string;
        maxRows: number;
        tabIndex: TabIndex;
        isDisplayClosureSelection: boolean;
        closureSelectionType: ClosureSelectionType;
        selectedClosureId: KnockoutObservable<any>;
        isDisplayFullClosureOption: boolean;
        closureList: KnockoutObservableArray<ClosureItem>;
        isShowNoSelectRow: boolean;
        isShowAlreadySet: boolean;
        isShowWorkPlaceName: boolean;
        showOptionalColumn: boolean;
        optionalColumnName: string;
        optionalColumnDatasource: KnockoutObservableArray<OptionalColumnDataSource>;
        hasUpdatedOptionalContent: KnockoutObservable<boolean>;
        componentWrapperId: string;
        searchBoxId: string;
        disableSelection : boolean;
        componentOption: ComponentOption;
        isSelectAllAfterReload: boolean;
        isRemoveFilterWhenReload: boolean;
        
        constructor() {
            this.itemList = ko.observableArray([]);
            this.listComponentColumn = [];
            this.isMultipleUse = false;
            this.isMultipleSelect = false;
            this.componentGridId = (Date.now()).toString();
            this.alreadySettingList = ko.observableArray([]);
            this.isDisplayClosureSelection = false;
            this.isDisplayFullClosureOption = true;
            this.closureSelectionType = ClosureSelectionType.NO_SELECT;
            this.closureList = ko.observableArray([]);
            this.hasUpdatedOptionalContent = ko.observable(false);
            // set random id to prevent bug caused by calling multiple component on the same page
            this.componentWrapperId = nts.uk.util.randomId();
            this.searchBoxId = nts.uk.util.randomId();
            this.isSelectAllAfterReload = true;
            this.disableSelection = false;
            this.isRemoveFilterWhenReload = true;
        }

        /**
         * Init component.
         */
        public init($input: JQuery, data: ComponentOption) :JQueryPromise<any> {
            var dfd = $.Deferred<any>();
            var self = this;

            $(document).undelegate('#' + self.componentGridId, 'iggriddatarendered');

            // clear subscriptions
            if (data.subscriptions) {
                _.each(data.subscriptions, sub => {
                    sub.dispose();
                });
            }
            
            // Init self data.
            if (!nts.uk.util.isNullOrUndefined(data) && !nts.uk.util.isNullOrUndefined(data.isMultipleUse)) { 
                self.isMultipleUse = data.isMultipleUse; 
            }
            self.componentOption = data;
            self.isMultipleSelect = data.isMultiSelect;
            self.targetKey = data.listType == ListType.JOB_TITLE ? 'id': 'code';
            self.maxRows = data.maxRows ? data.maxRows : 12;
            self.selectType = data.selectType;
            self.selectedCodes = data.selectedCode;
            self.isDialog = data.isDialog;
            self.hasPadding = _.isNil(data.hasPadding) ? true : data.hasPadding; // default = true
            // 複数使用区分　＝　単独使用 : show baseDate
            // 複数使用区分　＝　複数使用 : hide baseDate
            self.hasBaseDate = data.listType == ListType.JOB_TITLE && !data.isMultipleUse;
            self.isShowNoSelectRow = data.isShowNoSelectRow;
            self.isShowAlreadySet = data.isShowAlreadySet;
            self.isShowWorkPlaceName = data.isShowWorkPlaceName;
            self.showOptionalColumn = data.showOptionalColumn ? data.showOptionalColumn : false;
            self.optionalColumnName = data.optionalColumnName;
            self.optionalColumnDatasource = (data.optionalColumnDatasource != null) ? data.optionalColumnDatasource : ko.observableArray([]);
            self.selectedClosureId = ko.observable(null);
            self.isSelectAllAfterReload = _.isNil(data.isSelectAllAfterReload) ? false : data.isSelectAllAfterReload;
            self.disableSelection = data.disableSelection;
            if (data.isRemoveFilterWhenReload !== undefined) { 
                self.isRemoveFilterWhenReload = data.isRemoveFilterWhenReload; 
            }
            
            // Init data for employment list component.
            if (data.listType == ListType.EMPLOYMENT) {
                self.selectedClosureId = _.isFunction(data.selectedClosureId) ?
                    data.selectedClosureId : ko.observable(data.selectedClosureId);
                self.isDisplayClosureSelection = data.isDisplayClosureSelection ? true : false;
                self.isDisplayFullClosureOption = data.isDisplayFullClosureOption ? true : false;
                self.closureSelectionType = data.closureSelectionType ? data.closureSelectionType : ClosureSelectionType.NO_SELECT;
                if (data.isDisplayClosureSelection) {
                    self.initSelectClosureOption(data);
                }
            }
            self.initGridStyle(data);
            self.listType = data.listType;
            self.tabIndex = this.getTabIndexByListType(data);
            if (data.baseDate) {
                self.baseDate = data.baseDate;
            } else {
                self.baseDate = ko.observable(new Date());
            }

            // Setup list column.
            self.setupListColumns();

            // With list type is employee list, use employee input.
            if (self.listType == ListType.EMPLOYEE) {
                self.initEmployeeSubscription(data);
                self.initComponent(data, data.employeeInputList(), $input).done(function() {
                    dfd.resolve(self);
                });
                return dfd.promise();
            }
            
            // Find data list.
            this.findDataList(data.listType).done(function(dataList: Array<UnitModel>) {
                self.initComponent(data, dataList, $input).done(function() {
                    dfd.resolve(self);
                });
            });
            return dfd.promise();
        }

        /**
         * Reload nts grid list
         */
        private reloadNtsGridList(): void {
            let self = this;
            const gridList = $('#' + self.componentGridId);
            const searchBox = $('#' + self.searchBoxId);
            if (!_.isEmpty(gridList) && gridList.hasClass('nts-gridlist')) {
                _.defer(() => {
                    // clear search box before update datasource
                    if (self.isRemoveFilterWhenReload) {
                        searchBox.find('.clear-btn').click();    
                    }

                    // update datasource
                    gridList.ntsGridList("setDataSource", self.itemList());
                    
                    _.defer(() => {
                        if (self.listType !== ListType.EMPLOYEE) {
                            searchBox.ntsSearchBox("setDataSource", self.itemList());        
                        }
    
                        // select all items in multi mode
                        if (self.isSelectAllAfterReload && !_.isEmpty(self.itemList()) && self.isMultipleSelect) {
                            let selectedValues = _.map(self.itemList(), item => self.listType == ListType.JOB_TITLE ? item.id : item.code);
                            if (_.isEmpty(selectedValues)){
                                self.selectedCodes([]);
                            } else {
                                gridList.ntsGridList("setSelectedValue", selectedValues);
                            }
                            
                            /*setTimeout(function() {
                                let chk = gridList.closest('.ui-iggrid').find(".ui-iggrid-rowselector-header").find("span[data-role='checkbox']");
                                if (chk[0].getAttribute("data-chk") == "off") { 
                                    chk.click();
                                }
                            }, 1);*/
                        }    
                    });
                });
            }
        }

        private loadNtsGridList(): void {
            let self = this;
            self.initNoSelectRow();
            self.setOptionalContent();

            _.defer(() => {
                // Set default value when init component.
                self.initSelectedValue();
                
                let options;
                // fix bug constructor of value of knockoutObservableArray != Array.
                const selectedCodes = self.isMultipleSelect ? [].slice.call(self.selectedCodes()) : self.selectedCodes();
                
                if (self.disableSelection) {
                    let selectionDisables = _.map(self.itemList(), 'code');
                    options = {
                        width: self.componentOption.width ? self.componentOption.width : self.gridStyle.totalColumnSize,
                        dataSource: self.itemList(),
                        primaryKey: self.targetKey,
                        columns: self.listComponentColumn,
                        multiple: true,
                        value: selectionDisables,
                        name: self.getItemNameForList(),
                        rows: self.maxRows,
                        selectionDisables: selectionDisables
                    };
                } else {
                    options = {
                        width: self.componentOption.width ? self.componentOption.width : self.gridStyle.totalColumnSize,
                        dataSource: self.itemList(),
                        primaryKey: self.targetKey,
                        columns: self.listComponentColumn,
                        multiple: self.isMultipleSelect,
                        value: selectedCodes,
                        name: self.getItemNameForList(),
                        rows: self.maxRows,
                    };
                }
                
                const searchBoxOptions = {
                    searchMode: 'filter',
                    targetKey: self.targetKey,
                    comId: self.componentGridId,
                    items: self.itemList(),
                    selected: selectedCodes,
                    selectedKey: self.targetKey,
                    fields: ['name', 'code'],
                    mode: 'igGrid'
                };

                // load ntsGrid & searchbox component
                $('#' + self.searchBoxId).ntsSearchBox(searchBoxOptions);
                $('#' + self.componentGridId).ntsGridList(options);

                // setup event
                self.initEvent();

                // set focus if parent screen has no focus
                if (document.activeElement.tagName == 'BODY') {
                    _.defer(() => $('#' + self.searchBoxId + ' .ntsSearchBox').focus());
                }
            });
        }

        // set up on selected code changed event
        private initEvent(): void {
            let self = this;
            const gridList = $('#' + self.componentGridId);
            gridList.on('selectionchanged', evt => {
                const selectedValues = gridList.ntsGridList("getSelectedValue");
                const selectedIds = self.isMultipleSelect ? _.map(selectedValues, o => o.id) : selectedValues.id;
                if(!_.isEqual(self.selectedCodes(), selectedIds)){
                    self.selectedCodes(selectedIds);        
                }
            });
            gridList.on('selectChange', evt => {
                // scroll to top if select all
                if ((!_.isEmpty(self.selectedCodes())) && (self.itemList().length == self.selectedCodes().length)) {
                    gridList.igGrid("virtualScrollTo", '0px');
                }
            });

            self.selectedCodes.subscribe(() => {
                // can not use OUTSIDE "gridList" variable here. must to use $('#' + self.componentGridId)
                let gridList = $('#' + self.componentGridId);
                if (gridList.length > 0) {
                    _.defer(() => {
                        var selectedValues = gridList.ntsGridList("getSelectedValue");
                        if (_.isEmpty(selectedValues)) {
                            gridList.ntsGridList('setSelected', self.selectedCodes());        
                        } else {
                            var selectedIds = self.isMultipleSelect ? _.map(selectedValues, o => o.id) : selectedValues.id;
                            if(!_.isEqual(self.selectedCodes(), selectedIds)){
                                gridList.ntsGridList('setSelected', self.selectedCodes());    
                            }    
                        }    
                    });
                }
            });

        }

        private initEmployeeSubscription(data: ComponentOption): void {
            let self = this;
            if (data.subscriptions) {
                data.subscriptions.push(data.employeeInputList.subscribe(dataList => {
                    self.addAreadySettingAttr(dataList, self.alreadySettingList());
                    self.itemList(dataList);
                }));
            } else {
                data.employeeInputList.subscribe(dataList => {
                    self.addAreadySettingAttr(dataList, self.alreadySettingList());
                    self.itemList(dataList);
                });
            }
        }

        /**
         * Setup list columns
         */
        private setupListColumns(): void {
            let self = this;

            // id column
            if (self.listType == ListType.JOB_TITLE) {
                self.listComponentColumn.push({ headerText: '', hidden: true, prop: 'id' });
            }

            // code column
            self.listComponentColumn.push({
                headerText: nts.uk.resource.getText('KCP001_2'), prop: 'code', width: self.gridStyle.codeColumnSize,
                formatter: function(code) {
                    return code;
                }
            });

            // name column
            self.listComponentColumn.push({
                headerText: nts.uk.resource.getText('KCP001_3'), prop: 'name', width: self.gridStyle.nameColumnSize,
                template: "<td class='list-component-name-col'>${name}</td>",
            });

            // workplace name column
            if (self.listType == ListType.EMPLOYEE && self.isShowWorkPlaceName) {
                self.listComponentColumn.push({
                    headerText: nts.uk.resource.getText('KCP005_4'), prop: 'affiliationName', width: self.gridStyle.workplaceColumnSize,
                    template: "<td class='list-component-name-col'>${affiliationName}</td>"
                });
            }

            // optional column
            if (self.showOptionalColumn) {
                self.addOptionalContentToItemList();
                self.listComponentColumn.push({
                    headerText: self.optionalColumnName, prop: 'optionalColumn', width: self.gridStyle.optionalColumnSize,
                    template: "<td class='list-component-name-col'>${optionalColumn}</td>"
                });
            }

            // Already setting column
            if (self.isShowAlreadySet) {
                self.listComponentColumn.push({
                    headerText: nts.uk.resource.getText('KCP001_4'), prop: 'isAlreadySetting', width: self.gridStyle.alreadySetColumnSize,
                    formatter: function(isAlreadySet) {
                        if (isAlreadySet == true || isAlreadySet == 'true') {
                            return '<div class="already-setting" style="text-align: center;max-height: 18px;"><i class="icon icon-78"></i></div>';
                        }
                        return '';
                    }
                });
            }

        }

        /**
         * Add optional content to item list
         */
        private addOptionalContentToItemList(): void {
            let self = this;
            let mappedList = _.map(self.itemList(), item => {
                const found = _.find(self.optionalColumnDatasource(), vl => vl.empId == item.code);
                item.optionalColumn = found ? found.content : '';
                return item;
            });
            self.hasUpdatedOptionalContent(true);
            self.itemList(mappedList);
        }
        
        /**
         * Inint Closure Selection Type.
         */
        private initSelectClosureOption(data: ComponentOption) {
            var self = this;
            switch(data.closureSelectionType) {
                case ClosureSelectionType.SELECT_FULL_OPTION:
                    if (data.isDisplayFullClosureOption) {
                        self.selectedClosureId(0);
                    }
                    break;
                case ClosureSelectionType.SELECT_BY_SELECTED_CODE:
                    break;
                case ClosureSelectionType.NO_SELECT:
                    self.selectedClosureId(data.isDisplayFullClosureOption ? 0 : 1);
                    break;
                default:
                    break;
            }
        }
        
        /**
         * Init component.
         */
        private initComponent(data: ComponentOption, dataList: Array<UnitModel>, $input: JQuery) :JQueryPromise<void>{
            var dfd = $.Deferred<void>();
            var self = this;

            if (self.showOptionalColumn) {
                self.optionalColumnDatasource.subscribe(vl => {
                    self.addOptionalContentToItemList();
                });
            }

            // Map already setting attr to data list.
            if (!_.isNil(data.alreadySettingList)) {
                self.alreadySettingList = data.alreadySettingList;
                self.addAreadySettingAttr(dataList, self.alreadySettingList());

                // subscribe when alreadySettingList update => reload component.
                self.alreadySettingList.subscribe((newSettings: Array<UnitModel>) => {
                    var currentDataList = self.itemList();
                    self.addAreadySettingAttr(currentDataList, newSettings);
                    self.itemList(currentDataList);
                })
            }
            self.itemList(dataList);
            self.addOptionalContentToItemList();
            
            // Init component.
            var fields: Array<string> = ['name', 'code'];
            if (data.isShowWorkPlaceName) {
                fields.push('affiliationName');
            }

            const startComponent = () => {
                $input.html(LIST_COMPONENT_HTML);
                $input.find('table').attr('id', self.componentGridId);
                ko.cleanNode($input[0]);
                ko.applyBindings(self, $input[0]);
                $input.find('.base-date-editor').find('.nts-input').width(133);
                
                self.loadNtsGridList();

                // ReloadNtsGridList when itemList changed
                self.itemList.subscribe(newList => {
                    self.setOptionalContent();
                    self.initNoSelectRow();
                    self.reloadNtsGridList();
                    self.createGlobalVarDataList(newList, $input);
                });
                
                if (data.listType == ListType.EMPLOYMENT) {
                    self.selectedClosureId.subscribe(id => {
                        self.componentOption.selectedClosureId(id); // update selected closureId to caller's screen
                        self.reloadEmployment(id);
                    });
                }
                $(document).delegate('#' + self.componentGridId, "iggridrowsrendered", function(evt) {
                    self.addIconToAlreadyCol();
                });

                // defined function get data list.
                self.createGlobalVarDataList(dataList, $input);
                $.fn.getDataList = function(): Array<kcp.share.list.UnitModel> {
                    return window['dataList' + this.attr('id').replace(/-/gi, '')];
                }

                // defined function focus
                $.fn.focusComponent = function() {
                    if (self.hasBaseDate) {
                        $input.find('.base-date-editor').first().focus();
                    } else {
                        $input.find(".ntsSearchBox").focus();
                    }
                }
                $.fn.reloadJobtitleDataList = self.reload;
                $.fn.isNoSelectRowSelected = function() {
                    if (self.isMultipleSelect) {
                        return false;
                    }
                    var selectedRow: any = $('#' + self.componentGridId).igGridSelection("selectedRow");
                    if (selectedRow && selectedRow.id === '' && selectedRow.index > -1) {
                        return true;
                    }
                    return false;
                }
                dfd.resolve();
            };

            if (_.isNil(ko.dataFor(document.body))) {
                nts.uk.ui.viewModelApplied.add(startComponent);
            } else {
                startComponent();
            }
            
            if (data.autoAdjustHeight) {
                self.autoAdjustHeight(data, $input);
            }
            
            return dfd.promise();
        }

        /**
         * Set optional content
         */
        private setOptionalContent(): void {
            let self = this;
            if (self.showOptionalColumn && !self.hasUpdatedOptionalContent()) {
                self.addOptionalContentToItemList();
            }
            self.hasUpdatedOptionalContent(false);
        }
        
        /**
         * Add No select row to list
         */
        private initNoSelectRow() {
            var self = this;
            let noSelectRow = _.find(self.itemList(), item => item.code === '');
            
            // Check is show no select row.
            if (self.isShowNoSelectRow && !self.isMultipleSelect && _.isNil(noSelectRow)) {
                self.itemList.unshift({ code: '', id: '', name: nts.uk.resource.getText('KCP001_5'), isAlreadySetting: false });
            }
            if ((!self.isShowNoSelectRow || self.isMultipleSelect) && !_.isNil(noSelectRow)) {
                self.itemList.remove(noSelectRow);
            }
        }
        
        /**
         * Get tab index by list type.
         */
        private getTabIndexByListType(data: ComponentOption): TabIndex {
            if (data.tabindex) {
                return {
                    searchBox: data.tabindex,
                    table: data.tabindex,
                    baseDateInput: data.tabindex,
                    decideButton: data.tabindex
                }
            }
            switch(data.listType) {
                case ListType.EMPLOYMENT, ListType.Classification:
                    return {
                        searchBox: 1,
                        table: 2
                    }
                case ListType.JOB_TITLE:
                    return {
                        searchBox: 3,
                        table: 4,
                        baseDateInput: 1,
                        decideButton: 2
                    }
                case ListType.EMPLOYEE:
                    return {
                        searchBox: 1,
                        table: 2,
                        selectAllButton: 3
                    }
            }
            return {
                searchBox: 1,
                table: 2
            }
        }
        
        /**
         * create Global Data List.
         */
        private createGlobalVarDataList(dataList: Array<UnitModel>, $input: JQuery) {
            var dataListCloned : Array<UnitModel> = _.cloneDeep(dataList);
            _.remove(dataListCloned, item => !item.id && !item.code)
            $('#script-for-' + $input.attr('id')).remove();
            var s = document.createElement("script");
            s.type = "text/javascript";
            s.innerHTML = 'var dataList' + $input.attr('id').replace(/-/gi, '') + ' = ' + JSON.stringify(dataListCloned);
            s.id = 'script-for-' + $input.attr('id');
            $("head").append(s);
        }
        
        /**
         * Add Icon to already column setting
         */
        private addIconToAlreadyCol() {
            // Add icon to column already setting.
            var iconLink = nts.uk.request.location.siteRoot
                .mergeRelativePath(nts.uk.request.WEB_APP_NAME["com"] + '/')
                .mergeRelativePath('/view/kcp/share/icon/icon78.png').serialize();
            $('.icon-78').attr('style', "background: url('" + iconLink + "');width: 20px;height: 20px;background-size: 20px 20px;")
        }
        
        /**
         * Init default selected value.
         */
        private initSelectedValue() {
            var self = this;
            switch(self.selectType) {
                case SelectType.SELECT_BY_SELECTED_CODE:
                    if(self.isShowNoSelectRow && _.isEmpty(self.selectedCodes())) {
                        self.selectedCodes("");
                    }
                    return;
                case SelectType.SELECT_ALL:
                    if (!self.isMultipleSelect){
                        return;
                    }
                    self.selectedCodes(self.itemList().map(item => self.listType == ListType.JOB_TITLE ? item.id : item.code));
                    return;
                case SelectType.SELECT_FIRST_ITEM:
                    self.selectedCodes(_.isEmpty(self.itemList()) ? null : self.selectData(self.itemList()[0]));
                    return;
                case SelectType.NO_SELECT:
                    self.selectedCodes(self.isMultipleSelect ? [] : null);
                    return;
                default:
                    self.selectedCodes(self.isMultipleSelect ? [] : null);
            }
        }
        
        /**
         * Add Aready Setting Attr into data list.
         */
        private addAreadySettingAttr(dataList: Array<UnitModel>, alreadySettingList: Array<UnitModel>) {
            if (this.listType == ListType.JOB_TITLE) {
                // Use id to set already  setting list.
                var alreadyListCode = alreadySettingList.filter(item => item.isAlreadySetting).map(item => item.id);
                dataList.map((item => {
                    item.isAlreadySetting = alreadyListCode.indexOf(item.id) > -1;
                }))
                return;
            }
            var alreadyListCode = alreadySettingList.filter(item => item.isAlreadySetting).map(item => item.code);
            dataList.map((item => {
                item.isAlreadySetting = alreadyListCode.indexOf(item.code) > -1;
            }))
        }
        
        /**
         * Select data for multiple or not
         */
        private selectData(data: UnitModel): any {
            let self = this;
            if (self.isMultipleSelect) {
                return self.listType == ListType.JOB_TITLE ? [data.id] : [data.code];
            }
            if (self.listType == ListType.JOB_TITLE) {
                return data.id;
            }
            return data.code;
        }
        
        /**
         * Init Grid Style.
         */
        private initGridStyle(data: ComponentOption) {
            let self = this;
            var codeColumnSize: any = 50;
            var companyColumnSize: number = 0;
            var heightOfRow : number = data.isMultiSelect ? 24 : 23;
            switch(data.listType) {
                case ListType.EMPLOYMENT:
                    break;
                case ListType.JOB_TITLE:
                    codeColumnSize = 70;
                    break;
                case ListType.Classification:
                    codeColumnSize = 120;
                    break;
                case ListType.EMPLOYEE:
                    codeColumnSize = 120;
                    companyColumnSize = data.isShowWorkPlaceName ? 150 : 0;
                    break;
                default:
                break;
            }
            var alreadySettingColSize = data.isShowAlreadySet ? 70 : 0;
            var multiSelectColSize = data.isMultiSelect ? 55 : 0;
            var totalColumnSize: number = data.maxWidth ? data.maxWidth : codeColumnSize + 170 + companyColumnSize
                + alreadySettingColSize + multiSelectColSize;
            var minTotalSize = 280;
            var totalRowsHeight = heightOfRow * this.maxRows + 20;
            var totalHeight: number = self.calcTotalHeightRev(data);
            

            var optionalColumnSize: any = 0;

            if (this.showOptionalColumn) {
                codeColumnSize = data.maxWidth ? '15%': codeColumnSize;
                var nameColumnSize = data.maxWidth ? '30%' : 170;
                var workplaceColumnSize = data.maxWidth ? '20%' : 150;
                var alreadySetColumnSize = data.maxWidth ? '15%' : 40;
                optionalColumnSize = data.maxWidth ? '20%' : 150;
            } else {
                codeColumnSize = data.maxWidth ? '30%' : codeColumnSize;
                var nameColumnSize = data.maxWidth ? '30%' : 170;
                var workplaceColumnSize = data.maxWidth ? '30%' : 150;
                var alreadySetColumnSize = data.maxWidth ? '15%' : 40;
            }
            this.gridStyle = {
                codeColumnSize: codeColumnSize,
                totalColumnSize: Math.max(minTotalSize, totalColumnSize),
                totalComponentSize: Math.max(minTotalSize, totalColumnSize) + 2,
                totalHeight: totalHeight + totalRowsHeight,
                rowHeight: totalRowsHeight,
                nameColumnSize: nameColumnSize,
                workplaceColumnSize: workplaceColumnSize,
                alreadySetColumnSize: alreadySetColumnSize,
                optionalColumnSize: optionalColumnSize
            };
            
            if (data.maxWidth && data.maxWidth <= 280) {
                data.maxWidth = 280;
            }
        }
        

        private calcTotalHeightRev(data: ComponentOption) {
            var totalHeightRev = this.hasBaseDate || this.isDisplayClosureSelection ? 97 : 55;
            if (data.listType === ListType.EMPLOYEE ) {
                totalHeightRev -= 48;
            }
            return totalHeightRev;
        }
        
        private autoAdjustHeight(data: ComponentOption, $input: JQuery) {
            _.defer(() => {
                $("#" + this.componentWrapperId).css("height", "auto");
                this.resetGridHeight(data, $input);
                $(window).resize(() => this.resetGridHeight(data, $input));
            });
        }
        
        private resetGridHeight(data: ComponentOption, $input: JQuery) {
            
            let offsetTop = $input.offset().top;
            
            var totalHeightRev = this.calcTotalHeightRev(data);
            
            let PADDING = 20;
            let MARGIN = 20;
            let totalHeight = $(window).height() - $input.offset().top - PADDING * 2 - MARGIN;
            
            let $grid = $('#' + this.componentGridId);
            $grid.igGrid("option", "height", totalHeight - totalHeightRev);
        }
        
        /**
         * Find data list.
         */
        private findDataList(listType: ListType):JQueryPromise<Array<UnitModel>> {
            var self = this;
            var dfd = $.Deferred<any>();
            switch(listType) {
                case ListType.EMPLOYMENT:
                    if (self.isDisplayClosureSelection) {
                        // Find all closure in current month.
                        service.findAllClosure().done((items: ClosureItem[]) => {
                            items = _.sortBy(items, item => item.id);
                            self.closureList(items);
                            // if show FullClosureOption -> add option.
                            if (self.isDisplayFullClosureOption) {
                                self.closureList.unshift({id: 0, name: nts.uk.resource.getText('CCG001_64')})
                            }
                            var selectedClosure = 0;
                            if (!self.selectedClosureId()) {
                                selectedClosure = self.closureList().length > 1 ? self.closureList()[0].id : 0;
                            } else {
                                selectedClosure = self.selectedClosureId();
                            }
                            service.findEmployments(selectedClosure).done(data => {
                                dfd.resolve(data);
                            });
                        })
                        return dfd.promise();
                    } else {
                        if (self.selectedClosureId()){
                            return service.findEmployments(self.selectedClosureId());
                        }
                        return service.findAllEmployments();
                    }
                case ListType.JOB_TITLE:
                    return service.findJobTitles(this.baseDate());
                case ListType.Classification:
                    return service.findClassifications();
                case ListType.WORKPLACE:
                    return service.findAllWorkplace();
                default:
                    return;
            }
        }
        
        /**
         * Select all.
         */
        public selectAll() {
            var self = this;
            if (self.itemList().length == 0 || !self.isMultipleSelect) {
                return;
            }
            const gridList = $('#' + self.componentGridId);
            const allSelectedCodes = gridList.ntsGridList("getDataSource").map(item => item.code);
            self.selectedCodes(allSelectedCodes);
            gridList.ntsGridList("setSelectedValue", allSelectedCodes);
        }
        
        /**
         * Reload screen data.
         */
        public reload() {
            var self = this;
            // Check if is has base date.
            if ((self.hasBaseDate && (!self.baseDate() || self.baseDate().toString() == ''))||nts.uk.ui.errors.hasError()) {
                return;
            }
            self.findDataList(self.listType).done((data: UnitModel[]) => {
                if (self.alreadySettingList) {
                    self.addAreadySettingAttr(data, self.alreadySettingList());
                }
                _.defer(() => {
                    self.itemList(data);
                });
            });
        }
        
        /**
         * Reload employment.
         */
        private reloadEmployment(closureId: number) {
            var self = this;
            service.findEmployments(closureId).done(function(data: UnitModel[]) {
                if (!_.isEmpty(self.alreadySettingList())) {
                    self.addAreadySettingAttr(data, self.alreadySettingList());
                }
                self.itemList(data);
                let check = _.filter(data, (i: any) => { return self.componentOption.backupSelectedCode && i.code == self.componentOption.backupSelectedCode[0]; });
                if (check.length > 0) {
                    self.componentOption.selectedCode(self.componentOption.backupSelectedCode);
                } else {
                    self.componentOption.selectedCode([]);
                }
            })
        }
        
        /**
         * Get item name for each component type.
         */
        public getItemNameForList(): string {
            switch(this.listType) {
                case ListType.EMPLOYMENT:
                    return '#[KCP001_1]';
                case ListType.JOB_TITLE:
                    return '#[KCP003_1]';
                case ListType.Classification:
                    return '#[KCP002_1]';
                case ListType.EMPLOYEE:
                    return '#[KCP005_1]';
                case ListType.WORKPLACE:
                    return '#[KCP012_1]';
                default:
                    return '';
            }
        }
        
        public getItemNameForBaseDate(): string {
            if (this.hasBaseDate) {
                return '#[KCP003_2]'
            }
            return '';
        }
    }

    
    /**
     * Service,
     */
    export module service {
        
        // Service paths.
        var servicePath = {
            findEmployments: "bs/employee/employment/findAll/",
            findJobTitles: 'bs/employee/jobtitle/findAll',
            findClassifications: 'bs/employee/classification/findAll',
            findAllClosureItems: 'ctx/at/shared/workrule/closure/find/currentyearmonthandused',
            findEmploymentByClosureId: 'ctx/at/shared/workrule/closure/findEmpByClosureId/',
            findEmploymentByCodes: 'bs/employee/employment/findByCodes',
            findAllWorkplace: 'at/record/worklocation/kcp012'
        }
        
        /**
         * Find Employment list.
         */
        export function findEmployments(closureId?: number): JQueryPromise<Array<UnitModel>> {
            
            // Find Employment Closure.
            var dfd = $.Deferred<Array<UnitModel>>();
            nts.uk.request.ajax('at', servicePath.findEmploymentByClosureId + closureId).done(function(empList: Array<any>) {
                if (empList && empList.length > 0) {
                    // Find by employment codes.
                    nts.uk.request.ajax('com', servicePath.findEmploymentByCodes, empList).done(data => {
                        data = _.sortBy(data,['code']);
                        dfd.resolve(data);
                    })
                    return dfd.promise();
                }
                dfd.resolve([])
            })
            return dfd.promise();
        }
        
        export function findAllEmployments(): JQueryPromise<Array<UnitModel>>{
            let dfd = $.Deferred<Array<UnitModel>>();
            nts.uk.request.ajax('com', servicePath.findEmployments).done((data: any) =>{
                data =_.sortBy(data,['code']);
                dfd.resolve(data);
            });
            return dfd.promise();
        }
        
        /**
         * Find Closure list.
         */
        export function findAllClosure(): JQueryPromise<Array<ClosureItem>> {
            return nts.uk.request.ajax('at', servicePath.findAllClosureItems);
        }
        
        /**
         * Find Job title.
         */
        export function findJobTitles(baseDate: Date): JQueryPromise<Array<UnitModel>> {
            return nts.uk.request.ajax('com', servicePath.findJobTitles, {baseDate: baseDate});
        }
        
        /**
         * Find Classification list.
         */
        export function findClassifications(): JQueryPromise<Array<UnitModel>> {
            return nts.uk.request.ajax('com', servicePath.findClassifications);
        }
        

        /**
         * Find Workplace list.
         */
        export function findAllWorkplace(): JQueryPromise<Array<UnitModel>> {
            return nts.uk.request.ajax('at', servicePath.findAllWorkplace);
        }

    }

    export class ListComponentTextResource {
        static KCP003_2 = nts.uk.resource.getText('KCP003_2');
        static KCP003_3 = nts.uk.resource.getText('KCP003_3');
        static KCP004_7 = nts.uk.resource.getText('KCP004_7');
        static CCG001_28  = nts.uk.resource.getText('CCG001_28');
    }

var LIST_COMPONENT_HTML = `<style type="text/css">
#nts-component-list table tr td {
    white-space: nowrap;
}

.float-left {
    float: left;
}
</style>
    <div style="border-radius: 5px;"
        data-bind="style: {width: gridStyle.totalComponentSize + 'px',
                height: gridStyle.totalHeight + 'px',
                padding: hasPadding ? '20px' : '0px'},
                css: {'caret-right caret-background bg-green': !isDialog},
                attr: {id: componentWrapperId}">
        <!-- ko if: (!isDialog && listType !== kcp.share.list.ListType.EMPLOYEE) -->
            <i class="icon icon-searchbox"></i>
        <!-- /ko -->
        <!-- ko if: hasBaseDate -->
            <div data-bind="ntsFormLabel: {}" style="margin-bottom: 10px;">`+ListComponentTextResource.KCP003_2+`</div>
            <div class="base-date-editor" style="margin-left: 20px;"
                data-bind="attr: {tabindex: tabIndex.baseDateInput}, ntsDatePicker: {dateFormat: 'YYYY/MM/DD', value: baseDate, name: getItemNameForBaseDate(), required: true}"></div>
            <div style="display: inline-flex">
                <button data-bind="attr: {tabindex: tabIndex.decideButton}, click: reload"
                    style="width: 50px">`+ListComponentTextResource.KCP003_3+`</button>
            </div>
        <!-- /ko -->
        <!-- Upgrade: Search By closureId-->
        <!-- ko if: isDisplayClosureSelection -->
            <div style="margin-bottom: 10px">
                <div data-bind="ntsFormLabel: {required: true}"
                    style="float: left; margin-left: 10px; margin-right: 15px;">`+ListComponentTextResource.CCG001_28+`</div>
                <div id="combo-box" style="min-width: 160px;"
                    data-bind="ntsComboBox: {
                                        options: closureList,
                                        optionsValue: 'id',
                                        visibleItemsCount: 5,
                                        value: selectedClosureId,
                                        enable: true,
                                        columns: [
                                            { prop: 'name', length: 4 },
                                        ]}"></div>
            </div>
        <!-- /ko -->
        <!-- End of Upgrade -->

        <!-- ko if: (listType !== kcp.share.list.ListType.EMPLOYEE  ) -->
            <div data-bind=" attr: {id: searchBoxId}, style:{width: gridStyle.totalComponentSize + 'px'}" style="display: inline-block"></div>
        <!-- /ko -->
        <table id="grid-list-all-kcp"></table>
    </div>`;
}

/**
 * Defined Jquery interface.
 */
interface JQuery {

    /**
     * Nts list component.
     * This Function used after apply binding only.
     */
    ntsListComponent(option: kcp.share.list.ComponentOption): JQueryPromise<void>;
    
    /**
     * Get data list in component.
     */
    getDataList(): Array<kcp.share.list.UnitModel>;
    
    /**
     * Focus component.
     */
    focusComponent(): void;
    
    /**
     * Function reload job title data list. Support job title list only.
     */
    reloadJobtitleDataList(): void;
    
    /**
     * Check isNoSelectRowSelected.
     */
    isNoSelectRowSelected(): boolean;
}

(function($: any) {
    $.fn.ntsListComponent = function(option: kcp.share.list.ComponentOption): JQueryPromise<void> {
        let $list = $(this);
        // Return.
        let dfd = $.Deferred<any>();
        new kcp.share.list.ListComponentScreenModel().init(this, option).done(list => {
            $list.data("ntsListComponent", list);
            dfd.resolve(list);
        }).fail((er) => {
            dfd.reject(er);
        });
        
        return dfd.promise();
    }
    
    $.fn.ntsListComponentApi = function(action: string, param?: any): any {
        let $list = $(this);
        switch (action) {
            case 'getSelectedRecords': {
                let list: kcp.share.list.ListComponentScreenModel = $list.data("ntsListComponent");
                
                if(_.isEmpty(list.selectedCodes())){
                    return [];
                }
                
                let isId = list.listType === kcp.share.list.ListType.JOB_TITLE;
                
                return _.filter(list.itemList(), (item: kcp.share.list.UnitModel)=> {
                    if (list.isMultipleSelect) {
                        return list.selectedCodes().includes(isId ? item.id : item.code);
                    }
                    return list.selectedCodes() === (isId ? item.id : item.code);
                });
            }
        }
    }
    
} (jQuery));