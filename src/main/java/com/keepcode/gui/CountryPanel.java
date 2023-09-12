package com.keepcode.gui;

import com.keepcode.entity.AvailableNumber;
import com.keepcode.entity.Country;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

public class CountryPanel extends JPanel {

    public CountryPanel(Country country, List<AvailableNumber> availableNumbers, int position) {
        setLayout(new BorderLayout());

        JPanel countryNamePanel = new JPanel();
        countryNamePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel countryLabel = new JLabel(country.countryName());
        countryNamePanel.add(countryLabel);

        StyleUtils.styleLeftH3Text(countryLabel);

        countryNamePanel.setBackground(StyleUtils.getBackgroundColor(position));

        add(countryNamePanel, BorderLayout.NORTH);

        AvailableNumbersPanel availableNumbersPanel = new AvailableNumbersPanel(availableNumbers, position);
        availableNumbersPanel.setBackground(StyleUtils.getBackgroundColor(position));
        add(availableNumbersPanel, BorderLayout.CENTER);
    }
}