module nts.uk.at.view.kdl052.test {
    import setShared = nts.uk.ui.windows.setShared;
    const Paths = {
        GET_SID: "at/request/dialog/employmentsystem/getSid"
    };

    
    @bean()
    class Kdl052TestViewModel extends ko.ViewModel {

        date: KnockoutObservable<any> = ko.observable(new Date());

        enable: KnockoutObservable<boolean> = ko.observable(true);
		//_____KCP005________
		listComponentOption: any = [];
		selectedCode: KnockoutObservable<string> = ko.observable('1');
		multiSelectedCode: KnockoutObservableArray<string> = ko.observableArray(['0', '1', '4']);
		isShowAlreadySet: KnockoutObservable<boolean> = ko.observable(false);
		alreadySettingList: KnockoutObservableArray<UnitAlreadySettingModel>;
		isDialog: KnockoutObservable<boolean> = ko.observable(false);
		isShowNoSelectRow: KnockoutObservable<boolean> = ko.observable(false);
		isMultiSelect: KnockoutObservable<boolean> = ko.observable(false);
		isShowWorkPlaceName: KnockoutObservable<boolean> = ko.observable(false);
		isShowSelectAllButton: KnockoutObservable<boolean> = ko.observable(false);
		disableSelection: KnockoutObservable<boolean> = ko.observable(false);

		employeeList: KnockoutObservableArray<UnitModel> = ko.observableArray<UnitModel>([]);

        constructor(private data: any) {
            super();
            const self = this;

            self.alreadySettingList = ko.observableArray([
				{ code: '1', isAlreadySetting: true },
				{ code: '2', isAlreadySetting: true }
			]);

          

            self.listComponentOption = {
				isShowAlreadySet: self.isShowAlreadySet(),
				isMultiSelect: true,
				listType: ListType.EMPLOYEE,
				employeeInputList: self.employeeList,
				selectType: SelectType.SELECT_BY_SELECTED_CODE,
				selectedCode: self.selectedCode,
				isDialog: self.isDialog(),
				isShowNoSelectRow: self.isShowNoSelectRow(),
				alreadySettingList: self.alreadySettingList,
				isShowWorkPlaceName: self.isShowWorkPlaceName(),
				isShowSelectAllButton: self.isShowSelectAllButton(),
				disableSelection: self.disableSelection(),
				maxRows : 15
			};
           
            self.loadData();
            // $('#kcp005Com').ntsListComponent(self.listComponentOption);

        }

        created() {

        }

        loadData(): void {
            let self = this;
            self.$ajax(Paths.GET_SID).done((data: any) => {
                _.forEach(data, (a: any, ind) => {
                    self.employeeList.push({ id: a, code: "00000000000" + (ind+1), name: a, workplaceName: 'HN' })
                });
               
				// self.listComponentOption.employeeInputList = data;
                $('#kcp005Com').ntsListComponent(self.listComponentOption);
			});
        }

        openDialog(): void {
            let self = this;           

            let empIds: any = _.map(_.filter(self.employeeList(), (z: any) => {
				return self.listComponentOption.selectedCode().contains(z.code + "");
			}), (a: any) => a.name);

            let data: any = {
                empIds : empIds,
                managementCheck: self.enable() ? 0 : 1
            }
            
            if(empIds.length == 0){
				alert( "Please choose employee !!!");
                return;
			}
            
			let param = {
                employeeIds: empIds,
                baseDate: moment(new Date()).format("YYYY/MM/DD")
            }
			setShared('KDL052A_PARAM', param);
            self.$window.modal('at', '/view/kdl/052/a/index.xhtml');            
        }
    }

    export interface UnitAlreadySettingModel {
		code: string;
		isAlreadySetting: boolean;
	}

    export class ListType {
		static EMPLOYMENT = 1;
		static CLASSIFICATION = 2;
		static JOB_TITLE = 3;
		static EMPLOYEE = 4;
	}

    export class SelectType {
		static SELECT_BY_SELECTED_CODE = 1;
		static SELECT_ALL = 2;
		static SELECT_FIRST_ITEM = 3;
		static NO_SELECT = 4;
	}

    export interface UnitModel {
		id?: string;
		code: string;
		name?: string;
		workplaceName?: string;
		isAlreadySetting?: boolean;
		optionalColumn?: any;
	}

}
	