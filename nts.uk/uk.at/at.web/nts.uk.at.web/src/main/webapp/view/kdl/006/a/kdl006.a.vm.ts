module nts.uk.at.view.kdl006.a {

    export module viewModel {

        import CurrentClosure = service.model.CurrentClosure;
        import WorkplaceDto = service.model.WorkplaceDto;
        import WorkFixedDto = service.model.WorkFixedDto;


        export class ScreenModel {

            items: KnockoutObservableArray<any>;
            columns: KnockoutObservableArray<any>;   //nts.uk.ui.NtsGridListColumn       
            control: KnockoutObservableArray<any>;
            currentCode: KnockoutObservable<any>;
            columnText: KnockoutObservable<string>;
            listClosure: CurrentClosure[];
            listWorkplace: WorkplaceDto[];
            
            constructor() {
                let _self = this;
                _self.currentCode = ko.observable(null);
                _self.items = ko.observableArray([]);             
                _self.columns = ko.observableArray([]);

                _self.control = ko.observableArray([
                    {name: 'CheckBox1', options: { value: 1, text: 'column1' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                    {name: 'CheckBox2', options: { value: 1, text: 'column2' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                    {name: 'CheckBox3', options: { value: 1, text: 'column3' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                    {name: 'CheckBox4', options: { value: 1, text: 'column4' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true },
                    {name: 'CheckBox5', options: { value: 1, text: 'column5' }, optionsValue: 'value', optionsText: 'text', controlType: 'CheckBox', enable: true }
                ]);
                //options: { value: 1, text: '' }, 
                
                _self.listClosure = [];
                _self.listWorkplace = [];
            }

            loadGrid() {
                let _self = this;
                _self.items(_self.listWorkplace);
                console.log(_self.items());
                $("#grid-list").ntsGrid({
                    width: '970px',
                    height: '350px',
                    dataSource: _self.items(),
                    primaryKey: 'workplaceId',
                    virtualization: true,
                    virtualizationMode: 'continuous',
                    columns: _self.columns(),
                    features: [],
                    ntsControls: _self.control()
                }); 
            }
            
            /**
             * Start page
             */
            public startPage(): JQueryPromise<any> {
                let _self = this;
                let dfd = $.Deferred<any>();
                
                // Load closure
                $.when(_self.loadClosure(), _self.loadWorkplace())
                    .done(() => {
                        
                        // Load closure    
                        _self.loadGridList(_self.listClosure);
                        
                        // Load workplace
                        _.map(_self.listWorkplace, (dto: WorkplaceDto) => {
                            return dto.viewText = dto.workplaceCode + " " + dto.workplaceName;
                        });                        
                        
                        // Load workfixed
                        _self.loadWorkFixed()
                            .done(() => {
                                _self.loadGrid();    
                                dfd.resolve(_self); 
                            })
                            .fail((res: any) => {
                                dfd.fail(res);
                            });
                    })
                    .fail((res: any) => {
                        dfd.fail(res);
                    });
                
                return dfd.promise();
            }

            /**
             * Prevent Promise Hell - loadClosure 
             */
            private loadClosure(): JQueryPromise<any> {
                let _self = this;
                let dfd = $.Deferred<any>();

                service.findCurrentClosure()
                    .done((data: any[]) => {
                        _self.listClosure = data;
                        dfd.resolve();
                    })
                    .fail((res: any) => dfd.fail(res));

                return dfd.promise();
            }

            /**
             * Prevent Promise Hell - loadWorkplace
             */
            private loadWorkplace(): JQueryPromise<any> {
                let _self = this;
                let dfd = $.Deferred<any>();

                service.findWorkplaceInfo()
                    .done((data: any[]) => {
                        _self.listWorkplace = data;
                        dfd.resolve();
                    })
                    .fail((res: any) => dfd.fail(res));

                return dfd.promise();
            }

            /**
             * Prevent Promise Hell - loadWorkFixed
             */
            private loadWorkFixed(): JQueryPromise<any> {
                let _self = this;
                let dfd = $.Deferred<any>();

                let listClosure = _self.listClosure;
                let listWorkplace = _self.listWorkplace;

                //TODO refactor
                for (let workplace of listWorkplace) {
                    let columnIndex = 1;
                    for (let closure of listClosure) {
                        ((columnIndex: any) => {
                            service.findWorkFixedByWkpIdAndClosureId(workplace.workplaceId, closure.closureId)
                                .done((data: WorkFixedDto) => {                                   
                                    switch (columnIndex) {
                                        case 1: {  
                                            workplace.columnText1 = data.confirmPid;                                       
                                        }
                                        case 2: {
                                            workplace.columnText2 = data.confirmPid;    
                                        }
                                        case 3: {
                                            workplace.columnText3 = data.confirmPid;    
                                        }
                                        case 4: {
                                            workplace.columnText4 = data.confirmPid;    
                                        }
                                        case 5: {
                                            workplace.columnText5 = data.confirmPid;    
                                        }
                                        default: {
                                            // Do nothing
                                        }
                                    }                                                                
                                    
                                    // Update grid data
                                    _self.items(listWorkplace);
                                    // console.log(listWorkplace);
                                    dfd.resolve();
                                })                        
                        })(columnIndex);                        
                        
                        columnIndex++;
                    }
                }

                
                return dfd.promise();
            }

            /**
             * Load grid list
             */
            private loadGridList(newList: any[]): void {
                let _self = this;
                let columns: any[] = [];

                //...
                columns.push({ headerText: nts.uk.resource.getText('KDL006_4'), key: 'workplaceId', width: 50, hidden: true });
                columns.push({ headerText: nts.uk.resource.getText('KDL006_4'), key: 'viewText', width: 250, height: 50 });
                
                let columnIndex = 1;
                for (let item of newList) {
                    item.viewText = _self.getHeader(item);
                    let column = {
                        headerText: item.viewText,
                        key: "columnCheck" + columnIndex,
                        dataType: 'boolean',
                        width: 150,
                        showHeaderCheckbox: true,
                        ntsControl: 'CheckBox' + columnIndex
                    }
                    columns.push(column);
                    columnIndex++;
                }
                
                if (columns.length < 7) {
                    let columnLength = 7 - columns.length;
                    for (let i = 0; i < columnLength; i++) {
                        let column = {
                            headerText: "",
                            key: "",
                            width: 150
                        }
                        columns.push(column);
                    }
                }
               
                _self.columns(columns);
            }

            /**
             *  Get Header
             */
            private getHeader(item: CurrentClosure): string {
                let self = this;
                let startMonthRage: string = item.startDate.slice(5, 10);
                let endMonthRage: string = item.endDate.slice(5, 10);
                return item.closureName + "<br>" + startMonthRage + ' ~ ' + endMonthRage;
            }
            
        }
    }
}