module nts.uk.com.view.cas005.a {  
    __viewContext.ready(function() {
        let screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            $("#roleNameFocus").focus(); 
        });        
    });
}