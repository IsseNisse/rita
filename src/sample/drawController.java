package sample;

import com.sun.javafx.geom.Path2D;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class drawController {

    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker colorPicker;

    private static final Stack<Image> savedImages = new Stack<>();
    private final Stack<Image> savedLines = new Stack<>();

    private int size = 10;
    private Color color = Color.BLACK;

    private double anchor1X;
    private double anchor1Y;
    private double anchor2X;
    private double anchor2Y;

    private String drawFunction = "freeDraw";



    public void draw(javafx.scene.input.MouseEvent mouseEvent) {

        if (savedImages.empty()) {
            makeSnapshot(savedImages);
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        if (drawFunction.equals("freeDraw")) {
            freeDraw(gc, mouseX, mouseY, mouseEvent);
        } else if (drawFunction.equals("drawLine")) {
            drawLine(gc, mouseX, mouseY, mouseEvent);
        } else if (drawFunction.equals("fill")) {
            fill(gc, mouseX, mouseY, mouseEvent);
        }
    }

    public void freeDraw(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        if (!eventType.getName().equals("MOUSE_RELEASED")) {
            gc.setFill(color);
            gc.fillRect(mouseX - (size/2), mouseY - (size/2), size, size);
        } else {
            /* save snapshot */
            makeSnapshot(savedImages);
        }
    }

    public void drawLine(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();

        if (!eventType.getName().equals("MOUSE_RELEASED")) {
            if (eventType.getName().equals("MOUSE_PRESSED")) {
                anchor1X = mouseX;
                anchor1Y = mouseY;
                makeSnapshot(savedLines);
            } else if (eventType.getName().equals("MOUSE_DRAGGED")) {
                if (!savedLines.empty()) {
                    Image undoImage = savedLines.get(savedLines.size() - 1);
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
            makeSnapshot(savedImages);
        }
    }

    private void fill(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        ArrayList<Coordinate> toCheck = new ArrayList<>();
        HashSet<Coordinate> toColor = new HashSet<>();
        HashSet<Coordinate> checked = new HashSet<>();
        Coordinate startCoordinate = new Coordinate((int)mouseX, (int)mouseY);
        toCheck.add(startCoordinate);

        if (eventType.getName().equals("MOUSE_PRESSED")) {
            Image latestSave = savedImages.get(savedImages.size() - 1);
            PixelReader pixelReader = latestSave.getPixelReader();
            Color colorToChange = pixelReader.getColor((int)mouseX, (int)mouseY);

            while (!toCheck.isEmpty()) {
                Coordinate check = toCheck.get(toCheck.size() - 1);
                Color pixelColor = pixelReader.getColor(check.getX(), check.getY());
                System.out.println(pixelColor);
                if (pixelColor == colorToChange) {
                    toColor.add(check);
                }

                Coordinate co1 = new Coordinate(check.getX() + 1, check.getY());
                Coordinate co2 = new Coordinate(check.getX() - 1, check.getY());
                Coordinate co3 = new Coordinate(check.getX(), check.getY() + 1);
                Coordinate co4 = new Coordinate(check.getX(), check.getY() - 1);
                if (!checked.contains(co1)) {
                    toCheck.add(co1);
                } else if (!checked.contains(co2)) {
                    toCheck.add(co2);
                } else if (!checked.contains(co3)) {
                    toCheck.add(co3);
                } else if (!checked.contains(co4)) {
                    toCheck.add(co4);
                }

                checked.add(check);
                toCheck.remove(check);
            }

            for (Coordinate co : toColor) {
                gc.setFill(color);
                gc.fillRect(co.getX(), co.getY(), 1, 1);
            }
        }
    }

    private void makeSnapshot(Stack<Image> savedImages) {
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

    public void fillBtn(ActionEvent actionEvent) { drawFunction = "fill"; }


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

    public void size40(ActionEvent actionEvent) {
        size = 40;
    }

    public void size80(ActionEvent actionEvent) {
        size = 80;
    }

    public void openBtn(ActionEvent actionEvent) {
        Image image = Controller.openBtn();
        canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    public void saveBtn(ActionEvent actionEvent) {
        makeSnapshot(savedImages);
        Image latestImage = savedImages.lastElement();
        Controller.saveBtn(latestImage);
    }

    public static void emptySavedImages() {
        savedImages.clear();
    }
}
