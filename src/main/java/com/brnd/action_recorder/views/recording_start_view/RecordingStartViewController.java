/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.brnd.action_recorder.views.recording_start_view;

import com.brnd.action_recorder.views.utils.ViewController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author brdn
 */
public class RecordingStartViewController implements Initializable, ViewController{

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    
    
    
    @Override
    public void minimizeStage(Event event) {
        ViewController.super.minimizeStage(event);
    }
    @Override
    public void closeStage(Event event) {
        ViewController.super.closeStage(event);
    }
    @Override
    public void navigateToMainView(Event event) throws IOException {
        ViewController.super.navigateToMainView(event);
    }
    
}
