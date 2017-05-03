module nts.uk.com.view.ccg015.a {
    export module viewmodel {
        import TopPageItemDto = ccg015.a.service.model.TopPageItemDto;
        import TopPageDto = ccg015.a.service.model.TopPageDto;
        export class ScreenModel {
            listTopPage: KnockoutObservableArray<Node>;
            toppageSelectedCode: KnockoutObservable<string>;
            topPageModel: KnockoutObservable<TopPageModel>;
            columns: KnockoutObservable<any>;
            isNewMode: KnockoutObservable<boolean>;
            languageListOption: KnockoutObservableArray<ItemCbbModel>;
            languageSelectedCode: KnockoutObservable<string>;
            constructor() {
                var self = this;
                self.listTopPage = ko.observableArray<Node>([]);
                self.toppageSelectedCode = ko.observable(null);
                self.topPageModel = ko.observable(new TopPageModel());
                self.columns = ko.observableArray([
                    { headerText: "コード", width: "50px", key: 'code', dataType: "string", hidden: false },
                    { headerText: "名称", width: "300px", key: 'nodeText', dataType: "string" }
                ]);
                self.isNewMode = ko.observable(true);
                self.toppageSelectedCode.subscribe(function(selectedTopPageCode: string) {
                    service.loadDetailTopPage(selectedTopPageCode).done(function(data: TopPageDto) {
                        self.loadTopPageItemDetail(data);
                    });
                    self.isNewMode(false);
                });
                self.languageListOption = ko.observableArray([
                    new ItemCbbModel("0", "日本語"),
                    new ItemCbbModel("1", "英語"),
                    new ItemCbbModel("2", "ベトナム語")
                ]);
                self.languageSelectedCode = ko.observable("0");
                //end constructor
            }
            start(): JQueryPromise<void> {
                var self = this;
                var dfd = $.Deferred<void>();
                self.loadTopPageList().done(function() {
                    //TODO 
                    dfd.resolve();
                });
                return dfd.promise();
            }
            
            private loadTopPageList() : JQueryPromise<void>
            {
                var self = this;
                var dfd = $.Deferred<void>();
                self.listTopPage([]);
                service.loadTopPage().done(function(data: Array<TopPageItemDto>) {
                    data.forEach(function(item, index) {
                        self.listTopPage.push(new Node(item.topPageCode, item.topPageName, null));
                        dfd.resolve();
                    });
                    if (self.listTopPage().length > 0) {
                        self.toppageSelectedCode(self.listTopPage()[0].code);
                    }
                });
                return dfd.promise();
            }

            //load top page Item 
            private loadTopPageItemDetail(data: TopPageDto) {
                var self = this;
                self.topPageModel().topPageCode(data.topPageCode);
                self.topPageModel().topPageName(data.topPageName);
                //TODO su dung service de lay layout rieng
//                if (data.placements) {
//                    data.placements.forEach(function(item, index) {
//                        var placementModel = new PlacementModel();
//                        var topPagePartModel = new TopPagePartModel();
//                        //set value for top page part
//                        topPagePartModel.topPagePartType(item.topPagePart.topPagePartType);
//                        topPagePartModel.topPagePartCode(item.topPagePart.topPagePartCode);
//                        topPagePartModel.topPagePartName(item.topPagePart.topPagePartName);
//                        topPagePartModel.width(item.topPagePart.width);
//                        topPagePartModel.height(item.topPagePart.height);
//
//                        placementModel.row(item.row);
//                        placementModel.column(item.column);
//                        placementModel.topPagePart(topPagePartModel);
//
//                        self.topPageModel().placement().push(placementModel);
//                    });
//                }
            }
            private collectData(): TopPageDto {
                var self = this;
                //mock data
                var data: TopPageDto = { topPageCode: self.topPageModel().topPageCode(), topPageName: self.topPageModel().topPageName(), languageNumber: 0, layoutId: "luid" };
                return data;
            }
            private collectDataForCreateNew(): TopPageDto {
                return null;
            }
            private saveTopPage() {
                var self = this;
                //check update or create
                if (self.isNewMode()) {
                    service.registerTopPage(self.collectData()).done(function() {
                        //register success                     
                    });
                }
                else {
                    service.updateTopPage(self.collectData()).done(function() {
                        //update success
                    });
                }
                self.loadTopPageList();
                //TODO focus create item   
            }
            private openMyPageSettingDialog() {
                nts.uk.ui.windows.sub.modal("/view/ccg/015/b/index.xhtml", {
                    height: 700, width: 850,
                    title: "マイページの設定",
                    dialogClass: 'no-close'
                }).onClosed(() => {
                    //TODO on Close dialog
                });

            }

            private copyTopPage() {
                var self = this;
                nts.uk.ui.windows.setShared('topPageCode', self.topPageModel().topPageCode());
                nts.uk.ui.windows.setShared('topPageName', self.topPageModel().topPageName());
                nts.uk.ui.windows.sub.modal("/view/ccg/015/c/index.xhtml", {
                    height: 350, width: 650,
                    title: "他のトップページコピー",
                    dialogClass: 'no-close'
                }).onClosed(() => {
                    //TODO on Close dialog
                });

            }
            private newTopPage() {
                var self = this;
                self.topPageModel(new TopPageModel());
                self.isNewMode(true);
            }
            private removeTopPage() {
                var self = this;
                service.deleteTopPage(self.toppageSelectedCode()).done(function() {
                    //delete success
                }).fail();
            }
        }
        export class Node {
            code: string;
            name: string;
            nodeText: string;
            custom: string;
            childs: Array<Node>;
            constructor(code: string, name: string, childs: Array<Node>) {
                var self = this;
                self.code = code;
                self.name = name;
                self.nodeText = name;
                self.childs = childs;
                self.custom = 'Random' + new Date().getTime();
            }
        }
        export class TopPageModel {
            topPageCode: KnockoutObservable<string>;
            topPageName: KnockoutObservable<string>;
            placement: KnockoutObservableArray<PlacementModel>;
            constructor() {
                this.topPageCode = ko.observable('');
                this.topPageName = ko.observable('');
                this.placement = ko.observableArray([]);
            }
        }
        export class PlacementModel {
            row: KnockoutObservable<number>;
            column: KnockoutObservable<number>;
            topPagePart: KnockoutObservable<TopPagePartModel>;
            constructor() {
                this.row = ko.observable(0);
                this.column = ko.observable(0);
                this.topPagePart = ko.observable(new TopPagePartModel());
            }
        }
        export class TopPagePartModel {
            topPagePartType: KnockoutObservable<number>;
            topPagePartCode: KnockoutObservable<string>;
            topPagePartName: KnockoutObservable<string>;
            width: KnockoutObservable<number>;
            height: KnockoutObservable<number>;
            constructor() {
                this.topPagePartType = ko.observable(0);
                this.topPagePartCode = ko.observable("");
                this.topPagePartName = ko.observable("");
                this.width = ko.observable(0);
                this.height = ko.observable(0);
            }
        }

        export class ItemCbbModel {
            code: string;
            name: string;
            label: string;
            constructor(code: string, name: string) {
                this.code = code;
                this.name = name;
            }
        }

    }
}