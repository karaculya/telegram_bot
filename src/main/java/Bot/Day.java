package Bot;

import java.util.Locale;

public enum Day {
    // OOD-НЕЧЕТ, EVEN-ЧЕТ
    ODD_MONDAY(0, "НеЧёт_пн"),
    ODD_TUESDAY(1, "НеЧёт_вт"),
    ODD_WEDNESDAY (2, "НеЧёт_ср"),
    ODD_THURSDAY (3, "НеЧёт_чт"),
    ODD_FRIDAY(4, "НеЧёт_пт"),
    OOD_SATURDAY(5,"НеЧёт_сб"),
    EVEN_MONDAY(6, "Чётная_пн"),
    EVEN_TUESDAY(7, "Чётная_вт"),
    EVEN_WEDNESDAY(8, "Чётная_ср"),
    EVEN_THURSDAY(9, "Чётная_чт"),
    EVEN_FRIDAY(10, "Чётная_пт"),
    EVEN_SATURDAY(11, "Четная_сб");
    
    private final int number;
    private final String name;

    Day(int number, String name) {
        this.number = number;
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
    
    public static Day valueOfName (String s){
        for (Day day : Day.values()) {
            if(s.toLowerCase(Locale.ROOT)
                    .equals(day.name.toLowerCase(Locale.ROOT)))
                return day;
        }
        return null;
    }
}






