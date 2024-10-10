package com.nxt.lib.model;

import lombok.Data;

import java.util.Objects;

/**
 * Rational represent left and right of an {@link Interval} base on Farey sequence
 * */
@Data
public class Rational implements Comparable<Rational> {

    /**
     * Rational element
     * */
    public enum Element {
        NUMERATOR, DENOMINATOR
    }

    /**
     * Numerator
     * */
    private Long num;

    /**
     * Denominator
     * */
    private Long den;

    /**
     * Prevent using constructor, using factory method instead
     * @see #of(Long, Long)
     * */
    private Rational(Long den, Long num) {
        this.den = den;
        this.num = num;
    }

    /**
     * Factory method of rational
     * @param num numerator
     * @param den denominator
     * @return {@code Rational} represent num / den
     * */
    public static Rational of(Long num, Long den) {
        Long gcd = gcd(num, den);
        return new Rational(num / gcd, den / gcd);
    }

    /**
     * Greatest common divisor
     * @param a number a
     * @param b number b
     * @return greatest common divisor of a and b
     * */
    public static long gcd(Long a, Long b) {
        if (a == 0) return b;
        if (b == 0) return a;
        if (a > b) return gcd(a % b, b);
        else return (gcd(a, b % a));
    }

    /**
     * Find mediant of two rational.
     * <p>
     * Mediant of a / b and c / d is (a + c) / (b + d)
     * @param a rational a
     * @param b rational b
     * @return mediant of a and b
     * */
    public static Rational mediant(Rational a, Rational b) {
        return of(a.num + b.num, a.den + b.den);
    }

    /**
     * A modification's of Modular multiplicative inverse (x) of a modulus m (-a*x + m*y = 1)
     * <p>
     * Use to calculate right element's denominator (x) of interval base on left (a / m)
     * <p>
     * Right element is the next element of left (a / m) in F(m) Farey sequence
     * @param a left numerator
     * @param m left denominator
     * @return Interval right element denominator
     * @throws ArithmeticException if the inverse does not existed
     * */
    @SuppressWarnings("all")
    public static long inverse(long a, long m) {
        long u = m;
        long v = a;
        long x = 0;
        long y = 1;
        while (v != 0) {
            long q = u / v;
            long r = u % v;
            long tempX = x;
            x = y;
            y = tempX - q * y;
            u = v;
            v = r;
        }
        if (u == 1 || u == -1) {
            return (x < 0) ? -x : m - x;
        } else {
            throw new ArithmeticException("Inverse does not exist");
        }
    }

    /**
     * Next rational of a / b in F(b) Farey sequence
     * @param rational given rational
     * @return Next rational of a / b
     * */
    public static Rational nextRational(Rational rational) {
        long den = inverse(rational.num, rational.den);
        long num = (rational.num * den + 1) / rational.den;
        return of(num, den);
    }

    /**
     * Comparing two rational
     * <p>
     * Support H2 database function test
     * @param a first rational numerator
     * @param b first rational denominator
     * @param c second rational numerator
     * @param d second rational denominator
     * @return zero if two rational is equal, negative if first < second, positive is first > second
     * */
    public static long compare(long a, long b, long c, long d) {
        return a * d - b * c;
    }

    @Override
    public int compareTo(Rational arg) {
        return (int) (compare(num, den, arg.num, arg.den));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rational rational = (Rational) o;
        return Objects.equals(num, rational.num) && Objects.equals(den, rational.den);
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, den);
    }

    @Override
    public String toString() {
        return String.format("%s/%s", num, den);
    }
}