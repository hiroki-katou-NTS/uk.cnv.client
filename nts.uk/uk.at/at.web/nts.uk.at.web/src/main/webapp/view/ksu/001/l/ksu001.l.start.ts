module nts.uk.at.view.ksu001.l {
    let __viewContext: any = window["__viewContext"] || {};
    __viewContext.ready(function() {
        let screenModel = new viewmodel.ScreenModel();
        screenModel.startPage().done(function() {
            __viewContext.bind(screenModel);
            $('#swap-list-search-area-input').focus();
        });
        
    });
}