module nts.uk.com.view.ccg025.a {  
    
    __viewContext.ready(function() {
        let component = new nts.uk.com.view.ccg025.a.component.viewmodel.ComponentModel({ 
            roleType: 3,
            multiple: false,
            isAlreadySetting: false
        });
        let vm = {
            componentViewmodel: component    
        }
        vm.componentViewmodel.startPage().done(function() {
            __viewContext.bind(vm); 
        });        
    }); 
}