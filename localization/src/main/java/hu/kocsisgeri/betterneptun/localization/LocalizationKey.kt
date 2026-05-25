package hu.kocsisgeri.betterneptun.localization

import hu.kocsisgeri.betterneptun.domain.service.Localization

enum class LocalizationKey(
    override val key: String,
    override val defaultValue: String,
) : Localization {

    HOME_LABEL_UNDERDEVELOPMENT(
        key = "home_label_underdevelopment",
        defaultValue = "Fejlesztés alatt"
    ),
    HOME_ONGOING_COURSE(
        key = "home_ongoing_course",
        defaultValue = "Éppen tart"
    ),
    HOME_ONGOING_COURSE_MINUTES(
        key = "home_ongoing_course_minutes",
        defaultValue = "%s perc"
    ),
    HOME_MENU_COURSES(
        key = "home_menu_courses",
        defaultValue = "Kurzusok"
    ),
    HOME_MENU_EXAMS(
        key = "home_menu_exams",
        defaultValue = "Vizsgák"
    ),
    HOME_MENU_MESSAGES(
        key = "home_menu_messages",
        defaultValue = "Üzenetek"
    ),
    HOME_MENU_PERIODS(
        key = "home_menu_periods",
        defaultValue = "Időszakok"
    ),
    HOME_MENU_SEMESTERS(
        key = "home_menu_semesters",
        defaultValue = "Félévek"
    ),
    HOME_MENU_TIMETABLE(
        key = "home_menu_timetable",
        defaultValue = "Órarend"
    ),
    HOME_NEXT_COURSE(
        key = "home_next_course",
        defaultValue = "Következő óra"
    ),
    HOME_NEXT_COURSE_DAYS(
        key = "home_next_course_days",
        defaultValue = "%s nap múlva"
    ),
    HOME_NEXT_COURSE_HOURS(
        key = "home_next_course_hours",
        defaultValue = "%s óra múlva"
    ),
    HOME_NEXT_COURSE_MINUTES(
        key = "home_next_course_minutes",
        defaultValue = "%s perc múlva"
    ),
    HOME_PERMISSION_PERMIT(
        key = "home_permission_permit",
        defaultValue = "Engedélyezés"
    ),

    LANGUAGE_HU(
        key = "language_hu",
        defaultValue = "Magyar"
    ),
    LANGUAGE_EN(
        key = "language_en",
        defaultValue = "Angol"
    ),

    LOGIN_CHECKBOX_STAY_LOGGEDIN(
        key = "login_checkbox_stay_loggedin",
        defaultValue = "Maradjak bejelentkezve"
    ),
    LOGIN_INPUT_NEPTUN_CODE(
        key = "login_input_neptun_code",
        defaultValue = "Neptun kód"
    ),
    LOGIN_INPUT_PASSWORD(
        key = "login_input_password",
        defaultValue = "Jelszó"
    ),
    LOGIN_SUBMIT(
        key = "login_submit",
        defaultValue = "Belépés"
    ),

    SETTINGS_LOGOUT_BUTTON_DESCRIPTION(
        key = "settings_logout_button_description",
        defaultValue = "Minden elmentett adat törlése és kijelentkezés"
    ),
    SETTINGS_LOGOUT_BUTTON_TITLE(
        key = "settings_logout_button_title",
        defaultValue = "Kilépés"
    ),
    SETTINGS_SECTION_INFORMATION(
        key = "settings_section_information",
        defaultValue = "Információk"
    ),
    SETTINGS_SECTION_INFORMATION_VERSION(
        key = "settings_section_information_version",
        defaultValue = "Verzió"
    ),
    SETTINGS_SECTION_LANGUAGE(
        key = "settings_section_language",
        defaultValue = "Nyelv"
    ),
    SETTINGS_SECTION_THEME(
        key = "settings_section_theme",
        defaultValue = "Megjelenés"
    ),
    SETTINGS_SECTION_THEME_DARK(
        key = "settings_section_theme_dark",
        defaultValue = "Sötét mód"
    ),
    SETTINGS_SECTION_THEME_LIGHT(
        key = "settings_section_theme_light",
        defaultValue = "Világos mód"
    ),
    SETTINGS_SECTION_THEME_SYSTEM(
        key = "settings_section_theme_system",
        defaultValue = "Automatikus"
    ),
    SETTINGS_SECTION_TIMETABLE(
        key = "settings_section_timetable",
        defaultValue = "Naptár"
    ),
    SETTINGS_SECTION_TIMETABLE_MINUTES(
        key = "settings_section_timetable_minutes",
        defaultValue = "%s perccel előtte"
    ),
    SETTINGS_SECTION_TIMETABLE_NONE(
        key = "settings_section_timetable_none",
        defaultValue = "Nincs értesítés"
    ),
    SETTINGS_TITLE(
        key = "settings_title",
        defaultValue = "Beállítások"
    );
}
