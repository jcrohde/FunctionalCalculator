/*

Copyright (C) 2015-2018 Jan Christian Rohde

This file is part of FunctionalCalculator.

FunctionalCalculator is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or (at your
option) any later version.

FunctionalCalculator is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
License for more details.

You should have received a copy of the GNU General Public License
along with FunctionalCalculator. If not, see http://www.gnu.org/licenses/.

*/

import java.math.BigDecimal;
import java.math.BigInteger;

enum FunctionOperation {
    BESSEL1, BESSEL2, BINOMIAL, PLUS, MINUS, MULT, DIV, POWER, EVAL, TANGENT, NORMAL, GCD, LCM, LOG, MOD, MAX, MIN, NONE, WRITEFILE
}

class MyParserException extends Exception {
    public static enum type {
        DIVBYZERO, NOBRACKOP, NOBRACKCL, NOTINT, NOTREAL, INCORRECTFCTN, NOPRIMARY,
        INCORRECTARGUMENTS, INCORRECTINPUT, NOTNUMBER, NOTARBITRARYPRECISION,
        NOTINVERTIBLEMATRIX, NOTDEFINEDATPLACE, INTNEG, NOTMATRIX
    }

    public type myType;
    public String message;

    MyParserException(type myType, String message) {
        this.myType = myType;
        this.message = message;
    }
}

class ArbitraryPrecisionRational {
    private BigInteger numerator;
    private BigInteger denominator;

