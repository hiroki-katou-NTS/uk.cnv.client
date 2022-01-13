module nts.uk.com.view.cps018 {  
    __viewContext.ready(function() {
        let screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
        }).then(() => {
            $('#A3_3').focus();
        });
    });
}