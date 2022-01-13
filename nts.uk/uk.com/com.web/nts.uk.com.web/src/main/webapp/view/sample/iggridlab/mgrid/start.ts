module nts.uk.ui.gridlist {
    
    __viewContext.ready(function () {
    
        class ScreenModel {
            
            items = (function () {
                var list = [];
                for (var i = 0; i < 320; i++) {
                    list.push(new GridItem(i));
                }
                return list;
            })();
        }
        
        class GridItem {
            id: number;
            flexImage: string;
            flag: boolean;
            ruleCode: number;
            select: boolean;
            time: string;
            addressCode1: string;
            addressCode2: string;
            address1: string;
            fullDate: Date;
            yearMonth: string;
            year: string;
            address2: string;
            comboCode1: number;
            combo: string;
            header0: string;
            workplace: string;
            comboCode2: number;
            header01: string;
            header1: string;
            header2: string;
            header3: number;
            header4: string;
            header5: string;
            header6: string;
            alert: string;
            constructor(index: number) {
                this.id = index;
                this.flexImage = index % 3 == 0 ? "1" : null;
                this.flag = index % 2 == 0;
                this.ruleCode = index;
                this.select = index % 2 != 0;
                this.time = _.random(0, 16) + ":" + _.random(10, 59); //"13:36";
                this.addressCode1 = "111";
                this.addressCode2 = "002";
                this.address1 = index + "";
                this.fullDate = new Date(Date.UTC(2010, 10, _.random(1, 30)));
                this.yearMonth = "200" + _.random(0, 9) + "/01";
                this.year = "200" + _.random(0, 9);
                this.address2 = "愛知県日本";
                this.comboCode1 = String(index % 3 + 1);
                this.combo = String(index % 3 + 1);
                this.header0 = "Out";
                this.workplace = "Not select";
                this.comboCode2 = String(index % 3 + 4);
                this.header01 = String(index % 3 + 4);
                this.header02 = String(index % 3 + 1);
                this.header1 = "001";
                this.header2 = "内容１２";
                this.header3 = index % 9;
                this.header4 = "内容４";
                this.header5 = "002"; 
                this.header6 = "内容５６";
                this.alert = "Act";
            }
        }
        
        var model = new ScreenModel();
        
        class ItemModel {
            code: string;
            name: string;
    
            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }
        
        var comboItems = [ new ItemModel('1', '基本給'),
                            new ItemModel('2', '役職手当'),
                            new ItemModel('3', '基本給2') ];
        let newComboItems1 = [ new ItemModel("4", "NewText4"),
                              new ItemModel("5", "NewText5"),
                              new ItemModel("6", "NewText6"),
                              new ItemModel("7", "NewText7")];
        let newComboItems2 = [ new ItemModel("7", "NewText7"),
                              new ItemModel("8", "NewText8"),
                              new ItemModel("9", "NewText9"),
                              new ItemModel("10", "NewText10"),
                              new ItemModel("11", "NewText11") ];
        var comboColumns = [{ prop: 'code', length: 1 },
                            { prop: 'name', length: 4 }];
        var comboItems2 = [ new ItemModel('4', '基本給'),
                            new ItemModel('5', '役職手当'),
                            new ItemModel('6', '基本給2') ];
        var comboItems3 = [ new ItemModel('1', 'Text1'),
                            new ItemModel('2', 'Text2'),
                            new ItemModel('3', 'Text3')];
        
        class CellState {
            rowId: number;
            columnKey: string;
            state: Array<any>
            constructor(rowId: string, columnKey: string, state: Array<any>) {
                this.rowId = rowId;
                this.columnKey = columnKey;
                this.state = state;
            }
        }
        var statesTable = [];
        statesTable.push(new CellState(0, "address1", [nts.uk.ui.mgrid.color.Error, nts.uk.ui.mgrid.color.Alarm]));
        statesTable.push(new CellState(0, "time", [nts.uk.ui.mgrid.color.ManualEditTarget, nts.uk.ui.mgrid.color.ManualEditOther]));
        statesTable.push(new CellState(1, "time", [nts.uk.ui.mgrid.color.Reflect, nts.uk.ui.mgrid.color.Calculation]));
        statesTable.push(new CellState(5, "time", [nts.uk.ui.mgrid.color.Disable]));
        statesTable.push(new CellState(6, "header0", [nts.uk.ui.mgrid.color.Disable]));
        statesTable.push(new CellState(1, "header3", [nts.uk.ui.mgrid.color.Disable]));
        statesTable.push(new CellState(1, "fullDate", [nts.uk.ui.mgrid.color.Lock]));
        statesTable.push(new CellState(1, "comboCode1", [nts.uk.ui.mgrid.color.Lock]));
        statesTable.push(new CellState(1, "combo", [nts.uk.ui.mgrid.color.Lock]));
        statesTable.push(new CellState(1, "ruleCode", [nts.uk.ui.mgrid.color.Lock]));
        for (let i = 1; i < 200; i++) {
            if (i % 2 === 0) {
                statesTable.push(new CellState(i, "address1", [nts.uk.ui.mgrid.color.Alarm, nts.uk.ui.mgrid.color.Reflect]));
                statesTable.push(new CellState(i, "comboCode1", [nts.uk.ui.mgrid.color.Disable]));
                statesTable.push(new CellState(i, "combo", [nts.uk.ui.mgrid.color.Disable]));
            }
            if (i === 3) {
                statesTable.push(new CellState(i, "alert", [nts.uk.ui.mgrid.color.Disable]));
                statesTable.push(new CellState(i, "flag", [nts.uk.ui.mgrid.color.Disable]));
            }
            statesTable.push(new CellState(i, "addressCode1", [nts.uk.ui.mgrid.color.Disable]));
            statesTable.push(new CellState(i, "time", [nts.uk.ui.mgrid.color.Disable]));
            statesTable.push(new CellState(i, "address1", [nts.uk.ui.mgrid.color.Disable]));
            statesTable.push(new CellState(i, "address2", [nts.uk.ui.mgrid.color.Disable]));
            statesTable.push(new CellState(i, "header4", ["text-color1", "bold-text"]));
            statesTable.push(new CellState(i, "header5", ["#B3AEF1", "italic-text"]));
        }
        
        let comboPattern = {
            3: 0,
            8: 0,
            9: 1,
            11: 1,
            30: 0,
            39: 1,
            60: 1,
            84: 0,
            90: 0,
            99: 0,
            106: 1,
            139: 0
        }, patternArr = [ newComboItems1, newComboItems2 ];
        
        class TextColor {
            rowId: number;
            columnKey: string;
            color: string;
            constructor(rowId: any, columnKey: string, color: string) {
                this.rowId = rowId;
                this.columnKey = columnKey;
                this.color = color;
            } 
        }
        let colorsTable = [];
        for (let i = 0; i < 400; i++) {
            if (i % 7 === 0) {
                colorsTable.push(new TextColor(i, "id", "text-color1"));
            } else if (i % 3 === 0) {
                colorsTable.push(new TextColor(i, "id", "#B3AEF1"));    
            }
        }
        
        class TextStyle {
            rowId: any;
            columnKey: string;
            style: string;
            constructor(rowId: any, columnKey: string, style: string) {
                this.rowId = rowId;
                this.columnKey = columnKey;
                this.style = style;
            }
        }
        let textStyles = [];
        for (let i = 0; i < 10; i++) {
            if (i % 3 === 0) { 
                textStyles.push(new TextStyle(i, "header0", "italic-text"));
            } else {
                textStyles.push(new TextStyle(i, "header0", "bold-text"));
            } 
        }
        
        class RowState {
            rowId: number;
            disable: boolean;
            constructor(rowId: number, disable: boolean) {
                this.rowId = rowId;
                this.disable = disable;
            }
        }
        let rowStates = [];
        for (let i = 9; i < 10; i++) {
            rowStates.push(new RowState(i, true));
        }
        
        let keys = [];
        for (let i = 0; i < 300; i++) {
            keys.push(i);
        }
//        $("#grid").mGrid({ 
        new nts.uk.ui.mgrid.MGrid($("#grid")[0], {
                            width: '1500px',
                            height: '800px',
                            headerHeight: '80px',
                            dataSource: model.items,
                            dataSourceAdapter: function(ds) {
                                return ds;
                            },
                            primaryKey: 'id',
                            primaryKeyDataType: 'number',
                            rowVirtualization: true,
                            virtualization: true,
                            virtualizationMode: 'continuous',
                            enter: 'right',
                            autoFitWindow: true,
                            hideZero: false,
                            preventEditInError: false,
//                            userId: "4",
//                            getUserId: function(k) { return String(k); },
                            errorColumns: [ "ruleCode" ],
                            errorsOnPage: true,
                            idGen: function(id) { return id + 10000 + _.random(1000); },
//                            minRows: 31,
//                            maxRows: 50,
//                            recordKeys: keys, 
//                            avgRowHeight: 36,
//                            autoAdjustHeight: false,
//                            adjustVirtualHeights: false,
                            columns: [
                                { headerText: '', key: 'rowNumber', dataType: 'number', width: '40px' }, 
                                { headerText: 'ID', key: 'id', dataType: 'number', width: '60px', ntsControl: 'Label', hidden: true },
                                { headerText: 'Image', key: 'flexImage', dataType: 'string', width: '60px', ntsControl: 'FlexImage' },
//                                { headerText: 'Picture', key: "picture", dataType: "string", width: '60px', ntsControl: 'Image' },
//                                { headerText: 'Common1',
//                                    group: [
                                        { headerText: 'FLAG', key: 'flag', dataType: 'boolean', width: '60px', checkbox: true, ntsControl: 'Checkbox', hidden: false },
                                            { headerText: 'RULECODE', key: 'ruleCode', dataType: 'number', width: '100px',
                                                            constraint: { 
                                                                primitiveValue: 'ProcessingNo',
                                                                required: true
                                                            }
                                            },
//                                    ]},
                                { headerText: "表示", key: "show", dataType: "string", width: '40px', ntsControl: 'Image' },
                                { headerText: "追加", key: "add", dataType: "string", width: "40px", ntsControl: "ImageAdd" },
                                { headerText: "Select", key: "select", dataType: "boolean", width: "40px", columnCssClass: "halign-center", checkbox: true, ntsControl: "Checkbox1" },
                                { headerText: 'Inbound time', key: 'time', width: '140px', columnCssClass: "halign-right", headerCssClass: "center-align",
                                                constraint: { 
//                                                                primitiveValue: 'SampleTimeClock',
                                                                cDisplayType: "TimeWithDay",
                                                                min: "-12:00", max: "71:59",
                                                                required: true
                                                            }
                                },
                                { headerText: 'Address',
                                    group: [
                                            { headerText: 'Address<br/>Code1', key: 'addressCode1', dataType: 'string', width: '150px', columnCssClass: 'currency-symbol',
                                                constraint: {
                                                    cDisplayType: "Currency",
                                                    min: 1, max: 99999,
                                                    required: true
                                                }},
                                            { headerText: 'Address1', key: 'address1', dataType: 'string', width: '150px', grant: true},
                                ]},
                                { headerText: 'Full date', key: 'fullDate', dataType: 'date', width: '100px', ntsControl: 'DatePicker',
                                    constraint: { 
                                        cDisplayType: "Date",
                                        required: true,
                                        type: 'ymd'
                                    }
                                },
                                { headerText: 'Year month', key: 'yearMonth', dataType: 'string', width: '70px', ntsControl: 'DatePickerYM',
                                    constraint: {
                                        cDisplayType: "Date",
                                        required: true,
                                        type: "ym"
                                    }
                                },
                                { headerText: 'Year', key: 'year', dataType: 'string', width: '40px', ntsControl: 'DatePickerY',
                                    constraint: {
                                        cDisplayType: "Date",
                                        required: true,
                                        type: "y"
                                    }
                                },
                                { headerText: 'Combo1',
                                    group: [
                                            { headerText: 'コンボボックス状況ComboCode1', key: 'comboCode1', dataType: 'string', width: '60px', ntsType: 'comboCode_combo',
                                                constraint: {
                                                    cDisplayType: "Integer",
//                                                    min: 1, max: 3,
                                                    values: [1, 3],
                                                    required: true
                                                }},
                                            { headerText: 'Combo', key: 'combo', dataType: 'string', width: '230px', ntsControl: 'Combobox' },
                               ]},
                                { headerText: 'Header0', key: 'header0', dataType: 'string', width: '150px', ntsControl: 'Label', 
                                        click: function(id, key) { 
                                            console.log(id + " - " + key);
                                        }
                                },
                                { headerText: 'Combo2',
                                    group: [
                                            { headerText: 'ComboCode2', key: 'comboCode2', dataType: 'string', width: '60px', ntsType: 'comboCode_header01', 
                                                constraint: { primitiveValue: "JobTitleCode" }},
                                            { headerText: 'Header01', key: 'header01', dataType: 'string', width: '500px', ntsControl: 'Combobox2' },
                                ]},
                                { headerText: 'Header02', key: 'header02', dataType: 'string', width: '500px', ntsControl: 'Combobox3' },
                                { headerText: 'Workplace', key: "workplace", dataType: 'string', width: '190px', ntsControl: 'WpButton' },
                                { headerText: '住所',
                                    group: [
                                            { headerText: 'Address<br/>Code2', key: 'addressCode2', dataType: 'string', width: '150px', inputProcess: inputProcess },
                                            { headerText: 'Address2', key: 'address2', dataType: 'string', width: '150px'},
                               ]},
                                { headerText: 'Header12',
                                    group: [
                                            { headerText: 'Header<br/>1', key: 'header1', dataType: 'string', width: '150px', ntsType: 'code_header2', inputProcess: inputProcess, onChange: search,
                                                constraint: {
                                                    cDisplayType: "Integer",
                                                    min: 1, max: 3,
                                                    required: true
                                                } },
                                            { headerText: 'Header2', key: 'header2', dataType: 'string', width: '150px', ntsControl: 'Link1' },
                                   ]},
                                { headerText: 'Header3', key: 'header3', dataType: 'number', width: '150px'/*, ntsControl: 'TextEditor'*/ },
                                { headerText: 'Header4', key: 'header4', dataType: 'string', width: '150px', japanese: true },
                                { headerText: 'Header56',
                                    group: [
                                            { headerText: 'Header<br/>5', key: 'header5', dataType: 'string', width: '150px', ntsType: 'code_header6', onChange: search,
                                                constraint: { primitiveValue: "JobTitleCode" }},
                                            { headerText: 'Header6', key: 'header6', dataType: 'string', width: '150px', ntsControl: 'Link2' },
                                   ]},
//                                { headerText: 'Delete', key: 'delete', dataType: 'string', width: '90px', unbound: true, ntsControl: 'DeleteButton' }
                                { headerText: 'Button', key: 'alert', dataType: 'string', width: '90px', ntsControl: 'Button' }
                            ], 
                            features: [
//                                { name: 'Sorting', type: 'local' },
                                        { name: 'Resizing',
                                            columnSettings: [{
                                                columnKey: 'id', allowResizing: false, minimumWidth: 0
                                            }]
                                        },
                                        {
                                            name: 'WidthSaving',
                                            reset: false
                                        },
                                        {
                                            name: 'Paging',
                                            pageSize: 100,
                                            currentPageIndex: 0,
                                            loaded: function() {}
                                        },
                                        {
                                            name: "Sorting",
                                            columnSettings: [
                                                { columnKey: "time", allowSorting: true, type: "Time" },
                                                { columnKey: "ruleCode", allowSorting: true, type: "Number" },
                                                { columnKey: "combo", allowSorting: true },
                                                { columnKey: "fullDate", allowSorting: true, type: "FullDate" },
                                                { columnKey: "yearMonth", allowSorting: true, type: "YearMonth" },
                                                { columnKey: "year", allowSorting: true }
                                            ]
                                        }, 
                                        {
                                            name: "ColumnMoving"
                                        },
                                        { name: 'Copy' },
                                        { 
                                            name: 'CellStyles',
                                            states: statesTable
                                        },
                                        {
                                            name: 'HeaderStyles',
                                            columns: [
                                                { key: 'ruleCode', colors: ['header1'] },
                                                { key: 'addressCode1', colors: ['header1'] },
                                                { key: 'address1', colors: ['header1'] },
                                                { key: 'comboCode1', colors: ['header2'] },
                                                { key: 'combo', colors: ['header2'] },
                                                { key: 'header3', colors: ['header2'] },
                                                { key: 'header0', colors: ['header1'] },
                                                { key: 'time', colors: ['#E9AEF1'] }
                                            ]
                                        },
                                        { name: "Sheet", 
                                          initialDisplay: "sheet1",
                                          sheets: [ 
                                                    { name: "sheet1", text: "Sheet 1", columns: ["select", "time", "addressCode1", "address1", "fullDate", "yearMonth", "year", "comboCode1", "combo", "addressCode2", "address2", "header0", "comboCode2", "header01", "workplace", "header02"] }, 
                                                    { name: "sheet2", text: "Sheet 2", columns: ["addressCode1", "address1", "time", "header1", "header2", "header3", "header4", "header5", "header6", "alert"] }
                                                  ]
                                        },
                                        { name: 'ColumnFixing', fixingDirection: 'left',
//                                            syncRowHeights: true,
                                            showFixButtons: false,
                                            columnSettings: [
                                                            { columnKey: 'rowNumber', isFixed: true },
                                                            { columnKey: 'id', isFixed: true },
                                                            { columnKey: 'flexImage', isFixed: true },
                                                            { columnKey: 'show', isFixed: true },
                                                            { columnKey: 'add', isFixed: true },
                                                             { columnKey: 'flag', isFixed: true },
                                                             { columnKey: 'ruleCode', isFixed: true } ]},
                                        { name: 'Tooltip', 
                                            error: true
                                        },
                                        { name: 'Summaries',
                                          columnSettings: [
                                            { columnKey: 'flexImage', summaryCalculator: "合計" },
                                            { columnKey: 'time', summaryCalculator: "Time" },
                                            { columnKey: 'ruleCode', summaryCalculator: "Number" },
                                            { columnKey: 'header3', summaryCalculator: "Number" },
                                            { columnKey: 'addressCode1', summaryCalculator: "Number", formatter: "Currency" }
                                          ]
                                        }
                                      ],
                            ntsFeatures: [
                                            { name: 'CellEdit' },
//                                            { name: 'Storage',
//                                                type: 'Remote',
//                                                loadPath: 'sample/store/load',
//                                                savePath: 'sample/store/save'
//                                            },
                                            { name: 'CellColor', columns: [ 
                                                  { 
                                                    key: 'ruleCode', 
                                                    parse: function(value) {
                                                        return value;
                                                    }, 
                                                    map: function(result) {
                                                        if (result <= 1) return "#00b050";
                                                        else if (result === 2) return "pink";
                                                        else return "#0ff";
                                                    } 
                                                  } 
                                                ]
                                            },
                                            {
                                                name: 'RowState',
                                                rows: rowStates
                                            },
                                            {
                                                name: "LoadOnDemand",
                                                allKeysPath: "/sample/lazyload/keys",
                                                pageRecordsPath: "/sample/lazyload/data",
                                            }
                                         ],
                            ntsControls: [{ name: 'Checkbox', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true, onChange: function() {} },
                                            { name: 'Checkbox1', options: { value: 1, text: '' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true, onChange: function() {} },
                                            { name: 'WpButton', text: 'Select', enable: true, click: function() {}, controlType: "ReferButton" },
                                            { name: 'Combobox', options: comboItems, optionsValue: 'code', optionsText: 'name', columns: comboColumns, editable: false, displayMode: 'codeName', controlType: 'ComboBox', enable: true, spaceSize: 'small' },
                                            { name: 'DeleteButton', text: 'Delete', controlType: 'DeleteButton', enable: true },
                                            { name: 'Button', controlType: 'Button', text: 'Warn me', enable: true, click: function() { alert("Oops!!"); } },
                                            { name: 'Combobox2', options: comboItems2, optionsValue: 'code', optionsText: 'name', columns: comboColumns, editable: false, displayMode: 'name', controlType: 'ComboBox', enable: true },
                                            { name: 'Combobox3', options: comboItems3, optionsValue: 'code', optionsText: 'name', columns: comboColumns, editable: false, displayMode: 'name', controlType: 'ComboBox', enable: true,
                                                pattern: patternArr, list: comboPattern 
                                            },
                                            { name: 'DatePicker', format: 'ymd', controlType: 'DatePicker' },
                                            { name: 'DatePickerYM', format: 'ym', controlType: 'DatePicker' },
                                            { name: 'DatePickerY', format: 'y', controlType: 'DatePicker' },
                                            { name: 'Link1', click: function() { alert('Do something.'); }, controlType: 'LinkLabel' },
                                            { name: 'Link2', click: function() { alert('Do something.'); }, controlType: 'LinkLabel' },
                                            { name: 'FlexImage', source: 'ui-icon ui-icon-info', click: function() { alert('Show!'); }, controlType: 'FlexImage' },
                                            { name: 'Image', source: 'hidden-button', controlType: 'Image' },
                                            { name: "ImageAdd", source: "plus-button", controlType: "Image", copy: true },
                                            { name: 'TextEditor', controlType: 'TextEditor', constraint: { valueType: 'Integer', required: true, format: "Number_Separated" } }]
//                                            { name: 'TextEditor', controlType: 'TextEditor', constraint: { valueType: 'Time', required: true, format: "Time_Short_HM" } }]
                            })
                            .create();
        $("#run").on("click", function() {
        });
        $("#update-row").on("click", function() {
            $("#grid2").ntsGrid("updateRow", 0, { flag: false, ruleCode: '6', combo: '3' });
        });
        $("#enable-ctrl").on("click", function() {
            $("#grid2").ntsGrid("enableNtsControlAt", 1, "combo", "ComboBox");
        });
        $("#disable-ctrl").on("click", function() {
            $("#grid2").ntsGrid("disableNtsControlAt", 1, "combo", "ComboBox");
        });
        $("#enable-ctrls").on("click", function() {
            $("#grid2").ntsGrid("enableNtsControls", "header01", "ComboBox");
        });
        $("#disable-ctrls").on("click", function() {
            let data = { name: 'Combobox', width: '100px', options: comboItems, optionsValue: 'code', optionsText: 'name', columns: comboColumns, editable: false, displayMode: 'codeName', controlType: 'ComboBox', enable: true };
            let start = performance.now();
            let $combo = nts.uk.ui.mgrid.ntsControls.comboBox({initValue: 1, controlDef: data});
            console.log(performance.now() - start);
            document.getElementById("combo").appendChild($combo);
        });
        
        
        function totalNumber(data) {
            let total = 0;
            let currentPageIndex = $("#grid2").igGridPaging("option", "currentPageIndex");
            let pageSize = $("#grid2").igGridPaging("option", "pageSize");
            let startIndex = currentPageIndex * pageSize;
            let endIndex = startIndex + pageSize;
            _.forEach(data, function(d, i) {
                if (i < startIndex || i >= endIndex) return;
                let n = parseInt(d);
                if (!isNaN(n)) total += n;
            });
            return total;
        }
        function totalTime(data) {
            let currentPageIndex = $("#grid2").igGridPaging("option", "currentPageIndex");
            let pageSize = $("#grid2").igGridPaging("option", "pageSize");
            let startIndex = currentPageIndex * pageSize;
            let endIndex = startIndex + pageSize;
            let total = moment.duration("0");
            _.forEach(data, function(d, i) {
                if (i < startIndex || i >= endIndex) return;
                total.add(moment.duration(d));
            });
            let time = total.asHours();
            let hour = Math.floor(time);
            let minute = (time - hour) * 60;
            let roundMin = Math.round(minute);
            let minuteStr = roundMin < 10 ? ("0" + roundMin) : String(roundMin);
            return hour + ":" + minuteStr;
        }
        function search(a, b, val) {
            let dfd = $.Deferred();
            let i = 0;
            let result = "Not found";
            while(i < 500) {
                i++;
            }
            if (val === "001") {
                result = "結果01";
            } else if (val === "002") {
                result = "結果02"
            }
            dfd.resolve(result);
            return dfd.promise();
        }
        
        function inputProcess(id, item, value) {
            let dfd = $.Deferred();
            let data = { id: id, item: item, value: value };
            ui.block.grayout();
            request.ajax("/sample/lazyload/process", data).done(function(d) {
                setTimeout(() => {
                dfd.resolve(d);
                ui.block.clear();
                }, 2000);
            });
            return dfd.promise();
        }
        
        // Grid cell errors
        let dialogOptions: any = {
            forGrid: true,
            headers: [
                    new nts.uk.ui.errors.ErrorHeader("rowId", "Row ID", "auto", true),
                    new nts.uk.ui.errors.ErrorHeader("columnKey", "Column Key", "auto", true),
                    new nts.uk.ui.errors.ErrorHeader("message", "Message", "auto", true),
                    new nts.uk.ui.errors.ErrorHeader("ruleCode", "Rule code", "auto", true) 
                ]
        };
        this.bind(model, dialogOptions);
//        this.bind(model);
    });
}