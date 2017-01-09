__viewContext.ready(function () {
    
    class ScreenModel {
        items: KnockoutObservableArray<ItemModel>;
        columns: KnockoutObservableArray<any>;
        columns2: KnockoutObservableArray<any>;
        currentCode: KnockoutObservable<any>;
        currentCodeList: KnockoutObservableArray<any>;       
        constructor() {
            
            this.items = ko.observableArray([
                new ItemModel('001', 'Hanoi Vietnam', "description 1", "other1"),
                new ItemModel('150', 'Bankok Thailand', "description 2", "other2"),
                new ItemModel('ABC', 'Tokyo Japan', "description 3", "other3"),
                new ItemModel('002', 'London United Kingdoms', "description 1", "other1"),
                new ItemModel('152', 'Washington United States', "description 2", "other2"),
                new ItemModel('AB2', 'Jarkata Indonesia', "description 3", "other3"),
                new ItemModel('003', 'Singapore Singapore', "description 1", "other1"),
                new ItemModel('153', 'Beijing China', "description 2", "other2"),
                new ItemModel('AB3', 'Berlin Germany', "description 3", "other3")       
            ]);          
            this.columns = ko.observableArray([
                { headerText: 'コード', prop: 'code', width: 100 },
                { headerText: '名称', prop: 'name', width: 230 },
                { headerText: '説明', prop: 'description', width: 150 },
                { headerText: '説明1', prop: 'other1', width: 150 },
                { headerText: '説明2', prop: 'other2', width: 150 }
            ]);
            this.columns2 = ko.observableArray([
                { headerText: 'コード', prop: 'code', width: 100 },
                { headerText: '名称', prop: 'name', width: 150 },
                { headerText: '説明', prop: 'description', width: 150 },
                { headerText: '説明1', prop: 'other1', width: 150 },
                { headerText: '説明2', prop: 'other2', width: 150 }
            ]);
            
            this.currentCode = ko.observable();
            this.currentCodeList = ko.observableArray([]);
        }
        
        selectSomeItems() {
            this.currentCode('150');
            this.currentCodeList.removeAll();
            this.currentCodeList.push('001');
            this.currentCodeList.push('ABC');
        }
        
        deselectAll() {
            this.currentCode(null);
            this.currentCodeList.removeAll();
        }
        
        addItem() {
            this.items.push(new ItemModel('999', '基本給', "description 1", "other1"));
        }
        
        removeItem() {
            this.items.shift();
        }

    }
    
    class ItemModel {
        code: string;
        name: string;
        description: string;
        other1: string;
        other2: string;
        constructor(code: string, name: string, description: string, other1?: string, other2?: string) {
            this.code = code;
            this.name = name;
            this.description = description;
            this.other1 = other1;
            this.other2 = other2 || other1;         
        }
    }
    
    this.bind(new ScreenModel());
});