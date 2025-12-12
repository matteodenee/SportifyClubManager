rm -rf bin
javac --module-path /usr/share/openjfx/lib \
      --add-modules javafx.controls,javafx.fxml \
      -cp "postgresql-42.7.3.jar" \
      -d bin \
      $(find src -name "*.java")

java --module-path /usr/share/openjfx/lib \
     --add-modules javafx.controls,javafx.fxml \
     -cp "bin:postgresql-42.7.3.jar" \
     SportifyClubManager.frame.LoginFrame

