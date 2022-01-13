module nts.uk.at.view.kdw006.i {  
    import kdw = nts.uk.at.view.kdw006.i;
    let __viewContext: any = window["__viewContext"] || {};

    __viewContext.ready(function() {
        let screenModel = new kdw.viewmodel.ScreenModelI();
        screenModel.start().done(function() {
            __viewContext.bind(screenModel);
            //$('#I2_2').focus();
            setTimeout(function() { $('#I2_2').focus() }, 1000);
        });
    });
}