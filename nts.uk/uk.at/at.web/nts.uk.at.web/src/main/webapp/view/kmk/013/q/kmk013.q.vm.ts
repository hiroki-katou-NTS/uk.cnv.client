module nts.uk.at.view.kmk013.q {
    
    import service = nts.uk.at.view.kmk013.q.service;
    import blockUI = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    
    export module viewmodel {
        const lastTabIndexTabPanel1 = 6;
        const lastTabIndexBeforeJumpTabPanel1 = 4;
        
        export class ScreenModel {
            
            
            tabs: KnockoutObservableArray<nts.uk.ui.NtsTabPanelModel>;
            selectedTab: KnockoutObservable<string>;
            
            overtimeList: KnockoutObservableArray<any>;
            roleFrameList: KnockoutObservableArray<any>;
            
            itemListQ5_5: KnockoutObservableArray<any>;
            selectedIdQ5_5: KnockoutObservable<number>;
//            enableQ5_5: KnockoutObservable<boolean>;
            
            dataRoleOfOpenPeriod: KnockoutObservable<any>;
            dataRoleOTWork: KnockoutObservable<any>;
            
            itemListQ13_5: KnockoutObservableArray<any>;
            
            constructor() {
                let self = this;
                self.tabs = ko.observableArray([
                    {id: 'tab-1', title: nts.uk.resource.getText("KMK013_383"), content: '.tab-content-1', enable: ko.observable(true), visible: ko.observable(true)},
                    {id: 'tab-2', title: nts.uk.resource.getText("KMK013_384"), content: '.tab-content-2', enable: ko.observable(true), visible: ko.observable(true)}
                ]);
                self.selectedTab = ko.observable('tab-1');
                // Q5_1
                self.overtimeList = ko.observableArray([]);
                self.roleFrameList = ko.observableArray([]);
                
                self.itemListQ5_5 = ko.observableArray([
                    new BoxModel(1, getText("KMK013_389")),
                    new BoxModel(0, getText("KMK013_390")),
                    new BoxModel(2, getText("KMK013_391"))
                ]);
                
                self.itemListQ13_5 = ko.observableArray([
                    new BoxModel(0, getText("KMK013_403")),
                    new BoxModel(1, getText("KMK013_404")),
                    new BoxModel(2, getText("KMK013_405"))
                ]);
                
                self.selectedIdQ5_5 = ko.observable(1);
//                self.enableQ5_5 = ko.observable(true);
                
                self.dataRoleOfOpenPeriod = ko.observable();
                self.dataRoleOTWork = ko.observable();
                
                self.changeTabPanel();
            }
            
            changeTabPanel(): void {
                let self = this;
                $( document ).keydown(function( event ) {
                    // catch event press tab button
                    if (event.which == 9) {
                        if (lastTabIndexTabPanel1 == _.toNumber($( "*:focus" ).attr("tabindex"))) {
                            if (9 == _.toNumber($( "*:focus" ).attr("posTab"))) {
                                self.selectedTab("tab-2");
                            }
                        }
                        
                        if (lastTabIndexBeforeJumpTabPanel1 == _.toNumber($( "*:focus" ).attr("tabindex"))) {
                            self.selectedTab("tab-1");
                        }
                    }
                });
            }

            // 起動する
            loadDataFirst(): JQueryPromise<any> {
                let self = this;
                var dfd = $.Deferred();
                self.overtimeList([]);
                self.roleFrameList([]);
                // get data from service
                $.when(service.findAllRoleOfOpenPeriod(), service.findAllRoleOvertimeWork(), 
                        service.getEnumRoleOfOpenPeriod(), service.getEnumRoleOvertimeWork(),
                        service.findOvertimeworkframe(), service.findWorkdayoffframe()).done(function(dataRoleOpenPeriod: any, dataRoleOTWork: any, 
                                                                                                        enumRoleOPenPeriod: any, enumRoleOTWork: any,
                                                                                                        dataOvertimeworkframe: any, dataWorkdayoffframe: any) {
                            
                            dataOvertimeworkframe = _.sortBy(dataOvertimeworkframe, (item: any) => { return item.overtimeWorkFrNo; });
                            dataWorkdayoffframe = _.sortBy(dataWorkdayoffframe,(item:any)=>{return item.workdayoffFrNo;});

                    _.forEach(dataOvertimeworkframe, function(value: any, index) {
                        self.overtimeList.push({ overtimeNo: nts.uk.resource.getText('KMK013_152') + value.overtimeWorkFrNo,
                            overtimeName: value.overtimeWorkFrName,
                            otWorkRoleSelect: ko.observable(value.role),
                            useAtr: ko.observable(value.useAtr)
                            });
                    });

                    _.forEach(dataWorkdayoffframe, function(value: any, index) {
                        self.roleFrameList.push({ HDFrameNo: nts.uk.resource.getText('KMK013_157') + value.workdayoffFrNo,
                            HDFrameName: value.workdayoffFrName,
                            roleFrameSelect: ko.observable(value.role),
                            useAtr: ko.observable(value.useAtr)
                            });
                    });

                    $( "#tab-panel" ).focus();
                    dfd.resolve();    
                });  
                return dfd.promise();
            }
            
            startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred();
                $.when(self.loadDataFirst()).done(function() {
                    dfd.resolve();
                });
                return dfd.promise();
            }

            // 登録する
            saveData(): void {
                let self = this;
                
                let overtimeList = [];
                _.forEach(self.overtimeList(), function(value: any, index){
                    overtimeList.push({'overtimeFrNo': index + 1, 'roleOTWorkEnum': value.otWorkRoleSelect()});        
                })
                
                let roleFrameList = [];
                _.forEach(self.roleFrameList(), function(value: any, index){
                    roleFrameList.push({'breakoutFrNo': index + 1, 'roleOfOpenPeriodEnum': value.roleFrameSelect()});        
                })
                
                $.when(service.saveRoleOvertimeWork(overtimeList), service.saveRoleOfOpenPeriod(roleFrameList)).done(function() {
                    nts.uk.ui.dialog.info({messageId: 'Msg_15'}).then(() => {
                        $( "#tab-panel" ).focus();
                    });
                });
            }
        }
        
        class BoxModel {
            id: number;
            name: string;
            constructor(id, name){
                var self = this;
                self.id = id;
                self.name = name;
            }
        }
    }
}