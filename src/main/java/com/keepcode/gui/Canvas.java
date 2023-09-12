package com.keepcode.gui;

import com.keepcode.entity.AvailableNumber;
import com.keepcode.entity.Country;
import com.keepcode.state.AppState;
import com.keepcode.state.AppStateChangeEvent;
import com.keepcode.storage.InMemoryMapListStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.GridLayout;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class Canvas extends JFrame {

    private final InMemoryMapListStorage<Country, AvailableNumber> inMemoryStorage;
    private Optional<URL> gifLoadingResource = Optional.empty();

    @Autowired
    public Canvas(InMemoryMapListStorage<Country, AvailableNumber> inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @EventListener
    public void handleAppStateChange(AppStateChangeEvent event) {
        AppState state = event.getNewState();
        if (state == AppState.LOADING) {
            SwingUtilities.invokeLater(this::displayLoading);
        }
        if (state == AppState.DATA_AVAILABLE) {
            SwingUtilities.invokeLater(this::updateCanvasData);
        }
        if (state == AppState.ERROR) {
            SwingUtilities.invokeLater(this::displayErrorMessage);
        }
    }

    public void initUI() {
        setTitle("Keep Code");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 800);

        setLayout(new GridLayout(0, 1));

        findLoadingGif();
        displayLoading();

        setLocationRelativeTo(null);
    }

    private void findLoadingGif() {
        try {
            gifLoadingResource = Optional.of(getClass().getResource("/gui/loading.gif"));
        } catch (Exception e) {
            gifLoadingResource = Optional.empty();
        }
    }

    private void updateCanvasData() {
        Map<Country, List<AvailableNumber>> updatedData = inMemoryStorage.getStorage();
        getContentPane().removeAll();

        JPanel countryPanelContainer = new JPanel();
        countryPanelContainer.setLayout(new BoxLayout(countryPanelContainer, BoxLayout.Y_AXIS));

        var position = 0;
        for (Map.Entry<Country, List<AvailableNumber>> entry : updatedData.entrySet()) {
            var country = entry.getKey();
            var numbers = entry.getValue();
            CountryPanel countryPanel = new CountryPanel(country, numbers, position);
            countryPanelContainer.add(countryPanel);
            position++;
        }

        JScrollPane scrollPane = new JScrollPane(countryPanelContainer);
        add(scrollPane);

        revalidate();
        repaint();
    }

    private void displayLoading() {
        getContentPane().removeAll();

        if (gifLoadingResource.isPresent()) {
            var loadingGif = new JLabel(new ImageIcon(gifLoadingResource.get()));
            loadingGif.setHorizontalAlignment(SwingConstants.CENTER);
            add(loadingGif);
        } else {
            displayLoadingMessage();
        }

        revalidate();
        repaint();
    }

    private void displayLoadingMessage() {
        displayMainCenterText("Data updating, please wait.");
    }

    private void displayErrorMessage() {
        displayMainCenterText("An error has occurred. Please wait.");
    }

    private void displayMainCenterText(String msg) {
        var msgElem = new JLabel(msg);
        StyleUtils.styleCenterH1Text(msgElem);
        add(msgElem);

        revalidate();
        repaint();
    }
}
