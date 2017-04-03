module qmm020.c.viewmodel {
    export class ScreenModel {
        // listbox
        itemList: KnockoutObservableArray<ItemModel>;
        itemName: KnockoutObservable<string>;
        currentCode: KnockoutObservable<number>
        selectedName: KnockoutObservable<string>;
        selectedCode: KnockoutObservableArray<string>;
        selectedCodes: KnockoutObservableArray<string>;
        isEnable: KnockoutObservable<boolean>;
        //selectedCode: KnockoutObservable<string>;
        
        
        employeeAllotHead : Array<service.model.EmployeeAllotSettingHeaderDto>;
        dataSource : any;
        selectedList: any;
        
        constructor() {
            var self = this;
            self.itemList = ko.observableArray([]);
            self.selectedCode = ko.observableArray([]);
            self.isEnable = ko.observable(true);
            self.selectedList = ko.observableArray([]);
            

            // Array Data 1 
            let employment1 = ko.mapping.fromJS([
                { "NO": 1, "ID": "000000001", "Name": "正社員", "PaymentDocID": "K001", "PaymentDocName": "給与明細書001", "BonusDocID": "S001", "BonusDocName": "賞与明細書001" },
                { "NO": 2, "ID": "000000002", "Name": "DucPham社員", "PaymentDocID": "K002", "PaymentDocName": "給与明細書002", "BonusDocID": "S001", "BonusDocName": "賞与明細書002" },
                { "NO": 3, "ID": "000000003", "Name": "HoangMai社員", "PaymentDocID": "K003", "PaymentDocName": "給与明細書003", "BonusDocID": "S001", "BonusDocName": "賞与明細書003" }
            ]);
            // Array Data 2 
            let employment2 = ko.mapping.fromJS([
                { "NO": 1, "ID": "000000004", "Name": "ABC社員", "PaymentDocID": "K001", "PaymentDocName": "給与明細書001", "BonusDocID": "S001", "BonusDocName": "賞与明細書001" },
                { "NO": 2, "ID": "000000005", "Name": "DEF社員", "PaymentDocID": "K002", "PaymentDocName": "給与明細書002", "BonusDocID": "S001", "BonusDocName": "賞与明細書002" },
                { "NO": 3, "ID": "000000006", "Name": "GHK社員", "PaymentDocID": "K003", "PaymentDocName": "給与明細書003", "BonusDocID": "S001", "BonusDocName": "賞与明細書003" }
            ]);
            //self.buildGrid("#C_LST_001", "C_BTN_001", "C_BTN_002");
            
            self.dataSource = ko.mapping.toJS(employment1());
            //console.log(self.dataSource);
            //Build IgGrid
            $("#C_LST_001").igGrid({
               columns: [
                    { headerText: "", key: "NO", dataType: "string", width: "20px" },
                    { headerText: "コード", key: "ID", dataType: "string", width: "100px" },
                    { headerText: "名称", key: "Name", dataType: "string", width: "200px" },
                    { headerText: "", key: "PaymentDocID", dataType: "string", hidden: true },
                    { headerText: "", key: "PaymentDocName", dataType: "string", hidden: true },
                    { headerText: "", key: "BonusDocID", dataType: "string", hidden: true },
                    { headerText: "", key: "BonusDocName", dataType: "string", hidden: true },
                    {
                        headerText: "給与明細書", key: "PaymentDocID", dataType: "string", width: "250px", unbound: true,
                        template: "<input type='button' id='" + "C_BTN_001"  + "' value='選択'/><label style='margin-left:5px;'>${PaymentDocID}</label><label style='margin-left:15px;'>${PaymentDocName}</label>"
                    },
                    {
                        headerText: "賞与明細書", key: "BonusDoc", dataType: "string", width: "20%", unbound: true,
                        template: "<input type='button' id='" + "C_BTN_002" + "' value='選択'/><label style='margin-left:5px;'>${BonusDocID}</label><label style='margin-left:15px;'>${BonusDocName}</label>"
                    },
                ],
               features: [{
                    name: 'Selection',
                    mode: 'row',
                    multipleSelection: true,
                    activation: false,
                    rowSelectionChanged: this.selectionChanged.bind(this)
                }],
               virtualization: true,
               virtualizationMode: 'continuous',
               width: "800px",
               height: "240px",
               primaryKey: "ID",
               dataSource: ko.mapping.toJS(employment1())
           });
            
            
            //subcribe list box's change
            self.selectedCode.subscribe(function(codeChange) {
                if (codeChange.length > 0) {
                    //get ValueName when click to item in HistoryList
                    let row = _.find(self.itemList(), function(item) {
                        let code = self.selectedCode();
//                        if (code === item.code) {
//                            //self.selectedName(item.name);
//                        }
                    });
                }
            });
            //SCREEN C
            //Event : Click to button Sentaku on igGrid
            var openPaymentDialog = function(evt, ui) {
                if (ui.colKey === "PaymentDocID") {
                    //Gọi hàm open SubWindow
                    //Khi close Subwindow, get dc cái new object(ID, Name... )
                    let row = _.find(employment1(), function(item) {
                        //return item.ID() === ui.rowKey;
                    });
                    //row.PaymentDocName("test");
                    //self.buildGrid("#C_LST_001", "C_BTN_001", "C_BTN_002");
                }
            }
            self.start();
        }
        //Selected changed
        selectionChanged (evt, ui) {
            //console.log(evt.type);
            var selectedRows = ui.selectedRows;
            var arr = [];
            for(var i = 0; i < selectedRows.length; i++) {
                arr.push(""+selectedRows[i].id);
            }
            this.selectedList(arr);
        };
        
        
        //Rebuild igGrid Screen C
//        buildGrid(id: string, buttonKID: string, buttonSID: string) {
//            var self = this;
//            if ($(id).data("igGrid") !== undefined) {
//                $(id).igGrid({
//                    dataSource: [],
//                    features: []
//                });
//                $(id).igGrid("destroy");
//            }
//            $(id).igGrid({
//                columns: [
//                    { headerText: "", key: "NO", dataType: "string", width: "20px" },
//                    { headerText: "コード", key: "ID", dataType: "string", width: "100px" },
//                    { headerText: "名称", key: "Name", dataType: "string", width: "200px" },
//                    { headerText: "", key: "PaymentDocID", dataType: "string", hidden: true },
//                    { headerText: "", key: "PaymentDocName", dataType: "string", hidden: true },
//                    { headerText: "", key: "BonusDocID", dataType: "string", hidden: true },
//                    { headerText: "", key: "BonusDocName", dataType: "string", hidden: true },
//                    {
//                        headerText: "給与明細書", key: "PaymentDocID", dataType: "string", width: "250px", unbound: true,
//                        template: "<input type='button' id='" + buttonKID + "' value='選択'/><label style='margin-left:5px;'>${PaymentDocID}</label><label style='margin-left:15px;'>${PaymentDocName}</label>"
//                    },
//                    {
//                        headerText: "賞与明細書", key: "BonusDoc", dataType: "string", width: "20%", unbound: true,
//                        template: "<input type='button' id='" + buttonSID + "' value='選択'/><label style='margin-left:5px;'>${BonusDocID}</label><label style='margin-left:15px;'>${BonusDocName}</label>"
//                    },
//                ],
//                primaryKey: "ID",
//                width: "800px",
//                autofitLastColumn: true,
//                avgColumnWidth: "100px",
//                dataSource: ko.mapping.toJS(self.employment1()),
//                value: self.currentCodeList,
//                cellClick: self.openPaymentDialog,
//                features: [{
//                    name: "Selection",
//                    mode: "row",
//                    activation: true
//                }]
//            });
//        }
        //Search Employment
//        searchEmployment(idInputSearch: string, idList: string) {
//            var self = this;
//            let textSearch: string = $("#C_INP_001").val().trim();
//            if (textSearch.length === 0) {
//                nts.uk.ui.dialog.alert("コード/名称が入力されていません。");
//            } else {
//                if (self.textSearch !== textSearch) {
//                    let searchResult = _.filter(self.products, function(item) {
//                        return _.includes(item.ID, textSearch) || _.includes(item.Name, textSearch);
//                    });
//                    self.queueSearchResult = [];
//                    for (let item of searchResult) {
//                        self.queueSearchResult.push(item);
//                    }
//                    self.textSearch = textSearch;
//                }
//                if (self.queueSearchResult.length === 0) {
//                    nts.uk.ui.dialog.alert("対象データがありません。");
//                } else {
//                    let firstResult: ItemModel = _.first(self.queueSearchResult);
//                    //self.listBox().selectedCode(firstResult.id);
//                    $("#grid").igGridSelection("selectRowById", firstResult.ID);
//                    self.queueSearchResult.shift();
//                    self.queueSearchResult.push(firstResult);
//                }
//            }
//        }
        
        // start function
        start(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred<any>();
                //Get list startDate, endDate of History  
                service.getEmployeeAllotHeaderList().done(function(employeeAllotHeader: Array<service.model.EmployeeAllotSettingHeaderDto>) {
                    if (employeeAllotHeader.length > 0) {
                          //TODO 2017.03.14
                          self.employeeAllotHead = employeeAllotHeader;
                          let _items : Array<ItemModel> = [];
                          _.forEach(employeeAllotHeader,function(histEm,i){
                             _items.push(new ItemModel(histEm.historyId, histEm.startYM+" ~ "+histEm.endYM));
                              if (i == employeeAllotHeader.length - 1) {
                                self.itemList(_items);
                            }
                          })
                    } else {
                        alert('dddd');
                        //self.allowClick(false);
                        dfd.resolve();
                    }
                }).fail(function(res) {
                    // Alert message
                    alert(res);
                });
            
                               
                // Return.
                return dfd.promise();
        }
        //Open dialog Add History
        openJDialog() {
//            var self = this;
//            //console.log(self);
//            //alert($($("#sidebar-area .navigator a.active")[0]).attr("href"));
//            var valueShareJDialog = $($("#sidebar-area .navigator a.active")[0]).attr("href");
//            //Get value TabCode + value of selected Name in History List
//            valueShareJDialog = valueShareJDialog + "~" + self.selectedName();
//            nts.uk.ui.windows.setShared('valJDialog', valueShareJDialog);
//            nts.uk.ui.windows.sub.modal('/view/qmm/020/f/index.xhtml', { title: '明細書の紐ずけ＞履歴追加' }).onClosed(function(): any {
//                self.returnJDialog = ko.observable(nts.uk.ui.windows.getShared('returnJDialog'));
//                //self.itemList.removeAll();
//                self.itemList.push(new ItemModel('4', self.returnJDialog() + '~9999/12'));
//            });
        }
        //Open dialog Edit History
        openKDialog() {
            var self = this;
            //var singleSelectedCode = self.singleSelectedCode().split(';');
            //nts.uk.ui.windows.setShared('stmtCode', singleSelectedCode[0]);
            nts.uk.ui.windows.sub.modal('/view/qmm/020/k/index.xhtml', { title: '明細書の紐ずけ＞履歴編集' }).onClosed(function(): any {
                //self.start(self.singleSelectedCode());
            });
        }
        
    }
    
    class ItemModel {
        code: string;
        name: string;
        constructor(code: string, name: string) {
            this.code = code;
            this.name = name;
        }
    }
    class EmployeeList {
        code: string;
        name: string;
        paymentCode: string;
        paymentName: string;
        bonusCode: string;
        bonusName: string;
        constructor(code:string,name:string,paymentCode:string,paymentName:string,bonusCode:string,bonusName:string){
            this.code = code;
            this.name = name;
            this.paymentCode = paymentCode;
            this.paymentName = paymentName;
            this.bonusCode = bonusCode;
            this.bonusName = bonusName;
        }    
    
    }
}