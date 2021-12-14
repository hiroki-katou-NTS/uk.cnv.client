module nts.uk.com.view.cmm051.a {
    export module viewmodel {
        import alert = nts.uk.ui.dialog.alert;
        import modal = nts.uk.ui.windows.sub.modal;
        import setShared = nts.uk.ui.windows.setShared;
        import getShared = nts.uk.ui.windows.getShared;
        import FunctionPermission = base.FunctionPermission;
        import WorkplaceManager = base.WorkplaceManager;
        import ccg = nts.uk.com.view.ccg026;
        import model = nts.uk.com.view.ccg026.component.model;
        
        export class ScreenModel {
            langId: KnockoutObservable<string> = ko.observable('ja');
            // KCP010
            kcp010Model : kcp010.viewmodel.ScreenModel;
            listComponentOption: ComponentOption;
            
            // Screen mode
            isNewMode: KnockoutObservable<boolean>;
            //Date Range Picker
            dateValue: KnockoutObservable<any>;
            
            // Lists
            wkpManagerList : KnockoutObservableArray<WorkplaceManager>;
            
            // Selected items 
            selectedWkpId: KnockoutObservable<string>;
            selectedWpkManagerId: KnockoutObservable<string>;
            selectedWkpManager: KnockoutObservable<WorkplaceManager>;
            
            wkpManagerTree: any;
            selectedCode: any;
            headers: any;

            // CCG026
            component: ccg.component.viewmodel.ComponentModel;
            listPermission: KnockoutObservableArray<FunctionPermission>;
            constructor() {
                let self = this;
                
                self.selectedWkpId = ko.observable('');
                
                // Initial listComponentOption
                self.listComponentOption = {
                    targetBtnText: nts.uk.resource.getText("KCP010_3"),
                    tabIndex: 4
                };
                
                self.selectedCode = ko.observable('');
                self.selectedCode.subscribe(newValue => {
                    self.selectedWpkManagerId(newValue);
                });
                
                self.kcp010Model = $('#wkp-component').ntsLoadListComponent(self.listComponentOption);
                self.kcp010Model.workplaceId.subscribe(function(wkpId) {
                    if (wkpId) {
                        self.selectedWkpId(wkpId);
                        // Get workplace manager list
                        self.getWkpManagerList(wkpId, '');
                    }
                });
                
                self.isNewMode = ko.observable(false);
                self.dateValue = ko.observable({});
                
                self.wkpManagerList = ko.observableArray([]);
                self.wkpManagerTree = ko.observableArray([]);
                
                self.selectedWkpManager = ko.observable(new WorkplaceManager(null));
                self.selectedWpkManagerId = ko.observable('');
                self.selectedWpkManagerId.subscribe(function(newValue) {
                    // validate null or empty
                    if (nts.uk.text.isNullOrEmpty(newValue)) {
                        self.isNewMode(true);
                    } else {
                        // set update mode
                        self.isNewMode(false);
                        let data = _.filter(self.wkpManagerList(), function(o) { return o.wkpManagerId == newValue; });
                        if (data[0]) {
                            self.selectedWkpManager(new WorkplaceManager(data[0]));
                        } else {
                            let node = _.filter(self.wkpManagerTree(), function(o : Node) { return o.wkpManagerId == newValue; });
                            let firstWkpMng = node[0].childs[0];
                            if (firstWkpMng && firstWkpMng != null) {
                                self.selectedCode(firstWkpMng.wkpManagerId);
                            } else {
                                self.selectedCode(self.wkpManagerList()[0].wkpManagerId);
                            }
                        }
                    }
                    self.initWkpManager();
                });
                
                self.headers = ko.observableArray(["コード／名称"]);
                self.listPermission = ko.observableArray([]);
            }
            
            // Start page
            start() {
                let self = this;
                var dfd = $.Deferred();
                
               
                // CCG026
                self.component = new ccg.component.viewmodel.ComponentModel({ 
                      classification: 1,
                      maxRow: 5
                });
                self.component.startPage().done(() => {
                    self.listPermission(self.component.listPermissions());
                });
                dfd.resolve(); 
                return dfd.promise();
            }
            
            private reBindingTreeList() {
                let self = this;
                // re-binding
                let $treeGrid: any = $("#treegrid2");
                ko.cleanNode($treeGrid[0]);
                ko.applyBindings(self, $treeGrid[0]);
            }
            
            private getWkpManagerList(wkpId : string, savedWkpMngId: string): JQueryPromise<void> {
                let self = this;
                let dfd = $.Deferred<void>();
                
                self.wkpManagerTree([]);
                self.wkpManagerList([]);
                service.findAllWkpManagerByWkpId(wkpId).done(function(dataList) {
                    if (dataList && dataList.length > 0) {
                        self.isNewMode(false);
                        self.wkpManagerList(dataList);
                        if (nts.uk.text.isNullOrEmpty(savedWkpMngId)) {
                            self.selectedCode(self.wkpManagerList()[0].wkpManagerId);
                        } else {
                            self.selectedCode(savedWkpMngId);
                        }
                        // Setup workplace manager display tree
                        _.forEach(dataList, (mng) => {
                            let node = _.find(self.wkpManagerTree(), function(o : Node) { return o.wkpManagerId == mng.employeeId; });
                            if (node) { // Existed employee
                                node.childs.push(new WorkplaceManager(mng));
                            } else {
                                self.wkpManagerTree.push(new Node(mng.employeeInfo.employeeCode, mng.employeeInfo.namePerson, [new WorkplaceManager(mng)], mng.employeeId));
                            }
                        });
                        self.reBindingTreeList();
                    } else {
                        self.createWkpManager();
                    }
                    dfd.resolve();
                }).fail((res: any) => {
                    nts.uk.ui.block.clear();
                });
                return dfd.promise();
            }
            
            private initWkpManager() {
                let self = this;
                nts.uk.ui.errors.clearAll();
                if (self.isNewMode() == true) {
                    self.selectedWkpManager(new WorkplaceManager(null));
                    self.dateValue({});
                    self.component.roleId('');
                    self.component.roleId.valueHasMutated();
                } else {
                    self.dateValue({startDate : self.selectedWkpManager().startDate, endDate : self.selectedWkpManager().endDate});
                    self.component.roleId(self.selectedWpkManagerId());
                }
                setTimeout(function(){$(".ntsStartDatePicker").focus();},100);
            }
            
            /**
             * Button on screen
             */
            // 新規 button
            createWkpManager() {
                let self = this;
                nts.uk.ui.errors.clearAll();
                self.isNewMode(true);
                self.selectedCode('');
                self.initWkpManager();
            }
            
            // 登録 button
            saveWkpManager() {
                let self = this;

                // validate
                if (!self.validate()) {
                    return;
                }

                // get JsObject
                let command: any = self.toJsonObject();

                nts.uk.ui.block.grayout();
               
                // insert or update workplace
                service.saveWkpManager(ko.toJS(command)).done(function(savedWkpMngId) {
                    nts.uk.ui.block.clear();
                    
                    // notice success
                    nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(() => {
                        // Get workplace manager list
                        self.getWkpManagerList(self.selectedWkpId(), savedWkpMngId);
                    });
                }).fail((res: any) => {
                    nts.uk.ui.block.clear();
                    self.showMessageError(res);
                });
            }
            
            /**
             * validate
             */
            private validate() {
                let self = this;

                // clear error
                self.clearError();

                // validate
                $(".ntsDatepicker ").ntsEditor('validate');
                $(".nts-editor").trigger("validate");
                return !$('.nts-input').ntsError('hasError');
            }
            
            /**
             * clearError
             */
            private clearError() {
                nts.uk.ui.errors.clearAll();
            }
            
            // 削除 button
            delWkpManager() {
                let self = this;
                let nodeList : Array<Node> = self.wkpManagerTree();
                let currentNodeIndex = _.findIndex(nodeList, x => x.wkpManagerId == self.selectedWkpManager().employeeId);
                if (currentNodeIndex < 0) {
                    nts.uk.ui.dialog.alert('エラーがあります');
                    return;
                }
                let currentNode = nodeList[currentNodeIndex];
                let lastNodeIndex = nodeList.length - 1;
                let currentItemList : Array<WorkplaceManager> = currentNode.childs;
                let currentItemIndex = _.findIndex(currentItemList, x => x.wkpManagerId == self.selectedWkpManager().wkpManagerId);
                let lastItemIndex = currentItemList.length - 1;
                let isFinalElement = currentItemList.length - 1 == 0 ? true : false;

                // show message confirm
                nts.uk.ui.dialog.confirm({ messageId: 'Msg_18' }).ifYes(() => {
                    let command: any = {};
                    command.wkpManagerId = self.selectedWpkManagerId();

                    nts.uk.ui.block.grayout();
                    service.deleteWkpManager(command).done(() => {
                        // Get workplace manager list
                        nts.uk.ui.dialog.info({ messageId: "Msg_16" }).then(function() {
                            $.when(self.getWkpManagerList(self.selectedWkpId(), '')).done(()=>{
                                var selectedId = "";
                                if (self.wkpManagerList().length > 0) {
                                    if (currentItemIndex == lastItemIndex) {
                                        if (isFinalElement) {
                                            if (currentNodeIndex == lastNodeIndex) {
                                                var prevItemList = nodeList[currentNodeIndex - 1].childs;
                                                selectedId = prevItemList[prevItemList.length - 1].wkpManagerId;
                                            } else {
                                                var nextItemList = nodeList[currentNodeIndex + 1].childs;
                                                selectedId = nextItemList[0].wkpManagerId;
                                            }
                                        } else {
                                            selectedId = currentItemList[currentItemIndex - 1].wkpManagerId;
                                        }
                                    } else {
                                        selectedId = currentItemList[currentItemIndex + 1].wkpManagerId;
                                    }
                                }
                                self.selectedCode(selectedId);
                            });
                        });
                    }).fail((res: any) => {
                        nts.uk.ui.dialog.bundledErrors(res);
                    })
                    nts.uk.ui.block.clear();
                });
            }
            
            // 社員選択 button
            openDialogCDL009() {
                let self = this;
    
                setShared('CDL009Params', {
                    isMultiSelect: false,
                    baseDate: moment(new Date()).toDate(),
                    target: 1
                }, true);
    
                modal("/view/cdl/009/a/index.xhtml").onClosed(function() {
                    var isCancel = getShared('CDL009Cancel');
                    if (isCancel) {
                        return;
                    }
                    var employeeId = getShared('CDL009Output');
                    self.getEmployeeInfo(employeeId);
                });
            }
            
            private getEmployeeInfo(empId: string) {
                let self = this;
                service.getEmployeeInfo(empId).done(function(data: any) {
                    if (data) {
                        self.selectedWkpManager().employeeId = empId;
                        self.selectedWkpManager().employeeInfo({employeeId:data.employeeId,
                                                                employeeCode:ko.observable(data.employeeCode),
                                                                namePerson:data.personalName});
                    }
                }).fail(function(error) {
                    self.showMessageError({ messageId: error.messageId });
                });
            }
            /**
             * Common
             */
            /**
             * showMessageError
             */
            private showMessageError(res: any) {
                if (res.businessException) {
                    nts.uk.ui.dialog.alertError({ messageId: res.messageId, messageParams: res.parameterIds });
                }
            }
            /**
             * toJsonObject
             */
            private toJsonObject(): any {
                let self = this;

                // to JsObject
                let command: any = {};
                command.newMode = self.isNewMode();
                command.startDate = new Date(self.dateValue().startDate);
                command.endDate = new Date(self.dateValue().endDate);
                command.employeeId = self.selectedWkpManager().employeeId;
                command.wkpId = self.selectedWkpId();
                command.wkpManagerId = self.selectedWkpManager().wkpManagerId;
                command.roleList = self.listPermission();

                return command;
            }
            /*  
           * Print file excel
           */
           private saveAsExcel(): void {
               let self = this;
//                modal("/view/cmm/051/m/index.xhtml").onClosed(function() {
//                });
               
               let params = {
                   date: null,
                   mode: 1
               };             
               if (!nts.uk.ui.windows.getShared("CDL028_INPUT")) {
                    nts.uk.ui.windows.setShared("CDL028_INPUT", params);
                }
               nts.uk.ui.windows.sub.modal("/view/cdl/028/a/index.xhtml").onClosed(function() {
                   var result = getShared('CDL028_A_PARAMS');
                   if (result.status) {
                        nts.uk.ui.block.grayout();
                        let langId = self.langId();
                        let date = moment.utc(result.standardDate, "YYYY/MM/DD");
                        service.saveAsExcel(langId, date).done(function() {
                            //nts.uk.ui.windows.close();
                        }).fail(function(error) {
                            nts.uk.ui.dialog.alertError({ messageId: error.messageId });
                        }).always(function() {
                            nts.uk.ui.block.clear();
                        });
                   }           
               });
            }
            
            
        }
        
        /**
         * Interface ComponentOption of KCP010
         */
        export interface ComponentOption {
            targetBtnText: string;
            tabIndex: number;
        }
        
        class Node {
            wkpManagerId: string;
            code: string;
            name: string;
            nodeText: string;
            childs: any;
            constructor(code: string, name: string, childs: Array<WorkplaceManager>, parentId: string) {
                var self = this;
                self.wkpManagerId = parentId;
                self.code = code;
                self.name = name;
                self.nodeText = self.code + ' ' + self.name;
                self.childs = childs;
            }
        }
    }
}