module nts.uk.at.view.kdl053.test {
	import setShare = nts.uk.ui.windows.setShared;
	// Import
	export module viewmodel {
		export class ScreenModel {	
			currentScreen: any = null;	
			columns: KnockoutObservableArray<NtsGridListColumn>;

			items: KnockoutObservableArray<ItemModel> = ko.observableArray([]);;		
			currentCode: KnockoutObservable<any> = ko.observable();
			currentCodeList: KnockoutObservableArray<any> = ko.observableArray([]);;
			count: number = 100;
			// switchOptions: KnockoutObservableArray<any>;

			itemList: KnockoutObservableArray<any>;
			selectedId: KnockoutObservable<number>;
			enable: KnockoutObservable<boolean>;
			dispItemCol: KnockoutObservable<number> = ko.observable(null);

			errorRegistrations = ko.observable( [
						{id: 0,sid: 'b4496e4d-2c79-4611-85c1-e251a72c8ce8', scd:'000001', empName:'天海　春香',  date:'2020/12/08', attendanceItemId: 163, errorMessage: '開始時刻と終了時刻が逆転してします。'}, 
                        {id: 1,sid: 'b4496e4d-2c79-4611-85c1-e251a72c8ce8', scd:'000001', empName:'天海　春香', date:'2020/12/09', attendanceItemId: 195, errorMessage: '開始時刻と終了時刻が逆転してします。'}, 
                        {id: 2,sid: 'b4496e4d-2c79-4611-85c1-e251a72c8ce8', scd:'000001', empName:'天海　春香', date:'2020/12/10', attendanceItemId: 201, errorMessage: '開始時刻と終了時刻が逆転してします。' },
                        {id: 3,sid: 'b4496e4d-2c79-4611-85c1-e251a72c8ce8', scd:'000001', empName:'天海　春香', date:'2020/12/11', attendanceItemId: 205, errorMessage: '開始時刻と終了時刻が逆転してします。' },
                        {id: 4,sid: 'b4496e4d-2c79-4611-85c1-e251a72c8ce8', scd:'000001', empName:'天海　春香', date:'2020/12/12', attendanceItemId: 537, errorMessage: '開始時刻と終了時刻が逆転してします。' },                  
                        {id: 5,sid: 'b4496e4d-2c79-4611-85c1-e251a72c8ce8', scd:'000001', empName:'天海　春香', date:'2020/12/13', attendanceItemId: 165, errorMessage: '開始時刻と終了時刻が逆転してします。' },
                        {id: 6,sid: '47cd3836-8dd3-4352-ab81-50eb4074e5fc',scd:'000002',empName:'新規　ログ', date:'2020/12/11', attendanceItemId: 165, errorMessage: '開始時刻と終了時刻が逆転してします。'},                        
                        {id: 7,sid: '47cd3836-8dd3-4352-ab81-50eb4074e5fc',scd:'000002',empName:'新規　ログ', date:'2020/12/12', attendanceItemId: 195, errorMessage: '開始時刻と終了時刻が逆転してします。' },
                        {id: 8,sid: '47cd3836-8dd3-4352-ab81-50eb4074e5fc',scd:'000002',empName:'新規　ログ', date:'2020/12/13', attendanceItemId: 201, errorMessage: '開始時刻と終了時刻が逆転してします。' },
                        {id: 9,sid: '1d7a260a-864d-4577-8e1d-4814c04c540f',scd:'000012', empName:'承認者　課長', date:'2020/12/08', attendanceItemId: 205, errorMessage: '開始時刻と終了時刻が逆転してします。' },
                        {id: 10,sid: '1d7a260a-864d-4577-8e1d-4814c04c540f',scd:'000012', empName:'承認者　課長', date:'2020/12/09', attendanceItemId: 537, errorMessage: '開始時刻と終了時刻が逆転してします。' },
						{id: 11,sid: '1d7a260a-864d-4577-8e1d-4814c04c540f',scd:'000012', empName:'承認者　課長', date:'2020/12/13', attendanceItemId: 537, errorMessage: '開始時刻と終了時刻が逆転してします。'},
						{id: 12,sid: 'b4496e4d-2c79-4611-85c1-e251a72c8ce8', scd:'000001', empName:'天海　春香', date:'2020/12/07', attendanceItemId: 165, errorMessage: '開始時刻と終了時刻が逆転してします。' }
					]);
			employeeIdList = ko.observable(
				'1d7a260a-864d-4577-8e1d-4814c04c540f,' +  'b4496e4d-2c79-4611-85c1-e251a72c8ce8,' +
				'47cd3836-8dd3-4352-ab81-50eb4074e5fc'
			);			
			
			constructor() {
				var self = this;
				self.columns = ko.observableArray([
					{ key: 'id',width:20, hidden: true },
					{ headerText: '社員コード', key: 'code', width: 80 },
					{ headerText: '社員名称', key: 'name', width: 100 },
					{ headerText: '日付', key: 'date', width: 150 }, 
					{ headerText: '項目ID', key: 'attendanceItemId', width: 80 }, 
					{ headerText: 'メッセージ', key: 'msg', width: 280}					
				]);
				_.each(self.errorRegistrations(), err =>{
					self.items.push(new ItemModel(err.id, err.scd, err.empName, err.date, err.attendanceItemId, err.errorMessage));
				})	

				self.itemList = ko.observableArray([
					new BoxModel(1, 'Yes'),
					new BoxModel(0, 'No')
				
				]);
				self.selectedId = ko.observable(1);
				self.enable = ko.observable(true);
			}
		

			public startPage(): JQueryPromise<any> {
				let self = this,
					dfd = $.Deferred();

				dfd.resolve();
				return dfd.promise();
			}

			openDialog(): void {
				let self = this;
				let request: any = {};		
				let errorRegistrationList: any = [];
				let employeeIds: any = [];
				
				const vm = new ko.ViewModel();
				

				request.isRegistered = self.selectedId();				
				employeeIds =  _.split(self.employeeIdList(), ','),
				request.employeeIds = employeeIds;
				
				_.each(self.currentCodeList(), id =>{
					errorRegistrationList.push(self.errorRegistrations()[id]);
				})
				request.errorRegistrationList = errorRegistrationList;
				request.dispItemCol = self.dispItemCol() != null ? self.dispItemCol() == 1 : null;
				// setShare('dataShareDialogKDL053', request);
				// self.currentScreen = nts.uk.ui.windows.sub.modal('/view/kdl/053/a/index.xhtml');
				vm.$window.modal('at', '/view/kdl/053/a/index.xhtml', request);
				
			}
		}	
		class ItemModel {
			id: number;
			code: string;
			name: string;
			date: string;
			attendanceItemId: number;
			msg: string;			
			constructor(id: number, code: string, name: string, date: string, attendanceItemId: number, msg: string) {
				this.id = id;
				this.code = code;
				this.name = name;
				this.date = date;
				this.attendanceItemId = attendanceItemId;
				this.msg = msg;				
			}
		}

		class BoxModel {
			id: number;
			name: string;
			constructor(id: number, name: string){
				var self = this;
				self.id = id;
				self.name = name;
			}
		}
	}	
}