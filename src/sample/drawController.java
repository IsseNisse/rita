package sample;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class drawController {

    @FXML
    private Canvas canvas;

    private int size = 10;

    private double anchor1X;
    private double anchor1Y;
    private double anchor2X;
    private double anchor2Y;

    private String drawFunction = "freeDraw";

    public void draw(javafx.scene.input.MouseEvent mouseEvent) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        if (drawFunction == "freeDraw") {
            freeDraw(gc, mouseX, mouseY);
        } else if (drawFunction == "drawLine") {
            drawLine(gc, mouseX, mouseY, mouseEvent);
        }
    }

    public void freeDraw(GraphicsContext gc, double mouseX, double mouseY) {
        drawFunction = "freeDraw";
        gc.setFill(Color.GREEN);
        gc.fillRect(mouseX - (size/2), mouseY - (size/2), size, size);
    }

    public void drawLine(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();

        System.out.println(eventType);
        if (!eventType.getName().equals("MOUSE_RELEASED")) {
            if (eventType.getName().equals("MOUSE_PRESSED")) {
                anchor1X = mouseX;
                anchor1Y = mouseY;
            }
        } else {
            anchor2X = mouseX;
            anchor2Y = mouseY;
            gc.strokeLine(anchor1X, anchor1Y, anchor2X, anchor2Y);
            gc.setLineWidth(size);
        }
    }

    public void drawLineBtn(ActionEvent actionEvent) {
        drawFunction = "drawLine";
    }

    public void size10(ActionEvent actionEvent) {
        size = 10;
    }

    public void size15(ActionEvent actionEvent) {
        size = 15;
    }

    public void size25(ActionEvent actionEvent) {
        size = 25;
    }
}
