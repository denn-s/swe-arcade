package sample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {

        // TODO: Zu Demonstrationszwecken auskommentiert, sollte später wieder verwendet werden
        /**
         Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
         primaryStage.setTitle("ArcadeBox");
         primaryStage.setScene(new Scene(root, 800, 600));
         primaryStage.show();
         */

        // Dummy Anzeige zur Demonstation der Ausführung eines Spiels
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setTitle("ArcadeBox");
        primaryStage.setScene(scene);

        Button btn = new Button();
        btn.setText("Start Game");

        // TODO: muss an den richtigen Button aus der fxml Datei gebunden werden
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("ArcadeBox: ArcadeBox started");
                System.out.println("ArcadeBox: Working Directory = " +
                        System.getProperty("user.dir"));

                Task<Integer> task = new GameTask();

                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        // hier kann ggf. etwas gemacht werden
                    }
                });

                if (task.isDone()) {
                    // hier kann ggf. etwas gemacht werden
                }

                // Der Task wird in einen eigenen Thread ausgeführt
                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();

            }
        });

        root.getChildren().add(btn);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }


    // Task zum separaten Ausführen eines Spiels
    private class GameTask extends Task<Integer> {

        @Override
        protected Integer call() throws Exception {
            Process proc = null;
            try {

                //TODO: die echten Benutzernamen verwenden, müssen zur Laufzeit ermittelt werden
                String playerName1 = "Daniel";
                String playerName2 = "Randeep";

                // hier wird die jar-Datei eines Spiels ausgeführt
                //TODO: Name des Spiels muss zur Laufzeit ermittelt werden, .jar nicht vergessen!
                //TODO: Fehlerbehandlung falls Spiel nicht existiert
                String gameName = "desktop-1.0.jar";
                proc = Runtime.getRuntime().exec("java -jar " + gameName + " " + playerName1 + " " + playerName2);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                proc.waitFor();
                System.out.println("ArcadeBox: game process finished");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Stream für die Ausgabe des Prozesses anlegen
            InputStream in = proc.getInputStream();
            InputStream err = proc.getErrorStream();

            byte b[] = new byte[0];
            try {
                b = new byte[in.available()];
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.read(b, 0, b.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Ausgabe des Prozesses
            String output = new String(b);

            System.out.println("ArcadeBox-> got message from game: " + output);

            // Es wird ein Parser für Json verwendet um die Nachricht des Spiels auzulesen
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(output).getAsJsonObject();

            // Das Json Objekt muss hierbei ein winner und loser Attribut enthalten
            String winner = obj.get("winner").getAsString();
            String loser = obj.get("loser").getAsString();

            System.out.println("ArcadeBox: parsed json, winner: " + winner);
            System.out.println("ArcadeBox: parsed json, loser: " + loser);

            // TODO: braucht man das noch?
            /**
             byte c[]= new byte[0];
             try {
             c = new byte[err.available()];
             } catch (IOException e) {
             e.printStackTrace();
             }
             try {
             err.read(c,0,c.length);
             } catch (IOException e) {
             e.printStackTrace();
             }
             System.out.println(new String(c));
             */

            return 0;
        }
    }


}
