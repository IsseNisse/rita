package sample;

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
        boolean canSearch = true;
        ArrayList<Coordinate> coordinates = new ArrayList<>();

        if (eventType.getName().equals("MOUSE_PRESSED")) {
            Image latestSave = savedImages.get(savedImages.size() - 1);
            PixelReader pixelReader = latestSave.getPixelReader();
            pixelReader.getColor((int)mouseX, (int)mouseY);
            Coordinate startCoordinate = new Coordinate((int)mouseX, (int)mouseY);
            coordinates.add(startCoordinate);
            int i = 1;

            while (canSearch) {
                int edgeCoordinatesCount = i * 8;

                for (int k = 0; k < edgeCoordinatesCount/4 + 1; k++) {
                    if (k == 0) {
                        coordinates.add(new Coordinate(startCoordinate.getX() - i, startCoordinate.getY() - i));
                        coordinates.remove(0);

                    } else {
                        coordinates.add(new Coordinate(coordinates.get(0).getX() + k, coordinates.get(0).getY()));
                    }
                    coordinates.add(new Coordinate(coordinates.get(0).getX(), coordinates.get(0).getY() + k + 1));
                    coordinates.add(new Coordinate(coordinates.get(0).getX() + k + 1, coordinates.get(0).getY() + k + 1));
                }

                i++;
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
