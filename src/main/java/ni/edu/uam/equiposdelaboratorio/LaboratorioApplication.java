package ni.edu.uam.equiposdelaboratorio;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LaboratorioApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LaboratorioApplication.class.getResource("laboratorio-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Laboratorio de Equipos!");
        stage.setScene(scene);
        stage.show();
    }
}