    public ArbitraryPrecisionRational(BigInteger numerator, BigInteger denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public ArbitraryPrecisionRational add(ArbitraryPrecisionRational r) {
        BigInteger n1 = numerator.multiply(r.denominator);
        BigInteger n2 = denominator.multiply(r.numerator);
        BigInteger n = n1.add(n2);
        BigInteger d = denominator.multiply(r.denominator);
        return new ArbitraryPrecisionRational(n, d);
    }

    public ArbitraryPrecisionRational subtract(ArbitraryPrecisionRational r) {
        BigInteger n1 = numerator.multiply(r.denominator);
        BigInteger n2 = denominator.multiply(r.numerator);
        BigInteger n = n1.subtract(n2);
        BigInteger d = denominator.multiply(r.denominator);
        return new ArbitraryPrecisionRational(n, d);
    }

    public ArbitraryPrecisionRational multiply(ArbitraryPrecisionRational r) {
        BigInteger n = numerator.multiply(r.numerator);
        BigInteger d = denominator.multiply(r.denominator);
        return new ArbitraryPrecisionRational(n, d);
    }

    public ArbitraryPrecisionRational divide(ArbitraryPrecisionRational r) {
        BigInteger n = numerator.multiply(r.denominator);
        BigInteger d = denominator.multiply(r.numerator);
        return new ArbitraryPrecisionRational(n, d);
    }

    public ArbitraryPrecisionRational power(BigInteger n) {
        return new ArbitraryPrecisionRational(numerator.pow(n.intValue()), denominator.pow(n.intValue()));
    }

    public double getDouble() {
        double n = numerator.doubleValue(), d = denominator.doubleValue();
        return n / d;
    }


    public boolean isInteger() {
        canonicalize();
        return denominator.equals(BigInteger.ONE);
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public String printString(int precision) {
        double value = this.getDouble();
        if (precision == 2) return String.format("%.2f", value);
        if (precision == 3) return String.format("%.3f", value);
        if (precision == 4) return String.format("%.4f", value);
        if (precision == 5) return String.format("%.5f", value);
        if (precision == 6) return String.format("%.6f", value);
        if (precision == 7) return String.format("%.7f", value);
        if (precision == 8) return String.format("%.8f", value);
        if (precision == 9) return String.format("%.9f", value);
        if (precision == 10) return String.format("%.10f", value);
        if (precision == 11) return String.format("%.11f", value);
        if (precision == 12) return String.format("%.12f", value);
        if (precision == 13) return String.format("%.13f", value);
        if (precision == 14) return String.format("%.14f", value);
        if (precision == 15) return String.format("%.15f", value);
        return String.format("%.16f", value);
    }

    public boolean equals(ArbitraryPrecisionRational r) {
        this.canonicalize();
        r.canonicalize();
        return numerator.equals(r.numerator) && denominator.equals(r.denominator);
    }

    private void canonicalize() {
        BigInteger d = numerator.gcd(denominator);
        numerator = numerator.divide(d);
        denominator = denominator.divide(d);
        if (denominator.compareTo(BigInteger.ZERO) < 0) {
            numerator = numerator.negate();
            denominator = denominator.negate();
        }
    }
}

abstract class ValueStrategy {

    public abstract double getDouble();

    public abstract ArbitraryPrecisionRational getRational() throws MyParserException;

    public abstract BigInteger getInteger() throws MyParserException;

    public abstract String print(int precision);

    public abstract boolean isRational();

    public abstract boolean isInteger();

    public abstract ValueStrategy performOperation(FunctionOperation Op, ValueStrategy strat) throws MyParserException;

    public abstract ValueStrategy negate();

    public String printNumber(int precision) {
        double d = getDouble();
        String str;
        if (precision == 2) str = String.format("%.2f", d);
        else if (precision == 3) str = String.format("%.3f", d);
        else if (precision == 4) str = String.format("%.4f", d);
        else if (precision == 5) str = String.format("%.5f", d);
        else if (precision == 6) str = String.format("%.6f", d);
        else if (precision == 7) str = String.format("%.7f", d);
        else if (precision == 8) str = String.format("%.8f", d);
        else if (precision == 9) str = String.format("%.9f", d);
        else if (precision == 10) str = String.format("%.10f", d);
        else if (precision == 11) str = String.format("%.11f", d);
        else if (precision == 12) str = String.format("%.12f", d);
        else if (precision == 13) str = String.format("%.13f", d);
        else if (precision == 14) str = String.format("%.14f", d);
        else if (precision == 15) str = String.format("%.15f", d);
        else str = String.format("%.16f", d);
        str = str.replace(",", ".");
        return str;
    }

}

class DoubleStrategy extends ValueStrategy {
    private double value;

    public DoubleStrategy(double value) {
        this.value = value;
    }

    public double getDouble() {
        return value;
    }

    public ArbitraryPrecisionRational getRational() throws MyParserException {
        throw new MyParserException(MyParserException.type.NOTARBITRARYPRECISION, print(6) + " is not an arbitrary precision rational number");
    }

    public BigInteger getInteger() throws MyParserException {
        throw new MyParserException(MyParserException.type.NOTINT, print(6) + " is not an integer");
    }

    public String print(int precision) {
        return printNumber(precision);
    }

    public boolean isRational() {
        return false;
    }

    public boolean isInteger() {
        return false;
    }

    public ValueStrategy performOperation(FunctionOperation Op, ValueStrategy strat) throws MyParserException {
        double a = getDouble(), b = strat.getDouble();
        if (Op == FunctionOperation.PLUS) return new DoubleStrategy(a + b);
        else if (Op == FunctionOperation.MINUS) return new DoubleStrategy(a - b);
        else if (Op == FunctionOperation.MULT) return new DoubleStrategy(a * b);
        else if (Op == FunctionOperation.DIV) return new DoubleStrategy(a / b);
        else if (Op == FunctionOperation.POWER) return new DoubleStrategy(Math.pow(a, b));
        else if (Op == FunctionOperation.LOG) return new DoubleStrategy(Math.log(b) / Math.log(a));
        else
            throw new MyParserException(MyParserException.type.INCORRECTARGUMENTS, "For an inserted binary function or operation the arguments are not correct.");
    }

    public ValueStrategy negate() {
        return new DoubleStrategy(-value);
    }
}

class ComplexStrategy extends ValueStrategy {
    ComplexStrategy(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double getDouble() {
        return Math.sqrt(re * re + im * im);
    }

    public ArbitraryPrecisionRational getRational() throws MyParserException {
        throw new MyParserException(MyParserException.type.NOTREAL, print(6) + " is not a real number");
    }

    public BigInteger getInteger() throws MyParserException {
        throw new MyParserException(MyParserException.type.NOTINT, print(6) + " is not an integer");
    }

    double re() {
        return re;
    }

    double im() {
        return im;
    }

    public ComplexStrategy multiply(ComplexStrategy str) {
        return new ComplexStrategy(re * str.re() - im * str.im(), re * str.im + im * str.re);
    }

    public ComplexStrategy minus(ComplexStrategy str) {
        return new ComplexStrategy(re - str.re(), im - str.im);
    }

    public ComplexStrategy add(ComplexStrategy str) {
        return new ComplexStrategy(re + str.re(), im + str.im);
    }

    public ComplexStrategy divide(ComplexStrategy str) {
        double sqr = str.getDouble() * str.getDouble();
        return new ComplexStrategy((re * str.re() + im * str.im()) / sqr, (-re * str.im() + im * str.re()) / sqr);
    }

    public ComplexStrategy sin() {
        return new ComplexStrategy(Math.sin(re()) * Math.cosh(im()), Math.cos(re()) * Math.sinh(im()));
    }

    public ComplexStrategy cos() {
        return new ComplexStrategy(Math.cos(re()) * Math.cosh(im()), -Math.sin(re()) * Math.sinh(im()));
    }

    public ComplexStrategy ln() {
        return new ComplexStrategy(Math.log(Math.sqrt(re() * re() + im() * im())), Math.atan2(im(), re()));
    }

    public ComplexStrategy cosh() {
        ComplexStrategy i = new ComplexStrategy(0, 1);
        ComplexStrategy arg = i.multiply(this);
        return arg.cos();
    }

    public ComplexStrategy sinh() {
        ComplexStrategy i = new ComplexStrategy(0, 1), minusI = new ComplexStrategy(0, -1);
        ComplexStrategy arg = i.multiply(this);
        return minusI.multiply(arg.sin());
    }

    public ComplexStrategy sqrt() {
        double lnaRe = Math.log(Math.sqrt(re() * re() + im() * im())), lnaIm = Math.atan2(im(), re());
        double norm = Math.exp(lnaRe / 2), expIm = lnaIm / 2;
        return new ComplexStrategy(norm * Math.cos(expIm), norm * Math.sin(expIm));
    }

    public ComplexStrategy asin() {
        ComplexStrategy s = new ComplexStrategy(1.0, 0.0).minus(this.multiply(this));
        s = s.sqrt();
        ComplexStrategy t = new ComplexStrategy(0.0, 1.0).multiply(this).add(s);
        t = t.ln();
        return t.multiply(new ComplexStrategy(0.0, -1.0));
    }

    public String print(int precision) {
        String str;
        if (im == 0.0) str = printANumber(precision, re);
        else {
            boolean posIm = true;
            if (im == 1.0) str = "i";
            else if (im == -1.0) {
                str = "-i";
                posIm = false;
            } else if (im < 0.0) {
                str = "-i*" + printANumber(precision, -im);
                posIm = false;
            } else str = "i*" + printANumber(precision, im);

            if (re != 0.0) {
                if (posIm) str = printANumber(precision, re) + "+" + str;
                else str = printANumber(precision, re) + str;
            }
        }
        return str;
    }

    private String printANumber(int precision, double d) {

        String str;
        if (precision == 2) str = String.format("%.2f", d);
        else if (precision == 3) str = String.format("%.3f", d);
        else if (precision == 4) str = String.format("%.4f", d);
        else if (precision == 5) str = String.format("%.5f", d);
        else if (precision == 6) str = String.format("%.6f", d);
        else if (precision == 7) str = String.format("%.7f", d);
        else if (precision == 8) str = String.format("%.8f", d);
        else if (precision == 9) str = String.format("%.9f", d);
        else if (precision == 10) str = String.format("%.10f", d);
        else if (precision == 11) str = String.format("%.11f", d);
        else if (precision == 12) str = String.format("%.12f", d);
        else if (precision == 13) str = String.format("%.13f", d);
        else if (precision == 14) str = String.format("%.14f", d);
        else if (precision == 15) str = String.format("%.15f", d);
        else str = String.format("%.16f", d);
        str = str.replace(",", ".");
        return str;
    }

    public boolean isRational() {
        return false;
    }

    public boolean isInteger() {
        return false;
    }

    public ValueStrategy performOperation(FunctionOperation Op, ValueStrategy strat) throws MyParserException {
        if (strat instanceof ComplexStrategy) {
            return performComplexOperation(Op, (ComplexStrategy) strat);
        } else {
            return performComplexOperation(Op, new ComplexStrategy(strat.getDouble(), 0.0));
        }
    }

    public ComplexStrategy performComplexOperation(FunctionOperation Op, ComplexStrategy c) throws MyParserException {
        ComplexStrategy result = null;
        if (Op == FunctionOperation.PLUS) result = add(c);
        else if (Op == FunctionOperation.MINUS) result = new ComplexStrategy(re - c.re, im - c.im);
        else if (Op == FunctionOperation.MULT) result = multiply(c);
        else if (Op == FunctionOperation.DIV) result = divide(c);
        else if (Op == FunctionOperation.LOG) {
            result = (c.ln()).divide(ln());
        } else if (Op == FunctionOperation.POWER) {
            double lnaRe = Math.log(Math.sqrt(re() * re() + im() * im())), lnaIm = Math.atan2(im(), re());
            double norm = Math.exp(lnaRe * c.re - lnaIm * c.im), expIm = lnaRe * c.im + lnaIm * c.re;
            result = new ComplexStrategy(norm * Math.cos(expIm), norm * Math.sin(expIm));
        }
        return result;
    }

    public ValueStrategy negate() {
        return new ComplexStrategy(-re, -im);
    }

    private double re, im;
}

class RationalStrategy extends ValueStrategy {
    private ArbitraryPrecisionRational value;

    public RationalStrategy(ArbitraryPrecisionRational value) {
        this.value = value;
    }

    public double getDouble() {
        return value.getDouble();
    }

    public ArbitraryPrecisionRational getRational() {
        return value;
    }

    public BigInteger getInteger() throws MyParserException {
        if (!value.isInteger())
            throw new MyParserException(MyParserException.type.NOTINT, print(6) + " is not an integer");
        return value.getNumerator();
    }

    public String print(int precision) {
        return printNumber(precision);
    }

    public boolean isRational() {
        return true;
    }

    public boolean isInteger() {
        return value.isInteger();
    }

    public ValueStrategy performOperation(FunctionOperation Op, ValueStrategy strat) throws MyParserException {
        if (strat.isRational()) {
            ArbitraryPrecisionRational a = getRational(), b = strat.getRational();
            RationalStrategy result = null;
            if (Op == FunctionOperation.PLUS) result = new RationalStrategy(a.add(b));
            else if (Op == FunctionOperation.MINUS) result = new RationalStrategy(a.subtract(b));
            else if (Op == FunctionOperation.MULT) result = new RationalStrategy(a.multiply(b));
            else if (Op == FunctionOperation.DIV) result = new RationalStrategy(a.divide(b));
            else if (Op == FunctionOperation.POWER && strat.isInteger())
                result = new RationalStrategy(a.power(strat.getInteger()));
            if (result != null) return result;
        }
        DoubleStrategy r = new DoubleStrategy(value.getDouble());
        return r.performOperation(Op, strat);
    }

    public ValueStrategy negate() {
        return new RationalStrategy(value.multiply(new ArbitraryPrecisionRational(BigInteger.ONE.negate(), BigInteger.ONE)));
    }
}

class IntegerStrategy extends ValueStrategy {
    private BigInteger value;

    public IntegerStrategy(BigInteger value) {
        this.value = value;
    }

    public double getDouble() {
        return value.doubleValue();
    }

    public ArbitraryPrecisionRational getRational() {
        return new ArbitraryPrecisionRational(value, BigInteger.ONE);
    }

    public BigInteger getInteger() {
        return value;
    }

    public String print(int precision) {
        return String.valueOf(value);
    }

    public boolean isRational() {
        return true;
    }

    public boolean isInteger() {
        return true;
    }

    public ValueStrategy performOperation(FunctionOperation Op, ValueStrategy strat) throws MyParserException {
        if (strat.isInteger()) {
            BigInteger a = getInteger(), b = strat.getInteger();
            if (Op == FunctionOperation.PLUS) return new IntegerStrategy(a.add(b));
            else if (Op == FunctionOperation.MINUS) return new IntegerStrategy(a.subtract(b));
            else if (Op == FunctionOperation.MULT) return new IntegerStrategy(a.multiply(b));
            else if (Op == FunctionOperation.BINOMIAL) {
                double d = cern.jet.math.Arithmetic.binomial(a.intValue(), b.intValue()) +0.01;
                return new IntegerStrategy(BigInteger.valueOf((int) d));
            }
            else if (Op == FunctionOperation.MOD) {
                if (b.compareTo(BigInteger.ZERO) < 0) b = b.negate();
                else if (b.compareTo(BigInteger.ZERO) == 0) return this;
                return new IntegerStrategy(a.mod(b));
            }
            else if (Op == FunctionOperation.DIV) {
                BigInteger c = (b.compareTo(BigInteger.ZERO) < 0) ? b.negate() : b;
                if (a.mod(c).equals(BigInteger.ZERO)) {
                    return new IntegerStrategy(a.divide(b));
                }
            }
            else if (Op == FunctionOperation.GCD) return new IntegerStrategy(a.gcd(b));
            else if (Op == FunctionOperation.LCM) {
                BigInteger gcd = a.gcd(b), prod = a.multiply(b);
                return new IntegerStrategy(prod.divide(gcd));
            }
        }
        if (Op == FunctionOperation.BESSEL1) {
            int a = getInteger().intValue();
            double b = strat.getDouble();
            return new DoubleStrategy(cern.jet.math.Bessel.jn(a, b));
        }
        else if (Op == FunctionOperation.BESSEL2) {
            int a = getInteger().intValue();
            double b = strat.getDouble();
            return new DoubleStrategy(cern.jet.math.Bessel.yn(a, b));
        }
        RationalStrategy r = new RationalStrategy(getRational());
        return r.performOperation(Op, strat);
    }

    public ValueStrategy negate() {
        return new IntegerStrategy(value.negate());
    }
}

class ValueStrategyFactory {
    public ValueStrategy build(String str) {
        if (str.contains(".")) return new DoubleStrategy(Double.parseDouble(str));
        return new IntegerStrategy(new BigInteger(str));
    }
}
