package sample;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import java.util.Stack;

public class drawController {

    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker colorPicker;

    private Stack<Image> savedImages = new Stack<>();

    private int size = 10;
    private Color color = Color.BLACK;

    private double anchor1X;
    private double anchor1Y;
    private double anchor2X;
    private double anchor2Y;

    private String drawFunction = "freeDraw";

    public void draw(javafx.scene.input.MouseEvent mouseEvent) {
        if (savedImages.empty()) {
            makeSnapshot();
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        if (drawFunction == "freeDraw") {
            freeDraw(gc, mouseX, mouseY, mouseEvent);
        } else if (drawFunction == "drawLine") {
            drawLine(gc, mouseX, mouseY, mouseEvent);
        }
    }

    public void freeDraw(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        if (!eventType.getName().equals("MOUSE_RELEASED")) {
            gc.setFill(color);
            gc.fillRect(mouseX - (size/2), mouseY - (size/2), size, size);
        } else {
            /* save snapshot */
            makeSnapshot();
        }
    }

    public void drawLine(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();

        if (!eventType.getName().equals("MOUSE_RELEASED")) {
            if (eventType.getName().equals("MOUSE_PRESSED")) {
                anchor1X = mouseX;
                anchor1Y = mouseY;
                makeSnapshot();
            } else if (eventType.getName().equals("MOUSE_DRAGGED")) {
                if (!savedImages.empty()) {
                    Image undoImage = savedImages.get(savedImages.size() - 1);
                    canvas.getGraphicsContext2D().drawImage(undoImage, 0, 0);
                }
                gc.setStroke(color);
                gc.setLineWidth(size);
                gc.strokeLine(anchor1X, anchor1Y, mouseX, mouseY);

            }
        } else {
            anchor2X = mouseX;
            anchor2Y = mouseY;
            gc.setStroke(color);
            gc.setLineWidth(size);
            gc.strokeLine(anchor1X, anchor1Y, anchor2X, anchor2Y);

            /* save snapshot */
            makeSnapshot();
        }
    }

    private void makeSnapshot() {
        Image snapshot = canvas.snapshot(null, null);
        savedImages.push(snapshot);
    }

    public void undo(ActionEvent actionEvent) {
        if (!savedImages.empty()) {
            Image undoImage = savedImages.pop();
            canvas.getGraphicsContext2D().drawImage(undoImage, 0, 0);
        }
    }

    public void colorPicker(ActionEvent actionEvent) {
        Color colorValue = colorPicker.getValue();
        color = Color.web(colorValue.toString());
    }


    /* Buttons for changing drawing type */

    public void freeDrawBtn(ActionEvent actionEvent) {
        drawFunction = "freeDraw";
    }

    public void drawLineBtn(ActionEvent actionEvent) {
        drawFunction = "drawLine";
    }


    /* Size buttons */

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
