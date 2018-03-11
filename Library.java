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

public class Library {
    static public FunctionType getType(String str) {
        if (str.equals("sin")) return FunctionType.SIN;
        else if (str.equals("exp")) return FunctionType.EXP;
        else if (str.equals("ln")) return FunctionType.LN;
        else if (str.equals("tan")) return FunctionType.TAN;
        else if (str.equals("cot")) return FunctionType.COT;
        else if (str.equals("sqrt")) return FunctionType.SQRT;
        else if (str.equals("atan")) return FunctionType.ATAN;
        else if (str.equals("asin")) return FunctionType.ASIN;
        else if (str.equals("acos")) return FunctionType.ACOS;
        else if (str.equals("cosh")) return FunctionType.COSH;
        else if (str.equals("sinh")) return FunctionType.SINH;
        else if (str.equals("tanh")) return FunctionType.TANH;
        else if (str.equals("cubeRoot")) return FunctionType.CUBEROOT;
        else if (str.equals("derivative")) return FunctionType.DER;
        else if (str.equals("nextPrime")) return FunctionType.NEXTPRIME;
        else if (str.equals("prime")) return FunctionType.PRIME;
        else if (str.equals("phi")) return FunctionType.PHI;
        else if (str.equals("gamma")) return FunctionType.GAMMA;
        else if (str.equals("digamma")) return FunctionType.DIGAMMA;
        else if (str.equals("trigamma")) return FunctionType.TRIGAMMA;
        else if (str.equals("cos")) return FunctionType.COS;
        else if (str.equals("det")) return FunctionType.DET;
        else if (str.equals("minipoly")) return FunctionType.MINIPOLY;
        else if (str.equals("charpoly")) return FunctionType.CHARPOLY;
        else if (str.equals("rank")) return FunctionType.RANK;
        else if (str.equals("trace")) return FunctionType.TRACE;
        else if (str.equals("gauss")) return FunctionType.GAUSS;
        else if (str.equals("invert")) return FunctionType.INVERTMATRIX;
        else if (str.equals("readFile")) return FunctionType.READFILE;
        else if (str.equals("deleteFile")) return FunctionType.DELETEFILE;
        else if (str.equals("symmetry")) return FunctionType.SYMMETRY;
        else if (str.equals("plotComplex")) return FunctionType.PLOTCOMPLEX;
        else if (str.equals("ceil")) return FunctionType.CEIL;
        else if (str.equals("floor")) return FunctionType.FLOOR;
        else if (str.equals("round")) return FunctionType.ROUND;
        else if (str.equals("abs")) return FunctionType.ABS;
        else if (str.equals("frac")) return FunctionType.FRAC;
        else if (str.equals("conj")) return FunctionType.CONJ;
        else if (str.equals("arg")) return FunctionType.ARG;
        else if (str.equals("re")) return FunctionType.RE;
        else if (str.equals("im")) return FunctionType.IM;
        else if (str.equals("log2")) return FunctionType.LOG2;
        else if (str.equals("isconst")) return FunctionType.ISCONST;
        else return null;
    }

    static public FunctionOperation getBinaryFunction(String str) {
        if (str.equals("eval")) return FunctionOperation.EVAL;
        else if (str.equals("bessel1")) return FunctionOperation.BESSEL1;
        else if (str.equals("bessel2")) return FunctionOperation.BESSEL2;
        else if (str.equals("tangent")) return FunctionOperation.TANGENT;
        else if (str.equals("tangent")) return FunctionOperation.TANGENT;
        else if (str.equals("normal")) return FunctionOperation.NORMAL;
        else if (str.equals("gcd")) return FunctionOperation.GCD;
        else if (str.equals("lcm")) return FunctionOperation.LCM;
        else if (str.equals("log")) return FunctionOperation.LOG;
        else if (str.equals("max")) return FunctionOperation.MAX;
        else if (str.equals("min")) return FunctionOperation.MIN;
        else if (str.equals("writeFile")) return FunctionOperation.WRITEFILE;
        else if (str.equals("binomial")) return FunctionOperation.BINOMIAL;
        else return FunctionOperation.NONE;
    }
}
