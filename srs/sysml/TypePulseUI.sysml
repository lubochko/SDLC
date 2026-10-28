package TypePulseUI {
    doc /*
     * Прототип GUI TypePulse на SysML v2.
     * Экраны смоделированы как part с вложенными элементами управления.
     * Визуальный экспорт (PNG) — в каталоге srs/mockups/.
     */

    private import ScalarValues::*;
    private import Views::*;

    part def Screen { attribute title : String; }
    part def Button { attribute label : String; }
    part def TextField { attribute placeholder : String; }
    part def Label { attribute text : String; }
    part def TimerWidget { attribute remainingSeconds : Integer; }
    part def MetricCard {
        attribute name : String;
        attribute value : String;
    }
    part def Table { attribute columns : String; }
    part def NavBar {
        attribute items : String = "Главная | Тренировка | История | Рейтинг";
    }

    // Экран 1: Главная
    part HomeScreen : Screen {
        attribute :>> title = "TypePulse — Главная";
        part nav : NavBar;
        part brand : Label { attribute :>> text = "TypePulse"; }
        part tagline : Label { attribute :>> text = "Интерактивный тренажёр скорости печати"; }
        part btnStart : Button { attribute :>> label = "Начать тренировку"; }
        part btnLogin : Button { attribute :>> label = "Войти"; }
        part btnRegister : Button { attribute :>> label = "Регистрация"; }
    }

    // Экран 2: Настройка тренировки
    part SetupScreen : Screen {
        attribute :>> title = "Настройка тренировки";
        part nav : NavBar;
        part langSelect : Label { attribute :>> text = "Язык: RU | EN"; }
        part durationSelect : Label { attribute :>> text = "Длительность: 30 | 60 | 120 с"; }
        part difficultySelect : Label { attribute :>> text = "Сложность: лёгкий | средний | сложный"; }
        part btnGo : Button { attribute :>> label = "Старт"; }
    }

    // Экран 3: Сеанс тренировки
    part TrainingScreen : Screen {
        attribute :>> title = "Тренировка";
        part nav : NavBar;
        part timer : TimerWidget { attribute :>> remainingSeconds = 60; }
        part promptText : Label { attribute :>> text = "Эталонный текст (верные символы — зелёные, ошибки — красные)"; }
        part inputArea : TextField { attribute :>> placeholder = "Печатайте здесь"; }
        part liveWpm : MetricCard {
            attribute :>> name = "WPM";
            attribute :>> value = "0";
        }
        part liveAccuracy : MetricCard {
            attribute :>> name = "Точность";
            attribute :>> value = "100%";
        }
        part liveErrors : MetricCard {
            attribute :>> name = "Ошибки";
            attribute :>> value = "0";
        }
    }

    // Экран 4: Результат
    part ResultScreen : Screen {
        attribute :>> title = "Результат";
        part nav : NavBar;
        part wpm : MetricCard {
            attribute :>> name = "WPM";
            attribute :>> value = "62";
        }
        part accuracy : MetricCard {
            attribute :>> name = "Точность";
            attribute :>> value = "96%";
        }
        part errors : MetricCard {
            attribute :>> name = "Ошибки";
            attribute :>> value = "4";
        }
        part btnAgain : Button { attribute :>> label = "Ещё раз"; }
        part btnHistory : Button { attribute :>> label = "К истории"; }
    }

    // Экран 5: История и рейтинг
    part HistoryScreen : Screen {
        attribute :>> title = "История и рейтинг";
        part nav : NavBar;
        part historyTable : Table { attribute :>> columns = "Дата | WPM | Точность | Язык"; }
        part leaderboard : Table { attribute :>> columns = "# | Ник | Лучший WPM"; }
    }

    // Навигация между экранами (поток пользователя)
    action def UserFlow {
        action openHome;
        then action openSetup;
        then action train;
        then action showResult;
        then action openHistory;
    }

    concern 'ui layout clarity' {
        doc /* Пользователь должен понимать, куда смотреть и что нажимать. */
    }

    viewpoint 'ui mockup viewpoint' {
        frame 'ui layout clarity';
    }

    view 'Home mockup' {
        satisfy 'ui mockup viewpoint';
        expose HomeScreen::**;
        render asInterconnectionDiagram;
    }
    view 'Setup mockup' {
        satisfy 'ui mockup viewpoint';
        expose SetupScreen::**;
        render asInterconnectionDiagram;
    }
    view 'Training mockup' {
        satisfy 'ui mockup viewpoint';
        expose TrainingScreen::**;
        render asInterconnectionDiagram;
    }
    view 'Result mockup' {
        satisfy 'ui mockup viewpoint';
        expose ResultScreen::**;
        render asInterconnectionDiagram;
    }
    view 'History mockup' {
        satisfy 'ui mockup viewpoint';
        expose HistoryScreen::**;
        render asInterconnectionDiagram;
    }
}
