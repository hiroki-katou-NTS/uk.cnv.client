module nts.uk.com.view.cmm011.v2.b.viewmodel {
    import block = nts.uk.ui.block;
    import getText = nts.uk.resource.getText;
    import confirm = nts.uk.ui.dialog.confirm;
    import alertError = nts.uk.ui.dialog.alertError;
    import info = nts.uk.ui.dialog.info;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    const DEFAULT_END = "9999/12/31";

    export class ScreenModel {
        initMode: KnockoutObservable<number> = ko.observable(INIT_MODE.WORKPLACE);
        screenMode: KnockoutObservable<number> = ko.observable(SCREEN_MODE.NEW);
        lstWpkHistory: KnockoutObservableArray<HistoryItem>;
        selectedHistoryId: KnockoutObservable<string>;
        selectedStartDate: KnockoutObservable<string>;
        selectedEndDate: KnockoutObservable<string>;
        copyPreviousConfig: KnockoutObservable<boolean>;
        
        constructor() {
            let self = this, params = getShared("CMM011AParams");
            self.lstWpkHistory = ko.observableArray([]);
            self.selectedHistoryId = ko.observable(null);
            self.selectedStartDate = ko.observable(null);
            self.selectedEndDate = ko.observable(DEFAULT_END);
            self.copyPreviousConfig = ko.observable(false);
            if (params) {
                self.initMode(params.initMode);
                self.selectedHistoryId(params.historyId);
            }
            self.selectedHistoryId.subscribe(value => {
                if (value) {
                    let history: HistoryItem = _.find(self.lstWpkHistory(), i => i.historyId == value);
                    if (history) {
                        self.selectedStartDate(history.startDate);
                        self.selectedEndDate(history.endDate);
                    }
                    self.screenMode(SCREEN_MODE.SELECT);
                } else {
//                    if (self.screenMode() == SCREEN_MODE.NEW || self.screenMode() == SCREEN_MODE.ADD)
//                        self.selectedEndDate(DEFAULT_END);
                }
            });
        }
        
        startPage(): JQueryPromise<any> {
            let self = this, dfd = $.Deferred();
            block.invisible();
            service.getAllConfiguration(self.initMode()).done(data => {
                if (data) {
                    self.lstWpkHistory(_.map(data, i => new HistoryItem(i)));
                    self.selectedHistoryId.valueHasMutated();
                }
                dfd.resolve();
            }).fail((error) => {
                dfd.reject();
                alertError(error);
            }).always(() => {
                block.clear()
            });
            return dfd.promise();
        }
        
        addHistory() {
            let self = this;
            self.screenMode(SCREEN_MODE.ADD);
            self.selectedHistoryId(self.lstWpkHistory()[0].historyId);
            self.selectedStartDate(null)
            self.selectedEndDate(DEFAULT_END);
        }
        
        updateHistory() {
            let self = this;
            self.screenMode(SCREEN_MODE.UPDATE);
        }
        
        deleteHistory() {
            
        }
        
        registerConfig() {
            let self = this, data = null;
            block.invisible();
            switch (self.screenMode()) {
                case SCREEN_MODE.NEW:
                    data = {
                        initMode: self.initMode(),
                        newHistoryId: null,
                        prevHistoryId: null,
                        startDate: moment.utc(self.selectedStartDate(), "YYYY/MM/DD").toISOString(),
                        endDate: moment.utc(self.selectedEndDate(), "YYYY/MM/DD").toISOString(),
                        copyPreviousConfig: false
                    };
                    service.addConfiguration(data).done((historyId) => {
                        self.startPage().done(() => {
                            self.selectedHistoryId(historyId);
                            self.sendDataToParentScreen();
                        });
                    }).fail((error) => {
                        alertError(error);
                    }).always(() => {
                        block.clear();
                    });
                    break;
                case SCREEN_MODE.ADD:
                    data = {
                        initMode: self.initMode(),
                        newHistoryId: null,
                        prevHistoryId: self.lstWpkHistory()[0].historyId,
                        startDate: moment.utc(self.selectedStartDate(), "YYYY/MM/DD").toISOString(),
                        endDate: moment.utc(self.selectedEndDate(), "YYYY/MM/DD").toISOString(),
                        copyPreviousConfig: self.copyPreviousConfig()
                    };
                    service.addConfiguration(data).done((historyId) => {
                        self.startPage().done(() => {
                            self.selectedHistoryId(historyId);
                            self.sendDataToParentScreen();
                        });
                    }).fail((error) => {
                        alertError(error);
                    }).always(() => {
                        block.clear();
                    });
                    break;
                case SCREEN_MODE.UPDATE:
                    block.clear();
                    self.sendDataToParentScreen();
                    break;
                default:
                    block.clear();
                    self.sendDataToParentScreen();
                    break;
            }
        }
        
        sendDataToParentScreen() {
            let self = this;
            let params = { 
                historyId: self.selectedHistoryId(), 
                startDate: self.selectedStartDate(), 
                endDate: self.selectedEndDate() 
            };
            setShared("CMM011BParams", params);
            nts.uk.ui.windows.close();
        }
        
        cancel() {
            nts.uk.ui.windows.close();
        }
    }
    
    enum INIT_MODE {
        WORKPLACE = 0,
        DEPARTMENT = 1
    }
    
    enum SCREEN_MODE {
        SELECT = 0,
        NEW = 1,
        ADD = 2,
        UPDATE = 3
    }
    
    class HistoryItem {
        historyId: string;
        startDate: string;
        endDate: string;
        displayText: string;
        
        constructor(params) {
            if (params) {
                this.historyId = params.historyId;
                this.startDate = params.startDate;
                this.endDate = params.endDate;
                this.displayText = params.startDate + getText("CMM011-0_25") + params.endDate;
            }
        }
    }
}