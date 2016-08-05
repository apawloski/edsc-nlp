/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.earthdata.edsc.temporal;

import java.io.Serializable;

/**
 *
 * @author bsun
 */
public class Pair<T1, T2> implements Comparable<Pair<T1, T2>>, Serializable {

    private T1 first;
    private T2 second;

    public Pair() {
        // first = null; second = null; -- default initialization
    }

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return second;
    }

    public void setFirst(T1 o) {
        first = o;
    }

    public void setSecond(T2 o) {
        second = o;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }

    @Override
    public int compareTo(Pair<T1, T2> o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
