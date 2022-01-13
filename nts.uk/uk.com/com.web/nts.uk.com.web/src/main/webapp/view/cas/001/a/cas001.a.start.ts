module nts.uk.com.view.cas001.a {
    import setShared = nts.uk.ui.windows.setShared;

    __viewContext.ready(function() {
        __viewContext.transferred.ifPresent((data: any) => {
            setShared("CAS001A_PARAMS", data);
        });

        __viewContext['screenModel'] = new viewmodel.ScreenModel();
        __viewContext.bind(__viewContext['screenModel']);

        $("#A2_008").on("iggridrendered", (evt, ui) => {
            let helpButton = "<button id=\"A2_012\" tabindex= \"-1\" data-bind=\"ntsHelpButton: {image: \'A2_012.png\', position: \'right top\', enable: true }\">?</button>";
            nts.uk.ui.ig.grid.header.getCell('A2_008', 'setting').append($(helpButton));
            ko.applyBindings(__viewContext['screenModel'], nts.uk.ui.ig.grid.header.getCell('A2_008', 'setting')[0]);
        });
         $(document).ready(function() {
             setTimeout(() =>{
                 $("#item_role_table_body_isChecked > span > div > label > span").attr("tabindex", 21);
                 $("button.manual-button").attr("tabindex", -1); 
             }, 1000);
           });
    });
}

