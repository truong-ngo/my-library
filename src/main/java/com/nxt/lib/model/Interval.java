package com.nxt.lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * An interval use to encoding node (encoding position) in tree structure base on Farey sequence
 * */
@Data
public class Interval {

    /**
     * Interval side
     * */
    public enum Side {
        LEFT, RIGHT
    }

    /**
     * Interval left side
     * */
    private Rational left;

    /**
     * Interval right side
     * */
    private Rational right;

    /**
     * Prevent using constructor, using factory method instead
     * @see #of(Rational)
     * */
    private Interval(Rational left, Rational right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Interval factory method with left rational
     * @param left left rational
     * @return {@code Interval} with left and calculated right
     * */
    public static Interval of(Rational left) {
        Rational right = Rational.nextRational(left);
        return new Interval(left, right);
    }

    /**
     * Factory method for create root interval
     * */
    public static Interval root() {
        Rational rational = Rational.of(0L, 1L);
        return Interval.of(rational);
    }

    /**
     * Find parent of an interval
     * @return {@code Interval} represent parent interval or null if interval is root
     * */
    public Interval parent() {
        if (isRoot()) return null;
        long num = left.getNum() - right.getNum();
        long den = left.getDen() - right.getDen();
        return Interval.of(Rational.of(num, den));
    }

    /**
     * Find child number n of an interval
     * @param n position of child interval
     * @return {@code Interval} represent child number n of interval
     * */
    public Interval child(int n) {
        long num = left.getNum() * n + right.getNum();
        long den = left.getDen() * n + right.getDen();
        return Interval.of(Rational.of(num, den));
    }

    /**
     * Find insertion interval of an interval
     * @param children available children's interval
     * @return {@code Interval} insertion interval
     * @throws RuntimeException if children interval is invalid
     * */
    @JsonIgnore
    public Interval getInsertion(List<Interval> children) {
        boolean isValid = children.stream().allMatch(p -> p.isChildOf(this));
        if (!isValid) throw new RuntimeException("Invalid children interval");
        Rational mediant = Rational.mediant(left, right);
        List<Rational> childLefts = children.stream().map(Interval::getLeft).toList();
        while (childLefts.contains(mediant)) mediant = Rational.mediant(left, mediant);
        return Interval.of(mediant);
    }

    /**
     * Transform current interval (position) to new one when moving ancestor interval
     * <p>
     * Use to calculate new descendants position (interval) if necessary when moving node in tree
     * @param oldAncestor old ancestor position
     * @param newAncestor new ancestor position
     * @return {@code Interval} new position base on old ancestor position and new ancestor position
     * @throws RuntimeException if old ancestor is invalid (not contains this interval)
     * */
    public Interval transform(Interval oldAncestor, Interval newAncestor) {
        if (!oldAncestor.contains(this)) throw new RuntimeException("Old ancestor doesn't contains interval");
        long num = (oldAncestor.left.getDen() * newAncestor.right.getNum() - oldAncestor.right.getDen() * newAncestor.left.getNum()) * left.getNum() +
                   (oldAncestor.right.getNum() * newAncestor.left.getNum() - oldAncestor.left.getNum() * newAncestor.right.getNum()) * left.getDen();
        long den = (oldAncestor.left.getDen() * newAncestor.right.getDen() - oldAncestor.right.getDen() * newAncestor.left.getDen()) * left.getNum() +
                   (oldAncestor.right.getNum() * newAncestor.left.getDen() - oldAncestor.left.getNum() * newAncestor.right.getDen()) * left.getDen();
        return Interval.of(Rational.of(num, den));
    }

    /**
     * Indicate that interval is root or not
     * */
    @JsonIgnore
    public boolean isRoot() {
        return left.getNum() == 0 && left.getDen() == 1;
    }

    /**
     * Indicate that interval is child of other interval
     * @param interval parent interval
     * */
    @JsonIgnore
    public boolean isChildOf(Interval interval) {
        if (this.isRoot()) return false;
        return left.getNum() - right.getNum() == interval.left.getNum() ||
               left.getDen() - right.getDen() == interval.left.getDen();
    }

    /**
     * Indicate that if this interval contains other interval
     * @param interval other interval
     * */
    public boolean contains(Interval interval) {
        return left.compareTo(interval.left) < 0 && right.compareTo(interval.right) >= 0;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s]", left.toString(), right.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interval interval = (Interval) o;
        return Objects.equals(left, interval.left) && Objects.equals(right, interval.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
