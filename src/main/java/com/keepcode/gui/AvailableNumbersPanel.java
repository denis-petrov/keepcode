package com.keepcode.gui;

import com.keepcode.entity.AvailableNumber;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.util.List;

public class AvailableNumbersPanel extends JPanel {
    private final List<AvailableNumber> numbers;

    public AvailableNumbersPanel(List<AvailableNumber> numbers, int position) {
        this.numbers = numbers;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        if (numbers.isEmpty()) {
            var noNumbersLabel = new JLabel("Not found available numbers");
            noNumbersLabel.setHorizontalAlignment(SwingConstants.LEFT);
            add(noNumbersLabel);
        } else {
            for (AvailableNumber number : numbers) {
                var numberLabel = new JLabel(
                        number.fullNumber() + " - " + number.status() + " - " + number.countryText()
                );
                numberLabel.setHorizontalAlignment(SwingConstants.LEFT);

                numberLabel.setBackground(StyleUtils.getBackgroundColor(position));

                numberLabel.setOpaque(true);
                add(numberLabel);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int elementHeight = 30;
        int height = numbers.isEmpty() ? elementHeight : numbers.size() * elementHeight;
        return new Dimension(super.getPreferredSize().width, height);
    }
}
