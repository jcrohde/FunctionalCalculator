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

//import com.sun.org.apache.xpath.internal.operations.String;

import javax.swing.JDialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import javax.swing.border.Border;

class DialogGenerator {

    private static void showErrorMessage(final JDialog f, String Message) {
        JOptionPane.showMessageDialog(f,
                Message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    String result;

    public String exec(final int rows, final int columns, final FuParser parser) throws MyParserException {
        result = "";
        final CalcDialog matrixDialog = new CalcDialog();
        matrixDialog.setLayout(new BorderLayout(10, 10));
        matrixDialog.setIconImage(Toolkit.getDefaultToolkit().getImage("icons/FCLogo.png"));

        final ArrayList<JTextField> entry = new ArrayList<JTextField>();
        for (int i = 0; i < rows * columns; i++) {
            entry.add(new JTextField("0"));
        }

        final Panel Tabs = new Panel();
        Tabs.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < rows * columns; i++) {
            Tabs.add(entry.get(i), i);
        }

        Tabs.setPreferredSize(new Dimension(20 * columns, 20 * rows));
        Tabs.setMinimumSize(new Dimension(20 * columns, 20 * rows));
        Tabs.setMaximumSize(new Dimension(20 * columns, 20 * rows));

        Panel closePanel = new Panel();
        closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
        closePanel.add(Box.createHorizontalGlue());
        JButton okButton = new JButton("Ok");
        closePanel.add(okButton);
        closePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton noButton = new JButton("No");
        closePanel.add(noButton);
        closePanel.add(Box.createRigidArea(new Dimension(10, 0)));

        Panel BottomPanel = new Panel();
        BottomPanel.setLayout(new BorderLayout());
        BottomPanel.add(closePanel, BorderLayout.CENTER);
        BottomPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.SOUTH);

        matrixDialog.add(BottomPanel, BorderLayout.SOUTH);

        matrixDialog.add(Tabs, BorderLayout.CENTER);
        matrixDialog.add(BottomPanel, BorderLayout.SOUTH);

        matrixDialog.setTitle("Matrices");
        matrixDialog.setSize(60 * columns, 40 * rows + 40);

        noButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {
                                           result = null;
                                           matrixDialog.dispose();
                                       }
                                   }
        );

        okButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {

                                           ArrayList<ArbitraryPrecisionRational> matrixEntry = new ArrayList<ArbitraryPrecisionRational>();
                                           String eStr;
                                           result = "matrix(" + rows + "," + columns;
                                           ArbitraryPrecisionRational r;
                                           for (int i = 0; i < rows * columns; i++) {
                                               result += "," + entry.get(i).getText();
                                           }
                                           result += ")";
                                           matrixDialog.dispose();
                                       }
                                   }
        );

        matrixDialog.setModal(true);
        matrixDialog.setVisible(true);

        return result;
    }

    private matrix res;

    public matrix system(final int equations, final int variables, final FuParser parser) throws MyParserException {
        final JDialog matrixDialog = new JDialog();
        matrixDialog.setLayout(new BorderLayout(10, 10));

        matrixDialog.setIconImage(Toolkit.getDefaultToolkit().getImage("icons/FCLogo.png"));

        final ArrayList<JTextField> entry = new ArrayList<JTextField>();
        for (int i = 0; i < equations * (variables + 1); i++) {
            entry.add(new JTextField("0"));
        }

        final Panel Tabs = new Panel();
        Tabs.setLayout(new GridLayout(equations, 2 * (variables + 1)));
        for (int i = 0; i < equations; i++) {
            for (int j = 0; j < 2 * (variables + 1); j++) {
                if (j == 0)
                    Tabs.add(new JLabel("<html>a<sub>1</sub></html>", SwingConstants.CENTER), i * 2 * (variables + 1));
                else if (j == 2 * variables)
                    Tabs.add(new JLabel(" = ", SwingConstants.CENTER), i * 2 * (variables + 1) + j);
                else if (j % 2 == 0) {
                    String str = "<html> + a<sub>" + String.valueOf(j / 2 + 1) + "</sub></html>";
                    Tabs.add(new JLabel(str, SwingConstants.CENTER), i * 2 * (variables + 1) + j);
                } else Tabs.add(entry.get(i * (variables + 1) + (j - 1) / 2), i * 2 * (variables + 1) + j);
            }
        }

        Panel closePanel = new Panel();
        closePanel.setLayout(new BoxLayout(closePanel, BoxLayout.LINE_AXIS));
        closePanel.add(Box.createHorizontalGlue());
        JButton okButton = new JButton("Ok");
        closePanel.add(okButton);
        closePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        JButton noButton = new JButton("No");
        closePanel.add(noButton);
        closePanel.add(Box.createRigidArea(new Dimension(10, 0)));

        Panel BottomPanel = new Panel();
        BottomPanel.setLayout(new BorderLayout());
        BottomPanel.add(closePanel, BorderLayout.CENTER);
        BottomPanel.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.SOUTH);

        matrixDialog.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.NORTH);
        matrixDialog.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.EAST);
        matrixDialog.add(Tabs, BorderLayout.CENTER);
        matrixDialog.add(BottomPanel, BorderLayout.SOUTH);

        matrixDialog.setTitle("Matrices");
        matrixDialog.setSize(2 * 40 * (variables + 1) + 30, 35 * equations + 70);

        noButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {
                                           result = null;
                                           matrixDialog.dispose();
                                       }
                                   }
        );

        okButton.addActionListener(new ActionListener() {
                                       @Override
                                       public void actionPerformed(ActionEvent event) {

                                           ArrayList<MyFunction> matrixEntry = new ArrayList<>();
                                           String eStr;
                                           MyFunction f;
                                           try {
                                               for (int i = 0; i < equations * (variables + 1); i++) {
                                                   eStr = entry.get(i).getText();
                                                   f = parser.parseFunction(eStr);
                                                   matrixEntry.add(f);
                                               }
                                               res = new matrix(equations, variables + 1, matrixEntry);

                                               matrixDialog.dispose();
                                           } catch (MyParserException e) {
                                               showErrorMessage(matrixDialog, e.message);
                                           }
                                       }
                                   }
        );

        matrixDialog.setModal(true);
        matrixDialog.setVisible(true);

        return res;
    }
}
