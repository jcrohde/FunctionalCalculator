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

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Point {
    public int xb, yb;
    Color color;

    public Point(int xb, int yb, Color color) {
        this.xb = xb;
        this.yb = yb;
        this.color = color;
    }
}

class ComplexPlotCanvas extends Canvas {

    ArrayList<Point> points;

    double xBeg, xEnd, yBeg, yEnd;

    public ComplexPlotCanvas() {
        super();
        points = new ArrayList<>();
        xBeg = -10;
        xEnd = 10;
        yBeg = -10;
        yEnd = 10;

        points.clear();
        for (int i = 0; i < 400; i++) {
            double x = getXCoordinate(i);
            for (int j = 0; j < 400; j++) {
                double y = getYCoordinate(j);
                float h = (float) Math.atan2(y, x);
                if (h < 0) h += 2 * Math.PI;
                if (h < 0) h = 0;
                if (h > 2 * Math.PI) h = 2 * ((float) Math.PI);
                h = h / (2 * ((float) Math.PI));
                double norm = Math.sqrt(x * x + y * y);
                float b = (float) Math.exp(-(norm - (int) norm) * Math.log(2));
                points.add(new Point(i, j, Color.getHSBColor(h, 1, b)));
            }
        }
    }

    public void paint(final Graphics g) {
        super.paint(g);

        for (Point p : points) {
            g.setColor(p.color);
            g.fillRect(p.xb, p.yb, 1, 1);
        }
    }

    public double getXCoordinate(int i) {
        return xBeg + i * (xEnd - xBeg) / 400;
    }

    public double getYCoordinate(int i) {
        return yEnd + i * (yBeg - yEnd) / 400;
    }
}

class ComplexPlotDialogGenerator {

    private static void showErrorMessage(final JDialog f, String Message) {
        JOptionPane.showMessageDialog(f,
                Message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void exec(final FuParser parser, String function) {
        final CalcDialog dialog = new CalcDialog();

        final Color functionColor = Color.BLUE;
        final String functionName = "f";

        dialog.setTitle("Plot");
        dialog.setLayout(new BorderLayout());


        Panel cPanel = new Panel();
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.LINE_AXIS));
        final ComplexPlotCanvas myCanvas = new ComplexPlotCanvas();
        myCanvas.setSize(400, 400);
        cPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        cPanel.add(myCanvas);
        dialog.add(cPanel, BorderLayout.EAST);

        Panel left = new Panel();
        left.setLayout(new java.awt.GridLayout(7, 2));

        final JTextField inPut = new JTextField(function);
        final Panel functionPanel = new Panel();
        final JLabel label = new JLabel("<html><font color=rgb(" + String.valueOf(functionColor.getRed()) + "," +
                String.valueOf(functionColor.getGreen()) + "," + String.valueOf(functionColor.getBlue()) + ")>" + functionName + "(x)</font>" + " = </html>");
        inPut.add(new JTextField());
        functionPanel.add(new Panel());

        Panel control = new Panel();
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
        control.add(Box.createRigidArea(new Dimension(0, 10)));

        functionPanel.setLayout(new BoxLayout(functionPanel, BoxLayout.X_AXIS));
        functionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        functionPanel.add(label);
        functionPanel.add(Box.createHorizontalGlue());
        functionPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        functionPanel.add(inPut);
        inPut.setColumns(21);
        control.add(functionPanel);

        control.add(Box.createRigidArea(new Dimension(0, 200)));

        final JTextField xBeg = new JTextField("-10");
        final JTextField xEnd = new JTextField("10");
        final JTextField yBeg = new JTextField("-10");
        final JTextField yEnd = new JTextField("10");

        Panel xAxis = new Panel();
        xAxis.setLayout(new BoxLayout(xAxis, BoxLayout.LINE_AXIS));
        xAxis.add(Box.createRigidArea(new Dimension(10, 0)));
        xAxis.add(new JLabel(" x axis range"));
        xAxis.add(Box.createRigidArea(new Dimension(10, 0)));
        xAxis.add(xBeg);
        xAxis.add(xEnd);

        Panel yAxis = new Panel();
        yAxis.setLayout(new BoxLayout(yAxis, BoxLayout.LINE_AXIS));
        yAxis.add(Box.createRigidArea(new Dimension(10, 0)));
        yAxis.add(new JLabel(" y axis range"));
        yAxis.add(Box.createRigidArea(new Dimension(10, 0)));
        yAxis.add(yBeg);
        yAxis.add(yEnd);

        control.add(xAxis);
        control.add(yAxis);
        control.add(Box.createRigidArea(new Dimension(0, 10)));

        Panel closePanel = new Panel();
        closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
        closePanel.add(Box.createHorizontalGlue());
        JButton okButton = new JButton("Ok");
        closePanel.add(okButton);
        closePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton noButton = new JButton("Close");
        closePanel.add(noButton);

        control.add(closePanel);
        control.add(Box.createRigidArea(new Dimension(0, 10)));

        dialog.add(control, BorderLayout.CENTER);

        okButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {
                                           //myCanvas.lines.clear();

                                           myCanvas.xBeg = Double.parseDouble(xBeg.getText());
                                           myCanvas.xEnd = Double.parseDouble(xEnd.getText());
                                           myCanvas.yBeg = Double.parseDouble(yBeg.getText());
                                           myCanvas.yEnd = Double.parseDouble(yEnd.getText());

                                           try {
                                               String eStr = inPut.getText();

                                               MyFunction f = parser.parsePlot(eStr);
                                               ComplexStrategy c;

                                               myCanvas.points.clear();
                                               for (int i = 0; i < 400; i++) {
                                                   double x = myCanvas.getXCoordinate(i);
                                                   for (int j = 0; j < 400; j++) {
                                                       double y = myCanvas.getYCoordinate(j);
                                                       c = f.eval(x, y);
                                                       float h = (float) Math.atan2(c.im(), c.re());
                                                       if (h < 0) h += 2 * Math.PI;
                                                       if (h < 0) h = 0;
                                                       if (h > 2 * Math.PI) h = 2 * ((float) Math.PI);
                                                       h = h / (2 * ((float) Math.PI));
                                                       double norm = Math.sqrt(c.re() * c.re() + c.im() * c.im());
                                                       float b = (float) Math.exp(-(norm - (int) norm) * Math.log(2));
                                                       myCanvas.points.add(new Point(i, j, Color.getHSBColor(h, 1, b)));
                                                   }
                                               }
                                           } catch (MyParserException e) {
                                               showErrorMessage(dialog, "In function " + functionName + ": " + e.message);
                                           } catch (Exception ex) {
                                               showErrorMessage(dialog, "Syntax Error in function " + functionName);
                                           }
                                           myCanvas.repaint();
                                       }
                                   }
        );

        noButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {
                                           dialog.dispose();
                                       }
                                   }
        );

        dialog.setModal(false);
        dialog.pack();
        dialog.setResizable(false);
        okButton.doClick();
        dialog.setVisible(true);
    }

}
