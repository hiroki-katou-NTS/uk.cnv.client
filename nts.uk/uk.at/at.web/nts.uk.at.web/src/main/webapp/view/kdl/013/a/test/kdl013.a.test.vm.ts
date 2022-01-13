module nts.uk.at.kdl013.a.test {    

    import setShared = nts.uk.ui.windows.setShared;
    import getShared = nts.uk.ui.windows.getShared;
    const PATH = {
        GET_POSSIBLE_ATTENDANCE_ITEM: "at/record/divergencetime/setting/AttendanceDivergenceName",
        GET_ALL_ATTENDANCE_ITEM: "at/record/businesstype/attendanceItem/getAttendanceItems"
    }

    @bean()
    class Kdl013aTestViewModel extends ko.ViewModel {
        items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);
        selectedCode: KnockoutObservable<string> = ko.observable('');
        date: KnockoutObservable<any> = ko.observable(new Date());
        
        constructor(params: any) {
            super();
            let self = this;
            self.loadData();
        }

        loadData(): void {
            let self = this;
            self.$ajax(PATH.GET_ALL_ATTENDANCE_ITEM).done((lstItem: Array<any>) => {                
                let data = _.map(lstItem, item => { return new ItemModel(item.attendanceItemId, item.attendanceItemName) });
                self.items(data)
            }).always(() => {
                self.$blockui("hide");
            });
        }

        openDialog(): void {
            let self = this;   
            let request = {
                atdId: self.selectedCode(),
                baseDate: moment(new Date()).format("YYYY/MM/DD"),
                // selectedCode: ""
            }

            setShared('KDL013Params', {
                atdId: self.selectedCode(),
                baseDate: moment(new Date()).format("YYYY/MM/DD"),
                // selectedCode: " "
            });
           
            self.$window.modal('at', '/view/kdl/013/a/index.xhtml', request).then(() =>{
                let temp = getShared('KDL013ParamsReturn');
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
}