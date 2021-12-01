package br.rochards.item14;

import java.util.Comparator;

public class PhoneNumber implements Comparable<PhoneNumber> {

    private final short areaCode;
    private final short prefix;
    private final short lineNum;
    private static final Comparator<PhoneNumber> COMPARATOR = Comparator.comparingInt((PhoneNumber pn)  -> pn.areaCode);

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "area code");
        this.prefix = rangeCheck(prefix, 999, "prefix");
        this.lineNum = rangeCheck(lineNum, 9999, "line num");
    }

    private short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PhoneNumber that = (PhoneNumber) o;

        if (areaCode != that.areaCode) return false;
        if (prefix != that.prefix) return false;
        return lineNum == that.lineNum;
    }

    @Override
    public int hashCode() {
        int result = areaCode;
        result = 31 * result + (int) prefix;
        result = 31 * result + (int) lineNum;
        return result;
    }


    @Override
    public int compareTo(PhoneNumber phoneNumber) {
        int result = Short.compare(areaCode, phoneNumber.areaCode);
        if (result == 0) {
            result = Short.compare(prefix, phoneNumber.prefix);
            if (result == 0)
                return Short.compare(lineNum, phoneNumber.lineNum);
        }
        return result;
    }
}
