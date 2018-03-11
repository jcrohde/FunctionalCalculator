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
import javax.swing.border.Border;

class line {
    public int xb, xe, yb, ye;
    Color color;

    public line(int xb, int yb, int xe, int ye, Color color) {
        this.xb = xb;
        this.xe = xe;
        this.yb = yb;
        this.ye = ye;
        this.color = color;
    }
}

class plotCanvas extends Canvas {

    public ArrayList<line> lines;

    double xBeg, xEnd, yBeg, yEnd;

    public plotCanvas() {
        super();
        lines = new ArrayList<line>();
        xBeg = -10;
        xEnd = 10;
        yBeg = -10;
        yEnd = 10;
    }

    public void paint(final Graphics g) {
        super.paint(g);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 400, 400);

        g.setColor(Color.BLACK);
        g.drawLine(yAxis(), 0, yAxis(), 400);
        g.drawLine(0, xAxis(), 400, xAxis());


        double xDist = xEnd - xBeg;
        double xForm = Math.log(xDist) / Math.log(10.0);
        if (xForm < 0.0) xForm -= 1.0;
        int xFormI = (int) xForm;
        xFormI--;
        xForm = (double) xFormI;
        if (xForm > 0.0) xForm = Math.pow(10, xForm);
        else xForm = 1.0 / Math.pow(10, -xForm);
        double help = 0.0;
        int count = 0;
        while (help < xEnd) {
            help += xForm;
            count++;
            drawXScale(g, help, count);
        }
        help = 0.0;
        count = 0;
        while (help > xBeg) {
            help -= xForm;
            count++;
            drawXScale(g, help, count);
        }

        double yDist = yEnd - yBeg;
        double yForm = Math.log(yDist) / Math.log(10.0);
        if (yForm < 0.0) yForm -= 1.0;
        int yFormI = (int) yForm;
        yFormI--;
        yForm = (double) yFormI;
        if (yForm > 0.0) yForm = Math.pow(10, yForm);
        else yForm = 1.0 / Math.pow(10, -yForm);
        help = 0.0;
        count = 0;
        while (help < yEnd) {
            help += yForm;
            count++;
            drawYScale(g, help, count);
        }
        help = 0.0;
        count = 0;
        while (help > yBeg) {
            help -= yForm;
            count++;
            drawYScale(g, help, count);
        }

        for (line l : lines) {
            g.setColor(l.color);
            g.drawLine(l.xb, l.yb, l.xe, l.ye);
        }
    }

    private void drawXScale(final Graphics g, double help, int count) {
        double res = (help * 400 / (-xBeg + xEnd)) - (xBeg * 400 / (-xBeg + xEnd));
        int k = (int) res;
        int j = xAxis();
        if (count % 5 != 0) g.drawLine(k, j, k, j + 6);
        else if (count % 10 != 0) g.drawLine(k, j + 9, k, j);
        else g.drawLine(k, j + 12, k, j);
    }

    private void drawYScale(final Graphics g, double help, int count) {
        double res = (help * 400 / (yBeg - yEnd)) - (yEnd * 400 / (yBeg - yEnd));
        int k = (int) res;
        int j = yAxis();
        if (count % 5 != 0) g.drawLine(j, k, j - 6, k);
        else if (count % 10 != 0) g.drawLine(j - 9, k, j, k);
        else g.drawLine(j - 12, k, j, k);
    }

    private int yAxis() {
        return (int) ((400 * (-xBeg)) / (xEnd - xBeg));
    }

    private int xAxis() {
        return (int) ((400 * (-yEnd)) / (yBeg - yEnd));
    }
}

class plotDialogGenerator {

