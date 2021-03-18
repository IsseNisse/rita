package sample;

import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.*;

public class drawController {

    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker strokeColorPicker;

    @FXML
    private ColorPicker fillColorPicker;

    private static final Stack<Image> savedImages = new Stack<>();
    private final Stack<Image> savedLines = new Stack<>();

    private int size = 10;
    private Color strokeColor = Color.BLACK;
    private Color fillColor = Color.TRANSPARENT;

    private double anchor1X;
    private double anchor1Y;

    private String drawFunction = "freeDraw";
    private String brushShape = "circle";
    private boolean keyPressed;


    public void draw(javafx.scene.input.MouseEvent mouseEvent) {

        if (savedImages.empty()) {
            makeSnapshot(savedImages);
        }
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        switch (drawFunction) {
            case "freeDraw":
                freeDraw(gc, mouseX, mouseY, mouseEvent);
                break;
            case "drawLine":
                drawLine(gc, mouseX, mouseY, mouseEvent);
                break;
            case "fill":
                fill(gc, mouseX, mouseY, mouseEvent);
                break;
            case "drawSquare":
                drawSquare(gc, mouseX, mouseY, mouseEvent);
                break;
            case "drawCircle":
                drawCircle(gc, mouseX, mouseY, mouseEvent);
                break;
        }
    }


    /* Functions for different types of drawing */
    public void freeDraw(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        if (!eventType.getName().equals("MOUSE_RELEASED")) {
            gc.setFill(strokeColor);
            brush(gc, mouseX, mouseY);
        } else {
            /* save snapshot */
            makeSnapshot(savedImages);
        }
    }

    public void drawLine(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        String shape = "line";
        shapeDraw(gc, mouseX, mouseY, eventType, shape);
    }

    private void fill(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        Coordinate[] toCheck = new Coordinate[(int) (canvas.getWidth() * canvas.getHeight())];
        HashSet<Coordinate> toColor = new HashSet<>();
        HashSet<Coordinate> processed = new HashSet<>();

        if (eventType.getName().equals("MOUSE_PRESSED")) {
            Image latestSave = savedImages.get(savedImages.size() - 1);
            Coordinate startCoordinate = new Coordinate((int)mouseX, (int)mouseY);
            int arrayIndex = 0;
            toCheck[0] = startCoordinate;
            PixelReader pixelReader = latestSave.getPixelReader();
            Color colorToChange = pixelReader.getColor((int)mouseX, (int)mouseY);

            while (arrayIndex != -1) {
                Coordinate check = toCheck[arrayIndex];
                arrayIndex -= 1;
                Color pixelColor = pixelReader.getColor(check.getX(), check.getY());
                if (pixelColor.equals(colorToChange)) {
                    toColor.add(check);
                    Coordinate co1 = new Coordinate(check.getX() + 1, check.getY());
                    Coordinate co2 = new Coordinate(check.getX() - 1, check.getY());
                    Coordinate co3 = new Coordinate(check.getX(), check.getY() + 1);
                    Coordinate co4 = new Coordinate(check.getX(), check.getY() - 1);
                    if (!processed.contains(co1)) {
                        toCheck[++arrayIndex] = co1;
                        processed.add(co1);
                    }
                    if (!processed.contains(co2)) {
                        toCheck[++arrayIndex] = co2;
                        processed.add(co2);
                    }
                    if (!processed.contains(co3)) {
                        toCheck[++arrayIndex] = co3;
                        processed.add(co3);
                    }
                    if (!processed.contains(co4)) {
                        toCheck[++arrayIndex] = co4;
                        processed.add(co4);
                    }
                }
            }

            for (Coordinate co : toColor) {
                gc.setFill(strokeColor);
                gc.fillRect(co.getX(), co.getY(), 1, 1);
            }
            makeSnapshot(savedImages);
        }
    }

    private void drawSquare(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        String shape = "square";
        shapeDraw(gc, mouseX, mouseY, eventType, shape);
    }

    private void drawCircle(GraphicsContext gc, double mouseX, double mouseY, MouseEvent mouseEvent) {
        EventType<? extends MouseEvent> eventType = mouseEvent.getEventType();
        String shape = "circle";
        shapeDraw(gc, mouseX, mouseY, eventType, shape);
    }


