package ru.simplgroupp.rest.api;

import java.math.BigInteger;

/**
 * Объекты класса <code>PayCashAccount</code> представляют номер счета в Яндекс.Деньгах.
 * Более подробную информацию можно получить из документа <code>CExtendedAccountNumbers.doc</code>
 */
public final class YandexAccount implements Comparable<YandexAccount> {

    /**
     * Номер счета в строком представлени
     */
    private String account;
    /**
     * Номер процессингового центра
     */
    private int bank_number;
    /**
     * Номер счета в процессинговом центре
     */
    private long account_id;

    /**
     * @param account номер счета в системе Яндекс.Деньги
     * @throws IllegalArgumentException Если переданная последовательность не соответствует установленному формату.
     */
    public YandexAccount(String account) {
        Builder builder = new Builder(account);
        builder.compute();
        this.account = builder.source;
        this.bank_number = Integer.parseInt(builder.x);
        this.account_id = Long.parseLong(builder.y);
    }

    /**
     * Номер счета в системе Яндекс.Деньги
     *
     * @return -
     */
    public String getAccount() {
        return account;
    }

    /**
     * @return Номер процессингового цетра в системе Яндекс.Деньги
     */
    public int getBankNumber() {
        return bank_number;
    }

    /**
     * @return Номер виртуального счета пользователя в процессинговом центре системы Яндекс.Деньги
     */
    public long getAccountID() {
        return account_id;
    }

    @Override
    public int hashCode() {
        return 17 + (bank_number) * 37 + ((int) account_id) * 37;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof YandexAccount))
            return false;
        YandexAccount a = (YandexAccount) o;
        return account.equals(a.account);
    }

    @Override
    public String toString() {
        return account;
    }

    public int compareTo(YandexAccount o) {
        return account.compareTo(o.getAccount());
    }

    public static final class Builder {
        private static final String ERROR_MSG_HEADER = "Illegal account number: ";
        private static final int N_LEN = 1;
        private static final int Z_LEN = 2;
        private static final int Y_MAX_LEN = 19;
        private static final BigInteger INT_99 = BigInteger.valueOf(99L);
        private static final int p[][] = new int[10][30];

        static {
            BigInteger m_13 = BigInteger.valueOf(13L);

            BigInteger[] m = new BigInteger[30];
            m[0] = BigInteger.valueOf(169L);
            for (int i = 1; i < m.length; i++)
                m[i] = m[i - 1].multiply(m_13);

            for (int i = 0; i < 10; i++) {
                BigInteger t_i = BigInteger.valueOf(i != 0 ? i : 10);
                for (int j = 0; j < 30; j++)
                    p[i][j] = t_i.multiply(m[j]).mod(INT_99).intValue();
            }
        }

        private String source;
        private int n;
        private String x;
        private String y;
        private String z;

        private Builder(String source) {
            if (source == null || source.isEmpty())
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", account sequence is empty.");
            this.source = source;
        }

        private static boolean isDigit(String value) {
            for (int i = 0; i < value.length(); i++)
                if (!Character.isDigit(value.charAt(i)))
                    return false;
            return true;
        }

        public static String calculateRedundancy(String x, String y) {
            int[] t = new int[30]; // 0-9

            for (int t_i = 0, y_j = y.length() - 1; y_j >= 0; y_j--, t_i++)
                t[t_i] = Character.digit(y.charAt(y_j), 10);

            for (int t_i = 20, x_i = x.length() - 1; x_i >= 0; x_i--, t_i++)
                t[t_i] = Character.digit(x.charAt(x_i), 10);

            int result = 0;
            for (int i = 0; i < t.length; i++)
                result += p[t[i]][i];

            String _z = String.valueOf(result % INT_99.intValue() + 1);
            if (_z.length() == 1)
                _z = '0' + _z;
            return _z;
        }

        /**
         * Осуществляет вычленение данных из входящей последовательности.
         *
         * @throws IllegalArgumentException в случае невозможности разобрать входящую последовательность.
         */
        private void compute() {
            fetchN();
            fetchX();
            fetchY();
            fetchZ();
            String _z = calculateRedundancy(x, y);
            if (!_z.equals(z))
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Z is not valid. z=" + z + ", but expected " + _z);
        }

        private void fetchN() {
            try {
                int _n = Integer.parseInt(source.substring(0, N_LEN));
                if (!(_n >= 0 && _n <= 9)) // Paranoia
                    throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", wrong value of N. N=" + _n);
                this.n = _n == 0 ? 10 : _n;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", N is not a number.");
            }
        }

        private void fetchX() {
            if (source.length() < N_LEN + n)
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", X is invalid. Source lenght is too small. len="
                        + (source.length() - 1));
            String _x = source.substring(N_LEN, N_LEN + n);
            if (_x.charAt(0) == '0')
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", X has leading digit '0'");
            if (!isDigit(_x))
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", X is not a number. x=" + _x);
            try {
                Integer.parseInt(_x);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", X is out of range. x=" + _x);
            }
            this.x = _x;
        }

        private void fetchY() {
            int start_index = N_LEN + n;
            int end_index = source.length() - Z_LEN;
            if (end_index <= start_index)
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Y is empty. start=" + start_index + ", end=" + end_index);
            if (end_index - start_index > Y_MAX_LEN)
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Y is too long. y.len=" + (end_index - start_index));
            String _y = source.substring(start_index, end_index);
            if (_y.charAt(0) == '0')
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Y has leading digit '0'");
            if (!isDigit(_y))
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Y is not a number. y=" + _y);
            try {
                Long.parseLong(_y);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Y is out of range. y=" + _y, e);
            }
            this.y = _y;
        }

        private void fetchZ() {
            String _z = source.substring(source.length() - Z_LEN, source.length());
            if (_z.charAt(0) == '0' && _z.charAt(1) == '0')
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Z is invalid. y=" + _z);
            if (!isDigit(_z))
                throw new IllegalArgumentException(ERROR_MSG_HEADER + source + ", Z is not a number. z=" + _z);
            this.z = _z;
        }
    }

}
