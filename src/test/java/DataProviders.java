import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by dolkons on 08.02.17.
 */
public class DataProviders {

    @DataProvider(name = "tariffsData")
    public static Object[][] tariffsData(Method m){
        if (m.getName().equals("TestDecrease")) {
            return new Object[][]{
                    {"Макс","1400", "Мбит/сек (макс.)"},
                    {"15.0","1350", "Мбит/сек (макс.)"},
                    {"12.0","1300", "Мбит/сек (макс.)"},
                    {"10.0","1250", "Мбит/сек (макс.)"},
                    {"9.2","1200", "Мбит/сек (макс.)"},
                    {"8.5","1150", "Мбит/сек (макс.)"},
                    {"7.8","1100", "Мбит/сек (макс.)"},
                    {"7.1","1050", "Мбит/сек (макс.)"},
                    {"6.4","1000", "Мбит/сек (макс.)"},
                    {"5.7","950", "Мбит/сек (макс.)"},
                    {"5.0","900", "Мбит/сек (макс.)"},
                    {"4.1","850", "Мбит/сек (макс.)"},
                    {"3.1","800", "Мбит/сек (макс.)"},
                    {"2.1","750", "Мбит/сек (макс.)"},
                    {"1.7","700", "Мбит/сек (макс.)"},
                    {"1.3","650", "Мбит/сек (макс.)"},
                    {"1.0","600", "Мбит/сек (макс.)"},
                    {"896","550", "Кбит/сек (макс.)"},
                    {"768","500", "Кбит/сек (макс.)"},
                    {"640","450", "Кбит/сек (макс.)"},
                    {"512","400", "Кбит/сек (макс.)"},
                    {"416","350", "Кбит/сек (макс.)"},
                    {"320","300", "Кбит/сек (макс.)"},
                    {"64","0", "Кбит/сек (макс.)"},
            };
        }
        return new Object[][]{
                {"64","0", "Кбит/сек (макс.)"},
                {"320","300", "Кбит/сек (макс.)"},
                {"416","350", "Кбит/сек (макс.)"},
                {"512","400", "Кбит/сек (макс.)"},
                {"640","450", "Кбит/сек (макс.)"},
                {"768","500", "Кбит/сек (макс.)"},
                {"896","550", "Кбит/сек (макс.)"},
                {"1.0","600", "Мбит/сек (макс.)"},
                {"1.3","650", "Мбит/сек (макс.)"},
                {"1.7","700", "Мбит/сек (макс.)"},
                {"2.1","750", "Мбит/сек (макс.)"},
                {"3.1","800", "Мбит/сек (макс.)"},
                {"4.1","850", "Мбит/сек (макс.)"},
                {"5.0","900", "Мбит/сек (макс.)"},
                {"5.7","950", "Мбит/сек (макс.)"},
                {"6.4","1000", "Мбит/сек (макс.)"},
                {"7.1","1050", "Мбит/сек (макс.)"},
                {"7.8","1100", "Мбит/сек (макс.)"},
                {"8.5","1150", "Мбит/сек (макс.)"},
                {"9.2","1200", "Мбит/сек (макс.)"},
                {"10.0","1250", "Мбит/сек (макс.)"},
                {"12.0","1300", "Мбит/сек (макс.)"},
                {"15.0","1350", "Мбит/сек (макс.)"},
                {"Макс","1400", "Мбит/сек (макс.)"},
        };
    }
}