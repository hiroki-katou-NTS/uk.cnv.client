module nts.uk.at.view.kmk012.a {

    import ClosureHistoryFindDto = service.model.ClosureHistoryFindDto;
    import DayofMonth = service.model.DayofMonth;
    import ClosureDto = service.model.ClosureDto;
    import ClosureHistoryMDto = service.model.ClosureHistoryMDto;
    import ClosureHistoryDDto = service.model.ClosureHistoryDDto;
    import ClosureSaveDto = service.model.ClosureSaveDto;
    import ClosureHistoryDto = service.model.ClosureHistoryDto;
    
    export module viewmodel {

        export class ScreenModel {
            lstClosureHistory: KnockoutObservableArray<ClosureHistoryFindDto>;
            closureModel: ClosureModel;
            closureHistoryModel: ClosureHistoryDetailModel;
            useClassification: KnockoutObservableArray<any>;
            lstDayOfMonth: KnockoutObservableArray<DayofMonth>;
            columnsLstClosureHistory: KnockoutObservableArray<any>;
            selectCodeLstClosure: KnockoutObservable<ClosureHistoryFindDto>;
            selectCodeLstClosureHistory: KnockoutObservable<ClosureHistoryMDto>;
            textEditorOption: KnockoutObservable<any>;
            visibleUseClassification: KnockoutObservable<boolean>;
            enableChangeClosureDate: KnockoutObservable<boolean>;
            enableChangeClosureDateAnd: KnockoutObservable<boolean>;
            enableUseClassification: KnockoutObservable<boolean>;
                        
            constructor() {
                var self = this;
                self.lstClosureHistory = ko.observableArray<ClosureHistoryFindDto>([]);
                self.closureModel = new ClosureModel();
                self.closureHistoryModel = new ClosureHistoryDetailModel();
                 self.columnsLstClosureHistory = ko.observableArray([
                    { headerText: 'コード', prop: 'id', width: 120 },
                    { headerText: '名称', prop: 'name', width: 120 }
                ]);
                
                self.useClassification = ko.observableArray([
                    { code: '1', name: nts.uk.resource.getText("KMK012_3")},
                    { code: '0', name: nts.uk.resource.getText("KMK012_4") }
                ]);
                self.selectCodeLstClosure = ko.observable(new ClosureHistoryFindDto());
                self.selectCodeLstClosureHistory = ko.observable(new ClosureHistoryMDto());
                self.lstDayOfMonth = ko.observableArray<DayofMonth>(self.intDataMonth());
                
                self.textEditorOption = ko.mapping.fromJS(new nts.uk.ui.option.TextEditorOption({
                    width: "50px",
                    textmode: "text",
                    textalign: "left"
                }));
                
                self.selectCodeLstClosure.subscribe(function(val: ClosureHistoryFindDto) {
                    self.visibleUseClassification(val.id != self.lstClosureHistory()[0].id);
                    self.detailClosure(val.id);
                });
                
                self.selectCodeLstClosureHistory.subscribe(function(val: ClosureHistoryMDto){
                    self.enableChangeClosureDate(val.historyId == self.closureModel.closureHistories()[0].historyId);
                    self.enableChangeClosureDateAnd(self.enableChangeClosureDate() && self.enableUseClassification());
                    self.detailClosureHistory(val);
                });
                
                self.visibleUseClassification = ko.observable(true);
                self.enableChangeClosureDate = ko.observable(true);
                self.enableUseClassification = ko.observable(true);
                self.enableChangeClosureDateAnd = ko.observable(true);
                
                
                self.closureModel.useClassification.subscribe(function(val: number){
                    if (val == 0) {
                        self.enableUseClassification(false);
                        self.enableChangeClosureDateAnd(self.enableChangeClosureDate() && self.enableUseClassification());
                    }
                    else {
                        self.enableUseClassification(true);
                        self.enableChangeClosureDateAnd(self.enableChangeClosureDate() && self.enableUseClassification());
                    }
                });
            }

            // start page
            startPage(): JQueryPromise<any> {
                var self = this;
                var dfd = $.Deferred();
                service.getAllClosureHistory().done(function(data) {
                    var dataRes: ClosureHistoryFindDto[] = [];
                    for (var item: ClosureHistoryFindDto of data) {
                        var dataI: ClosureHistoryFindDto = new ClosureHistoryFindDto();
                        dataI.id = item.id; 
                        dataI.name = item.name;
                        dataI.updateData();
                        dataRes.push(dataI);
                    }
                   self.lstClosureHistory(dataRes);
                   self.selectCodeLstClosure(data[0]);
                   self.detailClosure(data[0].id);
                });
                dfd.resolve();
                return dfd.promise();
            }
            
            
            detailClosure(closureId: number): void{
                var self = this;
                service.detailClosure(closureId).done(function(data: ClosureDto) {
                    self.closureModel.updateData(data);
                    self.selectCodeLstClosureHistory(data.closureSelected);
                    self.detailClosureHistory(data.closureSelected);
                });
           }
            
            detailClosureHistory(master: ClosureHistoryMDto){
                var self = this;
                service.detailClosureHistory(master).done(function(data){
                    self.closureHistoryModel.updateData(data);
                    self.clearValiate();
                });
            }
            
            intDataMonth(): DayofMonth[]{
                var data: DayofMonth[] = [];
                var i: number = 1 ;
                for(i=1; i<=30; i++){
                    var dayI:  DayofMonth;
                    dayI = new DayofMonth();
                    dayI.day = i;
                    dayI.name=i+"日";
                    data.push(dayI);   
                }
                var dayLast: DayofMonth;
                dayLast = new DayofMonth();
                dayLast.day = 0;
                dayLast.name = "末日";
                data.push(dayLast);  
                return data;
            }
            
            
            
            
            collectData(): ClosureSaveDto {
                var self = this;
                var dto: ClosureSaveDto;
                dto = new ClosureSaveDto();
                dto.closureId = self.closureModel.closureId();
                dto.useClassification = self.closureModel.useClassification();
                dto.month = self.closureModel.month();
                return dto;
            }
            
            clearValiate(){
                $('#inpMonth').ntsError('clear');
                 
            }
            
            validateClient(): boolean {
                $("#inpMonth").ntsEditor("validate");
                $("#inpname").ntsEditor("validate");
                
                if($('.nts-input').ntsError('hasError')) {
                    return true;
                }
                return false;
            }
            
            saveClosureHistory(): void {
                var self = this;
                if(self.validateClient()){
                    return;    
                }
                service.saveClosure(self.collectData()).done(function() {
                    service.saveClosureHistory(self.collectDataHistory()).done(function() {
                        nts.uk.ui.dialog.info({ messageId: "Msg_15" }).then(function() {
                            self.reloadPage(self.selectCodeLstClosure().id,
                                self.selectCodeLstClosureHistory().historyId);
                        });
                    }).fail(function(error) {
                        nts.uk.ui.dialog.alertError(error).then(function() {
                            self.reloadPage(self.selectCodeLstClosure().id,
                                self.selectCodeLstClosureHistory().historyId);
                        });
                    });
                });
            }
            
            reloadPage(closureId: number, historyId: string): void{
                var self = this;
                 service.getAllClosureHistory().done(function(data) {
                    var dataRes: ClosureHistoryFindDto[] = [];
                    for (var item: ClosureHistoryFindDto of data) {
                        var dataI: ClosureHistoryFindDto = new ClosureHistoryFindDto();
                        dataI.id = item.id; 
                        dataI.name = item.name;
                        dataI.updateData();
                        dataRes.push(dataI);
                    }
                   self.lstClosureHistory(dataRes);
                   for (var closure: ClosureHistoryFindDto of data){
                        if(closure.id == closureId){
                            self.selectCodeLstClosure(closure);
                            self.detailClosure(closureId);
                            return;    
                        }    
                   }
                   
                });
            }
            
            
            collectDataHistory(): ClosureHistoryDto{
                var self = this;
                var dto: ClosureHistoryDto;
                dto = new ClosureHistoryDto();
                dto.closeName = self.closureHistoryModel.closureName();
                dto.closureId = self.closureHistoryModel.closureId();
                dto.closureHistoryId = self.closureHistoryModel.historyId();
                dto.closureDate = self.closureHistoryModel.closureDate();
                return dto;    
            }
            
             // 締め期間確認 
            public openConfirmClosingPeriodDialog(): void {
                var self = this;
                nts.uk.ui.windows.setShared('closureId', self.closureModel.closureId());
                nts.uk.ui.windows.setShared('historyId', self.closureHistoryModel.historyId());
                nts.uk.ui.windows.sub.modal('/view/kmk/012/d/index.xhtml', 
                { title: '締め期間確認 ', dialogClass: 'no-close' }).onClosed(function(){
                    self.reloadPage(self.closureModel.closureId(), self.closureHistoryModel.historyId());    
                });
            }
    
            
        }
        
            
        export class ClosureHistoryModel {

            id: KnockoutObservable<number>;
            name: KnockoutObservable<string>;

            constructor() {
                this.id = ko.observable(0);
                this.name = ko.observable('');
            }

            updateDate(dto: ClosureHistoryFindDto) {
                this.id(dto.id);
                this.name(dto.name);
            }
        }

        export class ClosureModel {
            /** The closure id. */
            closureId: KnockoutObservable<number>;

            /** The use classification. */
            useClassification: KnockoutObservable<number>;

            /** The day. */
            month: KnockoutObservable<number>;
            
            
            closureHistories: KnockoutObservableArray<ClosureHistoryMDto>;

            constructor() {
                this.closureId = ko.observable(0);
                this.useClassification = ko.observable(0)
                this.month = ko.observable(0);
                this.closureHistories = ko.observableArray<ClosureHistoryMDto>([]);
            }

            updateData(dto: ClosureDto):void {
                this.closureId(dto.closureId);
                this.useClassification(dto.useClassification);
                this.month(dto.month);
                this.closureHistories([]);
                var dataRes : ClosureHistoryMDto[] = [];
                for(var history :ClosureHistoryMDto of dto.closureHistories){
                     var dataI: ClosureHistoryMDto = new ClosureHistoryMDto();
                    dataI.historyId = history.historyId; 
                    dataI.closureId = history.closureId;
                    dataI.endDate = history.endDate;
                    dataI.startDate = history.startDate;
                    dataI.updateData();
                    dataRes.push(dataI);
                }
                this.closureHistories(dataRes);
            }
            
            
        }

        export class ClosureHistoryDetailModel {

            /** The history id. */
            historyId: KnockoutObservable<string>;

            /** The closure id. */
            closureId: KnockoutObservable<number>;

            /** The end date. */
            // 終了年月: 年月
            closureName: KnockoutObservable<string>;

            /** The start date. */
            // 開始年月: 年月
            closureDate: KnockoutObservable<number>;

            constructor(){
                this.historyId = ko.observable('')
                this.closureId = ko.observable(0);
                this.closureName = ko.observable('');
                this.closureDate = ko.observable(0);
            }
                
                
            updateData(dto: ClosureHistoryDDto): void{
                this.historyId(dto.historyId);
                this.closureId(dto.closureId);
                this.closureName(dto.closureName);
                this.closureDate(dto.closureDate);
            }
            
        }

    }
}