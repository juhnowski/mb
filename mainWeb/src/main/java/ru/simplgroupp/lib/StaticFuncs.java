package ru.simplgroupp.lib;

import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

/**
 * StaticFuncs набор статических функций используемых в модуле
 *
 * @author Andrey Unger aka Cobalt <unger1984@gmail.com> http://www.servicepro-online.ru
 * @since 25.04.14
 */
public class StaticFuncs {
	
	private static Logger logger = Logger.getLogger(StaticFuncs.class.getName());

    /**
     * Вычисляет возраст из даты рождения
     * // взято у старого разработчика
     *
     * @param birthday
     * @return
     */
    public static Integer calculateAge(final Date birthday) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.setTime(birthday);
        dob.add(Calendar.DAY_OF_MONTH, -1);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) <= dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    /**
     * Проверяем чтобы дата не превышала сегодня
     *
     * @param date
     * @return
     */
    public static boolean checkDate(final Date date) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        if (date.compareTo(today.getTime()) <= 0) {
            return true;
        }
        return false;
    }

    /**
     * Генерирует код нужной динны
     *
     * @param quantum
     * @return
     */
    public static String generateCode(int quantum) {
        Random randomGenerator = new Random();
        String res = "";
        for (int i = 1; i <= quantum; i++) {
            Integer randomInt = randomGenerator.nextInt(10);
            res += randomInt.toString();
        }
        return res;
    }

    /**
     * Получает md5 хэш строки
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        byte[] data = string.getBytes();
        m.update(data, 0, data.length);
        BigInteger i = new BigInteger(1, m.digest());
        return String.format("%1$032X", i);
    }

    /**
     * Так как у нас нет http сессии, извратимся и сэмулируем ее сами
     * (с) люблю велосипеды))
     *
     * @param id        идентификатор заявки
     * @param stepcount кол-во пройденных в этой заявке шагов
     * @return хэш идентификатор сессии
     */
    public static String generateSessionId(Integer id, Integer stepcount) {
        return md5("salt" + id + "tlas" + stepcount + "salt"); // хрен кто догадается если не знает
    }

    /**
     * Так как у нас нет http сессии, извратимся и сэмулируем ее сами
     * тут мы из методом подбора из хитрого хэша получаем кол-во проеденных шагов
     * (с) люблю велосипеды))
     *
     * @param hash хэш который проверяем
     * @param id   id заявки которую проверяем
     * @return 0 если не нашли или кол-во шагов
     */
    public static Integer getSessionStepCount(String hash, Integer id) {
        if (StringUtils.isNotEmpty(hash) && id != null && id != 0)
            for (int i = 1; i <= 8; i++)
                if (hash.equals(md5("salt" + id + "tlas" + i + "salt")))
                    return i;
        return 0;
    }

    /**
     * Так как мы не можем хранить состояние сессии, а просто ID передавать не
     * секурно, Извратимся и "зашифруем" его в хэш чтобы потом этот хэш можно
     * было разобрать. На самом деле делаем md5 от id, отрезаем от него id.length*2 символов,
     * и чередуя их со случайными буквами дописываем к полученной строке
     *
     * @param id
     * @return
     */
    public static String generateHashId(Integer id) {
        String i = id.toString();
        int l = i.length();
        String hash = md5(i).substring(l * 2); // обрезанный на нужное-кол-во символов хэш
        String salt = "" + l;
        for (int n = 0; n < l; n++) {
            salt += randomLetter() + i.substring(n, n + 1);
        }
        return salt + hash;
    }

    /**
     * Раскодируем наш хитрый хэш чтобы получить id заявки закодированной через
     * generateHashId. Сомнительно что тот кто не знает исходников догадается о таком алгоритме.
     *
     * @param hash
     * @return
     */
    public static Integer decodeIdFromHash(String hash) {
        Integer i = 0;
        try {
            int l = Integer.parseInt(hash.substring(0, 1));
            String c = hash.substring(1, l * 2 + 1).replaceAll("[^\\d.]", "");
            i = new Integer(c);
        } catch (Exception e) {

        }
        return i;
    }

    /**
     * Случайную бвукы из хексов для большей запутанности
     *
     * @return
     */
    public static String randomLetter() {
        Random randomGenerator = new Random();
        Integer randomInt = randomGenerator.nextInt(6);
        String[] array = {"A", "B", "C", "D", "E", "F"};
        return array[randomInt];
    }

    /**
     * Простая эмуляция логировния
     *
     * @param o
     */
    public static void log(Object o){
    	logger.info("=>=== log ====> ");
        if(o instanceof Exception)
            ((Exception) o).printStackTrace();
        else {
        	logger.info(o.toString());
        }
    }
    
    
}
