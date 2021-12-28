module nts.uk.at.view.kaf022.v.viewmodel {
    import getText = nts.uk.resource.getText;

    export class ScreenModelV {
        menuList: KnockoutObservableArray<MenuModel>;
        allChecked: KnockoutObservable<boolean>;

        constructor() {
            const self = this;
            self.menuList = ko.observableArray([]);
            $("#fixed-table-v2").ntsFixedTable({});

            self.allChecked = ko.pureComputed({
                read: function () {
                    return self.menuList().length > 0 && self.menuList().filter(m => m.checked()).length == self.menuList().length;
                },
                write: function (value) {
                    if (value) {
                        self.menuList().forEach(m => m.checked(true));
                    } else {
                        self.menuList().forEach(m => m.checked(false));
                    }
                },
                owner: self
            });
        }

        initData(allData: any): void {
            const self = this;
            const menus: Array<StandardMenuNameExport> = allData.menus || [];
            const appSetForProxyApp: Array<any> = allData.applicationSetting ? allData.applicationSetting.appSetForProxyApp : [];
            const displayList: Array<MenuModel> = [
                new MenuModel(
                    "KAF005A0",
                    !!_.find(appSetForProxyApp, o => o.appType == 0 && o.overtimeAppAtr == 0),
                    (_.find(menus, m => m.programId == "KAF005" && m.queryString == "overworkatr=0") || {displayName: null}).displayName || getText("KAF022_775")
                ),
                new MenuModel(
                    "KAF005A1",
                    !!_.find(appSetForProxyApp, o => o.appType == 0 && o.overtimeAppAtr == 1),
                    (_.find(menus, m => m.programId == "KAF005" && m.queryString == "overworkatr=1") || {displayName: null}).displayName || getText("KAF022_776")
                ),
                new MenuModel(
                    "KAF005A2",
                    !!_.find(appSetForProxyApp, o => o.appType == 0 && o.overtimeAppAtr == 2),
                    (_.find(menus, m => m.programId == "KAF005" && m.queryString == "overworkatr=2") || {displayName: null}).displayName || getText("KAF022_777")
                ),
                new MenuModel(
                    "KAF005A3",
                    !!_.find(appSetForProxyApp, o => o.appType == 0 && o.overtimeAppAtr == 3),
                    (_.find(menus, m => m.programId == "KAF005" && m.queryString == "overworkatr=3") || {displayName: null}).displayName || getText("KAF022_802")
                ),
                new MenuModel(
                    "KAF006A",
                    !!_.find(appSetForProxyApp, o => o.appType == 1),
                    (_.find(menus, m => m.programId == "KAF006") || {displayName: null}).displayName || getText("KAF022_4")
                ),
                new MenuModel(
                    "KAF007A",
                    !!_.find(appSetForProxyApp, o => o.appType == 2),
                    (_.find(menus, m => m.programId == "KAF007") || {displayName: null}).displayName || getText("KAF022_5")
                ),
                new MenuModel(
                    "KAF008A",
                    !!_.find(appSetForProxyApp, o => o.appType == 3),
                    (_.find(menus, m => m.programId == "KAF008") || {displayName: null}).displayName || getText("KAF022_6")
                ),
                new MenuModel(
                    "KAF009A",
                    !!_.find(appSetForProxyApp, o => o.appType == 4),
                    (_.find(menus, m => m.programId == "KAF009") || {displayName: null}).displayName || getText("KAF022_7")
                ),
                new MenuModel(
                    "KAF010A",
                    !!_.find(appSetForProxyApp, o => o.appType == 6),
                    (_.find(menus, m => m.programId == "KAF010") || {displayName: null}).displayName || getText("KAF022_8")
                ),
                new MenuModel(
                    "KAF012A",
                    !!_.find(appSetForProxyApp, o => o.appType == 8),
                    (_.find(menus, m => m.programId == "KAF012") || {displayName: null}).displayName || getText("KAF022_9")
                ),
                new MenuModel(
                    "KAF004A",
                    !!_.find(appSetForProxyApp, o => o.appType == 9),
                    (_.find(menus, m => m.programId == "KAF004") || {displayName: null}).displayName || getText("KAF022_10")
                ),
                new MenuModel(
                    "KAF002A",
                    !!_.find(appSetForProxyApp, o => o.appType == 7 && o.stampRequestMode == 0),
                    (_.find(menus, m => m.programId == "KAF002" && m.screenId == "A") || {displayName: null}).displayName || getText("KAF022_11")
                ),
                new MenuModel(
                    "KAF002B",
                    !!_.find(appSetForProxyApp, o => o.appType == 7 && o.stampRequestMode == 1),
                    (_.find(menus, m => m.programId == "KAF002" && m.screenId == "B") || {displayName: null}).displayName || getText("KAF022_778")
                ),
                new MenuModel(
                    "KAF011A",
                    !!_.find(appSetForProxyApp, o => o.appType == 10),
                    (_.find(menus, m => m.programId == "KAF011") || {displayName: null}).displayName || getText("KAF022_12")
                ),
                new MenuModel(
                    "KAF020A",
                    !!_.find(appSetForProxyApp, o => o.appType == 15),
                    (_.find(menus, m => m.programId == "KAF020") || {displayName: null}).displayName || getText("KAF022_705")
                ),
            ];
            self.menuList(displayList);
        }

        collectData(): Array<any> {
            const self = this;
            const data: Array<any> = ko.toJS(self.menuList);
            return data.filter(o => o.checked)
                .map(o => {
                    switch (o.id) {
                        case "KAF005A0":
                            return {appType: 0, overtimeAppAtr: 0, stampRequestMode: null};
                        case "KAF005A1":
                            return {appType: 0, overtimeAppAtr: 1, stampRequestMode: null};
                        case "KAF005A2":
                            return {appType: 0, overtimeAppAtr: 2, stampRequestMode: null};
                        case "KAF005A3":
                            return {appType: 0, overtimeAppAtr: 3, stampRequestMode: null};
                        case "KAF006A":
                            return {appType: 1, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF007A":
                            return {appType: 2, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF008A":
                            return {appType: 3, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF009A":
                            return {appType: 4, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF010A":
                            return {appType: 6, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF012A":
                            return {appType: 8, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF004A":
                            return {appType: 9, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF002A":
                            return {appType: 7, overtimeAppAtr: null, stampRequestMode: 0};
                        case "KAF002B":
                            return {appType: 7, overtimeAppAtr: null, stampRequestMode: 1};
                        case "KAF011A":
                            return {appType: 10, overtimeAppAtr: null, stampRequestMode: null};
                        case "KAF020A":
                            return {appType: 15, overtimeAppAtr: null, stampRequestMode: null};
                        default:
                            return null;
                    }
                });
        }

    }

    class MenuModel {
        id: string;
        checked: KnockoutObservable<boolean>;
        name: string; //表示名称

        constructor(id: string, checked: boolean, name: string) {
            this.id = id;
            this.checked = ko.observable(checked);
            this.name = name;
        }
    }

    interface StandardMenuNameExport {
        // プログラムID
        programId: string;

        // 遷移先の画面ID
        screenId: string;

        // クエリ文字列
        queryString: string;

        //表示名称
        displayName: string;
    }

}