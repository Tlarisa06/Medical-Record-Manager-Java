package medicalrecord;

import medicalrecord.service.CabinetService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private static CabinetService serviceInstance;

    public static void setService(CabinetService service) {
        serviceInstance = service;
    }

    public static void run(CabinetService service) {
        setService(service);
        Application.launch(HelloApplication.class);
    }

    public HelloApplication() {
    }

    @Override
    public void start(Stage stage) throws IOException {
        CabinetService service = serviceInstance;
        if (service == null) {
            System.err.println("Eroare : CabinetService-ul nu a fost initializat");
            stage.close();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);

        HelloController hc = fxmlLoader.getController();
        if (hc == null) {
            throw new IllegalStateException("Nu s-a putut încărca HelloController din FXML. Verificați fișierul hello-view.fxml.");
        }
        hc.setService(service);

        stage.setTitle("Cabinet Stomatologic - JavaFX");
        stage.setScene(scene);
        stage.show();
    }
}