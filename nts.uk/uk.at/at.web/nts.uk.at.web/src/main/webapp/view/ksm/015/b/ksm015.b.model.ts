let gridColums = [
       { headerText: nts.uk.resource.getText('KSM015_13'), key: 'shiftMasterCode', width: 55 },
       { headerText: nts.uk.resource.getText('KSM015_14'), key: 'shiftMasterName', width: 55, formatter: _.escape },
       { headerText: nts.uk.resource.getText('KSM015_15'), key: 'workTypeName', width: 125, formatter: _.escape },
       { headerText: nts.uk.resource.getText('KSM015_16'), key: 'workTimeName', width: 125, formatter: _.escape }
];

let searchValueOption = ko.mapping.fromJS(new nts.uk.ui.option.TextEditorOption({
       textmode: "text",
       placeholder: "Placeholder for text editor",
       width: "120px",
       textalign: "left"
}));

class ShiftGridOption {
       gridColums: Array<any>;
       searchValueOption: String;

       constructor() {
              let self = this;
              self.gridColums = gridColums;
              self.searchValueOption = searchValueOption;

       }
}

class WorkStyle {
       	color: KnockoutObservable<String>;
		backGroundColor: KnockoutObservable<String>;
		borderColor: KnockoutObservable<String>;
        workTimeSetDisplay: KnockoutObservable<String>;
		isDisplay: KnockoutObservable<String>;

       constructor() {
              let self = this;
              self.color = ko.observable('');
		      self.backGroundColor = ko.observable('');
              self.workTimeSetDisplay = ko.observable('');
			  self.isDisplay = ko.observable('');
			  self.borderColor = ko.observable('');

       }
}

class RegistrationForm {
       selectedCode: KnockoutObservable<String>;
       shiftMasterName: KnockoutObservable<String>;
       color: KnockoutObservable<String>;
       colorSmartphone: KnockoutObservable<String>;
       note: KnockoutObservable<String>;
       importCode: KnockoutObservable<String>;
       newMode: KnockoutObservable<Boolean>;
       workTypeCd: KnockoutObservable<String>;
       workTypeName: KnockoutObservable<String>;
       workTypeDisplay: KnockoutObservable<String>;
       workTimeSetCd: KnockoutObservable<String>;
       workTimeSetName: KnockoutObservable<String>;
       workTimeSetDisplay: KnockoutObservable<String>;

       constructor() {
              let self = this;
              self.selectedCode = ko.observable("");
              self.selectedCode.subscribe(function(codeChanged: string) {
                     self.selectedCode($.trim(self.selectedCode()));
              });
              self.shiftMasterName = ko.observable("");
              self.shiftMasterName.subscribe(function(codeChanged: string) {
                     self.shiftMasterName($.trim(self.shiftMasterName()));
                     if (_.isEmpty(self.importCode())){
                            //copy B7_2 to B11_3
                            self.importCode(self.shiftMasterName());
                     }
              });
              self.color = ko.observable("#FFFFFF");
              self.colorSmartphone = ko.observable("#FFFFFF");
              self.note = ko.observable("");
              self.importCode = ko.observable("");
              self.importCode.subscribe(function (){
                     self.importCode($.trim(self.importCode()));
              })
              self.newMode = ko.observable(false);
              self.workTypeCd = ko.observable('');
              self.workTypeName = ko.observable('');
              self.workTypeDisplay = ko.observable('');
              self.workTypeCd.subscribe((val) => {
                      if(self.workTypeName().endsWith(nts.uk.resource.getText('KSM015_29'))) {
                             self.workTypeDisplay(self.workTypeName());
                      } else {
                             self.workTypeDisplay(val + '   ' + self.workTypeName());
                      }
              });
           
              self.workTimeSetCd = ko.observable('');
              self.workTimeSetName = ko.observable('');
              self.workTimeSetDisplay = ko.observable('');
              self.workTimeSetCd.subscribe((val) => {
                  if(self.workTimeSetName().endsWith(nts.uk.resource.getText('KSM015_29'))) {
                             self.workTimeSetDisplay(self.workTimeSetName());
                      } else {
                             self.workTimeSetDisplay(val + '   ' + self.workTimeSetName());
                      }
              });
       }

       public bindData(shiftMaster: ShiftMaster) {
              let self = this;
              if (shiftMaster) {
                     self.selectedCode(shiftMaster.shiftMasterCode);
                     self.shiftMasterName(shiftMaster.shiftMasterName);
                     self.color("#" + shiftMaster.color);
                     self.note(shiftMaster.remark);
                     self.importCode(shiftMaster.importCode);
                     self.newMode(false);
                     self.workTypeName(shiftMaster.workTypeName);
                     self.workTypeCd(shiftMaster.workTypeCd);
                     self.workTimeSetName(shiftMaster.workTimeName);
                     self.workTimeSetCd(shiftMaster.workTimeCd);
              }

       }

       public clearData() {
              let self = this;
              self.newMode(true);
              self.selectedCode('');
              self.shiftMasterName('');
              self.color("#FFFFFF");
              self.note('');
              self.importCode("");
              self.workTypeName('');
              self.workTypeCd('');
              self.workTimeSetName('');
              self.workTimeSetCd('');
       }

       public trimData() {
              let self = this;
              self.selectedCode(self.selectedCode().trim());
              self.shiftMasterName(self.shiftMasterName().trim());
              self.importCode(self.importCode().trim());
       }
       
}

interface ShiftMaster {
       companyId: String;
       shiftMasterName: String;
       shiftMasterCode: String;
       color: String;
       remark: String;
       importCode: String;
       workTypeCd: String;
       workTypeName: String;
       workTimeCd: String;
       workTimeName: String;
}

class RegisterShiftMasterDto {
       shiftMasterName: String;
       shiftMasterCode: String;
       color: String;
       colorSmartphone: String;
       remark: String;
       importCode: String;
       workTypeCd: String;
       workTimeSetCd: String;
       newMode: Boolean;
       constructor(form: RegistrationForm) {
              this.shiftMasterName = form.shiftMasterName();
              this.shiftMasterCode = form.selectedCode();
              this.color = form.color().replace('#', '');
              this.colorSmartphone = form.color().replace('#', ''); // Hien tai bang vs color
              this.remark = form.note();
              this.importCode = form.importCode();
              this.workTypeCd = form.workTypeCd();
              this.workTimeSetCd = form.workTimeSetCd();
              this.newMode = form.newMode();
       }
}