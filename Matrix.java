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

import java.util.ArrayList;
import java.math.BigInteger;
import java.util.function.Function;

class matrix {
    private int rows, columns;
    private ArrayList<MyFunction> entry;

    public matrix(int rows, int columns, ArrayList<MyFunction> entry) {
        this.rows = rows;
        this.columns = columns;
        this.entry = entry;
    }

    int rows() {
        return rows;
    }

    int columns() {
        return columns;
    }

    public matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        ArrayList<ArbitraryPrecisionRational> entry = new ArrayList<ArbitraryPrecisionRational>();
        for (int i = 0; i < rows * columns; i++) {
            entry.add(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE));
        }
        System.out.println(entry.size());
    }

    public MyFunction getEntry(int i, int j) {
        return entry.get(columns * (i - 1) + j - 1);
    }

    public void setEntry(int i, int j, MyFunction r) {
        entry.set(columns * (i - 1) + j - 1, r);
    }

    public boolean elemZero(int row, int column) throws MyParserException {
        ArbitraryPrecisionRational n = new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE);
        return getEntry(row, column).eval(0.0).getRational().equals(n);
    }

    public matrix add(matrix m) throws MyParserException {
        if (m.rows == rows && m.columns == columns) {
            ArrayList<MyFunction> resEntry = new ArrayList<MyFunction>();
            int size = entry.size();
            for (int i = 0; i < size; i++) {
                resEntry.add(FunctionArithmetics.add(entry.get(i), m.entry.get(i)));
            }
            return new matrix(rows, columns, resEntry);
        }
        return null;
    }

    public matrix subtract(matrix m) throws MyParserException {
        if (m.rows == rows && m.columns == columns) {
            ArrayList<MyFunction> resEntry = new ArrayList<MyFunction>();
            int size = entry.size();
            for (int i = 0; i < size; i++) {
                resEntry.add(FunctionArithmetics.subtract(entry.get(i), m.entry.get(i)));
            }
            return new matrix(rows, columns, resEntry);
        }
        return null;
    }

    public void changeRows(int i, int j) {
        MyFunction memory;
        for (int k = 1; k <= columns; k++) {
            memory = getEntry(i, k);
            setEntry(i, k, getEntry(j, k));
            setEntry(j, k, memory);
        }
    }

    public void normRow(int i, int j) throws MyParserException {

        MyFunction memory = getEntry(i, j);
        for (int k = 1; k <= columns; k++) {
            setEntry(i, k, FunctionArithmetics.divide(getEntry(i, k), memory));
            if (!getEntry(i, k).isConstant() || !getEntry(i, k).eval(0.0).isRational())
                throw new MyParserException(MyParserException.type.NOTARBITRARYPRECISION, "The operation only defined for matrices with constant entries of arbitrary precision.");
        }
    }

    public void addRows(int i, int row, int column) throws MyParserException {
        MyFunction s, memory = getEntry(i, column);
        for (int k = 1; k <= columns; k++) {
            s = FunctionArithmetics.multiply(getEntry(row, k), memory);
            setEntry(i, k, FunctionArithmetics.subtract(getEntry(i, k), s));
            if (!getEntry(i, k).isConstant() || !getEntry(i, k).eval(0.0).isRational())
                throw new MyParserException(MyParserException.type.NOTARBITRARYPRECISION, "The operation only defined for matrices with constant entries of arbitrary precision.");
        }
    }

    public matrix gauss() throws MyParserException {
        matrix result = new matrix(rows, columns, entry);

        int clm = 1;
        int help = 0;
        int i;

        for (int ln = 1; ln <= rows && ln <= columns; ln++) {

            while (clm <= columns) {
                help = ln;
                while (help < rows) {
                    if (!result.elemZero(help, clm)) {
                        break;
                    }
                    help++;
                }
                if (!result.elemZero(help, clm)) {
                    break;
                } else {
                    clm++;
                }
            }

            if (help <= rows && clm <= columns) {

                if (help > ln) {
                    result.changeRows(help, ln);
                }

                if (result.getEntry(ln, clm).eval(0.0).getDouble() != 0.0) {
                    result.normRow(ln, clm);
                }

                for (i = 1; i < ln; i++) {
                    if (!result.elemZero(i, clm)) result.addRows(i, ln, clm);
                }

                for (i = ln + 1; i <= rows; i++) {
                    if (!elemZero(i, clm)) result.addRows(i, ln, clm);
                }

                clm++;
            }
        }
        return result;
    }

    public matrix multiply(matrix m) throws MyParserException {

        ArrayList<MyFunction> entry = new ArrayList<MyFunction>();
        for (int i = 0; i < rows * m.columns; i++) {
            entry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
        }

        matrix product = new matrix(rows, m.columns, entry);
        MyFunction sum, p;

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                sum = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE)));
                for (int k = 1; k <= columns; k++) {
                    p = FunctionArithmetics.multiply(getEntry(i, k), m.getEntry(k, j));
                    sum = FunctionArithmetics.add(sum, p);
                }
                product.setEntry(i, j, sum);
            }
        }
        return product;
    }

    public MyFunction trace() throws MyParserException {
        MyFunction tr = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE)));
        for (int i = 1; i <= rows; i++) {
            tr = FunctionArithmetics.add(tr, getEntry(i, i));
        }
        return tr;
    }

    public MyFunction charPoly() throws MyParserException {
        MyFunction one = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE, BigInteger.ONE)));
        MyFunction q, myI;
        MyFunction minusOne = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE.negate(), BigInteger.ONE)));

        MyFunction a = one, res = null, b = null;
        if (rows == columns) {

            ArrayList<MyFunction> bentry = new ArrayList<MyFunction>();
            for (int i = 0; i < rows * columns * 2; i++) {
                bentry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
            }

            ArrayList<MyFunction> centry = new ArrayList<MyFunction>();
            for (int i = 0; i < rows * columns * 2; i++) {
                centry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
            }

            ArrayList<MyFunction> ientry = new ArrayList<MyFunction>();
            for (int i = 0; i < rows * columns * 2; i++) {
                ientry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
            }

            matrix A = new matrix(rows, columns, this.entry);
            matrix B = new matrix(rows, columns, bentry);
            matrix C = new matrix(rows, columns, centry);
            matrix I = new matrix(rows, columns, ientry);

            for (int i = 1; i <= rows; i++) {
                I.setEntry(i, i, one);
            }

            B.entry.clear();
            for (int i = 0; i < I.entry.size(); i++) {
                B.entry.add(I.entry.get(i));
            }

            res = a;

            for (int i = 1; i <= rows; i++) {
                res = new BinaryFunction(FunctionOperation.MULT, res, new XFunction());
                C = A.multiply(B);
                q = C.trace();
                myI = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(new BigInteger(String.valueOf(i)).negate(), BigInteger.ONE)));
                q = FunctionArithmetics.divide(q, myI);
                b = a;
                b = FunctionArithmetics.multiply(b, q);
                res = FunctionArithmetics.add(res, b);
                for (int k = 1; k <= rows; k++) {
                    for (int j = 1; j <= columns; j++) {
                        B.setEntry(k, j, FunctionArithmetics.add(C.getEntry(k, j), FunctionArithmetics.multiply(q, I.getEntry(k, j))));
                    }
                }
            }
        }
        return res;
    }

    public MyFunction det() throws MyParserException {
        int clm = 1;
        int help = 0;
        int i;

        MyFunction factor, div, zero;

        MyFunction minusOne = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE.negate(), BigInteger.ONE)));
        MyFunction correct = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE, BigInteger.ONE)));

        for (int ln = 1; ln <= rows && ln <= columns; ln++) {

            //find a pivot
            while (clm <= columns) {
                help = ln;
                while (help < rows) {
                    if (!elemZero(help, clm)) {
                        break;
                    }
                    help++;
                }
                if (!elemZero(help, clm)) {
                    break;
                } else {
                    clm++;
                }
            }

            //if one has found a pivot, take it into the right line and eliminate all other entries of its column.
            if (help <= rows && clm <= columns) {

                if (help > ln) {
                    changeRows(help, ln);
                    correct = FunctionArithmetics.multiply(correct, minusOne);
                }

                div = getEntry(ln, clm);
                correct = FunctionArithmetics.multiply(correct, div);
                normRow(ln, clm);

                for (i = ln + 1; i <= rows; i++) {
                    if (!elemZero(i, clm)) addRows(i, ln, clm);
                }

                clm++;
            }
        }

        for (i = 1; i <= columns; i++) {
            correct = FunctionArithmetics.multiply(correct, getEntry(i, i));
        }
        return correct;
    }

    public int rank() throws MyParserException {
        matrix m = gauss();

        int lin = 1, col = 1;

        while (lin <= m.rows && col <= m.columns) {
            if (!m.elemZero(lin, col)) {
                lin++;
            } else {
                col++;
            }
        }

        lin--;
        return lin;
    }

    public matrix invert() throws MyParserException {

        ArrayList<MyFunction> hentry = new ArrayList<MyFunction>();
        for (int i = 0; i < rows * columns * 2; i++) {
            hentry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
        }

        ArrayList<MyFunction> ientry = new ArrayList<MyFunction>();
        for (int i = 0; i < rows * columns * 2; i++) {
            ientry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
        }

        matrix help = new matrix(rows, 2 * columns, hentry);
        matrix inverse = new matrix(rows, columns, ientry);

        for (int i = 1; i <= rows; i++) {
            help.setEntry(i, i + rows, new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE, BigInteger.ONE))));
        }

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= rows; j++) {
                help.setEntry(i, j, this.getEntry(i, j));
            }
        }

        help = help.gauss();
        if (rank() < rows)
            throw new MyParserException(MyParserException.type.NOTINVERTIBLEMATRIX, "The inserted matrix is not invertible.");

        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= rows; j++) {
                inverse.setEntry(i, j, help.getEntry(i, j + rows));
            }
        }


        return inverse;
    }

    public MyFunction miniPoly() throws MyParserException {
        int m = 1, diff = 1, rank, oldRank = 0, a = 1, b;
        MyFunction one = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE, BigInteger.ONE)));
        MyFunction test = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE, BigInteger.ONE)));
        MyFunction minusOne = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE.negate(), BigInteger.ONE)));
        MyFunction zero = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE)));

        ArrayList<MyFunction> bentry = new ArrayList<>();
        ArrayList<MyFunction> hentry = new ArrayList<>();
        for (int i = 0; i < rows * rows * (columns + 1); i++) {
            bentry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
        }

        ArrayList<MyFunction> ientry = new ArrayList<>();
        for (int i = 0; i < rows * columns; i++) {
            ientry.add(new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ZERO, BigInteger.ONE))));
        }

        matrix B = new matrix(rows * rows, 1 + columns, bentry);
        matrix I = new matrix(rows, columns, ientry);

        for (int i = 1; i <= rows; i++) {
            I.setEntry(i, i, one);
        }

        while (diff > 0 && m <= rows + 1) {
            for (int k = 1; k <= rows; k++) {
                for (int j = 1; j <= rows; j++) {
                    B.setEntry((rows) * (k - 1) + j, m, I.getEntry(k, j));
                }
            }

            hentry.clear();
            for (int k = 0; k < B.entry.size(); k++) {
                hentry.add(B.entry.get(k));
            }
            matrix help = new matrix(B.rows, B.columns, hentry);
            rank = help.rank();
            diff = rank - oldRank;

            oldRank = rank;
            I = I.multiply(this);
            m++;
        }

        B.gauss();

        a = 1;
        while ((!B.elemZero(a, a)) && a < B.rows) {
            a++;
        }

        b = a;
        MyFunction p = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE, BigInteger.ONE)));
        MyFunction q;

        while (b > 1) {
            p = new BinaryFunction(FunctionOperation.MULT, p, new XFunction());
            q = new ConstantFunction(new RationalStrategy(new ArbitraryPrecisionRational(BigInteger.ONE.negate(), BigInteger.ONE)));
            q = new BinaryFunction(FunctionOperation.MULT, q, B.getEntry(b - 1, a));
            p = new BinaryFunction(FunctionOperation.PLUS, p, q);
            b--;
        }


        return p;
    }

    public String print() throws MyParserException {

        String str, res = "<table border=\"1\"><tr><th><table border=\"0\">";
        for (int i = 1; i <= rows; i++) {
            res = res + "<tr>";
            for (int j = 1; j <= columns; j++) {

                res = res + "<th><span style=\"font-weight:200\"><font color=\"#0000FF\">";
                str = getEntry(i, j).print(6);
                res = res + str;
                res = res + "</font></span></th>";


            }
            res = res + "</tr>";
        }

        res = res + "</table></th></tr></table>";
        return res;
    }

    String interpreteAsSolution() throws MyParserException {

        ArbitraryPrecisionRational w;
        int r = rows + 1, c = columns;

        do {
            r--;
        } while (elemZero(r, columns) && r > 1);

        if (!elemZero(r, columns)) {
            do {
                c--;
            } while (elemZero(r, c) && c > 1);

            if (elemZero(r, c)) return " the empty set";
        }
        String qres = "<table><tr>";
        qres = qres + "<th><table border=1><tr><th><table border = 0>";

        r = 1;

        for (c = 1; c < columns; c++) {

            qres = qres + "<tr><th><span style=\"font-weight:200\">";
            if (r <= rows) {
                if (elemZero(r, c)) {
                    qres = qres + getEntry(r, c).print(6);
                } else {
                    qres = qres + getEntry(r, columns).print(6);
                    r++;
                }
            } else qres = qres + "0.000000";
            qres = qres + "</span></th></tr>";

        }

        qres = qres + "</table></th></tr></table></th>";

        int count = 1;
        int[] columnRef = new int[columns];
        for (c = 0; c < columns; c++) {
            columnRef[c] = 0;
        }

        r = 0;
        for (c = 1; c < columns; c++) {
            if (r == rows || elemZero(r + 1, c)) {
                qres = qres + "<th><table border=0><tr><th><span style=\"font-weight:200\"> + a<sub>";
                qres = qres + String.valueOf(count);
                qres = qres + "</sub></span></th></tr></table></th>";
                qres = qres + "<th><table border=1><tr><th><table border=0>";
                for (int s = 1; s < columns; s++) {
                    qres = qres + "<tr><th><span style=\"font-weight:200\">";

                    if (s == c) qres = qres + "-1.000000";
                    else if (columnRef[s] == 0) qres = qres + "0.000000";
                    else qres = qres + getEntry(columnRef[s], c).print(6);
                    qres = qres + "</span></th></tr>";
                }

                qres = qres + "</table></th></tr></table></th>";


                count++;
            } else {
                r++;
                columnRef[c] = r;
            }
        }


        qres = qres + "</tr></table>";
        return qres;
    }

    public String printAsSLE() throws MyParserException {
        String str, res = "<table border=\"1\"><tr><th><table border=\"0\">";
        for (int i = 1; i <= rows; i++) {
            res = res + "<tr>";
            for (int j = 1; j < columns; j++) {

                res = res + "<th><span style=\"font-weight:200\">";
                str = getEntry(i, j).print(6);
                res = res + str;
                res = res + "</span></th>";


            }
            res = res + "</tr>";
        }
        res = res + "</table></th><th><table border=\"0\">";
        for (int j = 1; j <= rows; j++) {
            res = res + "<tr><th><span style=\"font-weight:200\">";
            res = res + getEntry(j, columns).print(6);
            res = res + "</span></th></tr>";
        }
        res = res + "</table></th></tr></table>";
        return res;
    }

    private void debugPrint() throws MyParserException {
        System.out.println("--Matrix begin-------------------------------------");
        for (int i = 1; i <= rows; i++) {
            for (int j = 1; j <= columns; j++) {
                System.out.printf(getEntry(i, j).print(6));
                System.out.printf(" ");
            }
            System.out.printf("%n");
        }
        System.out.println("--Matrix end---------------------------------------");
    }
}
