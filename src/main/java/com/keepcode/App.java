package com.keepcode;

import com.keepcode.gui.Canvas;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.awt.EventQueue;

@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        //SpringApplication.run(App.class, args);

        var ctx = new SpringApplicationBuilder(App.class)
                .headless(false)
                .run(args);

        EventQueue.invokeLater(() -> {
            var canvas = ctx.getBean(Canvas.class);
            canvas.initUI();
            canvas.setVisible(true);
        });
    }
}
