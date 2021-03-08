package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Controller {
    @FXML
    private Canvas canvas;


    public void onPaint(javafx.scene.input.MouseEvent mouseEvent) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();
        draw(gc, mouseX, mouseY);
    }

    public void draw(GraphicsContext gc, double mouseX, double mouseY) {
        gc.setFill(Color.GREEN);
        gc.fillRect(mouseX - 5, mouseY - 5, 10, 10);
    }
}
