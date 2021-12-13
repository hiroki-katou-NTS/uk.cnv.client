module nts.uk.at.view.ksu003.ab.viewmodel {
	import setShared = nts.uk.ui.windows.setShared;
	import getShared = nts.uk.ui.windows.getShared;
	import formatById = nts.uk.time.format.byId;
	import alertError = nts.uk.ui.dialog.alertError;
	import getText = nts.uk.resource.getText;
	import characteristics = nts.uk.characteristics;
	import block = nts.uk.ui.block;

	export class ScreenModel {
		KEY: string = 'USER_KSU003_INFOR';
		//viewA : any = __viewContext.viewModel.viewmodelA;
		dataFromScrA: any = [];
		characteristic: KnockoutObservable<ksu003.a.model.ILocalStore> = ko.observable();
		// Screen Ab1
		width: KnockoutObservable<number>;
		tabIndex: KnockoutObservable<number | string>;
		filter: KnockoutObservable<boolean> = ko.observable(false);
		disabled: KnockoutObservable<boolean> = ko.observable(false);
		workplaceIdKCP013: KnockoutObservable<string> = ko.observable('');
		selected: KnockoutObservable<string> = ko.observable();
		dataSources: KnockoutObservableArray<any> = ko.observableArray([]);
		taskList: KnockoutObservableArray<any> = ko.observableArray([]);
		taskChecked: KnockoutObservable<string> = ko.observable('');
		taskSelect: KnockoutObservable<string> = ko.observable('');
		enableAb : KnockoutObservable<boolean> = ko.observable(false);

		// Screen Ab2
		//list hyper link
		textButtonArr: KnockoutObservableArray<any> = ko.observableArray([]);
		selectedButton: KnockoutObservable<any> = ko.observable({});
		sourceCompany: KnockoutObservableArray<any> = ko.observableArray([]);
		dataSourceCompany: KnockoutObservableArray<any> = ko.observableArray([null, null, null, null, null]);
		hasDataButton: any = [];
		selectedPage: KnockoutObservable<number> = ko.observable(0);
		contextMenu: Array<any>;
		dataTaskPallet: any = [];
		dataTaskPalletDis: any = [];
		dataTaskInfo: any = [];
		errButton: any = [];
		lstEmpToC: any = [];
		constructor() { //id : workplaceId || workplaceGroupId; 
			let self = this;
			// Screen Ab1
			self.width = ko.observable(875);
			self.tabIndex = ko.observable('');
			// Screen Ab2
			let source: any[] = [{}, {}, {}, {}, {}];
			self.sourceCompany(source);
			self.contextMenu = [
				{ id: "openDialog", text: getText("作業選択"), action: "" }
			];

			$('#contain-view-right-ksu003').mousedown(function(event) {
				if (event.which == 3)
					setTimeout(function() {
						$('.ntsContextMenu').css("display", "none");
						$('.ntsContextMenu').css("margin-left", "30000px");
					}, 10);
			});

			self.selectedButton.subscribe((value) => {
				if (_.isEmpty(self.sourceCompany()[0]) && _.isEmpty(self.sourceCompany()[1]) && _.isEmpty(self.sourceCompany()[2]) &&
				_.isEmpty(self.sourceCompany()[3]) && _.isEmpty(self.sourceCompany()[4])){
					__viewContext.viewModel.viewmodelA.delPasteTask();
				}
				
				if(__viewContext.viewModel.viewmodelA.localStore.workSelection != 1)
					return;
					
				if (value.data == null || value.data == {}){
					__viewContext.viewModel.viewmodelA.delPasteTask();
					return;
				}

				if (value.data.text == getText("KSU003_70") || value.data.text == getText("KSU003_83")){
					__viewContext.viewModel.viewmodelA.delPasteTask();
					return;
				}
				
				if (_.isEmpty(self.dataTaskPallet) && _.isEmpty(self.dataTaskPalletDis)){
					__viewContext.viewModel.viewmodelA.delPasteTask();
					return;
				}
				
				if (value.column == -1 || value.row == -1) {
					self.selectedButton(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails);
				} else {
					__viewContext.viewModel.viewmodelA.localStore.workPalletDetails = value;
					characteristics.save(self.KEY, __viewContext.viewModel.viewmodelA.localStore);
				}
				//__viewContext.viewModel.viewmodelA.ruler.setMode("pasteFlex");
				if(!_.isNil(value.data)){
					
					let taskFilter = _.filter(self.dataTaskInfo.lstTaskDto, (x : any) => {
							return value.data.tooltip === x.taskDisplayInfoDto.taskName;
					});
					if(value.data.text != "t" && value.data.tooltip != "test" && __viewContext.viewModel.viewmodelA.selectedDisplayPeriod() == 2){
						__viewContext.viewModel.viewmodelA.setTaskMode("pasteFlex");
						
						if (taskFilter.length > 0)
						__viewContext.viewModel.viewmodelA.pasteTask(value, taskFilter[0].code);
					}
				}
				
				//$("#tableButton1").ntsButtonTable("setSelectedCell", value.row, value.column);
			});
		}

		public startPage(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			self.dataFromScrA = getShared("dataShareAbFromA");
			self.setDataDefautStore();
			self.textButtonArr([]);
			self.getTaskInfo().done(() => {
				//$("#tableButton1").ntsButtonTable("setSelectedCell", 1, 0);
				self.getTaskPallet().done(() => {
					self.setDataTaskInfo();
					self.sourceCompany(self.dataSourceCompany()[self.selectedPage()]);

					if (self.sourceCompany() != null)
						$("#tableButton1").ntsButtonTable("init", {
							row: 2, column: 5, source: self.sourceCompany(),
							contextMenu: self.contextMenu,
							disableMenuOnDataNotSet: [1], mode: "normal"
						});
						
						if(window.innerWidth >= 1366){
							$("#tableButton1 button").css("width", "200px");
						} else {
							$("#comboTime").css("width", "800px");
						}
						
						
					if (self.sourceCompany() != null)
						self.checkSelectButton(0);

					if (_.isNil(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails)) {
						__viewContext.viewModel.viewmodelA.localStore.workPalletDetails = self.selectedButton();
						characteristics.save(self.KEY, __viewContext.viewModel.viewmodelA.localStore);
					}
					self.setErrButton();
					self.subscribeTaskCombo();
					if(__viewContext.viewModel.viewmodelA.localStore.workSelection != 1)
					self.taskChecked.valueHasMutated();
				});
			});
			return dfd.promise();
		}
		
		public subscribeTaskCombo(){
			let self = this;
			self.taskChecked.subscribe((value) => {
						
				if(_.isNil(value)) return;
				
				let taskFilter = _.filter(self.dataTaskInfo.lstTaskDto, (x : any) => {
					return value === x.code;
					});
				
				let taskInfo : any = {
					data :{
						page: taskFilter.length > 0 ? taskFilter[0].code : "",
						text: taskFilter.length > 0 ? taskFilter[0].taskDisplayInfoDto.taskAbName : "",
						tooltip: taskFilter.length > 0 ? taskFilter[0].taskDisplayInfoDto.taskName : "",
						color: taskFilter.length > 0 ? taskFilter[0].taskDisplayInfoDto.color : ""
					}
				}
				
				__viewContext.viewModel.viewmodelA.pasteTask(taskInfo, taskFilter.length > 0 ? taskFilter[0].code : "");
				__viewContext.viewModel.viewmodelA.setTaskMode("paste");
				__viewContext.viewModel.viewmodelA.localStore.work1Selection = value;
				characteristics.save(self.KEY, __viewContext.viewModel.viewmodelA.localStore);
			});
		}

		// ③<<ScreenQuery>>作業選択準備情報を取得する
		public getTaskInfo(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			let page = 1;
			if (!_.isNil(__viewContext.viewModel.viewmodelA.localStore)) {
				if (!_.isNil(__viewContext.viewModel.viewmodelA.localStore.pageNo)) {
					page = __viewContext.viewModel.viewmodelA.localStore.pageNo + 1;
				}

			}
			let param = {
				baseDate: __viewContext.viewModel.viewmodelA.targetDate(),
				targetUnit: __viewContext.viewModel.viewmodelA.dataScreen003A().unit,
				page: page,
				organizationID: __viewContext.viewModel.viewmodelA.dataScreen003A().id
			}
			service.getTaskInfo(param)
				.done((data: any) => {
					self.dataTaskInfo = data;
					if (self.dataTaskInfo != null && self.dataTaskInfo.workPaletteDisplayInforDto.lstTaskPaletteOrganizationDto.length  == 0){
						self.selectedPage(0);
					}
					_.forEach(self.dataTaskInfo.lstTaskDto, (x : any) => {
						let taskInfo : any = {
							data :{
								page: x.code,
								text: x.taskDisplayInfoDto.taskAbName,
								tooltip: x.taskDisplayInfoDto.taskName,
								color: x.taskDisplayInfoDto.color
							}
						}
						
						//if (__viewContext.viewModel.viewmodelA.selectedDisplayPeriod() == 2)
						//	__viewContext.viewModel.viewmodelA.addTypeOfTask(taskInfo.data.color, taskInfo);
					});
						
					dfd.resolve();
				}).fail(function(error) {
					alertError({ messageId: error.messageId });
					dfd.reject();
				}).always(function() {
				});
			return dfd.promise();
		}

		// ①<<Query>> 作業パレットを取得する
		public getTaskPallet(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			self.dataFromScrA = getShared("dataShareAbFromA");
			let param = {
				baseDate: self.dataFromScrA.targetDate,
				targetUnit: self.dataFromScrA.dataScreen003A.unit,
				page: self.selectedPage() + 1, // fake
				organizationID: self.dataFromScrA.dataScreen003A.id
			}

			service.getTaskPallet(param)
				.done((data: any) => {
					if (!_.isNil(data)){
						self.hasDataButton.add(data.page);	
						self.dataTaskPallet = data;					
					} else {
						self.dataTaskPallet = [];
					}
					dfd.resolve();
				}).fail(function(error) {
					alertError({ messageId: error.messageId });
					dfd.reject();
				}).always(function() {
				});
			return dfd.promise();
		}

		// 
		public getTaskPaletteDisplay(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			let param = {
				baseDate: self.dataFromScrA.targetDate,
				targetUnit: self.dataFromScrA.dataScreen003A.unit,
				page: self.selectedPage() + 1, // fake
				organizationID: self.dataFromScrA.dataScreen003A.id
			}

			service.getTaskPaletteDisplay(param)
				.done((data: any) => {
					if (!_.isNil(data) && !_.isNil(data.taskPalette))
						self.hasDataButton.add(data.taskPalette.page);

						self.dataTaskPalletDis = data;
					dfd.resolve();
				}).fail(function(error) {
					alertError({ messageId: error.messageId });
					dfd.reject();
				}).always(function() {
				});
			return dfd.promise();
		}


		// ①<<ScreenQuery>>社員の出勤系をチェックする

		setDataTaskInfo() {
			let datas: any = [];
			let self = this, data = self.dataTaskInfo, pallet = self.dataTaskPallet;
			for (let i = 0; i < data.lstTaskDto.length; i++) {
				let taskData = new TaskModel(
					data.lstTaskDto[i].code, data.lstTaskDto[i].taskDisplayInfoDto.taskName,
					data.lstTaskDto[i].taskDisplayInfoDto.taskNote);
				datas.add(taskData);
			}
			datas = datas.sort((x : any,y : any) => {
				return x.code - y.code;
			});
			if (datas.length > 0){
				self.taskList(datas);
				self.enableAb(true);
			} else {
				self.taskList([
					new TaskModel('なし', '', '')
				]);
				self.enableAb(false);
			}
			if (!_.isNil(__viewContext.viewModel.viewmodelA.localStore.work1Selection)) {
				self.taskChecked(__viewContext.viewModel.viewmodelA.localStore.work1Selection);
			}

			if (__viewContext.viewModel.viewmodelA.localStore.workSelection == 0 || _.isNil(__viewContext.viewModel.viewmodelA.localStore.workSelection)) {
				setTimeout(function() {
					$("#screen-Ab2").hide();
				}, 10);
				setTimeout(function() {
					$("#screen-Ab1").show();
				}, 10);
			} else if (!_.isNil(__viewContext.viewModel.viewmodelA.localStore.workSelection) && __viewContext.viewModel.viewmodelA.localStore.workSelection == 1) {
				setTimeout(function() {
					$("#screen-Ab1").hide();
				}, 10);
				setTimeout(function() {
					$("#screen-Ab2").show();
				}, 10);
			}

			self.setPalletButton(self.dataTaskPallet);
			self.textButtonArr([]);

			for (let i = 1; i <= 5; i++) {
				let dataPallet = data.workPaletteDisplayInforDto.lstTaskPaletteOrganizationDto;
				let checkPage = _.filter(dataPallet, (x : any) => {
					return x.page == i;
				});
				if (checkPage.length > 0) {
					self.textButtonArr.push({
						name: ko.observable(checkPage[0].taskPaletteDisplayInfoDto.taskPaletteName),
						id: checkPage[0].page, formatter: _.escape
					});
				} else {
					if (!_.includes(self.hasDataButton, i)) {
						self.textButtonArr.push({ name: ko.observable(getText("KSU003_84", [i])), id: i, formatter: _.escape });
					}
				}
			}
		}

		setPalletButton(data: any) {
			let self = this, pallet = data;
			let source: any[] = [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}], lstPallet: any = [];
			if (_.isEmpty(pallet)) {
				self.dataSourceCompany().splice(self.selectedPage(), 1, source);
			} else {
				let dataPallet = pallet.tasks;
				_.map(dataPallet, (x: any, y: any) => {
					lstPallet.push({ page: Number(y), name: x.taskName, abName: x.taskAbName, code: x.taskCode, status: x.taskStatus })
				})
				_.forEach(lstPallet, (tsk: any) => {
					let name = tsk.name, abName = tsk.abName;
					if (tsk.status == 1) {
						abName = name = getText("KSU003_70");
						self.errButton.add(tsk.page - 1);
					}
					else if (tsk.status == 2) {
						abName = name = getText("KSU003_82");
						self.errButton.add(tsk.page - 1);
					}

					source.splice(tsk.page - 1, 1, { text: abName, tooltip: name, page: tsk.page });
				});
				self.dataSourceCompany().splice(self.selectedPage(), 1, source);
			}
		}

		checkSelectButton(type: number) {
			let self = this;
			let selectButtonChoice = _.filter(self.sourceCompany(), x => {
				return !_.isNil(x.page) && (x.text != getText("KSU003_70") && x.text != getText("KSU003_83"));
			});
			
			if (type == 2){
				let value : any = null 
				for(let i =0; i < self.sourceCompany().length; i++) {
						let cpn = self.sourceCompany()[i];
						if (!_.isEmpty(cpn)){
							let column = 0;
							if (cpn.page == 2 || cpn.page == 7) column = 1;
							if (cpn.page == 3 || cpn.page == 8) column = 2;
							if (cpn.page == 4 || cpn.page == 9) column = 3;
							if (cpn.page == 5 || cpn.page == 10) column = 4;
							value = {
								column: column,
								data: {
									page: cpn.page,
									text: cpn.text,
									tooltip: cpn.tooltip
								},
								row: cpn.page > 5 ? 1 : 0
							}
						if(!_.isEqual(cpn.text,getText("KSU003_82")) && !_.isEqual(cpn.text,getText("KSU003_70")) && !_.isEqual(cpn.text,getText("KSU003_83")))
							break;
						}
					}
				if (value == null) {
					self.selectedButton(0);
				} else {
					$("#tableButton1").ntsButtonTable("setSelectedCell",
							value.data.page > 5 ? 1 : 0, value.column);
				}
				return;
			}

			if (_.isNil(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails) || type == 1) {
				if (_.isEmpty(self.sourceCompany())) return;
				if (_.isEmpty(selectButtonChoice)) {
					self.selectedButton(0);
					return;
				}
				if (!_.isEmpty(self.sourceCompany()[selectButtonChoice[0].page - 1])) {
					let value : any = null;
					let checkCell = _.filter(self.sourceCompany(), (x : any) => {
							return __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.page == x.page;
					});
					
					if(checkCell.length > 0 && !_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text,"storeNull") && 
					!_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text , getText("KSU003_82")) &&
					 !_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text ,getText("KSU003_70")) &&
					  !_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text ,getText("KSU003_83"))) {
						
						$("#tableButton1").ntsButtonTable("setSelectedCell",
						__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.page > 5 ? 1 : 0, __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.column);
					} else {
						for(let i =0; i < selectButtonChoice.length; i++) {
							if (!_.isEqual(selectButtonChoice[i].text,getText("KSU003_82")) && !_.isEqual(selectButtonChoice[i].text,getText("KSU003_70")) 
								&& !_.isEqual(selectButtonChoice[i].text,getText("KSU003_83"))) {
								value = self.getColumn(selectButtonChoice[i].page - 1);
								break;
							}
						}
						
						if (value == null) {
							self.selectedButton(0);
						} else {
							$("#tableButton1").ntsButtonTable("setSelectedCell",
							value.data.page > 5 ? 1 : 0, value.column);
						}
					}
					return;
				}
			} else {
				if (_.isEmpty(selectButtonChoice)) {
					self.selectedButton(0);
					return;
				}
				
				let checkCell = _.filter(self.sourceCompany(), (x : any) => {
						return __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.page == x.page;
				});
				
				let value : any = null 
				if(checkCell.length > 0 && checkCell[0].text != getText("KSU003_70") && checkCell[0].text != getText("KSU003_82") && checkCell[0].text != getText("KSU003_83") &&
				!_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text,"storeNull") && 
					!_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text , getText("KSU003_82")) &&
					 !_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text ,getText("KSU003_70")) &&
					  !_.isEqual(__viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text ,getText("KSU003_83"))) {
					value = {
						column: __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.column,
						data: {
							page: __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.page,
							text: __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.text,
							tooltip: __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.data.tooltip
						},
						row: __viewContext.viewModel.viewmodelA.localStore.workPalletDetails.row
					}
				} else {
					for(let i =0; i < self.sourceCompany().length; i++) {
						let cpn = self.sourceCompany()[i];
						if (!_.isEmpty(cpn)){
							let column = 0;
							if (cpn.page == 2 || cpn.page == 7) column = 1;
							if (cpn.page == 3 || cpn.page == 8) column = 2;
							if (cpn.page == 4 || cpn.page == 9) column = 3;
							if (cpn.page == 5 || cpn.page == 10) column = 4;
							value = {
								column: column,
								data: {
									page: cpn.page,
									text: cpn.text,
									tooltip: cpn.tooltip
								},
								row: cpn.page > 5 ? 1 : 0
							}
						if(!_.isEqual(cpn.text,getText("KSU003_82")) && !_.isEqual(cpn.text,getText("KSU003_70")) && !_.isEqual(cpn.text,getText("KSU003_83")))
							break;
						}
					}
				}
				
				if (value == null) {
					self.selectedButton(0);
				}
				if (type == 0) {
					self.selectedButton(value)
				}
				return;
			}
		}

		getColumn(i: number) {
			let self = this;
			let column = 0;
			if (self.sourceCompany()[i].page > 5) {
				column = self.sourceCompany()[i].page - 6;
			} else {
				column = self.sourceCompany()[i].page - 1;
			}
			let value = {
				column: column,
				data: {
					page: self.sourceCompany()[i].page,
					text: self.sourceCompany()[i].text,
					tooltip: self.sourceCompany()[i].tooltip
				},
				row: self.sourceCompany()[i].page > 5 ? 1 : 0
			}
			return value;
		}

		clickLinkPage(element: any, indexLinkBtn?: any): void {
			let self = this;
			self.selectedPage(indexLinkBtn());
			self.setDataPalletToButton(indexLinkBtn(), 2);
			if(window.innerWidth >= 1366)
			$("#tableButton1 button").css("min-width", "200px");
			__viewContext.viewModel.viewmodelA.localStore.pageNo = indexLinkBtn();
			characteristics.save(self.KEY, __viewContext.viewModel.viewmodelA.localStore);
		}

		setDataPalletToButton(index: any, type: any) {
			let self = this, soure: any[] = [{}, {}, {}, {}, {}, {}, {}, {}, {}, {}];
			self.errButton = [];
			if (type == 0) {
				self.getTaskPallet().done(() => {
					self.setPalletButton(self.dataTaskPallet);
					if (_.includes(self.hasDataButton, index + 1)) {
						self.sourceCompany(self.dataSourceCompany()[self.selectedPage()]);
					} else {
						self.sourceCompany(soure);
					}
					self.checkSelectButton(1);
					$("#tableButton1 button").css("border", "1px solid LightGrey");
					self.setErrButton();
					
					if(window.innerWidth >= 1366)
					$("#tableButton1 button").css("min-width", "200px");
				});

			} else {
				self.getTaskPaletteDisplay().done(() => {
					self.setPalletButton(self.dataTaskPalletDis.taskPalette);
					if (_.includes(self.hasDataButton, index + 1)) {
						self.sourceCompany(self.dataSourceCompany()[self.selectedPage()]);
					} else {
						self.sourceCompany(soure);
					}
					
					if (type == 1)
						self.checkSelectButton(1);
					else
						self.checkSelectButton(2);
					
					self.setErrButton();
					self.textButtonArr([]);
					for (let i = 1; i <= 5; i++) {
						let dataPallet = self.dataTaskPalletDis.lstTaskPaletteOrganizationDto;
						let checkPage = _.filter(dataPallet, (x : any) => {
							return x.page == i;
						});
						if (checkPage.length > 0) {
							self.textButtonArr.push({
								name: ko.observable(checkPage[0].taskPaletteDisplayInfoDto.taskPaletteName),
								id: checkPage[0].page, formatter: _.escape
							});
						} else {
							self.textButtonArr.push({ name: ko.observable(getText("KSU003_84", [i])), id: i, formatter: _.escape });
						}
					}
					
					if(window.innerWidth >= 1366)
					$("#tableButton1 button").css("min-width", "200px");
				});
			}
		}

		setErrButton() {
			let self = this;
			if (self.errButton.length > 0) {
				_.each(self.errButton, idx => {
					$($("#tableButton1 button")[idx]).css("border", "1px solid red");
					$($("#tableButton1 button")[idx]).attr("disabled", "true");
					$($("#tableButton1 button")[idx]).css("background-color", "#D9D9D9");
				});
			}
		}

		setDataDefautStore() {
			let self = this;
			if (_.isNil(__viewContext.viewModel.viewmodelA.localStore.pageNo)) {
				self.selectedPage(0);
				__viewContext.viewModel.viewmodelA.localStore.pageNo = self.selectedPage();
			} else {
				self.selectedPage(__viewContext.viewModel.viewmodelA.localStore.pageNo);
			}

			if (_.isNil(__viewContext.viewModel.viewmodelA.localStore.work1Selection))
				__viewContext.viewModel.viewmodelA.localStore.work1Selection = self.taskChecked();

			if (_.isNil(__viewContext.viewModel.viewmodelA.localStore.workSelection))
				__viewContext.viewModel.viewmodelA.localStore.workSelection = 0;

			if (_.isNil(__viewContext.viewModel.viewmodelA.localStore))
				characteristics.save(self.KEY, __viewContext.viewModel.viewmodelA.localStore);
		}

		closeAb1() {
			let self = this;
			__viewContext.viewModel.viewmodelA.delPasteTask();
			$("#screen-Ab1").hide();
			$("#screen-Ab2").show();
			__viewContext.viewModel.viewmodelA.localStore.workSelection = 1;
			characteristics.save(self.KEY, __viewContext.viewModel.viewmodelA.localStore);
			self.selectedButton.valueHasMutated();
			__viewContext.viewModel.viewmodelA.setTaskMode("pasteFlex");
		}

		closeAb2() {
			let self = this;
			__viewContext.viewModel.viewmodelA.delPasteTask();
			$("#screen-Ab2").hide();
			$("#screen-Ab1").show();
			__viewContext.viewModel.viewmodelA.localStore.workSelection = 0;
			characteristics.save(self.KEY, __viewContext.viewModel.viewmodelA.localStore);
			self.taskChecked.valueHasMutated();
			__viewContext.viewModel.viewmodelA.setTaskMode("paste");
		}

		openB() {
			let self = this;
			let param = {
				page: self.selectedPage() + 1,
				targetUnit: self.dataFromScrA.dataScreen003A.unit,
				targetId: self.dataFromScrA.dataScreen003A.id,
				organizationName: self.dataFromScrA.organizationName,
				referenceDate: self.dataFromScrA.targetDate
			}
			setShared("dataShareKsu003b", param);
			nts.uk.ui.windows.sub.modal('/view/ksu/003/b/index.xhtml').onClosed(() => {
				let data = getShared("dataShareFromKsu003b");
				if (data == null) return;
				self.selectedPage(data - 1);
				self.setDataPalletToButton(data - 1, 1);
				if(window.innerWidth >= 1366)
					$("#tableButton1 button").css("min-width", "200px");
			});
		}

		openC() {
			let self = this;
			block.grayout();
			self.checkEmpAttendance().done((data: any) => {
				setShared("dataShareKsu003c", self.lstEmpToC);
				nts.uk.ui.windows.sub.modal('/view/ksu/003/c/index.xhtml').onClosed(() => {
					let data = getShared('dataShareFromKsu003c');
					if (data == "Cancel") return;
					__viewContext.viewModel.viewmodelA.destroyAndCreateGrid(__viewContext.viewModel.viewmodelA.lstEmpId, 0)
					block.clear();
				});
			});
		}

		// ①<<ScreenQuery>> 社員の出勤系をチェックする

		public checkEmpAttendance(): JQueryPromise<any> {
			let self = this, dfd = $.Deferred<any>();
			let param = {
				lstEmpId: _.map(__viewContext.viewModel.viewmodelA.lstEmpId, (x: ksu003.a.model.IEmpidName) => { return x.empId }),
				startDate: __viewContext.viewModel.viewmodelA.targetDate(),
				endDate: __viewContext.viewModel.viewmodelA.targetDate(),
				displayMode: __viewContext.viewModel.viewmodelA.selectedDisplayPeriod()
			}

			service.checkEmpAttendance(param).done((data: any) => {
				let lstParam = _.filter(__viewContext.viewModel.viewmodelA.lstEmpId, (x: ksu003.a.model.IEmpidName) => {
					return _.includes(data, x.empId)
				});
				self.lstEmpToC = {
					date: __viewContext.viewModel.viewmodelA.targetDate(),
					employeeCodes: [],
					employeeIds: [],
					employeeNames: []
				}
				_.each(lstParam, idx => {
					self.lstEmpToC.employeeCodes.push(idx.code);
					self.lstEmpToC.employeeIds.push(idx.empId);
					self.lstEmpToC.employeeNames.push(idx.name);
				});

				console.log();
				dfd.resolve();
			}).fail(function(error: any) {
				alertError({ messageId: error.messageId });
			}).always(function() {
			});

			return dfd.promise();
		}
	}
	export class TaskModel {
		code: string;
		name: string;
		des: string;
		constructor(code: string, name: string, des: string) {
			this.code = code;
			this.name = name;
			this.des = des;
		}
	}
}
