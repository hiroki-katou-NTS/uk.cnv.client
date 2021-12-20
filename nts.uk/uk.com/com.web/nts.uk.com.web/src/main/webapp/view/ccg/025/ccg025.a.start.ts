module nts.uk.com.view.ccg025.a {  
    import ComponentModel = component.viewmodel.ComponentModel;
    __viewContext.ready(function() {
        const vm = new ViewModel();
        __viewContext.bind(vm);
        vm.reloadData();
    });

    class ViewModel {
        roleType: KnockoutObservable<number>;
        roleAtr: KnockoutObservable<number>;
        multiple: KnockoutObservable<boolean>;
        onDialog: KnockoutObservable<boolean>;
        isAlreadySetting: KnockoutObservable<boolean>;
        showEmptyItem: KnockoutObservable<boolean>;
        selectType: KnockoutObservable<number>;
        rows: KnockoutObservable<number>;
        componentViewmodel: KnockoutObservable<ComponentModel>;
        selectedId: KnockoutObservable<string>;
        selectedSetId: KnockoutObservable<string>;

        selectTypes: KnockoutObservableArray<any>;
        listRole: KnockoutObservableArray<any>;
        displaySelected: KnockoutComputed<string>;

        constructor() {
            const self = this;
            self.roleType = ko.observable(0);
            self.roleAtr = ko.observable(null);
            self.multiple = ko.observable(false);
            self.onDialog = ko.observable(false);
            self.isAlreadySetting = ko.observable(false);
            self.showEmptyItem = ko.observable(false);
            self.selectType = ko.observable(3);
            self.rows = ko.observable(15);
            self.selectedId = ko.observable(null);
            self.selectedSetId = ko.observable(null);
            self.listRole = ko.observableArray([]);
            self.componentViewmodel = ko.observable(new ComponentModel({
                roleType: self.roleType(),
                roleAtr: self.roleAtr(),
                multiple: self.multiple(),
                isAlreadySetting: self.isAlreadySetting(),
                rows: self.rows(),
                tabindex: 0,
                showEmptyItem: self.showEmptyItem(),
                selectType: self.selectType(),
                hasFocus: true,
                selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId()
            }));

            self.selectTypes = ko.observableArray([
                {value: 1, name: "Selected List"},
                {value: 2, name: "Select All", enable: self.multiple},
                {value: 3, name: "Select First"},
                {value: 4, name: "Select None"}
            ]);

            self.roleType.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: value,
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: self.selectType(),
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });
            self.roleAtr.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: value,
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: self.selectType(),
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });
            self.multiple.subscribe(value => {
                if (self.selectType() == 2 && !value) {
                    self.selectType(3);
                } else {
                    self.componentViewmodel(new ComponentModel({
                        roleType: self.roleType(),
                        roleAtr: self.roleAtr(),
                        multiple: value,
                        isAlreadySetting: self.isAlreadySetting(),
                        rows: self.rows(),
                        tabindex: 0,
                        showEmptyItem: self.showEmptyItem(),
                        selectType: self.selectType(),
                        onDialog: self.onDialog(),
                        hasFocus: true,
                        selectedId: value ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                        alreadySetList: [self.selectedSetId()]
                    }));
                    self.reloadData();
                }
            });
            self.isAlreadySetting.subscribe(value => {
                self.selectedSetId(null);
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: value,
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: self.selectType(),
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });
            self.rows.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: value,
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: self.selectType(),
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });
            self.showEmptyItem.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: value,
                    selectType: self.selectType(),
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });
            self.selectType.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: value,
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });
            self.onDialog.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: self.selectType(),
                    onDialog: value,
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });
            self.selectedId.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: self.selectType(),
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [value].filter(i => !!i) : value,
                    alreadySetList: [self.selectedSetId()]
                }));
                self.reloadData();
            });

            self.selectedSetId.subscribe(value => {
                self.componentViewmodel(new ComponentModel({
                    roleType: self.roleType(),
                    roleAtr: self.roleAtr(),
                    multiple: self.multiple(),
                    isAlreadySetting: self.isAlreadySetting(),
                    rows: self.rows(),
                    tabindex: 0,
                    showEmptyItem: self.showEmptyItem(),
                    selectType: self.selectType(),
                    onDialog: self.onDialog(),
                    hasFocus: true,
                    selectedId: self.multiple() ? [self.selectedId()].filter(i => !!i) : self.selectedId(),
                    alreadySetList: [value]
                }));
                self.reloadData();
            });

            self.displaySelected = ko.computed(() => {
                if (!_.isEmpty(self.componentViewmodel().currentRoleId())) {
                    const roles: Array<any> = _.filter(self.componentViewmodel().listRole(), r => self.componentViewmodel().currentRoleId().indexOf(r.roleId) >= 0);
                    return ko.toJSON(roles.map(r => ({roleId: r.roleId, roleCode: r.roleCode, name: r.name})));
                }
                return "";
            });
        }

        reloadData() {
            const self = this;
            self.componentViewmodel().startPage().done(() => {
                self.listRole(self.componentViewmodel().listRole());
            });
        }

        setConfigured() {
            const self = this;
            if (!_.isEmpty(self.componentViewmodel().currentRoleId())) {
                if (self.multiple()) {
                    self.componentViewmodel().listRole().forEach(role => {
                        if (self.componentViewmodel().currentRoleId().indexOf(role.roleId) >= 0) {
                            role.configured = 1;
                        }
                    });
                    self.componentViewmodel().listRole.valueHasMutated();
                } else {
                    self.componentViewmodel().listRole().forEach(role => {
                        if (self.componentViewmodel().currentRoleId() == role.roleId) {
                            role.configured = 1;
                        }
                    });
                    self.componentViewmodel().listRole.valueHasMutated();
                }
            }
        }

        unSetConfigured() {
            const self = this;
            if (!_.isEmpty(self.componentViewmodel().currentRoleId())) {
                if (self.multiple()) {
                    self.componentViewmodel().listRole().forEach(role => {
                        if (self.componentViewmodel().currentRoleId().indexOf(role.roleId) >= 0) {
                            role.configured = 0;
                        }
                    });
                    self.componentViewmodel().listRole.valueHasMutated();
                } else {
                    self.componentViewmodel().listRole().forEach(role => {
                        if (self.componentViewmodel().currentRoleId() == role.roleId) {
                            role.configured = 0;
                        }
                    });
                    self.componentViewmodel().listRole.valueHasMutated();
                }
            }
        }
    }
}