    private static void showErrorMessage(final JDialog f, String Message) {
        JOptionPane.showMessageDialog(f,
                Message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public void exec(final FuParser parser, String fs, String gs, String hs, String is) {
        final CalcDialog dialog = new CalcDialog();

        final Color functionColors[] = {Color.BLUE, Color.RED, Color.GREEN, Color.PINK, Color.CYAN, Color.ORANGE, Color.GRAY};
        final String functionNames[] = {"f", "g", "h", "i", "j", "k", "l"};

        dialog.setTitle("Plot");
        dialog.setLayout(new BorderLayout());


        Panel cPanel = new Panel();
        cPanel.setLayout(new BoxLayout(cPanel, BoxLayout.LINE_AXIS));
        final plotCanvas myCanvas = new plotCanvas();
        myCanvas.setSize(400, 400);
        cPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        cPanel.add(myCanvas);
        dialog.add(cPanel, BorderLayout.EAST);

        Panel left = new Panel();
        left.setLayout(new java.awt.GridLayout(7, 2));

        final ArrayList<JTextField> inPut = new ArrayList<JTextField>();
        final ArrayList<JLabel> labels = new ArrayList<JLabel>();
        final ArrayList<Panel> functionPanels = new ArrayList<Panel>();
        for (int i = 0; i < 7; i++) {
            labels.add(new JLabel("<html><font color=rgb(" + String.valueOf(functionColors[i].getRed()) + "," +
                    String.valueOf(functionColors[i].getGreen()) + "," + String.valueOf(functionColors[i].getBlue()) + ")>" + functionNames[i] + "(x)</font>" + " = </html>"));
            inPut.add(new JTextField());
            functionPanels.add(new Panel());
        }
        inPut.get(0).setText(fs);
        inPut.get(1).setText(gs);
        inPut.get(2).setText(hs);
        inPut.get(3).setText(is);

        Panel control = new Panel();
        control.setLayout(new BoxLayout(control, BoxLayout.Y_AXIS));
        control.add(Box.createRigidArea(new Dimension(0, 10)));

        for (int i = 0; i < 7; i++) {
            functionPanels.get(i).setLayout(new BoxLayout(functionPanels.get(i), BoxLayout.X_AXIS));
            functionPanels.get(i).add(Box.createRigidArea(new Dimension(10, 0)));
            functionPanels.get(i).add(labels.get(i));
            functionPanels.get(i).add(Box.createHorizontalGlue());
            functionPanels.get(i).add(Box.createRigidArea(new Dimension(10, 0)));
            functionPanels.get(i).add(inPut.get(i));
            inPut.get(i).setColumns(21);
            control.add(functionPanels.get(i));
        }

        control.add(Box.createRigidArea(new Dimension(0, 10)));

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
                                           myCanvas.lines.clear();

                                           myCanvas.xBeg = Double.parseDouble(xBeg.getText());
                                           myCanvas.xEnd = Double.parseDouble(xEnd.getText());
                                           myCanvas.yBeg = Double.parseDouble(yBeg.getText());
                                           myCanvas.yEnd = Double.parseDouble(yEnd.getText());

                                           for (int j = 0; j < 7; j++) {
                                               try {
                                                   String eStr = inPut.get(j).getText();
                                                   if (eStr.length() > 0) {
                                                       MyFunction f = parser.parsePlot(eStr);
                                                       double d, e;
                                                       int k, l;
                                                       if (f instanceof MatrixAdapter)
                                                           throw new MyParserException(MyParserException.type.NOTDEFINEDATPLACE, "Can not plot matrices here");

                                                       for (int i = 0; i < 400; i++) {
                                                           d = f.eval(((myCanvas.xEnd - myCanvas.xBeg) / 400) * i + myCanvas.xBeg).getDouble();
                                                           k = (int) ((400 / (myCanvas.yBeg - myCanvas.yEnd)) * (d - myCanvas.yEnd));//    (200-d*20);
                                                           e = f.eval(((myCanvas.xEnd - myCanvas.xBeg) / 400) * (i + 1) + myCanvas.xBeg).getDouble();
                                                           l = (int) ((400 / (myCanvas.yBeg - myCanvas.yEnd)) * (e - myCanvas.yEnd));
                                                           if (!Double.isNaN(d) && !Double.isNaN(e))
                                                               myCanvas.lines.add(new line(i, k, i + 1, l, functionColors[j]));
                                                       }
                                                   }
                                               } catch (MyParserException e) {
                                                   showErrorMessage(dialog, "In function " + functionNames[j] + ": " + e.message);
                                               } catch (Exception ex) {
                                                   showErrorMessage(dialog, "Syntax Error in function " + functionNames[j]);
                                               }
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