    /* Function for general shape drawing and animation */
    private void shapeDraw(GraphicsContext gc, double mouseX, double mouseY, EventType<? extends MouseEvent> eventType, String shape) {
        double height;
        double width;
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
                if (keyPressed) {
                    width = mouseX;
                    height = mouseX;
                } else {
                    width = mouseX - anchor1X;
                    height = mouseY - anchor1Y;
                }
                whichShapeToBeDrawn(gc, mouseX, mouseY, shape, width, height);
            }
        } else {
            if (keyPressed) {
                width = mouseX;
                height = mouseX;
            } else {
                width = mouseX - anchor1X;
                height = mouseY - anchor1Y;
            }
            whichShapeToBeDrawn(gc, mouseX, mouseY, shape, width, height);

            /* save snapshot */
            makeSnapshot(savedImages);
        }
    }


    /* Switch case to decide which function is calling on shapeDraw */
    private void whichShapeToBeDrawn(GraphicsContext gc, double mouseX, double mouseY, String shape, double width, double height) {
        switch (shape) {
            case "circle":
                makeCircle(gc, width, height);
                break;
            case "square":
                makeSquare(gc, width, height);
                break;
            case "line":
                makeLine(gc, mouseX, mouseY);
                break;
        }
    }


    /* The functions for actually drawing a shape */
    private void makeLine(GraphicsContext gc, double mouseX, double mouseY) {
        gc.setStroke(strokeColor);
        gc.setLineWidth(size);
        gc.strokeLine(anchor1X, anchor1Y, mouseX, mouseY);
    }

    private void makeSquare(GraphicsContext gc, double width, double height) {
        gc.setStroke(strokeColor);
        gc.setFill(fillColor);
        gc.setLineWidth(size);
        gc.fillRect(anchor1X, anchor1Y, width, height);
        gc.strokeRect(anchor1X, anchor1Y, width, height);
    }

    private void makeCircle(GraphicsContext gc, double width, double height) {
        gc.setStroke(strokeColor);
        gc.setFill(fillColor);
        gc.setLineWidth(size);
        gc.fillOval(anchor1X, anchor1Y, width, height);
        gc.strokeOval(anchor1X, anchor1Y, width, height);
    }


    /* Function for deciding which shape on the brush is to be used */
    private void brush(GraphicsContext gc, double mouseX, double mouseY) {
        if (brushShape.equals("square")) {
            gc.fillRect(mouseX - (size/2), mouseY - (size/2), size, size);
        } else if (brushShape.equals("circle")) {
            gc.fillOval(mouseX - (size/2), mouseY - (size/2), size, size);
        }
    }


    /* Make a snapshot function */
    private void makeSnapshot(Stack<Image> savedImages) {
        Image snapshot = canvas.snapshot(null, null);
        savedImages.push(snapshot);
    }


    /* Undo function */
    public void undo(ActionEvent actionEvent) {
        if (!savedImages.empty()) {
            Image undoImage = savedImages.pop();
            canvas.getGraphicsContext2D().drawImage(undoImage, 0, 0);
        }
    }


    /* Get Color Picker value */
    public void strokeColorPicker(ActionEvent actionEvent) {
        Color colorValue = strokeColorPicker.getValue();
        strokeColor = Color.web(colorValue.toString());
    }

    public void fillColorPicker(ActionEvent actionEvent) {
        Color colorValue = fillColorPicker.getValue();
        fillColor = Color.web(colorValue.toString());
    }


    /* Buttons for changing drawing type */

    public void freeDrawBtn(ActionEvent actionEvent) {
        drawFunction = "freeDraw";
    }

    public void drawLineBtn(ActionEvent actionEvent) {
        drawFunction = "drawLine";
    }

    public void fillBtn(ActionEvent actionEvent) {
        drawFunction = "fill";
    }

    public void SquareBtn(ActionEvent actionEvent) {
        drawFunction = "drawSquare";
    }

    public void CircleBtn(ActionEvent actionEvent) {
        drawFunction = "drawCircle";
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

    public void size40(ActionEvent actionEvent) {
        size = 40;
    }

    public void size80(ActionEvent actionEvent) {
        size = 80;
    }


    /* Button Actions */
    public void openBtn(ActionEvent actionEvent) {
        Image image = Controller.openBtn();
        canvas.getGraphicsContext2D().drawImage(image, 0, 0);
    }

    public void Square(ActionEvent actionEvent) {
        brushShape = "square";
    }

    public void Circle(ActionEvent actionEvent) {
        brushShape = "circle";
    }

    public void saveBtn(ActionEvent actionEvent) {
        makeSnapshot(savedImages);
        Image latestImage = savedImages.lastElement();
        Controller.saveBtn(latestImage);
    }


    /* Remove all saved Images */
    public static void emptySavedImages() {
        savedImages.clear();
    }

    public void keyPressed(KeyEvent keyEvent) {
        keyPressed = keyEvent.isShiftDown();
        System.out.println(keyPressed);
    }
}
