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

import java.math.BigInteger;

public class MatrixAdapter extends MyFunction {

    public MatrixAdapter(matrix value) {
        this.value = value;
    }

    public matrix value;

    public MyFunction symmetry() throws MyParserException {
        return new ConstantFunction(new IntegerStrategy(new BigInteger("-1")));
    }

    public ValueStrategy eval(double d) throws MyParserException {
        return new DoubleStrategy(0);
    }

    public ComplexStrategy eval(double re, double im) throws MyParserException {
        return new ComplexStrategy(0, 0);
    }

    protected String printFormat(int precision) throws MyParserException {
        return value.print();
    }

    public boolean isConstant() {
        boolean isconst = true;
        for (int i = 1; i <= value.rows() && isconst; i++) {
            for (int j = 1; j <= value.columns() && isconst; j++) {
                isconst = value.getEntry(i, j).isConstant();
            }
        }
        return isconst;
    }

    public MyFunction simplify() throws MyParserException {
        return this;
    }

    public MyFunction getDerivative() throws MyParserException {
        return this;
    }

    public int precedense() {
        return 0;
    }

    public MyFunction extractNumber(FunctionOperation A, FunctionOperation B) {
        return this;
    }

    public MyFunction extractNonConstant(FunctionOperation A, FunctionOperation B) {
        return null;
    }

    protected FunctionOperation ConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected FunctionOperation NonConstantOp(FunctionOperation A, FunctionOperation B) {
        return A;
    }

    protected MyFunction extractNumbers(FunctionOperation A, FunctionOperation B) throws MyParserException {
        return this;
    }

    public String print(int precision) throws MyParserException {
        return value.print();
    }

    public MatrixAdapter add(MatrixAdapter ma) throws MyParserException {
        return new MatrixAdapter(value.add(ma.value));
    }

    public MatrixAdapter subtract(MatrixAdapter ma) throws MyParserException {
        return new MatrixAdapter(value.subtract(ma.value));
    }

    public MatrixAdapter multiply(MatrixAdapter ma) throws MyParserException {
        return new MatrixAdapter(value.multiply(ma.value));
    }

    public MyFunction compose(MyFunction f) throws MyParserException {
        matrix m = new matrix(value.rows(), value.columns());
        for (int i = 1; i <= value.rows(); i++) {
            for (int j = 1; j <= value.columns(); j++) {
                m.setEntry(i, j, value.getEntry(i, j).compose(f));
            }
        }
        return new MatrixAdapter(m);
    }
}
