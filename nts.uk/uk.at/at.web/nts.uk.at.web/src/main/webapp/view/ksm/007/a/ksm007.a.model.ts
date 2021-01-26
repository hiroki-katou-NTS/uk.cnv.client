class RegisterForm {
    workplaceGroupCd: KnockoutObservable<string> = ko.observable('');
    workplaceGroupName: KnockoutObservable<string> = ko.observable('');
    workplaceGroupTypes: KnockoutObservableArray<string> = ko.observableArray([
        {value: 1, name: nts.uk.resource.getText('KSM007_9')},
        {value: 2, name: nts.uk.resource.getText('KSM007_10')},
        {value: 0, name: nts.uk.resource.getText('KSM007_17')}
    ]);
    selectedWkpType: KnockoutObservable<number> = ko.observable(1);
    newMode: KnockoutObservable<any> = ko.observable(true);
    workplaces: KnockoutObservableArray<any> = ko.observableArray([]);
    gridColumns: Array<any> = ko.observableArray([
        { headerText: nts.uk.resource.getText('KSM007_13'), key: 'workplaceId', width: 100, hidden: true },
        { headerText: nts.uk.resource.getText('KSM007_13'), key: 'workplaceCode', width: 100,}, 
        { headerText: nts.uk.resource.getText('KSM007_14'), key: 'workplaceName', width: 170 },
        { headerText: nts.uk.resource.getText('KSM007_15'), key: 'genericName', width: 890 }
    ]);
    selectedWorkplaces: KnockoutObservableArray<any> = ko.observableArray([]);
    //ver 10
    startTime: KnockoutObservable<string> = ko.observable('10:00');
    endTime: KnockoutObservable<string> = ko.observable('18:00');

    constructor() {
        let self = this;
    }

    public clearData() {
        let self = this;
        nts.uk.ui.errors.clearAll();
        self.workplaceGroupCd('');
        self.workplaceGroupName('');
        self.selectedWkpType(1);
        self.workplaces([]);
        self.newMode(true);
    }

    public bindData(val: IWorkplaceGroup) {
        let self = this;
        self.workplaceGroupCd(val.code);
        self.workplaceGroupName(val.name);
        self.selectedWkpType(val.type); // if 介護事業所 = 0 -> 0
        self.newMode(false);
        //self.startTime(val.startTime); 
        //self.endTime(val.endTime);
        nts.uk.ui.errors.clearAll();
    }

    public bindWorkplace(workplaces) {
        let sorted = _.orderBy(workplaces, (val) =>  'hierarchyCode');
        this.workplaces(sorted);
        this.selectedWorkplaces([]);
    }

    public removeWorkplace() {
        let self = this;
        let filtered = _.filter(self.workplaces(), (val) => { return self.selectedWorkplaces().indexOf(val.workplaceId) === -1; });
        self.workplaces(filtered);
    }

    public trimData() {
        let self = this;
        self.workplaceGroupCd(self.workplaceGroupCd().trim());
        self.workplaceGroupName(self.workplaceGroupName().trim());
 }
    
    public convertToCommand(wkpGrID: number) {
        let self = this;
        return {
            wkpGrID: wkpGrID,
            wkpGrCD: self.workplaceGroupCd(),
            wkpGrName: self.workplaceGroupName(),
            wkpGrType: self.selectedWkpType(),
            lstWKPID: _.map(self.workplaces(), "workplaceId")
        }
        
    }
}


interface IWorkplaceGroup {
    id: string;
    code: string;
    name: string;
    type: number;
    startTime: string;
    endTime: string;
}

interface IWorkplaceRegisterCmd {
    wkpGrID: string;
    wkpGrCD: string;
    wkpGrName: string;
    wkpGrType: string;
    lstWKPID: Array<string>;
}