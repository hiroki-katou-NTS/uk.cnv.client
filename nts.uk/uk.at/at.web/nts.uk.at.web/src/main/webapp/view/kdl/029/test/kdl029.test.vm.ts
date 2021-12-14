module nts.uk.at.view.kdl029.test.screenModel {

    import dialog = nts.uk.ui.dialog.info;
    import text = nts.uk.resource.getText;
    import formatDate = nts.uk.time.formatDate;
    import block = nts.uk.ui.block;
    import jump = nts.uk.request.jump;
    import alError = nts.uk.ui.dialog.alertError;
    import modal = nts.uk.ui.windows.sub.modal;
    import setShared = nts.uk.ui.windows.setShared;
    export class ViewModel {
        lstSID: KnockoutObservable<string> = ko.observable('');
        demo: KnockoutObservable<string> = ko.observable('');
        constructor() {
            let self = this;
            let demo = 'c7cb93ce-d23b-4283-875e-a0bbb21b9d36(D00004),fe3b1f44-dbc8-44c0-ab32-f617f01f00a5(D00009),'
                    + '96c1e494-5cde-402c-8629-81b0dec7ac92(D00007),da1886cf-b80f-425c-af09-44a94a7643f2(D00010),'
                    + 'ae69eb3f-4198-47e3-9f98-967d23c00997(D00002),958bcd65-bdff-4051-8a71-181de1890f14(D00005),'
                    + 'ed40e708-a397-4a12-bc94-08485e731eeb(D00006),c5faa723-8e1e-49c8-b10b-28657f7e0f69(D00001),'
                    + 'de0124d4-1f65-412a-9a83-f55c9649fa1d(000001),f6984674-1bc6-4be1-807e-4e09853df5c6(D00003),'
                    + 'adac4103-e0b9-4873-988a-2b04046d55f7(D00008)';
            self.demo(demo);
        }

        openkdl029Dialog() {
            let self = this;
            let listID = self.list(self.lstSID());
            if(nts.uk.util.isNullOrEmpty(self.lstSID()) || listID.length == 0){
                alert("Hay chon nhan vien");
                return;
            }
            let param = {employeeIds: listID,
                        baseDate: moment(new Date()).format("YYYY/MM/DD")}
            setShared('KDL029_PARAM', param);
            modal('/view/kdl/029/a/index.xhtml').onClosed(function(): any {

            });

        }
        list(str: string):Array<string>{
            return _.split(str, ',');
        }
        start(): JQueryPromise<any> {
        
            var self = this,
                dfd = $.Deferred();


            dfd.resolve();

            return dfd.promise();
        }
    }
}