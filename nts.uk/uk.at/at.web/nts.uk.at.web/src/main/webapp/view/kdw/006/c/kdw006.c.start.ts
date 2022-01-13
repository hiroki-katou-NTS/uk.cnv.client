
module nts.uk.at.view.kdw006.c {  
    import kdw = nts.uk.at.view.kdw006.c;
    let __viewContext: any = window["__viewContext"] || {};
    __viewContext.ready(function() {
        let screenModel = new kdw.viewmodel.ScreenModelC();
        screenModel.start().done(function() {
            __viewContext.bind(screenModel);
            $('#C2_4').focus();
        });
    });
}