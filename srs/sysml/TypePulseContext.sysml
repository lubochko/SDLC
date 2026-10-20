package TypePulseContext {
    doc /* Контекстная модель веб-приложения TypePulse (лаб. №3 ЖЦРПО). */

    private import Views::*;

    part def Person;
    part def WebBrowser;
    part def WebUI;
    part def Backend;
    part def Database;

    part def TypePulseSystem {
        part webUI : WebUI[1];
        part backend : Backend[1];
        part database : Database[1];
    }

    part user : Person[1..*] {
        doc /* Конечный пользователь (студент / офисный работник). */
    }
    part browser : WebBrowser[1];
    part typePulse : TypePulseSystem[1];

    connection userToBrowser connect user to browser;
    connection browserToUI connect browser to typePulse.webUI;
    connection uiToBackend connect typePulse.webUI to typePulse.backend;
    connection backendToDb connect typePulse.backend to typePulse.database;

    concern 'product boundary' {
        doc /* Показать границы системы и внешних акторов. */
    }

    viewpoint 'stakeholder context' {
        frame 'product boundary';
    }

    view 'TypePulse context view' {
        satisfy 'stakeholder context';
        expose TypePulseContext::**;
        render asInterconnectionDiagram;
    }
}
