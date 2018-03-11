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

public class FunctionalString extends MyFunction {

    public FunctionalString(String content) {
        this.content = content;
    }

    private String content;

    @Override
    public MyFunction symmetry() throws MyParserException {
        return new ConstantFunction(new IntegerStrategy(BigInteger.ZERO));
    }

    public ValueStrategy eval(double d) throws MyParserException {
        return new DoubleStrategy(0);
    }

    static public String completeRelativePath(String path) {
        if (!path.isEmpty() && path.charAt(0) != '/' ) {
            java.nio.file.Path currentRelativePath = java.nio.file.Paths.get("");
            String s = currentRelativePath.toAbsolutePath().toString();
            return s + "/" + path;
        }
        else
            return path;
    }

    public ComplexStrategy eval(double re, double im) throws MyParserException {
        return new ComplexStrategy(0, 0);
    }

    protected String printFormat(int precision) throws MyParserException {
        return content;
    }

    public boolean isConstant() {
        return true;
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
        return content;
    }

    public MyFunction compose(MyFunction f) throws MyParserException {
        return new FunctionalString(content + f.print(6));
    }
}
