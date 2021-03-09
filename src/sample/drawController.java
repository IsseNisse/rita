package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Stack;

public class drawController {

    @FXML
    private Canvas canvas;
    private Stack<Image> savedImages = new Stack<>();

    private int size = 10;

    private double anchor1X;
    private double anchor1Y;
    private double anchor2X;
    private double anchor2Y;

    private String drawFunction = "freeDraw";

    public void draw(javafx.scene.input.MouseEvent mouseEvent) {
        if (savedImages.empty()) {
            Image snapshot = canvas.snapshot(null, null);
            savedImages.push(snapshot);
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
            gc.setFill(Color.GREEN);
            gc.fillRect(mouseX - (size/2), mouseY - (size/2), size, size);
        } else {
            /* save snapshot */
            Image snapshot = canvas.snapshot(null, null);
            savedImages.push(snapshot);
        }
    }

    public void drawLine(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();

        if (!eventType.getName().equals("MOUSE_RELEASED")) {
            if (eventType.getName().equals("MOUSE_PRESSED")) {
                anchor1X = mouseX;
                anchor1Y = mouseY;
            }
        } else {
            anchor2X = mouseX;
            anchor2Y = mouseY;
            gc.setLineWidth(size);
            gc.strokeLine(anchor1X, anchor1Y, anchor2X, anchor2Y);

            /* save snapshot */
            Image snapshot = canvas.snapshot(null, null);
            savedImages.push(snapshot);
        }
    }

    public void undo(ActionEvent actionEvent) {
        if (!savedImages.empty()) {
            Image undoImage = savedImages.pop();
            canvas.getGraphicsContext2D().drawImage(undoImage, 0, 0);
        }
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
